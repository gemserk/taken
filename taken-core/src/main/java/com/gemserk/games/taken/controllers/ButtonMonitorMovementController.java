package com.gemserk.games.taken.controllers;

import com.gemserk.componentsengine.input.ButtonMonitor;

public class ButtonMonitorMovementController implements MovementController {

	private float direction = 0f;

	private boolean walking = false;

	private final ButtonMonitor leftKeyButton;

	private final ButtonMonitor rightKeyButton;

	public ButtonMonitorMovementController(ButtonMonitor leftKeyButton, ButtonMonitor rightKeyButton) {
		this.leftKeyButton = leftKeyButton;
		this.rightKeyButton = rightKeyButton;
	}

	@Override
	public void update(int delta) {

		walking = false;

		leftKeyButton.update();
		rightKeyButton.update();

		if (leftKeyButton.isHolded()) {
			direction = -1f;
			walking = true;
		}

		if (rightKeyButton.isHolded()) {
			direction = 1f;
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