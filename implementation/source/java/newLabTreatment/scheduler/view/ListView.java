package scheduler.view;

import scheduler.db.Time;
import scheduler.db.coursedb.*;
import scheduler.db.instructordb.Instructor;
import scheduler.generate.Schedule;
import scheduler.generate.ScheduleItem;
import scheduler.view.view_ui.ListViewRow;
import scheduler.view.view_ui.ListViewUI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;


class MyDefaultCellRenderer extends DefaultTableCellRenderer {
   public MyDefaultCellRenderer() {
      super();
      setHorizontalAlignment((SwingConstants.CENTER));
   }
}

/****
 * The List View model class. This class filters the data from
 * ViewSettings and passes it to its companion view class ListViewUI.
 * This class contains a table model that serves as an adaptor between
 * a schedule and a table object that is displayed.
 *
 * @author Jason Mak
 */

public class ListView {
   public static int lastColAdded = -1;
   /**
    * Construct this class using its parent view object. Get settings
    * from the view object and instantiate the table model.
    *
    *                                                                 <pre>
    * pre: ;
    *
    * post: this.view' == view && this.schedule' == schedule
    *        && this.filterOptions' == view.getViewSettings().getFilterOptions()
    *        && this.tableModel' != null && this.listViewUI' != null;
    *
    *                                                                </pre>
    * @param view the scheduler's view object
    * @param schedule the schedule to be viewed
    * @throws scheduler.db.instructordb.Instructor.NullUserIDException check instructor validity
    */
   public ListView(View view, Schedule schedule) throws Instructor.NullUserIDException {
      this.schedule = schedule;
      this.view = view;
      filterOptions = view.getViewSettings().getFilterOptions();
      makeNewTabelModel();
      final MyDefaultCellRenderer cellRenderer = new MyDefaultCellRenderer();
      table = new JTable(tableModel) {
         public TableCellRenderer getCellRenderer(int row, int column) {
            return cellRenderer;
         }

         public Component prepareRenderer(TableCellRenderer renderer,
                                          int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
            if (ListView.lastColAdded != -1 && ListView.lastColAdded == vColIndex) {
               c.setBackground(Color.CYAN);
            } else if (c.getBackground() == Color.CYAN) {
               // If not shaded, match the table's background
               c.setBackground(getBackground());
            }
            return c;
         }
      };

      //table.setAutoCreateRowSorter(true);
      createRows();
      createColumns();
      defaultRowSort();
      updateRows();
      updateColumns();

      listViewUI = new ListViewUI(this);
   }

   protected void makeNewTabelModel() {
      tableModel = new DefaultTableModel(FilterOptions.filterNames, 0) {
         public boolean isCellEditable(int rowIndex, int vColIndex) {
            return (vColIndex == 13 || vColIndex == 14 || vColIndex == 18 || vColIndex == 19);
         }

         public Class getColumnClass(int columnIndex) {
            Object o = getValueAt(0, columnIndex);

            if (o == null) {
               return Object.class;
            } else {
               return o.getClass();
            }
         }
      };
   }

