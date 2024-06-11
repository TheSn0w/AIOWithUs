package net.botwithus.Archaeology;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import java.util.List;

import static net.botwithus.Archaeology.Traversal.traverseToLastSkillingLocation;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.player;

public class Stormguard {

    public static boolean shouldTraverseToStormguard(List<String> selectedArchNames) {
        return selectedArchNames.contains("Keshik ger")
                || selectedArchNames.contains("Material cache (Armadylean yellow)")
                || selectedArchNames.contains("Tailory debris");
    }

    public static void traverseToStormguard(List<String> selectedArchNames) {
        EntityResultSet<SceneObject> firstGap = SceneObjectQuery.newQuery().id(117179).option("Traverse").results();
        EntityResultSet<SceneObject> secondGap = SceneObjectQuery.newQuery().id(117180).option("Traverse").results();
        Coordinate stormguard = new Coordinate(2458, 7180, 1);

        log("[Archaeology] Traversing to Stormguard Citadel.");
        if (Movement.traverse((NavPath.resolve(stormguard))) == TraverseEvent.State.FINISHED) {
            log("[Archaeology] Finished traversing to Stormguard Citadel.");
            if (!firstGap.isEmpty()) {
                SceneObject firstGapObject = firstGap.first();
                if (firstGapObject != null && firstGapObject.interact("Traverse")) {
                    log("[Archaeology] Traversing first gap.");
                    Execution.delayUntil(10000, () -> player.getAnimationId() == -1);
                } else {
                    log("[Error] Failed to traverse first gap.");
                }
            } else {
                log("[Error] No first gap found.");
            }
            if (!secondGap.isEmpty()) {
                SceneObject secondGapObject = secondGap.first();
                if (secondGapObject != null && secondGapObject.interact("Traverse")) {
                    log("[Archaeology] Traversing second gap.");
                    Execution.delayUntil(10000, () -> player.getCoordinate().equals(new Coordinate(2468, 7191, 1)));
                } else {
                    log("[Error] Failed to traverse second gap.");
                }
            } else {
                log("[Error] No second gap found.");
            }
            traverseToLastSkillingLocation();
        }
        log("[Archaeology] Traversing to Stormguard Citadel.");
    }
}
