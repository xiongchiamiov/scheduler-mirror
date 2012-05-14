package scheduler.view.web.client;

public interface NotifyFailureStrategy {
	void onFailure(String failureString);
	void onFailure(String failureString, Throwable caught);
}
