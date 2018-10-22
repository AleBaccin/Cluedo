// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

public class Envelope {
    private final Token weapon;
    private final Token room;
    private final Token character;

    Envelope() {
        this.weapon = Weapons.getRandomWeapon();
        this.room = Rooms.getRandomRoom();
        this.character = Characters.getRandomCharacter();
    }

    public String cheat() {
        return "Murderer: " + character.getDisplayName() + ".\n" + "Weapon: " + weapon.getDisplayName() + ".\n" + "Crime scene: " + room.getDisplayName() + ".\n";
    }

    public String getWeaponName() {
        return weapon.getName();
    }

    public String getRoomName() {
        return room.getName();
    }

    public String getKillerName() {
        return character.getName();
    }
}