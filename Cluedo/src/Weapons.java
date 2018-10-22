// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import java.awt.*;
import java.security.SecureRandom;

public enum Weapons implements Token {
    CANDLESTICK("candlestick", "Candlestick"),
    KNIFE("knife", "Knife"),
    LEAD_PIPE("pipe", "Pipe"),
    REVOLVER("revolver", "Revolver"),
    ROPE("rope", "Rope"),
    WRENCH("wrench", "Wrench");

    private final String weaponName;
    private final String displayName;
    private final WeaponLabel weaponLabel;

    Weapons(String weaponName, String displayName) {
        this.weaponName = weaponName;
        this.displayName = displayName;
        this.weaponLabel = new WeaponLabel(this.weaponName);
    }

    public static Weapons getRandomWeapon() {
        SecureRandom random = new SecureRandom();
        return values()[random.nextInt(values().length)];
    }

    @Override
    public String getName() {
        return weaponName;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    public WeaponLabel getLabel() {
        return weaponLabel;
    }

    public Point getLocation() {
        return weaponLabel.getLocation();
    }

    public void setLocation(int x, int y) {
        weaponLabel.setLocation(x, y);
    }
}
