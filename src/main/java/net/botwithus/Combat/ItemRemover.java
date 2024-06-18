package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;

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

    public static void addDroppedItemName(String name) {
        if (!selectedDroppedItems.contains(name)) {
            selectedDroppedItems.add(name);
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
}