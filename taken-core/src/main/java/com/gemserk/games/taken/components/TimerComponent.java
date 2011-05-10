package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.gemserk.commons.artemis.triggers.Trigger;

public class TimerComponent extends Component {
	
	private int time;
	
	private Trigger trigger;
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public boolean isFinished() {
		return time < 0;
	}
	
	public Trigger getTrigger() {
		return trigger;
	}
	
	public TimerComponent(int time, Trigger trigger) {
		this.time = time;
		this.trigger = trigger;
	}

}
