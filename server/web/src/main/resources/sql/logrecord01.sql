/*
Navicat MySQL Data Transfer

Source Server         : Single
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : za102

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-04-23 05:23:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for logrecord01
-- ----------------------------
DROP TABLE IF EXISTS `logrecord01`;
CREATE TABLE `logrecord01` (
  `logid` int(11) NOT NULL AUTO_INCREMENT,
  `id` varchar(255) DEFAULT NULL,
  `cardid` varchar(255) DEFAULT NULL,
  `changevalue` varchar(255) DEFAULT NULL,
  `gentime` varchar(255) DEFAULT NULL,
  `indoorname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`logid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
