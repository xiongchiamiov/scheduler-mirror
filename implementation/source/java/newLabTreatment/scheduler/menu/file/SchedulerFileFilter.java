package scheduler.menu.file;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Represents a FileFilter object designed to display only file with the 
 * Scheduler file extension (".sdf" for Scheduler Data File)
 *
 * @author Eric Liebowitz
 * @version 22jul10
 */
public class SchedulerFileFilter extends FileFilter
{
   public SchedulerFileFilter ()
   {
      super ();
   }

   public boolean accept (File f)
   {
      return f.getName().endsWith(FileMenu.extension) || f.isDirectory();
   }

   public String getDescription ()
   {
      return "'*" + FileMenu.extension + "'";
   }
}
