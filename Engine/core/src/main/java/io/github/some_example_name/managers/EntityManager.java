package io.github.some_example_name.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.entities.Entity;

public class EntityManager {
    private Array<Entity> entities;
    
    public EntityManager() {
        entities = new Array<>();
    }
    
    public void add_entity(Entity entity) {
        entities.add(entity);
    }
    
    public Array<Entity> getEntities() {
        return entities;
    }
    
    public void update(float deltaTime, IOManager ioManager, MovementManager movementManager) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }
    
    public void render(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);
        }
    }
    
    public void dispose() {
        for (Entity entity : entities) {
            entity.dispose();
        }
        entities.clear();
    }
}