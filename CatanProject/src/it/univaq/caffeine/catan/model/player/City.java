package it.univaq.caffeine.catan.model.player;

import it.univaq.caffeine.catan.model.board.Intersection;

public class City extends Building {
    private static final int VICTORY_POINTS = 2;
    private Intersection intersection;

    public City(String color, Player owner) {
        super(color, owner);
    }

    public void setIntersection(Intersection i) { this.intersection = i; }
    public Intersection getIntersection() { return intersection; }

    @Override
    public int getVictoryPoints() { return VICTORY_POINTS; }
}
