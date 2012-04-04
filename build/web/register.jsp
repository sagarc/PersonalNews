<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*" %>
<%@page import="javax.servlet.*" %>
<%@page import="collector.*" %>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <%@include file='header.jsp'%>
        <script type="text/javascript" src="jquery.json-2.3.js"></script>
        <script type="text/javascript" src="jquery.validate.js"></script>
        
        <script type="text/javascript">
            SubmittingForm=function() {
                
                $.post("SessionManager", {
                  type: "validate",
                  username: $('#username').val(),
                  emailid: $('#emailid').val(),
                  passwd: $('#passwd').val(),
                  location: $('#location').val(),
                  fullname: $('#fullname').val()
                },
                function(data, textStatus) {
                    // Assuming it returns the value 1 on valid user_name
                    if(data != "1"){ // if it reaches here, then some error in form input
                        $("#success").html("Oops! Username already in use, Please enter some other name");
                    }
                    else {	// valid username!
                        // REGISTRATION
                        // type = register	
                        $("#success").html("Processing request...");												
                        $.post("SessionManager", {
                           type: "register",
                           username: $('#username').val(),
                           emailid: $('#emailid').val(),
                           passwd: $('#passwd').val(),
                           location: $('#location').val(),
                           fullname: $('#fullname').val()
                        },
                        function(data, textStatus) {
                            if(data){
                                curr_id = data;
                                if (curr_id != "-1") {
                                    $("#success").html("Congrats! You have been registered successfully! Please login and update your preferences!");
                                }
                                else {
                                    $("#success").html("Due to some reason, registration was unsuccessful");
                                }
                            }
                            else {
                                $("#success").html("Due to some reason, registration was unsuccessful");
                                // Some Error
                            }
                        }, "html");
                        
                    }
                }, "html");
                
            }
            
            $(document).ready(function() {
                $("#loginform").validate({
                    submitHandler:function(form) {
                        validForm = 1;
                        //SubmittingForm();
                    },
                    rules: {
                        username: {
                            required: true
                        },		
                        emailid: {				
                            required: true,
                            email: true
                        },
                        fullname: {
                            required: true
                        },
                        passwd: {
                            required: true
                        },
                        location: {
                            required: true
                        }
                    }
                });
                
                $('#loginform').submit(function(){			
                    if (validForm == 1){
                        validForm = 0;
                        SubmittingForm();					
                    }
                    return false; // this will disallow the form to post any value
                });
            });	
        </script>
    </head>
    <body>
        <div id="wrapper">
            <%@include file='banner.jsp'%>
            
            <div id="page">
                <div id="page-bgtop">
                    <div id="page-bgbtm">
                        <div id="content">
                            <% if (session.getValue("userid") != null) { %>
                            <h4>Please logout first to register new user!</h4>
                            <% } else { %>
                            <h3>Register</h3>
                            <div class="form-div">
                        <form id="loginform" method="post" action="">
                           
                           <div class="form-row">
                              <span class="label">Username *</span>
                              <input type="text" name="username" id="username"/>
                           </div>  
                           
                           <div class="form-row">
                              <span class="label">Password *</span>
                              <input name="passwd" id="passwd" type="password"/>
                           </div>
                            
                           <div class="form-row">
                              <span class="label">Full Name *</span>
                              <input type="text" name="fullname" id="fullname"/>
                           </div>

                            <div class="form-row">
                              <span class="label">E-Mail *</span>
                              <input type="text" name="emailid" id="emailid"/>
                           </div>  		

                           <div class="form-row">
                              <span class="label">Location *</span>
                              <input name="location" id="location" type="text"/>
                           </div>

                           <div class="form-row">
                              <input class="submit" type="submit" value="Submit"/>			
                           </div>

                           <br/><br/><font color="#990000" size="2">* These fields are mandatory</font><br />
                        </form> 	
                        
                        <div id="success"></div>

                        <br></br>

                     </div>
                     
                        <% } %>
                        </div>
                        <%@include file='sidebar.jsp'%>
                        <!-- end #sidebar -->
                    </div>
                </div>
            </div>
            <!-- end #page -->
        </div>
        
        <%@include file='footer.jsp'%>
        <!-- end #footer -->
    </body>
</html>
