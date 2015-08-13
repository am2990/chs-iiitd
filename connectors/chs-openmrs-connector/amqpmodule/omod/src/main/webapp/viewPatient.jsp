<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>
<%@ include file="./resources/js/js_css.jsp" %>

<script type="text/javascript">
$(document).ready(function() {
	$('#sampleForm').submit(
		function(event) {
			var obs = $('#obs').val();
			var pid = $('#pid').val();
							
			var data = 'obs='
					+ encodeURIComponent(obs)+' pid='+ encodeURIComponent(pid);
			$.ajax({
				url : $("#sampleForm").attr("action"),
				data :{
                	"obs" : obs
                	"pid" : pid
                },
				type : "GET",
 
				success : function(response) {
					//$(".patientQueue").jsp(data);
					alert( response );
				},
				error : function(xhr, status, error) {
					alert(xhr.responseText);
				}
			});
			return false;
		});
	});
</script>
 

</head>

<body>

<p>Hello ${user.systemId}!</p>
<p>${patient.name}</p>
<p>${patient.obs}</p>

	<form id="sampleForm" method="get" action="profile.form">
		<div>
			<input type="text" name="obs" id="obs">
		</div>
		<input type="hidden" name="pid" id="pid" value="${patient.id}"/>
		<div>
			<button type="submit" name="submit">Submit</button>
		</div>
	</form>
	
	
</body>
</html>


<%@ include file="/WEB-INF/template/footer.jsp"%>