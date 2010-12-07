package scheduler.view.view_ui;

import scheduler.*;
import scheduler.view.*;
import scheduler.fair_qual.fair_qual_ui.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
/****
 *
 * Initial version of ViewUI class that constructs and composes its pulldown
 * menu and some dialogs.  Construction and composition of remaining View UI
 * dialogs is forthcoming.
 *
 * @author Aaron Rivera
 */

public class ViewUI {

    public ViewUI(View view) {
       // super(screen, view);
        advancedFilterUI = new AdvancedFilterUI(view);
   //     itemEditor = new ItemEditor(this);
    //    appointmentEditor = new AppointmentEditor(screen, schedule, calToolUI);
     //   meetingEditor = new MeetingEditor(screen, schedule, calToolUI);
     //   taskEditor = new TaskEditor(screen, schedule, calToolUI);
     //   eventEditor = new EventEditor(screen, schedule, calToolUI);
     //   monthlyAgendaDisplay = new MonthlyAgendaDisplay(screen,
     //       view.viewMonth());
     //   appointmentsListDisplay = new AppointmentsListDisplay(screen,
     //       view.getLists(), calToolUI);
    }

    public Component compose() {
//        appointmentEditor.compose();
  //      meetingEditor.compose();
  //      taskEditor.compose();
  //      eventEditor.compose();
        //      monthlyAgendaDisplay.compose();
        //      appointmentsListDisplay.compose();
        advancedFilterButton = new JButton("Advanced Filters");
        advancedFilterButton.addActionListener(
         new ActionListener() {
             public void actionPerformed(ActionEvent ev) {
                 ViewUI.getAdvancedFilterView().setVisible(true);
             }
         }
        );
        
        return viewMenu.compose();
    }

    /**
     * Returns the UI button that opens advanded filters.
     */
    public static JButton getAdvancedFilterButton() {
        return advancedFilterButton;
    }

    public JFrame getCourseView() {
        return ViewSettingsUI.getNewCourseViewSettingsUI(view);
    }
    
    public JFrame getLocationView() {
         return ViewSettingsUI.getNewLocationViewSettingsUI(view);
    }
   
    public JFrame getInstructorView() {
         return ViewSettingsUI.getNewInstructorViewSettingsUI(view);
    }
    
    public static JFrame getAdvancedFilterView() {
         return advancedFilterUI;
    }

    public JFrame getFairView() {
         return (JFrame) (new FairUI());
    }
    
    public JFrame getQualView() {
         return (JFrame) (new QualUI());
    }


    /** Button to open the advanced filters window. */
    public static JButton advancedFilterButton;

    protected ViewMenu viewMenu;
    protected View view;

    /** The window to set advanced filters. */
    public static AdvancedFilterUI advancedFilterUI;
}

