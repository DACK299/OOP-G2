package io.github.some_example_name.lwjgl3;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;
import java.util.Map;

public class SceneManagement {
    private Game game;
    private Map<String, Screen> screens;
    private String currentScreenKey;
    private Texture backgroundTexture;
    private boolean inTransition;
    private String background;

    public SceneManagement(Game game) {
        this.game = game;
        this.screens = new HashMap<>();
        this.inTransition = false;
    }

    // Screen management methods
    public void addScreen(String screenKey, Screen screen) {
        screens.put(screenKey, screen);
    }

    public void removeScreen(String screenKey) {
        Screen screen = screens.remove(screenKey);
        if (screen != null) {
            screen.dispose();
        }
    }

    public void switchToScreen(String screenKey) {
        if (!screens.containsKey(screenKey)) {
            throw new IllegalArgumentException("Screen " + screenKey + " not found!");
        }

        inTransition = true;
        
        // Hide current screen if exists
        if (currentScreenKey != null && game.getScreen() != null) {
            game.getScreen().hide();
        }

        // Set and show new screen
        Screen newScreen = screens.get(screenKey);
        game.setScreen(newScreen);
        newScreen.show();
        
        currentScreenKey = screenKey;
        inTransition = false;
    }

    // Background management methods
    public void load_scene(String backgroundPath) {
        this.background = backgroundPath;
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        backgroundTexture = new Texture(backgroundPath);
    }

    public void render(SpriteBatch batch) {
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0);
        }
    }

    public void update() {
        // Update scene logic
        if (inTransition) {
            // Handle transition effects
        }
    }

    // Screen creation helpers
    public GameScreen createGameplayScreen() {
        GameScreen gameplayScreen = new GameScreen(game) {
            private EntityManager entityManager = new EntityManager();

            @Override
            public void show() {
                // Initialize gameplay elements
            }

            @Override
            public void render(float delta) {
                // Update
                entityManager.update(delta);

                // Render
                batch.begin();
                render(batch); // Render background
                entityManager.render(batch);
                batch.end();
            }
        };
        
        addScreen("gameplay", gameplayScreen);
        return gameplayScreen;
    }

    public MenuScreen createMenuScreen() {
        MenuScreen menuScreen = new MenuScreen(game) {
            @Override
            public void show() {
                // Initialize menu elements
            }

            @Override
            public void render(float delta) {
                batch.begin();
                render(batch); // Render background
                // Render menu elements
                batch.end();
            }
        };
        
        addScreen("menu", menuScreen);
        return menuScreen;
    }

    public void dispose() {
        // Dispose all screens
        for (Screen screen : screens.values()) {
            screen.dispose();
        }
        screens.clear();

        // Dispose background
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    // Getter for current screen
    public Screen getCurrentScreen() {
        return game.getScreen();
    }

    public String getCurrentScreenKey() {
        return currentScreenKey;
    }
}