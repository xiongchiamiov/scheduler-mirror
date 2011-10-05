package edu.calpoly.csc.scheduler.view.desktop.menu.schedule.generate;

import javax.swing.*;
import java.awt.event.*;

/**
 * Magic I found on the internet and shoved into a class. Takes care of whether
 * a table should resize itself or display a horizontal scroll bar. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class AutoAdjustTableColumnListener extends ComponentAdapter
{
   GenList list;

   /**
    * Makes a magic resizer thing. 
    * 
    * @param l The GenList to apply our auto-resizing magic
    */
   public AutoAdjustTableColumnListener (GenList l)
   {
      super ();
      this.list = l;
   }

   /**
    * I assume this listens for actions pertaining only to when a component is
    * resized. When it does, it determines whether the table's autoResizeMode
    * needs to change to "JTable.AUTO_RESIZE_NEXT_COLUMN)" or 
    * "JTable.AUTO_RESIZE_OFF", depending on whether the table has expanded 
    * beyond the width of its viewport or not. 
    */
   public void componentResized(ComponentEvent e)
   {
      if (list.table.getPreferredSize().width <= list.list.getViewport().getExtentSize().width)
      {
         list.table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
      }
      else
      {
         list.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      }
   }
}
