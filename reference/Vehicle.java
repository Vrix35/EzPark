import java.util.ArrayList;
import java.util.List;

/** Base class for every incoming vehicle (Inheritance + Polymorphism). */
public abstract class Vehicle {
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
