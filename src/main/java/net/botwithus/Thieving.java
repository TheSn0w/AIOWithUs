package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
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

public class Thieving {
    private static final Random random = new Random();
    public static SnowsScript skeletonScript; // Store a reference to SkeletonScript
    private int failedAttempts = 0;

    public Thieving(SnowsScript script) {
        skeletonScript = script; // Initialize with the correct instance
    }

    final Coordinate BakerystallLocation = new Coordinate(3208, 3257, 0); // Set the BakeryStall coordinate

    public long interactWithBakeryStall(LocalPlayer player) {
        if (!Backpack.isFull() && !player.inCombat()) {
            SceneObject bakeryStall = SceneObjectQuery.newQuery().id(66692).results().nearest();

            if (bakeryStall != null) {
                List<String> options = bakeryStall.getOptions();

                if (options.contains("Steal from")) {
                    Execution.delay(random.nextInt(500, 1000));  // Small delay before interaction
                    boolean interactionSuccess = bakeryStall.interact("Steal from");
                    ScriptConsole.println("Attempted interaction with Bakery Stall: " + interactionSuccess);

                    long startTime = System.currentTimeMillis();
                    long endTime = startTime + 2000;  // 2-second monitoring window

                    boolean success = false;
                    while (System.currentTimeMillis() < endTime) {  // Monitor within 2 seconds
                        Execution.delay(100);  // Check every 100 milliseconds
                        int currentAnimation = player.getAnimationId();  // Get animation ID

                        if (currentAnimation == 832) {  // Successful interaction
                            success = true;
                            break;  // Exit loop if success is detected
                        }
                    }

                    if (success) {  // Successful interaction
                        ScriptConsole.println("Interaction successful after monitoring. Animation ID: 832");
                        failedAttempts = 0;  // Reset failed attempts
                        return random.nextLong(1500, 3000);  // Shorter delay for success
                    } else {  // Unsuccessful interaction
                        failedAttempts++;  // Increment failed attempts
                        ScriptConsole.println("Interaction unsuccessful after monitoring. Failed attempts: " + failedAttempts);

                        if (failedAttempts >= 3) {  // If failed three times or more
                            if (!BakerystallLocation.equals(player.getCoordinate())) {  // Check if not at Bakery Stall
                                ScriptConsole.println("Traversing to BakeryStallLocation after 3 failed attempts.");
                                Movement.traverse(NavPath.resolve(BakerystallLocation));  // Move to Bakery Stall location
                                return random.nextLong(3500, 5000);  // Longer delay for failure and movement
                            }
                        }

                        return random.nextLong(3500, 5000);  // Longer delay for failure
                    }
                }
            }
        }

        return random.nextLong(3500, 5000);  // Default longer delay for failure
    }


    public long handleThieving(LocalPlayer player) {
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
                        ScriptConsole.println("Interacting with Pompous merchant.");
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
                        ScriptConsole.println("Interacted with BankChest.");
                        Execution.delayUntil(15000, Backpack::isEmpty);
                        return random.nextLong(3500, 5000);
                    } else {
                        ScriptConsole.println("BankChest interaction failed.");
                    }
                } else {
                    ScriptConsole.println("BankChest not found.");
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
                            ScriptConsole.println("Activating Crystal Mask.");
                            if (ActionBar.useAbility("Crystal Mask")) {
                                ScriptConsole.println("Crystal Mask activated successfully.");
                                Execution.delay(RandomGenerator.nextInt(1000, 2000));
                            } else {
                                ScriptConsole.println("Failed to activate Crystal Mask.");
                            }
                        }
                        LightFormActivation();

                        Npc npc = results.nearest();
                        if (npc != null) {
                            ScriptConsole.println("Interacting with Druid.");
                            npc.interact("Pickpocket");
                        }
                    }
                }
            }
        }
        return animationStartTime;
    }

    private void LightFormActivation() {
        if (VarManager.getVarbitValue(29066) == 0) {
            activateLightForm();
        }
    }

    private void activateLightForm() {
        ActionBar.useAbility("Light Form");
        ScriptConsole.println("Light Form activated.");
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
                ScriptConsole.println("[EatFood] No food found. Banking for food.");
            SnowsScript.setBotState(SnowsScript.BotState.BANKING);
                return random.nextLong(1500, 3000);
        }

        boolean eatSuccess = Backpack.interact(food.getName(), "Eat");

        if (eatSuccess) {
            ScriptConsole.println("[EatFood] Successfully ate " + food.getName());
            Execution.delay(RandomGenerator.nextInt(250, 450));
        } else {
            ScriptConsole.println("[EatFood] Failed to eat.");
        }
        return 0;
    }
    public long bankForfood() {
        SceneObject bankChest = SceneObjectQuery.newQuery().name("Bank chest").option("Bank").results().nearest();
        if (bankChest != null) {
            boolean bankInteractionSuccess = bankChest.interact("Load Last Preset from");
            if (bankInteractionSuccess) {
                ScriptConsole.println("Interacted with BankChest.");
                Execution.delayUntil(15000, () -> Backpack.containsItemByCategory(58));
                if (Backpack.containsItemByCategory(58)) { // Corrected syntax here
                    ScriptConsole.println("Food found in backpack.");
                    SnowsScript.setBotState(SnowsScript.BotState.SKILLING);
                }
            } else {
                ScriptConsole.println("BankChest interaction failed.");
            }
        } else {
            ScriptConsole.println("BankChest not found.");
            ActionBar.useAbility("War's Retreat Teleport");
            Execution.delay(random.nextLong(7000, 7500));
        }
        return random.nextLong(1500, 3000);
    }
}