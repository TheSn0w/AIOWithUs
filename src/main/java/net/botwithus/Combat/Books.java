package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.regex.Pattern;

import static net.botwithus.Combat.Abilities.manageAnimateDead;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Books {

    public static void manageScripturesAndScrimshaws(LocalPlayer player) {
        if (scriptureofJas) {
            manageScriptureOfJas();
        }
        if (scriptureofWen) {
            manageScriptureOfWen();
        }
        if (animateDead) {
            manageAnimateDead(player);
        }
        if (useScrimshaws) {
            manageScrimshaws(player);
        }
    }

    public static void manageScriptureOfJas() {
        if (getLocalPlayer() != null) {
            if (getLocalPlayer().inCombat()) {
                Execution.delay(activateScriptureOfJas());
            } else {
                Execution.delay(deactivateScriptureOfJas());
            }
        }
    }

    public static long activateScriptureOfJas() {
        if (VarManager.getVarbitValue(30605) == 0 && VarManager.getVarbitValue(30604) >= 60) {
            log("[Combat] Activated Scripture of Jas:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            return random.nextLong(1500, 3000);
        }
        return 0L;
    }

    public static long deactivateScriptureOfJas() {
        if (VarManager.getVarbitValue(30605) == 1) {
            log("[Combat] Deactivated Scripture of Jas:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            return random.nextLong(1500, 3000);
        }
        return 0L;
    }

    public static void manageScriptureOfWen() {
        if (getLocalPlayer() != null) {
            if (getLocalPlayer().inCombat()) {
                Execution.delay(activateScriptureOfWen());
            } else {
                Execution.delay(deactivateScriptureOfWen());
            }
        }
    }

    public static long activateScriptureOfWen() {
        if (VarManager.getVarbitValue(30605) == 0 && VarManager.getVarbitValue(30604) >= 60) {
            log("[Combat] Activated Scripture of Wen:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            return random.nextLong(1500, 3000);
        }
        return 0L;
    }

    public static long deactivateScriptureOfWen() {
        if (VarManager.getVarbitValue(30605) == 1) {
            log("[Combat] Deactivated Scripture of Wen:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            return random.nextLong(1500, 3000);
        }
        return 0L;
    }
    static void manageScrimshaws(LocalPlayer player) {
        Pattern scrimshawPattern = Pattern.compile("scrimshaw", Pattern.CASE_INSENSITIVE);
        Item Scrimshaw = InventoryItemQuery.newQuery(94).name(scrimshawPattern).results().first();

        if (Scrimshaw != null) {
            if (player.inCombat()) {
                Execution.delay(activateScrimshaws());
            } else {
                Execution.delay(deactivateScrimshaws());
            }
        } else {
            log("[Error] Pocket slot does not contain a scrimshaw.");
        }
    }

    private static long activateScrimshaws() {
        Pattern scrimshawPattern = Pattern.compile("scrimshaw", Pattern.CASE_INSENSITIVE);
        Item Scrimshaw = InventoryItemQuery.newQuery(94).name(scrimshawPattern).results().first();
        if (Scrimshaw != null && VarManager.getInvVarbit(Scrimshaw.getInventoryType().getId(), Scrimshaw.getSlot(), 17232) == 0) {
            log("[Combat] Activating Scrimshaws.");
            Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate");
            return RandomGenerator.nextInt(1500, 3000);
        }
        return 0L;
    }

    private static long deactivateScrimshaws() {
        Pattern scrimshawPattern = Pattern.compile("scrimshaw", Pattern.CASE_INSENSITIVE);
        Item Scrimshaw = InventoryItemQuery.newQuery(94).name(scrimshawPattern).results().first();
        if (Scrimshaw != null && VarManager.getInvVarbit(Scrimshaw.getInventoryType().getId(), Scrimshaw.getSlot(), 17232) == 1) {
            log("[Combat] Deactivating Scrimshaws.");
            Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate");
            return RandomGenerator.nextInt(1500, 3000);
        }
        return 0L;
    }
}
