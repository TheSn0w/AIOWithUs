package net.botwithus.Misc;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;

import java.util.Random;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.component;
import static net.botwithus.Variables.Variables.random;

public class Smelter {

    public static boolean handleGoldBar;
    public static boolean handleGoldGauntlets;

    public static int logValue = VarManager.getVarValue(VarDomainType.PLAYER, 8336);


    public static long handleSmelter(LocalPlayer player) {
        EntityResultSet<SceneObject> furnace = SceneObjectQuery.newQuery().name("Furnace").option("Smelt").results();
        if (Interfaces.isOpen(1251) || player.isMoving()) {
            return random.nextLong(700, 980);
        }
        if (Interfaces.isOpen(37)) {
            if (logValue == 0) {
                shutdown();
            } else if (logValue >= 1) {
                component(1, -1, 2424995);
                log("[Smelter] Selecting 'Craft'");
                return random.nextLong(750, 1050);
            }
        }
        if (Backpack.containsItemByCategory(5290) || Backpack.contains("Enchanted gem")) {
            log("[Smelter] Backpack contains item by category 5290 or Enchanted gem.");
            furnace.nearest().interact("Smelt");
        } else {
            log("[Error] Backpack does not contain item by category 5290 or Enchanted gem.");
            EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Load Last Preset from").results();
            if (!results.isEmpty()) {
                log("[Smelter] Interacting with Banker.");
                results.nearest().interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            } else {
                log("[Error] Banker not found.");
                EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery().name("Bank chest").option("Load Last Preset from").results();
                if (!chestResults.isEmpty()) {
                    log("[Smelter] Interacting with Bank chest.");
                    chestResults.nearest().interact("Load Last Preset from");
                    return random.nextLong(750, 1050);
                } else {
                    log("[Error] Bank chest not found.");
                }
            }
        }
        return random.nextLong(750, 1050);
    }
    public static long smeltGold(LocalPlayer player) {
        EntityResultSet<SceneObject> furnace = SceneObjectQuery.newQuery().name("Furnace").option("Smelt").results();
        if (Interfaces.isOpen(1251) || player.isMoving()) {
            return random.nextLong(700, 980);
        }
        if (Interfaces.isOpen(37)) {
            if (logValue == 0) {
                shutdown();
            } else if (logValue >= 1) {
                component(1, -1, 2424995);
                log("[Smelter] Selecting 'Craft'");
                return random.nextLong(750, 1050);
            }
        }
        if (Backpack.contains("Gold ore")) {
            log("[Smelter] Backpack contains Gold ore.");
            furnace.nearest().interact("Smelt");
        } else {
            log("[Error] Backpack does not contain Gold ore.");
            EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Load Last Preset from").results();
            if (!results.isEmpty()) {
                log("[Smelter] Interacting with Banker.");
                results.nearest().interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            } else {
                log("[Error] Banker not found.");
                EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery().name("Bank chest").option("Load Last Preset from").results();
                if (!chestResults.isEmpty()) {
                    log("[Smelter] Interacting with Bank chest.");
                    chestResults.nearest().interact("Load Last Preset from");
                    return random.nextLong(750, 1050);
                } else {
                    log("[Error] Bank chest not found.");
                }
            }
        }
        return random.nextLong(750, 1050);
    }
    public static long smeltGoldGauntlets(LocalPlayer player) {
        EntityResultSet<SceneObject> furnace = SceneObjectQuery.newQuery().name("Furnace").option("Smelt").results();
        if (Interfaces.isOpen(1251) || player.isMoving()) {
            return random.nextLong(700, 980);
        }
        if (Interfaces.isOpen(37)) {
            if (logValue == 0) {
                shutdown();
            } else if (logValue >= 1) {
                component(1, -1, 2424995);
                log("[Smelter] Selecting 'Craft'");
                return random.nextLong(750, 1050);
            }
        }
        furnace.nearest().interact("Smelt");
        return random.nextLong(750, 1050);
    }
}
