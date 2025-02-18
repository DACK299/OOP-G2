package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.managers.ScreenManager;

public class Door extends Entity implements ICollidable {
    private Rectangle bounds;
    private ShapeRenderer shapeRenderer;
    private String targetScreen;
    private boolean playerCollided = false;
    
    public Door(float x, float y, float width, float height, String targetScreen) {
        super(x, y, width, height);
        this.targetScreen = targetScreen;
        bounds = new Rectangle(x, y, width, height);
        shapeRenderer = new ShapeRenderer();
    }
    
    @Override
    public void update(float deltaTime) {
        // Check if player has collided and handle screen transition
        if (playerCollided) {
            ScreenManager.getInstance().showScreen(targetScreen);
            playerCollided = false;
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        // End the SpriteBatch before using ShapeRenderer
        batch.end();
        
        // Draw the door
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BROWN);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        
        // Begin the SpriteBatch again
        batch.begin();
    }
    
    @Override
    public boolean checkCollision(Entity e) {
        if (e instanceof ICollidable) {
            Rectangle otherBounds = ((ICollidable) e).getBounds();
            return bounds.overlaps(otherBounds);
        }
        return false;
    }
    
    @Override
    public void handleCollision(Entity e) {
        // Only trigger screen change if it's the player
        if (e instanceof Player) {
            playerCollided = true;
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    public String getTargetScreen() {
        return targetScreen;
    }
    
    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}