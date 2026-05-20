/*************************************
 * Filename:  Sender.java
 *************************************/

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Sender extends NetworkHost {
    public static int WINDOWS_SIZE;
    public static int CURRENT_WINDOWS_SIZE;
    public static int WAITING_INTERVAL;
    public static int WINDOWS_MIN_SEQ;
    public static int WINDOWS_MAX_SEQ;
    boolean isSending;
    private List<Packet> QUEUE;
    private List<Packet> CACHE_SEND;
    private int SEQUENCE_NUMBER;

    public Sender(int entityName, EventList events, double pLoss, double pCorrupt, int trace, Random random) {
        super(entityName, events, pLoss, pCorrupt, trace, random);
    }

    public static boolean isInWindows(int seq) {
        return seq >= Sender.WINDOWS_MIN_SEQ && seq <= Sender.WINDOWS_MAX_SEQ;
    }

    protected void Output(Message message) {
        SEQUENCE_NUMBER++;
        Packet packet = new Packet(SEQUENCE_NUMBER, SEQUENCE_NUMBER, 0, message.getData());
        packet.setChecksum(Verification.hash(packet));
        QUEUE.add(packet);
        trySendPacket();
    }

    private void trySendPacket() {
        if (isSending) return;
        isSending = true;
        int fetchNumber = Math.min(WINDOWS_SIZE, QUEUE.size());
        for (int i = 0; i < fetchNumber; i++) {
            CACHE_SEND.add(QUEUE.remove(0));
        }
        CURRENT_WINDOWS_SIZE = CACHE_SEND.size();
        WINDOWS_MIN_SEQ = CACHE_SEND.get(0).getSeqnum();
        WINDOWS_MAX_SEQ = CACHE_SEND.get(CACHE_SEND.size() - 1).getSeqnum();
        for (Packet packet : CACHE_SEND) udtSend(packet);
        startTimer(WAITING_INTERVAL);
    }

    private void resentCache() {
        stopTimer();
        for (Packet packet : CACHE_SEND) udtSend(packet);
        startTimer(WAITING_INTERVAL);
    }

    protected void Input(Packet packet) {
        if (Verification.isBrokenPacket(packet)) {
            resentCache();
            return;
        }
        if (isInWindows(packet.getSeqnum())) for (int i = 0; i < CACHE_SEND.size(); i++)
            if (CACHE_SEND.get(i).getSeqnum() == packet.getSeqnum()) {
                CACHE_SEND.remove(i);
                break;
            }
        if (CACHE_SEND.isEmpty()) {
            stopTimer();
            isSending = false;
            if (!QUEUE.isEmpty()) trySendPacket();
        }
    }

    protected void TimerInterrupt() {
        resentCache();
    }

    protected void Init() {
        QUEUE = new ArrayList<>(128);
        CACHE_SEND = new ArrayList<>(128);
        SEQUENCE_NUMBER = -1;
        isSending = false;
        WINDOWS_SIZE = 5;
        WAITING_INTERVAL = 57;
    }

    public static final class Verification {
        public static int hash(Packet packet) {
            return Objects.hash(packet.getSeqnum(), packet.getAcknum(), packet.getPayload());
        }

        public static boolean isBrokenPacket(Packet packet) {
            return hash(packet) != packet.getChecksum();
        }
    }
}
