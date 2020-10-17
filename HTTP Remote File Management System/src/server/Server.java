package server;

import model.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//cd \workspace\LA3_Router\router\windows
//router_x64.exe --port=3000 --drop-rate=0 --max-delay=0ms --seed=2
public class Server {
    private static boolean V = false;
    private static int PORT_NUMBER = 8080;
    private static File DIRECTORY = new File(System.getProperty("user.dir"));
    private static String BAD_REQUEST = "400 BAD_REQUEST";
    private static String METHOD_NOT_ALLOWED = "405 METHOD NOT ALLOWED";
    long hash_sequenceNumber = 6000L;
    
    private HashMap<InetSocketAddress, receiveClient> rcvClient = new HashMap<>();
	private static int get_SYN = 1;
	private static int get_SYN_ACK_ACK = 2;
	
    private static String HELP_HELP = "httpfs is a simple file server.\n" +
            "usage: httpfs [-v] [-p PORT] [-d PATH-TO-DIR]\n" +
            "   -v Prints debugging messages.\n" +
            "   -p Specifies the port number that the server will listen and serve at. Default is 8080.\n" +
            "   -d Specifies the directory that the server will use to read/write requested files. Default is the current directory when launching the application.\n" +
            "\nPlease input server command line: ";

    public void setServer(String str){
        String[] s = str.split(" ");
        for(int i = 1; i < s.length; i++){
            if(s[i].equalsIgnoreCase("-v")){
                V = true;
                System.out.println("Prints debugging messages.");
            }
            if(s[i].equalsIgnoreCase("-p")){
                PORT_NUMBER = Integer.parseInt(s[i+1]);
                System.out.println("Specifies the port number: " + PORT_NUMBER);
            }
            if(s[i].equalsIgnoreCase("-d")){
                DIRECTORY =  new File(s[i+1]);
                System.out.println("Specifies the directory: " + DIRECTORY);
            }
        }
    }
     
    private Packet rcvMsg(DatagramChannel channel, ByteBuffer buf) {
    	Packet packet = null;
        try {
	        buf.clear(); 
			SocketAddress router = channel.receive(buf);		
	        buf.flip();	        
			packet = Packet.fromBuffer(buf);
		} catch (IOException e) {				
		}
		buf.flip();
		return packet;
    }
    
