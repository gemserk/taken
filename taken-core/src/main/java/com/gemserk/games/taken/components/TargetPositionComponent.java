package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.triggers.Trigger;

public class TargetPositionComponent extends Component {

	private final Trigger trigger;

	private Vector2 position = new Vector2();
	
	private float distance = 5f;
	
	public void setPosition(Vector2 position) {
		this.position.set(position);
	}
	
	public void setPosition(float x, float y) {
		this.position.set(x,y);
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public Trigger getTrigger() {
		return trigger;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public TargetPositionComponent(Trigger trigger) {
		this.trigger = trigger;
	}
	
}
