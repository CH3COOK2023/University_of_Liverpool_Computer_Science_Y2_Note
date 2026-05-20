package Message;

public class Message {
    private final int slot;
    private final int data;
    private final int from;
    private final int to;

    /**
     * Construct a message.
     * @param slot The corresponding slot of the message.
     * @param data The data of the message.
     * @param from the id of sender
     * @param to the id of receiver
     */
    public Message(int slot, int data, int from, int to) {
        this.slot = slot;
        this.data = data;
        this.from = from;
        this.to = to;
    }

    public int type() {
        return slot;
    }

    public int data() {
        return data;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }
}
