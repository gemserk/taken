package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.games.taken.components.CharacterControllerComponent;

public class CorrectSpriteDirectionSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	private final float[] direction = new float[] { 0f, 0f };

	public CorrectSpriteDirectionSystem() {
		super(CharacterControllerComponent.class);
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

		CharacterControllerComponent characterControllerComponent = e.getComponent(CharacterControllerComponent.class);
		SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);

		characterControllerComponent.getCharacterController().getWalkingDirection(direction);
		Sprite currentSprite = spriteComponent.getSprite();
		
		if (direction[0] > 0)
			currentSprite.setScale(1f, 1f);
		else if (direction[0] < 0)
			currentSprite.setScale(-1f, 1f);

	}

}
