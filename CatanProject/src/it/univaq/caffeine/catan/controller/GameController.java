package it.univaq.caffeine.catan.controller;

import it.univaq.caffeine.catan.model.Game;
import it.univaq.caffeine.catan.model.GameSetup;
import java.util.List;

public class GameController {
    private GameSetup setup;
    private Game      game;

    // ── UC 1.0 ──────────────────────────────────────────────
    public List<String> startNewGame() {
        setup = new GameSetup();
        return setup.getAvailableGameModes();
    }

    public void selectGameMode(String type) { setup.setGameMode(type); }

    public void setNumberOfPlayers(int n) {
        if (n < 3 || n > 4) throw new IllegalArgumentException("Players must be 3 or 4.");
        setup.setNumberOfPlayers(n);
    }

    public void addPlayerName(String name) { setup.addPlayerName(name); }

    /** Difficulty used for bots: "Easy" or "Medium". Null = all human. */
    public void setBotDifficulty(String difficulty) { setup.setBotDifficulty(difficulty); }

    private int numHumans = 0;
    public void setNumHumans(int n) { this.numHumans = n; }

    public void confirmGameSetup() {
        int          n    = setup.getNumberOfPlayers();
        List<String> names= setup.getPlayerNames();
        String       diff = setup.getBotDifficulty();

        game = new Game();
        game.setMode(setup.getGameMode());
        game.setMaxPlayers(n);

        String[] colors = {"Red","Blue","Green","Brown"};

        for (int i = 0; i < n; i++) {
            boolean isBot = (numHumans > 0) ? (i >= numHumans) : ("Against Bots".equals(setup.getGameMode()) && i > 0);
            if (isBot) {
                game.createBotPlayer(names.get(i), colors[i], diff != null ? diff : "Easy");
            } else {
                game.createPlayer(names.get(i), colors[i]);
            }
        }

        game.createMap();
        game.createDeck();
        game.createBank();
        game.generateTurnOrder();
        game.setGameState("InitialPlacement");

        // SD5 1.12: createRobber — CO5 postcondition
        game.createRobber();
    }

    // ── UC 2.0 ──────────────────────────────────────────────
    public void placeColony(int intersectionId) { game.placeColony(intersectionId); }
    public void placeRoad(int edgeId)           { game.placeRoad(edgeId); }

    public Game      getGame()  { return game; }
    public GameSetup getSetup() { return setup; }
}
