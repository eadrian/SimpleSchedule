var count = 2;

function blah() {
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
				$( this ).find('span').toggle();
			});
			$('#search_results li').hover(
				function (e) {
					alert("mousing over");
					var idName = $( this ).attr("id");
					var mySplitResult = idName.split("_");
					var id = mySplitResult[1];
					$('#data_source').val(id);
					var num = allSchedules[0]["dates"].length - 1;
					var lastitemID = allSchedules[0]["dates"][num]['id'];
					var newID = lastitemID + 1;
					alert(newID);
					var item = {'id':newID, 'start': new Date(year, month, day + 1, 14), 'end': new Date(year, month, day + 1, 15),'title':'Berry'};
					allSchedules[0]["dates"].push(item);
					$calendar.weekCalendar('refresh');
				}, function() {
					allSchedules[0]["dates"].pop();
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

