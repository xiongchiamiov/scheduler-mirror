#!/bin/tcsh
foreach f ( \
	admin-scheduler.html \
	advanced-filter.html \
	advanced-schedule.html \
	appendix-a.html \
	close.html \
	conflict-resolution.html \
	conflict.sl.html \
	constraints-add.html \
	constraints-edit.html \
	constraints-remove.html \
	constraints.html \
	course-add.html \
	course-edit.html \
	course-manual.html \
	course-manual.sl.html \
	course-remove.html \
	course-view.html \
	daily-course-view.html \
	daily-instructor-view.html \
	daily-location-view.html \
	data-course.html \
	data-instructor.html \
	data-room.html \
	data.html \
	database.sl.html \
	developer-overview.html \
	feedback-student.html \
	file-exit.html \
	file-open-recent.html \
	file-save.html \
	file.html \
	file_new.html \
	file_open.html \
	images.html \
	instructor-add.html \
	instructor-edit.html \
	instructor-remove.html \
	instructor-view.html \
	location-view.html \
	locking.html \
	lof.html \
	manual.html \
	non-functional.html \
	preference.sl.html \
	prefsetting.html \
	print.html \
	room-add.html \
	room-edit.html \
	room-remove.html \
	saveAs.html \
	schedule-attributes-fairness.html \
	schedule-attributes-quality.html \
	schedule-attributes.html \
	server-admin.html \
	sorting-view.html \
	spec.html \
	todo.html \
	ui-admin.html \
	ui-instructor.html \
	ui-overview.html \
	ui-student.html \
	view-availability.html \
	view-schedule.html \
	view.sl.html \
	warmup.html \
	weekly-course-view.html \
	weekly-instructor-view.html \
	weekly-location-view.html )

    echo $f
    svn remove $f
    svn commit -m "Purged 309 files." $f

end
