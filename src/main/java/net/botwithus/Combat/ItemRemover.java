package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.script.Execution;

import java.util.ArrayList;
import java.util.List;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.random;

public class ItemRemover {
    public static boolean isDropActive = false;
    public static String itemName = "";
    public static List<String> selectedItems = new ArrayList<>();

    public static void removeItemName(String name) {
        selectedItems.remove(name);
    }

    public static void setItemName(String name) {
        itemName = name;
    }

    public static String getItemName() {
        return itemName;
    }

    public static List<String> getSelectedItems() {
        return selectedItems;
    }

    public static void addItemName(String name) {
        if (!selectedItems.contains(name)) {
            selectedItems.add(name);
        }
    }

    public static void dropItems() {
        for (String itemName : selectedItems) {
            if (Backpack.contains(itemName)) {
                Backpack.interact(itemName, "Drop");
                log("Dropping " + itemName);
                break;
            }
        }
    }
}