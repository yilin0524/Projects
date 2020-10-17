import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Vector;

import org.omg.CORBA.PUBLIC_MEMBER;



public interface DEMSInterf extends Remote {
	
	public Boolean manLogin(String manID) throws java.rmi.RemoteException;
	
	public Boolean cusLogin(String cusID) throws java.rmi.RemoteException;

	
	public boolean addEvent(String eventID, String eventType, String manID, LinkedList<String> eventDetail) throws java.rmi.RemoteException;
	
	public boolean removeEvent(String eventID,String eventType, String manID) throws java.rmi.RemoteException;
	
	public String listEventA(String manID,String eventType) throws java.rmi.RemoteException;
	
	public boolean bookEvent(String custID,String eventID, String eventType)throws java.rmi.RemoteException;
	
	public boolean cancelEvent(String custID, String eventID, String eventType)throws java.rmi.RemoteException;
	
	public String getBookingSchedule(String custID) throws java.rmi.RemoteException;
	
	

}
