/*
Navicat MySQL Data Transfer

Source Server         : Single
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : za102

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-05-05 23:42:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `mac` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'lgc', '1a30914ce446ce58b7e1ad55346a5ba1', 'I60c7+kbbHsTjGu1O3w+7Q==', '68:c6:33:85:3d:69');
INSERT INTO `user` VALUES ('2', 'lgc2', '514d14969428752836d8d06dd9b00e1a', 'pNdG/wr8t/vYby/KaRqnkQ==', '68:c6:44:85:3d:69');
INSERT INTO `user` VALUES ('3', 'bb', '9f5a28f87fc778ab2a8c3b8b242032b7', 'MfKa5nVH1K36+b+PVMwCIg==', '68:c6:33:85:3d:03');
INSERT INTO `user` VALUES ('4', '廖冠昌', '777fab814cf3bdf594f622e460792bdd', '63/A7DYWfvQzefqkyvyWwg==', '68:c6:33:85:3d:01');
INSERT INTO `user` VALUES ('5', 'jlkjds', '5dda6f2e4e649cbd4d4a088ec20b9075', 'qRmc2o7RQpssxoEZswVFLQ==', '68:c6:33:85:3d:06');
INSERT INTO `user` VALUES ('6', 'zhbit', 'a39ff0fac454b2beb30adcc04644e4f4', 'm2cSgwUuA1Pm/049YfVB7Q==', '68:c6:33:85:3d:02');
