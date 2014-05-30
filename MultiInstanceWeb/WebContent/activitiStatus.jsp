<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Activiti Status</title>
</head>
<body>
<%
	Collection processNames = (Collection) request.getAttribute("processNames");
	String jdbcUrl = (String) request.getAttribute("jdbcUrl");
%>
<h3>JDBC Url: <%=jdbcUrl %></h3>
</body>
</html>