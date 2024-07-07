package net.botwithus.Combat;

import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.Combat.RipperDemon.player;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.SoulSplit;
import static net.botwithus.Variables.Variables.random;

public class Prayers {

    public static void activateSoulSplit(LocalPlayer player) {
        if (VarManager.getVarbitValue(16779) == 0 && player.getPrayerPoints() > 10) {
            boolean success = ActionBar.useAbility("Soul Split");
            if (success) {
                log("[Combat] Activating Soul Split.");
                Execution.delay(random.nextLong(600));
            } else {
                log("[Error] Failed to activate Soul Split.");
            }
        }
    }

    public static void deactivateSoulSplit() {
        boolean isSoulSplitActive = VarManager.getVarbitValue(16779) == 1;
        if (isSoulSplitActive) {
            boolean success = ActionBar.useAbility("Soul Split");
            if (success) {
                log("[Combat] Deactivating Soul Split.");
                Execution.delayUntil( random.nextLong(2000, 3000), () -> VarManager.getVarbitValue(16779) == 0);
            } else {
                log("[Error] Failed to deactivate Soul Split.");
            }
        }
    }


    public static void updateQuickPrayersActiveStatus() {
        int[] varbitIds = {
                // Curses
                16761, 16762, 16763, 16786, 16764, 16765, 16787, 16788, 16765, 16766,
                16767, 16768, 16769, 16770, 16771, 16772, 16781, 16773, 16782, 16774,
                16775, 16776, 16777, 16778, 16779, 16780, 16784, 16783, 29065, 29066,
                29067, 29068, 29069, 49330, 29071, 34866, 34867, 34868, 53275, 53276,
                53277, 53278, 53279, 53280, 53281,
                // Normal
                16739, 16740, 16741, 16742, 16743, 16744, 16745, 16746, 16747, 16748,
                16749, 16750, 16751, 16752, 16753, 16754, 16755, 16756, 16757, 16758,
                16759, 16760, 53271, 53272, 53273, 53274
        };

        quickPrayersActive = false;
        for (int varbitId : varbitIds) {
            if (VarManager.getVarbitValue(varbitId) == 1) {
                quickPrayersActive = true;
                break;
            }
        }
    }

    public static boolean quickPrayersActive = false;


    public static void activateQuickPrayers() {
        if (!quickPrayersActive) {
            if (ActionBar.useAbility("Quick-prayers 1") && player.getPrayerPoints() > 10) {
                log("[Combat] Activating Quick Prayers.");
                quickPrayersActive = true;
                Execution.delay(random.nextLong(1500, 2000));
            } else {
                log("[Error] Failed to activate Quick Prayers.");
            }
        }
    }

    public static void deactivateQuickPrayers() {
        if (quickPrayersActive) {
            log("[Combat] Deactivating Quick Prayers.");
            if (ActionBar.useAbility("Quick-prayers 1")) {
                log("[Combat] Quick Prayers deactivated.");
                quickPrayersActive = false;
                Execution.delay(random.nextLong(1500, 2000));
            } else {
                log("[Error] Failed to deactivate Quick Prayers.");
            }
        }
    }

}
