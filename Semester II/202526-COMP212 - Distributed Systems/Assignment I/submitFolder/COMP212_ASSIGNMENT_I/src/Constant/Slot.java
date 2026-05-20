package Constant;

public class Slot {
    /**
     * If the processor want to send data to next(or prev) processor, use this type.
     */
    public static final int RECEIVE_SLOT = 0;
    /**
     * If the processor want to send termination signal to next(or prev) processor, use this type.
     */
    public static final int TERMINATION_SLOT = 1;
    /**
     * If the processor want to tell next processor that it wakes up, use this type.
     */
    public static final int TELL_NEXT_ACTIVATION_SIGN_SLOT = 2;
    /**
     * If the processor A want to tell next processor B that A received B's wake up signal, use this type
     */
    public static final int ACK_NEXT_ACTIVATION_SIGN_SLOT = 3;
    /**
     * If the processor want to tell prev processor that it wakes up, use this type.
     */
    public static final int TELL_PREV_ACTIVATION_SIGN_SLOT = 4;
    /**
     * If the processor A want to tell prev processor B that A received B's wake up signal, use this type
     */
    public static final int ACK_PREV_ACTIVATION_SIGN_SLOT = 5;
}
