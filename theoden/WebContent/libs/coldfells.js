var count = 2;



function blah() {
	////$('.course > span').show();
	$( this ).find('span').show();
	
}  
	
$(document).ready(function() {
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
		for (var i = 0; i < orderedFactors.length; i++) {
		   if (orderedFactors[i] == 'RELEVANCE') relevance = (i+1);
		   if (orderedFactors[i] == 'INTEREST') interest = (i+1);
		   if (orderedFactors[i] == 'LEVEL') level = (i+1);
		   if (orderedFactors[i] == 'WORK') work = (i+1);
		   if (orderedFactors[i] == 'POPULARITY') popularity = (i+1);
		   if (orderedFactors[i] == 'GERS') gers = (i+1);
		}

		// alert("relevance: " + relevance + " level:" + level + "project :" + project); 
		var query = $("#query").val();
		$("#search_results").load('Servlet_Search', {"query": query, "relevance": relevance, "interest": interest, "level": level, "work": work, "popularity":popularity, "gers":gers} );

	}
   $(function() {
		$( "#sortable" ).sortable({
			containment: '#stipulations',
		});
		$( "#sortable" ).disableSelection();
	});
	$('#query').keypress(function(e){
	  if(e.which == 13){
		submitQuery();
       }
    });
    $( "#submitSearch" ).click( function() {
		submitQuery();
   });
	
	$( "#sortable" ).sortable({
	   stop: function(event, ui) {
			submitQuery();
	   }
	});
});

