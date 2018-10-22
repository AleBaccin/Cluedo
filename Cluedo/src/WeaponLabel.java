// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import javax.swing.*;
import java.awt.*;

class WeaponLabel extends JLabel {
    WeaponLabel(String weaponName) {
        setSize(23, 60);
        try {
            ImageIcon weaponIcon = new ImageIcon(this.getClass().getResource("/Tokens/" + weaponName + "T.png"));
            Image weaponImg = weaponIcon.getImage();
            Image newWeaponImg = weaponImg.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon finalWeaponImg = new ImageIcon(newWeaponImg);
            setIcon(finalWeaponImg);
        } catch (NullPointerException e) {
            System.out.println("Error: could not load " + weaponName + "T.png");
        }
    }
}
