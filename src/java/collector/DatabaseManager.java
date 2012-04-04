/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collector;

import java.io.*;
import java.util.* ;
import java.sql.*;
import java.lang.*;

/**
 *
 * @author Sagar, Siddhesh, Pararth
 */
public class DatabaseManager {
    
    // Classes representing primary entities in the database 
    
    public static class Category {
        public int category_id;
        public String display_category_name;
        public String english_category_name;
        public String url_category_name;
        Category(){}
        Category(FeedZilla.Category c){
            category_id = c.category_id;
            display_category_name = c.display_category_name;
            english_category_name = c.english_category_name;
            url_category_name = c.url_category_name;
        }
    };
    
    public static class Subcategory {
        public int category_id;
        public String display_subcategory_name;
        public String english_subcategory_name;
        public int subcategory_id;
        public String url_subcategory_name;
        Subcategory(){}
        Subcategory(FeedZilla.Subcategory s){
            category_id = s.category_id;
            display_subcategory_name = s.display_subcategory_name;
            english_subcategory_name = s.english_subcategory_name;
            subcategory_id = s.subcategory_id;
            url_subcategory_name = s.url_subcategory_name;
            
        }
    };
    
    public static class NewsItem {
        public int news_id;
        public String author;
        public String publish_date;
        public String source;
        public String source_url;
        public String summary;
        public String title;
        public String url;
        NewsItem(){}
        NewsItem(FeedZilla.NewsItem n){
            news_id = -1;
            author = n.author;
            publish_date = n.publish_date;
            source = n.source;
            source_url = n.source_url;
            summary = n.summary;
            title = n.title;
            url = n.url;
        }
    };
    
    public static class NewsUser{
        public String username;
        public String fullname;
        public String emailid;
        public String location;
        public String password;
        NewsUser(){}
    };
    
    public static class FbEntity {
        public int EntityId;
        public String name;
        public String mid;
        public String type_name;
        public String type_id;
        FbEntity(){}
        FbEntity(Freebase.Entity e){
            name = e.name;
            mid = e.mid;
            type_name = e.type_name;
            type_id = e.type_id;
        }
        public String getString() {
            return String.format("%s", name);
        }
    }
    
    public static double alpha = 0.5;
    public static double beta = 1000.0;
    public static double gamma = 10.0; 
    public static double totalhits = 1.0;
    public static double totalentities = 1.0;
    public static double totalUserCatScore=1.0;
    
    //------------------------------------------------------------------
    // Primitive functions for connecting to database and adding data
    
    private static Connection getConnection(){
        try {
            Class.forName ("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/newsagg","root","newpass");
        } catch (Exception e){
            System.out.print("Connection Exception occured! ");
            e.printStackTrace();
        }
        return null;
    }
    
    public static int AddData(String tablename,String columns,List data){
        if (data == null) return -1;
        try{
            Connection conn = DatabaseManager.getConnection();
            String pstring = "insert into "+ tablename + " " + columns + " values " + "(";
            for(int i = 0;i< data.size();i++){
                pstring = pstring + "?";
                if(i < data.size() - 1) pstring = pstring + ",";
            }
            pstring = pstring + ")";
            PreparedStatement pstmt = conn.prepareStatement(pstring);
            for(int i = 0;i<data.size();i++){
                pstmt.setObject(i+1,data.get(i));
            }
            pstmt.executeUpdate();
            conn.close();
            return 0;
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -1;
        }
    }
    
    public static int addCategory(Category c){
        if (c == null) return -1;
        try{
            String tablename = "Category";
            String columns = "(category_id,category_name,url)";
            List data = new ArrayList();
            data.add(c.category_id);
            data.add(c.english_category_name);
            data.add(c.url_category_name);
            return AddData(tablename,columns,data);
        }
        catch(Exception e){
            System.out.println("Exception occured!");
            e.printStackTrace();
            return -2;
        }
        
    }
    
