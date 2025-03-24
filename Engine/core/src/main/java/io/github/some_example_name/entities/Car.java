package io.github.some_example_name.entities;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import io.github.some_example_name.managers.IOManager;
import io.github.some_example_name.managers.MovementManager;
import io.github.some_example_name.managers.PhysicsManager;
import io.github.some_example_name.managers.SoundManager;
import io.github.some_example_name.screens.PlayScreen;

public class Car extends Entity implements ICollidable {
    private float x;
    private float y;
    private float width;
    private float height;
    private float speed;
    private float verticalSpeed = 300f;
    private static final float MIN_Y = 0;    // Bottom boundary
    private static final float MAX_Y = 800;  // Changed from 850 to 800
    private Rectangle bounds;
    private Color color;
    private float maxHealth = 100;
    private float currentHealth;
    private boolean isInvulnerable;
    private float invulnerabilityTimer;
    private float flashTimer;
    private static final float INVULNERABILITY_DURATION = 2f;
    private static final float FLASH_INTERVAL = 0.2f;
    private Vector2 previousPosition;
    
    private float alcoholLevel;
    private float maxAlcoholLevel = 100;
    private static final float PASSIVE_ALCOHOL_INCREASE = 3f;
    private static final float ACTIVE_ALCOHOL_INCREASE = 12f;
    private static final float ALCOHOL_DECREASE = 4f;
    private static final float MAX_RANDOM_FORCE = 200f; // Reduced from 400f
    private static final float MIN_CONTROL = 0.15f;
    private boolean isAccelerating;
    private boolean isBraking = false;
    private float turnAmount = 0;
    private float gameTime = 0;
    private float previousDeltaX = 0;
    private float previousDeltaY = 0;
    private float collisionShakeTime = 0;
    private static final float COLLISION_SHAKE_DURATION = 0.5f;
    private static final float COLLISION_SHAKE_INTENSITY = 10f;
    
    private Texture carTexture;
    
    private float actualDrawWidth;
    private float actualDrawHeight;
    
    private Body body;
    
    // Add properties to track actual speed
    private Vector2 currentVelocity = new Vector2();
    private float currentSpeed = 0;
    
    public Car(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = 250;  // Car width
        this.height = 130; // Car height
        this.speed = 0;
        this.bounds = new Rectangle(x, y, this.width, this.height);
        this.color = Color.GREEN;
        this.currentHealth = maxHealth;
        this.alcoholLevel = 0;
        this.isAccelerating = false;
        this.isBraking = false;
        this.turnAmount = 0;
        
        // Load car texture
        this.carTexture = new Texture("paul walker car.png");
        
        // Create Box2D body
        PhysicsManager physicsManager = PhysicsManager.getInstance();
        this.body = physicsManager.createBody(
            BodyDef.BodyType.DynamicBody,
            x, y, width, height,
            1.0f, 0.2f, 0.4f,
            this
        );
        
        // Prevent rotation of the car body
        this.body.setFixedRotation(true);
    }
    
