package ConstraintChecker;
use strict;
use warnings;
use 5.010;
use base qw(Exporter);

#################################
# IMPORTS
#################################
use Data::Dumper;
use Instructor;
use ScheduleItem;
use Test::More; # See 'perldoc Test::More'

#################################
# EXPORTS
#################################
our @EXPORT = ();
our @EXPORT_OK = (qw(check));

#################################
# CONSTANTS
#################################
my %days = 
(
   'Mon' => 1,
   'Tue' => 2,
   'Wed' => 3,
   'Thu' => 4,
   'Fri' => 5,
);

###################################################################
#                            CODE                                 #
###################################################################

#
# Kicks off schedule verification. Takes a filename to open for opening, where
# it will find data it can parse to create ScheduleItem and Instructor object.
# (See "createInstructors" and "createScheduleItems" for details on what this
# data must look like). 
#
# This is the only function exported by this module. Thus, other packages can
# call it with any filename to verify schedule output to their heart's content.
# 
# The following constraints are checked:
#
#   - No location/instructor is double-booked
#   - Instructors are not teaches times/courses for which they specified a 
#     preference of 0
#
# $_[0] = Filename to open
#
# Returns true if all tests are passed. False otherwise. 
#
sub check
{
   my ($fn) = @_;
   my $r = 1;

   die "Need filename\n" unless $fn;
   my ($is, $s) = &gatherData($fn);

   $r &= &verifyGoodBookings($s);
   $r &= &verifyPreferenceRespect($s, $is);

   return $r;
}

#
# Verifies that every instructor isn't teaching during times which they
# specified a preference of 0 for, nor teaching courses which they specified a
# preference of 0 for. 
#
# NOTE: This will only consider instructors who are both in the generated 
#       schedule -AND- present in the IDB. Thus, the added-on-the-fly STAFF
#       instructor will not be checked for preference respect. This is how it
#       should be, as the STAFF instructor can be abused to no end. 
#
# $_[0] = Hash ref of the ScheduleItems, keyed by their ID's
# $_[1] = Hash ref of the Instructors, keyed by their ID's
#
# Returns true if all tests were passed. False otherwise. 
#
# verifyPreferenceRespect ==>
sub verifyPreferenceRespect
{
   my ($s, $is) = @_;
   my $r = 1;

   # 
   # Consider every SI
   #
   for my $si (values %{$s})
   {
      #
      # Consider every Instructor's preferences
      #
      for my $i (values %{$is})
      {
         #
         # Of course, we should only check the preference if this instructor is 
         # the one actually teaching the SI
         #
         if ($si->getInstructor() eq $i->getId())
         {
            my ($c, $ds, $s, $e) = ($si->getCourse(), 
                                    $si->getDays(), 
                                    $si->getS(), 
                                    $si->getE());
            #
            # Take advantage of the generic "checkOverDays" function. It will 
            # pass this closure two arguments:
            #
            # $_[0] = Day (number)
            # $_[1] = Time (object)
            # 
            # The closure will use these two things to lookup the Instructors 
            # preference for teaching on that day/time. As long as that 
            # preference is not 0, truth is returned. 
            #
            my $tp_test = sub 
            {
               my ($d, $t) = @_;
               $i->getTPref($d, $t->getId()) > 0; 
            };

            #
            # "ok" cries foul if the value you provide isn't "true". It's a 
            # function exported from "Test::More", which is "use"'d at the top 
            # of this file.
            # 
            $r &= ok(&checkOverDays($tp_test, $ds, $s, $e), 
               "Acceptable preference for '".$i->getId()."' to teach on ".
               "@{$ds} from ".$s->getId()." to ".$e->getId());
            #
            # Cheking course prefs won't require a closure, as I can make a 
            # quick call to the Instructor to verify that his preference for a 
            # given course isn't 0.
            #
            $r &= isnt($i->getCPref($si->getCourse()), 0, "Acceptable ".
               "preference for '".$i->getId()."' to teach '".$c."'");
         }
      }
   }
   $r;
}#<==

