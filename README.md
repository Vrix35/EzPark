<h1 align="center">EzPark</h1>

<p align="center"><em>Add your tagline here.</em></p>

## Project Overview

EzPark is a Java campus parking management system that assigns parking spots to incoming vehicles based on who is driving and what they are driving. It has a Swing desktop interface with a live occupancy dashboard, and a console version of the same logic.

When a vehicle enters, EzPark:

1. **Validates the permit.** Students and faculty must use a school ID in the format `25-06359`, and visitors must use a permit like `V-1234`. A plate that is already parked is rejected.
2. **Assigns a spot.** The vehicle is routed to its own zone first. If that zone is full, it may be redirected to an overflow zone (faculty to the Student Lot, students to the Visitor Lot). PWD and visitor vehicles have no overflow, so PWD spaces stay protected.
3. **Issues a ticket.** Each ticket has a unique ID, the plate number, the assigned spot, and the entry time.
4. **Updates the Live Campus Occupancy Board.** The board shows open and total spots per zone and marks each zone as Available or FULL. Releasing a vehicle by plate number frees its spot.

---

## Project Features

- **Role-based parking:** Spots are assigned by driver role (Faculty, Student, PWD, Visitor) and vehicle type (Car, Motorcycle, Truck), so each vehicle is routed to the right zone automatically.
- **Six dedicated zones:** Faculty Lot, PWD Zone, Student Lot, Visitor Lot, Motorcycle Bay, and Delivery Bay, with 28 spots in total.
- **Permit and ID validation:** School IDs must follow the format `25-06359` and visitor permits the format `V-1234`. Empty plate numbers are rejected.
- **Duplicate plate detection:** A vehicle that is already parked cannot be registered a second time.
- **Smart overflow parking:** When a zone is full, faculty vehicles are redirected to the Student Lot and student vehicles to the Visitor Lot. PWD and visitor vehicles never overflow, so PWD spaces stay protected.
- **Automatic ticketing:** Every entry generates a unique ticket ID with the plate number, assigned spot ID, and entry time.
- **Vehicle exit and spot release:** Selecting a parked vehicle or entering its plate number frees its spot right away.
- **Live Campus Occupancy Board:** Shows open and total spots per zone, marks each zone as Available or FULL, and displays overall lot totals.
- **Swing desktop interface:** A graphical dashboard with occupancy summary cards, a zone status table, an active tickets table, and a vehicle registration form.
- **Console version:** A menu-driven mode (Vehicle Entry, Vehicle Exit, Exit Program) that uses the same parking logic.
- **Clear error handling:** Custom exceptions give messages like "ACCESS DENIED" for invalid permits and "ZONE FULL" when no spot is available.

---

## OOP Principles

### Abstraction

- `Vehicle` is an abstract class. It declares `getPrimarySpotType()` without saying how each vehicle type decides its zone.
- `ParkingLotManager.parkVehicle()` hides the whole entry process (validate, assign spot, create ticket) behind one method call.

### Encapsulation

- The fields in `Vehicle`, `ParkingSpot`, `ParkingZone`, and `Ticket` are `private`, and other classes access them only through getters.
- A spot's occupied state can only be changed through `occupySpot()` and `vacateSpot()`, so it can't be set to an invalid state from outside the class.

### Inheritance

- `Car`, `Motorcycle`, and `Truck` all extend `Vehicle`. They reuse its plate number, school ID, driver role, and vehicle type, and add only what is specific to them.
- `ZoneFullException` and `InvalidPermitException` extend `Exception` to give the system its own error types.

### Polymorphism

- Each subclass overrides `getPrimarySpotType()`: `Car` chooses a zone by driver role, `Motorcycle` always returns the motorcycle bay, and `Truck` always returns the delivery bay.
- `Car` also overrides `getFallbackSpotTypes()` to define overflow rules, while other vehicles use the default of none.
- `Vehicle.canParkInZone()` and `ParkingLotManager.assignSpot()` work on any `Vehicle` and call the right behavior at runtime, so a new vehicle type needs no changes to the manager.

### Additional Design Practices

- **Composition:** `ParkingLotManager` has `ParkingZone`s, each zone has `ParkingSpot`s, and each `Ticket` holds a `Vehicle` and a `ParkingSpot`.
- **Enums:** `DriverRole`, `SpotType`, and `VehicleType` give fixed, type-safe values instead of loose strings.
- **Custom exceptions:** Invalid permits and full zones are reported with clear messages instead of silent failures.

---

## Project Structure (including UML)

_Add your folder structure and UML diagram here._

---

## How to Run the Program

_Add the run instructions here._

---

## Pictures (Screenshot Output Highlights)

_Add your screenshots here._

---

## Thank You

_Add your thank-you message here._

---

## Team Members

- Vrix
- Shem
- Ace

---

## Acknowledgements

_Add your acknowledgements here._
