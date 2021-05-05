/*
Navicat MySQL Data Transfer

Source Server         : Single
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : za102

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-05-05 23:41:31
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for register_approval
-- ----------------------------
DROP TABLE IF EXISTS `register_approval`;
CREATE TABLE `register_approval` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rid` int(11) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `mac` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of register_approval
-- ----------------------------
INSERT INTO `register_approval` VALUES ('14', '1', '313135', 'c3246da094b482fa4618ff33e32c3627', 'HNQz+yxeQ9vuuEujXOVZUw==', '5-66*21*6');
