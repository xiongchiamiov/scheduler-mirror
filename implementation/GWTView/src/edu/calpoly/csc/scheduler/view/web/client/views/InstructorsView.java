package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Collection;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
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
import edu.calpoly.csc.scheduler.view.web.client.table.Table;
import edu.calpoly.csc.scheduler.view.web.client.table.TableConstants;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorsView extends VerticalPanel {
	// These static variables are a temporary hack to get around the table bug
	public static GreetingServiceAsync service;
	public static Panel container;
	
	private final String scheduleName;
	private Table<InstructorGWT> iTable;
	private OsmTable<InstructorGWT> table;
	int nextLocationID = 1;

	public InstructorsView(Panel container, GreetingServiceAsync service, String scheduleName) {
		assert(service != null);
		InstructorsView.container = container;
		InstructorsView.service = service;
		this.scheduleName = scheduleName;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();
		this.add(vp);

		vp.add(new HTML("<h2>" + scheduleName + " - Instructors</h2>"));

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
						InstructorsView.container.clear();
				    	InstructorsView.container.add(new InstructorPreferencesView(InstructorsView.container, InstructorsView.service, object));
					}

					public String initialLabel(InstructorGWT object) {
						return TableConstants.INSTR_PREFERENCES;
					}
		}));
		
		vp.add(table);
		
		service.getInstructors2(new AsyncCallback<Collection<InstructorGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get instructors: " + caught.toString());
			}
			
			public void onSuccess(Collection<InstructorGWT> result){
				assert(result != null);
				popup.hide();
				for (InstructorGWT instr : result)
					nextLocationID = Math.max(nextLocationID, instr.getId() + 1);
				table.addRows(result);
			}
		});
	}
	
	
	private void save() {
		service.saveInstructors(table.getAddedUntouchedAndEditedObjects(), new AsyncCallback<Collection<InstructorGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onSuccess(Collection<InstructorGWT> result) {
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
}
