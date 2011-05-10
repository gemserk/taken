package com.gemserk.games.taken.components;

import com.artemis.Component;

public class LinearVelocityLimitComponent extends Component {
	
	private final float limit;
	
	public float getLimit() {
		return limit;
	}

	public LinearVelocityLimitComponent(float limit) {
		this.limit = limit;
	}

}
