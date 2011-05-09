package com.gemserk.games.taken.components;

import com.artemis.Entity;

public class TimerTrigger {
	
	private boolean alreadyTriggered = false;

	public boolean isAlreadyTriggered() {
		return alreadyTriggered;
	}
	
	/**
	 * The owner Entity of the TriggerComponent.
	 */
	public void trigger(Entity e) {
		alreadyTriggered = handle(e);
	}
	
	protected boolean handle(Entity owner) {
		return true;
	}
	
}
