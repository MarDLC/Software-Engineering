package it.univaq.caffeine.catan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Intersection — node in the Catan board graph.
 * Stores screen coordinates (x,y) for rendering.
 */
public class Intersection {
    private final int id;
    private final double x, y;          // screen coordinates
    private Building building;
    private final List<Edge> adjacentEdges;
    private final List<Intersection> adjacentIntersections;
    private final List<HexagonalTile> adjacentTiles;

    public Intersection(int id, double x, double y) {
        this.id = id;
        this.x  = x;
        this.y  = y;
        this.adjacentEdges         = new ArrayList<>();
        this.adjacentIntersections = new ArrayList<>();
        this.adjacentTiles         = new ArrayList<>();
    }

    public boolean isFree()                  { return building == null; }
    public void    addBuilding(Building b)   { this.building = b; }
    public void    addEdge(Edge e)           { if (!adjacentEdges.contains(e)) adjacentEdges.add(e); }
    public void    addAdjacentIntersection(Intersection i) {
        if (!adjacentIntersections.contains(i)) adjacentIntersections.add(i);
    }
    public void    addTile(HexagonalTile t)  { if (!adjacentTiles.contains(t)) adjacentTiles.add(t); }

    public int         getId()                        { return id; }
    public double      getX()                         { return x; }
    public double      getY()                         { return y; }
    public Building    getBuilding()                  { return building; }
    public List<Edge>  getAdjacentEdges()             { return adjacentEdges; }
    public List<Intersection> getAdjacentIntersections() { return adjacentIntersections; }
    public List<HexagonalTile> getAdjacentTiles()    { return adjacentTiles; }

    @Override public String toString()               { return "I" + id; }
}
