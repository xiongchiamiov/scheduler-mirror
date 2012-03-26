package edu.calpoly.csc.scheduler.model.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;

/**
 * This class is meant to hold the variables we only use during schedule generation 
 * and do not want to persist to the database.
 * 
 * @author Adam Armstrong
 *
 */
public class ScheduleDecorator {
	
	private HashMap<Instructor, Integer> WTUs;
	
	private HashMap<Instructor, WeekAvail> iAvailability;
	
	private HashMap<Instructor, Integer> iGenerosity;
	
	private HashMap<Instructor, Integer> iFairness;
	
	private HashMap<Location, WeekAvail> lAvailability;
	
	public ScheduleDecorator() {
		WTUs = new HashMap<Instructor, Integer>();
		iAvailability = new HashMap<Instructor, WeekAvail>();
		iGenerosity = new HashMap<Instructor, Integer>();
		iFairness = new HashMap<Instructor, Integer>();
		lAvailability = new HashMap<Location, WeekAvail>();
	}
	
	public void constructMaps(Collection<Instructor> i_list, Collection<Location> l_list) {
		List<Instructor> instructors = new ArrayList<Instructor>(i_list);
		List<Location> locations = new ArrayList<Location>(l_list);
		
		for(Instructor i : instructors) {
			WTUs.put(i, Integer.valueOf(0));
			iAvailability.put(i, new WeekAvail());
			iGenerosity.put(i, Integer.valueOf(0));
			iFairness.put(i, Integer.valueOf(0));
		}
		
		for(Location l : locations) {
			lAvailability.put(l, new WeekAvail());
		}
	}
	
	public void subtractWTU(Instructor i, Integer wtu) {
		WTUs.put(i, getCurWTU(i) - wtu);
	}
	
	public void addWTU(Instructor i, Integer wtu) {
		WTUs.put(i, getCurWTU(i) + wtu);
	}
	
	public int getCurWTU(Instructor i) {
		return WTUs.get(i).intValue();
	}
	
	public WeekAvail getIAvailability(Instructor i) {
		return iAvailability.get(i);
	}
	
	public WeekAvail getLAvailability(Location l) {
		return lAvailability.get(l);
	}
}
