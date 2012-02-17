package edu.calpoly.csc.scheduler.model.db;
/****
 *
 * This is skeleton testing class for SQLDB.java.  The path to this testing
 * class parallels exactly the path to the class under test.  I.e., the full
 * path to SQLDB.java is
 *
 *     scheduler/testing/implementation/GWTView/src/
 *         edu/calpoly/csc/scheduler/view/web/shared/SQLDBTest.java
 *
 * The full path to this file, SQLDBTest.java, is
 *
 *     scheduler/testing/testing/implementation/GWTView/src/
 *         edu/calpoly/csc/scheduler/view/web/shared/SQLDBTest.java
 *
 * There is a considerable amount of superfluous directory structure in these
 * paths.  This can be cleaned when and if there is an overall refactoring of
 * the project repository.
 *
 * Here is a preliminary class test plan:
 *                                                                         <br>
 *     Phase 1: Unit test the constructor.
 *                                                                         <br>
 *     Phase 2: Unit test both overloads of open.  Leave the DB open (and in
 *              memory) for the remainder of the unit tests.
 *                                                                         <br>
 *     Phase 3: Unit test executeInsert with a large number of test cases to
 *              add records to the DB.  Choose test case data from requirements
 *              examples, to provide realistic cases.  Leave the database test
 *              fixture open during tests.
 *                                                                         <br>
 *     Phase 4: Unit test executeSelect with a large number of test cases to
 *              find records in the DB.  Choose test case data based on records
 *              entered in Phase 3.
 *                                                                         <br>
 *     Phase 5: Unit test the other execute methods.
 *                                                                         <br>
 *     Phase 6: Unit test the two helper methods with data that have not been
 *              covered in preceding test phases.
 *                                                                         <br>
 *     Phase 7: Unit test the int-valued get methods.
 *                                                                         <br>
 *     Phase 8: Unit test the boolean-valued predicate methods.
 *                                                                         <br>
 *     Phase 9: Unit test serialize.
 *
 *     Phase 10: Stress test with DB of 10,000 records.
 *
 * @author: Gene Fisher (gfisher@calpoly.edu)
 * @version: 17feb12
 */
public class SQLDBTest extends TestCase {

}
