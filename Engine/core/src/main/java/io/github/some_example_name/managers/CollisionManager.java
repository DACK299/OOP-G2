package io.github.some_example_name.managers;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.entities.Entity;
import io.github.some_example_name.entities.ICollidable;

public class CollisionManager {
    private List<ICollidable> collidables;
    
    public CollisionManager() {
        collidables = new ArrayList<>();
    }
    
    public void addCollidable(ICollidable collidable) {
        if (!collidables.contains(collidable)) {
            collidables.add(collidable);
        }
    }
    
    public void removeCollidable(ICollidable collidable) {
        collidables.remove(collidable);
    }
    
    public void registerEntities(Array<Entity> entities) {
        // Clear existing collidables
        collidables.clear();
        
        // Add all ICollidable entities
        for (Entity entity : entities) {
            if (entity instanceof ICollidable) {
                addCollidable((ICollidable) entity);
            }
        }
    }
    
    public void detectAndHandleCollisions() {
        // Check each collidable against all others
        for (int i = 0; i < collidables.size(); i++) {
            ICollidable collidableA = collidables.get(i);
            Entity entityA = (Entity) collidableA;
            
            for (int j = i + 1; j < collidables.size(); j++) {
                ICollidable collidableB = collidables.get(j);
                Entity entityB = (Entity) collidableB;
                
                // Check for collision
                if (collidableA.checkCollision(entityB)) {
                    // Handle collision for both entities
                    collidableA.handleCollision(entityB);
                    collidableB.handleCollision(entityA);
                }
            }
        }
    }
    
    public void clear() {
        collidables.clear();
    }
}