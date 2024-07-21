package net.botwithus.Slayer;

import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
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
import net.botwithus.rs3.util.RandomGenerator;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Laniakea.slayerCape;
import static net.botwithus.Slayer.Main.setSlayerState;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.component;
import static net.botwithus.Variables.Variables.random;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Kuradal {

    public static void TeleporttoKuradal() {
        Coordinate mandrithCoords = new Coordinate(1689, 5286, 1);
        if (getLocalPlayer() == null) return;
        if (!InventoryItemQuery.newQuery(93).name(slayerCape).results().isEmpty()) {
            if (Movement.traverse(NavPath.resolve(mandrithCoords)) == TraverseEvent.State.FINISHED) {
                Execution.delay(getTaskKuradal());
            }
        } else {
            if (Movement.traverse(NavPath.resolve(mandrithCoords)) == TraverseEvent.State.FINISHED) {
                Execution.delay(getTaskKuradal());
            }
        }
    }

    private static long getTaskKuradal() {
        Npc kuradal = NpcQuery.newQuery().name("Kuradal").results().nearest();

        if (kuradal != null) {
            kuradal.interact("Get task");

            boolean taskInterfaceOpened = Execution.delayUntil(10000, () -> Interfaces.isOpen(1191));

            if (taskInterfaceOpened) {
                MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 78053391);
                Execution.delay(RandomGenerator.nextInt(2000, 3000));

                MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 77856776);
                Execution.delay(RandomGenerator.nextInt(1500, 2500));
                setSlayerState(Main.SlayerState.RETRIEVETASKINFO);
            } else {
                setSlayerState(Main.SlayerState.KURADAL);
                log("Failed to open the task interface.");
            }
        } else {
            setSlayerState(Main.SlayerState.KURADAL);
            log("Kuradal is not found.");
        }
        return random.nextLong(1000, 2000);
    }

    public static long skipTaskKuradal() {
        EntityResultSet<Npc> kuradal = NpcQuery.newQuery().name("Kuradal").results();

        if (kuradal.isEmpty()) {
            Coordinate kuradalCoords = new Coordinate(3053, 3952, 0);

            Area area = createAreaAroundCoordinate(kuradalCoords, 1); // Pass 1 as the radius
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

            Area area1 = createAreaAroundCoordinate(kuradalCoords, 1); // Pass 1 as the radius
            Coordinate randomWalkableCoordinate1 = getRandomWalkableCoordinateInArea(area1);

            if (!InventoryItemQuery.newQuery(93).name(slayerCape).results().isEmpty()) {
                if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate1)) == TraverseEvent.State.FINISHED) {
                    log("Teleporting via Slayers Cape.");
                }
            } else {
                if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                    log("Teleporting to Kuradal.");
                }
            }
        }

        if (VarManager.getVarbitValue(9071) >= 30) { // amount of slayer points remaining

            log("Skipping task.");
            if (!kuradal.isEmpty()) {
                log("Interacting with Kuradal.");
                kuradal.nearest().interact("Rewards");
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
                        setSlayerState(Main.SlayerState.KURADAL);
                    }
                }
            }
        } else {
            log("Not enough slayer points to skip task.");
            shutdown();
        }
        return 0;
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
}
