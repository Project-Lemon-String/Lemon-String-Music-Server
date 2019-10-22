import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Dispatcher{

    HashMap<String, Object> ListOfObjects;

    public Dispatcher()
    {
        ListOfObjects = new HashMap<String, Object>();
    }

    public String dispatch(String request){
        //Register our potential objects that we may need based on request
        registerObject(new SongServices(), "SongServices");
        registerObject(new UserServices(), "UserServices");
        
        //Create our jsonObjects that we will return and our jsonify our request
        System.out.println(request + "\n");
        JsonObject jsonReturn = new JsonObject();
        JsonParser parser = new JsonParser();
        JsonObject jsonRequest = parser.parse(request).getAsJsonObject();

        try {
            // Obtains the object pointing to SongServices or UserServices
            Object object = ListOfObjects.get(jsonRequest.get("objectName").getAsString());
            Method[] methods = object.getClass().getMethods();
            Method method = null;

            for (int i=0; i<methods.length; i++){   
                if (methods[i].getName().equals(jsonRequest.get("remoteMethod").getAsString()))
                    method = methods[i];
            }

            Class[] types =  method.getParameterTypes();
            Object[] parameter = new Object[types.length];
            String[] strParam = new String[types.length];
            JsonObject jsonParam = jsonRequest.get("param").getAsJsonObject();

            int j = 0;
            int i = 0;
            for (Map.Entry<String, JsonElement>  entry  :  jsonParam.entrySet())
            {
                strParam[j++] = entry.getValue().getAsString();
                parameter[i++] = new String(strParam[j-1]);
            }

            jsonReturn = (JsonObject) method.invoke(object, parameter);
            // Invoke the method needed based ObjectName and methodName


        } catch (Exception e){
        //    Catch any errors that may occur and return it
            e.printStackTrace();
            jsonReturn.addProperty("error", "Error on " + jsonRequest.get("objectName").getAsString() + "." + jsonRequest.get("remoteMethod").getAsString());
        }
     
        return jsonReturn.toString();
    }

    public void registerObject(Object remoteMethod, String objectName)
    {
        ListOfObjects.put(objectName, remoteMethod);
    }
}