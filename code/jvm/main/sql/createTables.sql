BEGIN TRANSACTION;
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;

CREATE TABLE "User"(
    id serial primary key,
    email text unique check (email like '%@%') not null,
    github_username text unique not null,
    name text not null
);

CREATE TABLE "Teacher"(
    id serial primary key,
    is_created boolean not null,
    foreign key(id) references "User"(id)
);

CREATE TABLE "Student"(
    id serial primary key,
    school_id int unique not null,
    foreign key(id) references "User"(id)
);

CREATE TABLE "Course"(
    id serial primary key,
    org_url text unique not null,
    name text unique not null,
    teacher serial not null,
    foreign key (teacher) references "Teacher"(id)
);

CREATE TABLE "Student_Course"(
    student serial,
    course serial,
    primary key (student, course),
    foreign key (student) references "Student"(id),
    foreign key (course) references "Course"(id)
);

CREATE TABLE "Classroom"(
    id serial primary key,
    name text not null,
    last_sync timestamp not null,
    invite_link text unique not null,
    course serial not null,
    foreign key (course) references "Course"(id)
);

CREATE TABLE "Request"(
    id serial primary key,
    creator serial not null,
    classroom serial not null,
    foreign key (creator) references "User"(id),
    foreign key (classroom) references "Classroom"(id)
);

CREATE TABLE "Create"(
    id serial primary key,
    team_id serial not null,
    foreign key (id) references "Request"(id)
);

CREATE TABLE "Join"(
    id serial primary key,
    team_id serial not null,
    foreign key (id) references "Request"(id)
);

CREATE TABLE "Leave"(
    id serial primary key,
    team_id serial not null,
    foreign key (id) references "Request"(id)
);

CREATE TABLE "Apply"(
    id serial primary key,
    foreign key (id) references "Request"(id)
);

CREATE TABLE "Assignment"(
    id serial primary key,
    max_number_elems int not null,
    max_number_groups int not null,
    release_date timestamp not null,
    description text unique not null,
    title text unique not null
);

CREATE TABLE "Team"(
    id serial primary key,
    name text not null,
    dirty_flag boolean not null,
    assignment serial not null,
    foreign key (assignment) references "Assignment"(id)
);

CREATE TABLE "Student_Team"(
    student serial,
    team serial,
    primary key (student, team),
    foreign key (student) references "Student"(id),
    foreign key (team) references "Course"(id)
);

CREATE TABLE "Delivery"(
    id serial primary key,
    due_date timestamp not null,
    tag_control text not null,
    assignment serial not null,
    foreign key (assignment) references "Assignment"(id)
);

CREATE TABLE "Repo"(
    id serial primary key,
    name text not null,
    url text unique not null,
    team serial not null,
    foreign key (team) references "Team"(id)
);

CREATE TABLE "Tags"(
    id serial primary key,
    name text not null,
    is_delivered boolean not null,
    tag_date timestamp not null,
    delivery serial not null,
    repo serial not null,
    foreign key (delivery) references "Delivery"(id),
    foreign key (repo) references "Repo"(id)
);

CREATE TABLE "Feedback"(
    id serial primary key,
    description text not null,
    label text not null,
    team serial not null,
    foreign key (team) references "Team"(id)
);

COMMIT;
