<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<li
		<c:if test='<%= request.getRequestURI().contains("/patientQueue") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/amqpmodule/patientQueue.form"><spring:message
				code="Patient Queue" /></a>
	</li>
	<li>
		<a
		href="${pageContext.request.contextPath}/module/amqpmodule/patientHistory.form"><spring:message
				code="Patient History" /></a>
	</li>
	
	<!-- Add further links here -->
</ul>
<h2>
	<!-- <spring:message code="amqpmodule.title" />  -->
</h2>
