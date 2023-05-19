SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

DELETE FROM Feedback;
DELETE FROM Tags;
DELETE FROM Repo;
DELETE FROM Delivery;
DELETE FROM Student_Team;
DELETE FROM createrepo;
DELETE FROM leaveteam;
DELETE FROM jointeam;
DELETE FROM Team;
DELETE FROM Assignment;
DELETE FROM Apply;
DELETE FROM archiverepo;
delete from leavecourse;
DELETE FROM createteam;
ALTER TABLE composite DROP CONSTRAINT composite_id_fkey;
DELETE FROM request;
DELETE FROM composite;
DELETE FROM student_classroom;
DELETE FROM Classroom;
DELETE FROM teacher_course;
DELETE FROM Course;
DELETE FROM Student;
DELETE FROM Teacher;
DELETE FROM OTP;
DELETE FROM outbox;
DELETE From cooldown;
DELETE FROM Users;
DELETE FROM ChallengeInfo;


COMMIT TRANSACTION;

