package com.gemserk.games.taken.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.gdx.math.MathUtils2;

public class AreaTouchCharacterController implements CharacterController {

	private final Rectangle moveLeftArea;
	
	private final Rectangle moveRightArea;

	private float direction = 0f;

	private boolean walking;

	public AreaTouchCharacterController(Rectangle moveLeftArea, Rectangle moveRightArea) {
		this.moveLeftArea = moveLeftArea;
		this.moveRightArea = moveRightArea;
	}

	@Override
	public void update(int delta) {
		walking = false;
		
		if (Gdx.input.isTouched()) {
			
			float x = Gdx.input.getX();
			float y = Gdx.graphics.getHeight() - Gdx.input.getY();
			
			if (MathUtils2.inside(moveLeftArea, x, y)) {
				direction = -1f;
				walking = true;
			}

			if (MathUtils2.inside(moveRightArea, x, y)) {
				direction = 1f;
				walking = true;
			}

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