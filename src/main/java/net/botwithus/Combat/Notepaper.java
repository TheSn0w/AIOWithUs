package net.botwithus.Combat;

import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.js5.types.configs.ConfigManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.*;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.itemNamesToUseOnNotepaper;
import static net.botwithus.SnowsScript.notedItemsTracker;

public class Notepaper {


    public static List<String> getItemNamesToUseOnNotepaper() {
        return new ArrayList<>(itemNamesToUseOnNotepaper);
    }

    public static void addItemNameToUseOnNotepaper(String itemName) {
        if (!itemNamesToUseOnNotepaper.contains(itemName)) {
            itemNamesToUseOnNotepaper.add(itemName);
        }
    }

    public static void removeItemNameToUseOnNotepaper(String itemName) {
        itemNamesToUseOnNotepaper.remove(itemName);
    }


    public static void useItemOnNotepaper() {
        for (String itemName : itemNamesToUseOnNotepaper) {
            if (notedItemsTracker.getOrDefault(itemName.toLowerCase(), false)) {
                continue;
            }
            Item targetItem = InventoryItemQuery.newQuery(93)
                    .name(itemName)
                    .results()
                    .stream()
                    .filter(item -> !Objects.requireNonNull(ConfigManager.getItemType(item.getId())).isNote()) // Ensure item is not noted
                    .findFirst()
                    .orElse(null);

            if (targetItem == null) {
                continue;
            }

            // Retrieve Magic Notepaper or Enchanted Notepaper from the inventory
            Item notepaper = InventoryItemQuery.newQuery(93)
                    .results()
                    .stream()
                    .filter(item -> item.getName().equalsIgnoreCase("Magic notepaper") || item.getName().equalsIgnoreCase("Enchanted notepaper"))
                    .findFirst()
                    .orElse(null);

            if (notepaper == null) {
                log("[Error] Neither Magic Notepaper nor Enchanted Notepaper found in inventory.");
                return;
            }

            String notepaperName = notepaper.getName(); // Store the name of the notepaper


            boolean itemSelected = MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, targetItem.getSlot(), 96534533);
            Execution.delay(RandomGenerator.nextInt(500, 750));

            if (itemSelected) {
                boolean notepaperSelected = MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, notepaper.getSlot(), 96534533);
                Execution.delay(RandomGenerator.nextInt(500, 750));

                if (notepaperSelected) {
                    log("[Success] " + itemName + " successfully used on " + notepaperName + ".");
                    // Mark the item as noted
                    notedItemsTracker.put(itemName.toLowerCase(), true);
                } else {
                    log("[Error] Failed to use " + itemName + " on " + notepaperName + ".");
                }
            } else {
                log("[Error] Failed to select " + itemName + ".");
            }
        }
    }
}
