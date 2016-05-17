<%@ page language="java" import="java.util.*" pageEncoding="utf-8"
	isELIgnored="false"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML >
<html>
<head>
<base href="/userSystem">
<meta charset="utf-8">

<title>用户信息</title>
</head>

<body>

	<table border="1" style="width:200px; margin:80px auto; text-align:center">
		<tr>
			<td>${requestScope.user.id }</td>
		</tr>
		<tr>
			<td>${requestScope.user.name }</td>
		</tr>
		<tr>
			<td>${requestScope.user.birthday }</td>
		</tr>
		<tr>
			<td>${requestScope.user.gender }</td>
		</tr>
		<tr>
			<td>${requestScope.user.career }</td>
		</tr>
		<tr>
			<td>${requestScope.user.address }</td>
		</tr>
		<tr>
			<td>${requestScope.user.mobile }</td>
		</tr>
		<tr>
			<td><a href="javascript:void(0)" onclick="javascript:history.back()">返回</a></td>
		</tr>
	</table>
	<hr />
</body>
</html>
