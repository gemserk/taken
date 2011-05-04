package com.gemserk.games.taken;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.animation4j.FrameAnimation;

public class Animation  {
	
	private final Sprite[] sprites;
	
	private final FrameAnimation frameAnimation;
	
	public Animation(Sprite[] frames, FrameAnimation frameAnimation) {
		this.sprites = frames;
		this.frameAnimation = frameAnimation;
	}
	
	public int getFramesCount() {
		return sprites.length;
	}
	
	public Sprite getFrame(int index) {
		return sprites[index];
	}
	
	public Sprite getCurrentFrame() {
		return getFrame(frameAnimation.getCurrentFrame());
	}
	
	public void update(int delta) {
		frameAnimation.update(delta);
	}

}