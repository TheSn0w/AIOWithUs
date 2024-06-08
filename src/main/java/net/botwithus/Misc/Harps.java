package net.botwithus.Misc;

import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.random;

public class Harps {

    public static long interactwithHarps(LocalPlayer player) {
        if (player.getAnimationId() != -1) {
            return random.nextLong(1500, 3000);
        }
        EntityResultSet<SceneObject> harps = SceneObjectQuery.newQuery().name("Harp").results();
        if (player.getAnimationId() == -1 || !player.isMoving()) {
            if (harps.nearest() != null) {
                log("Nearest harp found");
                if (harps.nearest().interact("Play")) {
                    log("Interacting with Harp using 'Play' option");
                } else if (harps.nearest().interact("Tune")) {
                    log("Interacting with Harp using 'Tune' option");
                } else {
                    log("Neither 'Play' nor 'Tune' options are available");
                }
            }
        }
        return random.nextLong(1500, 3000);
    }

    public static long useHarps(LocalPlayer player) {
        EntityResultSet<SceneObject> harps = SceneObjectQuery.newQuery().name("Harp").option("Tune").results();
        if (player.getAnimationId() == 25021) {
            if (harps.nearest() != null) {
                harps.nearest().interact("Tune");
            } else {
                log("No nearest harp found");
            }
        }
        return random.nextLong(1500, 3000);
    }
}