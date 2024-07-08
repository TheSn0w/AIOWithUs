package net.botwithus.Combat;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.LootInventory;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.ItemType;
import net.botwithus.rs3.game.js5.types.configs.ConfigManager;
import net.botwithus.rs3.game.queries.builders.items.GroundItemQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.item.GroundItem;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.List;
import java.util.regex.Pattern;

import static net.botwithus.Combat.Notepaper.useItemOnNotepaper;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.getBotState;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class LootManager {

    private SnowsScript snowsScript;

    public LootManager(SnowsScript snowsScript) {
        this.snowsScript = snowsScript;
    }

    public static boolean useLootAllNotedItems = false;


    public void manageLoot() {
        while (snowsScript.isActive()) {

            if (useCustomLoot) {
                useCustomLoot();
            }
            if (useLootAllNotedItems) {
                lootNotedItemsFromInventory();
            }
            if (useNotepaper) {
                useItemOnNotepaper();
            }
            if (useLootEverything) {
                lootAllButton();
            }

            Execution.delay(random.nextLong(600, 700));
        }
    }

    // =====================
// SECTION 1: Loot Everything
// =====================
    public static void useLootInventoryPickup() {
        if (LootInventory.isOpen()) {
            return;
        }
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
                    log("[LootEverything] Used Surge: " + ActionBar.useAbility("Surge"));
                    Execution.delay(RandomGenerator.nextInt(200, 250));
                }

                if (groundItem.getCoordinate() != null) {
                    Execution.delayUntil(RandomGenerator.nextInt(100, 200), () -> Distance.between(getLocalPlayer().getCoordinate(), groundItem.getCoordinate()) <= 10);
                }

                if (groundItem.interact("Take")) {
                    log("[LootEverything] Taking " + groundItem.getName() + "...");
                    Execution.delay(RandomGenerator.nextInt(600, 700));
                }

                boolean interfaceOpened = Execution.delayUntil(15000, () -> Interfaces.isOpen(1622));
                if (!interfaceOpened) {
                    log("[Error] Interface 1622 did not open. Attempting to interact with ground item again.");
                    if (groundItem.interact("Take")) {
                        log("[LootEverything] Attempting to take " + groundItem.getName() + " again...");
                        Execution.delay(RandomGenerator.nextInt(600, 650));
                    }
                }
            }
        }
    }

    public static void lootAllButton() {
        if (LootInventory.isOpen() && !LootInventory.getItems().isEmpty()) {
            if (!Backpack.isFull()) {
                LootInventory.lootAll();
                Execution.delay(random.nextLong(800, 1000));
                log("[LootAll] Looted all items from the inventory.");
            } else {
                log("[LootAll] Backpack is full, Cannot loot all items.");
            }
        }
    }

    // =====================
// SECTION 2: Loot Specific Items
// =====================
    public static void useCustomLootFromGround() {
        int totalSlots = 28;
        int usedSlots = totalSlots - Backpack.countFreeSlots();

        if (useDwarfcannon && usedSlots >= 27) {
            return;
        }
        LocalPlayer player = getLocalPlayer();
        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream()
                .filter(it -> it.getCoordinate().distanceTo(player.getCoordinate()) <= 25.0D)
                .toList();

        groundItems.stream()
                .filter(groundItem -> groundItem.getName() != null && lootPattern.matcher(groundItem.getName()).find())
                .anyMatch(groundItem -> {
                    if (!LootInventory.contains(groundItem.getName()) || !LootInventory.isOpen()) {
                        if (!player.isMoving() && !LootInventory.contains(groundItem.getName())) {
                            groundItem = GroundItemQuery.newQuery().itemId(groundItem.getId()).results().nearest();
                            if (groundItem == null) {
                                log("[CustomLootingFromGround] Ground item no longer exists.");
                            } else {
                                groundItem.interact("Take");
                                log("[CustomLootingFromGround] Interacted with: " + groundItem.getName() + " on the ground.");
                                Execution.delay(random.nextLong(600, 750));
                            }
                        }

                        if (player.isMoving() && groundItem != null && groundItem.getCoordinate() != null &&
                                Distance.between(player.getCoordinate(), groundItem.getCoordinate()) > 10 &&
                                ActionBar.containsAbility("Surge") && ActionBar.getCooldown("Surge") == 0) {

                            Execution.delay(random.nextLong(750, 1000));

                            log("[CustomLootingFromGround] Used Surge: " + ActionBar.useAbility("Surge"));
                            Execution.delay(RandomGenerator.nextInt(200, 250));
                            groundItem.interact("Take");
                        }

                        GroundItem finalGroundItem = groundItem;
                        Execution.delayUntil(random.nextLong(10000, 15000), () ->
                                LootInventory.contains(finalGroundItem.getName()) || !finalGroundItem.validate());
                    }
                    return false;
                });
    }

    public static void useCustomLoot() {
        if (targetItemNames.isEmpty()) {
            log("[Error] No items specified for looting.");
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

            Item currentItem = LootInventory.getItems().stream()
                    .filter(it -> it.getName().equals(item.getName()))
                    .findFirst()
                    .orElse(null);

            if (currentItem != null && currentItem.getSlot() == item.getSlot()) {
                LootInventory.take(item.getName());
                log("[CustomLooting] Found item to loot: " + item.getName());
                Execution.delay(random.nextLong(600, 700));
            } else {
                log("[CustomLooting] Item " + item.getName() + " no longer in the expected slot.");
            }
        }
    }

