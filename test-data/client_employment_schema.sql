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
/*Table structure for table `client_employment` */

DROP TABLE IF EXISTS `client_employment`;

CREATE TABLE `client_employment` (
  `sClientID` char(12) NOT NULL,
  `nAddrYrsx` decimal(4,1) DEFAULT '0.0',
  `sIncomSrc` varchar(64) DEFAULT NULL,
  `nDependnt` smallint(6) DEFAULT '0',
  `sEmployNm` varchar(128) DEFAULT NULL,
  `sBusAddrs` varchar(256) DEFAULT NULL,
  `sOffEmail` varchar(128) DEFAULT NULL,
  `sPosition` varchar(64) DEFAULT NULL,
  `nWorkYrsx` decimal(4,1) DEFAULT '0.0',
  `nGrossInc` decimal(15,2) DEFAULT '0.00',
  PRIMARY KEY (`sClientID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
