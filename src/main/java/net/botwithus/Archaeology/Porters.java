package net.botwithus.Archaeology;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.inventory.backpack;
import net.botwithus.inventory.equipment;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.getGraceChargesThreshold;
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
                int CurrentGraceCharges = VarManager.getInvVarbit(94, 2, 30214);
                if (CurrentGraceCharges <= getGraceChargesThreshold()) {
                    log("[Archaeology] Porters have " + CurrentGraceCharges + " charges. Charging.");
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
            Execution.delay(RandomGenerator.nextInt(500, 600));
        } else {
            if (Equipment.contains("Grace of the elves")) {
                int CurrentGraceCharges = VarManager.getInvVarbit(94, 2, 30214);
                if (CurrentGraceCharges <= getGraceChargesThreshold()) {
                    log("[Archaeology] No porters found in backpack, and we are below our Equip charge theshold.");
                    setBotState(BANKING);
                }
            } else {
                if(ComponentQuery.newQuery(284).spriteId(51490).results().isEmpty()) {
                    log("[Archaeology] No porters found in backpack, and weve run out, Banking.");
                    setBotState(BANKING);
                }
            }
        }
    }

    public static boolean bankwithoutPorter = false;

    public static long handleGoteCharges() {
        if (Equipment.contains("Grace of the elves")) {
            int CurrentGraceCharges = VarManager.getInvVarbit(94, 2, 30214);
            if (CurrentGraceCharges < getBankingThreshold()) {
                String selectedPorter = porterTypes[currentPorterType.get()];
                int quantity = getQuantityFromOption(quantities[currentQuantity.get()]);
                boolean withdrew;
                if (VarManager.getVarbitValue(45189) != 7) {
                    component(1, -1, 33882215);
                }
                withdrew = Bank.withdraw(selectedPorter, quantity);
                Execution.delay(random.nextLong(500, 600));
                if (withdrew && !InventoryItemQuery.newQuery(93).name(selectedPorter).results().isEmpty()) {
                    log("[Archaeology] Withdrew: " + selectedPorter + ".");
                } else {
                    log("[Error] Failed to withdraw " + selectedPorter + ".");
                    log("[Caution] use Gote/Porter has been disabled.");
                    useGote = false;
                    return random.nextLong(500, 600);
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
            Execution.delay(random.nextLong(500, 600));
            if (withdrew && !InventoryItemQuery.newQuery(93).name(selectedPorter).results().isEmpty()) {
                log("[Archaeology] Withdrew: " + selectedPorter + ".");
            } else {
                log("[Error] Failed to withdraw " + selectedPorter + ".");
                log("[Caution] use Gote/Porter has been disabled.");
                useGote = false;
                return random.nextLong(500, 600);
            }
        }
        return random.nextLong(500, 600);
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