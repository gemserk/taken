package com.gemserk.games.taken;

import com.artemis.Component;
import com.artemis.Entity;

public class BloodOverlayComponent extends Component {

	private final Animation[] animations;
	
	private int currentAnimation;

	private final Entity entity;
	
	public Animation getSpriteSheets() {
		return animations[currentAnimation];
	}
	
	public void setCurrentAnimation(int currentAnimation) {
		this.currentAnimation = currentAnimation;
	}
	
	public Entity getEntity() {
		return entity;
	}

	public BloodOverlayComponent(Entity entity, Animation[] spriteSheets) {
		this.entity = entity;
		this.animations = spriteSheets;
	}
	
}
