import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MTLServer {
	
public static void main(String[] args) throws Exception{
	    String portNum = " ", registryURL = " ";
	    int udpportnum = 0;
	    String p;
	    
	    try{     
	    	p= "MTL";
	    	portNum = "2222";
	    	udpportnum = 2223;
	    	
	        int RMIPortNum = Integer.parseInt(portNum);
	        startRegistry(RMIPortNum);
	        DEMSImpl obj = new DEMSImpl();
	        registryURL = "rmi://localhost:" + portNum + "/DEMS-" + p;
	        Naming.rebind(registryURL, obj);
	        System.out.println ("MTL Server registered.  Registry currently contains:");
	        listRegistry(registryURL); 
	        System.out.println("MTL Server ready. ");
	        
	        DatagramSocket serversocket = new DatagramSocket(udpportnum);
	        startlistening(p, obj, udpportnum, serversocket);

	        obj.StartServer(p);
	         
	    }	      
	      catch (Exception re) {
	         System.out.println("Exception in Server.main: " + re);
	      } 
	  } 
	
	
	private static void startlistening(String City, DEMSImpl citySever, int UDPlistenPort, DatagramSocket SeverSocket) throws Exception {
		
		String threadName = City + "listen";
		Listening listen = new Listening(threadName, SeverSocket, citySever);
		listen.start();
	}
	
	private static void startRegistry(int RMIPortNum) throws RemoteException {
		
		try {
			Registry registry = LocateRegistry.getRegistry(RMIPortNum);
		    registry.list( ); 
		} catch (RemoteException e) { 		
		    Registry registry = LocateRegistry.createRegistry(RMIPortNum);
		    System.out.println("RMI registry created at port " + RMIPortNum);
		}
	}
		 
	private static void listRegistry(String registryURL) throws RemoteException, MalformedURLException {
		
		String [ ] names = Naming.list(registryURL);
		for (int i=0; i < names.length; i++)
			System.out.println(names[i]);
	}
	
}
