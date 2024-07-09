package net.botwithus.Combat;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.LootInventory;
import net.botwithus.rs3.game.Client;
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

import static ImGui.Skills.CombatImGui.lootBasedonCost;
import static net.botwithus.Combat.Notepaper.useItemOnNotepaper;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class LootManager {

    private SnowsScript snowsScript;

    public LootManager(SnowsScript snowsScript) {
        this.snowsScript = snowsScript;
    }

    public static boolean useLootAllNotedItems = false;
    public static boolean walkToLoot = false;


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
            if (useLootAllStackableItems) {
                lootStackableItemsFromInventory();
            }
            if (lootBasedonCost) {
                lootValuableItemsFromInventory();
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
                    Execution.delay(RandomGenerator.nextInt(600, 750));
                    log("[LootEverything] Used Surge: " + ActionBar.useAbility("Surge"));
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
    public static long useCustomLootFromGround() {
        if (!walkToLoot && LootInventory.isOpen()) {
            return random.nextLong(300, 500);
        }
        int totalSlots = 28;
        int usedSlots = totalSlots - Backpack.countFreeSlots();

        LocalPlayer player = getLocalPlayer();
        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream()
                .filter(it -> it.getCoordinate().distanceTo(player.getCoordinate()) <= 25.0D)
                .toList();

        for (GroundItem groundItem : groundItems) {
            if (groundItem.getName() != null && lootPattern.matcher(groundItem.getName()).find()) {
                ItemType itemType = ConfigManager.getItemType(groundItem.getId());
                boolean isStackable = itemType != null && itemType.getStackability() == ItemType.Stackability.ALWAYS;

                if (!LootInventory.contains(groundItem.getName()) || !LootInventory.isOpen()) {
                    if (!groundItem.isReachable()) {
                        log("[CustomLootingFromGround] Ground item is not reachable. Skipping...");
                        return random.nextLong(300, 500);
                    }

                    if (useDwarfcannon && usedSlots >= 27 && (!Backpack.contains(groundItem.getName()) || !isStackable)) {
                        return random.nextLong(300, 500);
                    }

                    if (Backpack.isFull() && (!Backpack.contains(groundItem.getName()) || !isStackable)) {
                        return random.nextLong(300, 500);
                    }

                    groundItem = GroundItemQuery.newQuery().itemId(groundItem.getId()).results().nearest();
                    if (groundItem == null) {
                        log("[CustomLootingFromGround] Ground item no longer exists.");
                    } else {
                        boolean Interacted = groundItem.interact("Take");
                        log("[CustomLootingFromGround] Interacted with: " + groundItem.getName() + " on the ground.");
                        Execution.delay(random.nextLong(600, 750));

                        if (Interacted && player.isMoving() && groundItem != null && groundItem.getCoordinate() != null &&
                                Distance.between(player.getCoordinate(), groundItem.getCoordinate()) > 10 && ActionBar.getCooldown("Surge") == 0) {

                            Execution.delay(random.nextLong(600, 750));
                            log("[CustomLootingFromGround] Used Surge: " + ActionBar.useAbility("Surge"));
                            Execution.delay(RandomGenerator.nextInt(200, 250));
                            groundItem.interact("Take");
                            return random.nextLong(1250, 2000);
                        }
                    }
                }
            }
        }
        return random.nextLong(300, 500);
    }

    public static void useCustomLoot() {
        if (targetItemNames.isEmpty()) {
            log("[Error] No items specified for looting.");
            return;
        }

        int totalSlots = 28;
        int usedSlots = totalSlots - Backpack.countFreeSlots();

        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<Item> inventoryItems = LootInventory.getItems();

        inventoryItems.stream()
                .filter(it -> it.getName() != null && lootPattern.matcher(it.getName()).find())
                .forEach(item -> {
                    ItemType itemType = ConfigManager.getItemType(item.getId());
                    boolean isStackable = itemType != null && itemType.getStackability() == ItemType.Stackability.ALWAYS;

                    if (useDwarfcannon && usedSlots >= 27 && (!Backpack.contains(item.getName()) || !isStackable)) {
                        return;
                    }

                    if (Backpack.isFull() && (!Backpack.contains(item.getName()) || !isStackable)) {
                        return;
                    }

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
                });
    }

// =====================
// SECTION 3: Loot Noted Items
// =====================

   public static long useNotedLootFromGround() {
    if (!walkToLoot && LootInventory.isOpen()) {
        return random.nextLong(300, 500);
    }
    int totalSlots = 28;
    int usedSlots = totalSlots - Backpack.countFreeSlots();

    LocalPlayer player = Client.getLocalPlayer();
    List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream()
            .filter(it -> it.getCoordinate().distanceTo(player.getCoordinate()) <= 25.0D)
            .toList();

    for (GroundItem groundItem : groundItems) {
        if (groundItem.getName() != null && ConfigManager.getItemType(groundItem.getId()).isNote()) {
            if (!LootInventory.contains(groundItem.getName()) || !LootInventory.isOpen()) {
                if (!LootInventory.contains(groundItem.getName())) {
                    if (!groundItem.isReachable()) {
                        log("[NotedItemsFromGround] Ground item is not reachable. Skipping...");
                        return random.nextLong(300, 500);
                    }

                    if (useDwarfcannon && usedSlots >= 27 && !Backpack.contains(groundItem.getName())) {
                        return random.nextLong(300, 500);
                    }

                    if (Backpack.isFull() && !Backpack.contains(groundItem.getName())) {
                        return random.nextLong(300, 500);
                    }

                    groundItem = GroundItemQuery.newQuery().itemId(groundItem.getId()).results().nearest();
                    if (groundItem == null) {
                        log("[NotedItemsFromGround] Ground item no longer exists.");
                    } else {
                        groundItem.interact("Take");
                        log("[NotedItemsFromGround] Interacted with: " + groundItem.getName() + " on the ground.");
                        Execution.delay(random.nextLong(600, 750));

                        if (player.isMoving() && groundItem != null && groundItem.getCoordinate() != null &&
                                Distance.between(player.getCoordinate(), groundItem.getCoordinate()) > 15 &&
                                ActionBar.getCooldown("Surge") == 0) {

                            Execution.delay(random.nextLong(600, 750));
                            log("[NotedItemsFromGround] Used Surge: " + ActionBar.useAbility("Surge"));
                            Execution.delay(RandomGenerator.nextInt(200, 250));
                            groundItem.interact("Take");
                            return random.nextLong(1250, 2000);
                        }
                    }
                }
            }
        }
    }
    return random.nextLong(300, 500);
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
// SECTION 4: Loot Stackable Items
// =====================

    public static void lootStackableItemsFromInventory() {
        if (LootInventory.isOpen()) {
            List<Item> inventoryItems = LootInventory.getItems();

            int totalSlots = 28;
            int usedSlots = totalSlots - Backpack.countFreeSlots();

            for (Item item : inventoryItems) {
                if (item.getName() != null) {
                    var itemType = ConfigManager.getItemType(item.getId());
                    if (itemType != null && isStackable(itemType)) {
                        if (useDwarfcannon && usedSlots >= 27 && !Backpack.contains(item.getName())) {
                            return;
                        }

                        if (Backpack.isFull() && !Backpack.contains(item.getName())) {
                            return;
                        }

                        LootInventory.take(item.getName());
                        log("[StackedItem] Successfully looted stackable item: " + item.getName());
                        Execution.delay(random.nextLong(600, 650));
                    }
                }
            }
        }
    }

    private static boolean isStackable(ItemType itemType) {
        ItemType.Stackability stackability = itemType.getStackability();
        /*log("[Loot] Stackability of item: " + itemType.getName() + " is " + stackability);*/
        /*log("[Loot] Is item stackable? " + isStackable);*/
        return stackability == ItemType.Stackability.ALWAYS;
    }

    public static long lootStackableItemsFromGround() {
        if (!walkToLoot && LootInventory.isOpen()) {
            return random.nextLong(300, 500);
        }
        int totalSlots = 28;
        int usedSlots = totalSlots - Backpack.countFreeSlots();

        GroundItem groundItem = GroundItemQuery.newQuery().results().stream()
                .filter(it -> it.getName() != null && isStackable(ConfigManager.getItemType(it.getId())))
                .findFirst()
                .orElse(null);

        if (groundItem != null) {
            LocalPlayer player = Client.getLocalPlayer();
            if (!LootInventory.contains(groundItem.getName())) {
                if (!groundItem.isReachable()) {
                    return random.nextLong(300, 500);
                }

                if (useDwarfcannon && usedSlots >= 27 && !Backpack.contains(groundItem.getName())) {
                    return random.nextLong(300, 500);
                }

                if (Backpack.isFull() && !Backpack.contains(groundItem.getName())) {
                    return random.nextLong(300, 500);
                }

                groundItem = GroundItemQuery.newQuery().itemId(groundItem.getId()).results().nearest();
                if (groundItem == null) {
                    log("[Loot] Ground item no longer exists.");
                } else {
                    groundItem.interact("Take");
                    log("[Loot] Interacted with: " + groundItem.getName() + " on the ground.");
                    Execution.delay(random.nextLong(600, 750));

                    if (player.isMoving() && groundItem != null && groundItem.getCoordinate() != null &&
                            Distance.between(player.getCoordinate(), groundItem.getCoordinate()) > 15 && ActionBar.getCooldown("Surge") == 0) {

                        Execution.delay(random.nextLong(600, 750));
                        log("[Loot] Used Surge: " + ActionBar.useAbility("Surge"));
                        Execution.delay(random.nextInt(200, 250));
                        groundItem.interact("Take");
                        return random.nextLong(1250, 2000);

                    }
                }
            }
        }
        return random.nextLong(300, 500);
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

// =====================
// SECTION 6: Loot based on Cost
// =====================

    public static long costThreshold = 10000; // Set a default value

    public static int getCostThreshold() {
        return (int) costThreshold;
    }

    public static void setCostThreshold(long costThreshold) {
        LootManager.costThreshold = costThreshold;
    }

    public static void lootValuableItemsFromInventory() {
        long costThreshold = 10000;
        log("[ValuableItem] Cost threshold: " + costThreshold);
        if (LootInventory.isOpen()) {
            log("[ValuableItem] Loot inventory is open.");
            List<Item> inventoryItems = LootInventory.getItems();

            int totalSlots = 28;
            int usedSlots = totalSlots - Backpack.countFreeSlots();
            log("[ValuableItem] Total slots: " + totalSlots + ", Used slots: " + usedSlots);

            for (Item item : inventoryItems) {
                if (item.getName() != null) {
                    log("[ValuableItem] Checking item: " + item.getName());
                    var itemType = ConfigManager.getItemType(item.getId());
                    if (itemType != null && itemType.getCost() >= costThreshold) {
                        log("[ValuableItem] Item cost is above threshold.");
                        if (useDwarfcannon && usedSlots >= 27 && !Backpack.contains(item.getName())) {
                            log("[ValuableItem] Using dwarfcannon and used slots >= 27 and backpack does not contain the item. Returning...");
                            return;
                        }

                        if (Backpack.isFull() && !Backpack.contains(item.getName())) {
                            log("[ValuableItem] Backpack is full and does not contain the item. Returning...");
                            return;
                        }

                        LootInventory.take(item.getName());
                        log("[ValuableItem] Successfully looted valuable item: " + item.getName() + " (Cost: " + itemType.getCost() + ")");
                        Execution.delay(random.nextLong(600, 650));
                    } else {
                        log("[ValuableItem] Item cost is below threshold or item type is null.");
                    }
                } else {
                    log("[ValuableItem] Item name is null.");
                }
            }
        } else {
            log("[ValuableItem] Loot inventory is not open.");
        }
    }

}
