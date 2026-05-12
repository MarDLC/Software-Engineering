package it.univaq.caffeine.catan.view;

import it.univaq.caffeine.catan.controller.GameController;
import it.univaq.caffeine.catan.enums.ResourceType;
import it.univaq.caffeine.catan.model.*;
import it.univaq.caffeine.catan.model.Robber;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CatanBoardPanel extends JPanel implements MouseMotionListener, MouseListener {

    private static final int   INTER_RADIUS    = 10;
    private static final int   EDGE_CLICK_DIST = 14;
    private static final Color OCEAN           = new Color(59, 120, 180);
    private static final Color HEX_BORDER      = new Color(55, 38, 18);
    private static final Color TOKEN_BG        = new Color(255, 248, 220);

    static final java.util.Map<ResourceType, Color> TERRAIN = new EnumMap<>(ResourceType.class);
    static {
        TERRAIN.put(ResourceType.WOOD,   new Color(34,  120,  34));
        TERRAIN.put(ResourceType.SHEEP,  new Color(120, 210,  80));
        TERRAIN.put(ResourceType.WHEAT,  new Color(240, 200,  20));
        TERRAIN.put(ResourceType.BRICK,  new Color(185,  65,  30));
        TERRAIN.put(ResourceType.ORE,    new Color(120, 120, 145));
        TERRAIN.put(ResourceType.DESERT, new Color(210, 190, 140));
    }

    static final String[] PC_STR = {"Red","Blue","Green","Yellow"};
    static final Color[]  PC_COL = {
        new Color(220,50,50), new Color(50,110,220),
        new Color(40,175,40), new Color(220,185,0)
    };

    private final GameController gc;
    private final GameWindow     parent;
    private Intersection         hoveredInter;
    private Edge                 hoveredEdge;

    public enum Phase { PLACE_COLONY, PLACE_ROAD, DONE }
    private Phase phase = Phase.PLACE_COLONY;

    public CatanBoardPanel(GameController gc, GameWindow parent) {
        this.gc = gc; this.parent = parent;
        setPreferredSize(new Dimension(760, 780));
        setBackground(OCEAN);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gc.getGame() == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        GameMap map = gc.getGame().getMap();
        drawHexagons(g2, map);
        drawPorts(g2, map);
        drawEdges(g2, map);
        drawIntersections(g2, map);
        drawBotOverlay(g2);
    }

    // ── Hexagons ──────────────────────────────────────────────
    private void drawHexagons(Graphics2D g2, GameMap map) {
        for (HexagonalTile tile : map.getHexagons()) {
            Polygon hex = hexPolygon(tile.getCenterX(), tile.getCenterY());
            g2.setColor(TERRAIN.get(tile.getTerrainType()));
            g2.fillPolygon(hex);
            g2.setColor(HEX_BORDER);
            g2.setStroke(new BasicStroke(2f));
            g2.drawPolygon(hex);

            if (tile.getToken() != null) {
                int n = tile.getToken().getNumber();
                int cx = (int)tile.getCenterX(), cy = (int)tile.getCenterY();
                g2.setColor(TOKEN_BG);     g2.fillOval(cx-17,cy-17,34,34);
                g2.setColor(Color.GRAY);   g2.setStroke(new BasicStroke(1f));
                g2.drawOval(cx-17,cy-17,34,34);
                g2.setColor((n==6||n==8) ? new Color(200,20,20) : Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                String s = String.valueOf(n);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(s, cx - fm.stringWidth(s)/2, cy + fm.getAscent()/2 - 2);
                // Probability dots under number
                int dots = 6 - Math.abs(7-n);
                for (int d=0;d<dots;d++) {
                    int dx = cx - (dots-1)*3 + d*6;
                    g2.fillOval(dx-2, cy+11, 4, 4);
                }
            } else {
                // Desert label
                g2.setColor(new Color(120,90,50));
                g2.setFont(new Font("SansSerif", Font.BOLD|Font.ITALIC, 11));
                FontMetrics fm = g2.getFontMetrics();
                String s = "Desert";
                g2.drawString(s, (int)tile.getCenterX()-fm.stringWidth(s)/2, (int)tile.getCenterY()-8);
            }
            // Draw Robber if on this tile
            Robber robber = gc.getGame().getRobber();
            if (robber != null && robber.getCurrentTile() == tile) {
                drawRobber(g2, (int)tile.getCenterX(), (int)tile.getCenterY());
            }
        }
    }

    // ── Ports ─────────────────────────────────────────────────
    private void drawPorts(Graphics2D g2, GameMap map) {
        for (Port port : map.getPorts()) {
            int px = (int)port.getDisplayX(), py = (int)port.getDisplayY();
            int w = 46, h = 36;

            // Port background — resource color or sandy for generic
            Color bg = port.isGeneric()
                ? new Color(240, 220, 150)
                : TERRAIN.get(port.getResourceType()).brighter();
            Color border = port.isGeneric() ? new Color(160,130,50) : HEX_BORDER;

            g2.setColor(bg);
            g2.fillRoundRect(px-w/2, py-h/2, w, h, 8, 8);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawRoundRect(px-w/2, py-h/2, w, h, 8, 8);

            // Exchange rate text
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            String rate = port.getExchangeRate() + ":1";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(rate, px - fm.stringWidth(rate)/2, py - 1);

            // Resource abbreviation or "ANY"
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String sub = port.isGeneric()
                ? "any"
                : port.getResourceType().name().substring(0,3);
            fm = g2.getFontMetrics();
            g2.drawString(sub, px - fm.stringWidth(sub)/2, py + 11);
        }
    }

    // ── Edges ─────────────────────────────────────────────────
    private void drawEdges(Graphics2D g2, GameMap map) {
        for (Edge e : map.getEdges()) {
            Intersection a = e.getEndpoints()[0], b = e.getEndpoints()[1];
            if (e.getStreet() != null) {
                g2.setColor(playerColor(e.getStreet().getColor()));
                g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine((int)a.getX(),(int)a.getY(),(int)b.getX(),(int)b.getY());
                g2.setColor(new Color(255,255,255,80));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine((int)a.getX(),(int)a.getY(),(int)b.getX(),(int)b.getY());
            } else if (phase==Phase.PLACE_ROAD && e==hoveredEdge) {
                g2.setColor(new Color(255,255,80,210));
                g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine((int)a.getX(),(int)a.getY(),(int)b.getX(),(int)b.getY());
            } else {
                g2.setColor(new Color(0,0,0,35));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine((int)a.getX(),(int)a.getY(),(int)b.getX(),(int)b.getY());
            }
        }
    }

    // ── Intersections ─────────────────────────────────────────
    private void drawIntersections(Graphics2D g2, GameMap map) {
        for (Intersection inter : map.getIntersections()) {
            int x=(int)inter.getX(), y=(int)inter.getY(), r=INTER_RADIUS;
            if (inter.getBuilding()!=null) {
                g2.setColor(playerColor(inter.getBuilding().getColor()));
                g2.fillOval(x-r,y-r,2*r,2*r);
                g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x-r,y-r,2*r,2*r);
                // Colony icon
                int[] hx = {x-5,x,x+5,x+5,x-5};
                int[] hy = {y+2,y-5,y+2,y+5,y+5};
                g2.setColor(new Color(255,255,255,180));
                g2.fillPolygon(hx,hy,5);
            } else if (phase==Phase.PLACE_COLONY && inter==hoveredInter) {
                g2.setColor(new Color(255,230,0,220));
                g2.fillOval(x-r,y-r,2*r,2*r);
                g2.setColor(Color.ORANGE); g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x-r,y-r,2*r,2*r);
            } else {
                g2.setColor(new Color(200,200,200,80));
                g2.fillOval(x-r+2,y-r+2,2*(r-2),2*(r-2));
            }
        }
    }

    // ── Bot overlay ───────────────────────────────────────────
    private void drawBotOverlay(Graphics2D g2) {
        Player cur = gc.getGame().getCurrentPlayer();
        if (cur!=null && cur.isBot() && phase!=Phase.DONE) {
            String msg = cur.getName() + " is thinking...";
            g2.setFont(new Font("SansSerif", Font.BOLD, 17));
            FontMetrics fm = g2.getFontMetrics();
            int w=fm.stringWidth(msg)+24, h=34, x=(getWidth()-w)/2, y=18;
            g2.setColor(new Color(0,0,0,165));
            g2.fillRoundRect(x,y,w,h,12,12);
            g2.setColor(Color.WHITE);
            g2.drawString(msg, x+12, y+23);
        }
    }

    // ── Bot auto-play ─────────────────────────────────────────
    public void checkAndScheduleBotMove() {
        if (phase==Phase.DONE) return;
        Player cur = gc.getGame().getCurrentPlayer();
        if (cur!=null && cur.isBot()) {
            repaint(); parent.updateStatus();
            javax.swing.Timer t = new javax.swing.Timer(900, e -> executeBotMove());
            t.setRepeats(false); t.start();
        }
    }

    private void executeBotMove() {
        Player cur = gc.getGame().getCurrentPlayer();
        if (cur==null||!cur.isBot()) return;
        BotStrategy bot = cur.getBotStrategy();
        GameMap map = gc.getGame().getMap();
        try {
            if (phase==Phase.PLACE_COLONY) {
                Intersection chosen = bot.chooseColony(map);
                if (chosen!=null) {
                    gc.placeColony(chosen.getId());
                    phase=Phase.PLACE_ROAD;
                    parent.updateStatus(); repaint();
                    javax.swing.Timer t = new javax.swing.Timer(900, ev -> executeBotMove());
                    t.setRepeats(false); t.start();
                }
            } else if (phase==Phase.PLACE_ROAD) {
                Colony last = gc.getGame().getLastPlacedColony();
                Edge chosen = bot.chooseRoad(last);
                if (chosen!=null) {
                    gc.placeRoad(chosen.getId());
                    if ("ActiveGame".equals(gc.getGame().getState())) {
                        phase=Phase.DONE; parent.updateStatus(); repaint();
                        JOptionPane.showMessageDialog(this,
                            "Placement complete!\nThe game begins!",
                            "Placement Complete", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        phase=Phase.PLACE_COLONY; parent.updateStatus(); repaint();
                        checkAndScheduleBotMove();
                    }
                }
            }
        } catch (Exception ex) { System.err.println("Bot error: "+ex.getMessage()); }
    }

    // ── Human mouse ───────────────────────────────────────────
    @Override public void mouseMoved(MouseEvent e) {
        Player cur = gc.getGame()!=null ? gc.getGame().getCurrentPlayer() : null;
        if (cur!=null && cur.isBot()) return;
        GameMap map = gc.getGame().getMap();
        hoveredInter=null; hoveredEdge=null;
        if (phase==Phase.PLACE_COLONY)
            for (Intersection i : map.getIntersections())
                if (dist(e.getX(),e.getY(),i.getX(),i.getY()) < INTER_RADIUS+4) { hoveredInter=i; break; }
        if (phase==Phase.PLACE_ROAD)
            for (Edge ed : map.getEdges())
                if (dist(e.getX(),e.getY(),ed.getCenterX(),ed.getCenterY()) < EDGE_CLICK_DIST) { hoveredEdge=ed; break; }
        repaint();
    }

    @Override public void mouseClicked(MouseEvent e) {
        Player cur = gc.getGame()!=null ? gc.getGame().getCurrentPlayer() : null;
        if (cur==null||cur.isBot()) return;
        if (phase==Phase.PLACE_COLONY && hoveredInter!=null) {
            try {
                gc.placeColony(hoveredInter.getId());
                phase=Phase.PLACE_ROAD; parent.updateStatus(); repaint();
            } catch(Exception ex) {
                showError(ex.getMessage());
            }
        } else if (phase==Phase.PLACE_ROAD && hoveredEdge!=null) {
            try {
                gc.placeRoad(hoveredEdge.getId());
                if ("ActiveGame".equals(gc.getGame().getState())) {
                    phase=Phase.DONE; parent.updateStatus(); repaint();
                    JOptionPane.showMessageDialog(this,"Placement complete!\nThe game begins!",
                        "Placement Complete", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    phase=Phase.PLACE_COLONY; parent.updateStatus(); repaint();
                    checkAndScheduleBotMove();
                }
            } catch(Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    /** Shows an error dialog that renders HTML if the message contains HTML tags. */
    private void showError(String msg) {
        Object display = msg.startsWith("<html>") ? new JLabel(msg) : msg;
        JOptionPane.showMessageDialog(this, display,
            "Invalid Placement", JOptionPane.WARNING_MESSAGE);
    }

    // ── Utils ─────────────────────────────────────────────────
    /** Draws the Robber token centered at (cx, cy). */
    private void drawRobber(Graphics2D g2, int cx, int cy) {
        // Dark cloak shape
        int r = 16;
        g2.setColor(new Color(20, 20, 20, 220));
        g2.fillOval(cx - r, cy + 4, 2*r, r + 6);      // base/body
        g2.fillOval(cx - r/2, cy - r, r, r + 4);      // head
        // Hood highlight
        g2.setColor(new Color(80, 0, 0, 200));
        g2.fillArc(cx - r/2, cy - r, r, r, 0, 180);
        // White eyes
        g2.setColor(new Color(255, 80, 80));
        g2.fillOval(cx - 6, cy - r + 5, 4, 4);
        g2.fillOval(cx + 2, cy - r + 5, 4, 4);
        // Label
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 8));
        g2.drawString("Rob.", cx - 9, cy + 14);
    }

    private Polygon hexPolygon(double cx, double cy) {
        int[] xs=new int[6], ys=new int[6];
        for (int i=0;i<6;i++) {
            double a=Math.toRadians(-90+60*i);
            xs[i]=(int)(cx+GameMap.HEX_SIZE*Math.cos(a));
            ys[i]=(int)(cy+GameMap.HEX_SIZE*Math.sin(a));
        }
        return new Polygon(xs,ys,6);
    }
    private static double dist(double ax,double ay,double bx,double by) { return Math.hypot(ax-bx,ay-by); }
    public static Color playerColor(String n) {
        for (int i=0;i<PC_STR.length;i++) if(PC_STR[i].equalsIgnoreCase(n)) return PC_COL[i];
        return Color.GRAY;
    }

    public void setPhase(Phase p) { phase=p; repaint(); }
    public Phase getPhase()       { return phase; }

    @Override public void mouseDragged(MouseEvent e)  {}
    @Override public void mousePressed(MouseEvent e)  {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}
}
