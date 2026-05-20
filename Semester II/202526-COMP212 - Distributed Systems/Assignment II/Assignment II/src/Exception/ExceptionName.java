package Exception;

public class ExceptionName {
    public static final String CLIENT_CONNECTION_EXCEPTION = "Cannot connect to RMI service.";
    public static final String CLIENT_REGISTRY_EXCEPTION = "RMI registry failed.";
    public static final String CLIENT_FETCH_TICKET_EXCEPTION = "Cannot get ticket from RMI service.";
    public static final String CLIENT_VOTING_FAIL_EXCEPTION = "Cannot vote to remote system.";
    public static final String CLIENT_GET_UUID_EXCEPTION = "Cannot get UUID from RMI service.";


    public static final String SERVICE_INVALID_TICKET_EXCEPTION = "Invalid ticket.";
    public static final String SERVICE_INVALID_CHOICE_EXCEPTION = "Invalid choice.";
    public static final String SERVICE_RESULT_ACCESS_CONTROL_EXCEPTION = "Results will only be displayed after voting.";
    public static final String SERVICE_INVALID_CONFIGURATION_ARGUMENT_EXCEPTION = "Invalid configuration argument.";

}
