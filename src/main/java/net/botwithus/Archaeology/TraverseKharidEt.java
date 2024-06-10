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

public class TraverseKharidEt {

    static boolean shouldTraverseToKharidEt(List<String> selectedArchNames) {
        return selectedArchNames.contains("Legionary remains")
                || selectedArchNames.contains("Castra debris")
                || selectedArchNames.contains("Administratum debris")
                || selectedArchNames.contains("Praesidio remains")
                || selectedArchNames.contains("Carcerem debris")
                || selectedArchNames.contains("Kharid-et chapel debris")
                || selectedArchNames.contains("Orcus altar")
                || selectedArchNames.contains("Pontifex remains")
                || selectedArchNames.contains("Culinarum debris")
                || selectedArchNames.contains("Armarium debris")
                || selectedArchNames.contains("Praetorian remains")
                || selectedArchNames.contains("War table debris")
                || selectedArchNames.contains("Ancient magick munitions")
                || selectedArchNames.contains("Material cache (imperial steel)")
                || selectedArchNames.contains("Material cache (Zarosian insignia)")
                || selectedArchNames.contains("Material cache (ancient vis)")
                || selectedArchNames.contains("Material cache (Tyrian purple)")
                || selectedArchNames.contains("Material cache (Blood of Orcus)");


    }

    static void traverseToKharidEt(List<String> selectedArchNames) {
        Coordinate hellfire = new Coordinate(3374, 3181, 0);
        log("[Archaeology] Traversing to Fort Entrance.");

        if (Movement.traverse(NavPath.resolve(hellfire)) == TraverseEvent.State.FINISHED) {
            log("[Archaeology] Finished traversing to Fort Entrance.");
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Fort entrance").option("Enter").results();
            if (!results.isEmpty()) {
                SceneObject Fort = results.first();
                if (Fort != null && Fort.interact("Enter")) {
                    Execution.delayUntil(5000, () -> Interfaces.isOpen(720));
                    log("[Archaeology] Interface is Open.");
                    Execution.delay(random.nextLong(1000, 1500));
                    interactWithDialogOption(selectedArchNames);
                    Execution.delay(random.nextLong(4500, 6000));
                    if (selectedArchNames.contains("Carcerem debris")) {
                        log("[Archaeology] Traversing to Legatus barrier.");
                        if (Movement.traverse(NavPath.resolve(new Coordinate(2258, 7586, 0))) == TraverseEvent.State.FINISHED) {
                            EntityResultSet<SceneObject> legatusBarrier = SceneObjectQuery.newQuery().name("Legatus barrier").option("Pass").results();
                            if (!legatusBarrier.isEmpty()) {
                                SceneObject barrier = legatusBarrier.first();
                                log("[Archaeology] Interacting with Legatus Barrier.");
                                if (barrier != null && barrier.interact("Pass")) {
                                    Execution.delay(random.nextLong(3500, 5000));
                                }
                            }
                        }
                    }
                    if (selectedArchNames.contains("Pontifex remains") || selectedArchNames.contains("Orcus altar")) {
                        log("[Archaeology] Traversing to Pontifex barrier.");
                        if (Movement.traverse(NavPath.resolve(new Coordinate(2476, 7570, 0))) == TraverseEvent.State.FINISHED) {
                            EntityResultSet<SceneObject> pontifexBarrier = SceneObjectQuery.newQuery().name("Pontifex barrier").option("Pass").results();
                            if (!pontifexBarrier.isEmpty()) {
                                SceneObject barrier = pontifexBarrier.first();
                                log("[Archaeology] Interacting with Pontifex Barrier.");
                                if (barrier != null && barrier.interact("Pass")) {
                                    Execution.delay(random.nextLong(3500, 5000));
                                }
                            }
                        }
                    }
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
