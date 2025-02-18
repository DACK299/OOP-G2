package io.github.some_example_name.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

public class IOManager implements InputProcessor {
    private static IOManager instance;
    private Map<Integer, Boolean> keyStates;
    private Vector2 mousePosition;
    private boolean[] mouseButtonsPressed;
    
    private IOManager() {
        keyStates = new HashMap<>();
        mousePosition = new Vector2();
        mouseButtonsPressed = new boolean[5]; // Support up to 5 mouse buttons
        
        // Register as input processor
        Gdx.input.setInputProcessor(this);
    }
    
    public static IOManager getInstance() {
        if (instance == null) {
            instance = new IOManager();
        }
        return instance;
    }
    
    public boolean isKeyPressed(int keycode) {
        Boolean state = keyStates.get(keycode);
        return state != null && state;
    }
    
    public Vector2 getMousePosition() {
        return mousePosition.cpy();
    }
    
    public boolean isMouseButtonPressed(int button) {
        return button >= 0 && button < mouseButtonsPressed.length && mouseButtonsPressed[button];
    }
    
    // InputProcessor methods
    @Override
    public boolean keyDown(int keycode) {
        keyStates.put(keycode, true);
        return true;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        keyStates.put(keycode, false);
        return true;
    }
    
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button >= 0 && button < mouseButtonsPressed.length) {
            mouseButtonsPressed[button] = true;
        }
        return true;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button >= 0 && button < mouseButtonsPressed.length) {
            mouseButtonsPressed[button] = false;
        }
        return true;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mousePosition.set(screenX, Gdx.graphics.getHeight() - screenY); // Flip Y for libGDX coordinates
        return true;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePosition.set(screenX, Gdx.graphics.getHeight() - screenY); // Flip Y for libGDX coordinates
        return true;
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    
    public void update() {
        // Any per-frame updates if needed
    }

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
}