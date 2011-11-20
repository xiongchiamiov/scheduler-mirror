package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.table.ButtonColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.ButtonColumn.ClickCallback;
import edu.calpoly.csc.scheduler.view.web.client.table.CheckboxColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.Factory;
import edu.calpoly.csc.scheduler.view.web.client.table.IntColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.StringColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.TableConstants;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorsView extends VerticalPanel implements IViewContents {
	// These static variables are a temporary hack to get around the table bug
	public GreetingServiceAsync service;
	
	private final String scheduleName;
	private OsmTable<InstructorGWT> table;
	int nextLocationID = 1;
	
	ViewFrame myFrame;

	public InstructorsView(GreetingServiceAsync service, String scheduleName) {
		assert(service != null);
		this.service = service;
		this.scheduleName = scheduleName;
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
		this.setWidth("100%");
		this.setHeight("100%");

		this.add(new HTML("<h2>" + scheduleName + " - Instructors</h2>"));

		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		/*
		setWidth("100%");

		add(new HTML("<h2>" + scheduleName + " - Instructors</h2>"));
		
		iTable = TableFactory.instructor(service);
		add(iTable.getWidget());
		
		iTable.clear();


		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		
		service.getInstructors(new AsyncCallback<ArrayList<InstructorGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				
				Window.alert("Failed to get professors: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<InstructorGWT> result){
				popup.hide();
				
				if (result != null) {
					for (InstructorGWT ins : result)
						ins.verify();
					
					iTable.set(result);
				}
			}
		});
		*/
		
		table = new OsmTable<InstructorGWT>(
				new Factory<InstructorGWT>() {
					public InstructorGWT create() {
						return new InstructorGWT(nextLocationID++);
					}
					public InstructorGWT createHistoryFor(InstructorGWT location) {
						InstructorGWT i = location.clone();
						i.setId(-location.getId());
						return i;
					}
				},
				new OsmTable.SaveHandler<InstructorGWT>() {
					public void saveButtonClicked() {
						save();
					}
				});

		table.addColumn(new StringColumn<InstructorGWT>(TableConstants.INSTR_FIRSTNAME, "6em",
				new StaticGetter<InstructorGWT, String>() {
					public String getValueForObject(InstructorGWT object) { return object.getFirstName(); }
				},
				new StaticSetter<InstructorGWT, String>() {
					public void setValueInObject(InstructorGWT object, String newValue) { object.setFirstName(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER, null));

		table.addColumn(new StringColumn<InstructorGWT>(TableConstants.INSTR_LASTNAME, "6em",
				new StaticGetter<InstructorGWT, String>() {
					public String getValueForObject(InstructorGWT object) { return object.getLastName(); }
				},
				new StaticSetter<InstructorGWT, String>() {
					public void setValueInObject(InstructorGWT object, String newValue) { object.setLastName(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER, null));
		
		table.addColumn(new StringColumn<InstructorGWT>(TableConstants.INSTR_ID, "6em",
				new StaticGetter<InstructorGWT, String>() {
					public String getValueForObject(InstructorGWT object) { return object.getUserID(); }
				},
				new StaticSetter<InstructorGWT, String>() {
					public void setValueInObject(InstructorGWT object, String newValue) { object.setUserID(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER, 
				new StaticValidator<InstructorGWT, String>() {
					public void validate(InstructorGWT object, String newId) throws InvalidValueException {
						if (userIdExists(newId))
							throw new InvalidValueException("There is already a user with ID: " + newId);
					}
				}));

		table.addColumn(new IntColumn<InstructorGWT>(TableConstants.INSTR_MAX_WTU, "4em",
				new StaticGetter<InstructorGWT, Integer>() {
					public Integer getValueForObject(InstructorGWT object) { return object.getMaxWtu(); }
				}, new StaticSetter<InstructorGWT, Integer>() {
					public void setValueInObject(InstructorGWT object, Integer newValue) { object.setMaxWtu(newValue); }
				}, 
				new StaticValidator<InstructorGWT, Integer>() {
					public void validate(InstructorGWT object, Integer newWtu) throws InvalidValueException {
						if (newWtu < 0)
							throw new InvalidValueException(TableConstants.INSTR_MAX_WTU + " must be positive: " + newWtu + " is invalid.");
					}
				}));
		
		table.addColumn(new StringColumn<InstructorGWT>(TableConstants.INSTR_BUILDING, "6em",
				new StaticGetter<InstructorGWT, String>() {
					public String getValueForObject(InstructorGWT object) { return object.getBuilding(); }
				},
				new StaticSetter<InstructorGWT, String>() {
					public void setValueInObject(InstructorGWT object, String newValue) { object.setBuilding(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER, null));
		
		table.addColumn(new StringColumn<InstructorGWT>(TableConstants.INSTR_ROOMNUMBER, "6em",
				new StaticGetter<InstructorGWT, String>() {
					public String getValueForObject(InstructorGWT object) { return object.getRoomNumber(); }
				},
				new StaticSetter<InstructorGWT, String>() {
					public void setValueInObject(InstructorGWT object, String newValue) { object.setRoomNumber(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER, null));
		
		table.addColumn(new CheckboxColumn<InstructorGWT>(TableConstants.INSTR_DISABILITIES, "4em",
				new StaticGetter<InstructorGWT, Boolean>() {
					public Boolean getValueForObject(InstructorGWT object) { return object.getDisabilities(); }
				},
				new StaticSetter<InstructorGWT, Boolean>() {
					public void setValueInObject(InstructorGWT object, Boolean newValue) { object.setDisabilities(newValue); }
				}));
		
		table.addColumn(new ButtonColumn<InstructorGWT>(TableConstants.INSTR_PREFERENCES, "4em",
				new ClickCallback<InstructorGWT>(){
					public void buttonClickedForObject(InstructorGWT object, Button button) {
						if (myFrame.canPopViewsAboveMe()) {
							myFrame.popFramesAboveMe();
							myFrame.frameViewAndPushAboveMe(new InstructorPreferencesView(service, scheduleName, object));
						}	
					}

					public String initialLabel(InstructorGWT object) {
						return TableConstants.INSTR_PREFERENCES;
					}
		}));
		
		table.addDeleteColumn();
		
		this.add(table);
		
		service.getInstructors(new AsyncCallback<List<InstructorGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get instructors: " + caught.toString());
			}
			
			public void onSuccess(List<InstructorGWT> result){
				assert(result != null);
				popup.hide();
				for (InstructorGWT instr : result)
					nextLocationID = Math.max(nextLocationID, instr.getId() + 1);
				table.addRows(result);
			}
		});
	}
	
	
	private void save() {
		service.saveInstructors(
				table.getAddedObjects(),
				table.getEditedObjects(),
				table.getRemovedObjects(),
				new AsyncCallback<List<InstructorGWT>>() {
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void onSuccess(List<InstructorGWT> result) {
						table.clear();
						table.addRows(result);
					}
				});
	}
	
	private boolean userIdExists(String userId) {
		for (InstructorGWT instr : table.getAddedUntouchedAndEditedObjects())
			if (instr.getUserID().equals(userId))
				return true;
		return false;
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
