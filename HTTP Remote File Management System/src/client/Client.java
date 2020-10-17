package client;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.Response;

public class Client implements Runnable {
    private static String USER_AGENT = "User-Agent: Concordia-HTTP/1.0";
    private static String HELP = "httpc help( get| post| help)?";
    private static String GET= "httpc get (-v )?(-h .+:.+ )*(.+)( -o .+)?";
    private static String POST= "httpc post( -v)?( -h .+:.+)*( -d .+| -f .+)?( .+)( -o .+)?";

    private static String HELP_HELP = "httpc is a curl-like application but supports HTTP protocol only.\n" +
            "Usage:\n" +
            "   httpc command [arguments]\n" +
            "The commands are:\n" +
            "   get     executes a HTTP GET request and prints the response.\n" +
            "   post    executes a HTTP POST request and prints the response.\n" +
            "   help    prints this screen.\n\n" +
            "Use \"httpc help [command]\" for more information about a command.";

    private static String HELP_GET = "usage: httpc get [-v] [-h key:value] URL [-o filename]\n" +
            "Get executes a HTTP GET request for a given URL.\n" +
            "   -v  Prints the detail of the response such as protocol, status, and headers.\n" +
            "   -h  key:value Associates headers to HTTP Request with the format 'key:value'." +
            "   -o  write the body of the response to the specified file(filename) instead of the console. .\n";

    private static String HELP_POST = "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL [-o filename]" +
            "Post executes a HTTP POST request for a given URL with inline data or from file.\n" +
            "   -v  Prints the detail of the response such as protocol, status, and headers.\n" +
            "   -h  key:value Associates headers to HTTP Request with the format 'key:value'.\n" +
            "   -d  string Associates an inline data to the body HTTP POST request.\n" +
            "   -f  file Associates the content of a file to the body HTTP POST request.\n" +
            "   -o  write the body of the response to the specified file(filename) instead of the console.\n\n" +
            "Either [-d] or [-f] can be used but not both.";
    private String command1 = "";
    
    public Client(String command1) {
    	this.command1 = command1;
    }
    
    public static void main(String[] args) throws IOException{
        Scanner input = new Scanner(System.in);
        while(true){
        	String s = input.nextLine().trim();//.toLowerCase().trim();
        	Client command = new Client(s);
        	command.request();
        }
    }
    public void request() throws IOException{
    	String s = this.command1;
		Pattern p1 = Pattern.compile(HELP);
		Pattern p2 = Pattern.compile(GET);
		Pattern p3 = Pattern.compile(POST);
		Matcher m1 = p1.matcher(s);
		boolean b1 = m1.matches();
		Matcher m2 = p2.matcher(s);
		boolean b2 = m2.matches();
		Matcher m3 = p3.matcher(s);
		boolean b3 = m3.matches();
		if (b1) {
			if (s.contains("get")) {
				System.out.println(HELP_GET);
			} else if (s.contains("post")) {
				System.out.println(HELP_POST);
			} else {
				System.out.println(HELP_HELP);
			}
		} else if (b2) {
			Command obj = new Command(s);
			HttpGet(obj);
		} else if (b3) {
			if (s.contains("-d") && s.contains("-f")) {
				System.out.println(
						"Input format is wrong, please try again. (Either [-d] or [-f] can be used but not both.)");
			} else {
				Command obj = new Command(s);
				HttpPost(obj);
			}
		} else {
			System.out.println("Input format is wrong, please try again.");
		}
        
    }

