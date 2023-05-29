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

INSERT INTO assignment (id, classroom_id, min_elems_per_group, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (5, 4, 2, 2, 3, CURRENT_TIMESTAMP, 'description4', 'title4');
INSERT INTO assignment (id, classroom_id, min_elems_per_group, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (6, 5, 2, 2, 3, CURRENT_TIMESTAMP, 'description5', 'title5');

INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (5, 'team5', true, false, 6);

INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (7, 'repo7', null, false, 5);

INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (1, 'team1', true, false, 5);
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (2, 'team2', true, false, 5);


/*TEAM 6*/
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (6, 'team6', false, false, 5);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (4, 'repo4', null, false, 6);
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (10, 'team10', false, false, 5);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (33, 19, null, 'Pending');
INSERT INTO composite(id)
VALUES (33);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (34, 19, 33, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (34, 6);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (35, 19, 33, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (35, 4);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (36, 19, 33, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (36, 6, 5);

/*TEAM 7*/
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (7, 'team7', false, false, 5);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (5, 'repo5', null, false, 7);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (37, 19, null, 'Pending');
INSERT INTO composite(id)
VALUES (37);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (38, 19, 37, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (38, 7);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (39, 19, 37, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (39, 5);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (40, 19, 37, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (40, 7, 5);

/*TEAM 8*/
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (8, 'team8', false, false, 5);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (6, 'repo6', null, false, 8);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (41, 19, null, 'Pending');
INSERT INTO composite(id)
VALUES (41);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (42, 19, 41, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (42, 8);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (43, 19, 41, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (43, 6);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (44, 19, 41, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (44, 8, 5);

/*leaveTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (80, 19, null, 'Pending');
INSERT INTO leaveteam(id, team_id)
VALUES (80, 6);

COMMIT TRANSACTION;

INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (5, 'test4@alunos.isel.pt', true, 'test12345a', 12341527, 'token4', 'student3');
INSERT INTO student (id, school_id)
VALUES (5, 153);
INSERT INTO student_classroom (student, classroom)
VALUES (5, 4);
INSERT INTO student_team (student, team)
VALUES (5, 6);

INSERT INTO request(id, creator, composite, state)
VALUES (81, 19, null, 'Pending');
INSERT INTO composite(id)
VALUES (81);
INSERT INTO request(id, creator, composite, state)
VALUES (84, 19, 81, 'Pending');
INSERT INTO leavecourse(id, course_id)
VALUES (84, 1);
INSERT INTO request(id, creator, composite, state)
VALUES (82, 19, 81, 'Pending');
INSERT INTO leaveteam(id, team_id)
VALUES (82, 6);
INSERT INTO request(id, creator, composite, state)
VALUES (83, 19, 81, 'Pending');
INSERT INTO leaveteam(id, team_id)
VALUES (83, 7);

SELECT l.id, x.creator, x.state, x.composite, l.team_id, x.github_username, (SELECT COUNT(*) FROM student_team
                                                                             WHERE team = l.team_id) as members_count FROM
(SELECT u.github_username, r.id, r.creator, r.composite, r.state FROM request r JOIN users u on r.creator = u.id WHERE r.id = 83) as x JOIN
leaveteam l on x.id = l.id