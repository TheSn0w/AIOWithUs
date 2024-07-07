package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.Arrays;
import java.util.List;

import static net.botwithus.Combat.Combat.*;
import static net.botwithus.Combat.Prayers.deactivateQuickPrayers;
import static net.botwithus.Combat.Prayers.quickPrayersActive;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.*;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.BankInteractions.BANK_TYPES;
import static net.botwithus.Variables.Variables.*;

public class Banking {


    public static void bankToWars(LocalPlayer player) {
        if (VarManager.getVarbitValue(16779) == 1) {
            ActionBar.useAbility("Soul Split");
            log("[Banking] Using Soul Split ability");
        }
        if (usequickPrayers && quickPrayersActive) {
            deactivateQuickPrayers();
            log("[Banking] Deactivating quick prayers");
        }

        if (useDwarfcannon) {
            EntityResultSet<SceneObject> siegeEngine = SceneObjectQuery.newQuery().name("Dwarven siege engine").option("Fire").results();
            if (!siegeEngine.isEmpty() && !Backpack.isFull()) {
                SceneObject engine = siegeEngine.first();
                engine.interact("Pick up");
                log("[Banking] Picking up Dwarven siege engine");
                Execution.delayUntil(random.nextLong(15000, 20000), () -> Backpack.contains("Dwarven siege engine"));
            }
        }
        setLastSkillingLocation(player.getCoordinate());

        if (ActionBar.containsAbility("Max guild Teleport")) {
            ActionBar.useAbility("Max guild Teleport");
            log("[Banking] Using Max guild Teleport ability");
        } else {
            if (ActionBar.containsAbility("War's Retreat Teleport")) {
                ActionBar.useAbility("War's Retreat Teleport");
                log("[Banking] Using War's Retreat Teleport ability");
            }
        }
        Execution.delay(random.nextLong(6500, 7500));
        loadLastPreset();
    }

    public static void loadLastPreset() {
        EntityResultSet<Npc> bankerResults = NpcQuery.newQuery().name("Banker").option("Load Last Preset from").results();
        EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery().name("Bank chest").option("Load Last Preset from").results();

        if (!bankerResults.isEmpty()) {
            log("[Banking] Banker found, attempting to load last preset from banker");

            Coordinate initialPosition = player.getCoordinate();
            while (!player.isMoving()) {
                bankerResults.nearest().interact("Load Last Preset from");
                log("[Banking] Interacting with banker");
                Execution.delay(random.nextLong(1000, 2000));
                if (!initialPosition.equals(player.getCoordinate())) {
                    log("[Banking] Player position changed");
                    break;
                }
            }

            log("[Banking] Player is moving, waiting until the player stops moving");
            Execution.delayUntil(random.nextLong(3000, 4000), () -> !player.isMoving());

            if (!player.isMoving()) {
                handlePostBankingActions();
            }
        } else if (!chestResults.isEmpty()) {
            log("[Banking] Bank chest found, attempting to load last preset from bank chest");

            Coordinate initialPosition = player.getCoordinate();
            while (!player.isMoving()) {
                chestResults.nearest().interact("Load Last Preset from");
                log("[Banking] Interacting with bank chest");
                Execution.delay(random.nextLong(1000, 2000));
                if (!initialPosition.equals(player.getCoordinate())) {
                    log("[Banking] Player position changed");
                    break;
                }
            }

            log("[Banking] Player is moving, waiting until the player stops moving");
            Execution.delayUntil(random.nextLong(15000), () -> !player.isMoving());

            if (!player.isMoving()) {
                handlePostBankingActions();
            }
        }
    }

