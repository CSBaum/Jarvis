CREATE DATABASE  IF NOT EXISTS `jarvis` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `jarvis`;
-- MySQL dump 10.13  Distrib 5.1.40, for Win32 (ia32)
--
-- Host: localhost    Database: jarvis
-- ------------------------------------------------------
-- Server version	5.1.51-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `commands`
--

DROP TABLE IF EXISTS `commands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commands` (
  `UID` int(11) NOT NULL AUTO_INCREMENT,
  `command` varchar(128) DEFAULT NULL,
  `cmdAudio` blob,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`UID`),
  UNIQUE KEY `Command_UNIQUE` (`command`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commands`
--

LOCK TABLES `commands` WRITE;
/*!40000 ALTER TABLE `commands` DISABLE KEYS */;
/*!40000 ALTER TABLE `commands` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `locations`
--

DROP TABLE IF EXISTS `locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `locations` (
  `UID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `description` varchar(128) DEFAULT NULL,
  `gps` varchar(45) DEFAULT NULL,
  `altitude` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`UID`),
  UNIQUE KEY `UID_UNIQUE` (`UID`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locations`
--

LOCK TABLES `locations` WRITE;
/*!40000 ALTER TABLE `locations` DISABLE KEYS */;
INSERT INTO `locations` VALUES (1,'1st floor',NULL,NULL,NULL);
/*!40000 ALTER TABLE `locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `robots`
--

DROP TABLE IF EXISTS `robots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `robots` (
  `UID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `hasAudio` binary(1) DEFAULT NULL,
  `hasLaser` binary(1) DEFAULT NULL,
  `hasMic` binary(1) DEFAULT NULL,
  `hasSonar` binary(1) DEFAULT NULL,
  `hasVideo` binary(1) DEFAULT NULL,
  `cfgFile` text,
  `sensors` varchar(255) DEFAULT NULL COMMENT 'comma delimited list of associated sensor types',
  `sensors-front-center` varchar(255) DEFAULT NULL,
  `sensors-front-left` varchar(255) DEFAULT NULL,
  `sensors-front-right` varchar(255) DEFAULT NULL,
  `sensors-left-front` varchar(255) DEFAULT NULL,
  `sensors-left-middle` varchar(255) DEFAULT NULL,
  `sensors-left-back` varchar(255) DEFAULT NULL,
  `sensors-right-front` varchar(255) DEFAULT NULL,
  `sensors-right-middle` varchar(255) DEFAULT NULL,
  `sensors-right-back` varchar(255) DEFAULT NULL,
  `sensors-back-center` varchar(255) DEFAULT NULL,
  `sensors-back-left` varchar(255) DEFAULT NULL,
  `sensors-back-right` varchar(255) DEFAULT NULL,
  `tireCount` int(11) DEFAULT NULL,
  `tireType` int(11) DEFAULT NULL,
  `motorType` int(11) DEFAULT NULL,
  `passphrase` varchar(45) DEFAULT NULL,
  `commKey` char(16) DEFAULT NULL,
  PRIMARY KEY (`UID`),
  UNIQUE KEY `UID_UNIQUE` (`UID`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `robots_fk_tireType` (`tireType`),
  KEY `robots_fk_motorType` (`motorType`),
  CONSTRAINT `robots_fk_motorType` FOREIGN KEY (`motorType`) REFERENCES `robot_motors` (`UID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `robots_fk_tireType` FOREIGN KEY (`tireType`) REFERENCES `robot_tires` (`UID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `robots`
--

LOCK TABLES `robots` WRITE;
/*!40000 ALTER TABLE `robots` DISABLE KEYS */;
INSERT INTO `robots` VALUES (1,'roomba1','0','0','0','0','0',NULL,'1,2,3','2,3','2','2',NULL,NULL,NULL,NULL,NULL,NULL,'1,2,3',NULL,NULL,2,1,1,'yeah I can fly 2','5]¨¼KÓè“AÊ$²ùé%');
/*!40000 ALTER TABLE `robots` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `agents`
--

DROP TABLE IF EXISTS `agents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agents` (
  `UID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `class` varchar(128) DEFAULT NULL,
  `type` varchar(24) DEFAULT NULL COMMENT 'human readable type of agent [either robot or network]',
  PRIMARY KEY (`UID`),
  UNIQUE KEY `UID_UNIQUE` (`UID`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agents`
--

LOCK TABLES `agents` WRITE;
/*!40000 ALTER TABLE `agents` DISABLE KEYS */;
INSERT INTO `agents` VALUES (1,'JAgent-laptop','net.stallbaum.jarvisagent.JarvisAgent','robot'),(2,'JAgent-desktop','net.stallbaum.jarvisagent.JarvisAgent','robot'),(3,'JAgent-roomba','net.stallbaum.jarvisagent.JarvisAgent','robot');
/*!40000 ALTER TABLE `agents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archives`
--

DROP TABLE IF EXISTS `archives`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `archives` (
  `iUID` int(11) NOT NULL AUTO_INCREMENT,
  `agent` varchar(45) NOT NULL,
  `sensorType` int(11) NOT NULL,
  `sensorId` int(11) NOT NULL,
  `file` text,
  `timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`iUID`),
  UNIQUE KEY `iUID_UNIQUE` (`iUID`),
  KEY `archive_Agent` (`agent`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archives`
--

LOCK TABLES `archives` WRITE;
/*!40000 ALTER TABLE `archives` DISABLE KEYS */;
/*!40000 ALTER TABLE `archives` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensors`
--

DROP TABLE IF EXISTS `sensors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sensors` (
  `UID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(90) NOT NULL,
  `type` varchar(180) NOT NULL,
  `description` text,
  `refreshRate` float DEFAULT NULL,
  `refreshRateScale` varchar(16) DEFAULT NULL,
  `fieldOfView` float DEFAULT NULL,
  `FOVScale` varchar(16) DEFAULT NULL,
  `resolution` float DEFAULT NULL,
  `resolutionScale` varchar(15) DEFAULT NULL,
  `minRange` float DEFAULT NULL,
  `maxRange` float DEFAULT NULL,
  `rangeScale` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`UID`),
  UNIQUE KEY `UID_UNIQUE` (`UID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COMMENT='Table used to hold sensor descriptions';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensors`
--

LOCK TABLES `sensors` WRITE;
/*!40000 ALTER TABLE `sensors` DISABLE KEYS */;
INSERT INTO `sensors` VALUES (1,'Temp','Thermometer',NULL,NULL,NULL,NULL,NULL,0.067,'centigrade',-40,120,'centigrade'),(2,'LongRange-IR','Infrared proximity',NULL,50,'ms',NULL,NULL,NULL,NULL,15,150,'cm'),(3,'Range Finder - narror beam','Ultrsonic','Maxbotix LV-EZ1',20,'Hz',NULL,NULL,NULL,NULL,0,255,'inch');
/*!40000 ALTER TABLE `sensors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `robot_tires`
--

DROP TABLE IF EXISTS `robot_tires`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `robot_tires` (
  `UID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `radius` float DEFAULT NULL,
  PRIMARY KEY (`UID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COMMENT='holds the info for the various tire types used by the robots';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `robot_tires`
--

LOCK TABLES `robot_tires` WRITE;
/*!40000 ALTER TABLE `robot_tires` DISABLE KEYS */;
INSERT INTO `robot_tires` VALUES (1,'basic',24);
/*!40000 ALTER TABLE `robot_tires` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `robot_motors`
--

DROP TABLE IF EXISTS `robot_motors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `robot_motors` (
  `UID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `gear_ratio` varchar(45) DEFAULT NULL,
  `rpm` int(11) DEFAULT NULL,
  `torque` float DEFAULT NULL,
  PRIMARY KEY (`UID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `robot_motors`
--

LOCK TABLES `robot_motors` WRITE;
/*!40000 ALTER TABLE `robot_motors` DISABLE KEYS */;
INSERT INTO `robot_motors` VALUES (1,'Micro Metal Gearmotor','100:1',120,10);
/*!40000 ALTER TABLE `robot_motors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `containers`
--

DROP TABLE IF EXISTS `containers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `containers` (
  `UID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `MAC` varchar(45) DEFAULT NULL,
  `host` varchar(45) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `startupFlags` varchar(255) DEFAULT NULL,
  `agent_id` int(11) NOT NULL,
  `robot_id` int(11) NOT NULL,
  `location_id` int(11) NOT NULL,
  `enabled` char(1) DEFAULT '0',
  PRIMARY KEY (`UID`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `containers_fk_agent` (`agent_id`),
  KEY `containers_fk_robot` (`robot_id`),
  KEY `containers_fk_location` (`location_id`),
  CONSTRAINT `containers_fk_agent` FOREIGN KEY (`agent_id`) REFERENCES `agents` (`UID`) ON DELETE CASCADE,
  CONSTRAINT `containers_fk_location` FOREIGN KEY (`location_id`) REFERENCES `locations` (`UID`) ON DELETE CASCADE,
  CONSTRAINT `containers_fk_robot` FOREIGN KEY (`robot_id`) REFERENCES `robots` (`UID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `containers`
--

LOCK TABLES `containers` WRITE;
/*!40000 ALTER TABLE `containers` DISABLE KEYS */;
INSERT INTO `containers` VALUES (1,'roomba','00-21-70-D9-AE-DE','sstallbaumw7',6665,'-v',1,1,1,'1');
/*!40000 ALTER TABLE `containers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-10-28 12:50:25
