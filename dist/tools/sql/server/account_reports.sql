CREATE TABLE IF NOT EXISTS `account_reports` (
  `login` varchar(32) NOT NULL,
  `email` varchar(255) NOT NULL DEFAULT '',
  `ban_expire` int(11) NOT NULL DEFAULT '0',
  `bot_report_points` int(10) NOT NULL DEFAULT '7',
  `points` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`login`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;