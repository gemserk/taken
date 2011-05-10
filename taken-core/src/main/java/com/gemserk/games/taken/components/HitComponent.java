package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.gemserk.commons.artemis.triggers.Trigger;

public class HitComponent extends Component {

	private final String group;

	private final float damage;

	private final Trigger trigger;

	public String getGroup() {
		return group;
	}
	
	public float getDamage() {
		return damage;
	}
	
	public Trigger getTrigger() {
		return trigger;
	}

	public HitComponent(String group, float damage, Trigger trigger) {
		this.group = group;
		this.damage = damage;
		this.trigger = trigger;
	}

}
