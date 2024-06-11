package net.botwithus.Archaeology;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.LootInventory;
import net.botwithus.inventory.backpack;
import net.botwithus.inventory.equipment;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.Entity;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.List;

import static net.botwithus.Archaeology.Daeminheim.shouldTraverseToDaeminheimUpstairs;
import static net.botwithus.Archaeology.Daeminheim.shouldTraverseToDaeminheimWarpedFloor;
import static net.botwithus.Archaeology.Porters.handleGoteCharges;
import static net.botwithus.Archaeology.Traversal.returnToLastLocation;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.inventory.equipment.Slot.NECK;

public class Banking {

    public static long backpackIsFull(LocalPlayer player) {
        if (backpack.contains("Archaeological soil box")) {
            boolean success = backpack.interact("Archaeological soil box", "Fill");
            Execution.delay(random.nextLong(1500, 3000));
            log("[Archaeology] Attempting to fill soil box.");
            if (!Backpack.isFull() == success) {
                log("[Success] Soil box filled.");
            } else {
                log("[Caution] Soil box Full.");
            }
            Execution.delay(RandomGenerator.nextInt(1500, 3000));
        }
        if (backpack.isFull()) {
            log("[Caution] Going to the bank.");
            setBotState(BANKING);
        }

        return random.nextLong(1500, 3000);
    }

    public static long BankforArcheology(LocalPlayer player, List<String> selectedArchNames) {
        setLastSkillingLocation(player.getCoordinate());

        if (shouldTraverseToDaeminheimUpstairs(selectedArchNames) || shouldTraverseToDaeminheimWarpedFloor(selectedArchNames) || selectedArchNames.contains("Castle hall rubble") || selectedArchNames.contains("Tunnelling equipment repository")) {
            handleDaemonheim(player, selectedArchNames);
        }

        else if (!shouldTraverseToDaeminheimUpstairs(selectedArchNames) && !shouldTraverseToDaeminheimWarpedFloor(selectedArchNames) && !selectedArchNames.contains("Castle hall rubble") && !selectedArchNames.contains("Tunnelling equipment repository")) {
            Coordinate bankChestCoordinate = new Coordinate(3362, 3397, 0);
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery()
                    .name("Bank chest")
                    .isReachable()
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
        }

        return random.nextLong(1500, 3000);
    }

    private static void handleDaemonheim(LocalPlayer player, List<String> selectedArchNames) {
        EntityResultSet<SceneObject> imposingDoor = SceneObjectQuery.newQuery().name("Imposing door").option("Enter").results();
        EntityResultSet<SceneObject> stairs = SceneObjectQuery.newQuery().name("Staircase").option("Climb-down").results();
        EntityResultSet<Npc> banker = NpcQuery.newQuery().name("Fremennik banker").option("Bank").results();
        Coordinate imposingDoorCoordinate = new Coordinate(2208, 9219, 1);

        SceneObject door = imposingDoor.nearest();
        SceneObject stair = stairs.nearest();

        if (shouldTraverseToDaeminheimWarpedFloor(selectedArchNames)) {
            Movement.walkTo(imposingDoorCoordinate.getX(), imposingDoorCoordinate.getY(), true);
            Execution.delayUntil(60000, () -> player.getCoordinate().equals(imposingDoorCoordinate));
            if (player.getCoordinate().equals(imposingDoorCoordinate) && !imposingDoor.isEmpty()) {
                if (door.interact("Enter")) {
                    log("[Archaeology] Interacting with Imposing Door.");
                    Execution.delay(random.nextLong(3500, 5000));
                }
            }
        } else if (shouldTraverseToDaeminheimUpstairs(selectedArchNames) && stair != null) {
            if (stair.interact("Climb-down")) {
                log("[Archaeology] Interacting with Staircase.");
                Execution.delay(random.nextLong(10000, 12500));
            }
        }

        handleBankInteraction(player, selectedArchNames);
    }

