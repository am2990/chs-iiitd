<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>
<%@ include file="./resources/js/js_css.jsp" %>

<p>Hello ${user.systemId}!</p>

<ul data-role="listview" data-autodividers="true" data-filter="true"
	data-inset="true">

	<c:forEach var="patient" items="${patients}">
		<li><a href="viewPatient.form?patient_id=${patient.id}">${patient.name}</a></li>
    </c:forEach>
</ul>


<%@ include file="/WEB-INF/template/footer.jsp"%>