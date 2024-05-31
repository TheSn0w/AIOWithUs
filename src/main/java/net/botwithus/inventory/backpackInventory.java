package net.botwithus.inventory;

import net.botwithus.api.game.hud.inventories.Inventory;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.function.Function;
import java.util.regex.Pattern;

public class backpackInventory extends inventory {
    private static final Function<Integer, Integer> OPTION_MAPPER = i -> switch (i) {
        case 2 -> 3;
        case 3 -> 4;
        case 4 -> 5;
        case 5 -> 6;
        case 6 -> 7;
        case 7 -> 8;
        default -> 1;
    };

    public backpackInventory() {
        super(93, 1473, 5, OPTION_MAPPER);
    }

    @Override
    public boolean interact(String name, int option) {
        ResultSet<Item> results = InventoryItemQuery.newQuery(getId()).name(name).results();
        Item item = results.first();
        if (item != null) {
            ResultSet<Component> queryResults = ComponentQuery.newQuery(interfaceIndex).item(
                    item.getId()).componentIndex(componentIndex).results();
            var result = queryResults.first();
            return result != null && result.interact(option);
        }
        return false;
    }

    @Override
    public boolean interact(Pattern name, int option) {
        ResultSet<Item> results = InventoryItemQuery.newQuery(getId()).name(name).results();
        Item item = results.first();
        if (item != null) {
            ResultSet<Component> queryResults = ComponentQuery.newQuery(interfaceIndex).item(
                    item.getId()).componentIndex(componentIndex).results();
            var result = queryResults.first();
            return result != null && result.interact(option);
        }
        return false;
    }

    @Override
    public boolean interact(String name, String option) {
        ResultSet<Item> results = InventoryItemQuery.newQuery(getId()).name(name).results();
        Item item = results.first();
        if (item != null) {
            ResultSet<Component> queryResults = ComponentQuery.newQuery(interfaceIndex).item(
                    item.getId()).componentIndex(componentIndex).results();
            var result = queryResults.first();
            return result != null && result.interact(option);
        }
        return false;
    }

    @Override
    public boolean interact(int slot, int option) {
        ResultSet<Item> results = InventoryItemQuery.newQuery(getId()).slots(slot).results();
        Item item = results.first();
        if (item != null) {
            ResultSet<Component> queryResults = ComponentQuery.newQuery(interfaceIndex).item(
                    item.getId()).componentIndex(componentIndex).results();
            var result = queryResults.first();
            return result != null && result.interact(option);
        }
        return false;
    }

    @Override
    public boolean interact(int slot, String option) {
        ResultSet<Item> results = InventoryItemQuery.newQuery(getId()).slots(slot).results();
        Item item = results.first();
        if (item != null) {
            ResultSet<Component> queryResults = ComponentQuery.newQuery(interfaceIndex).item(
                    item.getId()).componentIndex(componentIndex).results();
            var result = queryResults.first();
            return result != null && result.interact(option);
        }
        return false;
    }

}