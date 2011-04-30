package com.gemserk.games.taken;

import com.artemis.Component;

public class CharacterControllerComponent extends Component {

	private final CharacterController characterController;
	
	public CharacterController getCharacterController() {
		return characterController;
	}

	public CharacterControllerComponent(CharacterController characterController) {
		this.characterController = characterController;
	}
	
}
