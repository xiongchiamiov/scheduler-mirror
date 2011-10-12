package edu.calpoly.csc.scheduler.model.db;

import java.util.ArrayList;
import java.sql.Connection;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;

public interface Database<T>
{
   // Puts all of the data in the database into an ArrayList and returns it
   public ArrayList<T> getData();

   // Pulls data from SQLDB, getting the same data that is on the server
   public void pullData();

   // Adds an item to the database
   public void addData(T data);

   // Edits an item in the database
   public void editData(T newData);

   // Removes and item from the database
   public void removeData(T data);
}
