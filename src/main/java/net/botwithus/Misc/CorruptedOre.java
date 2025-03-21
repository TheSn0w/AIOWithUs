package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.SnowsScript;
import net.botwithus.Variables.Variables;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.Random;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;

public class CorruptedOre {


    public static long mineCorruptedOre() {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Furnace").option("Smelt").results();
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1250, 2500);
        }
        if (Interfaces.isOpen(37)) {
            component(1, -1, 2424995);
            log("[Corrupted Ore] Selecting 'Smelt'");
            return random.nextLong(1500, 3000);
        }
        if (!results.isEmpty()) {
            SceneObject corruptedOre = results.nearest();
            if (corruptedOre != null) {
                corruptedOre.interact("Smelt");
                log("[Corrupted Ore] Interacting with Furnace");
                return random.nextLong(1250, 2500);
            }
        }
        return random.nextLong(1250, 2500);
    }
}
