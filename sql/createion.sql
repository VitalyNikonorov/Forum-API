
CREATE TABLE user (
  id int(10) NOT NULL AUTO_INCREMENT,
  name VARCHAR(255),
  username VARCHAR(255),
  email VARCHAR(255) NOT NULL UNIQUE,
  about TEXT,
  isAnonymous ENUM ('true', 'false'),
PRIMARY KEY (id));


CREATE TABLE forum (
  id int(10) NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) UNIQUE,
  short_name VARCHAR(255) UNIQUE,
  user VARCHAR(255),
  PRIMARY KEY (id));

CREATE TABLE thread (
  id int(10) NOT NULL AUTO_INCREMENT,
  date DATETIME,
  forum VARCHAR(255),
  isClosed ENUM ('true', 'false'),
  isDeleted ENUM ('true', 'false'),
  message text,
  slug text,
  title text,
  user VARCHAR(255),
PRIMARY KEY (id));

CREATE TABLE post (
  id int NOT NULL AUTO_INCREMENT,
  date DATETIME,
  forum VARCHAR(32),
  isApproved ENUM ('true', 'false'),
  isDeleted ENUM ('true', 'false'),
  isEdited ENUM ('true', 'false'),
  isHighlighted ENUM ('true', 'false'),
  isSpam ENUM ('true', 'false'),
  message text,
  parent int DEFAULT null,
  thread int,
  user VARCHAR(255),
PRIMARY KEY (id));