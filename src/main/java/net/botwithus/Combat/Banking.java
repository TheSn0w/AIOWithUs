package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
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
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.BankInteractions.BANK_TYPES;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.inventory.backpack.backpack;

public class Banking {


    public static long bankToWars(LocalPlayer player) {
        if (VarManager.getVarbitValue(16779) == 1) {
            ActionBar.useAbility("Soul Split");
        }
        if (usequickPrayers && quickPrayersActive) {
            deactivateQuickPrayers();
        }

        if (useDwarfcannon) {
            EntityResultSet<SceneObject> siegeEngine = SceneObjectQuery.newQuery().name("Dwarven siege engine").option("Fire").results();
            if (!siegeEngine.isEmpty() && !Backpack.isFull()) {
                SceneObject engine = siegeEngine.first();
                engine.interact("Pick up");
                Execution.delayUntil(random.nextLong(15000, 20000), () -> Backpack.contains("Dwarven siege engine"));
            }
        }
        setLastSkillingLocation(player.getCoordinate());
        if (ActionBar.containsAbility("War's Retreat Teleport")) {
            ActionBar.useAbility("War's Retreat Teleport");
            Execution.delay(random.nextLong(4500, 5000));
            Execution.delay(loadLastPreset(player, SceneObjectQuery.newQuery().name("Bank chest").option("Use").results().nearest()));
        } else {
            log("[Error] War's Retreat Teleport not found in ability bar.");
            return random.nextLong(1500, 3000);
        }
        return random.nextLong(1500, 3000);
    }

    public static long loadLastPreset(LocalPlayer player, SceneObject nearestBankBooth) {
        List<String> interactionOptions = List.of("Load Last Preset from");
        String bankType = nearestBankBooth.getName();
        final int maxAttempts = 3; // Define a maximum number of attempts

        if (BANK_TYPES.contains(bankType)) {
            for (String interactionOption : interactionOptions) {
                log("[Combat] Trying interaction option: " + interactionOption + " on " + bankType);
                boolean interactionSuccess = false;

                for (int attempt = 0; attempt < maxAttempts && !interactionSuccess; attempt++) {
                    interactionSuccess = nearestBankBooth.interact(interactionOption);
                    log("[Combat] Attempt " + (attempt + 1) + " to interact with bank using " + interactionOption + " on " + bankType + ": " + interactionSuccess);

                    if (interactionSuccess) {
                        Execution.delayUntil(random.nextLong(2000, 3000), player::isMoving);
                        if (player.isMoving()) {
                            Execution.delay(random.nextLong(5000, 7500));
                            if (!player.isMoving()) {
                                NavPath path = NavPath.resolve(lastSkillingLocation);
                                if (Movement.traverse(path) == TraverseEvent.State.FINISHED) {
                                    if (useDwarfcannon) {
                                        EntityResultSet<SceneObject> siegeEngine = SceneObjectQuery.newQuery().name("Dwarven siege engine").option("Fire").results();
                                        if (Backpack.contains("Dwarven siege engine") && Backpack.contains("Cannonball")) {
                                            Backpack.interact("Dwarven siege engine", "Set up");
                                            boolean isSetup = Execution.delayUntil(random.nextLong(4000, 5000), () -> !siegeEngine.isEmpty());
                                            if (!isSetup) {
                                                moveToNearbyCoordinate();
                                            }
                                        }
                                    }
                                    setBotState(SKILLING);
                                }
                            }
                            return random.nextLong(1500, 3000);
                        } else {
                            log("[Error] Player did not start moving after interaction. Trying again.");
                        }
                    } else {
                        log("[Error] Failed to interact with bank using " + interactionOption + " option. Trying again.");
                        Execution.delay(random.nextLong(3000, 5000));
                    }
                }

                if (!interactionSuccess) {
                    log("[Error] Failed to interact with the bank after " + maxAttempts + " attempts.");
                    shutdown();
                    return random.nextLong(1500, 3000);
                }
            }
        }
        return 0;
    }
    public static void moveToNearbyCoordinate() {
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
                    break;
                }
            }
        }
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


