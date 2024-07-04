package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.js5.types.ItemType;
import net.botwithus.rs3.game.js5.types.configs.ConfigManager;

import java.util.ArrayList;
import java.util.List;

import static net.botwithus.CustomLogger.log;

public class ItemRemover {
    public static boolean isDropActive = false;
    public static String droppednames = "";
    public static List<String> selectedDroppedItems = new ArrayList<>();

    public static void removeItemName(String name) {
        selectedDroppedItems.remove(name);
    }

    public static void setDroppednames(String name) {
        droppednames = name;
    }

    public static String getDroppeditems() {
        return droppednames;
    }

    public static List<String> getSelectedDroppedItems() {
        return selectedDroppedItems;
    }

    public static void addDroppedItemName(String nameOrId) {
        if (isNumeric(nameOrId)) {
            int itemId = Integer.parseInt(nameOrId);
            ItemType itemType = ConfigManager.getItemType(itemId);
            String itemName = itemType != null ? itemType.getName() : null;
            if (itemName != null && !selectedDroppedItems.contains(itemName)) {
                selectedDroppedItems.add(itemName);
            }
        } else {
            if (!selectedDroppedItems.contains(nameOrId)) {
                selectedDroppedItems.add(nameOrId);
            }
        }
    }

    public static void dropItems() {
        for (String itemName : selectedDroppedItems) {
            if (Backpack.contains(itemName)) {
                Backpack.interact(itemName, "Drop");
                log("Dropping " + itemName);
                break;
            }
        }
    }
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}