import java.util.*;
import java.lang.reflect.*;
import com.google.gson.*;

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
            
            String remoteMethodStr = jsonRequest.get("remoteMethod").getAsString();
            String objectNameStr = jsonRequest.get("objectName").getAsString();
            JsonObject params = jsonRequest.get("param").getAsJsonObject();
            ArrayList<String> paramsList = new ArrayList<>();

            for(Map.Entry<String, JsonElement> p : params.entrySet()){
                paramsList.add(p.getValue().getAsString());
            }

            // Invoke the method needed based ObjectName and methodName

            if(objectNameStr.equals("UserServices")){
                if(remoteMethodStr.equals("login")){
                    Class<?>[] paramTypes = {String.class, String.class};
                    Method loginMethod = object.getClass().getMethod(remoteMethodStr, paramTypes);
                    jsonReturn = (JsonObject) loginMethod.invoke(object, paramsList.get(0), paramsList.get(1));
                }
                if(remoteMethodStr.equals("signup")){
                    Class<?>[] paramTypes = {String.class, String.class, String.class};
                    Method signupMethod = object.getClass().getMethod(remoteMethodStr, paramTypes);
                    jsonReturn = (JsonObject) signupMethod.invoke(object, paramsList.get(0), paramsList.get(1), paramsList.get(2));
                }
                if(remoteMethodStr.equals("addPlaylist")){
                    Class<?>[] paramTypes = {String.class, String.class};
                    Method addPlaylistMethod = object.getClass().getMethod(remoteMethodStr, paramTypes);
                    jsonReturn = (JsonObject) addPlaylistMethod.invoke(object, paramsList.get(0), paramsList.get(1));
                }
                if(remoteMethodStr.equals("deletePlaylist")){
                    Class<?>[] paramTypes = {String.class, String.class};
                    Method removePlaylistMethod = object.getClass().getMethod(remoteMethodStr, paramTypes);
                    jsonReturn = (JsonObject) removePlaylistMethod.invoke(object, paramsList.get(0), paramsList.get(1));
                }
                if(remoteMethodStr.equals("searchSong")){
                    Class<?>[] paramTypes = {String.class};
                    Method searchSongMethod = object.getClass().getMethod(remoteMethodStr, paramTypes);
                    jsonReturn = (JsonObject) searchSongMethod.invoke(object, paramsList.get(0));
                }
            }

            if(objectNameStr.equals("SongServices")){
                if(remoteMethodStr.equals("playSong")){
                    Class<?>[] paramTypes = {String.class};
                    Method playSongMethod = object.getClass().getMethod(remoteMethodStr, paramTypes);
                    jsonReturn = (JsonObject) playSongMethod.invoke(object, paramsList.get(0));
                }
            }
        

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