    private static void handlePostBankingActions() {
        log("[Banking] Player is not moving after interacting with bank, attempting to move to last skilling location");
        NavPath path = NavPath.resolve(lastSkillingLocation);
        if (Movement.traverse(path) == TraverseEvent.State.FINISHED) {
            log("[Banking] Successfully moved to last skilling location");

            if (useDwarfcannon && Backpack.contains("Dwarven siege engine") && Backpack.contains("Cannonball")) {
                log("[Banking] Dwarfcannon is enabled, attempting to set up Dwarven siege engine");
                EntityResultSet<SceneObject> siegeEngine = SceneObjectQuery.newQuery().name("Dwarven siege engine").option("Fire").results();
                int attempts = 0;
                boolean isSetup = false;

                while (attempts < 3 && !isSetup) {
                    Backpack.interact("Dwarven siege engine", "Set up");
                    isSetup = Execution.delayUntil(random.nextLong(4000, 5000), () -> !siegeEngine.isEmpty());

                    if (!isSetup) {
                        log("[Banking] Failed to set up Dwarven siege engine, moving to nearby coordinate");
                        if (!moveToNearbyCoordinate()) {
                            log("[Banking] Unable to move to a nearby coordinate, setting bot state to SKILLING and disabling Dwarfcannon");
                            useDwarfcannon = false;
                            break;
                        }
                    }

                    attempts++;
                }

                if (isSetup) {
                    log("[Banking] Successfully set up Dwarven siege engine");
                } else {
                    log("[Banking] Failed to set up Dwarven siege engine after 3 attempts, setting bot state to SKILLING and disabling Dwarfcannon");
                    useDwarfcannon = false;
                }
            }

            setBotState(SKILLING);
        }
    }

    private static boolean moveToNearbyCoordinate() {
        Coordinate playerCoordinate = player.getCoordinate();
        List<Coordinate> nearbyCoordinates = Arrays.asList(
                new Coordinate(playerCoordinate.getX() + 1, playerCoordinate.getY(), playerCoordinate.getZ()),
                new Coordinate(playerCoordinate.getX() - 1, playerCoordinate.getY(), playerCoordinate.getZ()),
                new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() + 1, playerCoordinate.getZ()),
                new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() - 1, playerCoordinate.getZ())
        );

        for (Coordinate nearbyCoordinate : nearbyCoordinates) {
            if (nearbyCoordinate.isWalkable()) {
                Movement.walkTo(nearbyCoordinate.getX(), nearbyCoordinate.getY(), true);
                Execution.delay(random.nextLong(1500, 2500));

                if (player.getCoordinate().equals(nearbyCoordinate)) {
                    log("[Success] Player has moved to the desired coordinate.");
                    return true;
                }
            }
        }
        return false;
    }

    public static long handleBankforFood(LocalPlayer player, SceneObject nearestBankBooth) {
        List<String> interactionOptions = Arrays.asList("Bank", "Use");
        String bankType = nearestBankBooth.getName();

        if (BANK_TYPES.contains(bankType)) {
            for (String interactionOption : interactionOptions) {
                log("[Combat] Trying interaction option: " + interactionOption + " on " + bankType);

                for (int i = 0; i < 1; i++) {
                    boolean interactionSuccess = nearestBankBooth.interact(interactionOption);
                    log("[Combat] Trying to interact with bank using " + interactionOption + " on " + bankType + ": " + interactionSuccess);

                    if (interactionSuccess) {
                        Execution.delayUntil(random.nextLong(10000, 15000), Bank::isOpen);
                        if (Bank.isOpen()) {
                            log("[Combat] Bank is open. Depositing items.");
                            Execution.delay(random.nextLong(1000, 2000));
                            Bank.depositAll();
                            if (VarManager.getVarbitValue(45141) != 1) {
                                component(1, -1, 33882270);
                                Execution.delay(random.nextLong(1000, 2000));
                            } else {
                                log("[Combat] Bank Tab value is already 1");
                            }
                            Execution.delay(random.nextLong(1000, 2000));
                            Execution.delay(withdrawFood());
                            Execution.delay(random.nextLong(1000, 2000));
                            if (Backpack.containsItemByCategory(58)) {
                                log("[Success] Food withdrawn from the bank, going back to last location");
                                NavPath path = NavPath.resolve(lastSkillingLocation);
                                if (Movement.traverse(path) == TraverseEvent.State.FINISHED) {
                                    setBotState(SKILLING);
                                }
                                return random.nextLong(1500, 3000);
                            }
                        }
                    } else {
                        log("[Error] Failed to interact with bank using " + interactionOption + " option. Retrying, with Use option.");
                        Execution.delay(random.nextLong(3000, 5000));
                    }
                }
            }
        } else {
            log("[Error] Bank type " + bankType + " not recognized.");
        }

        log("[Error] Failed to interact with the bank using all available options.");
        shutdown();
        return random.nextLong(1500,3000);
    }

    private static long withdrawFood() {
        Execution.delay(random.nextLong(600, 1000));

        if (selectedFoodNames.isEmpty()) {
            log("[Error] No food names specified.");
            return 0;
        }

        for (String foodName : selectedFoodNames) {
            Bank.withdrawAll(foodName);
        }

        return random.nextLong(1500, 3000);
    }
}


