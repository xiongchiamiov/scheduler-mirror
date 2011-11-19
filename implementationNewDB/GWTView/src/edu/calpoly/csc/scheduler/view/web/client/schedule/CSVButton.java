package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.views.WindowHandler;

public class CSVButton {

	private GreetingServiceAsync service;
	
	public CSVButton(GreetingServiceAsync service){
		this.service = service;
	}
	
	public Button getButton(){
		
		return new Button("Export to CSV",
			new ClickHandler(){
			public void onClick(ClickEvent event){
				clickHandler();
			}
		});
	}
	
	private void clickHandler(){
		
		//WindowHandler.setExitWarning(false);
		
		service.exportCSV(new AsyncCallback<Integer>() {
			
			// failed
			public void onFailure(Throwable caught) {
				
				Window.alert("Error exporting to CSV: 1");
			}
			
			// succeed
			public void onSuccess(Integer result) {
				
				if(result == null){
					Window.alert("Error exporting to CSV: 2");
				}
				
				else{
					
					Window.Location.replace("gwtview/export?"
						+ "param" + "=" + result);
				}
			}
		});
		
		//WindowHandler.setExitWarning(true);
	}
}
