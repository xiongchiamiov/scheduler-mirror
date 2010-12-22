package SchedulerMimics::Course;
use strict;
use warnings;
use 5.010;
use base qw (Class::Accessor);
use Data::Dumper;

my @data;
BEGIN 
{
   @data = qw(id type sections dfc wtu scu lab hpw prefix) 
}
use fields @data;
SchedulerMimics::Course->mk_accessors(@data);

sub accessor_name_for { "get\u$_[1]"; }
sub mutator_name_for  { "set\u$_[1]"; }

sub new 
{
   my ($class, $c) = @_;

   my $self = fields::new($class);
   for (grep { $_ ne "lab" } @data)
   {
      $self->{$_} = $c->{$_} // die "Need $_";
   }

   if ($c->{lab})
   {
      $self->{lab} = Course->new($c->{lab});
   }
   #
   # Must capitalize type, as Java course-type constants are all caps
   #
   $self->{type} = "\U$self->{type}";

   $self;
}

sub isLab
{
   $_[0]->{type} eq "lab";
}

sub makeNewForJava
{
   my ($self) = @_;

   my $labName = "null";
   if ($self->getLab())
   {
      $labName = $self->{lab}->getName();
   }

   #
   # Have to "quote" strings for Java
   #
   # Required equipment parameters are all 'false'
   # Max student-count is 35
   # Prefix is "cpe"
   #
   my $params = join(", ", '"'.$self->getName().'"',
                           $self->getId(),
                           $self->getWtu(),
                           $self->getScu(),
                           "Course.".$self->getType(),
                           35,
                           $self->getSections(),
                           $labName,
                           "new RequiredEquipment(false, false, false)",
                           "\"cpe\"",
                           $self->getDfc(),
                           $self->getHpw(),
                           "\"".$self->getPrefix()."\"");
   "new Course ($params);";
}

sub getName
{
   my $self = shift;

   join("_", "cpe".$self->getId(), $self->getSections(), $self->getType());
}

1;
