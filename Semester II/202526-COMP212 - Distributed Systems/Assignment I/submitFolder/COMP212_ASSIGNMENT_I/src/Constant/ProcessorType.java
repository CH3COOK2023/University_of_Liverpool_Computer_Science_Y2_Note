package Constant;

public enum ProcessorType {
    /**
     * This processor is not an interface processor so it has no subnet ring.
     */
    NON_INTERFACE_PROCESSOR,
    /**
     * This processor is an interface processor so it has a subnet ring.
     */
    INTERFACE_PROCESSOR
}
