package net.botwithus.Archaeology;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;
import java.util.*;
import java.util.function.Supplier;

import static net.botwithus.Archaeology.Banking.backpackIsFull;
import static net.botwithus.Archaeology.Buffs.*;
import static net.botwithus.Archaeology.Porters.useGrace;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.*;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.Variables.Variables.*;

public class Archeology {
    public SnowsScript script;
    final Map<String, Supplier<Long>> methodMap;
    static Map<String, Coordinate> coordinateMap = initializeCoordinateMap();

    public Archeology(SnowsScript script) {
        this.script = script;
        this.methodMap = new HashMap<>();
    }


    private static Map<String, Coordinate> initializeCoordinateMap() {
        Map<String, Coordinate> map = new HashMap<>();
        map.put("Administratum debris", new Coordinate(2448, 7569, 0));
        map.put("Castra debris", new Coordinate(2444, 7585, 0));
        map.put("Legionary remains", new Coordinate(2437, 7596, 0));
        map.put("Venator remains", new Coordinate(3373, 3190, 0));
        map.put("Lodge art storage", new Coordinate(2589, 7331, 0));
        map.put("Lodge bar storage", new Coordinate(2589, 7331, 0));
        map.put("Material cache (samite silk)", new Coordinate(3373, 3200, 0));
        map.put("Ikovian memorial", new Coordinate(2681, 3397, 0));
        map.put("Saurthen debris", new Coordinate(1247, 230, 0));
        map.put("Amphitheatre debris", new Coordinate(3750, 3299, 0));
        return map;
    }


    public static long findSpotAnimationAndAct(LocalPlayer player, List<String> selectedArchNames) {
        if (selectedArchNames == null || selectedArchNames.isEmpty()) {
            log("[Error] No Excavation names provided.");
            return random.nextLong(1500, 3000);
        }

        String closestName = null;
        double closestDistance = Double.MAX_VALUE;

        for (String name : selectedArchNames) {
            if (name == null || name.isEmpty()) {
                continue;
            }

            Coordinate targetCoordinate = coordinateMap.get(name);
            if (targetCoordinate == null) {
                log("[Debug] No coordinate found for name: " + name);
                continue;
            }

            double distance = player.getCoordinate().distanceTo(targetCoordinate);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestName = name;
            }

            if (distance > 25.0D) {
                log("[Caution] attempting to traverse");
                if (Movement.traverse(NavPath.resolve(targetCoordinate)) == TraverseEvent.State.FINISHED) {
                    log("[Success] Finished traversing to: " + name);
                } else {
                    log("[Error] Failed to traverse to: " + name);
                }
                return handleExcavation(closestName);
            }
        }

        if (closestName == null) {
            log("[Error] No valid excavation names found.");
            return random.nextLong(1500, 3000);
        }

        if (MaterialCache) {
            return MaterialCaches(player, selectedArchNames);
        } else {
            if (closestDistance <= 25.0D) {
                return Excavation(player, selectedArchNames);
            }
        }

        return random.nextLong(1500, 3000);
    }

    public static long Excavation(LocalPlayer player, List<String> selectedArchNames) {
        if (Backpack.isFull()) {
            backpackIsFull(player);
            return random.nextLong(1500, 3000);
        }
        useGrace();
        useBuffs();


        long checkInterval = RandomGenerator.nextInt(3000, 10000);

        boolean playerIdle = (player == null || player.getAnimationId() == -1);


        SpotAnimation currentSpotAnimation = SpotAnimationQuery.newQuery()
                .ids(7307)
                .results()
                .nearest();

        if (currentSpotAnimation == null) {
            log("[Caution] No spot animation found.");
            return interactWithDefaultObjects(selectedArchNames, checkInterval);
        }

        Coordinate currentCoord = currentSpotAnimation.getCoordinate();

        boolean spotAnimationMoved = (lastSpotAnimationCoordinate == null || !lastSpotAnimationCoordinate.equals(currentCoord));

        if (spotAnimationMoved) {
            log("[Archaeology] Spot animation has moved.");
            Execution.delay(RandomGenerator.nextInt(1500, 3000));
            lastSpotAnimationCoordinate = currentCoord;
        }

        String[] archNamesArray = selectedArchNames.toArray(new String[0]);
        SceneObjectQuery query = SceneObjectQuery.newQuery()
                .name(archNamesArray)
                .hidden(false);

        SceneObject matchingArchObject = query.results()
                .nearestTo(currentCoord);

        if (matchingArchObject != null && matchingArchObject.getCoordinate().equals(currentCoord)) {
            if (spotAnimationMoved || playerIdle) {
                if (selectedArchNames.contains(matchingArchObject.getName())) {
                    matchingArchObject.interact("Excavate");
                    log("[Archaeology] Interacting with: " + matchingArchObject.getName());
                    return checkInterval;
                }
            }
        }
        if (playerIdle) {
            SceneObject nearestObject = query.results()
                    .nearestTo(currentCoord);

            if (nearestObject != null && selectedArchNames.contains(nearestObject.getName())) {
                nearestObject.interact("Excavate");
                log("[Archaeology] Interacting with: " + nearestObject.getName());
                return checkInterval;
            }
        }

        return random.nextLong(1500, 3000);
    }

    private static long interactWithDefaultObjects(List<String> selectedArchNames, long checkInterval) {
        for (String excavationName : selectedArchNames) {
            SceneObject nearestSceneObject = SceneObjectQuery.newQuery()
                    .name(excavationName)
                    .hidden(false)
                    .results()
                    .nearest();

            if (nearestSceneObject != null) {
                nearestSceneObject.interact("Excavate");
                log("[Archaeology] Interacting with: " + nearestSceneObject.getName());
                return checkInterval;
            }
        }

        return checkInterval;
    }

    public static long MaterialCaches(LocalPlayer player, List<String> selectedArchNames) {
        if (Backpack.isFull()) {
            setLastSkillingLocation(player.getCoordinate());
            setBotState(BANKING);
            return random.nextLong(1500, 3000);
        }

        if (player.isMoving() || player.getAnimationId() != -1) {
            return random.nextLong(1500, 5000);
        }

        for (String excavationName : selectedArchNames) {
            SceneObject nearestSceneObject = SceneObjectQuery.newQuery()
                    .name(excavationName)
                    .hidden(false)
                    .results()
                    .nearest();

            if (nearestSceneObject != null) {
                nearestSceneObject.interact("Excavate");
                log("[Archaeology] Interacting with: " + nearestSceneObject.getName());
                return random.nextLong(1500, 3000);
            }
        }

        return random.nextLong(1500, 3000);
    }


    public static long handleExcavation(String targetName) {
        Coordinate targetCoordinate = coordinateMap.get(targetName);

        if (targetCoordinate == null) {
            log("[Error] No coordinate for " + targetName);
            return random.nextLong(1500, 3000);
        }

        EntityResultSet<SceneObject> results = SceneObjectQuery
                .newQuery()
                .name(targetName)
                .option("Excavate")
                .results();

        if (results.isEmpty()) {
            log("[Error] No " + targetName + " found.");

            Movement.traverse(NavPath.resolve(targetCoordinate));
            return random.nextLong(1500, 3000);
        }

        SceneObject nearest = results.nearest();
        if (nearest != null) {
            setBotState(SKILLING);
            return random.nextLong(1500, 3000);
        }

        return random.nextLong(1500, 3000);
    }

}