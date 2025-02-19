package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Obstacle extends Entity implements IMovable, ICollidable {
    private ShapeRenderer shapeRenderer;
    private Rectangle bounds;
    private Vector2 velocity;
    private static final float SPEED = 150; // pixels per second
    private boolean movingRight = false;
    private boolean shouldBeRemoved = false;
    private boolean isColliding = false;
    private float collisionTimer = 0;
    private static final float COLLISION_DISPLAY_TIME = 1.0f; // Show message for 1 second
    private BitmapFont font;
    private Vector2 previousPosition;
    
    public Obstacle(float x, float y, float width, float height) {
        super(x, y, width, height);
        shapeRenderer = new ShapeRenderer();
        bounds = new Rectangle(x, y, width, height);
        velocity = new Vector2(-SPEED, 0); // Start moving left
        font = new BitmapFont();
        font.getData().setScale(2f);
        previousPosition = new Vector2(x, y);
    }
    
    @Override
    public void update(float deltaTime) {
        if (!isColliding) {
            // Store previous position before moving
            previousPosition.set(x, y);
            
            // Move
            move(deltaTime);
            
            // Update collision bounds
            bounds.setPosition(x, y);
        } else {
            // Update collision timer
            collisionTimer += deltaTime;
            if (collisionTimer >= COLLISION_DISPLAY_TIME) {
                shouldBeRemoved = true;
            }
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        // Only draw the obstacle if it hasn't been marked for removal
        if (!shouldBeRemoved) {
            // Draw the obstacle shape
            if (!isColliding) {
                batch.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect(x, y, width, height);
                shapeRenderer.end();
                batch.begin();
            }
            
            // Draw collision message if colliding
            if (isColliding) {
                font.setColor(Color.RED);
                font.draw(batch, "Collision!", 300, 300);
            }
        }
    }
    
    @Override
    public void move(float deltaTime) {
        // Apply movement based on current direction
        x += velocity.x * deltaTime;
    }
    
    @Override
    public boolean checkCollision(Entity other) {
        if (other instanceof ICollidable && !shouldBeRemoved && !isColliding) {
            Rectangle otherBounds = ((ICollidable) other).getBounds();
            return bounds.overlaps(otherBounds);
        }
        return false;
    }
    
    @Override
    public void handleCollision(Entity other) {
        if (other instanceof Wall) {
            // Move back to previous position
            x = previousPosition.x;
            y = previousPosition.y;
            bounds.setPosition(x, y);
            
            // Reverse direction
            velocity.x = -velocity.x;
            movingRight = !movingRight;
        } else if (other instanceof Player && !isColliding) {
            isColliding = true;
            collisionTimer = 0;
            velocity.x = 0; // Stop moving
        }
    }
    
    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}