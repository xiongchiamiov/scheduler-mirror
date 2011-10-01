package scheduler.db.admin.admin_ui;

import java.util.*;
import javax.swing.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.cdb.RequiredEquipment;
import edu.calpoly.csc.scheduler.model.db.pdb.DaysForClasses;
import edu.calpoly.csc.scheduler.view.desktop.MyView;
import scheduler.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;

/**
 * The edit course dialog.
 *
 * @author Jan Lorenz Soliman
 */
public class EditLocalCourse extends MyView implements Observer {


    /** Course database */
    private CourseDB database = Scheduler.cdb;
    /** Individual course */
    private Course course;

    /** Creates new form EditLocalCourse */
    public EditLocalCourse(Course c) {
        this.course = c;
        Scheduler.pdb.addObserver(this);
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    }


    /** Updates the view when the database changes
     *  @param obs The observable object
     *  @param obj The argument passed from the observable
     **/
    public void update(Observable obs, Object obj) {
        PreferenceBox.setModel(new javax.swing.DefaultComboBoxModel(Scheduler.pdb.getLocalDaysForClasses()));
    }


    /** 
     *  The method to initialize components.
     *
     **/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        String name = course.getCourseName();
        String number = course.getId() + "";
        String wtus = course.getWTUs() + "";
        String scus = course.getSCUs() + "";
        String type = course.getCourseType();
        String enrollment = course.getMaxEnrollment() + "";
        Course labpairing = course.getLabPairing();
        RequiredEquipment req = course.getRequiredEquipment();
        boolean isSmartroom = req.isSmartroom();
        boolean overhead = req.hasOverhead();
        boolean laptop = req.hasLaptopConnectivity();
        Prefix = new javax.swing.JLabel();
        PrefixBox = new javax.swing.JComboBox();
        int hpw = course.getHoursPerWeek();
        String typePrefix = course.getCTPrefix();
        NameField = new javax.swing.JTextField(name);
        CourseNumberField = new javax.swing.JTextField(number);
        CourseNumberField.setEnabled(false);
        wtusField = new javax.swing.JTextField(wtus);
        scusField = new javax.swing.JTextField(scus);
        Name = new javax.swing.JLabel();
        CourseNumber = new javax.swing.JLabel();
        WTUs = new javax.swing.JLabel();
        SCUs = new javax.swing.JLabel();
        ClassTypeField = new javax.swing.JComboBox();
        ClassTypeField.setSelectedItem((Object)type);
        ClassType = new javax.swing.JLabel();
        MaxEnrollmentField = new javax.swing.JTextField(enrollment);
        MaxEnrollment = new javax.swing.JLabel();
        OptionalFields = new javax.swing.JLabel();
        LabPairing = new javax.swing.JLabel();
        LabPairingField = new javax.swing.JComboBox();
        LabPairingField.setSelectedItem((Object)labpairing);
        Smartroom = new javax.swing.JRadioButton();
        Smartroom.setSelected(isSmartroom);
        Overhead = new javax.swing.JRadioButton();
        Overhead.setSelected(overhead);
        LaptopConnectivity = new javax.swing.JRadioButton();
        LaptopConnectivity.setSelected(laptop);
        jLabel9 = new javax.swing.JLabel();
        Submit = new javax.swing.JButton();
        Cancel = new javax.swing.JButton();
        Preference = new javax.swing.JLabel();
        PreferenceBox = new javax.swing.JComboBox();
        HPWField = new javax.swing.JTextField((hpw + ""));
        TypePrefixField = new javax.swing.JTextField(typePrefix);
        HoursPerWeekLabel = new javax.swing.JLabel();
        TypePrefixLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Name.setText("Name");

        CourseNumber.setText("Course #");

        WTUs.setText("WTUs");

        SCUs.setText("SCUs");


        DefaultComboBoxModel boxmodel = new DefaultComboBoxModel(database.getLocalLabNames() );
        LabPairingField.setModel(boxmodel );
        ClassTypeField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Lecture", "Lab" }));

        ClassType.setText("Class Type");

        MaxEnrollment.setText("Max Enrollment");

        OptionalFields.setText("Optional Fields");

        LabPairing.setText("Lab Pairing");

        Smartroom.setText("Smartroom");

        Overhead.setText("Overhead");

        LaptopConnectivity.setText("Laptop Connectivity");

        jLabel9.setText("Required Equipment");

