package io.github.some_example_name.managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class PhysicsManager {
    private static PhysicsManager instance;
    private World world;
    private final float TIME_STEP = 1/60f;
    private final int VELOCITY_ITERATIONS = 6;
    private final int POSITION_ITERATIONS = 2;
    private float accumulator = 0;
    
    // Box2D works best with smaller units, so we'll use a scale factor
    public static final float PIXELS_TO_METERS = 0.01f;
    public static final float METERS_TO_PIXELS = 100f;
    
    private PhysicsManager() {
        // Create a world with gravity set to zero (top-down game)
        world = new World(new Vector2(0, 0), true);
        
        // Set up collision listener if needed
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                
                // Get user data from fixtures to identify the colliding objects
                Object userDataA = fixtureA.getBody().getUserData();
                Object userDataB = fixtureB.getBody().getUserData();
                
                // Delegate to the objects for handling the collision
                if (userDataA != null && userDataB != null) {
                    // This will be handled by the entity classes
                }
            }

            @Override
            public void endContact(Contact contact) {
                // Handle end of contact if needed
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                // Pre-solve handling if needed
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                // Post-solve handling if needed
            }
        });
    }
    
    public static PhysicsManager getInstance() {
        if (instance == null) {
            instance = new PhysicsManager();
        }
        return instance;
    }
    
    public World getWorld() {
        return world;
    }
    
    public void update(float deltaTime) {
        // Fixed time step physics simulation
        accumulator += Math.min(deltaTime, 0.25f);
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }
    
    // Convert pixels to Box2D meters
    public static float toMeters(float pixels) {
        return pixels * PIXELS_TO_METERS;
    }
    
    // Convert Box2D meters to pixels
    public static float toPixels(float meters) {
        return meters * METERS_TO_PIXELS;
    }
    
    // Create a Box2D body with improved collision detection
    public Body createBody(BodyDef.BodyType type, float x, float y, float width, float height, 
                          float density, float friction, float restitution, Object userData) {
        
        // Convert position and size to meters
        float meterX = toMeters(x);
        float meterY = toMeters(y);
        float meterWidth = toMeters(width);
        float meterHeight = toMeters(height);
        
        // Create body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(meterX + meterWidth/2, meterY + meterHeight/2); // Set position at center
        bodyDef.fixedRotation = true; // Prevent rotation for better collision predictability
        bodyDef.bullet = true; // Enable continuous collision detection for fast-moving objects
        
        // Create the body
        Body body = world.createBody(bodyDef);
        
        // Create shape for the body
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(meterWidth/2, meterHeight/2); // Box2D expects half-width and half-height
        
        // Create fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        
        // Create the fixture
        body.createFixture(fixtureDef);
        
        // Set user data for identifying this body
        body.setUserData(userData);
        
        // Clean up
        shape.dispose();
        
        // Debug output
        System.out.println("Created physics body at: " + bodyDef.position.x + ", " + bodyDef.position.y + 
                         " size: " + meterWidth + "x" + meterHeight);
        
        return body;
    }
    
    public void destroyBody(Body body) {
        if (body != null && world != null) {
            try {
                // Check if the body is in the world before destroying it
                Array<Body> bodies = new Array<>();
                world.getBodies(bodies);
                
                if (bodies.contains(body, true)) {
                    world.destroyBody(body);
                } else {
                    System.err.println("Warning: Attempted to destroy a body that is not in the world");
                }
            } catch (Exception e) {
                System.err.println("Error destroying body: " + e.getMessage());
            }
        }
    }
    
    public void dispose() {
        if (world != null) {
            world.dispose();
            world = null;
        }
        instance = null;
    }
}
