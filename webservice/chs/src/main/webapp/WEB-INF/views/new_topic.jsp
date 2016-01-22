<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>CHS Dashboard</title>

	<script src="<c:url value="/resources/js/jquery.js" />"></script>
	<script src="<c:url value="/resources/js/bootstrap.js" />"></script>
  	<link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet">
	
	
    <!-- Custom CSS -->
    <style>
    body {
        padding-top: 70px;
        /* Required padding for .navbar-fixed-top. Remove if using .navbar-static-top. Change if height of navigation changes. */
    }
    </style>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

    <!-- Navigation -->
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            	
                
                    
                <ul class="nav navbar-nav">
                    <li>
                        <a href="/chs/dashboard">Home</a>
                    </li>
                    <li style="font-size: 150%; color: white;">
                        <a href="/chs/dashboard/newtopic">New Topic</a>
                    </li>
                    <li>
                        <a href="#">Services</a>
                    </li>
                    <li>
                        <a href="#">Contact</a>
                    </li>
                    
                    <li>
                        <a href="/chs/settings">Settings</a>
                    </li>
                    <li>
                    	<a href="/chs/search2">Search</a>
                    </li>
                   
                </ul>
                
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container -->
    </nav>

    <!-- Page Content -->
    
        <div class="container" >
    
	 <form:form class="form-horizontal" method="post" action="newtopic" commandName="topic">
		<fieldset>
		
		<!-- Form Name -->
		<legend>New Topc</legend>
		
		<!-- Text input-->
		<div class="control-group">
		  <label class="control-label" for="topicname">Name</label>
		  <div class="controls">
		    <input id="topicname" name="topicname" type="text" placeholder="topic name" class="input-large" required="">
		    
		  </div>
		</div>
		
		
		
		<!-- Select Multiple -->
		<div class="control-group">
		  <label class="control-label" for="topic_concept">Concepts</label>
		  <div class="controls">
		  	
		  <select id="concept_id" name="concept_id" class="input-xlarge" multiple="multiple">
		  <c:forEach items="${conceptList}" var="cpt">
		      <option>${cpt.conceptName }</option>
		    </c:forEach>
		  </select>    
		  </div>
		</div>
		
		<!-- Select Basic -->
		<div class="control-group">
		  <label class="control-label" for="topic_dissag">Dissagregation</label>
		  <div class="controls">
		  <select id="dissag" name="topic_dissag" class="input-xlarge">
		    <c:forEach items="${dissagList}" var="dsg">		    
		      <option>${dsg.dissagName }</option>
		    </c:forEach>  
		  </select>
		  </div>
		</div>
		
		<!-- Button (Double) -->
		<div class="control-group">
		  <label class="control-label" for="save"></label>
		  <div class="controls">
		    <button id="save" name="save" class="btn btn-info">Save</button>
		    <button id="button2id" name="button2id" class="btn btn-danger">Cancel</button>
		  </div>
		</div>
		
		</fieldset>
			
	</form:form>
    
    </div>
    <!-- /.container -->
</body>

</html>
