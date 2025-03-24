package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.math.MathUtils;

/**
 * Utility class to help manage graphics settings and transitions between modes
 */
public class GraphicsHelper {
    // Default window dimensions
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    
    /**
     * Safely switches to fullscreen mode with error handling
     * @return true if the operation succeeded, false otherwise
     */
    public static boolean setFullscreenMode() {
        try {
            if (Gdx.graphics.supportsDisplayModeChange()) {
                // Get the best display mode
                DisplayMode bestMode = getBestDisplayMode();
                if (bestMode != null) {
                    Gdx.graphics.setFullscreenMode(bestMode);
                    System.out.println("Switched to fullscreen mode: " + 
                                     bestMode.width + "x" + bestMode.height);
                    return true;
                } else {
                    System.err.println("No suitable display mode found");
                }
            } else {
                System.err.println("Display mode change not supported on this device");
            }
        } catch (Exception e) {
            System.err.println("Error setting fullscreen mode: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Safely switches to windowed mode with error handling
     * @param width desired window width
     * @param height desired window height
     * @return true if the operation succeeded, false otherwise
     */
    public static boolean setWindowedMode(int width, int height) {
        try {
            // Validate parameters
            int validWidth = MathUtils.clamp(width, 640, 3840);
            int validHeight = MathUtils.clamp(height, 480, 2160);
            
            Gdx.graphics.setWindowedMode(validWidth, validHeight);
            System.out.println("Switched to windowed mode: " + validWidth + "x" + validHeight);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting windowed mode: " + e.getMessage());
            e.printStackTrace();
            
            // Try with default dimensions as a fallback
            try {
                Gdx.graphics.setWindowedMode(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                return true;
            } catch (Exception ex) {
                System.err.println("Failed to recover to default window size: " + ex.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * Gets the best display mode for the current monitor
     * @return the best DisplayMode or null if not found
     */
    private static DisplayMode getBestDisplayMode() {
        try {
            DisplayMode[] modes = Gdx.graphics.getDisplayModes();
            
            if (modes.length == 0) {
                return Gdx.graphics.getDisplayMode(); // Current display mode
            }
            
            // Get the highest resolution with at least 60Hz refresh rate
            DisplayMode bestMode = modes[0];
            for (DisplayMode mode : modes) {
                if (mode.refreshRate >= 60) {
                    if (mode.width * mode.height > bestMode.width * bestMode.height) {
                        bestMode = mode;
                    }
                }
            }
            
            return bestMode;
        } catch (Exception e) {
            System.err.println("Error getting display modes: " + e.getMessage());
            return Gdx.graphics.getDisplayMode(); // Fallback to current mode
        }
    }
    
    /**
     * Checks if a given display mode is valid for the current system
     * @param width the width to check
     * @param height the height to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidDisplayMode(int width, int height) {
        try {
            DisplayMode[] modes = Gdx.graphics.getDisplayModes();
            for (DisplayMode mode : modes) {
                if (mode.width == width && mode.height == height) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking display modes: " + e.getMessage());
        }
        return false;
    }
}