   /**
    * Creates rows for the Table Model using a Schedule's ScheduleItems.
    *
    *                                                           <pre>
    * pre:;
    *
    * post: this.rows' != null &&
    *       forall (ScheduleItem s | s in schedule)
    *             exists (ListViewRow lvr)
    *                    (lvr in rows) && (lvr.schedulteItem == s)
    *                                                              </pre>
    *
    * @throws scheduler.db.instructordb.Instructor.NullUserIDException check instructor validity
    */
   protected void createRows() throws Instructor.NullUserIDException {
      rows = new ArrayList<ListViewRow>();
      String hasTheseEquip, hasDisability, hasPairedLab, ADACompliant;
      RequiredEquipment equipment;

      for (ScheduleItem aScheduleItem : schedule.s) {
         hasPairedLab = aScheduleItem.c.getLabPairing() == null ? "no" : "yes";
         equipment = aScheduleItem.c.getRequiredEquipment();
         hasTheseEquip = "";
         if (equipment.hasLaptopConnectivity())
            hasTheseEquip += "PC-connect ";
         if (equipment.hasOverhead())
            hasTheseEquip += "Overhead ";
         if (equipment.isSmartroom())
            hasTheseEquip += "Smartoom";
         hasDisability = aScheduleItem.i.getDisability() ? "yes" : "no";
         ADACompliant = aScheduleItem.l.isADACompliant() ? "yes" : "no";

         try {
            rows.add(
             new ListViewRow (aScheduleItem,
              new Object[]{aScheduleItem.c.getCourseName(),
               aScheduleItem.c.toString(),
               aScheduleItem.section, aScheduleItem.c.getWTU(),
               aScheduleItem.c.getCourseType(),
               aScheduleItem.c.getMaxEnrollment(), hasPairedLab, hasTheseEquip,
               aScheduleItem.i.getName(), aScheduleItem.i.getId(),
               aScheduleItem.i.getOffice(), aScheduleItem.i.getMaxWTU(),
               hasDisability, new Integer(aScheduleItem.l.getBuilding()),
               new Integer(aScheduleItem.l.getRoom()), aScheduleItem.l.getMaxOccupancy(),
               aScheduleItem.l.getType(), ADACompliant,
               aScheduleItem.start, aScheduleItem.end,
               aScheduleItem.days} )
            );
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * Saves the column objects from the table
    *                                                                 <pre>
    * pre: ;
    *
    * post: this.columns' != null &&
    *       forall (TableColumn tc | table)
    *       tc in columns;
    *                                                                 </pre>
    */
   protected void createColumns() {
      columns = new TableColumn[21];
      for (int k = 0; k < 21; k++) {
         columns[k] = table.getColumn(FilterOptions.filterNames[k]);
      }
   }

   /**
    * Sets the default column to sort by. This depends on the
    * active view type and view level. The defaults for sort order
    * in list mode are noted in the specification requirements.
    *                                                                    <pre>
    * pre: ;
    *
    * post: if (view.getViewSettings().getViewType() == ViewType.COURSE)
    *       then
    *           if (view.getViewSettings().getViewLevel().getLevel() ==
    *               ViewLevel.Level.WEEKLY)
    *           then for(int x = 0; x < table.getRowCount()-1; x++)
    *                    table.getValueAt(x, 20) <=  table.getValueAt(x + 1, 20)
    *           else for(int x = 0; x < table.getRowCount()-1; x++)
    *                    table.getValueAt(x, 1) <=  table.getValueAt(x + 1, 1)
    *        else if (view.getViewSettings().getViewType() == ViewType.INSTRUCTOR)
    *        then for(int x = 0; x < table.getRowCount()-1; x++)
    *                table.getValueAt(x, 8) <=  table.getValueAt(x + 1, 8)
    *        else
    *           if (view.getViewSettings().getViewLevel().getLevel() ==
    *               ViewLevel.Level.WEEKLY)
    *           then for(int x = 0; x < table.getRowCount()-1; x++)
    *                    table.getValueAt(x, 20) <=  table.getValueAt(x + 1, 20)
    *           else  for(int x = 0; x < table.getRowCount()-1; x++)
    *                    table.getValueAt(x, 13) <=  table.getValueAt(x + 1, 13);
    *
    *                                                                    </pre>
    */
   protected void defaultRowSort() {
      if (table.getColumnName(0).equals("Course Number")) {
         table.getRowSorter().toggleSortOrder(0);
      }

      /*if (view.getViewSettings().getViewType() == ViewType.COURSE) {
  if (view.getViewSettings().getViewLevel().getLevel() == ViewLevel.Level.WEEKLY) {
      table.getRowSorter().toggleSortOrder(2);
      table.getRowSorter().toggleSortOrder(1);
      table.getRowSorter().toggleSortOrder(20);
  } else {
      table.getRowSorter().toggleSortOrder(18);
      table.getRowSorter().toggleSortOrder(2);
      table.getRowSorter().toggleSortOrder(1);
  }
} else if (view.getViewSettings().getViewType() == ViewType.INSTRUCTOR) {
  table.getRowSorter().toggleSortOrder(18);
  if (view.getViewSettings().getViewLevel().getLevel() == ViewLevel.Level.WEEKLY) {
      table.getRowSorter().toggleSortOrder(20);
  }
  table.getRowSorter().toggleSortOrder(8);
} else {
  table.getRowSorter().toggleSortOrder(18);
  table.getRowSorter().toggleSortOrder(14);
  table.getRowSorter().toggleSortOrder(13);
  if (view.getViewSettings().getViewLevel().getLevel() == ViewLevel.Level.WEEKLY) {
      table.getRowSorter().toggleSortOrder(20);
  }
}        */
   }

   /**
    * Takes a list view and check if it has been filtered out
    * based on its course, instructor, or location.
    *
    *                                                                  <pre>
    * pre: ;
    * post: if (view.getViewSettings().getViewType() == ViewType.COURSE)
    *       then if (exists (CourseFilterObj x)
    *                     (x in courseFil ) &&
    *                     (x.getCourse.toString()
    *                       == row.scheduleItem.c.toString()) &&
    *                     (x.isSelected())
    *                 then return true
    *                 else return false
    *       else if (view.getViewSettings().getViewType() == ViewType.INSTRUCTOR)
    *            then if (exists (InstructorFilterObj x)
    *                     (x in instrFil ) &&
    *                     (x.getInstructor().getId()
    *                       == row.scheduleItem.getInstructor().getId()) &&
    *                     (x.isSelected())
    *                 then return true
    *                 else return false
    *       else if (exists (LocationFilterObj x)
    *                (x in locationFil) &&
    *                (x.getLocation().getBuilding() == row.rowData[13]) &&
    *                (x.getLocation().getRoom() == row.rowData[14]) &&
    *                (x.isSelected())
    *            then return true
    *            else return false;
    *                                                                     </pre>
    * @param row check if this row is filtered
    * @return true if visible, false otherwise
    */
   protected boolean unFiltered(ListViewRow row) {
      if (view.getViewSettings().getViewType() == ViewType.COURSE) {
         ArrayList<CourseFilterObj> courseFil = view.getViewCourseFilter().getCourseFilterList();

         for (CourseFilterObj aCourseFilterObj : courseFil) {
            if (row.scheduleItem.c.toString().equals(aCourseFilterObj.getCourse().toString())) {
               return aCourseFilterObj.isSelected();
            }
         }
      } else if (view.getViewSettings().getViewType() == ViewType.INSTRUCTOR) {
         ArrayList<InstructorFilterObj> instrFil =
          view.getViewInstructorFilter().getInstructorFilterList();

         for (InstructorFilterObj anInstructorFilterObj : instrFil) {
            if (row.rowData[9].equals(anInstructorFilterObj.getInstructor().getId())) {
               return anInstructorFilterObj.isSelected();
            }
         }
      } else {
         ArrayList<LocationFilterObj> locationFil =
          view.getViewLocationFilter().getLocationFilterList();

         for (LocationFilterObj aLocationFilterObj : locationFil) {
            if (row.rowData[13].equals(aLocationFilterObj.getLocation().getBuilding())
             && row.rowData[14].equals(aLocationFilterObj.getLocation().getRoom())) {
               return aLocationFilterObj.isSelected();
            }
         }
      }

      return false;
   }

   /**
    * Initializes table rows by adding all unfiltered rows
    * to the table. The rows must not be filtered out by advanced filters
    * or the view level.
    *                                                                  <pre>
    * pre: ;
    *
    * post: forall (ListViewRow r | r in rows &&
    *               unFiltered(r) == true &&
    *               Time.isWithin(r.start, r.end, advancedFilters.start,
    *                             advancedFilters.end) &&
    *               ((weekly && r.days.semiEquals(advancedFilters.days)) ||
    *                (daily && r.day == daily.day)))
    *               r.rowData in table;
    *                                                                   </pre>
    *
    *
    */
   protected void updateRows() {
      for (ListViewRow aRow : rows) {
         if (unFiltered(aRow)){
            if (Time.isWithin(aRow.scheduleItem.start, aRow.scheduleItem.end,
             view.getAdvancedFilter().getStartTime(), view.getAdvancedFilter().getEndTime())) {
               try {
                  if (view.getViewSettings().getViewLevel().getLevel() == ViewLevel.Level.DAILY) {
                     if (aRow.scheduleItem.days.contains(view.getViewSettings().getViewLevel().getDay())) {
                        tableModel.addRow(aRow.rowData);
                     }
                  }
                  else {
                     //    if (view.getAdvancedFilter().getDays().semiEquals(aRow.scheduleItem.days)) {
                     tableModel.addRow(aRow.rowData);
                     //    }
                  }
               } catch(Exception e) {
                  e.printStackTrace();
               }
            }
         }
      }
   }

   /**
    *    All columns have been created. This method filters out the ones
    *    that have been selected to be hidden. It also turns auto-column
    *    resize off if the column count has exceeded 11.
    *                                                         <pre>
    *    pre:;
    *
    *    post: forall (TableColumn c | c in columns && !filterOptionsArray[c.index])
    *              !(c in table)
    *
    *          &&
    *
    *          if (table.getColumnCount() >= 12)
    *          then table.autoResizeMode == JTable.AUTO_RESIZE_OFF
    *          else table.autoResizeMode == JTable.AUTO_RESIZE_LAST_COLUMN;
    *
    *                                                         </pre>
    *
    *
    */
   protected void updateColumns() {
      boolean[] filterOptionsArray = filterOptions.toArray();

      for (int k = 0; k < columns.length; k++) {
         if (!filterOptionsArray[k]) {
            table.removeColumn(columns[k]);
         }
      }

      if (table.getColumnCount() >= 12) {
         table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      }
      lastColAdded = -1;
   }

   /**
    * Removes the column if it has been deselected in the filter options pane
    * and turns on auto column resize if column is below 12,
    * otherwise add the column and turns auto-column
    * resize off if the column count has exceeded 11.
    *
    *                                                              <pre>
    * pre: ;
    *
    * post: if (visibile == true)
    *       then columns[col] in table
    *       else !(columns[col] in table)
    *
    *       &&
    *
    *       if (table.getColumnCount() >= 12)
    *       then table.autoResizeMode == JTable.AUTO_RESIZE_OFF
    *       else table.autoResizeMode == JTable.AUTO_RESIZE_LAST_COLUMN
    *
    *                                                              </pre>
    * @param col the column index to be added or removed
    * @param visible boolean to determine whether column is added or removed
    */
   public void updateColumn(int col, boolean visible) {
      int colPos = 0;
      if (visible) {
         table.addColumn(columns[col]);
         for (int k = 0; k < 21; k++) {
            if (filterOptions.toArray()[k]) {
               if (col <= k) {
                  break;
               }
               colPos++;
            }
         }
         table.moveColumn(table.getColumnCount() - 1, colPos);
         if (table.getColumnCount() == 7) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
         }
         lastColAdded = colPos;
      } else {
         table.removeColumn(columns[col]);
         if (table.getColumnCount() == 6) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
         }
         lastColAdded = -1;
      }
   }

   /**
    * updateCourseFilter finds all rows that have a course
    * matching the course in the CourseFilterObj. If the boolean value in
    * CourseFilterObj is true, the method adds the rows to the table,
    * otherwise they are already in the table and are removed.
    *
    *                                                               <pre>
    *
    * pre: ;
    *
    * post: forall (ListViewRow r : r in rows && cfo.course == row.scheduleItem.course)
    *       if (cfo.isSelected())
    *        then r.rowData in table
    *        else !(r.rowData in table;
    *
    *                                                               </pre>
    *
    * @param cfo  course filter object to determine if course is hidden
    */
   public void updateCourseFilter(CourseFilterObj cfo) {
      if (cfo.isSelected()) {
         for (ListViewRow aListViewRow : rows) {
            if (aListViewRow.scheduleItem.c.toString().equals(cfo.getCourse().toString())) {
               tableModel.addRow(aListViewRow.rowData);
            }
         }
      } else {
         int toRemove = 0;
         while (toRemove < tableModel.getRowCount()) {
            if ((tableModel.getValueAt(toRemove,1)).
             equals(cfo.getCourse().toString())) {
               tableModel.removeRow(toRemove);
            } else {
               toRemove++;
            }
         }
      }
   }

   /**
    * updateInstructorFilter finds all rows that have a course
    * matching the instructor in the InstructorFilterObj. If the boolean value in
    * InstructorFilterObj is true, the method adds the rows to the table,
    * otherwise they are already in the table and are removed.
    *                                                               <pre>
    *
    * pre: ;
    *
    * post: forall (ListViewRow r : r in rows && ifo.instructor == row.scheduleItem.instructor)
    *       if (ifo.isSelected())
    *        then r.rowData in table
    *        else !(r.rowData in table;
    *
    *                                                               </pre>
    *
    * @param ifo determine if instructor is hidden
    */
   public void updateInstructorFilter(InstructorFilterObj ifo) {
      if (ifo.isSelected()) {
         for (ListViewRow aListViewRow : rows) {
            if (aListViewRow.scheduleItem.i.getId().equals(ifo.getInstructor().getId())) {
               tableModel.addRow(aListViewRow.rowData);
            }
         }
      } else {
         int toRemove = 0;
         while (toRemove < tableModel.getRowCount()) {
            if ((tableModel.getValueAt(toRemove,9)).
             equals(ifo.getInstructor().getId())) {
               tableModel.removeRow(toRemove);
            } else {
               toRemove++;
            }
         }
      }
   }

   /**
    * updateCourseFilter finds all rows that have a location
    * matching the location in the LocationFilterObj. If the boolean value in
    * LocationFilterObj is true, the method adds the rows to the table,
    * otherwise they are already in the table and are removed.
    *
    *                                                               <pre>
    *
    * pre: ;
    *
    * post: forall (ListViewRow r : r in rows && lfo.location == row.scheduleItem.location)
    *       if (lfo.isSelected())
    *        then r.rowData in table
    *        else !(r.rowData in table;
    *
    *                                                               </pre>
    *
    * @param lfo determine if location is hidden
    */
   public void updateLocationFilter(LocationFilterObj lfo) {
      if (lfo.isSelected()) {
         for (ListViewRow aListViewRow : rows) {
            if (aListViewRow.scheduleItem.l.toString().equals(lfo.getLocation().toString())) {
               tableModel.addRow(aListViewRow.rowData);
            }
         }
      } else {
         int toRemove = 0;
         while (toRemove < tableModel.getRowCount()) {
            if ((tableModel.getValueAt(toRemove, 13)).
             equals(lfo.getLocation().getBuilding()) &&
             (tableModel.getValueAt(toRemove, 14)).
              equals(lfo.getLocation().getRoom())) {
               tableModel.removeRow(toRemove);
            } else {
               toRemove++;
            }
         }
      }
   }

   /**
    * Redraws the entire JTable.
    *                                                               <pre>
    * pre:;
    *
    * post: this.tableModel' != null;
    *                                                               </pre>
    */
   public void update() {
      makeNewTabelModel();
      table.setModel(tableModel);
      createColumns();
      updateColumns();
      updateRows();
   }

   /**
    * Returns the table of data.
    *                                                               <pre>
    * post: return == table;
    *                                                               </pre>
    *
    * @return the table of the list view
    */
   public JTable getTable() {
      return table;
   }

   /**
    * Returns the visible row with the given course name and section.
    *
    *                                                               <pre>
    *
    * pre: ;
    *
    * post: if exists (ListViewRow r)
    *       (r in rows) && (r.scheduleItem.c.courseName == courseName)
    *       && (r.scheduleItem.c.section == section)
    *       then return == r.scheduleItem
    *       else return == null
    *
    *                                                               </pre>
    *
    * @param row row to get the data from
    * @return the listViewRow with data of the parameter row
    */
   public ListViewRow getDataRow(int row) {
      String courseNo = (String) (tableModel.getValueAt(row, 1));
      int section = (Integer) (tableModel.getValueAt(row, 2));

      for (ListViewRow aListViewRow : rows) {
         if (aListViewRow.scheduleItem.c.toString().equals(courseNo)
          && aListViewRow.scheduleItem.section == section) {
            return aListViewRow;
         }
      }
      return null;
   }

   /***
    * Returns the companion View object.
    *                                                               <pre>
    * post: return == listViewUI;
    *                                                               </pre>
    *
    * @return the companion view
    */
   public ListViewUI getListViewUI() {
      return listViewUI;
   }

   /** The parent view. */
   public View view;

   /** The TableModel of viewable data. */
   protected DefaultTableModel tableModel;

   /** The Schedule to be drawn. */
   protected Schedule schedule;

   /** The columns of the table. */
   protected TableColumn[] columns;

   /** The rows of the table. */
   protected ArrayList<ListViewRow> rows;

   /** The JTable to be displayed. */
   protected JTable table;

   /** FilterOptions for the view. */
   protected FilterOptions filterOptions;

   /** The companion view object. */
   protected ListViewUI listViewUI;
}


