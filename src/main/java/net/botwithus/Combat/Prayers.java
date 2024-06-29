package net.botwithus.Combat;

import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.random;

public class Prayers {

    public static long manageSoulSplit(LocalPlayer player) {
        if (player == null) {
            return 0;
        }
        if (!ActionBar.containsAbility("Soul Split")) {
            return 0;
        }

        boolean isSoulSplitActive = VarManager.getVarbitValue(16779) == 1;

        if (player.inCombat()) {
            if (!isSoulSplitActive && player.getPrayerPoints() > 1) {
                boolean success = ActionBar.useAbility("Soul Split");
                if (success) {
                    log("[Combat] Activating Soul Split.");
                    return random.nextLong(600, 1500);
                } else {
                    log("[Error] Failed to activate Soul Split.");
                    return 0;
                }
            }
        } else {
            if (isSoulSplitActive) {
                boolean success = ActionBar.useAbility("Soul Split");
                if (success) {
                    log("[Combat] Deactivating Soul Split.");
                    return random.nextLong(600, 1500);
                } else {
                    log("[Error] Failed to deactivate Soul Split.");
                    return 0;
                }
            }
        }

        return 0L;
    }
    private static boolean isQuickPrayersActive() {
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

        for (int varbitId : varbitIds) {
            if (VarManager.getVarbitValue(varbitId) == 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean quickPrayersActive = false;

    public static void manageQuickPrayers(LocalPlayer player) {

        if (player.inCombat() && !quickPrayersActive) {
            updateQuickPrayersActivation(player);
        } else if (!player.inCombat() && quickPrayersActive) {
            updateQuickPrayersActivation(player);
        }
    }

    private static void updateQuickPrayersActivation(LocalPlayer player) {
        boolean isCurrentlyActive = isQuickPrayersActive();
        boolean shouldBeActive = shouldActivateQuickPrayers(player);

        if (shouldBeActive && !isCurrentlyActive) {
            activateQuickPrayers();
        } else if (!shouldBeActive && isCurrentlyActive) {
            deactivateQuickPrayers();
        }
    }

    private static void activateQuickPrayers() {
        if (!quickPrayersActive) {
            log("[Combat] Activating Quick Prayers.");
            if (ActionBar.useAbility("Quick-prayers 1")) {
                log("[Combat] Quick Prayers activated successfully.");
                quickPrayersActive = true;
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
            } else {
                log("[Error] Failed to deactivate Quick Prayers.");
            }
        }
    }

    private static boolean shouldActivateQuickPrayers(LocalPlayer player) {
        return player.inCombat();
    }
}
