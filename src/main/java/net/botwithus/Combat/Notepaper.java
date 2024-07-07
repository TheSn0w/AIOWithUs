package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.js5.types.configs.ConfigManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.*;

import static net.botwithus.Combat.LootManager.queueLoot;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.random;

public class Notepaper {

    public static String NotepaperName = "";

    public static String getNotepaperName() {
        return NotepaperName;
    }

    public static void setNotepaperName(String notepaperName) {
        NotepaperName = notepaperName;
    }

    public static void removeNotepaperName(String notepaperName) {
        log("[Info] Removing " + notepaperName + " from selected notepaper names.");
        selectedNotepaperNames.remove(notepaperName);
    }

    public static final List<String> selectedNotepaperNames = new ArrayList<>();

    public static List<String> getSelectedNotepaperNames() {
        return selectedNotepaperNames;
    }

    public static void addNotepaperName(String notepaperName) {
        log("[Info] Adding " + notepaperName + " to selected notepaper names.");
        selectedNotepaperNames.add(notepaperName);
    }

    private static volatile boolean isUsingNotepaper = false;

    public static void useItemOnNotepaper() {
        if (!isUsingNotepaper) {
            queueLoot(() -> {
                isUsingNotepaper = true;
                try {
                    List<Item> backpackItems = new ArrayList<>(backpack.getItems());

                    for (String itemName : getSelectedNotepaperNames()) {
                        List<Item> matchingItems = backpackItems.stream()
                                .filter(item -> item.getName().toLowerCase().contains(itemName.toLowerCase()))
                                .toList();

                        for (Item targetItem : matchingItems) {
                            var itemType = ConfigManager.getItemType(targetItem.getId());
                            boolean isNote = itemType != null && itemType.isNote();
                            if (isNote) {
                                continue;
                            }

                            Item notepaper = fetchNotepaperFromInventory();
                            if (notepaper == null) {
                                log("[Error] Neither Magic Notepaper nor Enchanted Notepaper found in inventory.");
                                return;
                            }

                            boolean itemSelected = MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, targetItem.getSlot(), 96534533);
                            log("[Info] Item selected: " + itemSelected);
                            Execution.delay(RandomGenerator.nextInt(200, 300));

                            if (itemSelected) {
                                boolean notepaperSelected = MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, notepaper.getSlot(), 96534533);
                                log("[Info] Notepaper selected: " + notepaperSelected);

                                if (notepaperSelected) {
                                    String notepaperName = notepaper.getName();
                                    log("[Success] " + itemName + " successfully used on " + notepaperName + ".");
                                    Execution.delay(random.nextLong(1250, 1500));
                                    break;
                                } else {
                                    String notepaperName = notepaper.getName();
                                    log("[Error] Failed to use " + itemName + " on " + notepaperName + ".");
                                    log("[Debug] Notepaper details - Name: " + notepaper.getName() + ", ID: " + notepaper.getId());
                                }
                            } else {
                                log("[Error] Failed to select " + itemName + ".");
                                log("[Debug] Item details - Name: " + targetItem.getName() + ", ID: " + targetItem.getId());
                            }
                        }
                    }
                } finally {
                    isUsingNotepaper = false;
                }
            });
        }
    }

    private static Item fetchNotepaperFromInventory() {
        Item magicNotepaper = fetchSpecificNotepaper("Magic notepaper");

        if (magicNotepaper == null) {
            log("[Debug] Magic Notepaper not found in inventory. Trying to fetch Enchanted notepaper...");
            Item enchantedNotepaper = fetchSpecificNotepaper("Enchanted notepaper");

            if (enchantedNotepaper == null) {
                log("[Debug] Enchanted Notepaper not found in inventory.");
                return null;
            } else {
                return enchantedNotepaper;
            }
        } else {
            return magicNotepaper;
        }
    }

    private static Item fetchSpecificNotepaper(String notepaperName) {
        Item notepaper = Backpack.getItem(notepaperName);
        if (notepaper != null) {
            log("[Info] Notepaper found: " + notepaper.getName());
            return notepaper;
        }
        log("[Debug] " + notepaperName + " not found in inventory.");
        return null;
    }

}
