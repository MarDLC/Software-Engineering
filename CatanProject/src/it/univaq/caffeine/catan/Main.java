package it.univaq.caffeine.catan;

import it.univaq.caffeine.catan.controller.GameController;
import it.univaq.caffeine.catan.view.GameWindow;

import javax.swing.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            GameController gc = new GameController();
            if (!runUC1Setup(gc)) return;
            GameWindow window = new GameWindow(gc);
            window.setVisible(true);
            window.getBoardPanel().checkAndScheduleBotMove();
        });
    }

    private static boolean runUC1Setup(GameController gc) {
        // ── SD1: startNewGame ────────────────────────────────
        List<String> modes = gc.startNewGame();
        String[] mArr = modes.toArray(new String[0]);
        int mIdx = JOptionPane.showOptionDialog(null,
            "Select game mode:",
            "New Game",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, mArr, mArr[0]);
        if (mIdx < 0) return false;
        gc.selectGameMode(modes.get(mIdx));
        boolean botMode = (mIdx == 1);   // "Contro i Bot"

        // ── SD3: number of players ─────────────────────────
        String[] numOpts = {"3 players total", "4 players total"};
        int nIdx = JOptionPane.showOptionDialog(null,
            "How many players in total (humans + bots)?",
            "Number of Players",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, numOpts, numOpts[0]);
        if (nIdx < 0) return false;
        int n = nIdx + 3;
        gc.setNumberOfPlayers(n);

        // ── Quanti umani? (sempre, sia Locale sia Bot) ───────
        int maxHumans = n; // all human if "Locale"
        if (botMode) maxHumans = n - 1; // at least 1 human in bot mode

        // Build human count options
        String[] humanOpts = new String[maxHumans];
        for (int i = 0; i < maxHumans; i++)
            humanOpts[i] = (i+1) + " human player" + (i>0 ? "s" : "");
        int hIdx = 0;
        if (n > 1) {
            hIdx = JOptionPane.showOptionDialog(null,
                "How many are human players? (the rest will be bots)",
                "Team Composition",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, humanOpts, humanOpts[0]);
            if (hIdx < 0) return false;
        }
        int numHumans = hIdx + 1;
        int numBots   = n - numHumans;

        // ── Difficoltà bot (se ci sono bot) ─────────────────
        String difficulty = "Easy";
        if (numBots > 0) {
            gc.selectGameMode(numBots > 0 && botMode ? "Against Bot" : modes.get(mIdx));
            String[] diffs = {"Easy", "Strategic"};
            int dIdx = JOptionPane.showOptionDialog(null,
                "Bot difficulty (" + numBots + " bot" + (numBots>1?"s":"") + "):",
                "Bot Difficulty", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, diffs, diffs[0]);
            if (dIdx < 0) return false;
            difficulty = (dIdx == 1) ? "Strategic" : "Easy";
            gc.setBotDifficulty(difficulty);
        }

        // ── SD4: player names (humans) + automatic bot names
        String[] colors = {"Red","Blue","Green","Yellow"};
        for (int i = 0; i < n; i++) {
            if (i < numHumans) {
                String name = JOptionPane.showInputDialog(null,
                    "Player name " + (i+1) + " (" + colors[i] + "):",
                    "Player " + (i+1), JOptionPane.PLAIN_MESSAGE);
                if (name == null || name.isBlank()) name = "Player " + (i+1);
                gc.addPlayerName(name.trim());
            } else {
                gc.addPlayerName("Bot " + (i - numHumans + 1));
            }
        }

        // Mark which players are bots in the controller
        gc.setNumHumans(numHumans);

        // ── SD5: confirmGameSetup ────────────────────────────
        gc.confirmGameSetup();

        // Recap dialog
        StringBuilder sb = new StringBuilder("Game ready!\n\n");
        sb.append("Mode: ").append(gc.getGame().getMode()).append("\n");
        sb.append("Players: ").append(numHumans).append(" human(s), ")
          .append(numBots).append(" bot(s) (").append(difficulty).append(")\n");
        sb.append("Turn order:\n");
        gc.getGame().getPlayers().forEach(p ->
            sb.append("  ").append(p.isBot() ? "🤖" : "👤").append(" ").append(p).append("\n"));
        JOptionPane.showMessageDialog(null, sb.toString(),
            "Setup Complete", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }
}
