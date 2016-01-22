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

<body>

  <div class="login-card">
  	<img src="/chs/resources/images/imgres.png" alt="IIITD Logo" width="165" height="85" border="0">
    <h1>CHS Log-in</h1><br>
  
  
  	<form:form method="post" action="/chs/dashboard/">
  		<!--  <div th:if="${param.error}" class="alert alert-error">    
                    Invalid username and password.
                </div>
                <div th:if="${param.logout}" class="alert alert-success"> 
                    You have been logged out.
        </div> -->
	    <input type="text" name="username" placeholder="Username or Email">
	    <input type="password" name="pass" placeholder="Password">
	    <input type="submit" class="login login-submit" value="login">
	</form:form>


  <div class="login-help">
    <a href="/chs/add">Register</a> &nbsp <a href="#">Forgot Password</a>
  </div>
</div>

<!-- <div id="error"><img src="https://dl.dropboxusercontent.com/u/23299152/Delete-icon.png" /> Your caps-lock is on.</div> -->

  <script src='http://codepen.io/assets/libs/fullpage/jquery_and_jqueryui.js'></script>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</body>

</html>