package net.botwithus.Fishing;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.inventory.equipment;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.List;
import java.util.regex.Pattern;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.inventory.equipment.Slot.NECK;

public class Fishing {

    public static long handleFishing(LocalPlayer player, String fishingLocation, String fishingAction) {
        if(useGote) {
            setLastSkillingLocation(player.getCoordinate());
            Execution.delay(random.nextLong(1500, 3000));
            usePorter();
        }
        if (backpack.isFull()) {
            log("[Fishing] Backpack is full. Handling full backpack.");
            return handleFullBackpack(player);
        }
        if (player.isMoving() || (AnimationCheck && player.getAnimationId() == -1)) {
            return random.nextLong(1500, 3000);
        }
        Npc nearestFishingSpot = findNearestFishingSpot(fishingLocation, fishingAction);

        if (nearestFishingSpot != null) {
            return interactWithFishingSpot(player, nearestFishingSpot, fishingAction);
        }

        log("[Fishing] No nearest fishing spot found. Returning.");
        return random.nextLong(3500, 5000);
    }

    static long handleFullBackpack(LocalPlayer player) {
        EntityResultSet<SceneObject> DepositBox = SceneObjectQuery.newQuery().name("Deposit box").results();
        if (nearestBank) {
            if (DepositBox.nearest() != null) {
                log("[Fishing] Depositing all fish...");
                boolean success = false;
                List<String> options = DepositBox.nearest().getOptions();
                if (options.contains("Deposit all fish")) {
                    success = DepositBox.nearest().interact("Deposit all fish");
                } else if (options.contains("Deposit-All")) {
                    success = DepositBox.nearest().interact("Deposit-All");
                }
                if (success) {
                    Execution.delayUntil(10000, () -> !Backpack.isFull());
                    if (!Backpack.isFull()) {
                        setBotState(SnowsScript.BotState.SKILLING);
                    }
                    return random.nextLong(1500, 3000);
                }
            } else {
                setLastSkillingLocation(player.getCoordinate());
                setBotState(SnowsScript.BotState.BANKING);
                return random.nextLong(1500, 3000);
            }
        }

        dropAllFish();
        return random.nextLong(1500, 3000);
    }

    static void dropAllFish() {
        log("[Fishing] Backpack is full. Dropping all fish...");

        ResultSet<Item> allItems = InventoryItemQuery.newQuery(93).results();

        for (Item item : allItems) {
            if (item != null) {
                dropItem(item);
            }
        }
    }

    static void dropItem(Item item) {
        String itemName = item.getName();
        int category = item.getConfigType().getCategory();

        if (ActionBar.containsItem(itemName)) {
            boolean success = ActionBar.useItem(itemName, "Drop");
            if (success) {
                log("[Fishing] Dropping (ActionBar): " + itemName);
                Execution.delay(random.nextLong(206, 405));
            }
        } else if (category == 57 || category == 929) { // Use Backpack fallback
            boolean success = backpack.interact(itemName, "Drop");
            if (success) {
                log("[Fishing] Dropping (Backpack): " + itemName);
                Execution.delay(random.nextLong(620, 650));
            }
        }
    }

    static Npc findNearestFishingSpot(String fishingLocation, String fishingAction) {
        Pattern actionPattern = Pattern.compile(".*" + Pattern.quote(fishingAction) + ".*", Pattern.CASE_INSENSITIVE);

        if (isNumeric(fishingLocation)) {
            int fishingLocationId = Integer.parseInt(fishingLocation);
            return NpcQuery.newQuery()
                    .id(fishingLocationId)
                    .option(actionPattern)
                    .results()
                    .nearest();
        } else {
            Pattern locationPattern = Pattern.compile(".*" + Pattern.quote(fishingLocation) + ".*", Pattern.CASE_INSENSITIVE);
            return NpcQuery.newQuery()
                    .name(locationPattern)
                    .option(actionPattern)
                    .results()
                    .nearest();
        }
    }

    static long interactWithFishingSpot(LocalPlayer player, Npc nearestFishingSpot, String fishingAction) {
        Coordinate currentFishingSpotCoord = nearestFishingSpot.getCoordinate();


        if (!currentFishingSpotCoord.equals(lastFishingSpotCoord) && player.getAnimationId() == -1) {
            boolean success = nearestFishingSpot.interact(fishingAction);
            if (success) {
                log("[Fishing] Interacted with fishing spot: " + nearestFishingSpot.getName());
                lastFishingSpotCoord = currentFishingSpotCoord;
                return random.nextLong(2500, 5000);
            }
        } else if (playerIsIdleForMoreThan5Seconds(player)) {
            boolean success = nearestFishingSpot.interact(fishingAction);
            if (success) {
                lastFishingSpotCoord = currentFishingSpotCoord;
                log("[Fishing] Interacted with fishing spot: " + nearestFishingSpot.getName());
                return random.nextLong(2500, 5000);
            }
        }
        return random.nextLong(2500, 5000);
    }

    private static boolean playerIsIdleForMoreThan5Seconds(LocalPlayer player) {
        long animationStart = System.currentTimeMillis();
        while (System.currentTimeMillis() - animationStart < 5000) {
            if (player.getAnimationId() != -1) {
                return false;
            }
            Execution.delay(1000);
        }
        return true;
    }
    private static void usePorter() {
        String currentPorter = porterTypes[currentPorterType.get()];
        int varbitValue = VarManager.getInvVarbit(94, 2, 30214);

        if (Backpack.contains(currentPorter) && varbitValue <= getGraceChargesThreshold()) {
            log("[Fishing] Porters have " + varbitValue + " charges. Charging.");
            log("[Caution] Interacting with Equipment - Equipment needs to be OPEN.");
            if (equipment.contains("Grace of the elves")) {
                boolean interactionResult = equipment.interact(NECK, "Charge all porters");
                if (interactionResult) {
                    log("[Success] Interaction with Equipment was successful.");
                } else {
                    log("[Error] Interaction with Equipment failed.");
                }
            } else {
                if (Backpack.contains(currentPorter)) {
                    boolean interactionResult = backpack.interact(currentPorter, "Wear");
                    if (interactionResult) {
                        log("[Success] Interaction with Backpack was successful.");
                    } else {
                        log("[Error] Interaction with Backpack failed.");
                    }
                }
            }
            Execution.delay(random.nextLong(1500, 3000));
        }
    }
}
