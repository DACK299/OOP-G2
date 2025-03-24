package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.entities.Entity;
import io.github.some_example_name.entities.Obstacle;
import io.github.some_example_name.entities.PlayerCar;
import io.github.some_example_name.managers.CollisionManager;
import io.github.some_example_name.managers.EntityManager;
import io.github.some_example_name.managers.IOManager;
import io.github.some_example_name.managers.MovementManager;
import io.github.some_example_name.managers.PhysicsManager;
import io.github.some_example_name.managers.ScoreManager;
import io.github.some_example_name.managers.SoundManager;
import io.github.some_example_name.settings.Settings;

public class PlayScreen implements Screen {
    private static String selectedCharacter = "paul"; // Default character
    private static PlayScreen instance;
    
    public static PlayScreen getInstance() {
        return instance;
    }
    
    // Add pause state
    private boolean isPaused = false;
    
    // Constants
    public static final float WORLD_WIDTH = 1600;
    public static final float WORLD_HEIGHT = 900;
    public static final float MARGIN = 20f;
    private static final float OBSTACLE_WIDTH = 250f;
    private static final float OBSTACLE_HEIGHT = 130f;
    private static final float LINE_SPACING = OBSTACLE_WIDTH * 2.5f; // Increased spacing between obstacles
    private static final float VERTICAL_SPACING = OBSTACLE_HEIGHT * 2f; // Increased vertical spacing
    private static final float INITIAL_SPAWN_TIME = 1.0f;
    private static final float MIN_SPAWN_TIME = 0.1f;
    private static final float SPAWN_TIME_DECREASE_RATE = 0.2f;
    private static final float DIFFICULTY_INCREASE_INTERVAL = 8f;
    private static final float SPAWN_PATTERN_CHANGE_INTERVAL = 2f;
    private static final float LANE_VARIATION = 100f;
    private static final float BASE_SPEED = 300f;
    private static final float MIN_SPEED_MULTIPLIER = 0.7f;
    private static final float MAX_SPEED_MULTIPLIER = 1.3f;
    private static final float ALCOHOL_INCREASE_INTERVAL = 5f; // Increase every 5 seconds
    private static final float ALCOHOL_INCREASE_AMOUNT = 5f; // Increase by 5%
    private static final float ALCOHOL_EFFECT_THRESHOLD = 50f; // Effects start at 50%
    private static final float MAX_ALCOHOL_MULTIPLIER = 5f; // Maximum spawn rate multiplier at 100% alcohol
    private static final float ALCOHOL_BAR_ANIMATION_SPEED = 15f; // Reduced to make small increments more visible
    private static final float COLLISION_DISPLAY_TIME = 1.5f; // Time to display collision effects
    
    // Calculate road and lane dimensions
    private static final float ROAD_SECTION_WIDTH = WORLD_WIDTH / 2;
    private static final float ROAD_SECTION_HEIGHT = WORLD_HEIGHT / 2;
    private static final float LANE_WIDTH = (ROAD_SECTION_WIDTH - 2 * MARGIN);
    private static final float LANE_HEIGHT = ROAD_SECTION_HEIGHT / 3;
    private static final int INITIAL_OBSTACLES_PER_SPAWN = 4;
    private static final int MAX_OBSTACLES_PER_SPAWN = 10;
    
    // Calculate lane positions for each road section
    private static final float LANE_Y_BOTTOM_LEFT = MARGIN + OBSTACLE_HEIGHT/2;
    private static final float LANE_Y_BOTTOM_RIGHT = LANE_Y_BOTTOM_LEFT;
    private static final float LANE_Y_TOP_LEFT = ROAD_SECTION_HEIGHT + MARGIN + OBSTACLE_HEIGHT/2;
    private static final float LANE_Y_TOP_RIGHT = LANE_Y_TOP_LEFT;
    private static final float LANE_Y_MID_LEFT = (LANE_Y_BOTTOM_LEFT + LANE_Y_TOP_LEFT) / 2;
    private static final float LANE_Y_MID_RIGHT = (LANE_Y_BOTTOM_RIGHT + LANE_Y_TOP_RIGHT) / 2;
    
    // Calculate lane X positions with wider spacing
    private static final float LANE_X_LEFT = MARGIN + LANE_WIDTH/2;
    private static final float LANE_X_RIGHT = ROAD_SECTION_WIDTH + MARGIN + LANE_WIDTH/2;
    
    // Core game objects
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final EntityManager entityManager;
    private IOManager ioManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private PhysicsManager physicsManager;
    
