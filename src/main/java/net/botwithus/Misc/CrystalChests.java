package net.botwithus.Misc;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;

public class CrystalChests {
    static Coordinate Bank = new Coordinate(2154, 3340, 1);
    static Coordinate Chest = new Coordinate(2182, 3281, 1);
    static CrystalChestState chestState;

    enum CrystalChestState {
        BANKING,
        CHEST,
    }

    public static void openChest() {
        if (Backpack.contains("Crystal key")) {
            chestState = CrystalChestState.CHEST;
        } else {
            chestState = CrystalChestState.BANKING;
        }

        switch (chestState) {
            case BANKING:
                handleBanking();
                break;
            case CHEST:
                handleChest();
                break;
        }
    }


    private static void navigateToBank() {
        log("[Chests] Navigating to Bank");
        if (Movement.traverse(NavPath.resolve(Bank)) == TraverseEvent.State.FINISHED) {
            log("[Chests] Arrived at Bank");
            chestState = CrystalChestState.BANKING;
        }
    }
    private static void navigateToChest() {
        log("[Chests] Navigating to Chest");
        if (Movement.traverse(NavPath.resolve(Chest)) == TraverseEvent.State.FINISHED) {
            log("[Chests] Arrived at Chest");
            chestState = CrystalChestState.CHEST;
        }
    }

    private static void handleBanking() {
        EntityResultSet<SceneObject> BankChest = SceneObjectQuery.newQuery().id(92692).results();
        if (BankChest.isEmpty()) {
           navigateToBank();
        } else {
            SceneObject bankChest = BankChest.nearest();
            if (bankChest != null) {
                log("[Chests] Interacting with bank chest to load last preset...");
                bankChest.interact("Load Last Preset from");
                Execution.delayUntil(15000, Backpack::isFull);
                if (Backpack.isFull()) {
                    log("[Chests] Loaded preset, backpack is full. Changing bot state to CHEST.");
                    navigateToChest();
                } else {
                    log("[Error] Backpack is not full after attempting to interact with bank chest.");
                    shutdown();
                }
            } else {
                log("[Error] Nearest bank chest is null.");
            }
        }
    }

    private static void handleChest() {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Crystal chest").option("Open").results();
        if (Backpack.contains("Crystal key") && results.isEmpty()) {
           navigateToChest();
        } else {
            if (Backpack.contains("Crystal key") && !Interfaces.isOpen(168)) {
                SceneObject chest = results.nearest();
                if (chest != null) {
                    random.nextLong(250, 600);
                    if (chest.interact("Open")) {
                        log("[Chests] Opening crystal chest...");
                        Execution.delayUntil(15000, () -> Interfaces.isOpen(168));
                        random.nextLong(250, 600);
                        handleInterface();
                    }
                } else {
                    log("[Error] Chest is null.");
                    navigateToChest();
                }
            } else if (!Backpack.contains("Crystal key")) {
                log("[Chests] No more Crystal keys, changing bot state to BANKING.");
                chestState = CrystalChestState.BANKING;
            }
        }
    }

    private static void handleInterface() {
        if (Interfaces.isOpen(168)) {
            log("[Chests] Interface is open, Banking all loot...");
            Execution.delay(random.nextLong(200, 300));
            component( 1, -1, 11010075);
        } else {
            log("[Error] Interface not open after attempting to interact with chest.");
        }
    }
}
