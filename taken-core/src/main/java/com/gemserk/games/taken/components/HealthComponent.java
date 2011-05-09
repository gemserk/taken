package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.gemserk.componentsengine.utils.Container;

public class HealthComponent extends Component {

	private final Container health;
	
	public Container getHealth() {
		return health;
	}

	public HealthComponent(Container health) {
		this.health = health;
	}

}
