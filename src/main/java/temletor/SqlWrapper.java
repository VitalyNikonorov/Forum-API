package temletor;

import org.json.JSONObject;

/**
 * Created by Виталий on 22.03.2015.
 */
public class SqlWrapper {
    public static String getInsertQuery(String table, String[] params, JSONObject jsonObj) {
        String response = "INSERT INTO " +table+ "( ";
        for (int i=0; i<params.length-1; i++){
            response = response +params[i]+ ", ";
        }
        response = response + params[params.length-1] +") VALUES (";
        for (int i=0; i<params.length-1; i++){
            response = response + "\'"+ jsonObj.get(params[i])+ "\', ";
        }
        response = response + "\'" + jsonObj.get(params[params.length-1]) +"\')";
        return response;
    }

}