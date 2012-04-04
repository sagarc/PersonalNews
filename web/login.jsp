<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*" %>
<%@page import="javax.servlet.*" %>
<%@page import="collector.*" %>
<%
    if (session.getValue("userid") != null) {
        session.putValue("userid", null);
        response.sendRedirect("login.jsp");
    } else {
%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <%@include file='header.jsp'%>
        <script type="text/javascript">
     var req_param = 0;
      $(document).ready(function() {
          
         $("#login_button").click(function(){
            
            var params = "type=authenticate";
            params += "&username=" + $('#username').val();                        
            params += "&passwd=" + $('#password').val();
            
            $.post("SessionManager", params, function(data, textStatus) {
               if (data == "1"){
                  
                   $('#success').html('You have Logged in successfully!');
                   window.location.replace("index.jsp")
                   
               }
               else if(data == "0"){
                  $('#success').html('Invalid username or password. Try Again or Register');
                  
               }
            });
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
                            <h3>Login</h3>
                            UserName: <input type="text" id="username" /><br />
                            Password: <input type="password" id="password" />  
                            <button id="login_button">Login</button>
                            <div id="success"></div>
                            
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
<% } %>