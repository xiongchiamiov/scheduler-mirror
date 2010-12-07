package scheduler.db.admin.admin_ui;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import scheduler.*;
import scheduler.db.coursedb.CourseDB;
import scheduler.db.coursedb.Course;
import scheduler.menu.schedule.*;
import javax.swing.*;
import javax.swing.text.*;
import scheduler.db.preferencesdb.DaysForClasses;

/**
 * The class for the add course dialog.
 *
 * @author Jan Lorenz Soliman
 */
public class AddCourse extends MyView implements Observer {

    /** Course database */
    protected CourseDB database = new CourseDB();
    /** Individual course */
    private Course course = new Course("",0,0,0,"",0,0,null,null, "", null);

    /** Creates new form AddCourse
     * */
    public AddCourse(CourseDB coursedb ) {
        this.database = coursedb;
        initComponents();
        Scheduler.pdb.addObserver(this);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setTitle("Add a Course");
    }

    /** Updates the view when the database changes
     *  @param obs The observable object
     *  @param obj The argument passed from the observable
     **/
    public void update(Observable obs, Object obj) {
        PreferenceBox.setModel(new javax.swing.DefaultComboBoxModel(Scheduler.pdb.getDayPreferences()));
    }

    /** Initializes the GUI */
    public void initialize() {
        NameField.setText("");
        CourseNumberField.setText("");
        wtusField.setText("");
        scusField.setText("");
        MaxEnrollmentField.setText("");
        ClassTypeField.setSelectedIndex(0);
        Smartroom.setSelected(false);
        Overhead.setSelected(false);
        LaptopConnectivity.setSelected(false);
        DefaultComboBoxModel boxmodel = new DefaultComboBoxModel(database.getLabNames() );
        LabPairingField.setModel(boxmodel );
    }


    public class JTextFieldLimit extends PlainDocument {  
           private int limit;  
           private boolean toUppercase = false;  
             
           JTextFieldLimit(int limit) {  
            super();  
            this.limit = limit;  
            }  
            
           public void insertString  
             (int offset, String  str, AttributeSet attr)  
               throws BadLocationException {  
            if (str == null) return;  
   
            if ((getLength() + str.length()) <= limit) {  
              if (toUppercase) str = str.toUpperCase();  
              super.insertString(offset, str, attr);  
              }  
            }  
         }     


    /**
     *  Method that initializes the GUI
     *
     **/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NameField = new javax.swing.JTextField();
        CourseNumberField = new javax.swing.JTextField();
        CourseNumberField.setDocument(new JTextFieldLimit(3));
        wtusField = new javax.swing.JTextField();
        scusField = new javax.swing.JTextField();
        Name = new javax.swing.JLabel();
        CourseNumber = new javax.swing.JLabel();
        WTUs = new javax.swing.JLabel();
        SCUs = new javax.swing.JLabel();
        ClassTypeField = new javax.swing.JComboBox();
        ClassType = new javax.swing.JLabel();
        MaxEnrollmentField = new javax.swing.JTextField();
        MaxEnrollment = new javax.swing.JLabel();
        OptionalFields = new javax.swing.JLabel();
        LabPairing = new javax.swing.JLabel();
        LabPairingField = new javax.swing.JComboBox();
        Smartroom = new javax.swing.JRadioButton();
        Overhead = new javax.swing.JRadioButton();
        LaptopConnectivity = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        Submit = new javax.swing.JButton();
        Cancel = new javax.swing.JButton();
        Prefix = new javax.swing.JLabel();
        PrefixBox = new javax.swing.JComboBox();
        Preference = new javax.swing.JLabel();
        PreferenceBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        Name.setText("Catalog Title");

        CourseNumber.setText("Course #");

        WTUs.setText("WTUs");

        SCUs.setText("SCUs");

        ClassTypeField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Lecture", "Lab" }));
        
