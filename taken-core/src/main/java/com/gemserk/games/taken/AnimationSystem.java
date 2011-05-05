package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.animation4j.gdx.Animation;
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
		Animation animation = animationComponent.getCurrentAnimation();
		animation.update(world.getDelta());
		Sprite currentSprite = animation.getCurrentFrame();
		spriteComponent.setSprite(currentSprite);
	}

}
