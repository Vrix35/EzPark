public class Motorcycle extends Vehicle {
    public Motorcycle(String plateNumber, String schoolId, DriverRole role) {
        super(plateNumber, schoolId, role, VehicleType.MOTORCYCLE);
    }

    public SpotType getPrimarySpotType() { return SpotType.MOTORCYCLE; }
}
