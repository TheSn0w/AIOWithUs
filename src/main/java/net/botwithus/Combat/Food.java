package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.LootInventory;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.js5.types.configs.ConfigManager;
import net.botwithus.rs3.game.queries.builders.items.GroundItemQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.item.GroundItem;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import static net.botwithus.Combat.Combat.shouldEatFood;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.healthPointsThreshold;

public class Food {
    public static long eatFood(LocalPlayer player) {
        if (shouldEatFood) {

            boolean isPlayerEating = player.getAnimationId() == 18001;
            double healthPercentage = calculateHealthPercentage(player);
            boolean isHealthAboveThreshold = healthPercentage > healthPointsThreshold;


            if (isPlayerEating || isHealthAboveThreshold) {
                return 0;
            }
        }


        return healHealth(player);
    }

    public static double calculateHealthPercentage(LocalPlayer player) {
        double currentHealth = player.getCurrentHealth();
        double maximumHealth = player.getMaximumHealth();

        if (maximumHealth == 0) {
            throw new ArithmeticException("Maximum health cannot be zero.");
        }

        return (currentHealth / maximumHealth) * 100;
    }

    private static long healHealth(LocalPlayer player) {
        ResultSet<Item> foodItems = InventoryItemQuery.newQuery(93).option("Eat").results();
        Item food = foodItems.isEmpty() ? null : foodItems.first();

        if (food == null) {
            // Check if there are any ground items that, when picked up, would have the "Eat" option in the backpack
            ResultSet<GroundItem> groundFoodItems = GroundItemQuery.newQuery().results();
            GroundItem groundFood = groundFoodItems.stream()
                    .filter(item -> {
                        var itemType = ConfigManager.getItemType(item.getId());
                        return itemType != null && itemType.getBackpackOptions().contains("Eat");
                    })
                    .findFirst()
                    .orElse(null);

            if (groundFood != null) {
                // If the backpack is full, return early
                if (backpack.isFull()) {
                    return 0;
                }

                // If LootInventory is open, interact with the item from LootInventory
                if (LootInventory.isOpen()) {
                    LootInventory.take(groundFood.getName());
                } else {
                    // If LootInventory is not open, interact with the ground item until LootInventory is open
                    groundFood.interact("Take");
                    Execution.delayUntil(random.nextLong(15000), LootInventory::isOpen);
                    Execution.delay(random.nextLong(1000, 2000));
                }
                food = InventoryItemQuery.newQuery().ids(groundFood.getId()).results().first();
            } else {
                log("[Caution] No food found");
                return 1L;
            }
        }

        boolean eatSuccess = backpack.interact(food.getName(), "Eat");

        if (eatSuccess) {
            log("[Combat] Successfully ate " + food.getName());
            Execution.delay(RandomGenerator.nextInt(600, 650));
        } else {
            log("[Error] Failed to eat.");
        }
        return 0;
    }

    static boolean isHealthLow(LocalPlayer player) {
        double healthPercentage = calculateHealthPercentage(player);
        return healthPercentage < healthPointsThreshold;
    }
}
