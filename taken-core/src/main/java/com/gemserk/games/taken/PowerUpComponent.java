package com.gemserk.games.taken;

import java.util.ArrayList;

import com.artemis.Component;

public class PowerUpComponent extends Component {
	
	private ArrayList<PowerUp> powerUps;
	
	public ArrayList<PowerUp> getPowerUps() {
		return powerUps;
	}
	
	public void add(ArrayList<PowerUp> powerUps) {
		this.powerUps.addAll(powerUps);
	}
	
	public PowerUpComponent() {
		this.powerUps = new ArrayList<PowerUp>();
	}

	public PowerUpComponent(PowerUp ...powerUps) {
		this.powerUps = new ArrayList<PowerUp>(powerUps.length);
		for (int i = 0; i < powerUps.length; i++) {
			this.powerUps.add(powerUps[i]);
		}
	}

	public PowerUpComponent(ArrayList<PowerUp> powerUps) {
		this.powerUps = new ArrayList<PowerUp>(powerUps);
	}

}