    // Game entities and state
    private PlayerCar playerCar;
    private Texture roadTexture;
    private float roadX = 0;
    private float spawnTimer = 0;
    private float currentSpawnTime = INITIAL_SPAWN_TIME;
    private float lastDifficultyIncrease = 0;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private int collisionCount = 0;
    private float gameTime = 0;
    private int totalVehiclesSpawned = 0;
    
    // Game state variables
    private int currentObstaclesPerSpawn = INITIAL_OBSTACLES_PER_SPAWN;
    private float difficultyMultiplier = 1.0f;
    
    // Spawn pattern variables
    private enum SpawnPattern {
        RANDOM,     // Completely random spawns
        ALTERNATING,// Alternates between top and bottom
        ZIGZAG,    // Zigzag pattern across lanes
        WAVE,      // Wave-like pattern
        PINCER,    // Spawns obstacles simultaneously from both sides
        TRIPLE,    // Uses three lanes simultaneously
        SNAKE,     // Snake-like pattern with smooth transitions
        CHAOS      // Rapid random spawns with varying speeds
    }
    private SpawnPattern currentPattern = SpawnPattern.RANDOM;
    private float patternTimer = 0;
    private int patternStep = 0;  // Used for patterns like zigzag
    private float lastPatternChange = 0;
    private float lastAlcoholIncrease = 0f;
    
    // New state variable for smooth alcohol animation
    private float displayedAlcoholLevel = 0f;
    
    // Add a constant for score calculation
    private static final int SCORE_PER_OBSTACLE_PASSED = 10;
    private static final int SCORE_PER_SECOND_SURVIVED = 1;
    private float lastScoreUpdateTime = 0;
    
    public PlayScreen(Main game) {
        instance = this;
        this.game = game;
        
        // Setup managers
        ioManager = IOManager.getInstance();
        movementManager = new MovementManager(200f, 300f);
        physicsManager = PhysicsManager.getInstance();
        
        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        // Initialize core objects
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        shapeRenderer = new ShapeRenderer();
        entityManager = new EntityManager();
        collisionManager = new CollisionManager();
        
        // Load textures
        roadTexture = new Texture(Gdx.files.internal("road.png"));
        
        // Initialize game objects
        init();
    }
    
    private void init() {
        // Create player car at the left side of the screen
        playerCar = new PlayerCar(MARGIN + OBSTACLE_WIDTH/2, WORLD_HEIGHT/2, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        entityManager.add_entity(playerCar);
        
        collisionManager.registerEntities(entityManager.getEntities());
    }
    
    @Override
    public void render(float delta) {
        // Handle pause input
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            if (isPaused) {
                SoundManager.getInstance().stopMusic();
            } else {
                SoundManager.getInstance().playMusic("play_screen_music");
            }
        }

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Only update game state if not paused
        if (!isPaused) {
            update(delta);
        }
        
        // Draw game elements
        camera.update();
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Calculate road scroll position
        if (!isPaused) {
        roadX = (roadX - playerCar.getSpeed() * delta) % ROAD_SECTION_WIDTH;
        if (roadX <= -ROAD_SECTION_WIDTH) roadX = 0;
        }
        
        // Draw 2x2 grid of roads with proper alignment
        float baseX = roadX;
        
        // Bottom row
        batch.draw(roadTexture, baseX, 0, ROAD_SECTION_WIDTH, ROAD_SECTION_HEIGHT);
        batch.draw(roadTexture, baseX + ROAD_SECTION_WIDTH, 0, ROAD_SECTION_WIDTH, ROAD_SECTION_HEIGHT);
        // Seamless continuation
        batch.draw(roadTexture, baseX + ROAD_SECTION_WIDTH * 2, 0, ROAD_SECTION_WIDTH, ROAD_SECTION_HEIGHT);
        
        // Top row
        batch.draw(roadTexture, baseX, ROAD_SECTION_HEIGHT, ROAD_SECTION_WIDTH, ROAD_SECTION_HEIGHT);
        batch.draw(roadTexture, baseX + ROAD_SECTION_WIDTH, ROAD_SECTION_HEIGHT, ROAD_SECTION_WIDTH, ROAD_SECTION_HEIGHT);
        // Seamless continuation
        batch.draw(roadTexture, baseX + ROAD_SECTION_WIDTH * 2, ROAD_SECTION_HEIGHT, ROAD_SECTION_WIDTH, ROAD_SECTION_HEIGHT);
        
        // Draw sprites for all entities
        entityManager.renderSprites(batch);
        
        batch.end();
        
        // Enable blending for transparent shapes
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        // Draw all shapes in one batch
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Draw UI elements with filled shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderUI(shapeRenderer);
        shapeRenderer.end();
        
        // Draw collision boxes with line mode
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        entityManager.renderShapes(shapeRenderer);
        shapeRenderer.end();
        
        // Disable blending after shape rendering
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        // Draw UI text
        renderUIText();
        
        // Draw pause menu if paused
        if (isPaused) {
            renderPauseMenu();
        }
        
        // Check for game over
        if (playerCar.getCurrentHealth() <= 0) {
            game.setScreen(new GameOverScreen(game, collisionCount, totalVehiclesSpawned));
        }
    }
    
