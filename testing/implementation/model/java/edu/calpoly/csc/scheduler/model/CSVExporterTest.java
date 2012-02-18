package edu.calpoly.csc.scheduler.model.db;
/****
 *
 * This is skeleton testing class for CSVExporter.java.  It outlines the unit
 * test plan for testing the method CSVExporter.export.  For unit test details,
 * see the documentation for the testExport method below.
 *
 * The path to this testing class parallels exactly the path to the class under
 * test.  I.e., the full path to CSVExporter.java is
 *                                                                        <pre>
 *     scheduler/testing/implementation/GWTView/src/
 *         edu/calpoly/csc/scheduler/view/web/shared/CSVExporterTest.java
 *                                                                       </pre>
 * The full path to this file, CSVExporterTest.java, is
 *                                                                        <pre>
 *     scheduler/testing/testing/implementation/GWTView/src/
 *         edu/calpoly/csc/scheduler/view/web/shared/CSVExporterTest.java
 *                                                                       </pre>
 * There is a considerable amount of superfluous directory structure in these
 * paths.  This can be cleaned when and if there is an overall refactoring of
 * the project repository.
 *
 * @author: Gene Fisher (gfisher@calpoly.edu)
 * @version: 17feb12
 */
public class CSVExporterTest extends TestCase {

    /**
     * Method testExport is the unit testing method that calls
     * CSVExporter.export.  Here is an outline of the unit test plan:
     *                                                                    <pre>
     *  Test
     *  Case    Input               Output              Remarks
     * ====================================================================
     *   1      empty schedule      empty CSV file      Null case
     *
     *   2      schedule with       CSV file with       1 item case
     *          1 scheduled item    that item, and
     *                              nothing else
     *
     *   3      schedule with       CSV file with       2 item case
     *          2 scheduled items   those items, and
     *                              nothing else

     *   4 - N  schedule with a     CSV files with      a variety of N
     *          wide variety of     those items, and    different test
     *          scheduled items,    nothing else,       cases
     *          based on range      each of which
     *          testing of the      matches the
     *          scheduled item      corresponding
     *          data fields         expected output
     *                              file
     *
     *  N+1     schedule with       CSV file that       stress test
     *          1000 items of       matches expected
     *          different data      output file
     *          range values
     *                                                                   </pre>
     */
    public void testExport() {

	//
        // Foreach test case, do the following:

        //    Set up the input data by calling a method to construct and
        //    populate a schedule with the desired number of items.

        //    Wrap that schedule in a Model object, since CSVExporter.export
        //    needs a model for its input.

        //    Call CSVExporter.export with the input for this test case.

        //    Capture the string-valued output and write it to an output file.

        //    Compare the actual output value from the method with the expected
        //    output, which is stored in a pre-defined expected output file.

	//
        // It's common JUnit practice to implement each test case in a separate
        // method, but this is not required.  You could have one parameterized,
        // helper method, like that sketched out in the testExportCase() method
        // below.
        //
     }

    /**
     * Call CSVExporter.export with a schedule containing the given
     * numberOfItems.  Data values in each item will contain ranges of values
     * for scheduled item data fields, per standard testing practices of data
     * range testing.
     *
     * Capture the string result of the export method and write it to a file.
     * Compare the actual output file with the expected output in the given
     * expectedOutputFile.
     */
    private void testExportCase(int numberOfItems, String expectedOutputFile) {
        // ...
    }
}
