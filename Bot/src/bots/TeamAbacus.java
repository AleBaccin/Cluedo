package bots;

import gameengine.*;

import java.util.*;
import java.util.Map;

public class TeamAbacus implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the board or the player objects
    // It may only inspect the state of the board and the player objects

    private final static int CARDS_IN_PLAY = 18;
    private final static Coordinates BASEMENT = new Coordinates(12, 16);
    private final PlayerData botData = new PlayerData("TeamAbacus");
    private Player player;
    private PlayersInfo playersInfo;
    private gameengine.Map map;
    private Dice dice;
    private Log log;
    private Deck deck;
    private HashMap<String, Boolean> visitedRooms = new HashMap<>();
    private ArrayList<String> askedQuestions = new ArrayList<>(), knownCards = new ArrayList<>(), seenCards = new ArrayList<>();
    private PlayerData[] playerMetadata;
    private boolean initialised = false, isAccusing = false, hasAskedQuestionDuringTurn = false;
    private String[] murderCards = new String[3], questionCards = new String[3];
    private String lastCommand, queryingPlayer, currentRoom;
    private int stepsTaken = 0, respondedPlayers = 0;

    // Data structures required for movement
    private LinkedList<Node> entrances = new LinkedList<>();
    private Graph graphedMap = new Graph();
    private LinkedList<Node> utilityPath = new LinkedList<>(); // Utility variable for BFS algorithm
    private LinkedList<String> commandsToMove = new LinkedList<>();
    private HashMap<Node, Integer> previousNodesState = new HashMap<>(6);
    private ArrayList<Integer> checkedOutEntrances = new ArrayList<>();
    private boolean firstTimeMoving = true;
    private boolean hasMoved = false;

    public TeamAbacus(Player player, PlayersInfo playersInfo, gameengine.Map map, Dice dice, Log log, Deck deck) {
        this.player = player;
        this.playersInfo = playersInfo;
        this.map = map;
        this.dice = dice;
        this.log = log;
        this.deck = deck;
    }

    // Core methods.
    public String getName() {
        return "TeamAbacus"; // must match the class name
    }

    public String getVersion() {
        return "0.1";   // change on a new release
    }

    public String getCommand() {
        if (!initialised) {
            init();
        }

        if (player.getToken().isInRoom()) {
            if (!player.getToken().getRoom().accusationAllowed()) {
                currentRoom = player.getToken().getRoom().toString();
            }
            stepsTaken = 0;
        }

        validateKnownCards();

        if (visitedRooms.size() > 8) {
            visitedRooms.clear();
            checkedOutEntrances.clear();
        }

        if (hasAskedQuestionDuringTurn && player.getToken().isInRoom()) {
            lastCommand = "done";
            hasAskedQuestionDuringTurn = false;
            firstTimeMoving = false;
        } else if (player.getToken().isInRoom()) {
            if (player.getToken().getRoom().accusationAllowed()) {
                lastCommand = "accuse";
            } else if (!visitedRooms.containsKey(currentRoom) && !hasAskedQuestionDuringTurn) {
                lastCommand = "question";
                hasAskedQuestionDuringTurn = true;
            } else if (player.getToken().getRoom().hasPassage() && !visitedRooms.containsKey(player.getToken().getRoom().getPassageDestination().toString()) && !checkedOutEntrances.contains(player.getToken().getRoom().getPassageDestination())) {
                hasMoved = true;
                lastCommand = "passage";
            } else if (!hasMoved){
                lastCommand = "roll";
            } else{
                lastCommand = "done";
            }
        } else if(!hasMoved){
            lastCommand = "roll";
        }
        else{
            lastCommand = "done";
        }

        if (player.getToken().isInRoom()) {
            if (!visitedRooms.containsKey(player.getToken().getRoom().toString())) {
                visitedRooms.put(player.getToken().getRoom().toString(), true);
                checkedOutEntrances.add(roomToInt(player.getToken().getRoom().toString()));
            }
        }

        if(lastCommand.equals("done"))
            hasMoved = false;

        return lastCommand;
    }

    private void isRollCommand() {
        hasMoved = true;
        if (commandsToMove.isEmpty())
            moveToClosestRoom();
    }

    private int getNextRoom() {
        int index = roomToInt(player.getToken().getRoom().toString());
        int count = 1;
        while (checkedOutEntrances.contains((index + count) % 9)) {
            count++;
        }
        return (index + count) % 9;
    }

    public String getMove() {
        isRollCommand();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!commandsToMove.isEmpty()) {
            stepsTaken++;
            lastCommand = commandsToMove.removeFirst();
        }
        return lastCommand;
    }

    public String getSuspect() {
        if (isAccusing) {
            // verifyMurderCards();
            return murderCards[0];
        } else {
            verifyQuestionCards();
            return questionCards[0];
        }
    }

    public String getWeapon() {
        if (isAccusing) {
            return murderCards[1];
        } else {
            return questionCards[1];
        }
    }

    public String getRoom() {
        return murderCards[2];
    }

    public String getDoor() {
        return Integer.toString(player.getToken().getRoom().getNumberOfDoors());
    }

    public String getCard(Cards matchingCards) {
        // Add your code here
        return matchingCards.get().toString();
    }

    public void notifyResponse(Log response) {
        // Add your code here
    }

    public void notifyPlayerName(String playerName) {
        // Add your code here
    }

    public void notifyTurnOver(String playerName, String position) {
        // Add your code here
    }

    public void notifyQuery(String playerName, String query) {
        queryingPlayer = playerName;
        askedQuestions.add(query);
    }

    public void notifyReply(String playerName, boolean cardShown) {
        if (cardShown && !queryingPlayer.equals(player.getName())) {
            respondedPlayers = 0;
        } else if (cardShown) {
            verifySeenCards();
            respondedPlayers = 0;
        } else if (queryingPlayer.equals(player.getName()) && !botData.cards.contains(currentRoom)) {
            if (++respondedPlayers == playersInfo.numPlayers() - 1) {
                isAccusing = true;
                murderCards[0] = questionCards[0];
                murderCards[1] = questionCards[1];
                murderCards[2] = currentRoom;
            }
        }
    }

    // Utility methods
    private void init() {
        addBotCards();
        initialisePlayerMetadata();
        generateGraphAndEntrances();
        addEdges();

        initialised = true;
    }

    private void initialisePlayerMetadata() {
        playerMetadata = new PlayerData[playersInfo.numPlayers() - 1];
        int i = 0;
        for (String name : playersInfo.getPlayersNames()) {
            if (name.equals(player.getName())) {
                continue;
            }
            playerMetadata[i++] = new PlayerData(name);
        }
    }

    private boolean isReadyForAccusation() {
        return knownCards.size() == CARDS_IN_PLAY;
    }

    private void validateKnownCards() {
        knownCards.clear();
        if (deck.getSharedCards().count() > 0) {
            for (Card card : deck.getSharedCards()) {
                knownCards.add(card.toString());
            }
        }
        knownCards.addAll(botData.cards);
        knownCards.addAll(seenCards);
    }

    private String[] parseQuery(String query) {
        String[] cardsInQuestion = new String[3];
        for (String suspect : Names.SUSPECT_NAMES) {
            if (query.contains(suspect)) {
                cardsInQuestion[0] = suspect;
                break;
            }
        }
        for (String weapon : Names.WEAPON_NAMES) {
            if (query.contains(weapon)) {
                cardsInQuestion[1] = weapon;
                break;
            }
        }
        for (String room : Names.ROOM_NAMES) {
            if (query.contains(room)) {
                cardsInQuestion[2] = room;
                break;
            }
        }
        return cardsInQuestion;
    }

    private void addBotCards() {
        for (Card card : player.getCards()) {
            botData.addCard(card.toString());
        }
    }

    private int findPlayerIndexByName(String name) {
        for (int i = 0; i < playerMetadata.length; i++) {
            if (playerMetadata[i].isOwner(name)) {
                return i;
            }
        }
        return 0;
    }

    private void verifyMurderCards() {
        for (String suspect : Names.SUSPECT_NAMES) {
            if (!knownCards.contains(suspect)) {
                murderCards[0] = suspect;
                break;
            }
        }
        for (String weapon : Names.WEAPON_NAMES) {
            if (!knownCards.contains(weapon)) {
                murderCards[1] = weapon;
                break;
            }
        }
        for (String room : Names.ROOM_NAMES) {
            if (!knownCards.contains(room)) {
                murderCards[2] = room;
                break;
            }
        }
    }

    private void verifyQuestionCards() {
        for (String suspect : Names.SUSPECT_NAMES) {
            if (!knownCards.contains(suspect)) {
                questionCards[0] = suspect;
                break;
            }
        }
        for (String weapon : Names.WEAPON_NAMES) {
            if (!knownCards.contains(weapon)) {
                questionCards[1] = weapon;
                break;
            }
        }
    }

    private void verifySeenCards() {
        for (String suspect : Names.SUSPECT_NAMES) {
            if (player.hasSeen(suspect) && !seenCards.contains(suspect)) {
                seenCards.add(suspect);
                return;
            }
        }
        for (String weapon : Names.WEAPON_NAMES) {
            if (player.hasSeen(weapon) && !seenCards.contains(weapon)) {
                seenCards.add(weapon);
                return;
            }
        }
        for (String room : Names.ROOM_NAMES) {
            if (player.hasSeen(room) && !seenCards.contains(room)) {
                seenCards.add(room);
                return;
            }
        }
    }

    private void setOccupiedNodes() {
        Coordinates[] tokensPosition = playersInfo.getPlayersPositions();
        for (Coordinates c : tokensPosition) {
            for (Node n : graphedMap.getAllVertices()) {
                if (c.equals(n.getItem()) && !c.equals(player.getToken().getPosition())) {
                    previousNodesState.put(n, n.getType());
                    System.out.println("Coordinates of Occupied Edges:" + n.getItem().toString() );
                    n.setType(102);
                }
            }
        }
        System.out.println("Size of the graph: " + graphedMap.adjacencyList.size());
    }

    private void freeOccupiedNodes() {
        for (Map.Entry n : previousNodesState.entrySet()) {
            Node node = (Node) n.getKey();
            node.setType((Integer) n.getValue());
        }
        System.out.println("Size of the graph:" + graphedMap.adjacencyList.size());
        previousNodesState.clear();
    }

    private void generateGraphAndEntrances() {
        for (int r = 0; r < 25; r++) {
            for (int c = 0; c < 24; c++) {
                Coordinates possibleVertex = new Coordinates(c, r);
                Node vertex = new Node(possibleVertex, map.MAP[r][c]);
                if (map.isCorridor(possibleVertex) || vertex.getType() <= 9) {
                    graphedMap.addVertex(vertex);
                }
                if (vertex.getType() <= 9) {
                    addEntrances(vertex);
                }
            }
        }
    }

    private void addEntrances(Node entrance) {
        entrances.add(entrance);
    }

    private void clearEdges(){
        for (Node k: graphedMap.getAllVertices()) {
            for (Node l: graphedMap.getAllVertices()) {
                if(graphedMap.isAdjacent(l, k)) {
                    graphedMap.removeEdge(k, l);
                }
            }
        }
    }

    private void addEdges() {
        for (Node i : graphedMap.getAllVertices()) {
            for (Node c : graphedMap.getAllVertices()) {
                if (i != c && !graphedMap.isAdjacent(c, i) && areNeighbours(i.getItem(), c.getItem()) && c.getType() >= 9 && i.getType() >= 9) { //We are only checking for different objects
                    graphedMap.addEdge(i, c);
                }

                if ((c.getType() <= 9 || i.getType() <= 9) && (c.getType() == 100 || i.getType() == 100) && !graphedMap.isAdjacent(c, i) && c != i && areNeighbours(i.getItem(), c.getItem())) {
                    graphedMap.addEdge(i, c);
                }
            }
        }
    }

    private boolean areNeighbours(Coordinates v, Coordinates u) {
        if (v.getCol() - u.getCol() == 0 && v.getRow() - u.getRow() == 1) {
            return true;
        } else if (v.getCol() - u.getCol() == 0 && v.getRow() - u.getRow() == -1) {
            return true;
        } else if (v.getCol() - u.getCol() == 1 && v.getRow() - u.getRow() == 0) {
            return true;
        } else return v.getCol() - u.getCol() == -1 && v.getRow() - u.getRow() == 0;
    }

    private LinkedList<Node> BFS(Node source, Node destination) {
        utilityPath.clear();

        LinkedList<Node> path = new LinkedList<>();

        if (source.equals(destination) && graphedMap.memberOf(source)) {
            path.add(source);
            return path;
        }

        ArrayDeque<Node> queue = new ArrayDeque<>();

        ArrayDeque<Node> visited = new ArrayDeque<>();

        queue.offer(source);
        while (!queue.isEmpty()) {
            Node vertex = queue.poll();
            visited.offer(vertex);

            LinkedList<Node> neighboursList = graphedMap.getNeighbors(vertex);

            int index = 0;
            int neighboursSize = neighboursList.size();
            while (index != neighboursSize) {
                Node neighbour = neighboursList.get(index);
                path.add(neighbour);
                path.add(vertex);

                if (neighbour.getItem().equals(destination.getItem())) {
                    return processPath(source, destination, path);
                } else {
                    if (!visited.contains(neighbour)) {
                        queue.offer(neighbour);
                    }
                }
                index++;
            }
        }
        return new LinkedList<>();
    }

    private void moveToClosestRoom() {
        clearEdges();
        freeOccupiedNodes();
        setOccupiedNodes();
        addEdges();
        pathToDoor();
    }

    private LinkedList<Node> processPath(Node src, Node destination, LinkedList<Node> path) {
        int index = path.indexOf(destination);
        Node source = path.get(index + 1);

        utilityPath.add(0, destination);

        if (source.equals(src)) {
            utilityPath.add(0, src);
            return utilityPath;
        } else {
            return processPath(src, source, path);
        }
    }

    private LinkedList<String> generatePathMovingCommands() {
        LinkedList<String> commandsForMovement = new LinkedList<>();
        if(utilityPath.isEmpty()){
            utilityPath = minPath(generateAllPossiblePaths());
        }
        if(utilityPath.isEmpty()){
            utilityPath = minPath(generatePathsToNextRoom(1));
        }
        if(utilityPath.isEmpty()){
            return new LinkedList<>();
        }
        Iterator prev = utilityPath.iterator();
        Iterator nex = utilityPath.iterator();
        nex.next();
        while (nex.hasNext() && prev.hasNext()) {
            Node previous = (Node) prev.next();
            Node next = (Node) nex.next();
            commandsForMovement.add(encodeToCommand(previous.getItem(), next.getItem()));
        }
        return commandsForMovement;
    }

    private String encodeToCommand(Coordinates v, Coordinates u) {
        if (v.getCol() - u.getCol() == 0 && v.getRow() - u.getRow() == 1) {
            return "u";
        } else if (v.getCol() - u.getCol() == 0 && v.getRow() - u.getRow() == -1) {
            return "d";
        } else if (v.getCol() - u.getCol() == 1 && v.getRow() - u.getRow() == 0) {
            return "l";
        } else if (v.getCol() - u.getCol() == -1 && v.getRow() - u.getRow() == 0) {
            return "r";
        }
        return "n";
    }

    private LinkedList<LinkedList<Node>> generatePathsToNextRoom(int nextEntrances) {
        LinkedList<LinkedList<Node>> variousPaths = new LinkedList<>();
        System.out.println("Player position: " + player.getToken().getPosition());
        for (Node ent : entrances) {
            if (ent.getType() == nextEntrances)
                variousPaths.add(new LinkedList<>(BFS(graphedMap.getNode(player.getToken().getPosition()), ent)));
        }
        return variousPaths;
    }

    private LinkedList<LinkedList<Node>> generateAllPossiblePaths() {
        LinkedList<LinkedList<Node>> variousPaths = new LinkedList<>();
        System.out.println("Player position: " + player.getToken().getPosition());
        for (Node ent : entrances) {
            if(!checkedOutEntrances.contains(ent.getType()))
                variousPaths.add(new LinkedList<>(BFS(graphedMap.getNode(player.getToken().getPosition()), ent)));
        }
        return variousPaths;
    }

    private void pathToDoor() {
        utilityPath.clear();
        if (isReadyForAccusation() || isAccusing) {
            utilityPath = BFS(graphedMap.getNode(player.getToken().getPosition()), graphedMap.getNode(BASEMENT));
        } else if (firstTimeMoving)
            utilityPath = minPath(generateAllPossiblePaths());
        else {
            utilityPath = minPath(generatePathsToNextRoom(getNextRoom()));
            if(utilityPath.isEmpty()){
                utilityPath = minPath(generateAllPossiblePaths());
            }
        }
        commandsToMove = generatePathMovingCommands();
    }

    private LinkedList<Node> minPath(LinkedList<LinkedList<Node>> variousPaths) {
        LinkedList<Node> min = variousPaths.getFirst();
        for (int i = 1; i < variousPaths.size(); i++) {
            if (variousPaths.get(i).size() < min.size()) {
                min = variousPaths.get(i);
            }
        }
        return min;
    }

    private int roomToInt(String roomName) {
        int index = 0;
        switch (roomName) {
            case "Kitchen":
                index = 0;
                break;
            case "Ballroom":
                index = 1;
                break;
            case "Conservatory":
                index = 2;
                break;
            case "Billiard Room":
                index = 3;
                break;
            case "Library":
                index = 4;
                break;
            case "Study":
                index = 5;
                break;
            case "Hall":
                index = 6;
                break;
            case "Lounge":
                index = 7;
                break;
            case "Dining Room":
                index = 8;
                break;
            case "Cellar":
                index = 9;
        }
        return index;
    }

    /* Nested classes. */

    // For tracking player suspicions.
    private static class PlayerData extends Metadata {
        private final String owner;
        private ArrayList<String> cards = new ArrayList<>();
        private int entryNumber = 0;

        PlayerData(String owner) {
            this.owner = owner;
        }

        void addCard(String card) {
            this.cards.add(card);
        }

        boolean containsCard(String comparingCard) {
            for (String card : cards) {
                if (card.equals(comparingCard)) {
                    return true;
                }
            }
            return false;
        }

        boolean isOwner(String name) {
            return this.owner.equals(name);
        }

        void addEntry(String[] cards) {
            String identifier = owner + "[" + Integer.toString(++entryNumber) + "]";
            for (String card : cards) {
                this.update(card, identifier);
            }
        }

        void validateDataSet() {
            if (!this.getDataSet().isEmpty()) {
                for (java.util.Map.Entry<String, String> entry : getDataSet().entrySet()) {
                    int occurrences = 0;
                    for (java.util.Map.Entry<String, String> subEntry : getDataSet().entrySet()) {
                        if (entry.getValue().equals(subEntry.getValue())) {
                            occurrences++;
                        }
                    }
                    if (occurrences < 2) {
                        cards.add(entry.getKey());
                        this.remove(entry.getKey());
                    }
                }
            }
        }
    }

    private static class Metadata {
        private HashMap<String, String> dataSet = new HashMap<>();

        HashMap<String, String> getDataSet() {
            return dataSet;
        }

        void update(String key, String data) {
            dataSet.put(key, data);
        }

        void remove(String key) {
            dataSet.remove(key);
        }
    }

    // For path finding.
    private class Node {
        private Coordinates item;
        private int type;
        private LinkedList<Node> neighbors;

        Node(Coordinates item, int type) {
            this.item = item;
            this.type = type;
            this.neighbors = new LinkedList<>();
        }

        Coordinates getItem() {
            return this.item;
        }

        int getType() {
            return this.type;
        }

        void setType(int type) {
            this.type = type;
        }

        LinkedList<Node> getNeighbors() {
            return this.neighbors;
        }
    }

    private class Graph {
        private LinkedList<Node> adjacencyList;

        Graph() {
            this.adjacencyList = new LinkedList<>();
        }

        Node getNode(Coordinates coordinates) {
            if (adjacencyList.isEmpty()) {
                throw new IllegalArgumentException("AdjacencyList is empty.");
            }
            for (Node n : adjacencyList) {
                if (coordinates.equals(n.getItem())) {
                    return n;
                }
            }
            return null;
        }

        public LinkedList<Node> getAdjacencyList() {
            return this.adjacencyList;
        }

        boolean memberOf(Node v) {
            return this.adjacencyList.contains(v);
        }

        void addVertex(Node v) {
            if (memberOf(v)) {
                throw new IllegalArgumentException("Vertex already exists.");
            }
            this.adjacencyList.add(v);
        }

        public void removeVertex(Node v) {
            if (!memberOf(v)) {
                throw new IllegalArgumentException("Vertex doesn't exist.");
            }

            this.adjacencyList.remove(v);

            for (Node u : this.getAllVertices()) {
                u.getNeighbors().remove(v);
            }
        }

        void addEdge(Node v, Node u) {
            if (!memberOf(v) || !memberOf(u)) {
                throw new IllegalArgumentException();
            }

            v.getNeighbors().add(u);
            u.getNeighbors().add(v);
        }

        public void removeEdge(Node v, Node u) {
            if (!memberOf(v) || !memberOf(u)) {
                throw new IllegalArgumentException();
            }

            v.getNeighbors().remove(u);
            u.getNeighbors().remove(v);
        }

        boolean isAdjacent(Node v, Node u) {
            return v.getNeighbors().contains(u);
        }

        LinkedList<Node> getNeighbors(Node v) {
            return v.getNeighbors();
        }

        LinkedList<Node> getAllVertices() {
            return this.adjacencyList;
        }
    }
}