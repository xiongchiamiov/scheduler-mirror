package scheduler.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import scheduler.model.Course;
import scheduler.model.Document;
import scheduler.model.Model;
import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDatabase.NotFoundException;

public abstract class CoursesTest extends ModelTestCase
{
   private static final int START_HALF_HOUR = 14; // 7am
   private static final int END_HALF_HOUR   = 44; // 10pm

   public void testTransientsNotInserted() throws DatabaseException
   {
      Model model = createBlankModel();

      Document document = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
      model.createTransientCourse("Test", "101", "CSC", "4", "4", "1", "LEC", "60", "6", true);

      assertEquals(model.findInstructorsForDocument(document, true).size(), 0);
   }
   
   public void testInsertAndFindCourse() throws DatabaseException {
      Model model = createBlankModel();
      
      int courseID;
      {
         Document doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
         courseID = model.createTransientCourse("Test", "101", "CSC", "4", "4", "1",
               "LEC", "60", "6", true).setDocument(doc).insert().getID();
      }
      
      Course found = model.findCourseByID(courseID);
      assertTrue(found.getName().equals("Test"));
      assertTrue(found.getCatalogNumber().equals("101"));
      assertTrue(found.getDepartment().equals("CSC"));
      assertTrue(found.getWTU().equals("4"));
      assertTrue(found.getSCU().equals("4"));
      assertTrue(found.getNumSections().equals("1"));
      assertTrue(found.getType().equals("LEC"));
      assertTrue(found.getMaxEnrollment().equals("60"));
      assertTrue(found.getNumHalfHoursPerWeek().equals("6"));
      assertTrue(found.isSchedulable());
   }
   
//   public void testInsertAndDeleteCourse() throws DatabaseException {
//      Model model = createBlankModel();
//      
//      Document doc;
//      int courseID;
//      
//      {
//         doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
//         courseID = ModelTestUtility.createCourse(model).setDocument(doc).insert().getID();
//      }
//      
//      model.findCourseByID(courseID).delete();
//      doc.delete();
//      System.out.println("courses left: " + model.findCoursesForDocument(doc));
//      assertTrue(model.isEmpty());
//   }
   
   public void testInsertAndDeleteCourse() throws DatabaseException {
      Model model = createBlankModel();
      
      Document doc;
      int courseID;
      
      {
         doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
         courseID = ModelTestUtility.createCourse(model).setDocument(doc).insert().getID();
      }
      
      model.findCourseByID(courseID).delete();
      doc.delete();
      //System.out.println("courses left: " + model.findCoursesForDocument(doc));
      //assertTrue(model.findCoursesForDocument(doc).isEmpty()); //note: SQLDB, this passes but model.isempty doesn't
      assertTrue(model.isEmpty());
   }

public void testAssociation() throws DatabaseException {
	   Model model = createBlankModel();
	   
	   int lectureID;
	   int labID;
	   
	   {
		   Document doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
		   Course lecture = model.createTransientCourse("intro c", "101lec", "csc", "4", "4", "1", "LEC", "30", "6", true).setDocument(doc).insert();
		   Course lab = model.createTransientCourse("intro c", "101lab", "csc", "4", "4", "1", "LAB", "30", "6", true).setDocument(doc).insert();
		   lab.setLecture(lecture);
		   lab.update();
		   
		   lectureID = lecture.getID();
		   labID = lab.getID();
	   }
	   
	   model.clearCache();
	   
	   Course lecture = model.findCourseByID(lectureID);
	   
	   Course lab = model.findCourseByID(labID);
	   assert(!lab.lectureLoaded);
	   lab.getLecture();
	   
	   assert(lab.getLecture() == lecture);
   }
   
