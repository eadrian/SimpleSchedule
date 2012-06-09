<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

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
	<link rel='stylesheet' type='text/css' href='css/demo.css' />				
	<link rel='stylesheet' type='text/css' href='css/jquery.weekcalendar.css' />	
	<link rel='stylesheet' type='text/css' href='css/jquery-ui-1.8.11.custom.css' />
	<style type="text/css">
	#debugging { display: none }
	#debugging ul { margin: 10px; }
	.thumb .wc-grid-timeslot-header { display: none; }
	.customScrollBox .wc-header { display: none; }
	.wc-time-slots:hover { cursor: pointer }
	.thumb { float: left; height: 70px; border: 1px solid gray; border: 1px solid #2b2b2b; overflow: hidden; width: 130px;}

	</style>	


	<!-- JQUERY CALLS -->

	<script type='text/javascript' src='libs/jquery-1.4.4.min.js'></script>

    <script type='text/javascript' src='libs/jquery-ui-1.8.11.custom.min.js'></script>

	  

	<!-- SCRIPTS: CALENDAR -->

	<script type="text/javascript" src="libs/date.js"></script>

	<script type='text/javascript' src='libs/jquery.weekcalendar.js'></script>
	<script type='text/javascript'>

	$(document).ready(function() {	
		function submitQuery() {
			var orderedFactors = $('#sortable').sortable('toArray');
			//alert(orderedFactors);
			var relevance = 0;
			var interest = 0;
			var level = 0;
			var work = 0;
			var popularity = 0;
			var gers = 0;
			
			/** gets factor numbers **/
			var len = orderedFactors.length;
			for (var i = 0; i < len; i++) {
			   if (orderedFactors[i] == 'RELEVANCE') relevance = (len - i);
			   if (orderedFactors[i] == 'INTEREST') interest = (len - i);
			   if (orderedFactors[i] == 'LEVEL') level = (len - i);
			   if (orderedFactors[i] == 'WORK') work = (len - i);
			   if (orderedFactors[i] == 'POPULARITY') popularity = (len - i);
			   if (orderedFactors[i] == 'GERS') gers = (len - i);
			}
			/** filters out the unchecked factors **/
			$('#sortable li').each(function(index) {
				var factor_id = $( this ).attr('id');
				if (!$( this ).find("input[type='checkbox']").is(':checked')) { 
				   if (factor_id == 'RELEVANCE') relevance = 0;
				   if (factor_id == 'INTEREST') interest = 0;
				   if (factor_id == 'LEVEL') level = 0;
				   if (factor_id == 'WORK') work = 0;
				   if (factor_id == 'POPULARITY') popularity = 0;
				   if (factor_id == 'GERS') gers = 0;
				}
			});
			
			// updates the input variables before form submission
			$('#scheduleForm #relevance').val(relevance);
			$('#scheduleForm #interest').val(interest);
			$('#scheduleForm #level').val(level);
			$('#scheduleForm #work').val(work);
			$('#scheduleForm #popularity').val(popularity);
			$('#scheduleForm #gers').val(gers);
			
			$('#scheduleForm').submit();
		}
		$(function() {
			$( "#sortable" ).sortable({
				containment: '#stipulations',
				stop: function(event, ui) {
					submitQuery();
				}
			});
			$( "#sortable" ).disableSelection();
			$( "li input[type='checkbox']" ).click(function() {
				submitQuery();
			});
		});
		$("nav ul li:nth-child(1)").addClass("current");
		
	});
	</script>

	<!-- <script type='text/javascript' src='libs/lab.js'></script>-->
	
	<jsp:include page="lab.jsp"></jsp:include>	

	<!-- SCRIPTS: SCROLLING -->	

	<link href="libs/scrollbar/jquery.mCustomScrollbar.css" rel="stylesheet" type="text/css" />

	<script type="text/javascript" src="libs/scrollbar/jquery.easing.1.3.js"></script>

	<script type="text/javascript" src="libs/scrollbar/jquery.mousewheel.min.js"></script>

</head>

<body> 

<div id="debugging" style="background: #2e2e2e; height: auto; color: white; z-index: 1000; position: absolute; top: 0; left: 200px; width: 200px;">
	<span class="collapse">Collapse</span>
	<span class="expand">Expand</span>
	<script>
	$('#debugging span.collapse').click(function() {
		$('#debugging').css("height", "10px");
		$('#debugging').css("overflow", "none");
	});
	$('#debugging span.expand').click(function() {
		$('#debugging').css("height", "auto");
		$('#debugging').css("overflow", "scroll");
	});
	</script>
</div>

<div id="scheduleFormDiv" style="display:none">
	<form id="scheduleForm" action="Servlet_Sched" method="post">
		<!-- factors -->
		<input type="hidden" name="relevance" id="relevance"  />
		<input type="hidden" name="interest" id="interest" />
		<input type="hidden" name="level" id="level" />
		<input type="hidden" name="work" id="work" />
		<input type="hidden" name="popularity" id="popularity"  />
		<input type="hidden" name="gers" id="gers" />
		
		<!-- x out blocks -->
		<input type="hidden" name="blockCourse" id="blockCourse"  />
		<input type="hidden" name="blockDay" id="blockDay" />
		<input type="hidden" name="blockStart" id="blockStart" />
		<input type="hidden" name="blockEnd" id="blockEnd" />
		<input type="submit" value="sub" />
	</form>
</div>
	<jsp:include page="includes/nav.jsp"></jsp:include>	

		<div id="calendar_selection" class="ui-corner-all" style="display: none">

			<strong>Event Data Sources: </strong>

			<select id="data_source">

			  <option value="">Select Event Data</option>
			  <option value="0">Event Data 0</option>
			  <option value="1">Event Data 1</option>
			  <option value="2">Event data 2</option>
			  <option value="3">Event data 3</option>
			  <option value="4">Event data 4</option>
			  <option value="5">Event data 5</option>
			  <option value="6">Event data 6</option>
			  <option value="7">Event data 7</option>
			  <option value="8">Event data 8</option>
			  <option value="9">Event data 9</option>
			  <option value="10">Event data 10</option>
			  <option value="11">Event data 11</option>

			</select>

		</div>

		<div id="calendarWrapperWrapper">

			<div id='calendarWrapper'>
				<div id='calendar'></div>
				<div id="event_edit_container">
					<form>
						<input type="hidden" />
						<ul>
							<li>
								<span>Date: </span><span class="date_holder"></span> 
							</li>
							<li>
								<label for="start">Start Time: </label><select name="start"><option value="">Select Start Time</option></select>
							</li>
							<li>
								<label for="end">End Time: </label><select name="end"><option value="">Select End Time</option></select>
							</li>
							<li>
								<label for="title">Title: </label><input type="text" name="title" />
							</li>
							<li>
								<label for="body">Body: </label><textarea name="body"></textarea>
							</li>
						</ul>
					</form>
				</div>

			</div>

		</div>

	</div>
	
	<span id="labresults"></span>

	
	<!-- HORIZONTAL SCROLL -->



	<!-- content block -->

	<div id="mcs5_container" style="margin-top: -24px">

		<div class="customScrollBox">

			<!-- horWrapper div is important for horizontal scrollers! -->

			<div class="horWrapper"> 

			<div class="container">

				<div class="content">

				</div>

			</div>

			<div class="dragger_container">

				<div class="dragger"></div>

			</div>

			</div>

		</div><a href="#" class="scrollUpBtn">&#9668; </a> 

		<a href="#" class="scrollDownBtn">&#9658; </a>

	</div>



<script>

$(window).load(function() {

	mCustomScrollbars();

});



function mCustomScrollbars(){

	/* 

	malihu custom scrollbar function parameters: 

	1) scroll type (values: "vertical" or "horizontal")

	2) scroll easing amount (0 for no easing) 

	3) scroll easing type 

	4) extra bottom scrolling space for vertical scroll type only (minimum value: 1)

	5) scrollbar height/width adjustment (values: "auto" or "fixed")

	6) mouse-wheel support (values: "yes" or "no")

	7) scrolling via buttons support (values: "yes" or "no")

	8) buttons scrolling speed (values: 1-20, 1 being the slowest)

	*/

	$("#mcs_container").mCustomScrollbar("vertical",400,"easeOutCirc",1.05,"auto","yes","yes",10); 

	$("#mcs2_container").mCustomScrollbar("vertical",0,"easeOutCirc",1.05,"auto","yes","no",0); 

	$("#mcs3_container").mCustomScrollbar("vertical",900,"easeOutCirc",1.05,"auto","no","no",0); 

	$("#mcs4_container").mCustomScrollbar("vertical",200,"easeOutCirc",1.25,"fixed","yes","no",0); 

	$("#mcs5_container").mCustomScrollbar("horizontal",500,"easeOutCirc",1,"fixed","yes","yes",20); 

}



/* function to fix the -10000 pixel limit of jquery.animate */

$.fx.prototype.cur = function(){

    if ( this.elem[this.prop] != null && (!this.elem.style || this.elem.style[this.prop] == null) ) {

      return this.elem[ this.prop ];

    }

    var r = parseFloat( jQuery.css( this.elem, this.prop ) );

    return typeof r == 'undefined' ? 0 : r;

}



/* function to load new content dynamically */

function LoadNewContent(id,file){

	$("#"+id+" .customScrollBox .content").load(file,function(){

		mCustomScrollbars();

	});

}

</script>

<script src="libs/scrollbar/jquery.mCustomScrollbar.js"></script>

</body>

</html>

