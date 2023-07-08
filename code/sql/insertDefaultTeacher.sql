SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;

INSERT INTO PendingTeacher
VALUES (1,'i-on-classcode-teacher@outlook.pt',true,'i-on-ClassCode-Teacher',133246860,'A-L3MgbqFGefNJJMT3FZUEFuJZDN6vw73eXDl9IPka0=','i-on-ClassCode-Teacher','YaMmkFUXa4YJ9VlUFld1lMKEAr+HyS3nM6tKgR1Artwnqq25cF+7IVt4bjIBSMVU','2023-07-05');
SELECT setval('pendingteacher_id_seq', (SELECT MAX(id) from "pendingteacher"));
INSERT INTO Apply VALUES (1, 1, 'Accepted');
SELECT setval('apply_id_seq', (SELECT MAX(id) from "apply"));
INSERT INTO Users
VALUES (1,'i-on-classcode-teacher@outlook.pt',true,'i-on-ClassCode-Teacher',133246860,'A-L3MgbqFGefNJJMT3FZUEFuJZDN6vw73eXDl9IPka0=','i-on-ClassCode-Teacher');
SELECT setval('users_id_seq', (SELECT MAX(id) from "users"));
INSERT INTO Teacher VALUES (1,'YaMmkFUXa4YJ9VlUFld1lMKEAr+HyS3nM6tKgR1Artwnqq25cF+7IVt4bjIBSMVU');
SELECT setval('team_id_seq', (SELECT MAX(id) from "teacher"));

COMMIT;