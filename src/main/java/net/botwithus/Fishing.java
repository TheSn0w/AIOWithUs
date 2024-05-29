package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
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
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class Fishing {
    private Random random = new Random();
    public SnowsScript skeletonScript;

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Fishing(SnowsScript script) {
        this.skeletonScript = script;
    }
    private Coordinate lastFishingSpotCoord = null;

    long handleFishing(LocalPlayer player, String fishingLocation, String fishingAction) {
        if (Backpack.isFull()) {
            return handleFullBackpack(player);
        }


        if (player.isMoving() || (skeletonScript.AnimationCheck && player.getAnimationId() == -1)) {
            return random.nextLong(1500, 3000);
        }

        Npc nearestFishingSpot = findNearestFishingSpot(fishingLocation, fishingAction);

        if (nearestFishingSpot != null) {
            return interactWithFishingSpot(player, nearestFishingSpot, fishingAction);
        }

        return random.nextLong(3500, 5000);
    }

    long handleFullBackpack(LocalPlayer player) {
        EntityResultSet<SceneObject> DepositBox = SceneObjectQuery.newQuery().name("Deposit box").results();
        if (skeletonScript.nearestBank) {
            if (DepositBox.nearest() != null) {
                ScriptConsole.println("[Fishing] Depositing all fish...");
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
                        SnowsScript.setBotState(SnowsScript.BotState.SKILLING);
                    }
                    return random.nextLong(1500, 3000);
                }
            } else {
                skeletonScript.setLastSkillingLocation(player.getCoordinate());
                SnowsScript.setBotState(SnowsScript.BotState.BANKING);
                return random.nextLong(1500, 3000);
            }
        }

        dropAllFish();
        return random.nextLong(1500, 3000);
    }

    void dropAllFish() {
        ScriptConsole.println("[Fishing] Backpack is full. Dropping all fish...");

        ResultSet<Item> allItems = InventoryItemQuery.newQuery(93).results();

        for (Item item : allItems) {
            if (item != null) {
                dropItem(item);
            }
        }
    }

    void dropItem(Item item) {
        String itemName = item.getName();
        int category = item.getConfigType().getCategory();

        if (ActionBar.containsItem(itemName)) {
            boolean success = ActionBar.useItem(itemName, "Drop");
            if (success) {
                ScriptConsole.println("[Fishing] Dropping (ActionBar): " + itemName);
                Execution.delay(random.nextLong(206, 405));
            }
        } else if (category == 57 || category == 929) { // Use Backpack fallback
            boolean success = Backpack.interact(itemName, "Drop");
            if (success) {
                ScriptConsole.println("[Fishing] Dropping (Backpack): " + itemName);
                Execution.delay(random.nextLong(620, 650));
            }
        }
    }

    Npc findNearestFishingSpot(String fishingLocation, String fishingAction) {
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

    long interactWithFishingSpot(LocalPlayer player, Npc nearestFishingSpot, String fishingAction) {
        Coordinate currentFishingSpotCoord = nearestFishingSpot.getCoordinate();


        if (!currentFishingSpotCoord.equals(lastFishingSpotCoord) && player.getAnimationId() == -1) {
            boolean success = nearestFishingSpot.interact(fishingAction);
            if (success) {
                ScriptConsole.println("[Fishing] Interacted with fishing spot: " + nearestFishingSpot.getName());
                lastFishingSpotCoord = currentFishingSpotCoord;
                return random.nextLong(2500, 5000);
            }
        } else if (playerIsIdleForMoreThan5Seconds(player)) {
            boolean success = nearestFishingSpot.interact(fishingAction);
            if (success) {
                lastFishingSpotCoord = currentFishingSpotCoord;
                ScriptConsole.println("[Fishing] Interacted with fishing spot: " + nearestFishingSpot.getName());
                return random.nextLong(2500, 5000);
            }
        }
        return random.nextLong(2500, 5000);
    }

    private boolean playerIsIdleForMoreThan5Seconds(LocalPlayer player) {
        long animationStart = System.currentTimeMillis();
        while (System.currentTimeMillis() - animationStart < 5000) {
            if (player.getAnimationId() != -1) {
                return false;
            }
            Execution.delay(1000);
        }
        return true;
    }

    public static String fishingLocation = "";
    public static String fishingAction = "";
    public static List<String> selectedFishingLocations = new ArrayList<>();
    public static List<String> selectedFishingActions = new ArrayList<>();

    public static String getFishingLocation() {
        return fishingLocation;
    }

    public static void setFishingLocation(String location) {
        fishingLocation = location;
    }

    public static List<String> getSelectedFishingLocations() {
        return selectedFishingLocations;
    }

    public static void addFishingLocation(String location) {
        if (location != null && !location.trim().isEmpty() && !selectedFishingLocations.contains(location)) {
            selectedFishingLocations.add(location);
        }
    }

    public static void removeFishingLocation(String location) {
        selectedFishingLocations.remove(location);
    }

    public static String getFishingAction() {
        return fishingAction;
    }

    public static void setFishingAction(String action) {
        fishingAction = action;
    }

    public static List<String> getSelectedFishingActions() {
        return new ArrayList<>(selectedFishingActions);
    }

    public static void addFishingAction(String action) {
        if (action != null && !action.trim().isEmpty() && !selectedFishingActions.contains(action)) {
            selectedFishingActions.add(action);
        }
    }

    public static void removeFishingAction(String action) {
        selectedFishingActions.remove(action);
    }
}
