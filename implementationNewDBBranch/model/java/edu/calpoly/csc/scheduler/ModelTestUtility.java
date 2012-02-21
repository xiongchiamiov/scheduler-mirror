package edu.calpoly.csc.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;

public class ModelTestUtility {
	public static Course createCourse(Model model, Document document) {
		return model.assembleCourse(document, "Test", "101", "CSC", "4", "4", "1",
				"LEC", "60", "6", new HashSet<String>(),
				new ArrayList<Set<Day>>(), true);
	}
	
	public static Location createLocation(Model model, Document document) {
		return model.assembleLocation(document, "123", "LEC", "60", new HashSet<String>());
	}
	
	public static Instructor createInstructor(Model model, Document document) {
		return model.assembleInstructor(document, "TestFirst", "TestLast", "testid", "4", new HashMap<Day, HashMap<Integer, Integer>>(), new HashMap<Integer, Integer>());
	}
	
}
