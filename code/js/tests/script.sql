
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
    select id from users where name = 'i-on-ClassCode-Teacher' into teacher_id;

    select id from users where name = 'i-on-ClassCode-Student' into student_id;


    insert into course (org_url, org_id, name, is_archived)
    values ('i-on-ClassCode', 1, 'ClassCode', false);

    select id from course where org_url = 'i-on-ClassCode' into course_id;

    insert into teacher_course (teacher, course)
    values (teacher_id, course_id);

    insert into classroom (name, last_sync, invite_link, is_archived, course_id, teacher_id)
    values ('ClassCode', now(), 'https://classroom.google.com/c/MTU5NjQ5NjYxNjIw', false, course_id, teacher_id);

    select id from classroom where name = 'ClassCode' into classroom_id;

    insert into student_classroom (student, classroom)
    values (student_id, classroom_id);

    insert into assignment (classroom_id, max_elems_per_group, max_number_groups, release_date, description, title)
    values (classroom_id, 4, 4, now(), 'Assignment description', 'Assignment title');

    select id from assignment where title = 'Assignment title' into assign_id;

    insert into team (name, is_created, assignment)
    values ('Team 1', true, assign_id);

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

delete from feedback where description = 'Demo';
