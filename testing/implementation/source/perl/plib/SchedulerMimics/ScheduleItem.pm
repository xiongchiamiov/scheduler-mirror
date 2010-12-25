package SchedulerMimics::ScheduleItem;
use strict;
use warnings;
use 5.010;
use base qw(Class::Accessor);

#################################
# IMPORTS
#################################
use Data::Dumper;
use SchedulerMimics::Time;

#################################
# FIELDS
#################################
my @data;
BEGIN { @data = qw(id course instructor location s e days value) }
use fields (@data);

SchedulerMimics::ScheduleItem->mk_accessors(@data);
sub accessor_name_for { "get\u$_[1]"; }
sub mutator_name_for  { "set\u$_[1]"; }

###################################################################
#                          METHODS                                #
###################################################################

sub new
{
   my ($class, $args) = @_;

   my $self = fields::new($class);

   for (@data)
   {
      $self->{$_} = "$args->{$_}" or die "'$_' cannot be undef";
   }

   #
   # Turn "days" into an array
   #
   $self->{days} =~ s/([A-Z]..)/$1 /g;
   $self->{days} = [split(/ /, $self->{days})];

   $self->{s} = SchedulerMimics::Time->new($self->{s});
   $self->{e} = SchedulerMimics::Time->new($self->{e});

   $self;
}

1;
