package com.gemserk.games.taken.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.gdx.math.MathUtils2;

public class AreaTouchJumpController implements JumpController {

	private final Rectangle jumpArea;

	private boolean jumping;
	
	public AreaTouchJumpController(Rectangle jumpArea) {
		this.jumpArea = jumpArea;
	}

	@Override
	public void update(int delta) {
		jumping = false;
		
		if (Gdx.input.isTouched()) {
			
			float x = Gdx.input.getX();
			float y = Gdx.graphics.getHeight() - Gdx.input.getY();
			
			if (MathUtils2.inside(jumpArea, x, y)) 
				jumping = true;

		}
	}

	@Override
	public boolean isJumping() {
		return jumping;
	}
}