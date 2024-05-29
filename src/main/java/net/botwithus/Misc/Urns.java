package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.Dialog;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.Random;

public class Urns {
    SnowsScript script;
    private static Random random = new Random();
    public static boolean makeUrn = true;

    public Urns(SnowsScript script) {
        this.script = script;
    }

    private long useclayCrafting() {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Portable crafter").option("Clay Crafting").results();
        EntityResultSet<SceneObject> bankChest = SceneObjectQuery.newQuery().name("Bank chest").option("Bank").results();

        if (Interfaces.isOpen(1251)) {
            return random.nextLong(100, 200);
        }

        if (Interfaces.isOpen(1188) && makeUrn) {
            return handleFormClayInterface();
        }
        if (Interfaces.isOpen(1188) && !makeUrn) {
            return handleFireClayInterface();
        }

        if (Interfaces.isOpen(1370)) {
            return handleCreateAllInterface();
        }

        if (results.isEmpty() && Backpack.contains("Portable crafter")) {
            return deployPortableCrafter();
        }

        if (makeUrn) {
            if (Backpack.contains("Soft clay")) {
                Execution.delay(interactWithPortable(results));
            } else {
                Execution.delay(loadLastPreset(bankChest));
            }
        } else {
            if (Backpack.contains("Decorated woodcutting urn (unf)")) {
                interactWithPortable(results);
            } else {
                loadLastPreset(bankChest);
            }
        }
        return 0;
    }

    private long handleFormClayInterface() {
        Dialog.interact("Form Clay");
        ScriptConsole.println("Interacting with Form Clay");
        return random.nextLong(400, 600);
    }

    private long handleCreateAllInterface() {
        MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
        ScriptConsole.println("Interacting with Create All");
        return random.nextLong(400, 600);
    }

    private long interactWithPortable(EntityResultSet<SceneObject> results) {
        results.nearest().interact("Clay Crafting");
        ScriptConsole.println("Interacting with Portable");
        return random.nextLong(400, 600);
    }

    private long loadLastPreset(EntityResultSet<SceneObject> bankChest) {
        bankChest.nearest().interact("Load Last Preset from");
        ScriptConsole.println("Loading Last Preset");
        return random.nextLong(400, 600);
    }

    private long deployPortableCrafter() {
        Backpack.interact("Portable crafter", "Deploy");
        ScriptConsole.println("Deploying Portable Crafter");
        Execution.delay(random.nextLong(500, 700));
        if (Interfaces.isOpen(1188)) {
            Execution.delay(random.nextLong(500, 700));
            ScriptConsole.println("Options" + Dialog.getOptions());
            Dialog.interact("Yes.");
            return random.nextLong(400, 600);
        }
        return 0;
    }
    private long handleFireClayInterface() {
        Dialog.interact("Fire Clay");
        ScriptConsole.println("Interacting with Fire Clay");
        return random.nextLong(400, 600);
    }
}
