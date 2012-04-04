/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collector;

import java.util.List;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author pararth, sagar, siddhesh
 */
@WebServlet(name = "SessionManager", urlPatterns = {"/SessionManager"})
public class SessionManager extends HttpServlet {

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
        try {
            String type = request.getParameter("type");
            int userid = -1;
            HttpSession session = request.getSession(true);
            if (session.getValue("userid") != null) 
                userid = Integer.parseInt(session.getValue("userid").toString());
            
            if (type == null) return;
            
            if ("register".equals(type)){
                if (userid != -1) return;
                String username = request.getParameter("username");
                String fullname = request.getParameter("fullname");
                String emailid = request.getParameter("emailid");
                String passwd = request.getParameter("passwd");
                String location = request.getParameter("location");    
                
                DatabaseManager.NewsUser n = new DatabaseManager.NewsUser();
                n.username = username;
                n.fullname = fullname;
                n.emailid = emailid;
                n.location = location;
                n.password = passwd;
                
                int id = DatabaseManager.addNewsUser(n);
                if (id >= 0) DatabaseManager.addUserScores(id);
                out.print(id);
                
            } else if ("pref".equals(type)){
                String pref_key = request.getParameter("pref_key");
                System.out.println(pref_key);
                if (pref_key == null || userid == -1) return;
                List<Freebase.Entity> ans = Freebase.getEntities(pref_key);
                out.print("[");
                int first = 0;
                for (Freebase.Entity e : ans){
                    if (first == 0) first++;
                    else out.print(",");
                    out.print(e.getString());
                    System.out.println(e.getString());
                    DatabaseManager.addTempUserEntity(userid, 
                            e.name, e.mid, e.score, e.type_name, e.type_id);
                }
                out.print("]");
                
            } else if ("setpref".equals(type)) {
                String param = request.getParameter("mid");
                System.out.println("'" + param + "'");
                if (param == null || userid == -1) {
                    System.out.println("trivial return");
                    return;
                }
                List<String> mids = new ArrayList<String>();
                StringTokenizer st = new StringTokenizer(param,",");
                while (st.hasMoreTokens()) {
                    mids.add(st.nextToken());
                }
                int ret = DatabaseManager.addSelectedEntity(userid, mids);
                if (ret >= 0) DatabaseManager.addUserScores(userid);
                if (ret >= 0) out.print("1");
                
            } else if ("validate".equals(type)) {
                String username=request.getParameter("username");
                if (username == null || DatabaseManager.existsUsername(username) != 0){
                    out.print("0");
                }
                else {
                    out.print("1");
                }
                
            } else if("authenticate".equals(type)){
               String username=request.getParameter("username");
               String password=request.getParameter("passwd");
               if (username == null || password == null || userid != -1) return;
               Integer uid = DatabaseManager.Authenticate(username, password);
               System.out.println(uid);
               if (uid >= 0){                  
                  out.print("1");                 
                  session.putValue("userid", uid);
               }
               else {                  
                  out.print("0");                 
                  session.putValue("userid", null);
               }
            } else if("newshit".equals(type)){
               String newsid = request.getParameter("newsid");
               if (newsid == null) return;
               int n_id = Integer.parseInt(newsid);
               DatabaseManager.updatePopularity(n_id);
               if (userid == -1) return;
               DatabaseManager.updateUserCategoryScore(userid,n_id);
            }
           
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
