<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN""http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>Simple Schedule</title>
	<!--
    <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Jura">
    <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Lato:100,200,300,400,800">
    <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Comfortaa">
	-->
	
	<!-- CSS -->
    <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Dosis:200,400,800">
    <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Open+Sans">
	<link rel='stylesheet' type='text/css' href='css/reset.css' />
	<link rel='stylesheet' type='text/css' href='css/coldfells.css' />					
	<link rel='stylesheet' type='text/css' href='css/jquery.weekcalendar.css' />	
	
	<style type="text/css">
	.calendar2 .wc-grid-timeslot-header, .calendar3 .wc-grid-timeslot-header { display: none; }
	.wc-header { display: none; }
	.calendar2, .calendar3 { float: right; height: 70px; border: 1px solid gray; border: 1px solid #2b2b2b; overflow: hidden; width: 130px;}
	</style>	
	
	<!-- JQUERY CALLS -->
	<script type='text/javascript' src='libs/jquery-1.4.4.min.js'></script>
    <script type='text/javascript' src='libs/jquery-ui-1.8.11.custom.min.js'></script>
	  
	<!-- SCRIPTS: CALENDAR -->
	<script type="text/javascript" src="libs/date.js"></script>
	<script type='text/javascript' src='libs/jquery.weekcalendar.js'></script>
	<script type='text/javascript' src='libs/coldfells.js'></script>
	
	<!-- SCRIPTS: SCROLLING -->	
	<link href="libs/scrollbar/jquery.mCustomScrollbar.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="libs/scrollbar/jquery.easing.1.3.js"></script>
	<script type="text/javascript" src="libs/scrollbar/jquery.mousewheel.min.js"></script>
   <script type="text/javascript" language="javascript">
   </script>
</head>
<body> 
	<jsp:include page="includes/nav.jsp"></jsp:include>	