package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
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
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.List;
import java.util.Random;

import static net.botwithus.CustomLogger.log;

public class Thieving {
    private static final Random random = new Random();
    public static SnowsScript skeletonScript;
    private static int failedAttempts = 0;

    public Thieving(SnowsScript script) {
        skeletonScript = script;
    }

    static final Coordinate BakerystallLocation = new Coordinate(3208, 3257, 0);

    public static long interactWithBakeryStall(LocalPlayer player) {
        if (!Backpack.isFull() && !player.inCombat()) {
            SceneObject bakeryStall = SceneObjectQuery.newQuery().id(66692).results().nearest();

            if (bakeryStall != null) {
                List<String> options = bakeryStall.getOptions();

                if (options.contains("Steal from")) {
                    Execution.delay(random.nextInt(500, 1000));
                    boolean interactionSuccess = bakeryStall.interact("Steal from");
                    log("[Thieving] Attempted interaction with Bakery Stall: " + interactionSuccess);

                    long startTime = System.currentTimeMillis();
                    long endTime = startTime + 2000;  // 2-second monitoring window

                    boolean success = false;
                    while (System.currentTimeMillis() < endTime) {
                        Execution.delay(100);
                        int currentAnimation = player.getAnimationId();

                        if (currentAnimation == 832) {
                            success = true;
                            break;
                        }
                    }

                    if (success) {
                        log("[Thieving] Interaction successful after monitoring. Animation ID: 832");
                        failedAttempts = 0;
                        return random.nextLong(1500, 3000);
                    } else {
                        failedAttempts++;
                        log("[Error] Interaction unsuccessful after monitoring. Failed attempts: " + failedAttempts);

                        if (failedAttempts >= 3) {
                            if (!BakerystallLocation.equals(player.getCoordinate())) {
                                log("[Thieving] Traversing to Bakery Stall Location after 3 failed attempts.");
                                Movement.traverse(NavPath.resolve(BakerystallLocation));
                                return random.nextLong(3500, 5000);
                            }
                        }

                        return random.nextLong(3500, 5000);
                    }
                }
            }
        }

        return random.nextLong(3500, 5000);
    }


    public static long handleThieving(LocalPlayer player) {
        if (Backpack.isFull()) {
            SnowsScript.setBotState(SnowsScript.BotState.BANKING);
            return random.nextLong(1500, 3000);
        }
        eatFood(player);

        int thievingLevel = Skills.THIEVING.getActualLevel();
        double distance = Distance.between(player.getCoordinate(), BakerystallLocation);

        if (thievingLevel < 5) {
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

        if (thievingLevel >= 5 && thievingLevel <= 42) {
            if (Backpack.isFull()) {
                SceneObject bankChest = SceneObjectQuery.newQuery().id(79036).option("Load Last Preset from").results().nearest();
                if (bankChest != null) {
                    boolean bankInteractionSuccess = bankChest.interact("Load Last Preset from");  // Attempt interaction
                    if (bankInteractionSuccess) {
                        log("[Thieving] Interacted with BankChest.");
                        Execution.delayUntil(15000, Backpack::isEmpty);
                        return random.nextLong(3500, 5000);
                    } else {
                        log("[Error] BankChest interaction failed.");
                    }
                } else {
                    log("[Error] BankChest not found.");
                }
            }
            if (distance > 15.0D) {
                Movement.traverse(NavPath.resolve(BakerystallLocation));
                return random.nextLong(3500, 5000);
            } else {
                long interactionDelay = interactWithBakeryStall(player);
                if (interactionDelay != random.nextLong(3500, 5000)) {
                    return interactionDelay;
                }
            }
        }
        long animationStartTime = 0;


        if (thievingLevel > 42) {
            EntityResultSet<Npc> results = NpcQuery.newQuery().id(23563).option("Pickpocket").results();
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
                        ComponentQuery query = ComponentQuery.newQuery(284).spriteId(25938);
                        ResultSet<Component> resultsMask = query.results();
                        boolean isCrystalMaskActive = !resultsMask.isEmpty();

                        if (!isCrystalMaskActive) {
                            log("[Thieving] Activating Crystal Mask.");
                            if (ActionBar.useAbility("Crystal Mask")) {
                                log("[Thieving] Crystal Mask activated successfully.");
                                Execution.delay(RandomGenerator.nextInt(1000, 2000));
                            } else {
                                log("[Error] Failed to activate Crystal Mask.");
                            }
                        }
                        LightFormActivation();

                        Npc npc = results.nearest();
                        if (npc != null) {
                            log("[Thieving] Interacting with Druid.");
                            npc.interact("Pickpocket");
                        }
                    }
                }
            }
        }
        return animationStartTime;
    }

    private static void LightFormActivation() {
        if (VarManager.getVarbitValue(29066) == 0) {
            activateLightForm();
        }
    }

    private static void activateLightForm() {
        ActionBar.useAbility("Light Form");
        log("[Thieving] Light Form activated.");
        Execution.delay(RandomGenerator.nextInt(1000, 2000));
    }
    public static void eatFood(LocalPlayer player) {
        boolean isPlayerEating = player.getAnimationId() == 18001;
        double healthPercentage = calculateHealthPercentage(player);
        boolean isHealthAboveThreshold = healthPercentage > 5;


        if (isPlayerEating || isHealthAboveThreshold) {
            return;
        }

        Execution.delay(healHealth(player));

    }

    public static double calculateHealthPercentage(LocalPlayer player) {
        double currentHealth = player.getCurrentHealth();
        double maximumHealth = player.getMaximumHealth();

        if (maximumHealth == 0) {
            throw new ArithmeticException("Maximum health cannot be zero.");
        }

        return (currentHealth / maximumHealth) * 100;
    }

    private static long healHealth(LocalPlayer player) {
        ResultSet<Item> foodItems = InventoryItemQuery.newQuery(93).option("Eat").results();
        Item food = foodItems.isEmpty() ? null : foodItems.first();

        if (food == null) {
                log("[EatFood] No food found. Banking for food.");
            SnowsScript.setBotState(SnowsScript.BotState.BANKING);
                return random.nextLong(1500, 3000);
        }

        boolean eatSuccess = backpack.interact(food.getName(), "Eat");

        if (eatSuccess) {
            log("[EatFood] Successfully ate " + food.getName());
            Execution.delay(RandomGenerator.nextInt(250, 450));
        } else {
            log("[Error] Failed to eat.");
        }
        return 0;
    }
    public static long bankForfood() {
        SceneObject bankChest = SceneObjectQuery.newQuery().name("Bank chest").option("Bank").results().nearest();
        if (bankChest != null) {
            boolean bankInteractionSuccess = bankChest.interact("Load Last Preset from");
            if (bankInteractionSuccess) {
                log("[Thieving] Interacted with Bank chest.");
                Execution.delayUntil(15000, () -> Backpack.containsItemByCategory(58));
                if (Backpack.containsItemByCategory(58)) { // Corrected syntax here
                    log("[Thieving] Food found in backpack.");
                    SnowsScript.setBotState(SnowsScript.BotState.SKILLING);
                }
            } else {
                log("[Error] Bank chest interaction failed.");
            }
        } else {
            log("[Error] BankChest not found.");
            ActionBar.useAbility("War's Retreat Teleport");
            Execution.delay(random.nextLong(7000, 7500));
        }
        return random.nextLong(1500, 3000);
    }
}