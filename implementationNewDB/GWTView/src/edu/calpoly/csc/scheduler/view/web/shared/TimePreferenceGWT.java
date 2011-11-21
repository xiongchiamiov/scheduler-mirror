package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class TimePreferenceGWT implements Serializable {

	/** The desired time */
	private Integer time;
	
	/** The desire value */
	private int desire;
	
	public TimePreferenceGWT() {
	}
	
	public TimePreferenceGWT(TimePreferenceGWT that) {
		this.time = that.time;
		this.desire = that.desire;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public int getDesire() {
		return desire;
	}

	public void setDesire(int desire) {
		this.desire = desire;
	}
	
}
