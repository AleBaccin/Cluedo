// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

// Enum class defining the playable characters of the game.
public enum Characters implements Token {
    COLONEL_MUSTARD("colonel_mustard", "Colonel Mustard", new Point(46, 488), new Color(255, 215, 122)),
    MRS_PEACOCK("mrs_peacock", "Mrs Peacock", new Point(667, 191), new Color(135, 185, 255)),//TODO new Point(262, 488)
    MRS_SCARLETT("mrs_scarlett", "Mrs Scarlett", new Point(235, 677), new Color(255, 109, 124)),
    MRS_WHITE("mrs_white", "Mrs White", new Point(289, 29), Color.WHITE),
    PROFESSOR_PLUM("professor_plum", "Professor Plum",  new Point(667, 542), new Color(193, 128, 255)),//TODO FIX new Point(262, 515)
    REV_GREEN("rev_green", "Rev Green", new Point(424, 29), new Color(144, 238, 144));

    private final String characterName;
    private final String displayName;

    private final CharacterLabel characterLabel;
    private final Point location;
    private final Color color;

    Characters(String characterName, String displayName, Point location, Color color) {
        this.displayName = displayName;
        this.characterName = characterName;
        this.location = location;
        this.characterLabel = new CharacterLabel(characterName, location);
        this.color = color;
    }

    public static Characters getRandomCharacter() {
        SecureRandom random = new SecureRandom();
        return values()[random.nextInt(values().length)];
    }

    public static List<Characters> getList() {
        EnumSet<Characters> charactersSet = EnumSet.of(Characters.COLONEL_MUSTARD,
                Characters.MRS_PEACOCK, Characters.MRS_SCARLETT, Characters.MRS_WHITE,
                Characters.PROFESSOR_PLUM, Characters.REV_GREEN);
        return new ArrayList<>(charactersSet);
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public String getName() {
        return this.characterName;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    public double getX() {
        return this.location.getX();
    }

    public double getY() {
        return this.location.getY();
    }
}
