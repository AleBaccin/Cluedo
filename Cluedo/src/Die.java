// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

public class Die {
    private static final int SIDES = 6;
    private int value;

    public int getValue() {
        return value;
    }

    public int roll() {
        value = (int) (Math.random() * SIDES) + 1;
        return value;
    }
}
