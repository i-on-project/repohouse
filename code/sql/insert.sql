SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (1, 'test@alunos.isel.pt', false, 'test123', 12345, 'token', 'teacher1');
INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (2, 'test1@alunos.isel.pt', false, 'test1234', 123452, 'token1', 'teacher2');
INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (3, 'test2@alunos.isel.pt', false, 'test1235', 123425, 'token2', 'student1');
INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (4, 'test3@alunos.isel.pt', false, 'test12345', 1234152, 'token3', 'student2');
INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (5, 'test4@alunos.isel.pt', false, 'test12345a', 12341527, 'token4', 'student3');
INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (6, 'test7@alunos.isel.pt', false, 'test123as', 1234187, 'token7', 'teacher3');
INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (7, 'test8@alunos.isel.pt', false, 'test1aa23as', 12341837, 'token8', 'teacher4');
INSERT INTO users (id, email, is_created, github_username, github_id, token, name)
VALUES (8, 'test9@alunos.isel.pt', false, 'test1aas', 123418378, 'token9', 'student3');


INSERT INTO teacher (id, github_token)
VALUES (1, 'token');
INSERT INTO teacher (id, github_token)
VALUES (2, 'token1');
INSERT INTO teacher (id, github_token)
VALUES (6, 'token2');
INSERT INTO teacher (id, github_token)
VALUES (7, 'token3');

INSERT INTO student (id, school_id)
VALUES (3, 1234);
INSERT INTO student (id, school_id)
VALUES (4, 1235);
INSERT INTO student (id, school_id)
VALUES (5, 1236);
INSERT INTO student (id, school_id)
VALUES (8, 1237);

INSERT INTO pendingstudent(id, email, is_created, github_username, github_id, token, name, created_at)
VALUES (3, 'test2@alunos.isel.pt', false, 'test1235', 2222, 'token10', 'student10', CURRENT_TIMESTAMP);

INSERT INTO pendingteacher(id ,email, is_created, github_username, github_id, token, name, github_token,created_at)
VALUES (1, 'test@alunos.isel.pt', false, 'test123', 2225, 'token', 'teacher1', 'githubToken2', CURRENT_TIMESTAMP);
INSERT INTO pendingteacher(id ,email, is_created, github_username, github_id, token, name, github_token,created_at)
VALUES (4, 'test3@alunos.isel.pt', false, 'test1239', 2226, 'token14', 'student15', 'githubToken', CURRENT_TIMESTAMP);
INSERT INTO pendingteacher(id, email, is_created, github_username, github_id, token, name, github_token,created_at)
VALUES (5, 'test4@alunos.isel.pt', false, 'test1240', 2227, 'token15', 'student16', 'githubToken1', '2023-01-01 00:00:00');


INSERT INTO course (id, org_url, name, org_id)
VALUES (1, 'https://daw.isel.pt', 'DAW', 10852760);
INSERT INTO course (id, org_url, name, org_id)
VALUES (2, 'https://daw1.isel.pt', 'PDM', 6817318);
INSERT INTO course (id, org_url, name, org_id)
VALUES (3, 'https://daw3.isel.pt', 'Ion', );

INSERT INTO teacher_course (teacher, course)
VALUES (1, 1);
INSERT INTO teacher_course (teacher, course)
VALUES (2, 1);
INSERT INTO teacher_course (teacher, course)
VALUES (6, 1);
INSERT INTO teacher_course (teacher, course)
VALUES (1, 2);
INSERT INTO teacher_course (teacher, course)
VALUES (2, 2);

INSERT INTO classroom (id, name, last_sync, invite_link, is_archived, course_id, teacher_id)
VALUES (1, 'DAW-2223v-LI51D', CURRENT_TIMESTAMP, 'https://classroom.github.com/a/123', false, 1, 1);
INSERT INTO classroom (id, name, last_sync, invite_link, is_archived, course_id, teacher_id)
VALUES (2, 'PDM-2223v-LI51D', CURRENT_TIMESTAMP, 'https://classroom.github.com/b/123', false, 2, 1);
INSERT INTO classroom (id, name, last_sync, invite_link, is_archived, course_id, teacher_id)
VALUES (3, 'TVS-2223v-LI51D', CURRENT_TIMESTAMP, 'https://classroom.github.com/c/123', false, 1, 2);

