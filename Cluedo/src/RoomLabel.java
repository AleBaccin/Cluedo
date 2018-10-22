// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import javax.swing.*;
import java.awt.*;

public class RoomLabel extends JLabel {
    RoomLabel(Dimension dimension, Point location, Color backGround) {
        this.setSize(dimension);
        this.setLocation(location);
        this.setBackground(backGround);
    }
}
