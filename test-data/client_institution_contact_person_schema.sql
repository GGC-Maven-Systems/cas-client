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
/*Table structure for table `client_institution_contact_person` */

DROP TABLE IF EXISTS `client_institution_contact_person`;

CREATE TABLE `client_institution_contact_person` (
  `sContctID` CHAR(12) NOT NULL,
  `sClientID` CHAR(12) NOT NULL,
  `sCategrCd` VARCHAR(7) NOT NULL,
  `cCPrsonID` VARCHAR(12) DEFAULT NULL,
  `sCPerson1` VARCHAR(64) DEFAULT NULL,
  `sCPPosit1` VARCHAR(32) DEFAULT NULL,
  `sJobTitle` VARCHAR(64) DEFAULT NULL,
  `sDeprtmnt` VARCHAR(64) DEFAULT NULL,
  `sRoleIDxx` VARCHAR(8) DEFAULT NULL,
  `sMobileNo` VARCHAR(30) DEFAULT NULL,
  `sTelNoxxx` VARCHAR(30) DEFAULT NULL,
  `sFaxNoxxx` VARCHAR(30) DEFAULT NULL,
  `sEMailAdd` VARCHAR(64) DEFAULT NULL,
  `sAccount1` VARCHAR(64) DEFAULT NULL,
  `sAccount2` VARCHAR(64) DEFAULT NULL,
  `sAccount3` VARCHAR(64) DEFAULT NULL,
  `sRemarksx` VARCHAR(128) DEFAULT NULL,
  `cPayeexxx` CHAR(1) DEFAULT NULL,
  `cPrimaryx` CHAR(1) DEFAULT NULL,
  `cRecdStat` CHAR(1) DEFAULT '1',
  `sModified` VARCHAR(32) DEFAULT NULL,
  `dModified` DATETIME DEFAULT NULL,
  `dTimeStmp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sContctID`)
) ENGINE=INNODB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
