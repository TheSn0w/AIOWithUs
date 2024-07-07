package net.botwithus.Combat;

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
import net.botwithus.rs3.util.RandomGenerator;

import java.util.List;
import java.util.regex.Pattern;

import static net.botwithus.Combat.Notepaper.useItemOnNotepaper;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class LootManager {
    private static final BlockingQueue<Runnable> lootQueue = new LinkedBlockingQueue<>();
    private static final AtomicBoolean isProcessingLoot = new AtomicBoolean(false);

    public static void startLootManager() {
        if (!isProcessingLoot.get()) {
            isProcessingLoot.set(true);
            Thread.ofVirtual().name("LootManagerThread").start(() -> {
                while (true) {
                    try {
                        Runnable lootTask = lootQueue.take();
                        lootTask.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } finally {
                        isProcessingLoot.set(false);
                    }
                }
            });
        }
    }

    public static void queueLoot(Runnable lootTask) {
        lootQueue.add(lootTask);
    }

    public static void manageLoot() {
        startLootManager();

        if (useCustomLoot) {
            queueLoot(LootManager::processLooting);
            Execution.delay(random.nextLong(800, 1000));
        }
        if (useLootAllNotedItems) {
            queueLoot(LootManager::lootNotedItems);
            Execution.delay(random.nextLong(800, 1000));
        }
        if (useLootAllStackableItems) {
            queueLoot(LootManager::lootStackableItemsFromInventory);
            Execution.delay(random.nextLong(800, 1000));
        }
        if (useNotepaper) {
            queueLoot(Notepaper::useItemOnNotepaper);
            Execution.delay(random.nextLong(800, 1000));
        }
    }


    public static boolean useLootAllNotedItems = false;

    public static void LootEverything() {
        if (Interfaces.isOpen(1622)) {
            LootAll();
        } else {
            lootInterface();
            Execution.delayUntil(10000, () -> Interfaces.isOpen(1622));
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
    public static void lootNotedItems() {
        if (LootInventory.isOpen()) {
            lootNotedItemsFromInventory();
        } else {
            pickUpItems();
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
    

    public static Pattern generateLootPattern(List<String> names) {
        return Pattern.compile(
                names.stream()
                        .map(Pattern::quote)
                        .reduce((name1, name2) -> name1 + "|" + name2)
                        .orElse(""),
                Pattern.CASE_INSENSITIVE
        );
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


    private static volatile boolean isLootingNoted = false;

    public static void lootNotedItemsFromInventory() {
        if (!isLootingNoted) {
            queueLoot(() -> {
                isLootingNoted = true;
                try {
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
                } finally {
                    isLootingNoted = false;
                }
            });
        }
    }

    private static volatile boolean isLootingStackable = false;

    public static void lootStackableItemsFromInventory() {
        if (!isLootingStackable) {
            queueLoot(() -> {
                isLootingStackable = true;
                try {
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
                } finally {
                    isLootingStackable = false;
                }
            });
        }
    }

    private static boolean isStackable(ItemType itemType) {
        ItemType.Stackability stackability = itemType.getStackability();
        log("[Loot] Stackability of item: " + itemType.getName() + " is " + stackability);
        boolean isStackable = stackability == ItemType.Stackability.ALWAYS;
        log("[Loot] Is item stackable? " + isStackable);
        return isStackable;
    }

    private static volatile boolean isLooting = false;

    public static void lootFromInventory() {
        if (!isLooting) {
            queueLoot(() -> {
                isLooting = true;
                try {
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
                    }
                } finally {
                    isLooting = false;
                }
            });
        }
    }

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
