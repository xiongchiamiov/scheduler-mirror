package edu.calpoly.csc.scheduler.model.db;

import java.util.ArrayList;

public interface DatabaseAPI<T>
{
   // Puts all of the data in the database into an ArrayList and returns it
   public ArrayList<T> getData();

   /**
    * Use this method to save all data. It will insert new items into the
    * database or edit items in the database if the data already exists.
    */
   public void saveData(T data);

   // Removes and item from the database
   public void removeData(T data);
}
