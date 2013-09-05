/*
Navicat MySQL Data Transfer

Source Server         : renhai
Source Server Version : 50610
Source Host           : localhost:3306
Source Database       : renhai

Target Server Type    : MYSQL
Target Server Version : 50610
File Encoding         : 65001

Date: 2013-09-05 06:22:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `devicecard`
-- ----------------------------
DROP TABLE IF EXISTS `devicecard`;
CREATE TABLE `devicecard` (
  `deviceId` int(11) NOT NULL AUTO_INCREMENT,
  `deviceSn` varchar(256) NOT NULL,
  `registerTime` bigint(20) NOT NULL,
  `serviceStatus` enum('Normal','Forbidden') NOT NULL,
  `forbiddenExpiredDate` bigint(20) DEFAULT NULL,
  `profileId` int(11) NOT NULL,
  `deviceModel` varchar(256) NOT NULL,
  `osVersion` varchar(128) NOT NULL,
  `appVersion` varchar(128) NOT NULL,
  `location` varchar(256) DEFAULT NULL,
  `isJailed` enum('True','False') DEFAULT NULL,
  PRIMARY KEY (`deviceId`),
  KEY `index_deviceSn` (`deviceSn`(255)),
  KEY `fk_DeviceCard_profileId_idx` (`profileId`),
  CONSTRAINT `fk_DeviceCard_profileId` FOREIGN KEY (`profileId`) REFERENCES `profile` (`profileId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of DeviceCard
-- ----------------------------

-- ----------------------------
-- Table structure for `deviceprofilemap`
-- ----------------------------
DROP TABLE IF EXISTS `deviceprofilemap`;
CREATE TABLE `deviceprofilemap` (
  `deviceProfileMapId` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` int(11) NOT NULL,
  `profileId` int(11) NOT NULL,
  PRIMARY KEY (`deviceProfileMapId`),
  KEY `deviceId_idx` (`deviceId`),
  KEY `profileId_idx` (`profileId`),
  CONSTRAINT `fk_DeviceProfileMap_deviceId` FOREIGN KEY (`deviceId`) REFERENCES `devicecard` (`deviceId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_DeviceProfileMap_profileId` FOREIGN KEY (`profileId`) REFERENCES `profile` (`profileId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of DeviceProfileMap
-- ----------------------------

-- ----------------------------
-- Table structure for `globalimpresslabel`
-- ----------------------------
DROP TABLE IF EXISTS `globalimpresslabel`;
CREATE TABLE `globalimpresslabel` (
  `globalImpressLabelId` int(11) NOT NULL AUTO_INCREMENT,
  `impressLabel` varchar(256) NOT NULL,
  `globalAssessCount` int(11) NOT NULL,
  PRIMARY KEY (`globalImpressLabelId`),
  KEY `index_impressLabel` (`impressLabel`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of GlobalImpressLabel
-- ----------------------------

-- ----------------------------
-- Table structure for `globalinterestlabel`
-- ----------------------------
DROP TABLE IF EXISTS `globalinterestlabel`;
CREATE TABLE `globalinterestlabel` (
  `globalInterestLabelId` int(11) NOT NULL AUTO_INCREMENT,
  `interestLabel` varchar(256) NOT NULL,
  `globalMatchCount` int(11) NOT NULL,
  PRIMARY KEY (`globalInterestLabelId`),
  KEY `index_interestLabel` (`interestLabel`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of GlobalInterestLabel
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
-- Records of HotInterestLabelStatistics
-- ----------------------------

-- ----------------------------
-- Table structure for `impresscard`
-- ----------------------------
DROP TABLE IF EXISTS `impresscard`;
CREATE TABLE `impresscard` (
  `impressCardId` int(11) NOT NULL AUTO_INCREMENT,
  `chatTotalCount` int(11) NOT NULL,
  `chatTotalDuration` int(11) NOT NULL,
  `chatLossCount` int(11) NOT NULL,
  PRIMARY KEY (`impressCardId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ImpressCard
-- ----------------------------

-- ----------------------------
-- Table structure for `impresslabelcollection`
-- ----------------------------
DROP TABLE IF EXISTS `impresslabelcollection`;
CREATE TABLE `impresslabelcollection` (
  `impressLabelMaplId` int(11) NOT NULL,
  `impressCardId` int(11) NOT NULL,
  `globalImpressLabelId` int(11) NOT NULL,
  `count` int(11) NOT NULL,
  `updateTime` bigint(20) NOT NULL,
  `assessCount` int(11) NOT NULL,
  PRIMARY KEY (`impressLabelMaplId`),
  KEY `fk_impressCardId_idx` (`impressCardId`),
  KEY `fk_globalImpressLabelId_idx` (`globalImpressLabelId`),
  CONSTRAINT `fk_globalImpressLabelId` FOREIGN KEY (`globalImpressLabelId`) REFERENCES `globalimpresslabel` (`globalImpressLabelId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_impressCardId` FOREIGN KEY (`impressCardId`) REFERENCES `impresscard` (`impressCardId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ImpressLabelCollection
-- ----------------------------

-- ----------------------------
-- Table structure for `interestcard`
-- ----------------------------
DROP TABLE IF EXISTS `interestcard`;
CREATE TABLE `interestcard` (
  `interestCardId` int(11) NOT NULL AUTO_INCREMENT,
  `createTime` bigint(20) NOT NULL,
  PRIMARY KEY (`interestCardId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of InterestCard
-- ----------------------------

-- ----------------------------
-- Table structure for `interestlabelcollection`
-- ----------------------------
DROP TABLE IF EXISTS `interestlabelcollection`;
CREATE TABLE `interestlabelcollection` (
  `interestLabelMaplId` int(11) NOT NULL AUTO_INCREMENT,
  `interestCardId` int(11) NOT NULL,
  `globalInterestLabelId` int(11) NOT NULL,
  `order` int(11) NOT NULL,
  `matchCount` int(11) NOT NULL,
  `validFlag` enum('Valid','Invalid') NOT NULL,
  PRIMARY KEY (`interestLabelMaplId`),
  KEY `interestCardId_idx` (`interestCardId`),
  KEY `fk_globalInterestLabelId_idx` (`globalInterestLabelId`),
  CONSTRAINT `fk_globalInterestLabelId` FOREIGN KEY (`globalInterestLabelId`) REFERENCES `globalinterestlabel` (`globalInterestLabelId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_interestCardId` FOREIGN KEY (`interestCardId`) REFERENCES `interestcard` (`interestCardId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of InterestLabelCollection
-- ----------------------------

-- ----------------------------
-- Table structure for `moduledefinition`
-- ----------------------------
DROP TABLE IF EXISTS `moduledefinition`;
CREATE TABLE `moduledefinition` (
  `moduleDefinitionId` int(11) NOT NULL AUTO_INCREMENT,
  `moduleId` int(11) NOT NULL,
  `description` varchar(256) NOT NULL,
  PRIMARY KEY (`moduleDefinitionId`),
  KEY `index_moduleId` (`moduleId`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ModuleDefinition
-- ----------------------------
INSERT INTO `moduledefinition` VALUES ('1', '1', 'business');
INSERT INTO `moduledefinition` VALUES ('2', '2', 'db');
INSERT INTO `moduledefinition` VALUES ('3', '3', 'json');
INSERT INTO `moduledefinition` VALUES ('4', '4', 'log');
INSERT INTO `moduledefinition` VALUES ('5', '5', 'servlets');
INSERT INTO `moduledefinition` VALUES ('6', '6', 'util');
INSERT INTO `moduledefinition` VALUES ('7', '7', 'websocket');

-- ----------------------------
-- Table structure for `operationcodedefinition`
-- ----------------------------
DROP TABLE IF EXISTS `operationcodedefinition`;
CREATE TABLE `operationcodedefinition` (
  `operationCodeDefinitionId` int(11) NOT NULL AUTO_INCREMENT,
  `operationCode` int(11) NOT NULL,
  `operationType` enum('All','System','User') NOT NULL,
  `description` varchar(256) NOT NULL,
  PRIMARY KEY (`operationCodeDefinitionId`),
  KEY `index_operationCode` (`operationCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of OperationCodeDefinition
-- ----------------------------

-- ----------------------------
-- Table structure for `profile`
-- ----------------------------
DROP TABLE IF EXISTS `profile`;
CREATE TABLE `profile` (
  `profileId` int(11) NOT NULL AUTO_INCREMENT,
  `interestCardId` int(11) NOT NULL,
  `impressCardId` int(11) NOT NULL,
  `lastActivityTime` bigint(20) NOT NULL,
  `createTime` bigint(20) NOT NULL,
  PRIMARY KEY (`profileId`),
  KEY `fk_Profile_intCardId_idx` (`interestCardId`),
  KEY `fk_Profile_impCardId_idx` (`impressCardId`),
  CONSTRAINT `fk_Profile_intCardId` FOREIGN KEY (`interestCardId`) REFERENCES `interestcard` (`interestCardId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Profile_impCardId` FOREIGN KEY (`impressCardId`) REFERENCES `impresscard` (`impressCardId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of Profile
-- ----------------------------

-- ----------------------------
-- Table structure for `profileoperationlog`
-- ----------------------------
DROP TABLE IF EXISTS `profileoperationlog`;
CREATE TABLE `profileoperationlog` (
  `profileOperationLogId` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` int(11) NOT NULL,
  `profileId` int(11) NOT NULL,
  `logTime` int(11) NOT NULL,
  `operationCode` int(11) NOT NULL,
  `logInfo` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`profileOperationLogId`),
  KEY `fk_Pro_deviceId_idx` (`deviceId`),
  KEY `fk_Pro_profileId_idx` (`profileId`),
  KEY `fk_Pro_operCode_idx` (`operationCode`),
  CONSTRAINT `fk_Pro_operCode` FOREIGN KEY (`operationCode`) REFERENCES `operationcodedefinition` (`operationCode`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Pro_deviceId` FOREIGN KEY (`deviceId`) REFERENCES `devicecard` (`deviceId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Pro_profileId` FOREIGN KEY (`profileId`) REFERENCES `profile` (`profileId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ProfileOperationLog
-- ----------------------------

-- ----------------------------
-- Table structure for `sessionprofilecollection`
-- ----------------------------
DROP TABLE IF EXISTS `sessionprofilecollection`;
CREATE TABLE `sessionprofilecollection` (
  `sessionImpressMapId` int(11) NOT NULL AUTO_INCREMENT,
  `sessionRecordId` int(11) NOT NULL,
  `profileId` int(11) NOT NULL,
  `count` int(11) NOT NULL,
  PRIMARY KEY (`sessionImpressMapId`),
  KEY `fk_sessionRecordId_idx` (`sessionRecordId`),
  KEY `fk_profileId_idx` (`profileId`),
  CONSTRAINT `fk_sessionRecordId` FOREIGN KEY (`sessionRecordId`) REFERENCES `sessionrecord` (`sessionRecordId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_profileId` FOREIGN KEY (`profileId`) REFERENCES `profile` (`profileId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SessionProfileCollection
-- ----------------------------

-- ----------------------------
-- Table structure for `sessionrecord`
-- ----------------------------
DROP TABLE IF EXISTS `sessionrecord`;
CREATE TABLE `sessionrecord` (
  `sessionRecordId` int(11) NOT NULL,
  `businessType` enum('Random','Interest') NOT NULL,
  `startTime` bigint(20) NOT NULL,
  `duration` int(11) NOT NULL,
  `endStatus` enum('ChatConfirm','VedioChat','Assess') NOT NULL,
  `endReason` enum('Reject','ConnectionLoss','NormalEnd') NOT NULL,
  PRIMARY KEY (`sessionRecordId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SessionRecord
-- ----------------------------

-- ----------------------------
-- Table structure for `statisticsitemdefinition`
-- ----------------------------
DROP TABLE IF EXISTS `statisticsitemdefinition`;
CREATE TABLE `statisticsitemdefinition` (
  `statisticsItemDefinitionId` int(11) NOT NULL AUTO_INCREMENT,
  `statisticsItem` int(11) NOT NULL,
  `description` varchar(256) NOT NULL,
  PRIMARY KEY (`statisticsItemDefinitionId`),
  KEY `index_statisticsItem` (`statisticsItem`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of StatisticsItemDefinition
-- ----------------------------
INSERT INTO `statisticsitemdefinition` VALUES ('1', '0', '在线设备数');
INSERT INTO `statisticsitemdefinition` VALUES ('2', '0', '随机业务设备数');
INSERT INTO `statisticsitemdefinition` VALUES ('3', '0', '兴趣业务设备数');
INSERT INTO `statisticsitemdefinition` VALUES ('4', '0', '处于聊天状态设备数');
INSERT INTO `statisticsitemdefinition` VALUES ('5', '0', '随机聊天设备数');
INSERT INTO `statisticsitemdefinition` VALUES ('6', '0', '兴趣聊天设备数');
INSERT INTO `statisticsitemdefinition` VALUES ('7', '0', '热门标签');

-- ----------------------------
-- Table structure for `systemoperationlog`
-- ----------------------------
DROP TABLE IF EXISTS `systemoperationlog`;
CREATE TABLE `systemoperationlog` (
  `systemOperationLogId` int(11) NOT NULL AUTO_INCREMENT,
  `moduleId` int(11) NOT NULL,
  `logTime` bigint(20) NOT NULL,
  `operationCode` int(11) NOT NULL,
  `logInfo` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`systemOperationLogId`),
  KEY `fk_SysOpLog_moduleId_idx` (`moduleId`),
  KEY `fk_SysOpLog_operCode_idx` (`operationCode`),
  CONSTRAINT `fk_SysOpLog_operCode` FOREIGN KEY (`operationCode`) REFERENCES `operationcodedefinition` (`operationCode`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_SysOpLog_moduleId` FOREIGN KEY (`moduleId`) REFERENCES `moduledefinition` (`moduleId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SystemOperationLog
-- ----------------------------

-- ----------------------------
-- Table structure for `systemstatistics`
-- ----------------------------
DROP TABLE IF EXISTS `systemstatistics`;
CREATE TABLE `systemstatistics` (
  `systemStatisticsId` int(11) NOT NULL AUTO_INCREMENT,
  `saveTime` int(11) NOT NULL,
  `statisticsItem` int(11) NOT NULL,
  `count` int(11) NOT NULL,
  PRIMARY KEY (`systemStatisticsId`),
  KEY `fk_SysStat_statItem_idx` (`statisticsItem`),
  CONSTRAINT `fk_SysStat_statItem` FOREIGN KEY (`statisticsItem`) REFERENCES `statisticsitemdefinition` (`statisticsItem`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SystemStatistics
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
-- Records of WebRtcSession
-- ----------------------------
