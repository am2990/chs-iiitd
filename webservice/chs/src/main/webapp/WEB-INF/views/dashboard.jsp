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
    
    <script type="text/javascript">
	    $(document).ready(function() {
	        $('.subscribe').click(function() {
	            var val = $(this).attr("value");
	            var button = $(this);
	            var user = $('.user').attr("value");
	            $.ajax({
	                url : '/chs/dashboard/subscribe?topicName='+val,
	                type : "GET",
	                data :{
	                	"User" : user
	                },
	                success : function(data) {
	                    //$('#result').html(data);
	                    button.text(" Done !!!")
	            		button.toggleClass("glyphicon glyphicon-ok")
	                }
	            })
	       
	        });
	        $('.unsubscribe').click(function() {
	            var val = $(this).attr("value");
	            var button = $(this);
	            $.ajax({
	                url : '/chs/dashboard/unsubscribe/'+val,
	                type : "GET",
	                success : function(data) {
	                    //$('#result').html(data);
	                    button.text("Done !!!")
	            		button.toggleClass("glyphicon glyphicon-ok")
	                }
	            })
	       
	        });
	        
	        $('.delete').click(function() {
	            var val = $(this).attr("value");
	            var button = $(this);
	            $.ajax({
	                url : '/chs/dashboard/delete/'+val,
	                type : "GET",
	                success : function(data) {
	                    //$('#result').html(data);
	                    button.text("  Done !!!")
	            		button.toggleClass("glyphicon glyphicon-ok")
	            		button.attr('disabled','disabled');
	            	    //location.reload(true);

	                }
	            })
	       
	        });
	        $('.edit').click(function() {
	            var val = $(this).attr("value");
	            var button = $(this);

	           	window.location.href = '/chs/dashboard/edit/'+val;

	       
	        });
	        
	        $('.modalButton').click(function() {
	            $('#myModal').modal('show'); 
				button = $(this);
				topicname = button.attr("value")
				
	        });
			
			$('.change-role').click(function() {
					var role = $('.selectpicker').val();
					//TODO : change button value on success
					//button.text(role);
					var user = $('.user').attr("value");
					$.ajax({
	                url : '/chs/dashboard/modifyrole?topicName='+topicname,
	                type : "GET",
	                data :{
	                	"User" : user,
	                	"Role" : role
	                },
	                success : function(data) {
	                    //$('#result').html(data);
	                    $('#myModal').modal('hide'); 
	                    button.text(role)
	            		//button.toggleClass("glyphicon glyphicon-ok")
						
	                }
					
					})
				
			});
	        
	    });
	</script>
    

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

	<!-- Logged In User Info-->
	
	<input type="hidden" class="user" value="${user.id}">
	

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
      </div>
      <div class="modal-body">
        <select class="selectpicker">
    <option value="Publisher">Publisher</option>
    <option value="Subscriber">Subscriber</option>
    <option value="Both">Both</option>
    <option value="None">None</option>
  </select>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary change-role">Save changes</button>
      </div>
    </div>
  </div>
</div>
	
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
                <a class="navbar-brand" href="#" style="color: white;">Home</a>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1" align="justify">
            	
                
                    
                <ul class="nav navbar-nav">
                    <li>
                        <a href="/chs/dashboard/newtopic">New Topic</a>
                    </li>
                    <li>
                        <a href="#">Services</a>
                    </li>
                    <li >
                        <a href="#">Contact</a>
                    </li>
                   
                     <li>
                    	<a href="/chs/settings">Settings</a>
                    </li>
                                       
                    <li >
                        <a href="/chs">Log Out</a>
                    </li>
                    
                  
                </ul>
                
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container -->
    </nav>

    <!-- Page Content -->
    <div class="container">
			
		      
		   <div class="panel panel-default">
		    <table class="table table-hover">
		      <tbody>
		      <c:forEach items="${topicList}" var="tpl">
		        <tr>
		          <td>
		          	<span class="glyphicon glyphicon-chevron-right"></span>		          
		            ${tpl.topicName}
		          </td>
		          <td class="text-right text-nowrap">
		          	<button class="btn btn-success modalButton" value="${tpl.topicName}">Add Role</button>&nbsp
		            <button class="btn btn-warning edit" value="${tpl.id}">Edit Topic</button>&nbsp
		            <button type="button" class="btn btn-danger btn-default delete" value="${tpl.id}">
		            	<span class="glyphicon glyphicon-trash"></span>
		            </button>&nbsp 
		          </td>
		        </tr>
		        </c:forEach>
		      <c:forEach items="${subscribed}" var="stpl">
		        <tr>
		          <td>
		          	<span class="glyphicon glyphicon-chevron-right"></span>		          
		            ${stpl.topic.topicName}
		          </td>
		          <td class="text-right text-nowrap">
		          	<button class="btn btn-success modalButton" value="${stpl.topic.topicName}">Change Role</button>&nbsp
		            <button class="btn btn-warning edit">Edit Topic</button>&nbsp
		            <button type="button" class="btn btn-danger btn-default">
		            	<span class="glyphicon glyphicon-trash"></span>
		            </button>&nbsp 
		          </td>
		        </tr>
		        </c:forEach>
		      </tbody>
		    </table>
		    
		  </div>

        <!-- /.row -->

    </div>
    <!-- /.container -->
</body>


</html>


