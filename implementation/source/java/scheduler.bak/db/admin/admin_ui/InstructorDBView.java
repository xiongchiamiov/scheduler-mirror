package scheduler.db.admin.admin_ui;

import java.awt.Dimension;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import java.awt.*;
import scheduler.db.instructordb.Instructor;
import scheduler.db.instructordb.InstructorDB;
import scheduler.db.locationdb.Location;

/**
 * This class provides the method by which the user may interact with the
 * instructor database. It will show a list of available instructors and,
 * upon selection in the list, will allow the user to see various bits of
 * information about that instructor.
 * 
 * This user interface will allow the user to add, edit, and delete an
 * instructor, as well as set his or her course preferences.
 * 
 * @author Cedric Wienold
 */
public class InstructorDBView extends JFrame {

	/** Initial instructor data to view in the list box */
	private Vector<Instructor> initialData;
	
	/** Vector to hold the string representations of the user names */
	private Vector<String> listitems;

	/**
	 * Constructor to create the user interface to the instructor database.
	 */
	public InstructorDBView () {

		Container overallPane = this.getContentPane();
		overallPane.setLayout(new BoxLayout(overallPane,BoxLayout.X_AXIS));
		
		/**
		 * Render left pane for instructor selection.
		 */
		Container selectPane = new Container();
		selectPane.setLayout(new BoxLayout(selectPane,BoxLayout.Y_AXIS));


		/**
		 * Form the listbox panel
		 */

		JPanel listPanel = new JPanel();

		initialData = new Vector<Instructor>();
		initialData.add(new Instructor("Gene","Fisher","gfisher",0,new Location("14","244")));

		listitems = new Vector<String>();

		int i;

		for (i = 0; i<initialData.size(); i++) {
			listitems.add(initialData.get(i).getName());
		}

		JList listbox = new JList(listitems);
		listbox.setPreferredSize(new Dimension(200,300));

		listPanel.add(listbox);

		/**
		 * Form the buttons panel
		 */
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.Y_AXIS));

		JButton addButton = new JButton("Add Instructor");
		JButton editButton = new JButton("Edit Instructor");
		JButton removeButton = new JButton("Remove Instructor");
		JButton preferredButton = new JButton("Preferred Courses");

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//new InstructorDB().AddInstructor(null);
			}
		});		
		
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			 	//new InstructorDB().EditInstructor(null);
			}
		});		
		
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//new InstructorDB().RemoveInstructor(null);
			}
		});		
		
		preferredButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//new InstructorDB().SetPreferredCourses(null, null);
			}
		});		
		
		buttonsPanel.add(addButton);
		buttonsPanel.add(editButton);
		buttonsPanel.add(removeButton);
		buttonsPanel.add(preferredButton);
		
		
		/**
		 * Render right pane for information display.
		 */
		Container infoPane = new Container();
		selectPane.setLayout(new BoxLayout(selectPane,BoxLayout.Y_AXIS));
		
		/**
		 * Render a horizontal pane for alignment
		 */
		Container infoSides = new Container();
		infoSides.setLayout(new BoxLayout(infoSides,BoxLayout.X_AXIS));
		
		/**
		 * Left side: Static labels
		 */
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel,BoxLayout.Y_AXIS));
		labelPanel.setPreferredSize(new Dimension(100,0));
		
		JLabel nameLabel = new JLabel("Name:");
		JLabel idLabel = new JLabel("ID:");
		JLabel wtuLabel = new JLabel("WTU:");
		JLabel officenumberLabel = new JLabel("Office Number:");
		
		labelPanel.add(nameLabel);
		labelPanel.add(idLabel);
		labelPanel.add(wtuLabel);
		labelPanel.add(officenumberLabel);
		
		/**
		 * Right side: Information based on selection
		 */
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
		infoPanel.setPreferredSize(new Dimension(150,0));
		
		JLabel name = new JLabel();
		JLabel id = new JLabel();
		JLabel wtu = new JLabel();
		JLabel officenumber = new JLabel();
		
		infoPanel.add(name);
		infoPanel.add(id);
		infoPanel.add(wtu);
		infoPanel.add(officenumber);
		
		/**
		 * Add the above two into infoSides
		 */
		infoSides.add(labelPanel);
		infoSides.add(infoPanel);
		
		/**
		 * Place the above into the container
		 */
		
		selectPane.add(listPanel);
		selectPane.add(buttonsPanel);
		
		overallPane.add(selectPane);
		overallPane.add(infoSides);
		
		this.pack();

		setTitle ("Instructor Database");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setVisible(true);
	}

}
