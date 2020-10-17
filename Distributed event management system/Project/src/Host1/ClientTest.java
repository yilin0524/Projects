package Host1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientTest {

	public static void main(String args[]) {
		 String addr = "132.205.46.182,6000";
		 String hostname = "132.205.46.182";
//		 String msg = "0:" + addr + ":addEvent,MTLM1001,MTLA100519,2,10";
//		 String msg1 = "1:" + addr + ":addEvent,OTWM1001,OTWA100519,2,10";
//		 String msg2 = "2:" + addr + ":addEvent,TORM1001,TORA100519,2,10";
//		 String msg3 = "3:" + addr + ":listEventAvailability,MTLM1001,2";
		 String msg = "4:" + addr + ":bookEvent,OTWC1001,MTLA100519,2";
		 String msg1 = "5:" + addr + ":bookEvent,OTWC1001,OTWA100519,2";
		 String msg2 = "6:" + addr + ":bookEvent,OTWC1001,TORA100519,2";
		 String msg3 = "7:" + addr + ":getBookingSchedule,OTWC1001";
		 UDP(msg, hostname);
		 UDP(msg1, hostname);
		 UDP(msg2, hostname);
		 UDP(msg3, hostname);

//		String hostname1 = "132.205.46.182";
//		String hostname2 = "132.205.46.183";
//		String addr1 = "132.205.46.182,6000";
//		String msg = "SetCrash:1";
//		String msg1 = "Crash:20:1";
//		String msg2 = "0:" + addr1 + ":addEvent,OTWM1001,OTWA100519,2,10";
//		String msg3 = "1:" + addr1 + ":listEventAvailability,MTLM1001,2";

//		UDP(msg, hostname1);
//		UDP(msg1, hostname1);
//		UDP(msg, hostname2);
//		UDP(msg1, hostname2);
//		UDP(msg2, hostname1);
//		UDP(msg3, hostname1);
//		UDP(msg2, hostname2);
//		UDP(msg3, hostname2);
	}

	public static void UDP(String msg, String hostname) {
		DatagramSocket socket = null;
		int serverPort = 6666;
		String result = "";
		try {
			socket = new DatagramSocket(5000);
			byte[] message = (msg.getBytes());
			InetAddress Host = InetAddress.getByName(hostname);
			DatagramPacket request = new DatagramPacket(message, message.length, Host, serverPort);
			socket.send(request);
		} catch (Exception e) {
			System.out.println("Socket: " + e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

}
