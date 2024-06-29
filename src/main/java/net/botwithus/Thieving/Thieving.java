package net.botwithus.Thieving;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.Variables.random;

public class Thieving {
    private static int failedAttempts = 0;
    static final Coordinate BakerystallLocation = new Coordinate(3209, 3266, 0);

    public static long interactWithBakeryStall(LocalPlayer player) {
        if (Backpack.isFull() || player.inCombat()) {
            return random.nextLong(3500, 5000);
        }

        // Check if player is at BakerystallLocation
        if (!player.getCoordinate().equals(BakerystallLocation)) {
            log("[Thieving] Traversing to Bakery Stall Location.");
            Movement.traverse(NavPath.resolve(BakerystallLocation));
            return 0;
        }

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(66692).option("Steal from").results();
        SceneObject bakeryStall = results.nearestTo(player);

        if (bakeryStall != null) {
            return stealFromBakeryStall(player, bakeryStall);
        }

        return random.nextLong(3500, 5000);
    }

    private static long stealFromBakeryStall(LocalPlayer player, SceneObject bakeryStall) {
        if (!bakeryStall.getOptions().contains("Steal from") || player.isMoving()) {
            return random.nextLong(3500, 5000);
        }

        Execution.delay(random.nextInt(500, 1000));
        boolean interactionSuccess = bakeryStall.interact("Steal from");
        log("[Thieving] Attempted interaction with Bakery Stall: " + interactionSuccess);

        if (wasStealSuccessful(player)) {
            failedAttempts = 0;
            return random.nextLong(1000, 1500);
        }

        failedAttempts++;
        log("[Error] Interaction unsuccessful after monitoring. Failed attempts: " + failedAttempts);

        return handleFailedAttempts(player);
    }

    private static boolean wasStealSuccessful(LocalPlayer player) {
        long endTime = System.currentTimeMillis() + 2000;  // 2-second monitoring window

        while (System.currentTimeMillis() < endTime) {
            Execution.delay(random.nextLong(100, 200));
            if (player.getAnimationId() == 832) {
                return true;
            }
        }

        return false;
    }

    private static long handleFailedAttempts(LocalPlayer player) {
        if (failedAttempts < 3 || BakerystallLocation.equals(player.getCoordinate())) {
            return random.nextLong(3500, 5000);
        }

        log("[Thieving] Traversing to Bakery Stall Location after 3 failed attempts.");
        Movement.traverse(NavPath.resolve(BakerystallLocation));
        return random.nextLong(3500, 5000);
    }