    private void renderUI(ShapeRenderer shapeRenderer) {
        // Draw health bar background
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(50, 30, 200, 20);
        
        // Draw health bar foreground with smooth transition
        float healthPercentage = playerCar.getCurrentHealth() / playerCar.getMaxHealth();
        
        // Color transition from green to red based on health
        if (healthPercentage > 0.6f) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (healthPercentage > 0.3f) {
            shapeRenderer.setColor(Color.ORANGE);
        } else {
            shapeRenderer.setColor(Color.RED);
        }
        
        float healthWidth = (200 * healthPercentage);
        shapeRenderer.rect(50, 30, healthWidth, 20);
        
        // Smoothly animate the displayed alcohol level
        float targetAlcohol = playerCar.getAlcoholLevel();
        if (displayedAlcoholLevel < targetAlcohol) {
            displayedAlcoholLevel = Math.min(targetAlcohol, 
                displayedAlcoholLevel + ALCOHOL_BAR_ANIMATION_SPEED * Gdx.graphics.getDeltaTime());
        } else if (displayedAlcoholLevel > targetAlcohol) {
            displayedAlcoholLevel = Math.max(targetAlcohol, 
                displayedAlcoholLevel - ALCOHOL_BAR_ANIMATION_SPEED * Gdx.graphics.getDeltaTime());
        }
        
        // Draw alcohol meter foreground with gradient color based on level
        float alcoholPercentage = displayedAlcoholLevel / playerCar.getMaxAlcoholLevel();
        Color alcoholColor = new Color();
        if (alcoholPercentage < 0.5f) {
            // Interpolate from yellow to orange (0% to 50%)
            alcoholColor.r = 1f;
            alcoholColor.g = 1f - (alcoholPercentage * 0.5f);
            alcoholColor.b = 0f;
        } else {
            // Interpolate from orange to red (50% to 100%)
            alcoholColor.r = 1f;
            alcoholColor.g = 0.5f - ((alcoholPercentage - 0.5f) * 0.5f);
            alcoholColor.b = 0f;
        }
        shapeRenderer.setColor(alcoholColor);
        
        float alcoholWidth = (200 * displayedAlcoholLevel) / playerCar.getMaxAlcoholLevel();
        shapeRenderer.rect(50, 60, alcoholWidth, 20);
        
        // Add pulsing effect when alcohol level is high
        if (displayedAlcoholLevel >= ALCOHOL_EFFECT_THRESHOLD) {
            float pulse = (float) Math.sin(gameTime * 5) * 0.2f + 0.8f; // Pulsing alpha between 0.6 and 1.0
            alcoholColor.a = pulse;
            shapeRenderer.setColor(alcoholColor);
            shapeRenderer.rect(50, 60, alcoholWidth, 20);
        }
    }
    
    private void renderUIText() {
        batch.begin();
        font.setColor(Color.WHITE);
        
        // Draw health text with percentage
        float healthPercentage = (playerCar.getCurrentHealth() / playerCar.getMaxHealth()) * 100;
        font.draw(batch, "Health: " + String.format("%.0f", healthPercentage) + "%", 
                 260, 45);
        
        // Draw alcohol percentage with color matching the bar
        float alcoholPercentage = displayedAlcoholLevel / playerCar.getMaxAlcoholLevel();
        if (alcoholPercentage < 0.5f) {
            font.setColor(1f, 1f - (alcoholPercentage * 0.5f), 0f, 1f);
        } else {
            font.setColor(1f, 0.5f - ((alcoholPercentage - 0.5f) * 0.5f), 0f, 1f);
        }
        
        // Add warning indicator when alcohol level is high
        String alcoholText = String.format("Alcohol Level: %.1f%%", displayedAlcoholLevel);
        if (displayedAlcoholLevel >= ALCOHOL_EFFECT_THRESHOLD) {
            alcoholText += " ⚠"; // Add warning symbol
            // Make text pulse when above threshold
            float pulse = (float) Math.sin(gameTime * 5) * 0.2f + 0.8f;
            font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, pulse);
        }
        font.draw(batch, alcoholText, 260, 75);
        
