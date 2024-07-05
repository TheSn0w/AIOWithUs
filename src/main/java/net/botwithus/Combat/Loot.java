package net.botwithus.Combat;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.LootInventory;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.ItemType;
import net.botwithus.rs3.game.js5.types.configs.ConfigManager;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.GroundItemQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.item.GroundItem;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.util.RandomGenerator;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.botwithus.Combat.Combat.useDwarfcannon;
import static net.botwithus.Combat.Notepaper.useItemOnNotepaper;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.SnowsScript.getBotState;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.api.game.hud.prayer.Prayer.isActive;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Loot {
    public static boolean canLoot() {
        return !targetItemNames.isEmpty();
    }

    public static boolean lootNoted = false;

    public static void LootEverything() {
        if (Interfaces.isOpen(1622)) {
            LootAll();
        } else {
            lootInterface();
            Execution.delayUntil(10000, () -> Interfaces.isOpen(1622));
        }
    }

    public static void LootAll() {
        EntityResultSet<GroundItem> groundItems = GroundItemQuery.newQuery().results();
        if (groundItems.isEmpty()) {
            return;
        }

        Execution.delay(RandomGenerator.nextInt(1500, 2000));
        ComponentQuery lootAllQuery = ComponentQuery.newQuery(1622);
        List<Component> components = lootAllQuery.componentIndex(22).results().stream().toList();

        if (!components.isEmpty() && components.get(0).interact(1)) {
            log("[Loot] Successfully interacted with Loot All.");
            Execution.delay(RandomGenerator.nextInt(806, 1259));
        }
    }

    public static void lootInterface() {
        EntityResultSet<GroundItem> groundItems = GroundItemQuery.newQuery().results();
        if (groundItems.isEmpty()) {
            return;
        }

        if (!groundItems.isEmpty() && !Backpack.isFull()) {
            GroundItem groundItem = groundItems.nearest();
            if (groundItem != null) {
                groundItem.interact("Take");
                Execution.delayUntil(RandomGenerator.nextInt(5000, 5500), () -> getLocalPlayer().isMoving());

                if (getLocalPlayer().isMoving() && groundItem.getCoordinate() != null && Distance.between(getLocalPlayer().getCoordinate(), groundItem.getCoordinate()) > 10) {
                    log("[Loot] Used Surge: " + ActionBar.useAbility("Surge"));
                    Execution.delay(RandomGenerator.nextInt(200, 250));
                }

                if (groundItem.getCoordinate() != null) {
                    Execution.delayUntil(RandomGenerator.nextInt(100, 200), () -> Distance.between(getLocalPlayer().getCoordinate(), groundItem.getCoordinate()) <= 10);
                }

                if (groundItem.interact("Take")) {
                    log("[Loot] Taking " + groundItem.getName() + "...");
                    Execution.delay(RandomGenerator.nextInt(600, 700));
                }

                boolean interfaceOpened = Execution.delayUntil(15000, () -> Interfaces.isOpen(1622));
                if (!interfaceOpened) {
                    log("[Error] Interface 1622 did not open. Attempting to interact with ground item again.");
                    if (groundItem.interact("Take")) {
                        log("[Combat] Attempting to take " + groundItem.getName() + " again...");
                        Execution.delay(RandomGenerator.nextInt(600, 650));
                    }
                }
                LootAll();
            }
        }
    }

    public static void processLooting() {
        if (targetItemNames.isEmpty()) {
            return;
        }
        if (LootInventory.isOpen()) {
            lootFromInventory();
        } else {
            lootFromGround();
        }
    }

    public static Pattern generateLootPattern(List<String> names) {
        return Pattern.compile(
                names.stream()
                        .map(Pattern::quote)
                        .reduce((name1, name2) -> name1 + "|" + name2)
                        .orElse(""),
                Pattern.CASE_INSENSITIVE
        );
    }

    public static void lootNotedItems() {
        if (LootInventory.isOpen()) {
            lootNotedItemsFromInventory();
        } else {
            pickUpItems();
        }
    }

    public static void pickUpItems() {
        List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream().toList();

        GroundItem groundItem = groundItems.stream()
                .filter(it -> it.getName() != null && ConfigManager.getItemType(it.getId()).isNote())
                .findFirst()
                .orElse(null);

        if (groundItem != null) {
            groundItem.interact("Take");
            log("[Loot] Interacted with: " + groundItem.getName() + " on the ground.");
            Execution.delayUntil(random.nextLong(10000, 15000), LootInventory::isOpen);
        }
    }


    public static void lootNotedItemsFromInventory() {
    if (LootInventory.isOpen()) {
        boolean itemLooted;
        do {
            itemLooted = false;
            List<Item> inventoryItems = LootInventory.getItems();

            Item item = inventoryItems.stream()
                    .filter(it -> it.getName() != null && ConfigManager.getItemType(it.getId()).isNote())
                    .findFirst()
                    .orElse(null);

            if (item != null) {
                int itemSlot = item.getSlot();
                if (Backpack.isFull() && !Backpack.contains(item.getName())) {
                    return;
                }

                // If useDwarfcannon is active and the backpack has 27 or more slots full, do not loot any new items that the backpack does not already contain
                int totalSlots = 28; // Backpack total capacity
                int usedSlots = totalSlots - Backpack.countFreeSlots();

                if (useDwarfcannon && usedSlots >= 27 && !Backpack.contains(item.getName())) {
                    return;
                }

                LootInventory.take(item.getName());

                itemLooted = Execution.delayUntil(random.nextLong(2000, 3000), () -> {
                    List<Item> updatedInventoryItems = LootInventory.getItems();
                    boolean itemTaken = updatedInventoryItems.stream()
                            .noneMatch(it -> it.getSlot() == itemSlot && it.getName().equals(item.getName()));
                    if (itemTaken) {
                        log("[Loot] Successfully looted noted item: " + item.getName());
                    }
                    return itemTaken;
                });
            }
            Execution.delay(random.nextLong(995, 1096));
        } while (itemLooted);
    }
}

    public static void lootStackableItemsFromInventory() {
        log("[Loot] Checking if LootInventory is open...");
        if (LootInventory.isOpen()) {
            log("[Loot] LootInventory is open. Getting items...");
            List<Item> inventoryItems = LootInventory.getItems();

            for (int i = 0; i < inventoryItems.size(); i++) {
                Item item = inventoryItems.get(i);
                if (item.getName() == null) {
                    log("[Loot] Item name is null. Skipping...");
                    continue;
                }

                log("[Loot] Getting ItemType for item: " + item.getName());
                var itemType = ConfigManager.getItemType(item.getId());
                if (itemType != null && isStackable(itemType)) {
                    log("[Loot] Item is stackable. Attempting to take item...");
                    LootInventory.take(item.getName());
                    log("[Loot] Successfully looted stackable item: " + item.getName());
                    inventoryItems = LootInventory.getItems();
                    Execution.delay(random.nextLong(600, 650));
                } else {
                    log("[Loot] Item is not stackable or ItemType is null. Skipping...");
                }
            }
        } else {
            log("[Loot] LootInventory is not open. Getting ground items...");
            List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream().toList();

            for (GroundItem groundItem : groundItems) {
                if (groundItem.getName() == null) {
                    log("[Loot] Ground item name is null. Skipping...");
                    continue;
                }

                log("[Loot] Getting ItemType for ground item: " + groundItem.getName());
                var itemType = ConfigManager.getItemType(groundItem.getId());
                if (itemType != null && isStackable(itemType)) {
                    log("[Loot] Ground item is stackable. Attempting to take item...");
                    groundItem.interact("Take");
                    log("[Loot] Interacted with: " + groundItem.getName() + " on the ground.");
                    Execution.delayUntil(random.nextLong(10000, 15000), LootInventory::isOpen);
                } else {
                    log("[Loot] Ground item is not stackable or ItemType is null. Skipping...");
                }
            }
        }
    }

    private static boolean isStackable(ItemType itemType) {
        ItemType.Stackability stackability = itemType.getStackability();
        log("[Loot] Stackability of item: " + itemType.getName() + " is " + stackability);
        boolean isStackable = stackability == ItemType.Stackability.ALWAYS;
        log("[Loot] Is item stackable? " + isStackable);
        return isStackable;
    }


    /*public static void lootFromInventory() {
        if (!canLoot()) {
            log("[Error] No target items specified for looting.");
            return;
        }

        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<Item> inventoryItems;
        boolean itemFound;

        long startTime = System.currentTimeMillis();

        do {
            itemFound = false;
            inventoryItems = LootInventory.getItems();

            if (inventoryItems.isEmpty()) {
                break;
            }

            boolean targetItemsFound = inventoryItems.stream()
                    .anyMatch(item -> item.getName() != null && lootPattern.matcher(item.getName()).find());

            if (!targetItemsFound) {
                break;
            }

            for (int i = inventoryItems.size() - 1; i >= 0; i--) {
                Item item = inventoryItems.get(i);
                if (item.getName() == null) {
                    continue;
                }

                Matcher matcher = lootPattern.matcher(item.getName());
                if (matcher.find()) {
                    // Count the number of items with the same name before attempting to take an item
                    long countBefore = inventoryItems.stream()
                            .filter(lootItem -> lootItem.getName().equals(item.getName()))
                            .count();

                    LootInventory.take(item.getName());
                    inventoryItems = LootInventory.getItems();

                    // Count the number of items with the same name after attempting to take an item
                    long countAfter = inventoryItems.stream()
                            .filter(lootItem -> lootItem.getName().equals(item.getName()))
                            .count();

                    // Check if the item has been successfully looted
                    if (countBefore > countAfter) {
                        log("[Loot] Successfully looted item: " + item.getName());
                        itemFound = true;
                    } else {
                        log("[Error] Failed to loot item: " + item.getName());
                    }

                    // Break after each loot attempt
                    break;
                }

                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 15000) {
                    break;
                }
            }
        } while (itemFound && System.currentTimeMillis() - startTime <= 15000);
    }*/

    /*public static long lootFromInventory() {
        if (!canLoot() || Backpack.isFull()) {
            log("[Error] Cant loot or Backpack is full.");
            return random.nextLong(1000, 2000);
        }

        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<Item> inventoryItems = LootInventory.getItems();

        for (Item item : inventoryItems) {
            if (item.getName() != null && lootPattern.matcher(item.getName()).find()) {
                LootInventory.take(item.getName());
                log("[Loot] Successfully looted item: " + item.getName());
                if (useNotepaper) {
                    useItemOnNotepaper();
                }
                break;
            }
        }

        return random.nextLong(550, 650);
    }*/
    public static void lootFromInventory() {
        if (targetItemNames.isEmpty()) {
            log("[Error] No target items specified for looting.");
            return;
        }

        int totalSlots = 28;
        int usedSlots = totalSlots - Backpack.countFreeSlots();

        if (useDwarfcannon && usedSlots >= 27) {
            return;
        }

        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<Item> inventoryItems = LootInventory.getItems();

        Item item = inventoryItems.stream()
                .filter(it -> it.getName() != null && lootPattern.matcher(it.getName()).find())
                .findFirst()
                .orElse(null);

        if (item != null) {
            log("[Loot] Found item to loot: " + item.getName());

            Item currentItem = LootInventory.getItems().stream()
                    .filter(it -> it.getName().equals(item.getName()))
                    .findFirst()
                    .orElse(null);

            if (currentItem != null && currentItem.getSlot() == item.getSlot()) {
                LootInventory.take(item.getName());
                if (useNotepaper) {
                    useItemOnNotepaper();
                }

                Execution.delay(random.nextInt(600, 650));
            } else {
                log("[Loot] Item " + item.getName() + " no longer in the expected slot.");
            }
        } else {
            log("[Loot] Backpack is full. Cannot loot more items.");
        }
    }



    /*public static long lootFromGround() {
        if (targetItemNames.isEmpty()) {
            log("[Error] No target items specified for looting.");
            return random.nextLong(1000, 2000);
        }

        if (LootInventory.isOpen()) {
            log("[Loot] Loot interface is open, skipping ground looting.");
            return 0;
        }

        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream().toList();

        for (GroundItem groundItem : groundItems) {
            if (groundItem.getName() == null) {
                continue;
            }

            Matcher matcher = lootPattern.matcher(groundItem.getName());
            if (matcher.find()) {
                groundItem.interact("Take");
                log("[Loot] Interacted with: " + groundItem.getName() + " on the ground.");
                if (Execution.delayUntil(random.nextLong(10000, 15000), LootInventory::isOpen)) {
                    break;
                }
            }
        }
        return random.nextLong(250, 300);
    }*/
    public static void lootFromGround() {
        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream().toList();

        boolean itemInteracted = groundItems.stream()
                .filter(groundItem -> groundItem.getName() != null && lootPattern.matcher(groundItem.getName()).find())
                .anyMatch(groundItem -> {
                    groundItem.interact("Take");
                    log("[Loot] Interacted with: " + groundItem.getName() + " on the ground.");
                    return Execution.delayUntil(random.nextLong(10000, 15000), LootInventory::isOpen);
                });

        if (!itemInteracted) {
            log("[Loot] No matching items found or LootInventory did not open.");
        }
    }
}
