package com.gemserk.games.taken;

import com.artemis.Component;
import com.gemserk.games.taken.controllers.CharacterController;

public class CharacterControllerComponent extends Component {

	private final CharacterController characterController;
	
	public CharacterController getCharacterController() {
		return characterController;
	}

	public CharacterControllerComponent(CharacterController characterController) {
		this.characterController = characterController;
	}
	
}
