/*
Navicat MySQL Data Transfer

Source Server         : l2glorydb
Source Server Version : 50562
Source Host           : localhost:3306
Source Database       : l2jexpressvacio

Target Server Type    : MYSQL
Target Server Version : 50562
File Encoding         : 65001

Date: 2020-04-10 23:31:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bot_report`
-- ----------------------------
DROP TABLE IF EXISTS `bot_report`;
CREATE TABLE `bot_report` (
  `report_id` int(10) NOT NULL AUTO_INCREMENT,
  `reported_name` varchar(45) DEFAULT NULL,
  `reported_objectId` int(10) DEFAULT NULL,
  `reporter_name` varchar(45) DEFAULT NULL,
  `reporter_objectId` int(10) DEFAULT NULL,
  `date` decimal(20,0) NOT NULL DEFAULT '0',
  `reportType` varchar(255) NOT NULL,
  `info` varchar(255) NOT NULL,
  `read` enum('true','false') NOT NULL DEFAULT 'false',
  PRIMARY KEY (`report_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of bot_report
-- ----------------------------
