package io.github.some_example_name.managers;

import io.github.some_example_name.entities.Entity;

public class MovementManager {
    private float acceleration;
    private float maxSpeed;
    
    public MovementManager(float acceleration, float maxSpeed) {
        this.acceleration = acceleration;
        this.maxSpeed = maxSpeed;
    }
    
    public void calculate_movement(Entity entity, float deltaTime) {
        // Implement movement calculations here
    }
    
    public float getSpeed() {
        return maxSpeed;
    }
    
    public void setSpeed(float speed) {
        this.maxSpeed = speed;
    }
}