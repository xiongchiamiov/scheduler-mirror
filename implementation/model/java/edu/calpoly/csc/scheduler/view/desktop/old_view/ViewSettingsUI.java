package edu.calpoly.csc.scheduler.view.desktop.old_view;

import javax.swing.*;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;

import java.util.*;
import scheduler.view.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;

/****
 * Class ViewSettingsUI provides a view of ViewSettings as an input
 * to the viewSchedule method.  Hence, the dialog is a view of both an
 * ViewSettings object as well as the viewSchedule method.  The
 * data-entry components of the dialog constitute the ViewSettings view.  The
 * 'OK' button is the view of the viewSchedule method.
 *                                                                          <p>
 * The data components consist of JLabels, JTables, JRadioButtons
 * JCheckBoxes, and a JComboBox.  The 'OK', 'Clear', and 'Cancel' buttons are
 * JButtons.  The description of the <a href= "#compose()"> compose </a> method
 * has details of how the components are laid out in the dialog window.
 *                                                                          <p>
 * For organizational clarity, some of the rows in the ViewSettingsUI
 * are defined in separate classes.
 *                                                                          <p>
 * The companion model for ViewSettingsUI is the View </a> class,
 * since View has the method that is invoked from the 'OK' button action listener.
 *
 * @author Jason Mak (jamak3@gmail.com) 50%
 * @author Sasiluk Ruangrongsorakai (sruangro@calpoly.edu) 50%
 */
public class ViewSettingsUI extends javax.swing.JFrame {