#
# Makes sure that no locations/instructors have been double-booked. You'd better
# read the in-line comments for this one, though: the closure magic is of 
# particular importance. 
#
# $_[0] = Hash ref of the ScheduleItem objects, keyed by their ID's
#
# Returns true if all tests are passed. False otherwise.
#
# verifyGoodBookings ==>
sub verifyGoodBookings
{
   my ($s) = @_;
   my $r = 1;

   #
   # Will keep track of when locations/instructors are booked. Each time "slot"
   # will be incremented each time it is checked. If any slot ever gets a count
   # greater than 1, we'll know more than one thing is trying to get at that 
   # time slot and can cry foul.
   #
   my %iBookings;
   my %lBookings;

   #
   # "If you're reading this, you have no idea what's going on."
   #
   #                                -Bruce Harvey
   #
   # A code ref which returns a code ref. Since checking instructor and 
   # location bookings involves similar lookups in a hash, it seemed
   # appropriate to let them use the same mechanism. 
   #
   # $_[0] = Hash ref to the bookings you'll be using/remembering
   # $_[1] = Key to lookup the bookings for a particular thing
   #
   # Returns a function which will expect two parameters: a day (number), and
   # a Time (object). Read further for what this'll do.
   #
   my $commonTest = sub 
   {
      my ($hRef, $key) = @_;
      #
      # If you don't love code refs now, I'm sorry.
      #
      # $_[0] = Day (a number)
      # $_[1] = Time (object)
      #
      # Will use these two parameters to access keys within the hash keyed
      # by the "key" in the "hRef". The time represented by these keys will be
      # incremented to show it as "in use". If a time's "in use" count exceeds
      # 1, we've reached a physical impossibility, and the function will return
      # false.
      #
      return sub
      {
         my ($d, $t) = @_;
         $hRef->{$key}{$d}{$t->getId()} ++;
         $hRef->{$key}{$d}{$t->getId()} == 1;
      }
   };

   #
   # Consider every SI for possible double-bookings
   #
   for my $si (values %{$s})
   {
      my ($i, $l, $ds, $s, $e) = ($si->getInstructor(),
                                  $si->getLocation(), 
                                  $si->getDays(), 
                                  $si->getS(), 
                                  $si->getE());

      #
      # Create custom test-closures by providing a specific hash and specific
      # key to use on them. For the locations, I'll use "%lBookings", and use
      # "l" as the key. We'll get back a function which will lookup the day(s)
      # time(s) for that particular location. For the instructors, I'll use 
      # "%iBookings", and use "i" as the key, achieving effects similar to those
      # of the location lookup. 
      #
      my $l_test = &{$commonTest}(\%lBookings, $l);
      my $i_test = &{$commonTest}(\%iBookings, $i);

      #
      # "ok" cries foul if the value you provide isn't "true". It's a function
      # exported from "Test::More", which is "use"'d at the top of this file.
      # 
      $r &= ok(&checkOverDays($l_test, $ds, $s, $e),
         "Booking '$l' on '@{$ds}' from ".$s->getId()." to ".$e->getId());
      $r &= ok(&checkOverDays($i_test, $ds, $s, $e),
         "Booking '$i' on '@{$ds}' from ".$s->getId()." to ".$e->getId());
   }
   $r;
}#<==

#
# Administers a given test over a given list of days over a given time 
# range. Of course, that sounds pretty generic: that's exactly what this 
# function is. 
#
# The parameter details may shed some light on the purpose of this function
#
# $_[0] = Code ref! This is the "test" you wish to apply across the given days
#         and times you supply. Eventually, this code ref will be called with 
#         two arguments: the day being tested (a number), and the time of that
#         day to test. Your provided closure can do whatever it likes with these
#         provides arguments, but it must provide a truth value of "0" or "1",
#         as the return will be AND'ed with other truth values to see if the 
#         test worked across all days/times
#
# $_[1] = Array ref of days (strings) to check. Valid days are:
#
#           - "Mon", "Tue", "Wed", "Thu", "Fri"
#
#         If you're interested in what these mean, take a look at the global
#         "%days" hash. (Note that they're case sensitive).
#
# $_[2] = Range's start Time object
#
# $_[3] = Range's end Time object
#
# Returns true if your provided closure returns true for all time slots in the
# supplied range on all days in the supplied list of days. Of course, your 
# closure could utterly ignore these and return true based on some god-awful
# condition. But, if you're doing that, why even call this function at all?
#
# checkOverDays ==>
sub checkOverDays
{
   my ($test, $days, $s, $e) = @_;
   my $r = 1;
   
   #
   # Each day (string) is aliased to "$_"
   #
   for (@{$days})
   {
      when (/Mon|Tue|Wed|Thu|Fri/) 
      {
         $r &= &checkOverTimeRange($test, $days{$_}, $s, $e);
      }
      default
      {
         die "Invalid day '$_'";
      }
   }

   $r;
}#<==

