package com.gemserk.games.taken.controllers;

import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.input.LibgdxPointer;

public class MultiTouchCharacterController implements CharacterController {

	private final LibgdxPointer pointer;

	private final LibgdxPointer jumpPointer;

	private final Vector2 direction = new Vector2();

	private boolean walking;

	private boolean jumping;

	public MultiTouchCharacterController(LibgdxPointer pointer, LibgdxPointer jumpPointer) {
		this.pointer = pointer;
		this.jumpPointer = jumpPointer;
	}

	@Override
	public void update(int delta) {

		walking = false;

		jumping = false;

		pointer.update();
		jumpPointer.update();

		if (pointer.touched) {

			direction.set(pointer.getPosition()).sub(pointer.getPressedPosition());
			direction.nor();

			walking = true;
		}

		if (jumpPointer.touched) {
			jumping = true;
		}
	}

	@Override
	public boolean isJumping() {
		return jumping;
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