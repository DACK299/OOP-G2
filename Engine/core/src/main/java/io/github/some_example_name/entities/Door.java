package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Door extends Entity implements ICollidable {
    private Rectangle bounds;
    private ShapeRenderer shapeRenderer;
    private String targetScreen;
    private boolean isPlayerColliding = false;
    
    public Door(float x, float y, float width, float height, String targetScreen) {
        super(x, y, width, height);
        this.targetScreen = targetScreen;
        bounds = new Rectangle(x, y, width, height);
        shapeRenderer = new ShapeRenderer();
    }
    
    @Override
    public void update(float deltaTime) {
        // No screen transition logic here
    }
    
    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BROWN);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        
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
        if (e instanceof Player) {
            isPlayerColliding = true;
        }
    }
    
    public boolean isPlayerColliding() {
        return isPlayerColliding;
    }
    
    public void resetCollision() {
        isPlayerColliding = false;
    }
    
    public String getTargetScreen() {
        return targetScreen;
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