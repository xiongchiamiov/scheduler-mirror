package scheduler.view.web.shared;

/*
 * Wrap generate exceptions in a GenerateExceptionGWT, an exception you'll define in ViewWebShared in GreetingServiveImpl
 *  catch all of your exceptions and feed the get message into GenerateExceptionGWT and throw it upwards. You'll also need to 
 *  add throws declarations to any relevant methods in GreetingService and GreetingServiceImpl and GreetingServiceImplInner
 */

public class GenerateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message = "";
	
	public GenerateException() {
		this.message = "";
	}
	
	public GenerateException(String message) {
		this.message = message;
	}
	
	public String toString() {
		return message;
	}
}
