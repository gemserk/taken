package com.gemserk.games.taken.components;

import com.artemis.Component;

public class GrabComponent extends Component {
	
	private GrabbedTrigger trigger;
	
	// should be using collision detection instead.
	// could be an area too, an area trigger or something...
	private float radius;
	
	public float getRadius() {
		return radius;
	}
	
	public GrabbedTrigger getGrabHandler() {
		return trigger;
	}
	
	public GrabComponent() {
		this(new GrabbedTrigger());
	}
	
	public GrabComponent(GrabbedTrigger grabbedTrigger) {
		this.trigger = grabbedTrigger;
		this.radius = 0.25f;
	}
	
	public GrabComponent(float radius, GrabbedTrigger grabbedTrigger) {
		this.trigger = grabbedTrigger;
		this.radius = radius;
	}

}
