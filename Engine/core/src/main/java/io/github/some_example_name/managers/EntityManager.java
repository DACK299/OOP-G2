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
    
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }
    
    public void render(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);
        }
    }
}