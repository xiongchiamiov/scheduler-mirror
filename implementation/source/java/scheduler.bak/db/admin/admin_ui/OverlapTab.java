package scheduler.db.admin.admin_ui;



import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import scheduler.*;
import scheduler.generate.*;
import scheduler.db.*;
import scheduler.db.coursedb.*;
import scheduler.db.preferencesdb.*;
import scheduler.db.preferencesdb.PreferencesDB.PreferenceExistsException;

public class OverlapTab extends MyView implements Observer {

   private CourseDB cdb;

   public OverlapTab() {
      initVars();
      setDataStructures();
      setTitles();
      formPanel();
      setListeners();
   }

   public void update(Observable obs, Object obj) {
      setDataStructures();
   }

   private void initVars() {
      selectedCourses = new ArrayList<String>();
      overlapPanel = new JPanel();
      cList = new JList();
      sList = new JList();
      cScrollPane = new JScrollPane();
      sScrollPane = new JScrollPane();
      cListLabel = new JLabel();
      sListLabel = new JLabel();
      leftArrow = new JButton();
      rightArrow = new JButton();
      resetButton = new JButton();
      createButton = new JButton();
   }

   private void setDataStructures() {
      cdb = Scheduler.cdb;
      cdb.addObserver(this);

      final ArrayList<String> names = new ArrayList<String>();
      final ArrayList<String> sListNames = new ArrayList<String>();
      ArrayList<Course> data = (ArrayList) cdb.getLocalData();
      Collections.sort((java.util.List)data);

      if(data != null) {
         for (Course c: data) {
            if(c.getCourseType().equals("Lab")) {}
            else {
               if( selectedCourses.contains(c.toString()) ) {
                  sListNames.add(c.toString());
               } else {
                  names.add(c.toString());
               }
            }
         }
      }

      cList.setModel(new AbstractListModel() {
         ArrayList<String> courses = names;
         public int getSize() { return courses.size(); }
         public Object getElementAt(int i) { return courses.get(i); }
      });

      sList.setModel(new AbstractListModel() {
         ArrayList<String> courses = sListNames;
         public int getSize() { return courses.size(); }
         public Object getElementAt(int i) { return courses.get(i); }
      });

      sScrollPane.setViewportView(sList);
      cScrollPane.setViewportView(cList);
   }

   private void setTitles() {
      leftArrow.setText("<-");
      rightArrow.setText("->");

      cListLabel.setText("Courses");
      sListLabel.setText("Selected Courses");

      resetButton.setText("Reset Fields");
      createButton.setText("Create");
   }

   private void formPanel() {
      Box titleBox,cBox,arrowBox,sBox,buttonBox;
      Box middle, everything;

      titleBox = Box.createHorizontalBox();
      cBox = Box.createHorizontalBox();
      arrowBox = Box.createVerticalBox();
      sBox = Box.createHorizontalBox();
      buttonBox = Box.createHorizontalBox();
      middle = Box.createHorizontalBox();
      everything = Box.createVerticalBox();

      
      titleBox.add(cListLabel);
      titleBox.add(Box.createHorizontalStrut(100));
      titleBox.add(sListLabel);

      cScrollPane.setPreferredSize(new Dimension(75,100));
      sScrollPane.setPreferredSize(new Dimension(75,100));

      cBox.add(cScrollPane);
      arrowBox.add(rightArrow);
      arrowBox.add(Box.createVerticalStrut(35));
      arrowBox.add(leftArrow);
      sBox.add(sScrollPane);

      middle.add(cBox);
      middle.add(Box.createHorizontalStrut(15));
      middle.add(arrowBox);
      middle.add(Box.createHorizontalStrut(15));
      middle.add(sBox);
      middle.add(Box.createGlue());

      buttonBox.add(createButton);
      buttonBox.add(Box.createHorizontalStrut(100));
      buttonBox.add(resetButton);

      everything.add(Box.createVerticalStrut(25));
      everything.add(titleBox);
      everything.add(Box.createVerticalStrut(20));
      everything.add(middle);
      everything.add(Box.createVerticalStrut(20));
      everything.add(buttonBox);
      everything.add(Box.createVerticalStrut(25));

      overlapPanel.add(everything);
   }

   private void setListeners() {
     leftArrow.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            leftArrowClicked(evt);
         }
     });
     rightArrow.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            rightArrowClicked(evt);
         }
     });
   }

   private void leftArrowClicked(ActionEvent evt) {
      int[] indices = sList.getSelectedIndices();

      for(int i = indices.length - 1 ; i >= 0 ; i--) {
         selectedCourses.remove( (String) sList.getModel().getElementAt(indices[i]) );
      }

      setDataStructures();
   }

   private void rightArrowClicked(ActionEvent evt) {
      int[] indices = cList.getSelectedIndices();

      for(int i = 0; i < indices.length ; i++) {
         selectedCourses.add( (String) cList.getModel().getElementAt(indices[i]) );
      }

      setDataStructures();
   }


   public JPanel getOverlapTab() {
      return overlapPanel;
   }

   private ArrayList<String> selectedCourses;
   private JPanel overlapPanel;
   private JList cList,sList;
   private JLabel cListLabel, sListLabel;
   private JButton leftArrow,rightArrow;
   private JButton resetButton, createButton;
   private JScrollPane cScrollPane,sScrollPane;

}
