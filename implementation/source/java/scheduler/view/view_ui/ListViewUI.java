package scheduler.view.view_ui;

import scheduler.view.ListView;
import scheduler.db.instructordb.*;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;

/****
 * Class ListViewUI provides a view of a Schedule in list mode. Its
 * companion model class is ListView.
 *
 * @author Jason Mak
 */
public class ListViewUI extends JScrollPane {
    /**
     * Construct this by calling compose
     *
     * @param listView companion model
     */
    public ListViewUI(ListView listView) {
        this.listView = listView;
        table = listView.getTable();
        table.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (table.isEditing()) {
                        try {
                            table.getDefaultEditor(Object.class).stopCellEditing();
                        } catch (Exception excep) {

                        }
                        try {
                            table.getDefaultEditor(Integer.class).stopCellEditing();
                        } catch (Exception excep) {

                        }
                        e.consume(); // prevent the event from passing on
                    }
                }
            }
        });

        manualEditListener = new ManualEditListener(listView, table);
        table.getDefaultEditor(Object.class).addCellEditorListener(manualEditListener);
        table.getDefaultEditor(Integer.class).addCellEditorListener(manualEditListener);
        compose();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form. This sets up a mouse listener so that when a row
     * in the table is double clicked, a new ScheduleItemUI window appears.
     *
     * @return this object
     * @throws scheduler.db.instructordb.Instructor.NullUserIDException
     */
    public Component compose() throws Instructor.NullUserIDException {
        setPreferredSize(new Dimension(1000, 700));
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setRowSelectionAllowed(true);

        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                if (table.isEditing()) {
                    e.consume();
                    return;
                }

                if (e.getClickCount() == 2){
                    try {
                        ScheduleItemUI scheduleItemUI =
                         new ScheduleItemUI(listView.getDataRow(table.getSelectedRow()).scheduleItem,
                          listView.view.getViewSettings().getFilterOptions());
                        scheduleItemUI.setLocation(e.getLocationOnScreen());
                    }
                    catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else if (e.getClickCount() == 1) {
                    manualEditListener.setEditAt(table.getSelectedRow(), table.getSelectedColumn());
                    table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
                }
            }
        } );
        setViewportView(table);
        this.setBorder(BorderFactory.createTitledBorder("List View"));

        return this;
    }

    public ManualEditListener manualEditListener;

    /** The list view table. */
    protected JTable table;


    /** The companion model object. */
    protected ListView listView;
}
