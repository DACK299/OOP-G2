package io.github.some_example_name.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.Main;
import io.github.some_example_name.managers.EntityManager;

public class PlayScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private EntityManager entityManager;
    
    public PlayScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        batch = new SpriteBatch();
        entityManager = new EntityManager();
        init();
    }
    
    private void init() {
        // Initialize game objects, load assets, etc.
    }
    
    @Override
    public void render(float delta) {
        // Update
        entityManager.update(delta);
        
        // Render
        batch.begin();
        entityManager.render(batch);
        batch.end();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
    }
    
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}