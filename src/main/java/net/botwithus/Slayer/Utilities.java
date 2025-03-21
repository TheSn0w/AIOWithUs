package net.botwithus.Slayer;

import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.vars.VarManager;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Utilities {


    public static void DeActivateMagicPrayer() {
        if (getLocalPlayer() != null) {
            if (VarManager.getVarbitValue(16768) != 0) {
                ActionBar.useAbility("Deflect Magic");
                log("Magic prayer deactivated!");
            }
        }
    }

    public static void DeActivateRangedPrayer() {
        if (getLocalPlayer() != null) {
            if (VarManager.getVarbitValue(16769) != 0) {
                ActionBar.useAbility("Deflect Ranged");
                log("Ranged prayer deactivated!");
            }
        }
    }

    public static void DeActivateMeleePrayer() {
        if (getLocalPlayer() != null) {
            if (VarManager.getVarbitValue(16770) != 0) {
                ActionBar.useAbility("Deflect Melee");
                log("Melee prayer deactivated!");
            }
        }
    }

    public static void DeHandleSoulSplit() {
        if (getLocalPlayer() != null) {
            if (VarManager.getVarbitValue(16779) != 0) {
                ActionBar.useAbility("Soul Split");
                log("Soul Split deactivated!");
            }
        }
    }


    public static void ActivateMagicPrayer() {
        if (getLocalPlayer() != null) {
            if (VarManager.getVarbitValue(16768) != 1) {
                ActionBar.useAbility("Deflect Magic");
                log("Magic prayer activated!");
            }
        }
    }

    public static void ActivateRangedPrayer() {
        if (getLocalPlayer() != null) {
            if (VarManager.getVarbitValue(16769) != 1) {
                ActionBar.useAbility("Deflect Ranged");
                log("Ranged prayer activated!");
            }
        }
    }

    public static void ActivateMeleePrayer() {
        if (getLocalPlayer() != null) {
            if (VarManager.getVarbitValue(16770) != 1) {
                ActionBar.useAbility("Deflect Melee");
                log("Melee prayer activated!");
            }
        }
    }

    public static void ActivateSoulSplit() {
        if (getLocalPlayer() != null) {
            if (VarManager.getVarbitValue(16779) != 1) {
                ActionBar.useAbility("Soul Split");
                log("Soul Split activated!");
            }
        }
    }
}
