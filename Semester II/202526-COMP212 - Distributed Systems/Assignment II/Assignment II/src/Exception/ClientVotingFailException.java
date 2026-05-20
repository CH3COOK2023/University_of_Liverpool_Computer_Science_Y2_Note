package Exception;

public class ClientVotingFailException extends RuntimeException {
    public ClientVotingFailException(String message) {
        super(message);
    }
}
