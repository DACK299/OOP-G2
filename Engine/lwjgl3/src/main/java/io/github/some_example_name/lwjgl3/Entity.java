package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Entity {
    private Vector2 position;
    private Vector2 velocity;
    protected float width;
    protected float height;
    protected EntityType type;
    protected Rectangle bounds;
    private boolean isActive;

    public enum EntityType {
        CIRCLE,
        TRIANGLE,
        SQUARE
    }

    public Entity(float x, float y, float width, float height, EntityType type) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.width = width;
        this.height = height;
        this.type = type;
        this.bounds = new Rectangle(x, y, width, height);
        this.isActive = true;
    }

    public void update(float deltaTime) {
        // Update position based on velocity
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        updateBounds();
    }

    private void updateBounds() {
        bounds.setPosition(position.x, position.y);
    }

    // Position methods
    public void setPosition(float x, float y) {
        position.set(x, y);
        updateBounds();
    }

    public void translate(float dx, float dy) {
        position.add(dx, dy);
        updateBounds();
    }

    public void setVelocity(float vx, float vy) {
        velocity.set(vx, vy);
    }

    // Getters and setters
    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Rectangle getBounds() { return bounds; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}