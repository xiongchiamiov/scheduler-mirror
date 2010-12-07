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
   private PreferencesDB pdb;

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
      prefLabel = new JLabel();
      prefNameField = new JTextField();      
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

      pdb = Scheduler.pdb;
      pdb.addObserver(this);

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
      prefLabel.setText("Preference Title:");
   
      leftArrow.setText("<-");
      rightArrow.setText("->");

      cListLabel.setText("Courses");
      sListLabel.setText("Selected Courses");

      resetButton.setText("Reset Fields");
      createButton.setText("Create");
   }

   private void formPanel() {
      GridBagLayout grid = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      overlapPanel.setLayout(grid);

      Box nameFieldBox, titleBox,cBox,arrowBox,sBox,buttonBox;
      Box middle, everything;

      nameFieldBox = Box.createHorizontalBox();
      titleBox = Box.createHorizontalBox();
      cBox = Box.createHorizontalBox();
      arrowBox = Box.createVerticalBox();
      sBox = Box.createHorizontalBox();
      buttonBox = Box.createHorizontalBox();
      middle = Box.createHorizontalBox();
      everything = Box.createVerticalBox();

      nameFieldBox.add(prefLabel);
      nameFieldBox.add(prefNameField);
      
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

      buttonBox.add(createButton);
      buttonBox.add(Box.createHorizontalStrut(100));
      buttonBox.add(resetButton);

      everything.add(Box.createVerticalStrut(25));
      everything.add(nameFieldBox);
      everything.add(Box.createVerticalStrut(15));
      everything.add(titleBox);
      everything.add(Box.createVerticalStrut(15));
      everything.add(middle);
      everything.add(Box.createVerticalStrut(15));
      everything.add(buttonBox);
      everything.add(Box.createVerticalStrut(25));

      c.fill = GridBagConstraints.NONE;
      c.anchor = GridBagConstraints.CENTER;
      c.gridwidth = GridBagConstraints.REMAINDER;

      overlapPanel.add(everything, c);
   }

   private void setListeners() {
     createButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            makeOverlapPreference(evt);
         }
     });
     resetButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            resetOverlapPreference(evt);
         }
     });
     prefNameField.addKeyListener(new KeyListener() {
         public void keyPressed(KeyEvent keyEvent) {}
         public void keyReleased(KeyEvent keyEvent) { validateOverlapTitle(keyEvent); }
         public void keyTyped(KeyEvent keyEvent) {}
     });
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

    private void validateOverlapTitle(KeyEvent evt) {
      prefLabel.setForeground(Color.BLACK);
    }

   private void makeOverlapPreference(ActionEvent evt) {
      String title = prefNameField.getText().trim();
      String error_dialog = "";
      int error_count = 0;
      
      ArrayList<Course> cannotOverlap = new ArrayList<Course>();
      ArrayList<Course> data = (ArrayList) cdb.getLocalData();
      Collections.sort((java.util.List)data);

      if(data != null) {
         for (Course c: data) {
            if(c.getCourseType().equals("Lab")) {}
            else {
               if( selectedCourses.contains(c.toString()) ) {
                  cannotOverlap.add(c);
               }
            }
         }
      }

      if( title.equals("") ) {
         prefLabel.setForeground(Color.RED);
         error_dialog += (++error_count) + ". Cannot Create a preference without a title.\n";
      }
      else if( checkForDuplicateTitle(title) ) {
         prefLabel.setForeground(Color.RED);
         error_dialog += (++error_count) + ". That title already exists.\n";
      }

      if(cannotOverlap.isEmpty() || cannotOverlap.size() < 2) {
         sListLabel.setForeground(Color.RED);
         error_dialog += (++error_count) + ". Two or more courses must be selected "
                                            + "to form a valid preference.\n";
      }

      if(error_count == 0) {
         try {
            Scheduler.pdb.addLocalNoClassOverlap(new NoClassOverlap(title,
                                                                    5,
                                                                    cannotOverlap));
                                                                    
            resetFields();
         }
         catch (PreferenceExistsException e) {
            System.err.println ("A redundant check somehow failed the " + 
                                "second time");
            e.printStackTrace();
         }
      } 
      else {
         String error_title = "Overlap Preference Error";
         if(error_count > 1) { error_title += "s"; }
         JOptionPane.showMessageDialog(null,
                                       error_dialog,
                                       error_title,
                                       JOptionPane.ERROR_MESSAGE);
      }
   }

   private boolean checkForDuplicateTitle(String title) {
      boolean duplicateExists = false;

      Vector<DaysForClasses> vDfc = pdb.getLocalDaysForClasses();
      Vector<NoClassOverlap> vNco = pdb.getLocalNoClassOverlaps();

      if(vDfc != null) {
         for (DaysForClasses dfc: vDfc) {
            if(dfc.name.equals(title)) {
               duplicateExists = true;
            }
         }
      }
      if(vNco != null) {
         for (NoClassOverlap nco: vNco) {
            if(nco.name.equals(title)) {
               duplicateExists = true;
            }
         }
      }

      return duplicateExists;
   }
   
   private void resetOverlapPreference(ActionEvent evt) {
      resetFields();
   }
   
   private void resetFields() {
      prefNameField.setText("");
      for(int i = selectedCourses.size() - 1 ; i >= 0 ; i--) {
         selectedCourses.remove( (String) sList.getModel().getElementAt(i) );
      }
      
      setDataStructures();   
   }
   
   private void leftArrowClicked(ActionEvent evt) {
      int[] indices = sList.getSelectedIndices();

      for(int i = indices.length - 1 ; i >= 0 ; i--) {
         selectedCourses.remove( (String) sList.getModel().getElementAt(indices[i]) );
      }

      setDataStructures();
   }

   private void rightArrowClicked(ActionEvent evt) {
      sListLabel.setForeground(Color.BLACK);
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
   private JTextField prefNameField;
   private JLabel prefLabel, cListLabel, sListLabel;
   private JButton leftArrow,rightArrow;
   private JButton resetButton, createButton;
   private JScrollPane cScrollPane,sScrollPane;

}
