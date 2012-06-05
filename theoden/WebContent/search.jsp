<jsp:include page="header.jsp">
  <jsp:param name="name" value="sos" />
</jsp:include>
<div style="border: 1px solid white; float: left; height: 100%; width: 700px;">

	<div id="search_calendar" style="border: 1px solid white; overflow: scroll; height: 40%">
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
			out.println("<ul>");
			for (String key : sortedScores.keySet()) {
				Course c = sortedScores.get(key);
				out.println("<li><strong>" + c.code + ": " + c.title + "</strong>");
				out.println("<i>" + c.lectureDays + "</i>");
				out.println("</li>");
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
	