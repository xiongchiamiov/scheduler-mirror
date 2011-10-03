package edu.calpoly.csc.scheduler.view.desktop.menu.schedule;

import javax.swing.*;
import javax.swing.table.*;

import edu.calpoly.csc.scheduler.view.desktop.MyView;

import java.awt.*;
import java.awt.event.*;

/**
 * <pre>
 * Displays information regarding the attributes of the current schedule file. 
 * In particular, the information displayed tells:
 * 
 *  - Year
 *  - Phase
 *  - Version
 *
 * @author Eric Liebowitz
 * @version 08jun10
 * </pre>
 */
public class ScheduleMenuItem extends MyView
{
   /**
    * Creates a JFrame to display the Schedule's information. 
    */
   public ScheduleMenuItem ()
   {
      Box content = new Box(BoxLayout.Y_AXIS);

      content.add(addTerm());

      Box yearBox = new Box(BoxLayout.X_AXIS);
      yearBox.add(new JLabel("Year: "));
      yearBox.add(new JTextArea("2010"));

      Box phaseBox = new Box(BoxLayout.X_AXIS);
      phaseBox.add(new JLabel("Phase: "));
      phaseBox.add(new JTextArea("1"));

      Box versionBox = new Box(BoxLayout.X_AXIS);
      versionBox.add(new JLabel("Version: "));
      versionBox.add(new JTextArea("Draft"));

      content.add(yearBox);
      content.add(phaseBox);
      content.add(versionBox);

      content.add(addOk());

      this.add(content);
   }

   /**
    * Adds 4 radio buttons to select which term this Schedule is for (Fall, 
    * Winter, Spring, and Summer)".
    */
   public Box addTerm()
   {
      Box content = new Box(BoxLayout.X_AXIS);
      JPanel bPanel = new JPanel(new GridLayout(2, 2));
      ButtonGroup bg = new ButtonGroup();

      JRadioButton fall   = new JRadioButton("Fall");
      JRadioButton winter = new JRadioButton("Winter");
      JRadioButton spring = new JRadioButton("Spring");
      JRadioButton summer = new JRadioButton("Summer");

      bg.add(fall);
      bg.add(winter);
      bg.add(spring);
      bg.add(summer);

      bPanel.add(fall);
      bPanel.add(winter);
      bPanel.add(spring);
      bPanel.add(summer);

      content.add(new JLabel("Term: "));
      content.add(bPanel);

      return content;
   }

   /**
    * Adds an "Ok" button. At present, all this does is close the window.
    */
   public Box addOk()
   {
      Box content = new Box(BoxLayout.X_AXIS);
      content.add(Box.createGlue());

      final MyView me = this;
      JButton ok = new JButton("Ok");
      ok.addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               me.setVisible(false);
            }
         }
      );
      content.add(ok);

      return content;
   }
}
