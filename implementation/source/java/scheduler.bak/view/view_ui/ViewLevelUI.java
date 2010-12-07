package scheduler.view.view_ui;

import scheduler.view.ViewLevel;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/****
 * The companion view class for the current view level of the schedule view.
 * This class creates the View Level pane on the right side of a schedule
 * view. The user can switch between a Week or Day view level by selecting
 * the radio button corresponding to the level. 
 *
 * @author Jason Mak
 */
public class ViewLevelUI extends JPanel {

    /**
     * Construct this by calling compose.
     *
     * @param viewLevel companion model
     */
    public ViewLevelUI(ViewLevel viewLevel) {
        this.viewLevel = viewLevel;
        compose();
    }

    /**
     * Create the pane and the radio buttons.
     */
    public void compose() {
        Box vbox = Box.createVerticalBox();
        levelButtonGroup = new ButtonGroup();

        this.setBorder(javax.swing.BorderFactory.createTitledBorder("View"));

        dailyButton = new JRadioButton("Day");
        weeklyButton = new JRadioButton("Week");

        levelButtonGroup.add(dailyButton);
        levelButtonGroup.add(weeklyButton);

        vbox.add(dailyButton);
        vbox.add(weeklyButton);

        if (viewLevel.getLevel() == ViewLevel.Level.DAILY) {
            dailyButton.setSelected(true);
        } else {
            weeklyButton.setSelected(true);
        }
        weeklyButton.addItemListener(
         new ItemListener() {
             public void itemStateChanged(ItemEvent e) {
                 if (e.getStateChange() == ItemEvent.SELECTED) {
                     viewLevel.setLevel(ViewLevel.Level.WEEKLY);                 
                 } else {
                     viewLevel.setLevel(ViewLevel.Level.DAILY);
                 }
             }
         }
        );
        add(vbox);
    }

    /** The button group of the Day and Week radio buttons. */
    protected ButtonGroup levelButtonGroup;

    /** The radio button for a daily schedule view. */
    protected JRadioButton dailyButton;

    /** The radio button for a weekly schedule view. */
    protected JRadioButton weeklyButton;

    /** The current view level of the schedule view. */
    protected ViewLevel viewLevel;
}