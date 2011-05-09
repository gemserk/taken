package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class FollowCharacterComponent extends Component {
	
	private Vector2 targetPosition;
	
	private float targetAngle;
	
	public float getTargetAngle() {
		return targetAngle;
	}
	
	public Vector2 getTargetPosition() {
		return targetPosition;
	}
	
	public void setTargetAngle(float targetAngle) {
		this.targetAngle = targetAngle;
	}
	
	public void setTargetPosition(Vector2 targetPosition) {
		this.targetPosition = targetPosition;
	}

	public FollowCharacterComponent(Vector2 targetPosition, float targetAngle) {
		this.targetPosition = targetPosition;
		this.targetAngle = targetAngle;
	}

}
