<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Services</title>
</head>
<body>

	<%@ page import="com.google.appengine.api.users.UserService"%>
	<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
	<%@ page import="com.google.appengine.api.users.User"%>



	<h4>Grant access to the following services:</h4>


	<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null) {
	%>

	<a href="<%=userService.createLoginURL(request.getRequestURL()
						.toString())%>">Login</a>


	<%
		} else {
			
			if (session.getAttribute("isAuthenticated") == null) {
				session.setAttribute("isAuthenticated", true);
				
				System.out.println("store or update user");
			}

			
	%>

	Welcome [<%=user.getNickname() %>]

	<ul>
		<li><a href="https://www.fitbit.com/oauth2/authorize?response_type=code&scope=activity+heartrate+profile&client_id=229NGX&redirect_uri=http://localhost:8080/oauth/fitbit">Fitbit</a></li>
		<li>RescueTime</li>
	</ul>

	<%
		}
	%>

</body>
</html>