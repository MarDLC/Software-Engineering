package it.univaq.caffeine.catan.model;

import it.univaq.caffeine.catan.model.board.*;
import it.univaq.caffeine.catan.model.player.Colony;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BotStrategy — simple bot logic for the initial placement phase (UC 2.0).
 *
 *  Easy   → random valid intersection / edge
 *  Medium → prefer intersections adjacent to high-probability tiles (5,6,8,9)
 */
public class BotStrategy {

    private final String difficulty;
    private final Random rnd = new Random();

    public BotStrategy(String difficulty) {
        this.difficulty = difficulty;
    }

    // ── Colony placement ──────────────────────────────────────

    /**
     * Choose a valid intersection for colony placement.
     * Validity = intersection free AND no adjacent intersection occupied.
     */
    public Intersection chooseColony(GameMap map) {
        List<Intersection> candidates = map.getIntersections().stream()
            .filter(Intersection::isFree)
            .filter(i -> i.getAdjacentIntersections().stream().allMatch(Intersection::isFree))
            .collect(Collectors.toList());

        if (candidates.isEmpty()) return null;

        if ("Medium".equalsIgnoreCase(difficulty)) {
            // Pick the intersection with the highest total token-probability score
            return candidates.stream()
                .max(Comparator.comparingInt(this::intersectionScore))
                .orElse(candidates.get(0));
        }

        // Easy: random
        Collections.shuffle(candidates, rnd);
        return candidates.get(0);
    }

    // ── Road placement ────────────────────────────────────────

    /**
     * Choose a valid edge adjacent to the colony just placed (CO7 constraint).
     */
    public Edge chooseRoad(Colony lastColony) {
        if (lastColony == null || lastColony.getIntersection() == null) return null;
        List<Edge> candidates = lastColony.getIntersection().getAdjacentEdges().stream()
            .filter(Edge::isFree)
            .collect(Collectors.toList());

        if (candidates.isEmpty()) return null;
        Collections.shuffle(candidates, rnd);
        return candidates.get(0);
    }

    // ── Score helpers ─────────────────────────────────────────

    /**
     * Sum of token-probability weights for all tiles adjacent to this intersection.
     * Weight = 6 - |7 - number|   (max=5 for 6 and 8, min=1 for 2 and 12)
     */
    private int intersectionScore(Intersection inter) {
        int score = 0;
        for (HexagonalTile tile : inter.getAdjacentTiles()) {
            if (tile.getToken() != null) {
                int n = tile.getToken().getNumber();
                score += (6 - Math.abs(7 - n));
            }
        }
        return score;
    }
}
