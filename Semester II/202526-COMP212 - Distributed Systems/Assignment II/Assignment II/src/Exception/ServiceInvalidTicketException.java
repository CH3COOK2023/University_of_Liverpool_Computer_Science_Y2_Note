package Exception;

public class ServiceInvalidTicketException extends RuntimeException {
    public ServiceInvalidTicketException(String message) {
        super(message);
    }
}
