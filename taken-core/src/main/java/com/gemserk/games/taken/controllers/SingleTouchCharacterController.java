package com.gemserk.games.taken.controllers;

import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.input.LibgdxPointer;

public class SingleTouchCharacterController implements CharacterController {

	private final LibgdxPointer pointer;

	private final Vector2 direction = new Vector2();

	private boolean walking;

	public SingleTouchCharacterController(LibgdxPointer pointer) {
		this.pointer = pointer;
	}

	@Override
	public void update(int delta) {
		walking = false;
		pointer.update();

		if (!pointer.touched)
			return;

		direction.set(pointer.getPosition()).sub(pointer.getPressedPosition());
		direction.nor();

		walking = true;
	}

	@Override
	public boolean isWalking() {
		return walking;
	}

	@Override
	public void getWalkingDirection(float[] d) {
		d[0] = direction.x;
		d[1] = 0;
	}
}