package it.univaq.caffeine.catan.view;

import it.univaq.caffeine.catan.controller.GameController;
import it.univaq.caffeine.catan.model.Bank;
import it.univaq.caffeine.catan.model.cards.Deck;
import it.univaq.caffeine.catan.model.cards.DevelopmentCard;
import it.univaq.caffeine.catan.model.enums.ResourceType;
import it.univaq.caffeine.catan.model.player.Player;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GameWindow extends JFrame {

    private static final Color BG        = new Color(245, 235, 210);
    private static final Color SECTION   = new Color(100, 60, 20);
    private static final Color CARD_BLUE = new Color(60, 90, 200);
    private static final Color CARD_GREY = new Color(100, 100, 110);
    private static final Color CARD_ORG  = new Color(210, 120, 20);

    private final GameController  gc;
    private final CatanBoardPanel boardPanel;

    // Dynamic labels
    private JLabel     lblPlayer;
    private JLabel     lblTurn;
    private JTextArea  txtInstruction;
    private JPanel     resPanel;
    private JPanel     devCardPanel;
    private JLabel     lblDeck;
    private JTextArea  txtPlayerList;

    public GameWindow(GameController gc) {
        this.gc = gc;
        boardPanel = new CatanBoardPanel(gc, this);

        setTitle("The Settlers of Catan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
        add(buildRightPanel(), BorderLayout.EAST);

        updateStatus();
        pack();
        setLocationRelativeTo(null);
    }

    // ══════════════════════════════════════════════════════════
    //  Right panel construction
    // ══════════════════════════════════════════════════════════
    private JPanel buildRightPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.setPreferredSize(new Dimension(295, 780));

        // ── Current player ────────────────────────────────
        p.add(sectionLabel("▶ Current Turn"));
        lblPlayer = boldLabel("—", 15);
        lblTurn   = plainLabel("—", 11);
        p.add(Box.createVerticalStrut(3));
        p.add(lblPlayer);
        p.add(lblTurn);

        p.add(Box.createVerticalStrut(8));
        p.add(hLine());

        // ── Phase instruction (fixed-height, no overflow) ──
        p.add(Box.createVerticalStrut(6));
        p.add(sectionLabel("📋 Phase"));
        txtInstruction = new JTextArea(4, 26);
        txtInstruction.setEditable(false);
        txtInstruction.setLineWrap(true);
        txtInstruction.setWrapStyleWord(true);
        txtInstruction.setFont(new Font("SansSerif", Font.PLAIN, 11));
        txtInstruction.setBackground(new Color(255, 252, 240));
        txtInstruction.setBorder(new CompoundBorder(
            new LineBorder(new Color(180,140,80), 1, true),
            new EmptyBorder(5,6,5,6)));
        p.add(Box.createVerticalStrut(3));
        p.add(txtInstruction);

        p.add(Box.createVerticalStrut(8));
        p.add(hLine());

        // ── Player resources ──────────────────────────────
        p.add(Box.createVerticalStrut(6));
        p.add(sectionLabel("💰 Resources (current player)"));
        resPanel = new JPanel(new GridLayout(1, 5, 4, 0));
        resPanel.setBackground(BG);
        resPanel.setMaximumSize(new Dimension(275, 54));
        p.add(Box.createVerticalStrut(3));
        p.add(resPanel);

        p.add(Box.createVerticalStrut(8));
        p.add(hLine());

        // ── Dev cards in hand ─────────────────────────────
        p.add(Box.createVerticalStrut(6));
        p.add(sectionLabel("📜 Development Cards in Hand"));
        devCardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
        devCardPanel.setBackground(BG);
        devCardPanel.setPreferredSize(new Dimension(275, 60));
        devCardPanel.setMaximumSize(new Dimension(275, 60));
        p.add(Box.createVerticalStrut(3));
        p.add(devCardPanel);

        p.add(Box.createVerticalStrut(8));
        p.add(hLine());

        // ── Deck status ───────────────────────────────────
        p.add(Box.createVerticalStrut(6));
        p.add(sectionLabel("🃏 Development Deck"));
        lblDeck = plainLabel("—", 11);
        p.add(Box.createVerticalStrut(2));
        p.add(lblDeck);
        p.add(Box.createVerticalStrut(4));
        p.add(buildDeckLegend());

        p.add(Box.createVerticalStrut(8));
        p.add(hLine());

        // ── Bank button ───────────────────────────────────
        p.add(Box.createVerticalStrut(6));
        JButton bankBtn = new JButton("🏦  Trade with Bank");
        bankBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        bankBtn.setBackground(new Color(200, 230, 200));
        bankBtn.setFocusPainted(false);
        bankBtn.setMaximumSize(new Dimension(275, 34));
        bankBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bankBtn.addActionListener(e -> openBankDialog());
        p.add(bankBtn);

        p.add(Box.createVerticalStrut(8));
        p.add(hLine());

        // ── Player list ───────────────────────────────────
        p.add(Box.createVerticalStrut(6));
        p.add(sectionLabel("👥 Players"));
        txtPlayerList = new JTextArea(6, 26);
        txtPlayerList.setEditable(false);
        txtPlayerList.setFont(new Font("Monospaced", Font.PLAIN, 10));
        txtPlayerList.setBackground(new Color(250,248,240));
        txtPlayerList.setLineWrap(true);
        txtPlayerList.setWrapStyleWord(true);
        txtPlayerList.setBorder(new EmptyBorder(4,4,4,4));
        JScrollPane sp = new JScrollPane(txtPlayerList);
        sp.setBorder(new LineBorder(new Color(180,140,80)));
        sp.setMaximumSize(new Dimension(275, 120));
        p.add(Box.createVerticalStrut(3));
        p.add(sp);

        p.add(Box.createVerticalGlue());
        JLabel ver = new JLabel("Caffeine Coders");
        ver.setFont(new Font("SansSerif", Font.ITALIC, 9));
        ver.setForeground(Color.GRAY);
        p.add(ver);

        return p;
    }

    // ══════════════════════════════════════════════════════════
    //  updateStatus — called after every placement
    // ══════════════════════════════════════════════════════════
    public void updateStatus() {
        if (gc.getGame()==null) return;
        Player cur  = gc.getGame().getCurrentPlayer();
        CatanBoardPanel.Phase phase = boardPanel.getPhase();

        // Current player
        if (cur!=null && phase!=CatanBoardPanel.Phase.DONE) {
            lblPlayer.setForeground(CatanBoardPanel.playerColor(cur.getColor()));
            lblPlayer.setText("  " + cur.getName() + (cur.isBot() ? " 🤖" : ""));
            lblTurn.setText("  Color: " + cur.getColor() +
                " · Turn " + (gc.getGame().getCurrentShift()+1) +
                "/" + (gc.getGame().getMaxPlayers()*2));
        } else if (phase==CatanBoardPanel.Phase.DONE) {
            lblPlayer.setForeground(new Color(30,140,30));
            lblPlayer.setText("  ✔ Game started!");
            lblTurn.setText("  State: " + gc.getGame().getState());
        }

        // Instruction (word-wrapped, no overflow)
        switch (phase) {
            case PLACE_COLONY -> txtInstruction.setText(
                "Initial Placement — COLONY\n\n" +
                "Click on an intersection (yellow circle) to place your settlement.\n" +
                "Rule: no adjacent settlements.");
            case PLACE_ROAD -> txtInstruction.setText(
                "Initial Placement — ROAD\n\n" +
                "Click on an edge (yellow line) adjacent to the settlement you just placed.");
            case DONE -> txtInstruction.setText(
                "✔ Placement complete!\n\nState: " +
                gc.getGame().getState());
        }

        // Resources of current player
        updateResourcePanel(cur);

        // Dev cards in hand
        updateDevCardPanel(cur);

        // Deck status
        Deck deck = gc.getGame().getDeck();
        if (deck!=null) {
            lblDeck.setText("  Cards remaining: " + deck.size() + " / 25");
        }

        // Player list
        StringBuilder sb = new StringBuilder();
        for (Player pl : gc.getGame().getPlayers()) {
            sb.append(pl.isBot() ? "🤖 " : "👤 ")
              .append(pl.getName())
              .append("\n   VP:").append(pl.getVictoryPoints())
              .append("  Resources:").append(pl.getResources().size())
              .append("  Buildings:").append(pl.getBuildings().size())
              .append("\n");
        }
        txtPlayerList.setText(sb.toString());
    }

    private void updateResourcePanel(Player cur) {
        resPanel.removeAll();
        ResourceType[] types = {ResourceType.BRICK, ResourceType.WOOD,
                                ResourceType.WHEAT, ResourceType.SHEEP, ResourceType.ORE};
        String[] icons = {"🧱","🌲","🌾","🐑","⛏"};
        String[] names = {"BRI","WOO","WHE","SHE","ORE"};
        for (int i=0;i<5;i++) {
            final ResourceType rt = types[i];
            int count = cur==null ? 0 :
                (int)cur.getResources().stream().filter(rc->rc.getType()==rt).count();
            JPanel cell = new JPanel();
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
            cell.setBackground(CatanBoardPanel.TERRAIN.get(types[i]).darker());
            cell.setBorder(new LineBorder(Color.BLACK, 1, true));

            JLabel ico = new JLabel(icons[i], SwingConstants.CENTER);
            ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            ico.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lbl = new JLabel(names[i], SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 8));
            lbl.setForeground(Color.WHITE);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel cnt = new JLabel(String.valueOf(count), SwingConstants.CENTER);
            cnt.setFont(new Font("SansSerif", Font.BOLD, 16));
            cnt.setForeground(Color.WHITE);
            cnt.setAlignmentX(Component.CENTER_ALIGNMENT);

            cell.add(ico); cell.add(lbl); cell.add(cnt);
            resPanel.add(cell);
        }
        resPanel.revalidate(); resPanel.repaint();
    }

    private void updateDevCardPanel(Player cur) {
        devCardPanel.removeAll();
        if (cur==null || cur.getCards().isEmpty()) {
            JLabel none = new JLabel("No cards");
            none.setFont(new Font("SansSerif", Font.ITALIC, 10));
            none.setForeground(Color.GRAY);
            devCardPanel.add(none);
        } else {
            // Count by type
            java.util.Map<String,Long> counts = new java.util.LinkedHashMap<>();
            for (DevelopmentCard c : cur.getCards()) {
                counts.merge(c.getDisplayName().replace("\n"," "), 1L, Long::sum);
            }
            for (var entry : counts.entrySet()) {
                JLabel tag = new JLabel(entry.getKey()+" ×"+entry.getValue());
                tag.setFont(new Font("SansSerif", Font.BOLD, 9));
                tag.setForeground(Color.WHITE);
                tag.setBackground(CARD_BLUE);
                tag.setOpaque(true);
                tag.setBorder(new EmptyBorder(2,4,2,4));
                devCardPanel.add(tag);
            }
        }
        devCardPanel.revalidate(); devCardPanel.repaint();
    }

    // ── Deck legend ───────────────────────────────────────────
    private JPanel buildDeckLegend() {
        JPanel p = new JPanel(new GridLayout(3,1,0,2));
        p.setBackground(BG);
        p.setMaximumSize(new Dimension(275, 54));
        p.add(deckTag("Knight ×14", CARD_BLUE));
        p.add(deckTag("Progress ×6 (Monopoly·YoP·Road Building)", CARD_GREY));
        p.add(deckTag("Victory Points ×5 (Town Hall·Market·…)", CARD_ORG));
        return p;
    }

    private JLabel deckTag(String text, Color bg) {
        JLabel l = new JLabel("  " + text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 9));
        l.setForeground(Color.WHITE);
        l.setBackground(bg);
        l.setOpaque(true);
        l.setBorder(new EmptyBorder(2,2,2,2));
        return l;
    }

    // ── Bank dialog ───────────────────────────────────────────
    private void openBankDialog() {
        JDialog dlg = new JDialog(this, "🏦 Bank — Resources", true);
        dlg.setLayout(new BorderLayout(10,10));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(12,16,12,16));
        content.setBackground(new Color(245,255,245));

        // Note
        JLabel note = new JLabel("<html><body style='width:280px'>" +
            "<b>ℹ️ Available in the game phase</b><br>" +
            "Bank trading will be active during normal gameplay.<br>" +
            "Default rate: <b>4:1</b> for any resource (or <b>3:1/2:1</b> with a port)." +
            "</body></html>");
        note.setFont(new Font("SansSerif", Font.PLAIN, 11));
        content.add(note);
        content.add(Box.createVerticalStrut(10));

        // Bank stock
        Bank bank = gc.getGame().getBank();
        if (bank!=null) {
            content.add(boldLabel("Current stock:", 12));
            content.add(Box.createVerticalStrut(4));
            JPanel grid = new JPanel(new GridLayout(1,5,6,0));
            grid.setBackground(new Color(245,255,245));
            ResourceType[] types = {ResourceType.BRICK,ResourceType.WOOD,
                                    ResourceType.WHEAT,ResourceType.SHEEP,ResourceType.ORE};
            String[] names = {"Brick","Wood","Wheat","Sheep","Ore"};
            for (int i=0;i<5;i++) {
                int qty = bank.getResourceStock().getOrDefault(types[i],0);
                JPanel cell = new JPanel();
                cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
                cell.setBackground(CatanBoardPanel.TERRAIN.get(types[i]));
                cell.setBorder(new LineBorder(Color.BLACK,1,true));
                JLabel n = new JLabel(names[i], SwingConstants.CENTER);
                n.setFont(new Font("SansSerif",Font.PLAIN,9));
                n.setForeground(Color.WHITE);
                n.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel q = new JLabel(String.valueOf(qty), SwingConstants.CENTER);
                q.setFont(new Font("SansSerif",Font.BOLD,18));
                q.setForeground(Color.WHITE);
                q.setAlignmentX(Component.CENTER_ALIGNMENT);
                cell.add(n); cell.add(q);
                grid.add(cell);
            }
            content.add(grid);
        }

        content.add(Box.createVerticalStrut(10));

        // Ports info
        content.add(boldLabel("Ports on the board (9 total):", 12));
        content.add(Box.createVerticalStrut(4));
        JTextArea portInfo = new JTextArea(
            "4 × Generic port    → 3:1 (any resource)\n" +
            "1 × Ore port        → 2:1 Ore\n" +
            "1 × Wheat port      → 2:1 Wheat\n" +
            "1 × Wood port       → 2:1 Wood\n" +
            "1 × Brick port      → 2:1 Brick\n" +
            "1 × Sheep port      → 2:1 Sheep"
        );
        portInfo.setEditable(false);
        portInfo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        portInfo.setBackground(new Color(245,255,245));
        portInfo.setBorder(new EmptyBorder(4,4,4,4));
        content.add(portInfo);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dlg.dispose());

        dlg.add(content, BorderLayout.CENTER);
        dlg.add(close, BorderLayout.SOUTH);
        dlg.setSize(340, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────
    private static JLabel sectionLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(SECTION);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    private static JLabel boldLabel(String t, int sz) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", Font.BOLD, sz));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    private static JLabel plainLabel(String t, int sz) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", Font.PLAIN, sz));
        l.setForeground(Color.DARK_GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    private static JSeparator hLine() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(275, 2));
        return sep;
    }

    public CatanBoardPanel getBoardPanel() { return boardPanel; }
}
