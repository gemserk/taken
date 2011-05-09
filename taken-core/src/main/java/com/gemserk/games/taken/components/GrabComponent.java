package com.gemserk.games.taken.components;

import com.artemis.Component;

public class GrabComponent extends Component {
	
	private GrabHandler grabHandler;
	
	// should be using collision detection instead.
	// could be an area too, an area trigger or something...
	private float radius;
	
	public float getRadius() {
		return radius;
	}
	
	public GrabHandler getGrabHandler() {
		return grabHandler;
	}
	
	public GrabComponent() {
		this(new GrabHandler());
	}
	
	public GrabComponent(GrabHandler grabHandler) {
		this.grabHandler = grabHandler;
		this.radius = 0.25f;
	}
	
	public GrabComponent(float radius, GrabHandler grabHandler) {
		this.grabHandler = grabHandler;
		this.radius = radius;
	}

}
