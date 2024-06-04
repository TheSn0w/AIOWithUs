package net.botwithus.Combat;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.random;

public class Radius {

    public static boolean enableRadiusTracking = false;
    public static Coordinate centerCoordinate = new Coordinate(0, 0, 0);
    public static int radius = 10;

    public static boolean isWithinRadius(LocalPlayer player) {
        if (player == null) return false;
        return Distance.between(player.getCoordinate(), centerCoordinate) <= radius;
    }

    public static long ensureWithinRadius(LocalPlayer player) {
        if (!isWithinRadius(player)) {
            Movement.walkTo(centerCoordinate.getX(), centerCoordinate.getY(), true);
            Execution.delayUntil(15000, () -> isWithinRadius(player));
            log("[Combat] Moved player back to center.");
        }
        return random.nextLong(1500, 3000);
    }

    public static void setCenterCoordinate(Coordinate newCenter) {
        centerCoordinate = newCenter;
        log("[Combat] Center coordinate set to: " + newCenter);
    }
}
