package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PlayerCar extends Car {
    private static final float MAX_HEALTH = 100f;
    private static final float MAX_ALCOHOL_LEVEL = 100f;
    private static final float ALCOHOL_INCREASE_RATE = 2f; // Amount to increase per second
    private static final float ALCOHOL_EFFECT_THRESHOLD = 30f; // Level at which effects start becoming more severe
    private float currentHealth;
    private float alcoholLevel;
    private float maxSpeed;
    private float acceleration;
    private float deceleration;
    private float maxTurnSpeed;
    private Color color;
    private boolean isInvulnerable = false;
    private float invulnerabilityTimer = 0;
    private static final float INVULNERABILITY_DURATION = 2f;
    private float flashTimer = 0;
    private static final float FLASH_INTERVAL = 0.2f;
    
    public PlayerCar(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.currentHealth = MAX_HEALTH;
        this.alcoholLevel = 0f;
        this.maxSpeed = 300f;
        this.acceleration = 400f;
        this.deceleration = 250f;
        this.maxTurnSpeed = 250f;
        this.color = new Color(0, 0.8f, 0.2f, 1); // Green color for player car
    }
        
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Increase alcohol level over time
        alcoholLevel = Math.min(MAX_ALCOHOL_LEVEL, alcoholLevel + ALCOHOL_INCREASE_RATE * deltaTime);
        
        // Apply alcohol effects to movement
        float alcoholEffect = alcoholLevel / MAX_ALCOHOL_LEVEL;
        
        // If alcohol level is above threshold, effects become more severe
        if (alcoholLevel > ALCOHOL_EFFECT_THRESHOLD) {
            float severeEffect = (alcoholLevel - ALCOHOL_EFFECT_THRESHOLD) / (MAX_ALCOHOL_LEVEL - ALCOHOL_EFFECT_THRESHOLD);
            alcoholEffect = alcoholEffect + severeEffect;
        }
        
        // Reduce max speed more severely
        setMaxSpeed(maxSpeed * (1 - alcoholEffect * 0.5f));
    
        // Increase turn speed more dramatically
        setMaxTurnSpeed(maxTurnSpeed * (1 + alcoholEffect * 1.5f));
        
        // Reduce acceleration as alcohol level increases
        setAcceleration(acceleration * (1 - alcoholEffect * 0.4f));
        
        // Update invulnerability
        if (isInvulnerable) {
            invulnerabilityTimer += deltaTime;
            flashTimer += deltaTime;
            if (invulnerabilityTimer >= INVULNERABILITY_DURATION) {
                isInvulnerable = false;
                invulnerabilityTimer = 0;
                flashTimer = 0;
            }
        }
    }
    
    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        // Draw the actual collision box to make debugging easier
        if (isInvulnerable && (int)(flashTimer / FLASH_INTERVAL) % 2 == 0) {
            shapeRenderer.setColor(new Color(1, 1, 1, 0.7f)); // Semi-transparent white
        } else {
            shapeRenderer.setColor(new Color(0, 0.8f, 0.2f, 0.7f)); // Semi-transparent green
        }
        // Draw the exact bounds rectangle to better visualize the collision area
        shapeRenderer.rect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
    }
    
    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }
    
    public void increaseAlcoholLevel() {
        alcoholLevel = Math.min(MAX_ALCOHOL_LEVEL, alcoholLevel + ALCOHOL_INCREASE_RATE);
    }
    
    public float getAlcoholLevel() {
        return alcoholLevel;
    }
    
    public float getMaxAlcoholLevel() {
        return MAX_ALCOHOL_LEVEL;
    }
    
    @Override
    public float getCurrentHealth() {
        return currentHealth;
    }
    
    @Override
    public float getMaxHealth() {
        return MAX_HEALTH;
    }
    
    @Override
    public void takeDamage(float damage) {
        if (!isInvulnerable) {
            currentHealth = Math.max(0, currentHealth - damage);
            isInvulnerable = true;
            invulnerabilityTimer = 0;
            flashTimer = 0;
            
            // Debug print to verify damage is being applied
            System.out.println("PlayerCar takeDamage called: " + damage + ", health now: " + currentHealth);
        }
    }
    
    // Override the handleCollision method from Car to ensure health reduction works
    @Override
    public void handleCollision(Entity other) {
        if (other instanceof Obstacle && !isInvulnerable) {
            // Apply damage directly to health
            currentHealth -= 33.34f;
            
            // Make sure health doesn't go below 0
            if (currentHealth < 0) {
                currentHealth = 0;
            }
            
            // Set invulnerability
            isInvulnerable = true;
            invulnerabilityTimer = 0;
            flashTimer = 0;
            
            // Debug print
            System.out.println("PlayerCar collision with obstacle! Health reduced to: " + currentHealth);
            
            // Call the parent class's handleCollision for other effects
            super.handleCollision(other);
        }
    }
    
    public void setMaxSpeed(float speed) {
        this.maxSpeed = speed;
    }
    
    public void setMaxTurnSpeed(float speed) {
        this.maxTurnSpeed = speed;
    }
    
    public boolean isAlive() {
        return currentHealth > 0;
    }
    
    public void makeInvulnerable() {
        isInvulnerable = true;
        invulnerabilityTimer = 0;
        flashTimer = 0;
    }
    
    public boolean isInvulnerable() {
        return isInvulnerable;
    }
}