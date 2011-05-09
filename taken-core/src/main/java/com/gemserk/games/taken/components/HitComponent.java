package com.gemserk.games.taken.components;

import com.artemis.Component;

public class HitComponent extends Component {

	private final String group;

	private final float damage;

	public String getGroup() {
		return group;
	}
	
	public float getDamage() {
		return damage;
	}

	public HitComponent(String group, float damage) {
		this.group = group;
		this.damage = damage;
	}

}
