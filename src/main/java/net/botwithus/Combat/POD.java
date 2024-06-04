package net.botwithus.Combat;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import static net.botwithus.Combat.Combat.attackTarget;
import static net.botwithus.Combat.Potions.*;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class POD {
    private static int currentStep = 1;

    public static void handlePOD() {
        switch (currentStep) {
            case 1:
                if (travelToPOD()) {
                    log("[Combat] Arrived at POD. Proceeding to interaction.");
                    currentStep = 2;
                } else {
                    log("[Combat] Traveling to POD...");
                }
                break;
            case 2:
                if (interactWithKags()) {
                    log("[Combat] Interacted with Kags. Proceeding to the next step.");
                    currentStep = 3;
                }
                break;
            case 3:
                if (interactWithFirstDoor()) {
                    log("[Combat] Interacted with the first door. Proceeding to the next step.");
                    currentStep = 4;
                }
                break;
            case 4:
                if (interactWithOtherDoor()) {
                    log("[Combat] Interacted with the other door. Proceeding to the next step.");
                    currentStep = 5;
                }
                break;
            case 5:
                if (movePlayerEast()) {
                    log("[Combat] Moved player east. Proceeding to the next step.");
                    currentStep = 6;
                }
                break;
            case 6:
                attackTarget(getLocalPlayer());
                if (shouldBank(getLocalPlayer())) {
                    currentStep = 7;
                }
                break;
            case 7:
                if (BankingforPoD(getLocalPlayer())) {
                    currentStep = 1;
                }
                break;

            default:
                log("[Error] Invalid step. Please check the process flow.");
                break;
        }
    }

    private static boolean travelToPOD() {
        NavPath path = NavPath.resolve(new Coordinate(3122, 2632, 0));
        return Movement.traverse(path) == TraverseEvent.State.FINISHED;
    }

    private static boolean interactWithKags() {
        EntityResultSet<Npc> kags = NpcQuery.newQuery().name("Portmaster Kags").option("Travel").results();
        if (!kags.isEmpty()) {
            Npc nearestKags = kags.nearest();
            if (nearestKags != null && nearestKags.interact("Travel")) {
                Execution.delayUntil((5000), () -> Interfaces.isOpen(1188));
                if (Interfaces.isOpen(1188)) {
                    dialog( 0, -1, 77856776);
                    Execution.delay(RandomGenerator.nextInt(5000, 8000));
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean interactWithFirstDoor() {
        EntityResultSet<SceneObject> door = SceneObjectQuery.newQuery().name("Door").option("Open").results();
        if (!door.isEmpty()) {
            SceneObject nearestDoor = door.nearest();
            if (nearestDoor != null && nearestDoor.interact("Enter dungeon")) {
                Execution.delay(RandomGenerator.nextInt(5000, 8000));
                return true;
            }
        }
        return false;
    }

    private static boolean interactWithOtherDoor() {
        EntityResultSet<SceneObject> otherDoor = SceneObjectQuery.newQuery().name("Barrier").option("Pass through").results();
        if (!otherDoor.isEmpty()) {
            SceneObject nearestOtherDoor = otherDoor.nearest();
            if (nearestOtherDoor != null && nearestOtherDoor.interact("Pass through")) {
                Execution.delay(RandomGenerator.nextInt(5000, 8000));
                return true;
            }
        }
        return false;
    }

    private static boolean movePlayerEast() {
        if (getLocalPlayer() != null) {
            Coordinate targetCoordinate = getLocalPlayer().getCoordinate();
            Movement.walkTo(targetCoordinate.getX() + 7, targetCoordinate.getY(), true);
        }
        return true;
    }
    private static boolean BankingforPoD(LocalPlayer player) {
        if (VarManager.getVarbitValue(16779) == 1) {
            ActionBar.useAbility("Soul Split");
        }
        ActionBar.useAbility("War's Retreat Teleport");
        Execution.delay(RandomGenerator.nextInt(6000, 8000));

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
        if (!results.isEmpty()) {
            SceneObject chest = results.nearest();
            if (chest != null) {
                chest.interact("Load Last Preset from");
                Execution.delay(RandomGenerator.nextInt(6000, 8000));
            }
        }
        return true;
    }
    public static boolean shouldBank(LocalPlayer player) {
        long overloadCheck = drinkOverloads(player);
        long prayerCheck = usePrayerOrRestorePots(player);
        long aggroCheck = useAggression(player);
        long weaponPoisonCheck = useWeaponPoison(player);

        return (useWeaponPoison && weaponPoisonCheck == 1L) ||
                (useOverloads && overloadCheck == 1L) ||
                (usePrayerPots && prayerCheck == 1L) ||
                (useAggroPots && aggroCheck == 1L);
    }
}
