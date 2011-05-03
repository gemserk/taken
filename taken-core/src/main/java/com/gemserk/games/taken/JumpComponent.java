package com.gemserk.games.taken;

import com.artemis.Component;

public class JumpComponent extends Component {

	private boolean jumping;

	private float force;

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public float getForce() {
		return force;
	}

	public void setForce(float force) {
		this.force = force;
	}

}
