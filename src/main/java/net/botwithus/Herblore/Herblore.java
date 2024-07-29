package net.botwithus.Herblore;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.inventory.bank;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;


import static net.botwithus.CustomLogger.log;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;

public class Herblore {


    public static long handleHerblore(LocalPlayer player) {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1111, 1857);
        }

        if (Interfaces.isOpen(1370)) {
            selectInterface();
            return random.nextLong(1111, 1857);
        }

        if (Backpack.isFull()) {
            SceneObject portable = SceneObjectQuery.newQuery().name("Portable well").results().nearest();
            if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                log("Interacting with Portable well");
            } else {
                log("Portable well not found or interaction failed");
            }
        } else {
            SceneObject bank = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
            if (bank != null && bank.interact("Load Last Preset from")) {
                log("Interacting with Bank chest");
                if (Backpack.isFull()) {
                    return random.nextLong(1111, 1857);
                } else {
                    log("Backpack is not full");
                    shutdown();
                }
            } else {
                log("Bank chest not found or interaction failed");

            }
        }
        return random.nextLong(1111, 1857);
    }

    private static void selectInterface() {
        Component craftPouchesInterface = ComponentQuery.newQuery(1370).results().first();
        if (craftPouchesInterface == null) {
            log("[Error] Interface not found.");
            return;
        }

        int resourcesCanMake = VarManager.getVarValue(VarDomainType.PLAYER, 8847);
        if (resourcesCanMake <= 0) {
            log("[Caution] Interacting with bank.");
            return;
        }

        boolean interact = MiniMenu.interact(16, 0, -1, 89784350);
        if (interact) {
            log("[Success] Successfully interacted with Mix!");
            Execution.delay(random.nextLong(1111, 1857));
        } else {
            log("[Error] Failed to Mix Potions.");
        }
    }
}