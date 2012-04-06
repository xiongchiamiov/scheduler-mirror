#!/bin/tcsh
set c = 2
while ($c <= 10)
    foreach f (*)
	mkdir $f/week$c
	svaci $f/week$c
	cp .template.html $f/week$c/review.html
	svaci $f/week$c/review.html	
    end
    @ c = $c + 1
end

