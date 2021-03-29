
DROP TABLE IF EXISTS piHome;
CREATE TABLE piHome
(
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    openInHex   TEXT NOT NULL,
    closeInHex  TEXT NOT NULL,
    groupName   TEXT NOT NULL,
    switchName  TEXT NOT NULL,
    status      INT DEFAULT 1,
    openOutHex  TEXT NOT NULL,
    closeOutHex TEXT NOT NULL
);



INSERT INTO piHome (openInHex, closeInHex, groupName, switchName, status, openOutHex, closeOutHex, openInHex)
VALUES ('123', '123', '123', 'ALI.123', 1, '123', '123', '123');
INSERT INTO piHome (openInHex, closeInHex, groupName, switchName, status, openOutHex, closeOutHex, openInHex)
VALUES ('321', '321', '123', 'ALI.123', 1, '123', '123', '123');



UPDATE piHome
SET switchName = '00000'
WHERE piHome.openInHex = '123';

DELETE
FROM piHome
WHERE openInHex = '321';

select *
from piHome;
DROP TABLE IF EXISTS temp;
CREATE TABLE temp( outHex TEXT);
insert into temp (outHex)values ('222');
insert into temp (outHex)values ('123');
delete from temp where outHex='123';
select outHex from temp where outHex='123';
select * from temp;