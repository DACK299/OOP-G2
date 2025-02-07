package io.github.some_example_name.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.Main;
import io.github.some_example_name.managers.ScreenManager;

public class MenuScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    
    public MenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        batch = new SpriteBatch();
        init();
    }
    
    private void init() {
        // Initialize menu items, load assets, etc.
    }
    
    @Override
    public void render(float delta) {
        // Render menu items
        batch.begin();
        // Draw menu items
        batch.end();
    }
    
    public void startGame() {
        ScreenManager.getInstance().showScreen("PLAY");
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