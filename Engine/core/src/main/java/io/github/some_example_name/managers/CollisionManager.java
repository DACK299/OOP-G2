package io.github.some_example_name.managers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.entities.Entity;
import io.github.some_example_name.entities.ICollidable;
import io.github.some_example_name.entities.Obstacle;
import io.github.some_example_name.entities.PlayerCar;

public class CollisionManager implements ContactListener {
    private List<ICollidable> collidables;
    private PhysicsManager physicsManager;
    
    public CollisionManager() {
        collidables = new ArrayList<>();
        physicsManager = PhysicsManager.getInstance();
        
        // Register as contact listener for Box2D world
        World world = physicsManager.getWorld();
        world.setContactListener(this);
    }
    
    public void addCollidable(ICollidable collidable) {
        if (!collidables.contains(collidable)) {
            collidables.add(collidable);
        }
    }
    
    public void removeCollidable(ICollidable collidable) {
        collidables.remove(collidable);
    }
    
    public void registerEntities(Array<Entity> entities) {
        // Clear existing collidables
        collidables.clear();
        
        // Add all ICollidable entities
        for (Entity entity : entities) {
            if (entity instanceof ICollidable) {
                addCollidable((ICollidable) entity);
            }
        }
    }
    
    // Box2D handles the actual collision detection, so this is now just a backup
    public void detectAndHandleCollisions() {
        // Box2D now handles collisions through the contact listener methods
    }
    
    // ContactListener methods
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        // Get the entities from the body user data
        Entity entityA = (Entity) fixtureA.getBody().getUserData();
        Entity entityB = (Entity) fixtureB.getBody().getUserData();
        
        if (entityA != null && entityB != null) {
            // Prevent duplicate collision handling
            boolean alreadyHandled = false;
            
            // Check if one entity is a PlayerCar and the other is an Obstacle
            if (entityA instanceof PlayerCar && entityB instanceof Obstacle) {
                // Let the obstacle handle the collision first to minimize double damage
                ((Obstacle) entityB).handleCollision(entityA);
                // Then the player car handles the collision
                ((PlayerCar) entityA).handleCollision(entityB);
                alreadyHandled = true;
            } else if (entityA instanceof Obstacle && entityB instanceof PlayerCar) {
                // Let the obstacle handle the collision first
                ((Obstacle) entityA).handleCollision(entityB);
                // Then the player car handles the collision
                ((PlayerCar) entityB).handleCollision(entityA);
                alreadyHandled = true;
            }
            
            // If not already handled specifically, delegate to general handling
            if (!alreadyHandled) {
                if (entityA instanceof ICollidable) {
                    ((ICollidable) entityA).handleCollision(entityB);
                }
                
                if (entityB instanceof ICollidable) {
                    ((ICollidable) entityB).handleCollision(entityA);
                }
            }
            
            // Debug output
            System.out.println("Collision detected between: " + entityA.getClass().getSimpleName() + 
                             " and " + entityB.getClass().getSimpleName());
        }
    }
    
    @Override
    public void endContact(Contact contact) {
        // Handle end of contact if needed
    }
    
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Modify contact before resolution if needed
    }
    
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Process collision results if needed
    }
    
    public void clear() {
        collidables.clear();
    }
}