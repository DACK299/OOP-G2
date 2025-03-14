package io.github.some_example_name.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ObstacleCar extends Entity implements IMovable, ICollidable {
    private ShapeRenderer shapeRenderer;
    private Rectangle bounds;
    private Vector2 velocity;
    private float speed;
    private Texture carTexture;
    
    public ObstacleCar(float x, float y, float width, float height, float speed) {
        super(x, y, width, height);
        shapeRenderer = new ShapeRenderer();
        bounds = new Rectangle(x, y, width, height);
        velocity = new Vector2(-speed, 0);
        this.speed = speed;
        
        // Load obstacle car texture if available
        try {
            carTexture = new Texture(Gdx.files.internal("obstacle_car.png"));
        } catch (Exception e) {
            // Texture not found, will use shape renderer as fallback
            carTexture = null;
        }
    }
    
    @Override
    public void update(float deltaTime) {
        // Move obstacle car
        move(deltaTime);
        
        // Update collision bounds
        bounds.setPosition(x, y);
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (carTexture != null) {
            // Draw with texture
            batch.draw(carTexture, x, y, width, height);
        } else {
            // Draw with shape renderer as fallback
            batch.end();
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(x, y, width, height);
            
            // Add some details to make it look like a car
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(x + width * 0.1f, y + height * 0.25f, width * 0.2f, height * 0.5f); // windows
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.circle(x + width * 0.2f, y, height * 0.25f, 8); // front wheel
            shapeRenderer.circle(x + width * 0.8f, y, height * 0.25f, 8); // back wheel
            
            shapeRenderer.end();
            
            batch.begin();
        }
    }
    
    @Override
    public void move(float deltaTime) {
        // Move left at constant speed
        x += velocity.x * deltaTime;
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
        // Obstacle cars don't need to handle collisions
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        if (carTexture != null) {
            carTexture.dispose();
        }
    }
    
    // Update speed (for difficulty progression)
    public void setSpeed(float newSpeed) {
        this.speed = newSpeed;
        velocity.x = -newSpeed;
    }
}
