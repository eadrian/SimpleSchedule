-- phpMyAdmin SQL Dump
-- version 3.3.7deb7
-- http://www.phpmyadmin.net
--
-- Host: tools.stanford.edu
-- Generation Time: Apr 23, 2012 at 12:34 AM
-- Server version: 5.1.61
-- PHP Version: 5.3.3-7+squeeze8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `c_cs108_elchen3`
--

-- --------------------------------------------------------

--
-- Table structure for table `rhun_courses`
--

CREATE TABLE IF NOT EXISTS `rhun_courses` (
  `ID` int(11) NOT NULL,
  `avgGrade` varchar(5) NOT NULL,
  `courseTitle` varchar(10) NOT NULL,
  `deptID` int(11) NOT NULL,
  `description` text NOT NULL,
  `fullTitle` varchar(100) NOT NULL,
  `lectureDays` varchar(50) NOT NULL,
  `lecturer` text NOT NULL,
  `location` text NOT NULL,
  `numReviews` int(11) NOT NULL,
  `numUnits` int(11) NOT NULL,
  `rating` float NOT NULL,
  `score` float NOT NULL,
  `timeBlock` varchar(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `rhun_courses`
--


-- --------------------------------------------------------

--
-- Table structure for table `rhun_coursesTaken`
--

CREATE TABLE IF NOT EXISTS `rhun_coursesTaken` (
  `sunetID` varchar(15) NOT NULL,
  `course` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `rhun_coursesTaken`
--


-- --------------------------------------------------------

--
-- Table structure for table `rhun_departments`
--

CREATE TABLE IF NOT EXISTS `rhun_departments` (
  `ID` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `fullname` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `rhun_departments`
--


-- --------------------------------------------------------

--
-- Table structure for table `rhun_lectureDays`
--

CREATE TABLE IF NOT EXISTS `rhun_lectureDays` (
  `courseID` int(11) NOT NULL,
  `day` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `rhun_lectureDays`
--


-- --------------------------------------------------------

--
-- Table structure for table `rhun_lecturers`
--

CREATE TABLE IF NOT EXISTS `rhun_lecturers` (
  `ID` int(11) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `rhun_lecturers`
--


-- --------------------------------------------------------

--
-- Table structure for table `rhun_quartersOffered`
--

CREATE TABLE IF NOT EXISTS `rhun_quartersOffered` (
  `courseID` int(11) NOT NULL,
  `quarter` varchar(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `rhun_quartersOffered`
--


-- --------------------------------------------------------

--
-- Table structure for table `rhun_requirements`
--

CREATE TABLE IF NOT EXISTS `rhun_requirements` (
  `courseID` int(11) NOT NULL,
  `requirement` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `rhun_requirements`
--


-- --------------------------------------------------------

--
-- Table structure for table `rhun_tags`
--

CREATE TABLE IF NOT EXISTS `rhun_tags` (
  `courseID` int(11) NOT NULL,
  `tag` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `rhun_tags`
--


-- --------------------------------------------------------

--
-- Table structure for table `rhun_users`
--

CREATE TABLE IF NOT EXISTS `rhun_users` (
  `sunetID` varchar(20) NOT NULL,
  `password` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `year` varchar(10) NOT NULL,
  `major` varchar(30) NOT NULL,
  `unitsTaken` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `rhun_users`
--

