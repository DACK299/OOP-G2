package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Pool.Poolable;

import io.github.some_example_name.managers.PhysicsManager;
import io.github.some_example_name.managers.SoundManager;
import io.github.some_example_name.screens.PlayScreen;

public class Obstacle extends Entity implements ICollidable, Poolable {
    private float x;
    private float y;
    private float width;
    private float height;
    private float speed;
    private Rectangle bounds;
    private Vector2 velocity;
    private Vector2 previousPosition;
    private Color color;
    private Texture obstacleTexture;
    private static final Texture policeTexture = new Texture("police.png");
    private static final Texture variantTexture = new Texture("variant.png");
    private static final Texture taxiTexture = new Texture("taxi.png");
    private boolean isColliding;
    private float collisionTimer;
    private boolean shouldBeRemoved;
    private static final float COLLISION_DISPLAY_TIME = 0.5f;
    private boolean isHit = false;
    private float hitVelocityX = 0;
    private float hitVelocityY = 0;
    private static final float HIT_FRICTION = 0.95f; // Friction to slow down hit obstacles
    private static final float MIN_VELOCITY = 10f; // Minimum velocity before stopping
    private static final float HIT_SPEED_MULTIPLIER = 0.5f; // How much of the player's speed transfers to obstacle
    private Body body;
    
