<%@page import="java.util.List" %>
<%@page import="collector.DatabaseManager"%>
<div id="sidebar">
    <ul>
        <li>
            <div id="search" >
                <form method="get" action="displaynews.jsp">
                    <div>
                        <input type="text" name="search" class="textinput" value="" />
                        <input type="submit"  class="textsubmit" value="GO" />
                    </div>
                </form>
            </div>
            <div style="clear: both;"></div>
        </li>
        <li>
            <h2>About</h2>
            <p>We provide the latest news content collected from various sources over the internet, matching to your interests.<br /> 
                We work hard to give you a personalized experience.<br /> The more you use this site, the better it gets!</p>
        </li>
        
        <li>
            <h2>Categories</h2>
            <ul>
                
                <%
                    List<DatabaseManager.Category> mycat = DatabaseManager.getCategory();
                        for (DatabaseManager.Category mcat : mycat) {
                            int mycat_id = mcat.category_id;
                            String cat_name = mcat.english_category_name;
                            out.println("<li><a href=\"displaynews.jsp?category_id=" + mycat_id +"\">"+ cat_name + "</a></li>");
                        }
                %>                
            </ul>
        </li>
        
        
        
        <li>
            <h2>Affiliation</h2>
            <ul>
                <li><a href="http://www.cse.iitb.ac.in">CSE IIT-Bombay</a></li>                              
            </ul>
        </li>
    </ul>
</div>
<div style="clear: both;"></div>