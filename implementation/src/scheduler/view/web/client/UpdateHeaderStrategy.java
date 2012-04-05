package scheduler.view.web.client;

public interface UpdateHeaderStrategy {
	public void clearHeader();
	public void onLogin(String username);
	public void onOpenedDocument(String documentName);
	public void onDocumentNameChanged(String newDocumentName);
	public void setDocumentChanged(boolean documentChanged); 
}
