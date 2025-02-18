package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.managers.EntityManager;
import io.github.some_example_name.managers.IOManager;
import io.github.some_example_name.managers.MovementManager;
import io.github.some_example_name.managers.ScreenManager;
import io.github.some_example_name.entities.Player;
import io.github.some_example_name.entities.Wall;
import io.github.some_example_name.entities.Door;

public class PlayScreen2 implements Screen {
    // Constants
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;
    
    // Core game objects
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final EntityManager entityManager;
    private final IOManager ioManager;
    private final MovementManager movementManager;
    
    // Game entities
    private Player player;
    
    public PlayScreen2(Main game) {
        this.game = game;
        
        // Get managers from ScreenManager
        this.ioManager = ScreenManager.getInstance().getIOManager();
        this.movementManager = ScreenManager.getInstance().getMovementManager();
        
        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        // Initialize core objects
        batch = new SpriteBatch();
        entityManager = new EntityManager();
        
        // Initialize game objects
        init();
    }
    
    private void init() {
        // Create player at center of screen
        player = new Player(WORLD_WIDTH / 2 - 16, WORLD_HEIGHT / 2 - 16, 32, 32);
        entityManager.add_entity(player);
        
        // Create walls (different layout from PlayScreen)
        Wall wallTop = new Wall(50, 500, 700, 40);
        Wall wallBottom = new Wall(50, 60, 700, 40);
        Wall wallLeft = new Wall(50, 60, 40, 480);
        Wall wallRight = new Wall(710, 60, 40, 480);
        
        // Add walls to entity manager
        entityManager.add_entity(wallTop);
        entityManager.add_entity(wallBottom);
        entityManager.add_entity(wallLeft);
        entityManager.add_entity(wallRight);
        
        // Create door back to first screen
        Door door = new Door(50, 400, 40, 80, "PLAY");
        entityManager.add_entity(door);
    }
    
    @Override
    public void render(float delta) {
        // Update
        update(delta);
        
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Render
        batch.begin();
        entityManager.render(batch);
        batch.end();
    }
    
    private void update(float delta) {
        // Update all entities with the managers
        entityManager.update(delta, ioManager, movementManager);
        
        // Get the collision manager from entity manager
        entityManager.getCollisionManager().detectAndHandleCollisions();
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        entityManager.dispose();
    }
    
    // Required methods
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    
    // Getters
    public Player getPlayer() {
        return player;
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public OrthographicCamera getCamera() {
        return camera;
    }
}