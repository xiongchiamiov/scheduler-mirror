package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class TimePreferenceGWT implements Serializable {

	/** The desired time */
	private TimeGWT time;
	
	/** The desire value */
	private int desire;
	
	public TimePreferenceGWT() {
	}
	
	public TimePreferenceGWT clone() {
		TimePreferenceGWT pref = new TimePreferenceGWT();
		pref.time = time.clone();
		pref.desire = desire;
		return pref;
	}

	public TimeGWT getTime() {
		return time;
	}

	public void setTime(TimeGWT time) {
		this.time = time;
	}

	public int getDesire() {
		return desire;
	}

	public void setDesire(int desire) {
		this.desire = desire;
	}
	
}
