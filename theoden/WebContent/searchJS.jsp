<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="web.*, java.util.*" %>

<% 
ServletContext sc = getServletContext();

List<Course> addedCourses = (List<Course>) session.getAttribute("addedCourses");
if (addedCourses == null) {
	System.out.println("adding addedCourses");
	List<Course> newAddedCourses = new ArrayList<Course>(); 
	session.setAttribute("addedCourses", newAddedCourses);
	addedCourses = (List<Course>) session.getAttribute("addedCourses");
} 
%>
<script type="text/javascript">
var count = 2;
/** CALENDAR **/
/** PUT DATA **/
var year = new Date().getFullYear();
var month = new Date().getMonth();
var day = new Date().getDate();
	
var allSchedules = [
    <% 
    Calendar cd = Calendar.getInstance();
    int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
    
    int count = 0;
    Course newcourse = new Course("Engr 40", 6328);
	//addedCourses.add(newcourse);
	newcourse = new Course("CS 194",6254);
	//addedCourses.add(newcourse);
    
    
  		String newItem = "";
  	    newItem += "{";
  	  	newItem += " 'id': 'calendar_" + count + "', 'dates': [";
  	  	int itemCount = 1;
  		for (Course c : addedCourses) {						// for each course
  			char[] ch = (c.lectureDays).toCharArray();
  			for (int j = 0; j < 5; j++) {			// for each day
  				if (ch[j] == '0') continue;
  				String shift = "";
  				if (dayOfWeek == 1) shift = "+ " + j + "+1";		// sunday
  				if (dayOfWeek == 2) shift = "+ " + j;			// monday
  				if (dayOfWeek == 3) shift = "+" + j + "-1";		// tues
  				if (dayOfWeek == 4) shift = "+" + j + "-2";		// wed
  				if (dayOfWeek == 5) shift = "+" + j + "-3";		// thu
  				if (dayOfWeek == 6) shift = "+" + j + "-4";		// fri
  				if (dayOfWeek == 7) shift = "+" + j + "-5";		// sat
  	  	  	  	newItem += "{'id':" + itemCount + ", 'start': new Date(year, month, day" + shift + ", " + c.timeBegin/100 + ", " + c.timeBegin%100 + "), ";
  	  	  	  	newItem += "'end': new Date(year, month, day" + shift + ", " + c.timeEnd/100 + ", " + c.timeEnd%100 + "), ";
  	  	  	  	newItem += "'title': '" + c.code + "'},";
  	  			itemCount++;  				
  			}
  		}
  		newItem += "]},";
  		count++;
  	  	out.println(newItem);
    %>
    /*
    
	{ 'id': 'calendar_0', 'dates': [
	   {'id':1, 'start': new Date(year, month, day, 12), 'end': new Date(year, month, day, 15, 30),'title':'Berry'},
	   {'id':2, 'start': new Date(year, month, day, 14), 'end': new Date(year, month, day, 14, 40),'title':'Berry'},
	   {'id':3, 'start': new Date(year, month, day + 1, 18), 'end': new Date(year, month, day + 1, 18, 40),'title':'Berry'},
	   {'id':4, 'start': new Date(year, month, day - 1, 8), 'end': new Date(year, month, day - 1, 9, 20),'title':'Berry'},
	   {'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'Berry'}
		]
	},
	{ 'id': 'calendar_1', 'dates': [
	   {'id':1, 'start': new Date(year, month, day, 12), 'end': new Date(year, month, day, 15, 30),'title':'Cherry'},
	   {'id':2, 'start': new Date(year, month, day - 1, 14), 'end': new Date(year, month, day - 1, 18, 45),'title':'Cherry'},
	   {'id':3, 'start': new Date(year, month, day + 1, 18), 'end': new Date(year, month, day + 1, 18, 45),'title':'Cherry'},
	   {'id':4, 'start': new Date(year, month, day - 2, 9), 'end': new Date(year, month, day - 2, 10, 30),'title':'Cherry'},
	   {'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'Cherry'}
		]
	},*/
];

/** Display **/
	
	
function resetForm($dialogContent) {
	$("#event_edit_container ul li:nth-child(4)").hide();
	$("#event_edit_container ul li:nth-child(5)").hide();
}

