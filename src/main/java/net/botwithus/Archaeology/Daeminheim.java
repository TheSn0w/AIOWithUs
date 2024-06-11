package net.botwithus.Archaeology;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import java.util.List;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.Variables.Variables.*;

public class Daeminheim {

    public static boolean shouldTraverseToDaeminheimWarpedFloor(List<String> selectedArchNames) {
        return selectedArchNames.contains("Traveller's station")
                || selectedArchNames.contains("Security booth")
                || selectedArchNames.contains("Projection space");
    }
    public static boolean shouldTraverseToDaeminheimUpstairs(List<String> selectedArchNames) {
        return selectedArchNames.contains("Botanical reserve")
                || selectedArchNames.contains("Communal space");
    }
    public static void traverseToDaeminheimWarpedFloor(List<String> selectedArchNames) {
        EntityResultSet<SceneObject> ImposingDoor = SceneObjectQuery.newQuery().name("Imposing door").option("Enter").results();
        Coordinate WarpedEntrance = new Coordinate(3461, 3749, 0);
        EntityResultSet<Npc> results = NpcQuery.newQuery().name("Fremennik banker").option("Bank").results();
        Coordinate banker = new Coordinate(3449, 3719, 0);

        if (results.isEmpty()) {
            if (Movement.traverse(NavPath.resolve(banker)) == TraverseEvent.State.FINISHED) {
                log("[Archaeology] Arrived at banker.");
            }
        } else {
            Movement.walkTo(WarpedEntrance.getRegionX(), WarpedEntrance.getRegionY(), true);
            log("[Archaeology] Traversing to Daemonheim Warped Floor.");
        }

        Execution.delayUntil(15000, () -> !ImposingDoor.isEmpty());
        log("[Archaeology] Interacting with Imposing Door.");
        if (selectedArchNames.contains("Traveller's station") || selectedArchNames.contains("Security booth") || selectedArchNames.contains("Projection space")) {
            if (ImposingDoor != null && ImposingDoor.first().interact("Enter")) {
                Execution.delayUntil(30000, () -> player.getCoordinate().equals(new Coordinate(2208, 9219, 1)));
                if (player.getCoordinate().equals(new Coordinate(2208, 9219, 1))) {
                    Execution.delay(random.nextLong(5000, 7500));
                    Movement.walkTo(lastSkillingLocation.getX(), lastSkillingLocation.getY(), true);
                    Execution.delayUntil(60000, () -> player.getCoordinate().equals(lastSkillingLocation));
                    setBotState(SKILLING);
                } else {
                    log("[Error] Failed to interact with Imposing door.");
                }
            } else {
                log("[Error] Failed to interact with Imposing door.");
            }
        } else {
            log("[Error] No Imposing door found.");
        }
    }
    public static void traverseToDaeminheimUpstairs(List<String> selectedArchNames) {
        EntityResultSet<SceneObject> Stairs = SceneObjectQuery.newQuery().name("Staircase").option("Climb-up").results();
        Coordinate Staircase = new Coordinate(3454, 3729, 0);

        if (Movement.traverse(NavPath.resolve(Staircase)) == TraverseEvent.State.FINISHED) {
            log("[Archaeology] Interacting with Staircase.");
            if (selectedArchNames.contains("Botanical reserve")|| selectedArchNames.contains("Communal space")) {
                if (Stairs != null && Stairs.nearest().interact("Climb-up")) {
                    setBotState(SKILLING);
                } else {
                    log("[Error] Failed to interact with Staircase.");
                }
            } else {
                log("[Error] No Staircase found.");
            }
        } else {
            log("[Error] Failed to traverse to Staircase.");
        }
    }
}
