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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
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
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('a@b.c','a','b','09/10/1996','28 Golden Meadow Dr.','Student','123456789','pass','12334553');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;


Drop table if exists `Location`;

create table `Location` (
`Listing Number` int NOT NUll auto_increment,
`Suite Number` varchar(256) ,
`House Number` varchar(256) Not Null,
`Street Name` varchar(256) Not Null,
`Postal Code` varchar(10) Not Null,
`City` varchar(256) Not Null,
`Country` varchar(256) Not Null,
`Latitude` decimal(30) Not Null,
`Longitude` decimal(30) Not Null,
primary key(`Latitude`, `Longitude`)

)ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into `Location`(`Suite Number`,`House Number`, `Street Name`,`Postal Code`, `City`, `Country`, `Latitude`, `Longitude`)values
('512','505','Cummer Ave','M2k2L8', 'Toronto', 'Canada',43.651890, -79.381706),
('600','60','Cummer Ave','M2k2L8', 'Toronto', 'Canada',43.651890, -79.381706),
(Null,'861','Redhead cres','M3V3B3', 'Scarborough', 'Country',43.651890, -79.381706),
(Null,'B20','Anita Colony','302015', 'Jaipur', 'India',26.912434, 75.787270),
('2','232','Fort street','G6123', 'Thunder Bay', 'Canada',48.380894, -89.247681),
('513','505','Cummer Ave','M2k2L8', 'Toronto', 'Canada',43.651890, -79.381706),
(Null,'B346','Orange rd','M9KC33', 'Orangville', 'Canada',45.651890, -81.781706),
('22','09','Simmer St','RK74N6', 'Toronto', 'Canada',43.651890, -79.381706);

create table `availability` (
`Listing Number` int NOT NUll ,
`Start Date` varchar(10) Not null,
`End Date` varchar(10) Not Null,
`Cost per day` decimal(30,2) Not Null,
primary key(`Listing Number`, `Start Date`, `End Date`)

)ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into `availability`(`Listing Number`,`Start Date`, `End Date`,`Cost per day`)values
(1,'1/03/2019', '10/03/2019', 80.00),
(4,'18/10/2019', '2/11/2019', 17.74),
(4,'7/6/2019', '7/7/2019', 27.23),
(2,'31/12/2019', '1/1/2020', 30.00),
(5,'14/6/2020', '25/6/2020', 60.74),
(7,'7/7/2019', '8/7/2019', 54.99),
(3,'1/03/2019', '10/03/2019', 80.00),
(3,'1/03/2019', '15/03/2019', 107.74),
(8,'15/2/2019', '19/2/2019', 322.69),
(6,'5/2/2019', '5/2/2019', 30.56),
(1, '1/1/2019', '3/1/2021', 30.00);



Drop table if exists `Bookings`;

create table `Bookings` (
`Listing Number` int NOT NUll ,
`Start Date` varchar(10) Not null,
`End Date` varchar(10) Not Null,
`Cost Per Day` decimal(30,2) Not Null,
`Renter sin` int(10) Not Null,
`Renter Comment on listing` varchar(3000),
`Renter comment on Host` varchar(3000),
`Host comment on Renter` varchar(3000),
`Rating` int(1),
primary key(`Listing Number`,`Start Date`, `End Date`)
);

insert into `Bookings`(`Listing Number`,`Start Date`, `End Date`,`Cost Per Day`, `Renter Sin`,`Renter Comment on listing`,`Renter comment on Host`,`Host comment on Renter`, `Rate listing`, `Rate Host`, `Rate Renter`)values
(1,'2/04/2021', '3/10/2021', 40.00,12121213, null, null, null, null),
(4,'18/10/2020', '2/11/2020', 47.74, 147785321 , null, null, null, null),
(4,'8/8/2019', '18/8/2019', 55.23,123123123, null, null, null, null),
(2,'31/8/2019', '1/10/2020', 250.00,123123123, null, null, null, null),
(5,'1/2/2019', '10/2/2019', 30.74, 123456789, null, null, null, null),
(7,'31/8/2019', '1/10/2020', 75.00,123123123, null, null, null, null),
(3,'1/5/2019', '10/11/2019', 82.74, 165456789, null, null, null, null),
(8,'1/2/2014', '10/2/2014', 37.74, 123445689, "beautiful","nice guy","clean", 5,3,5),
(6,'3/8/2017', '3/10/2017', 60.00,9953123, "mold", "unfriendly"," stubborn", 2,3,1),
(3,'1/5/2005', '10/11/2008', 180.74, 32134789, "clean", Null, "friendly",4,Null,5);





create table `Host` (
`Listing Number` int NOT NUll ,
`Host Sin Number` int Not null,
primary key(`Listing Number`)
);
insert into `Host`(`Listing Number`,`Host Sin number`)values
(1,123123231),
(2,78756231),
(3,27457531),
(4,95674323),
(5,50808231),
(6,7771772),
(7,123123231),
(8,123123231);



create table `Credit Card` (
`Number` int(10) not null,
`Expiry` datetime not null,
primary key(`Number`)
);

insert into `Credit Card`(`Number`, `Expiry`)values
(12334553, 2/2/2020),
(69696969, 1/2/2021),
(12245664, 16/2/2022),
(12333333, 5/11/2008),
(98334543, 8/6/2024);


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;





