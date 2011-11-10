package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.WeekGWT;

public class CTableBuilder implements TableBuilder<CourseGWT>{

	private GreetingServiceAsync service;
	
	public CTableBuilder(GreetingServiceAsync service) {
		this.service = service;
	}
	
	@Override
	public ArrayList<ColumnObject<CourseGWT>> getColumns(Widget hidden,
			ListDataProvider<CourseGWT> dataProvider, ListHandler<CourseGWT> sortHandler) {
		
		final Label fhidden = (Label)hidden;
		final ListDataProvider<CourseGWT> fdataProvider = dataProvider;
		
		ArrayList<ColumnObject<CourseGWT>> list = 
				new ArrayList<ColumnObject<CourseGWT>>();
		
		
		// course name		    
		Column<CourseGWT, String> courseName = 
				new Column<CourseGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return course.getCourseName();
		      }
		};
		sortHandler.setComparator(courseName, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getCourseName().compareTo(o2.getCourseName());
	        }
	    });
		courseName.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		        object.setCourseName(value);
		      }
		});
		courseName.setCellStyleNames("tableColumnWidthString");
		list.add(new ColumnObject<CourseGWT>(courseName, TableConstants.COURSE_NAME));
		
		
		// catalog number		    
		Column<CourseGWT, String> catalogNum = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_CATALOG_NUM, true)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getCatalogNum();
		      }
		};
		sortHandler.setComparator(catalogNum, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getCatalogNum() - o2.getCatalogNum();
	        }
	    });
		catalogNum.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setCatalogNum(TableValidate.positiveInt(value, true));
		      }
		});
		catalogNum.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(catalogNum, TableConstants.COURSE_CATALOG_NUM));
		
		
		// dept		    
		Column<CourseGWT, String> dept = 
				new Column<CourseGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return course.getDept();
		      }
		};
		sortHandler.setComparator(dept, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getDept().compareTo(o2.getDept());
	        }
	    });
		dept.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		        object.setDept(value);
		      }
		});
		dept.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(dept, TableConstants.COURSE_DEPARTMENT));
		
		
		// wtu		    
		Column<CourseGWT, String> wtu = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_WTU, false)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getWtu();
		      }
		};
		sortHandler.setComparator(wtu, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getWtu() - o2.getWtu();
	        }
	    });
		wtu.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setWtu(TableValidate.positiveInt(value, false));
		      }
		});
		wtu.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(wtu, TableConstants.COURSE_WTU));
		
		
		// stu		    
		Column<CourseGWT, String> stu = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_SCU, false)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getScu();
		      }
		};
		sortHandler.setComparator(stu, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getScu() - o2.getScu();
	        }
	    });
		stu.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setScu(TableValidate.positiveInt(value, false));
		      }
		});
		stu.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(stu, TableConstants.COURSE_SCU));
		
		
		// # sections		    
		Column<CourseGWT, String> numSections = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_NUM_SECTIONS, false)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getNumSections();
		      }
		};
		sortHandler.setComparator(numSections, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getNumSections() - o2.getNumSections();
	        }
	    });
		numSections.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setNumSections(TableValidate.positiveInt(value, false));
		      }
		});
		numSections.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(numSections, TableConstants.COURSE_NUM_SECTIONS));
		
		
		// course type
		ArrayList<String> typeOptions = new ArrayList<String>();
		typeOptions.add(TableConstants.LEC);
		typeOptions.add(TableConstants.LAB);
		SelectionCell selectionCellType = new SelectionCell(typeOptions);
		Column<CourseGWT, String> type = 
				new Column<CourseGWT, String>(selectionCellType) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return course.getType();
		      }
		};
		sortHandler.setComparator(type, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getType().compareTo(o2.getType());
	        }
	    });
		type.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		        object.setType(value);
		      }
		});
		type.setCellStyleNames("tableColumnWidthString");
		list.add(new ColumnObject<CourseGWT>(type, TableConstants.COURSE_TYPE));

		
		// max enroll		    
		Column<CourseGWT, String> maxEnroll = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_MAX_ENROLLMENT, false)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getMaxEnroll();
		      }
		};
		sortHandler.setComparator(maxEnroll, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getMaxEnroll() - o2.getMaxEnroll();
	        }
	    });
		maxEnroll.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setMaxEnroll(TableValidate.positiveInt(value, false));
		      }
		});
		maxEnroll.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(maxEnroll, TableConstants.COURSE_MAX_ENROLLMENT));

		
		// max enroll		    
		Column<CourseGWT, String> length = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_MAX_ENROLLMENT, false)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getLength() / 2.0;
		      }
		};
		sortHandler.setComparator(length, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getLength() - o2.getLength();
	        }
	    });
		length.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setLength((int)Math.round(TableValidate.positiveMultipleOfHalf(value, 1.0) * 2));
		      }
		});
		length.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(length, TableConstants.COURSE_LENGTH));
		
		
		// lab
		Column<CourseGWT, String> lab = 
				new Column<CourseGWT, String>(new ButtonCell()){ 
		
				@Override
			      public String getValue(CourseGWT course) {
			    	  if(course.getLabDept().trim().equals("")){
			    		  return "";
			    	  }
			    	  return course.getLabDept().trim() + course.getLabCatalogNum();
			      
				}
		};
		sortHandler.setComparator(lab, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	        	if(!o1.getLabDept().equals(o2.getLabDept())){
	        		return o1.getLabDept().compareTo(o2.getLabDept());
	        	}
	        	return o1.getLabCatalogNum() - o2.getLabCatalogNum();
	        }
	    });
		lab.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  /*
		    	  value = value.trim();
		    	  if(value.equals("")){
	  					object.setLabDept("");
	  					object.setLabName("");
	  					object.setLabCatalogNum(0);
	  				}
	  				else{
	  					
	  					// get first integer
	  					int i;
	  					for(i = 0; !Character.isDigit(value.charAt(i)) && i < value.length(); i++){}
	  					try{
	  						int cnum = Integer.parseInt(value.substring(i));
	  						object.setLabDept(value.substring(0, i));
		  					object.setLabName("");
		  					object.setLabCatalogNum(cnum);
	  						
	  						
	  					}catch(Exception e){
	  						object.setLabDept("");
		  					object.setLabName("");
		  					object.setLabCatalogNum(0);
	  					}
	  				}
		    	  
		    	  */
		    	  
		    	  
		    	  final PopupPanel popup = new PopupPanel(true);
		    	  final CourseGWT fobject = object;
		    	// get lab options
		    	ArrayList<String> labOptions = new ArrayList<String>();
		    	labOptions.add("");
		  		for(CourseGWT c : fdataProvider.getList()){
		  			//
		  			if(c.getType().equals(TableConstants.LAB) && 
		  					!c.getDept().trim().equals("")){
		  				labOptions.add(c.getDept().trim() + c.getCatalogNum());
		  			}
		  			//
		  			labOptions.add(c.getDept().trim() + c.getCatalogNum());
		  		}
		  		
		  		Collections.sort(labOptions);
		  		
		  		final ListBox listbox = new ListBox();
		  		listbox.addChangeHandler(new ChangeHandler(){
		  			public void onChange(ChangeEvent event){
		  				String value = listbox.getValue(listbox.getSelectedIndex());
		  				if(value.equals("")){
		  					fobject.setLabDept("");
		  					fobject.setLabName("");
		  					fobject.setLabCatalogNum(0);
		  				}
		  				else{
		  					
		  					// get first integer
		  					int i;
		  					for(i = 0; !Character.isDigit(value.charAt(i)) && i < value.length(); i++){}
		  					try{
		  						int cnum = Integer.parseInt(value.substring(i));
		  						fobject.setLabDept(value.substring(0, i));
			  					fobject.setLabName("");
			  					fobject.setLabCatalogNum(cnum);
		  						
		  						
		  					}catch(Exception e){
		  						fobject.setLabDept("");
			  					fobject.setLabName("");
			  					fobject.setLabCatalogNum(0);
		  					}
		  				}
		  				popup.hide();
		  			}
		  		});
		  		
		  		for(int i = 0; i < labOptions.size(); i++){
		  			String s = labOptions.get(i);
		  			listbox.addItem(s);
		  			if((object.getLabDept().trim() + object.getLabCatalogNum()).equals(s)){
		  				listbox.setSelectedIndex(i);
		  			}
		  		}
		    	
		  		popup.setWidget(listbox);
		  		popup.showRelativeTo(fhidden);
		      }
		});
		lab.setCellStyleNames("tableColumnWidthString");
		list.add(new ColumnObject<CourseGWT>(lab, TableConstants.COURSE_LAB));
		
		
		return list;
	}

	@Override
	public String getLabel(CourseGWT object) {
		return object.getDept() + " " + object.getCatalogNum() + ": " + object.getCourseName();
	}

	@Override
	public CourseGWT newObject() {
		CourseGWT course = new CourseGWT();
		course.setCatalogNum(0);
		course.setLabId(0);
		course.setLength(0);
		course.setDept("");
		
		WeekGWT week = new WeekGWT();
		week.setDays(new Vector<DayGWT>());
		course.setDays(week);
		
		course.setScheduleID(null);
		course.setMaxEnroll(1);
		course.setCourseName("");
		course.setNumSections(1);
		course.setScu(1);
		course.setWtu(1);
		course.setType("");
		course.setLabCatalogNum(0);
		course.setLabName("");
		course.setLabDept("");
		return course;
	}

	@Override
	public void save(ArrayList<CourseGWT> list) {
		for (CourseGWT course : list)
			course.verify();
		
		service.saveCourses(list, new AsyncCallback<Void>(){
			public void onFailure(Throwable caught){ 
				
				Window.alert("Error saving:\n" + 
						caught.getLocalizedMessage());
			}
			public void onSuccess(Void result){
				Window.alert("Successfully saved");
			}
		});
	}
	
	
	/**
	 * Panel for creating a new object
	 * @author David Seltzer
	 *
	 */
	public class NewIPanel implements NewObjPanel<CourseGWT>{

		private Grid grid;
		
		public NewIPanel(){
			grid = new Grid(2, 2);
		}
		
		@Override
		public Grid getGrid() {
			return grid;
		}

		@Override
		public CourseGWT getObject(ListDataProvider<CourseGWT> dataProvider) {
			// TODO Auto-generated method stub
			return new CourseGWT();
		}

		@Override
		public String getError() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void focus() {
			// TODO Auto-generated method stub
			
		}
	}


	@Override
	public NewObjPanel<CourseGWT> newObjPanel() {
		return new NewIPanel();
	}
}
