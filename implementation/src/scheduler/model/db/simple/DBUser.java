package scheduler.model.db.simple;

import scheduler.model.db.IDBUser;

public class DBUser extends DBObject implements IDBUser {
	private static final long serialVersionUID = 1337L;
	
	String username;
	boolean isAdmin;
	
	public DBUser(Integer id, String username, boolean isAdmin) {
		super(id);
		this.username = username;
		this.isAdmin = isAdmin;
	}
	
	public DBUser(DBUser that) {
		this(that.id, that.username, that.isAdmin);
	}
	
	@Override
	public String getUsername() { return username; }
	@Override
	public void setUsername(String username) { this.username = username; }
	@Override
	public boolean isAdmin() { return isAdmin; }
	@Override
	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
	
	public void sanityCheck() {
		assert(username != null);
	}
}
