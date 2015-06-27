CREATE INDEX f_short_name ON forum(short_name);

ALTER TABLE post ADD INDEX (forum, user_email);