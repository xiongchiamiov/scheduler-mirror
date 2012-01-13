package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.table.IFactory;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.MemberIntegerComparator;
import edu.calpoly.csc.scheduler.view.web.client.table.MemberStringComparator;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.ButtonColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.ButtonColumn.ClickCallback;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingCheckboxColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingStringColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.IntColumn;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimePreferenceGWT;

public class InstructorsView extends VerticalPanel implements IViewContents {
	/** Instructor table */
	public static final String INSTR_NAME = "Instructor Name";

	public static final String INSTR_FIRSTNAME = "First Name";

	public static final String INSTR_LASTNAME = "Last Name";

	public static final String INSTR_ID = "ID";
	
	public static final String INSTR_MAX_WTU = "Max WTU";
	
	public static final String INSTR_OFFICE = "Office";
	
	public static final String INSTR_BUILDING = "Building";

	public static final String INSTR_ROOMNUMBER = "Room#";

	public static final String INSTR_DISABILITIES = "Disabilities";
	
	public static final String INSTR_PREFERENCES = "Preferences";

	// These static variables are a temporary hack to get around the table bug
	public GreetingServiceAsync service;
	
	private final String scheduleName;
	private OsmTable<InstructorGWT> table;
	int nextInstructorID = 1;
	
	ViewFrame myFrame;

	public InstructorsView(GreetingServiceAsync service, String scheduleName) {
		assert(service != null);
		this.service = service;
		this.scheduleName = scheduleName;
		this.addStyleName("iViewPadding");
	}

	@Override
	public boolean canPop() {
		assert(table != null);
		if (table.isSaved())
			return true;
		return Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}
	