        Submit.setText("Submit");
        Submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitActionPerformed(evt);
            }
        });

        Cancel.setText("Cancel");
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        Prefix.setText("Prefix");

        PrefixBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CPE", "CSC" }));

        Preference.setText("Preference");

        PreferenceBox.setModel(new javax.swing.DefaultComboBoxModel(Scheduler.pdb.getDaysForClassesNames()));


        HoursPerWeekLabel.setText("Hours Per Week");
        TypePrefixLabel.setText("Type Prefix");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Prefix)
                        .addContainerGap(313, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(MaxEnrollment)
                        .addContainerGap(249, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(WTUs)
                                    .addComponent(CourseNumber)
                                    .addComponent(SCUs)
                                    .addComponent(Name)
                                    .addComponent(ClassType)
                                    .addComponent(HoursPerWeekLabel)
                                    .addComponent(TypePrefixLabel))
                                .addGap(66, 66, 66)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(TypePrefixField, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                    .addComponent(NameField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                    .addComponent(scusField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                    .addComponent(wtusField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                    .addComponent(CourseNumberField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                    .addComponent(PrefixBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 139, Short.MAX_VALUE)
                                    .addComponent(ClassTypeField, javax.swing.GroupLayout.Alignment.LEADING, 0, 139, Short.MAX_VALUE)
                                    .addComponent(MaxEnrollmentField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                    .addComponent(HPWField, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                                .addGap(21, 21, 21))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(Submit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                                .addComponent(Cancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(LaptopConnectivity)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(Overhead)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(Smartroom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(OptionalFields)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(LabPairing)
                                    .addComponent(Preference))
                                .addGap(66, 66, 66)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(PreferenceBox, 0, 191, Short.MAX_VALUE)
                                    .addComponent(LabPairingField, 0, 191, Short.MAX_VALUE))))
                        .addGap(21, 21, 21))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Prefix)
                            .addComponent(PrefixBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(WTUs)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(NameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Name))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(CourseNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CourseNumber))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(wtusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SCUs)
                            .addComponent(scusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ClassType)
                            .addComponent(ClassTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(MaxEnrollment)
                            .addComponent(MaxEnrollmentField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(HoursPerWeekLabel)
                            .addComponent(HPWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TypePrefixField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Preference)
                            .addComponent(PreferenceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(OptionalFields)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LabPairing)
                            .addComponent(LabPairingField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Smartroom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Overhead)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LaptopConnectivity)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Submit)
                            .addComponent(Cancel))
                        .addGap(48, 48, 48))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(TypePrefixLabel)
                        .addGap(305, 305, 305))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    /**
     *  Method performed when the cancel button is pressed
     *  @param evt The action event.
     **/
    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_CancelActionPerformed



    /**
     *  Method performed when the submit button is pressed
     *  @param evt The action event.
     **/
    private void SubmitActionPerformed (java.awt.event.ActionEvent evt) {
        boolean aPt, bPt, cPt, dPt, ePt;
        aPt = false; bPt = false; cPt = false; dPt = false; ePt = false;
        try {
            RequiredEquipment equip = new RequiredEquipment (Smartroom.isSelected(), Overhead.isSelected(), LaptopConnectivity.isSelected()  );
            String labSelected =  (String)LabPairingField.getSelectedItem();
            Course lab = null;
            if (labSelected == null) {

            }
            else {
               if (!labSelected.equals("")) {
                  System.out.println (labSelected);
                  labSelected = labSelected.substring(3, 6);
                  int id = Integer.parseInt(labSelected);
                  lab = database.getLocalCourse(id, "Lab");
               }
            }
            String name = NameField.getText();
            aPt = true;
            int id =  Integer.parseInt(CourseNumberField.getText() );
            bPt = true;
            int wtus = Integer.parseInt(wtusField.getText());
            cPt = true;
            int scus =  Integer.parseInt(scusField.getText());
            dPt = true;
            String classtype =  (String)ClassTypeField.getSelectedItem();
            Integer.parseInt(MaxEnrollmentField.getText());
            ePt = true;
            int hpw = Integer.parseInt(HPWField.getText());
            String typePref = TypePrefixField.getText();
           //String dfcName = (String)this.PreferenceBox.getSelectedItem();
           //System.out.println("DFCNAME is " + dfcName);
           //DaysForClasses dfc = Scheduler.pdb.getDaysForClassesByName(dfcName);
           DaysForClasses dfc = (DaysForClasses)this.PreferenceBox.getSelectedItem();

           this.course = new Course(NameField.getText() , Integer.parseInt(CourseNumberField.getText() ) , Integer.parseInt(wtusField.getText()), Integer.parseInt(scusField.getText()), (String)ClassTypeField.getSelectedItem(), Integer.parseInt(MaxEnrollmentField.getText()), 1, lab, equip,  (String)PrefixBox.getSelectedItem(), dfc, hpw, typePref );
            if (name.equals("")) {
                        JOptionPane.showMessageDialog(this,
                           "The name field is empty.",
                           "Error",
                           JOptionPane.ERROR_MESSAGE);                     
            }
            else if (id < 100) {
                        JOptionPane.showMessageDialog(this,
                           "The id must be three digits.",
                           "Error",
                           JOptionPane.ERROR_MESSAGE); 
            }
            else {
               try {
                  this.database.removeLocalCourse(course);
               }
               catch (CourseDB.CourseDoesNotExistException e) {}
               try {
                  this.database.addLocalCourse(course);
               }
               catch (CourseDB.CourseExistsException e) {
                  Scheduler.debug("Yep thats it.");
               }
               this.setVisible(false);
            }
        }
        catch ( NumberFormatException e) {
            String message = "";
            if (ePt) {
               message = "Hours per Week Field must not be empty.";
            }
            else if (dPt) {
               message = "Max Enrollment Field must be an integer.";
            }
            else if (cPt) {
               message = "SCUs Field must be an integer.";
            }
            else if (bPt) {
               message = "WTUs Field must be an integer.";
            }
            else if (aPt) {
               message = "Course Number must be an integer.";
            }

            JOptionPane.showMessageDialog(this,
               message,
               "Error",
               JOptionPane.ERROR_MESSAGE);
            aPt = false; bPt = false; cPt = false;
        }

    }


    /**
     * The main method to start the GUI.
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new EditLocalCourse().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    /** Cancel button */
    private javax.swing.JButton Cancel;
    /** Label for class type */
    private javax.swing.JLabel ClassType;
    /** Field for the Class type */
    private javax.swing.JComboBox ClassTypeField;
    /** Label for the course number */
    private javax.swing.JLabel CourseNumber;
    /** Course Number Field */
    private javax.swing.JTextField CourseNumberField;
    /** Label for the lab pairing */
    private javax.swing.JLabel LabPairing;
    /** Dropdown for the lab pairing */
    private javax.swing.JComboBox LabPairingField;
    /** Radio button for laptop connectivity*/
    private javax.swing.JRadioButton LaptopConnectivity;
    /** Label for the maximum enrollment */
    private javax.swing.JLabel MaxEnrollment;
    /** Field for the maximum enrollment */
    private javax.swing.JTextField MaxEnrollmentField;
    /** Label for the course name */
    private javax.swing.JLabel Name;
    /** Field for the course name */
    private javax.swing.JTextField NameField;
    /** Label for Optional Fields */
    private javax.swing.JLabel OptionalFields;
    /** Radio button for selecting Overhead  */
    private javax.swing.JRadioButton Overhead;
    /** Label for the preference box */
    private javax.swing.JLabel Preference;
    /** Dropdown box to select day preference*/
    private javax.swing.JComboBox PreferenceBox;
    /** Prefix Label */
    private javax.swing.JLabel Prefix;
    /** Prefix Box */
    private javax.swing.JComboBox PrefixBox;
    /** Label for SCUs  */
    private javax.swing.JLabel SCUs;
    /** Radio button for selecting Smartroom  */
    private javax.swing.JRadioButton Smartroom;
    /** Submit Button */
    private javax.swing.JButton Submit;
    /** Field for the WTUs */
    private javax.swing.JLabel WTUs;
    /**Label for required equipment */
    private javax.swing.JLabel jLabel9;
    /**Field for SCUs */
    private javax.swing.JTextField scusField;
    /**Field for wtus */
    private javax.swing.JTextField wtusField;
    /** The hours per week field */
    private javax.swing.JTextField HPWField; 
    /** The type prefix field */
    private javax.swing.JTextField TypePrefixField; 
    /** The hours per week label*/
    private javax.swing.JLabel HoursPerWeekLabel;
    /** The type prefix label*/
    private javax.swing.JLabel TypePrefixLabel; 
    // End of variables declaration//GEN-END:variables

}
