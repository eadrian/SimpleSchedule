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
	<nav>
		<article>
			<h1>Simple Schedule</h1>
			<h6>&#151; Stanford &#151;</h6>
		</article>
		<ul>
			<li>
				<a href="index.jsp" title="Schedule">
				<img src="images/search.png" />Schedule
				</a>
			</li>
			<li>
				<a href="search.jsp" title="Search">
				<img src="images/search.png" />Search
				</a>
			</li>
			<li>
				<a href="search.jsp" title="Profile">
				<img src="images/profile.png" />Profile
				</a>
			</li>
			<li>
				<a href="search.jsp" title="Planner">
				<img src="images/clock.png" />Planner
				</a>
			</li>
			<li class="secondary" style="margin-top: 100px">
				<a href="login.jsp?type=logout" title="Logout">Logout</a>
			</li>
			<li class="secondary" >About</li>
		</ul>
	</nav>
	<div id="content">
		<div id="stipulationsWrapper">
			<div id="stipulations">
				<h3>Stipulations:</h3>
				<ul id="sortable" class="ui-sortable" class="factors">
					<li id="RELEVANCE" class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span><input type="checkbox" checked />RELEVANCE</li>
					<li id="INTEREST" class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span><input type="checkbox" checked />INTEREST</li>
					<li id="LEVEL" class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span><input type="checkbox" checked />LEVEL</li>
					<li id="WORK" class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span><input type="checkbox" checked />WORK</li>
					<li id="POPULARITY" class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span><input type="checkbox" checked />POPULARITY</li>
					<li id="GERS" class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span><input type="checkbox" checked />GER Requirements</li>
				</ul>
				<h3>Major:</h3>Computer Science
			</div>
		</div>