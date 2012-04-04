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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author pararth , siddhesh, sagar
 */
public class Freebase {
    public static class Entity {
        String name;
        String mid;
        Double score;
        String type_name;
        String type_id;
        Entity(){}
        public String getString() {
            if (type_name != null)
                return String.format("{\"name\": \"%s\", \"mid\": \"%s\", \"type_name\": \"%s\"}", name, mid, type_name);
            else
                return String.format("{\"name\": \"%s\", \"mid\": \"%s\"}", name, mid);
        }
    }
    
    public static class Result {
        String status;
        List<Entity> result;
        int cost;
        int hits;
        Result(){}
        public String getString() {
            return String.format("%s", status);
        }
    }
    
    public static String scheme = "https";
    public static String host = "www.googleapis.com";
    public static String api_call = "/freebase/v1/search";
    
    public static Result getResult(String query) 
            throws java.io.IOException {
        String responseBody = FetchURL.getResponse(scheme,host,api_call,query);
        
        Gson gson = new Gson();
        Type myType = new TypeToken<Result>(){}.getType();
        Result myresult = gson.fromJson(responseBody, myType);
        return myresult;
        
        
    }
    
    public static String getEntitiesRaw(String term, int limit)
            throws java.io.IOException {
        String query = "query=" + term + "&limit=" + limit;
        String responseBody = FetchURL.getResponse(scheme,host,api_call,query);
        return responseBody;
    }
    
    public static String getEntitiesRaw(String term)
            throws java.io.IOException {
        return getEntitiesRaw(term,5);
    }
    
    public static List<Entity> getEntities(String term, int limit) 
            throws java.io.IOException {
        
        String responseBody = getEntitiesRaw(term,limit);
        
        List<Entity> ent = new ArrayList<Entity>();
        
        JSONParser parser = new JSONParser();
        
        try{
            JSONObject res = (JSONObject)parser.parse(responseBody);
            JSONArray entities = (JSONArray)res.get("result");
            for (int i = 0 ; i < entities.size(); i++){
                JSONObject e = (JSONObject) entities.get(i);
                Entity myent = new Entity();
                myent.name = (String)e.get("name");
                myent.mid = (String)e.get("mid");
                myent.score = (Double)e.get("score");
                JSONObject nt = (JSONObject)e.get("notable");
                if (nt != null){
                    myent.type_name = (String)nt.get("name");
                    myent.type_id = (String)nt.get("id");
                }
                ent.add(myent);
            }
        }
        catch(ParseException pe){
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }
        
        return ent;
    }
    
    public static List<Entity> getEntities(String term) 
            throws java.io.IOException {
        return getEntities(term,5);
    }
    
}
