package io.github.some_example_name.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.managers.MovementManager;

public class Player extends Entity implements IMovable, ICollidable {
    private Texture texture;
    private MovementManager movementManager;
    private Rectangle bounds;
    private float velocityX = 0;
    private float velocityY = 0;
    private final float SPEED = 200; // pixels per second
    
    public Player(float x, float y, float width, float height) {
        super(x, y, width, height);
        // Initialize the player's texture
        texture = new Texture(Gdx.files.internal("player.png")); // You'll need to add this image
        movementManager = new MovementManager(0.5f, SPEED);
        bounds = new Rectangle(x, y, width, height);
    }
    
    @Override
    public void update(float deltaTime) {
        // Handle movement
        move(deltaTime);
        
        // Update collision bounds
        bounds.setPosition(x, y);
    }
    
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }
    
    @Override
    public void move(float deltaTime) {
        // Reset velocity
        velocityX = 0;
        velocityY = 0;
        
        // Handle input
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocityX = -SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocityX = SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocityY = SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocityY = -SPEED;
        }
        
        // Apply movement
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;
        
        // Keep player in bounds
        x = Math.max(0, Math.min(x, Gdx.graphics.getWidth() - width));
        y = Math.max(0, Math.min(y, Gdx.graphics.getHeight() - height));
    }
    
    @Override
    public boolean checkCollision(Entity other) {
        if (other instanceof ICollidable) {
            // If the other entity is also collidable, check for intersection
            Rectangle otherBounds = ((ICollidable) other).getBounds();
            return bounds.overlaps(otherBounds);
        }
        return false;
    }
    
    @Override
    public void handleCollision(Entity other) {
        // Handle different types of collisions
        // This will be implemented based on game mechanics
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
