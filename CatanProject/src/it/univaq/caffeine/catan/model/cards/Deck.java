package it.univaq.caffeine.catan.model.cards;

import it.univaq.caffeine.catan.model.cards.DevelopmentCard.CardType;
import java.util.*;

/**
 * Deck — the 25-card development deck of standard Catan.
 *   14 × Knight
 *    2 × Monopoly
 *    2 × Discovery
 *    2 × Road Building
 *    5 × Victory Points (Town Hall, Market Square, Cathedral, Library, University)
 */
public class Deck {
    private final List<DevelopmentCard> cards = new ArrayList<>();

    public Deck() { initializeDeck(); }

    private void initializeDeck() {
        for (int i = 0; i < 14; i++) cards.add(new DevelopmentCard(CardType.KNIGHT));
        for (int i = 0; i < 2;  i++) cards.add(new DevelopmentCard(CardType.MONOPOLY));
        for (int i = 0; i < 2;  i++) cards.add(new DevelopmentCard(CardType.DISCOVERY));
        for (int i = 0; i < 2;  i++) cards.add(new DevelopmentCard(CardType.ROAD_BUILDING));
        cards.add(new DevelopmentCard(CardType.TOWN_HALL));
        cards.add(new DevelopmentCard(CardType.MARKET_SQUARE));
        cards.add(new DevelopmentCard(CardType.CATHEDRAL));
        cards.add(new DevelopmentCard(CardType.LIBRARY));
        cards.add(new DevelopmentCard(CardType.UNIVERSITY));
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
