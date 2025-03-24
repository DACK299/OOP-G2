package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.GLVersion;

import io.github.some_example_name.managers.IOManager;
import io.github.some_example_name.managers.MovementManager;
import io.github.some_example_name.managers.ScreenManager;
import io.github.some_example_name.managers.SoundManager;

public class Main extends Game {
    @Override
    public void create() {
        // Set global exception handler for the main thread
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Uncaught exception in thread: " + thread.getName());
            throwable.printStackTrace();
            
            // Try to recover from graphics-related exceptions
            if (throwable.getMessage() != null && 
                (throwable.getMessage().contains("OpenGL") ||
                 throwable.getMessage().contains("display") ||
                 throwable.getMessage().contains("graphics"))) {
                
                System.err.println("Graphics error detected, attempting to recover...");
                try {
                    Gdx.graphics.setWindowedMode(800, 600);
                } catch (Exception e) {
                    System.err.println("Failed to recover from graphics error");
                }
            }
        });
        
        try {
            // Configure graphics settings
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            
            // Print OpenGL information
            GLVersion glVersion = Gdx.graphics.getGLVersion();
            System.out.println("OpenGL Info:");
            System.out.println("  Vendor: " + glVersion.getVendorString());
            System.out.println("  Renderer: " + glVersion.getRendererString());
            System.out.println("  Version: " + glVersion.getMajorVersion() + "." + glVersion.getMinorVersion());
            
            // Verify required resources exist
            String[] requiredFiles = {
                "paul walker.png",
                "dominic.png",
                "bg.png",
                "sounds/wall_hit.wav",
                "sounds/Tokyo Drift (Fast & Furious) (From The Fast And The Furious_ Tokyo Drift Soundtrack).mp3"
            };
            
            for (String file : requiredFiles) {
                if (!Gdx.files.internal(file).exists()) {
                    throw new RuntimeException("Required resource not found: " + file);
                }
            }
            
            // Initialize all managers first before showing any screens
            try {
                // Initialize managers in order
                ScreenManager.initialize(this);
                System.out.println("ScreenManager initialized successfully");
                
                IOManager ioManager = IOManager.getInstance();
                System.out.println("IOManager initialized successfully");
                
                MovementManager movementManager = new MovementManager(400f, 300f);
                System.out.println("MovementManager initialized successfully");
                
                // Initialize screen manager components
                ScreenManager.getInstance().initialize(this, ioManager, movementManager);
                System.out.println("ScreenManager components initialized successfully");
                
                // Only show the menu screen after all managers are initialized
                try {
                    ScreenManager.getInstance().showScreen("MENU");
                    System.out.println("Menu screen shown successfully");
                } catch (Exception e) {
                    System.err.println("Error showing menu screen:");
                    e.printStackTrace();
                    throw e;
                }
            } catch (Exception e) {
                System.err.println("Error during manager initialization:");
                e.printStackTrace();
                throw e;
            }
        } catch (Exception e) {
            System.err.println("Fatal error during game initialization:");
            e.printStackTrace();
            if (Gdx.app != null) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void render() {
        try {
            // Clear the screen
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            
            // Call superclass render
            super.render();
        } catch (Exception e) {
            System.err.println("Error during render:");
            e.printStackTrace();
            if (Gdx.app != null) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void dispose() {
        try {
            // Dispose of the current screen if it exists
            if (getScreen() != null) {
                getScreen().dispose();
                System.out.println("Current screen disposed successfully");
            }
            
            // Dispose of managers
            try {
                ScreenManager.getInstance().dispose();
                System.out.println("ScreenManager disposed successfully");
            } catch (Exception e) {
                System.err.println("Error disposing ScreenManager: " + e.getMessage());
            }
            
            try {
                SoundManager.getInstance().dispose();
                System.out.println("SoundManager disposed successfully");
            } catch (Exception e) {
                System.err.println("Error disposing SoundManager: " + e.getMessage());
            }
            
            // Call superclass dispose
            try {
                super.dispose();
                System.out.println("Superclass disposed successfully");
            } catch (Exception e) {
                System.err.println("Error in superclass disposal: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error during disposal:");
            e.printStackTrace();
        }
    }
}
