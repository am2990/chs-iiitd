<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>


<html>
    <head>
        <title>CHS</title>
    </head>
    <body>
    <h2>Registration Page</h2>
    <br>
    <form:form method="post" action="add" commandName="user">
        <table class="table">
        <tr>
            <td><form:label path="firstname"><spring:message code="label.firstname"/></form:label></td>
            <td><form:input path="firstname" class="form-control"/></td>
        </tr>
        <tr>
            <td><form:label path="lastname"><spring:message code="label.lastname"/></form:label></td>
            <td><form:input path="lastname" class="form-control"/></td>
        </tr>
        <tr>
            <td><form:label path="email"><spring:message code="label.email"/></form:label></td>
            <td><form:input path="email" class="form-control"/></td>
        </tr>
        <tr>
            <td><form:label path="password"><spring:message code="label.password"/></form:label></td>
            <td><form:input path="password" type="password" class="form-control"/></td>
        </tr>
        
        <tr>
            <td colspan="2">
                <input type="submit" class="btn btn-default" value="<spring:message code="label.add"/>"/>
                <a href="/chs" class="btn btn-danger">Cancel</a>
            </td>
        </tr>
    </table> 
    </form:form>
    <!-- 
    <h3>Users</h3>
    <c:if  test="${!empty userList}">
	    <table class="table table-striped">
    <tr>
        <th>Name</th>
        <th>Email</th>
        <th>Password</th>
        <th>Action</th>
    </tr>
    <c:forEach items="${userList}" var="usr">
        <tr>
            <td>${usr.lastname}, ${usr.firstname} </td>
            <td>${usr.email}</td>
            <td>${usr.password}</td>
            <td>delete</td>
        </tr>
    </c:forEach>
    </table>
    </c:if>
     -->
    </body>
</html>