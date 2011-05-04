package com.gemserk.games.taken.controllers;

import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.input.LibgdxPointer;

public class DragJumpController implements JumpController {

	private final LibgdxPointer pointer;

	private final Vector2 previousPosition = new Vector2();

	private boolean jumping;

	public DragJumpController(LibgdxPointer pointer) {
		this.pointer = pointer;
	}

	@Override
	public void update(int delta) {
		
		jumping = false;

		pointer.update();

		if (pointer.wasPressed)
			previousPosition.set(pointer.getPosition());

		if (!pointer.touched)
			return;

		if (pointer.getPosition().tmp().sub(previousPosition).y > 10) 
			jumping = true;

	}

	@Override
	public boolean isJumping() {
		return jumping;
	}
}