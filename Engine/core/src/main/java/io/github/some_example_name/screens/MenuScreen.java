package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import io.github.some_example_name.Main;
import io.github.some_example_name.managers.IOManager;
import io.github.some_example_name.managers.MovementManager;
import io.github.some_example_name.managers.ScreenManager;

public class MenuScreen implements Screen {

    private Main game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private final IOManager ioManager;
    private final MovementManager movementManager;

    // Textures and Fonts
    private Texture background;
    private BitmapFont font;

    // "Play" button
    private float playButtonX, playButtonY;
    private float playButtonWidth, playButtonHeight;

    // "Exit" button
    private float exitButtonX, exitButtonY;
    private float exitButtonWidth, exitButtonHeight;

    public MenuScreen(Main game) {
        this.game = game;
        this.ioManager = ScreenManager.getInstance().getIOManager();
        this.movementManager = ScreenManager.getInstance().getMovementManager();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        batch = new SpriteBatch();

        init();
    }

    private void init() {
        // Load your menu items, textures, fonts, etc.
        background = new Texture("MENU_Background.jpg");
        font = new BitmapFont(); // By default uses a built-in font

        // Initialize "Play" button area
        playButtonWidth  = 200;
        playButtonHeight = 50;
        playButtonX = (800 - playButtonWidth) / 2f; // Center horizontally
        playButtonY = 200;                         // Position it vertically

        // Initialize "Exit" button area (similar size, lower on the screen)
        exitButtonWidth  = 200;
        exitButtonHeight = 50;
        exitButtonX = (800 - exitButtonWidth) / 2f; // Center horizontally
        exitButtonY = 120;                          // Slightly below the Play button
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, 800, 600);

        // Draw title
        font.getData().setScale(2f);
        font.draw(batch, "INF1009 Team 2 Engine Demo", 200, 500);

        // Draw the buttons
        font.getData().setScale(1.5f);
        // "PLAY" button text
        font.draw(batch, "PLAY", playButtonX + 60, playButtonY + 35);
        // "EXIT" button text
        font.draw(batch, "EXIT", exitButtonX + 60, exitButtonY + 35);

        batch.end();

        // Handle button clicks
        handleInput();
    }

    /**
     * Detect if the user clicked/touched any button.
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            // Convert screen coordinates to our camera's coordinate system
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            float tx = touchPos.x;
            float ty = touchPos.y;

            // Check "Play" button
            if (tx >= playButtonX && tx <= playButtonX + playButtonWidth &&
                ty >= playButtonY && ty <= playButtonY + playButtonHeight) {
                startGame();
            }

            // Check "Exit" button
            else if (tx >= exitButtonX && tx <= exitButtonX + exitButtonWidth &&
                ty >= exitButtonY && ty <= exitButtonY + exitButtonHeight) {
                exitGame();
            }
        }
    }

    /**
     * Transition to the Play screen
     */
    private void startGame() {
        ScreenManager.getInstance().showScreen("PLAY");
    }

    /**
     * Exit the application (works on desktop; may behave differently on mobile)
     */
    private void exitGame() {
        Gdx.app.exit();
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        font.dispose();
    }

    @Override
    public void show() {}
    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
