package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class TargetPositionComponent extends Component {

	private Vector2 position = new Vector2();
	
	public void setPosition(Vector2 position) {
		this.position.set(position);
	}
	
	public void setPosition(float x, float y) {
		this.position.set(x,y);
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public TargetPositionComponent() {
		// TODO Auto-generated constructor stub
	}
	
}
