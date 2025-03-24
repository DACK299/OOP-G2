package io.github.some_example_name.managers;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Screen;

import io.github.some_example_name.Main;
import io.github.some_example_name.screens.CharacterSelectScreen;
import io.github.some_example_name.screens.GameOverScreen;
import io.github.some_example_name.screens.HighScoreScreen;
import io.github.some_example_name.screens.MenuScreen;
import io.github.some_example_name.screens.PlayScreen;

public class ScreenManager {
    private static ScreenManager instance;
    private Main game;
    private final Map<String, Screen> screens;
    private IOManager ioManager;
    private MovementManager movementManager;
    private Screen currentScreen;

    private ScreenManager(Main game) {
        this.game = game;
        screens = new HashMap<>();
        screens.put("MENU", new MenuScreen(game));
        screens.put("CHARACTER_SELECT", new CharacterSelectScreen(game));
        screens.put("PLAY", new PlayScreen(game));
        screens.put("HIGH_SCORES", new HighScoreScreen(game)); // Add high score screen
        // GameOverScreen will be created dynamically when needed with the proper stats
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ScreenManager must be initialized with a game instance first");
        }
        return instance;
    }

    public static void initialize(Main game) {
        if (instance == null) {
            instance = new ScreenManager(game);
        }
    }

    public void initialize(Main game, IOManager ioManager, MovementManager movementManager) {
        this.ioManager = ioManager;
        this.movementManager = movementManager;
    }

    public void showScreen(String screenName) {
        Screen oldScreen = currentScreen;
        Screen screen = screens.get(screenName);
        
        if (screen == null) {
            if ("GAME_OVER".equals(screenName)) {
                // Create a new GameOverScreen with default values
                screen = new GameOverScreen(game, 0, 0);
            } else if ("PLAY".equals(screenName)) {
                // Create a new PlayScreen
                screen = new PlayScreen(game);
                screens.put(screenName, screen);
            } else {
                throw new IllegalArgumentException("Screen " + screenName + " not found");
            }
        }
        
        // Hide and dispose old screen if it exists
        if (oldScreen != null) {
            try {
                oldScreen.hide();
                if (oldScreen instanceof PlayScreen || oldScreen instanceof GameOverScreen) {
                    oldScreen.dispose();
                }
            } catch (Exception e) {
                System.err.println("Error disposing old screen: " + e.getMessage());
            }
        }
        
        // Set and show new screen
        try {
            game.setScreen(screen);
            screen.show();
            currentScreen = screen;
            System.out.println("Successfully switched to screen: " + screenName);
        } catch (Exception e) {
            System.err.println("Error showing new screen: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void showGameOver(int vehiclesHit, int totalVehicles) {
        // Save the score before showing game over screen
        ScoreManager.getInstance().saveScore();
        
        Screen oldScreen = currentScreen;
        Screen gameOverScreen = new GameOverScreen(game, vehiclesHit, totalVehicles);
        
        // Hide and dispose old screen if it exists
        if (oldScreen != null) {
            oldScreen.hide();
        }
        
        // Set and show new screen
        game.setScreen(gameOverScreen);
        gameOverScreen.show();
        currentScreen = gameOverScreen;
    }

    public void dispose() {
        // Dispose all screens
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen = null;
        }
        
        for (Screen screen : screens.values()) {
            if (screen != null) {
                screen.dispose();
            }
        }
        screens.clear();
        instance = null;
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public IOManager getIOManager() {
        return ioManager;
    }
    
    public MovementManager getMovementManager() {
        return movementManager;
    }
}