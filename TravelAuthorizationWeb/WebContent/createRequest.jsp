<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Request Travel</title>
</head>
<body>
<form action="TravelRequest" method="POST">
<div><label for="name">Student name: </label><input id="name" name="name"></div>
<div><label for="email">Email</label><input id="email" name="email"/></div>
<div><label for="amount">Amount: </label><input id="amount" name="amount"></div>
<div><label for="motivation">Motivation: </label><input id="motivation" name="motivation"></div>
<input type="submit"/>
</form>
</body>
</html>