package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StaticObject extends Entity {
    private Color color;
    
    public StaticObject(float x, float y, float width, float height, Color color) {
        super(x, y, width, height);
        this.color = color;
    }
    
    @Override
    public void update(float deltaTime) {
        // Static objects don't need to update
    }
    
    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width, height);
    }
    
    @Override
    public void renderSprite(SpriteBatch batch) {
        // No sprites to render
    }
    
    @Override
    public void dispose() {
        // Nothing to dispose
    }
}