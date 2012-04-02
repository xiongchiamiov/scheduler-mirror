package scheduler.view.web.client;

import scheduler.view.web.client.views.LoadingPopup;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class Login {
	public static void login(GreetingServiceAsync service, final String username, final AsyncCallback<Void> callback) {
		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		service.login(username, new AsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer userID) {
				popup.hide();
				callback.onSuccess(null);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				callback.onFailure(caught);
			}
		});
	}
}
