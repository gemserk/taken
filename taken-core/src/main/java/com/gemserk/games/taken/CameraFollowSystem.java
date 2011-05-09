package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.games.taken.components.CameraFollowComponent;

public class CameraFollowSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	public CameraFollowSystem() {
		super(CameraFollowComponent.class);
	}

	@Override
	public void toggle() {
		activableSystem.toggle();
	}

	@Override
	public boolean isEnabled() {
		return activableSystem.isEnabled();
	}

	@Override
	protected void process(Entity e) {

		CameraFollowComponent cameraFollowComponent = e.getComponent(CameraFollowComponent.class);
		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
		SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);

		Camera camera = cameraFollowComponent.getCamera();
		Vector2 position = spatialComponent.getPosition();

		Sprite sprite = spriteComponent.getSprite();
		float width = SpriteUtils.getOriginalHeight(sprite);
		
		camera.setPosition(position.x, position.y);
		camera.setZoom(width / spatialComponent.getSize().x);

	}

}
