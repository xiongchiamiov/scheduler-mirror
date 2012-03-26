package edu.calpoly.csc.scheduler.model.algorithm;

import edu.calpoly.csc.scheduler.model.Instructor;

public class InstructorDecorator {

	private Instructor instructor;
	private Integer WTU;
	private WeekAvail availability;
	
	public InstructorDecorator(Instructor ins) {
		this.instructor =  ins;
		this.WTU = Integer.valueOf(0);
		this.availability = new WeekAvail();
	}
	
	public void subtractWTU(Integer wtu) {
		this.WTU -= wtu;
	}
	
	public void addWTU(Integer wtu) {
		this.WTU += wtu;
	}
	
	public int getCurWTU() {
		return this.WTU.intValue();
	}
	
	public WeekAvail getAvailability() {
		return this.availability;
	}
	
	public Instructor getInstructor() {
		return this.instructor;
	}
}
