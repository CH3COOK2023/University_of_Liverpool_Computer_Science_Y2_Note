/*************************************
 * Filename:  Receiver.java
 *************************************/

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Receiver extends NetworkHost {
    private static final String EMPTY_PAYLOAD = "";
    private Packet[] CACHE;
    private int received;
    private Set<Integer> FINISHED_LAST;

    public Receiver(int entityName, EventList events, double pLoss, double pCorrupt, int trace, Random random) {
        super(entityName, events, pLoss, pCorrupt, trace, random);
    }

    public static boolean isInWindows(int seq) {
        return seq >= Sender.WINDOWS_MIN_SEQ && seq <= Sender.WINDOWS_MAX_SEQ;
    }

    protected void Input(Packet packet) {
        if (!Sender.Verification.isBrokenPacket(packet)) {
            boolean packetNotTimeOut = !FINISHED_LAST.contains(Sender.WINDOWS_MAX_SEQ);
            if (CACHE == null && isInWindows(packet.getSeqnum()) && packetNotTimeOut)
                CACHE = new Packet[Sender.CURRENT_WINDOWS_SIZE];
            if (isInWindows(packet.getSeqnum()) && packetNotTimeOut) {
                int index = packet.getSeqnum() - Sender.WINDOWS_MIN_SEQ;
                if (CACHE[index] == null) received++;
                CACHE[index] = packet;
            }
            if (received == Sender.CURRENT_WINDOWS_SIZE && packetNotTimeOut) {
                for (Packet deliverPacket : CACHE) deliverData(deliverPacket.getPayload());
                received = 0;
                CACHE = null;
                FINISHED_LAST.add(Sender.WINDOWS_MAX_SEQ);
            }
            Packet ackBack = new Packet(packet);
            ackBack.setPayload(EMPTY_PAYLOAD);
            ackBack.setChecksum(Sender.Verification.hash(ackBack));
            udtSend(ackBack);
        }
    }

    protected void Init() {
        FINISHED_LAST = new HashSet<>();
    }
}
