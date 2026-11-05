public class ParkingSpot {
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
