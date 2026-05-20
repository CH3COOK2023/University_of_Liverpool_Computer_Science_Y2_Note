package Constant;


public enum ProcessorStatus {
    /**
     * When the processor is waiting for its own (or its subnet) id or haven't received a greater id yet, it keeps the status UNKNOWN.
     */
    UNKNOWN,
    /**
     * When the processor received its own (or its subnet) id, it is the LEADER.
     */
    LEADER,
    /**
     * WHen the processor received a greater id, it is LOST in the election.
     */
    LOST
}
