package Exception;

public class ClientConnectionException extends RuntimeException {
    public ClientConnectionException(String message) {
        super(message);
    }
}
