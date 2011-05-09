package com.gemserk.games.taken.components;

import com.artemis.Component;

public class TimerComponent extends Component {
	
	private int time;
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public boolean isFinished() {
		return time < 0;
	}

	public TimerComponent(int time) {
		this.time = time;
	}

}
