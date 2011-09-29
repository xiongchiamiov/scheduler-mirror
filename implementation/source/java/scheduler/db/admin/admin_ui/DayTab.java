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
import edu.calpoly.csc.scheduler.model.schedule.Week;

import scheduler.*;
import edu.calpoly.csc.scheduler.model.schedule.*;

import scheduler.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import scheduler.db.preferencesdb.*;

public class DayTab implements Observer {
   
   private PreferencesDB pdb;
   
   public DayTab() {
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
      dayPanel = new JPanel();
      prefLabel = new JLabel();
      dayComboLabel = new JLabel();
      prefNameField = new JTextField();
      resetButton = new JButton();
      createButton = new JButton();
      m_toggle = new JToggleButton();
      t_toggle = new JToggleButton();
      w_toggle = new JToggleButton();
      r_toggle = new JToggleButton();
      f_toggle = new JToggleButton();
   }

   private void setDataStructures() {
      pdb = Scheduler.pdb;
      pdb.addObserver(this);
   }
   
   private void setTitles() {
      prefLabel.setText("Preference Title:");
      dayComboLabel.setText("Day Combination:");
      
      m_toggle.setText("M");
      t_toggle.setText("T");
      w_toggle.setText("W");
      r_toggle.setText("R");
      f_toggle.setText("F");

      resetButton.setText("Reset Fields");
      createButton.setText("Create");
   }
   
