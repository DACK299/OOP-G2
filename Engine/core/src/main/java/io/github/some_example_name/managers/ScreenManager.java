package io.github.some_example_name.managers;

import com.badlogic.gdx.Screen;
import io.github.some_example_name.Main;
import io.github.some_example_name.screens.PlayScreen;
import java.util.HashMap;

public class ScreenManager {
    private static ScreenManager instance;
    private Main game;
    private HashMap<String, Screen> screens;
    private Screen currentScreen;
    
    private ScreenManager() {
        screens = new HashMap<>();
    }
    
    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }
    
    public void initialize(Main game) {
        this.game = game;
        // Initialize screens
        screens.put("PLAY", new PlayScreen(game));
        // Add more screens here as needed
    }
    
    public void showScreen(String screenName) {
        Screen screen = screens.get(screenName);
        if (screen != null) {
            // Dispose current screen if it exists
            if (currentScreen != null) {
                currentScreen.dispose();
            }
            
            currentScreen = screen;
            game.setScreen(screen);
        }
    }
    
    public Screen getCurrentScreen() {
        return currentScreen;
    }
    
    public void dispose() {
        for (Screen screen : screens.values()) {
            screen.dispose();
        }
        screens.clear();
        instance = null;
    }
}