<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Activiti Status</title>
<style>
	a:link {color:blue;}      /* unvisited link */
	a:visited {color:blue;}  /* visited link */
	a:hover {color:blue;}  /* mouse over link */
	a:active {color:blue;}  /* selected link */
</style>
</head>
<body>
<%
	Collection<String> processNames = (Collection<String>) request.getAttribute("processNames");
	String result = (String) request.getAttribute("result"); result = (result == null ? "" : result);
%>

<h2 align="center"><%=result %></h2>

<form method="POST" action="">
	<input type="hidden" name="cmd" value="redeploy">
	<input type="submit" value="re-deploy">
</form>

<h4><div>Loaded processes</div>
<%
	for (String name : processNames) {
%>
	<form method="POST" action="">
		<input type="hidden" name="cmd" value="start">
		<input type="hidden" name="name" value="<%=name %>">
		<div style="margin-left: 40px;">
			<span style="color: red; margin-right: 10px;"><%=name %></span>
			<input type="submit" value="START"/>
		</div>
	</form>
<%
	}
%>
</h4>
</body>
</html>