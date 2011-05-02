package com.gemserk.games.taken;

import com.artemis.Component;
import com.artemis.Entity;

public class BloodOverlayComponent extends Component {

	private final SpriteSheet[] spriteSheets;
	
	private int currentAnimation;

	private final Entity entity;
	
	public SpriteSheet getSpriteSheets() {
		return spriteSheets[currentAnimation];
	}
	
	public void setCurrentAnimation(int currentAnimation) {
		this.currentAnimation = currentAnimation;
	}
	
	public Entity getEntity() {
		return entity;
	}

	public BloodOverlayComponent(Entity entity, SpriteSheet[] spriteSheets) {
		this.entity = entity;
		this.spriteSheets = spriteSheets;
	}
	
}
