package it.univaq.caffeine.catan.model;

import it.univaq.caffeine.catan.enums.ResourceType;
import java.util.*;

public class GameMap {

    public static final double HEX_SIZE = 60.0;
    public static final double CENTER_X = 380.0;
    public static final double CENTER_Y = 415.0;

    private static final int[][] TILE_AXIAL = {
        {0,-2},{1,-2},{2,-2},
        {-1,-1},{0,-1},{1,-1},{2,-1},
        {-2,0},{-1,0},{0,0},{1,0},{2,0},
        {-2,1},{-1,1},{0,1},{1,1},
        {-2,2},{-1,2},{0,2}
    };

    private static final ResourceType[] TERRAIN_DIST = {
        ResourceType.WOOD,  ResourceType.WOOD,  ResourceType.WOOD,  ResourceType.WOOD,
        ResourceType.SHEEP, ResourceType.SHEEP, ResourceType.SHEEP, ResourceType.SHEEP,
        ResourceType.WHEAT, ResourceType.WHEAT, ResourceType.WHEAT, ResourceType.WHEAT,
        ResourceType.BRICK, ResourceType.BRICK, ResourceType.BRICK,
        ResourceType.ORE,   ResourceType.ORE,   ResourceType.ORE,
        ResourceType.DESERT
    };

    private static final int[] TOKEN_NUMBERS = {
        2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12
    };

    /**
     * 9 ports in clockwise order from top-left.
     * {ResourceType or null, exchangeRate, displayX, displayY}
     * Null resourceType = generic 3:1.
     */
    private static final Object[][] PORT_DEFS = {
        {null,              3, 285.0, 150.0},   // 1. Generic   — top-left
        {ResourceType.ORE,  2, 483.0, 148.0},   // 2. Ore 2:1   — top-right
        {null,              3, 631.0, 278.0},   // 3. Generic   — right-top
        {ResourceType.WHEAT,2, 667.0, 415.0},   // 4. Grano 2:1 — right
        {ResourceType.WOOD, 2, 583.0, 555.0},   // 5. Legno 2:1 — bottom-right
        {null,              3, 380.0, 633.0},   // 6. Generic   — bottom
        {ResourceType.BRICK,2, 175.0, 555.0},   // 7. Argilla 2:1 — bottom-left
        {ResourceType.SHEEP,2,  90.0, 415.0},   // 8. Pecora 2:1  — left
        {null,              3, 135.0, 278.0},   // 9. Generic   — top-left
    };

    private final List<HexagonalTile> hexagons      = new ArrayList<>();
    private final List<Intersection>  intersections = new ArrayList<>();
    private final List<Edge>          edges         = new ArrayList<>();
    private final List<Token>         tokens        = new ArrayList<>();
    private final List<Port>          ports         = new ArrayList<>();

    private final HashMap<String, Intersection> intersectionIndex = new HashMap<>();
    private final HashMap<String, Edge>         edgeIndex         = new HashMap<>();
    private int nextIntersectionId = 0;
    private int nextEdgeId         = 0;

    public void generateMap() {
        List<ResourceType> terrains = new ArrayList<>(Arrays.asList(TERRAIN_DIST));
        Collections.shuffle(terrains);

        for (int t = 0; t < 19; t++) {
            int q = TILE_AXIAL[t][0], r = TILE_AXIAL[t][1];
            double cx = axialToPixelX(q, r), cy = axialToPixelY(q, r);
            HexagonalTile tile = new HexagonalTile(t, terrains.get(t), cx, cy);
            hexagons.add(tile);

            Intersection[] verts = new Intersection[6];
            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(-90.0 + 60.0 * i);
                verts[i] = findOrCreateIntersection(
                    cx + HEX_SIZE * Math.cos(angle),
                    cy + HEX_SIZE * Math.sin(angle));
                tile.addIntersection(verts[i]);
                verts[i].addTile(tile);
            }
            for (int i = 0; i < 6; i++) {
                Edge e = findOrCreateEdge(verts[i], verts[(i+1)%6]);
                tile.addEdge(e);
                e.addTile(tile);
            }
        }

        for (Edge e : edges) {
            Intersection a = e.getEndpoints()[0], b = e.getEndpoints()[1];
            a.addAdjacentIntersection(b);
            b.addAdjacentIntersection(a);
        }

        List<Integer> tokenNums = new ArrayList<>();
        for (int n : TOKEN_NUMBERS) tokenNums.add(n);
        Collections.shuffle(tokenNums);
        int ti = 0;
        for (HexagonalTile tile : hexagons) {
            if (tile.getTerrainType() != ResourceType.DESERT) {
                Token tok = new Token(tokenNums.get(ti++));
                tokens.add(tok); tile.setToken(tok);
            }
        }

        generatePorts();
    }

    /** SD5 1.7.2.5: creates 9 Port instances (CO5 postcondition). */
    private void generatePorts() {
        for (Object[] def : PORT_DEFS) {
            ResourceType rt   = (ResourceType) def[0];
            int           rate = (int)           def[1];
            double        px   = (double)         def[2];
            double        py   = (double)         def[3];
            ports.add(new Port(rt, rate, px, py));
        }
    }

    private Intersection findOrCreateIntersection(double x, double y) {
        String key = Math.round(x) + "_" + Math.round(y);
        if (intersectionIndex.containsKey(key)) return intersectionIndex.get(key);
        Intersection i = new Intersection(nextIntersectionId++, x, y);
        intersectionIndex.put(key, i); intersections.add(i);
        return i;
    }

    private Edge findOrCreateEdge(Intersection a, Intersection b) {
        int lo = Math.min(a.getId(), b.getId()), hi = Math.max(a.getId(), b.getId());
        String key = lo + "_" + hi;
        if (edgeIndex.containsKey(key)) return edgeIndex.get(key);
        Edge e = new Edge(nextEdgeId++, a, b);
        edgeIndex.put(key, e); edges.add(e);
        a.addEdge(e); b.addEdge(e);
        return e;
    }

    public static double axialToPixelX(int q, int r) { return CENTER_X + HEX_SIZE * Math.sqrt(3) * (q + r/2.0); }
    public static double axialToPixelY(int q, int r) { return CENTER_Y + HEX_SIZE * 1.5 * r; }

    public Intersection getIntersection(int id) {
        return intersections.stream().filter(i->i.getId()==id).findFirst().orElse(null);
    }
    public Edge getEdge(int id) {
        return edges.stream().filter(e->e.getId()==id).findFirst().orElse(null);
    }

    public List<HexagonalTile> getHexagons()      { return hexagons; }
    public List<Intersection>  getIntersections() { return intersections; }
    public List<Edge>          getEdges()          { return edges; }
    public List<Token>         getTokens()         { return tokens; }
    public List<Port>          getPorts()          { return ports; }
}
