package com.gemserk.games.taken;

import com.artemis.Component;
import com.gemserk.games.taken.controllers.CharacterController;
import com.gemserk.games.taken.controllers.JumpController;

public class CharacterControllerComponent extends Component {

	private final CharacterController characterController;
	
	private final JumpController jumpController;
	
	public CharacterController getCharacterController() {
		return characterController;
	}
	
	public JumpController getJumpController() {
		return jumpController;
	}

	public CharacterControllerComponent(CharacterController characterController, JumpController jumpController) {
		this.characterController = characterController;
		this.jumpController = jumpController;
	}
	
}
