package scheduler.generate;

import java.io.Serializable;

import scheduler.db.coursedb.*;
import scheduler.db.instructordb.*;

public class TBA implements Serializable
{
   private Course c;
   private Instructor i;

   public TBA () { }

   public TBA (Course c, Instructor i)
   {
      this.c = new Course (c);
      this.i = new Instructor (i);
   }

   public Course getC (Course c)
   {
      return this.c;
   }

   public Instructor getI (Instructor i)
   {
      return this.i;
   }

   public void setC (Course c)
   {
      c = new Course (c);
   }

   public void setI (Instructor i)
   {
      i = new Instructor (i);
   }

   public String toString()
   {
      return String.format("'%s' :: '%s'", c, i);
   }
}
