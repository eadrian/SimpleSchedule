
	<nav>
		<article>
			<h1>T H E O D E N</h1>
			<h6>&#151; Stanford &#151;</h6>
		</article>
		<ul>
			<li>
				<form id="schedulePage" action="Servlet_Sched" method="post" style="display: none">
					<input type="submit" value="sub" />
				</form>
				<a href="" title="Schedule" onclick="$('#schedulePage').submit();return false">
				<img src="images/search.png" />Schedule
				</a>
			</li>
			<li>
				<a href="search.jsp" title="Search">
				<img src="images/search.png" />Search
				</a>
			</li>
			<!-- 
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
			-->
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
					<li id="MAJOR" class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span><input type="checkbox" checked />MAJOR</li>
				</ul>
				<h3>Major:</h3><b>Computer Science</b><br /><b>Track:</b>
				<%
				if (session.getAttribute("track") == null) out.println("Unset");
				else out.println(session.getAttribute("track"));
				%>
				<br />
				<h6>Change track:</h6>
				<select id="changeTrack" name="track">
					<option></option>
					<option>Systems</option>
					<option>Human Computer Interaction</option>
					<option>Artificial Intelligence</option>
					<option>Information</option>
					<option>Graphics</option>
					<option>Theory</option>
					<option>Unspecialized</option>
					<option>Computer Engineering</option>
				</select>
				<button id=submitTrackChange type="submit" value="update" >Update</button>
				<script type="text/javascript">
					$("#submitTrackChange").click(function() {
						var track = $("#changeTrack").val();
						$("#changeTrack").load("Servlet_ChangeTrack", { "track" : track }, function() {
							if ($('#curPage').val() == "search") window.location.reload();
							else {
								$('#schedulePage').submit();
								return false;
							}
						});
					});
				</script>
			</div>
		</div>