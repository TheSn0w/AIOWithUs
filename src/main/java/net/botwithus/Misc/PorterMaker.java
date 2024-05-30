package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.Random;

import static net.botwithus.Variables.Variables.isportermakerActive;
import static net.botwithus.Variables.Variables.portersMade;


public class PorterMaker {
    SnowsScript script;
    private static Random random = new Random();

    public PorterMaker(SnowsScript script) {
        this.script = script;
    }



    public static long makePorters() {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1250, 2500);
        }
        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            ScriptConsole.println("Selecting 'Weave'");
            return random.nextLong(1250, 1500);
        }
        if (Backpack.contains("Dragonstone necklace")) {
            Backpack.interact("Incandescent energy", "Weave");
            return random.nextLong(1250, 2500);
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
    public static long divineCharges() {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1250, 2500);
        }
        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            ScriptConsole.println("Selecting 'Weave'");
            return random.nextLong(1250, 1500);
        }
        if (Backpack.contains("Incandescent energy")) {
            Backpack.interact("Incandescent energy", "Weave");
            ScriptConsole.println("Weaving Incandescent energy");
        }
        return random.nextLong(750, 1050);
    }
}
