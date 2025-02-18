package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Collectible extends Entity implements ICollidable {
    private Rectangle bounds;
    private ShapeRenderer shapeRenderer;
    private boolean collected = false;
    private Vector2 position;
    private float elapsedTime = 0;
    
    public Collectible(float x, float y, float width, float height) {
        super(x, y, width, height);
        position = new Vector2(x, y);
        bounds = new Rectangle(x, y, width, height);
        shapeRenderer = new ShapeRenderer();
    }
    
    @Override
    public void update(float deltaTime) {
        if (!collected) {
            // Make it float up and down slightly for visual effect
            elapsedTime += deltaTime;
            float offsetY = (float) Math.sin(elapsedTime * 3) * 3;
            y = position.y + offsetY;
            bounds.setPosition(x, y);
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (!collected) {
            batch.end();
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.GOLD);
            shapeRenderer.circle(x + width/2, y + height/2, width/2);
            shapeRenderer.end();
            
            batch.begin();
        }
    }
    
    @Override
    public boolean checkCollision(Entity e) {
        if (!collected && e instanceof ICollidable) {
            Rectangle otherBounds = ((ICollidable) e).getBounds();
            return bounds.overlaps(otherBounds);
        }
        return false;
    }
    
    @Override
    public void handleCollision(Entity e) {
        if (e instanceof Player) {
            collected = true;
            // You could add scoring, sound effects, etc. here
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}