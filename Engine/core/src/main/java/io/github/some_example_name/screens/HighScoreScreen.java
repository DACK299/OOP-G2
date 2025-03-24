package io.github.some_example_name.screens;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.managers.ScoreManager;
import io.github.some_example_name.managers.ScreenManager;

public class HighScoreScreen implements Screen {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;
    private static final float BUTTON_WIDTH = 200;
    private static final float BUTTON_HEIGHT = 50;
    
    private final Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont scoreFont;
    private BitmapFont buttonFont;
    private Texture backgroundTexture;
    private Rectangle backBounds;
    private Vector3 touchPoint;
    
    public HighScoreScreen(Main game) {
        this.game = game;
        
        // Initialize camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        // Initialize rendering objects
        batch = new SpriteBatch();
        
        // Load background texture
        backgroundTexture = new Texture(Gdx.files.internal("bg.png"));
        
        // Create fonts with different sizes
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3);
        titleFont.setColor(Color.WHITE);
        
        scoreFont = new BitmapFont();
        scoreFont.getData().setScale(2);
        scoreFont.setColor(Color.YELLOW);
        
        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(2);
        buttonFont.setColor(Color.WHITE);
        
        // Create back button
        float centerX = (WORLD_WIDTH - BUTTON_WIDTH) / 2;
        float buttonY = 100;
        backBounds = new Rectangle(centerX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        
        touchPoint = new Vector3();
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Handle input
        if (Gdx.input.justTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            
            if (backBounds.contains(touchPoint.x, touchPoint.y)) {
                ScreenManager.getInstance().showScreen("MENU");
            }
        }
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        // Draw background
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        
        // Draw title
        String title = "HIGH SCORES";
        GlyphLayout titleLayout = new GlyphLayout(titleFont, title);
        titleFont.draw(batch, title, (WORLD_WIDTH - titleLayout.width) / 2, 500);
        
        // Draw high scores - using loadScores() method from Engine project
        List<Integer> scores = ScoreManager.getInstance().loadScores();
        float y = 400;
        for (int i = 0; i < Math.min(10, scores.size()); i++) {
            String scoreText = (i + 1) + ". " + scores.get(i);
            scoreFont.draw(batch, scoreText, 300, y);
            y -= 40;
        }
        
        if (scores.isEmpty()) {
            scoreFont.draw(batch, "No scores yet!", 300, 400);
        }
        
        // Draw back button
        buttonFont.draw(batch, "BACK", backBounds.x + 65, backBounds.y + 35);
        
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void show() {
        // Reset camera position
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void hide() {
        // Nothing to do when hiding
    }

    @Override
    public void pause() {
        // Nothing to do when pausing
    }

    @Override
    public void resume() {
        // Nothing to do when resuming
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (scoreFont != null) {
            scoreFont.dispose();
        }
        if (buttonFont != null) {
            buttonFont.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
