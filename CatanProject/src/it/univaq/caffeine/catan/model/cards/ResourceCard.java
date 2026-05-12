package it.univaq.caffeine.catan.model.cards;

import it.univaq.caffeine.catan.model.enums.ResourceType;

public class ResourceCard {
    private ResourceType type;

    public ResourceCard(ResourceType type) { this.type = type; }
    public ResourceType getType() { return type; }

    @Override
    public String toString() { return type.name(); }
}
