package edu.calpoly.csc.scheduler.view.desktop.old_view;

import scheduler.*;
import scheduler.view.*;
import javax.swing.*;

import edu.calpoly.csc.scheduler.view.desktop.MyView;

import java.awt.*;
import java.awt.event.*;
// import mvp.*;        // Not imported to avoid conflict with caltool.View

/****
 *
 * Class ViewMenu is the pulldown menu view of the <a href = "View.html" View
 * </a> model class.  The ViewMenu widget is a Java JMenu.  Anonymous instances
 * of JMenuItem are defined for each item in the menu.
 *@author Aaron Rivera 
 *
 */

public class ViewMenu extends MyView {

    /**
     * Construct this with the given name as the pulldown label.  The given
     * View is the companion model.  Also construct the state-changing menu
     * items, that must be persistent so they're text can change.
     */
    /*
     * NOTE: We don't use "mvp" anymore, but I didn't want to go track down 
     *       every mentioning of this constructor. So, I just abused 
     *       and zoomed "screen" out to be an Object.
     * 
     *         -Eric
     */
    public ViewMenu(Object screen, View view, ViewUI viewUI) {
        //super(screen, view);
        this.viewUI = viewUI;

     //   showHideAppointmentsItem = new JMenuItem("Hide Appointments");
      //  showHideMeetingsItem = new JMenuItem("Hide Meetings");
      //  showHideTasksItem = new JMenuItem("Hide Tasks");
      //  showHideEventsItem = new JMenuItem("Hide Events");
    }

    /**
     * Compose this by inserting each of its menu items into the pulldown menu.
     * The items are: Item, Day, Week, Month, Year, Next, Previous, Today, Goto
     * Date, Lists, Filter, Other User, Group, Windows, and Calendars.
     * Separators are placed after the Year, Goto Date, Filter, and Group
     * items.
     *
     * A menu item is created with the following general code pattern:
     *                                                              <pre>
     *     JMenu.add(new JMenuItem("<em>Item name</em>").addActionListener(
     *         new ActionListener() {
     *             public void actionPerformed(ActionEvent e) {
     *                 <em>model.method()</em>
     *                                                              </pre>
     */
    public Component compose() {

        /*
         * Make the widget of this a JMenu.
         */
        menu = new JMenu("View");

        addCourseItem();
        addInstructorItem();
        addLocationItem();
        addAdvancedFilterItem();
        addConflictsItem();
        addFairnessItem();
        addQualityItem();

        return menu;
    }

    /**
     * Add the 'Course' menu item.
     */
    public void addCourseItem() {
        menu.add(new JMenuItem("Course")).addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    //viewUI.getCourseViewDialog().show();
                    viewUI.getCourseView().setVisible(true);
                }

            }
        );
    }

     /**
     * Add the 'Instructor' menu item.
     */
    public void addInstructorItem() {
        menu.add(new JMenuItem("Instructor")).addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                   viewUI.getInstructorView().setVisible(true);
                }

            }
        );
    }

     /**
     * Add the 'Location' menu item.
     */
    public void addLocationItem() {
        menu.add(new JMenuItem("Location")).addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                     viewUI.getLocationView().setVisible(true);
                }

            }
        );
    }

     /**
     * Add the 'Advanced Filter' menu item.
     */
    public void addAdvancedFilterItem() {
        menu.add(new JMenuItem("Advanced Filter")).addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                     viewUI.getAdvancedFilterView().setVisible(true);
                }

            }
        );
    }
     /**
     * Add the 'Conflicts' menu item.
     */
    public void addConflictsItem() {
        menu.add(new JMenuItem("Conflicts")).addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                System.out.println("conflicts");
                }

            }
        );
    }

     /**
     * Add the 'Fairness' menu item.
     */
    public void addFairnessItem() {
        menu.add(new JMenuItem("Fairness")).addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                     viewUI.getFairView().setVisible(true);//show
                }

            }
        );
    }

     /**
     * Add the 'Quality' menu item.
     */
    public void addQualityItem() {
        menu.add(new JMenuItem("Quality")).addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                     viewUI.getQualView().setVisible(true);//show
                }

            }
        );
    }




    /** Pre-cast reference to this' widget, which is a menu */
    protected JMenu menu;

    protected JMenuItem course;

    protected JMenuItem instructor;

    protected JMenuItem location;

    protected JMenuItem conflicts;

    protected JMenuItem fairness;

    protected JMenuItem quality;

    /** The parent view. */
    ViewUI viewUI;

}

