package it.univaq.caffeine.catan.model.board;

/**
 * Robber — domain class representing the robber token.
 *
 * CO5 postcondition: one Robber instance is created and placed on the
 * Desert tile at game start. It blocks resource production from its tile.
 *
 * SD5 msg 1.12: createRobber() — Game creates Robber on the Desert tile.
 */
public class Robber {
    private HexagonalTile currentTile;

    /** Placed on the Desert tile at game start (CO5). */
    public Robber(HexagonalTile desertTile) {
        this.currentTile = desertTile;
    }

    /** Moves the Robber to a new tile (triggered by rolling a 7 — UC 3.x). */
    public void moveTo(HexagonalTile tile) {
        this.currentTile = tile;
    }

    public HexagonalTile getCurrentTile() { return currentTile; }

    @Override public String toString() {
        return "Robber on: " + (currentTile != null ? currentTile.getTerrainType() : "?");
    }
}
