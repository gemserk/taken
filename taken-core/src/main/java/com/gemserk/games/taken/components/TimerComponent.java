package com.gemserk.games.taken.components;

import com.artemis.Component;

public class TimerComponent extends Component {
	
	private int time;
	
	private TimerTrigger timerTrigger;
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public boolean isFinished() {
		return time < 0;
	}
	
	public TimerTrigger getTimerTrigger() {
		return timerTrigger;
	}

	public TimerComponent(int time) {
		this(time, new TimerTrigger());
	}
	
	public TimerComponent(int time, TimerTrigger timerTrigger) {
		this.time = time;
		this.timerTrigger = timerTrigger;
	}

}
