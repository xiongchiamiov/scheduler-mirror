package scheduler.db.admin.admin_ui;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CourseView.java
 *
 * Created on Jan 24, 2010, 5:31:34 PM
 */

import java.util.*;
import javax.swing.*;
import scheduler.*;
import scheduler.generate.Week;
import scheduler.db.*;
import scheduler.db.coursedb.CourseDB;
import scheduler.db.coursedb.Course;
import scheduler.db.preferencesdb.*;
import scheduler.menu.schedule.*;

/**
 * The View of the course database class.
 * @author Jan Lorenz Soliman
 */
public class CourseView extends MyView implements Observer {


    /** Course database */
    private CourseDB database = Scheduler.cdb;
    /** Individual course */
    private Course course = new Course("",-1,0,0,"",0,0,null,null, "", null);
    /** The Add course dialog */
    private AddCourse addCourse;

    /** Creates new form CourseView */
    public CourseView() {
        database.addObserver(this);
        addCourse = new AddCourse(database);
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Course Database");
        /*PreferencesDB pdb = new PreferencesDB();
        Course c1 = new Course("Fundamentals of Computer Science I",0,0,0,"",0,0,null,null);
        Course c2 = new Course("Fundamentals of Computer Science II",0,0,0,"",0,0,null,null);
        HashSet<Course> hs = new HashSet<Course>();
        hs.add(c1);
        hs.add(c2);
        NoClassOverlap nco = new NoClassOverlap("Graduate classes cannot overlap.", 5, hs );

        int[] days = {1, 3, 5};
        Week wk = new Week(days);
        DaysForClasses dfc = new DaysForClasses("MWF", 3, wk);

         pdb.addPreference(nco);
         pdb.addPreference(dfc);
         pdb.removePreference(nco);
         pdb.removePreference(dfc);*/
    } 
    
    /** Updates the view when the database changes
     *  @param obs The observable object
     *  @param obj The argument passed from the observable
     **/
    public void update(Observable obs, Object obj) {
        database = Scheduler.cdb;
        database.addObserver(this);
        System.out.println("In Update");
        //this.initComponents();
        

        Name.setText("Name: ");
        CourseNumber.setText("Course #:");
        WTUs.setText("WTUs:");
        SCUs.setText("SCUs:");
        ClassType.setText("Class Type:");
        MaxEnrollment.setText("Max Enrollment: ");
        OptionalField.setText("Optional Fields");
        jLabel1.setText("Lab Pairing: ");
        jLabel2.setText("Required Equipment:");
        
        final ArrayList<String> names = new ArrayList<String>();
        ArrayList<Course> data = (ArrayList) database.getData();
        Collections.sort((List)data);
        if (data != null) {
            for (Course c: data ) {
               if (c.getCourseType().equals("Lab")) {
                  names.add(c + "L");
               }
               else {
                  names.add(c.toString());
               }
            }
        }
        jList1.setModel(new javax.swing.AbstractListModel() {
            //String[] strings = { "CPE 101", "CPE 102", "CPE 103", "CPE 225" };
            ArrayList<String> strings = names;
            public int getSize() { return strings.size(); }
            public Object getElementAt(int i) { return strings.get(i); }
        });
        jScrollPane1.setViewportView(jList1);
    }

