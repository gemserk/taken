package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.artemis.Entity;

public class TargetComponent extends Component {

	private Entity target;
	
	public void setTarget(Entity target) {
		this.target = target;
	}
	
	public Entity getTarget() {
		return target;
	}
	
}
