SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

CREATE TABLE Users(
    id serial primary key,
    email text unique check (email like '%@%') not null,
    is_created boolean not null default false,
    github_username text unique not null,
    github_id bigint unique not null,
    token text unique not null,
    name text not null
);

CREATE TABLE Teacher(
    id int primary key,
    github_token text unique not null,
    foreign key(id) references users(id)
);

CREATE TABLE Student(
    id int primary key,
    school_id int unique not null,
    foreign key(id) references users(id)
);

CREATE TABLE Course(
    id serial primary key,
    org_url text unique not null,
    name text unique not null,
    teacher_id int not null,
    foreign key (teacher_id) references Teacher(id)
);

CREATE TABLE Student_Course(
    student int,
    course int,
    primary key (student, course),
    foreign key (student) references Student(id),
    foreign key (course) references Course(id)
);

CREATE TABLE Classroom(
    id serial primary key,
    name text not null,
    last_sync timestamp not null,
    invite_link text unique not null,
    is_archive boolean not null default false,
    course_id int not null,
    foreign key (course_id) references Course(id)
);

CREATE TABLE Request(
    id serial primary key,
    creator int not null,
    composite int default null,
    state text not null check ( state in ('pending', 'accepted', 'rejected') ),
    foreign key (creator) references users(id),
    foreign key (composite) references Request(id)
);

CREATE TABLE Composite(
    id int primary key,
    foreign key (id) references Request(id)
);

CREATE TABLE CreateRepo(
    id int primary key,
    repo_id int not null,
    foreign key (id) references Request(id)
);

CREATE TABLE ArchiveRepo(
   id int primary key,
   repo_id int not null,
   foreign key (id) references Request(id)
);

CREATE TABLE LeaveCourse(
    id int primary key,
    course_id int not null,
    foreign key (id) references Request(id)
);

CREATE TABLE JoinTeam(
    id int primary key,
    team_id int not null,
    foreign key (id) references Request(id)
);

CREATE TABLE CreateTeam(
     id int primary key,
     team_id int not null,
     foreign key (id) references Request(id)
);

CREATE TABLE LeaveTeam(
   id int primary key,
   team_id int not null,
   foreign key (id) references Request(id)
);

CREATE TABLE Apply(
    id int primary key,
    teacher_id int not null,
    foreign key (id) references Request(id),
    foreign key (teacher_id) references users(id)
);

CREATE TABLE Assignment(
    id serial primary key,
    classroom_id int not null,
    max_number_elems int not null,
    max_number_groups int not null,
    release_date timestamp not null,
    description text unique not null,
    title text unique not null,
    foreign key (classroom_id) references Classroom(id)
);

CREATE TABLE Team(
    id serial primary key,
    name text not null,
    is_Created boolean not null,
    assignment int not null,
    foreign key (assignment) references Assignment(id)
);

CREATE TABLE Student_Team(
    student int,
    team int,
    primary key (student, team),
    foreign key (student) references Student(id),
    foreign key (team) references Course(id)
);

CREATE TABLE Delivery(
    id serial primary key,
    due_date timestamp not null,
    tag_control text not null,
    assignment_id int not null,
    foreign key (assignment_id) references Assignment(id)
);

CREATE TABLE Repo(
    id serial primary key,
    name text not null,
    url text unique not null,
    is_created boolean not null,
    team_id int not null,
    foreign key (team_id) references Team(id)
);

CREATE TABLE Tags(
    id serial primary key,
    name text not null,
    is_delivered boolean not null,
    tag_date timestamp not null,
    delivery_id int not null,
    repo_id int not null,
    foreign key (delivery_id) references Delivery(id),
    foreign key (repo_id) references Repo(id)
);

CREATE TABLE Feedback(
    id serial primary key,
    description text not null,
    label text not null,
    team_id int not null,
    foreign key (team_id) references Team(id)
);

COMMIT;

