import java.util.Scanner;

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