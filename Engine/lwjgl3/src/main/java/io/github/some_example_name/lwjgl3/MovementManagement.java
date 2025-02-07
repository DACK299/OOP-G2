package io.github.some_example_name.lwjgl3;
public class MovementManagement {
    private float acceleration;
    private float max_speed;
    private float currentSpeed;

    public MovementManagement(float acceleration, float max_speed) {
        this.acceleration = acceleration;
        this.max_speed = max_speed;
        this.currentSpeed = 0;
    }

    public void calculate_movement() {
        // Calculate movement based on acceleration and max speed
        currentSpeed += acceleration;
        if (currentSpeed > max_speed) {
            currentSpeed = max_speed;
        }
    }

    public float getSpeed() {
        return currentSpeed;
    }

    public void setSpeed(float speed) {
        this.currentSpeed = Math.min(speed, max_speed);
    }

    public float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public float getMaxSpeed() {
        return max_speed;
    }

    public void setMaxSpeed(float max_speed) {
        this.max_speed = max_speed;
    }
}
