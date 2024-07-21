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
import static net.botwithus.Combat.Food.eatFood;
import static net.botwithus.Combat.POD.PODStep.*;
import static net.botwithus.Combat.Potions.*;
import static net.botwithus.Combat.Prayers.*;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class POD {
    private static Coordinate targetCoordinate = null;

    public enum PODStep {
        TRAVEL_TO_POD,
        INTERACT_WITH_KAGS,
        INTERACT_WITH_FIRST_DOOR,
        INTERACT_WITH_OTHER_DOOR,
        MOVE_PLAYER_EAST,
        COMBAT,
        BANKING
    }

    public static PODStep getCurrentStep() {
        return currentStep;
    }

    public static void setCurrentStep(PODStep step) {
        currentStep = step;
    }

    private static PODStep currentStep = PODStep.TRAVEL_TO_POD;

    public static void handlePOD() {
        switch (currentStep) {
            case TRAVEL_TO_POD:
                log("[Combat] Traveling to POD...");
                if (travelToPOD()) {
                    setCurrentStep(INTERACT_WITH_KAGS);
                }
                break;
            case INTERACT_WITH_KAGS:
                if (interactWithKags()) {
                    log("[Combat] Interacted with Kags. Proceeding to the next step.");
                    currentStep = INTERACT_WITH_FIRST_DOOR;
                }
                break;
            case INTERACT_WITH_FIRST_DOOR:
                if (interactWithFirstDoor()) {
                    log("[Combat] Interacted with the first door. Proceeding to the next step.");
                    currentStep = INTERACT_WITH_OTHER_DOOR;
                }
                break;
            case INTERACT_WITH_OTHER_DOOR:
                if (interactWithOtherDoor()) {
                    if (SoulSplit && VarManager.getVarbitValue(16779) == 0) {
                        activateSoulSplit((LocalPlayer) player);
                    }
                    if (usequickPrayers) {
                        activateQuickPrayers();
                        log("[Combat] Interacted with the other door. Proceeding to the next step.");
                        Execution.delay(random.nextInt(1000, 1500));
                        currentStep = MOVE_PLAYER_EAST;
                    }
                }
                break;
            case MOVE_PLAYER_EAST:
                targetCoordinate = movePlayerEast();
                if (targetCoordinate != null) {
                    log("[Combat] Moved player east. Proceeding to the next step.");
                    currentStep = COMBAT;
                }
                break;
            case COMBAT:
                if (shouldBank((LocalPlayer) player)) {
                    currentStep = BANKING;
                } else {
                    attackTarget(getLocalPlayer());
                }
                break;
            case BANKING:
                if (BankingforPoD(getLocalPlayer())) {
                    currentStep = TRAVEL_TO_POD;
                }
                break;
            default:
                log("[Error] Invalid step. Please check the process flow.");
                break;
        }
    }


    private static boolean travelToPOD() {
        log("[Combat] Traveling to POD...");
        NavPath path = NavPath.resolve(new Coordinate(3122, 2632, 0));
        boolean success = Movement.traverse(path) == TraverseEvent.State.FINISHED;
        if (!success) {
            log("[Combat] Failed to travel to POD.");
        }
        return success;
    }

    private static boolean interactWithKags() {
        EntityResultSet<Npc> kags = NpcQuery.newQuery().name("Portmaster Kags").option("Travel").results();
        if (!kags.isEmpty()) {
            Npc nearestKags = kags.nearest();
            log("[Combat] Interacting with Kags...");
            if (nearestKags != null && nearestKags.interact("Travel")) {
                boolean dialogOpened = Execution.delayUntil(5000, () -> Interfaces.isOpen(1188));
                if (dialogOpened) {
                    dialog(0, -1, 77856776);
                    Execution.delay(random.nextInt(5000, 6000));
                    return true;
                } else {
                    log("[Combat] Dialog with Kags did not open.");
                }
            } else {
                log("[Combat] Failed to interact with Kags.");
            }
        } else {
            log("[Combat] Kags not found.");
        }
        return false;
    }

    private static boolean interactWithFirstDoor() {
        EntityResultSet<SceneObject> door = SceneObjectQuery.newQuery().name("Door").option("Open").results();
        if (!door.isEmpty()) {
            SceneObject nearestDoor = door.nearest();
            log("[Combat] Interacting with the first door...");
            if (nearestDoor != null && nearestDoor.interact("Enter dungeon")) {
                Execution.delay(random.nextInt(5000, 8000));
                return true;
            } else {
                log("[Combat] Failed to interact with the first door.");
            }
        } else {
            log("[Combat] First door not found.");
        }
        return false;
    }

    private static boolean interactWithOtherDoor() {
        EntityResultSet<SceneObject> otherDoor = SceneObjectQuery.newQuery().name("Barrier").option("Pass through").results();
        if (!otherDoor.isEmpty()) {
            SceneObject nearestOtherDoor = otherDoor.nearest();
            log("[Combat] Interacting with the other door...");
            if (nearestOtherDoor != null && nearestOtherDoor.interact("Pass through")) {
                Execution.delay(random.nextInt(5000, 8000));
                return true;
            } else {
                log("[Combat] Failed to interact with the other door.");
            }
        } else {
            log("[Combat] Other door not found.");
        }
        return false;
    }

    private static Coordinate movePlayerEast() {
        LocalPlayer player = getLocalPlayer();
        if (player != null) {
            log("[Combat] Moving player east...");
            Coordinate targetCoordinate = player.getCoordinate();
            Coordinate targetPosition = new Coordinate(targetCoordinate.getX() + 7, targetCoordinate.getY(), targetCoordinate.getZ());

            while (!player.getCoordinate().equals(targetPosition)) {
                Movement.walkTo(targetPosition.getX(), targetPosition.getY(), true);
                Execution.delay(random.nextInt(900, 1100));
                if (player.getCoordinate().equals(targetPosition)) {
                    log("[Combat] Player has reached the target position.");
                    return targetPosition;
                }
            }
        } else {
            log("[Combat] Local player is null.");
        }
        return null;
    }


    private static boolean BankingforPoD(LocalPlayer player) {
        log("[Banking] Starting banking process for PoD.");

        // Check and use Soul Split if the condition is met
        if (VarManager.getVarbitValue(16779) == 1) {
            log("[Banking] Using Soul Split.");
            ActionBar.useAbility("Soul Split");
        }

        // Check for Max Guild Teleport ability
        if (ActionBar.containsAbility("Max guild Teleport")) {
            log("[Banking] Using Max Guild Teleport.");
            ActionBar.useAbility("Max guild Teleport");
            Execution.delay(RandomGenerator.nextInt(5500, 7000));

            EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Bank").results();
            if (!results.isEmpty()) {
                Npc banker = results.nearest();
                if (banker != null) {
                    log("[Banking] Interacting with Banker.");
                    banker.interact("Load Last Preset from");
                    Execution.delay(RandomGenerator.nextInt(6000, 8000));
                    return true;
                } else {
                    log("[Banking] Banker not found.");
                }
            } else {
                log("[Banking] No bankers found.");
            }
        } else if (ActionBar.containsAbility("War's Retreat Teleport")) { // Check for War's Retreat Teleport ability
            log("[Banking] Using War's Retreat Teleport.");
            ActionBar.useAbility("War's Retreat Teleport");
            Execution.delay(RandomGenerator.nextInt(5500, 7000));

            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
            if (!results.isEmpty()) {
                SceneObject chest = results.nearest();
                if (chest != null) {
                    log("[Banking] Interacting with Bank chest.");
                    chest.interact("Load Last Preset from");
                    Execution.delay(RandomGenerator.nextInt(6000, 8000));
                    return true;
                } else {
                    log("[Banking] Bank chest not found.");
                }
            } else {
                log("[Banking] No bank chests found.");
            }
        } else {
            log("[Banking] No suitable teleport abilities found.");
        }

        log("[Banking] Banking process completed.");
        return false;
    }



    public static boolean shouldBank(LocalPlayer player) {

        long overloadCheck = drinkOverloads(player);
        long prayerCheck = usePrayerOrRestorePots(player);
        long aggroCheck = useAggression(player);
        long weaponPoisonCheck = useWeaponPoison(player);
        long foodCheck = shouldEatFood ? eatFood(player) : 0;

        boolean needsBanking = (useWeaponPoison && weaponPoisonCheck == 1L) ||
                (useOverloads && overloadCheck == 1L) ||
                (usePrayerPots && prayerCheck == 1L) ||
                (shouldEatFood && foodCheck == 1L) ||
                (useAggroPots && aggroCheck == 1L);

        if (needsBanking) {
            log("[Banking Check] Banking is required.");
        }

        return needsBanking;
    }

}
