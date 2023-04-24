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

CREATE TABLE PendingTeacher(
      id serial primary key,
      email text check (email like '%@%') not null,
      is_created boolean not null default false,
      github_username text not null,
      github_id bigint not null,
      token text not null,
      name text not null,
      github_token text not null,
      created_at date
);

CREATE TABLE PendingStudent(
       id serial primary key,
       email text check (email like '%@%') not null,
       is_created boolean not null default false,
       github_username text not null,
       github_id bigint not null,
       token text not null,
       name text not null,
       created_at date
);

CREATE TABLE Teacher(
    id int primary key,
    github_token text unique not null,
    foreign key(id) references users(id)
);

CREATE TABLE Student(
    id int primary key,
    school_id int unique default null,
    foreign key(id) references users(id)
);

CREATE TABLE Course(
    id serial primary key,
    org_url text unique not null,
    name text unique not null,
    is_archived boolean not null default false
);

CREATE TABLE Student_Course(
    student int,
    course int,
    primary key (student, course),
    foreign key (student) references Student(id),
    foreign key (course) references Course(id)
);

CREATE TABLE Teacher_Course(
    teacher int,
    course int,
    primary key (teacher, course),
    foreign key (teacher) references Teacher(id),
    foreign key (course) references Course(id)
);

CREATE TABLE Classroom(
    id serial primary key,
    name text not null,
    last_sync timestamp not null,
    invite_link text unique not null,
    is_archived boolean not null,
    course_id int not null,
    teacher_id int not null,
    foreign key (course_id) references Course(id),
    foreign key (teacher_id) references Teacher(id)
);

CREATE TABLE Assignment(
                           id serial primary key,
                           classroom_id int not null,
                           max_elems_per_group int not null,
                           max_number_groups int not null,
                           release_date timestamp not null,
                           description text not null,
                           title text not null,
                           foreign key (classroom_id) references Classroom(id)
);

CREATE TABLE Team(
                     id serial primary key,
                     name text not null,
                     is_Created boolean not null,
                     assignment int not null,
                     foreign key (assignment) references Assignment(id)
);

CREATE TABLE Request(
    id serial primary key,
    creator int not null,
    composite integer default null,
    state text not null check ( state in ('Pending', 'Accepted', 'Rejected') ),
    foreign key (creator) references Users(id)
);

CREATE TABLE Composite(
    id int primary key,
    foreign key (id) references Request(id)
);

ALTER TABLE Request
    ADD CONSTRAINT fk_composite
        FOREIGN KEY (composite)
            REFERENCES Composite(id);

CREATE TABLE CreateRepo(
    id int primary key,
    team_id int not null,
    foreign key (id) references Request(id),
    foreign key (team_id) references Team(id)
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
    assigment_id int not null,
    foreign key (id) references Request(id),
    foreign key (assigment_id) references Assignment(id),
    foreign key (team_id) references Team(id)
);

CREATE TABLE CreateTeam(
     id int primary key,
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
    foreign key (teacher_id) references Users(id)
);

CREATE TABLE Student_Team(
    student int,
    team int,
    primary key (student, team),
    foreign key (student) references Student(id),
    foreign key (team) references Team(id)
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
    url text unique default null,
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

CREATE TABLE Cooldown(
    id serial primary key,
    user_id int not null,
    end_date timestamp not null,
    foreign key (user_id) references users(id)
);

Create TABLE Outbox(
    user_id int primary key,
    otp int not null,
    status text not null check ( status in ('Pending', 'Sent') ),
    expired_at timestamp not null,
    sent_at timestamp default null,
    foreign key (user_id) references users(id)
);

COMMIT;


insert into users (email,github_username, github_id, token, name) values ('admin@admin','admin', 0, 'admin', 'admin');
insert into Teacher (id, github_token) values (31, 'admin');
insert into Course (org_url, name) values ('demo', 'demo');
insert into Teacher_Course (teacher, course) values (31, 3);
insert into Student_Course (student, course) values (29, 3);
insert into Classroom (name, last_sync, invite_link, is_archived, course_id, teacher_id) values ('demo', '2019-01-01', 'demo', false, 3, 31);
insert into Classroom (name, last_sync, invite_link, is_archived, course_id, teacher_id) values ('demo2', '2019-01-01', 'demo2', false, 3, 31);
insert into Assignment (classroom_id, max_elems_per_group, max_number_groups, release_date, description, title) values (1, 2, 2, '2019-01-01', 'demo', 'demo');
insert into Assignment (classroom_id, max_elems_per_group, max_number_groups, release_date, description, title) values (2, 2, 2, '2019-01-01', 'demo2', 'demo2');
insert into Team (name, is_Created, assignment) values ('demo', false, 1);
insert into Team (name, is_Created, assignment) values ('demo', false, 4);
insert into Student_Team (student, team) values (29, 1);
insert into Student_Team (student, team) values (29, 3);
insert into Delivery (due_date, tag_control, assignment_id) values ('2019-01-01', 'demo', 1);
insert into Delivery (due_date, tag_control, assignment_id) values ('2019-01-01', 'demo', 4);
insert into Repo (name, url, is_created, team_id) values ('demo', 'demo', false, 1);
insert into Repo (name, url, is_created, team_id) values ('demo2', 'demo2', false, 3);
insert into Tags (name, is_delivered, tag_date, delivery_id, repo_id) values ('demo', false, '2019-01-01', 1, 1);
insert into Tags (name, is_delivered, tag_date, delivery_id, repo_id) values ('demo', false, '2019-01-01', 2, 3);
insert into Feedback (description, label, team_id) values ('demo', 'demo', 1);
insert into Feedback (description, label, team_id) values ('demo', 'demo', 3);
