#drop database testdb;
create database testdbmyisam;

use testdbmyisam;

create table `users` (
`id` mediumint unsigned auto_increment not null,
`email` char(32) not null unique,
`username` char(32) not null,
`name` char(64) not null,
`isAnonymous` tinyint unsigned not null default '0',
`about` TEXT,
primary key (`id`)
) engine=MyISAM default charset=cp1251;

create table `follow` (
`follower_id` mediumint unsigned not null,
`followee_id` mediumint unsigned not null,
primary key (`follower_id`, `followee_id`)
) engine=MyISAM default charset=cp1251;

create table `forum` (
`id` mediumint unsigned auto_increment not null,
`user_email` char(32) not null,
`name` varchar(255) not null unique ,
`short_name` varchar(255) not null unique,
`date_of_creating` TIMESTAMP default NOW(),
primary key (`id`)
) engine=MyISAM default charset=cp1251;

create table `thread` (
`id` mediumint unsigned auto_increment not null unique,
`isDeleted` tinyint unsigned not null default '0',
`isClosed` tinyint unsigned not null default '0',
`user_email` char(32) not null,
`forum` varchar(255) not null,
`message` LONGTEXT not null,
`title` char(255) not null,
`slug` char(255) not null,
`date_of_creating` TIMESTAMP default NOW(),
`likes` mediumint default 0,
`dislikes` mediumint default 0,
primary key (`id`)
) engine=MyISAM default charset=cp1251;

create table `post` (
`id` mediumint unsigned auto_increment not null,
`isDeleted` tinyint unsigned not null default '0',
`isEdited` tinyint unsigned not null default '0',
`isApproved` tinyint unsigned not null default '0',
`isSpam` tinyint unsigned not null default '0',
`isHighlighted` tinyint unsigned not null default '0',
`user_email` char(32) not null,
`forum` varchar(255) not null,
`thread` mediumint unsigned not null,
`parent` varchar(255) not null,
`message` LONGTEXT not null,
`date_of_creating` TIMESTAMP default NOW(),
`likes` mediumint default 0,
`dislikes` mediumint default 0,
primary key (`id`)
) engine=MyISAM default charset=cp1251;

create table `subscribtion` (
`user_id` mediumint unsigned not null,
`thread_id` mediumint unsigned not null,
primary key (`user_id`, `thread_id`)
) engine=MyISAM default charset=cp1251;