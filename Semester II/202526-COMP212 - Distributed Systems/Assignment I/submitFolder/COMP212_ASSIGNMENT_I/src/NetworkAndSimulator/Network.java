package NetworkAndSimulator;

import Message.MessageSlot;

import java.util.HashMap;
import java.util.Map;

public class Network {
    private static final Map<Integer, MessageSlot> thisRound;
    private static final Map<Integer, MessageSlot> nextRound;
    private static int transmissionCounter;
    private static int roundCounter;

    static {
        transmissionCounter = 0;
        roundCounter = 0;
        thisRound = new HashMap<>();
        nextRound = new HashMap<>();
    }

    public static void reset() {
        transmissionCounter = 0;
        roundCounter = 0;
        thisRound.clear();
        nextRound.clear();
    }

    public static MessageSlot getMyMessage(int myId) {
        MessageSlot messageSlot = thisRound.getOrDefault(myId, null);
        if (messageSlot != null) transmissionCounter++;
        return thisRound.getOrDefault(myId, new MessageSlot());
    }

    public static int getRound() {
        return roundCounter;
    }

    public static int getTransmission() {
        return transmissionCounter;
    }

    public static void nextRound() {
        roundCounter++;
        thisRound.clear();
        thisRound.putAll(nextRound);
        nextRound.clear();
    }

    public static void send(int toId, MessageSlot messageSlot) {
        // 1. 获取目标已有的 Slot (如果没有则新建)
        MessageSlot existingSlot = nextRound.getOrDefault(toId, new MessageSlot());

        // 2. 将新的 messageSlot 内容合并到 existingSlot 中
        // 注意：这里需要你确保 MessageSlot.java 里有 merge 方法，并且逻辑正确
        existingSlot.merge(messageSlot, existingSlot);

        // 3. 放回 Map
        nextRound.put(toId, existingSlot);
    }

    public static void printInformation() {
        System.out.println("Round: " + roundCounter);
        System.out.println("Transmission: " + transmissionCounter);
    }
}
