package edu.calpoly.csc.scheduler.view.desktop.menu.schedule.generate;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.Vector;
 
/**
 * Drafted ages ago, this class makes a table with checkboxes in the left-most 
 * column. If you ever have to do something like this, I pity you...Swing 
 * doesn't make it easy. I've documented the things I understand, but the nested
 * "DefaultTableModel" class is pretty weird. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class CheckBoxTable extends JTable
{
   /** Makes javac happy */
   public static final long serialVersionUID = 0;

   private static class MyModel extends DefaultTableModel
   {
      /** Makes javac happy */
      public static final long serialVersionUID = 0;

      public MyModel ()
      {
         super ();
      }

      public MyModel (Object[][] data, Object[] colNames)
      {
         super (data, colNames);
      }

      public MyModel (Vector rowData, Vector colData)
      {
         super (rowData, colData);
      }
      
      public boolean isCellEditable(int row, int column) 
      {
         return column == 0;
      }

      public Class getColumnClass(int columnIndex) 
      {
         switch (columnIndex) 
         {
            case 0: return Boolean.class;
            default: return this.getValueAt(0, columnIndex).getClass();
         }
      }

   }

   /**
    * Creates an empty CheckBoxTable
    */
   public CheckBoxTable ()
   {
      super();
      MyModel model = new MyModel ();
      this.setModel (model);
   }

   /**
    * Creates a CheckBoxTable with supplied data for its rows and columns. 
    * Yes, I know, the Vector's types are specified. I wrote this a while back. 
    * I really don't know what in 'em, but it works as is. 
    *
    * @param rowData Data for rows
    * @param colData Data for columns
    */
   public CheckBoxTable (Vector rowData, Vector colData)
   {
      super (rowData, colData);
      MyModel model = new MyModel (rowData, colData);
      this.setModel(model);

      init();
   }

   /**
    * Disables row/col selection, along with draggin and column reordering. 
    */
   private void init ()
   {
      this.setColumnSelectionAllowed (false);
      this.setRowSelectionAllowed (false);
      this.setDragEnabled(false);
      this.getTableHeader().setReorderingAllowed(false);
   }

   /**
    * Never displays vertical lines
    *
    * @param flag Something to ignore. 
    */
   public void setShowVerticalLines (boolean flag)
   {
      this.showVerticalLines = false;
   }

   /**
    * Unchecks all check boxes
    */
   public void clearAll ()
   {
      for (int r = 0; r < this.getRowCount(); r++)
      {
         this.getModel().setValueAt(false, r, 0);
      }
   }

   /** 
    * Checks all check boxes
    */
   public void selectAll ()
   {
      for (int r = 0; r < this.getRowCount(); r++)
      {
         this.getModel().setValueAt(true, r, 0);
      }
   }

   /**
    * @return The DefaultTableModel for this object
    */
   public DefaultTableModel getModel()
   {
      return (DefaultTableModel)this.dataModel;
   }
}
