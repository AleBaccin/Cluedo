// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

// BoardPanel class, models the layout and makeup of the game board.
public class BoardPanel extends JComponent {
    private final JPanel boardDisplay;
    private final JLabel backgroundLabel;
    private BufferedImage backgroundImage;

    BoardPanel() {
        backgroundLabel = new JLabel();
        try {
            backgroundImage = ImageIO.read(this.getClass().getResource("board.png"));
        } catch (IOException e) {
            System.out.println("Error: Could not load board image.");
        }
        ImageIcon backgroundIcon = new ImageIcon(backgroundImage);
        backgroundLabel.setIcon(backgroundIcon);
        boardDisplay = new JPanel();
        boardDisplay.add(backgroundLabel);

        for (Rooms r : Rooms.values()) {
            backgroundLabel.add(r.getLabel());
        }
    }

    public JPanel getBoardDisplay() {
        return boardDisplay;
    }

    public JLabel getBackgroundLabel() {
        return backgroundLabel;
    }

    public Color getBoardPointColor(int x, int y) {
        return new Color(backgroundImage.getRGB(x, y), true);
    }

}