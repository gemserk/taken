package com.gemserk.games.taken;

import com.artemis.Component;
import com.gemserk.componentsengine.utils.Container;

public class HealthComponent extends Component {

	private final Container container;
	
	public Container getContainer() {
		return container;
	}

	public HealthComponent(Container container) {
		this.container = container;
	}

}
