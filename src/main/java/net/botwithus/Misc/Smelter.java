package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.Random;

public class Smelter {
    SnowsScript script;
    private static Random random = new Random();

    public Smelter(SnowsScript script) {
        this.script = script;
    }

    public static long handleSmelter(LocalPlayer player) {
        EntityResultSet<SceneObject> furnace = SceneObjectQuery.newQuery().name("Furnace").option("Smelt").results();
        if (Interfaces.isOpen(1251) || player.isMoving()) {
            return random.nextLong(700, 980);
        }
        if (Interfaces.isOpen(37)) {
            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 2424995);
            ScriptConsole.println("Selecting 'Craft'");
            return random.nextLong(750, 1050);
        }
        if (Backpack.containsItemByCategory(5290) || Backpack.contains("Enchanted gem")) {
            furnace.nearest().interact("Smelt");
        } else {
            EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Load Last Preset from").results();
            if (!results.isEmpty()) {
                results.nearest().interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            } else {
                EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery().name("Bank chest").option("Load Last Preset from").results();
                if (!chestResults.isEmpty()) {
                    chestResults.nearest().interact("Load Last Preset from");
                    return random.nextLong(750, 1050);
                }
            }
        }
        return random.nextLong(750, 1050);
    }
}
