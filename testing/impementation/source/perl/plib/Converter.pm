package Converter;
use strict;
use warnings;
use 5.010;
use base qw(Exporter Class::Accessor);

#################################
# IMPORTS
#################################
use Data::Dumper;
use Course;
use Instructor;
use Location;
use File::Basename;
use Getopt::Long;

#################################
# EXPORTS
#################################
our @EXPORT = ();
our @EXPORT_OK = qw(convert getC_Data);

#################################
# FIELDS
#################################
my @data;
my @otherData;
BEGIN
{
   @data = qw(cfile ifile lfile name dumpFile);
   @otherData = qw(path cs is ls dfcs);
}
use fields (@data, @otherData);
Converter->mk_accessors(@data, @otherData);

sub accessor_name_for { "get\u$_[1]" }
sub mutator_name_for  { "set\u$_[1]" }

#################################
# CONSTANTS
#################################
my $prefix  = "CPE";

#################################
# GLOBALS
#################################
my %dfc;

###################################################################
#                             METHODS                             #
###################################################################

sub new 
{
   my ($class, $args) = @_;
   
   my $self = fields::new($class);
   for (@data)
   {
      $self->{$_} = $args->{$_} // die "Need '$_'";
   }

   $self;
}


sub convert
{
   my ($self) = @_;
   
   #
   # Allow user to supply a path to the file. Also, make the ".java" extension
   # optional. I'll add it myself, so I can be sure it's there
   #
   ($self->{name}, $self->{path}) = fileparse ($self->{name}, qw(.java));

   #
   # Open up each file and read in the data. You'll likely -not- want to 
   # override these methods, as they depend on well-formatted data. If you want
   # a layout for how that data must look, check out the documentation for the
   # RandDataGenerator and the output it creates
   #
   eval
   {
      $self->getC_Data();
   };
   die "Error in file '$self->{cfile}'. Error $@\n" if $@;
   
   eval
   {
      $self->getI_Data();
   };
   die "Error in file '$self->{ifile}'. Error $@\n" if $@;
   eval
   {
      $self->getL_Data();
   };
   die "Error in file '$self->{lfile}'. Error $@\n" if $@;

   $self->makeClass ();
}

# getC_Data ==>
#
# Gathers all the data in the given file into Course objects
#
sub getC_Data
{
   my ($self) = @_;

   open (my $fh, "$self->{cfile}") or die "$!\n";

   # Change input delimiter to ";"
   $/ = ";\n";

   while (<$fh>)
   {
      chomp;
      if (/TODO/ or /^\s*$/)
      {
         next;
      }

      my $c;
      eval "\$c = Course->new($_);";
      die $@ if $@;
      $self->{cs}{$c->getId()} = $c;
         
      #
      # Add course's DFC's as they're needed
      #
      $self->{dfcs}{$c->getDfc()} ++;
      if ($c->getLab())
      {
         $self->{dfcs}{$c->getLab()->getDfc()} ++;
      }
   }

   # Chane input delimiter back
   $/ = "\n";
}#<==

# getI_Data ==>
sub getI_Data
{
   my ($self) = @_;
   open (my $fh, $self->{ifile}) or die $!;

   # Change input delimiter for this one
   $/ = ";\n";

   my  %is;
   while (<$fh>)
   {
      chomp;
      next if /^\s*$/;

      my $i;
      eval "\$i = Instructor->new($_);";
      die $@ if $@;
      $self->{is}{$i->getId()} = $i;
   }

   # Change input delimiter back
   $/ = "\n";
}#<==

# getL_Data ==>
sub getL_Data
{
   my ($self) = @_;
   open (my $fh, $self->{lfile}) or die $!;

   while (<$fh>)
   {
      next if (/^TODO/ or /^\s*$/);
      my ($type, $bldg, $room) = split(/:/);
      my $l = Location->new($type, $bldg, $room);
      $self->{ls}{$l->getName()} = $l;
   }

}#<==

# makeClass ==>
#
# Note: I "select" the filehandle where the Java code will go. So calls to "say"
#       and "print" will, by default, go to the file
#
sub makeClass
{
   my ($self) = @_;

   open (my $fh, ">", "$self->{path}$self->{name}.java") or 
      die "$!: $self->{name}.java";
   #
   # Default output is now the file!
   #
   select $fh;


   # Import packages
   $self->importPackages();

   # Begin class
   say "public class $self->{name}\n{";

   # Global data
   $self->makeGlobalData(3);

   # Main
   $self->makeMain();

   # Make Instructor init'er methods
   $self->makeI_inits();

   # End class
   say "}";

   #
   # Default output is STDOUT
   #
   select STDOUT;
}#<==

