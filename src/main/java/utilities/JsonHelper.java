package utilities;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;


/**
 * Created by Виталий on 31.05.2015.
 */
public class JsonHelper {

        static public JSONObject getJSONFromRequest(HttpServletRequest request, String Servlet) {
            StringBuffer jb = new StringBuffer();
            String line = null;
            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null)
                    jb.append(line);
            } catch (Exception e) {
                System.out.print("Error by parsing " +Servlet+ " request to JSON");
            }

            JSONObject jsonRequest = new JSONObject(jb.toString());

            return jsonRequest;
        }
}
