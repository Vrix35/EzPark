import java.util.ArrayList;
import java.util.List;

public class ParkingZone {
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
