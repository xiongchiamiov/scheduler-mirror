Tpackage RandDataGenerator;
use strict;
use warnings;
use 5.010;
use base qw(Class::Accessor);

#################################
# FIELDS
#################################
our @data;
BEGIN { @data = qw(minCourse maxCourse courses instructors locations) }
use fields (@data);

#################################
# IMPORTS
#################################
use Data::Dumper;
use Getopt::Long;
use String::Random;
use Time;

#################################
# EXPORTS
#################################
our @EXPORT = ();
our @EXPORT_OK = ();

#################################
# GLOBALS
#################################
my %dfcs = 
(
   0 => "MWF",
   1 => "TR",
);
my %days =
(
   0 => "Week.MON",
   1 => "Week.TUE",
   2 => "Week.WED",
   3 => "Week.THU",
   4 => "Week.FRI",
);

#################################

#
# Pretty obvious: creates a new RandomDataGenerator object. Takes arguments to 
# limit the size of the data generated. You'll use the returned object to call
# methods to generate the data.
#
# $_[0] = Class
# $_[1] = Hash of args to apply to the generator. Key/value pairs are:
#
#   - courses     => # of courses     you wish to generate. Limit is 899 
#   - instructors => # of instructors you wish to generate
#   - locations   => # of locations   you wish to generate
#
# new ==>
sub new
{
   my ($class, %args) = @_;

   my $self = fields::new($class);

   #
   # Course's must be 3 digits, so we'll start out all courses at the first 
   # valide, 3 digit course: 100. Of course, w/ the limit of 3-digit courses, 
   # we can only go as high as 999.
   #
   $self->{minCourse}   = 100;
   $self->{maxCourse}   = 999;
   $self->{courses}     = $args{courses}     // 0;
   $self->{courses} += $self->{minCourse};
   if ($self->{courses} > $self->{maxCourse})
   {
      die "Error: Cannot have more than 899 courses\n";
   }

   $self->{instructors} = $args{instructors} // 0;
   $self->{locations}   = $args{locations}   // 0;

   $self;
}#<==

#
# Makes random course, instructor, and location information.
#
# $_[0] = Self
# $_[1] = Filename where you want course info to go
# $_[2] = Filename where you want instructor info to go
# $_[3] = Filename where you want location info to go
#
# makeData ==>
sub makeData
{
   my ($self, $c, $i, $l) = @_;

   $self->makeCourses($c);
   $self->makeInstructors($i);
   $self->makeLocations($l);

}#<==

#
# Creates random data to represent locations. This data will be output to a 
# file in a format suitable to be read by the corresponding functions in 
# Converter.pm. Currently random data are:
#
#  - Whether the location is a lec/lab room
#  - The bldg number
#  - The room number
# 
# $_[0] = Self
# $_[1] = Name of file you wish to output data to
#
# makeLocations ==>
sub makeLocations
{
   my ($self, $fn) = @_;

   #
   # Makes sure we don't randomly generate the same location twice
   #
   my %created;

   open (my $fh, ">", $fn) or die $!;
   select $fh;

   for (my $l = 0; $l < $self->{locations}; $l ++)
   {
      #
      # 50/50 change to be a lecture or lab room
      #
      my $type = (int(rand(2))) ? "lec" : "lab";

      #
      # Keep generating new locations until a unique one is found
      #
      my ($bldg, $room);
      do
      {
         $bldg = int(rand($self->{locations}));
         $room = int(rand($self->{locations}));
      } while (defined $created{"${bldg}$room"});
      $created{$bldg}{$room} ++;

      say "$type:$bldg:$room";
   }

   select STDOUT;
}#<==

