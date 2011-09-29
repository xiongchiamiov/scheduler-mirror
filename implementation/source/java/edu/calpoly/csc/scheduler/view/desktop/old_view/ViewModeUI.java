package edu.calpoly.csc.scheduler.view.desktop.old_view;


import javax.swing.*;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/****
 * The companion view for class for the current view mode of the schedule view.
 * This class creates the view mode pane in the right side of a schedule view.
 * A user can change the view mode between list and calendar by selecting
 * a radio button in the pane.
 *
 * @author Jason Mak
 */
public class ViewModeUI extends JPanel {

    /**
     * Construct this by calling compose.
     *
     * @param viewMode companion model
     */
    public ViewModeUI(ViewMode viewMode) {
        this.viewMode = viewMode;
        compose();
    }

    /** Add a pane and the view mode radio buttons. */
    public void compose() {
        Box vbox = Box.createVerticalBox();
        modeButtonGroup = new ButtonGroup();

        this.setBorder(javax.swing.BorderFactory.createTitledBorder("View Mode"));

        listButton = new JRadioButton("List");
        calendarButton = new JRadioButton("Calendar");

        modeButtonGroup.add(listButton);
        modeButtonGroup.add(calendarButton);

        vbox.add(listButton);
        vbox.add(calendarButton);

        if (viewMode.getMode() == ViewMode.Mode.LIST) {
            listButton.setSelected(true);
        } else {
            calendarButton.setSelected(true);
        }
        listButton.addItemListener(
         new ItemListener() {
             public void itemStateChanged(ItemEvent e) {
                 if (e.getStateChange() == ItemEvent.SELECTED) {
                     viewMode.setMode(ViewMode.Mode.LIST);
                 } else {
                     viewMode.setMode(ViewMode.Mode.CALENDAR);
                 }
             }
         }
        );
        add(vbox);
    }

    /** The button group of the List and Calendar radio buttons. */
    protected ButtonGroup modeButtonGroup;

    /** The radio button to select list mode. */
    protected JRadioButton listButton;

    /** The radio button to select calendar mode. */
    protected JRadioButton calendarButton;

    /** The current mode of the schedule view. */
    protected ViewMode viewMode;
}