#
# Generic function to allow callers to administer a given test on a given day 
# over a given range of time. 
#
# Returns true if the supplied test returns true across the given time range
# on the given day. Note that times are in half-hour chunks. (So, if you were
# to check the range 9a-10a, it would check two slots: 9a-9:30a, and 
# 9:30a-10a).
#
# $_[0] = Code ref! All you need to know here is that it will be passed the 
#         day (a number), and the time to check (a Time object). You can do 
#         whatever you like with those in the closure you provide here.
# $_[1] = Day to check (a number). (See the global "%days" hash).
# $_[2] = Range start time
# $_[3] = Range end time
#
# checkOverTimeRange ==>
sub checkOverTimeRange
{
   my ($test, $d, $s, $e) = @_;
   my $r = 1;

   #
   # I alter the original start time as we go through the time range. So, before
   # tainting it, I save the original values to restore once testing is
   # complete
   #
   my ($oldH, $oldM) = ($s->getH(), $s->getM());

   until ($s->equals($e))
   {
      $r &= &{$test}($d, $s);
      $s->addHalf();
   }

   $s->setH($oldH);
   $s->setM($oldM);

   $r;
}#<==

#
# Creates data structures of Instructors and ScheduleItems from data scanned
# in from a given filename. Data is expected to be cordoned off for these items
# in the following ways:
#
#  Instructor data: Between the text "--LOCAL IDB BEGIN--" and 
#                   "--LOCAL IDB END--"
#
#  ScheduleItem data: Between the text "--SCHEDULE BEGIN--" and
#                     "--SCHEDULE END--"
#
# The text between the tags for each respective type will be passed to the 
# appropriate methods for parsing and creation of native, Perl object to 
# represent the Java data. 
#
# $_[0] = File name to get data from
#
# Returns a list of items:
#
#  - Hashref of instructors, keyed by their ID's
#  - Hash ref of ScheduleItems, keyed by their ID's
#
# gatherData ==>
sub gatherData
{
   my ($fn) = @_;
   open (my $fh, $fn) or die "$!: '$fn'";
   my $data = join("", <$fh>);

   my $is = &createInstructors(($data =~ /--LOCAL\ IDB\ BEGIN--
                                         (.*)
                                         --LOCAL\ IDB\ END/sx));
   my $s = &createScheduleItems(($data =~ /--SCHEDULE\ BEGIN--
                                           (.*)
                                           --SCHEDULE\ END/sx));
   ($is, $s);
}#<==

#
# Takes a "string" a data, splits it on "===", and creates Instructor objects
# out of the information found in between those "===". In particular, the data
# in this string must be legit, Perl code which can be used in the string 
# version of "eval" to pass arguments to the Instructor constructor. 
#
# For details on the args to the Instructorem constructor, take a look at 
# Instructor.pm. From here, you can know that the args are passed in a hash 
# (NOT a hashref). 
#
# $_[0] = String of data. May contain newlines and other forms of whitespace.
#
# Returns a hashref keyed by Instructors id's w/ Instructor objects as its
# values.
#
# createInstructors ==>
sub createInstructors
{
   #
   # Strip whitespace
   #
   $_[0] =~ s/\s//g;

   #
   # Split entries based on the "===" separator
   #
   my @iInfo = split(/===/, $_[0]);

   #
   # Go through each piece of Instructor data and "eval" it, which'll treat the 
   # string of Perl code as executable Perl code. The created object will be 
   # stored in the hashref return by this function, keyed by the id returned by 
   # the "getId()" method in the Instructor package
   #
   my %is;
   for (@iInfo)
   {
      my $i;
      eval "\$i = Instructor->new({$_});";
      die $@ if $@;
      $is{$i->getId()} = $i;
   }

   \%is;
}#<==

#
# Takes a "string" a data, splits it on "===", and creates ScheduleItem objects
# out of the information found in between those "===". In particular, the data
# in this string must be legit, Perl code which can be used in the string 
# version of "eval" to pass arguments to the SheduleItem constructor. 
#
# For details on the args to the ScheduleItem constructor, take a look at 
# ScheduleItem.pm. From here, you can know that the args are passed in a hash 
# (NOT a hashref). 
#
# $_[0] = String of data. May contain newlines and other forms of whitespace.
#
# Returns a hashref keyed by ScheduleItem id's w/ ScheduleItem objects as its
# values.
#
# createScheduleItems ==>
sub createScheduleItems
{
   #
   # Strip whitespace
   #
   $_[0] =~ s/\s//g;

   #
   # Split entries based on the "===" separator
   #
   my @sInfo = split(/===/, $_[0]);

   #
   # Go through each piece of SI data and "eval" it, which'll treat the string
   # of Perl code as executable Perl code. The created object will be stored
   # in the hashref return by this function, keyed by the id returned by the
   # "getId()" method in the ScheduleItem package
   #
   my %s;
   for (@sInfo)
   {
      my $si;
      eval "\$si = new ScheduleItem({$_});";
      die $@ if $@;
      $s{$si->getId()} = $si;
   }

   \%s;
}#<==

1;
