package client;

import model.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_READ;

public class UDPClient{
	private HashMap<Long, Timer> queue = new HashMap<>();	
	private ArrayList<Timer> send_window = new ArrayList<>();	
	private ArrayList<String> rcv_window = new ArrayList<>();
	private ArrayList<Packet> receive_queue = new ArrayList<>();
	private boolean finish_send = false;
	private long finish_receive = -1L;
	private int queue_start = 0;
	private int queue_end = 1;
	private long server_start = 0L;
	private long server_end = 0L;
	private Timer pFin;
	private DatagramChannel channel;
	
	public UDPClient() {
		for (int i = 0; i < Packet.WINDOW; i++) {
			rcv_window.add(null);
		}
	}
	
	private Packet rcvMessage_withTime(DatagramChannel channel) throws IOException {
		channel.configureBlocking(false);
		Selector selector = Selector.open();
		channel.register(selector, OP_READ);
//		System.out.println("Waiting for the response..");
		selector.select(Timer.TIME_OUT);
		Set<SelectionKey> keys = selector.selectedKeys();
		if (keys.isEmpty()) {
			System.out.println("No response after timeout!");
			return null;
		} else {
			ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
			SocketAddress router = channel.receive(buf);
			buf.flip();
			Packet resp = Packet.fromBuffer(buf);
			return resp;
		}
	}
	
	private Packet rcvMessage(DatagramChannel channel, ByteBuffer buf){
		buf.clear();
		try {
			SocketAddress router = channel.receive(buf);
		} catch (IOException e1) {
			System.out.println("Channel exception at rcvMessage! " + e1.getMessage());
			try {
				channel = DatagramChannel.open();
				channel.bind(new InetSocketAddress(9999));
			} catch (IOException e) {
				System.out.println("Channel exception at rcvMessage!" + e1.getMessage());
			}
		}	
		buf.flip();
        Packet packet = null;
		try {
			packet = Packet.fromBuffer(buf);
		} catch (IOException e) {	
			
		}
		buf.flip();	
		return packet;
	}
	
	private String runClient(InetSocketAddress serverAddr, String msg) throws IOException{
//		try(DatagramChannel channel = DatagramChannel.open()){
		try{
			channel = DatagramChannel.open();
//			channel.bind(new InetSocketAddress(9999));
			Packet p = new Packet.Builder()
					.setPortNumber(serverAddr.getPort())
					.setPeerAddress(serverAddr.getAddress())
					.create();					
			Packet pkt = ThreeHandShaking(channel, p);
			while (pkt == null) {
				pkt = ThreeHandShaking(channel, p);
			}			
			String response = transData(channel, pkt, msg);
			return response;
		}catch (IOException e) {
			e.printStackTrace();
			System.out.println("Channel closed!");
		}
		return "Channel closed!.....";
	}
	
	private Packet ThreeHandShaking(DatagramChannel channel, Packet p) throws IOException{	     
		long sequenceNumber = 0L;
		p = p.toBuilder().setType(Packet.SYN).setSequenceNumber(sequenceNumber).setPayload("".getBytes()).create();
		Packet packet = null;
		while (packet == null) {
			channel.send(p.toBuffer(), Packet.routerAddress);
			packet = rcvMessage_withTime(channel);
		}
		System.out.println("[Send SYN]");
		int get_type = packet.getType();
		if (get_type != Packet.SYN_ACK) {
			System.out.println("Three-way handshaking failed (not SYN_ACK)!");
			return null;
		} else {
			System.out.println("[Receive SYN_ACK]");
			long get_seqNum = packet.getSequenceNumber();
			String get_payload = new String(packet.getPayload(), StandardCharsets.UTF_8);
			long get_payload_ack = Long.parseLong(get_payload);
			if (get_payload_ack == (sequenceNumber + 1)) {
				packet = packet.toBuilder().setType(Packet.SYN_ACK_ACK).setSequenceNumber(sequenceNumber).setPayload(((get_seqNum + 1) + "").getBytes()).create();				
				channel.send(packet.toBuffer(), Packet.routerAddress);
				System.out.println("[Send SYN_ACK_ACK]");
				Packet pkt = rcvMessage_withTime(channel);
				if(pkt != null) {
					int get_type1 = pkt.getType();
					if(get_type1 == Packet.SYN_ACK_ACK) {
						System.out.println("==========Three-way handshaking success!!!==========");
						return pkt;
					}else {
						System.out.println("Three-way handshaking failed (wrong ack ack)!");
						return null;
					}
				}else {
					System.out.println("Three-way handshaking failed (no pkt)!");
					return null;
				}
			} else {
				System.out.println("Three-way handshaking failed (wrong ack number)!");
				return null;
			}
		}
	}
	private int slide_window() {
		int offset = 0;
		for (int i = 0; i < send_window.size(); i++) {
			Timer pTimer = send_window.get(i);
			if(pTimer.isAcked()) {
				offset++;
			}else {
				break;
			}
		}
		return offset;
	}
	