    public static long handleThieving(LocalPlayer player) {
        int thievingLevel = Skills.THIEVING.getActualLevel();
        double distance = Distance.between(player.getCoordinate(), BakerystallLocation);

        if (thievingLevel <= 5) {
            EntityResultSet<Npc> results = NpcQuery.newQuery().name("Pompous merchant").option("Talk to").results();
            if (results.isEmpty()) {
                if (Movement.traverse(NavPath.resolve(new Coordinate(2895, 3435, 0))) == TraverseEvent.State.FINISHED) {
                    return random.nextLong(3500, 5000);
                }
            } else {
                if (!player.isMoving() && player.getAnimationId() == -1) {
                    Npc npc = results.nearest();
                    if (npc != null) {
                        log("[Thieving] Interacting with Pompous merchant.");
                        npc.interact("Pickpocket");
                    }
                }
                return random.nextLong(1500, 3000);
            }
        }

        if (thievingLevel >= 6 && thievingLevel <= 41) {
            if (Backpack.isFull()) {
                EntityResultSet<SceneObject> bankChest = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
                boolean bankInteractionSuccess = bankChest.nearest().interact("Load Last Preset from");  // Attempt interaction
                if (bankInteractionSuccess) {
                    log("[Thieving] Interacted with BankChest.");
                    Execution.delayUntil(15000, Backpack::isEmpty);
                    return random.nextLong(1250, 2500);
                } else {
                    log("[Error] BankChest interaction failed.");
                }
            }
            if (distance > 15.0D) {
                if (Movement.traverse(NavPath.resolve(BakerystallLocation)) == TraverseEvent.State.FINISHED) {
                    log("[Thieving] Arrived at Bakery Stall Location.");
                    return random.nextLong(3500, 5000);
                }
            } else {
                long interactionDelay = interactWithBakeryStall(player);
                if (interactionDelay != random.nextLong(3500, 5000)) {
                    return interactionDelay;
                }
            }
        }
        long animationStartTime = 0;


        if (thievingLevel >= 42 && thievingLevel < 83) {
            if (Backpack.isFull()) {
                setBotState(BANKING);

            }
            eatFood(player);


            ComponentQuery query = ComponentQuery.newQuery(284).spriteId(25938);
            ResultSet<Component> resultsMask = query.results();
            boolean isCrystalMaskActive = !resultsMask.isEmpty();
            if (!isCrystalMaskActive && ActionBar.containsAbility("Crystal Mask")) {
                log("[Thieving] Activating Crystal Mask.");
                if (ActionBar.useAbility("Crystal Mask")) {
                    log("[Thieving] Crystal Mask activated successfully.");
                    Execution.delay(RandomGenerator.nextInt(1000, 2000));
                } else {
                    log("[Error] Failed to activate Crystal Mask.");
                }
            }

            if (VarManager.getVarbitValue(29066) == 0 && ActionBar.containsAbility("Light Form")) {
                activateLightForm();
            }

            EntityResultSet<Npc> results = NpcQuery.newQuery().name("Druid").option("Pickpocket").results();
            Coordinate druidLocation = new Coordinate(3311, 3304, 0);
            if (results.isEmpty()) {
                if (Movement.traverse(NavPath.resolve(druidLocation)) == TraverseEvent.State.FINISHED) {
                    return random.nextLong(3500, 5000);
                }
            } else {
                if (!player.isMoving() && player.getAnimationId() == -1) {
                    long startTime = System.currentTimeMillis();
                    long endTime = startTime + random.nextLong(4000, 5000);

                    boolean success = false;
                    while (System.currentTimeMillis() < endTime) {
                        Execution.delay(100);
                        int currentAnimation = player.getAnimationId();

                        if (currentAnimation == -1) {
                            success = true;
                        } else {
                            success = false;
                            break;
                        }
                    }

                    if (success) {
                        Npc npc = results.nearest();
                        if (npc != null) {
                            log("[Thieving] Interacting with Druid.");
                            npc.interact("Pickpocket");
                        }
                    }
                }
            }

            eatFood(player);

            return animationStartTime;
        }

        if (thievingLevel >= 83) {
            if (Backpack.isFull()) {
                setBotState(BANKING);

            }
            eatFood(player);

            ComponentQuery query = ComponentQuery.newQuery(284).spriteId(25938);
            ResultSet<Component> resultsMask = query.results();
            boolean isCrystalMaskActive = !resultsMask.isEmpty();
            if (!isCrystalMaskActive && ActionBar.containsAbility("Crystal Mask")) {
                log("[Thieving] Activating Crystal Mask.");
                if (ActionBar.useAbility("Crystal Mask")) {
                    log("[Thieving] Crystal Mask activated successfully.");
                    Execution.delay(RandomGenerator.nextInt(1000, 2000));
                } else {
                    log("[Error] Failed to activate Crystal Mask.");
                }
            }

            if (VarManager.getVarbitValue(29066) == 0 && ActionBar.containsAbility("Light Form")) {
                activateLightForm();
            }

            EntityResultSet<Npc> results = NpcQuery.newQuery().name("Crux Eqal Knight").option("Pickpocket").results();
            Coordinate druidLocation = new Coordinate(3320, 3290, 0);
            if (results.isEmpty()) {
                log("[Thieving] Traversing to knight Location.");
                if (Movement.traverse(NavPath.resolve(druidLocation)) == TraverseEvent.State.FINISHED) {
                    return random.nextLong(1500, 2500);
                }
            } else {
                if (!player.isMoving() && player.getAnimationId() == -1) {
                    long startTime = System.currentTimeMillis();
                    long endTime = startTime + random.nextLong(4000, 5000);

                    boolean success = false;
                    while (System.currentTimeMillis() < endTime) {
                        Execution.delay(100);
                        int currentAnimation = player.getAnimationId();

                        if (currentAnimation == -1) {
                            success = true;
                        } else {
                            success = false;
                            break;
                        }
                    }

                    if (success) {
                        Npc npc = results.nearest();
                        if (npc != null) {
                            log("[Thieving] Interacting with Druid.");
                            npc.interact("Pickpocket");
                        }
                    }
                }
            }
            eatFood(player);
        }
        return animationStartTime;
    }


    private static void activateLightForm() {
        if (ActionBar.useAbility("Light Form")) {
            log("[Thieving] Light Form activated.");
        }
    }

    public static void eatFood(LocalPlayer player) {
        boolean isPlayerEating = player.getAnimationId() == 18001;
        double healthPercentage = calculateHealthPercentage(player);
        boolean isHealthAboveThreshold = healthPercentage > 7;


        if (isPlayerEating || isHealthAboveThreshold) {
            return;
        }

        Execution.delay(healHealth(player));

    }

    public static double calculateHealthPercentage(LocalPlayer player) {
        double currentHealth = player.getCurrentHealth();
        double maximumHealth = player.getMaximumHealth();

        return maximumHealth > 0 ? (currentHealth / maximumHealth) * 100 : 0;
    }

    private static long healHealth(LocalPlayer player) {
        ResultSet<Item> foodItems = InventoryItemQuery.newQuery(93).option("Eat").results();
        Item food = foodItems.isEmpty() ? null : foodItems.first();

        if (food == null) {
            log("[Error] No food found. Banking for food.");
            setLastSkillingLocation(player.getCoordinate());
            setBotState(BANKING);
            return random.nextLong(1500, 3000);
        }

        boolean eatSuccess = backpack.interact(food.getName(), "Eat");

        if (eatSuccess) {
            log("[EatFood] Successfully ate " + food.getName());
        } else {
            log("[Error] Failed to eat.");
        }
        return 0;
    }

}
