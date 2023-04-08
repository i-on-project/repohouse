SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

CREATE OR REPLACE FUNCTION UpdateApplyRequets( )
RETURNS trigger
AS $$
declare
    teacherId integer;
begin
    if (new.state = 'Accepted') then
        select apply.teacher_id from apply where id = new.id into teacherId;
        if (teacherId is not null) then
            update users set is_created = true where id = teacherId;
            return new;
        end if;
    else if (new.state = 'Rejected') then
        select apply.teacher_id from apply where id = new.id into teacherId;
        if (teacherId is not null) then
            delete from student_team where team in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = teacherId)));
            delete from feedback where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = teacherId)));
            delete from createrepo where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = teacherId)));
            delete from tags where repo_id in (select id from repo where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = teacherId))));
            delete from repo where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = teacherId)));
            delete from leaveteam where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = teacherId)));
            delete from jointeam where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = teacherId)));
            delete from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = teacherId));
            delete from delivery where assignment_id in (select id from assignment where classroom_id in (select id from classroom where teacher_id = teacherId));
            delete from assignment where classroom_id in (select id from classroom where teacher_id = teacherId);
            delete from classroom where teacher_id = teacherId;
            delete from teacher_course where teacher = teacherId;
            delete from teacher where id = teacherId;
            return new;
        end if;
    end if;
    end if;
    return new;
END
$$ LANGUAGE plpgsql;


CREATE or Replace trigger UpdateApplyRequetsTrigger
after update on request
for each row execute procedure UpdateApplyRequets();


CREATE OR REPLACE FUNCTION DeleteTeachers()
RETURNS trigger
AS $$
begin
    delete from apply where teacher_id = old.id;
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