	private synchronized void process_msg(DatagramChannel channel, Packet packet) {
		InetSocketAddress id = new InetSocketAddress(packet.getPeerAddress(), packet.getPeerPort());	
		int get_type = packet.getType();
		long get_seqNum = packet.getSequenceNumber();
		String get_payload = new String(packet.getPayload(), StandardCharsets.UTF_8);
		long get_payload_ack = 0L;
		receiveClient client = null;
		if(rcvClient.containsKey(id)) {
			client = rcvClient.get(id);
		}else {
			client = new receiveClient(channel, id, hash_sequenceNumber, V);					
		}
		
		if(client.getConnect() != get_SYN_ACK_ACK) {					
			if(get_type == Packet.SYN) {
				if(V)
					System.out.println("[Receive SYN]: " + packet);
				client.setConnect(get_SYN);
				hash_sequenceNumber = hash_sequenceNumber + 1000L;
				rcvClient.put(id, client);
				long ack_seq = get_seqNum + 1L;
				packet = packet.toBuilder().setType(Packet.SYN_ACK).setSequenceNumber(client.getHandshake_sequenceNumber()).setPayload((ack_seq+"").getBytes()).create();		
				try {
					channel.send(packet.toBuffer(), Packet.routerAddress);
					if(V)
						System.out.println("[Send SYN_ACK]: " + packet);
				} catch (IOException e) {
					e.printStackTrace();
				}	
				
			}else if (get_type == Packet.SYN_ACK_ACK && client.getConnect() == get_SYN) {
				if(V)
					System.out.println("[Receive SYN_ACK_ACK]: " + packet);
				client.setConnect(get_SYN_ACK_ACK);
				long ack_seq = get_seqNum + 1L;
				packet = packet.toBuilder().setType(Packet.SYN_ACK_ACK).setSequenceNumber(client.getHandshake_sequenceNumber()).setPayload((ack_seq+"").getBytes()).create();		
				try {
					channel.send(packet.toBuffer(), Packet.routerAddress);
					if(V)
						System.out.println("[Send SYN_ACK_ACK]: " + packet);
				} catch (IOException e) {
					e.printStackTrace();
				}	
				client.setSequenceNumber(client.getHandshake_sequenceNumber() + 1);
				client.setClient_start(ack_seq);
				client.setClient_end(ack_seq + packet.WINDOW);				
				Thread cThread = new Thread(client);
				client.setcThread(cThread);
				client.getcThread().start();
//				rcvClient.put(id, client);
				if(V)
					System.out.println("[Three-way handshaking success!]: " + packet);	
			}		
		}else {
			if(get_type == Packet.SYN) {
				if(V)
					System.out.println("[Receive SYN]: " + packet);
				long ack_seq = get_seqNum + 1;
				packet = packet.toBuilder().setType(Packet.SYN_ACK).setSequenceNumber(client.getHandshake_sequenceNumber()).setPayload((ack_seq+"").getBytes()).create();		
				try {
					channel.send(packet.toBuffer(), Packet.routerAddress);
				} catch (IOException e) {
					e.printStackTrace();
				}	
				if(V)
					System.out.println("[Send SYN_ACK]: " + packet);
			} else if (get_type == Packet.SYN_ACK_ACK) {
				if(V)
					System.out.println("[Receive SYN_ACK_ACK]: " + packet);
				long ack_seq = get_seqNum + 1;
				packet = packet.toBuilder().setType(Packet.SYN_ACK_ACK).setSequenceNumber(client.getHandshake_sequenceNumber()).setPayload((ack_seq+"").getBytes()).create();		
				try {
					channel.send(packet.toBuffer(), Packet.routerAddress);
					if(V)
						System.out.println("[Send SYN_ACK_ACK]: " + packet);
				} catch (IOException e) {
					e.printStackTrace();
				}					
			}else if(get_type == Packet.DATA) {
				if(V)
					System.out.println("[Receive DATA]: " + packet);
				System.err.println("----"+get_seqNum);
    			if(get_seqNum < client.getClient_end()) { 
    				client.addReceive_queue(packet);
    				}
    			if(get_seqNum >= client.getClient_start() && get_seqNum < client.getClient_end()) {
    				client.setRcv_window((int)(get_seqNum - client.getClient_start()), get_payload);  				
    			}			
			}else if(get_type == Packet.FIN) {
				if(V)
					System.out.println("[Receive FIN]: " + packet);
    			long ack_seq = get_seqNum + 1;
				packet = packet.toBuilder().setType(Packet.FIN_ACK).setSequenceNumber(client.getFin_ack_sequenceNumber()).setPayload((ack_seq+"").getBytes()).create();		
				try {
					channel.send(packet.toBuffer(), Packet.routerAddress);
					if(V)
						System.out.println("[Send FIN_ACK]: " + packet);
				} catch (IOException e) {					
				}	
				if(!client.isFinish_receive()) {
					client.setFinish_receive(true);
					client.setSequenceNumber(client.getSequenceNumber() + 1);
					while(!client.isComplete_receive()) {
						//waiting for complete request
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(V)
						System.out.println("[Processing Request]: " + packet);
					String response = AcceptHttpRequest(client.getRequest());
					client.setResponse(response);		
				}
    		}else if(get_type == Packet.ACK) {
    			if(!client.isFinish_send()) {
    				get_payload_ack = Long.parseLong(get_payload);
    				if(V)
    					System.out.println("[Receive ACK]" + get_payload_ack + "(Data " + (get_payload_ack-1) + ") from Client: " + packet);
    				for (int i = 0; i < client.getSend_window().size(); i++) {
    					Timer pTimer = client.getSend_window().get(i);
    					if((pTimer.isAcked() == false) && (pTimer.getSequenceNumber() == (get_payload_ack-1))) {
    						pTimer.setAcked(true);
    						break;
    					}
    				}
    			}		
    		}else if(get_type == Packet.FIN_ACK) {
    			if(V)
					System.out.println("[Receive FIN_ACK] from Client: " + id);
    			client.getpFin().setAcked(true);
    			client.getcThread().stop();
    			packet = packet.toBuilder().setType(Packet.FIN_ACK_ACK).setSequenceNumber(client.getSequenceNumber()).setPayload((get_seqNum+1+"").getBytes()).create();						
    			try {
					channel.send(packet.toBuffer(), Packet.routerAddress);
					if(V)
						System.out.println("[Send FIN_ACK_ACK] to Client " + packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
    			rcvClient.remove(id);
    			if(V)
    				System.out.println("[Disconnect] Client: " + id);
    		}
		}
	}
		
	public void callUDP() throws IOException {
		DatagramChannel channel = DatagramChannel.open();
        channel.bind(new InetSocketAddress(PORT_NUMBER));       
		Thread listener = new Thread(new Runnable() {			
			@Override
			public void run() {
				ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN).order(ByteOrder.BIG_ENDIAN);
				while(true) {
					Packet receive_packet = rcvMsg(channel, buf);	
					if(receive_packet != null){
						process_msg(channel, receive_packet);
					}
				}				
			}
		});		
		listener.start();
	}

    public static void main(String[] args) throws IOException{
        System.out.println(HELP_HELP);
        Scanner input = new Scanner(System.in);
        String str = "";
        boolean b1 = false;
        Pattern p1 = Pattern.compile("httpfs( -v)?( -p .+)?( -d .+)?");
        while(!b1) {
            str = input.nextLine().trim();
            Matcher m1 = p1.matcher(str);
            b1 = m1.matches();
            if(!b1)
                System.out.println(HELP_HELP);
        }
        Server server = new Server();
        server.setServer(str);        
        server.callUDP();
    }

    private String AcceptHttpRequest(String msg){
		ArrayList<String> requestList = new ArrayList<>();
		String[] request1 = msg.split("\\r\\n");
		int index = 0;
		int k = 0;
		boolean method_get = true;
		for (int i = 0; i < request1.length; i++) {
			String request = request1[i];
			if (k == 0 && request.substring(0, 4).equalsIgnoreCase("POST")) {
				method_get = false;
			}
			k++;
			if (request.equals("") && method_get) {
				break;
			}
			requestList.add(request);
			if (V)
				System.out.println("[Processing Request] " + request);
			if (request.equals("") && !method_get) {
				method_get = true;
				index = requestList.size();
			}
		}
		String requestMethod;
		String response = "";
		if (requestList.size() > 0) {
			requestMethod = requestList.get(0);		
			String s1[] = requestMethod.split(" ");
			if (s1[1].length() > 0 && s1[1].charAt(0) == '/' || s1[1].length() == 0) {
				if (s1[0].equalsIgnoreCase("GET"))
					response = getFile(requestList, s1[1]);
				else if (s1[0].equalsIgnoreCase("POST"))
					response = postFileContent(index, requestList, s1[1]);
				else
					response = sthWrong(METHOD_NOT_ALLOWED);
			} else {
				response = sthWrong(BAD_REQUEST);
			}
		} else {
			if (V)
				System.out.println("[Client request method] " + BAD_REQUEST);
			response = sthWrong(BAD_REQUEST);
		}
		return response;
    }

    private String getFile(ArrayList<String> requestList, String filename){
        boolean flag = subFile(filename);
        File file = new File(DIRECTORY.getAbsolutePath() + filename);
        Command command = new Command();
        String files = "";
        String response = "";
        if(file.exists() && flag){
        	try {
	            response += "HTTP/1.0 200 OK\r\n";
	            if(file.isFile()){
	                BufferedReader br = new BufferedReader(new FileReader(file));
	                String str;
	                while((str = br.readLine()) != null){
	                    files = files + "\r\n" + str;
	                }
	                command.setHeader("Content-Type:", getFileType(file));
	                br.close();
	            }else if(file.isDirectory()){
	                String[] fileList = file.list();
	                for (int i = 0; i < fileList.length ; i++)
	                    files =  files + "\r\n" +fileList[i];
	            }
            }catch(IOException e) {
            	response += "HTTP/1.0 404 Not Found" + "\r\n";
	            files = "\r\nSomething wrong with the file!";
            }
        }else{
            if(filename.length()>=5 && filename.substring(0,5).equalsIgnoreCase("/get?") && flag){
            	response += "HTTP/1.0 200 OK\r\n";
                files = getARGS(filename.substring(5));
                files = files + getHeader(requestList, requestList.size());
                files = files + "\r\nurl: http:/" + command.getHeader().get("Server:") + ":" + PORT_NUMBER + filename;
            }else if(filename.length()>=11 && filename.substring(0,9).equalsIgnoreCase("/redirect") && flag) {
                String j = filename.substring(10);
                try{
                    int j1 = Integer.parseInt(j);
                    if(j1 > 1) {
                    	response += "HTTP/1.0 302 FOUND\r\n";
                        command.setHeader("Location:", "/redirect/" + (j1-1));
                    }else if(j1 == 1){
                    	response += "HTTP/1.0 302 FOUND\r\n";
                        command.setHeader("Location:", "/");
                    }else{
                    	response += "HTTP/1.0 404 Not Found\r\n";
                        if(file.getName().contains(".")){
                            files = "\r\nFile " + filename + " does not exist!";
                        }else{
                            files = "\r\nThe folder does not exist or you can not access this folder!";
                        }
                    }
                }catch (Exception e){
                	response += "HTTP/1.0 404 Not Found\r\n";
                    if(file.getName().contains(".")){
                        files = "\r\nFile " + filename + " does not exist!";
                    }else{
                        files = "\r\nThe folder does not exist or you can not access this folder!";
                    }
                }
            }else{
            	response += "HTTP/1.0 403 Not Found\r\n";
                if(file.getName().contains(".")){
                    files = "\r\nYou can not access this folder of the file!";
                }else{
                    files = "\r\nThe folder does not exist or you can not access this folder!";
                }
            }
        }
        //Content-Disposition: attachment; filename="filename.jpg"
        for(int i=0; i < requestList.size(); i++){
            String s = requestList.get(i).toLowerCase();
            if(s.contains("content-disposition")){
                String s1[] = s.split(":");
                command.setHeader("Content-Disposition:", s1[1].trim());
            }
            if(s.contains("keep-alive")){
                command.setHeader("Connection:", "keep-alive");
            }
        }
        command.setHeader("Content-Length:", files.length()+"");
        for(String key:command.getHeader().keySet()){
        	response += key + " " + command.getHeader().get(key) + "\r\n";
        }
        response += files;
//        if(V)
//            System.out.println("[Response][GET file or directory] " + files);
        return response;
    }

    private boolean subFile(String filename) {
    	File abpath1 = new File(DIRECTORY.getAbsolutePath() + filename);
    	File abpath2 = new File(DIRECTORY.getAbsolutePath());
    	String path1 = abpath1.getAbsolutePath().toString();
    	String path2 = abpath2.getAbsolutePath().toString();
    	if(path1.contains(path2) && !path1.contains("..")) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    private synchronized String postFileContent(int index, ArrayList<String> requestList, String filename){
    	boolean flag = subFile(filename);
        Command command = new Command();
        String files = "";
        String response = "";
        if(flag) {
	        try {
	            File file = new File(DIRECTORY.getAbsolutePath() + filename);
	            FileWriter fw = new FileWriter(DIRECTORY.getAbsolutePath() + filename, true);
	            response += "HTTP/1.0 200 OK" + "\r\n";
	            files = files + "\r\ndata:";
	            PrintWriter pw = new PrintWriter(fw);
	            for (int i = index; i < requestList.size(); i++){
	                pw.append(requestList.get(i) + "\r\n");
	                files = files + "\r\n   " + requestList.get(i);
	            }
	            pw.close();
	            command.setHeader("Content-Type:", getFileType(file));
	            files = files + getHeader(requestList, index-1);
	            String requestHost = requestList.get(1).split(" ")[1];
	            files = files + "\r\nurl: http:/" + command.getHeader().get("Server:") + ":" +PORT_NUMBER + filename;
	
	            files = files + "\r\nWrite data into file " + filename + " successfully!";
	        } catch (IOException e) {
	        	response += "HTTP/1.0 404 Not Found" + "\r\n";
	            files = "\r\nNo such directory!";
	        }
        }else {
        	response += "HTTP/1.0 404 Not Found" + "\r\n";
            files = "\r\nYou can not access such directory!";
        }
        for(int i=0; i < index; i++){
            String s = requestList.get(i).toLowerCase();
            if(s.contains("keep-alive")){
                command.setHeader("Connection:", "keep-alive");
            }
        }
        command.setHeader("Content-Length:", files.length()+"");
        for(String key:command.getHeader().keySet()){
        	response += key + " " + command.getHeader().get(key) + "\r\n";
        }
        response += files;
//        if(V)
//            System.out.println("[Response][POST data] " + files);
        return response;
    }

    private String sthWrong(String type){
    	String response = "";
        Command command = new Command();
        response += "HTTP/1.0 " + type + "\r\n";
        if(type.equals(BAD_REQUEST)){
        	response += "Date: "+(new Date()).toString() + "\r\n";
        	response += "Content-Length: 0" + "\r\n";
        	response += "\r\n";
        }else{
            String files = "\r\nThe method is not allowed for the requested URL.";
            command.setHeader("Content-Length:", files.length()+"");
            for(String key:command.getHeader().keySet()){
            	response += key + " " + command.getHeader().get(key) + "\r\n";
            }
            response += files;
        }
        if(V)
            System.out.println("Wrong command: " + type);
        return response;
    }

    public String getFileType(File file) {

        String fileName= file.getName();
        int suffix= fileName.indexOf(".");
        String type = fileName.substring(suffix+1).toLowerCase();
        String fileType="text/html";
        switch (type) {
            case "txt":
                fileType="text/plain";
                break;
            case "xml":
                fileType="text/xml";
                break;
            case "gif":
                fileType="image/gif";
                break;
            case "jepg":
                fileType="image/jepg";
                break;
            case "jpg":
                fileType="image/jepg";
                break;
            case "png":
                fileType="image/png";
                break;
            case "json":
                fileType= "application/json";
                break;
            case "pdf":
                fileType= "application/pdf";
                break;
            case "mp3":
                fileType= "audio/mp3";
                break;
            case "mp4":
                fileType= "video/mpeg4";
                break;
            default:
                break;
        }
        return fileType;
    }

    public String getARGS(String str){
        String s[] = str.split("&");
        String file = "\r\nargs:";
        for(int i = 0; i < s.length; i++){
            file = file + "\r\n   " +s[i];
        }
        return file;
    }

    public String getHeader(ArrayList<String> list, int index){
        String file = "\r\nheaders:";
        for(int i = 1; i < index; i++){
            String s = list.get(i).toLowerCase();
            if(s.contains("keep-alive")){
                continue;
            }else {
                file = file + "\r\n   " + list.get(i);
            }
        }
        return file;
    }
}
