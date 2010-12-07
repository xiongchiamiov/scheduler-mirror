package scheduler.splash;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SplashAdmin extends JFrame
{
   private Box contents;

   public SplashAdmin ()
   {
      super ("Startup Wizard - Administration");
      contents = new Box(BoxLayout.Y_AXIS).createVerticalBox();

      contents.add(addWelcome());
      contents.add(Box.createVerticalStrut (30));
      contents.add(addAsk());
      contents.add(addOptions());
      contents.add(Box.createVerticalStrut (60));
      contents.add(addButtons());

      this.add(contents);
      pack ();
   }

   private Box addWelcome ()
   {
      JLabel message = new JLabel ("Welcome to the Schedule Generator!", SwingConstants.LEFT);
      Box welcome = new Box (BoxLayout.X_AXIS).createHorizontalBox();
      welcome.add(message);
      welcome.add(Box.createHorizontalGlue());

      return welcome;
   }

   private Box addAsk ()
   {
      JLabel message = new JLabel ("Would you like to:", SwingConstants.LEFT);
      Box ask = new Box (BoxLayout.X_AXIS).createHorizontalBox();
      ask.add(Box.createHorizontalStrut (15));
      ask.add(message);
      ask.add(Box.createHorizontalGlue());

      return ask;
   }

   private Box addOptions ()
   {
      Box options = new Box (BoxLayout.Y_AXIS).createVerticalBox();
      Box temp = new Box (BoxLayout.X_AXIS).createHorizontalBox();

      temp.add(new JRadioButton ("Create a new schedule"));
      options.add (temp);

      temp = new Box (BoxLayout.X_AXIS).createHorizontalBox();
      temp.add(Box.createHorizontalStrut (3));
      temp.add(new JRadioButton ("Open a recent schedule"));
      options.add (temp);

      temp = new Box (BoxLayout.X_AXIS).createHorizontalBox();
      temp.add(Box.createHorizontalStrut (80));
      temp.add(new JRadioButton ("Open an already-existing schedule"));
      options.add (temp);

      return options;
   }

   private Box addButtons ()
   {
      Box buttons = new Box (BoxLayout.X_AXIS).createHorizontalBox();

      buttons.add(new JCheckBox ("Display at startup", true));
      buttons.add(Box.createHorizontalStrut (90));
      buttons.add(new JButton ("Next"));
      buttons.add(Box.createHorizontalStrut (30));
      buttons.add(new JButton ("Cancel"));

      return buttons;
   }

   public static void main (String[] args)
   {
      new SplashAdmin().setVisible(true);
   }
}
