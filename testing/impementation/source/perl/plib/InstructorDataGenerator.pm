package InstructorDataGenerator;
use strict;
use warnings;
use 5.010;
use base qw(Exporter);

#################################
# EXPORTS
#################################
our @EXPORT = ();
our @EXPORT_OK = qw(gen);

#################################
# IMPORTS
#################################
use Course;
use Data::Dumper;
use Scalar::Util qw(looks_like_number);

###################################################################
#                           FUNCTIONS                             #
###################################################################

sub gen 
{
   my ($iFile, $cData, $iData) = @_;

   my @cs = &gatherCourseIds($cData);
   my @is = &gatherInstructorInfo(\@cs, $iData);

   open (my $fh, ">", $iFile);
   say $fh "$_" for @is;
}

sub gatherCourseIds
{
   my (@data) = split(/;\n/, shift);
   my @r;
   
   for (@data)
   {
      next if /TODO/;
      
      my $info;
      eval "\$info = Course->new($_);";
      die $@ if $@;

      die "Course info is incorrectly formatted\n" 
         unless &looks_like_number($info->getId());

      push (@r, $info->getId());
   }

   @r;
}

sub gatherInstructorInfo
{
   my ($cs, $data) = @_;
   my @r;

   $data =~ s/\s//g;
   for (split(/\s*;\s*/, $data))
   {
      say "LINE: '$_'";
      my ($last, $first, $cPrefs, $tPrefs) = /^(.*?),(.*),?({.*?})?,?({.*})?/;
      say "FOUND: $last";
      say "FOUND: $first";
      $cPrefs = $cPrefs // "{}";
      $tPrefs = $tPrefs // "{}";
   
      eval "\$cPrefs = $cPrefs;"; die $@ if $@;
      eval "\$tPrefs = $tPrefs;"; die $@ if $@;
   
      push (@r, "{");
      push (@r, &indent(3, "first => \"$first\","));
      push (@r, &indent(3, "last  => \"$last\","));
      push (@r, &indent(3, "wtu   => 16,"));
      push (@r, &indent(3, "cPrefs => "));
      push (@r, &indent(3, "{"));
      for (@{$cs})
      {
         my $pref = (defined $cPrefs->{$_}) 
            ? "$cPrefs->{$_}," 
            : "5,\t\t#Default";
         push (@r, &indent(6, "$_ => $pref"));
      }
      push (@r, &indent(3, "},"));
   
      push (@r, &indent(3, "tPrefs =>"));
      push (@r, &indent(3, "{"));
      for my $d (qw(MON TUE WED THU FRI))
      {
         push (@r, &indent(6, "'Week.$d' =>"));
         push (@r, &indent(6, "{"));
         for my $h (00..23)
         {
            for my $key (map { "$h:$_" } ("00", "30"))
            {
               my $pref = (defined $tPrefs->{$d}{$key})
                  ? "$tPrefs->{$d}{$key},"
                  : ($h < 7 or $h >= 22)      # Before 7a and after 10p = 0
                     ? "0,\t\t#Default"
                     : "5,\t\t#Default";
               push (@r, &indent(9, "'$key' => $pref"));
            }
         }
         push (@r, &indent(6, "},"));
      }
      push (@r, &indent(3, "},"));
      push (@r, "};");
   }
   @r;
}

sub indent
{
   " " x shift(@_) . "@_";
}
