package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.gemserk.commons.gdx.camera.Camera;

public class CameraFollowComponent extends Component {

	private final Camera camera;
	
	public Camera getCamera() {
		return camera;
	}

	public CameraFollowComponent(Camera camera) {
		this.camera = camera;
	}
	
}
