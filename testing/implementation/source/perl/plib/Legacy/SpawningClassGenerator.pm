###################################################################
#                      CUSTOM CLASS GENERATOR                     #
#                                                                 #
# Will generate a class, using the 'ConvertToSpawn' package to    #
# convert provided data files to a particular class.              #
###################################################################
package Legacy::SpawningClassGenerator;
use strict;
use warnings;
use 5.010;
use base qw(Legacy::ClassGenerator);

sub convertFilesToClass
{
   my ($self) = @_;

   ConvertToSpawn->new(
   {
      cfile => $self->getCfile(),
      ifile => $self->getIfile(),
      lfile => $self->getLfile(),
      name  => $self->getJfile(),
      dumpFile => $self->getDumpFile(),
   })->convert();
}

###################################################################
#                      CUSTOM CLASS CONVERTOR                     #
#                                                                 #
# Will spawn the scheduler, populated with the data from the      #
# files you give it                                               #
###################################################################
package Legacy::ConvertToSpawn;
use strict;
use warnings;
use 5.010;
use base qw(Legacy::Converter);

sub whatMainDoes
{
   my ($self, $d, $s) = @_;

   say " " x $d . "Scheduler.schedulerUI.show(150, 150);";
}

1;
