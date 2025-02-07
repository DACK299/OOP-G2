package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player extends Entity {
    private float speed = 200f;
    private float health = 100f;
    private boolean isInvulnerable = false;
    private float invulnerabilityTimer = 0f;

    public Player(float x, float y) {
        super(x, y, 50, 50, EntityType.SQUARE);
    }

    @Override
    public void update(float deltaTime) {
        // Call parent update first to handle basic movement
        super.update(deltaTime);

        // Handle player-specific movement
        float moveX = 0;
        float moveY = 0;

        // Handle keyboard input
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX -= speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX += speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveY += speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveY -= speed;
        }

        // Update velocity
        setVelocity(moveX, moveY);

        // Update invulnerability
        if (isInvulnerable) {
            invulnerabilityTimer -= deltaTime;
            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
            }
        }
    }

    public void takeDamage(float damage) {
        if (!isInvulnerable) {
            health -= damage;
            isInvulnerable = true;
            invulnerabilityTimer = 1.0f; // 1 second of invulnerability
        }
    }

    public void heal(float amount) {
        health = Math.min(health + amount, 100f);
    }

    public float getHealth() {
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }
}