	private void process_msg(Packet packet) {
		int get_type = packet.getType();
		long get_seqNum = packet.getSequenceNumber();
		String get_payload = new String(packet.getPayload(), StandardCharsets.UTF_8);
		long get_payload_ack = 0L;
		if(get_type == Packet.ACK) {
			if(!finish_send) {
				get_payload_ack = Long.parseLong(get_payload);
				System.out.println("[Receive Ack]: "+get_payload_ack);
				for (int i = 0; i < send_window.size(); i++) {
					System.out.println("[Check send_window]: " + i);
					Timer pTimer = send_window.get(i);
					if((pTimer.isAcked() == false) && (pTimer.getSequenceNumber() == (get_payload_ack-1))) {
						pTimer.setAcked(true);
						break;
					}
				}	
			}
		}else if(get_type == Packet.FIN_ACK) {
			System.out.println("[Receive Fin_Ack]: " + packet);
			pFin.setAcked(true);	
			server_start = get_seqNum+1;
			server_end = server_start + Packet.WINDOW;			
		} else if (get_type == Packet.DATA) {
			System.out.println("[Receive Data packet]: "+ packet);
//			System.out.println("----------receive seq:"+get_seqNum+"  start:"+server_start+"  end:"+server_end);
			if(get_seqNum < server_end) {
				packet = packet.toBuilder().setType(Packet.ACK).setPayload((get_seqNum + 1 + "").getBytes()).create();	
				receive_queue.add(packet);
//				System.out.println("receive_queue size:"+receive_queue.size());
			}
			if(get_seqNum >= server_start && get_seqNum < server_end) {
				rcv_window.set((int)(get_seqNum - server_start), get_payload);
//				System.out.println("receive_win size:"+rcv_window.size());
			}			
		} else if (get_type == Packet.FIN) {
			System.out.println("[Receive FIN]: " + packet);
			finish_receive = get_seqNum + 1;
		} 		
	}
	
	
	private String transData(DatagramChannel channel, Packet p, String msg){	    
		long  sequenceNumber= Long.parseLong(new String(p.getPayload(), StandardCharsets.UTF_8));		
		p = p.toBuilder().setType(Packet.DATA).setSequenceNumber(sequenceNumber).create();

		Packet[] packets_data = chunkPacket.makeChunks(msg, p);	
		
		for (int i = 0; i < packets_data.length; i++) {
			Timer pTimer = new Timer(channel, packets_data[i],true);
			queue.put(pTimer.getSequenceNumber(), pTimer);
		}
		
		Thread listen_ack = new Thread(new Runnable() {			
			@Override
			public void run() {
				System.out.println("Listening from server...");
				ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN).order(ByteOrder.BIG_ENDIAN);
				while(true) {
					Packet receive_packet = rcvMessage(channel, buf);	
					if(receive_packet != null){
						process_msg(receive_packet);
					}
				}				
			}
		});
		
		
		while(!finish_send) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(!send_window.isEmpty()) {
				 int offset = slide_window();
				 if(offset > 0) {
					 System.out.println("Offset: "+offset);
					for (int j = 0; j < offset; j++) {
						send_window.remove(0);
					}
					boolean add_to_window = false;
					long remain_queue_size = queue.size() - queue_end - offset;  
					if((remain_queue_size <= 0) && ((queue.size() - queue_end) > 0)) {
						queue_start = queue_end;
						queue_end = queue.size();
						add_to_window = true;
					}else if(remain_queue_size > 0) {
						queue_start = queue_end;
						queue_end = queue_end + offset;
						add_to_window = true;	
					}else if(send_window.isEmpty() && (queue.size() - queue_end == 0)) {
						finish_send = true;
						p = p.toBuilder().setType(Packet.FIN).setSequenceNumber(packets_data[packets_data.length-1].getSequenceNumber()+1).setPayload("".getBytes()).create();												
						try {
							channel.send(p.toBuffer(), Packet.routerAddress);
							System.out.println("[Send FIN to server]");
						} catch (IOException e) {
							System.out.println("Channel DatagramChannel closed at send Fin!");
						}		
						pFin = new Timer(channel, p,true);
						new Thread(pFin).start();
						break;
					}
					
					
					if(add_to_window) {
//						System.out.println("2%%%%%------queue_start: "+ queue_start);
//						System.out.println("2%%%%%------queue_end: "+ queue_end);
						for(long j = sequenceNumber + queue_start; j < sequenceNumber + queue_end; j++) {
							
							send_window.add(queue.get(j));
							Timer pTimer = queue.get(j);				
							try {
								channel.send(pTimer.getPacket().toBuffer(), Packet.routerAddress);
								System.out.println("[Send packet]: "+ j+"  "+pTimer.getPacket());
							} catch (IOException e) {
								System.out.println("Channel exception after send next window pkt!");
							}
							new Thread(pTimer).start();
						}
					}
				 }
			}else {
				//the first time put into window
				long remain_queue_size = queue.size() - Packet.WINDOW;  
				if(remain_queue_size <= 0 && queue.size() > 0) {
					queue_end = queue.size();
				}else {
					queue_end = Packet.WINDOW;
				}
				
//				System.out.println("1%%%%%------queue_start: "+ queue_start);
//				System.out.println("1%%%%%------queue_end: "+ queue_end);
				
				for(long j = sequenceNumber; j < sequenceNumber + queue_end; j++) {
//					System.out.println("1------add packet "+ j);
					send_window.add(queue.get(j));
					Timer pTimer = queue.get(j);
					if(j == sequenceNumber)
						listen_ack.start();				
					try {
						channel.send(pTimer.getPacket().toBuffer(), Packet.routerAddress);
						System.out.println("[Send packet]: "+ j+"  "+pTimer.getPacket());
					} catch (IOException e) {
						System.out.println("Channel exception after send first window pkts!");
					}					
					new Thread(pTimer).start();
					
				}
			}			
		}
		
		long  ack_sequenceNumber= sequenceNumber + packets_data.length + 1;
		String response = "";
		
		System.out.println("Waiting for data...");
		while(finish_receive<0L) {
			while(receive_queue.isEmpty()&&finish_receive<0L) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			while(!receive_queue.isEmpty()) {
				p = receive_queue.get(0);
				p = p.toBuilder().setSequenceNumber(ack_sequenceNumber).create();												
				try {
					channel.send(p.toBuffer(), Packet.routerAddress);
					System.out.println("[Send ACK with ack_num]: "+ Long.parseLong(new String(p.getPayload(), StandardCharsets.UTF_8)));
				} catch (IOException e) {
					System.out.println("Ack data channel closed");
				}		
				receive_queue.remove(0);
				ack_sequenceNumber++;
			}

			if(rcv_window.get(0)!=null||rcv_window.get(1)!=null||rcv_window.get(2)!=null||rcv_window.get(3)!=null) {
				int offset = 0;
				for (int i = 0; i < rcv_window.size(); i++) {
					if(rcv_window.get(i) != null) {
						server_start++;
						server_end++;
						offset++;
					}else {
						break;
					}
				}
				for (int i = 0; i < offset; i++) {
					response += rcv_window.get(0);
					rcv_window.remove(0);
					rcv_window.add(null);
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}	

		listen_ack.stop();
		p = p.toBuilder().setType(Packet.FIN_ACK).setSequenceNumber(ack_sequenceNumber).setPayload((finish_receive+"").getBytes()).create();															
		try {
			Packet packet = null;
			channel.send(p.toBuffer(), Packet.routerAddress);
			System.out.println("[Send FIN_ACK]");
			for (int i = 0; i < 6; i++) {
				packet = rcvMessage_withTime(channel);
				if (packet != null) {
					int get_type = packet.getType();
					if (get_type == Packet.FIN_ACK_ACK) {
						System.out.println("[Receive FIN_ACK_ACK]: " + packet);
						break;
					} else if (get_type == Packet.FIN) {
						System.out.println("[Receive FIN]: " + packet);
						channel.send(p.toBuffer(), Packet.routerAddress);
						System.out.println("[Send FIN_ACK]");
					}
				}
			}
			channel.disconnect();
			System.out.println("==========Communication finish!!! Channel disconnect==========");
		} catch (IOException e) {
			System.out.println("Ack fin data channel closed");
		}
		return response;
	}


	public String callUDP(String serverHost, int serverPort, String msg) throws IOException {
        InetSocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
        return runClient(serverAddress, msg);
    }


}