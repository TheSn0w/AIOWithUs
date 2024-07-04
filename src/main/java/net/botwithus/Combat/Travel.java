package net.botwithus.Combat;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;

import static net.botwithus.CustomLogger.log;

public class Travel {
    public static boolean useTraveltoLocation = false;
    public static int x = 0;
    public static int y = 0;
    public static int z = 0;

    public static void travelToXYZ(int x, int y, int z) {
        Coordinate newLocation = new Coordinate(x, y, z);
        log("[Combat] Traveling to " + newLocation + "...");
        TraverseEvent.State traverseState = Movement.traverse(NavPath.resolve(newLocation));

        if (traverseState == TraverseEvent.State.FINISHED) {
            log("[Combat] Arrived at destination.");
        } else if (traverseState == TraverseEvent.State.NO_PATH) {
            log("[Combat] No path to destination, enter new destination.");
        } else if (traverseState == TraverseEvent.State.FAILED) {
            log("[Combat] Failed path to destination, enter new destination.");
        } else if (traverseState == TraverseEvent.State.INTERRUPTED) {
            log("[Combat] Path to destination was interrupted, enter new destination.");
        } else {
            log("[Combat] Unexpected state: " + traverseState);
        }
        useTraveltoLocation = false;
    }
}
