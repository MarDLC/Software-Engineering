package it.univaq.caffeine.catan.model.cards;

/**
 * DevelopmentCard — models the 25 development cards of Catan.
 * Types from the actual Italian Catan edition (see card image).
 */
public class DevelopmentCard {

    public enum CardType {
        // Knight (14 cards) — blue header
        KNIGHT,
        // Progress (6 cards total) — gray header
        MONOPOLY, DISCOVERY, ROAD_BUILDING,
        // Victory Points (5 cards) — orange header
        TOWN_HALL, MARKET_SQUARE, CATHEDRAL, LIBRARY, UNIVERSITY
    }

    private final CardType type;
    private boolean isPlayed;

    public DevelopmentCard(CardType type) {
        this.type     = type;
        this.isPlayed = false;
    }

    public void play() { this.isPlayed = true; }

    public boolean isVictoryPoint() {
        return type == CardType.TOWN_HALL || type == CardType.MARKET_SQUARE ||
               type == CardType.CATHEDRAL || type == CardType.LIBRARY ||
               type == CardType.UNIVERSITY;
    }

    public boolean isProgress() {
        return type == CardType.MONOPOLY || type == CardType.DISCOVERY ||
               type == CardType.ROAD_BUILDING;
    }

    public int getVictoryPoints() { return isVictoryPoint() ? 1 : 0; }

    /** English display name for the GUI. */
    public String getDisplayName() {
        return switch (type) {
            case KNIGHT        -> "Knight";
            case MONOPOLY      -> "Monopoly";
            case DISCOVERY     -> "Year of Plenty";
            case ROAD_BUILDING -> "Road\nBuilding";
            case TOWN_HALL     -> "Town Hall";
            case MARKET_SQUARE -> "Market\nSquare";
            case CATHEDRAL     -> "Cathedral";
            case LIBRARY       -> "Library";
            case UNIVERSITY    -> "University";
        };
    }

    public CardType getType()    { return type; }
    public boolean  isPlayed()   { return isPlayed; }

    @Override public String toString() { return getDisplayName().replace("\n", " "); }
}
