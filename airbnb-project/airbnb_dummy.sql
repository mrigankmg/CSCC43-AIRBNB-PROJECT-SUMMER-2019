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
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `dob` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `occupation` varchar(255) NOT NULL,
  `sin` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `cc` varchar(255),
  `num_cancellations` varchar(255) NOT NULL,
  primary key(`sin`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('a@b.c','Elon','Musk','09/10/1996','28 Golden Meadow Dr.','Founder of Tesla','123456789','pass','123456789012345','0');
INSERT INTO `user` VALUES ('b@b.c','Bill','Gates','09/10/1996','28 Golden Meadow Dr.','Multi-Billionaire','123123123','pass',NULL,'0');
INSERT INTO `user` VALUES ('c@b.c','Steph','Curry','09/10/1996','28 Golden Meadow Dr.','NBA Star','123457777','pass',NULL,'0');
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

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` VALUES ('1',NULL,'28','Golden Meadow Dr.','L6E1V9','Markham','Canada','87','176','House');
INSERT INTO `location` VALUES ('2',NULL,'12','Test Dr.','M65432','Toronto','Canada','87','176','House');
INSERT INTO `location` VALUES ('3',NULL,'29','Golden Meadow Dr.','L6E1V8','Alpes','Switzerland','87','176','House');
INSERT INTO `location` VALUES ('4','60','30','Golden Meadow Dr.','L6E1V8','Toronto','Canada','87','176','Apartment');
INSERT INTO `location` VALUES ('5',NULL,'31','Golden Meadow Dr.','L6E1V8','Brampton','Canada','87','176','House');
INSERT INTO `location` VALUES ('6',NULL,'32','Golden Meadow Dr.','L6E1V7','Toronto','Canada','25','25','Room');
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS `availability`;

create table `availability` (
`listing_num` varchar(700) NOT NUll ,
`start_date` varchar(10) Not null,
`end_date` varchar(10) Not Null,
`cost_per_day` varchar(500) Not Null,
primary key(`listing_num`, `start_date`)

)ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `availability` WRITE;
/*!40000 ALTER TABLE `availability` DISABLE KEYS */;
INSERT INTO `availability` VALUES ('1', '20/10/2019', '20/10/2020', '123');
INSERT INTO `availability` VALUES ('2', '20/11/2019', '20/05/2020', '12345');
INSERT INTO `availability` VALUES ('3', '20/11/2019', '20/05/2020', '1234');
INSERT INTO `availability` VALUES ('4', '20/10/2019', '20/10/2020', '1236');
INSERT INTO `availability` VALUES ('5', '20/11/2019', '20/05/2020', '1237');
INSERT INTO `availability` VALUES ('6', '20/10/2019', '20/10/2020', '1238');
/*!40000 ALTER TABLE `availability` ENABLE KEYS */;
UNLOCK TABLES;

Drop table if exists `booking`;

create table `booking` (
`booking_num` varchar(700) NOT NUll ,
`listing_num` varchar(700) NOT NUll ,
`start_date` varchar(10) Not null,
`end_date` varchar(10) Not Null,
`cost_per_day` decimal(30,2) Not Null,
`sin` varchar(255) Not Null,
`renter_comment_on_listing` varchar(3000),
`renter_comment_on_host` varchar(3000),
`host_comment_on_renter` varchar(3000),
`listing_rating` varchar(5),
`host_rating` varchar(5),
`renter_rating` varchar(5),
primary key(`booking_num`)
);

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
INSERT INTO `booking` VALUES ('1','1','20/10/2017','20/10/2018','125.5','123456789',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `booking` VALUES ('2','1','20/12/2017','20/09/2018','125.5','123456789',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `booking` VALUES ('3','2','20/12/2017','20/09/2018','125.5','123456789',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `booking` VALUES ('4','3','20/12/2017','20/09/2018','125.5','123123123',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `booking` VALUES ('5','4','20/12/2017','20/09/2018','125.5','123456789',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `booking` VALUES ('6','4','20/12/2017','20/09/2018','125.5','123123123',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `booking` VALUES ('7','4','20/09/2017','20/09/2018','125.5','123456789',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `booking` VALUES ('8','5','20/08/2017','20/12/2018','125.5','123457777',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS `host`;

create table `host` (
`listing_num` varchar(700) NOT NUll,
`sin` varchar(255) NOT NULL,
primary key(`listing_num`)
);

LOCK TABLES `host` WRITE;
/*!40000 ALTER TABLE `host` DISABLE KEYS */;
INSERT INTO `host` VALUES ('1','123456789'), ('2','123456789'), ('3','123123123'), ('4','123457777'), ('5','123123123'), ('6','123456789');
/*!40000 ALTER TABLE `host` ENABLE KEYS */;
UNLOCK TABLES;

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

LOCK TABLES `amenities` WRITE;
/*!40000 ALTER TABLE `amenities` DISABLE KEYS */;
INSERT INTO `amenities` VALUES ('1', 'y', 'y', 'y', 'n', 'n', 'y', 'n');
INSERT INTO `amenities` VALUES ('2', 'y', 'n', 'y', 'n', 'n', 'y', 'n');
INSERT INTO `amenities` VALUES ('3', 'y', 'n', 'y', 'n', 'n', 'y', 'n');
INSERT INTO `amenities` VALUES ('4', 'y', 'y', 'y', 'n', 'n', 'y', 'n');
INSERT INTO `amenities` VALUES ('5', 'y', 'y', 'y', 'n', 'n', 'y', 'n');
INSERT INTO `amenities` VALUES ('6', 'y', 'y', 'y', 'n', 'n', 'y', 'n');
/*!40000 ALTER TABLE `amenities` ENABLE KEYS */;
UNLOCK TABLES;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
