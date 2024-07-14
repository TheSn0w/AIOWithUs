package net.botwithus.Misc;

import net.botwithus.Runecrafting.SteamRunes;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;

import java.util.List;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;

public class SiftSoil {

    public static boolean useSiftSoilSpell = false;



    public static long handleSoil(LocalPlayer player) {
        if (useSiftSoilSpell) {
            return siftSoilSpell();
        }
        EntityResultSet<SceneObject> mesh = SceneObjectQuery.newQuery().name("Mesh").option("Screen").results();
        if (Interfaces.isOpen(1251) || player.isMoving()) {
            return random.nextLong(700, 980);
        }
        if (Interfaces.isOpen(1370)) {
            dialog(0, -1, 89784350);
            log("[Sift Soil] Selecting 'Screen");
            return random.nextLong(750, 1050);
        }
        if (Backpack.containsItemByCategory(4603)) {
                int count = 0;
                String itemName = "";
                List<Item> items = Backpack.getItems();
                for (Item item : items) {
                    if (item.getConfigType().getCategory() == 4603) {
                        count++;
                        itemName = item.getName();
                    }
                }
                log("[Sift Soil] Backpack contains: " + count + " " + itemName);
                mesh.random().interact("Screen");
                log("[Sift Soil] Interacting with Mesh.");
                return random.nextLong(750, 1050);
        } else {
            EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery().name("Bank chest").option("Load Last Preset from").results();
            if (!chestResults.isEmpty()) {
                log("[Sift Soil] Interacting with Bank chest.");
                chestResults.nearest().interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            } else {
                log("[Error] Bank chest not found.");
                return random.nextLong(750, 1050);
            }
        }
    }

    public static long siftSoilSpell() {
        if (Interfaces.isOpen(1251) || player.isMoving()) {
            return random.nextLong(700, 980);
        }
        if (Interfaces.isOpen(1370)) {
            dialog(0, -1, 89784350);
            log("[Sift Soil] Selecting 'Screen");
            return random.nextLong(750, 1050);
        }

        int astralRuneQuantity = SteamRunes.Rune.ASTRAL.getQuantity();
        boolean sufficientAstralRunes = astralRuneQuantity > 1;

        if (Backpack.containsItemByCategory(4603)) {
            if (sufficientAstralRunes) {
                int count = 0;
                String itemName = "";
                List<Item> items = Backpack.getItems();
                for (Item item : items) {
                    if (item.getConfigType().getCategory() == 4603) {
                        count++;
                        itemName = item.getName();
                    }
                }
                log("[Sift Soil] Backpack contains: " + count + " " + itemName);
                ActionBar.useAbility("Sift Soil");
                log("[Sift Soil] Interacting with Ability.");
                return random.nextLong(750, 1050);
            } else {
                log("[Sift Soil] Not enough Astral runes.");
                shutdown();
            }
        } else {
            EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Load Last Preset from").results();
            if (!results.isEmpty()) {
                log("[Sift Soil] Interacting with Banker.");
                results.nearest().interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            } else {
                log("[Caution] Banker not found.");
                EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery().name("Bank chest").option("Load Last Preset from").results();
                if (!chestResults.isEmpty()) {
                    log("[Sift Soil] Interacting with Bank chest.");
                    chestResults.nearest().interact("Load Last Preset from");
                    return random.nextLong(750, 1050);
                } else {
                    log("[Error] Bank chest not found.");
                }
            }
        }
        return 0;
    }


    public enum Rune {
        AIR(5886),
        WATER(5887),
        EARTH(5889),
        FIRE(5888),
        MIND(5902),
        BODY(5896),
        COSMIC(5897),
        CHAOS(5898),
        NATURE(5899),
        LAW(5900),
        ASTRAL(5903),
        DEATH(5901),
        BLOOD(5904),
        SOUL(5905);

        private final int index;

        Rune(int index) {
            this.index = index;
        }

        public int getQuantity() {
            return VarManager.getVarc(index);
        }
    }
}
