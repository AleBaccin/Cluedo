// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import java.util.LinkedList;

public class Cards extends LinkedList<Token> {

    Cards(Envelope envelope) {
        for (Rooms r : Rooms.values()) {
            if (! (r.getName().equals(envelope.getRoomName()) || r.getName().equals("basement")))
                this.add(r);
        }

        for (Weapons a : Weapons.values()) {
            if (! a.getName().equals(envelope.getWeaponName()))
                this.add(a);
        }

        for (Characters c : Characters.values()) {
            if (! c.getName().equals(envelope.getKillerName()))
                this.add(c);
        }
    }

    public String getNoteFormatString() {
        StringBuilder buffer = new StringBuilder();

        for (Token token : this) {
            buffer.append(token.getDisplayName()).append(" [A]").append("\n");
        }

        return buffer.toString();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        buffer.append("Deck:\n");

        for (Token token : this) {
            buffer.append(token.getDisplayName()).append("\n");
        }

        if(buffer.toString().length() == 6) {
            buffer.append("The deck is empty.\n");
        }

        return buffer.toString();
    }
}
