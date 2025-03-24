package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.managers.ScreenManager;

public class CharacterSelectScreen implements Screen {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;
    private static final float CHARACTER_WIDTH = 200;
    private static final float CHARACTER_HEIGHT = 300;
    private static final float SPACING = 100; // Space between characters
    
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture paulTexture;
    private Texture domTexture;
    private final Rectangle paulBounds;
    private final Rectangle domBounds;
    private final Vector3 touchPoint;
    private boolean isTransitioning;
    
    public CharacterSelectScreen(Main game) {
        this.game = game;
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
        font.setColor(Color.WHITE);
        
        // Load character textures with correct filenames
        paulTexture = new Texture("paul walker.png");
        domTexture = new Texture("dominic.png");
        
        // Calculate positions for character frames
        float centerX = WORLD_WIDTH / 2;
        float centerY = WORLD_HEIGHT / 2;
        
        // Create bounds for click detection
        paulBounds = new Rectangle(centerX - CHARACTER_WIDTH - SPACING/2, centerY - CHARACTER_HEIGHT/2, 
                                 CHARACTER_WIDTH, CHARACTER_HEIGHT);
        domBounds = new Rectangle(centerX + SPACING/2, centerY - CHARACTER_HEIGHT/2, 
                                CHARACTER_WIDTH, CHARACTER_HEIGHT);
        
        touchPoint = new Vector3();
        isTransitioning = false;
    }
    
    @Override
    public void render(float delta) {
        if (isTransitioning) {
            return;
        }

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Handle input
        if (Gdx.input.justTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            
            if (paulBounds.contains(touchPoint.x, touchPoint.y)) {
                // Player selected Paul Walker
                isTransitioning = true;
                PlayScreen.setSelectedCharacter("paul");
                ScreenManager.getInstance().showScreen("PLAY");
            } else if (domBounds.contains(touchPoint.x, touchPoint.y)) {
                // Player selected Dominic Toretto
                isTransitioning = true;
                PlayScreen.setSelectedCharacter("dom");
                ScreenManager.getInstance().showScreen("PLAY");
            }
        }
        
        batch.begin();
        
        // Draw title
        String title = "Choose Your Character";
        float titleWidth = font.draw(batch, title, 0, 0).width;
        font.draw(batch, title, (WORLD_WIDTH - titleWidth) / 2, WORLD_HEIGHT - 50);
        
        // Draw character frames and names
        batch.draw(paulTexture, paulBounds.x, paulBounds.y, paulBounds.width, paulBounds.height);
        batch.draw(domTexture, domBounds.x, domBounds.y, domBounds.width, domBounds.height);
        
        // Draw character names
        String paulName = "Paul Walker";
        String domName = "Dominic Toretto";
        float paulNameWidth = font.draw(batch, paulName, 0, 0).width;
        float domNameWidth = font.draw(batch, domName, 0, 0).width;
        
        font.draw(batch, paulName, paulBounds.x + (CHARACTER_WIDTH - paulNameWidth) / 2, paulBounds.y - 20);
        font.draw(batch, domName, domBounds.x + (CHARACTER_WIDTH - domNameWidth) / 2, domBounds.y - 20);
        
        batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }
    
    @Override
    public void dispose() {
        if (batch != null) {
            try {
                batch.dispose();
                batch = null;
            } catch (Exception e) {
                // Ignore buffer disposal errors
            }
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (paulTexture != null) {
            paulTexture.dispose();
            paulTexture = null;
        }
        if (domTexture != null) {
            domTexture.dispose();
            domTexture = null;
        }
    }
    
    @Override public void show() {
        isTransitioning = false;
    }
    
    @Override public void hide() {
        isTransitioning = true;
    }
    
    @Override public void pause() {}
    @Override public void resume() {}
} 