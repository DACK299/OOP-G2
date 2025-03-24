package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.Files;
import io.github.some_example_name.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        try {
            // Basic graphics settings
            System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
            System.setProperty("org.lwjgl.system.allocator", "system");
            System.setProperty("org.lwjgl.opengl.Display.enableHighDPI", "false");
            System.setProperty("org.lwjgl.glfw.libname", "glfw");
            
            // Error handling
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                System.err.println("Critical error in thread " + thread);
                System.err.println("Error details:");
                throwable.printStackTrace(System.err);
                System.exit(1);
            });
            
            createApplication();
        } catch (Exception e) {
            System.err.println("Failed to create application:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Drunk Driving Simulator");
        
        // Basic graphics settings
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL20, 2, 0);
        configuration.setBackBufferConfig(8, 8, 8, 8, 16, 0, 0);
        configuration.useVsync(false);
        configuration.setForegroundFPS(60);
        configuration.setWindowedMode(800, 600);
        configuration.setResizable(false);
        
        // Additional settings
        configuration.setTransparentFramebuffer(false);
        configuration.setInitialVisible(true);
        configuration.setDecorated(true);
        configuration.setIdleFPS(20);
        
        return configuration;
    }
}