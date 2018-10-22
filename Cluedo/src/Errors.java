// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

class Errors {
    public static String usernameAlreadyExisting(String username) {
        return "\n!!-->The username " + username + " already exists.\nPlease choose another username.<--!!\n";
    }

    public static String notEnoughPlayersError() {
        return "!!-->You need at least 2 players to play.<--!!\n";
    }

    public static String invalidCommandError() {
        return "\n!!-->You entered an invalid command.\nEnter \"help\" for more information.<--!!\n";
    }

    public static String runTimeException() {
        return "\n!!-->Error: runtime interruption.<--!!\n";
    }

    public static String rollRequiredError() {
        return "\n!!-->You entered an invalid command.\nNote: you must perform a roll on your turn.<--!!\n";
    }

    public static String positionNotAvailableError() {
        return "\n!!-->You must not walk through walls or other players. \nYou cannot move on the same square two times in the same turn.<--!!\n";
    }

    public static String characterNotFoundError(String character) {
        return "\n!!-->Error: could not find the character: " + character + ".<--!!\n";
    }

    public static String invalidMovingInput(String inputCommand) {
        return "\n!!-->" + inputCommand + " does not map to a valid movement command.<--!!\n";
    }

    public static String invalidExitNumber() {
        return "\n!!-->Invalid input, please try again.<--!!\n";
    }

    public static String invalidAccusation() {
        return "!!-->You may only make an accusation when you're in the basement.<--!!\n";
    }

    public static String invalidQuestion() {
        return "!!-->You may only ask a question when in a room (excluding the basement).<--!!\n";
    }

    public static String invalidQuestionRoom() {
        return "!!-->You can't ask a question in the same room two times in a row. You must leave this room.<--!!\n";
    }

    public static String couldNotFindHelpMessage() {
        return "\n!!-->Could not find help message.<--!!\n";
    }

    public static String movingTwoTimesError(String username){
        return "\n!!-->" + username +" you already moved in this turn.<--!!\n";
    }

    public static String enteringRoomTwoTimesError(String username, String room){
        return "\n!!-->" + username +" you already entered the "+ room +" in the previous turn.<--!!\n";
    }

    public static String sameSquareError(String username){
        return "\n!!-->" + username +" you already moved in this position.\n";
    }

    public static String exitOccupiedError(int exit) {
        return "\n!!-->Exit number: " + exit + " is occupied.<--!!\n";
    }
}
