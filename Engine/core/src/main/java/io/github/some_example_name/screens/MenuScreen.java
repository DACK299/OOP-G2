package io.github.some_example_name.screens;

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
import io.github.some_example_name.managers.ScreenManager;
import io.github.some_example_name.settings.Settings;
import io.github.some_example_name.utils.GraphicsHelper;

public class MenuScreen implements Screen {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;
    private static final float BUTTON_WIDTH = 200;
    private static final float BUTTON_HEIGHT = 50;
    private static final float BUTTON_SPACING = 30; // Space between buttons
    
    private final Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private Rectangle playBounds;
    private Rectangle highScoreBounds; // New high score button
    private Rectangle settingsBounds;
    private Rectangle exitBounds;
    private Rectangle fullscreenBounds;
    private Vector3 touchPoint;
    private Texture backgroundTexture;
    private GlyphLayout glyphLayout;
    private boolean isFullscreen = false;
    
    // Pre-calculated text positions
    private float titleX;
    private float titleY;
    private float playTextX;
    private float playTextY;
    private float highScoreTextX; // New high score text position
    private float highScoreTextY; // New high score text position
    private float settingsTextX;
    private float settingsTextY;
    private float exitTextX;
    private float exitTextY;
    private float fullscreenTextX;
    private float fullscreenTextY;

    public MenuScreen(Main game) {
        this.game = game;
        
        // Initialize camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        // Initialize rendering objects
        batch = new SpriteBatch();
        glyphLayout = new GlyphLayout();
        
        // Load background texture
        backgroundTexture = new Texture(Gdx.files.internal("bg.png"));
        
        // Create fonts with different sizes
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3);
        titleFont.setColor(Color.WHITE);
        
        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(2);
        buttonFont.setColor(Color.WHITE);
        
        // Calculate center position for buttons
        float centerX = (WORLD_WIDTH - BUTTON_WIDTH) / 2;
        float startY = 400; // Start position for first button
        
        // Create button rectangles vertically aligned
        playBounds = new Rectangle(centerX, startY, BUTTON_WIDTH, BUTTON_HEIGHT);
        highScoreBounds = new Rectangle(centerX, startY - BUTTON_HEIGHT - BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT); // New high score button
        settingsBounds = new Rectangle(centerX, startY - (BUTTON_HEIGHT + BUTTON_SPACING) * 2, BUTTON_WIDTH, BUTTON_HEIGHT);
        fullscreenBounds = new Rectangle(centerX, startY - (BUTTON_HEIGHT + BUTTON_SPACING) * 3, BUTTON_WIDTH, BUTTON_HEIGHT);
        exitBounds = new Rectangle(centerX, startY - (BUTTON_HEIGHT + BUTTON_SPACING) * 4, BUTTON_WIDTH, BUTTON_HEIGHT);
        
        touchPoint = new Vector3();
        
        // Pre-calculate text positions
        String title = "Drinking and Driving";
        glyphLayout.setText(titleFont, title);
        titleX = (WORLD_WIDTH - glyphLayout.width) / 2;
        titleY = 500;
        
        glyphLayout.setText(buttonFont, "PLAY");
        playTextX = playBounds.x + (BUTTON_WIDTH - glyphLayout.width) / 2;
        playTextY = playBounds.y + 35;
        
        glyphLayout.setText(buttonFont, "HIGH SCORES"); // New high score text
        highScoreTextX = highScoreBounds.x + (BUTTON_WIDTH - glyphLayout.width) / 2;
        highScoreTextY = highScoreBounds.y + 35;
        
        settingsTextX = settingsBounds.x + 10; // Fixed position since text changes
        settingsTextY = settingsBounds.y + 35;
        
        fullscreenTextX = fullscreenBounds.x + 10; // Fixed position since text changes
        fullscreenTextY = fullscreenBounds.y + 35;
        
        glyphLayout.setText(buttonFont, "EXIT");
        exitTextX = exitBounds.x + (BUTTON_WIDTH - glyphLayout.width) / 2;
        exitTextY = exitBounds.y + 35;
        