    public static void HttpGet(Command obj) throws IOException {
        URL link = new URL(obj.getURL());
        String host = link.getHost();
        int port = link.getPort() == -1 ? 80 : link.getPort();
        String file = link.getFile();
        BufferedWriter bw = null;
        BufferedReader br = null;

        String request = "";
        request += "GET " + file + " HTTP/1.0\r\n";
        request += "HOST: " + host + "\r\n";
        request += USER_AGENT+"\r\n";

        if(obj.isH()){
            for(int i = 0; i < obj.getHs().size(); i++){
            	request += obj.getHs().get(i) + "\r\n";
            }
        }
        request += "\r\n";
        UDPClient udp = new UDPClient();
        String response = udp.callUDP(host, port, request);
        
        String[] line1 = response.split("\\r\\n");
        boolean flag = false;
        boolean redirect = false;
        PrintWriter pw_o = null;
        PrintWriter pw_cd = null;
        if(obj.isO()){
            FileWriter fw = new FileWriter(obj.getO_filename(), true);
            pw_o = new PrintWriter(fw, true);
        }
        int k = 0, j = 0;
        String file_content = "";
        for (int i = 0; i < line1.length; i++) {
			String line = line1[i];
            if((line.contains("301")||line.contains("302")) && k == 0 && j == 0){
                redirect = true;
                k++;
            }
            //Content-Disposition: attachment; filename="filename.jpg"
//httpc get -v -h content-disposition:attachment;filename="1.txt" http://127.0.0.1:8081/out/aaaa.txt -o 2.txt
            if(line.contains("Content-Disposition")){
                String s1[] = line.split(":");
                if(s1[1].trim().contains("attachment")){
                    obj.setCD(true);
                    if(s1[1].trim().contains("filename")){
                        String f_name= s1[1].substring(s1[1].indexOf("=")+1);
                        obj.setCD_filename(f_name);
                    }    
                    FileWriter fw = new FileWriter(obj.getCD_filename(), true);
                    pw_cd = new PrintWriter(fw, true); 
                }
            }
            if(redirect && line.contains("Location:") && k == 1){
                obj.setURL("http://" + host + ":" + port + line.substring(line.indexOf(" ") + 1));//"http://" + host +
            }
            j++;
            if(obj.isV() || flag)
                file_content = file_content+ line + "\r\n";
            if(line.equals(""))
                flag = true;
        }
        if(obj.isCD()){
            pw_cd.append(file_content);
        }
        if(obj.isO()){
            pw_o.append(file_content);
        }
        if(!obj.isCD()){
            System.out.println(file_content.substring(0,file_content.lastIndexOf("\r\n")));
        }
        if(pw_o!=null){
            System.out.println("The response was written into file " + obj.getO_filename());
            pw_o.close();
        }
        if(pw_cd!=null){
            System.out.println("The response was written into file " + obj.getCD_filename());
            pw_cd.close();
        }

        if(redirect){
            System.out.println("*****************REDIRECT*****************");
            HttpGet(obj);
        }

    }

    public static void HttpPost(Command obj) throws IOException {
        URL link = new URL(obj.getURL());
        String host = link.getHost();
        int port = link.getPort() == -1 ? 80 : link.getPort();
        String file = link.getFile();

        BufferedWriter bw = null;
        BufferedReader br = null;

        String request = "";
        request += "POST " + file + " HTTP/1.0\r\n";
        request += "HOST: " + host + "\r\n";

        if(obj.isH()){
            for(int i = 0; i < obj.getHs().size(); i++){
            	request += obj.getHs().get(i) + "\r\n";
            }
        }

        if(obj.isD()){
        	request += "Content-Length:" + obj.getInline_data().length() + "\r\n";
        }
        if(obj.isF()){
        	request += "Content-Length:" + obj.getFile().length() + "\r\n";
        }
        request += USER_AGENT + "\r\n";
        request += "\r\n";
        if(obj.isD()){
        	request += obj.getInline_data().toString() + "\r\n";
        }
        if(obj.isF()){
        	request += obj.getFile().toString() + "\r\n";
        }
        request += "\r\n";
        
        UDPClient udp = new UDPClient();        
        String response = udp.callUDP(host, port, request);

        String[] line1 = response.split("\\r\\n");
        boolean flag = false;
        boolean redirect = false;
        PrintWriter pw = null;
        if(obj.isO()){
            FileWriter fw = new FileWriter(obj.getO_filename(), true);
            pw = new PrintWriter(fw, true);
        }
        int k = 0, j = 0;
		for (int i = 0; i < line1.length; i++) {
			String line = line1[i];
            if((line.contains("301")||line.contains("302")) && k == 0 && j == 0){
                redirect = true;
                k++;
            }
            if(redirect && line.contains("Location:") && k == 1){
                obj.setURL("http://" + host + line.substring(line.indexOf(" ") + 1));//"http://" + host +
            }
            j++;
            if(obj.isO()){
                if(obj.isV() || flag)
                    pw.append(line + "\r\n");
                if(line.equals(""))
                    flag = true;
            }else{
                if(obj.isV() || flag)
                    System.out.println(line);
                if(line.equals(""))
                    flag = true;
            }
        }
        if(pw!=null){
            System.out.println("The response was written into file " + obj.getO_filename());
            pw.close();
        }
        
        if(redirect){
            System.out.println("*****************REDIRECT*****************");
            HttpPost(obj);
        }
    }

	@Override
	public void run() {
		try {
			request();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

}
