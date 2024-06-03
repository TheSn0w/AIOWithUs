package net.botwithus.Agility;

import net.botwithus.Variables.Variables;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;

public class Agility {

    public static final Coordinate RESTART = new Coordinate(2487, 3437, 0);

    private static void interactWithSceneObject(LocalPlayer player, Coordinate location, String objectName, String interaction, Coordinate successLocation) {
        if (player.getCoordinate().equals(location)) {
            SceneObject nearestObject = SceneObjectQuery.newQuery().name(objectName).results().nearest();
            if (nearestObject != null) {
                if (nearestObject.interact(interaction)) {
                    log("[Agility] Interacted with: " + objectName);
                    Execution.delayUntil(random.nextLong(10000, 15000), () -> player.getCoordinate().equals(successLocation));
                    Execution.delay(random.nextLong(500, 1500));
                }
            }
        }
    }

    public static long handleSkillingAgility(LocalPlayer player) {
        int agilityLevel = Skills.AGILITY.getActualLevel();

        if (agilityLevel >= 1 && agilityLevel <= 34) {
            log("[Agility] Using Tree Gnome agility course.");

            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(69526).option("Walk-across").results();
            if (results.isEmpty() && !player.isMoving()) {
                log("[Agility] Walking to the starting point of the agility course.");
                Movement.traverse(NavPath.resolve(Variables.LOG_BALANCE));
                return random.nextLong(1500, 3000);
            }
            if (!results.isEmpty() && !player.isMoving() && !LOG_BALANCE.equals(player.getCoordinate())) {
                log("[Agility] Player is not at the correct location, walking to LOG_BALANCE.");
                Movement.walkTo(LOG_BALANCE.getX(), LOG_BALANCE.getY(), true);
                return random.nextLong(1500, 3000);
            }

            interactWithSceneObject(player, LOG_BALANCE, "Log balance", "Walk-across", OBSTACLE_NET);
            interactWithSceneObject(player, OBSTACLE_NET, "Obstacle net", "Climb-over", TREE_BRANCH);
            interactWithSceneObject(player, TREE_BRANCH, "Tree branch", "Climb", BALANCING_ROPE);
            interactWithSceneObject(player, BALANCING_ROPE, "Balancing rope", "Walk-on", TREE_BRANCH_2);
            interactWithSceneObject(player, TREE_BRANCH_2, "Tree branch", "Climb-down", OBSTACLE_NET_2);
            interactWithSceneObject(player, OBSTACLE_NET_2, "Obstacle net", "Climb-over", OBSTACLE_PIPE);
            interactWithSceneObject(player, OBSTACLE_PIPE, "Obstacle pipe", "Squeeze-through", RESTART);
        }
        return random.nextLong(1500, 3000);
    }



}
