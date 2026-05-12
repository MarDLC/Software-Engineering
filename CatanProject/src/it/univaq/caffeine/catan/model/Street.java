package it.univaq.caffeine.catan.model;

public class Street extends Building {
    private static final int VICTORY_POINTS = 0;
    private Edge edge;

    public Street(String color, Player owner) {
        super(color, owner);
    }

    public void setEdge(Edge e) { this.edge = e; }
    public Edge getEdge() { return edge; }

    @Override
    public int getVictoryPoints() { return VICTORY_POINTS; }
}