        DefaultComboBoxModel boxmodel = new DefaultComboBoxModel(database.getLabNames() );
        LabPairingField.setModel(boxmodel );
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Prefix)
                        .addContainerGap(254, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(MaxEnrollment)
                        .addContainerGap(190, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Submit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Cancel)
                        .addContainerGap(170, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(LaptopConnectivity)
                        .addContainerGap(139, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Overhead)
                        .addContainerGap(203, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Smartroom)
                        .addContainerGap(191, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addContainerGap(160, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(OptionalFields)
                        .addContainerGap(196, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(WTUs)
                            .addComponent(CourseNumber)
                            .addComponent(SCUs)
                            .addComponent(Name)
                            .addComponent(ClassType)
                            .addComponent(LabPairing)
                            .addComponent(Preference))
                        .addGap(97, 97, 97)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PreferenceBox, 0, 101, Short.MAX_VALUE)
                            .addComponent(LabPairingField, 0, 101, Short.MAX_VALUE)
                            .addComponent(NameField, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(scusField, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(wtusField, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(CourseNumberField, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(PrefixBox, 0, 101, Short.MAX_VALUE)
                            .addComponent(ClassTypeField, 0, 101, Short.MAX_VALUE)
                            .addComponent(MaxEnrollmentField, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                        .addGap(21, 21, 21))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
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
                .addGap(39, 39, 39))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    /**
     *  Method performed when the cancel button is pressed
     *  @param evt The event performed.
     **/
    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_CancelActionPerformed



    /**
     *  Method performed when the submit button is pressed
     *  @param evt The event performed
     **/
    private void SubmitActionPerformed (java.awt.event.ActionEvent evt) {
        System.out.println ("Submitting");
        boolean aPt, bPt, cPt, dPt;
        aPt = false; bPt = false; cPt = false; dPt = false;
        try {
        Course.RequiredEquipment equip = course.new RequiredEquipment (Smartroom.isSelected(), Overhead.isSelected(), LaptopConnectivity.isSelected()  );
        String labSelected =  (String)LabPairingField.getSelectedItem();
        Course lab = null;
           if (!labSelected.equals("")) {
           labSelected = labSelected.substring(3, 6);
           //System.out.println ("Labselected is " + labSelected);
           int id = Integer.parseInt(labSelected);
           lab = database.getCourse(id, "Lab");
           //System.out.println("Lab is " + lab);
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

           //String dfcName = (String)this.PreferenceBox.getSelectedItem();
           //System.out.println("DFCNAME is " + dfcName);
           //DaysForClasses dfc = Scheduler.pdb.getDaysForClassesByName(dfcName);
           DaysForClasses dfc = (DaysForClasses)this.PreferenceBox.getSelectedItem();
           
           this.course = new Course(NameField.getText() , Integer.parseInt(CourseNumberField.getText() ) , Integer.parseInt(wtusField.getText()), Integer.parseInt(scusField.getText()), (String)ClassTypeField.getSelectedItem(), Integer.parseInt(MaxEnrollmentField.getText()), 1, lab, equip,  (String)PrefixBox.getSelectedItem(), dfc );
               if (database.getData() == null  || !this.database.getData().contains(course )) {
                  try {
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
                        this.database.addCourse(course);
                        System.out.println ("Removing window.");
                        this.setVisible(false);
                     }
                  }
                  catch (CourseDB.CourseExistsException e) {
                        JOptionPane.showMessageDialog(this,
                           "The Course is already in the database.",
                           "Error",
                           JOptionPane.ERROR_MESSAGE); 
                  }
               }
              else {
                  System.out.println("Course is already in the database.");
                  String msg = "Course is already in the database.";
                  JOptionPane.showMessageDialog(this, msg, "Error", 
                  JOptionPane.ERROR_MESSAGE);
              }
        }
        catch ( NumberFormatException e) {
            String message = "";
            if (dPt) {
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
     * The main method to create the window.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new AddCourse().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    /** Cancel button */
    private javax.swing.JButton Cancel;
    /** Label for the Classtype field */
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
    /** Label for prefix */
    private javax.swing.JLabel Prefix;
    /** Box for selecting prefix */
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
    // End of variables declaration//GEN-END:variables

}
