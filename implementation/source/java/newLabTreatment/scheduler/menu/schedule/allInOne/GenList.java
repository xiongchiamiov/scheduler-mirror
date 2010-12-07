package scheduler.menu.schedule.allInOne;

import java.util.Vector;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * <pre>
 * Represents a list of "things", which can be selected via checkboxes. This
 * data is wrapped up in a Box so as to make adding the list to another GUI 
 * easy. Abstract b/c extending classes must specify how they want their lists
 * to be filled. 
 *
 * In particular, the methods contained here help to initialize, refresh, and
 * properly size the contents of the list.
 * 
 * Provides "select all" and "clear" buttons for whatever it contains. 
 *
 * By default, allows SINGLE_SELECTION in its ListSelectionModel. 
 * 
 * @author Eric Liebowitz
 * @version 08jun10
 * </pre>
 */
public abstract class GenList<T> extends Box
{
   /**
    * Names to display at top of the box
    */
   protected Box title;
   /**
    * Contains the checkbox list of "things"
    */
   protected CheckBoxTable table;
   /**
    * List to wrap around the CheckBoxTable
    */
   protected JScrollPane list;
   /**
    * Contains references to the "things" currently checked off in the list
    */
   protected Vector<T> selections;
   /**
    * The currently selected "thing", if any
    */
   public T selected;

   /**
    * Creates our Box of things.
    *
    * @param axis Direction you want the box to go (BoxLayout.X_AXIS or
    *             BoxLayout.Y_AXIS)
    * @param colData List of Strings representing column headers
    * @param name Name to be displayed at the top of the box
    */
   public GenList (int axis, Vector<String> colData, String name)
   {
      super (axis);
      init(colData, name);

      this.add(title);
      this.add(list);

      Box butBox = new Box (BoxLayout.X_AXIS);
      butBox.add(clearButton());
      butBox.add(allButton());
      this.add(butBox);
   }

   /**
    * Initializes data pertaining to this box. Creates the title. Creates the 
    * list containing the CheckBoxTable. Inits "selected" to null. Adds 
    * listeners to automatically resize the list and update the "selected" 
    * instance variable. 
    *
    * @param colData List of strings for what the column headers should be
    * @param name Name to be displayed at the top of the box
    */
   private void init (Vector<String> colData, String name)
   {
      table = new CheckBoxTable (new Vector(), colData);

      title = new Box (BoxLayout.X_AXIS);
      title.add(new JLabel(name));
      title.add(Box.createGlue());

      list = new JScrollPane (table);
      list.addComponentListener(new AutoAdjustTableColumnListener(this));

      table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      selections = new Vector<T>();
      selected = null;
      table.getSelectionModel().addListSelectionListener
      (
         new ListSelectionListener ()
         {
            public void valueChanged (ListSelectionEvent evt)
            {
               tableSelectionChanged(evt);
            }
         }
      );
   }

   /**
    * Listens for a change in selection and updates "selected" to reflext what  
    * the user has currently selected. 
    *
    * @param evt That things that comes with ComponentListeners. Check out their
    *        documentation for what this things contents are. 
    */
   private void tableSelectionChanged (ListSelectionEvent evt)
   {
      int r = table.getSelectedRow();
      if (r < 0)
      {
         selected = null;
      }
      else
      {
         selected = (T)table.getValueAt(r, 1);
      }
   }

   /**
    * Returns the currently selected "thing"
    *
    * @return The "thing" currently selected. Can be null, if the user hasn't yet
    *         clicked in the list.
    */
   public T getSelected () { return selected; }

