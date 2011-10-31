package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

public class DayGWT implements Serializable, Comparable {
	private int num;
	
	public DayGWT clone() {
		DayGWT result = new DayGWT();
		result.num = num;
		return result;
	}
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public int compareTo(Object that_) {
		DayGWT that = (DayGWT)that_;
		if (num != that.num)
			return num - that.num;
		return 0;
	}
}
