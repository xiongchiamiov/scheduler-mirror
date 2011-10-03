package edu.calpoly.csc.scheduler.view.desktop.menu.file;

import javax.swing.*;

import edu.calpoly.csc.scheduler.view.desktop.MyView;

import java.awt.*;

/**
 * *
 * Class
 *
 * @author Jason Mak (jamak3@gmail.com)
 */
public class FileNewUI extends MyView {

    public FileNewUI() {
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        vbox = Box.createVerticalBox();
        fallSpringColumn = Box.createVerticalBox();
        winterSummerColumn = Box.createVerticalBox();
        quarterButtonGroup = new ButtonGroup();
        quarterLabel = new JLabel("Quarter:");
        yearLabel = new JLabel("Year:");
        revisionLabel = new JLabel("Revision Number:");
        yearTextField = new JTextField();
        revisionTextField = new JTextField();
        okButton = new JButton("Ok");
        cancelButton = new JButton("Cancel");
        buttonRow = Box.createHorizontalBox();
        yearRow = Box.createHorizontalBox();
        quarterRow = Box.createHorizontalBox();
        revisionRow = Box.createHorizontalBox();
        summerRadioButton = new JRadioButton("Summer");
        springRadioButton = new JRadioButton("Spring");
        winterRadioButton  = new JRadioButton("Winter");
        fallRadioButton  = new JRadioButton("Fall");

        composeQuarterRow();
        composeYearRow();
        composeRevisionRow();
        composeButtonRow();

        addVerticalStrut(vbox);
        addVerticalGlue(vbox);
        vbox.add(quarterRow);
        addVerticalStrut(vbox);
        addVerticalGlue(vbox);
        vbox.add(yearRow);
        addVerticalStrut(vbox);
        addVerticalGlue(vbox);
        vbox.add(revisionRow);
        addVerticalStrut(vbox);
        addVerticalGlue(vbox);
        vbox.add(buttonRow);
        addVerticalStrut(vbox);
        addVerticalGlue(vbox);        

        add(vbox);

        pack();
    }

    protected void composeQuarterRow() {
        fallSpringColumn.add(fallRadioButton);
        fallSpringColumn.add(springRadioButton);

        winterSummerColumn.add(winterRadioButton);
        winterSummerColumn.add(summerRadioButton);

        quarterButtonGroup.add(fallRadioButton);
        quarterButtonGroup.add(springRadioButton);
        quarterButtonGroup.add(winterRadioButton);
        quarterButtonGroup.add(summerRadioButton);

        addHoriontalStrut(quarterRow);
        quarterRow.add(quarterLabel);
        addHoriontalStrut(quarterRow);
        quarterRow.add(fallSpringColumn);
        quarterRow.add(winterSummerColumn);
        addHoriontalStrut(quarterRow);        
    }

    protected void composeYearRow() {
        addHoriontalStrut(yearRow);
        yearRow.add(yearLabel);
        addHoriontalStrut(yearRow);
        addHoriontalGlue(yearRow);        
        yearRow.add(yearTextField);
        addHoriontalStrut(yearRow);       
    }

    protected void composeRevisionRow() {
        addHoriontalStrut(revisionRow);
        revisionRow.add(revisionLabel);
        addHoriontalStrut(revisionRow);
        addHoriontalGlue(revisionRow);
        revisionRow.add(revisionTextField);
        addHoriontalStrut(revisionRow);
    }

    protected void composeButtonRow() {
        addHoriontalStrut(buttonRow);
        buttonRow.add(okButton);
        addHoriontalStrut(buttonRow);
        buttonRow.add(cancelButton);
        addHoriontalStrut(buttonRow);
    }

    protected void addHoriontalStrut(JComponent c) {
        c.add(Box.createHorizontalStrut(40));
    }

    protected void addHoriontalGlue(JComponent c) {
        c.add(Box.createHorizontalGlue());
    }

    protected void addVerticalStrut(JComponent c) {
        c.add(Box.createVerticalStrut(40));
    }

    protected void addVerticalGlue(JComponent c) {
        c.add(Box.createVerticalGlue());
    }

    protected JRadioButton springRadioButton;
    protected JRadioButton winterRadioButton;
    protected JRadioButton fallRadioButton;
    protected JRadioButton summerRadioButton;
    protected Box fallSpringColumn;
    protected Box winterSummerColumn;
    protected ButtonGroup quarterButtonGroup;
    protected JLabel quarterLabel;
    protected JLabel yearLabel;
    protected JLabel revisionLabel;
    protected JTextField yearTextField;
    protected JTextField revisionTextField;
    protected JButton okButton;
    protected JButton cancelButton;
    protected Box buttonRow;
    protected Box yearRow;
    protected Box quarterRow;
    protected Box revisionRow;
    protected Box vbox;

    public static void main(String args[]) {
        new FileNewUI().setVisible(true);
    }
}
