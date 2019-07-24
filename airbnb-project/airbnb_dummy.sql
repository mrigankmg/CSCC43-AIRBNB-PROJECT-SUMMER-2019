-- MySQL dump 10.13  Distrib 5.7.24, for Linux (x86_64)
--
-- Host: localhost    Database: airbnb
-- ------------------------------------------------------
-- Server version	5.7.24-0ubuntu0.18.04.1

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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `dob` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `occupation` varchar(255) DEFAULT NULL,
  `sin` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `cc` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('a@b.c','a','b','09/10/1996','28 Golden Meadow Dr.','Student','123456789','pass','12334553');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

Drop table if exists `location`;

create table `location` (
`listing_num` varchar(700) Not Null,
`suite_num` varchar(256),
`house_num` varchar(256) Not Null,
`street_name` varchar(256) Not Null,
`postal_code` varchar(256) Not Null,
`city` varchar(256) Not Null,
`country` varchar(256) Not Null,
`latitude` varchar(30) Not Null,
`longitude` varchar(30) Not Null,
`type` varchar(256) Not Null,
primary key(`listing_num`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `availability`;

create table `availability` (
`listing_num` varchar(700) NOT NUll ,
`start_date` varchar(10) Not null,
`end_date` varchar(10) Not Null,
`cost_per_day` varchar(500) Not Null,
primary key(`listing_num`, `start_date`, `end_date`)

)ENGINE=InnoDB DEFAULT CHARSET=latin1;

Drop table if exists `booking`;

create table `booking` (
`listing_num` varchar(700) NOT NUll ,
`start_date` varchar(10) Not null,
`end_date` varchar(10) Not Null,
`cost_per_day` decimal(30,2) Not Null,
`renter_sin` varchar(255) Not Null,
`renter_comment_on_listing` varchar(3000),
`renter_comment_on_host` varchar(3000),
`host_comment_on_renter` varchar(3000),
 `listing_rating` int(1),
 `host_rating` int(1),
 `renter_rating` int(1),

primary key(`listing_num`,`start_date`, `end_date`)
);

DROP TABLE IF EXISTS `host`;

create table `host` (
`listing_num` varchar(700) NOT NUll ,
`host_sin` varchar(255) Not null,
primary key(`listing_num`)
);

DROP TABLE IF EXISTS `amenities`;

create table `amenities` (
`listing_num` varchar(700) not null,
`toilet_paper_included` varchar(1) not null,
`wifi_included` varchar(1) not null,
`towels_included` varchar(1) not null,
`iron_included` varchar(1) not null,
`pool_included` varchar(1) not null,
`ac_included` varchar(1) not null,
`fireplace_included` varchar(1) not null,
primary key(`listing_num`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
