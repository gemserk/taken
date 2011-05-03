package com.gemserk.games.taken;

import com.artemis.Component;
import com.gemserk.animation4j.FrameAnimation;

public class AnimationComponent extends Component {

	private final SpriteSheet[] spriteSheets;
	
	private final FrameAnimation[] animations;
	
	private int currentAnimation;
	
	public SpriteSheet getSpriteSheets() {
		return spriteSheets[currentAnimation];
	}
	
	public FrameAnimation getAnimation() {
		return animations[currentAnimation];
	}
	
	public void setCurrentAnimation(int currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	public AnimationComponent(SpriteSheet[] spriteSheets, FrameAnimation[] animations) {
		this.spriteSheets = spriteSheets;
		this.animations = animations;
	}
	
}
