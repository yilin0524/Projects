package Host1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FEreceive {

	public static void main(String[] args) {
		 UDP();
	}

	public static void UDP() {
		System.out.println("FE UDP start..");
		DatagramSocket socket = null;
//		String hostname = "132.205.94.10";
//		int serverPort = 6666;
		String result = "";
		try {
			socket = new DatagramSocket(6000);
			while(true) {	
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				socket.receive(reply);
				result = new String(reply.getData()).trim();
				System.out.println(result);
			}
		} catch (Exception e) {
			System.out.println("Socket: " + e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
//		return result;
	}
}
