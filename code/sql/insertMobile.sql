SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

insert into users(id, email, is_created, github_username, github_id, token, name)
values (9, 'jdiogo2302@gmail.com', true, 'JoaoMagalhaes23', '73882045', 'token1234', 'Joao Magalhaes');
insert into teacher(id, github_token)
values (9, 'token1234');

INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (19, 'andre.david.santos.02@gmail.com', true, 'AndreSantos0', 80883346, 'token420', 'student2');
INSERT INTO student (id, school_id)
VALUES (19, 1535);
INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (20, 'ricardo.freitas.henriques@gmail.com', true, 'Henriquess19', 80883225, 'token421', 'student3');
INSERT INTO student (id, school_id)
VALUES (20, 1534);

INSERT INTO course (id, org_url, name, org_id)
VALUES (1, 'https://daw.isel.pt', 'test-project-isel', 127772322);
INSERT INTO course (id, org_url, name, org_id)
VALUES (2, 'https://daw1.isel.pt', 'PDM', 6817318);
INSERT INTO course (id, org_url, name, org_id)
VALUES (3, 'https://daw3.isel.pt', 'Ion', 6764445);

INSERT INTO teacher_course (teacher, course)
VALUES (9, 1);
INSERT INTO teacher_course (teacher, course)
VALUES (9, 2);
INSERT INTO teacher_course (teacher, course)
VALUES (9, 3);

INSERT INTO classroom (id, name, last_sync, invite_code, is_archived, course_id, teacher_id)
VALUES (4, 'DAW-2223v-LI52D', CURRENT_TIMESTAMP, 'https://classroom1.github.com/a/123', false, 1, 9);
INSERT INTO classroom (id, name, last_sync, invite_code, is_archived, course_id, teacher_id)
VALUES (5, 'PDM-2223v-LI52D', CURRENT_TIMESTAMP, 'https://classroom1.github.com/b/123', false, 2, 9);
INSERT INTO classroom (id, name, last_sync, invite_code, is_archived, course_id, teacher_id)
VALUES (6, 'TVS-2223v-LI52D', CURRENT_TIMESTAMP, 'https://classroom1.github.com/c/123', false, 1, 9);

INSERT INTO student_classroom (student, classroom)
VALUES (19, 4);
INSERT INTO student_classroom (student, classroom)
VALUES (19, 5);
INSERT INTO student_classroom (student, classroom)
VALUES (20, 4);
INSERT INTO student_classroom (student, classroom)
VALUES (20, 5);

INSERT INTO assignment (id, classroom_id, min_elems_per_group, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (5, 4, 2, 2, 3, CURRENT_TIMESTAMP, 'description4', 'title4');
INSERT INTO assignment (id, classroom_id, min_elems_per_group, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (6, 5, 2, 2, 3, CURRENT_TIMESTAMP, 'description5', 'title5');
INSERT INTO assignment (id, classroom_id, min_elems_per_group, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (7, 6, 2, 2, 3, CURRENT_TIMESTAMP, 'description6', 'title6');

/*TEAM 1*/
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (1, 'team1', false, false, 5);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (1, 'repo1', null, false, 1);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (1, 20, null, 'Pending');
INSERT INTO composite(id)
VALUES (1);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (2, 20, 1, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (2, 1);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (3, 20, 1, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (3, 1);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (4, 20, 1, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (4, 1, 5);

/*TEAM 2*/
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (2, 'team2', false, false, 5);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (2, 'repo2', null, false, 2);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (5, 20, null, 'Pending');
INSERT INTO composite(id)
VALUES (5);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (6, 20, 5, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (6, 2);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (7, 20, 5, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (7, 2);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (8, 20, 5, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (8, 2, 5);

/*TEAM 10*/
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (3, 'team3', false, false, 7);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (3, 'repo3', null, false, 3);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (9, 20, null, 'Pending');
INSERT INTO composite(id)
VALUES (9);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (10, 20, 9, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (10, 3);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (11, 20, 9, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (11, 3);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (12, 20, 9, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (12, 3, 7);

COMMIT TRANSACTION;

INSERT INTO request(id, creator, composite, state)
VALUES (20, 19, null, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (20, 1, 5);

/*andre leavecourse 1*/
/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (13, 20, null, 'Pending');
INSERT INTO composite(id)
VALUES (13);
/*leaveCourse*/
INSERT INTO request(id, creator, composite, state)
VALUES (14, 20, 13, 'Pending');
INSERT INTO leavecourse(id, course_id)
VALUES (14, 1);
/*leaveClassroom4*/
INSERT INTO request(id, creator, composite, state)
VALUES (15, 20, 13, 'Pending');
insert into leaveclassroom(id, classroom_id)
values (15, 4);
/*leaveClassroom6*/
INSERT INTO request(id, creator, composite, state)
VALUES (16, 20, 13, 'Pending');
insert into leaveclassroom(id, classroom_id)
values (16, 6);
/*leaveTeam1*/
INSERT INTO request(id, creator, composite, state)
VALUES (17, 20, 13, 'Pending');
insert into leaveteam(id, team_id)
values (17, 1);
/*leaveTeam2*/
INSERT INTO request(id, creator, composite, state)
VALUES (18, 20, 13, 'Pending');
insert into leaveteam(id, team_id)
values (18, 2);
/*leaveTeam3*/
INSERT INTO request(id, creator, composite, state)
VALUES (19, 20, 13, 'Pending');
insert into leaveteam(id, team_id)
values (19, 3);
/*-------------------*/
/*leave classroom 4*/
/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (13, 20, null, 'Pending');
INSERT INTO composite(id)
VALUES (13);
/*leaveClassroom4*/
INSERT INTO request(id, creator, composite, state)
VALUES (15, 20, 13, 'Pending');
insert into leaveclassroom(id, classroom_id)
values (15, 4);
/*leaveTeam1*/
INSERT INTO request(id, creator, composite, state)
VALUES (17, 20, 13, 'Pending');
insert into leaveteam(id, team_id)
values (17, 1);
/*leaveTeam2*/
INSERT INTO request(id, creator, composite, state)
VALUES (18, 20, 13, 'Pending');
insert into leaveteam(id, team_id)
values (18, 2);