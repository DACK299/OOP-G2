package io.github.some_example_name;

import com.badlogic.gdx.Game;
import io.github.some_example_name.managers.ScreenManager;

public class Main extends Game {
    @Override
    public void create() {
        ScreenManager.getInstance().initialize(this, null, null);
        ScreenManager.getInstance().showScreen("PLAY");
    }
    
    @Override
    public void dispose() {
        super.dispose();
        ScreenManager.getInstance().dispose();
    }
}