package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.animation4j.FrameAnimation;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;

public class AnimationSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	public AnimationSystem() {
		super(AnimationComponent.class);
	}

	@Override
	public void toggle() {
		activableSystem.toggle();
	}

	@Override
	public boolean isEnabled() {
		return activableSystem.isEnabled();
	}

	@Override
	protected void process(Entity e) {
		
		AnimationComponent animationComponent = e.getComponent(AnimationComponent.class);
		SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);
		
		FrameAnimation frameAnimationImpl = animationComponent.getCurrentFrameAnimation();
		frameAnimationImpl.update(world.getDelta());
		
		Animation animation = animationComponent.getCurrentAnimation();
		
		Sprite currentSprite = animation.getFrame(frameAnimationImpl.getCurrentFrame());
		spriteComponent.setSprite(currentSprite);
		
		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		
		if (physicsComponent == null)
			return;
		
		Body body = physicsComponent.getBody();

		Vector2 linearVelocity = body.getLinearVelocity();
		
		if (linearVelocity.x < 0f) {
			currentSprite.setScale(-1f, 1f);
		} else {
			currentSprite.setScale(1f, 1f);
		}

	}

}
