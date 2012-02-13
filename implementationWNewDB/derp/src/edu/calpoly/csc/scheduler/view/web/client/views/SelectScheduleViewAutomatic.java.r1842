package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.Import;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.Pair;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

public class SelectScheduleViewAutomatic extends SelectScheduleView {
	final int automaticOpenDocumentID;
	
	public SelectScheduleViewAutomatic(GreetingServiceAsync service,
			MenuBar menuBar, String username, int automaticOpenDocumentID) {
		super(service, menuBar, username);
		this.automaticOpenDocumentID = automaticOpenDocumentID;
	}
	
	@Override
	protected void doneAddingDocuments() {
		service.openExistingSchedule(automaticOpenDocumentID, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) { }
			public void onSuccess(String name) {
				openLoadedSchedule(automaticOpenDocumentID, name);
			}
		});
	}
}