    /**
     * Construct this by calling compose
     *
     * @param viewType the viewtype specified in the menubar
     * @param view the view to apply settings on
     */
    public ViewSettingsUI(ViewType viewType, View view) {
        this.view = view;
        this.viewType = viewType;

        if (viewType == ViewType.COURSE) {
            title = "Course Schedule Viewing Options";
            viewTypeFilterPrompt = "Select Courses";
            viewTypeFilter = createFilterNames();
        } else if (viewType == ViewType.INSTRUCTOR) {
            title = "Instructor Schedule Viewing Options";
            viewTypeFilterPrompt = "Select Instructors";
            viewTypeFilter = createFilterNames();
        } else if (viewType == ViewType.LOCATION) {
            title = "Location Schedule Viewing Options";
            viewTypeFilterPrompt = "Select Locations";
            viewTypeFilter = createFilterNames();
        }
        compose();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void compose() {

        viewLevel = new javax.swing.ButtonGroup();
        viewMode = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        daily = new javax.swing.JRadioButton();
        weekly = new javax.swing.JRadioButton();
        dayComboBox = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        calendar = new javax.swing.JRadioButton();
        list = new javax.swing.JRadioButton();
        coursePanel = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        filterPanel = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        selectAllCourse = new javax.swing.JButton();
        clearCourse = new javax.swing.JButton();
        selectAllFilter = new javax.swing.JButton();
        clearFilter = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(title);

        jLabel1.setText("Select the elements you wish to appear in the schedule view:");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Select View Level"));

        viewLevel.add(daily);
        daily.setText("Daily");
        daily.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dailyActionPerformed(evt);
            }
        });

        viewLevel.add(weekly);
        weekly.setText("Weekly");
        weekly.setSelected(true);

        dayComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
           .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(daily)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
            .addComponent(dayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
           .addComponent(weekly))
          .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
           .addComponent(daily)
           .addComponent(dayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(weekly)
          .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Select View Mode"));

        viewMode.add(calendar);
        calendar.setText("Calendar");
        calendar.setSelected(true);

        viewMode.add(list);
        list.setText("List");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
           .addComponent(calendar)
           .addComponent(list))
          .addContainerGap(114, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(calendar)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
          .addComponent(list)
          .addContainerGap())
        );

        coursePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(viewTypeFilterPrompt));

        dataCheckBoxList = new ArrayList<JCheckBox>();

        for (int k = 0;  k < viewTypeFilter.size(); k++) {
            dataCheckBoxList.add(new JCheckBox(viewTypeFilter.get(k)));
            dataCheckBoxList.get(k).setSelected(true);
            if (viewType == ViewType.COURSE) {
                if (courseList.get(k).getCourseType().equals("Lab")) {
                    dataCheckBoxList.get(k).setVisible(false);  
                }
            }
        }
        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup((parallelGroup = jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)))
          .addContainerGap(153, Short.MAX_VALUE))
        );

        for (JCheckBox aDataCheckBoxList1 : dataCheckBoxList) {
            parallelGroup.addComponent(aDataCheckBoxList1);
        }

        jPanel3Layout.setVerticalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup( (sequentialGroup = jPanel3Layout.createSequentialGroup())
          .addContainerGap())
        );

        for (JCheckBox aDataCheckBoxList : dataCheckBoxList) {
            sequentialGroup.addComponent(aDataCheckBoxList);
            sequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
        }

        coursePanel.setViewportView(jPanel3);

        filterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Filter Options"));

        filterCheckBoxList = new JCheckBox[21];

        for (int k = 0; k < 21; k++) {
            filterCheckBoxList[k] = new JCheckBox();
        }

        filterCheckBoxList[0].setText("Course Name");

        filterCheckBoxList[1].setText("Course Number");

        filterCheckBoxList[2].setText("Section");

        filterCheckBoxList[3].setText("Course WTU");

        filterCheckBoxList[4].setText("Course Type");

        filterCheckBoxList[5].setText("Max Enrollment");

        filterCheckBoxList[6].setText("Lab Pairing");

        filterCheckBoxList[7].setText("Course Required Equipment");

        filterCheckBoxList[8].setText("Instructor Name");

        filterCheckBoxList[9].setText("Instructor ID");

        filterCheckBoxList[10].setText("Instructor Office");

        filterCheckBoxList[11].setText("Instructor WTU");

        filterCheckBoxList[12].setText("Instructor Disabilities");

        filterCheckBoxList[13].setText("Building");

        filterCheckBoxList[14].setText("Room");

        filterCheckBoxList[15].setText("Location Max Occupancy");

        filterCheckBoxList[16].setText("Room Type");

        filterCheckBoxList[17].setText("Location Disabilities Compliance");

        filterCheckBoxList[18].setText("Start Time");

        filterCheckBoxList[19].setText("End Time");

        filterCheckBoxList[20].setText("Days");

        defaultFilterOptions();

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel4Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
           .addComponent(filterCheckBoxList[0])
           .addComponent(filterCheckBoxList[1])
           .addComponent(filterCheckBoxList[2])
           .addComponent(filterCheckBoxList[3])
           .addComponent(filterCheckBoxList[4])
           .addComponent(filterCheckBoxList[5])
           .addComponent(filterCheckBoxList[6])
           .addComponent(filterCheckBoxList[7])
           .addComponent(filterCheckBoxList[8])
           .addComponent(filterCheckBoxList[9])
           .addComponent(filterCheckBoxList[10])
           .addComponent(filterCheckBoxList[11])
           .addComponent(filterCheckBoxList[12])
           .addComponent(filterCheckBoxList[13])
           .addComponent(filterCheckBoxList[14])
           .addComponent(filterCheckBoxList[15])
           .addComponent(filterCheckBoxList[16])
           .addComponent(filterCheckBoxList[17])
           .addComponent(filterCheckBoxList[18])
           .addComponent(filterCheckBoxList[19])
           .addComponent(filterCheckBoxList[20]))
          .addContainerGap(91, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel4Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(filterCheckBoxList[0])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[1])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[2])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[3])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[4])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[5])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[6])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[7])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[8])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[9])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[10])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[11])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[12])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[13])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[14])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[15])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[16])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[17])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[18])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[19])
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(filterCheckBoxList[20])
          .addContainerGap(222, Short.MAX_VALUE))
        );

        filterPanel.setViewportView(jPanel4);

        selectAllCourse.setText("Select All");
        selectAllCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllCourseActionPerformed(evt);
            }
        });

        clearCourse.setText("Clear");
        clearCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCourseActionPerformed(evt);
            }
        });

        selectAllFilter.setText("Select All");
        selectAllFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllFilterActionPerformed(evt);
            }
        });

        clearFilter.setText("Clear");
        clearFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFilterActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new OKViewScheduleButtonListener(view,this));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
           .addComponent(jLabel1)
           .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
           .addGroup(layout.createSequentialGroup()
           .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
             .addComponent(selectAllCourse)
             .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
             .addComponent(clearCourse))
            .addComponent(coursePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
            .addComponent(okButton))
           .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
           .addGroup(layout.createSequentialGroup()
            .addGap(16, 16, 16)
            .addComponent(filterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
           .addGroup(layout.createSequentialGroup()
           .addGap(18, 18, 18)
           .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
           .addComponent(cancelButton)
           .addGroup(layout.createSequentialGroup()
           .addComponent(selectAllFilter)
           .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
           .addComponent(clearFilter)))))))
          .addContainerGap())
        );
        layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(jLabel1)
          .addGap(18, 18, 18)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
           .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
           .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addGap(18, 18, 18)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
           .addComponent(filterPanel, 0, 0, Short.MAX_VALUE)
           .addComponent(coursePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE))
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
           .addComponent(selectAllCourse)
           .addComponent(clearCourse)
           .addComponent(selectAllFilter)
           .addComponent(clearFilter))
          .addGap(45, 45, 45)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
           .addComponent(okButton)
           .addComponent(cancelButton))
          .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:compose

    private void dailyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dailyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dailyActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_okButtonActionPerformed

    private void selectAllCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllCourseActionPerformed
        for (int k = 0; k < dataCheckBoxList.size(); k++) {
            dataCheckBoxList.get(k).setSelected(true);
        }
    }//GEN-LAST:event_selectAllCourseActionPerformed

    private void clearCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearCourseActionPerformed
        for (int k = 0; k < dataCheckBoxList.size(); k++) {
            dataCheckBoxList.get(k).setSelected(false);
        }
    }//GEN-LAST:event_clearCourseActionPerformed

    private void selectAllFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllFilterActionPerformed
        for (int k = 0; k < filterCheckBoxList.length; k++) {
            filterCheckBoxList[k].setSelected(true);
        }
    }//GEN-LAST:event_selectAllFilterActionPerformed

    private void clearFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFilterActionPerformed
        for (int k = 0; k < filterCheckBoxList.length; k++) {
            filterCheckBoxList[k].setSelected(false);
        }
    }//GEN-LAST:event_clearFilterActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton calendar;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearCourse;
    private javax.swing.JButton clearFilter;
    private javax.swing.JScrollPane coursePanel;
    private javax.swing.JRadioButton daily;
    private javax.swing.JScrollPane filterPanel;
    private GroupLayout.ParallelGroup parallelGroup;
    private GroupLayout.SequentialGroup sequentialGroup;
    private javax.swing.JComboBox dayComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton list;
    private javax.swing.JButton okButton;
    private javax.swing.JButton selectAllCourse;
    private javax.swing.JButton selectAllFilter;
    private javax.swing.ButtonGroup viewLevel;
    private javax.swing.ButtonGroup viewMode;
    private javax.swing.JRadioButton weekly;

    //End of variables declaration//GEN-END:variables

    /** Title of this window. */
    protected String title;

    /** Prompt for filters based on the ViewType. */
    protected String viewTypeFilterPrompt;

    /** List of label names for filtering ocurses, instructors, or locations. */
    protected ArrayList<String> viewTypeFilter;

    /** List of checkboxes for each course, instructor, or location. */
    protected ArrayList<JCheckBox> dataCheckBoxList;

    /** Array of checkboxes for each filter option. */
    protected JCheckBox[] filterCheckBoxList;

    /** The type of view created by this UI dialog. */
    protected ViewType viewType;

    /** The courses to be displayed in the left panel for a course view. */
    protected LinkedList<Course> courseList;

    /** The instructors to be displayed in the left panel for an instructor view. */
    protected LinkedList<Instructor> instructorList;

    /** The locations to be displayed in the left panel for a location view. */
    protected LinkedList<Location> locationList;

    /** View module */
    protected View view;

    /**
     * The default filters options for a course, instructor, or location view.    
     */
    protected void defaultFilterOptions(){
        if (viewType == ViewType.COURSE) {
            filterCheckBoxList[1].setSelected(true);
            filterCheckBoxList[2].setSelected(true);

        } else if (viewType == ViewType.INSTRUCTOR) {
            filterCheckBoxList[1].setSelected(true);
            filterCheckBoxList[2].setSelected(true);
            filterCheckBoxList[8].setSelected(true);

        } else {
            filterCheckBoxList[1].setSelected(true);
            filterCheckBoxList[2].setSelected(true);
            filterCheckBoxList[13].setSelected(true);
            filterCheckBoxList[14].setSelected(true);
        }
        filterCheckBoxList[18].setSelected(true);
        filterCheckBoxList[19].setSelected(true);
        filterCheckBoxList[20].setSelected(true);
    }


    /**
     * Returns the view mode selected in the dialog.
     *
     * @return the selected view mode
     */
    public ViewMode getViewMode() {
        if (calendar.isSelected()) {
            return new ViewMode(view, ViewMode.Mode.CALENDAR);
        }
        return new ViewMode(view, ViewMode.Mode.LIST);
    }

    /**
     * Returns the view type selected in the dialog.
     *
     * @return the selected view type
     */
    public ViewType getViewType() {
        return viewType;
    }

    /**
     * Returns the view level selected in the dialog.
     *
     * @return the view level selected in this dialog
     */
    public ViewLevel getViewLevel() {
        ViewLevel viewLevel;
        if (weekly.isSelected()) {
            return new ViewLevel(view, ViewLevel.Level.WEEKLY);
        } else {
            viewLevel = new ViewLevel(view, ViewLevel.Level.DAILY);
            if (dayComboBox.getSelectedItem().equals("Monday")) {
                viewLevel.setDay(DaysInWeek.Day.MON);
            } else if (dayComboBox.getSelectedItem().equals("Tuesday")) {
                viewLevel.setDay(DaysInWeek.Day.TUE);
            } else if (dayComboBox.getSelectedItem().equals("Wednesday")) {
                viewLevel.setDay(DaysInWeek.Day.WED);
            } else if (dayComboBox.getSelectedItem().equals("Thursday")) {
                viewLevel.setDay(DaysInWeek.Day.THU);
            } else {
                viewLevel.setDay(DaysInWeek.Day.FRI);
            }
            return viewLevel;
        }
    }

    /**
     * Makes a list of string representations of courses, instructors,
     * or location, depending on view type.
     *
     * @return a list of course names, instructor names, or location strings
     */
    public ArrayList<String> createFilterNames() {
        ArrayList<String> filterTextList = new ArrayList<String>();

        if (view.getSchedule() != null) {
            if (viewType == ViewType.COURSE) {
                courseList = view.getSchedule().getCourseList();
                for (Course aCourse : courseList) {
                    filterTextList.add(aCourse.toString());
                }
            } else if (viewType == ViewType.INSTRUCTOR) {
                instructorList = view.getSchedule().getInstructorList();
                for (Instructor aInstructor : instructorList) {
                    filterTextList.add(aInstructor.toString());
                }
            } else {
                locationList = view.getSchedule().getLocationList();
                for (Location aLocation : locationList) {
                    filterTextList.add(aLocation.toString());
                }
            }
        }

        return filterTextList;
    }
    /**
     * Returns the array of filter option checkboxes.
     *
     * @return the selected filter options
     */
    public JCheckBox[] getFilterCheckBoxlist() {
        return filterCheckBoxList;
    }

    /**
     * Returns the array of course, instructor, or location checkboxes.
     *
     * @return the selected courses, instructors, or locations
     */
    public ArrayList<JCheckBox> getDataCheckBoxlist() {
        return dataCheckBoxList;
    }

    /**
     * Returns an instance of this class using the settings for a location view.
     *
     * @param view the view to apply settings on
     * @return a new instance of this class with location view type
     */
    public static ViewSettingsUI getNewLocationViewSettingsUI(View view) {
        return new ViewSettingsUI(ViewType.LOCATION, view);
    }

    /**
     * Returns an instance of this class using the settings for an instructor view.
     *
     * @param view the view to apply settings on
     * @return a new instance of this class with instructor view type
     */
    public static ViewSettingsUI getNewInstructorViewSettingsUI(View view) {
        return new ViewSettingsUI(ViewType.INSTRUCTOR, view);
    }

    /**
     * Returns an instance of this class using the settings for a course view.
     *
     * @param view the view to apply settings on
     * @return a new instance of this class with course view type
     */
    public static ViewSettingsUI getNewCourseViewSettingsUI(View view) {
        return new ViewSettingsUI(ViewType.COURSE, view);
    }

    /**
     * stub main for testing.
     *
     * @param args for main
     */
    public static void main(String args[]) {
        ViewSettingsUI c = getNewLocationViewSettingsUI(new View(null));
        c.setVisible(true);
    }
}
