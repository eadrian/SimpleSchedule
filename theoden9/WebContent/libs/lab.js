var count = 2;
/** CALENDAR **/

/** PUT DATA **/
var year = new Date().getFullYear();
var month = new Date().getMonth();
var day = new Date().getDate();
	
var allSchedules = [
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
	},
	{ 'id': 'calendar_2', 'dates': [
	   {'id':1, 'start': new Date(year, month, day, 12), 'end': new Date(year, month, day, 15, 30),'title':'Apple'},
	   {'id':2, 'start': new Date(year, month, day - 1, 14), 'end': new Date(year, month, day - 1, 18, 45),'title':'Apple'},
	   {'id':3, 'start': new Date(year, month, day + 1, 18), 'end': new Date(year, month, day + 1, 18, 45),'title':'Apple'},
	   {'id':4, 'start': new Date(year, month, day - 2, 9), 'end': new Date(year, month, day - 2, 10, 30),'title':'Apple'},
	   {'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'Apple'}
		]
	},
	{ 'id': 'calendar_3', 'dates': [
	   {'id':1, 'start': new Date(year, month, day, 12), 'end': new Date(year, month, day, 15, 30),'title':'Peach'},
	   {'id':2, 'start': new Date(year, month, day - 1, 14), 'end': new Date(year, month, day - 1, 18, 45),'title':'Peach'},
	   {'id':3, 'start': new Date(year, month, day + 1, 18), 'end': new Date(year, month, day + 1, 18, 45),'title':'Peach'},
	   {'id':4, 'start': new Date(year, month, day - 2, 9), 'end': new Date(year, month, day - 2, 10, 30),'title':'Peach'},
	   {'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'Peach'}
		]
	},
	{ 'id': 'calendar_4', 'dates': [
		{'id':1, 'start': new Date(year, month, day, 12), 'end': new Date(year, month, day, 15, 30),'title':'Lemon'},
		{'id':2, 'start': new Date(year, month, day, 14), 'end': new Date(year, month, day, 14, 40),'title':'Lemon'},
		{'id':3, 'start': new Date(year, month, day - 2, 10), 'end': new Date(year, month, day - 2, 13, 40),'title':'Lemon'},
		{'id':4, 'start': new Date(year, month, day - 1, 9), 'end': new Date(year, month, day - 1, 12, 20),'title':'Lemon'},
		{'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'Lemon'}
		]
	},
	{ 'id': 'calendar_5', 'dates': [
		{'id':1, 'start': new Date(year, month, day - 4, 12), 'end': new Date(year, month, day - 4, 15, 30),'title':'Strawberry'},
		{'id':2, 'start': new Date(year, month, day, 14), 'end': new Date(year, month, day, 14, 40),'title':'Strawberry'},
		{'id':3, 'start': new Date(year, month, day - 2, 10), 'end': new Date(year, month, day - 2, 13, 40),'title':'Strawberry'},
		{'id':4, 'start': new Date(year, month, day - 1, 9), 'end': new Date(year, month, day - 1, 12, 20),'title':'Strawberry'},
		{'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'Strawberry'}
		]
	}
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
        daysToShow: 5,
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
	$dialogContent.find("input").val("");
	$dialogContent.find("textarea").val("");
}

$(document).ready(function() {
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
		eventDelete: function(calEvent, $event) {
			alert("deleting");
		},
		resizable: function(calEvent, $event) {
			return false;
		},
		timeslotsPerHour: 2,
		timeslotHeight: 20,
        allowEventCreation: true,
		eventRender : function(calEvent, $event) {
			switch(calEvent.id) {
				case 1: 
				  $event.css('backgroundColor', '#E6AC27');
				  break;
				case 2: 
				  $event.css('backgroundColor', '#BF4D28');
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
				  $event.css('backgroundColor', '#E6AC27');
			}
		},
		eventNew : function(calEvent, $event) {
			//alert('You\'ve added a new event. You would capture this event, add the logic for creating a new event with your own fields, data and whatever backend persistence you require.');

			var $dialogContent = $("#event_edit_container");
			resetForm($dialogContent);
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
						/*
						calEvent.id = id;
						id++;
						calEvent.start = new Date(startField.val());
						calEvent.end = new Date(endField.val());
						calEvent.title = titleField.val();
						calEvent.body = bodyField.val();
						*/
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

			 $dialogContent.find(".date_holder").text($calendar.weekCalendar("formatDate", calEvent.start));
			 setupStartAndEndTimeFields(startField, endField, calEvent, $calendar.weekCalendar("getTimeslotTimes", calEvent.start));
		},
		eventClick : function(calEvent, $event) {

			if (calEvent.readOnly) {
				return;
			}

			var $dialogContent = $("#event_edit_container");
			resetForm($dialogContent);
			var startField = $dialogContent.find("select[name='start']").val(calEvent.start);
			var endField = $dialogContent.find("select[name='end']").val(calEvent.end);
			var titleField = $dialogContent.find("input[name='title']").val(calEvent.title);
			var bodyField = $dialogContent.find("textarea[name='body']");
			bodyField.val(calEvent.body);

			$dialogContent.dialog({
				modal: true,
				title: "Edit - " + calEvent.title,
				close: function() {
				   $dialogContent.dialog("destroy");
				   $dialogContent.hide();
				},
				buttons: {
				   save : function() {

					  calEvent.start = new Date(startField.val());
					  calEvent.end = new Date(endField.val());
					  calEvent.title = titleField.val();
					  calEvent.body = bodyField.val();

					  $calendar.weekCalendar("updateEvent", calEvent);
					  $dialogContent.dialog("close");
				   },
				   "delete" : function() {
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
	
	
	// On thumb click, show enlargened calendar
	$('.horWrapper .content div').click(function (e) {
		var idName = $( this ).attr("id");
		var mySplitResult = idName.split("_");
		var id = mySplitResult[1];
		$('#data_source').val(id);
		$calendar.weekCalendar('refresh');
    });
	
});
