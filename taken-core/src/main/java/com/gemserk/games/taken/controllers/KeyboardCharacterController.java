package com.gemserk.games.taken.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class KeyboardCharacterController implements CharacterController {

	private Vector2 direction = new Vector2(0f, 0f);

	private boolean walking = false;

	@Override
	public void update(int delta) {

		walking = false;

		if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) {
			direction.x = 1f;
			walking = true;
		} else if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) {
			direction.x = -1f;
			walking = true;
		}

	}

	@Override
	public boolean isWalking() {
		return walking;
	}

	@Override
	public void getWalkingDirection(float[] d) {
		d[0] = direction.x;
		d[1] = direction.y;
	}

}