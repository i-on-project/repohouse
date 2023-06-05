SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

CREATE OR REPLACE FUNCTION UpdateApplyRequests( )
RETURNS trigger
AS $$
declare
    teacherId integer;
    userId integer;
begin
    if (new.state = 'Accepted') then
        select apply.pending_teacher_id from apply where id = new.id into teacherId;
        if (teacherId is not null) then
            insert into users (email, github_username,is_created, github_id, token, name) VALUES (
                (select email from pendingteacher where id = teacherId),
                (select github_username from pendingteacher where id = teacherId),
                true,
                (select github_id from pendingteacher where id = teacherId),
                (select token from pendingteacher where id = teacherId),
                (select name from pendingteacher where id = teacherId)
            ) returning users.id into userId;
            insert into teacher (id,github_token) values (
                 userId,
                (select pendingteacher.github_token from pendingteacher where id = teacherId)
            );
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

DROP TRIGGER IF EXISTS UpdateApplyRequestsTrigger ON apply;
CREATE trigger UpdateApplyRequestsTrigger
after update on apply
for each row execute procedure UpdateApplyRequests();


CREATE OR REPLACE FUNCTION DeleteTeachers()
RETURNS trigger
AS $$
begin
    delete from apply where pending_teacher_id = old.id;
    delete from request where creator = old.id;
    delete from cooldown where user_id = old.id;
    delete from student_team where team in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id)));
    delete from feedback where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id)));
    delete from createrepo where repo_id in (select id from repo where team_id in (select id from team where assignment in (select id from assignment where classroom_id in (select id from classroom where teacher_id = old.id))));
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

DROP TRIGGER IF EXISTS DeleteTeachersTrigger ON teacher;
CREATE trigger DeleteTeachersTrigger
after delete on teacher
for each row execute procedure DeleteTeachers();


CREATE OR REPLACE FUNCTION SyncClassroom()
    RETURNS trigger
AS $$
begin
    if (new.last_sync != old.last_sync) then
        update classroom set last_sync = new.last_sync where id = (
            select classroom_id from assignment where id = new.assignment_id
        );
        return new;
    end if;
    return new;
end;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS Sync ON delivery;
CREATE trigger Sync
    after update on delivery
    for each row execute procedure SyncClassroom();

CREATE OR REPLACE FUNCTION DeleteTeam()
    RETURNS trigger
AS $$
declare repo_id_h integer;
begin
    select r.id from repo r where r.team_id = old.id into repo_id_h;
    delete from student_team where team = old.id;
    delete from feedback where team_id = old.id;
    delete from archiverepo where repo_id = repo_id_h;
    delete from createrepo where repo_id = repo_id_h;
    delete from createteam where team_id = old.id;
    delete from tags where repo_id = repo_id_h;
    update repo set team_id = null where id = repo_id_h;
    delete from leaveteam where team_id = old.id;
    delete from jointeam where team_id = old.id;
    RAISE NOTICE 'old.id: %', old.id;
    return old;
end;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS DeleteTeamTrigger ON team;
CREATE trigger DeleteTeamTrigger
    before delete on team
    for each row execute function DeleteTeam();

COMMIT TRANSACTION;
