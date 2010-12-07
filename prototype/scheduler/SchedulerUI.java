/**
 * Top-Level GUI for Scheduler Tool prototype for CPE 308
 *
 * Programmed by: Eric Liebowitz
 */

package scheduler;

import javax.swing.*;
import java.awt.*;

/**
 * SchedulerUI will setup the basic framework under which all subsequent
 * functionality will operate. It will create and house a menu bar, under
 * which all supported commands will be featured/prototyped/implemented
 */
public class SchedulerUI extends JFrame
{
   /*Top level menu bar*/
   private JMenuBar menuBar;
   
   public SchedulerUI ()
   {
      /*Menu bar*/
      menuBar = new scheduler.top_menu.FullMenu();
      setJMenuBar (menuBar);

      /*Admin splash*/
      new scheduler.splash.SplashAdmin().setVisible(true);

      /*A view of a schedule*/
      new scheduler.view_ui.CalanderView().setVisible(true);

      this.setTitle ("Scheduler Tool");
      pack ();
   }

   public static void main (String[] args)
   {
      new SchedulerUI().setVisible(true);
   }
}
