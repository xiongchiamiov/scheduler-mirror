# POD ==>
=pod

=head1 NAME

TestUtils - A collection of functions to help facilitate testing of the 
Scheduler Project (esp. its "dumps" of schedule data). 

=head1 REQUIREMENTS

=over

=item - Perl v. 5.10 (or higher)

=item - An understanding of how schedule data is dumped, as documented
TODO: HERE

=back

=head1 SYNOPSIS

 use TestUtils qw(createInstructors createScheduleItems);

 open (my $fh, "someFile") or die $!;
 my ($data) = join("", <$fh>);

 my $instructors   = createInstructors($data);
 my $scheduleItems = createScheduleItems($data);



=head1 DESCRIPTION

The current testing framework relies on parsing a "dump" file of schedule data
after a schedule has been generated, loading that into various objects, and
performing tests on the data therein. By simply adding more data to these dumps,
further tests can easily be performed to the user's heart's content. 

Functions placed here are to provide the user with convenient ways to 
parse data and utilize basic testing tools to extend the current testing 
framework. So, when one adds more data to be parsed from a schedule dump, he
won't have to worry about parsing the data already present in said dump: these
functions will do it for him. 

=cut
#END POD<==
package TestUtils;
use strict;
use warnings;
use 5.010;
use base qw(Exporter);

#################################
# CONSTANTS
#################################
# POD ==>
=pod

=head1 CONSTANTS

The following are package-wide variables which can alter some of the behavior
of the functions contains in this module. 

=over

=item - $DATA_DELIMITER: Initial value of '==='. Used to separate continguous 
chunks of data for functions which parse strings of data (such as 
C<createInstructors> and C<createScheduleItems>.

=back

=cut
#<== END POD
our $DATA_DELIMITER = '===';

#################################
# EXPORTS
#################################
# POD ==>
=head1 EXPORTS

None, by default. 

By request, C<createScheduleItems, createInstructors>.

Tagged import are available as follows:

   :all
      :parsers
         createInstructors
         createScheduleItems

=cut
#<== END POD 
our @EXPORT = ();
our @EXPORT_OK = qw(createInstructors createScheduleItems);
our %EXPORT_TAGS =
(
   all => [@EXPORT, @EXPORT_OK],
   parsers => [qw(createInstructors createScheduleItems)],
);

###################################################################
#                           FUNCTIONS                             #
###################################################################

# POD ==>
=pod

=head1 FUNCTIONS

=head2 createInstructors

 my $instructors = createInstructors($someDataString);

=head3 Takes

A string of data which is to be parsed into individual, 
SchedulerMimics::Instructor objects. The arguments you pass in can
be multiple variables, as they will be treated as one line such as C<my $data = 
join ("", @_);> would yield. 

=head3 Description

Takes a string and creates Instructor objects. That's really all there is to it.
However, the syntax of the data passed in is very important. 

=head4 Data Syntax

Instructor data must be located between two tags, each of which must be on 
its own line:

 --LOCAL IDB BEGIN--

 # Data...

 --LOCAL IDB END--

Individual pieces of data between these tags must be delineated by the "===" 
token, which must also be on its own line. (Actually, it must be delineated
on the $TestUtils::DATA_DELIMITER value, which is, by default, '===').

The information found by the above constrains must be legitimate Perl code (i.e.
that which could be evaluated in a Perl 'eval' [string form] block). In 
particular, a given "chunk" of data must be able to run when subjected to the
following line of code:

 my $chuck = <$fh>; # Parsed according to the above rules
 my $obj;

 eval "\$obj = SchedulerMimics::Instructor->new({$chunk});

Thus, the "chunk" must be valid, Perl-hash syntax; able to be wrapped into a 
hashref with '{}'. For more details on data is necessary to make the 
L<SchedulerMimics::Instructor> constructor happy, take a look at its
documentation.

=head3 Returns

A hashref of SchedulerMimics::Instructor objects created from the data passed 
in. Were there any problems in parsing the data, the value of this return is not
guaranteed. The hash is keyed by the Instructor's ID's as given by their "getId"
method.

=cut
#<== END POD
# createInstructors ==>
sub createInstructors
{
   my $line = join("", @_);
   #
   # Strip whitespace
   #
   $line =~ s/\s//g;

   #
   # Split entries based on the "===" separator
   #
   my @iInfo = split(/$DATA_DELIMITER/, $line);

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
      eval "\$i = SchedulerMimics::Instructor->new({$_});";
      die $@ if $@;
      $is{$i->getId()} = $i;
   }

   \%is;
}#<==

# POD ==>

=head2 createScheduleItems

 my $scheduleItems = createScheduleItems($someDataString)

=head3 Takes

A string of data which is to be parsed into individual, 
SchedulerMimics::ScheduleItem objects. The arguments you pass in can
be multiple variables, as they will be treated as one line such as 
C<my $data = join ("", @_);> would yield. 

=head3 Description

Takes a string (wit newlines and everything else) and creates ScheduleItem 
objects out of it. That's really it. It's the formatting of the data you pass 
in which is important. 

=head4 Data Syntax

Schedule data must also be located between two tags, each of which must be on
its own line:

 --SCHEDULE BEGIN--

 # Data...

 --SCHEDULE END--

Individual pieces of data between these tags must be delineated by the '==='
token, which must also be on its own line. (Actually, it must be delineated
on the $TestUtils::DATA_DELIMITER value, which is, by default, '===').

The information found by the above constrains must be legitimate Perl code (i.e.
that which could be evaluated in a Perl 'eval' [string form] block). In 
particular, a given "chunk" of data must be able to run when subjected to the
following line of code:

 my $chuck = <$fh>; # Parsed according to the above rules
 my $obj;

 eval "\$obj = SchedulerMimics::ScheduleItem->new({$chunk});

Thus, the "chunk" must be valid, Perl-hash syntax; able to be wrapped into a 
hashref with '{}'. For more details on data is necessary to make the 
L<SchedulerMimics::ScheduleItem> constructor happy, take a look at its
documentation.

=head3 Returns

A hashref of SchedulerMimics::ScheduleItem objects created from the data passed 
in. Were there any problems in parsing the data, the value of this return is not
guaranteed. The hash is keyed by the ScheduleItem's ID's as given by their 
"getId" method.

=cut
#<== END POD
# createScheduleItems ==>
sub createScheduleItems
{
   my $line = join("", @_);
   #
   # Strip whitespace
   #
   $line =~ s/\s//g;

   #
   # Split entries based on the "===" separator
   #
   my @sInfo = split(/$DATA_DELIMITER/, $line);

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
      eval "\$si = new SchedulerMimics::ScheduleItem({$_});";
      die $@ if $@;
      $s{$si->getId()} = $si;
   }

   \%s;
}#<==

1;
