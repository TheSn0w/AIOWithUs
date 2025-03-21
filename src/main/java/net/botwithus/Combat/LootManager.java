package net.botwithus.Combat;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.LootInventory;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.ItemType;
import net.botwithus.rs3.game.js5.types.configs.ConfigManager;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.GroundItemQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.item.GroundItem;
import net.botwithus.rs3.script.Execution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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
        while (isCombatActive && ScriptisOn && snowsScript.isActive()) {
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
            if (useLootEverything) {
                lootAllButton();
            }
            Execution.delay(random.nextLong(800, 1000));
        }
    }


    // =====================
// SECTION 1: Loot Everything
// =====================
    public static void useLootInventoryPickup() {
        if (!walkToLoot && LootInventory.isOpen()) {
            return;
        }
        LocalPlayer player = getLocalPlayer();
        List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream()
                .filter(it -> it.getCoordinate().distanceTo(player.getCoordinate()) <= 25.0D)
                .toList();
        if (groundItems.isEmpty()) {
            return;
        }

        GroundItem groundItem = groundItems.stream().min(Comparator.comparingDouble(it -> it.getCoordinate().distanceTo(player.getCoordinate()))).orElse(null);
        if (groundItem != null) {
            if (Backpack.isFull() && (!Backpack.contains(groundItem.getName()) || !isStackable(groundItem.getConfigType()))) {
                return;
            }

            double distance = groundItem.getCoordinate().distanceTo(player.getCoordinate());
            if (distance <= 25.0D) {
                if (groundItem.interact("Take")) {
                    log("[LootEverything] Taking " + groundItem.getName() + "...");
                }

                boolean interfaceOpened = Execution.delayUntil(random.nextLong(3000, 5000), () -> Interfaces.isOpen(1622));
                if (!interfaceOpened) {
                    log("[Error] Loot Inventory did not open. Attempting to interact with ground item again.");
                    if (groundItem.interact("Take")) {
                        log("[LootEverything] Attempting to take " + groundItem.getName() + " again...");
                    }
                }
            }
        }
    }


   public static void lootAllButton() {
       if (LootInventory.isOpen() && !LootInventory.getItems().isEmpty() && !Backpack.isFull()) {
           ComponentQuery lootAllQuery = ComponentQuery.newQuery(1622);
           List<Component> components = lootAllQuery.componentIndex(22).results().stream().toList();

           if (!components.isEmpty() && components.get(0).interact(1)) {
               log("[LootAll] Looted all items from the inventory.");
           }
       }
   }



    // =====================
// SECTION 2: Loot Specific Items
// =====================
    public static void useCustomLootFromGround() {
        if (!walkToLoot && LootInventory.isOpen()) {
            return;
        }
        if (targetItemNames.isEmpty()) {
            log("[Error] No items specified for looting.");
            return;
        }

        int totalSlots = 28;
        int usedSlots = totalSlots - Backpack.countFreeSlots();

        LocalPlayer player = getLocalPlayer();
        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream()
                .filter(it -> it.getCoordinate().distanceTo(player.getCoordinate()) <= 25.0D)
                .toList();

        Optional<GroundItem> optionalGroundItem = groundItems.stream()
                .filter(groundItem -> groundItem.getName() != null && lootPattern.matcher(groundItem.getName()).find())
                .findFirst();

        if (!optionalGroundItem.isEmpty()) {
            GroundItem groundItem = optionalGroundItem.get();
            ItemType itemType = ConfigManager.getItemType(groundItem.getId());
            boolean isStackable = itemType != null && itemType.getStackability() == ItemType.Stackability.ALWAYS;

            if (!LootInventory.contains(groundItem.getName()) || !LootInventory.isOpen()) {
                if (useDwarfcannon && usedSlots >= 27 && (!Backpack.contains(groundItem.getName()) || !isStackable)) {
                    random.nextLong(300, 500);
                    return;
                }

                if (Backpack.isFull() && (!Backpack.contains(groundItem.getName()) || !isStackable)) {
                    random.nextLong(300, 500);
                    return;
                }

                groundItem = GroundItemQuery.newQuery().itemId(groundItem.getId()).results().nearest();
                if (groundItem == null) {
                    log("[CustomLootingFromGround] Ground item no longer exists.");
                } else {
                    boolean interacted = groundItem.interact("Take");
                    log("[CustomLootingFromGround] Interacted with: " + groundItem.getName() + " on the ground.");
                    Execution.delay(random.nextLong(1000, 1500));

                    if (interacted && player.isMoving() && groundItem.getCoordinate() != null) {

                        if (Direction.of(player.getCoordinate(), groundItem.getCoordinate()) == Direction.of(player) &&
                                Distance.between(player.getCoordinate(), groundItem.getCoordinate()) > 10 && ActionBar.getCooldown("Surge") == 0) {
                            Execution.delay(random.nextLong(600, 750));
                            log("[NotedItemsFromGround] Used Surge: " + ActionBar.useAbility("Surge"));
                            Execution.delay(random.nextInt(200, 250));
                            groundItem.interact("Take");
                        } else {
                            Execution.delayUntil(random.nextLong(7500, 10000), LootInventory::isOpen);
                        }
                    }
                }
            }
        }
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

        Optional<Item> optionalItem = inventoryItems.stream()
                .filter(it -> it.getName() != null && lootPattern.matcher(it.getName()).find())
                .findFirst();

        if (!optionalItem.isEmpty()) {
            Item item = optionalItem.get();
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
            }
        }
    }

