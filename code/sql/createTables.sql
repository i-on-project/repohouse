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

CREATE TABLE ChallengeInfo(
    state text primary key,
    challenge text not null,
    challenge_method text not null check (challenge_method in ('plain', 's256'))
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
    org_id bigint unique not null,
    name text unique not null,
    is_archived boolean not null default false
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

CREATE TABLE Student_Classroom(
      student int,
      classroom int,
      primary key (student, classroom),
      foreign key (student) references Student(id),
      foreign key (classroom) references Classroom(id)
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
    state text not null check ( state in ('Pending', 'Accepted', 'Rejected', 'Not_Concluded') ),
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
     team_id int unique not null,
     foreign key (id) references Request(id),
     foreign key (team_id) references Team(id)
);

CREATE TABLE LeaveTeam(
   id int primary key,
   team_id int not null,
   foreign key (id) references Request(id)
);

CREATE TABLE Apply(
    id serial primary key,
    pending_teacher_id int not null,
    state text not null check ( state in ('Pending', 'Accepted', 'Rejected') ),
    foreign key (pending_teacher_id) references PendingTeacher(id)
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
    last_sync timestamp not null,
    foreign key (assignment_id) references Assignment(id)
);

CREATE TABLE Repo(
    id serial primary key,
    name text not null,
    url text unique default null,
    is_created boolean not null,
    team_id int unique not null,
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

CREATE TABLE Outbox(
    user_id int primary key,
    status text not null check ( status in ('Pending', 'Sent') ),
    sent_at timestamp default null,
    foreign key (user_id) references users(id)
);

CREATE TABLE OTP(
    user_id int not null,
    otp int not null check ( otp between 100000 and 999999 ),
    expired_at timestamp not null,
    tries int not null,
    foreign key (user_id) references users(id)
);

CREATE TABLE CreateRepo(
    id int primary key,
    repo_id int unique not null,
    foreign key (id) references Request(id),
    foreign key (repo_id) references Repo(id)
);

COMMIT;
