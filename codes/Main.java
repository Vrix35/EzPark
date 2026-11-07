import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ======================= ENUMS =======================
enum DriverRole { FACULTY, STUDENT, PWD, VISITOR }

enum VehicleType { CAR, MOTORCYCLE, TRUCK }

enum SpotType { FACULTY, PWD, STUDENT, VISITOR, MOTORCYCLE, TRUCK }

// ======================= EXCEPTIONS =======================
/** Thrown when no spot can be found for a vehicle. */
class ZoneFullException extends Exception {
    private final String message;

    public ZoneFullException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() { return message; }
}

/** Thrown when a school ID / permit number is invalid or the vehicle is already parked. */
class InvalidPermitException extends Exception {
    private final String message;

    public InvalidPermitException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() { return message; }
}

// ======================= VEHICLES =======================
/** Base class for every incoming vehicle (Inheritance + Polymorphism). */
abstract class Vehicle {
    private final String plateNumber;
    private final String schoolId;
    private final DriverRole driverRole;
    private final VehicleType vehicleType;

    protected Vehicle(String plateNumber, String schoolId, DriverRole driverRole, VehicleType vehicleType) {
        this.plateNumber = plateNumber;
        this.schoolId = schoolId;
        this.driverRole = driverRole;
        this.vehicleType = vehicleType;
    }

    public String getPlateNumber() { return plateNumber; }
    public String getSchoolId() { return schoolId; }
    public DriverRole getDriverRole() { return driverRole; }
    public VehicleType getVehicleType() { return vehicleType; }

    /** The zone type this vehicle is primarily entitled to. */
    public abstract SpotType getPrimarySpotType();

    /** Overflow zones allowed during peak hours (never includes PWD). */
    public List<SpotType> getFallbackSpotTypes() { return new ArrayList<>(); }

    /** Polymorphic zone check used by the router. */
    public boolean canParkInZone(ParkingZone zone) {
        return zone.getAllowedType() == getPrimarySpotType()
                || getFallbackSpotTypes().contains(zone.getAllowedType());
    }

    public String toString() {
        return plateNumber + " (" + vehicleType + ", " + driverRole + ")";
    }
}

class Car extends Vehicle {
    public Car(String plateNumber, String schoolId, DriverRole role) {
        super(plateNumber, schoolId, role, VehicleType.CAR);
    }

    public SpotType getPrimarySpotType() {
        switch (getDriverRole()) {
            case FACULTY: return SpotType.FACULTY;
            case PWD:     return SpotType.PWD;
            case VISITOR: return SpotType.VISITOR;
            default:      return SpotType.STUDENT;
        }
    }

    /** Smart fallback: overflow traffic is redirected, but PWD zones stay protected. */
    public List<SpotType> getFallbackSpotTypes() {
        List<SpotType> list = new ArrayList<>();
        switch (getDriverRole()) {
            case FACULTY: list.add(SpotType.STUDENT); break;
            case STUDENT: list.add(SpotType.VISITOR); break;
            default: break; // PWD and VISITOR have no overflow
        }
        return list;
    }
}

class Motorcycle extends Vehicle {
    public Motorcycle(String plateNumber, String schoolId, DriverRole role) {
        super(plateNumber, schoolId, role, VehicleType.MOTORCYCLE);
    }

    public SpotType getPrimarySpotType() { return SpotType.MOTORCYCLE; }
}

class Truck extends Vehicle {
    public Truck(String plateNumber, String schoolId, DriverRole role) {
        super(plateNumber, schoolId, role, VehicleType.TRUCK);
    }

    public SpotType getPrimarySpotType() { return SpotType.TRUCK; }
}

// ======================= PARKING SPOT / ZONE =======================
class ParkingSpot {
    private final String spotId;
    private final SpotType spotType;
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, SpotType spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
    }

    public String getSpotId() { return spotId; }
    public SpotType getSpotType() { return spotType; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    public boolean isAvailable() { return !isOccupied; }

    public void occupySpot(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
        this.isOccupied = true;
    }

    public void vacateSpot() {
        this.parkedVehicle = null;
        this.isOccupied = false;
    }
}

class ParkingZone {
    private final String zoneName;
    private final SpotType allowedType;
    private final List<ParkingSpot> spots = new ArrayList<>();
    private final int capacity;

    public ParkingZone(String zoneName, SpotType allowedType, String prefix, int capacity) {
        this.zoneName = zoneName;
        this.allowedType = allowedType;
        this.capacity = capacity;
        for (int i = 1; i <= capacity; i++) {
            spots.add(new ParkingSpot(String.format("%s-%02d", prefix, i), allowedType));
        }
    }