	@Override
	public void afterPush(ViewFrame frame) {
		this.myFrame = frame;
		
		this.setWidth("100%");
		this.setHeight("100%");

		this.add(new HTML("<h2>" + scheduleName + " - Instructors</h2>"));

		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		table = new OsmTable<InstructorGWT>(
				new IFactory<InstructorGWT>() {
					public InstructorGWT create() {
						return new InstructorGWT(
								nextInstructorID++, "", "", "", "", "", false, 5, 5, 0, 0,
								new HashMap<Integer, Map<Integer, TimePreferenceGWT>>(),
								new HashMap<Integer, Integer>());
					}
				},
				new OsmTable.ModifyHandler<InstructorGWT>() {
					@Override
					public void add(InstructorGWT toAdd, AsyncCallback<Integer> callback) {
						service.addInstructor(toAdd, callback);
					}
					@Override
					public void edit(InstructorGWT toEdit, AsyncCallback<Void> callback) {
						service.editInstructor(toEdit, callback);
					}
					public void remove(InstructorGWT toRemove, AsyncCallback<Void> callback) {
						service.removeInstructor(toRemove, callback);
					}
				});

		table.addDeleteColumn();
		table.addEditSaveColumn();

		table.addColumn(
				INSTR_FIRSTNAME,
				"6em",
				true,
				new MemberStringComparator<InstructorGWT>(new IStaticGetter<InstructorGWT, String>() {
					public String getValueForObject(InstructorGWT object) { return object.getFirstName(); }
				}),
				new EditingStringColumn<InstructorGWT>(
						new IStaticGetter<InstructorGWT, String>() {
							public String getValueForObject(InstructorGWT object) { return object.getFirstName(); }
						},
						new IStaticSetter<InstructorGWT, String>() {
							public void setValueInObject(InstructorGWT object, String newValue) { object.setFirstName(newValue); }
						},
						null));

		table.addColumn(
				INSTR_LASTNAME,
				"6em",
				true,
				new MemberStringComparator<InstructorGWT>(new IStaticGetter<InstructorGWT, String>() {
					public String getValueForObject(InstructorGWT object) { return object.getLastName(); }
				}),
				new EditingStringColumn<InstructorGWT>(
						new IStaticGetter<InstructorGWT, String>() {
							public String getValueForObject(InstructorGWT object) { return object.getLastName(); }
						},
						new IStaticSetter<InstructorGWT, String>() {
							public void setValueInObject(InstructorGWT object, String newValue) { object.setLastName(newValue); }
						},
						null));
		
		table.addColumn(
				INSTR_ID,
				"6em",
				true,
				new MemberStringComparator<InstructorGWT>(new IStaticGetter<InstructorGWT, String>() {
					public String getValueForObject(InstructorGWT object) { return object.getUserID(); }
				}),
				new EditingStringColumn<InstructorGWT>(
						new IStaticGetter<InstructorGWT, String>() {
							public String getValueForObject(InstructorGWT object) { return object.getUserID(); }
						},
						new IStaticSetter<InstructorGWT, String>() {
							public void setValueInObject(InstructorGWT object, String newValue) { object.setUserID(newValue); }
						}, 
						new IStaticValidator<InstructorGWT, String>() {
							public void validate(InstructorGWT object, String newId) throws InvalidValueException {
								if (!canSetUserID(object, newId))
									throw new InvalidValueException("There is already a user with ID: " + newId);
							}
						}));

		table.addColumn(
				INSTR_MAX_WTU,
				"4em",
				true,
				new MemberIntegerComparator<InstructorGWT>(new IStaticGetter<InstructorGWT, Integer>() {
					public Integer getValueForObject(InstructorGWT object) { return object.getMaxWtu(); }
				}),
				new IntColumn<InstructorGWT>(
						new IStaticGetter<InstructorGWT, Integer>() {
							public Integer getValueForObject(InstructorGWT object) { return object.getMaxWtu(); }
						},
						new IStaticSetter<InstructorGWT, Integer>() {
							public void setValueInObject(InstructorGWT object, Integer newValue) { object.setMaxWtu(newValue); }
						}, 
						new IStaticValidator<InstructorGWT, Integer>() {
							public void validate(InstructorGWT object, Integer newWtu) throws InvalidValueException {
								if (newWtu < 0)
									throw new InvalidValueException(INSTR_MAX_WTU + " must be positive: " + newWtu + " is invalid.");
							}
						}));
		
		table.addColumn(
				INSTR_DISABILITIES,
				"4em",
				true,
				new MemberIntegerComparator<InstructorGWT>(new IStaticGetter<InstructorGWT, Integer>() {
					public Integer getValueForObject(InstructorGWT object) { return object.getDisabilities() ? 1 : 0; }
				}),
				new EditingCheckboxColumn<InstructorGWT>(
						new IStaticGetter<InstructorGWT, Boolean>() {
							public Boolean getValueForObject(InstructorGWT object) { return object.getDisabilities(); }
						},
						new IStaticSetter<InstructorGWT, Boolean>() {
							public void setValueInObject(InstructorGWT object, Boolean newValue) { object.setDisabilities(newValue); }
						}));
		
		table.addColumn(
				INSTR_PREFERENCES,
				"4em",
				true,
				null,
				new ButtonColumn<InstructorGWT>(
						"Preferences",
						new ClickCallback<InstructorGWT>(){
							public void buttonClickedForObject(InstructorGWT object, Button button) {
								if (myFrame.canPopViewsAboveMe()) {
									myFrame.popFramesAboveMe();
									myFrame.frameViewAndPushAboveMe(new InstructorPreferencesView(service, scheduleName, object));
								}	
							}
						}));
		
		
		this.add(table);
		
//		System.out.println("sending request");
		
		service.getInstructors(new AsyncCallback<List<InstructorGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get instructors: " + caught.toString());
			}
			
			public void onSuccess(List<InstructorGWT> result){
//				System.out.println("onsuccess got response");
				assert(result != null);
				popup.hide();
				for (InstructorGWT instr : result)
					nextInstructorID = Math.max(nextInstructorID, instr.getID() + 1);
				table.addRows(result);
			}
		});
	}
	
	
	private boolean canSetUserID(InstructorGWT forObject, String newUserID) {
		for (InstructorGWT instr : table.getObjects()) {
			if (instr == forObject)
				continue;
			if (instr.getUserID().equals(newUserID))
				return false;
		}
		return true;
	}

	@Override
	public void beforePop() { }
	@Override
	public void beforeViewPushedAboveMe() { }
	@Override
	public void afterViewPoppedFromAboveMe() { }
	@Override
	public Widget getContents() { return this; }
}
 