<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="web.*, java.util.*" %>

<% 
	ServletContext sc = getServletContext();

	List<List<Course>> results = (List<List<Course>>) request.getAttribute("scheduleResults");
	Schedule bs =(Schedule) request.getAttribute("BlockSched");
	for (int i=0; i<bs.items.size(); i++) {
		scheduleItem si = bs.items.get(i);
		System.out.println("BLOCKED:    "+si.name+": "+si.days+ " : "+si.start);
	}
	String returnedHTML = "";
	for (List<Course> lc : results) {
		returnedHTML += "<ul>";
		for (Course c : lc) {
			returnedHTML += "<li class='course'><strong>" + c.code + ": " + c.title + "</strong>";
			returnedHTML += "<i> " + c.lectureDays + " </i>";
			returnedHTML += "<i> " + c.timeBegin + " </i>";
			returnedHTML += "<i> " + c.timeEnd + " </i>";
			returnedHTML += "<span style='display: none'>" + c.description + "</span>";
			returnedHTML += "</li>";
			System.out.println(c.code + c.description);
		}
		returnedHTML += "</ul>";
	//	Gson gson = new Gson();
	//	String json = gson.toJson(lc);
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
  	for (List<Course> lc : results) {				// for each schedule
  		String newItem = "";
  	    newItem += "{";
  	  	newItem += " 'id': 'calendar_" + count + "', 'dates': [";
  	  	int itemCount = 1;
  		for (Course c : lc) {						// for each course
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
  	  	  	  	String etitle = new String(c.title.replace("'", ""));
  	  	  		newItem += "'name': '" + etitle + "',";
  	  	  	  	newItem += "'title': '" + c.code + "'},";
  	  			itemCount++;  				
  			}
  		}
  		
  		for (int i =0; i<bs.items.size(); i++) {
  			scheduleItem si = bs.items.get(i);
  			char[] ch = (si.days).toCharArray();
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
  	  	  	  	newItem += "{'id':" + itemCount + ", 'start': new Date(year, month, day" + shift + ", " + si.start/100 + ", " + si.start%100 + "), ";
  	  	  	  	newItem += "'end': new Date(year, month, day" + shift + ", " + si.end/100 + ", " + si.end%100 + "), ";
  	  	  		newItem += "'name': '" + "" + "',";
  	  	  	  	newItem += "'title': '" + "BLOCK" + "'},";
  	  			itemCount++;  				
  			}
  		}
  		/**List<Course> addedCourses = (List<Course>) session.getAttribute("addedCourses");
  		if (addedCourses != null) {
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
  		}**/
  		
  		
  		newItem += "]},";
  		count++;
  	  	out.println(newItem);
  	}
  	
  	
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
	
function drawCalendarThumb(calendarID, eventData) {
	$("<div/>", {
		id: calendarID,
		"class": "thumb",
	}).appendTo(".horWrapper .content");
	var $calendar = $("#" + calendarID).weekCalendar({
		timeslotsPerHour: 2,
		scrollToHourMillis : 0,
		timeslotHeight: 4,
        daysToShow: 7,
        businessHours: {start: 10, end: 16, limitDisplay: true},
		height: function($calendar){
			return $(window).height() - $('h1').outerHeight(true);
		},
		readonly: true,
		data: function(start, end, callback) {
			var dataSource = $('#data_source').val();
			callback(eventData);
		}
	});
}
	
function resetForm($dialogContent) {
	$("#event_edit_container ul li:nth-child(4)").hide();
	$("#event_edit_container ul li:nth-child(5)").hide();
}

$(document).ready(function() {
	$('#debugging').append("<%=returnedHTML%>");
	
	// load all the thumbs
	for (var i = 0; i < allSchedules.length; i++) {
		var schedule = allSchedules[i];
		var schedule_events = schedule['dates'];
		var eventData = {
			options: {
			},
			events : schedule_events
		};
		drawCalendarThumb(schedule['id'], eventData);
	}

	var colorkeeper = new Array();
	var curColor = "#BF4D28";
	
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
        daysToShow: 7,
		eventDelete: function(calEvent, $event) {
			alert("deleting");
		},
		resizable: function(calEvent, $event) {
			return false;
		},
		timeslotsPerHour: 2,
		timeslotHeight: 20,
        allowEventCreation: true,
        calendarAfterLoad: function(calendar) {
        	colorkeeper = [];
        },
		eventRender : function(calEvent, $event) {
			var eventColor = "#BF4D28";
			var found = false;
		    for(var i=0; i<colorkeeper.length; i++) {
		        if (colorkeeper[i] == calEvent.title) {
		        	found = true;
		        	break;
		        }
		    }
		    if (calEvent.title == "BLOCK") {
				$event.css('backgroundColor', 'red');
				return;
			} else if (!found) {
		    	colorkeeper.push(calEvent.title);
				switch(colorkeeper.length) {
					case 1: 
						curColor = '#BF4D28';
					  break;
					case 2: 
						curColor = '#E6AC27';
					  break;
					case 3:
						curColor = '#80BCA3';
					  break;
					case 4:
						curColor = '#655643';
					  break;
					case 5:
						curColor = '#4D7891';
					  break;
					case 5:
						curColor = '#548024';
					  break;
					default: 
						curColor = '#3D90AA';
				}
		    }
			$event.css('backgroundColor', curColor);
			/*
			switch(calEvent.id) {
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
				default: 
				  $event.css('backgroundColor', '#3D90AA');
			}
			*/
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
						if ((start.getDay() == 0) || (start.getDay() == 6)) return;
						$('#scheduleForm #blockDay').val(start.getDay());
						$('#scheduleForm #blockStart').val(startTime);
						$('#scheduleForm #blockEnd').val(endTime);

						$('#scheduleForm').submit();
						// $calendar.weekCalendar("removeUnsavedEvents");
						//$calendar.weekCalendar("updateEvent", calEvent);
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
					   $dialogContent.find('span').remove();
					  $dialogContent.dialog("close");
				   },
				   cancel : function() {
					  $dialogContent.find('span').remove();
					  $dialogContent.dialog("close");
				   }
				}
			}).show();
			if (calEvent.title != "BLOCK") {
				$dialogContent.append("<span>"+calEvent.name+"</span>");
			}
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
	
	
	// On thumb click, show enlargened calendar
	$('.horWrapper .content div').click(function (e) {
		var idName = $( this ).attr("id");
		var mySplitResult = idName.split("_");
		var id = mySplitResult[1];
		$('#data_source').val(id);
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
});


</script>