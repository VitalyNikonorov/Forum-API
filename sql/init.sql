SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `about` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `isAnonymous` tinyint(1) default NULL,

  UNIQUE KEY `email` (`email`),
  PRIMARY KEY (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-------------------------------

CREATE TABLE IF NOT EXISTS `thread` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `date` DATETIME,
  `forum` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `isClosed` tinyint(1) default NULL,
  'isDeleted' tinyint(1) default NULL,
  'message' text COLLATE utf8mb4_unicode_ci NOT NULL,
  'slug' text COLLATE utf8mb4_unicode_ci NOT NULL,
  'title' text COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

------------------------------

CREATE TABLE IF NOT EXISTS `post` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  'date' DATETIME,
  `forum` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `isApproved` tinyint(1) default NULL,
  `isDeleted` tinyint(1) default NULL,
  `isEdited` tinyint(1) default NULL,
  'isHighlighted' tinyint(1) default NULL,
  'isSpam' tinyint(1) default NULL,
  'message' text COLLATE utf8mb4_unicode_ci NOT NULL,
  'parent' int(10) DEFAULT null,
  'thread' int(10),
  'user' varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

----------------------------

CREATE TABLE IF NOT EXISTS `forum` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `short_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  'user' varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,

  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`), `short_name` (`short_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


------------------------