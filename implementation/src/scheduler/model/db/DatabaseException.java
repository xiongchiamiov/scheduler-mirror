package scheduler.model.db;

public class DatabaseException extends Exception {
	private static final long serialVersionUID = 1337L;
	
	public DatabaseException(Throwable thr) {
		super(thr);
	}
	
	public DatabaseException(){}
}
