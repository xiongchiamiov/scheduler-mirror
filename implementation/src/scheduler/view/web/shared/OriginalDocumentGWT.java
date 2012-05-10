package scheduler.view.web.shared;


public class OriginalDocumentGWT extends DocumentGWT implements Identified {
	private static final long serialVersionUID = 1L;

	Integer id;
	String workingChangesSummary;
	
	public OriginalDocumentGWT() { }
	
	public OriginalDocumentGWT(Integer id, String name, int staffInstructorID, int tbaLocationID, int chooseForMeInstructorID, int chooseForMeLocationID, boolean isTrashed, int startHalfHour, int endHalfHour, String workingChangesSummary) {
		super(name, staffInstructorID, tbaLocationID, chooseForMeInstructorID, chooseForMeLocationID, isTrashed, startHalfHour, endHalfHour);
		this.id = id;
		this.workingChangesSummary = workingChangesSummary;
	}
	
	public OriginalDocumentGWT(OriginalDocumentGWT that) {
		this(that.id, that.name, that.staffInstructorID, that.tbaLocationID, that.chooseForMeInstructorID, that.chooseForMeLocationID, that.isTrashed, that.startHalfHour, that.endHalfHour, that.workingChangesSummary);
	}

	public Integer getID() { return id; }
	public void setID(Integer id) { this.id = id; }
	
	public boolean hasWorkingCopy() { return this.workingChangesSummary != null; }
	
	public String getWorkingChangesSummary() { return this.workingChangesSummary; }
	public void setWorkingChangesSummary(String workingChangesSummary) { this.workingChangesSummary = workingChangesSummary; }

	public boolean attributesEqual(OriginalDocumentGWT that) {
		return super.attributesEqual(that) &&
				id.equals(that.id) &&
				(workingChangesSummary == null ? that.workingChangesSummary == null : workingChangesSummary.equals(that.workingChangesSummary));
	}
}