INSERT INTO assignment (id, classroom_id, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (1, 1, 2, 3, CURRENT_TIMESTAMP, 'description', 'title');
INSERT INTO assignment (id, classroom_id, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (2, 1, 2, 3, CURRENT_TIMESTAMP, 'description1', 'title1');
INSERT INTO assignment (id, classroom_id, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (3, 1, 2, 3, CURRENT_TIMESTAMP, 'description2', 'title2');
INSERT INTO assignment (id, classroom_id, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (4, 2, 2, 3, CURRENT_TIMESTAMP, 'description3', 'title3');

INSERT INTO team (id, name, is_created, assignment)
VALUES (1, 'team1', false, 1);
INSERT INTO team (id, name, is_created, assignment)
VALUES (2, 'team2', false, 2);
INSERT INTO team (id, name, is_created, assignment)
VALUES (3, 'team3', false, 1);
INSERT INTO team (id, name, is_created, assignment)
VALUES (4, 'team4', false, 4);

INSERT INTO student_team (student, team)
VALUES (4, 1);
INSERT INTO student_team (student, team)
VALUES (4, 2);
INSERT INTO student_team (student, team)
VALUES (3, 1);
INSERT INTO student_team (student, team)
VALUES (3, 4);

INSERT INTO delivery (id, due_date, tag_control, assignment_id)
VALUES (1, CURRENT_TIMESTAMP, 'tag', 1);
INSERT INTO delivery (id, due_date, tag_control, assignment_id)
VALUES (2, CURRENT_TIMESTAMP, 'tag1', 1);
INSERT INTO delivery (id, due_date, tag_control, assignment_id)
VALUES (3, CURRENT_TIMESTAMP, 'tag2', 2);

INSERT INTO feedback (description, label, team_id)
VALUES ('description1', 'label1', 1);
INSERT INTO feedback (description, label, team_id)
VALUES ('description2', 'label2', 1);

INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (1, 'repo1', 'https://repo.github.com/a/123', false, 1);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (2, 'repo2', 'https://repo.github.com/ab/123', false, 2);
INSERT INTO repo (id, name, url, is_created, team_id)
VALUES (3, 'repo3', 'https://repo.github.com/abc/123', false, 4);

INSERT INTO tags (name, is_delivered, tag_date, delivery_id, repo_id)
VALUES ('tag1', true, CURRENT_TIMESTAMP, 1, 1);
INSERT INTO tags (name, is_delivered, tag_date, delivery_id, repo_id)
VALUES ('tag2', false, CURRENT_TIMESTAMP, 1, 1);
INSERT INTO tags (name, is_delivered, tag_date, delivery_id, repo_id)
VALUES ('tag3', false, CURRENT_TIMESTAMP, 1, 2);

INSERT INTO cooldown (user_id, end_date)
VALUES (1, NOW() + INTERVAL '1 day');

INSERT INTO outbox (user_id, status, sent_at)
VALUES (3, 'Pending', CURRENT_TIMESTAMP);
INSERT INTO outbox (user_id, status, sent_at)
VALUES (4, 'Pending', CURRENT_TIMESTAMP);

INSERT INTO request(id, creator, composite, state)
VALUES (3, 1, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (4, 1, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (5, 3, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (6, 3, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (7, 4, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (8, 4, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (9, 5, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (10, 5, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (11, 4, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (12, 4, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (13, 5, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (14, 5, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (15, 4, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (16, 4, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (17, 4, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (18, 4, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (19, 5, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (20, 5, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (21, 4, null, 'Pending');
INSERT INTO request(id, creator, composite, state)
VALUES (22, 5, null, 'Pending');

INSERT INTO apply
VALUES (1, 1, 'Pending');
INSERT INTO apply
VALUES (2, 1, 'Pending');

INSERT INTO archiverepo(id, repo_id)
VALUES (3, 1);
INSERT INTO archiverepo(id, repo_id)
VALUES (4, 1);

INSERT INTO createrepo(id, team_id)
VALUES (5, 1);
INSERT INTO createrepo(id, team_id)
VALUES (6, 1);

INSERT INTO createteam(id)
VALUES (7);
INSERT INTO createteam(id)
VALUES (8);

INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (9, 1,1);
INSERT INTO jointeam(id, team_id, assigment_id)
VALUES (10, 1,1);

INSERT INTO leavecourse(id, course_id)
VALUES (11, 1);
INSERT INTO leavecourse(id, course_id)
VALUES (12, 1);

INSERT INTO leaveteam(id, team_id)
VALUES (13, 1);
INSERT INTO leaveteam(id, team_id)
VALUES (14, 1);

INSERT INTO composite(id)
VALUES (15);
INSERT INTO composite(id)
VALUES (16);
INSERT INTO composite(id)
VALUES (17);
INSERT INTO composite(id)
VALUES (18);
INSERT INTO composite(id)
VALUES (19);
INSERT INTO composite(id)
VALUES (20);

INSERT INTO request(creator, composite, state)
VALUES (4, 17, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, 18, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, 18, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, 19, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, 20, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, 17, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, 20, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, 20, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, 15, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, 16, 'Pending');

COMMIT TRANSACTION;