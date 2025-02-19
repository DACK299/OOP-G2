package io.github.some_example_name.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.entities.Entity;
import io.github.some_example_name.entities.ICollidable;
import io.github.some_example_name.entities.Obstacle;

public class EntityManager {
    private Array<Entity> entities;
    private CollisionManager collisionManager;
    
    public EntityManager() {
        entities = new Array<>();
        collisionManager = new CollisionManager();
    }
    
    public void add_entity(Entity entity) {
        entities.add(entity);
        
        // If entity is collidable, register with collision manager
        if (entity instanceof ICollidable) {
            collisionManager.addCollidable((ICollidable) entity);
        }
    }
    
    public void remove_entity(Entity entity) {
        entities.removeValue(entity, true);
        
        // If entity is collidable, remove from collision manager
        if (entity instanceof ICollidable) {
            collisionManager.removeCollidable((ICollidable) entity);
        }
    }
    
    public Array<Entity> getEntities() {
        return entities;
    }
    
    public void update(float deltaTime, IOManager ioManager, MovementManager movementManager) {
        // Update all entities
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
        
        // Handle collisions
        collisionManager.detectAndHandleCollisions();
        
     // Remove any obstacles that should be removed
        Array<Entity> entitiesToRemove = new Array<>();
        for (Entity entity : entities) {
            if (entity instanceof Obstacle && ((Obstacle) entity).shouldBeRemoved()) {
                entitiesToRemove.add(entity);
            }
        }
        
        // Remove the marked entities
        for (Entity entity : entitiesToRemove) {
            remove_entity(entity);
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
        collisionManager.clear();
    }
    
    // New helper method to find entities by type
    public <T extends Entity> Array<T> getEntitiesByType(Class<T> type) {
        Array<T> result = new Array<>();
        for (Entity entity : entities) {
            if (type.isInstance(entity)) {
                result.add(type.cast(entity));
            }
        }
        return result;
    }
    
    // Getter for the collision manager
    public CollisionManager getCollisionManager() {
        return collisionManager;
    }
}