package edu.calpoly.csc.scheduler.view.web.client.views.resources.instructors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.views.LoadingPopup;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimePreferenceGWT;

public class InstructorsView extends VerticalPanel implements IViewContents, InstructorsTable.Strategy {
	private GreetingServiceAsync service;
	private String scheduleName;
	int nextTableInstructorID = -2;
	int transactionsPending = 0;
	Map<Integer, Integer> realIDsByTableID = new HashMap<Integer, Integer>();
	ViewFrame viewFrame;
	
	final ArrayList<Integer> deletedTableInstructorIDs = new ArrayList<Integer>();
	final ArrayList<InstructorGWT> editedTableInstructors = new ArrayList<InstructorGWT>();
	final ArrayList<InstructorGWT> addedTableInstructors = new ArrayList<InstructorGWT>();
	
	private int generateTableInstructorID() {
		return nextTableInstructorID--;
	}
	
	public InstructorsView(GreetingServiceAsync service, String scheduleName) {
		this.service = service;
		this.scheduleName = scheduleName;
		this.addStyleName("iViewPadding");
	}

	@Override
	public boolean canPop() {
		return true;
//		assert(table != null);
//		if (table.isSaved())
//			return true;
//		return Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}
	
	@Override
	public void afterPush(ViewFrame frame) {
		this.viewFrame = frame;
		
		this.setWidth("100%");
		this.setHeight("100%");

		this.add(new HTML("<h2>" + scheduleName + " - Instructors</h2>"));

		add(new InstructorsTable(this));
	}

	@Override
	public void getAllInstructors(final AsyncCallback<List<InstructorGWT>> callback) {
		final LoadingPopup popup = new LoadingPopup();
		popup.show();

		service.getInstructors(new AsyncCallback<List<InstructorGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				callback.onFailure(caught);
			}
			
			public void onSuccess(List<InstructorGWT> instructors){
				assert(instructors != null);
				popup.hide();
				
				callback.onSuccess(instructors);
			}
		});
	}

	@Override
	public InstructorGWT createInstructor() {
		InstructorGWT instructor = new InstructorGWT(
				generateTableInstructorID(), "", "", "", "", "", false, 5, 5, 0, 0,
				new HashMap<Integer, Map<Integer, TimePreferenceGWT>>(),
				new HashMap<Integer, Integer>());
		
		
		addedTableInstructors.add(instructor);
		
		assert(!editedTableInstructors.contains(instructor));
		
		assert(!deletedTableInstructorIDs.contains(instructor));

		sendUpdates();
		
		return instructor;
	}
	
	@Override
	public void onInstructorEdited(InstructorGWT instructor) {
		assert(!deletedTableInstructorIDs.contains(instructor.getID()));
		
		if (realIDsByTableID.containsKey(instructor.getID())) {
			// exists on remote side
			if (!editedTableInstructors.contains(instructor))
				editedTableInstructors.add(instructor);
		}
		else {
			// doesnt exist on remote side
			// do nothing, its already on the add list.
			assert(addedTableInstructors.contains(instructor));
		}
		
		sendUpdates();
	}
	
	@Override
	public void onInstructorDeleted(InstructorGWT instructor) {
		editedTableInstructors.remove(instructor);
		
		if (addedTableInstructors.contains(instructor)) {
			addedTableInstructors.remove(instructor);
			return;
		}
		
		assert(!deletedTableInstructorIDs.contains(instructor.getID()));
		deletedTableInstructorIDs.add(instructor.getID());
		
		sendUpdates();
	}

	private void sendUpdates() {
		assert(transactionsPending == 0);
		transactionsPending = deletedTableInstructorIDs.size() + editedTableInstructors.size() + addedTableInstructors.size();
		if (transactionsPending == 0)
			return;
		
		for (Integer deletedTableInstructorID : deletedTableInstructorIDs) {
			Integer realInstructorID = realIDsByTableID.get(deletedTableInstructorID);
			service.removeInstructor(realInstructorID, new AsyncCallback<Void>() {
				public void onSuccess(Void result) { updateFinished(); }
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		for (InstructorGWT editedTableInstructor : editedTableInstructors) {
			Integer realInstructorID = realIDsByTableID.get(editedTableInstructor.getID());
			InstructorGWT realInstructor = new InstructorGWT(editedTableInstructor);
			realInstructor.setID(realInstructorID);
			service.editInstructor(realInstructor, new AsyncCallback<Void>() {
				public void onSuccess(Void result) { updateFinished(); }
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		for (InstructorGWT addedTableInstructor : addedTableInstructors) {
			final int tableInstructorID = addedTableInstructor.getID();
			InstructorGWT realInstructor = new InstructorGWT(addedTableInstructor);
			realInstructor.setID(-1);
			service.addInstructor(realInstructor, new AsyncCallback<InstructorGWT>() {
				public void onSuccess(InstructorGWT result) {
					realIDsByTableID.put(tableInstructorID, result.getID());
					updateFinished();
				}
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		deletedTableInstructorIDs.clear();
		editedTableInstructors.clear();
		addedTableInstructors.clear();
	}
	
	private void updateFinished() {
		assert(transactionsPending > 0);
		transactionsPending--;
		if (transactionsPending == 0)
			sendUpdates();
	}

	@Override
	public void beforePop() { }
	@Override
	public void beforeViewPushedAboveMe() { }
	@Override
	public void afterViewPoppedFromAboveMe() { }
	@Override
	public Widget getContents() { return this; }

	@Override
	public void preferencesButtonClicked(InstructorGWT instructor) {
		if (viewFrame.canPopViewsAboveMe()) {
			viewFrame.popFramesAboveMe();
			viewFrame.frameViewAndPushAboveMe(new InstructorPreferencesView(service, scheduleName, instructor));
		}
	}
}


//package edu.calpoly.csc.scheduler.view.web.client.views.resources.instructors;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.HTML;
//import com.google.gwt.user.client.ui.VerticalPanel;
//import com.google.gwt.user.client.ui.Widget;
//
//import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
//import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
//import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
//import edu.calpoly.csc.scheduler.view.web.client.table.IFactory;
//import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
//import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
//import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;
//import edu.calpoly.csc.scheduler.view.web.client.table.MemberIntegerComparator;
//import edu.calpoly.csc.scheduler.view.web.client.table.MemberStringComparator;
//import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
//import edu.calpoly.csc.scheduler.view.web.client.table.columns.ButtonColumn;
//import edu.calpoly.csc.scheduler.view.web.client.table.columns.ButtonColumn.ClickCallback;
//import edu.calpoly.csc.scheduler.view.web.client.table.columns.DeleteColumn.DeleteObserver;
//import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingCheckboxColumn;
//import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingIntColumn;
//import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingStringColumn;
//import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
//import edu.calpoly.csc.scheduler.view.web.shared.TimePreferenceGWT;
//
//private class InstructorsOldView extends VerticalPanel implements IViewContents {
//	/** Instructor table */
//
//	// These static variables are a temporary hack to get around the table bug
//	public GreetingServiceAsync service;
//	
//	private final String scheduleName;
//	private OsmTable<InstructorGWT> table;
//	int nextInstructorID = -2;
//	
//	ViewFrame myFrame;
//
//	public InstructorsView(GreetingServiceAsync service, String scheduleName) {
//		assert(service != null);
//		this.service = service;
//		this.scheduleName = scheduleName;
//		this.addStyleName("iViewPadding");
//	}
//
//	@Override
//	public boolean canPop() {
//		assert(table != null);
//		if (table.isSaved())
//			return true;
//		return Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
//	}
//	
//	private int generateTemporaryInstructorID() {
//		return nextInstructorID--;
//	}
//	
//	@Override
//	public void afterPush(ViewFrame frame) {
//		this.myFrame = frame;
//		
//		this.setWidth("100%");
//		this.setHeight("100%");
//
//		this.add(new HTML("<h2>" + scheduleName + " - Instructors</h2>"));
//
//		final LoadingPopup popup = new LoadingPopup();
//		popup.show();
//		
//		table = new OsmTable<InstructorGWT>(
//				new IFactory<InstructorGWT>() {
//					public InstructorGWT create() {
//						System.out.println("Gets to this point to create this mofo");
//					}
//				});
//
//		table.addDeleteColumn(new DeleteObserver<InstructorGWT>() {
//			public void afterDelete(InstructorGWT object) {
//				service.removeInstructor(object, new AsyncCallback<Void>() {
//					public void onSuccess(Void result) { }
//					public void onFailure(Throwable caught) { }
//				});
//			}
//		});
//		
//		
//		this.add(table);
//		
////		System.out.println("sending request");
//		
//		service.getInstructors(new AsyncCallback<List<InstructorGWT>>() {
//			public void onFailure(Throwable caught) {
//				popup.hide();
//				Window.alert("Failed to get instructors: " + caught.toString());
//			}
//			
//			public void onSuccess(List<InstructorGWT> result){
////				System.out.println("onsuccess got response");
//				assert(result != null);
//				popup.hide();
//				table.addRows(result);
//			}
//		});
//	}
//	
//
////	
//	
//	
//	@Override
//	public void beforePop() { }
//	@Override
//	public void beforeViewPushedAboveMe() { }
//	@Override
//	public void afterViewPoppedFromAboveMe() { }
//	@Override
//	public Widget getContents() { return this; }
//}
