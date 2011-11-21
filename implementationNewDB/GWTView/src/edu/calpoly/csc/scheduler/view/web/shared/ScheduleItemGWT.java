package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class ScheduleItemGWT implements Serializable {
	// private static final long serialVersionUID = 5345021880975658731L;
	private String courseName;
	private String professor;
	private String courseDept;
	private int courseNum;
	private int section;
	private String days;
	ArrayList<Integer> dayNums;
	private int startTimeHour;
	private int endTimeHour;
	private int startTimeMin;
	private int endTimeMin;
	private boolean placed = false;
	private String room;
	private boolean isConflicted;
 private CourseGWT course;
 
	public ScheduleItemGWT() {
	}

	public ScheduleItemGWT(CourseGWT course, String cName, String prof, String dept,
			int cNum, int sec, ArrayList<Integer> dNums, int sth, int stm,
			int eth, int etm, String rm, boolean conflicted) {
  this.course = course;
	 courseName = cName;
		professor = prof;
		courseDept = dept;
		courseNum = cNum;
		section = sec;
		dayNums = dNums;
		days = getDayString(dayNums);
		startTimeHour = sth;
		startTimeMin = stm;
		endTimeHour = eth;
		endTimeMin = etm;
		room = rm;
		isConflicted = conflicted;
	}

	public CourseGWT getCourse()
	{
	 return course;
	}
	
	private String getDayString(ArrayList<Integer> dayNums) {
		String dayString = new String();
		for (int day : dayNums) {
			switch (day) {
			case 1:
				dayString += "M";
				break;
			case 2:
				dayString += "T";
				break;
			case 3:
				dayString += "W";
				break;
			case 4:
				dayString += "R";
				break;
			case 5:
				dayString += "F";
				break;
			}
		}
		return dayString;
	}

	public String getSchdItemText() {
		return courseDept + "<br>" + courseNum;
	}

	public String getHoverText() {
		String startHour, endHour, startMin, endMin;
		String startAmPm, endAmPm;

		startAmPm = startTimeHour >= 12 ? "pm" : "am";
		endAmPm = endTimeHour >= 12 ? "pm" : "am";

		if (startTimeHour > 12) {
			startHour = Integer.toString(startTimeHour - 12);
		} else {
			startHour = Integer.toString(startTimeHour);
		}
		if (endTimeHour > 12) {
			endHour = Integer.toString(endTimeHour - 12);
		} else {
			endHour = Integer.toString(endTimeHour);
		}
		if (startTimeMin < 10) {
			startMin = "0" + Integer.toString(startTimeMin);
		} else {
			startMin = Integer.toString(startTimeMin);
		}
		if (endTimeMin < 10) {
			endMin = "0" + Integer.toString(endTimeMin);
		} else {
			endMin = Integer.toString(endTimeMin);
		}

		return courseName + "<br>" + courseDept + " " + courseNum + "-" + section + "<br>" + room + "<br>" + professor
				+ "<br>" + days + " " + startHour + ":" + startMin + startAmPm + " - "
				+ endHour + ":" + endMin + endAmPm;
	}

	public int getStartTimeHour() {
		return startTimeHour;
	}

	public int getEndTimeHour() {
		return endTimeHour;
	}

	public int getStartTimeMin() {
		return startTimeMin;
	}

	public int getEndTimeMin() {
		return endTimeMin;
	}

	public ArrayList<Integer> getDayNums() {
		return dayNums;
	}

	public String getProfessor() {
		return professor;
	}

	public String getCourseString() {
		return courseDept + " " + courseNum;
	}

	public String getRoom() {
		return room;
	}

	public boolean isPlaced() {
		return placed;
	}

	public void setPlaced(boolean p) {
		placed = p;
	}

	public boolean startsAfterHalf() {
		return startTimeMin >= 30;
	}

	public boolean endsAfterHalf() {
		return endTimeMin >= 30;
	}

	public void setStartTimeHour(int hour) {
		startTimeHour = hour;
	}

	public void setStartTimeMin(int min) {
		startTimeMin = min;
	}

	public void setEndTimeHour(int hour) {
		endTimeHour = hour;
	}

	public void setEndTimeMin(int min) {
		endTimeMin = min;
	}

	public String getDept() {
		return courseDept;
	}

	public int getCatalogNum() {
		return courseNum;
	}

	public int getSection() {
		return section;
	}
	
	public void setConflicted(boolean conflicted)
	{
		isConflicted = conflicted;
	}
	
	public boolean isConflicted()
	{
		return isConflicted;
	}
}
