package com.gemserk.games.taken;

import com.artemis.Component;
import com.gemserk.animation4j.FrameAnimation;

public class AnimationComponent extends Component {

	private final Animation[] animations;
	
	private final FrameAnimation[] frameAnimations;
	
	private int currentAnimation;
	
	public Animation getCurrentAnimation() {
		return animations[currentAnimation];
	}
	
	public FrameAnimation getCurrentFrameAnimation() {
		return frameAnimations[currentAnimation];
	}
	
	public void setCurrentAnimation(int currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	public AnimationComponent(Animation[] spriteSheets, FrameAnimation[] animations) {
		this.animations = spriteSheets;
		this.frameAnimations = animations;
	}
	
}
