package net.botwithus.Combat;

import net.botwithus.SnowsScript;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.Combat.Combat.attackTarget;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.SnowsScript.getBotState;
import static net.botwithus.Variables.Runnables.handleCombat;
import static net.botwithus.Variables.Variables.random;

public class Radius {

    public static boolean enableRadiusTracking = false;
    public static Coordinate centerCoordinate = new Coordinate(0, 0, 0);
    public static int radius = 3;

    public static boolean isWithinRadius(LocalPlayer player) {
        if (player == null) return false;
        return Distance.between(player.getCoordinate(), centerCoordinate) <= radius;
    }

    public static void ensureWithinRadius(LocalPlayer player) {
        if (getBotState() == SKILLING) {
            if (!isWithinRadius(player)) {
                double dx = centerCoordinate.getX() - player.getCoordinate().getX();
                double dy = centerCoordinate.getY() - player.getCoordinate().getY();
                double length = Math.sqrt(dx * dx + dy * dy);
                double normalizedDx = dx / length;
                double normalizedDy = dy / length;
                double targetX = centerCoordinate.getX() - normalizedDx * (radius - 1);
                double targetY = centerCoordinate.getY() - normalizedDy * (radius - 1);
                Movement.walkTo((int) targetX, (int) targetY, false);
                Execution.delayUntil(random.nextLong(3500, 5000), () -> isWithinRadius(player));
                log("[Combat] Moving player back inside the radius.");
                attackTarget(player);
            }
        }
    }

    public static void setCenterCoordinate(Coordinate newCenter) {
        centerCoordinate = newCenter;
        log("[Combat] Center coordinate set to: " + newCenter);
    }
}