# importPackages  ==>
sub importPackages
{
   my ($self) = @_;

   say "import scheduler.*;";
   say "import scheduler.db.Time;";
   say "import scheduler.db.coursedb.*;";
   say "import scheduler.db.instructordb.*;";
   say "import scheduler.db.locationdb.*;";
   say "import scheduler.db.preferencesdb.*;";
   say "import scheduler.generate.Week;";
   say "import scheduler.menu.schedule.allInOne.*;";
   say "";

   say "import java.util.ArrayList;";
   say "import java.util.HashMap;";
   say "import java.util.LinkedHashMap;";
   say "import java.util.Vector;";
   say "import java.awt.*;";
   say "import java.awt.event.*;";
   say "import java.io.*;";

   say "";
}#<==

# makeGlobalData ==>
sub makeGlobalData
{
   my ($self, $depth) = @_;

   say &indent($depth, "/* DFC's */");
   $self->makeDFCs($depth);
   say "";

   say &indent($depth, "/* COURSES */");
   $self->makeCourses($depth);
   say "";

   say &indent($depth, "/* LOCATIONS */");
   $self->makeLocations($depth);
   say "";
}#<==

# makeDFCs ==>
sub makeDFCs
{
   my ($self, $depth) = @_;
   for (keys %{$self->getDfcs()})
   {
      say &indent($depth, $self->makeDFC($_, 5));
   }
}#<==

# makeDFC ==>
sub makeDFC
{
   my ($self, $dayStr, $weight) = @_;
   my @days = $self->parseAndMakeWeekList($dayStr);

   "public static final DaysForClasses $dayStr = ".
      "new DaysForClasses (\"$dayStr\", ".
                          "$weight, ".
                          "new int[] { ".join(", ", @days)." });";
}#<==

