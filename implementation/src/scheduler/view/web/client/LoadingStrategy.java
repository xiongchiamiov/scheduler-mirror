package scheduler.view.web.client;

public interface LoadingStrategy {
	void onStartedLoadingSomething();
	void onFinishedLoadingSomething();
}
