package com.gemserk.games.taken.components;

import com.artemis.Entity;

public class GrabbedTrigger implements Trigger {

	private boolean alreadyTriggered = false;

	@Override
	public boolean isAlreadyTriggered() {
		return alreadyTriggered;
	}

	@Override
	public void trigger(Entity e) {
		alreadyTriggered = handle(e);
	}

	protected boolean handle(Entity e) {
		return true;
	}

}
