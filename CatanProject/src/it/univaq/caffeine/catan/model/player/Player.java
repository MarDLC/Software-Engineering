package it.univaq.caffeine.catan.model.player;

import it.univaq.caffeine.catan.model.BotStrategy;
import it.univaq.caffeine.catan.model.cards.DevelopmentCard;
import it.univaq.caffeine.catan.model.cards.ResourceCard;
import it.univaq.caffeine.catan.model.enums.ResourceType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {
    private String name;
    private String color;
    private boolean isBot;
    private BotStrategy botStrategy;         // null if human
    private List<DevelopmentCard> cards;
    private List<Building> buildings;
    private List<ResourceCard> resources;
    private int victoryPoints;

    /** Human player constructor */
    public Player(String name, String color) {
        this(name, color, false, null);
    }

    /** Bot player constructor */
    public Player(String name, String color, String difficulty) {
        this(name, color, true, new BotStrategy(difficulty));
    }

    private Player(String name, String color, boolean isBot, BotStrategy strategy) {
        this.name        = name;
        this.color       = color;
        this.isBot       = isBot;
        this.botStrategy = strategy;
        this.cards       = new ArrayList<>();
        this.buildings   = new ArrayList<>();
        this.resources   = new ArrayList<>();
        this.victoryPoints = 0;
    }

    public void addBuilding(Building b)  { buildings.add(b); }
    public void addResource(ResourceCard c) { resources.add(c); }

    public void removeResource(ResourceType type, int qty) {
        int removed = 0;
        Iterator<ResourceCard> it = resources.iterator();
        while (it.hasNext() && removed < qty) {
            if (it.next().getType() == type) { it.remove(); removed++; }
        }
    }

    public boolean hasResource(ResourceType type, int qty) {
        return resources.stream().filter(c -> c.getType()==type).count() >= qty;
    }

    public void addVictoryPoints(int pts) { victoryPoints += pts; }

    // Bot helpers
    public boolean     isBot()           { return isBot; }
    public BotStrategy getBotStrategy()  { return botStrategy; }

    public String getName()              { return name; }
    public String getColor()             { return color; }
    public List<Building> getBuildings() { return buildings; }
    public List<ResourceCard> getResources() { return resources; }
    public List<DevelopmentCard> getCards() { return cards; }
    public int getVictoryPoints()        { return victoryPoints; }

    @Override public String toString() {
        return name + " (" + color + (isBot ? "/Bot" : "") + ") VP:" + victoryPoints;
    }
}
