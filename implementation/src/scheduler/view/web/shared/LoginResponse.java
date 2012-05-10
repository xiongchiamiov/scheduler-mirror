package scheduler.view.web.shared;

import java.io.Serializable;

public class LoginResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int sessionID;
	public boolean isAdmin;
	public ServerResourcesResponse<OriginalDocumentGWT> initialOriginalDocuments = new ServerResourcesResponse<OriginalDocumentGWT>();
	
	public LoginResponse() { }
	
	public LoginResponse(int sessionID, boolean isAdmin,
			ServerResourcesResponse<OriginalDocumentGWT> initialOriginalDocuments) {
		this.sessionID = sessionID;
		this.isAdmin = isAdmin;
		this.initialOriginalDocuments = initialOriginalDocuments;
	}
}
