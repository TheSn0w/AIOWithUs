package net.botwithus.inventory;

import net.botwithus.api.game.hud.inventories.BackpackInventory;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * A class that provides methods to interact with the player's backpack.
 *
 * @author David
 */
public final class backpack {
    private static final Pattern SELECTED_ITEM_PATTERN = Pattern.compile("^Use\\s.*?(\\s->\\s).*$");

    static final backpackInventory backpack = new backpackInventory();

    private backpack() {
    }

    /**
     * Checks if the backpack is full.
     *
     * @return true if the backpack is full, false otherwise
     */
    public static boolean isFull() {
        return backpack.isFull();
    }

    /**
     * Checks if the backpack is empty.
     *
     * @return true if the backpack is empty, false otherwise.
     */
    public static boolean isEmpty() {
        return backpack.isEmpty();
    }

    public static boolean contains(String... names) {
        return backpack.contains(names);
    }

    public static boolean contains(int... ids) {
        return backpack.contains(ids);
    }

    public static int countFreeSlots() {
        return backpack.countFreeSlots();
    }

    public static int getCount() {
        return backpack.getCount();
    }

    public static int getCount(String... names) {
        return backpack.getCount(names);
    }

    public static int getCount(int... ids) {
        return backpack.getCount(ids);
    }

    public static int getCount(Pattern pattern) {
        return backpack.getCount(pattern);
    }

    public static int getQuantity(String... names) {
        return backpack.getQuantity(names);
    }

    public static int getQuantity(int... ids) {
        return backpack.getQuantity(ids);
    }

    public static int getQuantity(Pattern itemNamePattern) {
        return backpack.getQuantity(itemNamePattern);
    }

    public static boolean contains(Pattern itemNamePattern) {
        return backpack.contains(itemNamePattern);
    }

    public static boolean containsAllOf(String... names) {
        return backpack.containsAllOf(names);
    }

    public static boolean containsAllOf(Pattern pattern) {
        return backpack.containsAllOf(pattern);
    }

    public static boolean containsAnyExcept(String... names) {
        return backpack.containsAnyExcept(names);
    }

    public static boolean containsAnyExcept(Pattern... patterns) {
        return backpack.containsAnyExcept(patterns);
    }

    public static boolean containsItemByCategory(int... categoryIds) {
        return backpack.containsItemByCategory(categoryIds);
    }

    public static boolean interact(int slot, String option) {
        return backpack.interact(slot, option);
    }

    /**
     * Executes an action on the backpack.
     *
     * @param slot   The slot of the backpack to perform the action on.
     * @param option The option to perform on the slot.
     * @return Whether the action was successful.
     */
    public static boolean interact(int slot, int option) {
        return backpack.interact(slot, option);
    }

    /**
     * Executes an action on the backpack.
     *
     * @param name The name of the action to be executed.
     * @return True if the action was successful, false otherwise.
     */
    public static boolean interact(String name) {
        return backpack.interact(name);
    }

    /**
     * Executes an action on a given item in the backpack.
     *
     * @param name   The name of the item to perform the action on.
     * @param option The action to perform on the item.
     * @return True if the action was successful, false otherwise.
     */
    public static boolean interact(String name, String option) {
        return backpack.interact(name, option);
    }

    /**
     * Executes an action on the backpack.
     *
     * @param name       The name of the action to be executed.
     * @param option     The option of the action to be executed.
     * @param namepred   The predicate to be used to validate the name.
     * @param optionpred The predicate to be used to validate the option.
     * @return True if the action was successful, false otherwise.
     */
    public static boolean interact(String name, String option, BiFunction<String, CharSequence, Boolean> namepred, BiFunction<String, CharSequence, Boolean> optionpred) {
        return backpack.interact(name, option, namepred, optionpred);
    }

    /**
     * Executes an action on the backpack
     *
     * @param name   The name of the action to be executed
     * @param option The option to be used for the action
     * @return The result of the action
     */
    public static boolean interact(String name, int option) {
        return backpack.interact(name, option);
    }

    public static boolean interact(Pattern namePattern, String option) {
        return backpack.interact(namePattern, option);
    }

    public static boolean interact(Pattern namePattern, int option) {
        return backpack.interact(namePattern, option);
    }

    /**
     * Gets the value of a varbit in a given slot.
     *
     * @param slot     The slot of the varbit.
     * @param varbitId The ID of the varbit.
     * @return The value of the varbit.
     */
    public static int getVarbitValue(int slot, int varbitId) {
        return backpack.getVarbitValue(slot, varbitId);
    }

    /**
     * Retrieves an item from the backpack.
     *
     * @param name The name of the item to retrieve.
     * @return An {@link Optional} containing the item if it exists, or an empty {@link Optional} if it does not.
     */
    public static Item getItem(String name) {
        return backpack.getItem(name);
    }

    public static Item getItem(Pattern pattern) {
        return backpack.getItem(pattern);
    }

    public static List<Item> getItems() {
        return backpack.getItems();
    }

    public static List<Item> getItemsWithOption(String option) {
        return backpack.getItemsWithOptions(option);
    }

    public static Item getSelectedItem() {
        return InventoryItemQuery.newQuery(93).option(SELECTED_ITEM_PATTERN).results().first();
    }
}