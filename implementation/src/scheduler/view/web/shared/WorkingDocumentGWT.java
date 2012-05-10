package scheduler.view.web.shared;


public class WorkingDocumentGWT extends DocumentGWT {
	private static final long serialVersionUID = 1L;
	
	int realID;
	
	public WorkingDocumentGWT() { }
	
	public WorkingDocumentGWT(int realID, String name, int staffInstructorID, int tbaLocationID, int chooseForMeInstructorID, int chooseForMeLocationID, boolean isTrashed, int startHalfHour, int endHalfHour) {
		super(name, staffInstructorID, tbaLocationID, chooseForMeInstructorID, chooseForMeLocationID, isTrashed, startHalfHour, endHalfHour);
		this.realID = realID;
	}
	
	public WorkingDocumentGWT(WorkingDocumentGWT that) {
		this(that.realID, that.name, that.staffInstructorID, that.tbaLocationID, that.chooseForMeInstructorID, that.chooseForMeLocationID, that.isTrashed, that.startHalfHour, that.endHalfHour);
	}

	public boolean attributesEqual(WorkingDocumentGWT that) {
		return super.attributesEqual(that) &&
				realID == that.realID;
	}

	public int getRealID() { return realID; }
	public void setRealID(int id) { realID = id; }
}
