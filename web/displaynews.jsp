<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*" %>
<%@page import="java.util.*" %>
<%@page import="javax.servlet.*" %>
<%@page import="collector.*" %>
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
                            
                            <%                 
                            
                  int userid = -1;          
                  if (session.getValue("userid") != null)
                    userid = Integer.parseInt(session.getValue("userid").toString());
                            
                  String cat_id = request.getParameter("category_id");
                  String search_query = request.getParameter("search");
                  List<DatabaseManager.NewsItem> newsitems = null;
                  
                  if (cat_id != null) {
                                                        
                  int category_id = Integer.valueOf(cat_id);
                  out.println("<h4>Displaying news by category: " + category_id + "</h4>");
                  
                  if (userid == -1) newsitems = DatabaseManager.getNewsItemByCategory(category_id);
                  else newsitems = DatabaseManager.displayForUserByCategory(userid, category_id);
                  
                  } else if (search_query != null) {
                      if (userid == -1) newsitems = DatabaseManager.searchNews(search_query);
                      else newsitems = DatabaseManager.searchNewsForUser(search_query,userid);
                  }
                  
                  if (newsitems == null || newsitems.size() == 0){
                      out.println("<h4>No news found</h4>");
                  } else {
            
                  for (DatabaseManager.NewsItem curr : newsitems){ %>
                    <div class="post">
                        <h2 class="title"><a title="<% out.print(curr.news_id); %>" class="newshit" href="<% out.print(curr.url); %>"><% out.print(curr.title); %></a></h2>
                        <p class="meta">
                            <span class="date"><% out.print(curr.publish_date); %></span>
                            <span class="posted">Posted by <a href="<% out.print(curr.source_url); %>">
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
