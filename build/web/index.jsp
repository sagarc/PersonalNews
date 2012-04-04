<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*" %>
<%@page import="javax.servlet.*" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <%@include file='header.jsp'%>
    </head>
    <body>
        <div id="wrapper">
            <%@include file='banner.jsp'%>
            <div id="page">
                <div id="page-bgtop">
                    <div id="page-bgbtm">
                        <div id="content">
                           <% List<DatabaseManager.NewsItem> newsitems = null;
                               if (session.getValue("userid") == null) { 
                               newsitems = DatabaseManager.getTopNews(); %>
            
                            <h4>Welcome to News Aggregator!</h4>
                            
                            <% } else { 
                               Integer useridobj = Integer.parseInt(session.getValue("userid").toString());
                                int userid = useridobj.intValue();
                                    
                                List<String> userdetails = DatabaseManager.getUserDetails(userid);
                                newsitems = DatabaseManager.displayForUser(Integer.parseInt(userdetails.get(0)));
                            %>
                            <h4>Welcome, <% out.print(userdetails.get(1)); %></h4>
                            <% } %>
                            
                            <% 
                            if (newsitems == null || newsitems.size() == 0){
                            out.println("<h4>No news found</h4>");
                            } else {

                              for (DatabaseManager.NewsItem curr : newsitems){ %>
                            
                    <div class="post">
                        <h2 class="title"><a title="<% out.print(curr.news_id); %>" class="newshit" href="<% out.print(curr.url); %>"><% out.print(curr.title); %></a></h2>
                        <p class="meta">
                            <span class="date"><% out.print(curr.publish_date); %></span>
                            <span class="posted">Posted by <a class="newshit" href="<% out.print(curr.source_url); %>">
                                    <% out.print(curr.author); %> at <% out.print(curr.source); %></a></span></p>
                        <div style="clear: both;">&nbsp;</div>
                        <div class="entry">
                            <p><% out.print(curr.summary); %></p>
                           <p class="links"><a title="<% out.print(curr.news_id); %>" class="newshit" href="<% out.print(curr.url); %>">Read More</a>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;<a href="<% out.print(curr.source_url); %>">Source</a></p>
                        </div>
                     </div>
                        
                            <% } 
                            } %>
                            
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
