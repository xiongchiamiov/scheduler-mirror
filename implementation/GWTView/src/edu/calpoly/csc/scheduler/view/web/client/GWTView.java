package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTView implements EntryPoint {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	private VerticalPanel names;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		VerticalPanel panel = new VerticalPanel();
		names = new VerticalPanel();
		
		Button button = new Button("Get Professors", new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				greetingService.getProfessorNames(new AsyncCallback<ArrayList<String>>(){
					public void onFailure(Throwable caught){
						
						Window.alert("Failed to get professors: " + caught.toString());
					}
					
					public void onSuccess(ArrayList<String> result){
						names.clear();
						
						if(result != null){
							for(String s : result){
								names.add(new Label(s));
							}
						}
						
						Window.alert("Professor list successfully retrieved");
					}
				});
	        }
	    });
		
		panel.add(button);
		panel.add(names);
		
		RootPanel.get().add(panel);
	}
}
