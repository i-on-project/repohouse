SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

INSERT INTO users (email, is_created, github_username, github_id, token, name)
VALUES ('test@alunos.isel.pt', false, 'test123', 12345, 'token', 'teacher1');
INSERT INTO users (email, is_created, github_username, github_id, token, name)
VALUES ('test1@alunos.isel.pt', false, 'test1234', 123452, 'token1', 'teacher2');
INSERT INTO users (email, is_created, github_username, github_id, token, name)
VALUES ('test2@alunos.isel.pt', false, 'test1235', 123425, 'token2', 'student1');
INSERT INTO users (email, is_created, github_username, github_id, token, name)
VALUES ('test3@alunos.isel.pt', false, 'test12345', 1234152, 'token3', 'student2');
INSERT INTO users (email, is_created, github_username, github_id, token, name)
VALUES ('test4@alunos.isel.pt', false, 'test12345a', 12341527, 'token4', 'student3');
INSERT INTO users (email, is_created, github_username, github_id, token, name)
VALUES ('test7@alunos.isel.pt', false, 'test123as', 1234187, 'token7', 'teacher3');

INSERT INTO teacher (id, github_token)
VALUES (1, 'token');
INSERT INTO teacher (id, github_token)
VALUES (2, 'token1');
INSERT INTO teacher (id, github_token)
VALUES (6, 'token2');

INSERT INTO student (id, school_id)
VALUES (3, 1234);
INSERT INTO student (id, school_id)
VALUES (4, 1235);
INSERT INTO student (id)
VALUES (5);


INSERT INTO course (org_url, name)
VALUES ('https://daw.isel.pt', 'DAW');
INSERT INTO course (org_url, name)
VALUES ('https://daw1.isel.pt', 'DAW2');
INSERT INTO course (org_url, name)
VALUES ('https://daw3.isel.pt', 'DAW3');

INSERT INTO classroom (name, last_sync, invite_link, is_archived, course_id, teacher_id)
VALUES ('DAW-2223v-LI51D', CURRENT_TIMESTAMP, 'https://classroom.github.com/a/123', false, 1, 1);
INSERT INTO classroom (name, last_sync, invite_link, is_archived, course_id, teacher_id)
VALUES ('PDM-2223v-LI51D', CURRENT_TIMESTAMP, 'https://classroom.github.com/b/123', false, 2, 1);
INSERT INTO classroom (name, last_sync, invite_link, is_archived, course_id, teacher_id)
VALUES ('TVS-2223v-LI51D', CURRENT_TIMESTAMP, 'https://classroom.github.com/c/123', false, 1, 2);


INSERT INTO student_course (student, course)
VALUES (3, 1);
INSERT INTO student_course (student, course)
VALUES (4, 1);

INSERT INTO assignment (classroom_id, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (1, 2, 3, CURRENT_TIMESTAMP, 'description', 'title');
INSERT INTO assignment (classroom_id, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (1, 2, 3, CURRENT_TIMESTAMP, 'description1', 'title1');
INSERT INTO assignment (classroom_id, max_elems_per_group, max_number_groups, release_date, description, title)
VALUES (1, 2, 3, CURRENT_TIMESTAMP, 'description2', 'title2');

INSERT INTO team (name, is_created, assignment)
VALUES ('team1', false, 1);
INSERT INTO team (name, is_created, assignment)
VALUES ('team2', false, 1);
INSERT INTO team (name, is_created, assignment)
VALUES ('team3', false, 2);

INSERT INTO student_team (student, team)
VALUES (4, 1);
INSERT INTO student_team (student, team)
VALUES (4, 2);
INSERT INTO student_team (student, team)
VALUES (3, 1);

INSERT INTO delivery (due_date, tag_control, assignment_id)
VALUES (CURRENT_TIMESTAMP, 'tag', 1);
INSERT INTO delivery (due_date, tag_control, assignment_id)
VALUES (CURRENT_TIMESTAMP, 'tag1', 1);
INSERT INTO delivery (due_date, tag_control, assignment_id)
VALUES (CURRENT_TIMESTAMP, 'tag2', 2);

INSERT INTO feedback (description, label, team_id)
VALUES ('description1', 'label1', 1);
INSERT INTO feedback (description, label, team_id)
VALUES ('description2', 'label2', 1);

INSERT INTO repo (name, url, is_created, team_id)
VALUES ('repo1', 'https://repo.github.com/a/123', false, 1);
INSERT INTO repo (name, url, is_created, team_id)
VALUES ('repo2', 'https://repo.github.com/ab/123', false, 1);
INSERT INTO repo (name, url, is_created, team_id)
VALUES ('repo3', 'https://repo.github.com/abc/123', false, 1);

INSERT INTO tags (name, is_delivered, tag_date, delivery_id, repo_id)
VALUES ('tag1', false, CURRENT_TIMESTAMP, 1, 1);
INSERT INTO tags (name, is_delivered, tag_date, delivery_id, repo_id)
VALUES ('tag2', false, CURRENT_TIMESTAMP, 1, 1);
INSERT INTO tags (name, is_delivered, tag_date, delivery_id, repo_id)
VALUES ('tag3', false, CURRENT_TIMESTAMP, 1, 2);

INSERT INTO cooldown (user_id, end_date)
VALUES (1, NOW() + INTERVAL '1 day');

INSERT INTO outbox (user_id, otp, status, expired_at, sent_at)
VALUES (3, 123456, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO outbox (user_id, otp, status, expired_at, sent_at)
VALUES (4, 123456, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO request(creator, composite, state)
VALUES (1, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (1, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (3, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (3, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (5, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (5, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (5, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (5, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (5, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (5, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (4, null, 'Pending');
INSERT INTO request(creator, composite, state)
VALUES (5, null, 'Pending');

INSERT INTO apply(id, teacher_id)
VALUES (1, 1);
INSERT INTO apply(id, teacher_id)
VALUES (2, 1);

INSERT INTO archiverepo(id, repo_id)
VALUES (3, 1);
INSERT INTO archiverepo(id, repo_id)
VALUES (4, 1);

INSERT INTO createrepo(id, repo_id)
VALUES (5, 1);
INSERT INTO createrepo(id, repo_id)
VALUES (6, 1);

INSERT INTO createteam(id)
VALUES (7);
INSERT INTO createteam(id)
VALUES (8);

INSERT INTO jointeam(id, team_id)
VALUES (9, 1);
INSERT INTO jointeam(id, team_id)
VALUES (10, 1);

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

COMMIT;