    /**
     *  The method to initialize components.
     *
     * 
     **/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        AddCourse = new javax.swing.JButton();
        EditCourse = new javax.swing.JButton();
        RemoveCourse = new javax.swing.JButton();
        AddSections = new javax.swing.JButton();
        Name = new javax.swing.JLabel();
        CourseNumber = new javax.swing.JLabel();
        WTUs = new javax.swing.JLabel();
        SCUs = new javax.swing.JLabel();
        ClassType = new javax.swing.JLabel();
        MaxEnrollment = new javax.swing.JLabel();
        OptionalField = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ViewList = new javax.swing.JList();
        CoursesLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);


        final ArrayList<String> names = new ArrayList<String>();
        ArrayList<Course> data = (ArrayList) database.getData();
        Collections.sort((List)data);
        if (data != null) {
            for (Course c: data ) {
               if (c.getCourseType().equals("Lab")) {
                  names.add(c + "L");
               }
               else {
                  names.add(c.toString());
               }
            }
            System.out.println (data);
        }
        jList1.setModel(new javax.swing.AbstractListModel() {
            //String[] strings = { "CPE 101", "CPE 102", "CPE 103", "CPE 225" };
            ArrayList<String> strings = names;
            public int getSize() { return strings.size(); }
            public Object getElementAt(int i) { return strings.get(i); }
        });
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                instructorListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);
//        jScrollPane1.setViewportView(ScheduleMenu.cList);

        AddCourse.setText("Add Course");
        AddCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddCourseActionPerformed(evt);
            }
        });

        EditCourse.setText("Edit Course");
        EditCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditCourseActionPerformed(evt);
            }
        });

        RemoveCourse.setText("Remove Course");
        RemoveCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveCourseActionPerformed(evt);
            }
        });

        AddSections.setText("Add Sections");
        AddSections.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddSectionsActionPerformed(evt);
            }
        });

        Name.setText("Name: ");

        CourseNumber.setText("Course #:");

        WTUs.setText("WTUs:");

        SCUs.setText("SCUs:");

        ClassType.setText("Class Type:");

        MaxEnrollment.setText("Max Enrollment: ");

        OptionalField.setText("Optional Fields");

        jLabel1.setText("Lab Pairing: ");

        jLabel2.setText("Required Equipment:");

        ViewList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Name:", "Prefix:", "Course #:", "WTUs:", "SCUs:", "Class Type:", "Max Enrollment:",  "Hours Per Week:", "Type Prefix:", "Optional Fields", "Lab Pairing:", "Required Equipment:"};
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        ViewList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ViewList.setMaximumSize(new java.awt.Dimension(0, 0));
        ViewList.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPane2.setViewportView(ViewList);

        CoursesLabel.setText("Courses:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CoursesLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(RemoveCourse, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AddCourse, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(EditCourse, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CoursesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(AddCourse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(EditCourse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(RemoveCourse)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    /**
     *  Method invoked when the Add Course button is pressed.
     *  @param evt The event action.
     **/
    private void AddCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddCourseActionPerformed
        // TODO add your handling code here:
        addCourse.initialize();
        //addCourse.setVisible(true);
        addCourse.show(175,175);
        System.out.println("In CourseView.AddCourseActionPerformed");
    }//GEN-LAST:event_AddCourseActionPerformed


    /**
     *  Method invoked when the Edit Course button is pressed.
     *  @param evt The event action.
     **/
    private void EditCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditCourseActionPerformed
        // TODO add your handling code here:
        if (course.getId() >= 0) {
            EditCourse editCourse = new EditCourse(course);
            //editCourse.setVisible(true);
            editCourse.show(175,175);
        }
        System.out.println("In CourseView.EditCourseActionPerformed");
    }//GEN-LAST:event_EditCourseActionPerformed


    /**
     *  Method invoked when the Remove Course button is pressed.
     *  @param evt The event action.
     **/
    private void RemoveCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveCourseActionPerformed
        // TODO add your handling code here:
        Course removed = course;
        try {
            this.database.removeCourse(removed);
        }
        catch (CourseDB.CourseDoesNotExistException e) {

        }
        jList1.clearSelection();
        System.out.println("In CourseView.RemoveCourseActionPerformed");
    }//GEN-LAST:event_RemoveCourseActionPerformed


    /**
     *  Method invoked when the Add Sections button is pressed.
     *  @param evt The event action.
     **/
    private void AddSectionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddSectionsActionPerformed
        // TODO add your handling code here:
        SectionsPerCourse sections = new SectionsPerCourse();
        sections.show(175,175);
        //sections.setVisible(true);
        System.out.println("In CourseView.AddSectionsActionPerformed");
    }//GEN-LAST:event_AddSectionsActionPerformed

    /**
     * The main method for the view.
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CourseView().setVisible(true);
            }
        });
    }

    public int getId(String course) {
        int index = 0;
        for (int i = 0; i < course.length() ; i++) {
            if (Character.isDigit(  course.charAt(i) ) ) {
               index = i;
               break;
            }
        }
        return Integer.parseInt(course.substring(index, index+3));
    }

    private void instructorListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_instructorListValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (jList1.getSelectedValue() != null) {
               String selection = jList1.getSelectedValue().toString();
               int id = getId(selection);
               if (!selection.contains("L")) {
                  course = database.getCourse(id, "Lecture");
               }
               else {
                  course = database.getCourse(id, "Lab");
               }
               //System.out.println("Course is " + course.getCourseName());
               
               ClassType.setText("Class Type: " + course.getCourseType()); 
               CourseNumber.setText("Course #: " + course.getId());
               MaxEnrollment.setText("Max Enrollment: " + course.getMaxEnrollment());
               Name.setText("Name: " + course.getCourseName());
               SCUs.setText("SCUs: " + course.getSCUs());
               WTUs.setText("WTUs: " + course.getWTU());
               String labPairing = "";
               if (course.getLabPairing() != null) {
                  labPairing = course.getLabPairing().toString();
               }
               jLabel1.setText("Lab Pairing: " + labPairing);
               String reqEquip = "Required Equipment: ";
               if (course.getRequiredEquipment().isSmartroom()) {
                  reqEquip = reqEquip + "Smartroom, ";
               }
               if (course.getRequiredEquipment().hasOverhead()) {
                  reqEquip = reqEquip + "Overhead, ";
               }
               if (course.getRequiredEquipment().hasLaptopConnectivity()) {
                  reqEquip = reqEquip + "Laptop Connectivity";
               }
               jLabel2.setText(reqEquip);
               String cdp = "";
               if (course.getDFC().size() > 0) {
                   cdp = course.getDFC().get(0).toString();
               }
               String[] strings = { "Name: " + course.getCourseName(), "Prefix: " + course.getPrefix(),
                                      "Course #: " + course.getId(), "WTUs: " + course.getWTU(),
                                      "SCUs: " + course.getSCUs(), "Class Type: " + course.getCourseType(),
                                      "Max Enrollment: " + course.getMaxEnrollment(), "Class Day Preference: " + cdp,
                                       "Hours Per Week: " + course.getHoursPerWeek() , "Type Prefix: " + course.getCTPrefix(), "Optional Fields",
                                      "Lab Pairing: " + labPairing, reqEquip };
               ViewList.setListData(strings);
            }

        }
    }//GEN-LAST:event_instructorListValueChanged




    // Variables declaration - do not modify//GEN-BEGIN:variables
    /**Button for adding the course */
    private javax.swing.JButton AddCourse;
    /** Button for adding sections */
    private javax.swing.JButton AddSections;
    /** Label for the class type */
    private javax.swing.JLabel ClassType;
    /** Label for the course number */
    private javax.swing.JLabel CourseNumber;
    /** The top course label*/
    private javax.swing.JLabel CoursesLabel;
    /** Button for the edit course*/
    private javax.swing.JButton EditCourse;
    /** Label for the maximum enrollment */
    private javax.swing.JLabel MaxEnrollment;
    /** Label for the name*/
    private javax.swing.JLabel Name;
    /** Label for the optional field*/
    private javax.swing.JLabel OptionalField;
    /**Button to remove course */
    private javax.swing.JButton RemoveCourse;
    /** Label for SCUs */
    private javax.swing.JLabel SCUs;
    /** Label for WTUs */
    private javax.swing.JLabel WTUs;
    /** Label for lab pairing*/
    private javax.swing.JLabel jLabel1;
    /** Label for required equipment */
    private javax.swing.JLabel jLabel2;
    /** The side list.*/
    private javax.swing.JList ViewList;
    /** List of courses*/
    private javax.swing.JList jList1;
    /** Panel for the list*/
    private javax.swing.JScrollPane jScrollPane1;
    /** Panel for the second list*/
    private javax.swing.JScrollPane jScrollPane2;

    // End of variables declaration//GEN-END:variables

}
