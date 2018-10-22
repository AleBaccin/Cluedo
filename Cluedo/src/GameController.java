// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

class GameController {
    private final Movement movement;
    private final Game game;

    GameController(Game game) {
        this.game = game;
        this.movement = new Movement(null, this.game.getPlayers(), this.game.getUI());
    }

    public void startUp() {
        Utility.populateCharacterPool(game.getCharacterPool());
    }

    public void setup() {
        Utility.distributeCards(game.getPublicDeck(), game.getPlayers());
        Utility.setTurnOrder(game);
        game.getUI().displayCharacters(game.getPlayers());
    }

    public void addPlayers() {
        boolean validCommand;
        String command;
        try {
            game.getUI().updateInformationLog(Messages.getAddPlayersIntroMessage());

            do {
                if (game.getPlayers().size() == Game.getMaxPlayerCount()) return;

                if (game.getPlayers().size() > 0)
                    game.getUI().updateInformationLog("Type and enter \"done\" if you are finished adding players.\n");

                game.getUI().updateInformationLog("Player " + (game.getPlayers().size() + 1) + " select a character.");
                game.getUI().updateInformationLog(Utility.displayCharacterPool(game.getCharacterPool()));

                synchronized (game.getUI().getHolder()) {

                    while (game.getUI().getHolder().isEmpty()) {
                        game.getUI().getHolder().wait();
                    }

                    command = game.getUI().getHolder().remove(0).trim().toLowerCase();
                    game.getUI().updateInformationLog(game.getUI().formatUserText(command));
                    switch (command) {
                        case "quit":
                            System.exit(0);
                        case "help":
                        case "h":
                            game.getUI().clearInformationLog();
                            game.getUI().updateInformationLog(Utility.help("adding players help"));
                            break;
                        case "prelude":
                        case "p":
                            game.getUI().clearInformationLog();
                            game.getUI().updateInformationLog(Utility.help("prelude"));
                            break;
                        case "cheat":
                            game.getUI().clearInformationLog();
                            game.getUI().updateInformationLog(game.getEnvelope().cheat());
                            break;
                        default:
                            validCommand = false;
                            for (String key : game.getCharacterPool().keySet()) {
                                if (command.equals(key)) {
                                    validCommand = true;
                                    game.getUI().updateInformationLog("\nPlayer " + (game.getPlayers().size() + 1) + " you selected " + game.getCharacterPool().get(key) + ".\n");
                                    Characters characterToAssign = Utility.findCharacter(game.getCharacterPool().get(key));
                                    game.getUI().updateInformationLog("Enter a username or hit enter if you wish to skip this step.\nNote: by default your name will be: Player " + (game.getPlayers().size() + 1) + ".\n");

                                    while (game.getUI().getHolder().isEmpty()) {
                                        game.getUI().getHolder().wait();
                                    }

                                    command = game.getUI().getHolder().remove(0).trim().toLowerCase();


                                    if (command.equals("quit")) {
                                        System.exit(0);
                                    } else if (command.length() == 0) {
                                        game.getUI().clearInformationLog();
                                        game.getPlayers().add(new Player("Player " + (game.getPlayers().size() + 1), characterToAssign, game.getPlayers().size()));
                                        game.getCharacterPool().remove(key);
                                    } else if (Utility.validatePlayerName(game.getPlayers(), command)) {
                                        game.getUI().clearInformationLog();
                                        game.getPlayers().add(new Player(command, characterToAssign, game.getPlayers().size()));
                                        game.getCharacterPool().remove(key);
                                    } else {
                                        game.getUI().clearInformationLog();
                                        game.getUI().updateInformationLog(Errors.usernameAlreadyExisting(command));
                                    }
                                    break;
                                } else if (command.equals("done")) {
                                    validCommand = true;
                                    if (game.getPlayers().size() >= Game.getMinPlayerCount()) {
                                        return;
                                    } else {
                                        game.getUI().clearInformationLog();
                                        game.getUI().updateInformationLog(Errors.notEnoughPlayersError());
                                        break;
                                    }
                                } else if (command.equals("quit")) {
                                    System.exit(0);
                                } else if (command.equals("help") || command.equals("h")) {
                                    validCommand = true;
                                    game.getUI().clearInformationLog();
                                    game.getUI().updateInformationLog(Utility.help("adding players help"));
                                }
                            }
                            if (! validCommand) {
                                game.getUI().updateInformationLog(Errors.invalidCommandError());
                            }
                            break;
                    }
                }
            } while (true);
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
    }

    public void run() {
        game.getUI().clearInformationLog();
        String command;
        boolean canQuestion = false;
        game.getUI().updateInformationLog(Messages.getRunIntroMessage());
        game.getUI().updateInformationLog(game.getPlayers().getFirst().getName() + " got the highest roll and will start first.\n");
        try {
            do {
                synchronized (game.getUI().getHolder()) {
                    for (Player currentPlayer : game.getPlayers()) {

                        if(currentPlayer.hasAccused()) continue;

                        do {
                            movement.setPlayer(currentPlayer);
                            currentPlayer.clearVisitedBoxes();
                            game.getUI().getInformationPanel().changeLogColor(currentPlayer.getCharacter().getColor());

                            game.getUI().updateInformationLog(currentPlayer.getName() + " it's your turn to play.\n");

                            if(!currentPlayer.hasMoved()) {
                                if (currentPlayer.inRoom() && currentPlayer.getRoomIsIn().hasPassage() && movement.secretPassageOption()) {
                                    if(!currentPlayer.hasMoved()){
                                        game.getUI().updateInformationLog("You were not able to teleport. You can still move.\n");
                                    }
                                }
                            }

                            if(currentPlayer.inRoom()) {
                                if (!currentPlayer.inBasement() && currentPlayer.getRoomLastQuestion() == currentPlayer.getRoomIsIn()) {
                                    game.getUI().updateInformationLog(Errors.invalidQuestionRoom());
                                    canQuestion = false;
                                } else if (!currentPlayer.inBasement() && currentPlayer.getRoomLastQuestion() != currentPlayer.getRoomIsIn()) {
                                    canQuestion = true;
                                }
                            }

                            if (currentPlayer.inRoom() && !currentPlayer.inBasement()) {
                                game.getUI().updateInformationLog(Messages.getPoseQuestionMessage());
                            } else if (currentPlayer.inBasement()) {
                                game.getUI().updateInformationLog(Messages.getAccusationHelp());
                            }

                            game.getUI().updateInformationLog(Utility.help("turn help"));

                            while (game.getUI().getHolder().isEmpty()) {
                                game.getUI().getHolder().wait();
                            }

                            command = game.getUI().getHolder().remove(0).trim().toLowerCase();
                            game.getUI().updateInformationLog(game.getUI().formatUserText(command));

                            switch (command) {
                                case "quit":
                                    return;
                                case "notes":
                                    game.getUI().clearInformationLog();
                                    game.getUI().updateInformationLog(currentPlayer.getDeck().getNoteFormatString() + game.getPublicDeck().getNoteFormatString() + Messages.getNotes());
                                    break;
                                case "roll":
                                case "r":
                                    if(!currentPlayer.hasMoved()) {
                                        currentPlayer.moved();
                                        movement.setPlayers(game.getPlayers());
                                        game.getUI().clearInformationLog();
                                        rollForPlayer(currentPlayer);
                                    }
                                    else{
                                        game.getUI().updateInformationLog(Errors.movingTwoTimesError(currentPlayer.getName()));
                                    }
                                    break;
                                case "help":
                                case "h":
                                    game.getUI().clearInformationLog();
                                    game.getUI().updateInformationLog(Utility.help("roll help"));
                                    break;
                                case "cheat":
                                    game.getUI().clearInformationLog();
                                    game.getUI().updateInformationLog(game.getEnvelope().cheat());
                                    break;
                                case "deck":
                                    game.getUI().clearInformationLog();
                                    game.getUI().updateInformationLog(game.getPublicDeck().toString());
                                    break;
                                case "cards":
                                case "c":
                                    game.getUI().clearInformationLog();
                                    game.getUI().updateInformationLog(currentPlayer.getDeck().toString());
                                    break;
                                case "question":
                                    if (! currentPlayer.inBasement() && currentPlayer.getRoomLastQuestion() != currentPlayer.getRoomIsIn()) {
                                        currentPlayer.allowPlayerMovement();
                                        game.getUI().clearInformationLog();
                                        playerQuestion(currentPlayer);
                                    }
                                    else if (! currentPlayer.inBasement() && currentPlayer.getRoomLastQuestion() == currentPlayer.getRoomIsIn()) {
                                        canQuestion = false;
                                    }
                                    else {
                                        game.getUI().updateInformationLog(Errors.invalidQuestion());
                                    }
                                    break;
                                case "accuse":
                                    if (currentPlayer.inBasement()) {
                                        playerAccusation(currentPlayer);
                                    } else {
                                        game.getUI().updateInformationLog(Errors.invalidAccusation());
                                    }
                                    break;
                                case "log":
                                    game.getUI().clearInformationLog();
                                    game.getUI().updateInformationLog(Utility.getQuestionLog());
                                    break;
                                default:
                                    break;
                            }
                        }
                        while (!((command.equals("question") && canQuestion) || command.equals("accuse") || command.equals("done")));
                        currentPlayer.allowPlayerMovement();
                        game.getUI().clearInformationLog();
                    }
                }
            } while (true);
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
    }

    private void rollForPlayer(Player currentPlayer) {
        game.getUI().displayDie(game.getFirstDice(), game.getSecondDice());
        movement.setAllowedSteps(game.getFirstDice().getValue() + game.getSecondDice().getValue());

        if (currentPlayer.inRoom() && ! movement.startFromRoom()) {
            return;
        }

        game.getUI().updateInformationLog("You may now take a maximum of " + movement.getAllowedSteps() + " steps.\n");
        game.getUI().updateInformationLog(Utility.help("movement help"));
        movement.movingProcedure(game.getEnvelope(), game.getPublicDeck());
    }

    private void playerQuestion(Player askingPlayer) {
        String command, suspect, probableWeapon, room, cardToShow;
        askingPlayer.setRoomLastQuestion(askingPlayer.getRoomIsIn());

        suspect = getSuspectNameFromUser(askingPlayer);
        probableWeapon = getWeaponNameFromUser(askingPlayer);
        room = askingPlayer.getRoomIsIn().getName();
        moveSuspectAndWeapon(askingPlayer, suspect, probableWeapon);
        cardToShow = posePlayerQuestion(askingPlayer, suspect, probableWeapon, room);

        if (cardToShow.equals("none")) {
            game.getUI().clearInformationLog();
            game.getUI().updateInformationLog("None of the other players had any of the cards in your question.\n\nYour turn is now over. Press enter to continue.");

            try {
                synchronized (game.getUI().getHolder()) {

                    while (game.getUI().getHolder().isEmpty()) {
                        game.getUI().getHolder().wait();
                    }

                    command = game.getUI().getHolder().remove(0).trim().toLowerCase();

                    if (command.equals("quit")) System.exit(0);
                }
            } catch (InterruptedException e) {
                System.out.println(Errors.runTimeException());
            }
        }
    }

    private boolean playerQuestionCommandHelper(String command, Player player) {
        if (command.equals("help")) {
            game.getUI().clearInformationLog();

            if(player.inBasement()) {
                game.getUI().updateInformationLog("accusation help");
            } else {
                game.getUI().updateInformationLog(Utility.help("pose question help"));
            }
            return true;
        } else if (command.equals("quit")) System.exit(0);
        return false;
    }

    private String posePlayerQuestion(Player askingPlayer, String suspect, String probableWeapon, String room) {
        String question = "Question: was it " + suspect + " with a " + probableWeapon + " in the " + room + "?\n";
        Utility.addToQuestionLog("Asked by " + askingPlayer.getName() + ",\n" + question);

        String command, cardToShow;
        try {
            game.getUI().clearInformationLog();
            game.getUI().updateInformationLog("Press enter to confirm that the screen has been passed to next player.\n");

            synchronized (game.getUI().getHolder()) {
                while (game.getUI().getHolder().isEmpty()) {
                    game.getUI().getHolder().wait();
                }

                command = game.getUI().getHolder().remove(0).trim().toLowerCase();

                if (command.equals("quit")) System.exit(0);
                int numberOfPlayersAsked = 0;
                Player playerToBeAsked = askingPlayer;

                do {
                    playerToBeAsked = getNextPlayer(playerToBeAsked);
                    game.getUI().clearInformationLog();
                    game.getUI().updateInformationLog(playerToBeAsked.getName() + " it's your turn to respond to the question asked by " + askingPlayer.getName() + ".\n");
                    game.getUI().updateInformationLog(question);
                    cardToShow = checkForMatchingCards(playerToBeAsked, askingPlayer, suspect, probableWeapon, room);

                    if (! cardToShow.equals("none")) return cardToShow;
                    numberOfPlayersAsked++;
                } while (numberOfPlayersAsked < game.getPlayers().size() - 1);
            }
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        return "none";
    }

    private Player getNextPlayer(Player currentPlayer) {
        boolean flag = false;
        for (Player player : game.getPlayers()) {

            if (flag) return player;

            if (player.equals(currentPlayer)) {
                if (player.equals(game.getPlayers().getLast())) return game.getPlayers().getFirst();
                flag = true;
            }
        }
        return game.getPlayers().getFirst();
    }

    private String checkForMatchingCards(Player playerToBeAsked, Player askingPlayer, String suspect, String probableWeapon, String room) {
        boolean hasSuspect = false, hasWeapon = false, hasRoom = false;

        if (playerToBeAsked.getDeck().toString().toLowerCase().contains(suspect)) hasSuspect = true;

        if (playerToBeAsked.getDeck().toString().toLowerCase().contains(probableWeapon)) hasWeapon = true;

        if (playerToBeAsked.getDeck().toString().toLowerCase().contains(room)) hasRoom = true;

        if (! (hasSuspect || hasWeapon || hasRoom)) {
            game.getUI().updateInformationLog( playerToBeAsked.getName() + " you do not have any of these cards, press enter or use the \"done\" command to pass on the question to the next player.");
            try {
                synchronized (game.getUI().getHolder()) {

                    while (game.getUI().getHolder().isEmpty()) {
                        game.getUI().getHolder().wait();
                    }

                    String command = game.getUI().getHolder().remove(0).trim().toLowerCase();

                    if (command.equals("quit")) System.exit(0);

                    return "none";
                }
            } catch (InterruptedException e) {
                System.out.println(Errors.runTimeException());
            }
        }

        return selectMatchingCard(hasSuspect, hasWeapon, hasRoom, playerToBeAsked, askingPlayer, suspect, probableWeapon);
    }

    private String selectMatchingCard(boolean hasSuspect, boolean hasWeapon, boolean hasRoom, Player playerToAsk, Player askingPlayer, String suspect, String probableWeapon) {
        String command, cardToShow = "none";
        StringBuilder sb = new StringBuilder();

        game.getUI().clearInformationLog();
        game.getUI().updateInformationLog(playerToAsk.getName() + " you have one of the cards that " + askingPlayer.getName() + " is looking for! Cards: \n");

        if (hasSuspect) sb.append(suspect).append("\n");

        if (hasWeapon) sb.append(probableWeapon).append("\n");

        if (hasRoom) sb.append(askingPlayer.getRoomIsIn().getName()).append("\n");

        game.getUI().updateInformationLog(sb.toString() + "\nEnter the name of the card which you wish to show " + askingPlayer.getName() + " and return the screen to this player.");

        synchronized (game.getUI().getHolder()) {
            try {

                do {
                    while (game.getUI().getHolder().isEmpty()) {
                        game.getUI().getHolder().wait();
                    }

                    command = game.getUI().getHolder().remove(0).trim().toLowerCase();

                    if (command.equals(suspect) && hasSuspect) {
                        cardToShow = suspect;
                        break;
                    } else if (command.equals(probableWeapon) && hasWeapon) {
                        cardToShow = probableWeapon;
                        break;
                    } else if (command.equals(askingPlayer.getRoomIsIn().getName()) && hasRoom) {
                        cardToShow = askingPlayer.getRoomIsIn().getName();
                        break;
                    }

                    game.getUI().updateInformationLog("You entered an invalid card name.\n");

                }
                while (! (cardToShow.equals(suspect) || cardToShow.equals(probableWeapon) || cardToShow.equals(askingPlayer.getRoomIsIn().getName())));

            } catch (InterruptedException e) {
                System.out.println(Errors.runTimeException());
            }
        }

        game.getUI().clearInformationLog();
        game.getUI().updateInformationLog(playerToAsk.getName() + " showed you the following card: " + cardToShow + ".\nIt will now be marked on your notes [V], press enter to complete your turn.");

        askingPlayer.getDeck().updateSeenCards(cardToShow);

        synchronized (game.getUI().getHolder()) {
            try {
                while (game.getUI().getHolder().isEmpty()) {
                    game.getUI().getHolder().wait();
                }

                command = game.getUI().getHolder().remove(0).trim().toLowerCase();

                if(command.equals("quit")) System.exit(0);


            } catch (InterruptedException e) {
                System.out.println(Errors.runTimeException());
            }
        }

        return cardToShow;
    }

    private void playerAccusation(Player accusingPlayer) {
       String suspect = getSuspectNameFromUser(accusingPlayer), weapon = getWeaponNameFromUser(accusingPlayer), room = getRoomNameFromUser(accusingPlayer);

        accusingPlayer.setHasAccused(true);

       if(checkPlayerAccusation(suspect, weapon, room)) winGameForPlayer(accusingPlayer);

       if(findNumberOfPlayersThatHaveAccused() == game.getPlayers().size() - 1) winGameForPlayer(findPlayerYetToAccuse());

       if(findNumberOfPlayersThatHaveAccused() == game.getPlayers().size()) gameOver();

       game.getUI().clearInformationLog();
       game.getUI().updateInformationLog("Your accusation was incorrect and you may only now answer questions.\n\nPress enter to continue.");

        try {
            synchronized (game.getUI().getHolder()) {

                while (game.getUI().getHolder().isEmpty()) {
                    game.getUI().getHolder().wait();
                }

                String command = game.getUI().getHolder().remove(0).trim().toLowerCase();

                if (command.equals("quit")) System.exit(0);
            }
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
    }

    private String getSuspectNameFromUser(Player player) {
        game.getUI().clearInformationLog();
        String command = "";
        try {
            do {
                game.getUI().updateInformationLog("Enter the name of your suspect.\n");

                while (game.getUI().getHolder().isEmpty()) {
                    game.getUI().getHolder().wait();
                }

                command = game.getUI().getHolder().remove(0).trim().toLowerCase();
                game.getUI().updateInformationLog(game.getUI().formatUserText(command));

                if (playerQuestionCommandHelper(command, player)) continue;

                if (validateCharacterName(command)) {
                    break;
                }
                game.getUI().updateInformationLog("You entered an invalid suspect name.\nUse the \"help\" command if you need more information.\n");
            }
            while (true);


        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        return command;
    }

    private String getWeaponNameFromUser(Player player) {
        game.getUI().clearInformationLog();
        String command = "";
        try {
            do {
                game.getUI().updateInformationLog("Enter the name of the probable murder weapon.\n");

                while (game.getUI().getHolder().isEmpty()) {
                    game.getUI().getHolder().wait();
                }

                command = game.getUI().getHolder().remove(0).trim().toLowerCase();
                game.getUI().updateInformationLog(game.getUI().formatUserText(command));

                if (playerQuestionCommandHelper(command, player)) continue;

                if (validateWeaponName(command)) {
                    break;
                }
                game.getUI().updateInformationLog("You entered an invalid weapon name.\nUse the \"help\" command if you need more information.\n");
            }
            while (true);


        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        return command;
    }

    private String getRoomNameFromUser(Player player) {
        game.getUI().clearInformationLog();
        String command = "";
        try {
            do {
                game.getUI().updateInformationLog("What room do you believe the murder happened in?.\n");

                while (game.getUI().getHolder().isEmpty()) {
                    game.getUI().getHolder().wait();
                }

                command = game.getUI().getHolder().remove(0).trim().toLowerCase();
                game.getUI().updateInformationLog(game.getUI().formatUserText(command));

                if (playerQuestionCommandHelper(command, player)) continue;

                if (validateRoomName(command)) {
                    break;
                }

                game.getUI().updateInformationLog("You entered an invalid room name.\nUse the \"help\" command if you need more information.\n");
            }
            while (true);
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        return command;
    }

    private boolean checkPlayerAccusation(String suspect, String weaponName, String room) {
        String envelope = game.getEnvelope().cheat().toLowerCase().replace("_", " ").replace(".", "");

        return envelope.contains(suspect) && envelope.contains(weaponName) && envelope.contains(room);
    }

    private boolean validateCharacterName(String characterName) {
        for (Characters character : Characters.values()) {
            if (characterName.contains(character.getDisplayName().trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean validateWeaponName(String weaponName) {
        for (Weapons weapon : Weapons.values()) {
            if (weaponName.contains(weapon.getDisplayName().trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean validateRoomName(String roomName) {
        for (Rooms room : Rooms.values()) {
            if (roomName.contains(room.getDisplayName().trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private Player findPlayer(String suspect) {
        for (Player p : game.getPlayers()) {
            if (suspect.equals(p.getCharacter().getDisplayName().toLowerCase()))
                return p;
        }
        return null;
    }

    private Weapons findWeapon(String suspectWeapon) {
        for (Weapons a : Weapons.values()) {
            if (suspectWeapon.equals(a.getName().toLowerCase()))
                return a;
        }
        return null;
    }

    private void moveSuspectAndWeapon(Player currentPlayer, String suspect, String probableWeapon) {
        Player tmpPl = findPlayer(suspect);
        Weapons tmpWp = findWeapon(probableWeapon);
        if (tmpPl != null) {
            currentPlayer.getRoomIsIn().getLabel().add(tmpPl.getCharacterLabel());
            tmpPl.playerEnterRoom(currentPlayer.getRoomIsIn());
            game.getUI().getBoardPanel().getBackgroundLabel().remove(tmpPl.getCharacterLabel());
            game.getUI().getBoardPanel().getBackgroundLabel().remove(tmpPl.getPlayerNameLabel());
        }
        if(tmpWp != null) {
            currentPlayer.getRoomIsIn().getLabel().add(findWeapon(probableWeapon).getLabel());
        }
        game.getUI().clearInformationLog();
        game.getUI().refresh();
    }

    private int findNumberOfPlayersThatHaveAccused() {
        int numberOfPlayersThatHaveAccused = 0;
        for (Player player: game.getPlayers()) {
            if(player.hasAccused()) numberOfPlayersThatHaveAccused++;
        }
        return numberOfPlayersThatHaveAccused;
    }

    private Player findPlayerYetToAccuse() {
        for(Player player: game.getPlayers()) {
            if (! player.hasAccused()) return player;
        }
        return game.getPlayers().getFirst();
    }

    private void winGameForPlayer(Player player) {
        game.getUI().clearInformationLog();
        game.getUI().updateInformationLog("Congratulations! " + player.getName() + " has won the game.\nPress enter to end the game. Thanks for playing.");
        exitGameAfterUserInteraction();
    }

    private void gameOver() {
        game.getUI().clearInformationLog();
        game.getUI().updateInformationLog("It seems that all players have made a false accusation! The game is " +
                "now over as there can be no winner.\n\n Below are the contents of the murder envelope:\n " + game.getEnvelope().cheat() + "\n\nPress enter to end the game. Thanks for playing!");
        exitGameAfterUserInteraction();
    }

    private void exitGameAfterUserInteraction() {
        try {
            synchronized (game.getUI().getHolder()) {

                while (game.getUI().getHolder().isEmpty()) {
                    game.getUI().getHolder().wait();
                }

                String command = game.getUI().getHolder().remove(0).trim().toLowerCase();
                if (command.equals("quit")) System.exit(0);
            }
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        System.exit(0);
    }
}