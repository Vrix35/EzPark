import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private static int counter = 1000;

    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;

    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = "T" + (++counter);
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
    }

    public String getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getSpot() { return spot; }

    public String getEntryTimeText() {
        return entryTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