    public static int addSubcategory(Subcategory c){
        if (c == null) return -1;
        try{
            String tablename = "Subcategory";
            String columns = "(category_id,subcategory_id,subcategory_name,url)";
            List data = new ArrayList();
            data.add(c.category_id);
            data.add(c.subcategory_id);
            data.add(c.english_subcategory_name);
            data.add(c.url_subcategory_name);
            return AddData(tablename,columns,data);
        }
        catch(Exception e){
            System.out.println("Exception occured!");
            e.printStackTrace();
            return -2;
        }
    }
    
    public static int addNewsItem(NewsItem a){
        if (a == null) return -1;
        try{
            int retid;
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt2 = conn.prepareStatement("select * from NewsItem where url = ?");
            pstmt2.setString(1, a.url);
            ResultSet rset2 = pstmt2.executeQuery();
            if(rset2.next()){
                retid = rset2.getInt("NewsId");
                pstmt2.close();
                conn.close();
                return retid;
            }
            else{
                pstmt2.close();
                String tablename = "NewsItem";
                String columns = "(NewsId,Heading,NewsDate,Summary,Author,Url,NewsSource,SourceUrl)";
                PreparedStatement pstmt = conn.prepareStatement("select max(NewsId) from NewsItem");
                ResultSet rs = pstmt.executeQuery();
                int id;
                if(rs.next()){
                    id = rs.getInt(1)+1;
                }
                else{
                    id = 1;
                }
                pstmt.close();
                conn.close();
                List data = new ArrayList();
                data.add(id);
                data.add(a.title);
                data.add(a.publish_date);
                data.add(a.summary);
                data.add(a.author);
                data.add(a.url);
                data.add(a.source);
                data.add(a.source_url);
                if(AddData(tablename,columns,data) == 0){
                    return id;
                }
                else return -1;
            }
            
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -2;
        }
    }
    
    public static int addNewsUser(NewsUser u){
        if (u == null) return -1;
        try{
            String tablename = "NewsUser";
            String columns = "(UserId,Username,Fullname,EmailId,Location,Password)";
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select max(UserId) from NewsUser");
            ResultSet rs = pstmt.executeQuery();
            int id;
            if(rs.next()){
                id = rs.getInt(1)+1;
            }
            else{
                id = 1;
            }
            pstmt.close();
            conn.close();
            List data = new ArrayList();
            data.add(id);
            data.add(u.username);
            data.add(u.fullname);
            data.add(u.emailid);
            data.add(u.location);
            data.add(u.password);
            AddData(tablename,columns,data);
            return id;
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -1;
        }
    }
    
    public static int addFbEntity(FbEntity fbe){
        if (fbe == null) return -1;
        try{
            int retid;
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt2 = conn.prepareStatement("select * from FbEntity where Mid like binary ?");
            pstmt2.setString(1, fbe.mid);
            ResultSet rset2 = pstmt2.executeQuery();
            if(rset2.next()){
                retid = rset2.getInt("FbEntityId");
                pstmt2.close();
                conn.close();
                return retid;
            }
            else{
            pstmt2.close();
            String tablename = "FbEntity";
            String columns = "(FbEntityId,Name,Mid,TypeName,TypeId)";
            PreparedStatement pstmt = conn.prepareStatement("select max(FbEntityId) from FbEntity");
            ResultSet rs = pstmt.executeQuery();
            int id;
            if(rs.next()){
                id = rs.getInt(1)+1;
            }
            else{
                id = 1;
            }
            pstmt.close();
            conn.close();
            List data = new ArrayList();
            data.add(id);
            data.add(fbe.name);
            data.add(fbe.mid);
            data.add(fbe.type_name);
            data.add(fbe.type_id);
            if(AddData(tablename,columns,data) == 0){
                return id;
            }
            else return -1;
        }
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -2;
        }
    }
    
