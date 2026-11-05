/** Thrown when a school ID / permit number is invalid or the vehicle is already parked. */
public class InvalidPermitException extends Exception {
    private final String message;

    public InvalidPermitException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() { return message; }
}
