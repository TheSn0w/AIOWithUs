package net.botwithus.Archaeology;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.hiSpecMonocle;

public class Buffs {

    public static void useBuffs() {
        if (archaeologistsTea || materialManual || hiSpecMonocle) {
            if (archaeologistsTea) {
                useArchaeologistsTea();
            }
            if (materialManual) {
                useMaterialManual();
            }
            if (hiSpecMonocle) {
                useHiSpecMonocle();
            }
        }
    }

    public static void useArchaeologistsTea() { // 47027 must be material + 1
        if (VarManager.getVarbitValue(47028) == 0) {
            if (net.botwithus.api.game.hud.inventories.Backpack.contains("Archaeologist's tea")) {
                backpack.interact("Archaeologist's tea", "Drink");
                Execution.delay(RandomGenerator.nextInt(1500, 3000));
            }
        }
    }
    public static void useHiSpecMonocle() {
        if (VarManager.getVarbitValue(47026) == 0) {
            if (net.botwithus.api.game.hud.inventories.Backpack.contains("Hi-spec monocle")) {
                backpack.interact("Hi-spec monocle", "Wear");
                Execution.delay(RandomGenerator.nextInt(1500, 3000));
            }
        }
    }
    public static void useMaterialManual() {
        if (VarManager.getVarbitValue(47025) == 0) {
            if (Backpack.contains("Material manual")) {
                backpack.interact("Material manual", "Read");
                Execution.delay(RandomGenerator.nextInt(1500, 3000));
            }
        }
    }
}
