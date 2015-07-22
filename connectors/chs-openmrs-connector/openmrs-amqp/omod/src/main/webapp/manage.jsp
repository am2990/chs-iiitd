<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>
<html>

<head>
<TITLE>Crunchify - Spring MVC Example with AJAX call</TITLE>
 

<script type="text/javascript"
    src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('#sampleForm').submit(
		function(event) {
			var firstname = $('#firstname').val();
			var lastname = $('#lastname').val();				
			var data = 'firstname='
					+ encodeURIComponent(firstname)
					+ '&amp;lastname='
					+ encodeURIComponent(lastname);
			$.ajax({
				url : $("#sampleForm").attr("action"),
				data :{
                	"firstname" : firstname,
					"lastname" : lastname
                },
				type : "GET",
 
				success : function(response) {
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

	<form id="sampleForm" method="get" action="profile.form">
		<div>
			<input type="text" name="firstname" id="firstname">
		</div>
		<div>
			<input type="text" name="lastname" id="lastname">
		</div>
		<div>
			<button type="submit" name="submit">Submit</button>
		</div>
	</form>
	
	<p>Hello ${user.systemId}!</p>
</body>
</html>
<%@ include file="/WEB-INF/template/footer.jsp"%>