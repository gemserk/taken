package com.gemserk.games.taken;

import com.gemserk.commons.gdx.controllers.Controller;

public interface CharacterController extends Controller {

	boolean isWalking();
	
	void getWalkingDirection(float[] d);

}