#
# Creates random data to represent instructors. This data will be output to a 
# file in a format suitable to be read by the corresponding functions in 
# Converter.pm. Currently random data are:
#
#  - Instructor name (first and last)
#  - WTU (between 10 and 20)
#  - Course preferences (pref for every course will range from 0 to 10)
#  - Time preferences (prefs from 00:00 to 23:30 will range from 0 to 10)
#
# $_[0] = Self
# $_[1] = File you wish to output data to. 
#
# makeInstructors ==>
sub makeInstructors
{
   my ($self, $fn) = @_;

   open (my $fh, ">", $fn) or die $!;
   select $fh;

   #
   # Handy module to make random strings
   #
   my $randStrings = String::Random->new();

   for (my $i = 0; $i < $self->{instructors}; $i ++)
   {
      my %info;
      #
      # The names are random, 7-character strings
      #
      $info{first} = $randStrings->randpattern("ccccccc");
      $info{last}  = $randStrings->randpattern("ccccccc");

      #
      # 10 <= WTU <= 20
      #
      $info{wtu} = int(rand(11) + 10);

      #
      # This'll only take effect if you specified a number of courses to 
      # generate. If you did, this will generate a random preference for each
      # of those courses
      #
      for (my $c = $self->{minCourse}; $c < $self->{courses}; $c ++)
      {
         $info{cPrefs}{$c} = int(rand(11));
      }

      #
      # Assigns a random pref from 0-10 for time from 00:00 to 23:30 across
      # all days present in the %days hash
      #
      for (values %days)
      {
         my $s = Time->new("00:00");
         my $e = Time->new("24:00");
         until ($s->equals($e))
         {
            $info{tPrefs}{$_}{$s->getId()} = int(rand(11));
            $s->addHalf();
         }
      }

      #
      # Seems like a great time to take advantage of Dumper's ability to print
      # legit, Perl code to represent its arguments. I don't have to worry about
      # the exact formatting of the output. It will!.
      #
      print "my ";       # Have to tack on a lexical scope to the output
      say Dumper \%info;
   }


   select STDOUT;
}#<==

#
# Creates Course information appropriate for passing on to "Converter.pm". This
# data will be output to a file. Data generated here will be randomized as much
# as is reasonably possible. Currently "random" data are:
#
#  - Number of sections (range from 1 - 10)
#  - Existence of a "lab" component
#  - Whether the course is taught MWF or TR (this may become better in the 
#    future)
#  - If it doesn't have a lab, the number of units for a course can range from
#    1 to 4
#
# $_[0] = Self
# $_[1] = Filename you want the output to be sent to
#
# makeCourses ==>
sub makeCourses
{
   my ($self, $fn) = @_;

   open (my $fh, ">", $fn) or die "$!: '$fn'";
   select $fh;

   #
   # Start at course # 100 and increment to the limit
   #
   for (my $c = $self->{minCourse}; $c < $self->{courses}; $c ++)
   {
      my %info;
      #
      # Sections will range from 1 to 10
      #
      my $sections = int(rand(10) + 1);
      #
      # 50/50 chance there'll be a lab component
      #
      my $hasLab = int(rand(2));
      #
      # Decide whether to teach MWF or TR
      #
      my $dfc = $dfcs{int(rand(2))};
      #
      # Teach 1-4 hours per week
      #
      my $hpw = int(rand(4) + 1);

      $info{id} = $c;
      $info{type} = 'lec';
      $info{sections} = $sections;
      $info{dfc} = $dfc;
      $info{wtu} = $info{scu} = $info{hpw} = $hpw;
      $info{prefix} = "LEC";
      #
      # Create the lab portion, if necessary. Currently, de-coupled, 3-hour 
      # labs are not supported
      #
      if ($hasLab)
      {
         $info{lab} = 
         {
            id => $c,
            type => 'lab',
            sections => $sections,
            dfc => $dfc,
            wtu => $info{wtu},
            scu => $info{scu},
            hpw => $info{hpw},
            prefix => "LAB",
         };
      }

      #
      # Seems like a great time to take advantage of Dumper's ability to print
      # legit, Perl code to represent its arguments. I don't have to worry about
      # the exact formatting of the output...it will.
      #
      print "my ";
      say Dumper \%info;
   }

   select STDOUT;
}#<==

#
# Handy function to indent a line of text
# 
# $_[0] = Number of spaces to indent
# $_[1..] = Any/all thing to indent
#
sub indent
{
   " " x shift() . "@_";
}
