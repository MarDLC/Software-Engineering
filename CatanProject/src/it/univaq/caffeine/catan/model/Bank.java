package it.univaq.caffeine.catan.model;

import it.univaq.caffeine.catan.enums.ResourceType;
import java.util.HashMap;
import java.util.Map;

public class Bank {
    private Map<ResourceType, Integer> resourceStock;

    public Bank() {
        resourceStock = new HashMap<>();
        initializeResources();
    }

    private void initializeResources() {
        // Standard Catan: 19 of each resource type
        resourceStock.put(ResourceType.BRICK, 19);
        resourceStock.put(ResourceType.WOOD,  19);
        resourceStock.put(ResourceType.WHEAT, 19);
        resourceStock.put(ResourceType.SHEEP, 19);
        resourceStock.put(ResourceType.ORE,   19);
    }

    public boolean hasResources(ResourceType type, int quantity) {
        return resourceStock.getOrDefault(type, 0) >= quantity;
    }

    public void transferResourcesTo(Player player, ResourceType type, int quantity) {
        if (!hasResources(type, quantity)) return;
        resourceStock.put(type, resourceStock.get(type) - quantity);
        for (int i = 0; i < quantity; i++) player.addResource(new ResourceCard(type));
    }

    public void receiveResourcesFrom(Player player, ResourceType type, int quantity) {
        resourceStock.merge(type, quantity, Integer::sum);
        player.removeResource(type, quantity);
    }

    public Map<ResourceType, Integer> getResourceStock() { return resourceStock; }
}