    public static long handleBankInteraction(LocalPlayer player, List<String> selectedArchNames) {
        int varbitValue = getVarbitValue();
        openBank();
        ResultSet<Item> soilBox = findSoilBoxInInventory();
        if (isBankOpen()) {
            delayRandomly();
            depositItems();
            interactWithSoilBoxIfPresent(soilBox);
            handleGoteChargesAndPorter(varbitValue);
            if (Backpack.contains("Complete tome")) {
                log("[Archaeology] Complete tome found, going to hand it in.");
                Execution.delay(handleCompleteTome());
            }
            returnToLastLocation(player, selectedArchNames);
            return random.nextLong(1500, 2500);
        } else {
            log("[Error] Bank did not open.");
            return random.nextLong(750, 1250);
        }
    }
    private static long handleCompleteTome() {
        Coordinate DeskCoordinate = new Coordinate(3327, 3378, 1);
        if (Movement.traverse(NavPath.resolve(DeskCoordinate)) == TraverseEvent.State.FINISHED) {
            EntityResultSet<SceneObject> StudyDesk = SceneObjectQuery.newQuery().name("Desk").option("Study").results();
            if (!StudyDesk.isEmpty()) {
                SceneObject desk = StudyDesk.nearest();
                if (desk.interact("Study")) {
                    log("[Archaeology] Interacting with Study Desk.");
                    Execution.delayUntil(15000, () -> !Backpack.contains("Complete tome"));
                } else {
                    log("[Error] Failed to interact with Study Desk.");
                }
            } else {
                log("[Error] No Study Desk found.");
            }
        } else {
            log("[Error] Failed to traverse to Study Desk.");
        }
        return random.nextLong(1500, 2500);
    }

    public static int getVarbitValue() {
        return VarManager.getInvVarbit(94, 2, 30214);
    }

    public static void openBank() {
        interactWithBankChestOrBanker();
        log("[Archaeology] Waiting for bank to open.");
        waitForBankToOpen();
    }

    public static boolean isBankOpen() {
        return Interfaces.isOpen(517);
    }

    public static void depositItems() {
        log("[Archaeology] Depositing all items except selected.");
        int attempts = 0;

        while (attempts < 5) {
            depositAllExceptSelectedItems();
            Execution.delay(random.nextLong(1000, 1500)); // Delay for 500-1000 milliseconds

            if (Backpack.isFull()) {
                log("[Error] Failed to deposit items. Attempting again...");
                attempts++;
            } else {
                log("[Success] Successfully deposited items.");
                break;
            }
        }

        if (Backpack.isFull()) {
            log("[Error] Failed to deposit items after 3 attempts. Shutting down...");
            shutdown();
        } else {
            logItemsDeposited();
        }
    }

    public static void handleGoteChargesAndPorter(int varbitValue) {
        if (useGote) {
            if (VarManager.getVarbitValue(45141) != 1) {
                component(1, -1, 33882270);
                Execution.delay(random.nextLong(1000, 2000));
            } else {
                log("[Archaeology] Bank Tab value is already 1");
            }
            Execution.delay(handleGoteCharges());
            if (useGote) {
                Bank.close();
                Execution.delay(random.nextLong(1500, 2500));
                useBankingPorter();
                Execution.delay(random.nextLong(1500, 2500));
                if (varbitValue < getChargeThreshold()) {
                    Execution.delay(handleBankingInteractionforPorter());
                }
            } else {
                log("[Error] No " + porterTypes[currentPorterType.get()] + " found in the Backpack.");
            }
        }
    }

    public static long handleBankingInteractionforPorter() {
        if (!useGote) {
            return random.nextLong(1500, 2500);
        }

        int varbitValue = VarManager.getInvVarbit(94, 2, 30214);

        interactWithBankChestAndOpen();

        if (Interfaces.isOpen(517)) {
            handleBankInteraction(varbitValue);
        }

        return random.nextLong(1500, 2500);
    }

    private static void interactWithBankChestAndOpen() {
        interactWithBankChestOrBanker();
        waitForBankToOpen();
    }

    private static void handleBankInteraction(int varbitValue) {
        delayRandomly();

        checkVarbitAndHandleGoteCharges();

        closeBankAndUsePorter();

        checkVarbitAndReturnOrRetry(varbitValue);
    }

    private static void checkVarbitAndHandleGoteCharges() {
        if (VarManager.getVarbitValue(45141) != 1) {
            component(1, -1, 33882270);
            Execution.delay(random.nextLong(1000, 2000));
        }
        handleGoteCharges();
    }

