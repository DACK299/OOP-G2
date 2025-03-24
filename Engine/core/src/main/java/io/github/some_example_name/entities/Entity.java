package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.some_example_name.managers.PhysicsManager;

public abstract class Entity {
    protected float x, y;
    protected float width, height;
    protected Body body; // Box2D physics body
    
    public Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public abstract void update(float deltaTime);
    
    // Split rendering into shapes and sprites
    public void renderShape(ShapeRenderer shapeRenderer) {
        // Default empty implementation
    }
    
    public void renderSprite(SpriteBatch batch) {
        // Default empty implementation
    }
    
    public abstract void dispose();
    
    // Update entity position from Box2D body
    protected void updatePositionFromBody() {
        if (body != null) {
            // Convert Box2D position (center) to top-left origin
            x = PhysicsManager.toPixels(body.getPosition().x) - width/2;
            y = PhysicsManager.toPixels(body.getPosition().y) - height/2;
        }
    }
    
    // Getters and setters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public Body getBody() { return body; }
    public void setBody(Body body) { this.body = body; }
}