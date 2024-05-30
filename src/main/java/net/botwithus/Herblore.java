package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.Random;

import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.portersMade;

public class Herblore {
    public SnowsScript skeletonScript; // Store a reference to SkeletonScript
    public Herblore(SnowsScript script) {
        this.skeletonScript = script;
    }
    private static final Random random = new Random();

    public void updateChatMessageEvent(ChatMessageEvent event) {
        String message = event.getMessage();
        if (isHerbloreActive) {
            if (message.contains("You mix the ingredients")) {
                String potionType = "Potions Made";
                int count = Potions.getOrDefault(potionType, 0);
                Potions.put(potionType, count + 1);
            }
        }
    }

    public static long handleHerblore(LocalPlayer player) {
        if (Interfaces.isOpen(1251)) {
            return random.nextInt(600, 800);
        }
        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            ScriptConsole.println("Selecting 'Creating Potions'");
            return random.nextLong(1250, 1500);
        }
        if (makeBombs) {
            if (Backpack.contains("Bomb vial") && Backpack.isFull()) {
                Backpack.interact("Bomb vial", "Make");
            } else {
                SceneObject bank = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
                if(bank != null && bank.interact("Load Last Preset from")) {
                    return random.nextLong(600, 800);
                }
            }
        }
        if(!makeBombs && Backpack.isFull()) {
            SceneObject portable = SceneObjectQuery.newQuery().name("Portable well").results().nearest();
            if(portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                return random.nextLong(600, 800);
            } else {
                SceneObject bank = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
                if(bank != null && bank.interact("Load Last Preset from")) {
                    return random.nextLong(600, 800);
                }
            }
        }
        return random.nextLong(600, 800);
    }
}
