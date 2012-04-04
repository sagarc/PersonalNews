/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collector;

//import java.io.IOException;
//import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.List;

/**
 *
 * @author sagar , pararth
 */
@WebServlet(name = "PopulateTables", urlPatterns = {"/PopulateTables"})
public class PopulateTables extends HttpServlet {

    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("hi");    
        try {
            
            int mycount1 = 100, mycount2 = 100, mycount3 = 100;
            
            String cat = request.getParameter("c");
            String scat = request.getParameter("s");
            String ac = request.getParameter("ac");
            String as = request.getParameter("as");
            String ec = request.getParameter("ec");
            String es = request.getParameter("es");
            String c1 = request.getParameter("c1");
            String c2 = request.getParameter("c2");
            String c3 = request.getParameter("c3");
            
            if (c1 != null) mycount1 = Integer.parseInt(c1);
            if (c2 != null) mycount2 = Integer.parseInt(c2);
            if (c3 != null) mycount3 = Integer.parseInt(c3);
            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>PopulateTables</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PopulateTables at " + request.getContextPath () + "</h1>");
            
            if (cat != null) {
                List<FeedZilla.Category> mycategory = FeedZilla.getCategories();
                int count = mycount1;
                for (FeedZilla.Category curr : mycategory){
                    if (count == 0) break;
                    if (cat != null) {
                        DatabaseManager.Category c = new DatabaseManager.Category(curr);
                        DatabaseManager.addCategory(c);
                        out.println("Added category: " + curr.getString() + "<br>");
                    }
                    if (ac != null){
                        List<FeedZilla.NewsItem> newsitems = FeedZilla.getNewsItemsByCategory(curr.category_id);
                        for (FeedZilla.NewsItem news : newsitems){
                            if (count == 0) break;
                            DatabaseManager.NewsItem n = new DatabaseManager.NewsItem(news);
                            int news_id = DatabaseManager.addNewsItem(n);
                            if (news_id >= 0){
                                DatabaseManager.addNewsCategory(news_id, curr.category_id);
                                DatabaseManager.addNewsScores(news_id);
                                out.println("Added news item: " + news.title +
                                        " in category: " + curr.category_id + "<br>");
                                count--;
                            }
                        }
                    }
                }
                
            } else if (scat != null) {
                List<FeedZilla.Category> mycategory = FeedZilla.getCategories();
                int count1 = mycount1;
                for (FeedZilla.Category curr : mycategory){
                    out.println("<br>");
                    if (count1 == 0) break;
                    else count1--;
                    List<FeedZilla.Subcategory> mysubcategory = FeedZilla.getSubcategories(curr.category_id);
                    
                    int count2 = mycount2;
                    for (FeedZilla.Subcategory scurr : mysubcategory){
                        if (count2 == 0) break;
                        else count2--;
                        if (scat != null) {
                            out.println("Added subcategory:" + scurr.getString() + "<br>");
                            DatabaseManager.Subcategory s = new DatabaseManager.Subcategory(scurr);
                            DatabaseManager.addSubcategory(s);
                        }
                        if (as != null){
                            int count3 = mycount3;
                            List<FeedZilla.NewsItem> newsitems = FeedZilla.getNewsItemsBySubcategory(curr.category_id, scurr.subcategory_id);
                            for (FeedZilla.NewsItem news : newsitems){
                                if (count3 == 0) break;
                                else count3--;
                                DatabaseManager.NewsItem n = new DatabaseManager.NewsItem(news);
                                int news_id = DatabaseManager.addNewsItem(n);
                                if (news_id >= 0){
                                    DatabaseManager.addNewsSubcategory(news_id, scurr.subcategory_id);
                                    DatabaseManager.addNewsCategory(news_id, curr.category_id);
                                    DatabaseManager.addNewsScores(news_id);
                                    out.println("Added news item: " + news.title + " in subcategory: " +
                                            scurr.subcategory_id + "<br>");
                                    count3--;
                                }
                            }
                        }
                    }
                }
                
            } else if (ac != null) {
                List<DatabaseManager.Category> mycategory = DatabaseManager.getCategory();
                int count = mycount1;
                for (DatabaseManager.Category curr : mycategory){
                    if (count == 0) break;
                    else count--;
                    if (ac != null){
                        List<FeedZilla.NewsItem> newsitems = FeedZilla.getNewsItemsByCategory(curr.category_id);
                        int count2 = mycount2;
                        for (FeedZilla.NewsItem news : newsitems){
                            if (count2 == 0) break;
                            else count2--;
                            DatabaseManager.NewsItem n = new DatabaseManager.NewsItem(news);
                            int news_id = DatabaseManager.addNewsItem(n);
                            if (news_id >= 0){
                                DatabaseManager.addNewsCategory(news_id, curr.category_id);
                                DatabaseManager.addNewsScores(news_id);
                                out.println("Added news item: " + news.title +
                                        " in category: " + curr.category_id + "<br>");
                            }
                        }
                    }
                }
                
            } else if (as != null) {
                List<DatabaseManager.Category> mycategory = DatabaseManager.getCategory();
                int count1 = mycount1;
                for (DatabaseManager.Category curr : mycategory){
                    out.println("<br>");
                    if (count1 == 0) break;
                    else count1--;
                    List<DatabaseManager.Subcategory> mysubcategory = DatabaseManager.getSubcategory(curr.category_id);
                    
                    int count2 = mycount2;
                    for (DatabaseManager.Subcategory scurr : mysubcategory){
                        if (count2 == 0) break;
                        else count2--;
                        if (as != null){
                            int count3 = mycount3;
                            List<FeedZilla.NewsItem> newsitems = FeedZilla.getNewsItemsBySubcategory(curr.category_id, scurr.subcategory_id);
                            for (FeedZilla.NewsItem news : newsitems){
                                if (count3 == 0) break;
                                else count3--;
                                DatabaseManager.NewsItem n = new DatabaseManager.NewsItem(news);
                                int news_id = DatabaseManager.addNewsItem(n);
                                if (news_id >= 0){
                                    DatabaseManager.addNewsSubcategory(news_id, scurr.subcategory_id);
                                    DatabaseManager.addNewsScores(news_id);
                                    out.println("Added news item: " + news.title + " in subcategory: " +
                                            scurr.subcategory_id + "<br>");
                                    count3--;
                                }
                            }
                        }
                    }
                }
            } else if (ec != null) {
                List<DatabaseManager.Category> mycategory = DatabaseManager.getCategory();
                for (DatabaseManager.Category curr : mycategory){
                    
                    List<Freebase.Entity> ans = Freebase.getEntities(curr.english_category_name);
                    for (Freebase.Entity e : ans){
                        DatabaseManager.FbEntity fbe = new DatabaseManager.FbEntity(e);
                        int fb_id = DatabaseManager.addFbEntity(fbe);
                        if (fb_id >= 0) {
                            DatabaseManager.addCategoryEntity(curr.category_id, fb_id);
                            out.println("Added fb entity: " + fb_id + " for " + curr.category_id + "<br>");
                        }
                    }
                }
                
            } else if (es != null) {
                List<DatabaseManager.Category> mycategory = DatabaseManager.getCategory();
                for (DatabaseManager.Category curr : mycategory){
                    List<DatabaseManager.Subcategory> mysubcategory = DatabaseManager.getSubcategory(curr.category_id);
                    
                    for (DatabaseManager.Subcategory scurr : mysubcategory){
                        List<Freebase.Entity> ans = Freebase.getEntities(scurr.english_subcategory_name);
                        for (Freebase.Entity e : ans){
                            DatabaseManager.FbEntity fbe = new DatabaseManager.FbEntity(e);
                            int fb_id = DatabaseManager.addFbEntity(fbe);
                            if (fb_id >= 0) {
                                DatabaseManager.addSubCategoryEntity(scurr.subcategory_id, fb_id);
                                out.println("Added fb entity: " + fb_id + " for " + scurr.subcategory_id + "<br>");
                            }
                        }
                    }
                }
                
            }
            
            
            out.println("</body>");
            out.println("</html>");
            
            
            
        } finally {
            out.close();
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {        
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
