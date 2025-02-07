package io.github.some_example_name.managers;

import java.util.ArrayList;
import java.util.List;
import io.github.some_example_name.entities.ICollidable;

public class CollisionManager {
    private List<ICollidable> collidables;
    
    public CollisionManager() {
        collidables = new ArrayList<>();
    }
    
    public void detectCollisions() {
        // Implement collision detection
    }
    
    public void handleCollisions() {
        // Implement collision handling
    }
}