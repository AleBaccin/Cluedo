// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import java.awt.*;
import java.util.LinkedList;

class Player {
    private final CharacterLabel characterLabel;

    private final PlayerNameLabel playerNameLabel;
    private final String name;
    private final Characters character;
    private final Deck deck;
    private int diceValue;
    private boolean isContending;
    private int index;
    private Rooms room;
    private boolean hasAccused;
    private Rooms previouslyVisitedRoom;
    private Rooms roomLastQuestion;
    private LinkedList<Point> previouslyVisitedBoxes;
    private boolean moved;
    private String seenCards;

    Player(String name, Characters character, int index) {
        this.name = name;
        this.character = character;
        this.characterLabel = new CharacterLabel(character.getName(), new Point((int) character.getX(), (int) character.getY()));
        this.playerNameLabel = new PlayerNameLabel(character, name);
        this.room = null;
        this.moved = false;
        this.previouslyVisitedRoom = null;
        this.roomLastQuestion = null;
        this.previouslyVisitedBoxes = new LinkedList<Point>();
        this.isContending = true;
        this.deck = new Deck();
        this.index = index;
        this.hasAccused = false;
    }

    public void moved(){ this.moved = true; }

    public boolean hasMoved(){ return this.moved; }

    public void allowPlayerMovement(){
        this.moved = false;
    }

    public void addVisitedBoxes(Point p){
        this.previouslyVisitedBoxes.add(p);
    }

    public boolean isInVisitedBoxes(Point p){
        return previouslyVisitedBoxes.contains(p);
    }

    public void clearVisitedBoxes(){
        previouslyVisitedBoxes.clear();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public Characters getCharacter() {
        return character;
    }

    public CharacterLabel getCharacterLabel() {
        return this.characterLabel;
    }

    public PlayerNameLabel getPlayerNameLabel() {
        return this.playerNameLabel;
    }

    public Rooms getRoomLastQuestion(){
        return this.roomLastQuestion;
    }

    public void setRoomLastQuestion(Rooms roomLastQuestion){
        this.roomLastQuestion = roomLastQuestion;
    }

    public Deck getDeck() {
        return this.deck;
    }

    public int getDiceValue() {
        return diceValue;
    }

    public void setDiceValue(int diceValue) {
        this.diceValue = diceValue;
    }

    public boolean isContending() {
        return isContending;
    }

    public void setContending(boolean contending) {
        isContending = contending;
    }

    public Point getCharacterLabelLocation() {
        return this.characterLabel.getLocation();
    }

    public void setCharacterLabelLocation(int x, int y) {
        characterLabel.setLocation(x, y);
    }

    public Rooms getRoomIsIn() {
        return room;
    }

    public Rooms getPreviouslyVisitedRoom(){
        return previouslyVisitedRoom;
    }

    public void setPreviouslyVisitedRoom(Rooms previouslyVisitedRoom){
        this.previouslyVisitedRoom = previouslyVisitedRoom;
    }

    public void playerEnterRoom(Rooms room) {
        this.room = room;
    }

    public boolean hasAccused() {
        return hasAccused;
    }

    public void setHasAccused(boolean hasAccused) {
        this.hasAccused = hasAccused;
    }

    public Rooms playerLeaveRoom() {
        Rooms tmp = this.room;
        this.previouslyVisitedRoom = tmp;
        this.room = null;
        return tmp;
    }

    public Point getFutureLocation(char direction, Point p) {
        Point tmp = null;
        switch (direction) {
            case 'u':
                tmp = new Point((int) p.getX(), (int) p.getY() - 27);
                break;
            case 'd':
                tmp = new Point((int) p.getX(), (int) p.getY() + 27);
                break;
            case 'l':
                tmp = new Point((int) p.getX() - 27, (int) p.getY());
                break;
            case 'r':
                tmp = new Point((int) p.getX() + 27, (int) p.getY());
                break;
            default:
                break;
        }
        return tmp;
    }

    public boolean canMove(boolean isCell, int steps) {
        return isCell && steps > 0;
    }

    public boolean inRoom() {
        return ! (room == null);
    }

    public boolean inBasement() {
        if (inRoom())
            return (room.getName().equals(Rooms.BASEMENT.getName()));
        return false;
    }

    public Point moveUp() {
        Point tmp = new Point(getCharacterLabelLocation().x, getCharacterLabelLocation().y - 27);
        setCharacterLabelLocation(getCharacterLabelLocation().x, getCharacterLabelLocation().y - 27);
        updateNameLabelPosition();
        return tmp;
    }

    public Point moveDown() {
        Point tmp = new Point(getCharacterLabelLocation().x, getCharacterLabelLocation().y + 27);
        setCharacterLabelLocation(getCharacterLabelLocation().x, getCharacterLabelLocation().y + 27);
        updateNameLabelPosition();
        return tmp;
    }

    public Point moveLeft() {
        Point tmp = new Point(getCharacterLabelLocation().x - 27, getCharacterLabelLocation().y);
        setCharacterLabelLocation(getCharacterLabelLocation().x - 27, getCharacterLabelLocation().y);
        updateNameLabelPosition();
        return tmp;
    }

    public Point moveRight() {
        Point tmp = new Point(getCharacterLabelLocation().x + 27, getCharacterLabelLocation().y);
        setCharacterLabelLocation(getCharacterLabelLocation().x + 27, getCharacterLabelLocation().y);
        updateNameLabelPosition();
        return tmp;
    }

    public void updateNameLabelPosition() {
        playerNameLabel.setLocation(getCharacterLabelLocation().x - playerNameLabel.getWidth() - 1, getCharacterLabelLocation().y - playerNameLabel.getHeight() - 1);
    }
}
