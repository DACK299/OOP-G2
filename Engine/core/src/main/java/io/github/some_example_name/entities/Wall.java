package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Wall extends Entity implements ICollidable {
    private Rectangle bounds;
    private ShapeRenderer shapeRenderer;
    
    public Wall(float x, float y, float width, float height) {
        super(x, y, width, height);
        // Create collision bounds
        bounds = new Rectangle(x, y, width, height);
        shapeRenderer = new ShapeRenderer();
    }
    
    @Override
    public void update(float deltaTime) {
        // Walls don't need to update
    }
    
    @Override
    public void render(SpriteBatch batch) {
        // End the SpriteBatch before using ShapeRenderer
        batch.end();
        
        // Draw the wall
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GRAY);
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
        // Walls don't need to handle collisions
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