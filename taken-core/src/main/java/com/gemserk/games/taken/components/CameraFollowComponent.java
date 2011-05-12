package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.gemserk.commons.gdx.camera.Camera;

public class CameraFollowComponent extends Component {

	private final Camera cameraImpl;
	
	public Camera getCamera() {
		return cameraImpl;
	}

	public CameraFollowComponent(Camera cameraImpl) {
		this.cameraImpl = cameraImpl;
	}
	
}
