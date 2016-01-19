<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>
<%@ include file="resources/js/js_css.jsp" %>


<script type="text/javascript">
$(document).ready(function() {
	
	$('#sampleForm').submit(
		function(event) {
			var obs = $('#obs').val();
			var oid = $('#oid').val();
							
			var data = 'obs='
					+ encodeURIComponent(obs)+' oid='+ encodeURIComponent(oid);
			$.ajax({
				url : $("#sampleForm").attr("action"),
				data :{
                	"obs" : obs
                	"oid" : oid
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

<p>Patient Details</p>

	<form id="sampleForm" method="get" action="profile.form">
	 <div class="container">
	  
	    
		
		<p><b>${patient.name}</b> </p>
		
		
		<table class="table">
		<tr><td><b class="label label-info">Observation ID </b></td><td>${obs_id}</td><td><b class="label label-info">Allergies</b></td><td>${allergies}</td></tr>
		<tr><td><b class="label label-info">Date of Birth</b></td><td>${patient.dob}</td><td><b class="label label-info">Temperature</b></td><td>${temp}</td></tr>
		<tr><td><b class="label label-info">Gender<b></td><td>${patient.gender}</td><td><b class="label label-info">Sensor Name</b></td><td>${sensorname}</td></tr>
		<tr><td><b class="label label-info">UUID</b></td><td>${patient.uuid}</td><td><b class="label label-info">Readings</b></td><td>${readings}</td></tr>
		<tr><td><b class="label label-info">Date</b></td><td>new Date()</td><td><b class="label label-info">Blood Pressure</b></td><td></td></tr>

		<tr>
		<td><input class="form-control" type="text" name="obs" id="obs" placeholder="write priscription here..."cols="20" rows="20" style="width:400%; height:80px;"></td></tr>
		<tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td>
		<td><button class="btn-primary" type="submit" name="submit" align="right"><span class="glyphicon glyphicon-pencil"></span>Submit</button></tr>
		
		</table>
		<table>

		
		</table>
		
		
		
		
		
	<input type="hidden" name="oid" id="oid" value="${obs_id}"/></td>
	</form>
	</div>
	
</body>
</html>


<%@ include file="/WEB-INF/template/footer.jsp"%>