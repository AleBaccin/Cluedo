// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import java.util.HashMap;
import java.util.LinkedList;

class Game {
    private final static int MIN_PLAYER_COUNT = 2, MAX_PLAYER_COUNT = 6, CARDS_COUNT = 18;
    private final UserInterface ui = new UserInterface();
    private final HashMap<String, String> characterPool = new HashMap<>();
    private final Die firstDice = new Die(), secondDice = new Die();
    private final Envelope envelope = new Envelope();
    private final Cards publicDeck = new Cards(envelope);
    private LinkedList<Player> players = new LinkedList<>();

    public static int getMinPlayerCount() {
        return MIN_PLAYER_COUNT;
    }

    public static int getMaxPlayerCount() {
        return MAX_PLAYER_COUNT;
    }

    public static int getCardsCount() {
        return CARDS_COUNT;
    }

    public void launch() {
        GameController controller = new GameController(this);
        controller.startUp();
        controller.addPlayers();
        controller.setup();
        controller.run();
        System.exit(0);
    }

    public HashMap<String, String> getCharacterPool() {
        return characterPool;
    }

    public UserInterface getUI() {
        return ui;
    }

    public LinkedList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(LinkedList<Player> players) {
        this.players = players;
    }

    public Die getFirstDice() {
        return firstDice;
    }

    public Die getSecondDice() {
        return secondDice;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public Cards getPublicDeck() {
        return publicDeck;
    }


}