   public void testUpdateCourse() throws DatabaseException {
      Model model = createBlankModel();

      int courseID;
      
      {
         Document doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
         Course course = ModelTestUtility.createCourse(model).setDocument(doc).insert();
         course.setCatalogNumber("999");
         course.setDepartment("NotCSC");
         course.setIsSchedulable(false);
         course.setMaxEnrollment("10");
         course.setName("NotTest");
         course.setNumHalfHoursPerWeek("12");
         course.setNumSections("2");
         course.setSCU("8");
         course.setType("LAB");
         course.setWTU("8");
         courseID = course.getID();
         course.update();
      }
      
      Course course = model.findCourseByID(courseID);
      assertTrue(course.getCatalogNumber().equals("999"));
      assertTrue(course.getDepartment().equals("NotCSC"));
      assertTrue(course.isSchedulable() == false);
      assertTrue(course.getMaxEnrollment().equals("10"));
      assertTrue(course.getName().equals("NotTest"));
      assertTrue(course.getNumHalfHoursPerWeek().equals("12"));
      assertTrue(course.getNumSections().equals("2"));
      assertTrue(course.getSCU().equals("8"));
      assertTrue(course.getType().equals("LAB"));
      assertTrue(course.getWTU().equals("8"));
   }
   
   public void testDeleteInstructor() throws Exception {
      Model model = createBlankModel();

      Document doc;
      int courseID;
      
      {
         doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
         Course course = ModelTestUtility.createCourse(model).setDocument(doc).insert();
         courseID = course.getID();
         course.delete();
      }
      
      try {
         model.findCourseByID(courseID);
         assertTrue(false); // should have failed
      }
      catch (NotFoundException e) { }
      
      doc.delete();
      
      assertTrue(model.isEmpty());
   }
   
   public void testFindAllCoursesForDocument() throws DatabaseException {
      Model model = createBlankModel();

      Set<Integer> courseIDs = new HashSet<Integer>();
      
      Document doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
      courseIDs.add(model.createTransientCourse("Test", "101", "CSC", "4", "4", "1",
            "LEC", "60", "6", true).setDocument(doc).insert().getID());
      courseIDs.add(model.createTransientCourse("Test1", "1011", "CSC1", "8", "8", "2",
            "LAB", "10", "4", true).setDocument(doc).insert().getID());
      
      Collection<Course> returnedCourses = model.findCoursesForDocument(doc);
      for (Course returnedDoc : returnedCourses) {
         assertTrue(courseIDs.contains(returnedDoc.getID()));
         courseIDs.remove(returnedDoc.getID());
      }
      assertTrue(courseIDs.isEmpty());
   }
   
   public void testFindAllCoursesInMultipleDocuments() throws DatabaseException {
      Model model = createBlankModel();

      {
         Set<Integer> courseIDs1 = new HashSet<Integer>();
         
         Document doc1 = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc1", START_HALF_HOUR, END_HALF_HOUR);
         courseIDs1.add(model.createTransientCourse("Test", "101", "CSC", "4", "4", "1",
               "LEC", "60", "6", true).setDocument(doc1).insert().getID());
         courseIDs1.add(model.createTransientCourse("Test1", "1011", "CSC1", "8", "8", "2",
               "LAB", "10", "4", true).setDocument(doc1).insert().getID());
         
         Collection<Course> returnedCourses = model.findCoursesForDocument(doc1);
         for (Course returnedDoc : returnedCourses) {
            assertTrue(courseIDs1.contains(returnedDoc.getID()));
            courseIDs1.remove(returnedDoc.getID());
         }
         assertTrue(courseIDs1.isEmpty());
      }
      
      {
         Set<Integer> courseIDs2 = new HashSet<Integer>();
         
         Document doc2 = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc2", START_HALF_HOUR, END_HALF_HOUR);
         courseIDs2.add(model.createTransientCourse("2Test", "2101", "2CSC", "24", "24", "21",
               "LEC", "260", "26", true).setDocument(doc2).insert().getID());
         courseIDs2.add(model.createTransientCourse("2Test1", "21011", "2CSC1", "28", "28", "22",
               "LAB", "210", "24", true).setDocument(doc2).insert().getID());
         
         Collection<Course> returnedCourses = model.findCoursesForDocument(doc2);
         for (Course returnedDoc : returnedCourses) {
            assertTrue(courseIDs2.contains(returnedDoc.getID()));
            courseIDs2.remove(returnedDoc.getID());
         }
         assertTrue(courseIDs2.isEmpty());
      }
   }
}
