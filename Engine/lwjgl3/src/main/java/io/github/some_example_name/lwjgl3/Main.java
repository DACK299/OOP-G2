package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    private SpriteBatch batch;
    private EntityManager entityManager;
    private SceneManagement sceneManager;
    private CollisionManagement collisionManager;
    private MovementManagement movementManager;
    private IOManagement ioManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        
        // Initialize all managers
        entityManager = new EntityManager();
        sceneManager = new SceneManagement();
        collisionManager = new CollisionManagement();
        movementManager = new MovementManagement(200f, 500f); // Example values for acceleration and max speed
        ioManager = new IOManagement();

        // Load initial scene
        sceneManager.load_scene("background.png"); // Make sure this asset exists in your assets folder
        
        // Create some initial entities
        createInitialEntities();
    }

    private void createInitialEntities() {
        // Example entity creation
        Entity square = new Entity(100, 100, 50, 50, Entity.EntityType.SQUARE);
        Entity circle = new Entity(200, 200, 40, 40, Entity.EntityType.CIRCLE);
        
        entityManager.addEntity(square);
        entityManager.addEntity(circle);
    }

    @Override
    public void render() {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update
        float deltaTime = Gdx.graphics.getDeltaTime();
        update(deltaTime);

        // Render
        batch.begin();
        sceneManager.render(batch);
        entityManager.render(batch);
        batch.end();
    }

    private void update(float deltaTime) {
        // Handle input
        ioManager.checkKeyInputs();

        // Update all systems
        entityManager.update(deltaTime);
        collisionManager.detectCollisions();
        movementManager.calculate_movement();
        sceneManager.update();
    }

    @Override
    public void resize(int width, int height) {
        // Handle window resize
    }

    @Override
    public void pause() {
        // Handle pause state
    }

    @Override
    public void resume() {
        // Handle resume state
    }

    @Override
    public void dispose() {
        batch.dispose();
        // Dispose of all resources
        sceneManager.dispose();
    }
}
