package io.github.some_example_name.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import io.github.some_example_name.entities.Entity;
import io.github.some_example_name.entities.ICollidable;
import io.github.some_example_name.entities.Obstacle;

public class EntityManager {
    private Array<Entity> entities;
    private CollisionManager collisionManager;
    private final Pool<Obstacle> obstaclePool = new Pool<Obstacle>() {
        @Override
        protected Obstacle newObject() {
            return new Obstacle(0, 0, 250, 130, 300f);
        }
    };
    
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
        if (entity == null) {
            System.err.println("Attempted to remove null entity");
            return;
        }
        
        if (!entities.contains(entity, true)) {
            System.err.println("Attempted to remove entity not in list: " + entity);
            return;
        }
        
        entities.removeValue(entity, true);
        
        // If entity is collidable, remove from collision manager
        if (entity instanceof ICollidable) {
            collisionManager.removeCollidable((ICollidable) entity);
        }
        
        // Return obstacle to pool with careful cleanup
        if (entity instanceof Obstacle) {
            try {
                // Ensure the obstacle is properly reset before returning to pool
                Obstacle obstacle = (Obstacle) entity;
                obstacle.reset();
                obstaclePool.free(obstacle);
                System.out.println("Returned obstacle to pool: " + obstacle);
            } catch (Exception e) {
                System.err.println("Error returning obstacle to pool: " + e.getMessage());
                entity.dispose(); // Dispose if we can't pool it
            }
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
        
        // No need to call collision detection - Box2D handles it internally
        
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
    
    public void renderShapes(ShapeRenderer shapeRenderer) {
        for (Entity entity : entities) {
            entity.renderShape(shapeRenderer);
        }
    }
    
    public void renderSprites(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.renderSprite(batch);
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
    
    // Improve the obstacle obtain method to ensure proper initialization
    public Obstacle obtainObstacle() {
        try {
            Obstacle obstacle = obstaclePool.obtain();
            System.out.println("Obtained obstacle from pool: " + obstacle);
            return obstacle;
        } catch (Exception e) {
            System.err.println("Error obtaining obstacle from pool: " + e.getMessage());
            // Create a new obstacle as fallback
            return new Obstacle(0, 0, 250, 130, 300f);
        }
    }
}