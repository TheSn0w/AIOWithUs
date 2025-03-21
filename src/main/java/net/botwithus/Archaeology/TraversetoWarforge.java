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

public class TraversetoWarforge {

    public static boolean shouldTraverseToWarforge(List<String> selectedArchNames) {
        return selectedArchNames.contains("Gladiatorial goblin remains")
                || selectedArchNames.contains("Crucible stands debris")
                || selectedArchNames.contains("Goblin dorm debris")
                || selectedArchNames.contains("Big High War God shrine")
                || selectedArchNames.contains("Yu'biusk animal pen")
                || selectedArchNames.contains("Yu'biusk clay pit")
                || selectedArchNames.contains("Goblin trainee remains")
                || selectedArchNames.contains("Kyzaj champion's boudoir")
                || selectedArchNames.contains("Warforge scrap pile")
                || selectedArchNames.contains("Warforge weapon rack")
                || selectedArchNames.contains("Makeshift pie oven")
                || selectedArchNames.contains("Material cache (vulcanised rubber)")
                || selectedArchNames.contains("Material cache (malachite green)")
                || selectedArchNames.contains("Material cache (Mark of the Kyzaj)")
                || selectedArchNames.contains("Material cache (warforged bronze)")
                || selectedArchNames.contains("Material cache (Yu'biusk clay)");
    }

    static void traverseToWarforge(List<String> selectedArchNames) {
        Coordinate hellfire = new Coordinate(2412, 2838, 0);
        log("[Archaeology] Traversing to Warfroge digsite.");

        if (Movement.traverse(NavPath.resolve(hellfire)) == TraverseEvent.State.FINISHED) {
            log("[Archaeology] Finished traversing to Warfroge digsite.");
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Lift").results();
            if (!results.isEmpty()) {
                SceneObject Fort = results.first();
                if (Fort != null && Fort.interact("Descend")) {
                    Execution.delay(random.nextLong(2500, 3000));
                    if (Interfaces.isOpen(720)) {
                        interactWithDialogOption(selectedArchNames);
                    }
                    Execution.delay(random.nextLong(5500, 6000));
                    traverseToLastSkillingLocation();
                } else {
                    log("[Error] Failed to interact with Fort Entrance.");
                }
            } else {
                log("[Error] No Fort Entrance found.");
            }
        } else {
            log("[Error] Failed to traverse to Fort Entrance.");
        }
    }
}
