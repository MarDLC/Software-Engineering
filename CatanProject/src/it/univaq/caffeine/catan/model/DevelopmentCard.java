package it.univaq.caffeine.catan.model;

/**
 * DevelopmentCard — models the 25 development cards of Catan.
 * Types from the actual Italian Catan edition (see card image).
 */
public class DevelopmentCard {

    public enum CardType {
        // Cavaliere (14 cards) — blue header
        CAVALIERE,
        // Progresso (6 cards total) — gray header
        MONOPOLIO, SCOPERTA, COSTRUZIONE_STRADE,
        // Punti Vittoria (5 cards) — orange header
        MUNICIPIO, PIAZZA_DEL_MERCATO, CATTEDRALE, BIBLIOTECA, UNIVERSITA
    }

    private final CardType type;
    private boolean isPlayed;

    public DevelopmentCard(CardType type) {
        this.type     = type;
        this.isPlayed = false;
    }

    public void play() { this.isPlayed = true; }

    public boolean isVictoryPoint() {
        return type == CardType.MUNICIPIO    || type == CardType.PIAZZA_DEL_MERCATO ||
               type == CardType.CATTEDRALE   || type == CardType.BIBLIOTECA ||
               type == CardType.UNIVERSITA;
    }

    public boolean isProgress() {
        return type == CardType.MONOPOLIO || type == CardType.SCOPERTA ||
               type == CardType.COSTRUZIONE_STRADE;
    }

    public int getVictoryPoints() { return isVictoryPoint() ? 1 : 0; }

    /** Italian display name (for GUI). */
    public String getDisplayName() {
        return switch (type) {
            case CAVALIERE           -> "Knight";
            case MONOPOLIO           -> "Monopoly";
            case SCOPERTA            -> "Year of Plenty";
            case COSTRUZIONE_STRADE  -> "Costruzione\nStrade";
            case MUNICIPIO           -> "Town Hall";
            case PIAZZA_DEL_MERCATO  -> "Piazza\ndel Mercato";
            case CATTEDRALE          -> "Cathedral";
            case BIBLIOTECA          -> "Library";
            case UNIVERSITA          -> "University";
        };
    }

    public CardType getType()    { return type; }
    public boolean  isPlayed()   { return isPlayed; }

    @Override public String toString() { return getDisplayName().replace("\n", " "); }
}
