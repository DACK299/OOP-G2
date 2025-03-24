package io.github.some_example_name.settings;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Gdx;

public class Settings {
    private static Settings instance;
    private Preferences prefs;
    
    public enum ControlScheme {
        ARROWS,
        WASD
    }
    
    private ControlScheme currentControlScheme;
    
    private Settings() {
        prefs = Gdx.app.getPreferences("game_settings");
        loadSettings();
    }
    
    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
    
    private void loadSettings() {
        String controlScheme = prefs.getString("controlScheme", "ARROWS");
        currentControlScheme = ControlScheme.valueOf(controlScheme);
    }
    
    public void saveSettings() {
        prefs.putString("controlScheme", currentControlScheme.name());
        prefs.flush();
    }
    
    public ControlScheme getControlScheme() {
        return currentControlScheme;
    }
    
    public void setControlScheme(ControlScheme scheme) {
        currentControlScheme = scheme;
        saveSettings();
    }
    
    public int getUpKey() {
        return currentControlScheme == ControlScheme.ARROWS ? Input.Keys.UP : Input.Keys.W;
    }
    
    public int getDownKey() {
        return currentControlScheme == ControlScheme.ARROWS ? Input.Keys.DOWN : Input.Keys.S;
    }
    
    public int getLeftKey() {
        return currentControlScheme == ControlScheme.ARROWS ? Input.Keys.LEFT : Input.Keys.A;
    }
    
    public int getRightKey() {
        return currentControlScheme == ControlScheme.ARROWS ? Input.Keys.RIGHT : Input.Keys.D;
    }
} 