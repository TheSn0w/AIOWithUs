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

import static net.botwithus.Combat.Combat.attackTarget;
import static net.botwithus.Combat.POD.shouldBank;
import static net.botwithus.Variables.Variables.component;
import static net.botwithus.Variables.Variables.random;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class ArchGlacor {
    private static int currentStep = 1;

    public static long handleArchGlacor() {
        switch (currentStep) {
            case 1:
                if (travelToArchGlacor(getLocalPlayer())) {
                    currentStep = 2;
                }
                break;
            case 2:
                attackTarget(getLocalPlayer());
                Execution.delay(printRemainingTime());
                Component timerComponent = getTimerComponent();
                if (shouldBank(getLocalPlayer()) || isTimerZero(timerComponent) || Backpack.isFull()) {
                    currentStep = 3;
                }
                break;
            case 3:
                if (BankingforArch(getLocalPlayer())) {
                    currentStep = 1;
                }
                break;
            default:
                break;
        }
        return 0;
    }

    private static boolean BankingforArch(LocalPlayer player) {
        ActionBar.useAbility("Max Guild Teleport");
        Execution.delay(RandomGenerator.nextInt(6000, 8000));

        EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Bank").results();
        if (!results.isEmpty()) {
            Npc banker = results.nearest();
            if (banker != null) {
                banker.interact("Load Last Preset from");
                Execution.delay(RandomGenerator.nextInt(3000, 4000));
                return true;
            }
        }
        return false;
    }

    private static boolean travelToArchGlacor(LocalPlayer player) {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Arch-Glacor portal").option("Enter").results();
        if (!results.isEmpty()) {
            SceneObject portal = results.nearest();
            if (portal != null) {
                portal.interact("Enter");
                Execution.delayUntil(random.nextLong(15000, 20000), () -> {
                    EntityResultSet<SceneObject> Aqueduct = SceneObjectQuery.newQuery().name("Aqueduct Portal").option("Enter").results();
                    return !Aqueduct.isEmpty();
                });
            }
        }

        EntityResultSet<SceneObject> Aqueduct = SceneObjectQuery.newQuery().name("Aqueduct Portal").option("Enter").results();
        while (Aqueduct.isEmpty()) {
            Execution.delay(1000);
            Aqueduct = SceneObjectQuery.newQuery().name("Aqueduct Portal").option("Enter").results();
        }

        SceneObject nearestAqueduct = Aqueduct.nearest();
        nearestAqueduct.interact("Enter");
        Execution.delayUntil(random.nextLong(10000, 15000), () -> Interfaces.isOpen(1591));

        if (Interfaces.isOpen(1591)) {
            component( 1, -1, 104267836);
            Execution.delay(random.nextLong(2500, 3500));
        } else {
            return false;
        }

        Movement.walkTo(player.getCoordinate().getX() + 11, player.getCoordinate().getY() -4 , true);

        return true;
    }

    private static Component getTimerComponent() {
        return ComponentQuery.newQuery(861).componentIndex(8).results().first();
    }

    public static boolean isTimerZero(Component timerComponent) {
        String timerText = timerComponent.getText();
        return "00:00".equals(timerText);
    }
    public static long printRemainingTime() {
        Component timerComponent = getTimerComponent();
        String remainingTime = timerComponent.getText();
        /*log("[Combat] Remaining time: " + remainingTime);*/
        return random.nextLong(1500, 3000);
    }
}
