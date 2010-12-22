package SchedulerMimics::Instructor;
use strict;
use warnings;
use 5.010;
use base qw (Class::Accessor);
use Data::Dumper;

my @data;
BEGIN { @data = qw (first last cPrefs tPrefs wtu id); }
use fields (@data);
SchedulerMimics::Instructor->mk_accessors(@data);

sub accessor_name_for { "get\u$_[1]" }
sub mutator_name_for  { "set\u$_[1]" }

sub new
{
   my ($class, $args) = @_;
   
   #
   # Init all the fields
   #
   my $self = fields::new($class);
   for (grep { $_ ne "id" } @data)
   {
      $self->{$_} = $args->{$_} // die "Need '$_'";
   }

   my $first = "\L$self->{first}";
   my $last  = "\L$self->{last}";
   $first =~ s/\W//g;
   $last  =~ s/\W//g;

   # First initial + first 7 letters of last name, all lowercase
   $self->{id} = substr($first, 0, 1).substr($last, 0, 7);

   $self;
}

sub getName 
{
   my ($self) = @_;

   "$self->{last}, $self->{first}";
}

sub getTPref
{
   my ($self, $d, $t) = @_;

   $self->{tPrefs}{$d}{$t};
}

sub getCPref
{
   my ($self, $c) = @_;

   $self->{cPrefs}{$c};
}

sub makeNewForJava
{
   my ($self, $cs) = @_;
   my @r;

   my $params = join(", ", "\"$self->{first}\"",
                           "\"$self->{last}\"",
                           "\"$self->{id}\"",
                           $self->{wtu},
                           "l",);
   # Instructor declaration and init-method call
   push (@r, "Instructor $self->{id} = new Instructor($params);");
   push (@r, "init_$self->{id}($self->{id});");

   @r;
}

sub makeInitMethod
{
   my ($self, $cs) = @_;
   my @r;

   push (@r, "public static void init_$self->{id} (Instructor $self->{id})");
   push (@r, "{");
   push (@r, map { " " x 3 . $_ } $self->makeCPrefs($cs));
   push (@r, map { " " x 3 . $_ } $self->makeTPrefs());
   push (@r, "}");

   @r;
}

sub makeCPrefs
{
   my ($self, $cs) = @_;
   my $list = "$self->{id}_cPrefs";
   my @r;

   push (@r, "/* CPREFS */");
   push (@r, "ArrayList<CoursePreference> $list = ".
      "new ArrayList<CoursePreference>();");

   for (values %{$cs})
   {
      #
      # A safety measure. Shouldn't have to be taken, since the iInfo file is
      # generated based on the cInfo file, so instructor's should have 
      # preferences for courses that don't exist, and vice versa
      #
      if (!defined $self->{cPrefs}->{$_->getId()})
      {
         $self->{cPrefs}->{$_->getId()} = 5;
      }
      push(@r, "$list.add(new CoursePreference(".
         join(", ", $_->getName(), $self->{cPrefs}->{$_->getId()})."));");
   }

   push (@r, "$self->{id}.setCoursePreferences($list);\n");

   @r;
}

sub makeTPrefs
{
   my ($self) = @_;
   my $var = "$self->{id}_tPrefs";
   my @r;

   push (@r, "/* TPREFS */");
   push (@r, "HashMap<Integer, LinkedHashMap<Time, TimePreference>> $var = ".
      "new HashMap<Integer, LinkedHashMap<Time, TimePreference>>();");
   
   for my $d (keys %{$self->{tPrefs}})
   {
      push (@r, "$var.put($d, new LinkedHashMap<Time, TimePreference>());\t".
         "/* $d */");
      for my $t (keys %{$self->{tPrefs}{$d}})
      {
         push (@r, "$var.get($d).put(new Time(\"$t\"), ".
            "new TimePreference(new Time(\"$t\"), $self->{tPrefs}{$d}{$t}));");
      }
   }

   push (@r, "$self->{id}.setTimePreferences($var);\n");

   @r;
}

1;
