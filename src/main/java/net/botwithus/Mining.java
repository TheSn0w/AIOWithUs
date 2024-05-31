package net.botwithus;

import net.botwithus.Variables.Variables;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.Headbar;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.*;
import java.util.function.Supplier;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.Variables.*;

public class Mining {
    private static Random random = new Random();
    public SnowsScript skeletonScript;

    public Mining(SnowsScript script) {
        this.skeletonScript = script;
        this.coordinateMap = new HashMap<>();
        this.methodMap = new HashMap<>();
        this.initializeCoordinateMap();
    }
    Map<String, Supplier<Long>> methodMap;
    static Map<String, Coordinate> coordinateMap;

    private void initializeCoordinateMap() {
        coordinateMap = new HashMap<>();


        coordinateMap.put("Adamantite rock", new Coordinate(3287, 3362, 0));
        coordinateMap.put("Mithril rock", new Coordinate(3287, 3362, 0));
        coordinateMap.put("Tin rock", new Coordinate(3180, 3368, 0));
        coordinateMap.put("Copper rock", new Coordinate(3180, 3368, 0));
        coordinateMap.put("Iron rock", new Coordinate(3180, 3368, 0));

    }


    public static boolean isNearPlayer(LocalPlayer player, List<String> selectedRockNames) {
        if (selectedRockNames == null || selectedRockNames.isEmpty()) {
            return false;
        }

        Coordinate playerCoordinate = player.getCoordinate();

        SceneObject nearestTarget = SceneObjectQuery.newQuery()
                .name(selectedRockNames.toArray(new String[0]))
                .results()
                .nearest();

        if (nearestTarget == null) {
            return false;
        }

        Coordinate targetCoordinate = nearestTarget.getCoordinate();
        double distance = Distance.between(playerCoordinate, targetCoordinate);

        return distance <= 15.0;
    }

    public static long handleMining(LocalPlayer player, List<String> selectedRockNames) {
        if (isNearPlayer(player, selectedRockNames)) {
            return handleSkillingMining(player, selectedRockNames);
        } else {
            return handleTraversal(player, selectedRockNames);
        }
    }

    public static long handleTraversal(LocalPlayer player, List<String> selectedRockNames) {
        if (selectedRockNames == null || selectedRockNames.isEmpty()) {
            ScriptConsole.println("[Traversal] No rock names provided.");
            return random.nextLong(1500, 3000);
        }

        String targetName = selectedRockNames.get(0);
        Coordinate baseCoordinate = coordinateMap.get(targetName);

        if (baseCoordinate == null) {
            ScriptConsole.println("[Traversal] No coordinate for " + targetName);
            return random.nextLong(1500, 3000);
        }

        ScriptConsole.println("[Traversal] Moving to random location near " + targetName);

        Movement.traverse(NavPath.resolve(baseCoordinate));

        return random.nextLong(1500, 3000);
    }

    private static long handleBackpack(LocalPlayer player) {
        if (Backpack.isFull()) {
            if (nearestBank) {
                if (!Backpack.containsItemByCategory(4448)) {
                    sendToBank(player);
                    return random.nextLong(1500, 3000);
                }

                long oreBoxDelay = fillOreBox();
                if (oreBoxDelay > 0) {
                    if (Backpack.isFull()) {
                        sendToBank(player);
                        return oreBoxDelay;
                    } else {
                        return random.nextLong(500, 1000);
                    }
                }
            }

            dropAllOres();
            return random.nextLong(1500, 3000);
        }

        return 0;
    }

    private static long fillOreBox() {
        Item oreBox = InventoryItemQuery.newQuery(93).category(4448).results().first();

        if (oreBox != null) {
            boolean interactionSuccess = backpack.interact(oreBox.getName(), "Fill");
            Execution.delay(random.nextInt(1500, 3500));

            if (interactionSuccess) {
                ScriptConsole.println("[Mining] Filled: " + oreBox.getName());
                return random.nextLong(1500, 3000);
            } else {
                ScriptConsole.println("[Mining] Failed to interact with the ore box.");
            }
        } else {
            ScriptConsole.println("[Mining] No ore box found with category 4448.");
        }

        return 0;
    }


