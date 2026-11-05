public class Truck extends Vehicle {
    public Truck(String plateNumber, String schoolId, DriverRole role) {
        super(plateNumber, schoolId, role, VehicleType.TRUCK);
    }

    @Override
    public SpotType getPrimarySpotType() { return SpotType.TRUCK; }
}
