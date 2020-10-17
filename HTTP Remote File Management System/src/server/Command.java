package server;
import java.io.*;
import java.util.*;

public class Command {
    private HashMap<String, String> header = new HashMap<>();

    public Command(){
        header.put("Access-Control-Allow-Origin:","*");
        header.put("Access-Control-Allow-Credentials:", "true");
        header.put("Server:", "127.0.0.1");
        header.put("Date:", (new Date()).toString());
        header.put("Content-Type:", "text/html");
        header.put("Connection:", "close");//keepalive
//        header.put("Content-Disposition:", "attachment;filename=\"abc.txt\"");
//        header.put("Content-Disposition:", "inline");
    }
    
    public Command(String servername){
        header.put("Access-Control-Allow-Origin:","*");
        header.put("Access-Control-Allow-Credentials:", "true");
        header.put("Server:", servername);
        header.put("Date:", (new Date()).toString());
        header.put("Content-Type:", "text/html");
        header.put("Connection:", "close");//keepalive
//        header.put("Content-Disposition:", "attachment;filename=\"abc.txt\"");
//        header.put("Content-Disposition:", "inline");
    }

    public HashMap<String, String> getHeader(){
        return header;
    }

    public void setHeader(String key, String value){
        header.put(key, value);
    }

}