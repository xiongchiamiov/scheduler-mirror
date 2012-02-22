package edu.calpoly.csc.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public abstract class CoursesTest extends ModelTestCase
{
   private static final int START_HALF_HOUR = 14; // 7am
   private static final int END_HALF_HOUR   = 44; // 10pm

   public void testTransientsNotInserted()
   {
      Model model = createBlankModel();

      Document document = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
      model.assembleCourse(document, "Test", "101", "CSC", "4", "4", "1", "LEC", "60", "6", new HashSet<String>(),
            new ArrayList<Set<Day>>(), true);

      assertEquals(model.findInstructorsForDocument(document).size(), 0);
   }
   
   public void testInsertAndFindCourse() throws NotFoundException {
      Model model = createBlankModel();
      
      int courseID;
      {
         Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
         courseID = model.insertCourse(model.assembleCourse(doc, "Test", "101", "CSC", "4", "4", "1",
               "LEC", "60", "6", new HashSet<String>(),
               new ArrayList<Set<Day>>(), true)).getID();
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
   
   public void testInsertAndDeleteCourse() throws NotFoundException {
      Model model = createBlankModel();
      
      Document doc;
      int courseID;
      
      {
         doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
         courseID = model.insertCourse(ModelTestUtility.createCourse(model, doc)).getID();
      }
      
      model.deleteCourse(model.findCourseByID(courseID));
      model.deleteDocument(doc);
      
      assertTrue(model.isEmpty());
   }
   
   public void testModifyCourseValueDoesntAutomaticallyUpdateDatabase() throws NotFoundException {
      Model model = createBlankModel();
      
      int courseID;
      
      Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
      {
         Course course = model.insertCourse(ModelTestUtility.createCourse(model, doc));
         course.setDepartment("NotCSC");
         courseID = course.getID();
      }
      
      {
         Course course = model.findCourseByID(courseID);
         assertTrue(course.getDepartment().equals(ModelTestUtility.createCourse(model, doc).getDepartment()));
      }
   }
   
   public void testUpdateCourse() throws NotFoundException {
      Model model = createBlankModel();

      int courseID;
      
      {
         Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
         Course course = model.insertCourse(ModelTestUtility.createCourse(model, doc));
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
         model.updateCourse(course);
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
         doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
         Course course = model.insertCourse(ModelTestUtility.createCourse(model, doc));
         courseID = course.getID();
         model.deleteCourse(course);
      }
      
      try {
         model.findCourseByID(courseID);
         assertTrue(false); // should have failed
      }
      catch (NotFoundException e) { }
      
      model.deleteDocument(doc);
      
      assertTrue(model.isEmpty());
   }
   
   public void testFindAllCoursesForDocument() {
      Model model = createBlankModel();

      Set<Integer> courseIDs = new HashSet<Integer>();
      
      Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
      courseIDs.add(model.insertCourse(model.assembleCourse(doc, "Test", "101", "CSC", "4", "4", "1",
            "LEC", "60", "6", new HashSet<String>(),
            new ArrayList<Set<Day>>(), true)).getID());
      courseIDs.add(model.insertCourse(model.assembleCourse(doc, "Test1", "1011", "CSC1", "8", "8", "2",
            "LAB", "10", "4", new HashSet<String>(),
            new ArrayList<Set<Day>>(), true)).getID());
      
      Collection<Course> returnedCourses = model.findCoursesForDocument(doc);
      for (Course returnedDoc : returnedCourses) {
         assertTrue(courseIDs.contains(returnedDoc.getID()));
         courseIDs.remove(returnedDoc.getID());
      }
      assertTrue(courseIDs.isEmpty());
   }
   
   public void testFindAllCoursesInMultipleDocuments() {
      Model model = createBlankModel();

      {
         Set<Integer> courseIDs1 = new HashSet<Integer>();
         
         Document doc1 = model.insertDocument(model.assembleDocument("doc1", START_HALF_HOUR, END_HALF_HOUR));
         courseIDs1.add(model.insertCourse(model.assembleCourse(doc1, "Test", "101", "CSC", "4", "4", "1",
               "LEC", "60", "6", new HashSet<String>(),
               new ArrayList<Set<Day>>(), true)).getID());
         courseIDs1.add(model.insertCourse(model.assembleCourse(doc1, "Test1", "1011", "CSC1", "8", "8", "2",
               "LAB", "10", "4", new HashSet<String>(),
               new ArrayList<Set<Day>>(), true)).getID());
         
         Collection<Course> returnedCourses = model.findCoursesForDocument(doc1);
         for (Course returnedDoc : returnedCourses) {
            assertTrue(courseIDs1.contains(returnedDoc.getID()));
            courseIDs1.remove(returnedDoc.getID());
         }
         assertTrue(courseIDs1.isEmpty());
      }
      
      {
         Set<Integer> courseIDs2 = new HashSet<Integer>();
         
         Document doc2 = model.insertDocument(model.assembleDocument("doc2", START_HALF_HOUR, END_HALF_HOUR));
         courseIDs2.add(model.insertCourse(model.assembleCourse(doc2, "2Test", "2101", "2CSC", "24", "24", "21",
               "LEC", "260", "26", new HashSet<String>(),
               new ArrayList<Set<Day>>(), true)).getID());
         courseIDs2.add(model.insertCourse(model.assembleCourse(doc2, "2Test1", "21011", "2CSC1", "28", "28", "22",
               "LAB", "210", "24", new HashSet<String>(),
               new ArrayList<Set<Day>>(), true)).getID());
         
         Collection<Course> returnedCourses = model.findCoursesForDocument(doc2);
         for (Course returnedDoc : returnedCourses) {
            assertTrue(courseIDs2.contains(returnedDoc.getID()));
            courseIDs2.remove(returnedDoc.getID());
         }
         assertTrue(courseIDs2.isEmpty());
      }
   }
}
