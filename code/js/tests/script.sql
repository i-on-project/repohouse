
create or replace procedure playwright()
language plpgsql
as $$

declare
    teacher_id integer;
    student_id integer;
    course_id integer;
    classroom_id integer;
    assign_id integer;
    team_id integer;
    delivery_id integer;
begin

    insert into pendingteacher
    values (1,'i-on-classcode-pending-teacher@outlook.pt',true,'i-on-classcode-pending-teacher',134310986,'sIIvGATqt4W2ggV3hdAlsGXxx1Q2cpNwYx1SS_K0MpY=,i-on-classcode-pending-teacher','bra9qjEU5TJeguA9gu+Uh3NqyW6pyI/9U51QAEVUEv5ThjHIpJQZF1nrXPjVO30C','2023-07-05');
    insert into pendingteacher
    values (2,'i-on-classcode-teacher@outlook.pt',true,'i-on-ClassCode-Teacher',133246860,'A-L3MgbqFGefNJJMT3FZUEFuJZDN6vw73eXDl9IPka0=','i-on-ClassCode-Teacher','YaMmkFUXa4YJ9VlUFld1lMKEAr+HyS3nM6tKgR1Artwnqq25cF+7IVt4bjIBSMVU','2023-07-05');

    insert into apply values (1, 1, 'Pending');
    insert into apply values (2, 2, 'Accepted');

    insert into pendingstudent
    values (1,'i-on-classcode-pending-student@outlook.pt',false,'i-on-classcode-pending-student',134311116,'byvHL326fBulzEfxDsTvoMKPHEBy0JcEhkEj-6cJQyE=','i-on-classcode-pending-student','2023-07-05');
    insert into pendingstudent
    values (2,'i-on-classcode-student@outlook.pt',false,'i-on-ClassCode-Student',133246965,'_ppkCqT_oT0c6uQvcMS0VwKiu_EwHwBbMuZAXQgMuGM=','i-on-ClassCode-Student','2023-07-05');

    insert into users
    values (1,'A48322@alunos.isel.pt',false,'i-on-classcode-pending-student',134311116,'byvHL326fBulzEfxDsTvoMKPHEBy0JcEhkEj-6cJQyE=','i-on-classcode-pending-student');
    insert into users
    values (2,'A48309@alunos.isel.pt',true,'i-on-ClassCode-Student',133246965,'hw8hEb_nD8eCoohwGw4CnstiRXDugBD_L1LF02jfgT8=','i-on-ClassCode-Student');
    insert into users
    values (3,'i-on-classcode-teacher@outlook.pt',true,'i-on-ClassCode-Teacher',133246860,'A-L3MgbqFGefNJJMT3FZUEFuJZDN6vw73eXDl9IPka0=','i-on-ClassCode-Teacher');

    insert into student values (1,48322);
    insert into student values (2,48309);

    insert into teacher values (3,'YaMmkFUXa4YJ9VlUFld1lMKEAr+HyS3nM6tKgR1Artwnqq25cF+7IVt4bjIBSMVU');

    insert into otp values (1,769685,'2023-07-05 13:53:33.238421',0);
    insert into otp values (2,181061,'2023-07-05 13:54:51.102347',0);

    insert into outbox values (1,'Sent','2023-07-05 13:43:40.038662');
    insert into outbox values (2,'Sent','2023-07-05 13:45:00.211563');

    select id from users where name = 'i-on-ClassCode-Teacher' into teacher_id;
    select id from users where name = 'i-on-ClassCode-Student' into student_id;

    insert into course (org_url, org_id, name, is_archived)
    values ('i-on-ClassCode', 1, 'ClassCode', false);

    select id from course where org_url = 'i-on-ClassCode' into course_id;

    insert into teacher_course (teacher, course)
    values (teacher_id, course_id);

    insert into classroom (name, last_sync, invite_code, is_archived, course_id, teacher_id)
    values ('ClassCode', now(), 'https://classroom.google.com/c/MTU5NjQ5NjYxNjIw', false, course_id, teacher_id);

    select id from classroom where name = 'ClassCode' into classroom_id;

    insert into student_classroom (student, classroom)
    values (student_id, classroom_id);

    insert into assignment (classroom_id, max_elems_per_group, min_elems_per_group, max_number_groups, release_date, description, title)
    values (classroom_id, 4, 1, 4, now(), 'Assignment description', 'Assignment title');

    select id from assignment where title = 'Assignment title' into assign_id;

    insert into team (name, is_created, is_closed, assignment)
    values ('Team 1', true, false, assign_id);

    select id from team where name = 'Team 1' into team_id;

    insert into student_team (student, team)
    values (student_id, team_id);

    insert into delivery (due_date, tag_control, assignment_id, last_sync)
        values (now(), 'tag', assign_id, now());

    select id from delivery where assignment_id = assign_id into delivery_id;

    insert into repo (name, url, is_created, team_id)
        values ('Repo 1', 'http://github.com', true, team_id);

end $$;

call playwright();
