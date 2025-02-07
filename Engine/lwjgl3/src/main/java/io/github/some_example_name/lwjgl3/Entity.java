package io.github.some_example_name.lwjgl3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Entity {
    protected float width;
    protected float height;
    protected float x;
    protected float y;
    protected EntityType type;
    protected Rectangle bounds;

    public enum EntityType {
        CIRCLE,
        TRIANGLE,
        SQUARE
    }

    public Entity(float x, float y, float width, float height, EntityType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void add_entity() {
        // Implementation for adding entity to the game world
    }

    public void remove_entity() {
        // Implementation for removing entity from the game world
    }

    public void update_all_entities() {
        // Update logic for all entities
    }

    public void render_all_entities() {
        // Render logic for all entities
    }

    public void handle_collisions() {
        // Basic collision handling logic
    }

    public void shape_triangle() {
        // Triangle shape specific logic
    }

    // Getters and setters
    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float x) { 
        this.x = x;
        bounds.x = x;
    }
    public void setY(float y) { 
        this.y = y;
        bounds.y = y;
    }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Rectangle getBounds() { return bounds; }
}
