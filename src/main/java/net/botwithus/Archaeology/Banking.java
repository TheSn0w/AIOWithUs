package net.botwithus.Archaeology;

import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.List;

import static net.botwithus.Archaeology.Porters.handleGoteCharges;
import static net.botwithus.Archaeology.Traversal.returnToLastLocation;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.Variables.*;

public class Banking {

    public static long backpackIsFull(LocalPlayer player) {
        if (backpack.contains("Archaeological soil box")) {
            backpack.interact("Archaeological soil box", "Fill");
            log("[Archaeology] Filling soil box.");
            Execution.delay(RandomGenerator.nextInt(1500, 3000));
        }

        if (backpack.isFull()) {
            setBotState(BANKING);
        }

        return random.nextLong(1500, 3000);
    }

    public static long BankforArcheology(LocalPlayer player, List<String> selectedArchNames) {
        Coordinate bankChestCoordinate = new Coordinate(3362, 3397, 0);
        setLastSkillingLocation(player.getCoordinate());
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery()
                .name("Bank chest")
                .option("Use")
                .results();
        if (!results.isEmpty()) {
            Execution.delay(handleBankInteraction(player, selectedArchNames));
        } else {
            log("[Archaeology] Teleporting to bank.");
            TraverseEvent.State traverseState = Movement.traverse(NavPath.resolve(bankChestCoordinate));
            if (traverseState == TraverseEvent.State.FINISHED) {
                log("[Archaeology] Finished traversing to bank.");
                Execution.delay(handleBankInteraction(player, selectedArchNames));
            } else {
                log("[Error] Failed to traverse to bank.");
            }
        }
        return random.nextLong(1500, 3000);
    }

    public static long handleBankInteraction(LocalPlayer player, List<String> selectedArchNames) {
        interactWithBankChest();
        log("[Archaeology] Waiting for bank to open.");
        waitForBankToOpen();

        ResultSet<Item> soilBox = findSoilBoxInInventory();
        if (Interfaces.isOpen(517)) {
            log("[Archaeology] Bank is open.");

            delayRandomly();
            log("[Archaeology] Depositing all items except selected.");
            depositAllExceptSelectedItems();
            logItemsDeposited();

            delayRandomly();
            interactWithSoilBoxIfPresent(soilBox);

            if (useGote) {
                if (VarManager.getVarbitValue(45141) != 1) {
                    component(1, -1, 33882270);
                    Execution.delay(random.nextLong(1000, 2000));
                } else {
                    log("[Combat] Bank Tab value is already 1");
                }
                handleGoteCharges();
            }

            delayRandomly();
            returnToLastLocation(player, selectedArchNames);
            return random.nextLong(1500, 2500);
        } else {
            log("[Error] Bank did not open.");
            return random.nextLong(750, 1250);
        }
    }

    public static void interactWithBankChest() {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery()
                .name("Bank chest")
                .option("Use")
                .results();

        if (!results.isEmpty()) {
            SceneObject nearestBankChest = results.nearest();

            if (nearestBankChest != null) {
                for (int attempts = 0; attempts < 3; attempts++) {
                    if (nearestBankChest.interact("Use")) {
                        log("[Archaeology] Interacted with Bank chest.");
                        Execution.delay(random.nextLong(1500, 2500));
                        return;
                    } else {
                        Execution.delay(random.nextLong(1500, 2500));
                    }
                }
                log("[Error] Failed to interact with Bank chest after 3 attempts.");
            } else {
                log("[Error] No nearest Bank chest found.");
            }
        } else {
            log("[Error] No Bank chest found.");
        }
    }

    private static void waitForBankToOpen() {
        boolean bankOpened = Execution.delayUntil(30000, () -> Interfaces.isOpen(517));
        if (!bankOpened) {
            log("[Error] Bank did not open within 30 seconds.");
        }
    }

    private static ResultSet<Item> findSoilBoxInInventory() {
        return InventoryItemQuery.newQuery(93).name("Archaeological soil box").results();
    }

    private static void delayRandomly() {
        Execution.delay(RandomGenerator.nextInt(1500, 3000));
    }

    private static void depositAllExceptSelectedItems() {
        int[] itemIdsToKeep = {49538, 50096, 4614, 49976, 50431, 49947, 49949, 50753};
        Bank.depositAllExcept(itemIdsToKeep);
    }

    private static void logItemsDeposited() {
        log("[Archaeology] Deposited all items except selected.");
    }

    private static void interactWithSoilBoxIfPresent(ResultSet<Item> soilBox) {
        if (soilBox != null && !soilBox.isEmpty()) {
            int slotIndex = soilBox.first().getSlot();
            if (slotIndex >= 0) {
                boolean interactionSuccess = MiniMenu.interact(ComponentAction.COMPONENT.getType(), 9, slotIndex, 33882127);
                if (interactionSuccess) {
                    log("[Archaeology] Interacted with Archaeological soil box.");
                } else {
                    log("[Error] Failed to interact with Archaeological soil box.");
                }
            } else {
                log("[Error] Invalid slot index for Archaeological soil box.");
            }
        } else {
            log("[Error] No Archaeological soil box found in inventory.");
        }
    }
}
