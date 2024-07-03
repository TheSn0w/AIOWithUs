package net.botwithus.Archaeology;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.inventory.backpack;
import net.botwithus.inventory.equipment;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import static net.botwithus.Archaeology.Banking.BankforArcheology;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.getEquipChargeThreshold;
import static net.botwithus.inventory.equipment.Slot.NECK;

public class Porters {

    public static void useGrace() {
        if (useGote) {
            usePorter();
        }
    }

    public static void usePorter() {
        String currentPorter = porterTypes[currentPorterType.get()];

        if (Backpack.contains(currentPorter)) {
            if (equipment.contains("Grace of the elves")) {
                int varbitValue = VarManager.getInvVarbit(94, 2, 30214);
                if (varbitValue <= getEquipChargeThreshold()) {
                    log("[Archaeology] Porters have " + varbitValue + " charges. Charging.");
                    log("[Archaeology] Interacting with Equipment - Equipment needs to be OPEN.");
                    boolean interactionResult = equipment.interact(NECK, "Charge all porters");
                    if (interactionResult) {
                        log("[Archaeology] Interaction with Equipment was successful.");
                    } else {
                        log("[Error] Interaction with Equipment failed.");
                    }
                }
            } else {
                if (ComponentQuery.newQuery(284).spriteId(51490).results().isEmpty()) {
                    boolean interactionResult = backpack.interact(currentPorter, "Wear");
                    if (interactionResult) {
                        log("[Archaeology] Interaction with Backpack was successful.");
                    } else {
                        log("[Error] Interaction with Backpack failed.");
                    }
                }
            }
            Execution.delay(RandomGenerator.nextInt(1500, 3000));
        } else {
            if (ComponentQuery.newQuery(284).spriteId(51490).results().isEmpty() && bankwithoutPorter) {
                log("[Archaeology] No porters found in backpack, and weve run out, Banking.");
                setBotState(BANKING);
            }
        }
    }

    public static boolean bankwithoutPorter = false;

    public static long handleGoteCharges() {
        if (Equipment.contains("Grace of the elves")) {
            int charges = VarManager.getInvVarbit(94, 2, 30214);
            if (charges < getChargeThreshold()) {
                String selectedPorter = porterTypes[currentPorterType.get()];
                int quantity = getQuantityFromOption(quantities[currentQuantity.get()]);
                boolean withdrew;
                if (VarManager.getVarbitValue(45189) != 7) {
                    component(1, -1, 33882215);
                }
                withdrew = Bank.withdraw(selectedPorter, quantity);
                Execution.delay(random.nextLong(1500, 3000));
                if (withdrew && !InventoryItemQuery.newQuery(93).name(selectedPorter).results().isEmpty()) {
                    log("[Archaeology] Withdrew: " + selectedPorter + ".");
                } else {
                    log("[Error] Failed to withdraw " + selectedPorter + ".");
                    log("[Caution] use Gote/Porter has been disabled.");
                    useGote = false;
                    return random.nextLong(1500, 3000);
                }
            }
        } else {
            String selectedPorter = porterTypes[currentPorterType.get()];
            int quantity = getQuantityFromOption(quantities[currentQuantity.get()]);
            boolean withdrew;
            if (VarManager.getVarbitValue(45189) != 7) {
                component(1, -1, 33882215);
            }
            withdrew = Bank.withdraw(selectedPorter, quantity);
            Execution.delay(random.nextLong(1500, 3000));
            if (withdrew && !InventoryItemQuery.newQuery(93).name(selectedPorter).results().isEmpty()) {
                log("[Archaeology] Withdrew: " + selectedPorter + ".");
            } else {
                log("[Error] Failed to withdraw " + selectedPorter + ".");
                log("[Caution] use Gote/Porter has been disabled.");
                useGote = false;
                return random.nextLong(1500, 3000);
            }
        }
        return random.nextLong(1500, 2500);
    }


    public static int getQuantityFromOption(String option) {
        return switch (option) {
            case "ALL" -> 1;
            case "1" -> 2;
            case "5" -> 3;
            case "10" -> 4;
            case "Preset amount" -> 5;
            default -> 0;
        };
    }
}