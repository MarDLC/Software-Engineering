package it.univaq.caffeine.catan.model.board;

import it.univaq.caffeine.catan.model.enums.ResourceType;

/**
 * Port — represents one of the 9 Catan ports on the sea perimeter.
 * Stores display coordinates for rendering on the board.
 */
public class Port {
    private final ResourceType resourceType;  // null = generic 3:1
    private final int          exchangeRate;  // 2 or 3
    private final double       displayX;      // screen position
    private final double       displayY;
    private final String       label;

    public Port(ResourceType resourceType, int exchangeRate, double x, double y) {
        this.resourceType = resourceType;
        this.exchangeRate = exchangeRate;
        this.displayX     = x;
        this.displayY     = y;
        this.label        = buildLabel();
    }

    private String buildLabel() {
        if (resourceType == null) return "3:1";
        return "2:1\n" + resourceType.name().substring(0, 2);
    }

    public boolean isGeneric()          { return resourceType == null; }
    public ResourceType getResourceType(){ return resourceType; }
    public int    getExchangeRate()     { return exchangeRate; }
    public double getDisplayX()         { return displayX; }
    public double getDisplayY()         { return displayY; }
    public String getLabel()            { return label; }

    @Override public String toString() {
        return (resourceType == null ? "Generic" : resourceType.name()) + " " + exchangeRate + ":1";
    }
}
