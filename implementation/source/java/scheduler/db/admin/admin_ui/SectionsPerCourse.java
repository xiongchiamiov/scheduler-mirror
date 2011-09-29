package scheduler.db.admin.admin_ui;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.view.desktop.MyView;
import scheduler.*;

/**
 * The class for the sections per course dialog.
 * @author Jan Lorenz Soliman
 */
public class SectionsPerCourse extends MyView {

    public static int ROWS = 100;

    /** Course database */
    private CourseDB database = Scheduler.cdb;
    /** Creates new form AddCourse */
    private Course course = new Course("",0,0,0,"",0,0,null,null, "", null);

    /** Creates new form SectionsPerCourse */
    public SectionsPerCourse() {
        initComponents();
        setTitle("Sections Per Course (Local Copy Only)");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public Vector<Vector> getTable() {
        ArrayList<Vector> tabledata = new ArrayList<Vector>();
        ArrayList<Course> data = (ArrayList) database.getLocalData();
        Iterator iterator = data.iterator();

        while (iterator.hasNext()) {
           Vector<String> row = new Vector<String>();
           Course c = (Course)iterator.next();
           if (c.getCourseType().contains("Lecture")) {
               row.add("CPE" + c.getId()) ;
           }
           else {
               row.add("CPE" + c.getId() + "L") ;
           }
           row.add(c.getSection() + "") ;
           tabledata.add(row);
        }
        Vector<Vector> returnVal = new Vector(tabledata );
        return returnVal;
    }

    /** 
     *  The method to initialize the components.
     **/ 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Submit = new javax.swing.JButton();
        Cancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        //String[][] = new String[][];
        Vector<Vector> tabledata = getTable();
        Vector<String> names = new Vector<String>();
        names.add("Course");
        names.add("# of Sections");
        DefaultTableModel tableModel = new DefaultTableModel(tabledata, names )
        {
            public boolean isCellEditable(int row, int column ){
               return (column != 0);
            }
        };
        jTable1.setModel(tableModel  );
        jScrollPane1.setViewportView(jTable1);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Submit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cancel)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Submit)
                    .addComponent(Cancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     *  Method invoked when the Cancel Button is pressed
     *  @param evt The action event.
     **/
    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        this.setVisible(false);
    }

    /**
     *  Method invoked when the Submist Button is pressed
     *  @param evt The action event.
     **/
    private void SubmitActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        Vector<Vector> tableData;
        DefaultTableModel tblModel = (DefaultTableModel)jTable1.getModel();
        tableData = tblModel.getDataVector();
        try {
           database.changeSections(tableData);
           this.setVisible(false);
        }
        catch (NumberFormatException n) {
            JOptionPane.showMessageDialog(this,
               "One of more fields is not an integer.",
               "Error",
               JOptionPane.ERROR_MESSAGE);

        }
        //this.database.changeSections("", 0);
    }
    /**
     * The main method to set the GUI as visible.
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SectionsPerCourse().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    /**Cancel button */
    private javax.swing.JButton Cancel;
    /**Submit button */
    private javax.swing.JButton Submit;
    /**Panel for scrolling */
    private javax.swing.JScrollPane jScrollPane1;
    /**Tables for sections and courses  */
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
