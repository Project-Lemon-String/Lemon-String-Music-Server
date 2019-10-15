import java.io.*;
import com.google.gson.*;

public class SongServices{

    public SongServices(){}

    public JsonObject playSong(String songId) throws FileNotFoundException, IOException{
        JsonObject result = new JsonObject();

        PlaySong ps = new PlaySong();
        new Thread(ps).start();

        result.addProperty("ret", "success");

        return result;
    }
    
}
