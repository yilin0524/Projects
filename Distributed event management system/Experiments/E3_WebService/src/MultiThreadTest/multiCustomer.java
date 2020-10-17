package MultiThreadTest;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import web.DEMSInterf;

public class multiCustomer implements Runnable{

	static DEMSInterf DEMSInterface;
	
	String cusID;
	String eventID;
	String type;
	String eventID_new;
	String type_new;
	
	public multiCustomer(String cusID,String eventID,String type,String eventID_new,String type_new) {
		this.cusID=cusID;
		this.eventID=eventID;
		this.type=type;
		this.eventID_new=eventID_new;
		this.type_new=type_new;
	}
	
	public static void Log(String ID,String Message) throws Exception{
		String path = "D://eclipse-workspace/DEMS1.4/ClientLog/" +ID + ".txt";   
		FileWriter fileWriter = new FileWriter(path,true);
		BufferedWriter bf = new BufferedWriter(fileWriter);
		bf.write(Message + "\n");
		bf.close();
	}


	public static String getDate(){
	    Date date = new Date();
	    long times = date.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String dateString = formatter.format(date);
	    return dateString;
	}
	
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public void operation() {        
        String hostName;
        String portNum = " ";
        String name = " ";
                
        String customerID = cusID;
        String city = customerID.substring(0,3);

        String lastFour=customerID.substring(4);
        try {
	        if( customerID.charAt(3)=='C' && lastFour.length()==4 && isInteger(lastFour)) {
	       	 	if(city.equals("TOR")) {
		        	 portNum = "1111"; 
		        	 name = "TOR";
		        	 
		        	 URL addURL = new URL("http://localhost:"+portNum+"/"+name+"?wsdl");
			 		QName addQName = new QName("http://web/", "DEMSImplService");
			 			
			 		Service TOR = Service.create(addURL, addQName);
			 		DEMSInterface = TOR.getPort(DEMSInterf.class);
	       	 	}
		         else if(city.equals("MTL")) {
		        	 portNum = "2222";
		        	 name = "MTL";
		        	 
		        	 URL addURL = new URL("http://localhost:"+portNum+"/"+name+"?wsdl");
			 		QName addQName = new QName("http://web/", "DEMSImplService");
			 			
			 		Service MTL = Service.create(addURL, addQName);
			 		DEMSInterface = MTL.getPort(DEMSInterf.class);
		         }
		         else if(city.equals("OTW")) {
		        	 portNum = "3333";
		        	 name ="OTW";
		        	 
		        	 URL addURL = new URL("http://localhost:"+portNum+"/"+name+"?wsdl");
			 		QName addQName = new QName("http://web/", "DEMSImplService");
			 			
			 		Service OTW = Service.create(addURL, addQName);
			 		DEMSInterface = OTW.getPort(DEMSInterf.class);
		         }else {
		        	 System.out.println("Invalid customerID "+customerID);
		        	 System.exit(0);
		         }
	       
	        }
	        else {
	       	 System.out.println("Invalid customerID "+customerID);
	       	 System.exit(0);
	        }
        }catch (Exception e) {
			System.out.println("Exception in multiCustomer: " + e);
		}      
        
        try {
	        
	        if(DEMSInterface.cusLogin(customerID)){
	       	 System.out.println(customerID+" Login successfully");
	       	 Log(customerID, getDate() + " " + customerID + " login successfully");
	        }
	        else{
	       	 System.out.println(customerID+" Login failed");
	       	 Log(customerID, getDate() + " " + customerID + " login failed");
	        }
        }catch (Exception e) {
			System.out.println("Exception in multiCustomer: " + e);
		}      
        
	}
	
	public void book() {
		try {
			if(DEMSInterface.bookEvent(cusID,eventID, type)){
				 System.out.println("Customer "+cusID+" successfully booked in event "+ eventID+" in "+ type);
				 Log(cusID,  getDate() + " " + cusID + " successfully booked in event " + eventID
	       			 +" in "+ type);
			 }
			 else {
				 System.out.println("Customer "+ cusID+ "failed to book in event "+ eventID+" in "+ type);
				 Log(cusID, getDate() + " " + cusID + " failed to book in event " + eventID
	       			 +" in "+ type);
			 }
		}catch(Exception e) {
			System.out.println("Exception in multiCustomer: " + e);
		} 
	}
	
	public void swap() {
		try {
			if (DEMSInterface.swapEvent(cusID, eventID_new, type_new, eventID, type)) {
				System.out.println("Customer " + cusID + " successfully swap old event " + eventID +" in "+ type
						+ " and new event " + eventID_new +" in "+ type_new);
				Log(cusID, getDate() + " " + cusID + " successfully swap old event " + eventID +" in "+ type
						+ " and new event " + eventID_new +" in "+ type_new);
			} else {
				System.out.println("Customer " + cusID + " failed to swap old event " + eventID +" in "+ type
						+ " and new event " + eventID_new +" in "+ type_new);
				Log(cusID, getDate() + " " + cusID + " failed to swap old event " + eventID +" in "+ type
						+ " and new event " + eventID_new +" in "+ type_new);
			}
		}catch(Exception e) {
			System.out.println("Exception in multiCustomer: " + e);
		} 
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		operation();
		try {
			Thread.sleep(80);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		book();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		swap();
		
	}

}
