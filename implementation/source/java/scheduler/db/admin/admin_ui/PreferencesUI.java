package scheduler.db.admin.admin_ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.pdb.DaysForClasses;
import edu.calpoly.csc.scheduler.model.db.pdb.NoClassOverlap;
import edu.calpoly.csc.scheduler.model.db.pdb.PreferencesDB;
import edu.calpoly.csc.scheduler.model.db.pdb.PreferencesDB.PreferenceExistsException;
import edu.calpoly.csc.scheduler.view.desktop.MyView;

import scheduler.*;
import scheduler.generate.*;

import scheduler.db.*;
import scheduler.db.coursedb.*;
import scheduler.db.admin.admin_ui.DayTab;
import scheduler.db.preferencesdb.*;
/**
 *
 * @author alindt
 */
public class PreferencesUI extends MyView implements Observer {

   private PreferencesDB pdb;

   public PreferencesUI() {      
      initVars();
      setDataStructures();
      setTitles();
      formPanel();
      setListeners();
   }
    
   public void update(Observable o, Object arg) {
      setDataStructures();
   }

   private void initVars() {
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      dayTab = new DayTab();
      overlapTab = new OverlapTab();
      viewButton = new JButton();
      deleteButton = new JButton();
      helpButton = new JButton();
      prefScrollPane = new JScrollPane();
      pList = new JList();
      frameTitle = new JLabel();
      prefTabs = new JTabbedPane();
   }

   private void setDataStructures() {
      pdb = Scheduler.pdb;
      pdb.addObserver(this);

      final ArrayList<String> names = new ArrayList<String>();
      final ArrayList<String> pListNames = new ArrayList<String>();
      Vector<DaysForClasses> vDfc = pdb.getLocalDaysForClasses();
      Vector<NoClassOverlap> vNco = pdb.getLocalNoClassOverlaps();

      Collections.sort(vDfc);
      Collections.sort(vNco);

      if(vDfc != null) {
         for (DaysForClasses dfc: vDfc) {
            names.add(dfc.name);
         }
      }
      if(vNco != null) {
         for (NoClassOverlap nco: vNco) {
            names.add(nco.name);
         }
      }

      pList.setModel(new AbstractListModel() {
         ArrayList<String> courses = names;
         public int getSize() { return courses.size(); }
         public Object getElementAt(int i) { return courses.get(i); }
      });

      prefScrollPane.setViewportView(pList);
   }

   private void setTitles() {
      this.setTitle("Preferences");
      viewButton.setText("View");
      deleteButton.setText("Delete");
      helpButton.setText("?");      

      frameTitle.setFont(new Font("Serif", Font.BOLD, 24));
      frameTitle.setText("Create Preferences");
   }

    private void formPanel() {
      Box entireFrame, leftGroup, leftGroupButtons, rightGroup, titleBox;

      entireFrame = Box.createHorizontalBox();
      leftGroup = Box.createVerticalBox();
      leftGroupButtons = Box.createHorizontalBox();
      rightGroup = Box.createVerticalBox();
      titleBox = Box.createHorizontalBox();

      leftGroupButtons.add(viewButton);
      leftGroupButtons.add(deleteButton);
      leftGroupButtons.add(helpButton);

      leftGroup.add(prefScrollPane);
      leftGroup.add(leftGroupButtons);

      leftGroup.setPreferredSize(new Dimension(200,450));

      prefTabs.addTab("Days", dayTab.getDayTab());
      prefTabs.addTab("Overlap", overlapTab.getOverlapTab()); 

      titleBox.add(Box.createGlue());
      titleBox.add(frameTitle);
      titleBox.add(Box.createGlue());

      rightGroup.add(titleBox);
      rightGroup.add(prefTabs);

      rightGroup.setPreferredSize(new Dimension(400,450));

      entireFrame.add(leftGroup);
      entireFrame.add(new JSeparator(JSeparator.VERTICAL));
      entireFrame.add(rightGroup);

      entireFrame.setPreferredSize(new Dimension(600,450));

      this.add(entireFrame);

      pack();
    }

   private void setListeners() {

   }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PreferencesUI().setVisible(true);
            }
        });
    }


   private DayTab dayTab;
   private OverlapTab overlapTab;
   private JButton viewButton, deleteButton, helpButton;
   private JScrollPane prefScrollPane;
   private JList pList;
   private JLabel frameTitle;
   private JTabbedPane prefTabs;



}
