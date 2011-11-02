#!/bin/tcsh
#
# This script is a tad complicated, but only requires a single command line to
# execute:
#
#     ./make.csh
#
# To make only locally, without doing a release to the VM, usr -nr for $1,
# i.e.,
#
#     ./make.csh -nr
#
# The script will run the following steps:
#
#    1. Generate raw HTML content files from the groff content files.  These
#       raw HTML files are stored in *.html in his directory
#
#    2. Generate presentation versions of the raw HTML files.  This is done by
#       including the raw HTML files in the Dreamweaver-generated HTML
#       wrappers.  The wrapper HTML files are in the ../dreamweaver directory.
#       The presentation files are stored in the parent directory, i.e., one
#       directory up from this content sub-directory.
#
#    3. If $1 is not "-nr", use rsync to copy the entire project documentation
#       directory to the scheduler VM at
#           scheduler.csc.calpoly.edu:project/scheduler
#       This constitutes a release of the website content.

# Set up log file.  We use an abs path since the script changes working dirs.
set log_file = `pwd`/.make.log
rm -f $log_file

# Iterate through all of the groff files.
foreach f (*.me)

    # Define the files involved.
    set base = $f:r
    set raw_html = $base.html
    set html_wrapper = ../dreamweaver/$base.html
    set presentation_html = ../$base.html

    # DISABLED FOR NOW:
    # Only remake html if the corrersponding groff file is newer.
    # set base_newer = `find . -name $f -newer $raw_html`
    # if ($base_newer == "") continue

    # Echo a progress message.
    echo Processing $base | tee -a $log_file

    # Generate the raw HTML from the groff.  This uses some scripts that Fisher
    # wrote.  They should be moved to the project directory if we're going to
    # continue to use them.  If we abandon the groff sources and edit HTML
    # content directly, then this step of the script is outta here.
    # echo ~gfisher/bin/me2html $f
    ~gfisher/bin/me2html $f >>& $log_file
    # echo ~gfisher/bin/me2html-remove-body $raw_html
    ~gfisher/bin/me2html-remove-body $raw_html >>& $log_file

    # Go to the ../dreamweaver directory and generate the presentation versions
    # of the HTML files.  This is done using the UNIX soelim command.
    # Specifically, each dreamweaver wrapper X.html contains one line of the
    # form:
    #
    #     .so ../content/X.html
    #
    # This ".so" command acts just like a #include in C.  When the soelim
    # utility is run, it source includes the HTML content into the dreamweaver
    # wrapper.  The reason for this is so we can keep the raw HTML content for
    # a page separate from all the Dreamweaver and css doo-doo that's needed to
    # make the pages Cal Poly and ADA compliant.
    cd ../dreamweaver
    soelim -r $html_wrapper > $presentation_html

    # Go back to the content sub-directory and continue the loop.
    cd ../content

end

# Copy the entire documentation directory to the scheduler VM.  This step
# requires that the UID executing this script have write privileges on the root
# directory of the scheduler VM.  This is set up using the normal ssh
# authorized_keys mechanism.
cd ../../..
echo "Syncing with scheduler VM"  | tee -a $log_file
rsync -rptluv documentation scheduler.csc.calpoly.edu:/project/scheduler |& tee -a $log_file
