
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
                 else {													
                     $("#success").html("Process request...");
                    $.post("SessionManager", {
                       type: "update",
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
                             $("#success").html("Congrats! Your data has been updated successfully!");
                          }
                          else {
                             $("#success").html("Due to some reason, update was unsuccessful");
                          }
                       }
                       else {
                          $("#success").html("Due to some reason, update was unsuccessful");
                          // Some Error
                       }
                    }, "html");
                        
                 }
              }, "html");
                
           }
           $(document).ready(function() {
              var curr_id = "-1";
              var pref_count = 0;
                
              $('#add_keys').click(function(){		
                 var str = $('#pref_key').val();
                 if (str == null) return;
                 $.post("SessionManager", 
                 {
                    type: "pref",
                    pref_key: str
                 }, function(data, textStatus) {                												
                        if(data){ // if it reaches here, then display the received entitites
                            pref_count++;
                            var entities = $.parseJSON(data);
                            var items = [];
                            items.push("<p>");
                            $.each(entities, function(index, ent) {
                            var str = '<div class="pref_inst"><input class="chk_pref" type="checkbox" name="check" value="' + 
                                ent['mid'] + '" id="chk_' + pref_count + '_' + index +'" />';
                            var str2 = '<label for="chk_' + pref_count + '_' + index +'">' + ent['name']; 
                            if (ent['type_name']) str2 += ' ('+ ent['type_name'] +')';
                            str2 += '</label>';
                            items.push(str);
                            items.push(str2);
                            });
                            items.push("</p>");
                            $("#pref_list").append(items.join("\n"));
                        }
                        else {
                        // Some Error
                        }
                    }, "html");
                });
                
              $('#save_pref').click(function(){
                var str = [];
                $('input[type=checkbox]').each(function(){
                    if (this.checked) {
                        str.push($(this).val());
                    }
                });
                $("#success2").html("Processing request...");
                $.post("SessionManager", 
                {
                    type: "setpref",
                    mid: str.join(",")
                }, function(data, textStatus){
                    if (data){
                        $("#success2").html("Preferences saved successfully!");
                    } else {
                        $("#success2").html("Due to some reason, update was unsuccessful");
                    }
                }, "html");
              });
           });	
            
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
    
    
                                    
        
        <% if (session.getValue("userid") == null) { %>
        <h4>Please Login first</h4>
        <% } else { 
            Integer useridobj = Integer.parseInt(session.getValue("userid").toString());
            int userid = useridobj.intValue();
                                    
            List<String> userdetails = DatabaseManager.getUserDetails(userid);      
        %>
        <div class="form-div">
            <h3>User Details</h3>
            <div id="loginform">
                <br />
        <form id="loginform" method="post" action="">
              <div class="form-row"><span class="label">Username </span><% out.print(userdetails.get(1)); %><br>      
              <input type="hidden" name="username" id="username" value="<% out.print(userdetails.get(1)); %>" /></div>
              
              <div class="form-row"><span class="label">Fullname *</span>                                                                 
              <input type="text" name="fullname" class="textinput" id="fullname" value="<% out.print(userdetails.get(2)); %>" /></div>
              
              <div class="form-row"><span class="label">Password *</span>                                                                 
              <input type="password" name="passwd" class="textinput" id="passwd" value="<% out.print(userdetails.get(5)); %>" /></div>
              
              <div class="form-row"><span class="label">EmailID *</span>                                                                 
              <input type="text" name="emailid" class="textinput" id="emailid" value="<% out.print(userdetails.get(3)); %>" /></div>
              
              <div class="form-row"><span class="label">Location *</span>                                                                 
              <input type="text" name="location" class="textinput" id="location" value="<% out.print(userdetails.get(4)); %>" /></div>
              
              <div class="form-row"><input type="submit" class="textsubmit" value="Update" /></div>            
        </form> 	
            <div id="success"></div>    
            </div>
            <br></br> 
            
            <div id="pref">
                <h3>Preferences</h3>
                <div id="pref_list">
                    
                    <% 
                        List<DatabaseManager.FbEntity> fbe = DatabaseManager.getUserEntity(userid);
                        if (fbe != null) {
                        out.println("<p>");
                        for (int i = 0; i < fbe.size(); i++) {
                    %>
                    <div class="pref_inst">
                    <input class="chk_pref" type="checkbox" name="check" checked="true" value="<% out.print(fbe.get(i).mid); %>" id="chk_0_<% out.print(i); %>" />
                    <label for="chk_0_<% out.print(i); %>"><% out.print(fbe.get(i).name); %> 
                            <% if (fbe.get(i).type_name != null) out.print(fbe.get(i).type_name); %>
                    </label><br />
                    </div>
                    <%  }
                        out.println("</p>");
                      } %>
                    
                </div>
                <input name="preference" class="textinput" id="pref_key" type="text" width="30" style="margin-right: 20px" />
                <button type="button" class="textsubmit" id="add_keys">Add Keyword</button><br />
                <p>Type in a keyword/phrase relating to something that interests you. 
                    Out of the returned topics, choose which ones you like. Once you are done selecting, click save and start browsing!</p>
                <button type="button" class="textsubmit" id="save_pref">Save Preferences</button><br />
                <div id="success2"></div>   
            </div>
                                
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
