package it.univaq.caffeine.catan.model;

import it.univaq.caffeine.catan.enums.BuildingType;
import it.univaq.caffeine.catan.enums.ResourceType;
import java.util.HashMap;
import java.util.Map;

public class CostRegistry {
    private Map<BuildingType, Map<ResourceType, Integer>> resourceCost;

    public CostRegistry() {
        resourceCost = new HashMap<>();
        initializeCosts();
    }

    private void initializeCosts() {
        // Colony: 1 Wood, 1 Brick, 1 Wheat, 1 Sheep
        Map<ResourceType, Integer> colony = new HashMap<>();
        colony.put(ResourceType.WOOD,  1); colony.put(ResourceType.BRICK, 1);
        colony.put(ResourceType.WHEAT, 1); colony.put(ResourceType.SHEEP, 1);
        resourceCost.put(BuildingType.COLONY, colony);

        // Street: 1 Wood, 1 Brick
        Map<ResourceType, Integer> street = new HashMap<>();
        street.put(ResourceType.WOOD, 1); street.put(ResourceType.BRICK, 1);
        resourceCost.put(BuildingType.STREET, street);

        // City: 2 Wheat, 3 Ore
        Map<ResourceType, Integer> city = new HashMap<>();
        city.put(ResourceType.WHEAT, 2); city.put(ResourceType.ORE, 3);
        resourceCost.put(BuildingType.CITY, city);

        // Development Card: 1 Wheat, 1 Sheep, 1 Ore
        Map<ResourceType, Integer> devCard = new HashMap<>();
        devCard.put(ResourceType.WHEAT, 1); devCard.put(ResourceType.SHEEP, 1);
        devCard.put(ResourceType.ORE, 1);
        resourceCost.put(BuildingType.DEVELOPMENT_CARD, devCard);
    }

    public Map<ResourceType, Integer> getCost(BuildingType type) {
        return resourceCost.getOrDefault(type, new HashMap<>());
    }
}
