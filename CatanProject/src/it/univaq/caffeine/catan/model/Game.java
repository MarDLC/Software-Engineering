package it.univaq.caffeine.catan.model;

import it.univaq.caffeine.catan.model.board.*;
import it.univaq.caffeine.catan.model.cards.Deck;
import it.univaq.caffeine.catan.model.enums.ResourceType;
import it.univaq.caffeine.catan.model.player.*;
import it.univaq.caffeine.catan.model.rules.GameRules;
import java.util.*;

public class Game {
    private String  mode;
    private int     maxPlayers;
    private String  state;
    private int     currentShift;
    private List<Player> players;
    private GameMap map;
    private Deck    deck;
    private Bank    bank;
    private GameRules rules;
    private Robber  robber;
    private Colony  lastPlacedColony;

    public Game() {
        players      = new ArrayList<>();
        rules        = new GameRules();
        state        = "Setup";
        currentShift = 0;
    }

    // ── SD5 ─────────────────────────────────────────────────
    public Player createPlayer(String name, String color) {
        Player p = new Player(name, color);
        addPlayer(p);
        return p;
    }

    public Player createBotPlayer(String name, String color, String difficulty) {
        Player p = new Player(name, color, difficulty);
        addPlayer(p);
        return p;
    }

    public void addPlayer(Player p)   { players.add(p); }
    public void createMap()           { map = new GameMap(); map.generateMap(); }
    public Deck createDeck()          { deck = new Deck(); deck.shuffle(); return deck; }
    public Bank createBank()          { bank = new Bank(); return bank; }
    public void generateTurnOrder()   { Collections.shuffle(players); currentShift = 0; }
    public void setGameState(String s){ this.state = s; }

    // ── SD6 — placeColony ────────────────────────────────────
    public void placeColony(int intersectionId) {
        Intersection i = map.getIntersection(intersectionId);
        if (i == null)
            throw new IllegalArgumentException("Intersection not found: " + intersectionId);
        // Specific error messages for better UX
        if (!i.isFree())
            throw new IllegalStateException(
                "This intersection is already occupied by a colony.");
        for (Intersection adj : i.getAdjacentIntersections()) {
            if (!adj.isFree())
                throw new IllegalStateException(
                    "<html>Too close to another settlement!<br><br>" +
                    "<b>Catan distance rule:</b> every settlement must be at<br>" +
                    "least <b>2 intersections away</b> from any other settlement,<br>" +
                    "including your own. Please choose another spot.</html>");
        }
        if (!rules.validateColonyPlacement(i, getCurrentPlayer(), map))
            throw new IllegalStateException(
                "Invalid placement at intersection " + intersectionId);
        Player p = getCurrentPlayer();
        Colony c = new Colony(p.getColor(), p);
        c.setIntersection(i);
        i.addBuilding(c);
        p.addBuilding(c);
        p.addVictoryPoints(c.getVictoryPoints());
        lastPlacedColony = c;
    }

    // ── SD7 — placeRoad ──────────────────────────────────────
    public void placeRoad(int edgeId) {
        Edge e = map.getEdge(edgeId);
        if (e == null)
            throw new IllegalArgumentException("Edge not found: " + edgeId);
        if (!e.isFree())
            throw new IllegalStateException("This edge is already occupied by a road.");
        if (lastPlacedColony != null) {
            Intersection ci = lastPlacedColony.getIntersection();
            if (ci != null && !ci.getAdjacentEdges().contains(e))
                throw new IllegalStateException(
                    "<html><b>Road not adjacent to this turn's settlement!</b><br><br>" +
                    "Initial placement rule:<br>" +
                    "each road must connect to the settlement<br>" +
                    "placed <b>in the same turn</b>.<br><br>" +
                    "During normal play you will be able to build<br>" +
                    "roads connected to any of your existing<br>" +
                    "settlements or roads.</html>");
        }
        if (!rules.validateRoadPlacement(e, lastPlacedColony, map))
            throw new IllegalStateException("Invalid road placement at edge " + edgeId);
        Player p = getCurrentPlayer();
        Street s = new Street(p.getColor(), p);
        s.setEdge(e);
        e.setStreet(s);
        p.addBuilding(s);
        currentShift++;
        if (isSecondRoundComplete()) {
            distributeInitialResources();
            setGameState("ActiveGame");
        }
    }

    // ── Snake-order helpers ──────────────────────────────────
    public Player getCurrentPlayer() {
        if (players.isEmpty()) return null;
        int n = players.size();
        if (currentShift < n) return players.get(currentShift);
        if (currentShift < 2 * n) return players.get(2 * n - 1 - currentShift);
        return null;
    }

    private boolean isSecondRoundComplete() { return currentShift >= maxPlayers * 2; }

    public void distributeInitialResources() {
        for (Player p : players) {
            List<Building> cols = p.getBuildings().stream()
                .filter(b -> b instanceof Colony).toList();
            if (cols.size() >= 2) {
                Colony second = (Colony) cols.get(cols.size() - 1);
                Intersection inter = second.getIntersection();
                if (inter != null) {
                    for (HexagonalTile tile : inter.getAdjacentTiles()) {
                        if (tile.getTerrainType() != ResourceType.DESERT)
                            bank.transferResourcesTo(p, tile.getTerrainType(), 1);
                    }
                }
            }
        }
    }

    // ── Bot convenience ──────────────────────────────────────
    /** Returns the last colony placed (needed by BotStrategy for road placement). */
    public Colony getLastPlacedColony() { return lastPlacedColony; }

    // ── Getters / Setters ────────────────────────────────────
    public void setMode(String m)    { this.mode = m; }
    public void setMaxPlayers(int n) { this.maxPlayers = n; }
    public String       getMode()    { return mode; }
    public int          getMaxPlayers(){ return maxPlayers; }
    public String       getState()   { return state; }
    public int          getCurrentShift(){ return currentShift; }
    public List<Player> getPlayers() { return players; }
    public GameMap      getMap()     { return map; }
    public Deck         getDeck()    { return deck; }
    public Bank         getBank()    { return bank; }
    public GameRules    getRules()   { return rules; }

    /** SD5 1.12: creates Robber on Desert tile (CO5 postcondition). */
    public Robber createRobber() {
        HexagonalTile desert = map.getHexagons().stream()
            .filter(t -> t.getTerrainType() == ResourceType.DESERT)
            .findFirst().orElse(map.getHexagons().get(0));
        robber = new Robber(desert);
        return robber;
    }

    public Robber getRobber() { return robber; }
}
