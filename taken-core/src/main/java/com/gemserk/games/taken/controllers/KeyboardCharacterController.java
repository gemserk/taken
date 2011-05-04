package com.gemserk.games.taken.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.componentsengine.input.ButtonMonitor;
import com.gemserk.componentsengine.input.LibgdxButtonMonitor;

public class KeyboardCharacterController implements CharacterController {

	private Vector2 direction = new Vector2(0f, 0f);

	private boolean walking = false;

	private boolean jumped = false;

	ButtonMonitor jumpButtonMonitor = new LibgdxButtonMonitor(Keys.DPAD_UP);

	@Override
	public void update(int delta) {

		walking = false;
		jumped = false;

		direction.set(0f, 0f);

		if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) {
			direction.x = 1f;
			walking = true;
		} else if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) {
			direction.x = -1f;
			walking = true;
		}

		jumpButtonMonitor.update();
		jumped = jumpButtonMonitor.isHolded();

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

	@Override
	public boolean isJumping() {
		return jumped;
	}

}