<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>

<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>




<html>
<head>

  <meta charset="UTF-8">
  
<title>CHS - Log-in</title>
 <link href="<c:url value="/resources/css/jquery-ui.css" />" rel="stylesheet">
  <!-- <script src="<c:url value="/resources/js/jquery-ui.css" />"></script> -->
  <link href="<c:url value="/resources/css/style.css" />" rel="stylesheet">
</head>
<body onload='document.loginForm.username.focus();'>

	<h1> Login Form </h1>

 <div class="login-card">
  	<img src="/chs/resources/images/imgres.png" alt="IIITD Logo" width="165" height="85" border="0">
    <h1>CHS Log-in</h1><br>
 

	<div id="login-box">

		<h3>Login with Username and Password</h3>

		<c:if test="${not empty error}">
			<div class="error">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="msg">${msg}</div>
		</c:if>

		<form name='loginForm'
			action="<c:url value='/j_spring_security_check' />" method='POST'>

			<table>
				<tr>
					<td>User:</td>
					<td><input type='text' name='username'></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input type='password' name='password' /></td>
				</tr>
				<tr>
					<td colspan='2'><input name="submit" type="submit"
						value="submit" /></td>
				</tr>
			</table>

			<%-- <input type="hidden" name="${_csrf.parameterName}"
			 	value="${_csrf.token}" />--%>

		</form>

<div class="login-help">
    <a href="/chs/add">Register</a> &nbsp <a href="#">Forgot Password</a>
  </div>
	</div>
	</div>

</body>
</html>