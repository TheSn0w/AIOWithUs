package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.SnowsScript;
import net.botwithus.TaskScheduler;
import net.botwithus.Variables.Variables;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;
import java.util.Random;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.IDLE;
import static net.botwithus.Variables.Variables.*;

public class Dissasembler {
    public SnowsScript script;
    private static final Random random = new Random();

    public Dissasembler(SnowsScript script) {
        this.script = script;
    }

    public static long Dissasemble(LocalPlayer player) {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1250, 2500);
        } else {
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
                                Execution.delay(random.nextLong(750,1758));
                                log("[Disassembler] Selected disassemble item: " + MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, item.getSlot(), 96534533));
                                Execution.delay(random.nextLong(750,1758));
                                if (Interfaces.isOpen(847)) {
                                    log("[Error] Valuable item warning detected, but setting not enabled in the UI. Removing task.");
                                    return random.nextLong(1500, 3000);
                                }
                                if (task.getAmountDisassembled() >= itemMenuSize) { // Check if we have disassembled the required amount
                                    tasks.remove(task); // Remove the task from the list
                                }
                                return random.nextLong(1500, 3000);
                            }
                        } else {
                            log("[Error] Item is null or ActionBar does not contain 'Disassemble' ability.");
                            shutdown();
                        }
                    } else {
                        log("[Error] Backpack does not contain the item: " + itemName);
                        shutdown();
                    }
                }
            }
        }
        log("[Disassembler] Returning from Dissasemble method...");
        shutdown();
        return random.nextLong(750, 1250);
    }
    public static void shutdown() {
        SnowsScript.setBotState(IDLE);
        LoginManager.setAutoLogin(false);
        MiniMenu.interact(14, 1, -1, 93913156);
    }

    public static long castHighLevelAlchemy(LocalPlayer player) {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1250, 2500);
        } else {
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
                                if (Interfaces.isOpen(847)) {
                                    log("[Error] Valuable item warning detected, but setting not enabled in the UI. Removing task.");
                                    return random.nextLong(1500, 3000);
                                }
                                if (task.getAmountDisassembled() >= itemMenuSize) {
                                    tasks.remove(task);
                                }
                                return random.nextLong(2250, 3000);
                            }
                        } else {
                            log("[Error] Item is null or ActionBar does not contain 'High Level Alchemy' ability.");
                            shutdown();
                        }
                    } else {
                        log("[Error] Backpack does not contain the item: " + itemName);
                        shutdown();
                    }
                }
            }
        }
        log("[High Alchemy] Returning from Dissasemble method...");
        shutdown();
        return random.nextLong(1750, 2500);
    }
}