    public String getZoneName() { return zoneName; }
    public SpotType getAllowedType() { return allowedType; }
    public int getCapacity() { return capacity; }
    public List<ParkingSpot> getSpots() { return spots; }

    /** Returns the first free spot, or null if none. */
    public ParkingSpot findAvailableSpot() {
        for (ParkingSpot s : spots) {
            if (s.isAvailable()) return s;
        }
        return null;
    }

    public int getAvailableCount() {
        int n = 0;
        for (ParkingSpot s : spots) if (s.isAvailable()) n++;
        return n;
    }

    public boolean isZoneFull() { return getAvailableCount() == 0; }
}

// ======================= TICKET =======================
class Ticket {
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

// ======================= PARKING LOT MANAGER =======================
/** Central controller: validates, routes, releases, and shows the live board. */
class ParkingLotManager {
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

// ======================= MAIN PROGRAM =======================
public class Main {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        ParkingLotManager manager = new ParkingLotManager();
        boolean running = true;

        while (running) {
            manager.displayLiveStatus();
            System.out.println("1: Vehicle Entry | 2: Vehicle Exit | 3: Exit Program");
            System.out.print("Select operation: ");
            String choice = in.nextLine().trim();

            switch (choice) {
                case "1": handleEntry(manager); break;
                case "2": handleExit(manager); break;
                case "3": running = false; System.out.println("Thank you for using EzPark!"); break;
                default:  System.out.println("Invalid option. Please choose 1, 2, or 3.");
            }
        }
    }

    private static void handleEntry(ParkingLotManager manager) {
        System.out.println("\n--- VEHICLE ENTRY ---");
        System.out.print("School ID / Permit No. (e.g. 25-06359 or V-1234): ");
        String id = in.nextLine().trim();

        DriverRole role = pickRole();
        VehicleType type = pickVehicleType();

        System.out.print("Plate Number: ");
        String plate = in.nextLine().trim().toUpperCase();

        Vehicle v;
        switch (type) {
            case MOTORCYCLE: v = new Motorcycle(plate, id, role); break;
            case TRUCK:      v = new Truck(plate, id, role); break;
            default:         v = new Car(plate, id, role);
        }

        try {
            Ticket t = manager.parkVehicle(v);
            System.out.println("\n[ACCESS GRANTED]");
            System.out.println("Ticket ID : " + t.getTicketId());
            System.out.println("Vehicle   : " + v);
            System.out.println("Spot ID   : " + t.getSpot().getSpotId());
            System.out.println("Entry Time: " + t.getEntryTimeText());
        } catch (InvalidPermitException e) {
            System.out.println("\n[ACCESS DENIED] " + e.getMessage());
        } catch (ZoneFullException e) {
            System.out.println("\n[ZONE FULL] " + e.getMessage());
        }
        pause();
    }

    private static void handleExit(ParkingLotManager manager) {
        System.out.println("\n--- VEHICLE EXIT ---");
        if (manager.getActiveTickets().isEmpty()) {
            System.out.println("No vehicles are currently parked.");
            pause();
            return;
        }
        System.out.println("Currently parked:");
        for (Ticket t : manager.getActiveTickets()) {
            System.out.println("  " + t.getTicketId() + " | " + t.getVehicle() + " | " + t.getSpot().getSpotId());
        }
        System.out.print("Enter plate number to exit: ");
        String plate = in.nextLine().trim();

        Ticket t = manager.releaseSpot(plate);
        if (t == null) {
            System.out.println("[ERROR] No parked vehicle found with plate '" + plate + "'.");
        } else {
            System.out.println("[EXIT CONFIRMED] Spot " + t.getSpot().getSpotId() + " is now free. Safe travels!");
        }
        pause();
    }

    private static DriverRole pickRole() {
        while (true) {
            System.out.print("Role (1-Faculty 2-Student 3-PWD 4-Visitor): ");
            switch (in.nextLine().trim()) {
                case "1": return DriverRole.FACULTY;
                case "2": return DriverRole.STUDENT;
                case "3": return DriverRole.PWD;
                case "4": return DriverRole.VISITOR;
                default:  System.out.println("Invalid choice.");
            }
        }
    }

    private static VehicleType pickVehicleType() {
        while (true) {
            System.out.print("Vehicle Type (1-Car 2-Motorcycle 3-Truck): ");
            switch (in.nextLine().trim()) {
                case "1": return VehicleType.CAR;
                case "2": return VehicleType.MOTORCYCLE;
                case "3": return VehicleType.TRUCK;
                default:  System.out.println("Invalid choice.");
            }
        }
    }

    private static void pause() {
        System.out.print("\nPress Enter to return to the main menu...");
        in.nextLine();
    }
}
