package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.TaskScheduler;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;

public class Dissasembler {
    public static boolean doAll = false; // Add this line

    public static long Dissasemble(LocalPlayer player) {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1250, 2500);
        } else {
            return disassembleAllItems();
        }
    }

    public static long disassembleAllItems() {
        for (Item item : Backpack.getItems()) {
            if (item != null && ActionBar.containsAbility("Disassemble")) {
                Component disassemble = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673).spriteId(12510).option("Customise-keybind").results().first();
                if (disassemble != null) {
                    log("[Disassembler] Used disassemble spell: " + MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, disassemble.getInterfaceIndex() << 16 | disassemble.getComponentIndex()));
                    Execution.delay(random.nextLong(750, 1758));
                    log("[Disassembler] Selected disassemble item: " + MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, item.getSlot(), 96534533));
                    Execution.delay(random.nextLong(750, 1758));
                    return random.nextLong(1500, 3000);
                } else {
                    log("[Error] Item is null or ActionBar does not contain 'Disassemble' ability.");
                }
            } else {
                log("[Error] Backpack does not contain the item: " + item.getName());
            }
        }
        if (Backpack.isEmpty()) {
            log("[Disassembler] Backpack is empty. Shutting down...");
            shutdown();
        }
        return random.nextLong(1500, 3000);
    }

    public static long disassembleScheduledTasks() {
        for (TaskScheduler task : tasks) {
            if (!task.isComplete()) {
                String itemName = task.itemToDisassemble;
                if (Backpack.contains(itemName)) {
                    log("[Disassembler] Backpack contains the item: " + itemName);
                    Item item = Backpack.getItem(itemName);
                    if (item != null && ActionBar.containsAbility("Disassemble")) {
                        Component disassemble = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673).spriteId(12510).option("Customise-keybind").results().first();
                        if (disassemble != null) {
                            log("[Disassembler] Used disassemble spell: " + MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, disassemble.getInterfaceIndex() << 16 | disassemble.getComponentIndex()));
                            Execution.delay(random.nextLong(750, 1758));
                            log("[Disassembler] Selected disassemble item: " + MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, item.getSlot(), 96534533));
                            Execution.delay(random.nextLong(750, 1758));
                            return random.nextLong(1500, 3000);
                        }
                    } else {
                        log("[Error] Item is null or ActionBar does not contain 'Disassemble' ability.");
                    }
                } else {
                    log("[Error] Backpack does not contain the item: " + itemName);
                }
            }
        }
        if (tasks.isEmpty()) {
            log("[Disassembler] All tasks completed. Shutting down...");
            shutdown();
        }
        return random.nextLong(750, 1250);
    }

    public static long castHighLevelAlchemy(LocalPlayer player) {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1250, 2500);
        } else {
            return castHighLevelAlchemyOnAllItems();
        }
    }

    public static long castHighLevelAlchemyOnAllItems() {
        for (Item item : Backpack.getItems()) {
            if (item != null && !item.getName().equals("Nature rune") && ActionBar.containsAbility("High Level Alchemy")) {
                Component Alchemy = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673).spriteId(14379).option("Customise-keybind").results().first();
                if (Alchemy != null) {
                    log("[High Alchemy] Casting High Level Alchemy on: " + item.getName());
                    log("[High Alchemy] Selected High Level Alchemy: " + MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, Alchemy.getInterfaceIndex() << 16 | Alchemy.getComponentIndex()));
                    Execution.delay(random.nextLong(505, 650));
                    log("[High Alchemy] Selected item: " + MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, item.getSlot(), 96534533));
                    break;
                } else {
                    log("[Error] Item is null or ActionBar does not contain 'High Level Alchemy' ability.");
                }
            } else if (item != null && !item.getName().equals("Nature rune")) {
                log("[Error] Backpack does not contain the item: " + item.getName());
            }
        }
        if (isBackpackEmptyExceptNatureRune()) {
            log("[High Alchemy] Backpack is empty. Shutting down...");
            shutdown();
        }
        return random.nextLong(1750, 2500);
    }

    public static boolean isBackpackEmptyExceptNatureRune() {
        for (Item item : Backpack.getItems()) {
            if (!item.getName().equals("Nature rune")) {
                return false;
            }
        }
        return true;
    }

    public static long castHighLevelAlchemyOnScheduledTasks() {
        for (TaskScheduler task : tasks) {
            if (!task.isComplete()) {
                String itemName = task.itemToDisassemble;
                if (Backpack.contains(itemName)) {
                    log("[High Alchemy] Backpack contains the item: " + itemName);
                    Item item = Backpack.getItem(itemName);
                    if (item != null && ActionBar.containsAbility("High Level Alchemy")) {
                        Component Alchemy = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673).spriteId(14379).option("Customise-keybind").results().first();
                        if (Alchemy != null) {
                            log("[High Alchemy] Selected High Level Alchemy: " + MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, Alchemy.getInterfaceIndex() << 16 | Alchemy.getComponentIndex()));
                            Execution.delay(random.nextLong(505, 650));
                            log("[High Alchemy] Selected item: " + MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, item.getSlot(), 96534533));
                        }
                    } else {
                        log("[Error] Item is null or ActionBar does not contain 'High Level Alchemy' ability.");
                    }
                } else {
                    log("[Error] Backpack does not contain the item: " + itemName);
                }
            }
        }
        if (tasks.isEmpty()) {
            log("[High Alchemy] All tasks completed. Shutting down...");
            shutdown();
        }
        return random.nextLong(1750, 2500);
    }
}
