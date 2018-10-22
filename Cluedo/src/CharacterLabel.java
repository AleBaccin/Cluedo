// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import javax.swing.*;
import java.awt.*;

class CharacterLabel extends JLabel {
    CharacterLabel(String character, Point p) {
        setSize(25, 25);
        try {
            ImageIcon characterIcon = new ImageIcon(this.getClass().getResource("/Tokens/"+character + ".png"));
            Image characterImg = characterIcon.getImage();
            Image newCharacterImg = characterImg.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon finalCharacterImg = new ImageIcon(newCharacterImg);
            setIcon(finalCharacterImg);
        } catch (NullPointerException e) {
            System.out.println("Error: could not load " + character + " image.");
        }
        setLocation(p);
    }
}
