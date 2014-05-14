<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Manage Travel Request</title>
</head>
<body>
<%
	String description = (String) request.getAttribute("description");
	String pid = (String) request.getAttribute("pid");
	
	if (description != null && description.length() > 1) {
%>
		<h1><%=description %></h1>
		<form action="ManageTravelRequest?pid=<%=pid %>" method="POST">
		<label for="acceptRequest">Accept the request?</label>
		<input type="checkbox" id="acceptRequest" name="acceptRequest" /><br/>
		<label for="rejectMotivation">Motivation of rejection: </label>
		<input type="text" id="rejectMotivation" name="rejectMotivation" /><br/>
		<input type="submit" />
		</form>
<%
	} else {
%>
	<h1>No tasks for group management</h1>
<%
	}
%>
</body>
</html>