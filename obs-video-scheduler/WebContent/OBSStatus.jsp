<%@page import="util.OBSStatus"%>
<%@page import="util.OBSApi"%>
<%@page import="util.Config"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<%
	if (OBSStatus.get()) {
		out.println("Connected to OBS");
	} else {
		out.println("Not connected to OBS");
	}
%>
<br>
<br>
<%
	
	out.println("OBS: " + Config.getOBSHost());
%>
<br>
<br>
<a href="/settings.jsp"> settings </a>
</html>