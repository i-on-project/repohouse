SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

CREATE OR REPLACE FUNCTION UpdateApplyRequets( )
RETURNS trigger
AS $$
declare
    teacher_id integer;
begin
    if (new.state = 'Accepted') then
        select teacher_id from apply where id = new.id into teacher_id;
        if (teacher_id is not null) then
            update users set is_created = true where id = teacher_id;
            return new;
        end if;
    else if (new.state = 'Rejected') then
        select teacher_id from apply where id = new.id into teacher_id;
        if (teacher_id is not null) then
            delete from teacher where id = teacher_id;
            return new;
        end if;
    end if;
    end if;
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
    delete from users where id = old.id;
    return new;
END
$$ LANGUAGE plpgsql;

CREATE or Replace trigger DeleteTeachersTrigger
after delete on teacher
for each row execute procedure DeleteTeachers();

COMMIT TRANSACTION;