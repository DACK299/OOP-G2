package io.github.some_example_name.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.some_example_name.entities.Entity;
import io.github.some_example_name.entities.Player;

public class MovementManager {
    private float baseSpeed;
    private float turnSpeed;
    private float maxSpeed;
    private float minSpeed;
    private Vector2 velocity = new Vector2();
    private Vector2 impulse = new Vector2();
    
    public MovementManager(float baseSpeed, float maxSpeed) {
        this.baseSpeed = baseSpeed;
        this.maxSpeed = maxSpeed;
        this.minSpeed = 0;
        this.turnSpeed = 200f;
    }
    
    public Vector2 calculate_movement(Entity entity, float deltaTime) {
        // Reset velocity
        velocity.set(0, 0);
        
        if (entity instanceof Player) {
            // Handle input for Player
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocity.x = -maxSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocity.x = maxSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                velocity.y = maxSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                velocity.y = -maxSpeed;
            }
        }
        
        return velocity.cpy(); // Return a copy of the velocity vector
    }
    
    public void applyMovement(Body body, Vector2 direction, float force) {
        // Calculate impulse based on direction and force
        impulse.set(direction).nor().scl(force);
        
        // Apply force at center of body
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
        
        // Limit maximum velocity
        Vector2 vel = body.getLinearVelocity();
        float speed = vel.len();
        if (speed > maxSpeed * PhysicsManager.PIXELS_TO_METERS) {
            vel.nor().scl(maxSpeed * PhysicsManager.PIXELS_TO_METERS);
            body.setLinearVelocity(vel);
        }
    }
    
    public void applyCarMovement(Body body, Vector2 direction, float alcoholLevel, float maxAlcoholLevel) {
        // Calculate base force (scaled for physics world)
        float baseForce = baseSpeed * PhysicsManager.PIXELS_TO_METERS * 0.1f;
        
        // Calculate current velocity magnitude
        Vector2 currentVelocity = body.getLinearVelocity();
        float currentSpeed = currentVelocity.len();
        
        // If we have direction input, apply movement
        if (direction.len2() > 0) {
            direction.nor();
            
            // Apply alcohol effects if applicable
            if (alcoholLevel >= 50f) { // Alcohol effect threshold
                float excessAlcohol = (alcoholLevel - 50f) / (maxAlcoholLevel - 50f);
                float alcoholEffect = excessAlcohol * 0.8f; // Up to 80% random movement at max alcohol
                
                // Add random jitter to direction
                if (alcoholEffect > 0) {
                    float randomAngle = (float) (Math.random() * Math.PI * 2 * alcoholEffect);
                    direction.rotateRad(randomAngle);
                    
                    // Add random force magnitude variation
                    float forceFactor = 1.0f + (float)((Math.random() - 0.5) * alcoholEffect);
                    baseForce *= forceFactor;
                }
            }
            
            impulse.set(direction).scl(baseForce);
            body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
        }
        
        // Limit maximum velocity
        if (currentSpeed > maxSpeed * PhysicsManager.PIXELS_TO_METERS) {
            currentVelocity.nor().scl(maxSpeed * PhysicsManager.PIXELS_TO_METERS);
            body.setLinearVelocity(currentVelocity);
        }
        
        // Apply damping for smooth movement
        float damping = 0.5f;
        if (alcoholLevel >= 50f) { // Alcohol effect threshold
            float excessAlcohol = (alcoholLevel - 50f) / (maxAlcoholLevel - 50f);
            damping += excessAlcohol * 0.5f; // Increased damping with alcohol
        }
        body.setLinearDamping(damping);
    }
    
    // Getters and setters
    public float getBaseSpeed() {
        return baseSpeed;
    }
    
    public void setBaseSpeed(float baseSpeed) {
        this.baseSpeed = baseSpeed;
    }
    
    public float getTurnSpeed() {
        return turnSpeed;
    }
    
    public void setTurnSpeed(float turnSpeed) {
        this.turnSpeed = turnSpeed;
    }
    
    public float getMaxSpeed() {
        return maxSpeed;
    }
    
    public float getMinSpeed() {
        return minSpeed;
    }
}