        // Check if we're already in fullscreen
        isFullscreen = Gdx.graphics.isFullscreen();
    }

    @Override
    public void render(float delta) {
        try {
            // Ensure resources are available
            if (batch == null || batch.isDrawing()) {
                return;
            }
            
            // Clear screen
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            camera.update();
            batch.setProjectionMatrix(camera.combined);

            // Handle input
            handleInput();

            batch.begin();
            try {
                // Draw background
                if (backgroundTexture != null) {
                    batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
                }
                
                // Draw title
                if (titleFont != null) {
                    titleFont.draw(batch, "Drinking and Driving", titleX, titleY);
                }
                
                if (buttonFont != null) {
                    // Draw buttons with centered text
                    buttonFont.draw(batch, "PLAY", playTextX, playTextY);
                    buttonFont.draw(batch, "HIGH SCORES", highScoreTextX, highScoreTextY); // New high score button
                    
                    Settings settings = Settings.getInstance();
                    String controlText = settings.getControlScheme() == Settings.ControlScheme.ARROWS ? "Controls: Arrows" : "Controls: WASD";
                    buttonFont.draw(batch, controlText, settingsTextX, settingsTextY);
                    
                    String fullscreenText = isFullscreen ? "Fullscreen: ON" : "Fullscreen: OFF";
                    buttonFont.draw(batch, fullscreenText, fullscreenTextX, fullscreenTextY);
                    
                    buttonFont.draw(batch, "EXIT", exitTextX, exitTextY);
                }
            } finally {
                if (batch.isDrawing()) {
                    batch.end();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in MenuScreen render: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        try {
            if (batch != null) {
                if (batch.isDrawing()) {
                    batch.end();
                }
                batch.dispose();
                batch = null;
            }
            if (titleFont != null) {
                titleFont.dispose();
                titleFont = null;
            }
            if (buttonFont != null) {
                buttonFont.dispose();
                buttonFont = null;
            }
            if (backgroundTexture != null) {
                backgroundTexture.dispose();
                backgroundTexture = null;
            }
        } catch (Exception e) {
            System.err.println("Error disposing MenuScreen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void show() {
        // Reinitialize resources if they were disposed
        if (batch == null) {
            batch = new SpriteBatch();
        }
        if (glyphLayout == null) {
            glyphLayout = new GlyphLayout();
        }
        if (backgroundTexture == null) {
            backgroundTexture = new Texture(Gdx.files.internal("MENU_Background.jpg"));
        }
        if (titleFont == null) {
            titleFont = new BitmapFont();
            titleFont.getData().setScale(3);
            titleFont.setColor(Color.WHITE);
        }
        if (buttonFont == null) {
            buttonFont = new BitmapFont();
            buttonFont.getData().setScale(2);
            buttonFont.setColor(Color.WHITE);
        }
        
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

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
                // Go to character selection screen first
                ScreenManager.getInstance().showScreen("CHARACTER_SELECT");
            } else if (highScoreBounds.contains(touchPoint.x, touchPoint.y)) {
                // Show high scores screen
                ScreenManager.getInstance().showScreen("HIGH_SCORES");
            } else if (settingsBounds.contains(touchPoint.x, touchPoint.y)) {
                Settings settings = Settings.getInstance();
                if (settings.getControlScheme() == Settings.ControlScheme.ARROWS) {
                    settings.setControlScheme(Settings.ControlScheme.WASD);
                } else {
                    settings.setControlScheme(Settings.ControlScheme.ARROWS);
                }
            } else if (fullscreenBounds.contains(touchPoint.x, touchPoint.y)) {
                toggleFullscreen();
            } else if (exitBounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.exit();
            }
        }
    }

    private void toggleFullscreen() {
        boolean success;
        
        if (isFullscreen) {
            // Switch to windowed mode
            success = GraphicsHelper.setWindowedMode(800, 600);
        } else {
            // Switch to fullscreen
            success = GraphicsHelper.setFullscreenMode();
        }
        
        if (success) {
            isFullscreen = !isFullscreen;
            // Update viewport after changing display mode
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }
}
