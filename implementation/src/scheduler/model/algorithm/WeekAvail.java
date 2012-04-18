package scheduler.model.algorithm;

import scheduler.model.Day;

public class WeekAvail
{
    //public static final long serialVersionUID = 42;
	
	private boolean[][] availability = new boolean[5][48];

    public WeekAvail() {
        for(int days = 0; days < 5; days++) {
            for(int times = 0; times < 48; times++) {
    		    availability[days][times] = true;
    	    }
        }
    }

    public boolean book(Week days, TimeRange tr) {
    	if(tr.getS() > tr.getE())
    		return false;
    	
	    for(Day day : days.getDays()) {
	    	for(int time = tr.getS(); time < tr.getE(); time++) {
	    		availability[day.ordinal() - 1][time] = false;
	    	}
	    }
	    return true;
    }
    
    public boolean unbook(Week days, TimeRange tr) {
    	if(tr.getS() > tr.getE())
    		return false;
    	
	    for(Day day : days.getDays()) {
	    	for(int time = tr.getS(); time < tr.getE(); time++) {
	    		availability[day.ordinal() - 1][time] = true;
	    	}
	    }
	    return true;
    }

    public boolean isFree(Week days, TimeRange tr) {
	    for(Day day : days.getDays()) {
	    	for(int time = tr.getS(); time < tr.getE(); time++) {
	    		System.err.println("Trying day: " + day.ordinal());
	    		System.err.println("With time: " + time);
	    		System.err.println("Found: " + availability[day.ordinal() - 1][time]);
	    		if(!availability[day.ordinal() - 1][time])
	    			return false;
	    	}
	    }
	    
	    return true;
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < 5; i++) {
    		for(int j = 0; j < 48; j++) {
    			sb.append("[" + availability[i][j] + "]");
    		}
    		sb.append("\n");
    	}
    	
    	return sb.toString();
    }
}
