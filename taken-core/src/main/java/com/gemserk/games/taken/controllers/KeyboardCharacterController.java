package com.gemserk.games.taken.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class KeyboardCharacterController implements CharacterController {

	private float direction = 0f;
	
	private boolean walking = false;

	@Override
	public void update(int delta) {

		walking = false;

		if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) {
			direction = 1f;
			walking = true;
		} else if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) {
			direction = -1f;
			walking = true;
		}

	}

	@Override
	public boolean isWalking() {
		return walking;
	}

	@Override
	public void getWalkingDirection(float[] d) {
		d[0] = direction;
		d[1] = 0;
	}

}