    public static int addTempUserEntity(int userid, String name, String mid, Double score, String typename, String typeid){
        if (mid == null) return -1;
        try{
            String tablename = "TempUserEntity";
            String columns = "(UserId,Name,Mid,Score,TypeName,TypeId)";
            List data = new ArrayList();
            data.add(userid);
            data.add(name);
            data.add(mid);
            data.add(score);
            data.add(typename);
            data.add(typeid);
            return AddData(tablename,columns,data);
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -1;
        }
    }
    
    public static int addSelectedEntity(int userid,List<String> mid){
        if (mid == null) return -1;
        try{
            if (mid == null) return -1;
            int retval = 1;
            Connection conn = DatabaseManager.getConnection();
            for(int i = 0;i<mid.size();i++){
                PreparedStatement pstmtfb = conn.prepareStatement("select * from FbEntity WHERE Mid like binary ?");
                pstmtfb.setString(1,mid.get(i));
                ResultSet rsfb = pstmtfb.executeQuery();
                if(rsfb.next()){
                    int EntityId = rsfb.getInt("FbEntityId");
                    addUserEntity(userid,EntityId);
                }
                else{
                PreparedStatement pstmt = conn.prepareStatement("select * from TempUserEntity WHERE UserId = ? and "
                        + "Mid like binary ?");
                pstmt.setInt(1, userid);
                pstmt.setString(2,mid.get(i));
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()){
                    FbEntity tempent = new FbEntity();
                    tempent.mid = rs.getString("Mid");
                    tempent.name = rs.getString("Name");
                    tempent.type_id = rs.getString("TypeId");
                    tempent.type_name = rs.getString("TypeName");
                    int EntityId = addFbEntity(tempent);
                    if(EntityId <= 0){
                        retval = -1;
                    }
                    else addUserEntity(userid,EntityId);
                }
                else{
                    System.out.println("Not present in fbentity as well as tempuserentity!");
                    retval = -1;
                }
              pstmt.close();
              }
              pstmtfb.close();
            }
            PreparedStatement pstmt2 = conn.prepareStatement("delete from TempUserEntity where UserId = ?");
            pstmt2.setInt(1,userid);
            pstmt2.executeUpdate();
            pstmt2.close();
            conn.close();
            
            return retval;
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -1;
        }
    }
    
    public static int addNewsCategory(int newsid,int categoryid){
        try{
            String tablename = "NewsCategory";
            String columns = "(NewsId,CategoryId)";
            List data = new ArrayList();
            data.add(newsid);
            data.add(categoryid);
            return AddData(tablename,columns,data);
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -2;
        }
    }
    
    public static int addNewsSubcategory(int newsid,int subcategoryid){
        try{
            String tablename = "NewsSubcategory";
            String columns = "(NewsId,SubcategoryId)";
            List data = new ArrayList();
            data.add(newsid);
            data.add(subcategoryid);
            return AddData(tablename,columns,data);
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -2;
        }
    }
    
    public static int addUserEntity(int userid,int entityid){
        try{
            String tablename = "UserEntity";
            String columns = "(UserId,FbEntityId)";
            List data = new ArrayList();
            data.add(userid);
            data.add(entityid);
            return AddData(tablename,columns,data);
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -2;
        }
    }
    
    public static int addNewsEntity(int newsid,int entityid){
        try{
            String tablename = "NewsEntity";
            String columns = "(NewsId,FbEntityId)";
            List data = new ArrayList();
            data.add(newsid);
            data.add(entityid);
            return AddData(tablename,columns,data);
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -2;
        }
    }
    
    public static int addCategoryEntity(int category_id, int fbentityid){
        try {
            String tablename = "CategoryEntity";
            String columns = "(CategoryId,FbEntityId)";
            List data = new ArrayList();
            data.add(category_id);
            data.add(fbentityid);
            return AddData(tablename, columns, data);
        } catch (Exception e) {
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -2;
        }
    }
   
    public static int addSubCategoryEntity(int subcategory_id, int fbentityid){
        try {
            String tablename = "SubcategoryEntity";
            String columns = "(SubcategoryId,FbEntityId)";
            List data = new ArrayList();
            data.add(subcategory_id);
            data.add(fbentityid);
            return AddData(tablename, columns, data);
        } catch (Exception e) {
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -2;
        }
    }
    
    
    //------------------------------------------------------------------
    // Functions for managing user sessions
    
    public static int Authenticate(String userid,String passwd){
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT UserId, Password FROM NewsUser WHERE Username = ? ");
            pstmt.setString(1, userid);
            ResultSet rs = pstmt.executeQuery();
            String temp = null;
            int uid = -1;
            if (rs.next()) {
                temp = rs.getString("Password");
                uid = rs.getInt("UserId");
                System.out.println(temp);
                pstmt.close();
                conn.close();
            }
            else {
                pstmt.close();
                conn.close();
                return -1;
            }
            if (temp.compareTo(passwd) == 0) {
                return uid;
            }
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
        }
        return -1;
    }
    
    public static int existsUsername(String username){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * from NewsUser WHERE Username = ?");
            pstmt.setString(1, username);
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()) {
                pstmt.close();
                conn.close();
                return 1;
            }
            else {
                pstmt.close();
                conn.close();
                return 0;
            }
        }
        catch(Exception e){
            System.out.println("Exception occured!");
            e.printStackTrace();
            return -1;
        }
    }
    
    
    //------------------------------------------------------------------
    // Functions to obtain data
    
    public static List<Category> getCategory() {
        List<Category> res = new ArrayList<Category> ();
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Category");
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                Category temp = new Category();
                temp.category_id = rs.getInt("category_id");
                temp.english_category_name = rs.getString("category_name");
                temp.url_category_name = rs.getString("url");
                res.add(temp);
            }
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Exception occured!");
            e.printStackTrace();
        }
        return res;
    }
    
    public static List<Subcategory> getSubcategory(int category_id) {
        List<Subcategory> res = new ArrayList<Subcategory> ();
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Subcategory WHERE category_id = ?");
            pstmt.setInt(1, category_id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                Subcategory temp = new Subcategory();
                temp.category_id = rs.getInt("category_id");
                temp.english_subcategory_name = rs.getString("subcategory_name");
                temp.url_subcategory_name = rs.getString("url");
                temp.subcategory_id = rs.getInt("subcategory_id");
                res.add(temp);
            }
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Exception occured!");
            e.printStackTrace();
        }
        return res;
    }
    
    public static List<String> getUserDetails(int userid){
        List<String> ret = new ArrayList<String>();
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select * from NewsUser where UserId = ?");
            pstmt.setInt(1,userid);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            Integer temp = rs.getInt("UserId");
            ret.add(temp.toString());
            ret.add(rs.getString("Username"));
            ret.add(rs.getString("Fullname"));
            ret.add(rs.getString("EmailId"));
            ret.add(rs.getString("Location"));
            ret.add(rs.getString("Password"));
            pstmt.close();
            conn.close();
            return ret;
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return ret;
        }
    }
    
    public static List<FbEntity> getUserEntity(int userid){
        List<FbEntity> ret = new ArrayList<FbEntity>();
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select * from UserEntity where UserId = ?");
            pstmt.setInt(1,userid);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                int temp = rs.getInt("FbEntityId");
                PreparedStatement pstmt1 = conn.prepareStatement("select * from FbEntity where FbEntityId = ?");
                pstmt1.setInt(1,temp);
                ResultSet rs1 = pstmt1.executeQuery();
                if(rs1.next())
                {
                    FbEntity fbtemp = new FbEntity();
                    fbtemp.EntityId = rs1.getInt("FbEntityId");
                    fbtemp.name = rs1.getString("Name");
                    fbtemp.mid = rs1.getString("Mid");
                    fbtemp.type_name = rs1.getString("TypeName");
                    fbtemp.type_id = rs1.getString("TypeId");
                    ret.add(fbtemp);
                }
                pstmt1.close();
            }
            pstmt.close();
            conn.close();
            return ret;
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return null;
        }
    }
    
    
    //------------------------------------------------------------------
    // Functions for managing scores used in ranking news articles
    
    public static int updateUserNewsScore(int userid,int newsid){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmthits = conn.prepareStatement("select sum(hits) from Popularity");
            ResultSet rshits = pstmthits.executeQuery();
            if(rshits.next()){
                totalhits = rshits.getInt(1);
                if (totalhits == 0) totalhits = 1.0;
            }
            pstmthits.close();
            //finding total entites
            PreparedStatement pstmtentities = conn.prepareStatement("select count(*) from FbEntity");
            ResultSet rsent = pstmtentities.executeQuery();
            if(rsent.next()){
                totalentities = rsent.getInt(1);
                if (totalentities == 0) totalentities = 1.0;
            }
            pstmtentities.close();
            PreparedStatement pstmt = conn.prepareStatement("select count(distinct FbEntityId) from UserEntity where UserId = ? and "
                    + "FbEntityId in (select FbEntityId from CategoryEntity natural join NewsCategory where NewsId = ? "
                    + "union select FbEntityId from SubcategoryEntity natural join NewsSubcategory where NewsId = ?)");
            pstmt.setInt(1,userid);
            pstmt.setInt(2,newsid);
            pstmt.setInt(3,newsid);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int commonality = rs.getInt(1);
            String tablename = "UserNews";
            String columns = "(UserId,NewsId,Commonality,Score)";
            int hits = 0;
            PreparedStatement pstmt2 = conn.prepareStatement("select * from Popularity where NewsId = ?");
            pstmt2.setInt(1,newsid);
            ResultSet rs2 = pstmt2.executeQuery();
            if(rs2.next()){
                hits = rs2.getInt("hits");
            }
            pstmt2.close();
            double score = (commonality*alpha)/totalentities + (hits*beta)/totalhits; //calculate
            List data = new ArrayList();
            data.add(userid);
            data.add(newsid);
            data.add(commonality);
            data.add(score);
            pstmt.close();
            conn.close();
            return AddData(tablename,columns,data);
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -2;
        }
    }
    
    public static void updatePeriodicUserNewsScore(){
        try{
            Connection conn = DatabaseManager.getConnection();
            //finding total hits
            PreparedStatement pstmthits = conn.prepareStatement("select sum(hits) from Popularity");
            ResultSet rshits = pstmthits.executeQuery();
            if(rshits.next()){
                totalhits = rshits.getInt(1);
                if (totalhits == 0) totalhits = 1.0;
            }
            pstmthits.close();
            //finding total entites
            /*PreparedStatement pstmtentities = conn.prepareStatement("select count(*) from FbEntity");
             * ResultSet rsent = pstmtentities.executeQuery();
             * if(rsent.next()){
             * totalentities = rsent.getInt(1);
             * }
             * pstmtentities.close();
             */
            //finding total category score for a user
            PreparedStatement pstmtcat = conn.prepareStatement("select sum(Score) from UserCategoryScore group by UserId");
            ResultSet rscat = pstmtcat.executeQuery();
            if(rscat.next()){
                totalUserCatScore = rscat.getInt(1);
                if (totalUserCatScore == 0) totalUserCatScore = 1.0;
            }
            pstmtcat.close();
            PreparedStatement pstmt = conn.prepareStatement("select * from UserNews");
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()){
                double score;
                int hits = 0,usercategoryscore = 0;
                double commonality = rs.getDouble("Commonality");
                int newsid = rs.getInt("NewsId");
                int userid = rs.getInt("UserId");
                PreparedStatement pstmt2 = conn.prepareStatement("select * from Popularity where NewsId = ?");
                pstmt2.setInt(1,newsid);
                ResultSet rs2 = pstmt2.executeQuery();
                if(rs2.next()){
                    hits = rs2.getInt("hits");
                }
                pstmt2.close();
                PreparedStatement pstmt3 = conn.prepareStatement("select * from UserCategoryScore natural join NewsCategory "
                        + "where UserId = ? and NewsId = ?");
                pstmt3.setInt(1,userid);
                pstmt3.setInt(2,newsid);
                ResultSet rs3 = pstmt3.executeQuery();
                if(rs3.next()){
                    usercategoryscore = rs3.getInt("Score");
                }
                pstmt3.close();
                score = alpha * commonality + beta * hits/totalhits + gamma * usercategoryscore/totalUserCatScore;
                PreparedStatement pstmt4 = conn.prepareStatement("Update UserNews set Score = ? where "
                        + "UserId = ? and NewsId = ?");
                pstmt4.setDouble(1, score);
                pstmt4.setInt(2,userid);
                pstmt4.setInt(3,newsid);
            }
            pstmt.close();
            conn.close();
        }
        catch(Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
        }
    }  
    
    public static void addUserScores(int user_id){
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("delete from UserNews WHERE UserId = ?");
            pstmt.setInt(1, user_id);
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("select NewsId from NewsItem");
            ResultSet rset = pstmt.executeQuery();
            while(rset.next()){
                Integer n_id = rset.getInt("NewsId");
                updateUserNewsScore(user_id,n_id);
            }
            pstmt.close();
            conn.close();
        } catch (Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
        }
    }
    
    public static void addNewsScores(int news_id){
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("delete from UserNews WHERE NewsId = ?");
            pstmt.setInt(1, news_id);
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("select UserId from NewsUser");
            ResultSet rset = pstmt.executeQuery();
            while(rset.next()){
                Integer u_id = rset.getInt("NewsId");
                updateUserNewsScore(u_id,news_id);
            }
            pstmt.close();
            conn.close();
        } catch (Exception e){
            System.out.print("Exception occured! ");
            e.printStackTrace();
        }
    }
    
    public static void updatePopularity(int newsid){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select hits from Popularity where NewsId = ?");
            pstmt.setInt(1,newsid);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                int hits = rs.getInt("hits");
                pstmt = conn.prepareStatement("update Popularity set hits = ? where NewsId = ?" );
                pstmt.setInt(1,hits+1);
                pstmt.setInt(2,newsid);
                pstmt.executeUpdate();
            } else {
                pstmt = conn.prepareStatement("insert into Popularity(NewsId,hits) values(?,1)");
                pstmt.setInt(1,newsid);
                pstmt.executeUpdate();
            }
            pstmt.close();
            conn.close();
            
        }
        catch (Exception e) {
            System.out.println("Exception occured!");
            e.printStackTrace();
        }
    }
    
    public static int updateUserCategoryScore(int user_id, int news_id) {
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM NewsCategory WHERE NewsId = ?");
            pstmt.setInt(1, news_id);
            ResultSet rs = pstmt.executeQuery();
            
            int category_id;
            if (rs.next()) {
                category_id = rs.getInt("CategoryId");
                
                pstmt = conn.prepareStatement("SELECT * FROM UserCategoryScore WHERE CategoryId = ? AND UserId = ?");
                pstmt.setInt(1, category_id);
                pstmt.setInt(2, user_id);
                rs = pstmt.executeQuery();
                
                if(rs.next()){
                    int score = rs.getInt("Score");
                    score++;
                    pstmt = conn.prepareStatement("UPDATE UserCategoryScore SET Score = " + score + " WHERE WHERE CategoryId = ? AND UserId = ?");
                    pstmt.setInt(1, category_id);
                    pstmt.setInt(2, user_id);
                    pstmt.executeUpdate();
                    
                }
                else{
                    pstmt = conn.prepareStatement("INSERT INTO UserCategoryScore VALUES (?,?,?)");
                    pstmt.setInt(1, user_id);
                    pstmt.setInt(2, category_id);
                    pstmt.setInt(3, 1);
                    
                    pstmt.executeUpdate();
                    
                }
                
            }
            pstmt.close();
            conn.close();
            
            return 1;
        } catch (Exception e) {
            System.out.print("Exception occured! ");
            e.printStackTrace();
            return -2;
        }
    }
    
    //------------------------------------------------------------------
    // Functions for obtaining news to be displayed
    
    public static List<NewsItem> getNewsItemByCategory(int category_id) {
        List<NewsItem> res = new ArrayList<NewsItem> ();
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM NewsItem NATURAL JOIN NewsCategory WHERE CategoryId = ?");
            pstmt.setInt(1, category_id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                NewsItem temp = new NewsItem();
                temp.news_id = rs.getInt("NewsId");
                temp.author = rs.getString("Author");
                temp.publish_date = rs.getString("NewsDate");
                temp.source = rs.getString("NewsSource");
                temp.source_url = rs.getString("SourceUrl");
                temp.summary = rs.getString("Summary");
                temp.title = rs.getString("Heading");
                temp.url = rs.getString("Url");
                res.add(temp);
            }
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Exception occured!");
            e.printStackTrace();
        }
        return res;
    }
    
    public static List<NewsItem> getNewsItemBySubcategory(int subcategory_id) {
        List<NewsItem> res = new ArrayList<NewsItem> ();
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM NewsItem NATURAL JOIN NewsSubcategory WHERE SubcategoryId = ?");
            pstmt.setInt(1, subcategory_id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                NewsItem temp = new NewsItem();
                temp.news_id = rs.getInt("NewsId");
                temp.author = rs.getString("Author");
                temp.publish_date = rs.getString("NewsDate");
                temp.source = rs.getString("NewsSource");
                temp.source_url = rs.getString("SourceUrl");
                temp.summary = rs.getString("Summary");
                temp.title = rs.getString("Heading");
                temp.url = rs.getString("Url");
                res.add(temp);
            }
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Exception occured!");
            e.printStackTrace();
        }
        return res;
    }
        
    public static List<NewsItem> displayForUser(int user_id){
        List<NewsItem> res = new ArrayList<NewsItem> ();
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM NewsItem NATURAL JOIN UserNews WHERE UserId = ? ORDER BY Score DESC");
            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();
            int count=0;
            while(rs.next() && count < 20) {
                NewsItem temp = new NewsItem();
                temp.news_id = rs.getInt("NewsId");
                temp.author = rs.getString("Author");
                temp.publish_date = rs.getString("NewsDate");
                temp.source = rs.getString("NewsSource");
                temp.source_url = rs.getString("SourceUrl");
                temp.summary = rs.getString("Summary");
                temp.title = rs.getString("Heading");
                temp.url = rs.getString("Url");
                res.add(temp);
                count++;
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Exception occured!");
            e.printStackTrace();
        }
        return res;
    }
    
    public static List<NewsItem> displayForUserByCategory(int user_id, int category_id){
        List<NewsItem> res = new ArrayList<NewsItem> ();
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM NewsItem NATURAL JOIN UserNews NATURAL JOIN NewsCategory WHERE UserId = ? AND CategoryId = ? ORDER BY Score DESC");
            pstmt.setInt(1, user_id);
            pstmt.setInt(2, category_id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                NewsItem temp = new NewsItem();
                temp.news_id = rs.getInt("NewsId");
                temp.author = rs.getString("Author");
                temp.publish_date = rs.getString("NewsDate");
                temp.source = rs.getString("NewsSource");
                temp.source_url = rs.getString("SourceUrl");
                temp.summary = rs.getString("Summary");
                temp.title = rs.getString("Heading");
                temp.url = rs.getString("Url");
                res.add(temp);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Exception occured!");
            e.printStackTrace();
        }
        return res;
    }
    
    public static List<NewsItem> getTopNews(){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select * from Popularity right outer join NewsItem using (NewsId) order by hits desc");                        
            ResultSet rs = pstmt.executeQuery();            
            List TopNews = new ArrayList<NewsItem>();
            int count=0;
            while(rs.next() && count<20){                           
                NewsItem temp = new NewsItem();           
                temp.news_id = rs.getInt("NewsId");
                temp.author = rs.getString("Author");
                temp.publish_date = rs.getString("NewsDate");
                temp.source = rs.getString("NewsSource");
                temp.source_url = rs.getString("SourceUrl");
                temp.summary = rs.getString("Summary");
                temp.title = rs.getString("Heading");
                temp.url = rs.getString("Url");
                TopNews.add(temp);
                count++;               
            }
             pstmt.close();
             conn.close();
            return TopNews;            
        }
        catch (Exception e) {
            System.out.println("Exception occured!");
            e.printStackTrace();
            return null;
        }        
    }
    
    public static List<NewsItem> searchNews(String searchString){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select * from NewsItem left outer join Popularity using (NewsId) where Heading like CONCAT('%', ? ,'%') order by hits desc");
            pstmt.setString(1,searchString);
            ResultSet rs = pstmt.executeQuery();
            List TopNews = new ArrayList<NewsItem>();
            int count=0;
            while(rs.next() && count<20){
                NewsItem temp = new NewsItem();
                temp.news_id = rs.getInt("NewsId");
                temp.author = rs.getString("Author");
                temp.publish_date = rs.getString("NewsDate");
                temp.source = rs.getString("NewsSource");
                temp.source_url = rs.getString("SourceUrl");
                temp.summary = rs.getString("Summary");
                temp.title = rs.getString("Heading");
                temp.url = rs.getString("Url");
                TopNews.add(temp);
                count++;
            }
            
            pstmt.close();
            conn.close();
            return TopNews;
        }
        catch (Exception e) {
            System.out.println("Exception occured!");
            e.printStackTrace();
            return null;
        }
    }
    
    public static List<NewsItem> searchNewsForUser(String searchString, int userid){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select * from NewsItem join UserNews using (NewsId) where UserNews.UserId = ? and Heading like CONCAT('%', ? ,'%') order by score desc");
            pstmt.setInt(1,userid);
            pstmt.setString(2,searchString);
            ResultSet rs = pstmt.executeQuery();
            List TopNews = new ArrayList<NewsItem>();
            int count=0;
            while(rs.next() && count<20){
                NewsItem temp = new NewsItem();
                temp.news_id = rs.getInt("NewsId");
                temp.author = rs.getString("Author");
                temp.publish_date = rs.getString("NewsDate");
                temp.source = rs.getString("NewsSource");
                temp.source_url = rs.getString("SourceUrl");
                temp.summary = rs.getString("Summary");
                temp.title = rs.getString("Heading");
                temp.url = rs.getString("Url");
                TopNews.add(temp);
                count++;
            }
            
            pstmt.close();
            conn.close();
            return TopNews;
        }
        catch (Exception e) {
            System.out.println("Exception occured!");
            e.printStackTrace();
            return null;
        }
    }
    
    //------------------------------------------------------------------
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //TODO code application logic here
        //AddData("newsitem","(News_id,Heading,Date,Time,Location,Summary)","(3,\'First\',null,null,\'India\',\'Nothing!\')");
        /*category c = new category();
        c.category_id = 1;
        c.display_category_name = "'cat1'";
        c.english_category_name = "'sports'";
        c.url_category_name = "'www.com'";
        AddCategory(c);*/
        
        /*subcategory c = new subcategory();
        c.category_id = 1;
        c.subcategory_id = 2;
        c.display_subcategory_name = "'subcat1'";
        c.english_subcategory_name = "'sports'";
        c.url_subcategory_name = "'www.com1'";
        AddSubcategory(c);
         * 
         */
        
        NewsUser u = new NewsUser();
        u.emailid = "arbit.com";
        u.fullname = "Sachin t.";
        u.location = "mumbai";
        u.password = "xyz";
        u.username = "sachin";
        addNewsUser(u);
        
    }
}



