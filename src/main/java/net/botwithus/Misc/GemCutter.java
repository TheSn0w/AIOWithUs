package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;

import java.util.Random;

import static net.botwithus.CustomLogger.log;

public class GemCutter {
    SnowsScript script;
    private static Random random = new Random();

    public GemCutter(SnowsScript script) {
        this.script = script;
    }

    public static long cutGems() {
        ResultSet<Item> craftingItems = InventoryItemQuery.newQuery(93).option("Craft").results();
        Item craftitems = craftingItems.isEmpty() ? null : craftingItems.first();
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(700, 980);
        }
        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            log("[Gem Cutter] Selecting 'Craft'");
            return random.nextLong(750, 1050);
        }
        if (Backpack.containsItemByCategory(5289)) {
            if (craftitems != null) {
                log("[Gem Cutter] Crafting " + craftitems.getName());
                backpack.interact(craftitems.getName(), "Craft");
                return random.nextLong(750, 1050);
            }
        } else {
            EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Load Last Preset from").results();
            if (!results.isEmpty()) {
                log("[Gem Cutter] Loading last preset from banker");
                results.nearest().interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            } else {
                EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery().name("Bank chest").option("Load Last Preset from").results();
                if (!chestResults.isEmpty()) {
                    log("[Gem Cutter] Loading last preset from bank chest");
                    chestResults.nearest().interact("Load Last Preset from");
                    return random.nextLong(750, 1050);
                }
            }
        }
        return random.nextLong(750,1250);
    }
}
