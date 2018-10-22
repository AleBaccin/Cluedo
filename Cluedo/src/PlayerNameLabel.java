import javax.swing.*;
import java.awt.*;

public class PlayerNameLabel extends JTextArea {
    private static int FONT_SIZE = 13;

    PlayerNameLabel(Characters c, String name) {
        this.setFont(new Font("monospaced", Font.PLAIN, FONT_SIZE));
        this.setText(name);
        this.setSize(getPreferredSize());
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.setLocation((int) c.getX() - this.getWidth() - 1, (int) c.getY() - this.getHeight() - 1);
        this.setBackground(Color.BLACK);
        this.setForeground(c.getColor());
        this.setOpaque(true);
    }
}