   /** 
    * Creates a "select all" button. When clicked, all checkboxes will be 
    * checked.
    *
    * @return The button which does the "select all" action when clicked.
    */
   private JButton allButton()
   {
      JButton button = new JButton("Select All");
      final CheckBoxTable t = this.table;
      button.addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed (ActionEvent e)
            {
               t.selectAll();
            }
         }
      );
      return button;
   }

   /**
    * Creates a "clear" button. When clicked, all checkboxes will be unchecked. 
    * 
    * @return The button which does the "clear" action when clicked. 
    */
   private JButton clearButton ()
   {
      JButton button = new JButton("Clear");
      final CheckBoxTable t = this.table;
      button.addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed (ActionEvent e)
            {
               t.clearAll();
            }
         }
      );
      return button;
   }

   /**
    * Refreshes the contents of the list to reflect any changes in whatever's
    * backing the list.
    */
   public void refresh ()
   {
      gatherSelected();
      while (this.table.getRowCount() > 0)
      {
         this.table.getModel().removeRow(0);
      }

      try
      {
         populate ();
      }
      catch (NullPointerException e)
      {
         /* Thrown when the database is empty...just don't populate */
         System.out.println ("List says populate got null");
      }
      condenseColumns();
      sizeTheList();
      this.revalidate();
   }

   /**
    * Goes through the list of "things" and adds whichever ones are checked to 
    * the "selections" list. In particular, a "copy" of each item is added to 
    * the list, where "copy" is a method which children of this class must 
    * supply. 
    */
   protected void gatherSelected()
   {
      selections.clear();
      for (int r = 0; r < table.getRowCount(); r ++)
      {
         if (table.getValueAt(r, 0) == Boolean.TRUE)
         {
            T t = (T)table.getValueAt(r, 1);
            selections.add(copy(t));
         }
      }
   }
   
   /**
    * Resizes the table's columns to only take up as much space as each column's
    * content requires. 
    */
   protected void condenseColumns ()
   {
      packCheckColumn ();
      for (int col = 1; col < this.table.getColumnCount(); col ++)
      {
         packColumn(col, 0);
      }
   }

   /**
    * Decides how the "check" column (the first one) is sized
    */
   public void packCheckColumn ()
   {
      this.table.getColumnModel().getColumn(0).setMaxWidth(1);
   }
   
   /**
    * Resizes a particular column to take up only as much width as its widest
    * data field. I pulled the code for this off the web...I've a vague idea 
    * of how it works, but don't expect lots of comments on it. Read it enough
    * and the chaos might make sense.
    *
    * @param col Index of the column to change
    * @param margin Any margin space you'd like to go with 
    */
   public void packColumn (int col, int margin)
   {
      TableColumn tc = this.table.getColumnModel().getColumn(col);

      TableCellRenderer renderer = tc.getHeaderRenderer();
      if (renderer == null)
      {
         renderer = this.table.getTableHeader().getDefaultRenderer();
      }

      Component comp = renderer.getTableCellRendererComponent(
         this.table, tc.getHeaderValue(), false, false, 0, 0);
      int width = comp.getPreferredSize().width;

      for (int r = 0; r < this.table.getRowCount(); r ++)
      {
         renderer = table.getCellRenderer(r, col);
         comp = renderer.getTableCellRendererComponent(
            this.table, this.table.getValueAt(r, col), false, false, r, col);
         width = Math.max(width, comp.getPreferredSize().width);
      }

      width += 2 * margin;

      tc.setPreferredWidth(width);
   }

   /**
    * Resizes the list to accomadate whatever size the table within it is
    */
   protected void sizeTheList ()
   {
      int width = 0;
      for (int col = 0; col < this.table.getColumnCount(); col ++)
      {
         int size = this.table.getColumnModel().getColumn(col).getPreferredWidth();  
         width += size;
      }

      Dimension size = new Dimension (width, 175);
      this.list.setPreferredSize(size);
   }

   /**
    * Determines what is added to the "selections" list when a "thing" is 
    * checked. 
    *
    * @param t The thing that was checked
    */
   protected abstract T copy (T t);

   /**
    * Determines how data is laid out in this box's table. 
    */
   protected abstract void populate ();
}
