// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import java.awt.*;

public class Entrance {

    private final int id;
    private final Point[] entrancesCoordinates;

    Entrance(int id, Point[] entrancesCoordinates) {
        this.id = id;
        this.entrancesCoordinates = entrancesCoordinates;
    }

    public int size() {
        return this.entrancesCoordinates.length;
    }

    public int getId() {
        return this.id;
    }

    public Point[] getEntrancesCoordinates() {
        return this.entrancesCoordinates;
    }
}
