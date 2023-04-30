SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

CREATE OR REPLACE FUNCTION CreateTeacher()
    RETURNS trigger
AS $$
declare
    teacherId integer;
begin
    if (new.is_created = true) then
        insert into users (email, is_created,github_id,github_username,token,name) values (new.email, new.is_created,new.github_id,new.github_username,new.token,new.name) returning id into teacherId;
        insert into teacher (id,github_token) values (teacherId,new.github_token);
    end if;
    return new;
end;
$$ LANGUAGE plpgsql;

CREATE or Replace trigger CreateTeacherTrigger
    after update on pendingteacher
    for each row execute procedure CreateTeacher();

CREATE OR REPLACE FUNCTION UpdateApplyRequets( )
RETURNS trigger
AS $$
declare
    teacherId integer;
begin
    if (new.state = 'Accepted') then
        select apply.pending_teacher_id from apply where id = new.id into teacherId;
        if (teacherId is not null) then
            update pendingteacher set is_created = true where id = teacherId;
            delete from apply where id = new.id;
            return new;
        end if;
    else if (new.state = 'Rejected') then
        select apply.pending_teacher_id from apply where id = new.id into teacherId;
        if (teacherId is not null) then
            delete from users where id = teacherId;
            delete from apply where id = new.id;
            return new;
        end if;
    end if;
    end if;
    return new;
END
$$ LANGUAGE plpgsql;


CREATE or Replace trigger UpdateApplyRequetsTrigger
after update on apply
for each row execute procedure UpdateApplyRequets();


CREATE OR REPLACE FUNCTION DeleteTeachers()
RETURNS trigger
AS $$
begin
    delete from apply where pending_teacher_id = old.id;
    delete from request where creator = old.id;
    delete from cooldown where user_id = old.id;
    delete from student_team where team in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id)));
    delete from feedback where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id)));
    delete from createrepo where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id)));
    delete from tags where repo_id in (select id from repo where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id))));
    delete from repo where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id)));
    delete from leaveteam where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id)));
    delete from jointeam where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id)));
    delete from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id));
    delete from delivery where assignment_id in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id));
    delete from assignment where classroom_id in (select id from classroom where teacher_id = old.id);
    delete from classroom where teacher_id = old.id;
    delete from teacher_course where teacher = old.id;
    delete from users where id = old.id;
    return new;
END
$$ LANGUAGE plpgsql;

CREATE or Replace trigger DeleteTeachersTrigger
after delete on teacher
for each row execute procedure DeleteTeachers();

COMMIT TRANSACTION;