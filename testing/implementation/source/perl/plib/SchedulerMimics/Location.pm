package SchedulerMimics::Location;
use strict;
use warnings;
use 5.010;
use base qw(Class::Accessor);

my @data;
BEGIN { @data = qw(type bldg room); }
use fields (@data);
SchedulerMimics::Location->mk_accessors(@data);

sub accessor_name_for { "get\u$_[1]" }
sub  mutator_name_for { "set\u$_[1]" }

sub new
{
   chomp (my ($class, $type, $bldg, $room) = @_);

   my $self = fields::new($class);
   $self->{type} = "\U$type";
   $self->{bldg} = $bldg;
   $self->{room} = $room;

   $self;
}

sub getNewForJava
{
   my ($self) = @_;

   "new Location(\"$self->{bldg}\", \"$self->{room}\", 35, ".
      "Course.$self->{type}, false, false, false, false);";
}

sub getName
{
   my ($self) = @_;

   "$self->{type}_$self->{bldg}_$self->{room}";
}

1;
