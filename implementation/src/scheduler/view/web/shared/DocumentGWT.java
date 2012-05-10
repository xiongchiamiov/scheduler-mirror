package scheduler.view.web.shared;

import java.io.Serializable;

public abstract class DocumentGWT implements Serializable {
	private static final long serialVersionUID = 1L;
	
	String name;
	Integer staffInstructorID;
	Integer tbaLocationID;
	Integer chooseForMeInstructorID;
	Integer chooseForMeLocationID;
	boolean isTrashed;
	int startHalfHour;
	int endHalfHour;
	
	protected DocumentGWT() { }
	
	protected DocumentGWT(String name, int staffInstructorID, int tbaLocationID, int chooseForMeInstructorID, int chooseForMeLocationID, boolean isTrashed, int startHalfHour, int endHalfHour) {
		this.name = name;
		this.staffInstructorID = staffInstructorID;
		this.tbaLocationID = tbaLocationID;
		this.chooseForMeInstructorID = chooseForMeInstructorID;
		this.chooseForMeLocationID = chooseForMeLocationID;
		this.isTrashed = isTrashed;
		this.startHalfHour = startHalfHour;
		this.endHalfHour = endHalfHour;
	}
	
	protected DocumentGWT(DocumentGWT that) {
		this(that.name, that.staffInstructorID, that.tbaLocationID, that.chooseForMeInstructorID, that.chooseForMeLocationID, that.isTrashed, that.startHalfHour, that.endHalfHour);
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public Integer getStaffInstructorID() { return staffInstructorID; }
	public void setStaffInstructorID(Integer staffInstructorID) { this.staffInstructorID = staffInstructorID; }
	public Integer getTBALocationID() { return tbaLocationID; }
	public void setTBALocationID(Integer tbaLocationID) { this.tbaLocationID = tbaLocationID; }
	public boolean isTrashed() { return isTrashed; }
	public void setTrashed(boolean isTrashed) { this.isTrashed = isTrashed; }
	public int getStartHalfHour() { return startHalfHour; }
	public void setStartHalfHour(int startHalfHour) { this.startHalfHour = startHalfHour; }
	public int getEndHalfHour() { return endHalfHour; }
	public void setEndHalfHour(int endHalfHour) { this.endHalfHour = endHalfHour; }

	public Integer getChooseForMeInstructorID() { return chooseForMeInstructorID; }
	public void setChooseForMeInstructorID(Integer chooseForMeInstructorID) { this.chooseForMeInstructorID = chooseForMeInstructorID; }
	public Integer getChooseForMeLocationID() { return chooseForMeLocationID; }
	public void setChooseForMeLocationID(Integer chooseForMeLocationID) { this.chooseForMeLocationID = chooseForMeLocationID; }

	protected boolean attributesEqual(DocumentGWT that) {
		return name.equals(that.name) &&
				staffInstructorID == that.staffInstructorID &&
				tbaLocationID == that.tbaLocationID &&
				isTrashed == that.isTrashed &&
				startHalfHour == that.startHalfHour &&
				endHalfHour == that.endHalfHour;
	}
}
