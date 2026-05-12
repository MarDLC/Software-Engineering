package it.univaq.caffeine.catan.model;

public class Colony extends Building {
    private static final int VICTORY_POINTS = 1;
    private Intersection intersection;

    public Colony(String color, Player owner) {
        super(color, owner);
    }

    public void setIntersection(Intersection i) { this.intersection = i; }
    public Intersection getIntersection() { return intersection; }

    @Override
    public int getVictoryPoints() { return VICTORY_POINTS; }
}
