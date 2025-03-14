package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.entities.Car;
import io.github.some_example_name.entities.Entity;
import io.github.some_example_name.entities.ObstacleCar;
import io.github.some_example_name.managers.CollisionManager;
import io.github.some_example_name.managers.EntityManager;
import io.github.some_example_name.managers.IOManager;
import io.github.some_example_name.managers.MovementManager;
import io.github.some_example_name.managers.ScreenManager;
import io.github.some_example_name.managers.SoundManager;

public class CarGameScreen implements Screen {
    // Constants
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;
    private static final float LANE_HEIGHT = 80;
    private static final float CAR_WIDTH = 100;
    private static final float CAR_HEIGHT = 50;
    private static final int LANE_COUNT = 3;
    private static final float SPAWN_TIMER_MIN = 1.5f;
    private static final float SPAWN_TIMER_MAX = 3.0f;
    
    // Core game objects
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final EntityManager entityManager;
    private IOManager ioManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    
    // Game state
    private Car playerCar;
    private float gameSpeed = 200f;
    private int score = 0;
    private boolean gameOver = false;
    
    // Background scrolling
    private Texture roadTexture;
    private float[] backgroundOffsets = {0, WORLD_WIDTH};
    private float backgroundScrollSpeed = 200f;
    
    // Obstacle spawning
    private long lastSpawnTime;
    private float spawnTimer;
    
    public CarGameScreen(Main game) {
        this.game = game;
        
        // Get managers from ScreenManager
        this.ioManager = ScreenManager.getInstance().getIOManager();
        this.movementManager = new MovementManager(0, 300f);
        
        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        // Initialize core objects
        batch = new SpriteBatch();
        entityManager = new EntityManager();
        collisionManager = new CollisionManager();
        
        // Load resources
        roadTexture = new Texture(Gdx.files.internal("road_background.png"));
        
        // Set initial spawn timer
        resetSpawnTimer();
        
        // Initialize game objects
        init();
    }
    
    private void init() {
        // Create player car
        playerCar = new Car(100, WORLD_HEIGHT / 2 - CAR_HEIGHT / 2, CAR_WIDTH, CAR_HEIGHT);
        entityManager.add_entity(playerCar);
        
        // Create lane markers (for visual reference)
        for (int i = 0; i < LANE_COUNT - 1; i++) {
            float y = WORLD_HEIGHT / 2 - LANE_HEIGHT / 2 + (i - LANE_COUNT / 2 + 1) * LANE_HEIGHT;
            for (int j = 0; j < 10; j++) {
                Entity laneMarker = new Entity(j * 80, y, 40, 5) {
                    @Override
                    public void update(float deltaTime) {
                        x -= backgroundScrollSpeed * deltaTime;
                        if (x < -width) {
                            x = WORLD_WIDTH;
                        }
                    }
                    
                    @Override
                    public void render(SpriteBatch batch) {
                        batch.end();
                        Gdx.gl.glEnable(GL20.GL_BLEND);
                        ShapeRenderer shapeRenderer = new ShapeRenderer();
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                        shapeRenderer.setColor(Color.WHITE);
                        shapeRenderer.rect(x, y, width, height);
                        shapeRenderer.end();
                        Gdx.gl.glDisable(GL20.GL_BLEND);
                        batch.begin();
                    }
                    
                    @Override
                    public void dispose() {}
                };
                entityManager.add_entity(laneMarker);
            }
        }
        
        // Register collision detection
        collisionManager.registerEntities(entityManager.getEntities());
    }
    
    @Override
    public void render(float delta) {
        // Update
        if (!gameOver) {
            update(delta);
        }
        
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Render background
        batch.begin();
        for (int i = 0; i < backgroundOffsets.length; i++) {
            batch.draw(roadTexture, backgroundOffsets[i], 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
        
        // Render score
        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.draw(batch, "Score: " + score, 20, WORLD_HEIGHT - 20);
        
        if (gameOver) {
            font.getData().setScale(2);
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER", WORLD_WIDTH / 2 - 100, WORLD_HEIGHT / 2);
            font.getData().setScale(1);
            font.draw(batch, "Press SPACE to restart", WORLD_WIDTH / 2 - 100, WORLD_HEIGHT / 2 - 40);
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                restart();
            }
        }
        
        batch.end();
        
        // Render entities
        entityManager.render(batch);
    }
    
    private void update(float delta) {
        // Update background scrolling
        for (int i = 0; i < backgroundOffsets.length; i++) {
            backgroundOffsets[i] -= backgroundScrollSpeed * delta;
            
            // If background is completely off screen, move it back to the right
            if (backgroundOffsets[i] <= -WORLD_WIDTH) {
                backgroundOffsets[i] = backgroundOffsets[(i + 1) % backgroundOffsets.length] + WORLD_WIDTH;
            }
        }
        
        // Update entities
        entityManager.update(delta, ioManager, movementManager);
        
        // Spawn obstacles
        spawnTimer -= delta;
        if (spawnTimer <= 0) {
            spawnObstacle();
            resetSpawnTimer();
        }
        
        // Increase score over time
        score += (int)(delta * 10);
        
        // Gradually increase game speed
        gameSpeed += delta * 2;
        backgroundScrollSpeed = gameSpeed;
        
        // Check collisions
        collisionManager.detectAndHandleCollisions();
        
        // Check for game over
        if (playerCar.isCollided()) {
            gameOver = true;
            SoundManager.getInstance().playSound("wall_collision"); // Reusing existing sound
        }
        
        // Clean up off-screen obstacles
        cleanupOffscreenObstacles();
    }
    
    private void spawnObstacle() {
        // Choose a random lane
        int lane = MathUtils.random(0, LANE_COUNT - 1);
        float laneY = WORLD_HEIGHT / 2 - LANE_HEIGHT / 2 + (lane - LANE_COUNT / 2 + 1) * LANE_HEIGHT;
        
        // Create obstacle car
        ObstacleCar obstacleCar = new ObstacleCar(WORLD_WIDTH, laneY - CAR_HEIGHT / 2, CAR_WIDTH, CAR_HEIGHT, backgroundScrollSpeed);
        entityManager.add_entity(obstacleCar);
        
        // Update collision manager with new entity
        collisionManager.registerEntities(entityManager.getEntities());
    }
    
    private void resetSpawnTimer() {
        spawnTimer = MathUtils.random(SPAWN_TIMER_MIN, SPAWN_TIMER_MAX);
        // Make spawn rate faster as game progresses
        spawnTimer *= Math.max(0.5f, 1.0f - score / 10000f);
    }
    
    private void cleanupOffscreenObstacles() {
        Array<ObstacleCar> cars = entityManager.getEntitiesByType(ObstacleCar.class);
        for (ObstacleCar car : cars) {
            if (car.getX() < -car.getWidth()) {
                entityManager.remove_entity(car);
            }
        }
    }
    
    private void restart() {
        // Reset game state
        gameOver = false;
        score = 0;
        gameSpeed = 200f;
        backgroundScrollSpeed = gameSpeed;
        
        // Clear all entities
        entityManager.dispose();
        
        // Reinitialize game
        init();
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void show() {
        SoundManager.getInstance().playMusic("play_screen_music");
    }
    
    @Override
    public void hide() {
        SoundManager.getInstance().stopMusic();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        entityManager.dispose();
        roadTexture.dispose();
    }
}
