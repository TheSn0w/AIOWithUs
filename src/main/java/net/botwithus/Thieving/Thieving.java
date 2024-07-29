package net.botwithus.Thieving;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import static ImGui.Skills.ThievingImGui.*;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.Variables.player;
import static net.botwithus.Variables.Variables.random;

public class Thieving {
    public static final Coordinate BakerystallLocation = new Coordinate(3209, 3266, 0);




    public static long handleThieving(LocalPlayer player) {
        int thievingLevel = Skills.THIEVING.getActualLevel();

        if (thievingAuto) {
            if (thievingLevel <= 5) {
                Execution.delay(handlePompousMerchant());
            } else if (thievingLevel <= 42) {
                Execution.delay(handleBakeryStall());
            } else if (thievingLevel <= 82) {
                Execution.delay(handleCruxDruids());
            } else {
                Execution.delay(handleCruxKnights());
            }
        }


        if (doPompousMerchants) {
            Execution.delay(handlePompousMerchant());
        }

        if (doBakeryStall) {
            Execution.delay(handleBakeryStall());
        }
        return 0;
    }


    private static long handlePompousMerchant() {
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
        return 0;
    }

    private static long handleBakeryStall() {
        if (Backpack.isFull()) {
            EntityResultSet<SceneObject> bankChest = SceneObjectQuery.newQuery().name("Bank chest").results();
            boolean bankInteractionSuccess = bankChest.nearest().interact("Load Last Preset from");
            if (bankInteractionSuccess) {
                log("[Thieving] Interacted with Bank chest.");
                Execution.delayUntil(30000, Backpack::isEmpty);
                return random.nextLong(878, 1878);
            } else {
                log("[Error] BankChest interaction failed.");
            }
        } else {
            if (player.getCoordinate().equals(BakerystallLocation)) {
                EntityResultSet<SceneObject> bakeryStall = SceneObjectQuery.newQuery().id(66692).hidden(false).option("Steal from").results();
                if (bakeryStall.isEmpty()) {
                    log("[Error] Bakery Stall not found.");
                    return random.nextLong(3500, 5000);
                } else {
                    SceneObject stall = bakeryStall.nearest();
                    if (stall.getCoordinate().equals(new Coordinate(3208, 3264, 0)) && player.getAnimationId() == -1) {
                        log("[Thieving] Interacted with Bakery Stall: " + stall.interact("Steal from"));
                        return random.nextLong(1269, 1878);
                    }
                }
            } else {
                log("[Thieving] Moving to Bakery Stall Location.");
                if (Movement.traverse(NavPath.resolve(BakerystallLocation)) == TraverseEvent.State.FINISHED) {
                    log("[Thieving] Arrived at Bakery Stall Location.");
                    return 0;
                }
            }
        }
        return 0;
    }

    private static long handleCruxDruids() {
        return 0;
    }
    private static long handleCruxKnights() {
        return 0;
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
