--  This file creates the tables for the scheduler
--  database, for the software engineering capstone
--  series.
PRAGMA foreign_keys = ON;

drop table document; 
create table document (
    id integer primary key,
    name text not null,
    isTrash tinyint(1) not null,
    startHalfHour integer not null,
    endHalfHour integer not null
);

drop table workingcopy;
create table workingcopy (
    id integer primary key,
    originalDocID integer,
    unique (originalDocID),
    foreign key (originalDocID) references document(id),
    foreign key (id) references document(id)
);


drop table instructor;
create table instructor (
    id integer primary key,
    docID,
    firstName text not null,
    lastName text not null,
    username text not null,
    maxWTU integer not null,
    schedulable tinyint(1),
    unique (username, docID),
    foreign key (docID) references document(id)
);

drop table location;
create table location (
    id integer primary key,
    docID,
    maxOccupancy integer not null,
    type text not null,
    room text not null,
    schedulable tinyint(1) not null,
    unique (room, docID)
);

drop table locationequipment;
create table locationequipment (
    id integer primary key,
    locID,
    equipID,
    unique (locID, equipID),
    foreign key (locID) references location(id),
    foreign key (equipID) references equipment(id)
);

drop table course;
create table course (
    id integer primary key,
    docID,
    enrollment integer not null,
    wtu integer not null,
    scu integer not null,
    type text not null,
    numSections integer not null,
    dept text not null,
    catalogNum text not null,
    name text not null,
    schedulable tinyint(1) not null,
    numHalfHours integer not null,
    foreign key (docID) references document(id)
);

drop table courseequipment;
create table courseequipment (
    id integer primary key,
    courseID,
    equipID,
    unique (courseID, equipID),
    foreign key (courseID) references course(id),
    foreign key (equipID) references equipment(id)
);

drop table coursepatterns;
create table coursepatterns (
    id integer primary key,
    courseID,
    patternID,
    unique (courseID, patternID),
    foreign key (patternID) references pattern(id),
    foreign key (courseID) references course(id)
);

drop table scheduleitem;
create table scheduleitem (
    id integer primary key,
    docID,
    instID,
    locID,
    courseID,
    startTime integer not null, 
    endTime integer not null,
    dayPatternID,
    sectionNum integer not null,
    unique (docID, courseID, sectionNum),
    foreign key (instID) references instructors(id),
    foreign key (locID) references location(id),
    foreign key (courseID) references course(id),
    foreign key (docID) references document(id),
    foreign key (dayPatternID) references pattern(id)
);

drop table labassociations;
create table labassociations (
    id integer primary key,
    lecID,
    foreign key (lecID) references course(id)
);

drop table labtethered; 
create table labtethered (
    id integer primary key,
    lecID,
    tetheredID,
    unique (tetheredID),
    foreign key (lecID) references course(id),
    foreign key (tetheredID) references course(id)
);

drop table timeslotpref; 
create table timeslotpref (
    id integer primary key,
    day integer not null,
    time integer not null,
    instID,
    prefLevel integer not null,
    unique (instID, day, time),
    foreign key (instID) references instructor(id)
);

drop table coursepref; 
create table coursepref (
    id integer primary key autoincrement,
    instID,
    courseID,
    prefLevel integer not null,
    unique (instID, courseID),
    foreign key (instID) references instructor(id),
    foreign key (courseID) references course(id)
);

drop table pattern; 
create table pattern (
    id integer primary key,
    days text not null
);

drop table equipment; 
create table equipment (
    id integer primary key,
    desc text not null
);

drop table userdata; 
create table userdata (
    id integer primary key,
    username text not null,
    isAdmin tinyint(1) not null,
    unique (username)
);
------------------------------ end of script ------------------------------
