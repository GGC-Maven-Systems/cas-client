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
/*Table structure for table `client_mobile` */

DROP TABLE IF EXISTS `client_mobile`;

CREATE TABLE `client_mobile` (
  `sMobileID` char(12) NOT NULL,
  `sClientID` char(12) DEFAULT NULL,
  `sMobileNo` varchar(20) DEFAULT NULL,
  `cMobileTp` char(1) DEFAULT NULL,
  `cOwnerxxx` char(1) DEFAULT NULL,
  `cPrimaryx` char(1) DEFAULT NULL,
  `cIncdMktg` char(1) DEFAULT '1',
  `nUnreachx` tinyint(3) DEFAULT NULL,
  `dLastVeri` datetime DEFAULT NULL,
  `dInactive` datetime DEFAULT NULL,
  `nNoRetryx` tinyint(3) DEFAULT NULL,
  `cInvalidx` char(1) DEFAULT NULL,
  `cConfirmd` char(1) DEFAULT NULL,
  `dConfirmd` datetime DEFAULT NULL,
  `cSubscrbr` char(1) DEFAULT NULL,
  `dHoldMktg` datetime DEFAULT NULL,
  `dMktgMsg1` datetime DEFAULT NULL,
  `dMktgMsg2` datetime DEFAULT NULL,
  `dMktgMsg3` datetime DEFAULT NULL,
  `cNewMobil` char(1) DEFAULT '1',
  `sSourceCd` varchar(4) DEFAULT NULL,
  `sReferNox` varchar(12) DEFAULT NULL,
  `cRecdStat` char(1) DEFAULT '1',
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sMobileID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
