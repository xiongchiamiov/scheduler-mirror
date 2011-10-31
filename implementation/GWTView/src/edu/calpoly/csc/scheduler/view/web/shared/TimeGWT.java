package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

public class TimeGWT implements Serializable, Comparable {
	protected int hour;
	protected int minute;
	
	public TimeGWT() {
	}
	
	public TimeGWT clone() {
		TimeGWT time = new TimeGWT();
		time.hour = hour;
		time.minute = minute;
		return time;
	}
	
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}

	@Override
	public int compareTo(Object that_) {
		TimeGWT that = (TimeGWT)that_;
		if (hour != that.hour)
			return hour - that.hour;
		if (minute != that.minute)
			return minute - that.minute;
		return 0;
	}
}
