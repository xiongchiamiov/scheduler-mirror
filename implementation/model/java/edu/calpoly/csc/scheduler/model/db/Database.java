package edu.calpoly.csc.scheduler.model.db;

import java.util.ArrayList;
import java.sql.Connection;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;

public interface Database<T>
{
   public ArrayList<T> getData();

   // Pulls data from SQLDB, getting the same data that is on the server
   public void pullData();
}
