
<html>
	<head>
		<style type="text/css">
			body { background: #2b2b2b; font-family: Lucida Grande; color: white; padding: 150px}
			a:hover, a:link, a:visited, a:active { color: white }
		</style>
	</head>
<body>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
	<%@ page import="web.*, java.util.*" %>
	<%
			// initialize or retrieve the userData
			if (session.getAttribute("userData") == null) {
				out.println("<h4><a href='login.jsp'>login with SUNET</a></h4>");
			    String redirectURL = "login.jsp";
			    response.sendRedirect(redirectURL);
			} else {
			    String redirectURL = "search.jsp";
			    response.sendRedirect(redirectURL);
			}
	%>
</body>
</html>