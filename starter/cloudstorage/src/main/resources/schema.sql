-- DELETE from USERS;
-- DELETE from NOTES;
-- DELETE from FILES;
-- DELETE from CREDENTIALS;
CREATE TABLE IF NOT EXISTS USERS (
  userid serial PRIMARY KEY,
  username VARCHAR(20) UNIQUE,
  salt VARCHAR,
  password VARCHAR,
  firstname VARCHAR(20),
  lastname VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS NOTES (
    noteid serial PRIMARY KEY,
    notetitle VARCHAR(20),
    notedescription VARCHAR (1000),
    userid INT,
    foreign key (userid) references USERS(userid)
);

CREATE TABLE IF NOT EXISTS FILES (
    fileId serial PRIMARY KEY,
    filename VARCHAR,
    contenttype VARCHAR,
    filesize VARCHAR,
    userid INT,
    filedata BYTEA,
    foreign key (userid) references USERS(userid)
);

-- Credentials for storing site logins per user
CREATE TABLE IF NOT EXISTS CREDENTIALS (
    credentialid serial PRIMARY KEY,
    url VARCHAR(100) NOT NULL,
    username VARCHAR(30) NOT NULL,
    password VARCHAR(255) NOT NULL,
    userid INT NOT NULL,
    foreign key (userid) references USERS(userid)
);
