package net.botwithus.Misc;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.Random;

import static net.botwithus.Variables.Variables.*;

public class Planks {
    private static final Random random = new Random();

    public static long handleplankmaking(LocalPlayer player) {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1250, 2500);
        }
        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            ScriptConsole.println("Selecting 'Craft'");
            return random.nextLong(1250, 1500);
        }
        if (makePlanks) {
            Execution.delay(handlePlanks());
        }
        if (makeFrames) {
            Execution.delay(handleFrames());
        }
        if (makeRefinedPlanks) {
            Execution.delay(handleRefinedPlanks());
        }
        return random.nextLong(750, 1250);
    }

    public static long handlePlanks() {
        SceneObject bankChest = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results().nearest();
        SceneObject sawmill = SceneObjectQuery.newQuery().name("Sawmill").option("Process planks").results().first();

        if (sawmill != null && Backpack.containsItemByCategory(22)) {
            sawmill.interact("Process planks");
            ScriptConsole.println("Interacting with Sawmill");
            return random.nextLong(750, 1250);
        }

        if (!Backpack.containsItemByCategory(22)) {
            if (bankChest != null) {
                bankChest.interact("Load Last Preset from");
                ScriptConsole.println("Interacting with Bank Chest");
                return random.nextLong(750, 1250);
            }
        }

        return random.nextLong(750, 1250);
    }

    public static long handleRefinedPlanks() {
        SceneObject bankChest = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results().nearest();

        SceneObject sawmill = SceneObjectQuery.newQuery().name("Sawmill").option("Process planks").results().first();

        if (sawmill != null && Backpack.getItems().stream().anyMatch(item ->
                item.getName().toLowerCase().contains("plank") && !item.getName().toLowerCase().contains("refined"))) {
            sawmill.interact("Process planks");
            ScriptConsole.println("Interacting with Sawmill");
            return random.nextLong(750, 1250);
        }

        if (Backpack.getItems().stream().noneMatch(item ->
                item.getName().toLowerCase().contains("plank") && !item.getName().toLowerCase().contains("refined"))) {
            if (bankChest != null) {
                bankChest.interact("Load Last Preset from");
                ScriptConsole.println("Interacting with Bank Chest");
                return random.nextLong(750, 1250);
            }
        }

        return random.nextLong(750, 1250);
    }

    public static long handleFrames() {
        SceneObject bankChest = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results().nearest();
        SceneObject woodworkingBench = SceneObjectQuery.newQuery().name("Woodworking bench").option("Construct frames").results().first();

        if (woodworkingBench != null && Backpack.getItems().stream().anyMatch(item ->
                item.getName().toLowerCase().contains("plank"))) {
            woodworkingBench.interact("Construct frames");
            ScriptConsole.println("Interacting with Woodworking Bench");
            return random.nextLong(750, 1250);
        }

        if (Backpack.getItems().stream().noneMatch(item ->
                item.getName().toLowerCase().contains("plank"))) {
            if (bankChest != null) {
                bankChest.interact("Load Last Preset from");
                ScriptConsole.println("Interacting with Bank Chest");
                return random.nextLong(750, 1250);
            }
        }

        return random.nextLong(750, 1250);
    }
}