/** CALENDAR **/

  var year = new Date().getFullYear();
  var month = new Date().getMonth();
  var day = new Date().getDate();

  /** store the data for each course schedule here **/
  var eventData2_small = {
    options: {
      timeslotsPerHour: 2,
      timeslotHeight: 2
    },
    events : [
       {'id':1, 'start': new Date(year, month, day, 12), 'end': new Date(year, month, day, 15, 30),'title':'CS103'},
       {'id':2, 'start': new Date(year, month, day - 1, 14), 'end': new Date(year, month, day - 1, 18, 45),'title':'CS107'},
       {'id':3, 'start': new Date(year, month, day + 1, 18), 'end': new Date(year, month, day + 1, 18, 45),'title':'CS109'},
       {'id':4, 'start': new Date(year, month, day - 2, 9), 'end': new Date(year, month, day - 2, 10, 30),'title':'CS147'},
       {'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'CS148'}
    ]
  };
  
  var eventData3_small = {
    options: {
      timeslotsPerHour: 2,
      timeslotHeight: 2
    },
    events : [
       {'id':1, 'start': new Date(year, month, day, 12), 'end': new Date(year, month, day, 15, 30),'title':'CS103'},
       {'id':2, 'start': new Date(year, month, day - 1, 14), 'end': new Date(year, month, day - 1, 18, 45),'title':'CS107'},
       {'id':3, 'start': new Date(year, month, day + 1, 18), 'end': new Date(year, month, day + 1, 18, 45),'title':'CS109'},
       {'id':4, 'start': new Date(year, month, day - 2, 9), 'end': new Date(year, month, day - 2, 10, 30),'title':'CS147'},
       {'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'CS148'}
    ]
  };


  var eventData1 = {
    options: {
      timeslotsPerHour: 2,
      timeslotHeight: 20
    },
    events : [
       {'id':1, 'start': new Date(year, month, day, 12), 'end': new Date(year, month, day, 15, 30),'title':'CS103'},
       {'id':2, 'start': new Date(year, month, day - 1, 14), 'end': new Date(year, month, day - 1, 18, 45),'title':'CS107'},
       {'id':3, 'start': new Date(year, month, day + 1, 18), 'end': new Date(year, month, day + 1, 18, 45),'title':'CS109'},
       {'id':4, 'start': new Date(year, month, day - 2, 9), 'end': new Date(year, month, day - 2, 10, 30),'title':'CS147'},
       {'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'CS148'}
    ]
  };

  var eventData2 = {
    options: {
      timeslotsPerHour: 2,
      timeslotHeight: 20
    },
    events : [
       {'id':1, 'start': new Date(year, month, day, 7), 'end': new Date(year, month, day, 13, 00),'title':'Lunch with Sarah'},
       {'id':2, 'start': new Date(year, month, day, 14), 'end': new Date(year, month, day, 14, 40),'title':'Team Meeting'},
       {'id':3, 'start': new Date(year, month, day - 2, 10), 'end': new Date(year, month, day - 2, 13, 40),'title':'Meet with Joe'},
       {'id':4, 'start': new Date(year, month, day - 1, 9), 'end': new Date(year, month, day - 1, 12, 20),'title':'Coffee with Alison'},
       {'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'Product showcase'}
    ]
  };


// data set 3 : using event delete features
  var eventData3 = {
    options: {
      timeslotsPerHour: 2,
      timeslotHeight: 20,
      allowEventDelete: true,
      eventDelete: function(calEvent, element, dayFreeBusyManager, calendar, clickEvent) {
        if (confirm('You want to delete this event?')) {
          calendar.weekCalendar('removeEvent',calEvent.id);
        }
      },
      deletable: function(calEvent, element) {
        return calEvent.start > Date.today();
      }
    },
    events : [
       {'id':1, 'start': new Date(year, month, day, 12), 'end': new Date(year, month, day, 13, 00),'title':'Lunch with Ashley'},
       {'id':2, 'start': new Date(year, month, day, 14), 'end': new Date(year, month, day, 14, 40),'title':'Team Picnic'},
       {'id':3, 'start': new Date(year, month, day + 1, 18), 'end': new Date(year, month, day + 1, 18, 40),'title':'Meet with Cathy'},
       {'id':4, 'start': new Date(year, month, day - 1, 8), 'end': new Date(year, month, day - 1, 9, 20),'title':'Coffee with Alyssa'},
       {'id':5, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'Product kickoff'}
    ]
  };
  
  /** Display **/
  function drawCalendarThumb(calendarID, eventData) {
	var $calendar = $(calendarID).weekCalendar({
		timeslotsPerHour: 2,
		scrollToHourMillis : 0,
		height: function($calendar){
			return $(window).height() - $('h1').outerHeight(true);
		},
		eventNew : function(calEvent, $event) {
			alert('You\'ve added a new event. You would capture this event, add the logic for creating a new event with your own fields, data and whatever backend persistence you require.');
		},
		data: function(start, end, callback) {
			var dataSource = $('#data_source').val();
			callback(eventData);
		}
	});
	}

  $(document).ready(function() {
	// set up the thumbs
	drawCalendarThumb('.calendar2', eventData2_small);
	drawCalendarThumb('.calendar3', eventData3_small);

	// set up main calendar
    var $calendar = $('#calendar').weekCalendar({
      timeslotsPerHour: 2,
      scrollToHourMillis : 0,
      height: function($calendar){
        return $(window).height() - $('h1').outerHeight(true);
      },      
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
        alert('You\'ve added a new event. You would capture this event, add the logic for creating a new event with your own fields, data and whatever backend persistence you require.');
      },
      data: function(start, end, callback) {
        var dataSource = $('#data_source').val();

        if (dataSource === '1') {
          callback(eventData1);
        } else if(dataSource === '2') {
          callback(eventData2);
        } else if(dataSource === '3') {
          callback(eventData3);
        } else {
          callback(eventData1);
        }
      }
    });
	
    $('#data_source').change(function() {
      $calendar.weekCalendar('refresh');
      updateMessage();
    });
	
	// On thumb click, show enlargened calendar
	$('.thumb').click(function() {

		var dataSource = $('#data_source').val("" + count);
		count++;
		if (count > 3) count = 1;
		$calendar.weekCalendar('refresh');
		updateMessage();
    });

    function updateMessage() {
      var dataSource = $('#data_source').val();
      $('#message').fadeOut(function(){
        if(dataSource === '1') {
          $('#message').text('Displaying event data set 1 with timeslots per hour of 4 and timeslot height of 20px');
        } else if(dataSource === '2') {
          $('#message').text('Displaying event data set 2 with timeslots per hour of 3 and timeslot height of 30px');
        } else if(dataSource === '3') {
          $('#message').text('Displaying event data set 3 with allowEventDelete enabled. Events before today will not be deletable. A confirmation dialog is opened when you delete an event.');
        } else {
          $('#message').text('Displaying no events.');
        }
        $(this).fadeIn();
      });
    }

    updateMessage();
  });
