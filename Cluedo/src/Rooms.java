// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import java.awt.*;
import java.security.SecureRandom;

public enum Rooms implements Token {
    KITCHEN("kitchen", new Point(50, 54), new Dimension(157, 157), true, new Entrance(1, new Point[]{
            new Point(154, 218)})),
    BALLROOM("ballroom", new Point(270, 87), new Dimension(205, 152), false, new Entrance(2, new Point[]{
            new Point(235, 164),
            new Point(289, 245),
            new Point(424, 245),
            new Point(478, 164)})),
    CONSERVATORY("conservatory", new Point(538, 53), new Dimension(158, 105), true, new Entrance(3, new Point[]{
            new Point(532, 164)})),
    BILLBOARD("billiard room", new Point(536, 246), new Dimension(158, 129), false, new Entrance(4, new Point[]{
            new Point(505, 272),
            new Point(640, 380)})),
    LIBRARY("library", new Point(537, 407), new Dimension(130, 129), false, new Entrance(5, new Point[]{
            new Point(478, 461),
            new Point(586, 380)})),
    STUDY("study", new Point(511, 597), new Dimension(183, 105), true, new Entrance(6, new Point[]{
            new Point(505, 569)})),
    HALL("hall", new Point(295, 515), new Dimension(152, 181), false, new Entrance(7, new Point[]{
            new Point(343, 488),
            new Point(370, 488),
            new Point(451, 569)
    })),
    LOUNGE("lounge", new Point(47, 544), new Dimension(187, 160), true, new Entrance(8, new Point[]{new Point(208, 515)})),
    DININGROOM("dining room", new Point(44, 301), new Dimension(216, 152), false, new Entrance(9, new Point[]{
            new Point(208, 461),
            new Point(262, 353)})),
    BASEMENT("basement", new Point(325, 298), new Dimension(134, 187), false, new Entrance(10, new Point[]{
            new Point(370, 488)}));

    final Color BG = new Color(0, 0, 0, 0);
    private final String room;
    private final RoomLabel roomPanel;
    private final boolean passage;
    private final Entrance entrance;
    private Point location;
    private Dimension dimension;

    Rooms(String room, Point location, Dimension dimension, boolean passage, Entrance entrance) {
        this.room = room;
        Point location1 = location;
        roomPanel = new RoomLabel(dimension, location, BG);
        roomPanel.setLayout(new FlowLayout());
        Dimension dimension1 = dimension;
        this.passage = passage;
        this.entrance = entrance;
    }

    public static Rooms roomConnected(Rooms rooms) {
        Rooms temp = null;
        switch (rooms.getName()) {
            case "kitchen":
                temp = Rooms.STUDY;
                break;
            case "study":
                temp = Rooms.KITCHEN;
                break;
            case "conservatory":
                temp = Rooms.LOUNGE;
                break;
            case "lounge":
                temp = Rooms.CONSERVATORY;
                break;
        }
        return temp;
    }

    public static Rooms getRandomRoom() {
        SecureRandom random = new SecureRandom();
        return values()[random.nextInt(values().length - 1)];
    }

    @Override
    public String getName() {
        return this.room;
    }

    @Override
    public String getDisplayName() {
        return room.substring(0, 1).toUpperCase() + room.substring(1);
    }

    public RoomLabel getLabel() {
        return this.roomPanel;
    }

    public Entrance getEntrance() {
        return this.entrance;
    }

    public boolean hasPassage() {
        return this.passage;
    }
}
