package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.botwithus.Combat.Combat.attackTarget;
import static net.botwithus.Combat.POD.shouldBank;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class ArchGlacor {

    public static String teleportAbility;
    private static int currentStep = 1;

    public static long handleArchGlacor() {

        switch (currentStep) {
            case 1:
                if (BankingforArch(getLocalPlayer())) {
                    currentStep = 2;
                }
                break;
            case 2:
                if (travelToArchGlacor(getLocalPlayer(), teleportAbility)) {
                    currentStep = 3;
                }
                break;
            case 3:
                if (!getTargetNames().contains("arch-glacor")) {
                    addTargetName("arch-glacor");
                }
                attackTarget(getLocalPlayer());
                Component timerComponent = getTimerComponent();
                if (shouldBank(getLocalPlayer()) || isTimerZero(timerComponent) || Backpack.isFull()) {
                    currentStep = 1;
                }
                break;
            default:
                break;
        }
        return 0;
    }


    private static boolean BankingforArch(LocalPlayer player) {
        if (ActionBar.containsAbility("Max Guild Teleport")) {
            teleportAbility = "Max Guild Teleport";
        } else if (ActionBar.containsAbility("War's Retreat Teleport")) {
            teleportAbility = "War's Retreat Teleport";
        }

        if (teleportAbility != null) {
            log("[Combat] Teleporting to bank using " + teleportAbility);
            ActionBar.useAbility(teleportAbility);
            Execution.delay(RandomGenerator.nextInt(6000, 8000));

            String entityName = teleportAbility.equals("Max Guild Teleport") ? "Banker" : "Bank chest";
            return interactWithBankEntity(entityName);
        }

        return false;
    }

    private static boolean interactWithBankEntity(String entityName) {
        if (entityName.equals("Banker")) {
            EntityResultSet<Npc> results = NpcQuery.newQuery().name(entityName).option("Bank").results();
            if (!results.isEmpty()) {
                Npc banker = results.nearest();
                if (banker != null) {
                    log("Interacting with Banker");
                    banker.interact("Load Last Preset from");
                    Execution.delay(random.nextLong(6000, 7000));
                    log("Finished interacting with Banker");
                    return true;
                } else {
                    log("Banker is null");
                }
            } else {
                log("No Banker found");
            }
        } else {
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name(entityName).option("Use").results();
            if (!results.isEmpty()) {
                SceneObject bankChest = results.nearest();
                if (bankChest != null) {
                    log("Interacting with Bank Chest");
                    bankChest.interact("Load Last Preset from");
                    Execution.delay(random.nextLong(6000, 7000));
                    log("Finished interacting with Bank Chest");
                    return true;
                } else {
                    log("Bank Chest is null");
                }
            } else {
                log("No Bank Chest found");
            }
        }
        log("Failed to interact with " + entityName);
        return false;
    }

    private static boolean travelToArchGlacor(LocalPlayer player, String teleportAbility) {
        String portalName = teleportAbility.equals("Max Guild Teleport") ? "Arch-Glacor portal" : "Portal (Arch-Glacor)";
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name(portalName).option("Enter").results();
        if (!results.isEmpty()) {
            SceneObject portal = results.nearest();
            if (portal != null) {
                portal.interact("Enter");
                log("Interacted with portal: " + portalName);
                Execution.delayUntil(random.nextLong(15000, 20000), () -> {
                    EntityResultSet<SceneObject> Aqueduct = SceneObjectQuery.newQuery().name("Aqueduct Portal").option("Enter").results();
                    return !Aqueduct.isEmpty() && Aqueduct.nearest() != null;
                });
            }
        }

        EntityResultSet<SceneObject> Aqueduct = SceneObjectQuery.newQuery().name("Aqueduct Portal").option("Enter").results();
        int attempts = 0;
        while ((Aqueduct.isEmpty() || Aqueduct.nearest() == null) && attempts < 15) {
            Execution.delay(1000);
            Aqueduct = SceneObjectQuery.newQuery().name("Aqueduct Portal").option("Enter").results();
            attempts++;
            log("Attempt " + attempts + " to find Aqueduct Portal");
        }

        if (Aqueduct.isEmpty() || Aqueduct.nearest() == null) {
            log("Failed to find Aqueduct Portal after " + attempts + " attempts");
            return false;
        }

        SceneObject nearestAqueduct = Aqueduct.nearest();
        if (nearestAqueduct != null) {
            nearestAqueduct.interact("Enter");
            log("Interacted with Aqueduct Portal");
            Execution.delayUntil(random.nextLong(10000, 15000), () -> Interfaces.isOpen(1591));

            if (Interfaces.isOpen(1591)) {
                component( 1, -1, 104267836);
                Execution.delay(random.nextLong(2500, 3500));
            } else {
                log("Interface 1591 is not open");
                return false;
            }

            Movement.walkTo(player.getCoordinate().getX() + 11, player.getCoordinate().getY() -4 , true);
            log("Moved to new coordinates");

            return true;
        }
        log("Failed to interact with Aqueduct Portal");
        return false;
    }


    public static void printRemainingTime() {
        Component timerComponent = getTimerComponent();
        String remainingTime = timerComponent.getText();
        log("[Combat] Remaining time: " + remainingTime);
    }

    private static Component getTimerComponent() {
        return ComponentQuery.newQuery(861).componentIndex(8).results().first();
    }

    public static boolean isTimerZero(Component timerComponent) {
        String timerText = timerComponent.getText();
        return "00:00".equals(timerText);
    }
}
