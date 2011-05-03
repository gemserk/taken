package com.gemserk.games.taken;

import com.artemis.Component;

public class JumpComponent extends Component {

	private boolean jumping = false;

	private final float jumpForce;

	private float currentForce;

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
		if (!jumping)
			setCurrentForce(0f);
	}

	public float getCurrentForce() {
		return currentForce;
	}

	public void setCurrentForce(float force) {
		this.currentForce = force;
	}
	
	public float getJumpForce() {
		return jumpForce;
	}
	
	public JumpComponent(float jumpForce) {
		this.jumpForce = jumpForce;
	}

}
