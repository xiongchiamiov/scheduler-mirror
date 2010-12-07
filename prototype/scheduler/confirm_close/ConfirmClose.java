package scheduler.confirm_close;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConfirmClose extends JFrame
{
   private Box contents;

      public ConfirmClose ()
         {
               super ("Closing with unsaved chagned!");
                     contents = new Box(BoxLayout.Y_AXIS).createVerticalBox();
                           
                                 Box aBox = new Box(BoxLayout.X_AXIS).createHorizontalBox();
                                       aBox.add(new JLabel ("There are still unsaved changes in the schedule.\nDo you wish to saving before closing the schedule?"));
                                             aBox.add(Box.createHorizontalStrut(30));
                                                   aBox.add(Box.createHorizontalGlue());

                                                         Box myBox = new Box(BoxLayout.X_AXIS).createHorizontalBox();
                                                               myBox.add (Box.createHorizontalGlue());
                                                                     myBox.add (new JButton ("Cancel"));
                                                                           myBox.add (Box.createHorizontalStrut (30));
                                                                                 myBox.add (new JButton ("Close without saving"));
                                                                                       myBox.add (Box.createHorizontalStrut (30));
                                                                                             myBox.add (new JButton ("Save and close"));

                                                                                                   contents.add (aBox);
                                                                                                         contents.add (Box.createVerticalStrut(30));
                                                                                                               contents.add (myBox);
                                                                                                                     
                                                                                                                           this.add(contents);
                                                                                                                                 pack();
                                                                                                                                    }

                                                                                                                                       public static void main (String[] args)
                                                                                                                                          {
                                                                                                                                                new ConfirmClose().setVisible(true);
                                                                                                                                                   }
                                                                                                                                                   }
