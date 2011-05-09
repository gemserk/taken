package com.gemserk.games.taken.components;

import com.artemis.Entity;

public class WeaponFiredTrigger implements Trigger {

	@Override
	public boolean isAlreadyTriggered() {
		return false;
	}

	@Override
	public void trigger(Entity e) {
		handle(e);
	}

	protected boolean handle(Entity e) {
		return false;
	}

}