# parseAndMakeWeekList ==>
sub parseAndMakeWeekList
{
   my ($self, $days) = @_;
   my @r;
   for (split (//, $days))
   {
      given ($_)
      {
         when (/m/i) { push (@r, "Week.MON")    }
         when (/t/i) { push (@r, "Week.TUE")    }
         when (/w/i) { push (@r, "Week.WED")    }
         when (/r/i) { push (@r, "Week.THU")    }
         when (/f/i) { push (@r, "Week.FRI")    }
         default     { die "Invalid day '$_'" }

      }
   }
   @r;
}#<== parseAndMakeWeekList

# makeCourses ==>
sub makeCourses
{
   my ($self, $level) = @_;

   #
   # Courses are printed in ascending Id's
   #
   # 'print' b/c I need better control of '\n'. It's tacked on on 
   # "makeCourse"
   #
   for my $c (sort { $a->getId() <=> $b->getId() } values %{$self->getCs()})
   {
      #
      # Lab comes first so Lec can use it. Will do nothing if there is no lab.
      #
      print &indent($level, $self->makeCourse($c->getLab()));
      print &indent($level, $self->makeCourse($c));
   }
}#<==

# makeCourse ==>
sub makeCourse
{
   my ($self, $c) = @_;

   return "" unless $c; # For when you pass in the "lab" which doesn't exist

   "public static final Course ".$c->getName()." = ".$c->makeNewForJava()."\n";
}#<==

# makeLocations ==>
sub makeLocations
{
   my ($self, $depth) = @_;

   #
   # The office every instructor will be given (couldn't do null for some
   # reason)
   #
   say &indent($depth, "public static final Location l = ".
      "new Location (14, 200);");
   for (values %{$self->getLs()})
   {
      print &indent($depth, $self->makeLocation($_));
   }
}#<==

# makeLocation ==>
sub makeLocation
{
   my ($self, $l) = @_;

   "public static final Location ".$l->getName()." = ".$l->getNewForJava()."\n";
}#<==

# makeMain ==>
sub makeMain
{
   my ($self) = @_;
   my $s = "scheduler";

   # Begin main
   say &indent(3, "public static void main (String[] argv)");
   say &indent(3, "{");

   say &indent(6, "Scheduler $s = new Scheduler();\n");

   #
   # Will compile lists of course, instructor, and location information in 
   # Java, and subsequently pass the returned java variable names representing
   # this information to a method which will populate the databases w/ said
   # information
   #
   $self->populateDatabases(6, $s, $self->makeMainData(6, $s));

   #
   # Determines the purpose of main. By default, this'll spawn the scheduler, 
   # generate a schedule, and call Scheduler.dumpAsPerlTest, outputting all
   # information relevant to testing into a file $self->{dumpFile}
   #
   $self->whatMainDoes(6, $s);

   # End main
   say &indent(3, "}\n");
}#<==

# makeMainData ==>
sub makeMainData
{
   my ($self, $depth, $s) = @_;

   # Local LDB
   my $ls = $self->makeLocalLDB(6, $s);
   say "";

   # Local CDB
   my $cs = $self->makeLocalCDB(6, $s);
   say "";

   # Local IDB: Must happen after LDB and CDB have been set
   my $is = $self->makeLocalIDB(6, $s);
   say "";

   return ($ls, $cs, $is);
}#<==

# populateDatabases ==>
sub populateDatabases
{
   my ($self, $d, $s, $ls, $cs, $is) = @_;

   say &indent($d, "$s.setLocalLDB($ls);");
   say &indent($d, "$s.setLocalCDB($cs);");
   say &indent($d, "$s.setLocalIDB($is);");

}#<==

# whatMainDoes ==>
sub whatMainDoes
{
   my ($self, $d, $s) = @_;

   #
   # Use the "Progress" class to let me see the progress of a shedule dump
   # while testing. Useful for debugging purposes
   #
   #print &indent($d, "Progress p = new Progress(");
   print &indent($d, "Scheduler.schedule.generate(");
   print "new Vector<Course>($s.getLocalCDB()), ";
   print "new Vector<Instructor>($s.getLocalIDB()), ";
   print "new Vector<Location>($s.getLocalLDB()), ";
   print "new Vector<SchedulePreference>(),";
   print "null);";
   #say &indent($d, "p.execute();");

   #
   # We'll have to wait for the SwingWorker to finish before printing our
   # generated Schedule
   #
   #print &indent($d, "while (p.getState() != ");
   #say "javax.swing.SwingWorker.StateValue.DONE)";
   #say &indent($d, "{");
   #say &indent($d + 3, "try { Thread.sleep(1000); }");
   #say &indent($d + 3, "catch (Exception e) { e.printStackTrace(); }");
   #say &indent($d, "}");

   say &indent($d, "try");
   say &indent($d, "{");
   say &indent($d + 3, "$s.dumpAsPerlText(new PrintStream(".
      "new File(\"$self->{dumpFile}\")));");
   say &indent($d, "}");
   say &indent($d, "catch (FileNotFoundException e)");
   say &indent($d, "{");
   say &indent($d + 3, "System.err.println (\"Couldn't open ".
      "'$self->{dumpFile}'\");");
   say &indent($d, "}");
   print &indent ($d, "System.err.println(\"HERE\\n\" + $s.getSchedule());");
   say &indent($d, "System.exit(0);");
}#<==

# makeLocalLDB ==>
#
# Returns the name of the Java object which holds the data ("ls")
#
sub makeLocalLDB
{
   my ($self, $depth, $s) = @_;

   say &indent($depth, "Vector<Location> ls = new Vector<Location>();");
   for (map { $_->getName() } values %{$self->getLs()})
   {
      say &indent($depth, "ls.add($_);");
   }

   "ls";
}#<==

# makeLocalCDB ==>
#
# Returns the name of the Java object which holds the data ("cs")
#
sub makeLocalCDB
{
   my ($self, $depth, $s) = @_;

   # Put data in a Vector. Course objects are made in "makeGlobalData"
   say &indent($depth, "Vector<Course> cs = new Vector<Course>();");
   for ( map  { $_->getName() } 
         sort { $a->getId() <=> $b->getId() } values  %{$self->getCs()})
   {
      say &indent($depth, "cs.add($_);");
   }

   "cs";
}#<==

# makeLocalIDB ==>
#
# MUST HAPPEN AFTER COURSES/LOCATIONS ARE ADDED TO THE SCHEDULER'S RESPECTIVE
# DATABASES! ELSE, BAD THINGS HAPPEN!
#
# *ahem*
#
# If the courses and locations are not already created, this will try and create
# an instructor which references those unmade pieces of data. Java won't like 
# that, as things declared are available for use in the order of their 
# declaration. 
#
# Returns the name of the Java object which holds the data ("is")
#
sub makeLocalIDB
{
   my ($self, $depth, $s) = @_;

   say &indent($depth, "/* INSTRUCTORS */");
   $self->makeInstructors($depth);

   say &indent($depth, "Vector<Instructor> is = new Vector<Instructor>();");
   for (map { $_->getId() } 
           sort { $a->getId() cmp $b->getId() } 
              values %{$self->getIs()})
   {
      say &indent($depth, "is.add($_);");
   }

   "is";
}#<==

# makeInstructors ==>
sub makeInstructors
{
   my ($self, $depth) = @_;

   for my $i (sort { $a->getId() cmp $b->getId() } values %{$self->getIs()})
   {
      $self->makeInstructor($depth, $i);
   }
}#<==

# makeInstructor ==>
sub makeInstructor
{
   my ($self, $depth, $i) = @_;

   say &indent($depth, $_) for ($i->makeNewForJava($self->getCs()));
}#<==

# makeI_inits ==>
sub makeI_inits
{
   my ($self) = @_;

   for my $i (values %{$self->getIs()})
   {
      say &indent(3, $_) for $i->makeInitMethod($self->getCs());
   }
}#<==

# indent ==>
#
# $_[0] = Indentation level
# $_[1...] = Rest of message to print
#
sub indent
{
   unless ($_[1])
   {
      "";
   }
   else
   {
      " " x shift(@_) . "@_";
   }
}#<==


1;
