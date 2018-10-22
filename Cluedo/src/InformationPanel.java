// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

class InformationPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int TEXT_AREA_HEIGHT = 40;
    private static final int CHARACTER_WIDTH = 57;
    private static final int FONT_SIZE = 12;
    private final JTextArea log = new JTextArea(TEXT_AREA_HEIGHT, CHARACTER_WIDTH);

    InformationPanel() {
        JScrollPane scrollPane = new JScrollPane(log);
        DefaultCaret caret = (DefaultCaret) log.getCaret();
        log.setEditable(false);
        log.setFont(new Font("monospaced", Font.PLAIN, FONT_SIZE));
        log.setBackground(Color.BLACK);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setForeground(Color.WHITE);
        log.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void changeLogColor(Color c){
        log.setForeground(c);
    }

    public void addLogText(String text) {
        log.append(text + "\n");
    }

    public JTextArea getLog() {
        return log;
    }

}