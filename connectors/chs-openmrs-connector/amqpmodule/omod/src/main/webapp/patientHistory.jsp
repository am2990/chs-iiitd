<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<%@ include file="resources/js/js_css.jsp" %>

<script>
function display(){

var a;
a = document.getElementById('box').value;
$(list1).find("a:not(:contains(" + a + "))").parent().slideUp();
$(list1).find("a:contains(" + a + ")").parent().slideDown();
}


</script>
<div class="tabbale">
<%@ include file="template/localHeader.jsp"%>
	
<p>Hello ${user.systemId}!</br></br><b>Patients history List</b></p>

<div class="container">
      <div class="panel panel-default">
	  <input class="form-control" type="text" id="box" placeholder="search.." class="target" onkeyup="display()"/>
	      <ul class="nav nav-pills nav-stacked" id="list1"> 
		  <c:forEach var="patient" items="${patients}">
		      <li class="list-group-item">
			  <a href="viewPatient.form?patient_id=${patient.id}">
		      <h4 class="list-group-item-heading"><span id="span1">${patient.givenName}&nbsp;${patient.middleName}&nbsp;${patient.familyName}</span></h4>
              <p class="list-group-item-text">${patient.birthdate}...${patient.gender}...${patient.age}...</p>
		      </span>
		      </a>
			  </li>
          </c:forEach>
          </ul>
	 
    </div>
</div>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>