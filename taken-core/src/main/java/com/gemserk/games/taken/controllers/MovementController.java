package com.gemserk.games.taken.controllers;

import com.gemserk.commons.gdx.controllers.Controller;

public interface MovementController extends Controller {

	boolean isWalking();
	
	void getWalkingDirection(float[] d);

}
