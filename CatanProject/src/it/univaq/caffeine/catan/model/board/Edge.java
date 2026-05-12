package it.univaq.caffeine.catan.model.board;

import it.univaq.caffeine.catan.model.player.Street;
import java.util.ArrayList;
import java.util.List;

/**
 * Edge — arc in the Catan board graph, connecting two Intersection nodes.
 */
public class Edge {
    private final int id;
    private final Intersection[] endpoints;   // exactly 2
    private Street street;
    private final List<HexagonalTile> adjacentTiles;

    public Edge(int id, Intersection a, Intersection b) {
        this.id        = id;
        this.endpoints = new Intersection[]{a, b};
        this.adjacentTiles = new ArrayList<>();
    }

    public boolean isFree()              { return street == null; }
    public void    setStreet(Street s)   { this.street = s; }
    public void    addTile(HexagonalTile t) { if (!adjacentTiles.contains(t)) adjacentTiles.add(t); }

    /** Screen center of this edge (midpoint of its two endpoints). */
    public double getCenterX() { return (endpoints[0].getX() + endpoints[1].getX()) / 2.0; }
    public double getCenterY() { return (endpoints[0].getY() + endpoints[1].getY()) / 2.0; }

    public int              getId()           { return id; }
    public Intersection[]   getEndpoints()    { return endpoints; }
    public Street           getStreet()       { return street; }
    public List<HexagonalTile> getAdjacentTiles() { return adjacentTiles; }

    @Override public String toString()        { return "E" + id + "(" + endpoints[0].getId() + "-" + endpoints[1].getId() + ")"; }
}
