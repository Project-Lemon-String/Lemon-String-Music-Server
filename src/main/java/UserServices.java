import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserServices{

    public UserServices(){}
    
    public JsonObject login(String username, String password) throws Exception{
        JsonObject result = new JsonObject();

        JsonObject userList = new JsonObject();
        String userFile = "users.json";

        // Open file
        JsonParser jsonParser = new JsonParser();
        try(FileReader fileReader = new FileReader((userFile));){
            userList = jsonParser.parse(fileReader).getAsJsonObject();
        }
        catch (Exception e){ //e.printStackTrace();
        }

        // Flags to keep track of failedLogin
        boolean failedLogin = false;
        JsonObject user;

        //Check if user exists and if password matches our users.json
        try {
            user = userList.get(username).getAsJsonObject();
            result.addProperty("userData", user.toString());
            if(user!=null){
                String checkPassword = user.get("password").getAsString();

                if(!(checkPassword.equals(password))){
                    failedLogin = true;
                }
            }
        }
        catch(Exception e){}


        // If login credentials were correct then we save it in our global variables
        if(!failedLogin && (!username.equals("") && !password.equals(""))){
            result.addProperty("ret", "success");
        }else{
            result.addProperty("ret", "failed");
        }

        return result;
    }
    
    public JsonObject signup(String username, String password, String email) throws Exception{
        JsonObject result = new JsonObject();
        //Create JSON and GSON objects
        JsonObject userList = new JsonObject();
        String userFile = "users.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Open file and gather list of users from users.json
        JsonParser jsonParser = new JsonParser();

        try(FileReader fileReader = new FileReader((userFile));){
            userList = jsonParser.parse(fileReader).getAsJsonObject();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Saving our email and pass in a JSONObject
        JsonObject userDetails = new JsonObject();
        userDetails.addProperty("email", email);
        userDetails.addProperty("password", password);
        
        // Create a playlist object in our current user to keep track of their playlists.
        JsonArray playlist = new JsonArray();
        userDetails.add("playlists", playlist);

        // User exists flag
        boolean userExists = false;

        //Check if user exists
        try {
            JsonObject user = userList.get(username).getAsJsonObject();
            userExists = true;
        }
        catch (NullPointerException e){}
        catch (Exception e){}

        // if user doesn't exist, don't add it and error out.
        // if user does exist, add user.
        if(!userExists){
            userList.add(username, userDetails);
            
            try (FileWriter fileWriter = new FileWriter(userFile)){
                gson.toJson(userList,fileWriter);
                result.addProperty("ret", "success");
            }
            catch(IOException e){
                //e.printStackTrace();
                System.out.println("File failed to write");
            }
            catch (Exception e){}
        }
        else{
            System.out.println("Failed to add user.");
            result.addProperty("ret", "failed");
        }
        //Add user to user.json
        return result;
    }
    
    public JsonObject addPlaylist(String username, String playlistName){
        JsonObject result = new JsonObject();

        JsonObject userList = new JsonObject();
        String userFile = "users.json";
        Gson gson = new Gson();

        // Open file
        JsonParser jsonParser = new JsonParser();
        try(FileReader fileReader = new FileReader((userFile));){
            userList = jsonParser.parse(fileReader).getAsJsonObject();
        }
        catch (Exception e){ //e.printStackTrace();
        }

        JsonObject tempUser;

        //Check if user exists and if password matches our users.json
        try {
            tempUser = userList.get(username).getAsJsonObject();
            JsonArray tempPlaylist = tempUser.get("playlists").getAsJsonArray();
            JsonElement newPlaylist = gson.toJsonTree(playlistName);
            tempPlaylist.add(newPlaylist);
            tempUser.addProperty("playlists", tempPlaylist.getAsString());
            result.addProperty("userData", tempUser.toString());
        }
        catch(Exception e){}

        //Write to file
        try (FileWriter fileWriter = new FileWriter(userFile)){
            gson.toJson(userList,fileWriter);
            result.addProperty("ret", "success");
        }
        catch(IOException e){
            //e.printStackTrace();
            System.out.println("File failed to write");
            result.addProperty("ret", "failed");
        }
        catch (Exception e){}

        return result;
    }

    public JsonObject deletePlaylist(String username, String playlistName){
        JsonObject result = new JsonObject();

        JsonObject userList = new JsonObject();
        String userFile = "users.json";
        Gson gson = new Gson();

        // Open file
        JsonParser jsonParser = new JsonParser();
        try(FileReader fileReader = new FileReader((userFile));){
            userList = jsonParser.parse(fileReader).getAsJsonObject();
        }
        catch (Exception e){ //e.printStackTrace();
        }

        JsonObject tempUser;

        //Check if user exists and if password matches our users.json
        try {
            tempUser = userList.get(username).getAsJsonObject();
            JsonArray tempPlaylist = tempUser.get("playlists").getAsJsonArray();
            JsonElement newPlaylist = gson.toJsonTree(playlistName);
            tempPlaylist.remove(newPlaylist);
            tempUser.addProperty("playlists", tempPlaylist.getAsString());
            result.addProperty("userData", tempUser.toString());
        }
        catch(Exception e){}

        //Write to file
        try (FileWriter fileWriter = new FileWriter(userFile)){
            gson.toJson(userList,fileWriter);
            result.addProperty("ret", "success");
        }
        catch(IOException e){
            //e.printStackTrace();
            System.out.println("File failed to write");
            result.addProperty("ret", "failed");
        }
        catch (Exception e){}

        return result;
    }

    public JsonObject searchSong(String key){
        JsonObject result = new JsonObject();
        Gson gson = new Gson();
        String keyword = key;

        JsonParser jsonParser =  new JsonParser();
        JsonArray musicList = new JsonArray();

        try(FileReader fileReader = new FileReader(("music.json"));){
            musicList = jsonParser.parse(fileReader).getAsJsonArray();
        }
        catch(FileNotFoundException e){}
        catch(IOException e){}

        ArrayList<JsonObject> matches = new ArrayList<>();

        for(int i = 0; i < musicList.size(); i++){
            JsonObject songInfo = musicList.get(i).getAsJsonObject();
            JsonObject artist = songInfo.get("artist").getAsJsonObject();
            JsonObject song = songInfo.get("song").getAsJsonObject();
            String artistName = artist.get("name").getAsString();
            String songTitle = song.get("title").getAsString();
            if(artistName.equalsIgnoreCase(keyword) || songTitle.equalsIgnoreCase(keyword)){
                matches.add(songInfo);
            }
        }

        ArrayList<String> finalMatches = new ArrayList<>();

        for(JsonObject el: matches){
            finalMatches.add(el.get("artist").getAsJsonObject().get("name").getAsString() + " - " + el.get("song").getAsJsonObject().get("title").getAsString());
        }

        System.out.println(finalMatches);

        result.add("finalMatches", gson.toJsonTree(finalMatches).getAsJsonArray());
        result.addProperty("ret", "success");

        return result;
    }
}
