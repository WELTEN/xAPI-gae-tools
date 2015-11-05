<%@ page import="nl.welteninstituut.tel.oauth.jdo.UserLoggedInManager"%>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.AccountJDO"%>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.AccountManager"%>
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


	<h1>Services4</h1>

	<%
		String userName = UserLoggedInManager.getUser(request.getParameter("accessToken"));
		AccountJDO account = AccountManager.getAccount(userName);
		
		if (account == null) {
	%>

		<h4>Niet ingelogd</h4>
		<a href="/login.jsp">login</a>
	<% 
		} else { 
	
	%>
		[<%= account.getName()%>]

		<h4>Grant access to the following services:</h4>



		<ul>
			<li><a
				href="https://www.fitbit.com/oauth2/authorize?response_type=code&scope=activity+heartrate+profile+settings&client_id=229NGX&redirect_uri=http://localhost:8080/oauth/fitbit&state=<%=request.getParameter("accessToken")%>">Fitbit</a></li>
			<li>RescueTime</li>
		</ul>
	<% } %>
</body>
</html>