<%@ page language="java" import="java.util.*" pageEncoding="utf-8"
	isELIgnored="false"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML >
<html>
<head>
<base href="/UserSystem/">
<meta charset="utf-8">

<title>用户信息更新</title>
</head>
<body>

	<form action="doupdate.action" method="post">
		<table border="1"
			style="width: 150px; margin: 80px auto; text-align: center">

			<tr>
				<td><font color="red">${requestScope.errorMsg}</font></td>
			</tr>
			<tr>
				<td><input type="hidden" name="id"
					value="${sessionScope.user.id }" />${sessionScope.user.id }</td>
			</tr>
			<tr>
				<td><input type="text" name="name"
					value="${sessionScope.user.name }" /></td>
			</tr>
			<tr>
				<td><input type="text" name="birthday"
					value=${sessionScope.user.birthday }></td>
			</tr>
			<tr>
				<td><select name="gender">
						<c:choose>
							<c:when test="${sessionScope.user.gender eq '男' }">
								<option selected>男</option>
								<option>女</option>
							</c:when>
							<c:otherwise>
								<option>男</option>
								<option selected>女</option>
							</c:otherwise>
						</c:choose>
				</select></td>
			</tr>
			<tr>
				<td><input type="text" name="career"
					value=${sessionScope.user.career }></td>
			</tr>
			<tr>
				<td><input type="text" name="address"
					value=${sessionScope.user.address }></td>
			</tr>
			<tr>
				<td><input type="text" name="mobile"
					value=${sessionScope.user.mobile }></td>
			</tr>
			<tr>
				<td><input type="submit" value="更新" /></td>
			</tr>
		</table>
	</form>
	<hr />

</body>
</html>