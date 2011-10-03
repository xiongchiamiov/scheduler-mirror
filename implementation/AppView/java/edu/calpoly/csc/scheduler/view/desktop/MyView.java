package edu.calpoly.csc.scheduler.view.desktop;

import javax.swing.*;
import java.awt.*;

/**
 * Class for primary views to extend. Provides common methods for easily 
 * windows in a nice fashion.
 * 
 * This class extends JFrame and is set to DISPOSE_ON_CLOSE. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class MyView extends JFrame
{
   /**
    * Make a JFrame window w/o a title
    */
   public MyView ()
   {
      super ();
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
   }

   /**
    * Make a JFrame w/ a title
    */
   public MyView (String s)
   {
      super (s);
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
   }

   /**
    * Makes a JFrame w/ a title and an action for what to do when the frame is
    * closed.
    *
    * @param s Title of the window
    * @param a Action to perform when window is closed
    */
   public MyView (String s, int a)
   {
      super (s);
      this.setDefaultCloseOperation(a);
   }

   /**
    * Sets the location to "x, y", packs the object, and calls setVisible(true).
    *
    * @param x "x" coordinate
    * @param y "y" coordinate
    */
   public void show (int x, int y)
   {
      this.setLocation(x, y);
      this.pack();
      this.setVisible(true);
   }

   /**
    * Sets the location to "x, y" and sizes to a given size.
    * 
    * @param x "x" coordinate
    * @param y "y" coordinate
    * @param size Dimension to make this the size of
    */
   public void show (int x, int y, Dimension size)
   {
      this.setLocation(x, y);
      this.setSize(size);
      this.setVisible(true);
   }

   /**
    * Displays a given component at coordinates computed as an offset from
    * this one's. Also packs the component. 
    *
    * @param c Component to display and pack
    * @param x "x" coordinate offset
    * @param y "y" coordiante offset
    */
   public void show (Component c, int x, int y)
   {
      Point me = this.getLocation();

      c.setLocation(me.x + x, me.y + y);
      this.pack();
      c.setVisible(true);
   }

   /**
    * Displays a given component at coordinate computed as an offset from this
    * one's. Sizes the component to a given size.
    *
    * @param c Component to display
    * @param x "x" coordinate offset
    * @param y "y" coordinate offset
    * @param size Size to make "c"
    */
   public void show (Component c, int x, int y, Dimension size)
   {
      Point me = this.getLocation();

      c.setLocation(me.x + x, me.y + y);
      this.setSize(size);
      c.setVisible(true);
   }
}
