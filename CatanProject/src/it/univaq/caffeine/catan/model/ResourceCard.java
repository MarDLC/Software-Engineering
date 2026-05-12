package it.univaq.caffeine.catan.model;

import it.univaq.caffeine.catan.enums.ResourceType;

public class ResourceCard {
    private ResourceType type;

    public ResourceCard(ResourceType type) { this.type = type; }
    public ResourceType getType() { return type; }

    @Override
    public String toString() { return type.name(); }
}
