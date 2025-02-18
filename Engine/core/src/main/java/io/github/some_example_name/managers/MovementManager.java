package io.github.some_example_name.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.entities.Entity;
import io.github.some_example_name.entities.Player;

public class MovementManager {
    private float acceleration;
    private float maxSpeed;
    private Vector2 velocity = new Vector2();
    
    public MovementManager(float acceleration, float maxSpeed) {
        this.acceleration = acceleration;
        this.maxSpeed = maxSpeed;
    }
    
    public Vector2 calculate_movement(Entity entity, float deltaTime) {
        // Reset velocity
        velocity.set(0, 0);
        
        if (entity instanceof Player) {
            // Handle input for Player
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocity.x = -maxSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocity.x = maxSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                velocity.y = maxSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                velocity.y = -maxSpeed;
            }
        }
        
        return velocity.cpy(); // Return a copy of the velocity vector
    }
    
    public float getSpeed() {
        return maxSpeed;
    }
    
    public void setSpeed(float speed) {
        this.maxSpeed = speed;
    }
}