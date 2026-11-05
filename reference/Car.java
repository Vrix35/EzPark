import java.util.ArrayList;
import java.util.List;

public class Car extends Vehicle {
    public Car(String plateNumber, String schoolId, DriverRole role) {
        super(plateNumber, schoolId, role, VehicleType.CAR);
    }

    @Override
    public SpotType getPrimarySpotType() {
        switch (getDriverRole()) {
            case FACULTY: return SpotType.FACULTY;
            case PWD:     return SpotType.PWD;
            case VISITOR: return SpotType.VISITOR;
            default:      return SpotType.STUDENT;
        }
    }

    /** Smart fallback: overflow traffic is redirected, but PWD zones stay protected. */
    @Override
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
