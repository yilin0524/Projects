package model;

import java.awt.print.Printable;
import java.nio.charset.StandardCharsets;

public class chunkPacket {
	public static void main(String[] args) {
		String msg = "abckdaf adsfklj klasdjf asdklfjewrwq asdfadsfljk asdklfj;sa";
		Packet packet = new Packet.Builder().setType(0).setSequenceNumber(1L).setPayload("133".getBytes()).create();
		Packet[] packets = chunkPacket.makeChunks(msg, packet);
		for (int i = 0; i < packets.length; i++) {
			System.out.println("---------"+i);
			System.out.println(packets[i].getPayload());
			System.out.println(new String(packets[i].getPayload(), StandardCharsets.UTF_8));
			
		}

	}
	
	public static Packet[] makeChunks(String msg, Packet p) {
		long sequenceNumber = p.getSequenceNumber();
		byte[] message = msg.getBytes();
        int msg_len = message.length;
        int packet_num = (int) Math.ceil((double)msg_len / (double)Packet.MAX_DATA);
        int offset = msg_len % Packet.MAX_DATA;
        int start = 0;
        Packet[] packets;
        packets = new Packet[packet_num];

        for (int i = 0; i < packet_num; i++) {
            byte[] temp = new byte[Packet.MAX_DATA];
            int len = (((i+1)*Packet.MAX_DATA) <= (msg_len - offset)) ? Packet.MAX_DATA : offset;
            System.arraycopy(message, start, temp, 0, len);
            start += len;        
            p = p.toBuilder().setSequenceNumber(sequenceNumber).setPayload(temp).create();
            sequenceNumber++;
            packets[i] = p;
        }
        return packets;
    }
}
