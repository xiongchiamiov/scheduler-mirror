package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.schedule.Day;

public class DayGWT implements Serializable, Comparable {
	   /** Sunday */
	   public static final DayGWT SUN = new DayGWT(0);
	   /** Monday */
	   public static final DayGWT MON = new DayGWT(1);
	   /** Tuesday */
	   public static final DayGWT TUE = new DayGWT(2);
	   public static final DayGWT WED = new DayGWT(3);
	   public static final DayGWT THU = new DayGWT(4);
	   public static final DayGWT FRI = new DayGWT(5);
	   public static final DayGWT SAT = new DayGWT(6);
	   
	private int num;
	
	public DayGWT(int num) {
		this.num = num;
	}
	
	public DayGWT() {
	}
	
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