// =====================
// SECTION 3: Loot Noted Items
// =====================

    public static void useNotedLootFromGround() {
        if (!walkToLoot && LootInventory.isOpen()) {
            return;
        }
        int totalSlots = 28;
        int usedSlots = totalSlots - Backpack.countFreeSlots();

        LocalPlayer player = Client.getLocalPlayer();
        List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream()
                .filter(it -> it.getCoordinate().distanceTo(player.getCoordinate()) <= 25.0D)
                .filter(it -> excludedKeywords.stream().noneMatch(keyword -> it.getName().contains(keyword))) // Exclude items with the specified keywords
                .toList();

        Optional<GroundItem> optionalGroundItem = groundItems.stream()
                .filter(groundItem -> groundItem.getName() != null && ConfigManager.getItemType(groundItem.getId()).isNote())
                .findFirst();

    if (!optionalGroundItem.isEmpty()) {
        GroundItem groundItem = optionalGroundItem.get();
        if (!LootInventory.contains(groundItem.getName()) || !LootInventory.isOpen()) {
            if (!LootInventory.contains(groundItem.getName())) {
                if (useDwarfcannon && usedSlots >= 27 && !Backpack.contains(groundItem.getName())) {
                    return;
                }

                if (Backpack.isFull() && !Backpack.contains(groundItem.getName())) {
                    return;
                }

                groundItem = GroundItemQuery.newQuery().itemId(groundItem.getId()).results().nearest();
                if (groundItem == null) {
                    log("[NotedItemsFromGround] Ground item no longer exists.");
                } else {
                    boolean interacted = groundItem.interact("Take");
                    log("[NotedItemsFromGround] Interacted with: " + groundItem.getName() + " on the ground.");
                    Execution.delay(random.nextLong(1000, 1500));

                    if (interacted && player.isMoving() && groundItem.getCoordinate() != null) {

                        if (Direction.of(player.getCoordinate(), groundItem.getCoordinate()) == Direction.of(player) && Distance.between(player.getCoordinate(), groundItem.getCoordinate()) > 10 && ActionBar.getCooldown("Surge") == 0) {
                            Execution.delay(random.nextLong(600, 750));
                            log("[NotedItemsFromGround] Used Surge: " + ActionBar.useAbility("Surge"));
                            Execution.delay(random.nextInt(200, 250));
                            groundItem.interact("Take");
                        } else {
                            Execution.delayUntil(random.nextLong(7500, 10000), LootInventory::isOpen);
                        }
                    }
                }
            }
        }
    }
}


    public static void lootNotedItemsFromInventory() {
        if (LootInventory.isOpen()) {
            List<Item> inventoryItems = LootInventory.getItems();

            Item item = inventoryItems.stream()
                    .filter(it -> it.getName() != null && ConfigManager.getItemType(it.getId()).isNote())
                    .filter(it -> excludedKeywords.stream().noneMatch(keyword -> it.getName().contains(keyword))) // Exclude items with the specified keywords
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

    public static List<String> excludedKeywords = new ArrayList<>();

    public static List<String> getExcludedKeywords() {
        return excludedKeywords;
    }

    public static void addExcludedKeyword(String keyword) {
        if (!excludedKeywords.contains(keyword)) {
            excludedKeywords.add(keyword);
        }
    }

    public static void removeExcludedKeyword(String keyword) {
        excludedKeywords.remove(keyword);
    }


    private static String excludedKeyword = "";

    public static String getExcludedKeyword() {
        return excludedKeyword;
    }

    public static void setExcludedKeyword(String keyword) {
        excludedKeyword = keyword;
    }



    public static void lootStackableItemsFromInventory() {
        if (LootInventory.isOpen()) {
            List<Item> inventoryItems = LootInventory.getItems();

            int totalSlots = 28;
            int usedSlots = totalSlots - Backpack.countFreeSlots();

            inventoryItems.stream()
                    .filter(item -> item.getName() != null)
                    .filter(item -> excludedKeywords.stream().noneMatch(keyword -> item.getName().contains(keyword))) // Exclude items with the specified keywords
                    .map(item -> ConfigManager.getItemType(item.getId()))
                    .filter(itemType -> itemType != null && isStackable(itemType))
                    .filter(itemType -> !(useDwarfcannon && usedSlots >= 27 && !Backpack.contains(itemType.getName())))
                    .filter(itemType -> !(Backpack.isFull() && !Backpack.contains(itemType.getName())))
                    .forEach(itemType -> {
                        LootInventory.take(itemType.getName());
                        log("[StackedItem] Successfully looted stackable item: " + itemType.getName());
                        Execution.delay(random.nextLong(600, 650));
                    });
        }
    }


    private static boolean isStackable(ItemType itemType) {
        ItemType.Stackability stackability = itemType.getStackability();
        /*log("[Loot] Stackability of item: " + itemType.getName() + " is " + stackability);*/
        /*log("[Loot] Is item stackable? " + isStackable);*/
        return stackability == ItemType.Stackability.ALWAYS;
    }

    public static void lootStackableItemsFromGround() {
        if (!walkToLoot && LootInventory.isOpen()) {
            return;
        }
        int totalSlots = 28;
        int usedSlots = totalSlots - Backpack.countFreeSlots();

        LocalPlayer player = Client.getLocalPlayer();
        GroundItem groundItem = GroundItemQuery.newQuery().results().stream()
                .filter(it -> it.getName() != null && isStackable(ConfigManager.getItemType(it.getId())))
                .filter(it -> excludedKeywords.stream().noneMatch(keyword -> it.getName().contains(keyword))) // Exclude items with the specified keywords
                .filter(it -> it.getCoordinate().distanceTo(player.getCoordinate()) <= 25) // Filter items within a 30 tile radius
                .findFirst()
                .orElse(null);

        if (groundItem != null) {
            if (!LootInventory.contains(groundItem.getName())) {
                if (useDwarfcannon && usedSlots >= 27 && !Backpack.contains(groundItem.getName())) {
                    return;
                }

                if (Backpack.isFull() && !Backpack.contains(groundItem.getName())) {
                    return;
                }

                groundItem = GroundItemQuery.newQuery().itemId(groundItem.getId()).results().nearest();
                if (groundItem == null) {
                    log("[Loot] Ground item no longer exists.");
                } else {
                    boolean interacted = groundItem.interact("Take");
                    log("[Loot] Interacted with: " + groundItem.getName() + " on the ground.");
                    Execution.delay(random.nextLong(1000, 1500));

                    if (interacted && player.isMoving() && groundItem.getCoordinate() != null) {

                        if (Direction.of(player.getCoordinate(), groundItem.getCoordinate()) == Direction.of(player) && Distance.between(player.getCoordinate(), groundItem.getCoordinate()) > 10 && ActionBar.getCooldown("Surge") == 0) {
                            Execution.delay(random.nextLong(600, 750));
                            log("[NotedItemsFromGround] Used Surge: " + ActionBar.useAbility("Surge"));
                            Execution.delay(random.nextInt(200, 250));
                            groundItem.interact("Take");
                        } else {
                            Execution.delayUntil(random.nextLong(7500, 10000), LootInventory::isOpen);
                        }
                    }
                }
            }
        }
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