// =====================
// SECTION 3: Loot Noted Items
// =====================

    public static void useNotedLootFromGround() {
        int totalSlots = 28;
        int usedSlots = totalSlots - Backpack.countFreeSlots();

        if (useDwarfcannon && usedSlots >= 27) {
            return;
        }
        LocalPlayer player = Client.getLocalPlayer();
        List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream()
                .filter(it -> it.getCoordinate().distanceTo(player.getCoordinate()) <= 25.0D)
                .toList();

        GroundItem groundItem = groundItems.stream()
                .filter(it -> it.getName() != null && ConfigManager.getItemType(it.getId()).isNote())
                .findAny()
                .orElse(null);

        if (groundItem != null) {
            if (!LootInventory.contains(groundItem.getName()) || !LootInventory.isOpen()) {
                if (!player.isMoving() && !LootInventory.contains(groundItem.getName())) {
                    groundItem = GroundItemQuery.newQuery().itemId(groundItem.getId()).results().nearest();
                    if (groundItem == null) {
                        log("[NotedItemsFromGround] Ground item no longer exists.");
                    } else {
                        groundItem.interact("Take");
                        log("[NotedItemsFromGround] Interacted with: " + groundItem.getName() + " on the ground.");
                        Execution.delay(random.nextLong(600, 750));
                    }
                }

                if (player.isMoving() && groundItem != null && groundItem.getCoordinate() != null &&
                        Distance.between(player.getCoordinate(), groundItem.getCoordinate()) > 15 &&
                        ActionBar.containsAbility("Surge") && ActionBar.getCooldown("Surge") == 0) {

                    Execution.delay(random.nextLong(750, 1000));

                    log("[NotedItemsFromGround] Used Surge: " + ActionBar.useAbility("Surge"));
                    Execution.delay(random.nextInt(200, 250));
                    groundItem.interact("Take");
                }

                GroundItem finalGroundItem = groundItem;
                Execution.delayUntil(random.nextLong(10000, 15000), () ->
                        LootInventory.contains(finalGroundItem.getName()) || !finalGroundItem.validate()
                );
            }
        }
    }


    public static void lootNotedItemsFromInventory() {
        if (LootInventory.isOpen()) {
            List<Item> inventoryItems = LootInventory.getItems();

            Item item = inventoryItems.stream()
                    .filter(it -> it.getName() != null && ConfigManager.getItemType(it.getId()).isNote())
                    .findFirst()
                    .orElse(null);

            if (item != null) {
                int totalSlots = 28; // Backpack total capacity
                int usedSlots = totalSlots - Backpack.countFreeSlots();

                if (useDwarfcannon && usedSlots >= 27 && !Backpack.contains(item.getName())) {
                    return;
                }

                if (!Backpack.isFull() || Backpack.contains(item.getName())) {
                    LootInventory.take(item.getName());
                    log("[NotedItems] Found item to loot: " + item.getName());
                    Execution.delay(random.nextLong(600, 700));
                }
            }
        }
    }

// =====================
// SECTION 4: Loot Stackable Items //TODO: Needs to be broken down into 2 Sections
// =====================

    public static void lootStackableItemsFromInventory() {
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


// =====================
// SECTION 5: Utility Methods
// =====================

    public static Pattern generateLootPattern(List<String> names) {
        return Pattern.compile(
                names.stream()
                        .map(Pattern::quote)
                        .reduce((name1, name2) -> name1 + "|" + name2)
                        .orElse(""),
                Pattern.CASE_INSENSITIVE
        );
    }
}
