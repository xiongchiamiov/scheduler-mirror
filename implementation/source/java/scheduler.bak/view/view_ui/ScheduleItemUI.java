package scheduler.view.view_ui;

import scheduler.db.coursedb.Course;
import scheduler.db.instructordb.Instructor;
import scheduler.generate.ScheduleItem;
import scheduler.view.FilterOptions;

import javax.swing.*;

/****
 * Class ScheduleItemUI is hte companion view class for a ScheduleItem.
 * It displays a ScheduleItem in a view dialog info window.
 *
 * @author Jason Mak
 */
public class ScheduleItemUI extends JFrame {

    /**
     * Construct this with a ScheduleItem and FilterOptions.
     *
     * @param scheduleItem the companion model
     * @param filterOptions the current set of filter options
     * @throws scheduler.db.instructordb.Instructor.NullUserIDException check valid instructor
     */
    public ScheduleItemUI(ScheduleItem scheduleItem, FilterOptions filterOptions) throws Instructor.NullUserIDException {
        this.scheduleItem = scheduleItem;
        this.filterOptionsArray = filterOptions.toArray();
        compose();
    }

    /**
     * Add all unfiltered information to the ScheduleItem Display.
     * 
     * @throws scheduler.db.instructordb.Instructor.NullUserIDException  check valid instructor
     */
    public void compose() throws Instructor.NullUserIDException {
        JPanel scheduleItemPanel = new JPanel();
        String hasTheseEquip, hasDisability, hasPairedLab, ADACompliant;
        Course.RequiredEquipment equipment;

        hasPairedLab = scheduleItem.c.getLabPairing() == null ? "no" : "yes";
        equipment = scheduleItem.c.getRequiredEquipment();
        hasTheseEquip = "";
        if (equipment.hasLaptopConnectivity())
            hasTheseEquip += "PC-connect ";
        if (equipment.hasOverhead())
            hasTheseEquip += "Overhead ";
        if (equipment.isSmartroom())
            hasTheseEquip += "Smartoom";
        hasDisability = scheduleItem.i.getDisability() ? "yes" : "no";
        ADACompliant = scheduleItem.l.isADACompliant() ? "yes" : "no";

        Box vBox = Box.createVerticalBox();
        if (filterOptionsArray[0])
            vBox.add(new JLabel(FilterOptions.filterNames[0] + ": " + scheduleItem.c.getCourseName()));
        if (filterOptionsArray[1])
            vBox.add(new JLabel(FilterOptions.filterNames[1] + ": " + scheduleItem.c.getId()));
        if (filterOptionsArray[2])
            vBox.add(new JLabel(FilterOptions.filterNames[2] + ": " + scheduleItem.section));
        if (filterOptionsArray[3])
            vBox.add(new JLabel(FilterOptions.filterNames[3] + ": " + scheduleItem.c.getWTU()));
        if (filterOptionsArray[4])
            vBox.add(new JLabel(FilterOptions.filterNames[4] + ": " + scheduleItem.c.getCourseType()));
        if (filterOptionsArray[5])
            vBox.add(new JLabel(FilterOptions.filterNames[5] + ": " + scheduleItem.c.getMaxEnrollment()));
        if (filterOptionsArray[6])
            vBox.add(new JLabel(FilterOptions.filterNames[6] + ": " + hasPairedLab));
        if (filterOptionsArray[7])
            vBox.add(new JLabel(FilterOptions.filterNames[7] + ": " + hasTheseEquip));
        if (filterOptionsArray[8])
            vBox.add(new JLabel(FilterOptions.filterNames[8] + ": " +  scheduleItem.i.getName()));
        if (filterOptionsArray[9])
            try {
                vBox.add(new JLabel(FilterOptions.filterNames[9] + ": " + scheduleItem.i.getId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (filterOptionsArray[10])
            vBox.add(new JLabel(FilterOptions.filterNames[10] + ": " + scheduleItem.i.getOffice()));
        if (filterOptionsArray[11])
            vBox.add(new JLabel(FilterOptions.filterNames[11] + ": " + scheduleItem.i.getMaxWTU()));
        if (filterOptionsArray[12])
            vBox.add(new JLabel(FilterOptions.filterNames[12] + ": " + hasDisability));
        if (filterOptionsArray[13])
            vBox.add(new JLabel(FilterOptions.filterNames[13] + ": " + scheduleItem.l.getBuilding()));
        if (filterOptionsArray[14])
            vBox.add(new JLabel(FilterOptions.filterNames[14] + ": " + scheduleItem.l.getRoom()));
        if (filterOptionsArray[15])
            vBox.add(new JLabel(FilterOptions.filterNames[15] + ": " + scheduleItem.l.getMaxOccupancy()));
        if (filterOptionsArray[16])
            vBox.add(new JLabel(FilterOptions.filterNames[16] + ": " + scheduleItem.l.getType()));
        if (filterOptionsArray[17])
            vBox.add(new JLabel(FilterOptions.filterNames[17] + ": " + ADACompliant));
        if (filterOptionsArray[18])
            vBox.add(new JLabel(FilterOptions.filterNames[18] + ": " + scheduleItem.start));
        if (filterOptionsArray[19])
            vBox.add(new JLabel(FilterOptions.filterNames[19] + ": " + scheduleItem.end));
        if (filterOptionsArray[20])
            vBox.add(new JLabel(FilterOptions.filterNames[20] + ": " + scheduleItem.days));
        scheduleItemPanel.add(vBox);
        add(scheduleItemPanel);
        setTitle("Course Information");
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);        
        setVisible(true);
    }

    /** ScheduleItem to be displayed. */
    protected ScheduleItem scheduleItem;

    /** FilterOptions to determine data displayed. */
    protected boolean[] filterOptionsArray;
}
