// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

// UserInterface class to setup components for display on a single frame.
public class UserInterface {
    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 800;
    private static final Color BOARD_CELL = new Color(224, 193, 126);
    private static final Color ENTRANCE_CELL = new Color(75, 134, 101);
    private static final int ROOMS = 9;
    private final List<String> holder = new LinkedList<>();

    private final LinkedList<Square> positionsAllowed;
    private final BoardPanel boardPanel;
    private final InformationPanel informationPanel;
    private final CommandPanel commandPanel;
    private final JFrame frame;
    private final int[][] positionCheckers;


    UserInterface() {
        positionsAllowed = new LinkedList<>();
        positionCheckers = new int[][]{
                {- 27, 0},
                {+ 27, 0},
                {0, + 27},
                {0, - 27},
        };
        frame = new JFrame("Cluedo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        boardPanel = new BoardPanel();
        informationPanel = new InformationPanel();
        commandPanel = new CommandPanel();
        frame.add(boardPanel.getBoardDisplay(), BorderLayout.LINE_START);
        frame.add(informationPanel, BorderLayout.LINE_END);
        frame.add(commandPanel.getCommandPanel(), BorderLayout.PAGE_END);
        displayWeapons();
        displayGreeting();
        frame.setVisible(true);
        createUIActionListeners();
    }

    public boolean isCell(int x, int y) {
        boolean cell = false;
        Color c = getBoardPanel().getBoardPointColor(x, y);
        if (c.equals(BOARD_CELL) || c.equals(ENTRANCE_CELL))
            cell = true;
        return cell;
    }

    public boolean isCellFree(Player playerToMove, List<Player> players, Point p) {
        boolean occupied = false;
        Point point;
        for (Player player : players) {
            point = p;
            if (point.equals(player.getCharacterLabelLocation()) && playerToMove != player) {
                occupied = true;
            }
        }
        return occupied;
    }

    public boolean isEntrance(int x, int y) {
        boolean entrance = false;
        Color c = getBoardPanel().getBoardPointColor(x, y);
        if (isCell(x, y) && c.equals(ENTRANCE_CELL)) {
            updateInformationLog("Entrance");
            entrance = true;
        }
        return entrance;
    }

    private void addAllowedPositions(Player playerToMove, List<Player> players, Point p) {
        Point temp = new Point();
        Point utility;
        for (int[] i : positionCheckers) {
            utility = new Point((int) p.getX() + i[0], ((int) p.getY()) + i[1]);
            if(!playerToMove.isInVisitedBoxes(utility)) {
                if (isCell(utility.x, utility.y) && !isCellFree(playerToMove, players, utility)) {
                    temp.setLocation(((int) p.getX() + i[0] - 2), ((int) p.getY()) + i[1] - 2);
                    positionsAllowed.add(new Square(temp));
                }
            }
        }
    }

    public void showAllowedPositions(Player playerToMove, List<Player> players, Point p) {
        for (Square s : positionsAllowed) {
            getBoardPanel().getBackgroundLabel().remove(s);
        }
        positionsAllowed.clear();

        addAllowedPositions(playerToMove, players, p);

        for (Square sq : positionsAllowed) {
            this.getBoardPanel().getBackgroundLabel().add(sq);
        }
        refresh();
    }

    public void clearPositionsAllowed() {
        for (Square s : positionsAllowed) {
            getBoardPanel().getBackgroundLabel().remove(s);
        }
        positionsAllowed.clear();
        refresh();
    }

    private void createUIActionListeners() {
        commandPanel.getCommandInputArea().addActionListener(e -> {
            commandPanel.getCommandInputArea().setText(null);
            synchronized (holder) {
                holder.add(e.getActionCommand());
                holder.notify();
            }
        });
    }

    public List<String> getHolder() {
        return holder;
    }

    public String formatUserText(String text) {
        return "> " + text;
    }

    private void displayWeapons() {
        List<Rooms> roomsWithoutWeapon = new ArrayList<>(EnumSet.allOf(Rooms.class));
        SecureRandom random = new SecureRandom();
        int index;
        int c = 0;
        for (Weapons weapon : Weapons.values()) {
            index = random.nextInt(ROOMS - c);
            roomsWithoutWeapon.get(index).getLabel().add(weapon.getLabel());
            roomsWithoutWeapon.remove(index);
            c++;
        }
        refresh();
    }

    public void displayCharacters(LinkedList<Player> players) {
        for (Player player : players) {
            getBoardPanel().getBackgroundLabel().add(player.getCharacterLabel());
            getBoardPanel().getBackgroundLabel().add(player.getPlayerNameLabel());
        }
        refresh();
    }

    private void displayGreeting() {
        informationPanel.addLogText(Messages.getGreeting());
    }

    public void displayDie(Die d1, Die d2) {
        informationPanel.addLogText(String.format("Your roll: \nFirst Die: %s Second Die: %s \n", Integer.toString(d1.roll()), Integer.toString(d2.roll())));
    }

    public void updateInformationLog(String text) {
        informationPanel.addLogText(text);
    }

    public void clearInformationLog() {
        informationPanel.getLog().setText(null);
    }

    public BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    public InformationPanel getInformationPanel() {
        return this.informationPanel;
    }

    public void refresh() {
        this.frame.revalidate();
        this.frame.repaint();
    }
}