    public Obstacle(float x, float y, float width, float height, float speed) {
        super(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        bounds = new Rectangle(x, y, width, height);
        color = Color.RED;
        
        // Randomly select a sprite
        float random = MathUtils.random();
        if (random < 0.33f) {
            obstacleTexture = policeTexture;
        } else if (random < 0.66f) {
            obstacleTexture = variantTexture;
        } else {
            obstacleTexture = taxiTexture;
        }
        
        velocity = new Vector2(-speed, 0);
        previousPosition = new Vector2(x, y);
        reset();
        
        // Physics body will be created in init() to allow for pooling
    }
    
    @Override
    public void reset() {
        isColliding = false;
        collisionTimer = 0;
        shouldBeRemoved = false;
        velocity.set(-speed, 0);
        isHit = false;
        hitVelocityX = 0;
        hitVelocityY = 0;
        
        // Clean up Box2D body if it exists
        if (body != null) {
            try {
                PhysicsManager.getInstance().destroyBody(body);
            } catch (Exception e) {
                System.err.println("Error destroying Obstacle body: " + e.getMessage());
            }
            body = null;
        }
    }
    
    public void init(float x, float y) {
        this.x = x;
        this.y = y;
        bounds.setPosition(x, y);
        reset();
        
        // Create Box2D body for this obstacle
        PhysicsManager physicsManager = PhysicsManager.getInstance();
        body = physicsManager.createBody(
            BodyDef.BodyType.DynamicBody,
            x, y, width, height,
            1.0f, 0.1f, 0.2f,
            this
        );
        
        // Set initial velocity
        body.setLinearVelocity(PhysicsManager.toMeters(-speed), 0);
    }
    
    @Override
    public void update(float deltaTime) {
        // Update position from Box2D body
        if (body != null) {
            updatePositionFromBody();
            
            // Ensure bounds are immediately updated with the new position
            bounds.set(x, y, width, height);
            
            if (!isHit) {
                // Update velocity of the physics body
                body.setLinearVelocity(PhysicsManager.toMeters(-speed), 0);
            } else {
                // For hit obstacles, let Box2D handle the movement with damping
                body.setLinearDamping(0.5f);
            }
            
            // Check if obstacle should be removed
            if (isColliding) {
                collisionTimer += deltaTime;
            }
            
            // Log warning before removing to track unexpected disappearances
            if (x < -width * 2 || x > PlayScreen.WORLD_WIDTH + width * 2) {
                if (!shouldBeRemoved) {
                    shouldBeRemoved = true;
                    System.out.println("Obstacle set for removal due to being out of bounds: " + this);
                }
            }
        } else {
            System.out.println("Warning: Obstacle update called with null body: " + this);
        }
    }
    
    // Getter for collision timer to improve removal timing
    public float getCollisionTimer() {
        return collisionTimer;
    }
    
    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        // Draw collision bounds with semi-transparency to make it clear
        if (isHit) {
            shapeRenderer.setColor(new Color(1, 0, 0, 0.7f)); // Semi-transparent red
        } else {
            shapeRenderer.setColor(new Color(1, 0.5f, 0, 0.7f)); // Semi-transparent orange
        }
        // Draw the exact bounds rectangle
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    @Override
    public void renderSprite(SpriteBatch batch) {
        // Draw the sprite with rotation and flipping
        float originX = width / 2;
        float originY = height / 2;
        
        batch.draw(obstacleTexture,
                  x, y,           // Position
                  originX, originY, // Origin for rotation
                  width, height,   // Size (250x130)
                  1, 1,           // Scale
                  180,            // Rotate 180 degrees to face right
                  0, 0,           // Source position in texture
                  obstacleTexture.getWidth(), obstacleTexture.getHeight(), // Source size
                  false, false);  // No flipping needed with 180 rotation
    }
    
    @Override
    public boolean checkCollision(Entity other) {
        if (other instanceof ICollidable) {
            return bounds.overlaps(((ICollidable) other).getBounds());
        }
        return false;
    }
    
    @Override
    public void handleCollision(Entity other) {
        if (other instanceof Car && !isHit) {
            Car car = (Car) other;
            isHit = true;
            
            // Explicitly damage the car when hit
            if (other instanceof PlayerCar) {
                // Let PlayerCar handle its own damage in its handleCollision method
                // Don't apply damage here to avoid double damage
            }
            
            // Play hit sound
            SoundManager.getInstance().playSound("wall_hit");
            PlayScreen playScreen = PlayScreen.getInstance();
            if (playScreen != null) {
                playScreen.incrementCollisionCount();
            }
            
            // Apply impulse for physics-based collision response
            if (other.getBody() != null && body != null) {
                // Get car velocity
                Vector2 carVel = car.getBody().getLinearVelocity();
                float carSpeed = carVel.len();
                
                // Calculate collision impulse direction
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
                    impulseDir.set(1, 0);
                }
                
                // Scale impulse by car speed
                float impulseStrength = carSpeed * HIT_SPEED_MULTIPLIER;
                body.applyLinearImpulse(
                    impulseDir.scl(impulseStrength),
                    body.getWorldCenter(),
                    true
                );
            }
            
            // Set collision state
            isColliding = true;
            collisionTimer = 0;
            color.set(0.5f, 0, 0, 1);
            
            // Debug output
            System.out.println("Obstacle hit by car at position: " + x + ", " + y);
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }
    
    @Override
    public void dispose() {
        // Clean up Box2D body
        if (body != null) {
            PhysicsManager.getInstance().destroyBody(body);
            body = null;
        }
        // Note: We don't dispose the static textures here as they are shared
    }
    
    public static void disposeTextures() {
        // Call this method when the game is closing
        if (policeTexture != null) policeTexture.dispose();
        if (variantTexture != null) policeTexture.dispose();
        if (taxiTexture != null) policeTexture.dispose();
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public void setY(float y) {
        this.y = y;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    @Override
    protected void updatePositionFromBody() {
        if (body != null) {
            Vector2 position = body.getPosition();
            x = PhysicsManager.toPixels(position.x) - width / 2;
            y = PhysicsManager.toPixels(position.y) - height / 2;
            
            // Immediately update bounds
            bounds.set(x, y, width, height);
        }
    }
    
    @Override
    public String toString() {
        return "Obstacle [x=" + x + ", y=" + y + ", isHit=" + isHit + ", isColliding=" + isColliding + 
               ", shouldBeRemoved=" + shouldBeRemoved + "]";
    }
}