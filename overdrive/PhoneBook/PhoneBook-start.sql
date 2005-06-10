CREATE DATABASE `phonebook`;
USE `phonebook`;
CREATE TABLE `entry` (
  `pk_entry` char(36) NOT NULL default '',
  `last_name` char(18) NOT NULL default '',
  `first_name` char(18) NOT NULL default '',
  `extension` char(18) NOT NULL default '',
  `user_name` char(9) NOT NULL default '',
  `editor` tinyint(3) unsigned NOT NULL default '0',
  `hired` datetime NOT NULL default '0000-00-00 00:00:00',
  `hours` double NOT NULL default '37.5'
) TYPE=MyISAM ROW_FORMAT=FIXED;
INSERT INTO `entry` (`pk_entry`,`last_name`,`first_name`,`extension`,`user_name`,`editor`,`hired`,`hours`) VALUES 
 ('c5b6bbb1-66d6-49cb-9db6-743af6627828','Clinton','William','5557437828','bubba',0,'1992-08-19 00:00:00',37.5),
 ('7c424227-8e19-4fb5-b089-423cfca723e1','Roosevelt','Theodore','5557438942','bull',0,'2001-09-14 00:00:00',37.5),
 ('9320ea40-0c01-43e8-9cec-8fb9b3928c2c','Kennedy','John F.','5557433928','fitz',0,'1987-05-29 00:00:00',37.5),
 ('3b27c933-c1dc-4d85-9744-c7d9debae196','Pierce','Franklin','5557437919','hawkeye',0,'1984-11-18 00:00:00',35),
 ('554ff9e7-a6f5-478a-b76b-a666f5c54e40','Jefferson','Thomas','5557435440','monty',0,'1976-07-04 00:00:00',37.5);
