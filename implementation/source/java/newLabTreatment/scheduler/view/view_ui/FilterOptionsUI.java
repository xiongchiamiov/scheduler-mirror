package scheduler.view.view_ui;

import scheduler.view.FilterOptions;
import javax.swing.*;


/****
 * Companion view class for FilterOptions. This updates the schedule view
 * immediately with user interaction.
 *
 * @author Jason Mak, jamak3@gmail.com
 */
public class FilterOptionsUI extends JScrollPane {

    /**
     * Construct this with the companion model.
     *
     * @param filterOptions companion model
     */
    public FilterOptionsUI (FilterOptions filterOptions) {
        this.filterOptions = filterOptions;
    }

    /** The companion model. */
    protected FilterOptions filterOptions;

    /** Array of checkboxes for each filter option. */
    protected JCheckBox[] filterCheckBoxList;

    /** gui builder code */
    private javax.swing.JPanel jPanel4;

    /** gui builder code */
    public void compose() {
        jPanel4 = new javax.swing.JPanel();
                
        setBorder(javax.swing.BorderFactory.createTitledBorder("Filters"));

        boolean[] filterOptionsArray = filterOptions.toArray();          
        filterCheckBoxList = new JCheckBox[filterOptionsArray.length];

        for (int k = 0; k < filterOptionsArray.length; k++) {
            filterCheckBoxList[k] = new JCheckBox();
            if (filterOptionsArray[k]) {
                filterCheckBoxList[k].setSelected(true);
            }
            filterCheckBoxList[k].addItemListener(new FilterOptionCheckBoxListener(k));
        }

        filterCheckBoxList[0].setText("Course Name");

        filterCheckBoxList[1].setText("Course Number");

        filterCheckBoxList[2].setText("Section");

        filterCheckBoxList[3].setText("Course WTU");

        filterCheckBoxList[4].setText("Course Type");

        filterCheckBoxList[5].setText("Max Enrollment");

        filterCheckBoxList[6].setText("Lab Pairing");

        filterCheckBoxList[7].setText("Course Required Equipment");

        filterCheckBoxList[8].setText("Instructor Name");

        filterCheckBoxList[9].setText("Instructor ID");

        filterCheckBoxList[10].setText("Instructor Office");

        filterCheckBoxList[11].setText("Instructor WTU");

        filterCheckBoxList[12].setText("Instructor Disabilities");

        filterCheckBoxList[13].setText("Building");

        filterCheckBoxList[14].setText("Room");

        filterCheckBoxList[15].setText("Location Max Occupancy");

        filterCheckBoxList[16].setText("Room Type");

        filterCheckBoxList[17].setText("Location Disabilities Compliance");

        filterCheckBoxList[18].setText("Start Time");

        filterCheckBoxList[19].setText("End Time");

        filterCheckBoxList[20].setText("Days");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filterCheckBoxList[0])
                    .addComponent(filterCheckBoxList[1])
                    .addComponent(filterCheckBoxList[2])
                    .addComponent(filterCheckBoxList[3])
                    .addComponent(filterCheckBoxList[4])
                    .addComponent(filterCheckBoxList[5])
                    .addComponent(filterCheckBoxList[6])
                    .addComponent(filterCheckBoxList[7])
                    .addComponent(filterCheckBoxList[8])
                    .addComponent(filterCheckBoxList[9])
                    .addComponent(filterCheckBoxList[10])
                    .addComponent(filterCheckBoxList[11])
                    .addComponent(filterCheckBoxList[12])
                    .addComponent(filterCheckBoxList[13])
                    .addComponent(filterCheckBoxList[14])
                    .addComponent(filterCheckBoxList[15])
                    .addComponent(filterCheckBoxList[16])
                    .addComponent(filterCheckBoxList[17])
                    .addComponent(filterCheckBoxList[18])
                    .addComponent(filterCheckBoxList[19])
                    .addComponent(filterCheckBoxList[20]))
                .addContainerGap(91, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filterCheckBoxList[0])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[1])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[2])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[3])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[4])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[5])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[6])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[7])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[8])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[9])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[10])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[11])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[12])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[13])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[14])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[15])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[16])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[17])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[18])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[19])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterCheckBoxList[20])
                .addContainerGap(222, Short.MAX_VALUE))
        );

        setViewportView(jPanel4);
    }
}
