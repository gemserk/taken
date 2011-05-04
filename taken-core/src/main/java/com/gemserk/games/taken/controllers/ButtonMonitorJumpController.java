package com.gemserk.games.taken.controllers;

import com.gemserk.componentsengine.input.ButtonMonitor;

public class ButtonMonitorJumpController implements JumpController {

	private ButtonMonitor button;

	public ButtonMonitorJumpController(ButtonMonitor button) {
		this.button = button;
	}
	
	@Override
	public void update(int delta) {
		button.update();
	}

	@Override
	public boolean isJumping() {
		return button.isHolded();
	}


}