        // Reset color for other UI elements
        font.setColor(Color.WHITE);
        
        // Draw difficulty indicator
        font.draw(batch, "Difficulty: x" + String.format("%.1f", difficultyMultiplier), 50, 120);
        
        // Draw control scheme
        Settings settings = Settings.getInstance();
        String controlText = settings.getControlScheme() == Settings.ControlScheme.ARROWS ? 
            "Controls: Arrows" : "Controls: WASD";
        font.draw(batch, controlText, 50, 150);
        
        // Keep the speed indicator but get it from the standard method
        font.draw(batch, "Speed: " + String.format("%.0f", playerCar.getSpeed()), 50, 180);
        
        // Draw score in top right corner
        String scoreText = "Score: " + ScoreManager.getInstance().getCurrentScore();
        font.setColor(Color.YELLOW);
        GlyphLayout scoreLayout = new GlyphLayout(font, scoreText);
        font.draw(batch, scoreText, WORLD_WIDTH - scoreLayout.width - 50, WORLD_HEIGHT - 30);
        
        // Reset color for other UI elements
        font.setColor(Color.WHITE);
        
        batch.end();
    }
    
    private void renderPauseMenu() {
        // Enable blending for semi-transparent background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        
        // Draw dark overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer.end();
        
        // Draw pause text
        batch.begin();
        font.setColor(Color.WHITE);
        
        // Draw "PAUSED" text
        String pausedText = "PAUSED";
        GlyphLayout layout = new GlyphLayout(font, pausedText);
        font.getData().setScale(3);  // Temporarily increase scale for title
        font.draw(batch, pausedText, (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 + 100);
        
        // Draw instructions
        font.getData().setScale(1.5f);  // Reset scale for instructions
        String[] instructions = {
            "Press ESC to Resume",
            "Press BACKSPACE to Return to Menu"
        };
        
        float y = WORLD_HEIGHT / 2;
        for (String instruction : instructions) {
            layout.setText(font, instruction);
            font.draw(batch, instruction, (WORLD_WIDTH - layout.width) / 2, y);
            y -= 40;
        }
        
        batch.end();
        
        // Handle menu input
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.BACKSPACE)) {
            game.setScreen(new MenuScreen(game));
        }
        
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    private void update(float delta) {
        // Update game time
        gameTime += delta;
        patternTimer += delta;
        
        // Update Box2D physics world
        physicsManager.update(delta);
        
        // Update alcohol level
        if (gameTime - lastAlcoholIncrease >= ALCOHOL_INCREASE_INTERVAL) {
            float currentAlcohol = playerCar.getAlcoholLevel();
            if (currentAlcohol < playerCar.getMaxAlcoholLevel()) {
                playerCar.setAlcoholLevel(Math.min(playerCar.getMaxAlcoholLevel(), 
                                                 currentAlcohol + ALCOHOL_INCREASE_AMOUNT));
                lastAlcoholIncrease = gameTime;
                
                // Calculate alcohol effect multiplier (exponential increase after threshold)
                float alcoholLevel = playerCar.getAlcoholLevel();
                if (alcoholLevel >= ALCOHOL_EFFECT_THRESHOLD) {
                    float excessAlcohol = (alcoholLevel - ALCOHOL_EFFECT_THRESHOLD) / 
                                        (playerCar.getMaxAlcoholLevel() - ALCOHOL_EFFECT_THRESHOLD);
                    float alcoholMultiplier = 1f + (MAX_ALCOHOL_MULTIPLIER - 1f) * 
                                            (float)Math.pow(excessAlcohol, 2);
                    
                    // Apply alcohol effects to difficulty
                    difficultyMultiplier *= alcoholMultiplier;
                    
                    // Decrease spawn time based on alcohol level
                    currentSpawnTime = Math.max(MIN_SPAWN_TIME,
                        currentSpawnTime / alcoholMultiplier);
                    
                    // Increase number of obstacles
                    currentObstaclesPerSpawn = Math.min(MAX_OBSTACLES_PER_SPAWN,
                        (int)(currentObstaclesPerSpawn * alcoholMultiplier));
                }
            }
        }
        
        // Update spawn pattern
        if (gameTime - lastPatternChange >= SPAWN_PATTERN_CHANGE_INTERVAL) {
            // Randomly select new pattern
            SpawnPattern[] patterns = SpawnPattern.values();
            currentPattern = patterns[(int)(Math.random() * patterns.length)];
            lastPatternChange = gameTime;
            patternStep = 0;  // Reset pattern step
        }
        
        // Update difficulty based on time
        if (gameTime - lastDifficultyIncrease >= DIFFICULTY_INCREASE_INTERVAL) {
            // Base difficulty from time
            float timeBasedDifficulty = gameTime / DIFFICULTY_INCREASE_INTERVAL * 0.3f;
            float baseMultiplier = 1.0f + timeBasedDifficulty;
            
            // Apply alcohol effect to difficulty
            float alcoholLevel = playerCar.getAlcoholLevel();
            if (alcoholLevel >= ALCOHOL_EFFECT_THRESHOLD) {
                float excessAlcohol = (alcoholLevel - ALCOHOL_EFFECT_THRESHOLD) / 
                                    (playerCar.getMaxAlcoholLevel() - ALCOHOL_EFFECT_THRESHOLD);
                float alcoholMultiplier = 1f + (MAX_ALCOHOL_MULTIPLIER - 1f) * 
                                        (float)Math.pow(excessAlcohol, 2);
                baseMultiplier *= alcoholMultiplier;
            }
            
            difficultyMultiplier = baseMultiplier;
            
            // Decrease spawn time more aggressively with alcohol
            float spawnTimeMultiplier = 1.0f;
            if (alcoholLevel >= ALCOHOL_EFFECT_THRESHOLD) {
                float excessAlcohol = (alcoholLevel - ALCOHOL_EFFECT_THRESHOLD) / 
                                    (playerCar.getMaxAlcoholLevel() - ALCOHOL_EFFECT_THRESHOLD);
                spawnTimeMultiplier = 1f + excessAlcohol * 2f;
            }
            
            currentSpawnTime = Math.max(MIN_SPAWN_TIME,
                INITIAL_SPAWN_TIME - (SPAWN_TIME_DECREASE_RATE * spawnTimeMultiplier * 
                                    (gameTime / DIFFICULTY_INCREASE_INTERVAL)));
            
            // Increase number of obstacles based on difficulty and alcohol
            currentObstaclesPerSpawn = Math.min(MAX_OBSTACLES_PER_SPAWN,
                (int)(INITIAL_OBSTACLES_PER_SPAWN * difficultyMultiplier));
            
            lastDifficultyIncrease = gameTime;
            
            // Update movement manager with increased speeds and alcohol effects
            float speedMultiplier = 1.0f + (timeBasedDifficulty * 0.5f);
            float baseSpeed = 300f * speedMultiplier;
            float turnSpeed = 300f * speedMultiplier;
            
            // Apply alcohol effects to car control
            if (alcoholLevel >= ALCOHOL_EFFECT_THRESHOLD) {
                float excessAlcohol = (alcoholLevel - ALCOHOL_EFFECT_THRESHOLD) / 
                                    (playerCar.getMaxAlcoholLevel() - ALCOHOL_EFFECT_THRESHOLD);
                
                // Reduce base speed and increase turn speed as alcohol level rises
                baseSpeed *= (1f - excessAlcohol * 0.3f); // Up to 30% slower
                turnSpeed *= (1f + excessAlcohol * 1.5f); // Up to 150% more turn speed
            }
            
            // Just set basic speeds without acceleration/deceleration modifiers
            movementManager.setBaseSpeed(baseSpeed);
            movementManager.setTurnSpeed(turnSpeed);
            
            lastDifficultyIncrease = gameTime;
        }
        
        // Update player car with input
        playerCar.update(delta, ioManager, movementManager);
        
        // Update all other entities
        entityManager.update(delta, ioManager, movementManager);
        
        // No need to explicitly check for collisions - Box2D handles it
        
        // Remove obstacles that are fully out of frame
        Array<Entity> entities = entityManager.getEntities();
        Array<Entity> entitiesToRemove = new Array<>();  // Use a separate array for removal
        
        for (Entity entity : entities) {
            if (entity instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) entity;
                // Only remove if COMPLETELY out of frame with a margin of safety
                if (obstacle.getX() + obstacle.getWidth() < -OBSTACLE_WIDTH * 1.5f) {
                    // Give points for passing an obstacle
                    ScoreManager.getInstance().incrementScore(SCORE_PER_OBSTACLE_PASSED);
                    entitiesToRemove.add(obstacle);
                } else if (obstacle.shouldBeRemoved() && obstacle.getCollisionTimer() > COLLISION_DISPLAY_TIME) {
                    // Only remove after proper delay
                    entitiesToRemove.add(obstacle);
                }
            }
        }
        
        // Remove the marked entities AFTER iteration
        for (Entity entity : entitiesToRemove) {
            entityManager.remove_entity(entity);
        }
        
        // Spawn new obstacles
        spawnTimer += delta;
        if (spawnTimer >= currentSpawnTime) {
            // Spawn multiple obstacles
            for (int i = 0; i < currentObstaclesPerSpawn; i++) {
                spawnObstacle();
            }
            spawnTimer = 0;
        }
        
        // Update score based on time survived
        float scoreUpdateInterval = 1.0f; // Update score every second
        if (gameTime - lastScoreUpdateTime >= scoreUpdateInterval) {
            ScoreManager.getInstance().incrementScore(SCORE_PER_SECOND_SURVIVED);
            lastScoreUpdateTime = gameTime;
        }
    }
    
    private void spawnObstacle() {
        float laneY;
        int lineCount = currentObstaclesPerSpawn;
        
        // Determine spawn position based on current pattern
        switch (currentPattern) {
            case RANDOM:
                // Spawn a line of obstacles at random heights
                laneY = MathUtils.random(LANE_Y_BOTTOM_LEFT, LANE_Y_TOP_LEFT);
                spawnLineFormation(laneY, lineCount);
                break;
                
            case ALTERNATING:
                // Spawn lines alternating between lanes
                if (patternStep % 3 == 0) {
                    laneY = LANE_Y_TOP_LEFT;
                } else if (patternStep % 3 == 1) {
                    laneY = LANE_Y_MID_LEFT;
                } else {
                    laneY = LANE_Y_BOTTOM_LEFT;
                }
                spawnLineFormation(laneY, lineCount);
                break;
                
            case ZIGZAG:
                // Enhanced zigzag pattern using all six positions
                switch (patternStep % 6) {
                    case 0: laneY = LANE_Y_BOTTOM_LEFT; break;
                    case 1: laneY = LANE_Y_MID_RIGHT; break;
                    case 2: laneY = LANE_Y_TOP_LEFT; break;
                    case 3: laneY = LANE_Y_BOTTOM_RIGHT; break;
                    case 4: laneY = LANE_Y_MID_LEFT; break;
                    default: laneY = LANE_Y_TOP_RIGHT; break;
                }
                spawnLineFormation(laneY, lineCount);
                break;
                
            case WAVE:
                // Enhanced wave pattern with smoother transitions
                float wavePosition = (patternStep % 12) / 12.0f;
                float wave = (float) Math.sin(wavePosition * Math.PI * 2);
                laneY = LANE_Y_MID_LEFT + wave * (LANE_Y_TOP_LEFT - LANE_Y_MID_LEFT);
                spawnLineFormation(laneY, lineCount);
                break;
                
            case PINCER:
                // Modified PINCER pattern - now spawns two lines from the right side at different heights
                float topY = MathUtils.random(LANE_Y_MID_LEFT, LANE_Y_TOP_LEFT);
                float bottomY = MathUtils.random(LANE_Y_BOTTOM_LEFT, LANE_Y_MID_LEFT - VERTICAL_SPACING);
                spawnLineFormation(topY, lineCount/2);
                spawnLineFormation(bottomY, lineCount/2);
                break;
                
            case TRIPLE:
                // Spawn three lines simultaneously from right side
                float baseY = MathUtils.random(LANE_Y_BOTTOM_LEFT, LANE_Y_TOP_LEFT - LANE_HEIGHT * 2);
                spawnLineFormation(baseY, lineCount/3);
                spawnLineFormation(baseY + LANE_HEIGHT, lineCount/3);
                spawnLineFormation(baseY + LANE_HEIGHT * 2, lineCount/3);
                break;
                
            case SNAKE:
                // Smooth snake-like pattern
                float t = (patternStep % 20) / 20.0f;
                laneY = LANE_Y_MID_LEFT + 
                       (float)(Math.sin(t * Math.PI * 4) * (LANE_Y_TOP_LEFT - LANE_Y_MID_LEFT));
                spawnLineFormation(laneY, lineCount);
                break;
                
            case CHAOS:
                // Multiple random lines from right side
                for (int i = 0; i < 3; i++) {
                    laneY = MathUtils.random(LANE_Y_BOTTOM_LEFT, LANE_Y_TOP_LEFT);
                    spawnLineFormation(laneY, lineCount/3);
                }
                break;
                
            default:
                laneY = LANE_Y_BOTTOM_LEFT;
                spawnLineFormation(laneY, lineCount);
        }
        
        patternStep++;
    }
    
    private void spawnLineFormation(float y, int count) {
        // Always spawn from right side
        spawnLineFormation(y, count, false);
    }
    
    private void spawnLineFormation(float y, int count, boolean fromLeft) {
        // Override fromLeft parameter to always spawn from right
        fromLeft = false;
        
        // First, get all existing obstacles to check for collisions
        Array<Entity> entities = entityManager.getEntities();
        Array<Obstacle> obstacles = new Array<>();
        for (Entity entity : entities) {
            if (entity instanceof Obstacle) {
                obstacles.add((Obstacle) entity);
            }
        }
        
        // Adjust y position to avoid collisions with existing obstacles
        float adjustedY = findSafeYPosition(y, obstacles);
        
        // If no safe position found, skip this spawn
        if (adjustedY < 0) return;
        
        // Add random offset after finding safe position
        float randomOffset = MathUtils.random(-LANE_VARIATION/4, LANE_VARIATION/4);
        adjustedY += randomOffset;
        
        // Clamp to ensure it stays within bounds even after offset
        adjustedY = MathUtils.clamp(adjustedY, MARGIN + OBSTACLE_HEIGHT/2, 
                                   WORLD_HEIGHT - MARGIN - OBSTACLE_HEIGHT/2);
        
        // Calculate speeds for each obstacle in advance to ensure proper spacing
        float[] speeds = new float[count];
        for (int i = 0; i < count; i++) {
            // Generate a random speed multiplier between MIN_SPEED_MULTIPLIER and MAX_SPEED_MULTIPLIER
            float speedMultiplier = MathUtils.random(MIN_SPEED_MULTIPLIER, MAX_SPEED_MULTIPLIER);
            speeds[i] = BASE_SPEED * speedMultiplier * difficultyMultiplier;
        }
        
        // Sort speeds in descending order (faster ones first for right-side spawning)
        java.util.Arrays.sort(speeds);
        for (int i = 0; i < speeds.length / 2; i++) {
            float temp = speeds[i];
            speeds[i] = speeds[speeds.length - 1 - i];
            speeds[speeds.length - 1 - i] = temp;
        }
        
        // Spawn the line of obstacles with calculated speeds
        float baseSpacing = LINE_SPACING;
        for (int i = 0; i < count; i++) {
            // Adjust spacing based on speed difference
            float speedRatio = speeds[i] / BASE_SPEED;
            float adjustedSpacing = baseSpacing * speedRatio;
            
            float xOffset = i * adjustedSpacing;
            float spawnX = WORLD_WIDTH + OBSTACLE_WIDTH/2 + xOffset;
            
            spawnObstacleAt(spawnX, adjustedY, speeds[i]);
        }
    }
    
    private float findSafeYPosition(float desiredY, Array<Obstacle> existingObstacles) {
        // Check if the desired position is safe
        if (isSafePosition(desiredY, existingObstacles)) {
            return desiredY;
        }
        
        // Try positions above and below with increasing distance
        float maxOffset = WORLD_HEIGHT / 2; // Maximum search distance
        for (float offset = VERTICAL_SPACING; offset <= maxOffset; offset += VERTICAL_SPACING) {
            // Try above
            float upperY = desiredY + offset;
            if (upperY <= WORLD_HEIGHT - MARGIN - OBSTACLE_HEIGHT/2 && 
                isSafePosition(upperY, existingObstacles)) {
                return upperY;
            }
            
            // Try below
            float lowerY = desiredY - offset;
            if (lowerY >= MARGIN + OBSTACLE_HEIGHT/2 && 
                isSafePosition(lowerY, existingObstacles)) {
                return lowerY;
            }
        }
        
        // No safe position found
        return -1;
    }
    
    private boolean isSafePosition(float y, Array<Obstacle> existingObstacles) {
        // Create a test rectangle for collision checking
        Rectangle testBounds = new Rectangle(WORLD_WIDTH + OBSTACLE_WIDTH/2, y - OBSTACLE_HEIGHT/2,
                                          OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        
        // Check against all existing obstacles
        for (Obstacle obstacle : existingObstacles) {
            Rectangle obstacleBounds = obstacle.getBounds();
            
            // Skip obstacles that are too far to the left (already passed)
            if (obstacleBounds.x + obstacleBounds.width < 0) continue;
            
            // Calculate vertical distance between centers
            float verticalDistance = Math.abs(y - (obstacleBounds.y + obstacleBounds.height/2));
            
            // Calculate horizontal distance between centers
            float horizontalDistance = Math.abs((WORLD_WIDTH + OBSTACLE_WIDTH/2) - 
                                              (obstacleBounds.x + obstacleBounds.width/2));
            
            // If too close either vertically or horizontally, position is not safe
            if (verticalDistance < VERTICAL_SPACING || 
                (horizontalDistance < LINE_SPACING && verticalDistance < VERTICAL_SPACING * 2)) {
                return false;
            }
        }
        
        return true;
    }
    
    private void spawnObstacleAt(float x, float y, float speed) {
        try {
            // Ensure we're not spawning off-screen
            if (x < -OBSTACLE_WIDTH || x > WORLD_WIDTH + OBSTACLE_WIDTH * 2) {
                System.out.println("Skipping obstacle spawn at invalid X position: " + x);
                return;
            }
            
            // Ensure y position stays within world bounds with margin
            y = MathUtils.clamp(y, MARGIN + OBSTACLE_HEIGHT/2, 
                               WORLD_HEIGHT - MARGIN - OBSTACLE_HEIGHT/2);
            
            Obstacle obstacle = entityManager.obtainObstacle();
            
            // Set the calculated speed
            obstacle.setSpeed(speed);
            
            // Initialize the obstacle with proper position
            obstacle.init(x, y);
            
            // Add to entity manager
            entityManager.add_entity(obstacle);
            totalVehiclesSpawned++;
            
            // Debug output
            System.out.println("Spawned obstacle #" + totalVehiclesSpawned + " at position: " + x + ", " + y + 
                              " with speed: " + speed);
        } catch (Exception e) {
            System.err.println("Error spawning obstacle: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void incrementCollisionCount() {
        collisionCount++;
    }
    
    @Override
    public void resize(int width, int height) {
        try {
            // Safe resize that handles invalid dimensions
            if (width > 0 && height > 0) {
                viewport.update(width, height, true);
                camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
                System.out.println("PlayScreen resized to: " + width + "x" + height);
            } else {
                System.err.println("Invalid resize dimensions: " + width + "x" + height);
            }
        } catch (Exception e) {
            System.err.println("Error during PlayScreen resize: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void dispose() {
        if (batch != null) {
            try {
                batch.dispose();
            } catch (Exception e) {
                // Ignore buffer disposal errors
            }
        }
        if (font != null) {
            font.dispose();
        }
        if (roadTexture != null) {
            roadTexture.dispose();
        }
        if (entityManager != null) {
            entityManager.dispose();
        }
        if (shapeRenderer != null) {
            try {
                shapeRenderer.dispose();
            } catch (Exception e) {
                // Ignore buffer disposal errors
            }
        }
        
        // Dispose of physics world
        if (physicsManager != null) {
            physicsManager.dispose();
        }
    }
    
    @Override
    public void show() {
        try {
            // Clear any leftover obstacles from previous games
            if (entityManager != null) {
                // Get a clean copy of all current entities
                Array<Entity> currentEntities = new Array<>(entityManager.getEntities());
                
                // Remove all obstacles
                for (Entity entity : currentEntities) {
                    if (entity instanceof Obstacle) {
                        entityManager.remove_entity(entity);
                    }
                }
                
                // Reset collision manager with the cleaned entity list
                collisionManager.registerEntities(entityManager.getEntities());
                System.out.println("Cleared all obstacles before starting new game");
            }
            
            // Reset game state variables
            roadX = 0;
            spawnTimer = 0;
            currentSpawnTime = INITIAL_SPAWN_TIME;
            lastDifficultyIncrease = 0;
            collisionCount = 0;
            gameTime = 0;
            totalVehiclesSpawned = 0;
            currentObstaclesPerSpawn = INITIAL_OBSTACLES_PER_SPAWN;
            difficultyMultiplier = 1.0f;
            patternTimer = 0;
            patternStep = 0;
            lastPatternChange = 0;
            lastAlcoholIncrease = 0f;
            displayedAlcoholLevel = 0f;
            lastScoreUpdateTime = 0;
            
            // Ensure player car is at full health and zero alcohol
            if (playerCar != null) {
                // Reset player car health and alcohol level
                playerCar.takeDamage(0); // Reset invulnerability without damaging
                playerCar.setAlcoholLevel(0);
            }
            
            SoundManager.getInstance().playMusic("play_screen_music");
            isPaused = false;  // Ensure game starts unpaused
            
            // Reset score when starting a new game
            ScoreManager.getInstance().resetScore();
            
            // Debug print to verify player health at start
            System.out.println("PlayScreen initialized. Player health: " + 
                              playerCar.getCurrentHealth() + "/" + playerCar.getMaxHealth());
        } catch (Exception e) {
            System.err.println("Error showing PlayScreen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        SoundManager.getInstance().stopMusic();
    }
    
    public static void setSelectedCharacter(String character) {
        selectedCharacter = character;
    }

    public String getSelectedCharacter() {
        return selectedCharacter;
    }

    public PlayerCar getPlayerCar() {
        return playerCar;
    }
}