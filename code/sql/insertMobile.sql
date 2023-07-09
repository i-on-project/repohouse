SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

insert into users(id, email, is_created, github_username, github_id, token, name)
values (1, 'jdiogo2302@gmail.com', true, 'JoaoMagalhaes23', '73882045', 'token1234', 'Joao Magalhaes');
INSERT INTO student (id, school_id)
VALUES (1, 1535);

INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (2, 'andre.david.santos.02@gmail.com', true, 'AndreSantos0', 80883346, 'token420', 'student2');
insert into teacher(id, github_token)
values (2, 'token1234');
INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (3, 'ricardo.freitas.henriques@gmail.com', true, 'Henriquess19', 80883225, 'token421', 'student3');
INSERT INTO student (id, school_id)
VALUES (3, 1534);

INSERT INTO course (id, org_url, name, org_id)
VALUES (1, 'https://daw.isel.pt', 'test-project-isel', 127772322);
INSERT INTO course (id, org_url, name, org_id)
VALUES (2, 'https://daw1.isel.pt', 'PDM', 6817318);
INSERT INTO course (id, org_url, name, org_id)
VALUES (3, 'https://daw3.isel.pt', 'Ion', 6764445);

INSERT INTO teacher_course (teacher, course)
VALUES (2, 1);
INSERT INTO teacher_course (teacher, course)
VALUES (2, 2);
INSERT INTO teacher_course (teacher, course)
VALUES (2, 3);

INSERT INTO classroom (id, name, last_sync, invite_code, is_archived, course_id, teacher_id)
VALUES (1, 'DAW-2223v-LI52D', CURRENT_TIMESTAMP, 'https://classroom1.github.com/a/1234', false, 1, 2);
INSERT INTO classroom (id, name, last_sync, invite_code, is_archived, course_id, teacher_id)
VALUES (2, 'PDM-2223v-LI52D', CURRENT_TIMESTAMP, 'https://classroom1.github.com/b/1234', false, 1, 2);
INSERT INTO classroom (id, name, last_sync, invite_code, is_archived, course_id, teacher_id)
VALUES (3, 'TVS-2223v-LI52D', CURRENT_TIMESTAMP, 'https://classroom1.github.com/c/1234', false, 1, 2);

INSERT INTO student_classroom (student, classroom)
VALUES (1, 1);
INSERT INTO student_classroom (student, classroom)
VALUES (1, 2);
INSERT INTO student_classroom (student, classroom)
VALUES (3, 1);
INSERT INTO student_classroom (student, classroom)
VALUES (3, 2);

INSERT INTO assignment (id, classroom_id, min_elems_per_group, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (1, 1, 2, 2, 3, CURRENT_TIMESTAMP, 'description1', 'title1');
INSERT INTO assignment (id, classroom_id, min_elems_per_group, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (2, 2, 2, 2, 3, CURRENT_TIMESTAMP, 'description2', 'title2');
INSERT INTO assignment (id, classroom_id, min_elems_per_group, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (3, 3, 2, 2, 3, CURRENT_TIMESTAMP, 'description3', 'title3');

/*TEAM 1*/
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (1, 'team1', false, false, 1);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (1, 'repo1', null, false, 1);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (1, 3, null, 'Pending');
INSERT INTO composite(id)
VALUES (1);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (2, 3, 1, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (2, 1);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (3, 3, 1, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (3, 1);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (4, 3, 1, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (4, 1, 1);

/*TEAM 2*/
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (2, 'team2', false, false, 1);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (2, 'repo2', null, false, 2);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (5, 3, null, 'Pending');
INSERT INTO composite(id)
VALUES (5);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (6, 3, 5, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (6, 2);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (7, 3, 5, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (7, 2);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (8, 3, 5, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (8, 2, 1);

/*TEAM 10*/
INSERT INTO team (id, name, is_created, is_closed, assignment)
VALUES (3, 'team3', false, false, 3);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (3, 'repo3', null, false, 3);

/*composite*/
INSERT INTO request(id, creator, composite, state)
VALUES (9, 3, null, 'Pending');
INSERT INTO composite(id)
VALUES (9);

/*createTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (10, 3, 9, 'Pending');
INSERT INTO createteam(id, team_id)
VALUES (10, 3);

/*createRepo*/
INSERT INTO request(id, creator, composite, state)
VALUES (11, 3, 9, 'Pending');
INSERT INTO createrepo(id, repo_id)
VALUES (11, 3);

/*joinTeam*/
INSERT INTO request(id, creator, composite, state)
VALUES (12, 3, 9, 'Pending');
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (12, 3, 3);

COMMIT TRANSACTION;
