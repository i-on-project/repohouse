SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

DROP TABLE IF EXISTS OTP;
DROP TABLE IF EXISTS Cooldown;
DROP TABLE IF EXISTS Outbox;
DROP TABLE IF EXISTS Feedback;
DROP TABLE IF EXISTS Tags;
DROP TABLE IF EXISTS createteam;
DROP TABLE IF EXISTS jointeam;
DROP TABLE IF EXISTS createrepo;
DROP TABLE IF EXISTS Repo;
DROP TABLE IF EXISTS Delivery;
DROP TABLE IF EXISTS Student_Team;
DROP TABLE IF EXISTS student_classroom;
DROP TABLE IF EXISTS Team;
DROP TABLE IF EXISTS Assignment;
DROP TABLE IF EXISTS Apply;
DROP TABLE IF EXISTS archiverepo;
DROP TABLE IF EXISTS leavecourse;
DROP TABLE IF EXISTS leaveteam;
DROP TABLE IF EXISTS Request cascade ;
DROP TABLE IF EXISTS composite;
DROP TABLE IF EXISTS Student_Course;
DROP TABLE IF EXISTS Teacher_Course;
DROP TABLE IF EXISTS Classroom;
DROP TABLE IF EXISTS Course;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS Teacher;
DROP TABLE IF EXISTS pendingteacher;
DROP TABLE IF EXISTS pendingstudent;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS AccessToken;

COMMIT TRANSACTION;
