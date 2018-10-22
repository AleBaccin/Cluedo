// Cluedo - TeamAbacus
// Authors: Alessandro Baccin(16724489) & William Akinsanya(16350593)

import java.util.LinkedList;

public class Deck extends LinkedList<Token> {
    private StringBuilder seenCards = new StringBuilder();

    public void updateSeenCards(String card) {
        seenCards.append(card).append(" [V]").append("\n");
    }

    public String getNoteFormatString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Cards visible to you (Key: X = your cards, A = public deck, V = seen cards)\n");

        for (Token token : this) {
            sb.append(token.getDisplayName()).append(" [X]").append("\n");
        }
        sb.append(seenCards);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Your cards:\n");

        for (Token token : this) {
            sb.append(token.getDisplayName()).append("\n");
        }

        return sb.toString();
    }
}
