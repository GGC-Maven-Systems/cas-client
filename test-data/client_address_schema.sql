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
/*Table structure for table `client_address` */

DROP TABLE IF EXISTS `client_address`;

CREATE TABLE `client_address` (
  `sAddrssID` char(12) NOT NULL,
  `sClientID` char(12) DEFAULT NULL,
  `sHouseNox` varchar(5) DEFAULT NULL,
  `sAddressx` varchar(128) DEFAULT NULL,
  `sBrgyIDxx` varchar(7) DEFAULT NULL,
  `sTownIDxx` varchar(5) DEFAULT NULL,
  `nLatitude` decimal(15,11) DEFAULT NULL,
  `nLongitud` decimal(15,11) DEFAULT NULL,
  `cPrimaryx` char(1) DEFAULT '0',
  `cOfficexx` char(1) DEFAULT '0',
  `cProvince` char(1) DEFAULT '0',
  `cBillingx` char(1) DEFAULT '0',
  `cShipping` char(1) DEFAULT '0',
  `cCurrentx` char(1) DEFAULT '0',
  `cLTMSAddx` char(1) DEFAULT '0',
  `sSourceCd` varchar(4) DEFAULT NULL,
  `sReferNox` varchar(12) DEFAULT NULL,
  `cRecdStat` char(1) DEFAULT '1',
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sAddrssID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