    private static void closeBankAndUsePorter() {
        Bank.close();
        Execution.delay(random.nextLong(1500, 2500));
        useBankingPorter();
        Execution.delay(random.nextLong(1500, 2500));
    }

    private static void checkVarbitAndReturnOrRetry(int varbitValue) {
        if (varbitValue >= getChargeThreshold()) {
            log("[Success] we are above our theshold");
        } else {
            log("[Caution] Porters have " + varbitValue + " charges, but we need atleast: " + getChargeThreshold());
            handleBankingInteractionforPorter();
        }
    }
    public static void useBankingPorter() {
        String currentPorter = porterTypes[currentPorterType.get()];
        int varbitValue = VarManager.getInvVarbit(94, 2, 30214);
        if (useGote) {

            if (Backpack.contains(currentPorter) && varbitValue <= getChargeThreshold()) {
                if (equipment.contains("Grace of the elves")) {
                    boolean interactionResult = equipment.interact(NECK, "Charge all porters");
                    if (interactionResult) {
                        log("[Success] Interaction with Equipment was successful.");
                    } else {
                        log("[Error] Interaction with Equipment failed.");
                    }
                } else {
                    if (Backpack.contains(currentPorter)) {
                        boolean interactionResult = backpack.interact(currentPorter, "Wear");
                        if (interactionResult) {
                            log("[Success] Interaction with " + currentPorter + " was successful.");
                        } else {
                            log("[Error] Interaction with Backpack failed.");
                        }
                    } else {
                        log("[Error] No '" + currentPorter + "' found in the Backpack.");
                    }
                }
            }
        } else {
            log("[Error] No " + currentPorter + " found in the Backpack.");
        }
    }

    public static void interactWithBankChestOrBanker() {
        EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery()
                .name("Bank chest")
                .option("Use")
                .results();

        EntityResultSet<Npc> bankerResults = NpcQuery.newQuery()
                .name("Fremennik banker")
                .option("Bank")
                .results();

        if (!chestResults.isEmpty()) {
            interactWithEntity(chestResults, "[Archaeology] Interacted with Bank chest.", "[Error] Failed to interact with Bank chest after 3 attempts.");
        } else if (!bankerResults.isEmpty()) {
            interactWithEntity(bankerResults, "[Archaeology] Interacted with Fremennik banker.", "[Error] Failed to interact with Fremennik banker after 3 attempts.");
        } else {
            log("[Error] No Bank chest or Fremennik banker found.");
        }
    }

    private static void interactWithEntity(EntityResultSet<? extends Entity> results, String successLog, String failureLog) {
        Entity nearestEntity = results.nearest();

        if (nearestEntity != null) {
            for (int attempts = 0; attempts < 3; attempts++) {
                if ((nearestEntity instanceof SceneObject && ((SceneObject) nearestEntity).interact("Use")) ||
                        (nearestEntity instanceof Npc && ((Npc) nearestEntity).interact("Bank"))) {
                    log(successLog);
                    Execution.delay(random.nextLong(1500, 2500));
                    return;
                } else {
                    Execution.delay(random.nextLong(1500, 2500));
                }
            }
            log(failureLog);
        } else {
            log("[Error] No nearest entity found.");
        }
    }

    private static void waitForBankToOpen() {
        boolean bankOpened = Execution.delayUntil(30000, () -> Interfaces.isOpen(517));
        if (!bankOpened) {
            log("[Error] Bank did not open within 30 seconds.");
        }
    }

    public static ResultSet<Item> findSoilBoxInInventory() {
        return InventoryItemQuery.newQuery(93).name("Archaeological soil box").results();
    }

    public static void delayRandomly() {
        Execution.delay(RandomGenerator.nextInt(1500, 3000));
    }

    private static boolean depositAllExceptSelectedItems() {
        int[] itemIdsToKeep = {49538, 50096, 4614, 49976, 50431, 49947, 49949, 50753, 41092, 49429};
        Bank.depositAllExcept(itemIdsToKeep);
        return false;
    }

    private static void logItemsDeposited() {
        log("[Archaeology] Deposited all items except selected.");
    }

    public static void interactWithSoilBoxIfPresent(ResultSet<Item> soilBox) {
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
