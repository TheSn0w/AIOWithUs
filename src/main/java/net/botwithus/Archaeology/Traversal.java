package net.botwithus.Archaeology;

import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;

import java.util.List;

import static net.botwithus.Archaeology.TraverseHellfire.shouldTraverseToHellfire;
import static net.botwithus.Archaeology.TraverseHellfire.traverseToHellfireLift;
import static net.botwithus.Archaeology.TraverseKharidEt.shouldTraverseToKharidEt;
import static net.botwithus.Archaeology.TraverseKharidEt.traverseToKharidEt;
import static net.botwithus.Archaeology.TraversetoWarforge.shouldTraverseToWarforge;
import static net.botwithus.Archaeology.TraversetoWarforge.traverseToWarforge;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.Variables.Variables.dialog;
import static net.botwithus.Variables.Variables.lastSkillingLocation;

public class Traversal {

    public static void returnToLastLocation(LocalPlayer player, List<String> selectedArchNames) {
        log("[Archaeology] Returning to last location");

        if (shouldTraverseToHellfire(selectedArchNames)) {
            traverseToHellfireLift(selectedArchNames);
        }
        if (shouldTraverseToKharidEt(selectedArchNames)) {
            traverseToKharidEt(selectedArchNames);
        }
        if (shouldTraverseToWarforge(selectedArchNames)) {
            traverseToWarforge(selectedArchNames);
        } else {
            traverseToLastSkillingLocation();
        }
    }

    public static void interactWithDialogOption(List<String> selectedArchNames) {
        if ((selectedArchNames.contains("Sacrificial altar") || selectedArchNames.contains("Dis dungeon debris") || selectedArchNames.contains("Cultist footlocker"))) {
            dialog(0, -1, 47185940);
            log("[Archaeology] Selecting: Dungeon of Disorder.");
        } else if ((selectedArchNames.contains("Lodge bar storage") || selectedArchNames.contains("Lodge art storage"))) {
            dialog(0, -1, 47185921);
            log("[Archaeology] Selecting: Star Lodge cellar.");
        } else if ((selectedArchNames.contains("Legionary remains") || selectedArchNames.contains("Castra debris") || selectedArchNames.contains("Administratum debris"))) {
            dialog(0, -1, 47185921);
            log("[Archaeology] Selecting: Main Fortress.");
        } else if (selectedArchNames.contains("Praesidio remains") || selectedArchNames.contains("Carcerem debris")) {
            dialog(0, -1, 47185940);
            log("[Archaeology] Selecting: Prison block.");
        } else if (selectedArchNames.contains("Gladiatorial goblin remains") || selectedArchNames.contains("Crucible stands debris")) {
            dialog(0, -1, 47185921);
            log("[Archaeology] Selecting: Crucible.");
        } else if (selectedArchNames.contains("Goblin dorm debris") || selectedArchNames.contains("Big High War God shrine") || selectedArchNames.contains("Yu'biusk animal pen") || selectedArchNames.contains("Yu'biusk clay pit") || selectedArchNames.contains("Goblin trainee remains") || selectedArchNames.contains("Kyzaj champion's boudoir") || selectedArchNames.contains("Warforge scrap pile") || selectedArchNames.contains("Warforge weapon rack") || selectedArchNames.contains("Makeshift pie oven")) {
            dialog(0, -1, 47185940);
            log("[Archaeology] Selecting: Warforge tunnels.");
        } else {
            log("[Error] No valid dialog option found.");
        }
    }

    public static void traverseToLastSkillingLocation() {
        if (Movement.traverse(NavPath.resolve(lastSkillingLocation)) == TraverseEvent.State.FINISHED) {
            log("[Archaeology] Finished traversing to last location.");
            setBotState(SKILLING);
        } else {
            log("[Error] Failed to traverse to last location.");
        }
    }
}
