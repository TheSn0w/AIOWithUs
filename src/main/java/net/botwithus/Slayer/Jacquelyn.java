package net.botwithus.Slayer;

import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Laniakea.slayerCape;
import static net.botwithus.Slayer.Main.setSlayerState;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.component;

public class Jacquelyn {

        public static void TeleporttoJacquelyn() {
        Coordinate jacquelynCoordinates = new Coordinate(3221, 3224, 0);

        Area area = createAreaAroundCoordinate(jacquelynCoordinates, 1); // Pass 1 as the radius
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
            getTaskJacquelyn();
        }
    }

    private static Area createAreaAroundCoordinate(Coordinate center, int radius) {
        Coordinate topLeft = new Coordinate(center.getX() - radius, center.getY() + radius, center.getZ());
        Coordinate bottomRight = new Coordinate(center.getX() + radius, center.getY() - radius, center.getZ());

        Area area = new Area.Rectangular(topLeft, bottomRight);
        log("Created area with top left coordinate: " + topLeft + " and bottom right coordinate: " + bottomRight);
        return area;
    }

    private static Coordinate getRandomWalkableCoordinateInArea(Area area) {
        Coordinate randomCoordinate = area.getRandomWalkableCoordinate();
        log("Selected random walkable coordinate: " + randomCoordinate);
        return randomCoordinate;
    }


    public static void getTaskJacquelyn() {
        EntityResultSet<Npc> jaquelyn = NpcQuery.newQuery().name("Jacquelyn").option("Talk to").results();
        if (!jaquelyn.isEmpty()) {
            jaquelyn.nearest().interact("Get task");
            Execution.delayUntil(10000, () -> Interfaces.isOpen(1191));
            if (Interfaces.isOpen(1191)) {
                Execution.delay(random.nextLong(800, 1000));
                log("I need another Assignment");
                dialog(0, -1, 78053391);
                Execution.delayUntil(10000, () -> Interfaces.isOpen(1184) || Interfaces.isOpen(1188));
                if (Interfaces.isOpen(1184)) {
                    Execution.delay(random.nextLong(800, 1000));
                    dialog(0, -1, 77594639);
                    Execution.delayUntil(10000, () -> Interfaces.isOpen(1188));
                    if (Interfaces.isOpen(1188)) {
                        Execution.delay(random.nextLong(800, 1000));
                        dialog(0, -1, 77856786);
                        getTaskJacquelyn();
                    }
                } else {
                    if (Interfaces.isOpen(1188)) {
                        Execution.delay(random.nextLong(800, 1000));
                        dialog(0, -1, 77856776);
                        setSlayerState(Main.SlayerState.RETRIEVETASKINFO);
                    }
                }
            }
        }
    }

    public static long skipTaskJacquelyn() {
        EntityResultSet<Npc> jacquelyn = NpcQuery.newQuery().name("Jacquelyn").results();

        if (jacquelyn.isEmpty()) {
            Coordinate jacquelynCoords = new Coordinate(3221, 3224, 0);

            Area area = createAreaAroundCoordinate(jacquelynCoords, 1); // Pass 1 as the radius
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

            Area area1 = createAreaAroundCoordinate(jacquelynCoords, 1); // Pass 1 as the radius
            Coordinate randomWalkableCoordinate1 = getRandomWalkableCoordinateInArea(area1);

            if (!InventoryItemQuery.newQuery(93).name(slayerCape).results().isEmpty()) {
                if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate1)) == TraverseEvent.State.FINISHED) {
                    log("Teleporting via Slayers Cape.");
                }
            } else {
                if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                    log("Teleporting to Jacquelyn.");
                }
            }
        }

        if (VarManager.getVarbitValue(9071) >= 30) { // amount of slayer points remaining

            log("Skipping task.");
            if (!jacquelyn.isEmpty()) {
                log("Interacting with Jacquelyn.");
                jacquelyn.nearest().interact("Rewards");
                Execution.delayUntil(random.nextLong(3000, 5000), () -> Interfaces.isOpen(1308));
                Execution.delay(random.nextLong(800, 1100));
                if (!ComponentQuery.newQuery(1308).componentIndex(21).subComponentIndex(-1).results().isEmpty()) {
                    log("Interacting with Assignments");
                    component(1, -1, 85721106);
                    Execution.delay(random.nextLong(800, 1100));
                    if (!ComponentQuery.newQuery(1308).componentIndex(555).subComponentIndex(-1).results().isEmpty()) {
                        log("Cancelling task.");
                        component(1, -1, 85721639);
                        Execution.delay(random.nextLong(1500, 3000));
                        log("Getting new task.");
                        setSlayerState(Main.SlayerState.JACQUELYN);
                    }
                }
            }
        } else {
            log("Not enough slayer points to skip task.");
            shutdown();
        }
        return 0;
    }
}
