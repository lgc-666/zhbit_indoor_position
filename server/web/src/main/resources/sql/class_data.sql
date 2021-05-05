/*
Navicat MySQL Data Transfer

Source Server         : Single
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : za102

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-04-21 15:30:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for class_data
-- ----------------------------
DROP TABLE IF EXISTS `class_data`;
CREATE TABLE `class_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `adress` varchar(255) DEFAULT NULL,
  `new_student` int(11) NOT NULL DEFAULT '0',
  `in_class_number` int(11) NOT NULL DEFAULT '0',
  `jump_out` int(11) NOT NULL DEFAULT '0',
  `class_now_number` int(11) NOT NULL DEFAULT '0',
  `hour_class_number` int(11) NOT NULL,
  `hour_in_class_number` int(11) NOT NULL DEFAULT '0',
  `updatetime` datetime DEFAULT NULL,
  `hours` int(11) NOT NULL DEFAULT '0',
  `indoorname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
