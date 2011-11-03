package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

public class TimeGWT implements Serializable, Comparable<TimeGWT> {
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
	public int compareTo(TimeGWT that) {
		if (hour != that.hour)
			return hour - that.hour;
		return minute - that.minute;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof TimeGWT && compareTo((TimeGWT)obj) == 0;
	}
	
	@Override
	public int hashCode() {
		return hour * 1037 + minute;
	}
}