   private void formPanel() {
      GridBagLayout grid = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      setRowMinHeight(grid, 3, 80);
      dayPanel.setLayout(grid);
      
      c.gridheight = 2;
      dayPanel.add(prefLabel,c);

      c.gridwidth = GridBagConstraints.REMAINDER;
      c.fill = GridBagConstraints.HORIZONTAL;
      dayPanel.add(prefNameField,c);
      c.gridwidth = 1;
      
      dayPanel.add(dayComboLabel,c);

      dayPanel.add(m_toggle,c);
      dayPanel.add(t_toggle,c);
      dayPanel.add(w_toggle,c);
      dayPanel.add(r_toggle,c);

      c.gridwidth = GridBagConstraints.REMAINDER;
      dayPanel.add(f_toggle,c);
      c.gridwidth = 1;

      c.gridheight = 4;
      c.fill = GridBagConstraints.NONE;
      c.anchor = GridBagConstraints.CENTER;
      dayPanel.add(createButton,c);

      c.fill = GridBagConstraints.NONE;
      c.anchor = GridBagConstraints.CENTER;
      c.gridwidth = GridBagConstraints.REMAINDER;
      dayPanel.add(resetButton,c);
   }

   
   private void setListeners() {
     createButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            makeDayPreference(evt);
         }
     });
     resetButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            resetDayPreference(evt);
         }
     });
     prefNameField.addKeyListener(new KeyListener() {
         public void keyPressed(KeyEvent keyEvent) {}
         public void keyReleased(KeyEvent keyEvent) { validateDayTitle(keyEvent); }
         public void keyTyped(KeyEvent keyEvent) {}
     });
     
     m_toggle.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            dayAction(evt, DAY.MONDAY);
         }
     });
     t_toggle.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            dayAction(evt, DAY.TUESDAY);
         }
     });
     w_toggle.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            dayAction(evt, DAY.WEDNESDAY);
         }
     });
     r_toggle.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            dayAction(evt, DAY.THURSDAY);
         }
     });
     f_toggle.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            dayAction(evt, DAY.FRIDAY);
         }
     });     
   }

   public void setColumnMinWidth(GridBagLayout gbl, int c, int w) {
      int[] ws = gbl.columnWidths;
      
      if (ws == null) {
         ws = new int[c+1];
      } else if (ws.length < c+1) {
         ws = new int[c+1]; 
         System.arraycopy(gbl.columnWidths, 0, ws, 0, gbl.columnWidths.length);
      } 

      ws[c] = w;
      gbl.columnWidths = ws;
   }

   public void setRowMinHeight(GridBagLayout gbl, int r, int h) {
      int[] hs = gbl.rowHeights;

      if (hs == null) {
         hs = new int[r+1];
      } else if (hs.length < r+1) {
         hs = new int[r+1];
         System.arraycopy(gbl.rowHeights, 0, hs, 0, gbl.rowHeights.length);
      } 

      hs[r] = h;
      gbl.rowHeights = hs;
   } 

    /**
     * Action listener for the "DaysOfWeek" preference "create"
     * button. This will attempt to create the preference and
     * if there is any reason it cannot, an error message will
     * appear stating all the reasons that the preference was
     * not possible to create. Title texts for invalid fields
     * will additionally turn red until any change is made to
     * that field.
     *
     * Pre: All DaysOfWeek fields are filled in with valid data
     *      ---Title : Any string with at least 1 character of non
     *                 white space. It must also not be a currently
     *                 existing preference.
     *      ---Days  : At least one day must be selected for the
     *                 preference.
     *      ---Weight: Must be an integer between 1 and 10 inclusive
     *
     * Post: All fields are cleared and a DaysOfWeek preference is
     *       added to the database matching the given input.
     *
     */   
   private void makeDayPreference(ActionEvent evt) {
     Week week_pref = new Week();
     String error_dialog = "";
     String title = prefNameField.getText().trim();
     int weight = -1, error_count = 0;

     if( title.equals("") ) {
         prefLabel.setForeground(Color.RED);
         error_dialog += (++error_count) + ". Cannot Create a preference without a title.\n";
     }
     else if( checkForDuplicateTitle(title) ) {
         prefLabel.setForeground(Color.RED);
         error_dialog += (++error_count) + ". That title already exists.\n";
     }

     if( !M && !T && !W && !R && !F ) {
         dayComboLabel.setForeground(Color.RED);
         error_dialog += (++error_count) + ". One or more days must be selected.\n";
     } else {
         if(M){week_pref.add(Week.MON);}     if(T){week_pref.add(Week.TUE);}
         if(W){week_pref.add(Week.WED);}     if(R){week_pref.add(Week.THU);}
         if(F){week_pref.add(Week.FRI);}
     }
     
     if(error_count == 0) 
     {
         try
         {
            Scheduler.pdb.addLocalDaysForClasses(new DaysForClasses(title,
                                                                    weight,
                                                                    week_pref));
         }
         catch (PreferenceExistsException e)
         {
            System.err.println ("A redundant check somehow failed the " + 
                                "second time");
            e.printStackTrace();
         }
         
         resetFields();
     } else {
         String error_title = "Day Preference Error";
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

   private void resetDayPreference(ActionEvent evt) {
      resetFields();
   }
   
   public void resetFields() {
      prefNameField.setText("");
      m_toggle.setSelected(false);
      t_toggle.setSelected(false);
      w_toggle.setSelected(false);
      r_toggle.setSelected(false);
      f_toggle.setSelected(false);
   
      prefLabel.setForeground(Color.BLACK);
      dayComboLabel.setForeground(Color.BLACK);
   }
   
    /**
     * If any action is done to the DayTitle textfield
     * then set the text for that field to be the
     * default color, black.
     */ 
    private void validateDayTitle(KeyEvent evt) {
      prefLabel.setForeground(Color.BLACK);
    }

 
   private void dayAction(ItemEvent evt, DAY day) {
      if(evt.getStateChange() == ItemEvent.SELECTED) {
         switch (day) {
            case MONDAY: M = true; break;
            case TUESDAY: T = true; break;
            case WEDNESDAY: W = true; break;
            case THURSDAY: R = true; break;
            case FRIDAY: F = true; break;
         }
         if(dayComboLabel.getForeground().equals(Color.RED)) {
            dayComboLabel.setForeground(Color.BLACK);
         }
      } else {
         switch (day) {
            case MONDAY: M = false; break;
            case TUESDAY: T = false; break;
            case WEDNESDAY: W = false; break;
            case THURSDAY: R = false; break;
            case FRIDAY: F = false; break;
         }
      }
   }


   public JPanel getDayTab() {
      return dayPanel;
   }
   
   private enum DAY { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY };
   private boolean M, T, W, R, F;
   private JPanel dayPanel;
   private JLabel prefLabel, dayComboLabel;
   private JTextField prefNameField;
   private JToggleButton m_toggle, t_toggle, w_toggle, r_toggle, f_toggle;
   private JButton resetButton,createButton;
   

}
