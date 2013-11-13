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

CREATE SCHEMA `renhai` DEFAULT CHARACTER SET utf8 ;

use renhai;

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
  UNIQUE KEY `index_impressLabel` (`impressLabelName`(255)) USING BTREE
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
  UNIQUE KEY `index_interestLabel` (`interestLabelName`(255)) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `globalinterestlabel` VALUES ('1', '人海', '0');

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
  `impressLabelMapId` int(11) NOT NULL AUTO_INCREMENT,
  `impressCardId` int(11) NOT NULL,
  `globalImpressLabelId` int(11) NOT NULL,
  `assessedCount` int(11) NOT NULL,
  `updateTime` bigint(20) NOT NULL,
  `assessCount` int(11) NOT NULL,
  PRIMARY KEY (`impressLabelMapId`),
  KEY `fk_impressCardId_idx` (`impressCardId`),
  KEY `fk_globalImpressLabelId_idx` (`globalImpressLabelId`),
  CONSTRAINT `fk_globalImpressLabelId` FOREIGN KEY (`globalImpressLabelId`) REFERENCES `globalimpresslabel` (`globalImpressLabelId`) ON DELETE CASCADE ON UPDATE CASCADE,
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
  `interestLabelMapId` int(11) NOT NULL AUTO_INCREMENT,
  `interestCardId` int(11) NOT NULL,
  `globalInterestLabelId` int(11) NOT NULL,
  `labelOrder` int(11) NOT NULL,
  `matchCount` int(11) NOT NULL,
  `validFlag` enum('Valid','Invalid') NOT NULL,
  PRIMARY KEY (`interestLabelMapId`),
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
  `logTime` bigint(11) NOT NULL,
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
  CONSTRAINT `fk_profileId` FOREIGN KEY (`profileId`) REFERENCES `profile` (`profileId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `sessionprofilemap_ibfk_1` FOREIGN KEY (`sessionRecordId`) REFERENCES `sessionrecord` (`sessionRecordId`) ON DELETE CASCADE ON UPDATE CASCADE
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
  `sessionStartTime` bigint(20) NOT NULL,
  `sessionDuration` int(11) NOT NULL,
  `chatStartTime` bigint(20) DEFAULT NULL,
  `chatDuration` int(11) DEFAULT NULL,
  `endStatus` enum('ChatConfirm','VideoChat','Idle','Assess') NOT NULL,
  `endReason` enum('Reject','ConnectionLoss','AllDeviceRemoved','CheckFailed','NormalEnd') NOT NULL,
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
  `saveTime` bigint(11) NOT NULL,
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
  `webrtcsession` varchar(512) NOT NULL,
  `requestDate` bigint(20) NOT NULL,
  `token` varchar(512) NOT NULL,
  `tokenUpdateDate` bigint(20) NOT NULL,
  `expirationDate` bigint(20) NOT NULL,
  PRIMARY KEY (`webRtcSessionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of webrtcsession
-- ----------------------------
INSERT INTO `webrtcsession` VALUES (1, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMTowOTo0NSBQU1QgMjAxM34wLjkxNzM3MzR-', 1384319374375, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NTM5NWVhZTNlY2JjMmI2YWY3Y2RiOThkODdiMGJmYzgyNjQ5NDExNTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG93T1RvME5TQlFVMVFnTWpBeE0zNHdMamt4TnpNM016Ui0mY3JlYXRlX3RpbWU9MTM4NDMxOTM3NCZub25jZT0tMTYzMDk1NDE0MiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI0OTc0', 1384319374375, 1386824974375);
INSERT INTO `webrtcsession` VALUES (2, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxOSBQU1QgMjAxM34wLjM1MDA4MTI2fg', 1384320548406, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NTUwZjEyOTNmZTBlMDE3Y2Y3MGVjMjE0ODQ3ODNhYTIyODI0MTU5YzpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE9TQlFVMVFnTWpBeE0zNHdMak0xTURBNE1USTJmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTQ4Jm5vbmNlPTU2MTYwMDg1MCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MTQ4', 1384320548406, 1386826148406);
INSERT INTO `webrtcsession` VALUES (3, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToyMCBQU1QgMjAxM34wLjc3MjE1MDF-', 1384320548984, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZjhkY2E0ODIyZmIzZmEyNTc4ZWVjYmMwMmVkZjk2ODBlZjkwYjA2ZjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveU1DQlFVMVFnTWpBeE0zNHdMamMzTWpFMU1ERi0mY3JlYXRlX3RpbWU9MTM4NDMyMDU0OCZub25jZT0yMTE2NjQzNjU2JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxNDg=', 1384320548984, 1386826148984);
INSERT INTO `webrtcsession` VALUES (4, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToxNDo1MiBQU1QgMjAxM34wLjI5MjY1MjJ-', 1384319680781, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MGRjMTUzNDU5ZDk1NTVlNDMwYmIxNmI4Y2Q2NzEyMDYyMzlhZTMxNzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG94TkRvMU1pQlFVMVFnTWpBeE0zNHdMakk1TWpZMU1qSi0mY3JlYXRlX3RpbWU9MTM4NDMxOTY4MCZub25jZT0tODA5NjU0OTAxJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjUyODA=', 1384319680781, 1386825280781);
INSERT INTO `webrtcsession` VALUES (5, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODowNCBQU1QgMjAxM34wLjkwMjM3Mjh-', 1384320473343, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9OGRiOWQ3NWFmOGU4MGU2MDI5ZGIyNTUxNjAwMmYwN2RiMDkyODlkMjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvd05DQlFVMVFnTWpBeE0zNHdMamt3TWpNM01qaC0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ3MyZub25jZT0xNjg3OTY1MDQ1JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwNzM=', 1384320473343, 1386826073343);
INSERT INTO `webrtcsession` VALUES (6, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODowNyBQU1QgMjAxM34wLjIxNjM5MzQxfg', 1384320475953, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZDdiNjA3Nzk0NDEzNDU5NTcxOGI2YzQ2ZGQ3OThiMTVmMTE3MWY1YzpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvd055QlFVMVFnTWpBeE0zNHdMakl4TmpNNU16UXhmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDc1Jm5vbmNlPS03ODUwNTQ1MDYmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjA3NQ==', 1384320475953, 1386826075953);
INSERT INTO `webrtcsession` VALUES (7, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODowOCBQU1QgMjAxM34wLjM5MDUyNDg2fg', 1384320477609, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MGE4MjQ5NzM0MjRhN2MyNzU5NGVkYzYzYWM2YjJjNzAxNWY4ZDc5OTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvd09DQlFVMVFnTWpBeE0zNHdMak01TURVeU5EZzJmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDc3Jm5vbmNlPS0xOTA3NTcyMTM4JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwNzc=', 1384320477609, 1386826077609);
INSERT INTO `webrtcsession` VALUES (8, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoxMCBQU1QgMjAxM34wLjY4OTU5ODE0fg', 1384320479000, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YTU2NWY1YTEyZTliNDQ5OTAzN2NlMjM3N2UzYWE2NDQ2NTZlNTFkOTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveE1DQlFVMVFnTWpBeE0zNHdMalk0T1RVNU9ERTBmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDc5Jm5vbmNlPS04MjM0OTQ2NTUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjA3OQ==', 1384320479000, 1386826079000);
INSERT INTO `webrtcsession` VALUES (9, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoxMiBQU1QgMjAxM34wLjA0NTY3MzM3fg', 1384320481578, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZDVmYWMwZjBhOTRjNGI0MGE5Y2YwNmMyZjU5N2U4MDA5MzgxMDAyMTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveE1pQlFVMVFnTWpBeE0zNHdMakEwTlRZM016TTNmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDgxJm5vbmNlPTY1NzIzMDEyMyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MDgx', 1384320481578, 1386826081578);
INSERT INTO `webrtcsession` VALUES (10, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoxNSBQU1QgMjAxM34wLjUzODgyOTF-', 1384320483890, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MTI5ODNlNjhiMDVjMWVlN2MxNDgzMmM5NzU3NWJmOGY4Yzc1YTU3YTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveE5TQlFVMVFnTWpBeE0zNHdMalV6T0RneU9URi0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ4MyZub25jZT0tMTg3MTc3MDI4NyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MDgz', 1384320483890, 1386826083890);
INSERT INTO `webrtcsession` VALUES (11, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoxNSBQU1QgMjAxM34wLjMyMjc2OTY0fg', 1384320484593, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MzIxNDJlYzU1OTMwM2VlZDg3YjViMWZiMDBhMThjZmVjNGU5Nzk0NjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveE5TQlFVMVFnTWpBeE0zNHdMak15TWpjMk9UWTBmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDg0Jm5vbmNlPS0xNDM5NDA5NjU4JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwODQ=', 1384320484593, 1386826084593);
INSERT INTO `webrtcsession` VALUES (12, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoxNiBQU1QgMjAxM34wLjgwNzAxNzc0fg', 1384320485250, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YmQyNDc2ZGQwYjljNjdhYmM1YWM3NDQ5MGJlOTY0MmVjNTRiOWFlYTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveE5pQlFVMVFnTWpBeE0zNHdMamd3TnpBeE56YzBmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDg1Jm5vbmNlPTQzMTcyMDc3MyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MDg1', 1384320485250, 1386826085250);
INSERT INTO `webrtcsession` VALUES (13, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoxNyBQU1QgMjAxM34wLjMzNzcxMzZ-', 1384320486468, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MDc5ZDJiZjM5ZDMyNjhmMTJlYzM1ZWYyOTVkNDA2YTA1OGM3MTJiMTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveE55QlFVMVFnTWpBeE0zNHdMak16TnpjeE16Wi0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ4NiZub25jZT0xOTg1OTUzNTg2JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwODY=', 1384320486468, 1386826086468);
INSERT INTO `webrtcsession` VALUES (14, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoxOCBQU1QgMjAxM34wLjk1MTk3OTh-', 1384320487453, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZGYyY2FhMzM1MGU3YjIxMjFjZmUwYWE4OGFiMDFlZWJiNzQxYWMzZDpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveE9DQlFVMVFnTWpBeE0zNHdMamsxTVRrM09UaC0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ4NyZub25jZT0xMTk0ODMwOTE0JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwODc=', 1384320487453, 1386826087453);
INSERT INTO `webrtcsession` VALUES (15, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoxOSBQU1QgMjAxM34wLjIzOTUwMzU2fg', 1384320487953, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZmQ4ZjFiN2RlMzBiM2JmMGRjYTU4NDYxYjlhOGJmM2Y5NmRkMzdlNjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveE9TQlFVMVFnTWpBeE0zNHdMakl6T1RVd016VTJmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDg3Jm5vbmNlPTEwODA1MTMyNTQmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjA4Nw==', 1384320487953, 1386826087953);
INSERT INTO `webrtcsession` VALUES (16, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoxOSBQU1QgMjAxM34wLjkxMjgwMzA1fg', 1384320488484, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZWRjNjBlZDgyOWIwOWIwYjVhMzg3ZmM4NjVjNzMxYWQ5M2MxZTU2MTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveE9TQlFVMVFnTWpBeE0zNHdMamt4TWpnd016QTFmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDg4Jm5vbmNlPTg0OTM2Njk2NyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MDg4', 1384320488484, 1386826088484);
INSERT INTO `webrtcsession` VALUES (17, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyMCBQU1QgMjAxM34wLjQyNzUzMzQ1fg', 1384320489109, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MTEzODk3ZjZiODdmZmExNmFmZDQ4M2U2NjAwMWY4MGZiZDVjZWRlYjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU1DQlFVMVFnTWpBeE0zNHdMalF5TnpVek16UTFmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDg5Jm5vbmNlPTgzOTUxMTI4JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwODk=', 1384320489109, 1386826089109);
INSERT INTO `webrtcsession` VALUES (18, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyMSBQU1QgMjAxM34wLjc5NTk0ODU3fg', 1384320489718, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9OTEyMGU5YmJkYmYzNTFmNTIwZjllNDMwMDhiZmVhN2UzZDk3N2RmNjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU1TQlFVMVFnTWpBeE0zNHdMamM1TlRrME9EVTNmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDg5Jm5vbmNlPS0xMTMwOTU1NjI5JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwODk=', 1384320489718, 1386826089718);
INSERT INTO `webrtcsession` VALUES (19, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyMSBQU1QgMjAxM34wLjM3MjU5OTZ-', 1384320490265, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NjlhNGYxYzU2N2NiMzllODE0NDU1YzNjODZkNGYzNjcwMGNkMTQ3YjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU1TQlFVMVFnTWpBeE0zNHdMak0zTWpVNU9UWi0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ5MCZub25jZT0tMTE2ODg0ODQ1NSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MDkw', 1384320490265, 1386826090265);
INSERT INTO `webrtcsession` VALUES (20, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyMiBQU1QgMjAxM34wLjc2NDkxNjN-', 1384320490906, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NTYyYmI3ZmFkYmZiNWQ3ZjAzMzU0MmJmZGJiOGUyOTYwYWQ1N2NmNDpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU1pQlFVMVFnTWpBeE0zNHdMamMyTkRreE5qTi0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ5MCZub25jZT04ODgyNjk0MjYmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjA5MA==', 1384320490906, 1386826090906);
INSERT INTO `webrtcsession` VALUES (21, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyMiBQU1QgMjAxM34wLjEyMjk1ODY2fg', 1384320491500, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZjRlNmU3MzdiMTNkMmE1MzQwY2FjNDAyNmRlNWY0NTljZjBhZTFjNzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU1pQlFVMVFnTWpBeE0zNHdMakV5TWprMU9EWTJmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDkxJm5vbmNlPTk3NTIxMzQ3NyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MDkx', 1384320491500, 1386826091500);
INSERT INTO `webrtcsession` VALUES (22, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyMyBQU1QgMjAxM34wLjQ1NzM4NDR-', 1384320491937, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9Njk3OTZmNjgyNDVkODQwZjk0NDFhZDk1MzY5YWQwMzljNzJkYTI4YjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU15QlFVMVFnTWpBeE0zNHdMalExTnpNNE5EUi0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ5MSZub25jZT05MjYxMTQ2ODEmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjA5MQ==', 1384320491937, 1386826091937);
INSERT INTO `webrtcsession` VALUES (23, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyMyBQU1QgMjAxM34wLjUyMjYxODd-', 1384320492625, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZDk3ZTRlMWFlYzI3NTk2YjA5NDc0MmI4YzI2NDZhYmUyNTcyNjNlNzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU15QlFVMVFnTWpBeE0zNHdMalV5TWpZeE9EZC0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ5MiZub25jZT0xMzIwMDQwNjA1JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwOTI=', 1384320492625, 1386826092625);
INSERT INTO `webrtcsession` VALUES (24, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyNCBQU1QgMjAxM34wLjc2MjM1NDh-', 1384320493140, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NDk2NzViZDJhNTJlOGE5ZTQ4OWY1ZjNmN2RkZGVkOTcwMWYxNjIyYjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU5DQlFVMVFnTWpBeE0zNHdMamMyTWpNMU5EaC0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ5MyZub25jZT0xMTg4NTQxNzAxJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwOTM=', 1384320493140, 1386826093140);
INSERT INTO `webrtcsession` VALUES (25, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyNSBQU1QgMjAxM34wLjEyMzcyMjk3fg', 1384320493890, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MjQ1NzBlNDc0ZWNjZTBkODU2YjMxMWQzMTk2OGUwODZhZmUzOWRiODpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU5TQlFVMVFnTWpBeE0zNHdMakV5TXpjeU1qazNmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDkzJm5vbmNlPTIxMzgwMDQ3MTUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjA5Mw==', 1384320493890, 1386826093890);
INSERT INTO `webrtcsession` VALUES (26, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyNSBQU1QgMjAxM34wLjg5MDY2Njd-', 1384320494390, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9OWE5MzM1MDBlNDgzZDg2YjhhNzIwMWQ2YWUzZDZhYWY1Mjk3NTkzNjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU5TQlFVMVFnTWpBeE0zNHdMamc1TURZMk5qZC0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ5NCZub25jZT0tMTgxMzI4MDUzMSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MDk0', 1384320494390, 1386826094390);
INSERT INTO `webrtcsession` VALUES (27, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyNiBQU1QgMjAxM34wLjEwMzMxMjU1fg', 1384320494859, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YzQ3ZmQzNTk3ZmE3MTA1NzRhYjFhMGFkNWJmODNmMjhmYThkMDMzNzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU5pQlFVMVFnTWpBeE0zNHdMakV3TXpNeE1qVTFmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDk0Jm5vbmNlPS0xNzA2MTk4MDA0JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwOTQ=', 1384320494859, 1386826094859);
INSERT INTO `webrtcsession` VALUES (28, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyNiBQU1QgMjAxM34wLjYyMzExNDl-', 1384320495578, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NmNjMjY3ZjZjNTI1NDM1NjhlZTdiZjljMDkxZDNjM2FkYzRkZjIxMTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU5pQlFVMVFnTWpBeE0zNHdMall5TXpFeE5EbC0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ5NSZub25jZT0tMjAxNDI0MTM5JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwOTU=', 1384320495578, 1386826095578);
INSERT INTO `webrtcsession` VALUES (29, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyNyBQU1QgMjAxM34wLjAxMjc3OTQ3NH4', 1384320496375, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9OWQ2MzY1M2ExNzc5MDExYTkxMjE3YmUzNTRjODQyZjA5ZDJmYjRiZTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU55QlFVMVFnTWpBeE0zNHdMakF4TWpjM09UUTNOSDQmY3JlYXRlX3RpbWU9MTM4NDMyMDQ5NiZub25jZT0tMTU4ODUxMzQyJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYwOTY=', 1384320496375, 1386826096375);
INSERT INTO `webrtcsession` VALUES (30, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyOCBQU1QgMjAxM34wLjc1MTcxMTM3fg', 1384320496828, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NjBmZTRmODA1Y2E5ZDVlNjUyMzJiOTA2NjVkOThkYmJjOTY3YWQyNzpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU9DQlFVMVFnTWpBeE0zNHdMamMxTVRjeE1UTTNmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDk2Jm5vbmNlPTMyOTUzODg0MCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MDk2', 1384320496828, 1386826096828);
INSERT INTO `webrtcsession` VALUES (31, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyOCBQU1QgMjAxM34wLjAzMzAzNjI5fg', 1384320497437, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZGJlNTMyODQzZGNlYjk5NTRjMjgzNzM3YjE0YzY1ODY1MWQ2YjI3YzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU9DQlFVMVFnTWpBeE0zNHdMakF6TXpBek5qSTVmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDk3Jm5vbmNlPTU2NTgyMzA3MiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MDk3', 1384320497437, 1386826097437);
INSERT INTO `webrtcsession` VALUES (32, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyOSBQU1QgMjAxM34wLjYyOTc4Nzh-', 1384320497921, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NDc4NGQ1N2I1M2VhYWZhOTU5ODQyMjIzZDM2YWQ5MzM1NmM0MWRmOTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU9TQlFVMVFnTWpBeE0zNHdMall5T1RjNE56aC0mY3JlYXRlX3RpbWU9MTM4NDMyMDQ5NyZub25jZT0tMTYzNjE1MjY5MCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MDk3', 1384320497921, 1386826097921);
INSERT INTO `webrtcsession` VALUES (33, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODoyOSBQU1QgMjAxM34wLjI5NjExNTY0fg', 1384320499203, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9OWUyNjVjNzNmZjEzMTEyMGE5YmI1MTA5NTYxODY0MGUwNjI4YTY4ZjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RveU9TQlFVMVFnTWpBeE0zNHdMakk1TmpFeE5UWTBmZyZjcmVhdGVfdGltZT0xMzg0MzIwNDk5Jm5vbmNlPTEzODg0NDM5MTMmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjA5OQ==', 1384320499203, 1386826099203);
INSERT INTO `webrtcsession` VALUES (34, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozMSBQU1QgMjAxM34wLjExMzAyOTI0fg', 1384320500171, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YjBmY2VlOGNjNzhkZTYzNTZjMDkxY2RkNDA0Zjc2NjgzZDk5NTRhYzpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek1TQlFVMVFnTWpBeE0zNHdMakV4TXpBeU9USTBmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTAwJm5vbmNlPS04NzA0MDk4Njkmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEwMA==', 1384320500171, 1386826100171);
INSERT INTO `webrtcsession` VALUES (35, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozMiBQU1QgMjAxM34wLjU0MTkzMzV-', 1384320500953, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MzJhODg0MmVhOTg1ZmQ2NWRiOTNmN2E5ZGY1NTk3OTIyOWJhYzM0MDpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek1pQlFVMVFnTWpBeE0zNHdMalUwTVRrek16Vi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUwMCZub25jZT03MjUxMTQ4MjMmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEwMA==', 1384320500953, 1386826100953);
INSERT INTO `webrtcsession` VALUES (36, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozMiBQU1QgMjAxM34wLjk5MjA1MX4', 1384320501500, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NzcwY2E2N2NkNGZkOTA4Y2FlOWJmNTM3MjJiY2ZlYTUzYjQzODgzYTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek1pQlFVMVFnTWpBeE0zNHdMams1TWpBMU1YNCZjcmVhdGVfdGltZT0xMzg0MzIwNTAxJm5vbmNlPS0xNDg5MDM2MDc2JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMDE=', 1384320501500, 1386826101500);
INSERT INTO `webrtcsession` VALUES (37, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozMyBQU1QgMjAxM34wLjUyNDk0MDQzfg', 1384320502031, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NjgxZWMwODViZDAwNmE0NzJhNzA1MWQ1ZDM0OTIzYTRjNTZiMjI1MjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek15QlFVMVFnTWpBeE0zNHdMalV5TkRrME1EUXpmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTAyJm5vbmNlPS0xMzY2NTk2OTU4JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMDI=', 1384320502031, 1386826102031);
INSERT INTO `webrtcsession` VALUES (38, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozNCBQU1QgMjAxM34wLjg4NDkzNjh-', 1384320502625, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MWVhYzUxZTY2NDBmNGZmMzc3MDg3YjgwYWFkNjA4ODk4MmY1M2NlMzpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek5DQlFVMVFnTWpBeE0zNHdMamc0TkRrek5qaC0mY3JlYXRlX3RpbWU9MTM4NDMyMDUwMiZub25jZT03MDg0ODY4NTMmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEwMg==', 1384320502625, 1386826102625);
INSERT INTO `webrtcsession` VALUES (39, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozNCBQU1QgMjAxM34wLjg0Njg5NTE2fg', 1384320503062, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ODg3Y2U2ZGQ1Njg3NjkxMDRkMjVlMjZkMmJjYjI1MTM0OGMxZDhkZTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek5DQlFVMVFnTWpBeE0zNHdMamcwTmpnNU5URTJmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTAzJm5vbmNlPS02ODcwOTE4NjQmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEwMw==', 1384320503062, 1386826103062);
INSERT INTO `webrtcsession` VALUES (40, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozNSBQU1QgMjAxM34wLjIzNjc2ODQ4fg', 1384320503546, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9M2Q0YTEwYTZjMTE0ZmIwMDVhODc1NTg5OGRiMjBjYjA2MjIwMzk1NzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek5TQlFVMVFnTWpBeE0zNHdMakl6TmpjMk9EUTRmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTAzJm5vbmNlPTE2ODQxMjY4ODgmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEwMw==', 1384320503546, 1386826103546);
INSERT INTO `webrtcsession` VALUES (41, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozNSBQU1QgMjAxM34wLjY1NDYzMDV-', 1384320504171, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YzRmYzExYjRjY2RiZGRkYTRmYmUyMDYwMDMwMTMyMTIwMDNmYTdlNTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek5TQlFVMVFnTWpBeE0zNHdMalkxTkRZek1EVi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUwNCZub25jZT0xNDU3ODgxODk4JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMDQ=', 1384320504171, 1386826104171);
INSERT INTO `webrtcsession` VALUES (42, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozNiBQU1QgMjAxM34wLjI2ODQ5NzUzfg', 1384320504671, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NzFlYjUzMmU4NmY0ZDJhNzhkNWE5MmJkNWZiNDlhOWY0M2VhMGUxMDpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek5pQlFVMVFnTWpBeE0zNHdMakkyT0RRNU56VXpmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTA0Jm5vbmNlPTE1MzQ2MjczMjAmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEwNA==', 1384320504671, 1386826104671);
INSERT INTO `webrtcsession` VALUES (43, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozNiBQU1QgMjAxM34wLjU2Njc0NzN-', 1384320505250, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MDg5NGJiODc0Yjg2MjExMWJjOTMxNjA5MDJlYTMzMWJjNzRmMTVkYTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek5pQlFVMVFnTWpBeE0zNHdMalUyTmpjME56Ti0mY3JlYXRlX3RpbWU9MTM4NDMyMDUwNSZub25jZT0yNTkzMjA0Nzkmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEwNQ==', 1384320505250, 1386826105250);
INSERT INTO `webrtcsession` VALUES (44, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODozNyBQU1QgMjAxM34wLjMwMjY0NTE1fg', 1384320506109, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NzM3NDY3YTliZmI3MzZjNGM5NDRkYTIzMmFkZjk0YjYyODE1MDkyZjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0Rvek55QlFVMVFnTWpBeE0zNHdMak13TWpZME5URTFmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTA2Jm5vbmNlPS0xNjUyMzk3MDM2JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMDY=', 1384320506109, 1386826106109);
INSERT INTO `webrtcsession` VALUES (45, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0MSBQU1QgMjAxM34wLjcwNzU5fg', 1384320509734, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NGJlYTBhNWJiZjhlZDY0YjQzNjRiNTkwZmJmZjNmOTJkODIyMzVjYzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME1TQlFVMVFnTWpBeE0zNHdMamN3TnpVNWZnJmNyZWF0ZV90aW1lPTEzODQzMjA1MDkmbm9uY2U9LTE2MjkwMjk3OTQmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEwOQ==', 1384320509734, 1386826109734);
INSERT INTO `webrtcsession` VALUES (46, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0MSBQU1QgMjAxM34wLjA3NjY5MDQzNX4', 1384320510703, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YzRiOGIzMjc1ZmFkMjU2ZWEzZTQ0NGFlMDQ5ZWZlOThlNmVlOGEwZTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME1TQlFVMVFnTWpBeE0zNHdMakEzTmpZNU1EUXpOWDQmY3JlYXRlX3RpbWU9MTM4NDMyMDUxMCZub25jZT0tMTczOTY4MTc4JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMTA=', 1384320510703, 1386826110703);
INSERT INTO `webrtcsession` VALUES (47, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0MiBQU1QgMjAxM34wLjY1MzEzNjd-', 1384320511500, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MzUwNGFiYTFlYTk2ZDFmZDJjZDFlNGVjODQ3YzRlODU2M2Y0ZTVhMjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME1pQlFVMVFnTWpBeE0zNHdMalkxTXpFek5qZC0mY3JlYXRlX3RpbWU9MTM4NDMyMDUxMSZub25jZT0zNjg2NTExMzUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjExMQ==', 1384320511500, 1386826111500);
INSERT INTO `webrtcsession` VALUES (48, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0MyBQU1QgMjAxM34wLjQxNzIxMDF-', 1384320512015, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YzkwYjFiZTM0ODJiMzQ0NGQzYTcwOTcxYzY2YWI4Y2I3NjQwMjEwMDpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME15QlFVMVFnTWpBeE0zNHdMalF4TnpJeE1ERi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUxMiZub25jZT0tMTI5NTQ3MTg2MCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MTEy', 1384320512015, 1386826112015);
INSERT INTO `webrtcsession` VALUES (49, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0NCBQU1QgMjAxM34wLjI1NDQ3NTI0fg', 1384320512687, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9M2YwOWMwOGJkMjIxZDhjZDYyOTcwN2JjZDVmY2Y4NDRkNjBhNDFkMzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME5DQlFVMVFnTWpBeE0zNHdMakkxTkRRM05USTBmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTEyJm5vbmNlPTE1OTg0Mjc5ODUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjExMg==', 1384320512687, 1386826112687);
INSERT INTO `webrtcsession` VALUES (50, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0NCBQU1QgMjAxM34wLjk5NDc0ODl-', 1384320513234, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZTRlNDcyYTliMzcwMWM3MzI5MjA5YTIwOTA0M2VmZDVkMjU5NDIxMDpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME5DQlFVMVFnTWpBeE0zNHdMams1TkRjME9EbC0mY3JlYXRlX3RpbWU9MTM4NDMyMDUxMyZub25jZT0tODM2NDQyMjI0JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMTM=', 1384320513234, 1386826113234);
INSERT INTO `webrtcsession` VALUES (51, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0NSBQU1QgMjAxM34wLjE1MjE2MTN-', 1384320513812, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MDA1M2ZiNjFiYmRjNzM0NGJhZmFlZDJhNGJmMjFhNWFjNTMwZjMyNzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME5TQlFVMVFnTWpBeE0zNHdMakUxTWpFMk1UTi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUxMyZub25jZT0zNjY3OTIxNTcmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjExMw==', 1384320513812, 1386826113812);
INSERT INTO `webrtcsession` VALUES (52, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0NSBQU1QgMjAxM34wLjI2NDA5NjJ-', 1384320514609, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YTg2MGJmNmFkOTAwNmY5ZmEyZDE1ZjlkYzRiMDcwNGUzYmFlMjlmMjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME5TQlFVMVFnTWpBeE0zNHdMakkyTkRBNU5qSi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUxNCZub25jZT01MzYzMDM3OCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MTE0', 1384320514609, 1386826114609);
INSERT INTO `webrtcsession` VALUES (53, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0NiBQU1QgMjAxM34wLjU4NDU1MzJ-', 1384320515265, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MmZiNTJmZGJlYWFlOTE1MmMzNTQ1YzEwODc4YjIwNjI1YzI0OGNiNjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME5pQlFVMVFnTWpBeE0zNHdMalU0TkRVMU16Si0mY3JlYXRlX3RpbWU9MTM4NDMyMDUxNSZub25jZT01NDUwNDIyMjkmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjExNQ==', 1384320515265, 1386826115265);
INSERT INTO `webrtcsession` VALUES (54, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0NyBQU1QgMjAxM34wLjkyMzc3MTI2fg', 1384320515984, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MTdmZWViM2Y3MTdmNmUxMzQxNmM4OGRjNzBhZTY4OGUwZWFkYmMwMzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME55QlFVMVFnTWpBeE0zNHdMamt5TXpjM01USTJmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTE1Jm5vbmNlPTE2MzE3NjA1MjEmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjExNQ==', 1384320515984, 1386826115984);
INSERT INTO `webrtcsession` VALUES (55, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0NyBQU1QgMjAxM34wLjIwMjIyNDA4fg', 1384320516734, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NDk5MzI5NzZhODk4ZTc0ODBiMjlhZTc1YmM4NTlkNTYzMDY1MWUzZjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME55QlFVMVFnTWpBeE0zNHdMakl3TWpJeU5EQTRmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTE2Jm5vbmNlPS00OTE0MjI3OTgmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjExNg==', 1384320516734, 1386826116734);
INSERT INTO `webrtcsession` VALUES (56, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0OCBQU1QgMjAxM34wLjU4ODMwNTV-', 1384320517421, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZWE0YzlhMDA5NDIwN2FlNWQ4OWM5YTcwNTY2ZWRkOTgzNjE3N2IxZTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME9DQlFVMVFnTWpBeE0zNHdMalU0T0RNd05UVi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUxNyZub25jZT0xNDIyODEyNTAxJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMTc=', 1384320517421, 1386826117421);
INSERT INTO `webrtcsession` VALUES (57, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0OSBQU1QgMjAxM34wLjA2NTk0MTY5fg', 1384320517906, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NGEzNGViZmY3ZGU3NzAyY2Y5NDgzZTQ4ZTkxMzNlYjAxNGViYjZhNTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME9TQlFVMVFnTWpBeE0zNHdMakEyTlRrME1UWTVmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTE3Jm5vbmNlPS0xNDY2Njc0MjkxJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMTc=', 1384320517906, 1386826117906);
INSERT INTO `webrtcsession` VALUES (58, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo0OSBQU1QgMjAxM34wLjM2MDYzMDg3fg', 1384320518500, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NGRmZTYzN2VlZjU1Mzc3Zjc3MTFiMzk0NzNlZWU2Y2EwZGEwZTkxYzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvME9TQlFVMVFnTWpBeE0zNHdMak0yTURZek1EZzNmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTE4Jm5vbmNlPTE3MzI0OTk1Mjgmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjExOA==', 1384320518500, 1386826118500);
INSERT INTO `webrtcsession` VALUES (59, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1MCBQU1QgMjAxM34wLjkwNjA5MjM1fg', 1384320519031, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9Mjg2YWJiMTE3MzhhZjQ4ZjA3MDJjMDkzYTdiMWE1MzI5MDNlNGMwNjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU1DQlFVMVFnTWpBeE0zNHdMamt3TmpBNU1qTTFmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTE5Jm5vbmNlPS03NDAxNzUzNDUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjExOQ==', 1384320519031, 1386826119031);
INSERT INTO `webrtcsession` VALUES (60, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1MSBQU1QgMjAxM34wLjAzMzU2NzMxfg', 1384320519593, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9OWVkYmVjMmZkZjFmZmM0NjNkNmJlMjUxN2NjZGZkOGI4MjgxODlkYzpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU1TQlFVMVFnTWpBeE0zNHdMakF6TXpVMk56TXhmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTE5Jm5vbmNlPS0xMjUxMTI3OTgwJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMTk=', 1384320519593, 1386826119593);
INSERT INTO `webrtcsession` VALUES (61, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1MSBQU1QgMjAxM34wLjQzNjk5MDM4fg', 1384320520453, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NWVjZmNkODUxZTMzMWMzNzEyNzI2Zjg3MTFiYWRlZjY3ZWE2OGU3ZDpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU1TQlFVMVFnTWpBeE0zNHdMalF6TmprNU1ETTRmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTIwJm5vbmNlPS0xNDkwMzg0OTYwJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMjA=', 1384320520453, 1386826120453);
INSERT INTO `webrtcsession` VALUES (62, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1MiBQU1QgMjAxM34wLjQ1MTk4ODF-', 1384320521000, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NmFjNTM1ZGQ5NDFiMThkZTdiNDlmYjcwZTUwNWVjZDJiOGFlOWFhMzpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU1pQlFVMVFnTWpBeE0zNHdMalExTVRrNE9ERi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUyMSZub25jZT0tMTU1MTExMDM1MyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MTIx', 1384320521000, 1386826121000);
INSERT INTO `webrtcsession` VALUES (63, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1MyBQU1QgMjAxM34wLjc2MjQ4NDY3fg', 1384320521562, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YzE5MTAxZjc5ZDk1ZGVhYTJmYWEwNjUwODYzNjU5MzY2NmExYTBhNTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU15QlFVMVFnTWpBeE0zNHdMamMyTWpRNE5EWTNmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTIxJm5vbmNlPS05ODIxNzQ0ODgmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEyMQ==', 1384320521562, 1386826121562);
INSERT INTO `webrtcsession` VALUES (64, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1MyBQU1QgMjAxM34wLjMwNzQwMDUyfg', 1384320522187, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YTMwNGY2YWMyODFmZjExZWRkZjAzNjgwOGI3OWM3MzE5MWQwOWFjYjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU15QlFVMVFnTWpBeE0zNHdMak13TnpRd01EVXlmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTIyJm5vbmNlPS0zMjQ3NzY3ODMmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEyMg==', 1384320522187, 1386826122187);
INSERT INTO `webrtcsession` VALUES (65, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1NCBQU1QgMjAxM34wLjU4NDExNDV-', 1384320522671, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9OGM2ODgxMTRmM2MzZWRjODVmODIzYzJmYWY4MWM3MmFiYTQ3MDM1ZjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU5DQlFVMVFnTWpBeE0zNHdMalU0TkRFeE5EVi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUyMiZub25jZT0xNDUxNzA2OTgyJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMjI=', 1384320522671, 1386826122671);
INSERT INTO `webrtcsession` VALUES (66, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1NCBQU1QgMjAxM34wLjg3MDk3MTV-', 1384320523234, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MzAxNGZjZWY3N2M0NGY3ZDY4YmZjMTA2YmI5NWY4NjAwNWI0NWU2YzpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU5DQlFVMVFnTWpBeE0zNHdMamczTURrM01UVi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUyMyZub25jZT05MjgyNDMxODAmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEyMw==', 1384320523234, 1386826123234);
INSERT INTO `webrtcsession` VALUES (67, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1NSBQU1QgMjAxM34wLjkxMzIxNjM1fg', 1384320523781, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ODlhMGY0Y2EwMzE1ZDI1NmZmYTdiZTVjYjNkNmRiMWYxNjc3NmFhMzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU5TQlFVMVFnTWpBeE0zNHdMamt4TXpJeE5qTTFmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTIzJm5vbmNlPS02MTQ0NTc1OTQmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEyMw==', 1384320523781, 1386826123781);
INSERT INTO `webrtcsession` VALUES (68, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1NSBQU1QgMjAxM34wLjkyODgwMTY2fg', 1384320524453, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ODRjNjZmYWU1MjIyYzMzZjFjYTZmZDBmNDAzNWIwNWI0ZDU5OWE4YjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU5TQlFVMVFnTWpBeE0zNHdMamt5T0Rnd01UWTJmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTI0Jm5vbmNlPTExODUwNjA1NDUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEyNA==', 1384320524453, 1386826124453);
INSERT INTO `webrtcsession` VALUES (69, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1NiBQU1QgMjAxM34wLjk0NDIzMjZ-', 1384320525140, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NTc2ZmU0ZjJjMTJkOTYxYjk2OGMyZTQxMDkwMTkzNzM1MDliNDA5ODpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU5pQlFVMVFnTWpBeE0zNHdMamswTkRJek1qWi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUyNSZub25jZT0yMDc0NjEyNjY1JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMjU=', 1384320525140, 1386826125140);
INSERT INTO `webrtcsession` VALUES (70, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1NyBQU1QgMjAxM34wLjEwMjcxNzY0fg', 1384320525671, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YzNiM2Y4MDgxY2I0YmI4ZjVlYzQ0ZmFiZDk2ZjY1YzI2MzJjNjExNjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU55QlFVMVFnTWpBeE0zNHdMakV3TWpjeE56WTBmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTI1Jm5vbmNlPTE1NTc2NzY3NjMmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEyNQ==', 1384320525671, 1386826125671);
INSERT INTO `webrtcsession` VALUES (71, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1NyBQU1QgMjAxM34wLjI1NDYyMzgzfg', 1384320526234, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NGIyOTFhZDhlMjgwODIyM2QwM2MzODYzOTEzNWJhYjAwMjk1MzAwYjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU55QlFVMVFnTWpBeE0zNHdMakkxTkRZeU16Z3pmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTI2Jm5vbmNlPS0xNzg2OTQ1NDk0JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMjY=', 1384320526234, 1386826126234);
INSERT INTO `webrtcsession` VALUES (72, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1OCBQU1QgMjAxM34wLjI4NjIwMDg4fg', 1384320527078, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZWVhYjNhNGZjMTBhMDUwZDk4M2QxMzQ3NzgyYWY4OTQyMGZkNGNmMjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU9DQlFVMVFnTWpBeE0zNHdMakk0TmpJd01EZzRmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTI3Jm5vbmNlPS0xMTgyNDU2NzU4JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMjc=', 1384320527078, 1386826127078);
INSERT INTO `webrtcsession` VALUES (73, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1OSBQU1QgMjAxM34wLjk1NTQ0ODZ-', 1384320527765, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MWYzZGQ5MWJiMzA0ODlmMjdiYjkwNWM3OTllMzhiOTk2NGMzNTRmZDpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU9TQlFVMVFnTWpBeE0zNHdMamsxTlRRME9EWi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUyNyZub25jZT0yNjUyNjAzNDMmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEyNw==', 1384320527765, 1386826127765);
INSERT INTO `webrtcsession` VALUES (74, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyODo1OSBQU1QgMjAxM34wLjAwNDYwNjcyNH4', 1384320528468, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YTBkZDVlYTFmYTRmMjQ5ZGY1YzI3NjAzZDkxNzAzMTM3NGY1Mzk0ZTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T0RvMU9TQlFVMVFnTWpBeE0zNHdMakF3TkRZd05qY3lOSDQmY3JlYXRlX3RpbWU9MTM4NDMyMDUyOCZub25jZT0xNDE0ODA4NDUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEyOA==', 1384320528468, 1386826128468);
INSERT INTO `webrtcsession` VALUES (75, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowMCBQU1QgMjAxM34wLjYwNTMwMjc1fg', 1384320529093, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZmM0ZmRlOGNmOGNhNzg0Y2IyNjI1NDBkYTlhMDk3MDBlMWJkZGZhMjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd01DQlFVMVFnTWpBeE0zNHdMall3TlRNd01qYzFmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTI5Jm5vbmNlPTIwMjE3NzU0MjMmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEyOQ==', 1384320529093, 1386826129093);
INSERT INTO `webrtcsession` VALUES (76, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowMSBQU1QgMjAxM34wLjU3OTczNzd-', 1384320529562, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZTY3OGUzZjg0OTE1ZTM4MjNiZTk4OThmZWRjYjk3ODA3MDA5NWQ5NjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd01TQlFVMVFnTWpBeE0zNHdMalUzT1Rjek56ZC0mY3JlYXRlX3RpbWU9MTM4NDMyMDUyOSZub25jZT0xMDUwODQ1Mjk4JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMjk=', 1384320529562, 1386826129562);
INSERT INTO `webrtcsession` VALUES (77, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowMSBQU1QgMjAxM34wLjQ5NDExODkzfg', 1384320530015, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9Y2ZiNmVlMDY0OWQ1N2M4NGQ1YTgwZWQzZmFiMDZkODcyMjQ5OTdhODpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd01TQlFVMVFnTWpBeE0zNHdMalE1TkRFeE9Ea3pmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTMwJm5vbmNlPS01MzYyMDE1NjAmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEzMA==', 1384320530015, 1386826130015);
INSERT INTO `webrtcsession` VALUES (78, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowMSBQU1QgMjAxM34wLjUxNjEwMX4', 1384320530453, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZjQ1MGYyNmQ4MzU3NWZhNTNhZjA1NTJmNzVmMDI2OTAwNDMxYzNmODpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd01TQlFVMVFnTWpBeE0zNHdMalV4TmpFd01YNCZjcmVhdGVfdGltZT0xMzg0MzIwNTMwJm5vbmNlPTE5MDE0ODQ1NTAmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEzMA==', 1384320530453, 1386826130453);
INSERT INTO `webrtcsession` VALUES (79, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowMiBQU1QgMjAxM34wLjU5NjQ1Mjd-', 1384320530890, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NWNlYTlhZWMxOWRhZDViMTMxNjFlMWMzZDM1MDE5NTJmYTQwNTJhMjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd01pQlFVMVFnTWpBeE0zNHdMalU1TmpRMU1qZC0mY3JlYXRlX3RpbWU9MTM4NDMyMDUzMCZub25jZT0tMTgwNzU1NTMzNyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MTMw', 1384320530890, 1386826130890);
INSERT INTO `webrtcsession` VALUES (80, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowMiBQU1QgMjAxM34wLjQ0MzM0OTE4fg', 1384320531437, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9N2I1MzdhZWU1MDYzMmY0NGZjNWViOTRiNmE0MmJiZGMzYjgyZWRiZTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd01pQlFVMVFnTWpBeE0zNHdMalEwTXpNME9URTRmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTMxJm5vbmNlPS01MTUyMjc1NDkmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEzMQ==', 1384320531437, 1386826131437);
INSERT INTO `webrtcsession` VALUES (81, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowMyBQU1QgMjAxM34wLjU4NjkyMjk0fg', 1384320532109, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZWMwYWEzZjg3ZWNjMzZhYzgxNjMyYTNiZDA5NTcxMGM4ODExZDFkNjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd015QlFVMVFnTWpBeE0zNHdMalU0TmpreU1qazBmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTMyJm5vbmNlPTU5ODMzODY1OSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MTMy', 1384320532109, 1386826132109);
INSERT INTO `webrtcsession` VALUES (82, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowNCBQU1QgMjAxM34wLjU3Mjg5NjU0fg', 1384320533031, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MTFkMTNjMTY2OTliNGU1YWY4ZWFlZjdjZmRjZDBlOTA2MzAwZmM3NzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd05DQlFVMVFnTWpBeE0zNHdMalUzTWpnNU5qVTBmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTMzJm5vbmNlPTE0OTk1ODI1MjImcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjEzMw==', 1384320533031, 1386826133031);
INSERT INTO `webrtcsession` VALUES (83, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowNSBQU1QgMjAxM34wLjA5MTcwNzc2Nn4', 1384320533750, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MmU5MjQ4OTVmNzNjZGVmMjU0NzZkMWFiM2I0ZjY1NWM2NTYwMjk5ZjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd05TQlFVMVFnTWpBeE0zNHdMakE1TVRjd056YzJObjQmY3JlYXRlX3RpbWU9MTM4NDMyMDUzMyZub25jZT0tMTczMDE1NzkxOCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MTMz', 1384320533750, 1386826133750);
INSERT INTO `webrtcsession` VALUES (84, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowNSBQU1QgMjAxM34wLjI5ODYyOTgyfg', 1384320534343, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NjQ0YjMwNmViYWQxYjM5YWQ1YTMzNDY0MmJhMzA0ZTU2MGE1MDRjNjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd05TQlFVMVFnTWpBeE0zNHdMakk1T0RZeU9UZ3lmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTM0Jm5vbmNlPS0xNzkzODk5NTk3JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMzQ=', 1384320534343, 1386826134343);
INSERT INTO `webrtcsession` VALUES (85, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowNiBQU1QgMjAxM34wLjA5NzM4ODM5fg', 1384320535234, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MDU3ODc4MGFhY2U4Y2MxMTQ3YjlmZjM1NzNlZDRiMTM2Y2NmYjkwMDpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd05pQlFVMVFnTWpBeE0zNHdMakE1TnpNNE9ETTVmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTM1Jm5vbmNlPS0xNzc3MTUwNjE5JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMzU=', 1384320535234, 1386826135234);
INSERT INTO `webrtcsession` VALUES (86, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowNyBQU1QgMjAxM34wLjQ0NjUwMzA0fg', 1384320535968, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZmRkZjM4NzQ1NWE5NWM0NGJkZjU2NzVlNTFjZDIyOGIyM2RhYTI2ZDpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd055QlFVMVFnTWpBeE0zNHdMalEwTmpVd016QTBmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTM1Jm5vbmNlPTY0NDEyMzAyNCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MTM1', 1384320535968, 1386826135968);
INSERT INTO `webrtcsession` VALUES (87, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowNyBQU1QgMjAxM34wLjUxODM2MzZ-', 1384320537031, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NGU4ODgyOTQ2MmI1ZDE1ODc0ZWE0ZDU2YmEwYzQyMWVmY2E3MjhhMzpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd055QlFVMVFnTWpBeE0zNHdMalV4T0RNMk16Wi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUzNyZub25jZT0tMTQxNTUzNDQ4NiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MTM3', 1384320537031, 1386826137031);
INSERT INTO `webrtcsession` VALUES (88, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowOSBQU1QgMjAxM34wLjYxMzMzOTh-', 1384320537859, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YzQyYjZmYjllY2UyMTYwZmFiODdiODg5MWM5YmEwNTQ2MzAwNzk1YzpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd09TQlFVMVFnTWpBeE0zNHdMall4TXpNek9UaC0mY3JlYXRlX3RpbWU9MTM4NDMyMDUzNyZub25jZT0tNjcyOTAwODkwJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMzc=', 1384320537859, 1386826137859);
INSERT INTO `webrtcsession` VALUES (89, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOTowOSBQU1QgMjAxM34wLjE5MDA0NTEyfg', 1384320538562, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ODgwMTMwNjA4YWUwZDIwOWM5M2FiYWVhMzhhMmM5MDJjYzMyOTkxMjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1Rvd09TQlFVMVFnTWpBeE0zNHdMakU1TURBME5URXlmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTM4Jm5vbmNlPS0xMDcwMDg0OTQ3JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMzg=', 1384320538562, 1386826138562);
INSERT INTO `webrtcsession` VALUES (90, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxMCBQU1QgMjAxM34wLjA4MzU1MjA2fg', 1384320539218, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MTZmOTcwMGVhNzFkMGFmNzY5N2Y3NjA3N2I2YjhmOTI0ZGRjMTRhOTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE1DQlFVMVFnTWpBeE0zNHdMakE0TXpVMU1qQTJmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTM5Jm5vbmNlPS0xODkyNjAwNDUzJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMzk=', 1384320539218, 1386826139218);
INSERT INTO `webrtcsession` VALUES (91, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxMSBQU1QgMjAxM34wLjg0NDI3OTR-', 1384320539937, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZTBlNDBhNDM3NjNiZGZhMjljNDg0OTVkNzFjMDYwMGI0NDM1YmVjMDpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE1TQlFVMVFnTWpBeE0zNHdMamcwTkRJM09UUi0mY3JlYXRlX3RpbWU9MTM4NDMyMDUzOSZub25jZT0tODY0OTIzOTY2JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxMzk=', 1384320539937, 1386826139937);
INSERT INTO `webrtcsession` VALUES (92, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxMSBQU1QgMjAxM34wLjY1NDI1MDd-', 1384320540593, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NmZjMzg5MWEwYTIxZjY1MTFmZjFlZWFkYWM4M2MyOTNhOTE3MWJjOTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE1TQlFVMVFnTWpBeE0zNHdMalkxTkRJMU1EZC0mY3JlYXRlX3RpbWU9MTM4NDMyMDU0MCZub25jZT0tMzc1MDM1MjA2JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxNDA=', 1384320540593, 1386826140593);
INSERT INTO `webrtcsession` VALUES (93, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxMiBQU1QgMjAxM34wLjUzOTUzNzQzfg', 1384320541078, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9YTQzODQwMzdhODRiYWM3MDBiYTMwYzk5ODI1YzZjNmRlYWZkMTljMjpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE1pQlFVMVFnTWpBeE0zNHdMalV6T1RVek56UXpmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTQxJm5vbmNlPTExOTEyNzUxMzUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjE0MQ==', 1384320541078, 1386826141078);
INSERT INTO `webrtcsession` VALUES (94, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxMyBQU1QgMjAxM34wLjAzMjA2NTg3fg', 1384320541578, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ZDVlOTE3NWVjMzFkNjgwMGY1M2M2ZjFhY2VlYmQyMmRiMWFlNGY3ZDpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE15QlFVMVFnTWpBeE0zNHdMakF6TWpBMk5UZzNmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTQxJm5vbmNlPTIxMjk3MTgzJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxNDE=', 1384320541578, 1386826141578);
INSERT INTO `webrtcsession` VALUES (95, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxMyBQU1QgMjAxM34wLjkyODI0MDV-', 1384320542078, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9M2NmNjM1ZTRlYzlhNTQ3YjI1ZjEzYWVhZTUxNDVkMmI4MGMzYzhlZjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE15QlFVMVFnTWpBeE0zNHdMamt5T0RJME1EVi0mY3JlYXRlX3RpbWU9MTM4NDMyMDU0MiZub25jZT0tMTQ1Njg3MDgxNyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xMzg2ODI2MTQy', 1384320542078, 1386826142078);
INSERT INTO `webrtcsession` VALUES (96, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxNyBQU1QgMjAxM34wLjQ4MjcyODE4fg', 1384320545531, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9MDQzNmMxYjMzOTQwZDcwYzJjNWRmZTNhYjJhZjBjNTk0NGUyMDEzZjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE55QlFVMVFnTWpBeE0zNHdMalE0TWpjeU9ERTRmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTQ1Jm5vbmNlPS05MzE0ODIzNzcmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjE0NQ==', 1384320545531, 1386826145531);
INSERT INTO `webrtcsession` VALUES (97, '2_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxNyBQU1QgMjAxM34wLjEzMjAxMDU4fg', 1384320546109, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9OTU3NDc4MGM0N2I2YzZiZWRlMTQ1ZDQ1NTkyY2YxMmMxZjFhODk0OTpzZXNzaW9uX2lkPTJfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE55QlFVMVFnTWpBeE0zNHdMakV6TWpBeE1EVTRmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTQ2Jm5vbmNlPS0xOTA0ODQyNDU5JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxNDY=', 1384320546109, 1386826146109);
INSERT INTO `webrtcsession` VALUES (98, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxOCBQU1QgMjAxM34wLjg5Mzk4NTl-', 1384320546828, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9NzJkYTg4ODgyNmE0MmRkYTQ5Y2ZlYzM0Mjc4ODQ4MzVjNzgyN2U0OTpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE9DQlFVMVFnTWpBeE0zNHdMamc1TXprNE5UbC0mY3JlYXRlX3RpbWU9MTM4NDMyMDU0NiZub25jZT0xMjQwODMwMjQmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjE0Ng==', 1384320546828, 1386826146828);
INSERT INTO `webrtcsession` VALUES (99, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxOCBQU1QgMjAxM34wLjM1Njk3MTMyfg', 1384320547375, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9ODg4NzQ1M2IyOWRiZDlmNWU3MDUyMDQ2NWU4YmExMGNjOWYyZDg3NDpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE9DQlFVMVFnTWpBeE0zNHdMak0xTmprM01UTXlmZyZjcmVhdGVfdGltZT0xMzg0MzIwNTQ3Jm5vbmNlPTEyMTE3OTg0MTYmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTM4NjgyNjE0Nw==', 1384320547375, 1386826147375);
INSERT INTO `webrtcsession` VALUES (100, '1_MX4zNDU1NjgwMn5-VHVlIE5vdiAxMiAyMToyOToxOSBQU1QgMjAxM34wLjkwNDQwOTF-', 1384320547859, 'T1==cGFydG5lcl9pZD0zNDU1NjgwMiZzaWc9OWIzYjQzZWY0NTRiY2Y2N2FlZmNjY2JjNjBmNjQxNWU5NTJlZjhiMjpzZXNzaW9uX2lkPTFfTVg0ek5EVTFOamd3TW41LVZIVmxJRTV2ZGlBeE1pQXlNVG95T1RveE9TQlFVMVFnTWpBeE0zNHdMamt3TkRRd09URi0mY3JlYXRlX3RpbWU9MTM4NDMyMDU0NyZub25jZT0tNTY2MDQwNzY2JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTEzODY4MjYxNDc=', 1384320547859, 1386826147859);

