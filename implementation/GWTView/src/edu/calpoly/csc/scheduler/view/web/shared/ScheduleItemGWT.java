package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class ScheduleItemGWT implements Serializable {

	private String mCourseName;
	private String mProfessor;
	private String mCourseDept;
	private String mCourseNum;
	private int mSection;
	private String mDays;
	ArrayList<Integer> mDayNums;
	private int mStartTimeHour;
	private int mEndTimeHour;
	private int mStartTimeMin;
	private int mEndTimeMin;
	private boolean mPlaced = false;
	private String mRoom;
	private boolean mIsConflicted;
	private CourseGWT mCourse;

	public ScheduleItemGWT() {
	}

	public ScheduleItemGWT(CourseGWT course, String cName, String prof,
			String dept, String cNum, int sec, ArrayList<Integer> dNums,
			int sth, int stm, int eth, int etm, String rm, boolean conflicted) {
		
		mCourse = course;
		mCourseName = cName;
		mProfessor = prof;
		mCourseDept = dept;
		mCourseNum = cNum;
		mSection = sec;
		mDayNums = dNums;
		mDays = getDayString(mDayNums);
		mStartTimeHour = sth;
		mStartTimeMin = stm;
		mEndTimeHour = eth;
		mEndTimeMin = etm;
		mRoom = rm;
		mIsConflicted = conflicted;
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
		return mCourseDept + "<br>" + mCourseNum;
	}

	public String getHoverText() {
		String startHour, endHour, startMin, endMin;
		String startAmPm, endAmPm;

		startAmPm = mStartTimeHour >= 12 ? "pm" : "am";
		endAmPm = mEndTimeHour >= 12 ? "pm" : "am";

		if (mStartTimeHour > 12) {
			startHour = Integer.toString(mStartTimeHour - 12);
		} else {
			startHour = Integer.toString(mStartTimeHour);
		}
		if (mEndTimeHour > 12) {
			endHour = Integer.toString(mEndTimeHour - 12);
		} else {
			endHour = Integer.toString(mEndTimeHour);
		}
		if (mStartTimeMin < 10) {
			startMin = "0" + Integer.toString(mStartTimeMin);
		} else {
			startMin = Integer.toString(mStartTimeMin);
		}
		if (mEndTimeMin < 10) {
			endMin = "0" + Integer.toString(mEndTimeMin);
		} else {
			endMin = Integer.toString(mEndTimeMin);
		}

		return mCourseName + "<br>" + mCourseDept + " " + mCourseNum + "-"
				+ mSection + "<br>" + mRoom + "<br>" + mProfessor + "<br>" + mDays
				+ " " + startHour + ":" + startMin + startAmPm + " - "
				+ endHour + ":" + endMin + endAmPm;
	}
	
	public CourseGWT getCourse() {
		return mCourse;
	}
	
	public void setCourse(CourseGWT course) {
		mCourse = course;
	}

	public int getStartTimeHour() {
		return mStartTimeHour;
	}

	public void setStartTimeHour(int startTimeHour) {
		mStartTimeHour = startTimeHour;
	}
	
	public int getEndTimeHour() {
		return mEndTimeHour;
	}

	public int getStartTimeMin() {
		return mStartTimeMin;
	}

	public int getEndTimeMin() {
		return mEndTimeMin;
	}

	public ArrayList<Integer> getDayNums() {
		return mDayNums;
	}

	public void setDayNums(ArrayList<Integer> dayNums) {
		mDayNums = dayNums;
	}
	
	public String getProfessor() {
		return mProfessor;
	}

	public void setProfessor(String professor) {
		mProfessor = professor;
	}
	
	public String getCourseString() {
		return mCourseDept + " " + mCourseNum;
	}

	public String getRoom() {
		return mRoom;
	}

	public boolean isPlaced() {
		return mPlaced;
	}

	public void setPlaced(boolean p) {
		mPlaced = p;
	}

	public boolean startsAfterHalf() {
		return mStartTimeMin >= 30;
	}

	public boolean endsAfterHalf() {
		return mEndTimeMin >= 30;
	}

	public void setStartTimeMin(int min) {
		mStartTimeMin = min;
	}

	public void setEndTimeHour(int hour) {
		mEndTimeHour = hour;
	}

	public void setEndTimeMin(int min) {
		mEndTimeMin = min;
	}

	public String getDept() {
		return mCourseDept;
	}

	public String getCatalogNum() {
		return mCourseNum;
	}

	public int getSection() {
		return mSection;
	}

	public void setConflicted(boolean conflicted) {
		mIsConflicted = conflicted;
	}

	public boolean isConflicted() {
		return mIsConflicted;
	}
}
