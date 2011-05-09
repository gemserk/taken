package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.gemserk.games.taken.controllers.MovementController;
import com.gemserk.games.taken.controllers.JumpController;

public class CharacterControllerComponent extends Component {

	private final MovementController movementController;
	
	private final JumpController jumpController;
	
	public MovementController getCharacterController() {
		return movementController;
	}
	
	public JumpController getJumpController() {
		return jumpController;
	}

	public CharacterControllerComponent(MovementController movementController, JumpController jumpController) {
		this.movementController = movementController;
		this.jumpController = jumpController;
	}
	
}
