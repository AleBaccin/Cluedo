// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import java.awt.*;
import java.util.LinkedList;

class Movement {
    private UserInterface ui;
    private Player playerToMove;
    private LinkedList<Player> players;
    private int allowedSteps;

    Movement(Player playerToMove, LinkedList<Player> players, UserInterface ui) {
        this.players = players;
        this.ui = ui;
        this.playerToMove = playerToMove;
    }

    public void setPlayers(LinkedList<Player> players) {
        this.players = players;
    }

    public void setPlayer(Player playerToMove) {
        this.playerToMove = playerToMove;
    }

    public int getAllowedSteps() {
        return allowedSteps;
    }

    public void setAllowedSteps(int allowedSteps) {
        this.allowedSteps = allowedSteps;
    }

    public void movingProcedure(Envelope envelope, Cards publicDeck) {
        String command;
        try {
            while (getAllowedSteps() > 0) {
                playerToMove.addVisitedBoxes(playerToMove.getCharacterLabelLocation());
                ui.showAllowedPositions(playerToMove, players, playerToMove.getCharacterLabelLocation());
                if (ui.isEntrance(playerToMove.getCharacterLabel().getX(), playerToMove.getCharacterLabel().getY())) {
                    if (enteringRoomProcedure(playerToMove.getCharacterLabelLocation())) {
                        ui.clearPositionsAllowed();
                        return;
                    }
                    ui.updateInformationLog(Messages.getMovementLegend());
                }

                synchronized (ui.getHolder()) {
                    while (ui.getHolder().isEmpty()) {
                        ui.getHolder().wait();
                    }
                    command = ui.getHolder().remove(0).trim().toLowerCase();
                    ui.updateInformationLog(ui.formatUserText(command));

                    if (command.equals("done")) {
                        ui.clearPositionsAllowed();
                        return;
                    } else if (movingCharacter(command, Utility.encodingMovingInput(command))) {
                        setAllowedSteps(getAllowedSteps() - 1);
                        ui.updateInformationLog("You have: " + getAllowedSteps() + " steps remaining.\n");
                    } else if (command.equals("cheat")) {
                        ui.updateInformationLog(envelope.cheat());
                        ui.updateInformationLog(Messages.getMovementLegend());
                    } else if (command.equals("deck")) {
                        ui.updateInformationLog(publicDeck.toString());
                    } else if (command.equals("cards") || command.equals("c")) {
                        ui.updateInformationLog(playerToMove.getDeck().toString());
                    } else if (command.equals("notes")) {
                        ui.updateInformationLog(playerToMove.getDeck().getNoteFormatString() + publicDeck.getNoteFormatString() + Messages.getNotes());
                    } else if (command.equals("quit")) {
                        System.exit(0);
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        ui.clearPositionsAllowed();
    }

    private boolean movingCharacter(String command, char direction) {
        boolean moved = false;
        if (direction == 'i') {
            ui.updateInformationLog(Errors.invalidMovingInput(command));
            moved = false;
        } else if (playerToMove.canMove(ui.isCell((int) playerToMove.getFutureLocation(direction, playerToMove.getCharacterLabelLocation()).getX(),
                (int) playerToMove.getFutureLocation(direction, playerToMove.getCharacterLabelLocation()).getY()), getAllowedSteps()) &&
                ! ui.isCellFree(playerToMove, players, playerToMove.getFutureLocation(direction, playerToMove.getCharacterLabelLocation())) && !(playerToMove.isInVisitedBoxes(playerToMove.getFutureLocation(direction, playerToMove.getCharacterLabelLocation())))) {
            switch (direction) {
                case 'u':
                    playerToMove.addVisitedBoxes(playerToMove.moveUp());
                    moved = true;
                    break;
                case 'd':
                    playerToMove.addVisitedBoxes(playerToMove.moveDown());
                    moved = true;
                    break;
                case 'l':
                    playerToMove.addVisitedBoxes(playerToMove.moveLeft());
                    moved = true;
                    break;
                case 'r':
                    playerToMove.addVisitedBoxes(playerToMove.moveRight());
                    moved = true;
                    break;
                default:
                    break;
            }
        } else {
            ui.updateInformationLog(Errors.positionNotAvailableError());
        }
        return moved;
    }

    private boolean enteringRoomProcedure(Point p) {
        if(!isPreviousRoom(p) || (playerToMove.getPreviouslyVisitedRoom() == Rooms.BASEMENT || playerToMove.getPreviouslyVisitedRoom() == Rooms.HALL) && p.equals(new Point(370, 488))){
            ui.updateInformationLog("This is a room entrance, do you want to enter the room? Enter \"y\" for yes and \"n\" for no. (Y/N).\n");
        }
        else{
            ui.updateInformationLog(Errors.enteringRoomTwoTimesError(playerToMove.getName(), playerToMove.getPreviouslyVisitedRoom().getDisplayName()));
            return false;
        }
        String command;
        try {
            synchronized (ui.getHolder()) {
                do {
                    while (ui.getHolder().isEmpty()) {
                        ui.getHolder().wait();
                    }
                    command = ui.getHolder().remove(0).trim().toLowerCase();
                    ui.updateInformationLog(ui.formatUserText(command));

                    switch (command) {
                        case "done":
                            return false;
                        case "yes":
                        case "y":
                            enterRoom(p);
                            return true;
                        case "no":
                        case "n":
                            return false;
                        case "quit":
                            System.exit(0);
                        default:
                           ui.updateInformationLog("You entered an invalid command. Please enter \"y\" for yes or \"n\" for no.");
                    }
                }
                while (! (command.equals("done") || command.equals("yes") || command.equals("no") || command.equals("n") || command.equals("y")));
            }
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        return false;
    }

    private void enterRoom(Point p) {
        for (Rooms r : Rooms.values()) {
            for (Point ep : r.getEntrance().getEntrancesCoordinates()) {
                if (ep.equals(p)) {
                    Rooms temp;
                    if (ep.equals(Rooms.HALL.getEntrance().getEntrancesCoordinates()[1])) {
                        temp = hallOrBasement();
                        assert temp != null;
                        temp.getLabel().add(playerToMove.getCharacterLabel());
                    } else {
                        temp = r;
                        temp.getLabel().add(playerToMove.getCharacterLabel());
                    }
                    playerToMove.playerEnterRoom(temp);
                    ui.getBoardPanel().getBackgroundLabel().remove(playerToMove.getCharacterLabel());
                    ui.getBoardPanel().getBackgroundLabel().remove(playerToMove.getPlayerNameLabel());
                    ui.refresh();
                    return;
                }
            }
            ui.clearPositionsAllowed();
        }
        setAllowedSteps(0);
    }

    private Rooms hallOrBasement() {
        if(playerToMove.getPreviouslyVisitedRoom() == Rooms.BASEMENT){
            return Rooms.HALL;
        }
        else if (playerToMove.getPreviouslyVisitedRoom() == Rooms.HALL){
            return Rooms.BASEMENT;
        }
        ui.updateInformationLog(Messages.getHallOrBasement());
        String command;
        try {
            synchronized (ui.getHolder()) {
                do {
                    while (ui.getHolder().isEmpty()) {
                        ui.getHolder().wait();
                    }
                    command = ui.getHolder().remove(0).trim().toLowerCase();
                    ui.updateInformationLog(ui.formatUserText(command));

                    switch (command) {
                        case "hall":
                        case "h":
                            return Rooms.HALL;
                        case "basement":
                        case "b":
                            return Rooms.BASEMENT;
                    }
                }
                while (! (command.equals("hall") || command.equals("h") || command.equals("basement") || command.equals("b")));
            }
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        return null;
    }

    public boolean startFromRoom() {
        if(freeEntrances()[0] || freeEntrances()[1] || freeEntrances()[2] || freeEntrances()[3]) {
            ui.updateInformationLog("You are in a Room. Do you want to leave? Enter \"y\" for yes and \"n\" for no. (Y/N).\n");
        }else{
            ui.updateInformationLog("You can't leave cause all the exits are occupied.\n");
            playerToMove.allowPlayerMovement();
            return false;
        }

        String command;
        try {
            synchronized (ui.getHolder()) {
                do {
                    while (ui.getHolder().isEmpty()) {
                        ui.getHolder().wait();
                    }
                    command = ui.getHolder().remove(0).trim().toLowerCase();
                    ui.updateInformationLog(ui.formatUserText(command));

                    switch (command) {
                        case "done":
                            return false;
                        case "yes":
                        case "y":
                            leaveRoom();
                            return true;
                        case "no":
                        case "n":
                            return false;
                        case "quit":
                            System.exit(0);
                    }
                }
                while (!(command.equals("done") || command.equals("yes") || command.equals("no") || command.equals("n") || command.equals("y")));
            }
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        return false;
    }

    private boolean[] freeEntrances(){
        boolean[] exits = new boolean[]{true, true, true, true};
        int count = 0;
        Rooms temp = playerToMove.getRoomIsIn();
            for (Point ep : temp.getEntrance().getEntrancesCoordinates()) {
                if(ui.isCellFree(playerToMove, players, ep)){
                    exits[count] = false;
                }
                count++;
            }
            return exits;
    }

    private void leaveRoom() {
        Rooms temp = playerToMove.getRoomIsIn();
        Point p;
        if (temp.getEntrance().getEntrancesCoordinates().length > 1) {
            int exit = getExitNumber(temp.getEntrance()) - 1;
            p = temp.getEntrance().getEntrancesCoordinates()[exit];
        } else {
            p = temp.getEntrance().getEntrancesCoordinates()[0];
        }
        playerToMove.playerLeaveRoom();
        playerToMove.setCharacterLabelLocation((int) p.getX(), (int) p.getY());
        playerToMove.addVisitedBoxes(p);
        playerToMove.updateNameLabelPosition();
        temp.getLabel().remove(playerToMove.getCharacterLabel());
        ui.getBoardPanel().getBackgroundLabel().add(playerToMove.getCharacterLabel());
        ui.getBoardPanel().getBackgroundLabel().add(playerToMove.getPlayerNameLabel());
        ui.refresh();
        setAllowedSteps(getAllowedSteps() - 1);
    }

    private int getExitNumber(Entrance en) {
        int exit = 1;
        String command;
        try {
            synchronized (ui.getHolder()) {
                ui.updateInformationLog("From which exit would you prefer to leave? (Numbered from left to right)\n");
                do {
                    while (ui.getHolder().isEmpty()) {
                        ui.getHolder().wait();
                    }
                    command = ui.getHolder().remove(0).trim().toLowerCase();
                    ui.updateInformationLog(ui.formatUserText(command));
                    if (command.equals("done")) {
                        return exit;
                    } else if (Utility.isNumeric(command) && en.getEntrancesCoordinates().length > Integer.parseInt(command) - 1) {
                        exit = Integer.parseInt(command);
                        if(freeEntrances()[exit-1]) {
                            return exit;
                        }
                        else {
                            ui.updateInformationLog(Errors.exitOccupiedError(exit));
                        }
                    } else {
                        ui.updateInformationLog(Errors.invalidExitNumber());
                    }
                }
                while (Utility.isNumeric(command) && en.getEntrancesCoordinates().length > Integer.parseInt(command) - 1);
            }
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        return exit;
    }

    private void teleport() {
        Rooms temp = Rooms.roomConnected(playerToMove.getRoomIsIn());
        if(temp != playerToMove.getPreviouslyVisitedRoom()){
            playerToMove.playerLeaveRoom();
            playerToMove.playerEnterRoom(temp);
            playerToMove.getRoomIsIn().getLabel().remove(playerToMove.getCharacterLabel());
            temp.getLabel().add(playerToMove.getCharacterLabel());
            playerToMove.moved();
            ui.refresh();
        }
        else{
            ui.updateInformationLog(Errors.enteringRoomTwoTimesError(playerToMove.getName(), playerToMove.getPreviouslyVisitedRoom().getDisplayName()));
            playerToMove.allowPlayerMovement();
        }
    }

    public boolean secretPassageOption() {
        String command;
        try {
            synchronized (ui.getHolder()) {
                do {
                    ui.updateInformationLog("Do you want to use the secret passage? Enter \"y\" for yes and \"n\" for no. (Y/N)");
                    while (ui.getHolder().isEmpty()) {
                        ui.getHolder().wait();
                    }
                    command = ui.getHolder().remove(0).trim().toLowerCase();
                    ui.updateInformationLog(ui.formatUserText(command));

                    switch (command) {
                        case "done":
                            playerToMove.allowPlayerMovement();
                            return false;
                        case "yes":
                        case "y":
                            teleport();
                            return true;
                        case "no":
                        case "n":
                            playerToMove.allowPlayerMovement();
                            return false;
                        case "quit":
                            System.exit(0);
                    }
                }
                while (! (command.equals("done") || command.equals("yes") || command.equals("no") || command.equals("n") || command.equals("y")));
            }
        } catch (InterruptedException e) {
            System.out.println(Errors.runTimeException());
        }
        return false;
    }

    public boolean isPreviousRoom(Point p) {
        for (Rooms r : Rooms.values()) {
            for (Point ep : r.getEntrance().getEntrancesCoordinates()) {
                    if (ep.equals(p) && !(playerToMove.getPreviouslyVisitedRoom() == r)) {
                        return false;
                    } else if (ep.equals(p) && (playerToMove.getPreviouslyVisitedRoom() == r)) {
                        return true;
                    }
            }
        }
        return true;
    }
}
