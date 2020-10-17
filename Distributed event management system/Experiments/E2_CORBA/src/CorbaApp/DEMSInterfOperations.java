package CorbaApp;


/**
* CorbaApp/DEMSInterfOperations.java .
* ��IDL-to-Java ������ (����ֲ), �汾 "3.2"����
* ��D:/eclipse-workspace/DEMS1.3/src/corba.idl
* 2019��7��8�� ����һ ����12ʱ17��44�� EDT
*/

public interface DEMSInterfOperations 
{
  boolean manLogin (String manID);
  boolean cusLogin (String cusID);
  boolean addEvent (String eventID, String eventType, String manID, int capacity);
  boolean removeEvent (String eventID, String eventType, String manID);
  String listEventA (String manID, String eventType);
  boolean bookEvent (String custID, String eventID, String eventType);
  boolean cancelEvent (String custID, String eventID, String eventType);
  String getBookingSchedule (String custID);
  boolean swapEvent (String cusID, String newEventID, String newEventType, String oldEventID, String oldEventType);
} // interface DEMSInterfOperations
