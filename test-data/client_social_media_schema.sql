/*
SQLyog Ultimate v8.55 
MySQL - 5.7.44-log : Database - gcasys_dbf
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `client_social_media` */

DROP TABLE IF EXISTS `client_social_media`;

CREATE TABLE `client_social_media` (
  `sSocialID` CHAR(12) NOT NULL,
  `sClientID` CHAR(12) DEFAULT NULL,
  `sAccountx` CHAR(64) DEFAULT NULL,
  `sRemarksx` CHAR(64) DEFAULT NULL,
  `cSocialTp` CHAR(1) DEFAULT NULL,
  `cRecdStat` CHAR(1) DEFAULT '1',
  `dModified` DATETIME DEFAULT NULL,
  `dTimeStmp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sSocialID`)
) ENGINE=INNODB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
