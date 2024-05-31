package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;

import java.util.List;
import java.util.Random;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.component;
import static net.botwithus.Variables.Variables.dialog;

public class SiftSoil {
    SnowsScript script;
    private static Random random = new Random();

    public SiftSoil(SnowsScript script) {
        this.script = script;
    }

    public static long handleSoil(LocalPlayer player) {
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
            log("[Error] Backpack does not contain the correct item.");
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
}
