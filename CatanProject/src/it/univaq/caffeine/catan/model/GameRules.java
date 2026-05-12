package it.univaq.caffeine.catan.model;

import it.univaq.caffeine.catan.enums.BuildingType;

public class GameRules {
    private final int victoryPointsGoal   = 10;
    private final int robberHandLimit     = 7;
    private final int maxStreetsForPlayer = 15;
    private final int maxColonyForPlayer  = 5;
    private final int maxCitiesForPlayer  = 4;

    /** CO6 — intersection must be free (distance rule EX.1 simplified). */
    public boolean validateColonyPlacement(Intersection intersection, Player player, GameMap map) {
        if (intersection == null || !intersection.isFree()) return false;
        // Distance rule: no adjacent intersection can have a building
        for (Intersection adj : intersection.getAdjacentIntersections()) {
            if (!adj.isFree()) return false;
        }
        return true;
    }

    /**
     * CO7 — edge must be free AND adjacent to the last placed colony's intersection.
     * Uses the graph adjacency list for correctness.
     */
    public boolean validateRoadPlacement(Edge edge, Colony lastColony, GameMap map) {
        if (edge == null || !edge.isFree()) return false;
        if (lastColony == null) return true;
        Intersection colonyIntersection = lastColony.getIntersection();
        if (colonyIntersection == null) return true;
        // Edge must be connected to the colony's intersection
        return colonyIntersection.getAdjacentEdges().contains(edge);
    }

    public boolean checkResources(Player player, BuildingType type) {
        return true; // TODO: full check via CostRegistry
    }

    public int getVictoryPointsGoal()   { return victoryPointsGoal; }
    public int getRobberHandLimit()     { return robberHandLimit; }
    public int getMaxColonyForPlayer()  { return maxColonyForPlayer; }
    public int getMaxStreetsForPlayer() { return maxStreetsForPlayer; }
    public int getMaxCitiesForPlayer()  { return maxCitiesForPlayer; }
}
