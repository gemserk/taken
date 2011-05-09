package com.gemserk.games.taken.components;

import com.artemis.Component;

public class GrabComponent extends Component {
	
	private GrabHandler grabHandler;
	
	public GrabHandler getGrabHandler() {
		return grabHandler;
	}
	
	public GrabComponent() {
		this(new GrabHandler());
	}
	
	public GrabComponent(GrabHandler grabHandler) {
		this.grabHandler = grabHandler;
	}

}
