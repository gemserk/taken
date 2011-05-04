package com.gemserk.games.taken;

import com.artemis.Component;

public class AnimationComponent extends Component {

	private final Animation[] animations;
	
	private int currentAnimation;
	
	public Animation getCurrentAnimation() {
		return animations[currentAnimation];
	}
	
	public void setCurrentAnimation(int currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	public AnimationComponent(Animation[] spriteSheets) {
		this.animations = spriteSheets;
	}
	
}
