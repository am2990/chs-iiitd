<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>
<%@ include file="resources/js/js_css.jsp"%>

<script>
function display(){

var a;
a = document.getElementById('box').value;
$(list1).find("a:not(:contains(" + a + "))").parent().slideUp();
$(list1).find("a:contains(" + a + ")").parent().slideDown();
}

</script>


<div class="tabbale">


	<p>
		Hello ${user.systemId}!</br>
		</br>
		<b>Patients List</b>
	</p>

	<div class="container">
		<div class="panel panel-default">
			<input class="form-control" type="text" id="box"
				placeholder="search.." class="target" onkeyup="display()" />
			<ul class="nav nav-pills nav-stacked" id="list1">
				<c:forEach var="patient" items="${patients}">
					<li class="list-group-item"><a
						href="viewPatient.form?patient_id=${patient.id}">
							<h4 class="list-group-item-heading">
								<span id="span1">${patient.name}</span>
							</h4>
					</a></li>
				</c:forEach>
			</ul>

		</div>
	</div>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>