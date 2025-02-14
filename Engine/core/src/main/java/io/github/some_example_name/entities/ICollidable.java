package io.github.some_example_name.entities;

import com.badlogic.gdx.math.Rectangle;

public interface ICollidable {
    boolean checkCollision(Entity e);
    void handleCollision(Entity e);
    Rectangle getBounds();
}