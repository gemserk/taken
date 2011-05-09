package com.gemserk.games.taken;

import java.util.ArrayList;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.games.taken.components.PowerUpComponent;

public class PowerUpSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();
	
	private final ArrayList<PowerUp> removePowerUps = new ArrayList<PowerUp>();

	public PowerUpSystem() {
		super(PowerUpComponent.class);
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
		
		// apply only to the powerup holder, not the providers.
		
		PowerUpComponent powerUpComponent = e.getComponent(PowerUpComponent.class);
		
		ArrayList<PowerUp> powerUps = powerUpComponent.getPowerUps();
		removePowerUps.clear();
		
		for (int i = 0; i < powerUps.size(); i++) {
			PowerUp powerUp = powerUps.get(i);
			powerUp.setTime(powerUp.getTime() - world.getDelta());
			if (powerUp.getTime() < 0)  {
				Gdx.app.log("Taken", "Removing power up from PowerUpComponent from entity " + e.getId());
				removePowerUps.add(powerUp);
			}
		}
		
		powerUps.removeAll(removePowerUps);
		
	}

}
