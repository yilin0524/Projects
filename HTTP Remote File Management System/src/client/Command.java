package client;


import java.io.*;
import java.util.ArrayList;

public class Command {
    private String method = "";
    private boolean v = false;
    private boolean h = false;
    private ArrayList<String> hs = new ArrayList<>();
    private boolean d = false;
    private String inline_data = null;
    private boolean f = false;
    private String file = null;
    private String URL = "";
    private boolean o = false;
    private String o_filename = null;
    private boolean CD = false;
    private String CD_filename = "attachment.txt";

    public Command(String s){
        String msg[] = s.split(" ");
        this.setMethod(msg[1]);

        if(s.contains("-o")){
            this.setO(true);
            for(int i = 3; i < msg.length - 1; i++){
                if(msg[i].equalsIgnoreCase("-o")){
                    this.setO_filename(msg[i+1]);
                    break;
                }
            }
            this.setURL(msg[msg.length-3]);
        }else{
            this.setURL(msg[msg.length-1]);
        }

        if(this.getMethod().equalsIgnoreCase("get")){
            if(s.contains("-v")){
                this.setV(true);
            }
            if(s.contains("-h")){
                this.setH(true);
                for(int i = 2; i < msg.length - 2; i++){
                    if(msg[i].equalsIgnoreCase("-h")){
                        hs.add(msg[i+1]);
                        i = i + 1;
                    }
                }
            }
        }else if(this.getMethod().equalsIgnoreCase("post")){
            if(s.contains("-v")){
                this.setV(true);
            }
            if(s.contains("-h")){
                this.setH(true);
                for(int i = 2; i < msg.length - 2; i++){
                    if(msg[i].equalsIgnoreCase("-h")){
                        hs.add(msg[i+1]);
                        i = i + 1;
                    }
                }
            }

            if(s.contains("-d")){
                this.setD(true);
                for(int i = 2; i < msg.length - 2; i++){
                    if(msg[i].equalsIgnoreCase("-d")){
                        this.setInline_data(msg[i+1]);
                        break;
                    }
                }
            }

            if(s.contains("-f")){
                this.setF(true);
                for(int i = 2; i < msg.length - 2; i++){
                    if(msg[i].equalsIgnoreCase("-f")){
                        FileReader reader = null;
                        try {
                            reader = new FileReader(msg[i+1]);
                            BufferedReader br = new BufferedReader(reader);
                            String line;
                            String str = "";
                            while ((line = br.readLine()) != null) {
                                str += line + "\n";
                            }
                            this.setFile(str);
                        } catch (FileNotFoundException e) {
                            System.out.println("File Not Found.");
                        } catch (IOException e) {
                            System.out.println("IOException.");
                        }
                        break;
                    }
                }
            }
        }

    }

    public static String cleanUp(String s){
        if(s.charAt(0)=='\''&&s.charAt(s.length()-1)=='\'')
            s = s.substring(1, s.length()-1);
        if(s.charAt(0)=='\"'&&s.charAt(s.length()-1)=='\"')
            s = s.substring(1, s.length()-1);
        return s;
    }

    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = cleanUp(method);
    }
    public boolean isV() {
        return v;
    }
    public void setV(boolean v) {
        this.v = v;
    }
    public boolean isH() {
        return h;
    }
    public void setH(boolean h) {
        this.h = h;
    }
    public ArrayList<String> getHs() {
        return hs;
    }
    public void setHs(ArrayList<String> hs) {
        this.hs = hs;
    }
    public boolean isD() {
        return d;
    }
    public void setD(boolean d) {
        this.d = d;
    }
    public String getInline_data() {
        return inline_data;
    }
    public void setInline_data(String inline_data) {
        this.inline_data = cleanUp(inline_data);
    }
    public boolean isF() {
        return f;
    }
    public void setF(boolean f) {
        this.f = f;
    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = cleanUp(file);
    }
    public String getURL() {
        return URL;
    }
    public void setURL(String url) {
        this.URL = cleanUp(url);
        if(this.URL.indexOf("http://")==-1){
            this.URL = "http://" + this.URL;
        }
    }
    public boolean isO() {
        return o;
    }
    public void setO(boolean o) {
        this.o = o;
    }
    public String getO_filename() {
        return o_filename;
    }
    public void setO_filename(String o_filename) {
        this.o_filename = cleanUp(o_filename);
    }
    public boolean isCD() {
        return CD;
    }
    public void setCD(boolean CD) {
        this.CD = CD;
    }
    public String getCD_filename() {
        return CD_filename;
    }
    public void setCD_filename(String CD_filename) {
        this.CD_filename = cleanUp(CD_filename);
    }

}
