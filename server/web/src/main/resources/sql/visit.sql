/*
Navicat MySQL Data Transfer

Source Server         : Single
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : za102

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-04-23 05:23:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for visit
-- ----------------------------
DROP TABLE IF EXISTS `visit`;
CREATE TABLE `visit` (
  `visitid` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `inJudge` int(11) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `left_time` datetime DEFAULT NULL,
  `rt` varchar(255) DEFAULT NULL,
  `visited_times` int(11) DEFAULT NULL,
  `beat` datetime DEFAULT NULL,
  `last_in_time` datetime DEFAULT NULL,
  `mac` varchar(255) DEFAULT NULL,
  `rssi` int(11) DEFAULT NULL,
  `indoorname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`visitid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
