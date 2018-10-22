// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class Utility {

    private static StringBuilder questionLog = new StringBuilder("Question log: ");

    public static char encodingMovingInput(String inputForMoving) {
        char res;
        if (inputForMoving.equals("up") || inputForMoving.equals("u")) {
            res = 'u';
        } else if (inputForMoving.equals("down") || inputForMoving.equals("d")) {
            res = 'd';
        } else if (inputForMoving.equals("left") || inputForMoving.equals("l")) {
            res = 'l';
        } else if (inputForMoving.trim().equals("right") || inputForMoving.equals("r")) {
            res = 'r';
        } else {
            res = 'i';
        }
        return res;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static Characters findCharacter(String name) {
        Characters character = null;
        switch (name) {
            case "Colonel Mustard":
                character = Characters.COLONEL_MUSTARD;
                break;
            case "Mrs. Peacock":
                character = Characters.MRS_PEACOCK;
                break;
            case "Miss Scarlet":
                character = Characters.MRS_SCARLETT;
                break;
            case "Mrs. White":
                character = Characters.MRS_WHITE;
                break;
            case "Professor Plum":
                character = Characters.PROFESSOR_PLUM;
                break;
            case "Rev. Green":
                character = Characters.REV_GREEN;
                break;
            default:
                System.out.println(Errors.characterNotFoundError(name));
                break;
        }
        return character;
    }

    public static void addToQuestionLog(String text) {
        questionLog.append("\n").append(text);
    }

    public static String getQuestionLog() {
        return questionLog.toString();
    }

    public static String help(String command) {
        switch (command) {
            case "adding players help":
                return Messages.getAddingPlayersHelp();
            case "roll help":
                return Messages.getRollHelp();
            case "movement help":
                return Messages.getMovementLegend();
            case "turn help":
                return Messages.getTurnHelp();
            case "pose question help":
                return Messages.getPoseQuestionHelp();
            case "accusation help":
                return Messages.getAccusationHelp();
            case "prelude":
                return Messages.getPRELUDE();
            default:
                return Errors.couldNotFindHelpMessage();
        }
    }

    public static void populateCharacterPool(HashMap<String, String> characterPool) {
        characterPool.clear();
        characterPool.put("cm", "Colonel Mustard");
        characterPool.put("mp", "Mrs. Peacock");
        characterPool.put("ms", "Miss Scarlet");
        characterPool.put("mw", "Mrs. White");
        characterPool.put("pp", "Professor Plum");
        characterPool.put("rg", "Rev. Green");
    }

    public static String displayCharacterPool(HashMap<String, String> characterPool) {
        StringBuilder formattedCharacterPool = new StringBuilder();
        for (Map.Entry<String, String> entry : characterPool.entrySet()) {
            formattedCharacterPool.append("Character: (").append(entry.getKey()).append(") ").append(entry.getValue()).append("\n");
        }
        return formattedCharacterPool.toString();
    }

    public static boolean validatePlayerName(LinkedList<Player> players, String name) {
        for (Player player : players) {
            if (player.getName().trim().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public static void distributeCards(Cards publicDeck, LinkedList<Player> players) {
        SecureRandom random = new SecureRandom();
        int counter = 0;
        int index;
        for (int i = 0; i < Game.getCardsCount() / players.size(); i++) {
            for (Player p : players) {
                index = random.nextInt(Game.getCardsCount() - counter);
                p.getDeck().add(publicDeck.get(index));
                publicDeck.remove(index);
                counter++;
            }
        }
    }

    private static int findMaxDiceValue(LinkedList<Player> players) {
        int maxValue = 0;
        for (Player player : players) {
            if (player.isContending()) {
                maxValue = Math.max(player.getDiceValue(), maxValue);
            }
        }
        return maxValue;
    }

    private static void rollDiceForPlayers(LinkedList<Player> players, Die firstDice, Die secondDice) {
        for (Player player : players) {
            if (player.isContending()) {
                player.setDiceValue(firstDice.roll() + secondDice.roll());
            }
        }
    }

    private static int determineStartingPlayer(int contenders, int startingPlayerIndex, LinkedList<Player> players, Die firstDice, Die secondDice) {
        if (contenders == 1) {
            return startingPlayerIndex;
        }

        rollDiceForPlayers(players, firstDice, secondDice);
        int maxValue = findMaxDiceValue(players);
        for (int i = 0; i < players.size(); i++) {
            if ((players.get(i).getDiceValue() != maxValue) && (players.get(i).isContending())) {
                contenders--;
                players.get(i).setContending(false);
            } else if (players.get(i).isContending()) {
                startingPlayerIndex = i;
            }
        }
        determineStartingPlayer(contenders, startingPlayerIndex, players, firstDice, secondDice);
        return startingPlayerIndex;
    }

    public static void setTurnOrder(Game game) {
        int startingPlayerIndex = determineStartingPlayer(game.getPlayers().size(), 0, game.getPlayers(), game.getFirstDice(), game.getSecondDice());
        game.getUI().updateInformationLog("\n" + game.getPlayers().get(startingPlayerIndex).getName() + " got the highest roll and will go first.\n");
        if (startingPlayerIndex == 0) {
            return;
        }
        LinkedList<Player> tmp = new LinkedList<>();
        tmp.add(0, game.getPlayers().get(startingPlayerIndex));
        game.getPlayers().remove(startingPlayerIndex);
        for (int i = 0; i < game.getPlayers().size(); i++) {
            tmp.add(i + 1, game.getPlayers().get(i));
        }
        game.setPlayers(tmp);
    }
}
