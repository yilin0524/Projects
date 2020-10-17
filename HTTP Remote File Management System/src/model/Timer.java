package model;

import java.io.IOException;
import java.nio.channels.DatagramChannel;

public class Timer implements Runnable{
    public static final int TIME_OUT = 5000;
	private long  sequenceNumber;
    private Packet  packet;
    private boolean acked;
	private DatagramChannel channel;
	private boolean V;

    public Timer(DatagramChannel channel, Packet packet, boolean V) {
    	this.sequenceNumber = packet.getSequenceNumber();
    	this.channel = channel;
        this.packet   = packet;
        this.acked = false;
        this.V = V;
    }

    @Override
    public void run() {
        while(this.acked == false) {
	    	try {
	            Thread.sleep(TIME_OUT);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	if(this.acked == false) {
	    		try {
					this.channel.send(this.packet.toBuffer(), Packet.routerAddress);
					if(V)
						System.out.println("[Resend packet]: type=" + this.packet.getType()+"(SYN=0;SYN_ACK=1;SYN_ACK_ACK=2;DATA=3;ACK=4;FIN=5;FIN_ACK=6;FIN_ACK_ACK=7), seq=" + this.sequenceNumber);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}else {
	    		break;
	    	}
    	}
    }

    public long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public boolean isAcked() {
		return acked;
	}
    public void setAcked(boolean acked) {
		this.acked = acked;
	}
}
