package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;

public class PhysicsSystem extends EntitySystem implements ActivableSystem {
	
	ActivableSystem activableSystem = new ActivableSystemImpl();

	static class PhysicsContactListener implements ContactListener {
		@Override
		public void endContact(Contact contact) {

			Body bodyA = contact.getFixtureA().getBody();
			Body bodyB = contact.getFixtureB().getBody();
			
			Entity entityA = (Entity) bodyA.getUserData();
			Entity entityB = (Entity) bodyB.getUserData();
			
			if (entityA != null) {
				PhysicsComponent physicsComponent = entityA.getComponent(PhysicsComponent.class);
				physicsComponent.getContact().removeBox2dContact();
			}

			if (entityB != null) {
				PhysicsComponent physicsComponent = entityB.getComponent(PhysicsComponent.class);
				physicsComponent.getContact().removeBox2dContact();
			}
			
		}

		@Override
		public void beginContact(Contact contact) {

			Body bodyA = contact.getFixtureA().getBody();
			Body bodyB = contact.getFixtureB().getBody();
			
			Entity entityA = (Entity) bodyA.getUserData();
			Entity entityB = (Entity) bodyB.getUserData();
			
			if (entityA != null) {
				PhysicsComponent physicsComponent = entityA.getComponent(PhysicsComponent.class);
				physicsComponent.getContact().setBox2dContact(contact, entityB);
			}

			if (entityB != null) {
				PhysicsComponent physicsComponent = entityB.getComponent(PhysicsComponent.class);
				physicsComponent.getContact().setBox2dContact(contact, entityA);
			}

		}

	}

	World physicsWorld;
	
	private PhysicsContactListener physicsContactListener;

	@SuppressWarnings("unchecked")
	public PhysicsSystem(World physicsWorld) {
		super(PhysicsComponent.class, SpatialComponent.class);
		this.physicsWorld = physicsWorld;
		physicsContactListener = new PhysicsContactListener();
	}

	@Override
	protected void begin() {
		physicsWorld.step(Gdx.app.getGraphics().getDeltaTime(), 3, 3);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

	}

	@Override
	protected boolean checkProcessing() {
		return isEnabled();
	}
	
	@Override
	protected void removed(Entity e) {
		
		// on entity removed, we should remove body from physics world
		
		PhysicsComponent component = e.getComponent(PhysicsComponent.class);
		
		if (component == null) {
			// throw new RuntimeException("Entity without physics component found");
			Gdx.app.log("Archer Vs Zombies", "Entity without physics component");
			return;
		}
		
		Body body = component.getBody();
		body.setUserData(null);
		
		physicsWorld.destroyBody(body);
		
	}

	@Override
	public void initialize() {
		physicsWorld.setContactListener(physicsContactListener);
	}

	public World getPhysicsWorld() {
		return physicsWorld;
	}

	public void toggle() {
		activableSystem.toggle();
	}

	public boolean isEnabled() {
		return activableSystem.isEnabled();
	}
	
	

}
