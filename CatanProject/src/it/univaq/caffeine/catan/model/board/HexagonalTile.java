package it.univaq.caffeine.catan.model.board;

import it.univaq.caffeine.catan.model.enums.ResourceType;
import java.util.ArrayList;
import java.util.List;

public class HexagonalTile {
    private final int          id;
    private final ResourceType terrainType;
    private final double       cx, cy;         // screen center
    private final List<Intersection> intersections;
    private final List<Edge>         edges;
    private Token token;

    public HexagonalTile(int id, ResourceType terrainType, double cx, double cy) {
        this.id          = id;
        this.terrainType = terrainType;
        this.cx          = cx;
        this.cy          = cy;
        this.intersections = new ArrayList<>();
        this.edges         = new ArrayList<>();
    }

    public ResourceType produceResource() {
        return (terrainType == ResourceType.DESERT) ? null : terrainType;
    }

    public void addIntersection(Intersection i) { intersections.add(i); }
    public void addEdge(Edge e)                 { edges.add(e); }
    public void setToken(Token t)               { this.token = t; }

    public int           getId()           { return id; }
    public ResourceType  getTerrainType()  { return terrainType; }
    public double        getCenterX()      { return cx; }
    public double        getCenterY()      { return cy; }
    public List<Intersection> getIntersections() { return intersections; }
    public List<Edge>    getEdges()        { return edges; }
    public Token         getToken()        { return token; }

    @Override public String toString() {
        String tok = token != null ? "[" + token.getNumber() + "]" : "[D]";
        return terrainType.name().substring(0,2) + tok;
    }
}
