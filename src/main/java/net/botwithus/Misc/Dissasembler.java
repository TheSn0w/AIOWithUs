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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static net.botwithus.SnowsScript.BotState.IDLE;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.energy;

public class Dissasembler {
    public SnowsScript script;
    private static Random random = new Random();

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
                        ScriptConsole.println("Backpack contains the item: " + itemName);
                        Item item = Backpack.getItem(itemName);
                        if (item != null && ActionBar.containsAbility("Disassemble")) {
                            ScriptConsole.println("Item is not null and ActionBar contains 'Disassemble' ability, interacting with MiniMenu...");
                            Component disassemble = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673).spriteId(12510).option("Customise-keybind").results().first();
                            if (disassemble != null) {
                                ScriptConsole.println("Used disassemble spell: " + MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, disassemble.getInterfaceIndex() << 16 | disassemble.getComponentIndex()));
                                Execution.delay(random.nextLong(750,1758));
                                ScriptConsole.println("Selected disassemble item: " + MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, item.getSlot(), 96534533));
                                Execution.delay(random.nextLong(750,1758));
                                if (Interfaces.isOpen(847)) {
                                    ScriptConsole.println("Valuable item warning detected, but setting not enabled in the UI. Removing task.");
                                    return random.nextLong(1500, 3000);
                                }
                                if (task.getAmountDisassembled() >= itemMenuSize) { // Check if we have disassembled the required amount
                                    tasks.remove(task); // Remove the task from the list
                                }
                                return random.nextLong(1500, 3000);
                            }
                        } else {
                            ScriptConsole.println("Item is null or ActionBar does not contain 'Disassemble' ability.");
                            shutdown();
                        }
                    } else {
                        ScriptConsole.println("Backpack does not contain the item: " + itemName);
                        shutdown();
                    }
                }
            }
        }
        ScriptConsole.println("Returning from Dissasemble method...");
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
                        ScriptConsole.println("Backpack contains the item: " + itemName);
                        Item item = Backpack.getItem(itemName);
                        if (item != null && ActionBar.containsAbility("High Level Alchemy")) {
                            ScriptConsole.println("Item is not null and ActionBar contains 'High Level Alchemy' ability, interacting with MiniMenu...");
                            Component Alchemy = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673).spriteId(14379).option("Customise-keybind").results().first();
                            if (Alchemy != null) {
                                ScriptConsole.println("Selected High Level Alchemy: " + MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, Alchemy.getInterfaceIndex() << 16 | Alchemy.getComponentIndex()));
                                Execution.delay(random.nextLong(505, 650));
                                ScriptConsole.println("Selected item: " + MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, item.getSlot(), 96534533));
                                if (Interfaces.isOpen(847)) {
                                    ScriptConsole.println("Valuable item warning detected, but setting not enabled in the UI. Removing task.");
                                    return random.nextLong(1500, 3000);
                                }
                                if (task.getAmountDisassembled() >= itemMenuSize) {
                                    tasks.remove(task);
                                }
                                return random.nextLong(1500, 3000);
                            }
                        } else {
                            ScriptConsole.println("Item is null or ActionBar does not contain 'High Level Alchemy' ability.");
                            shutdown();
                        }
                    } else {
                        ScriptConsole.println("Backpack does not contain the item: " + itemName);
                        shutdown();
                    }
                }
            }
        }
        ScriptConsole.println("Returning from Dissasemble method...");
        shutdown();
        return random.nextLong(1250, 2500);
    }
}
