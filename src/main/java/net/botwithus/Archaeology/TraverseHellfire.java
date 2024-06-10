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

import static net.botwithus.Archaeology.Traversal.interactWithDialogOption;
import static net.botwithus.Archaeology.Traversal.traverseToLastSkillingLocation;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.random;

public class TraverseHellfire {

    public static boolean shouldTraverseToHellfire(List<String> selectedArchNames) {
        return selectedArchNames.contains("Sacrificial altar")
                || selectedArchNames.contains("Dis dungeon debris")
                || selectedArchNames.contains("Cultist footlocker")
                || selectedArchNames.contains("Lodge bar storage")
                || selectedArchNames.contains("Lodge art storage");
    }

    public static void traverseToHellfireLift(List<String> selectedArchNames) {
        Coordinate hellfire = new Coordinate(3263, 3504, 0);
        log("[Archaeology] Traversing to Hellfire Lift.");

        if (Movement.traverse(NavPath.resolve(hellfire)) == TraverseEvent.State.FINISHED) {
            log("[Archaeology] Finished traversing to Hellfire Lift.");
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Lift").option("Descend").results();
            if (!results.isEmpty()) {
                SceneObject hellfireLift = results.first();
                if (hellfireLift != null && hellfireLift.interact("Descend")) {
                    Execution.delayUntil(5000, () -> Interfaces.isOpen(720));
                    log("[Archaeology] Interface is Open.");
                    Execution.delay(random.nextLong(1000, 1500));
                    interactWithDialogOption(selectedArchNames);
                    Execution.delay(random.nextLong(4500, 6000));
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
