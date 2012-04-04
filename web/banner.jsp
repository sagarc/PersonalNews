<div id="header-wrapper">
            <div id="header">
               <div id="logo">
                  <h1>News Aggregator  </h1>                  
               </div>
               <div id="menu">
                  <ul>
                     <li><a id ="home" href="index.jsp">Home</a></li>                     
                     <li><a id ="profile" href="profile.jsp">Profile</a></li>
                     <li><a id ="login" href="login.jsp">
                             <% if (session.getValue("userid") == null) { %>
                             Login
                             <% } else { %>
                             Logout
                             <% } %>
                         </a></li>
                     <li><a id ="register" href="register.jsp">Register</a></li>
                     <li><a id ="contact" href="#">Contact</a></li>
                  </ul>
               </div>
            </div>
         </div>