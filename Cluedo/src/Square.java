import javax.swing.*;
import java.awt.*;

class Square extends JLabel {
    Square(Point p) {
        this.setLocation(p);
        this.setSize(29, 29);
        this.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
    }
}
