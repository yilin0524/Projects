package CorbaApp;

/**
* CorbaApp/DEMSInterfHolder.java .
* ��IDL-to-Java ������ (����ֲ), �汾 "3.2"����
* ��D:/eclipse-workspace/DEMS1.3/src/corba.idl
* 2019��7��8�� ����һ ����12ʱ17��44�� EDT
*/

public final class DEMSInterfHolder implements org.omg.CORBA.portable.Streamable
{
  public CorbaApp.DEMSInterf value = null;

  public DEMSInterfHolder ()
  {
  }

  public DEMSInterfHolder (CorbaApp.DEMSInterf initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = CorbaApp.DEMSInterfHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    CorbaApp.DEMSInterfHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return CorbaApp.DEMSInterfHelper.type ();
  }

}
