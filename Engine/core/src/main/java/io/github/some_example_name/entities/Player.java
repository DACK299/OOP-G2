package io.github.some_example_name.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.managers.MovementManager;

public class Player extends Entity implements IMovable, ICollidable {
    private ShapeRenderer shapeRenderer;
    private Rectangle bounds;
    private Vector2 velocity;
    private final float SPEED = 200; // pixels per second
    private Vector2 previousPosition;
    private MovementManager movementManager;
    
    public Player(float x, float y, float width, float height) {
        super(x, y, width, height);
        shapeRenderer = new ShapeRenderer();
        bounds = new Rectangle(x, y, width, height);
        velocity = new Vector2(0, 0);
        previousPosition = new Vector2(x, y);
        movementManager = new MovementManager(0, SPEED);
    }
    
    @Override
    public void update(float deltaTime) {
        // Store previous position before moving
        previousPosition.set(x, y);
        
        // Handle movement
        move(deltaTime);
        
        // Update collision bounds
        bounds.setPosition(x, y);
    }
    
    @Override
    public void render(SpriteBatch batch) {
        // End SpriteBatch before using ShapeRenderer
        batch.end();
        
        // Draw the player
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        
        // Begin SpriteBatch again
        batch.begin();
    }
    
    @Override
    public void move(float deltaTime) {
        // Get velocity from movement manager
        Vector2 newVelocity = movementManager.calculate_movement(this, deltaTime);
        velocity.set(newVelocity);
        
        // Apply movement
        x += velocity.x * deltaTime;
        y += velocity.y * deltaTime;
        
        // Keep player in screen bounds
        x = Math.max(0, Math.min(x, Gdx.graphics.getWidth() - width));
        y = Math.max(0, Math.min(y, Gdx.graphics.getHeight() - height));
    }
    
    @Override
    public boolean checkCollision(Entity other) {
        if (other instanceof ICollidable) {
            Rectangle otherBounds = ((ICollidable) other).getBounds();
            return bounds.overlaps(otherBounds);
        }
        return false;
    }
    
    @Override
    public void handleCollision(Entity other) {
        // On collision, move back to previous position
        x = previousPosition.x;
        y = previousPosition.y;
        bounds.setPosition(x, y);
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}