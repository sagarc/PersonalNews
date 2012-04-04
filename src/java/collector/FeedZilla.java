/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collector;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 *
 * @author sagar
 */
public class FeedZilla {
    public class Culture {
        String country_code;
        String culture_code;
        String display_culture_name;
        String english_culture_name;
        String language_code;
        Culture(){}
        public String getString() {
            return String.format("country_code: %s", country_code);
        }
    }
    
    public class Category {
        int category_id;
        String display_category_name;
        String english_category_name;
        String url_category_name;
        Category(){}
        public String getString() {
            return String.format("category_id: %s, name: %s", category_id, english_category_name);
        }
    }
    
    public class Subcategory {
        int category_id;
        String display_subcategory_name;
        String english_subcategory_name;
        int subcategory_id;
        String url_subcategory_name;
        Subcategory(){}
        public String getString() {
            return String.format("category_id: %s, subcategory_id: %s, name: %s", category_id, subcategory_id, english_subcategory_name);
        }
    }
    
    public class Enclosure {
        public int length;
        public String media_type;
        public String uri;
        Enclosure(){}
    }
    
    public class NewsItem {
        public String author;
        public List<Enclosure> enclosures = new ArrayList<Enclosure>();
        public String publish_date;
        public String source;
        public String source_url;
        public String summary;
        public String title;
        public String url;
        NewsItem(){}
    }
    
    public class Articles {
        List<NewsItem> articles;
        Articles(){}
    }
    
    public static String scheme = "http";
    public static String host = "api.feedzilla.com";
    
    public static List<Category> getCategories() 
            throws java.io.IOException {
        String responseBody = FetchURL.getResponse(scheme,host,"/v1/categories.json","");
        
        Gson gson = new Gson();
        Type mycategoryType = new TypeToken<List<Category>>(){}.getType();
        List<Category> mycategory = gson.fromJson(responseBody, mycategoryType);
        return mycategory;
    }
    
    public static List<Subcategory> getSubcategories(int category_id) 
            throws java.io.IOException {
        String responseBody = FetchURL.getResponse(scheme,host,"/v1/categories/" + Integer.toString(category_id) 
                + "/subcategories.json","");
        
        Gson gson = new Gson();
        Type mysubcategoryType = new TypeToken<List<Subcategory>>(){}.getType();
        List<Subcategory> mysubcategory = gson.fromJson(responseBody, mysubcategoryType);
        return mysubcategory;
    }
    
    public static List<NewsItem> getNewsItems() 
            throws java.io.IOException {
        String responseBody = FetchURL.getResponse(scheme,host,"/v1/articles.json","");
        
        Gson gson = new Gson();
        Type myitemType = new TypeToken<Articles>(){}.getType();
        Articles mynewsitem = gson.fromJson(responseBody, myitemType);
        return mynewsitem.articles;
    }
    
    public static List<NewsItem> getNewsItemsByCategory(int category_id) 
            throws java.io.IOException {
        if (category_id <= 0) return getNewsItems();
        String responseBody = FetchURL.getResponse(scheme,host,"/v1/categories/" + Integer.toString(category_id) 
                + "/articles.json","");
        
        Gson gson = new Gson();
        Type myitemType = new TypeToken<Articles>(){}.getType();
        Articles mynewsitem = gson.fromJson(responseBody, myitemType);
        return mynewsitem.articles;
    }
    
    public static List<NewsItem> getNewsItemsBySubcategory(int category_id, int subcategory_id) 
            throws java.io.IOException {
        if (category_id <= 0) return getNewsItems();
        if (subcategory_id <= 0) return getNewsItemsByCategory(category_id);
        String responseBody = FetchURL.getResponse(scheme,host,"/v1/categories/" + Integer.toString(category_id) 
                + "/subcategories/" + Integer.toString(subcategory_id) + "/articles.json","");
        
        Gson gson = new Gson();
        Type myitemType = new TypeToken<Articles>(){}.getType();
        Articles mynewsitem = gson.fromJson(responseBody, myitemType);
        return mynewsitem.articles;
    }
}
