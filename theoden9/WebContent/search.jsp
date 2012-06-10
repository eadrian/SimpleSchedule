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
	<jsp:include page="searchJS.jsp"></jsp:include>	
	
	<!-- SCRIPTS: SCROLLING -->	
	<link href="libs/scrollbar/jquery.mCustomScrollbar.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="libs/scrollbar/jquery.easing.1.3.js"></script>
	<script type="text/javascript" src="libs/scrollbar/jquery.mousewheel.min.js"></script>
   <script type="text/javascript" language="javascript">
   </script>
</head>
<body> 
	<jsp:include page="includes/nav.jsp"></jsp:include>	
<script type="text/javascript">
	$(document).ready(function() {	
		$("nav ul li:nth-child(2)").addClass("current");
	});
</script>
<div style="border: 1px solid white; float: left; height: 100%; width: 700px;">

	<div id="search_calendar" style="border: 1px solid white; overflow: hidden; height: 40%">
		<div id="calendarWrapperWrapper">
			<div id='calendarWrapper'>
				<div id='calendar'></div>
			</div>
		</div>
	</div>
	<div id="search_bar" style="border: 1px solid white; overflow: scroll; height: 10%; overflow: hidden">
		<input id="interestFactor" name="interestFactor" type="hidden" value ="0" placeholder="interestFactor ..."/>
		<input id="query" name="query" type="text" placeholder="computer networks..."/>
		<span id="submitSearch"> SUBMIT</span>
	</div>
	<style>
	#search_results li {
		background: white;
		padding: 20px;
		margin: 5px 0;
		cursor: pointer;
	}
	</style>
	<div id="search_results" style="border: 1px solid white; overflow: scroll; height: 40%">
		<%@ page import="web.*, java.util.*" %>
		<% ServletContext sc = getServletContext();
		List<String> registerErrors = (ArrayList<String>) request.getAttribute("searchResults");
		Map<String, Course> sortedScores = (Map<String, Course>) request.getAttribute("sortedScores");
		if (registerErrors != null) {
			out.println("<h3>Search Results:</h3>");
			out.println("<ul id='searchResultsList'>");
			for (String key : sortedScores.keySet()) {
				Course c = sortedScores.get(key);
			}
			out.println("</ul>");
		} else {
			out.println("[nothing received]");
		}
		
		%>
	</div>
</div>
</div>
<div class="modal"><!-- Place at bottom of page --></div>
	
</body>
</html>
	