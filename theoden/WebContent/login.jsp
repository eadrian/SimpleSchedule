
<% ServletContext sc = getServletContext();

// if came here to log out, log them out
String action = request.getParameter( "type" );
if ((action != null) && action.equals("logout")) {
	session.removeAttribute("userData");
}
%>
		
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN""http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>Simple Schedule</title>
	
	<!-- CSS -->
    <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Dosis:200,400,800">
    <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Open+Sans">
	<link rel='stylesheet' type='text/css' href='css/reset.css' />
	<link rel='stylesheet' type='text/css' href='css/coldfells.css' />					
	<link rel='stylesheet' type='text/css' href='css/jquery.weekcalendar.css' />	
	
	<!-- JQUERY CALLS -->
	<script type='text/javascript' src='libs/jquery-1.4.4.min.js'></script>
    <script type='text/javascript' src='libs/jquery-ui-1.8.11.custom.min.js'></script>
	  
	
</head>
<body> 
	<nav>
		<article>
			<h1>T H E O D E N</h1>
			<h6>&#151; Stanford &#151;</h6>
		</article>
	</nav>
	<div id="content">
	<div id="loginWrapper">
		<h3>Log in</h3>
		<form id="loginForm">
   		<p>Enter your SUNetID and password: </p>
		<input type="input" id="name" size="40" placeholder="username" /><br />
		<input type="password" id="password" size="40" placeholder="password" /><br />
		<div id="stage"></div>
		<input type="button" id="driver" value="Login" />
		</form>
		<!--  <input type="button" id="logout" value="logout" /> -->
		<script type="text/javascript">
			function loginLoad(event)  {
			    var username = $("#name").val();
			    var password = $("#password").val();
			    $("#stage").ajaxStart(function() {
			        $('body').addClass("loading"); 
			    });
			    $("#stage").ajaxStop(function() {
			        $('body').removeClass("loading"); 
					window.location.href = "search.jsp";
				});
			    $("#stage").load('Servlet_Login', {"username": username, "password": password, "action": "login"} );
	
			};
			$(document).ready(function() {
				$("#driver").click(function(event){
					loginLoad(event);
				});
				$('#password').keypress(function(e){
				      if(e.which == 13){

							loginLoad(event);
				       }
			      });
				/*
				$("#logout").click(function(event){
					$("#stage").load('Servlet_Login', {"action": "logout"} );
				});
				*/
	   		});
		</script>
	</div>
	<div class="modal"><!-- Place at bottom of page --></div>	
</body>
</html>
	