/*
Navicat MySQL Data Transfer

Source Server         : Single
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : za102

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-05-05 23:40:58
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for machine
-- ----------------------------
DROP TABLE IF EXISTS `machine`;
CREATE TABLE `machine` (
  `mid` int(11) NOT NULL AUTO_INCREMENT,
  `machineid` varchar(255) DEFAULT NULL,
  `adress` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `leastRssi` int(11) DEFAULT NULL,
  `beat` datetime DEFAULT NULL,
  `x` varchar(255) DEFAULT NULL,
  `y` varchar(255) DEFAULT NULL,
  `indoorname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`mid`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of machine
-- ----------------------------
INSERT INTO `machine` VALUES ('1', '101', '办公区', '离线', '-100', '2021-05-05 23:20:18', '1', '1', 'za102');
INSERT INTO `machine` VALUES ('2', '102', '办公区', '离线', '-100', '2021-05-05 23:20:18', '1', '29', 'za102');
INSERT INTO `machine` VALUES ('3', '103', '办公区', '离线', '-100', '2021-05-05 23:20:18', '19', '29', 'za102');
INSERT INTO `machine` VALUES ('4', '104', '办公区', '离线', '-100', '2021-05-05 23:20:18', '19', '1', 'za102');
INSERT INTO `machine` VALUES ('5', '201', '展示区', '在线', '-100', '2021-05-05 23:20:18', '21', '16', 'za102');
INSERT INTO `machine` VALUES ('6', '202', '展示区', '在线', '-100', '2021-05-05 23:20:18', '21', '29', 'za102');
INSERT INTO `machine` VALUES ('7', '203', '展示区', '在线', '-100', '2021-05-05 23:20:18', '49', '29', 'za102');
INSERT INTO `machine` VALUES ('8', '204', '展示区', '在线', '-100', '2021-05-05 23:20:18', '49', '16', 'za102');
INSERT INTO `machine` VALUES ('9', '301', '销售区', '在线', '-100', '2021-05-05 23:20:18', '51', '16', 'za102');
INSERT INTO `machine` VALUES ('10', '302', '销售区', '在线', '-100', '2021-05-05 23:20:18', '51', '29', 'za102');
INSERT INTO `machine` VALUES ('11', '303', '销售区', '在线', '-100', '2021-05-05 23:20:18', '59', '29', 'za102');
INSERT INTO `machine` VALUES ('12', '304', '销售区', '在线', '-100', '2021-05-05 23:20:18', '59', '16', 'za102');
INSERT INTO `machine` VALUES ('13', '401', '休息区', '在线', '-100', '2021-05-05 23:20:18', '21', '1', 'za102');
INSERT INTO `machine` VALUES ('14', '402', '休息区', '在线', '-100', '2021-05-05 23:20:18', '21', '14', 'za102');
INSERT INTO `machine` VALUES ('15', '403', '休息区', '在线', '-100', '2021-05-05 23:20:18', '59', '14', 'za102');
INSERT INTO `machine` VALUES ('16', '404', '休息区', '在线', '-100', '2021-05-05 23:20:18', '59', '1', 'za102');
