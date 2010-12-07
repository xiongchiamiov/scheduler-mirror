package scheduler.view.view_ui;

import scheduler.Scheduler;
import scheduler.view.*;

import javax.swing.*;
import java.util.ArrayList;

/****
 * Class ViewTypeFilterUI provides a panel with a checkbox for each course.
 * This allows for the filtering of courses in a schedule view.
 * The companion model class is ViewCourseFilter.
 *
 * @author Jason Mak
 */
public class ViewTypeFilterUI extends JScrollPane {
    /**
     * Construct this by calling compose.
     *
     * @param viewTypeFilter the companion model
     */
    public ViewTypeFilterUI(View view, Object viewTypeFilter) {
        this.view = view;

        viewType = view.getViewSettings().getViewType();
        if (viewType == ViewType.COURSE) {
            viewCourseFilter = (ViewCourseFilter) viewTypeFilter;
        } else if (viewType == ViewType.INSTRUCTOR) {
            viewInstructorFilter = (ViewInstructorFilter) viewTypeFilter;
        } else {
            viewLocationFilter = (ViewLocationFilter) viewTypeFilter;
        }
        compose();
    }

    /** gui builder code */
    private javax.swing.JPanel jPanel3;
    /** gui builder code */
    private GroupLayout.ParallelGroup parallelGroup;
    /** gui builder code */
    private GroupLayout.SequentialGroup sequentialGroup;

    /**
     * This method is called from within the constructor to
     * initialize the form.
     */
    public void compose() {
        jPanel3 = new JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Currently Viewing"));

        viewTypeFilterNames = createFilterNames();
        dataCheckBoxList = new ArrayList<JCheckBox>();

        for (int k = 0;  k < viewTypeFilterNames.size(); k++) {
            dataCheckBoxList.add(new JCheckBox(viewTypeFilterNames.get(k)));
            if (viewType == ViewType.COURSE) {
                dataCheckBoxList.get(k).setSelected(
                 viewCourseFilter.getCourseFilterList().get(k).isSelected());
            } else if (viewType == ViewType.INSTRUCTOR) {
                dataCheckBoxList.get(k).setSelected(
                 viewInstructorFilter.getInstructorFilterList().get(k).isSelected());
            } else {
                dataCheckBoxList.get(k).setSelected(
                 viewLocationFilter.getLocationFilterList().get(k).isSelected());
            }
            dataCheckBoxList.get(k).addItemListener(new ViewTypeFilterCheckBoxListener(view, k));
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup((parallelGroup = jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)))
          .addContainerGap(153, Short.MAX_VALUE))
        );

        for (JCheckBox aDataCheckBoxList1 : dataCheckBoxList) {
            parallelGroup.addComponent(aDataCheckBoxList1);
        }

        jPanel3Layout.setVerticalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup( (sequentialGroup = jPanel3Layout.createSequentialGroup())
          .addContainerGap())
        );

        for (JCheckBox aDataCheckBoxList : dataCheckBoxList) {
            sequentialGroup.addComponent(aDataCheckBoxList);
            sequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
        }
        setViewportView(jPanel3);
    }

    /**
     * Makes a list of string representations of courses, instructors,
     * or location, depending on view type.
     *
     * @return a list of course names, instructor names, or location strings
     */
    public ArrayList<String> createFilterNames() {
        ArrayList<String> filterTextList = new ArrayList<String>();
        ArrayList<CourseFilterObj> courseFilterObjList;
        ArrayList<InstructorFilterObj> instructorFilterObjList;
        ArrayList<LocationFilterObj> locationFilterObjList;

        if (viewType == ViewType.COURSE) {
            courseFilterObjList = viewCourseFilter.getCourseFilterList();
            for (CourseFilterObj aCourseFilterObj : courseFilterObjList) {
                filterTextList.add(aCourseFilterObj.getCourse().toString());
            }
        } else if (viewType == ViewType.INSTRUCTOR) {
            instructorFilterObjList = viewInstructorFilter.getInstructorFilterList();
            for (InstructorFilterObj anInstructorFilterObj : instructorFilterObjList) {
                filterTextList.add(anInstructorFilterObj.getInstructor().getName());
            }
        } else {
            locationFilterObjList = viewLocationFilter.getLocationFilterList();
            for (LocationFilterObj aLocationFilterObj : locationFilterObjList) {
                filterTextList.add(aLocationFilterObj.getLocation().toString());
            }
        }

        return filterTextList;
    }

    /** The parent view. */
    protected View view;

    /** The current view type, course, instructor, or location. */
    protected ViewType viewType;

    /** The companion model class for a course view. */
    protected ViewCourseFilter viewCourseFilter;

    /** The companion model class for an instructor view. */
    protected ViewInstructorFilter viewInstructorFilter;

    /** The companion model class for a location view. */
    protected ViewLocationFilter viewLocationFilter;

    /** List of checkboxes for each course. */
    protected ArrayList<JCheckBox> dataCheckBoxList;

    /** List of label names for filtering courses, instructors, or locations. */
    protected ArrayList<String> viewTypeFilterNames;

}
