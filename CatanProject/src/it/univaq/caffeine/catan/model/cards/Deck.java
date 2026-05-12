package it.univaq.caffeine.catan.model.cards;

import it.univaq.caffeine.catan.model.cards.DevelopmentCard.CardType;
import java.util.*;

/**
 * Deck — the 25-card development deck of standard Catan.
 *   14 × Cavaliere
 *    2 × Monopolio
 *    2 × Scoperta
 *    2 × Costruzione di Strade
 *    5 × Punti Vittoria (Municipio, Piazza, Cattedrale, Biblioteca, Università)
 */
public class Deck {
    private final List<DevelopmentCard> cards = new ArrayList<>();

    public Deck() { initializeDeck(); }

    private void initializeDeck() {
        for (int i = 0; i < 14; i++) cards.add(new DevelopmentCard(CardType.CAVALIERE));
        for (int i = 0; i < 2;  i++) cards.add(new DevelopmentCard(CardType.MONOPOLIO));
        for (int i = 0; i < 2;  i++) cards.add(new DevelopmentCard(CardType.SCOPERTA));
        for (int i = 0; i < 2;  i++) cards.add(new DevelopmentCard(CardType.COSTRUZIONE_STRADE));
        cards.add(new DevelopmentCard(CardType.MUNICIPIO));
        cards.add(new DevelopmentCard(CardType.PIAZZA_DEL_MERCATO));
        cards.add(new DevelopmentCard(CardType.CATTEDRALE));
        cards.add(new DevelopmentCard(CardType.BIBLIOTECA));
        cards.add(new DevelopmentCard(CardType.UNIVERSITA));
    }

    public DevelopmentCard drawCard() {
        return cards.isEmpty() ? null : cards.remove(0);
    }

    public void shuffle() { Collections.shuffle(cards); }

    /** Count of cards of a specific type remaining. */
    public long countByType(CardType type) {
        return cards.stream().filter(c -> c.getType() == type).count();
    }

    public List<DevelopmentCard> getCards() { return cards; }
    public int  size()                       { return cards.size(); }
    public boolean isEmpty()                 { return cards.isEmpty(); }
}
