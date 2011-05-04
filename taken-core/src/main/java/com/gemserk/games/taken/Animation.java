package com.gemserk.games.taken;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Animation  {
	
	private final Sprite[] sprites;
	
	public Animation(Sprite[] frames) {
		this.sprites = frames;
	}
	
	public int getFramesCount() {
		return sprites.length;
	}
	
	public Sprite getFrame(int index) {
		return sprites[index];
	}

}