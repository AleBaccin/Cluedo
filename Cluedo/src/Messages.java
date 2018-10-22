class Messages {

    private final static String GREETING = "WELCOME TO CLUEDO!\nThe game allows for 2 - 6 players.\nIt's time to know who's playing, " +
            "enter \"done\" to finish adding players.\n\nYou can always use the \"help\" command for more information.";

    private final static String MOVEMENT_LEGEND = "Move your character by entering:\n" +
            "\"up\" or \"u\" to go up.\n" +
            "\"down\" or \"d\" to move down.\n" +
            "\"left\" or \"l\" to move left.\n" +
            "\"right\" or \"r\" to move right.\n";


    private final static String ADDING_PLAYERS_HELP = "Select a character by entering their initials." +
            "\nEnter \"done\" when you are finished adding players.\nRemember the game requires at least 2 players!\n";


    private final static String ROLL_HELP = "Roll the dice and move your character token that number of spaces around the board.\n" +
            "You can move vertically and horizontally, forward and back through the corridor, but not diagonally.\n" +
            "Always try to enter a room (at the start, any room will do). You don’t need an exact roll to enter a room.\n" +
            "If your roll would’ve taken you beyond the room, just  answer yes to the prompt and your move will end in the room.\n" +
            "To check your own cards type \"cards\". To check the cards in the remaining deck type \"deck\".\n";

    private final static String PRELUDE = "This evening Samuel Black was found murdered in his mansion! Detectives found six suspects and " +
            "six weapons in the mansion’s nine rooms, but could not solve the case. So, now it’s up to you to solve the murder!\n" +
            "To win the game you must find out three things about the murder:\n" +
            "1. Who did it?\n" +
            "2. With what weapon?\n" +
            "3. And where?\n";

    private final static String NOTES = "\nALL GAME CARDS\nSuspects:\nColonel Mustard\nMrs. Peacock\nMiss Scarlet\nMrs. White\nProfessor Plum\nRev. Green\n" +
            "\nWeapons:\nCandlestick\nKnife\nLead Pipe\nRevolver\nRope\nWrench\n\nRooms:\nKitchen\nBallroom\nConservatory\nDining Room\nBilliard Room\n" +
            "Library\nLounge\nHall\nStudy\n";

    private final static String TURN_HELP = "Roll the dice by entering \"roll\" or \"r\".\n" +
            "\nTo view your cards enter \"cards\" or \"c\".\n" +
            "\nUse the command \"deck\" to see the cards which do not belong to any player.\n" +
            "\nThe \"notes\" command returns a list of all cards including a subsection of cards visible to you.\n" +
            "\nEnter \"done\" when you are finished. Don't hug the game.\n";

    private final static String ADD_PLAYERS_INTRO_MESSAGE = "\nIf this is your first time playing the game, type and enter \"prelude\".\n" +
            "\nHint: Select a character by entering their initials.\n";

    private final static String RUN_INTRO_MESSAGE = "The game has now started, quit the game at anytime by entering \"quit\".\n\nHint: Enter " +
            "\"done\" if you're finished with your turn.\n";

    private final static String POSE_QUESTION_MESSAGE = "You are in a room. Use the \"question\" command to interrogate other players!\n";

    private final static String POSING_QUESTION_MESSAGE = "You may now pose a question to your fellow players.\nTo do this you must provide a suspect and a " +
            "probable murder weapon. The room which you are in will form the final part of your \'CLUE\' question.\n\nIf any of the other players have a card from " +
            "your question they must show it to you. If they have more than one of your" +
            " suspected cards, they may choose which of these cards to show to you.\n";

    private final static String HALL_OR_BASEMENT = "\nDo you want to enter the hall or basement?\nType and enter \"basement\" (b) or \"hall\" (h).\n";

    private final static String POSE_QUESTION_HELP = "In order to \'pose\' a question to other players, you must name a suspect (one of the six characters of the game)" +
            " and your suspected murder weapon.\nThe room you've just entered will form the final part of your \'CLUE\' question.\n";

    private final static String ACCUSATION_HELP = "You are in the basement and can now make an accusation (using the \"accuse\" command). If your accusation is correct, you win the game. Making an incorrect accusation will " +
            "cause you to be eliminated from the game and only be able to answer questions.\n\nImportant: if there are two players playing or remaining and your accusation is incorrect, the " +
            "player yet to accuse will automatically win the game. So think carefully!\n";

    private final static String SUSPECT_LIST = "";

    public static String getGreeting() {
        return GREETING;
    }

    public static String getMovementLegend() {
        return MOVEMENT_LEGEND;
    }

    public static String getAddingPlayersHelp() {
        return ADDING_PLAYERS_HELP;
    }

    public static String getRollHelp() {
        return ROLL_HELP;
    }

    public static String getPRELUDE() {
        return PRELUDE;
    }

    public static String getNOTES() {
        return NOTES;
    }

    public static String getTurnHelp() {
        return TURN_HELP;
    }

    public static String getAddPlayersIntroMessage() {
        return ADD_PLAYERS_INTRO_MESSAGE;
    }

    public static String getRunIntroMessage() {
        return RUN_INTRO_MESSAGE;
    }

    public static String getPoseQuestionMessage() {
        return POSE_QUESTION_MESSAGE;
    }

    public static String getHallOrBasement() {
        return HALL_OR_BASEMENT;
    }

    public static String getPoseQuestionHelp() {
        return POSE_QUESTION_HELP;
    }

    public static String getPosingQuestionMessage() {
        return POSING_QUESTION_MESSAGE;
    }

    public static String getSuspectList() {
        return SUSPECT_LIST;
    }

    public static String getNotes() {
        return NOTES;
    }

    public static String getAccusationHelp() {
        return ACCUSATION_HELP;
    }

}
