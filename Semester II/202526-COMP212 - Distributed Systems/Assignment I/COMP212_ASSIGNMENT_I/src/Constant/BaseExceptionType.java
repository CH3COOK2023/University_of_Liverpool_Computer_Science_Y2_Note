package Constant;

public class BaseExceptionType {
    /**
     * Thrown when different leaderIds are found while validating the leaderId field across the entire ring.
     */
    public static final String INCONSISTENT_LEADER_ID = "Inconsistent leader id found!";

    /**
     * Thrown when at least one processor in the ring still has an UNKNOWN status.
     */
    public static final String PROCESSOR_STATUS_NOT_DETERMINED = "Status of processor is UNKNOWN.";

    /**
     * Thrown when there is more than one processor with the LEADER status in the ring.
     */
    public static final String TOO_MANY_LEADER = "There are more than 1 leader in the ring.";

    /**
     * Thrown when no processor with the LEADER status is found in the ring.
     */
    public static final String LEADER_NOT_FOUND = "There are NO leader in the ring.";

    /**
     * Thrown when the number of processors with LOST status does not equal (total processors - 1).
     */
    public static final String WRONG_NUMBER_OF_LOST_PROCESSOR = "Wrong number of lost processor.";

    /**
     * Thrown when the simulator's ring is empty.
     */
    public static final String EMPTY_RING = "The ring of the simulator is empty.";

    /**
     * Thrown when the parameters are invalid, typically due to conflicting arguments.
     */
    public static final String ILLEGAL_PARAMETER = "The parameter is not acceptable.";

    /**
     * Thrown when the number of subnets exceeds the number of nodes in the main ring.
     */
    public static final String TOO_MANY_SUBNET = "The number of subnet is larger than the main ring.";

    /**
     * Thrown when the specified number of interface processors does not match the number of subnets.
     */
    public static final String NUMBER_NOT_MATCHING = "The number of interface processor is not equals to the number of subnets.";

    /**
     * Internal exception, thrown when the shuffle starting index exceeds the array bounds.
     */
    public static final String SHUFFLE_ID_OUT_OF_BOUND = "Shuffle id is out of bound.";

    /**
     * When Tools.Graph.java occurs an IOException.
     */
    public static final String FILE_IO_EXCEPTION = "Cannot write out to file!";
}
