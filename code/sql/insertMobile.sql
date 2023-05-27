SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

insert into users(id, email, is_created, github_username, github_id, token, name)
values (9, 'jdiogo2302@gmail.com', true, 'JoaoMagalhaes23', '73882045', 'token1234', 'Joao Magalhaes');
insert into teacher(id, github_token)
values (9, 'token1234');

INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (4, 'andre.david.santos.02@gmail.com', true, 'AndreSantos0', 80883346, 'token3', 'student2');
INSERT INTO student (id, school_id)
VALUES (4, 1237);


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

INSERT INTO classroom (id, name, last_sync, invite_link, is_archived, course_id, teacher_id)
VALUES (4, 'DAW-2223v-LI52D', CURRENT_TIMESTAMP, 'https://classroom1.github.com/a/123', false, 1, 9);
INSERT INTO classroom (id, name, last_sync, invite_link, is_archived, course_id, teacher_id)
VALUES (5, 'PDM-2223v-LI52D', CURRENT_TIMESTAMP, 'https://classroom1.github.com/b/123', false, 2, 9);
INSERT INTO classroom (id, name, last_sync, invite_link, is_archived, course_id, teacher_id)
VALUES (6, 'TVS-2223v-LI52D', CURRENT_TIMESTAMP, 'https://classroom1.github.com/c/123', false, 1, 9);

INSERT INTO student_classroom (student, classroom)
VALUES (4, 4);

INSERT INTO assignment (id, classroom_id, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (5, 4, 2, 3, CURRENT_TIMESTAMP, 'description4', 'title4');
INSERT INTO assignment (id, classroom_id, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (6, 5, 2, 3, CURRENT_TIMESTAMP, 'description5', 'title5');

INSERT INTO team (id, name, is_created, assignment)
VALUES (5, 'team5', true, 6);

INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (7, 'repo7', null, false, 5);

INSERT INTO team (id, name, is_created, assignment)
VALUES (1, 'team1', true, 5);
INSERT INTO team (id, name, is_created, assignment)
VALUES (2, 'team2', true, 5);


/*TEAM 6*/
INSERT INTO team (id, name, is_created, assignment)
VALUES (6, 'team6', false, 5);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (4, 'repo4', null, false, 6);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (33, 4, null, 'Pending');
INSERT INTO composite(id)
VALUES (33);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (34, 4, 33, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (34, 6);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (35, 4, 33, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (35, 4);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (36, 4, 33, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (36, 6, 5);

/*TEAM 7*/
INSERT INTO team (id, name, is_created, assignment)
VALUES (7, 'team7', false, 5);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (5, 'repo5', null, false, 7);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (37, 4, null, 'Pending');
INSERT INTO composite(id)
VALUES (37);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (38, 4, 37, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (38, 7);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (39, 4, 37, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (39, 5);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (40, 4, 37, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (40, 7, 5);

/*TEAM 8*/
INSERT INTO team (id, name, is_created, assignment)
VALUES (8, 'team8', false, 5);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (6, 'repo6', null, false, 8);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (41, 4, null, 'Pending');
INSERT INTO composite(id)
VALUES (41);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (42, 4, 41, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (42, 8);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (43, 4, 41, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (43, 6);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (44, 4, 41, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (44, 8, 5);

/*archiveRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (45, 4, null, 'Pending');
INSERT INTO archiverepo(id, repo_id)
VALUES (45, 4);

/*leaveTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (48, 4, null, 'Pending');
INSERT INTO leaveteam(id, team_id)
VALUES (48, 6);

INSERT INTO request(id, creator, composite, state)
VALUES (49, 4, null, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (49, 6, 5);

COMMIT TRANSACTION;
