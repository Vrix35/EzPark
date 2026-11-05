/** Thrown when no spot can be found for a vehicle. */
public class ZoneFullException extends Exception {
    private final String message;

    public ZoneFullException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() { return message; }
}