$(document).ready(function() {
	
	
	// set up main calendar
	var $calendar = $('#calendar').weekCalendar({
		timeslotsPerHour: 2,
		scrollToHourMillis : 0,
        dateFormat: '',
		height: function($calendar){
			// return $(window).height() - $('h1').outerHeight(true);
			return 608;
		},      
        businessHours: {start: 8, end: 20, limitDisplay: true},
        allowCalEventOverlap : true,
        overlapEventsSeparate: true,
		eventDelete: function(calEvent, $event) {
			alert("deleting");
		},
		readonly: true,
		resizable: function(calEvent, $event) {
			return false;
		},
		timeslotsPerHour: 2,
		timeslotHeight: 10,
        allowEventCreation: false,
		eventRender : function(calEvent, $event) {
			switch(calEvent.id) {
				/*
				case 1: 
				  $event.css('backgroundColor', '#BF4D28');
				  break;
				case 2: 
				  $event.css('backgroundColor', '#E6AC27');
				  break;
				case 3:
				  $event.css('backgroundColor', '#80BCA3');
				  break;
				case 4:
				  $event.css('backgroundColor', '#F6F7BD');
				  break;
				case 5:
				  $event.css('backgroundColor', '#655643');
				  break;
				  */
				default: 
				  $event.css('backgroundColor', '#3D90AA');
			}
		},
		eventNew : function(calEvent, $event) {
			//alert('You\'ve added a new event. You would capture this event, add the logic for creating a new event with your own fields, data and whatever backend persistence you require.');

			var $dialogContent = $("#event_edit_container");
			$("#event_edit_container ul li:nth-child(2)").show();
			$("#event_edit_container ul li:nth-child(3)").show();

			
			var startField = $dialogContent.find("select[name='start']").val(calEvent.start);
			var endField = $dialogContent.find("select[name='end']").val(calEvent.end);
			var titleField = $dialogContent.find("input[name='title']");
			var bodyField = $dialogContent.find("textarea[name='body']");
		  $event.css('backgroundColor', 'red');
			$dialogContent.dialog({
				modal: true,
				title: "Blocking Out",
				close: function() {
				   $dialogContent.dialog("destroy");
				   $dialogContent.hide();
				   //$('#calendar').weekCalendar("removeUnsavedEvents");
				},
				buttons: {
					save : function() {
						$dialogContent.dialog("close");
						//calEvent.id = id;
						//id++;
						var start = new Date(startField.val());
						var end = new Date(endField.val());
						//calEvent.title = titleField.val();
						//calEvent.body = bodyField.val();
						var startTime = parseInt(start.getHours())*100 + parseInt(start.getMinutes());
						var endTime = parseInt(end.getHours())*100 + parseInt(end.getMinutes());
						$('#scheduleForm #blockDay').val(start.getDay());
						$('#scheduleForm #blockStart').val(startTime);
						$('#scheduleForm #blockEnd').val(endTime);

						$('#scheduleForm').submit();
						// $calendar.weekCalendar("removeUnsavedEvents");
						// $calendar.weekCalendar("updateEvent", calEvent);
				   },
				   cancel : function() {
						$event.remove();
						//$('#calendar').weekCalendar("removeUnsavedEvents");
						$dialogContent.dialog("close");
				   }
				}
			}).show();
			$event.find('.wc-title').text("blocked out");

			 $dialogContent.find(".date_holder").text($calendar.weekCalendar("formatDate", calEvent.start));
			 setupStartAndEndTimeFields(startField, endField, calEvent, $calendar.weekCalendar("getTimeslotTimes", calEvent.start));
		},
		eventClick : function(calEvent, $event) {

			if (calEvent.readOnly) {
				return;
			}

			var $dialogContent = $("#event_edit_container");
			$("#event_edit_container ul li:nth-child(1)").hide();
			$("#event_edit_container ul li:nth-child(2)").hide();
			$("#event_edit_container ul li:nth-child(3)").hide();
			$("#event_edit_container ul li:nth-child(4)").hide();
			$("#event_edit_container ul li:nth-child(5)").hide();
			var startField = $dialogContent.find("select[name='start']").val(calEvent.start);
			var endField = $dialogContent.find("select[name='end']").val(calEvent.end);
			var titleField = $dialogContent.find("input[name='title']").val(calEvent.title);
			var bodyField = $dialogContent.find("textarea[name='body']");
			bodyField.val(calEvent.body);

			$dialogContent.dialog({
				modal: true,
				title: "Course: " + calEvent.title,
				close: function() {
				   $dialogContent.dialog("destroy");
				   $dialogContent.hide();
				},
				buttons: {
				   "delete" : function() {
				   
						$dialogContent.dialog("close");
						
						var courseName = titleField.val();
						alert("removing : " + courseName);
						$('#scheduleForm #blockCourse').val(courseName);
						$('#scheduleForm').submit();
						
						
						$event.remove();
					  //$calendar.weekCalendar("removeEvent", calEvent.id);
					  $dialogContent.dialog("close");
				   },
				   cancel : function() {
					  $dialogContent.dialog("close");
				   }
				}
			}).show();

			 var startField = $dialogContent.find("select[name='start']").val(calEvent.start);
			 var endField = $dialogContent.find("select[name='end']").val(calEvent.end);
			 $dialogContent.find(".date_holder").text($calendar.weekCalendar("formatDate", calEvent.start));
			 setupStartAndEndTimeFields(startField, endField, calEvent, $calendar.weekCalendar("getTimeslotTimes", calEvent.start));
			 $(window).resize().resize(); //fixes a bug in modal overlay size ??

		},
		data: function(start, end, callback) {
			var dataSource = $('#data_source').val();
			var dataSourceInt = parseInt(dataSource);
			if (isNaN(dataSourceInt)) dataSourceInt = 0;
			var schedule = allSchedules[dataSourceInt];
			var schedule_events = schedule['dates'];
			var newEventData = {
				options: {
				},
				events : schedule_events
			}
			callback(newEventData);
		}
    });
	

    $('#data_source').change(function() {
		$calendar.weekCalendar('refresh');
    });
	
	
	function setupStartAndEndTimeFields($startTimeField, $endTimeField, calEvent, timeslotTimes) {

      $startTimeField.empty();
      $endTimeField.empty();

      for (var i = 0; i < timeslotTimes.length; i++) {
         var startTime = timeslotTimes[i].start;
         var endTime = timeslotTimes[i].end;
         var startSelected = "";
         if (startTime.getTime() === calEvent.start.getTime()) {
            startSelected = "selected=\"selected\"";
         }
         var endSelected = "";
         if (endTime.getTime() === calEvent.end.getTime()) {
            endSelected = "selected=\"selected\"";
         }
         $startTimeField.append("<option value=\"" + startTime + "\" " + startSelected + ">" + timeslotTimes[i].startFormatted + "</option>");
         $endTimeField.append("<option value=\"" + endTime + "\" " + endSelected + ">" + timeslotTimes[i].endFormatted + "</option>");

         $timestampsOfOptions.start[timeslotTimes[i].startFormatted] = startTime.getTime();
         $timestampsOfOptions.end[timeslotTimes[i].endFormatted] = endTime.getTime();

      }
      $endTimeOptions = $endTimeField.find("option");
      $startTimeField.trigger("change");
   }
	   
   var $endTimeField = $("select[name='end']");
   var $endTimeOptions = $endTimeField.find("option");
   var $timestampsOfOptions = {start:[],end:[]};

	   //reduces the end time options to be only after the start time options.
   $("select[name='start']").change(function() {
      var startTime = $timestampsOfOptions.start[$(this).find(":selected").text()];
      var currentEndTime = $endTimeField.find("option:selected").val();
      $endTimeField.html(
            $endTimeOptions.filter(function() {
               return startTime < $timestampsOfOptions.end[$(this).text()];
            })
            );

      var endTimeSelected = false;
      $endTimeField.find("option").each(function() {
         if ($(this).val() === currentEndTime) {
            $(this).attr("selected", "selected");
            endTimeSelected = true;
            return false;
         }
      });

      if (!endTimeSelected) {
         //automatically select an end date 2 slots away.
         $endTimeField.find("option:eq(1)").attr("selected", "selected");
      }

   });
	   
	var $about = $("#about");
	$("#about_button").click(function() {
		$about.dialog({
			title: "About this calendar demo",
			width: 600,
			close: function() {
				$about.dialog("destroy");
				$about.hide();
			},
			buttons: {
				close : function() {
					$about.dialog("close");
	            }
			}
		}).show();
	});
	
	function submitQuery() {
		
		var orderedFactors = $('#sortable').sortable('toArray');
		// alert(orderedFactors);
		var relevance = 0;
		var interest = 0;
		var level = 0;
		var work = 0;
		var popularity = 0;
		var gers = 0;
		var major = 0;
		
		/** gets factor numbers **/
		var len = orderedFactors.length;
		for (var i = 0; i < len; i++) {
		   if (orderedFactors[i] == 'RELEVANCE') relevance = (len - i);
		   if (orderedFactors[i] == 'INTEREST') interest = (len - i);
		   if (orderedFactors[i] == 'LEVEL') level = (len - i);
		   if (orderedFactors[i] == 'WORK') work = (len - i);
		   if (orderedFactors[i] == 'POPULARITY') popularity = (len - i);
		   if (orderedFactors[i] == 'GERS') gers = (len - i);
		   if (orderedFactors[i] == 'MAJOR') major = (len - i);
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
			   if (factor_id == 'MAJOR') major = 0;
			}
		});
		// alert("relevance: " + relevance + " level:" + level + "project :" + project); 
		var query = $("#query").val();
		$("#search_results").load('Servlet_Search', {"query": query, "relevance": relevance, "interest": interest, "level": level, "work": work, "popularity":popularity, "gers":gers, "major":major}, function() {
			$('#search_results li').click(function() {
				$( this ).find('span.descr').toggle();
			});		
			$('#search_results li span.addCourse').click(function() {
				var courseid = $(this).parent().find('span.id').text();
				var coursecode = $(this).parent().find('span.code').text();
				//alert("adding Course " + courseid + coursecode);
				$("#search_results").load('Servlet_AddCourse', {"courseid": courseid, "coursecode": coursecode}, function() {

					window.location.reload();
				});
			});
			$('#search_results li').hover(
				function (e) {
					var timeBegin = $( this ).find('span.beginTime').text();
					var timeEnd = $( this ).find('span.endTime').text();
					var lectureDays = $( this ).find('span.lectureDays').text();
					var code = $( this ).find('span.code').text();
					
					var idName = $( this ).attr("id");
					var mySplitResult = idName.split("_");
					var id = mySplitResult[1];
					$('#data_source').val(id);
					
					
					
					var d = new Date();
					var dayOfWeek = d.getDay() + 1;
					var shift = 0;
					//alert(code + " " + lectureDays + " " + timeBegin + " " + timeEnd);
					for(j=0; j<lectureDays.length; j++) {
		  				if (lectureDays.charAt(j) == '0') continue;
		  				if (dayOfWeek == 1) shift = j + 1;		// sunday
		  				if (dayOfWeek == 2) shift = j;			// monday
		  				if (dayOfWeek == 3) shift = j-1;		// tues
		  				if (dayOfWeek == 4) shift = j-2;		// wed
		  				if (dayOfWeek == 5) shift = j-3;		// thu
		  				if (dayOfWeek == 6) shift = j-4;		// fri
		  				if (dayOfWeek == 7) shift = j-5;		// sat
						var num = allSchedules[0]["dates"].length - 1;
		  				var lastitemID = 0;
		  				if (num < 0) {
							lastitemID = 0;
		  				} else {
							lastitemID = allSchedules[0]["dates"][num]['id'];
		  				}
						var newID = lastitemID + 1;
						var item = {'id':newID, 'start': new Date(year, month, day + shift, timeBegin/100, timeBegin%100), 'end': new Date(year, month, day + shift, timeEnd/100, timeEnd%100),'title':code};
						allSchedules[0]["dates"].push(item);
					}
					$calendar.weekCalendar('refresh');
				}, function() {

					var code = $( this ).find('span.code').text();
					for (var i = 0; i < allSchedules[0]['dates'].length; i++) {
					    if( allSchedules[0]['dates'][i]["title"] == code ) {
					    	allSchedules[0]['dates'].splice(i, 1);
					    	i--;
					    }
					}
					$calendar.weekCalendar('refresh');
					
				}
			);
			
		});


	}
	$(function() {
		$( "#sortable" ).sortable({
			containment: '#stipulations',
		});
		$( "#sortable" ).disableSelection();
	});
	/* Detection for submitting queries **/
	$('#query').keypress(function(e){
		if (e.which == 13) submitQuery();
    });
    $( "#submitSearch" ).click( function() {
		submitQuery();
	});	
	$( "#sortable" ).sortable({
	   stop: function(event, ui) {
			submitQuery();
	   }
	});
	$( "li input[type='checkbox']" ).click(function() {
		submitQuery();
	});
});



</script>