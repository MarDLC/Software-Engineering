package it.univaq.caffeine.catan.model;

public abstract class Building {
    protected String color;
    protected Player owner;

    public Building(String color, Player owner) {
        this.color = color;
        this.owner = owner;
    }

    public abstract int getVictoryPoints();

    public String getColor() { return color; }
    public Player getOwner() { return owner; }
}