    private static void sendToBank(LocalPlayer player) {
        setLastSkillingLocation(player.getCoordinate());
        Execution.delay(random.nextLong(1500, 3000));
        SnowsScript.setBotState(SnowsScript.BotState.BANKING);
        ScriptConsole.println("[Mining] Sending to bank.");
    }

    private static void dropAllOres() {
        ScriptConsole.println("[Mining] Backpack is full. Dropping all ores...");

        ResultSet<Item> allItems = InventoryItemQuery.newQuery(93).results();

        for (Item item : allItems) {
            if (item != null) {
                String itemName = item.getName();
                int category = item.getConfigType().getCategory();

                if (ActionBar.containsItem(itemName)) {
                    boolean success = ActionBar.useItem(itemName, "Drop");
                    if (success) {
                        ScriptConsole.println("Dropping (ActionBar): " + itemName);
                        Execution.delay(random.nextLong(206, 405));
                    }
                } else if (category == 91) { // Use Backpack fallback
                    boolean success = backpack.interact(itemName, "Drop");
                    if (success) {
                        ScriptConsole.println("Dropping (Backpack): " + itemName);
                        Execution.delay(random.nextLong(620, 650));
                    }
                }
            }
        }
    }

    public static long handleSkillingMining(LocalPlayer player, List<String> selectedRockNames) {
        long backpackDelay = handleBackpack(player);
        if (backpackDelay > 0) {
            return backpackDelay;
        }


        if (player.isMoving()) {
            return random.nextLong(1500, 3000);
        }

        long headbarDelay = handleHeadbars(player, selectedRockNames);
        if (headbarDelay > 0) {
            return headbarDelay;
        }

        return handleMiningInteractions(player, selectedRockNames);
    }

    private static long handleHeadbars(LocalPlayer player, List<String> selectedRockNames) {
        Optional<Headbar> bar = player.getHeadbars().stream()
                .filter(headbar -> headbar.getId() == 5 && headbar.getWidth() < RandomGenerator.nextInt(140, 180))
                .findAny();

        EntityResultSet<SpotAnimation> animations = SpotAnimationQuery.newQuery().ids(7164, 7165).results();

        if (bar.isPresent() && animations.isEmpty()) {

            return interactWithSelectedRocks(player, selectedRockNames);
        }

        if (!animations.isEmpty()) {
            return interactWithSpotAnimations(selectedRockNames, animations);
        }
        return 0;
    }

    private static long interactWithSelectedRocks(LocalPlayer player, List<String> selectedRockNames) {
        for (String rockName : selectedRockNames) {
            SceneObject nearestRock = SceneObjectQuery.newQuery().name(rockName).results().nearest();

            if (nearestRock != null) {
                if (nearestRock.interact("Mine")) {
                    ScriptConsole.println("[Mining] Interacted with: " + rockName);
                    Execution.delayUntil(RandomGenerator.nextInt(1500, 3000), () -> player.getAnimationId() == -1);
                }
            }
        }
        return random.nextLong(1500, 3000);
    }

    private static long interactWithSpotAnimations(List<String> selectedRockNames, EntityResultSet<SpotAnimation> animations) {
        SpotAnimation currentAnimation = animations.first();
        for (String rockName : selectedRockNames) {
            SceneObject matchingRock = SceneObjectQuery.newQuery().name(rockName)
                    .results()
                    .stream()
                    .filter(rock -> rock.getCoordinate().equals(currentAnimation.getCoordinate()))
                    .findFirst()
                    .orElse(null);

            if (matchingRock != null) {
                Execution.delay(RandomGenerator.nextInt(750, 3000));
                if (matchingRock.interact("Mine")) {
                    ScriptConsole.println("[Mining] Interacted with: Rockertunity");
                    Execution.delayUntil(RandomGenerator.nextInt(1500, 3000), animations::isEmpty);
                }
            }
        }
        return random.nextLong(1500, 3000);
    }

    private static long handleMiningInteractions(LocalPlayer player, List<String> selectedRockNames) {
        if (player.getAnimationId() == -1) {
            return interactWithSelectedRocks(player, selectedRockNames);
        }

        return random.nextLong(1500, 3000);
    }
}
