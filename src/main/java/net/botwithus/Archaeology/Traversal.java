package net.botwithus.Archaeology;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;

import java.util.List;

import static net.botwithus.Archaeology.Daeminheim.*;
import static net.botwithus.Archaeology.Stormguard.shouldTraverseToStormguard;
import static net.botwithus.Archaeology.Stormguard.traverseToStormguard;
import static net.botwithus.Archaeology.TraverseHellfire.shouldTraverseToHellfire;
import static net.botwithus.Archaeology.TraverseHellfire.traverseToHellfireLift;
import static net.botwithus.Archaeology.TraverseKharidEt.shouldTraverseToKharidEt;
import static net.botwithus.Archaeology.TraverseKharidEt.traverseToKharidEt;
import static net.botwithus.Archaeology.TraversetoWarforge.shouldTraverseToWarforge;
import static net.botwithus.Archaeology.TraversetoWarforge.traverseToWarforge;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.SnowsScript.getBotState;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.Variables.Variables.dialog;
import static net.botwithus.Variables.Variables.lastSkillingLocation;

public class Traversal {

    public static void returnToLastLocation(LocalPlayer player, List<String> selectedArchNames) {

        if (shouldTraverseToStormguard(selectedArchNames)) {
            traverseToStormguard(selectedArchNames);
        }
        if (shouldTraverseToHellfire(selectedArchNames)) {
            traverseToHellfireLift(selectedArchNames);
        }
        if (shouldTraverseToKharidEt(selectedArchNames)) {
            traverseToKharidEt(selectedArchNames);
        }
        if (shouldTraverseToWarforge(selectedArchNames)) {
            traverseToWarforge(selectedArchNames);
        }
        if (shouldTraverseToDaeminheimWarpedFloor(selectedArchNames)) {
            traverseToDaeminheimWarpedFloor(selectedArchNames);
        }
        if (shouldTraverseToDaeminheimUpstairs(selectedArchNames)) {
            traverseToDaeminheimUpstairs(selectedArchNames);
        }
        if (selectedArchNames.contains("Castle wall rubble") || (selectedArchNames.contains("Tunnelling equipment repository"))) {
            EntityResultSet<Npc> banker = NpcQuery.newQuery().name("Fremennik banker").option("Bank").results();
            if (banker.isEmpty()) {
                if (Movement.traverse(NavPath.resolve(new Coordinate(3449, 3719, 0))) == TraverseEvent.State.FINISHED) {
                    log("[Archaeology] Arrived at banker.");
                }
            }
            Movement.walkTo(lastSkillingLocation.getX(), lastSkillingLocation.getY(), true);
            setBotState(SKILLING);
        } else {
            if (getBotState() != SKILLING) {
                traverseToLastSkillingLocation();
            }
        }
    }

    public static void interactWithDialogOption(List<String> selectedArchNames) {
        if (Interfaces.isOpen(720)) {
            log("[Archaeology] Interface is Open.");
            // Hellfire Lift
            if ((selectedArchNames.contains("Sacrificial altar") || selectedArchNames.contains("Dis dungeon debris") || selectedArchNames.contains("Cultist footlocker"))) {
                dialog(0, -1, 47185940);
                log("[Archaeology] Selecting: Dungeon of Disorder.");
            } else if ((selectedArchNames.contains("Lodge bar storage") || selectedArchNames.contains("Lodge art storage"))) {
                dialog(0, -1, 47185921);
                log("[Archaeology] Selecting: Star Lodge cellar.");

                // Kharid-et
            } else if ((selectedArchNames.contains("Legionary remains") || selectedArchNames.contains("Castra debris") || selectedArchNames.contains("Administratum debris") || selectedArchNames.contains("Material cache (imperial steel)") || selectedArchNames.contains("Material cache (Zarosian insignia)") || selectedArchNames.contains("Kharid-et chapel debris") || selectedArchNames.contains("Orcus altar") || selectedArchNames.contains("Pontifex remains") || selectedArchNames.contains("Culinarum debris") || selectedArchNames.contains("Armarium debris"))) {
                dialog(0, -1, 47185921);
                log("[Archaeology] Selecting: Main fortress.");
            } else if (selectedArchNames.contains("Praetorian remains") || selectedArchNames.contains("War table debris") || selectedArchNames.contains("Ancient magick munitions")) {
                dialog(0, -1, 47185943);
                log("[Archaeology] Selecting: Praetorium.");
            } else if (selectedArchNames.contains("Praesidio remains") || selectedArchNames.contains("Carcerem debris")) {
                dialog(0, -1, 47185940);
                log("[Archaeology] Selecting: Prison block.");

                // Warforge
            } else if (selectedArchNames.contains("Gladiatorial goblin remains") || selectedArchNames.contains("Crucible stands debris")) {
                dialog(0, -1, 47185921);
                log("[Archaeology] Selecting: Crucible.");
            } else if (selectedArchNames.contains("Goblin dorm debris") || selectedArchNames.contains("Big High War God shrine") || selectedArchNames.contains("Yu'biusk animal pen") || selectedArchNames.contains("Yu'biusk clay pit") || selectedArchNames.contains("Goblin trainee remains") || selectedArchNames.contains("Kyzaj champion's boudoir") || selectedArchNames.contains("Warforge scrap pile") || selectedArchNames.contains("Warforge weapon rack") || selectedArchNames.contains("Makeshift pie oven") || selectedArchNames.contains("Material cache (vulcanised rubber)")) {
                dialog(0, -1, 47185940);
                log("[Archaeology] Selecting: Warforge tunnels.");
            } else {
                log("[Error] No valid dialog option found.");
            }
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
