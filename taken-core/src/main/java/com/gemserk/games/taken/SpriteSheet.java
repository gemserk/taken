package com.gemserk.games.taken;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;

public class SpriteSheet implements Disposable {
	
	private final Sprite[] sprites;
	
	private final Texture texture;

	public SpriteSheet(Texture texture, Sprite[] frames) {
		this.texture = texture;
		this.sprites = frames;
	}
	
	public int getFramesCount() {
		return sprites.length;
	}
	
	public Sprite getFrame(int index) {
		return sprites[index];
	}

	@Override
	public void dispose() {
		texture.dispose();
	}

}