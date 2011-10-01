package edu.calpoly.csc.scheduler.view.desktop.old_view;

import javax.swing.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.Time;

import java.awt.event.*;

import scheduler.view.*;
/**
 * AdvancedFilterUUI is the companion view for the AdvancedFilter 
 * model class. AdvancedFilterUI contains all of the interface components
 * needed to access model components of AdvancedFilter object.
 * @author Sasiluk Ruangrongsorakai (sruangro@calpoly.edu)
 */
public class AdvancedFilterUI extends JFrame {

	
    /** View module: to display the current setting for Advanced Filter*/
	 protected View v;
    
    /** Save the new setting to this Advanced Filter obj*/
    protected AdvancedFilter af;
	 
    /** Time obj to set new start time */
    protected Time newStartTime;
    
    /** Time obj to set new end time */
    protected Time newEndTime;
    
    /** DaysInWeek obj to set new days */
    protected DaysInWeek newDays;
    /**
	 * Construct this by calling initComponents  
	 */
    public AdvancedFilterUI(View view) {
        v = view;
        af = View.advancedFilter;
        newStartTime = View.advancedFilter.getStartTime();
        newEndTime = View.advancedFilter.getEndTime();
        System.out.println("End Time:" + newEndTime);
        newDays = View.advancedFilter.getDays();
        this.setResizable(false);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {

        jPanel1 = new  JPanel();
        jPanel2 = new  JPanel();
        jLabel1 = new  JLabel();
        jLabel2 = new  JLabel();
        jLabel5 = new  JLabel();
        jLabel6 = new  JLabel();
        jLabel7 = new  JLabel();
        jLabel8 = new  JLabel();
        jSeparator1 = new  JSeparator();
        
        startHr  = new JSpinner(new SpinnerNumberModel(12,1,12,1));
        startMin = new JSpinner(new SpinnerNumberModel(0,0,59,1));
        endHr  = new JSpinner(new SpinnerNumberModel(12,1,12,1));
        endMin = new JSpinner(new SpinnerNumberModel(0,0,59,1));
        startAmPm  = new JComboBox(new String[] { "AM", "PM" });
        endAmPm    = new JComboBox(new String[] { "AM", "PM" });
        jCheckBox1 = new  JCheckBox();
        jCheckBox2 = new  JCheckBox();
        jCheckBox3 = new  JCheckBox();
        jCheckBox4 = new  JCheckBox();
        jCheckBox5 = new  JCheckBox();
        jCheckBox6 = new  JCheckBox();
        jCheckBox7 = new  JCheckBox();
        applyButton = new  JButton();
        cancelButton = new  JButton();
        
        updateTime();

        setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE);

        setTitle("Advanced Filter Options");

        jPanel1.setBorder( BorderFactory.createTitledBorder("Select Time"));

        jLabel1.setText("Start Time");

        jLabel2.setText("End Time");


        jLabel5.setText("Hour:");
        jLabel6.setText("Min:");
        jLabel7.setText("Hour:");
        jLabel8.setText("Min:");
        
        /* set the selected start time and start hour*/
        setStartHr(curStartHr);
        setEndHr(curEndHr);


        GroupLayout jPanel1Layout = new  GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addContainerGap(141, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(startHr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(startMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(startAmPm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(16, 16, 16))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addContainerGap(163, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addContainerGap(147, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(endHr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(endMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(endAmPm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel8))
                            .addGap(36, 36, 36))))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addGap(14, 14, 14)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(startHr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(startAmPm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(startMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel2)
                    .addGap(18, 18, 18)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(endHr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(endMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(endAmPm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8))
            );


        jPanel2.setBorder( BorderFactory.createTitledBorder("Select Day"));
        
        try 
        {
           jCheckBox1.setSelected(v.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.MON));
           jCheckBox1.setText("Monday");

           jCheckBox2.setSelected(v.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.TUE));
           jCheckBox2.setText("Tuesday");

           jCheckBox3.setSelected(v.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.WED));
           jCheckBox3.setText("Wednesday");

           jCheckBox4.setSelected(v.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.THU));
           jCheckBox4.setText("Thursday");

           jCheckBox5.setSelected(v.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.FRI));
           jCheckBox5.setText("Friday");

           jCheckBox6.setSelected(v.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.SAT));
           jCheckBox6.setText("Saturday");

           jCheckBox7.setSelected(v.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.SUN));
           jCheckBox7.setText("Sunday");
        }
        catch (NullDayException e)
        {
            System.out.println("Error: isDaySelected from AFUI.java");
        }
        GroupLayout jPanel2Layout = new  GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jCheckBox1)
                        .addComponent(jCheckBox2)
                        .addComponent(jCheckBox3)
                        .addComponent(jCheckBox4)
                        .addComponent(jCheckBox5)
                        .addComponent(jCheckBox6)
                        .addComponent(jCheckBox7))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBox1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jCheckBox2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jCheckBox3)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jCheckBox4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jCheckBox5)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jCheckBox6)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jCheckBox7))
            );
        /* action listener */
        /* action listener */
        applyButton.setText("Apply");
        applyButton.addActionListener(new  ActionListener() {
            public void actionPerformed( ActionEvent evt) {
            	from12to24(0);
            	from12to24(1);
               if ( !View.advancedFilter.setTime(newStartTime,newEndTime) )
            	   System.out.println("Start Time must be before the End Time and End Time must be before 12:00AM");
               else {
	               try 
	               {
	                  newDays.setDay(DaysInWeek.Day.MON,jCheckBox1.isSelected());
	                  newDays.setDay(DaysInWeek.Day.TUE,jCheckBox2.isSelected());
	                  newDays.setDay(DaysInWeek.Day.WED,jCheckBox3.isSelected());
	                  newDays.setDay(DaysInWeek.Day.THU,jCheckBox4.isSelected());
	                  newDays.setDay(DaysInWeek.Day.FRI,jCheckBox5.isSelected());
	                  newDays.setDay(DaysInWeek.Day.SAT,jCheckBox6.isSelected());
	                  newDays.setDay(DaysInWeek.Day.SUN,jCheckBox7.isSelected());
	                  if ( !View.advancedFilter.setDays(newDays) )             //set new days in week to the new af
	                	  System.out.println("Must select at least one day");
	                  else {
	                	  View.advancedFilter.updateObserver();
			              setVisible(false);
			              updateTime();
		            	  setStartHr(curStartHr);
		                  setEndHr(curEndHr);
	                  }
	               }
	               catch (NullDayException e)
	               {
	                  System.out.println("Error setting the days");
	               }
	               
               }
            }
        });

        cancelButton.setText("Cancel");
		cancelButton.addActionListener(new  ActionListener() {
            public void actionPerformed( ActionEvent evt) {
                //reset all the selections?
                
                setVisible(false);// a code to close the window
            }
        });

        startAmPm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String str;
                JComboBox cb = (JComboBox)evt.getSource();
                str = (String)cb.getSelectedItem();
                startPm = str.compareTo("PM") == 0 ? true : false;
            }
        });
        
        endAmPm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String str;
                JComboBox cb = (JComboBox)evt.getSource();
                str = (String)cb.getSelectedItem();
                endPm = str.compareTo("PM") == 0 ? true : false;
            }
        });

        GroupLayout layout = new  GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(applyButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cancelButton))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, 0, 213, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(applyButton))
                    .addContainerGap(29, Short.MAX_VALUE))
            );

        pack();
    }// </editor-fold>                        
    /**
     * @return true if the given int is an PM hour
     */
    private boolean isPM(int hour){
        if ( (hour >= 13 && hour <= 23) )
            return true;
        return false;
    }
    /**
     * Switch from 12 hours to 24 hours format
     * and save it to the startTime and endTime for AF
     * @param startEnd - 0 for setting up startTime, 1 for endTime
     */
    private void from12to24(int startEnd){
    	int tempPm;
    	if ( startEnd == 1){
	    	if ( ((Integer)startHr.getValue() == 12) && !startPm )
	    		tempPm = -12;
	    	else if ( ((Integer)startHr.getValue() == 12) && startPm )
	    		tempPm = 0;
	    	else
	    		tempPm = startPm ? 12 : 0;
	    	
			newStartTime.setHour(((Integer)startHr.getValue())+tempPm);	
			newStartTime.setMinute((Integer)startMin.getValue());
    	}
    	else {
    		if ( ((Integer)endHr.getValue() == 12) && !endPm )
	    		tempPm = -12;
    		else if ( ((Integer)endHr.getValue() == 12) && endPm )
	    		tempPm = 0;
	    	else
	    		tempPm = endPm ? 12 : 0;
			newEndTime.setHour(((Integer)endHr.getValue())+tempPm);	
			newEndTime.setMinute((Integer)endMin.getValue());
    	}
    }
    
    /**
     * converting from 24hours unit to 12 hours unit
     * for end hour
     * @param h - end hour in 24 hours format 
     */
    private void setEndHr(int h){
    	if ( h == 0){
    		endAmPm.setSelectedIndex(0);
    		endHr.setValue(curEndHr+12);
    	}
    	else {
    		if ( isPM(h) )  {
	    		endAmPm.setSelectedIndex(1);
		        endHr.setValue(curEndHr-12);
		        endPm = true;
		    }
    		else if (h == 12){
    			endAmPm.setSelectedIndex(1);
    			endHr.setValue(curEndHr);
    			endPm = true;
    		}
		    else {
		        endAmPm.setSelectedIndex(0);
		        endHr.setValue(curEndHr);
		        endPm = false;
		    }
    	}
    }
    /**
     * converting from 24hours unit to 12 hours unit
     * for start hour
     * @param h - start hour in 24 hours format 
     */
    private void setStartHr(int h){
    	if ( h == 0){
    		startAmPm.setSelectedIndex(0);
    		startHr.setValue(curStartHr+12);
    	}
    	else {
    		if ( isPM(h) )  {
    			startAmPm.setSelectedIndex(1);
    			startHr.setValue(curStartHr-12);
		        startPm = true;
		    }
    		else if (h == 12){
    			startAmPm.setSelectedIndex(1);
    			startHr.setValue(curStartHr);
    			startPm = true;
    		}
		    else {
		    	startAmPm.setSelectedIndex(0);
		    	startHr.setValue(curStartHr);
		    	startPm = false;
		    }
    	}
    }
    
    private void updateTime(){
    	curStartHr = v.getAdvancedFilter().getStartTime().getHour();
        curStartMin = v.getAdvancedFilter().getStartTime().getMinute();
        curEndHr = v.getAdvancedFilter().getEndTime().getHour();
        curEndMin = v.getAdvancedFilter().getEndTime().getMinute();
        startHr.setValue(curStartHr);
		startMin.setValue(curStartMin);
		endHr.setValue(curEndHr);
		endMin.setValue(curEndMin);
    }
    // Variables declaration - do not modify                     
    private  JButton applyButton;
    private  JButton cancelButton;
    private  JCheckBox jCheckBox1;
    private  JCheckBox jCheckBox2;
    private  JCheckBox jCheckBox3;
    private  JCheckBox jCheckBox4;
    private  JCheckBox jCheckBox5;
    private  JCheckBox jCheckBox6;
    private  JCheckBox jCheckBox7;
    private  JSpinner startHr;
    private  JSpinner startMin;
    private  JComboBox startAmPm;
    private  JSpinner endHr;
    private  JSpinner endMin;
    private  JComboBox endAmPm;
    private  JLabel jLabel1;
    private  JLabel jLabel2;
    private  JLabel jLabel5;
    private  JLabel jLabel6;
    private  JLabel jLabel7;
    private  JLabel jLabel8;
    private  JPanel jPanel1;
    private  JPanel jPanel2;
    private  JSeparator jSeparator1;
    private  int curStartHr;
    private  int curStartMin;
    private  int curEndHr;
    private  int curEndMin;
    private  boolean startPm;
    private  boolean endPm;
    // End of variables declaration                   

}
