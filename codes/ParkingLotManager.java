import java.util.ArrayList;
import java.util.List;

/** Central controller: validates, routes, releases, and shows the live board. */
public class ParkingLotManager {
    private final List<ParkingZone> zones = new ArrayList<>();
    private final List<Ticket> activeTickets = new ArrayList<>();
    private int totalCapacity;

    public ParkingLotManager() {
        addZone(new ParkingZone("Faculty Lot",    SpotType.FACULTY,    "FAC", 5));
        addZone(new ParkingZone("PWD Zone",       SpotType.PWD,        "PWD", 3));
        addZone(new ParkingZone("Student Lot",    SpotType.STUDENT,    "STU", 8));
        addZone(new ParkingZone("Visitor Lot",    SpotType.VISITOR,    "VIS", 4));
        addZone(new ParkingZone("Motorcycle Bay", SpotType.MOTORCYCLE, "MOT", 6));
        addZone(new ParkingZone("Delivery Bay",   SpotType.TRUCK,      "TRK", 2));
    }

    private void addZone(ParkingZone z) {
        zones.add(z);
        totalCapacity += z.getCapacity();
    }

    /** validatePermit(): checks ID/permit format and duplicate plates. */
    public void validatePermit(Vehicle v) throws InvalidPermitException {
        if (v.getPlateNumber().isEmpty()) {
            throw new InvalidPermitException("Plate number cannot be empty.");
        }
        String id = v.getSchoolId();
        if (v.getDriverRole() == DriverRole.VISITOR) {
            if (!id.matches("V-\\d{3,5}")) {
                throw new InvalidPermitException("Invalid visitor permit '" + id + "'. Expected format: V-1234");
            }
        } else if (!id.matches("\\d{2}-\\d{5}")) {
            throw new InvalidPermitException("Invalid school ID '" + id + "'. Expected format: 25-06359");
        }
        for (Ticket t : activeTickets) {
            if (t.getVehicle().getPlateNumber().equalsIgnoreCase(v.getPlateNumber())) {
                throw new InvalidPermitException("Vehicle " + v.getPlateNumber() + " is already parked.");
            }
        }
    }

    private ParkingZone zoneFor(SpotType type) {
        for (ParkingZone z : zones) if (z.getAllowedType() == type) return z;
        return null;
    }

    /** assignSpot(): primary zone first, then peak-hour fallback zones. */
    public ParkingSpot assignSpot(Vehicle v) throws ZoneFullException {
        List<SpotType> order = new ArrayList<>();
        order.add(v.getPrimarySpotType());
        order.addAll(v.getFallbackSpotTypes());

        for (SpotType type : order) {
            ParkingZone zone = zoneFor(type);
            if (zone != null && v.canParkInZone(zone)) {
                ParkingSpot spot = zone.findAvailableSpot();
                if (spot != null) {
                    spot.occupySpot(v);
                    return spot;
                }
            }
        }
        throw new ZoneFullException("No available spot for " + v.getDriverRole() + " " + v.getVehicleType()
                + ". " + zoneFor(v.getPrimarySpotType()).getZoneName() + " is full.");
    }

    /** parkVehicle(): high-level abstraction for the whole entry process. */
    public Ticket parkVehicle(Vehicle v) throws InvalidPermitException, ZoneFullException {
        validatePermit(v);
        ParkingSpot spot = assignSpot(v);
        Ticket ticket = new Ticket(v, spot);
        activeTickets.add(ticket);
        return ticket;
    }

    /** releaseSpot(): frees the spot of the vehicle with the given plate number. */
    public Ticket releaseSpot(String plateNumber) {
        for (Ticket t : activeTickets) {
            if (t.getVehicle().getPlateNumber().equalsIgnoreCase(plateNumber)) {
                t.getSpot().vacateSpot();
                activeTickets.remove(t);
                return t;
            }
        }
        return null;
    }

    /** displayLiveStatus(): the Live Campus Occupancy Board. */
    public void displayLiveStatus() {
        int free = 0;
        System.out.println("\n==================================================");
        System.out.println("        EZPARK - LIVE CAMPUS OCCUPANCY BOARD");
        System.out.println("==================================================");
        System.out.printf("%-16s %-10s %s%n", "ZONE", "OPEN", "STATUS");
        System.out.println("--------------------------------------------------");
        for (ParkingZone z : zones) {
            int open = z.getAvailableCount();
            free += open;
            System.out.printf("%-16s %2d / %-5d %s%n", z.getZoneName(), open, z.getCapacity(),
                    z.isZoneFull() ? "FULL" : "Available");
        }
        System.out.println("--------------------------------------------------");
        System.out.printf("%-16s %2d / %-5d%n", "TOTAL", free, totalCapacity);
        System.out.println("==================================================");
    }

    public List<Ticket> getActiveTickets() { return activeTickets; }
}