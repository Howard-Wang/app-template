
-- 该表用来进行数据库结构初始化, 如果要填充数据请放到 data-h2.sql 中去执行

DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `username` varchar(128) DEFAULT NULL,
    `password` varchar(128) DEFAULT NULL,
    `userRole` varchar(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `setting`;
CREATE TABLE `setting` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `value` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `webTask`;
CREATE TABLE `webTask` (
  `id` varchar(32) NOT NULL,
  `deviceId` varchar(128) NOT NULL DEFAULT '' COMMENT '设备id',
  `webSource` varchar(128) DEFAULT NULL COMMENT '来源',
  `environment` varchar(2048) DEFAULT NULL COMMENT '客户端自定义环境信息',
  `pageNum` int(11) NOT NULL COMMENT '日志页码',
  `content` mediumtext NOT NULL COMMENT '日志内容',
  `addTime` bigint(20) NOT NULL COMMENT '添加时间',
  `logDate` bigint(20) NOT NULL COMMENT '日志所属日期',
  `status` int(11) NOT NULL COMMENT '日志状态0未解析，1已解析',
  `customReportInfo` varchar(2048) DEFAULT NULL COMMENT '自定义上报信息',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
    `id` varchar(32) NOT NULL,
    `fileUrl` varchar(255) NOT NULL,
    `filename` varchar(255) NOT NULL,
    `filePath` varchar(255) DEFAULT NULL,
    `status` varchar(20) NOT NULL,
    `fileSize` int(128) DEFAULT NULL,
    `createdTime` datetime NOT NULL,
    PRIMARY KEY (`id`)
);
