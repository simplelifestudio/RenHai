/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50610
Source Host           : localhost:3306
Source Database       : renhai

Target Server Type    : MYSQL
Target Server Version : 50610
File Encoding         : 65001

Date: 2013-09-13 07:59:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `device`
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
  `deviceId` int(11) NOT NULL AUTO_INCREMENT,
  `deviceSn` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`deviceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of device
-- ----------------------------

-- ----------------------------
-- Table structure for `devicecard`
-- ----------------------------
DROP TABLE IF EXISTS `devicecard`;
CREATE TABLE `devicecard` (
  `deviceCardId` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` int(11) NOT NULL,
  `registerTime` bigint(20) NOT NULL,
  `deviceModel` varchar(256) NOT NULL,
  `osVersion` varchar(128) NOT NULL,
  `appVersion` varchar(128) NOT NULL,
  `location` varchar(256) DEFAULT NULL,
  `isJailed` enum('Yes','No') NOT NULL,
  PRIMARY KEY (`deviceCardId`),
  KEY `devicecard_ibfk_1` (`deviceId`),
  CONSTRAINT `devicecard_ibfk_1` FOREIGN KEY (`deviceId`) REFERENCES `device` (`deviceId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of devicecard
-- ----------------------------

-- ----------------------------
-- Table structure for `globalimpresslabel`
-- ----------------------------
DROP TABLE IF EXISTS `globalimpresslabel`;
CREATE TABLE `globalimpresslabel` (
  `globalImpressLabelId` int(11) NOT NULL AUTO_INCREMENT,
  `impressLabelName` varchar(256) NOT NULL,
  `globalAssessCount` int(11) NOT NULL,
  PRIMARY KEY (`globalImpressLabelId`),
  KEY `index_impressLabel` (`impressLabelName`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of globalimpresslabel
-- ----------------------------
INSERT INTO `globalimpresslabel` VALUES ('1', '^#Happy#^', '0');
INSERT INTO `globalimpresslabel` VALUES ('2', '^#SoSo#^', '0');
INSERT INTO `globalimpresslabel` VALUES ('3', '^#Disgusting#^', '0');

-- ----------------------------
-- Table structure for `globalinterestlabel`
-- ----------------------------
DROP TABLE IF EXISTS `globalinterestlabel`;
CREATE TABLE `globalinterestlabel` (
  `globalInterestLabelId` int(11) NOT NULL AUTO_INCREMENT,
  `interestLabelName` varchar(256) NOT NULL,
  `globalMatchCount` int(11) NOT NULL,
  PRIMARY KEY (`globalInterestLabelId`),
  KEY `index_interestLabel` (`interestLabelName`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of globalinterestlabel
-- ----------------------------

-- ----------------------------
-- Table structure for `hotinterestlabelstatistics`
-- ----------------------------
DROP TABLE IF EXISTS `hotinterestlabelstatistics`;
CREATE TABLE `hotinterestlabelstatistics` (
  `hotInterestLabelStatisticsId` int(11) NOT NULL AUTO_INCREMENT,
  `saveTime` bigint(20) NOT NULL,
  `globalInterestLabelId` int(11) NOT NULL,
  `count` int(11) NOT NULL,
  PRIMARY KEY (`hotInterestLabelStatisticsId`),
  KEY `fk_HotInterestLabelStatistics_globalInterestLabelId_idx` (`globalInterestLabelId`),
  CONSTRAINT `fk_HotInterestLabelStatistics_globalInterestLabelId` FOREIGN KEY (`globalInterestLabelId`) REFERENCES `globalinterestlabel` (`globalInterestLabelId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of hotinterestlabelstatistics
-- ----------------------------

-- ----------------------------
-- Table structure for `impresscard`
-- ----------------------------
DROP TABLE IF EXISTS `impresscard`;
CREATE TABLE `impresscard` (
  `impressCardId` int(11) NOT NULL AUTO_INCREMENT,
  `profileId` int(11) DEFAULT NULL,
  `chatTotalCount` int(11) NOT NULL,
  `chatTotalDuration` int(11) NOT NULL,
  `chatLossCount` int(11) NOT NULL,
  PRIMARY KEY (`impressCardId`),
  KEY `profileId` (`profileId`),
  CONSTRAINT `impresscard_ibfk_1` FOREIGN KEY (`profileId`) REFERENCES `profile` (`profileId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of impresscard
-- ----------------------------

-- ----------------------------
-- Table structure for `impresslabelmap`
-- ----------------------------
DROP TABLE IF EXISTS `impresslabelmap`;
CREATE TABLE `impresslabelmap` (
  `impressLabelMaplId` int(11) NOT NULL AUTO_INCREMENT,
  `impressCardId` int(11) NOT NULL,
  `globalImpressLabelId` int(11) NOT NULL,
  `assessedCount` int(11) NOT NULL,
  `updateTime` bigint(20) NOT NULL,
  `assessCount` int(11) NOT NULL,
  PRIMARY KEY (`impressLabelMaplId`),
  KEY `fk_impressCardId_idx` (`impressCardId`),
  KEY `fk_globalImpressLabelId_idx` (`globalImpressLabelId`),
  CONSTRAINT `fk_globalImpressLabelId` FOREIGN KEY (`globalImpressLabelId`) REFERENCES `globalimpresslabel` (`globalImpressLabelId`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `fk_impressCardId` FOREIGN KEY (`impressCardId`) REFERENCES `impresscard` (`impressCardId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of impresslabelmap
-- ----------------------------

-- ----------------------------
-- Table structure for `interestcard`
-- ----------------------------
DROP TABLE IF EXISTS `interestcard`;
CREATE TABLE `interestcard` (
  `interestCardId` int(11) NOT NULL AUTO_INCREMENT,
  `profileId` int(11) NOT NULL,
  PRIMARY KEY (`interestCardId`),
  KEY `profileId` (`profileId`),
  CONSTRAINT `interestcard_ibfk_1` FOREIGN KEY (`profileId`) REFERENCES `profile` (`profileId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of interestcard
-- ----------------------------

-- ----------------------------
-- Table structure for `interestlabelmap`
-- ----------------------------
DROP TABLE IF EXISTS `interestlabelmap`;
CREATE TABLE `interestlabelmap` (
  `interestLabelMaplId` int(11) NOT NULL AUTO_INCREMENT,
  `interestCardId` int(11) NOT NULL,
  `globalInterestLabelId` int(11) NOT NULL,
  `labelOrder` int(11) NOT NULL,
  `matchCount` int(11) NOT NULL,
  `validFlag` enum('Valid','Invalid') NOT NULL,
  PRIMARY KEY (`interestLabelMaplId`),
  KEY `interestCardId_idx` (`interestCardId`),
  KEY `fk_globalInterestLabelId_idx` (`globalInterestLabelId`),
  CONSTRAINT `fk_globalInterestLabelId` FOREIGN KEY (`globalInterestLabelId`) REFERENCES `globalinterestlabel` (`globalInterestLabelId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_interestCardId` FOREIGN KEY (`interestCardId`) REFERENCES `interestcard` (`interestCardId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of interestlabelmap
-- ----------------------------

-- ----------------------------
-- Table structure for `operationcode`
-- ----------------------------
DROP TABLE IF EXISTS `operationcode`;
CREATE TABLE `operationcode` (
  `operationCodeId` int(11) NOT NULL AUTO_INCREMENT,
  `operationCode` int(11) NOT NULL,
  `operationType` enum('All','System','User') NOT NULL,
  `description` varchar(256) NOT NULL,
  PRIMARY KEY (`operationCodeId`),
  KEY `index_operationCode` (`operationCode`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of operationcode
-- ----------------------------
INSERT INTO `operationcode` VALUES ('1', '1001', 'User', '建立WebSocket连接');
INSERT INTO `operationcode` VALUES ('2', '1002', 'User', '测试请求 AlohaRequest');
INSERT INTO `operationcode` VALUES ('3', '1003', 'System', '测试响应 AlohaResponse');
INSERT INTO `operationcode` VALUES ('4', '1004', 'User', 'App数据同步请求 AppDataSyncRequest - 查询');
INSERT INTO `operationcode` VALUES ('5', '1005', 'User', 'App数据同步请求 AppDataSyncRequest - 更新设备卡片');
INSERT INTO `operationcode` VALUES ('6', '1006', 'User', 'App数据同步请求 AppDataSyncRequest - 更新兴趣卡片');
INSERT INTO `operationcode` VALUES ('7', '1007', 'System', 'App数据同步响应 AppDataSyncResponse');
INSERT INTO `operationcode` VALUES ('8', '1008', 'User', 'Server数据同步请求 ServerDataSyncRequest');
INSERT INTO `operationcode` VALUES ('9', '1009', 'System', 'Server数据同步响应 ServerDataSyncResponse');
INSERT INTO `operationcode` VALUES ('10', '1010', 'System', '业务会话通知 BusinessSessionNotification - 会话绑定');
INSERT INTO `operationcode` VALUES ('11', '1011', 'System', '业务会话通知 BusinessSessionNotification - 对方拒绝');
INSERT INTO `operationcode` VALUES ('12', '1012', 'System', '业务会话通知 BusinessSessionNotification - 对方同意');
INSERT INTO `operationcode` VALUES ('13', '1013', 'User', '业务会话通知响应 BusinessSessionNotificationResponse - 确认收到');
INSERT INTO `operationcode` VALUES ('14', '1014', 'User', '业务会话请求 BusinessSessionRequest - 选择业务');
INSERT INTO `operationcode` VALUES ('15', '1015', 'User', '业务会话请求 BusinessSessionRequest - 退出业务');
INSERT INTO `operationcode` VALUES ('16', '1016', 'User', '业务会话请求 BusinessSessionRequest - 同意聊天');
INSERT INTO `operationcode` VALUES ('17', '1017', 'User', '业务会话请求 BusinessSessionRequest - 拒绝聊天');
INSERT INTO `operationcode` VALUES ('18', '1018', 'User', '业务会话请求 BusinessSessionRequest - 结束通话');
INSERT INTO `operationcode` VALUES ('19', '1019', 'User', '业务会话请求 BusinessSessionRequest - 评价');
INSERT INTO `operationcode` VALUES ('20', '1020', 'User', '业务会话请求 BusinessSessionRequest - 评价并退出');
INSERT INTO `operationcode` VALUES ('21', '1021', 'System', '业务会话响应 BusinessSessionResponse');
INSERT INTO `operationcode` VALUES ('22', '1022', 'System', 'Server广播通知 BroadcastNotification');
INSERT INTO `operationcode` VALUES ('23', '1100', 'System', 'WebSocket连接超时，释放设备');
INSERT INTO `operationcode` VALUES ('24', '1101', 'System', '空闲状态超时，释放设备');
INSERT INTO `operationcode` VALUES ('25', '1102', 'System', '消息响应超时，释放设备');
INSERT INTO `operationcode` VALUES ('26', '1103', 'System', '周期性保存数据到数据库');
INSERT INTO `operationcode` VALUES ('27', '1104', 'System', '周期性更新WebRTC Session');

-- ----------------------------
-- Table structure for `profile`
-- ----------------------------
DROP TABLE IF EXISTS `profile`;
CREATE TABLE `profile` (
  `profileId` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` int(11) NOT NULL,
  `serviceStatus` enum('Banned','Normal') NOT NULL,
  `unbanDate` bigint(20) DEFAULT NULL,
  `lastActivityTime` bigint(20) NOT NULL,
  `createTime` bigint(20) NOT NULL,
  `active` enum('No','Yes') DEFAULT NULL,
  PRIMARY KEY (`profileId`),
  KEY `profile_ibfk_1` (`deviceId`),
  CONSTRAINT `profile_ibfk_1` FOREIGN KEY (`deviceId`) REFERENCES `device` (`deviceId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of profile
-- ----------------------------

-- ----------------------------
-- Table structure for `profileoperationlog`
-- ----------------------------
DROP TABLE IF EXISTS `profileoperationlog`;
CREATE TABLE `profileoperationlog` (
  `profileOperationLogId` int(11) NOT NULL AUTO_INCREMENT,
  `profileId` int(11) NOT NULL,
  `logTime` int(11) NOT NULL,
  `operationCodeId` int(11) NOT NULL,
  `logInfo` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`profileOperationLogId`),
  KEY `fk_Pro_profileId_idx` (`profileId`),
  KEY `fk_Pro_operCode_idx` (`operationCodeId`),
  CONSTRAINT `fk_Pro_operCode` FOREIGN KEY (`operationCodeId`) REFERENCES `operationcode` (`operationCodeId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Pro_profileId` FOREIGN KEY (`profileId`) REFERENCES `profile` (`profileId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of profileoperationlog
-- ----------------------------

-- ----------------------------
-- Table structure for `sessionprofilemap`
-- ----------------------------
DROP TABLE IF EXISTS `sessionprofilemap`;
CREATE TABLE `sessionprofilemap` (
  `sessionImpressMapId` int(11) NOT NULL AUTO_INCREMENT,
  `sessionRecordId` int(11) NOT NULL,
  `profileId` int(11) NOT NULL,
  PRIMARY KEY (`sessionImpressMapId`),
  KEY `fk_sessionRecordId_idx` (`sessionRecordId`),
  KEY `fk_profileId_idx` (`profileId`),
  CONSTRAINT `sessionprofilemap_ibfk_1` FOREIGN KEY (`sessionRecordId`) REFERENCES `sessionrecord` (`sessionRecordId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_profileId` FOREIGN KEY (`profileId`) REFERENCES `profile` (`profileId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sessionprofilemap
-- ----------------------------

-- ----------------------------
-- Table structure for `sessionrecord`
-- ----------------------------
DROP TABLE IF EXISTS `sessionrecord`;
CREATE TABLE `sessionrecord` (
  `sessionRecordId` int(11) NOT NULL AUTO_INCREMENT,
  `businessType` enum('Random','Interest') NOT NULL,
  `startTime` bigint(20) NOT NULL,
  `duration` int(11) NOT NULL,
  `endStatus` enum('ChatConfirm','VideoChat','Idle','Assess') NOT NULL,
  `endReason` enum('Reject','ConnectionLoss','NormalEnd') NOT NULL,
  PRIMARY KEY (`sessionRecordId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sessionrecord
-- ----------------------------

-- ----------------------------
-- Table structure for `statisticsitem`
-- ----------------------------
DROP TABLE IF EXISTS `statisticsitem`;
CREATE TABLE `statisticsitem` (
  `statisticsitemId` int(11) NOT NULL AUTO_INCREMENT,
  `statisticsItem` int(11) NOT NULL,
  `description` varchar(256) NOT NULL,
  PRIMARY KEY (`statisticsitemId`),
  KEY `index_statisticsItem` (`statisticsItem`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of statisticsitem
-- ----------------------------
INSERT INTO `statisticsitem` VALUES ('1', '1', '在线设备数');
INSERT INTO `statisticsitem` VALUES ('2', '2', '随机业务设备数');
INSERT INTO `statisticsitem` VALUES ('3', '3', '兴趣业务设备数');
INSERT INTO `statisticsitem` VALUES ('4', '4', '处于聊天状态设备数');
INSERT INTO `statisticsitem` VALUES ('5', '5', '随机聊天设备数');
INSERT INTO `statisticsitem` VALUES ('6', '6', '兴趣聊天设备数');
INSERT INTO `statisticsitem` VALUES ('7', '7', '热门标签');

-- ----------------------------
-- Table structure for `systemmodule`
-- ----------------------------
DROP TABLE IF EXISTS `systemmodule`;
CREATE TABLE `systemmodule` (
  `moduleId` int(11) NOT NULL AUTO_INCREMENT,
  `moduleNo` int(11) NOT NULL,
  `description` varchar(256) NOT NULL,
  PRIMARY KEY (`moduleId`),
  KEY `index_moduleId` (`moduleNo`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of systemmodule
-- ----------------------------
INSERT INTO `systemmodule` VALUES ('1', '1', 'business');
INSERT INTO `systemmodule` VALUES ('2', '2', 'db');
INSERT INTO `systemmodule` VALUES ('3', '3', 'json');
INSERT INTO `systemmodule` VALUES ('4', '4', 'log');
INSERT INTO `systemmodule` VALUES ('5', '5', 'servlets');
INSERT INTO `systemmodule` VALUES ('6', '6', 'util');
INSERT INTO `systemmodule` VALUES ('7', '7', 'websocket');

-- ----------------------------
-- Table structure for `systemoperationlog`
-- ----------------------------
DROP TABLE IF EXISTS `systemoperationlog`;
CREATE TABLE `systemoperationlog` (
  `systemOperationLogId` int(11) NOT NULL AUTO_INCREMENT,
  `moduleId` int(11) NOT NULL,
  `logTime` bigint(20) NOT NULL,
  `operationCodeId` int(11) NOT NULL,
  `logInfo` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`systemOperationLogId`),
  KEY `fk_SysOpLog_moduleId_idx` (`moduleId`),
  KEY `fk_SysOpLog_operCode_idx` (`operationCodeId`),
  CONSTRAINT `fk_SysOpLog_moduleId` FOREIGN KEY (`moduleId`) REFERENCES `systemmodule` (`moduleId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_SysOpLog_operCode` FOREIGN KEY (`operationCodeId`) REFERENCES `operationcode` (`operationCodeId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of systemoperationlog
-- ----------------------------

-- ----------------------------
-- Table structure for `systemstatistics`
-- ----------------------------
DROP TABLE IF EXISTS `systemstatistics`;
CREATE TABLE `systemstatistics` (
  `systemStatisticsId` int(11) NOT NULL AUTO_INCREMENT,
  `saveTime` int(11) NOT NULL,
  `statisticsItemId` int(11) NOT NULL,
  `count` int(11) NOT NULL,
  PRIMARY KEY (`systemStatisticsId`),
  KEY `fk_SysStat_statItem_idx` (`statisticsItemId`),
  CONSTRAINT `fk_SysStat_statItem` FOREIGN KEY (`statisticsItemId`) REFERENCES `statisticsitem` (`statisticsitemId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of systemstatistics
-- ----------------------------

-- ----------------------------
-- Table structure for `webrtcsession`
-- ----------------------------
DROP TABLE IF EXISTS `webrtcsession`;
CREATE TABLE `webrtcsession` (
  `webRtcSessionId` int(11) NOT NULL AUTO_INCREMENT,
  `webrtcsession` varchar(256) NOT NULL,
  `requestDate` int(11) NOT NULL,
  `token` varchar(256) NOT NULL,
  `tokenUpdateDate` int(11) NOT NULL,
  `expirationDate` varchar(256) NOT NULL,
  PRIMARY KEY (`webRtcSessionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of webrtcsession
-- ----------------------------
