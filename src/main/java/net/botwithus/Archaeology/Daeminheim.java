package net.botwithus.Archaeology;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import java.util.List;
import java.util.Random;

import static net.botwithus.Archaeology.Traversal.interactWithDialogOption;
import static net.botwithus.Archaeology.Traversal.traverseToLastSkillingLocation;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.random;

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
        log("[Archaeology] Traversing to Daemeinheim.");

        if (Movement.traverse(NavPath.resolve(WarpedEntrance)) == TraverseEvent.State.FINISHED) {
            log("[Archaeology] Finished traversing to Daemeinheim.");
            if (selectedArchNames.contains("Traveller's station") || selectedArchNames.contains("Security booth") || selectedArchNames.contains("Projection space")) {
                if (ImposingDoor != null && ImposingDoor.first().interact("Enter")) {
                    Execution.delay(random.nextLong(4500, 6500));
                    traverseToLastSkillingLocation();
                } else {
                    log("[Error] Failed to interact with Hellfire Lift.");
                }
            } else {
                log("[Error] No Hellfire Lift found.");
            }
        } else {
            log("[Error] Failed to traverse to Hellfire Lift.");
        }
    }
    public static void traverseToDaeminheimUpstairs(List<String> selectedArchNames) {
        EntityResultSet<SceneObject> Stairs = SceneObjectQuery.newQuery().name("Staircase").option("Climb-up").results();
        Coordinate WarpedEntrance = new Coordinate(3454, 3729, 0);
        log("[Archaeology] Traversing to Daemeinheim.");

        if (Movement.traverse(NavPath.resolve(WarpedEntrance)) == TraverseEvent.State.FINISHED) {
            log("[Archaeology] Finished traversing to Daemeinheim.");
            if (selectedArchNames.contains("Traveller's station") || selectedArchNames.contains("Security booth") || selectedArchNames.contains("Projection space")) {
                if (Stairs != null && Stairs.first().interact("Climb-up")) {
                    Execution.delay(random.nextLong(4500, 6500));
                    traverseToLastSkillingLocation();
                } else {
                    log("[Error] Failed to interact with Hellfire Lift.");
                }
            } else {
                log("[Error] No Hellfire Lift found.");
            }
        } else {
            log("[Error] Failed to traverse to Hellfire Lift.");
        }
    }
}
