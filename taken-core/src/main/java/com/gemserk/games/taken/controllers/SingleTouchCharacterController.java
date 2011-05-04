package com.gemserk.games.taken.controllers;

import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.input.LibgdxPointer;

public class SingleTouchCharacterController implements CharacterController {

	private final LibgdxPointer pointer;

	private final Vector2 previousPosition = new Vector2();

	private final Vector2 direction = new Vector2();

	private boolean walking;

	private boolean jumping;

	public SingleTouchCharacterController(LibgdxPointer pointer) {
		this.pointer = pointer;
	}

	@Override
	public void update(int delta) {

		walking = false;

		jumping = false;

		pointer.update();

		if (pointer.wasPressed) {
			previousPosition.set(pointer.getPosition());
		}

		if (!pointer.touched)
			return;

		direction.set(pointer.getPosition()).sub(pointer.getPressedPosition());
		direction.nor();

		walking = true;

		if (pointer.getPosition().tmp().sub(previousPosition).y > 10) {
			jumping = true;
			// previousPosition.set(pointer.getPosition());
		} else
			previousPosition.set(pointer.getPosition());

	}

//	@Override
//	public boolean isJumping() {
//		return jumping;
//	}

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