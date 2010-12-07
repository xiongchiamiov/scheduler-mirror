package scheduler.generate;

public class ScheduledCourse implements Comparable<ScheduledCourse>
{
   protected DaysAndTime lec_dat;
   protected DaysAndTime lab_dat;

   public ScheduledCourse (DaysAndTime lec_dat, DaysAndTime lab_dat)
   {
      this.lec_dat = lec_dat;
      this.lab_dat = lab_dat;
   }

   public int compareTo (ScheduledCourse sc)
   {
      return 0;
   }
}
