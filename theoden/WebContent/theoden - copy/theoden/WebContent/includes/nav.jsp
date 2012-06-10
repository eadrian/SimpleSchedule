
	<nav>
		<article>
			<h1>Simple Schedule</h1>
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
				</ul>
				<h3>Major:</h3>Computer Science
			</div>
		</div>