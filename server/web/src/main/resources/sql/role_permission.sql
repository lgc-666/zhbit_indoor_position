/*
Navicat MySQL Data Transfer

Source Server         : Single
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : za102

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-05-05 23:41:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rid` int(11) DEFAULT NULL,
  `pid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES ('2', '3', '2');
INSERT INTO `role_permission` VALUES ('13', '3', '9');
INSERT INTO `role_permission` VALUES ('15', '2', '2');
INSERT INTO `role_permission` VALUES ('22', '3', '7');
INSERT INTO `role_permission` VALUES ('26', '3', '3');
INSERT INTO `role_permission` VALUES ('29', '3', '5');
INSERT INTO `role_permission` VALUES ('32', '3', '4');
INSERT INTO `role_permission` VALUES ('35', '3', '10');
INSERT INTO `role_permission` VALUES ('38', '2', '10');
INSERT INTO `role_permission` VALUES ('39', '2', '7');
INSERT INTO `role_permission` VALUES ('42', '3', '14');
INSERT INTO `role_permission` VALUES ('43', '3', '15');
INSERT INTO `role_permission` VALUES ('49', '1', '15');
INSERT INTO `role_permission` VALUES ('50', '1', '14');
INSERT INTO `role_permission` VALUES ('54', '2', '14');
INSERT INTO `role_permission` VALUES ('55', '2', '15');
INSERT INTO `role_permission` VALUES ('58', '2', '9');
INSERT INTO `role_permission` VALUES ('59', '3', '14');
INSERT INTO `role_permission` VALUES ('60', '3', '15');
INSERT INTO `role_permission` VALUES ('61', '3', '8');
INSERT INTO `role_permission` VALUES ('62', '2', '8');
INSERT INTO `role_permission` VALUES ('65', '1', '11');
INSERT INTO `role_permission` VALUES ('66', '3', '11');
INSERT INTO `role_permission` VALUES ('67', '2', '11');
INSERT INTO `role_permission` VALUES ('68', '2', '3');
INSERT INTO `role_permission` VALUES ('69', '2', '13');
INSERT INTO `role_permission` VALUES ('70', '2', '12');
INSERT INTO `role_permission` VALUES ('71', '3', '13');
INSERT INTO `role_permission` VALUES ('72', '3', '12');
INSERT INTO `role_permission` VALUES ('73', '3', '6');
INSERT INTO `role_permission` VALUES ('74', '3', '1');
INSERT INTO `role_permission` VALUES ('75', '3', '20');
INSERT INTO `role_permission` VALUES ('76', '3', '22');
INSERT INTO `role_permission` VALUES ('77', '3', '21');
INSERT INTO `role_permission` VALUES ('78', '3', '23');
INSERT INTO `role_permission` VALUES ('79', '2', '23');
INSERT INTO `role_permission` VALUES ('80', '2', '22');
INSERT INTO `role_permission` VALUES ('81', '2', '21');
INSERT INTO `role_permission` VALUES ('82', '2', '20');
INSERT INTO `role_permission` VALUES ('83', '1', '23');
INSERT INTO `role_permission` VALUES ('84', '1', '24');
INSERT INTO `role_permission` VALUES ('85', '2', '24');
INSERT INTO `role_permission` VALUES ('86', '3', '24');
