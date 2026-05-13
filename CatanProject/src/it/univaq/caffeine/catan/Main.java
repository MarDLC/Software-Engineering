package it.univaq.caffeine.catan;

import it.univaq.caffeine.catan.controller.GameController;
import it.univaq.caffeine.catan.view.GameWindow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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

    // ══════════════════════════════════════════════════════════════
    //  UC 1.0 — Setup wizard con navigazione avanti/indietro
    // ══════════════════════════════════════════════════════════════
    private static boolean runUC1Setup(GameController gc) {

        int          modeIdx = 0;
        int          nIdx    = 0;
        int          hIdx    = 0;
        int          dIdx    = 0;
        List<String> names   = new ArrayList<>();

        int     step = 0;
        boolean done = false;

        while (!done) {
            switch (step) {

                // ── Step 0: Modalità (nessun Back — primo step) ──────────
                case 0: {
                    List<String> modes = gc.startNewGame();
                    String[] opts = modes.toArray(new String[0]);
                    int r = JOptionPane.showOptionDialog(null,
                            "Select game mode:",
                            "New Game — Step 1 of 4",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                            null, opts, opts[modeIdx]);
                    if (r < 0) return false;
                    modeIdx = r;
                    step = 1;
                    break;
                }

                // ── Step 1: Numero di giocatori ───────────────────────────
                case 1: {
                    String[] opts = {"3 players total", "4 players total"};
                    int r = showWithBack(
                            "How many players in total (humans + bots)?",
                            "New Game — Step 2 of 4", opts, nIdx);
                    if (r == -2) return false;
                    if (r == -1) { step = 0; break; }
                    nIdx = r;
                    step = 2;
                    break;
                }

                // ── Step 2: Quanti umani ──────────────────────────────────
                case 2: {
                    boolean botMode = (modeIdx == 1);
                    int n    = nIdx + 3;
                    int maxH = botMode ? n - 1 : n;

                    String[] opts = new String[maxH];
                    for (int i = 0; i < maxH; i++)
                        opts[i] = (i + 1) + " human player" + (i > 0 ? "s" : "");

                    int r = showWithBack(
                            "How many are human players? (the rest will be bots)",
                            "New Game — Step 3 of 4", opts, Math.min(hIdx, maxH - 1));
                    if (r == -2) return false;
                    if (r == -1) { step = 1; break; }
                    hIdx = r;
                    step = 3;
                    break;
                }

                // ── Step 3: Difficoltà bot (saltato se no bot) ────────────
                case 3: {
                    int numBots = (nIdx + 3) - (hIdx + 1);
                    if (numBots == 0) { step = 4; break; }

                    String[] diffs = {"Easy", "Strategic"};
                    int r = showWithBack(
                            "Bot difficulty (" + numBots + " bot" + (numBots > 1 ? "s" : "") + "):",
                            "New Game — Step 4 of 4", diffs, dIdx);
                    if (r == -2) return false;
                    if (r == -1) { step = 2; break; }
                    dIdx = r;
                    step = 4;
                    break;
                }

                // ── Step 4: Nomi giocatori umani ──────────────────────────
                case 4: {
                    names.clear();
                    int      numHumans = hIdx + 1;
                    String[] colors    = {"Red", "Blue", "Green", "Brown"};
                    boolean  back      = false;
                    boolean  cancelled = false;

                    for (int i = 0; i < numHumans; i++) {
                        String[] nameOut = {""};
                        int r = showNameInput(
                                "Player " + (i + 1) + " of " + numHumans,
                                "Player name " + (i + 1) + " (" + colors[i] + "):",
                                nameOut);

                        if (r == -2) { cancelled = true; break; }
                        if (r == -1) { back      = true; break; }

                        String name = nameOut[0].trim();
                        if (name.isBlank()) name = "Player " + (i + 1);
                        names.add(name);
                    }

                    if (cancelled) return false;
                    if (back) {
                        int numBots = (nIdx + 3) - (hIdx + 1);
                        step = (numBots > 0) ? 3 : 2;
                        break;
                    }

                    int n = nIdx + 3;
                    for (int i = numHumans; i < n; i++)
                        names.add("Bot " + (i - numHumans + 1));

                    done = true;
                    break;
                }
            }
        }

        // ── Applica la configurazione al controller ────────────────────────
        List<String> modes = gc.startNewGame();
        gc.selectGameMode(modes.get(modeIdx));

        int n         = nIdx + 3;
        int numHumans = hIdx + 1;
        int numBots   = n - numHumans;
        gc.setNumberOfPlayers(n);
        gc.setNumHumans(numHumans);

        String difficulty = "Easy";
        if (numBots > 0) {
            boolean botMode = (modeIdx == 1);
            gc.selectGameMode(botMode ? "Against Bot" : modes.get(modeIdx));
            difficulty = (dIdx == 1) ? "Strategic" : "Easy";
            gc.setBotDifficulty(difficulty);
        }

        for (String name : names) gc.addPlayerName(name);

        gc.confirmGameSetup();

        StringBuilder sb = new StringBuilder("Game ready!\n\n");
        sb.append("Mode: ").append(gc.getGame().getMode()).append("\n");
        sb.append("Players: ").append(numHumans).append(" human(s), ")
                .append(numBots).append(" bot(s) (").append(difficulty).append(")\n");
        sb.append("Turn order:\n");
        gc.getGame().getPlayers().forEach(p ->
                sb.append("  ").append(p.isBot() ? "🤖" : "👤")
                        .append(" ").append(p).append("\n"));
        JOptionPane.showMessageDialog(null, sb.toString(),
                "Setup Complete", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    // ══════════════════════════════════════════════════════════════
    //  Helper 1 — dialogo con opzioni + "← Back" + "Cancel"
    //  Ritorna: 0+ = opzione scelta, -1 = Back, -2 = Cancel/Chiuso
    // ══════════════════════════════════════════════════════════════
    private static int showWithBack(String message, String title,
                                    String[] options, int defaultIdx) {
        final int[] result = {-2};

        Font paneFont = UIManager.getFont("OptionPane.messageFont");
        Font btnFont  = UIManager.getFont("OptionPane.buttonFont");

        JDialog dlg = new JDialog((Frame) null, title, true);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setLayout(new BorderLayout(10, 8));

        // Riga 1: icona + messaggio
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setBorder(BorderFactory.createEmptyBorder(15, 15, 8, 15));
        JLabel icon = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));
        JLabel msg  = new JLabel("<html><body style='width:260px'>"
                + message + "</body></html>");
        if (paneFont != null) msg.setFont(paneFont);
        top.add(icon, BorderLayout.WEST);
        top.add(msg,  BorderLayout.CENTER);
        dlg.add(top, BorderLayout.NORTH);

        // Riga 2: bottoni opzioni
        JPanel middle = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        middle.setBorder(BorderFactory.createEmptyBorder(0, 15, 4, 15));
        for (int i = 0; i < options.length; i++) {
            final int idx = i;
            JButton btn = new JButton(options[i]);
            if (btnFont != null) btn.setFont(btnFont);
            btn.addActionListener(e -> { result[0] = idx; dlg.dispose(); });
            if (i == defaultIdx) dlg.getRootPane().setDefaultButton(btn);
            middle.add(btn);
        }
        dlg.add(middle, BorderLayout.CENTER);

        // Riga 3: ← Back + Cancel
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        JButton backBtn   = new JButton("← Back");
        JButton cancelBtn = new JButton("Cancel");
        if (btnFont != null) backBtn.setFont(btnFont);
        if (btnFont != null) cancelBtn.setFont(btnFont);
        backBtn.addActionListener(e   -> { result[0] = -1; dlg.dispose(); });
        cancelBtn.addActionListener(e -> { result[0] = -2; dlg.dispose(); });
        bottom.add(backBtn);
        bottom.add(cancelBtn);
        dlg.add(bottom, BorderLayout.SOUTH);

        dlg.pack();
        dlg.setMinimumSize(new Dimension(380, 0));
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);

        return result[0];
    }

    // ══════════════════════════════════════════════════════════════
    //  Helper 2 — dialogo inserimento nome con campo testo
    //  Ritorna: 0 = OK, -1 = Back, -2 = Cancel/Chiuso
    //  Il nome inserito viene scritto in nameOut[0]
    // ══════════════════════════════════════════════════════════════
    private static int showNameInput(String title, String message, String[] nameOut) {
        final int[] result = {-2};

        Font paneFont = UIManager.getFont("OptionPane.messageFont");
        Font btnFont  = UIManager.getFont("OptionPane.buttonFont");

        JDialog dlg = new JDialog((Frame) null, title, true);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setLayout(new BorderLayout(10, 8));

        // Riga 1: icona + label
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setBorder(BorderFactory.createEmptyBorder(15, 15, 8, 15));
        JLabel icon = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));
        JLabel lbl  = new JLabel(message);
        if (paneFont != null) lbl.setFont(paneFont);
        top.add(icon, BorderLayout.WEST);
        top.add(lbl,  BorderLayout.CENTER);
        dlg.add(top, BorderLayout.NORTH);

        // Riga 2: campo testo
        JTextField field = new JTextField(16);
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        center.add(field);
        dlg.add(center, BorderLayout.CENTER);

        // Sezione inferiore: OK sopra, Back+Cancel sotto separati
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));

        // OK
        JPanel okRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        JButton okBtn = new JButton("OK");
        if (btnFont != null) okBtn.setFont(btnFont);
        okBtn.addActionListener(e -> {
            nameOut[0] = field.getText();
            result[0]  = 0;
            dlg.dispose();
        });
        dlg.getRootPane().setDefaultButton(okBtn);
        okRow.add(okBtn);
        south.add(okRow);

        // ← Back + Cancel (riga separata)
        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        navRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        JButton backBtn   = new JButton("← Back");
        JButton cancelBtn = new JButton("Cancel");
        if (btnFont != null) backBtn.setFont(btnFont);
        if (btnFont != null) cancelBtn.setFont(btnFont);
        backBtn.addActionListener(e   -> { result[0] = -1; dlg.dispose(); });
        cancelBtn.addActionListener(e -> { result[0] = -2; dlg.dispose(); });
        navRow.add(backBtn);
        navRow.add(cancelBtn);
        south.add(navRow);

        dlg.add(south, BorderLayout.SOUTH);

        dlg.pack();
        dlg.setMinimumSize(new Dimension(320, 0));
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);

        return result[0];
    }
}
