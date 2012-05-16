package scheduler.view.web.shared;

public class SessionClosedFromInactivityExceptionGWT extends Exception {
	private static final long serialVersionUID = 1L;

	public SessionClosedFromInactivityExceptionGWT() { }
	
	public SessionClosedFromInactivityExceptionGWT(String string) {
		super(string);
	}
}
