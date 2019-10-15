import java.io.*;
import java.util.*;
import java.net.*;
import com.google.gson.*;

public class Server {

    HashMap<String, Object> ListOfObjects;
    
    public static void main(String[] args) { 
        try{
            //Attempt to create open connection
            CommunicationProtocol cp = new CommunicationProtocol();
            cp.openConn();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Opening connection failed.");
        }
    } 

}