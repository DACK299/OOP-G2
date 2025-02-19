package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.managers.CollisionManager;
import io.github.some_example_name.managers.EntityManager;
import io.github.some_example_name.managers.IOManager;
import io.github.some_example_name.managers.MovementManager;
import io.github.some_example_name.managers.ScreenManager;
import io.github.some_example_name.managers.SoundManager;
import io.github.some_example_name.entities.Player;
import io.github.some_example_name.entities.Wall;
import io.github.some_example_name.entities.Door;
import io.github.some_example_name.entities.Entity;
import io.github.some_example_name.entities.ICollidable;
import io.github.some_example_name.entities.StaticObject;

public class PlayScreen implements Screen {
    // Constants
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;
    
    // Core game objects
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final EntityManager entityManager;
    private IOManager ioManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    
    // Game entities
    private Player player;
    
    public PlayScreen(Main game) {
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
        collisionManager = new CollisionManager();
        
        // Initialize game objects
        init();
    }
    
    private void init() {
    	
        StaticObject circle1 = new StaticObject(200, 200, 50, 50, new Color(0.2f, 0.2f, 0.2f, 1));
        entityManager.add_entity(circle1);
        
        // Create player at center of screen
        player = new Player(WORLD_WIDTH / 2 - 16, WORLD_HEIGHT / 2 - 16, 32, 32);
        entityManager.add_entity(player);
        
        // Left wall
        Wall wallLeft = new Wall(100, 100, 40, 400);
        entityManager.add_entity(wallLeft);
        
        Wall wallLeft2 = new Wall(0, 0, 40, 600);
        entityManager.add_entity(wallLeft2);
        
        // Right wall
        Wall wallRight = new Wall(600, 0, 40, 600);
        entityManager.add_entity(wallRight);
        
        // Top wall
        Wall wallTop = new Wall(100, 460, 600, 40);
        entityManager.add_entity(wallTop);
        
        // Bottom wall
        Wall wallBottom = new Wall(0, 0, 600, 40);
        entityManager.add_entity(wallBottom);
        
        Door door = new Door(40, 450, 60, 40, "PLAY2");
        entityManager.add_entity(door);
        
        collisionManager.registerEntities(entityManager.getEntities());
    }
    
    @Override
    public void render(float delta) {
        // Update
        update(delta);
        
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Render
        batch.begin();
        entityManager.render(batch);
        batch.end();
        
        checkDoorTransitions();
    }
    
    private void checkDoorTransitions() {
        // Get all doors
        Array<Door> doors = entityManager.getEntitiesByType(Door.class);
        for (Door door : doors) {
            if (door.isPlayerColliding()) {
                door.resetCollision();
                ScreenManager.getInstance().showScreen(door.getTargetScreen());
                return; // Exit after first transition
            }
        }
    }
    
    private void update(float delta) {
        // Update all entities with the managers
        entityManager.update(delta, ioManager, movementManager);
        
        // Check for collisions
        entityManager.getCollisionManager().detectAndHandleCollisions();
    }
    
    @Override
    public void resize(int width, int height) {
        // Update viewport when window is resized
        viewport.update(width, height, true);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }
    
    @Override
    public void pause() {
        // Called when game is paused (on Android)
    }
    
    @Override
    public void resume() {
        // Called when game is resumed (on Android)
    }
    
    @Override
    public void show() {
        // Called when this screen becomes the current screen
    	SoundManager.getInstance().playMusic("play_screen_music");
    }
    
    @Override
    public void hide() {
        // Called when this screen is no longer the current screen
    	SoundManager.getInstance().stopMusic();
    }
    
    @Override
    public void dispose() {
        // Dispose of all resources
        batch.dispose();
        entityManager.dispose();
    }
    
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