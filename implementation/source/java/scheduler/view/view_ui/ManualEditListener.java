package scheduler.view.view_ui;

import scheduler.db.Time;
import scheduler.db.locationdb.Location;
import scheduler.generate.ScheduleItem;
import scheduler.view.ListView;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableModel;

/****
 * 
 * Class ManualEditListener overrides the default CellEditorListener 
 * provided by JTable. At the moment, its main function is editingStopped
 * which allows us to do custom data validation on a cell once the user
 * has signaled the end of a cell edit.
 *
 * @author Jason Mak (jamak3@gmail.com)
 */
public class ManualEditListener implements CellEditorListener {

   /**
    * Construct this class using its parent ListView object and corresponding Jtable. 
    *
    *                                                                 <pre>
    * pre: ;
    *
    * post: this.listView' == listView && this.table' == table;
    *
    *                                                                </pre>
    * @param listView the scheduler's ListView object
    * @param table the ListView object's JTable
    */
    public ManualEditListener(ListView listView, JTable table) {
        this.listView = listView;
        this.table = table; 
    }

   /**
    * Editing of the current cell has stopped. Perform data validation. If
    * the start time and end time are not logically correct in relation to each
    * other, check if this was the second automatically initiated edit. If 
    * it was, output an error and don't accept the input. If it wasn't,
    * initiate the second edit to give the user a chance to correct the time. 
    *                                                                    <pre>
    * pre: table.isEditing == false;
    *
    * post: tableModel.getValueAt(rowEdited, colEdited) != null &&
    *           if (secondEdit) 
    *           then dataRow.scheduleItem.start < dataRow.scheduleItem.end
    *           else secondEdit == true; 
    *                                                                    </pre>
    * @param e the event to notify a stopped edit
    */
    public void editingStopped(ChangeEvent e) {
        tableModel = table.getModel();
	
	// When implemented, this would cancel the edit and restore the cell's contents        
        if(table.getValueAt(rowEdited, colEdited) == null) {
            System.out.println("This would set the blank cell back" +
             " to what it was but I dont feel like doing it.");
            return;
        }
      
        // if the cell contained the start time
        if (table.getColumnName(colEdited).equals("Start")) {
            Time start = (Time) tableModel.getValueAt(rowEdited, 18);
            Time end = (Time) tableModel.getValueAt(rowEdited, 19); ;

            // if start and end time are not logically correct
            if(start.compareTo(end) >= 0) {
                // if end time is not in the list view
                if (colEdited == table.getColumnCount() - 1 ||
                 !table.getColumnName(colEdited + 1).equals("End")) {
                    JOptionPane.showMessageDialog(null,
                     "Start time must be before end time.",
                     "Error Changing Start Time",
                     JOptionPane.ERROR_MESSAGE);
                     table.editCellAt(rowEdited, colEdited);
                } else {
                    if (secondEdit) {
                        // dont' accept
                        JOptionPane.showMessageDialog(null,
                         "Start time must be before end time.",
                         "Error Changing Start Time",
                         JOptionPane.ERROR_MESSAGE);
                        table.editCellAt(rowEdited, colEdited);
                    } else { // give the user a chance to fix it
                        secondEdit = true;
                        colEdited += 1;
                        table.editCellAt(rowEdited, colEdited);
                    }
                }
            } else { // input was logically correct
                ListViewRow dataRow = listView.getDataRow(rowEdited);
                dataRow.scheduleItem.start = (Time) table.getValueAt(rowEdited, colEdited);
                dataRow.rowData[18] = dataRow.scheduleItem.start;
                if (secondEdit) {
                    dataRow.scheduleItem.end = (Time) table.getValueAt(rowEdited, colEdited + 1);
                    dataRow.rowData[19] = dataRow.scheduleItem.end;
                }
                secondEdit = false;
            }
        } else if (table.getColumnName(colEdited).equals("End")) {
            // if cell edited contained end time
            Time start = (Time) tableModel.getValueAt(rowEdited, 18);            
            Time end = (Time) tableModel.getValueAt(rowEdited, 19);

            // if the start and end time are not logically correct
            if(start.compareTo(end) >= 0) {
                // if the start time is not in the list view
                if (colEdited == 0 ||
                 !table.getColumnName(colEdited - 1).equals("Start")) {
                    JOptionPane.showMessageDialog(null,
                     "Start time must be before end time.",
                     "Error Changing End Time",
                     JOptionPane.ERROR_MESSAGE);
                    table.editCellAt(rowEdited, colEdited);
                } else {
                    if (secondEdit) { 
                        // don't accept
                        JOptionPane.showMessageDialog(null,
                         "Start time must be before end time.",
                         "Error Changing End Time",
                         JOptionPane.ERROR_MESSAGE);

                        table.editCellAt(rowEdited, colEdited);

                    } else { // give the user a chance to fix it
                        secondEdit = true;
                        colEdited -= 1;
                        table.editCellAt(rowEdited, colEdited);
                    }
                }
            } else { // input was logically correct
                ListViewRow dataRow = listView.getDataRow(rowEdited);
                dataRow.scheduleItem.end = (Time) table.getValueAt(rowEdited, colEdited);
                dataRow.rowData[19] = dataRow.scheduleItem.end;
                if (secondEdit) {
                    dataRow.scheduleItem.start = (Time) table.getValueAt(rowEdited, colEdited - 1);
                    dataRow.rowData[18] = dataRow.scheduleItem.start;
                }
                secondEdit = false;
            }
        } else if (table.getColumnName(colEdited).equals("Building")) {
            // editing a building number cell
            ListViewRow dataRow = listView.getDataRow(rowEdited);
            dataRow.scheduleItem.l =
             new Location((Integer) table.getValueAt(rowEdited, colEdited),
              new Integer(dataRow.scheduleItem.l.getRoom()));
            dataRow.rowData[13] = new Integer(dataRow.scheduleItem.l.getBuilding());
        } else if (table.getColumnName(colEdited).equals("Room")) {
            // editing a room number cell
            ListViewRow dataRow = listView.getDataRow(rowEdited);
            dataRow.scheduleItem.l =
             new Location(new Integer(dataRow.scheduleItem.l.getBuilding()),
              (Integer) table.getValueAt(rowEdited, colEdited));
            dataRow.rowData[14] = new Integer(dataRow.scheduleItem.l.getRoom());
        }
    }

   /**
    * Temporary debug method to determine when a user cancels a cell edit.
    *
    *                                                                 <pre>
    * pre: table.isEditing == false;
    *
    * post: ;    
    *                                                                </pre>
    * @param e JTable cell event
    */
    public void editingCanceled(ChangeEvent e) {
        System.out.println("Editing canceled");
    }

   /**
    * This method lets the ManualEditListener know explicitly which
    * cell is about to be edited. 
    *
    *                                                                 <pre>
    * pre: table.isEditing == false;
    *
    * post: this.rowEdited' == row && this.colEdited' == col;    
    *                                                                </pre>
    * @param row the cell's identifying row
    * @param col the cell's identifying column
    */
    public void setEditAt(int row, int col) {
        rowEdited = row;
        colEdited = col;
    }

    /** Variable to hold the row number of the cell being edited. */
    protected int rowEdited = -1;

    /** Variable to hold the column number of the cell being edited. */
    protected int colEdited = -1;

    /** The parent ListView. */
    protected ListView listView;

    /** The data of the table being edited. */
    protected TableModel tableModel;

    /** The table being edited. */
    protected JTable table;

    /** Variable that tracks if the current time edit was automatically initiated. */
    protected boolean secondEdit;
}
