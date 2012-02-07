package edu.calpoly.csc.scheduler.view.web.client.views.resources.instructors;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.client.table.IFactory;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.MemberIntegerComparator;
import edu.calpoly.csc.scheduler.view.web.client.table.MemberStringComparator;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.ObjectChangedObserver;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.ButtonColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.ButtonColumn.ClickCallback;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.DeleteColumn.DeleteObserver;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingCheckboxColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingStringColumn;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorsTable extends SimplePanel {
	private static final String FIRSTNAME_HEADER = "First Name";
	private static final String FIRSTNAME_WIDTH = null;
	
	private static final String LASTNAME_HEADER = "Last Name";
	private static final String LASTNAME_WIDTH = null;

	private static final String USERNAME_HEADER = "Username";
	private static final String USERNAME_WIDTH = null;

	private static final String MAX_WTU_HEADER = "Max WTU";
	private static final String MAX_WTU_WIDTH = null;
	
	private static final String DISABILITIES_HEADER = "Disabilities";
	private static final String DISABILITIES_WIDTH = "4em";
	
	private static final String PREFERENCES_HEADER = "Preferences";
	private static final String PREFERENCES_WIDTH = null;

	public interface Strategy {
		void getAllInstructors(AsyncCallback<List<InstructorGWT>> callback);
		InstructorGWT createInstructor();
		void onInstructorEdited(InstructorGWT Instructor);
		void onInstructorDeleted(InstructorGWT Instructor);
		void preferencesButtonClicked(InstructorGWT instructor);
	}
	
	final OsmTable<InstructorGWT> table;
	final Strategy strategy;
	final ArrayList<InstructorGWT> tableInstructors = new ArrayList<InstructorGWT>();
	
	public InstructorsTable(Strategy strategy_) {
		this.strategy = strategy_;
		
		table = new OsmTable<InstructorGWT>(
				new IFactory<InstructorGWT>() {
					public InstructorGWT create() {
						InstructorGWT newInstructor = strategy.createInstructor();
						tableInstructors.add(newInstructor);
						return newInstructor;
					}
				});
		
		table.setObjectChangedObserver(new ObjectChangedObserver<InstructorGWT>() {
			public void objectChanged(final InstructorGWT object) {
				strategy.onInstructorEdited(object);
			}
		});

		table.addDeleteColumn(new DeleteObserver<InstructorGWT>() {
			@Override
			public void afterDelete(InstructorGWT object) {
				tableInstructors.remove(object);
				strategy.onInstructorDeleted(object);
			}
		});
		
		addFieldColumns();

		this.add(table);
	}

	@Override
	public void onLoad() {
		strategy.getAllInstructors(new AsyncCallback<List<InstructorGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get Instructors: " + caught.toString());
			}
			
			public void onSuccess(List<InstructorGWT> Instructors){
				assert(tableInstructors.isEmpty());
				for (InstructorGWT Instructor : Instructors)
					tableInstructors.add(new InstructorGWT(Instructor));
				
				table.addRows(tableInstructors);
			}
		});
	}
	
	void addFieldColumns() {
		table.addColumn(
				FIRSTNAME_HEADER,
				FIRSTNAME_WIDTH,
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
				LASTNAME_HEADER,
				LASTNAME_WIDTH,
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
		
//		table.addColumn(
//				USERNAME_HEADER,
//				USERNAME_WIDTH,
//				true,
//				new MemberStringComparator<InstructorGWT>(new IStaticGetter<InstructorGWT, String>() {
//					public String getValueForObject(InstructorGWT object) { return object.getUsername(); }
//				}),
//				new EditingStringColumn<InstructorGWT>(
//						new IStaticGetter<InstructorGWT, String>() {
//							public String getValueForObject(InstructorGWT object) { return object.getUsername(); }
//						},
//						new IStaticSetter<InstructorGWT, String>() {
//							public void setValueInObject(InstructorGWT object, String newValue) { object.setUsername(newValue); }
//						}, 
//						new IStaticValidator<InstructorGWT, String>() {
//							public void validate(InstructorGWT object, String newId) throws InvalidValueException {
//								if (!canSetUsername(object, newId))
//									throw new InvalidValueException("There is already a user with ID: " + newId);
//							}
//						}));
//
//		table.addColumn(
//				MAX_WTU_HEADER,
//				MAX_WTU_WIDTH,
//				true,
//				new MemberIntegerComparator<InstructorGWT>(new IStaticGetter<InstructorGWT, Integer>() {
//					public Integer getValueForObject(InstructorGWT object) { return object.getMaxWtu(); }
//				}),
//				new EditingIntColumn<InstructorGWT>(
//						new IStaticGetter<InstructorGWT, Integer>() {
//							public Integer getValueForObject(InstructorGWT object) { return object.getMaxWtu(); }
//						},
//						new IStaticSetter<InstructorGWT, Integer>() {
//							public void setValueInObject(InstructorGWT object, Integer newValue) { object.setMaxWtu(newValue); }
//						}, 
//						new IStaticValidator<InstructorGWT, Integer>() {
//							public void validate(InstructorGWT object, Integer newWtu) throws InvalidValueException {
//								if (newWtu < 0)
//									throw new InvalidValueException(MAX_WTU_HEADER + " must be positive: " + newWtu + " is invalid.");
//							}
//						}));
		
		table.addColumn(
				DISABILITIES_HEADER,
				DISABILITIES_WIDTH,
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
				PREFERENCES_HEADER,
				PREFERENCES_WIDTH,
				true,
				null,
				new ButtonColumn<InstructorGWT>(
						"Preferences",
						new ClickCallback<InstructorGWT>(){
							public void buttonClickedForObject(InstructorGWT object, Button button) {
								strategy.preferencesButtonClicked(object);
							}
						}));

	}

	private boolean canSetUsername(InstructorGWT forObject, String newUsername) {
		for (InstructorGWT instr : table.getObjects()) {
			if (instr == forObject)
				continue;
			if (instr.getUsername().equals(newUsername))
				return false;
		}
		return true;
	}

}
