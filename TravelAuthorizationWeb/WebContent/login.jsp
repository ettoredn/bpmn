<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login</title>
</head>
<body>
	<form action="Authenticate" method="POST">
		<div><label for="username">Username: </label><input type="text" name="username" id="username" /></div>
		<div><label for="password">Password: </label><input type="text" name="password" id="password" /></div>
		<div><label for="role">Role: </label><input type="text" name="role" id="role" /></div>
		<div><input type="submit" value="Login" /></div>
	</form>
<%
	Boolean failure = (Boolean) request.getAttribute("failure");
	if (failure) {
%>
	<p style="color: red">Login failure: wrong username or password.</p>
<%
	}
%>
</body>
</html>