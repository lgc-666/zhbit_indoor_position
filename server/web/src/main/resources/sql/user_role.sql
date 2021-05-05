/*
Navicat MySQL Data Transfer

Source Server         : Single
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : za102

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-05-05 23:42:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) DEFAULT NULL,
  `rid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  KEY `rid` (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('27', '4', '1');
INSERT INTO `user_role` VALUES ('78', '6', '1');
INSERT INTO `user_role` VALUES ('80', '2', '2');
INSERT INTO `user_role` VALUES ('81', '2', '3');
INSERT INTO `user_role` VALUES ('92', '3', '1');
INSERT INTO `user_role` VALUES ('98', '1', '1');
INSERT INTO `user_role` VALUES ('99', '1', '2');
INSERT INTO `user_role` VALUES ('100', '1', '3');
