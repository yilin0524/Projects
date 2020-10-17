package model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class receiveClient implements Runnable{
	private InetSocketAddress id = null;
	private int connect = 0;
	long sequenceNumber = 6001L;
	long handshake_sequenceNumber = 6000L;
	long fin_ack_sequenceNumber = 6000L;
	private ArrayList<Packet> receive_queue = new ArrayList<>();
	private String request = "";
	private String response = null;
	private long client_start = 0L;
	private long client_end = 0L;
	private ArrayList<String> rcv_window = new ArrayList<>();
	private boolean finish_receive = false;
	private boolean complete_receive = false;
	private Packet packet = null;
	private boolean V = false;
	private Thread cThread = null;	
	private HashMap<Long, Timer> queue = new HashMap<>();		
	private ArrayList<Timer> send_window = new ArrayList<>();	
	private boolean finish_send = false;	
	private int queue_start = 0;
	private int queue_end = 1;	
	private Timer pFin;
	private DatagramChannel channel;

	public receiveClient(DatagramChannel channel, InetSocketAddress address, long handshake_sequenceNumber, boolean V) {		
		this.channel = channel;
		this.id = address;
		this.V = V;
		this.handshake_sequenceNumber = handshake_sequenceNumber;
		packet = new Packet.Builder().setPortNumber(id.getPort()).setPeerAddress(id.getAddress()).create();	
		for (int i = 0; i < Packet.WINDOW; i++) {
			rcv_window.add(null);
		}
	}
	
	@Override
	public void run() {
		if(V)
			System.out.println("[New client connect!]: " + id);
		while(receive_queue.size() == 0 && rcv_window.get(0)==null && rcv_window.get(1)==null && rcv_window.get(2)==null && rcv_window.get(3)==null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		while(!finish_receive || rcv_window.get(0)!=null || rcv_window.get(1)!=null || rcv_window.get(2)!=null || rcv_window.get(3)!=null) {					
			processRequest();
		}
		setComplete_receive(true);
		while(response == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		if(response != null) {
			packet = packet.toBuilder().setType(Packet.DATA).setSequenceNumber(sequenceNumber).create();				
			Packet[] packets_data = chunkPacket.makeChunks(this.getResponse(), packet);		
			for (int i = 0; i < packets_data.length; i++) {
				Timer pTimer = new Timer(channel, packets_data[i], V);
				queue.put(pTimer.getSequenceNumber(), pTimer);
			}				
			sendResponse();	
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
	
	public void sendResponse() {
		while (!finish_send) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
			}
			if (!send_window.isEmpty()) {
				int offset = slide_window();
				if (offset > 0) {
					for (int j = 0; j < offset; j++) {
						send_window.remove(0);
					}
					boolean add_to_window = false;
					long remain_queue_size = queue.size() - queue_end - offset;
					
					if ((remain_queue_size <= 0) && ((queue.size() - queue_end) > 0)) {
						queue_start = queue_end;
						queue_end = queue.size();
						add_to_window = true;
					} else if (remain_queue_size > 0) {
						queue_start = queue_end;
						queue_end = queue_end + offset;
						add_to_window = true;
					} else if (send_window.isEmpty() && (queue.size() - queue_end == 0)) {
						finish_send = true;						
						sequenceNumber = sequenceNumber + queue.size();
						packet = packet.toBuilder().setType(Packet.FIN)
								.setSequenceNumber(sequenceNumber)
								.setPayload("".getBytes()).create();
						sequenceNumber++;
						try {
							channel.send(packet.toBuffer(), Packet.routerAddress);
							if(V)
								System.out.println("[Send FIN]: " + packet);
						} catch (IOException e) {
							if(V)
								System.out.println("channel DatagramChannel closed at send Fin!");
						}
						pFin = new Timer(channel, packet, V);
						new Thread(pFin).start();
						break;
					}

					if (add_to_window) {
						for (long j = sequenceNumber + queue_start; j < sequenceNumber + queue_end; j++) {
							send_window.add(queue.get(j));
							Timer pTimer = queue.get(j);
							try {
								channel.send(pTimer.getPacket().toBuffer(), Packet.routerAddress);
								if(V)
									System.out.println("[Send DATA]: " + queue.get(j).getSequenceNumber() + " to Client " + id);
							} catch (IOException e) {
								if(V)
									System.out.println("channel exception after send next window pkt!");
							}
							new Thread(pTimer).start();
						}
					}
				}
			} else {
				// the first time put into window
				long remain_queue_size = queue.size() - Packet.WINDOW;
				if (remain_queue_size <= 0 && queue.size() > 0) {
					queue_end = queue.size();
				} else {
					queue_end = Packet.WINDOW;
				}

				for (long j = sequenceNumber; j < sequenceNumber + queue_end; j++) {
					send_window.add(queue.get(j));
					Timer pTimer = queue.get(j);
					try {
						channel.send(pTimer.getPacket().toBuffer(), Packet.routerAddress);
						if(V)
							System.out.println("[Send DATA]: " + queue.get(j).getSequenceNumber() + " to Client " + id);
					} catch (IOException e) {
						if(V)
							System.out.println("channel exception after send first window pkts!");
					}
					new Thread(pTimer).start();
				}
			}
		}
	}
	
	public void processRequest() {	
		while(!receive_queue.isEmpty()) {
			Packet pkt = receive_queue.get(0);
			System.err.println(pkt.getSequenceNumber());
			long get_seqNum = pkt.getSequenceNumber();
			
			pkt = pkt.toBuilder().setType(Packet.ACK).setSequenceNumber(sequenceNumber).setPayload(((get_seqNum + 1) + "").getBytes()).create();												
			try {
				channel.send(pkt.toBuffer(), Packet.routerAddress);	
				if(V)
					System.out.println("[Send ACK]: " + (get_seqNum + 1) + " to Client " + pkt);			
			} catch (IOException e) {
				if(V)
					System.out.println("[Ack data channel closed!]");
			}		
			receive_queue.remove(0);
			sequenceNumber++;
			fin_ack_sequenceNumber = sequenceNumber;
		}
		
		if (rcv_window.get(0)!=null || rcv_window.get(1)!=null || rcv_window.get(2)!=null || rcv_window.get(3)!=null) {
			int offset = 0;
			for (int i = 0; i < rcv_window.size(); i++) {
				if (rcv_window.get(i) != null) {
					client_start++;
					client_end++;
					offset++;
				} else {
					break;
				}
			}
			for (int i = 0; i < offset; i++) {
				request += rcv_window.get(0);
				rcv_window.remove(0);
				rcv_window.add(null);
			}
		}
	}	
	
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public boolean isComplete_receive() {
		return complete_receive;
	}

	public void setComplete_receive(boolean complete_receive) {
		this.complete_receive = complete_receive;
	}

	public boolean isFinish_receive() {
		return finish_receive;
	}

	public void setFinish_receive(boolean finish_receive) {
		this.finish_receive = finish_receive;
	}

	public ArrayList<String> getRcv_window() {
		return rcv_window;
	}

	public void setRcv_window(int index, String payload) {
		this.rcv_window.set(index, payload);
	}

	public long getClient_start() {
		return client_start;
	}

	public void setClient_start(long client_start) {
		this.client_start = client_start;
	}

	public long getClient_end() {
		return client_end;
	}

	public void setClient_end(long client_end) {
		this.client_end = client_end;
	}

	public ArrayList<Packet> getReceive_queue() {
		return receive_queue;
	}

	public void addReceive_queue(Packet receive_pkt) {
		this.receive_queue.add(receive_pkt);
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public int getConnect() {
		return connect;
	}

	public void setConnect(int connect) {
		this.connect = connect;
	}
	
	public boolean isFinish_send() {
		return finish_send;
	}

	public void setFinish_send(boolean finish_send) {
		this.finish_send = finish_send;
	}
	
	public HashMap<Long, Timer> getQueue() {
		return queue;
	}

	public void setQueue(HashMap<Long, Timer> queue) {
		this.queue = queue;
	}
		
	public ArrayList<Timer> getSend_window() {
		return send_window;
	}

	public void setSend_window(ArrayList<Timer> send_window) {
		this.send_window = send_window;
	}
	
	public long getHandshake_sequenceNumber() {
		return handshake_sequenceNumber;
	}


	public void setHandshake_sequenceNumber(long handshake_sequenceNumber) {
		this.handshake_sequenceNumber = handshake_sequenceNumber;
	}
	
	public Thread getcThread() {
		return cThread;
	}


	public void setcThread(Thread cThread) {
		this.cThread = cThread;
	}

	
	public Timer getpFin() {
		return pFin;
	}


	public void setpFin(Timer pFin) {
		this.pFin = pFin;
	}
	
	public long getFin_ack_sequenceNumber() {
		return fin_ack_sequenceNumber;
	}


	public void setFin_ack_sequenceNumber(long fin_ack_sequenceNumber) {
		this.fin_ack_sequenceNumber = fin_ack_sequenceNumber;
	}

}
