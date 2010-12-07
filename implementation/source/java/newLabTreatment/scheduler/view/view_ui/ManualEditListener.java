package scheduler.view.view_ui;

import scheduler.db.Time;
import scheduler.db.locationdb.Location;
import scheduler.generate.ScheduleItem;
import scheduler.view.ListView;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableModel;

/**
 * *
 * Class
 *
 * @author Jason Mak (jamak3@gmail.com)
 */
public class ManualEditListener implements CellEditorListener {

    public ManualEditListener(ListView listView, JTable table) {
        this.listView = listView;
        this.table = table;
        secondEdit = false;
    }

    public void editingStopped(ChangeEvent e) {
        tableModel = table.getModel();
        
        if(table.getValueAt(rowEdited, colEdited) == null) {
            System.out.println("This would set the blank cell back" +
             " to what it was but I dont feel like doing it.");
            return;
        }
      
        if (table.getColumnName(colEdited).equals("Start")) {
            Time start = (Time) tableModel.getValueAt(rowEdited, 18);
            Time end = (Time) tableModel.getValueAt(rowEdited, 19); ;

            if(start.compareTo(end) >= 0) {
                if (colEdited == table.getColumnCount() - 1 ||
                 !table.getColumnName(colEdited + 1).equals("End")) {
                    JOptionPane.showMessageDialog(null,
                     "Start time must be before end time.",
                     "Error Changing Start Time",
                     JOptionPane.ERROR_MESSAGE);
                     table.editCellAt(rowEdited, colEdited);
                } else {
                    if (secondEdit) {
                        JOptionPane.showMessageDialog(null,
                         "Start time must be before end time.",
                         "Error Changing Start Time",
                         JOptionPane.ERROR_MESSAGE);
                        table.editCellAt(rowEdited, colEdited);
                    } else {
                        secondEdit = true;
                        colEdited += 1;
                        table.editCellAt(rowEdited, colEdited);
                    }
                }
            } else {
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
            Time start = (Time) tableModel.getValueAt(rowEdited, 18);            
            Time end = (Time) tableModel.getValueAt(rowEdited, 19);

            if(start.compareTo(end) >= 0) {
                if (colEdited == 0 ||
                 !table.getColumnName(colEdited - 1).equals("Start")) {
                    JOptionPane.showMessageDialog(null,
                     "Start time must be before end time.",
                     "Error Changing End Time",
                     JOptionPane.ERROR_MESSAGE);
                    table.editCellAt(rowEdited, colEdited);
                } else {
                    if (secondEdit) {
                        JOptionPane.showMessageDialog(null,
                         "Start time must be before end time.",
                         "Error Changing End Time",
                         JOptionPane.ERROR_MESSAGE);

                        table.editCellAt(rowEdited, colEdited);

                    } else {

                        secondEdit = true;
                        colEdited -= 1;
                        table.editCellAt(rowEdited, colEdited);
                    }
                }
            } else {
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
            ListViewRow dataRow = listView.getDataRow(rowEdited);
            dataRow.scheduleItem.l =
             new Location((Integer) table.getValueAt(rowEdited, colEdited),
              new Integer(dataRow.scheduleItem.l.getRoom()));
            dataRow.rowData[13] = new Integer(dataRow.scheduleItem.l.getBuilding());
        } else if (table.getColumnName(colEdited).equals("Room")) {
            ListViewRow dataRow = listView.getDataRow(rowEdited);
            dataRow.scheduleItem.l =
             new Location(new Integer(dataRow.scheduleItem.l.getBuilding()),
              (Integer) table.getValueAt(rowEdited, colEdited));
            dataRow.rowData[14] = new Integer(dataRow.scheduleItem.l.getRoom());
        }
    }

    public void editingCanceled(ChangeEvent e) {
        System.out.println("Editing canceled");
    }


    public void setEditAt(int row, int col) {
        rowEdited = row;
        colEdited = col;
    }

    protected int rowEdited = -1;

    protected int colEdited = -1;

    protected ListView listView;

    protected TableModel tableModel;

    protected JTable table;

    protected boolean secondEdit;
}
