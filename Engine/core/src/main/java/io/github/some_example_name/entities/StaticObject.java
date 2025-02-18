package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StaticObject extends Entity {
    private ShapeRenderer shapeRenderer;
    private Color color;
    
    public StaticObject(float x, float y, float width, float height, Color color) {
        super(x, y, width, height);
        this.color = color;
        shapeRenderer = new ShapeRenderer();
    }
    
    @Override
    public void update(float deltaTime) {
        // Static objects don't need to update
    }
    
    @Override
    public void render(SpriteBatch batch) {
        // End SpriteBatch before using ShapeRenderer
        batch.end();
        
        // Draw the static object
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        
        // Begin SpriteBatch again
        batch.begin();
    }
    
    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}