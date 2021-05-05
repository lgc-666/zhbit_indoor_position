/*
Navicat MySQL Data Transfer

Source Server         : Single
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : za102

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-05-05 23:41:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for map_mamage
-- ----------------------------
DROP TABLE IF EXISTS `map_mamage`;
CREATE TABLE `map_mamage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fmapID` varchar(255) DEFAULT NULL,
  `indoorname` varchar(255) DEFAULT NULL,
  `longitude` varchar(255) DEFAULT NULL,
  `latitude` varchar(255) DEFAULT NULL,
  `charge` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of map_mamage
-- ----------------------------
INSERT INTO `map_mamage` VALUES ('1', '1315702510946439169', 'za102', '113.54034', '22.375079', '罗回彬');
INSERT INTO `map_mamage` VALUES ('6', '1380330418480115714', 'za101', '113.54043', '22.375079', '罗回彬');
INSERT INTO `map_mamage` VALUES ('7', '1380329452053688322', 'mb404', '113.545491', '22.368977', '谢青');
