/*
Navicat MySQL Data Transfer

Source Server         : Single
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : za102

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-05-05 23:40:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for class
-- ----------------------------
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class` (
  `classid` int(11) NOT NULL AUTO_INCREMENT,
  `adress` varchar(255) DEFAULT NULL,
  `x1` varchar(255) DEFAULT NULL,
  `y1` varchar(255) DEFAULT NULL,
  `x2` varchar(255) DEFAULT NULL,
  `y2` varchar(255) DEFAULT NULL,
  `stopJudge` int(11) DEFAULT NULL,
  `indoorname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`classid`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of class
-- ----------------------------
INSERT INTO `class` VALUES ('1', '办公区', '0,0', '0,30', '20,0', '20,30', '1', 'za102');
INSERT INTO `class` VALUES ('2', '展示区', '20,15', '20,30', '50,15', '50,30', '0', 'za102');
INSERT INTO `class` VALUES ('6', '休息区', '20,0', '20,15', '60,0', '60,15', '0', 'za102');
INSERT INTO `class` VALUES ('22', '销售区', '50,15', '50,30', '60,30', '60,15', '0', 'za102');
INSERT INTO `class` VALUES ('23', '实验区1', '0,0', '0,15', '15,0', '15,15', '0', 'za101');
INSERT INTO `class` VALUES ('24', '实验区2', '15,5', '15,15', '30,5', '30,15', '0', 'za101');
INSERT INTO `class` VALUES ('25', '实验区3', '15,0', '15,5', '30,0', '30,5', '0', 'za101');
INSERT INTO `class` VALUES ('26', '讲台', '0,25', '0,30', '50,25', '50,30', '0', 'mb404');
INSERT INTO `class` VALUES ('27', '听讲区', '0,0', '0,25', '50,0', '50,25', '0', 'mb404');
