package com.gemserk.games.taken.components;

import com.artemis.Component;

public class GrabComponent extends Component {
	
	private Trigger trigger;
	
	// should be using collision detection instead.
	// could be an area too, an area trigger or something...
	private float radius;
	
	public float getRadius() {
		return radius;
	}
	
	public Trigger getTrigger() {
		return trigger;
	}
	
	public GrabComponent(Trigger trigger) {
		this.trigger = trigger;
		this.radius = 0.25f;
	}
	
	public GrabComponent(float radius, Trigger trigger) {
		this.trigger = trigger;
		this.radius = radius;
	}

}
