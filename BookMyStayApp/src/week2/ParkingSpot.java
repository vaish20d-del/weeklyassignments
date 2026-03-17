import java.util.*;

class ParkingSpot {

    String licensePlate;
    long entryTime;
    boolean occupied;

    ParkingSpot() {
        occupied = false;
    }
}

class ParkingLot {

    private ParkingSpot[] table;
    private int size;

    public ParkingLot(int capacity) {
        table = new ParkingSpot[capacity];
        size = capacity;

        for (int i = 0; i < capacity; i++)
            table[i] = new ParkingSpot();
    }

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % size;
    }

    public int parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % size;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
        return index;
    }

    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (table[index].occupied) {

            if (plate.equals(table[index].licensePlate)) {

                long duration = System.currentTimeMillis() - table[index].entryTime;
                table[index].occupied = false;

                double hours = duration / 3600000.0;
                double fee = hours * 5;

                System.out.println("Spot #" + index + " freed. Fee: $" + fee);
                return;
            }

            index = (index + 1) % size;
        }

        System.out.println("Vehicle not found");
    }
}