    @Override
    public void update(float deltaTime) {
        // Update from Box2D body position
        updatePositionFromBody();
        
        // Ensure bounds match the actual position - this is crucial for collision detection
        bounds.set(x, y, width, height);
        
        // Update game time
        gameTime += deltaTime;
        
        // Update collision shake effect
        if (collisionShakeTime > 0) {
            collisionShakeTime -= deltaTime;
        }
        
        // Update invulnerability timer
        if (isInvulnerable) {
            invulnerabilityTimer -= deltaTime;
            flashTimer += deltaTime;
            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
                invulnerabilityTimer = 0;
                flashTimer = 0;
            }
        }
    }
    
    public void update(float deltaTime, IOManager io, MovementManager movement) {
        // Update game time and other timers like in the original method
        update(deltaTime);
        
        // Get input
        boolean leftPressed = io.isKeyPressed(Input.Keys.LEFT) || io.isKeyPressed(Input.Keys.A);
        boolean rightPressed = io.isKeyPressed(Input.Keys.RIGHT) || io.isKeyPressed(Input.Keys.D);
        boolean upPressed = io.isKeyPressed(Input.Keys.UP) || io.isKeyPressed(Input.Keys.W);
        boolean downPressed = io.isKeyPressed(Input.Keys.DOWN) || io.isKeyPressed(Input.Keys.S);
        
        // Create movement vector from input
        Vector2 moveDirection = new Vector2(0, 0);
        if (leftPressed) moveDirection.x -= 1;
        if (rightPressed) moveDirection.x += 1;
        if (upPressed) moveDirection.y += 1;
        if (downPressed) moveDirection.y -= 1;
        
        // Apply movement via physics with acceleration/deceleration
        movement.applyCarMovement(body, moveDirection, alcoholLevel, maxAlcoholLevel);
        
        // Update current velocity and speed for gameplay logic
        currentVelocity.set(body.getLinearVelocity());
        currentSpeed = PhysicsManager.toPixels(currentVelocity.len());
        
        // Clamp position to world bounds
        Vector2 position = body.getPosition();
        float halfWidthInMeters = PhysicsManager.toMeters(width) / 2;
        float halfHeightInMeters = PhysicsManager.toMeters(height) / 2;
        
        float minX = halfWidthInMeters;
        float maxX = PhysicsManager.toMeters(PlayScreen.WORLD_WIDTH) - halfWidthInMeters;
        float minY = halfHeightInMeters;
        float maxY = PhysicsManager.toMeters(PlayScreen.WORLD_HEIGHT) - halfHeightInMeters;
        
        position.x = MathUtils.clamp(position.x, minX, maxX);
        position.y = MathUtils.clamp(position.y, minY, maxY);
        
        body.setTransform(position, body.getAngle());
        
        // Update the speed property for compatibility with existing code
        speed = currentSpeed;
    }
    
    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        // Draw collision bounds outline
        shapeRenderer.setColor(isInvulnerable ? Color.YELLOW : Color.RED);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    @Override
    public void renderSprite(SpriteBatch batch) {
        // Draw the sprite centered on the physics body position
        batch.draw(carTexture, 
                  x, y,                    // Position
                  width/2, height/2,       // Origin at center for better rotation
                  width, height,           // Use collision box dimensions
                  1, 1,                    // No additional scaling
                  0,                       // No rotation
                  0, 0,                    // Source position in texture
                  carTexture.getWidth(),   // Use full texture width
                  carTexture.getHeight(),  // Use full texture height
                  false, false);           // No flipping
    }
    
    @Override
    public void dispose() {
        if (carTexture != null) {
            carTexture.dispose();
        }
        
        // Clean up Box2D body
        if (body != null) {
            PhysicsManager.getInstance().destroyBody(body);
            body = null;
        }
    }
    
    @Override
    public boolean checkCollision(Entity other) {
        // Box2D now handles collision detection, but we can still use this for non-physics checks
        if (other instanceof ICollidable) {
            return bounds.overlaps(((ICollidable) other).getBounds());
        }
        return false;
    }
    
    @Override
    public void handleCollision(Entity other) {
        if (other instanceof Obstacle && !isInvulnerable) {
            // Start collision shake effect
            collisionShakeTime = COLLISION_SHAKE_DURATION;
            
            // Set invulnerability here but let PlayerCar handle health reduction
            isInvulnerable = true;
            invulnerabilityTimer = INVULNERABILITY_DURATION;
            
            // Play collision sound
            SoundManager.getInstance().playSound("collision");
            
            // Increment collision count in PlayScreen
            PlayScreen playScreen = PlayScreen.getInstance();
            if (playScreen != null) {
                playScreen.incrementCollisionCount();
            }
            
            // Apply impulse for physics-based collision response
            if (other.getBody() != null && body != null) {
                // Calculate collision impulse direction - make sure it's reliable
                Vector2 myPos = body.getPosition();
                Vector2 otherPos = other.getBody().getPosition();
                
                Vector2 impulseDir = new Vector2(
                    myPos.x - otherPos.x,
                    myPos.y - otherPos.y
                );
                
                // Normalize only if the vector isn't zero
                if (impulseDir.len2() > 0.001f) {
                    impulseDir.nor();
                } else {
                    // Fallback if positions are too close
                    impulseDir.set(0, 1);
                }
                
                // Apply impulse to the car - reduce strength to prevent excessive bouncing
                float impulseStrength = 1.5f;
                body.applyLinearImpulse(
                    impulseDir.scl(impulseStrength),
                    body.getWorldCenter(),
                    true
                );
                
                // Debug output
                System.out.println("Collision impulse applied: " + impulseDir + " strength: " + impulseStrength);
            }
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    public float getSpeed() {
        return currentSpeed; // Return actual speed from physics
    }
    
    public Vector2 getVelocity() {
        return currentVelocity;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public float getCurrentHealth() {
        return currentHealth;
    }
    
    public float getMaxHealth() {
        return maxHealth;
    }
    
    public void damage(float amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }
    
    public float getAlcoholLevel() {
        return alcoholLevel;
    }
    
    public void setAlcoholLevel(float level) {
        this.alcoholLevel = MathUtils.clamp(level, 0, maxAlcoholLevel);
    }
    
    public float getMaxAlcoholLevel() {
        return maxAlcoholLevel;
    }
    
    public void setAccelerating(boolean accelerating) {
        this.isAccelerating = accelerating;
    }
    
    public void setBraking(boolean braking) {
        this.isBraking = braking;
    }
    
    public float getActualDrawWidth() {
        return actualDrawWidth;
    }
    
    public float getActualDrawHeight() {
        return actualDrawHeight;
    }
    
    protected void updatePositionFromBody() {
        if (body != null) {
            Vector2 position = body.getPosition();
            // Make sure to update the center position correctly
            x = PhysicsManager.toPixels(position.x) - width / 2;
            y = PhysicsManager.toPixels(position.y) - height / 2;
            
            // Ensure the bounds are updated too
            bounds.set(x, y, width, height);
        }
    }
    
    // Implement the takeDamage method
    public void takeDamage(float amount) {
        if (!isInvulnerable) {
            currentHealth = Math.max(0, currentHealth - amount);
            isInvulnerable = true;
            invulnerabilityTimer = INVULNERABILITY_DURATION;
            
            // Debug print
            System.out.println("Car.takeDamage called: " + amount + ", health now: " + currentHealth);
        }
    }
}