package io.github.some_example_name.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.managers.SoundManager;

public class Car extends Entity implements IMovable, ICollidable {
    private ShapeRenderer shapeRenderer;
    private Rectangle bounds;
    private Vector2 velocity;
    private boolean isCollided = false;
    private static final float SPEED = 300f; // vertical movement speed
    private static final float LANE_HEIGHT = 80f;
    private Vector2 previousPosition;
    private Texture carTexture;
    
    public Car(float x, float y, float width, float height) {
        super(x, y, width, height);
        shapeRenderer = new ShapeRenderer();
        bounds = new Rectangle(x, y, width, height);
        velocity = new Vector2(0, 0);
        previousPosition = new Vector2(x, y);
        
        // Load car texture if available
        try {
            carTexture = new Texture(Gdx.files.internal("player_car.png"));
        } catch (Exception e) {
            // Texture not found, will use shape renderer as fallback
            carTexture = null;
        }
    }
    
    @Override
    public void update(float deltaTime) {
        if (!isCollided) {
            // Store previous position
            previousPosition.set(x, y);
            
            // Handle movement
            move(deltaTime);
            
            // Update collision bounds
            bounds.setPosition(x, y);
        }
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
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.rect(x, y, width, height);
            
            // Add some details to make it look like a car
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.rect(x + width * 0.7f, y + height * 0.25f, width * 0.2f, height * 0.5f); // windows
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.circle(x + width * 0.2f, y, height * 0.25f, 8); // front wheel
            shapeRenderer.circle(x + width * 0.8f, y, height * 0.25f, 8); // back wheel
            
            shapeRenderer.end();
            
            batch.begin();
        }
    }
    
    @Override
    public void move(float deltaTime) {
        // Reset velocity
        velocity.set(0, 0);
        
        // Handle up/down movement (lane changing)
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocity.y = SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocity.y = -SPEED;
        }
        
        // Apply movement
        y += velocity.y * deltaTime;
        
        // Keep car within screen bounds
        y = Math.max(0, Math.min(y, Gdx.graphics.getHeight() - height));
    }
    
    @Override
    public boolean checkCollision(Entity other) {
        if (other instanceof ICollidable && !isCollided) {
            if (other instanceof ObstacleCar) {
                Rectangle otherBounds = ((ICollidable) other).getBounds();
                return bounds.overlaps(otherBounds);
            }
        }
        return false;
    }
    
    @Override
    public void handleCollision(Entity other) {
        if (other instanceof ObstacleCar) {
            isCollided = true;
            SoundManager.getInstance().playSound("wall_collision");
        }
    }
    
    public boolean isCollided() {
        return isCollided;
    }
    
    public void resetCollision() {
        isCollided = false;
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
}
