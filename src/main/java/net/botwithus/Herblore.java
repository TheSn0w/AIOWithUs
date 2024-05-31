package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.Random;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.portersMade;

public class Herblore {
    public SnowsScript skeletonScript; // Store a reference to SkeletonScript
    public Herblore(SnowsScript script) {
        this.skeletonScript = script;
    }
    private static final Random random = new Random();

    public static long handleHerblore(LocalPlayer player) {
        if (Interfaces.isOpen(1251)) {
            return random.nextInt(600, 800);
        }
        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            log("[Herblore] Selecting 'Creating Potions'");
            return random.nextLong(1250, 1500);
        }
        if (makeBombs) {
            log("[Herblore] makeBombs is true.");
            if (Backpack.contains("Bomb vial") && Backpack.isFull()) {
                log("[Herblore] Backpack contains 'Bomb vial' and is full.");
                backpack.interact("Bomb vial", "Make");
            } else {
                log("[Error] Backpack does not contain 'Bomb vial' or is not full.");
                SceneObject bank = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
                if(bank != null && bank.interact("Load Last Preset from")) {
                    log("[Herblore] Bank chest is present and interaction was successful.");
                    return random.nextLong(600, 800);
                }
            }
        }
        if(!makeBombs && Backpack.isFull()) {
            log("[Herblore] Make Bombs is false and Backpack is full.");
            SceneObject portable = SceneObjectQuery.newQuery().name("Portable well").results().nearest();
            if(portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                log("[Herblore] Portable well is present, interaction was successful and distance to player is less than 5.0D.");
                return random.nextLong(600, 800);
            } else {
                log("[Error] Portable well is not present or interaction was not successful or distance to player is more than 5 Tiles.");
                SceneObject bank = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
                if(bank != null && bank.interact("Load Last Preset from")) {
                    log("[Herblore] Bank chest is present and interaction was successful.");
                    return random.nextLong(600, 800);
                }
            }
        }
        return random.nextLong(600, 800);
    }
}