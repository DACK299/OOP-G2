import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

public class EntityManager {
    private float width;
    private float height;
    private float x;
    private float y;
    private List<Entity> entities;

    public EntityManager() {
        entities = new ArrayList<>();
    }

    public void render(SpriteBatch batch) {
        for (Entity entity : entities) {
            // Implement rendering logic for each entity
        }
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            // Update logic for each entity
        }
    }

    // Getters and setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    // Entity management methods
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
