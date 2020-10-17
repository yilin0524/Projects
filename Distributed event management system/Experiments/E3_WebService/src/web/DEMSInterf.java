package web;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)

public interface DEMSInterf {
	
	public boolean manLogin(String manID) throws java.rmi.RemoteException;
	
	public boolean cusLogin(String cusID) throws java.rmi.RemoteException;

	
	public boolean addEvent(String eventID, String eventType, String manID, int capacity) throws java.rmi.RemoteException;
	
	public boolean removeEvent(String eventID,String eventType, String manID) throws java.rmi.RemoteException;
	
	public String listEventA(String manID,String eventType) throws java.rmi.RemoteException;
	
	public boolean bookEvent(String custID,String eventID, String eventType)throws java.rmi.RemoteException;
	
	public boolean cancelEvent(String custID, String eventID, String eventType)throws java.rmi.RemoteException;
	
	public String getBookingSchedule(String custID) throws java.rmi.RemoteException;
	
	public boolean swapEvent(String cusID, String newEventID, String newEventType, String oldEventID, String oldEventType)throws java.rmi.RemoteException;
	
	

}
