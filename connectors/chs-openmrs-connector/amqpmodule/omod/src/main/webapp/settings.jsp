<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>
<%@ include file="./resources/js/js_css.jsp" %>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/amqpmodule/css/bootstrap.min.css" />

<!--<script type="text/javascript"  src="${pageContext.request.contextPath}/moduleResources/amqpmodule/js//bootstrap.js" ></script>-->
<script type="text/javascript"  src="${pageContext.request.contextPath}/moduleResources/amqpmodule/js//bootstrap.min.js" ></script>
<script type="text/javascript"  src="${pageContext.request.contextPath}/moduleResources/amqpmodule/js//jquery.js" ></script>

</head>

<body>

<p>Settings</p>

	<form id="sampleForm" method="get" action="savesettings.form">
	 <div class="container">
	 <table class="table">
		
		<tr>
		<td><b class="label label-info">Url</b></td>
		<td><input class="form-control" type="text" name="url" id="url" placeholder="url"cols="10" style="width:50%; height:3%;"></td>
		</tr>
		<tr>
		<td><b class="label label-info">Username</b></td>
		<td><input class="form-control" type="text" name="username" id="username" placeholder="url"cols="20" style="width:50%; height:3%;"></td>
		</tr>
		<tr>
		<td><b class="label label-info">password</b></td>
		<td><input class="form-control" type="text" name="password" id="password" placeholder="url"cols="20" style="width:50%; height:3%;"></td>
		</tr>
		<tr><td>&nbsp;</td><td><button class="btn-primary" type="submit" name="submit">OK</button></tr>
		</table>
		
		
		
	<input type="hidden" name="pid" id="pid" value="${patient.id}"/></td>
	</form>
	</div>
	
</body>
</html>


<%@ include file="/WEB-INF/template/footer.jsp"%>