package scheduler.view.web.shared;

public class WorkingCopyDocumentExistsException extends Exception {
	private static final long serialVersionUID = 1L;

	public WorkingCopyDocumentExistsException() { }
	
	public WorkingCopyDocumentExistsException(String string) {
		super(string);
	}
}
