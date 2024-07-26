package net.botwithus.Archaeology;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.inventory.backpack;
import net.botwithus.inventory.equipment;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
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

import static net.botwithus.Archaeology.Daeminheim.*;
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

    public enum SoilType {
        ANCIENT_GRAVEL(9370),
        SALTWATER_MUD(9371),
        FIERY_BRIMSTONE(9372),
        AERATED_SEDIMENT(9373),
        EARTHEN_CLAY(9374),
        VOLCANIC_ASH(9578),
        UNKNOWN(9370);

        private final int soilBoxId;

        SoilType(int soilBoxId) {
            this.soilBoxId = soilBoxId;
        }

        public int getSoilBoxId() {
            return soilBoxId;
        }
    }

    /*{
        "ids": [
        47021
      ],
        "description": "Soil box upgrades",
            "maxValue": 3,
            "minValue": 0,
            "testbit": -1
    },

    {
        "ids": [
        47021
      ],
        "description": "Soil box upgrades",
            "maxValue": 3,
            "minValue": 0,
            "testbit": -1
    },
    {
        "ids": [
        47022
      ],
        "description": "Material storage upgrades",
            "maxValue": 3,
            "minValue": 0,
            "testbit": -1
    },
    {
        "ids": [
        47023
      ],
        "description": "Mattock precision upgrades",
            "maxValue": 4,
            "minValue": 0,
            "testbit": -1
    },
    {
        "ids": [
        47350
      ],
        "description": "Auto-screener v1.080 blueprint",
            "maxValue": 1,
            "minValue": 0,
            "testbit": -1
    }*/

   public static long backpackIsFull(LocalPlayer player) {
    int soilBoxUpgradeLevel = VarManager.getVarbitValue(47021);
    int soilBoxCapacity = switch (soilBoxUpgradeLevel) {
        case 0 -> 50;
        case 1 -> 100;
        case 2 -> 250;
        case 3 -> 500;
        default -> 0;
    };

    if (backpack.contains("Archaeological soil box")) {
        log("Soil box capacity: " + soilBoxCapacity);
        for (SoilType soilType : SoilType.values()) {
            int soilAmount = VarManager.getVarValue(VarDomainType.PLAYER, soilType.getSoilBoxId());
            log("Soil Type: " + soilType.name() + ", Current Amount: " + soilAmount);
            if (Backpack.containsItemByCategory(4603) && soilAmount < soilBoxCapacity) {
                boolean success = backpack.interact("Archaeological soil box", "Fill");
                Execution.delay(random.nextLong(1200, 1600));
                if (!Backpack.isFull() == success) {
                    log("[Success] Soil box filled.");
                }
            }
        }
        Execution.delay(RandomGenerator.nextInt(1500, 3000));
    }
    if (backpack.isFull()) {
        setLastSkillingLocation(player.getCoordinate());
        log("[Caution] Going to the bank, setting skilling location as: " + player.getCoordinate());
        setBotState(BANKING);
    }

    return random.nextLong(1500, 3000);
}

    public static long BankforArcheology(LocalPlayer player, List<String> selectedArchNames) {
        setLastSkillingLocation(player.getCoordinate());

        if (shouldTraverseToDaeminheimUpstairs(selectedArchNames) || shouldTraverseToDaeminheimWarpedFloor(selectedArchNames) || selectedArchNames.contains("Castle hall rubble") || selectedArchNames.contains("Tunnelling equipment repository")) {
            handleDaemonheim(player, selectedArchNames);
        } else {
            Coordinate bankChestCoordinate = new Coordinate(3362, 3397, 0);
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery()
                    .name("Bank chest")
                    .option("Use")
                    .results();

            if (!results.isEmpty()) {
                log("[Archaeology] Found Nearby Bank chest.");
                SceneObject bankChest = results.nearest();
                Coordinate nearbyBankChest = bankChest.getCoordinate();

                Area areaAroundBankChest = new Area.Rectangular(nearbyBankChest, 2, 2);
                Coordinate randomCoordinate = areaAroundBankChest.getRandomWalkableCoordinate();

                log("[Archaeology] Traversing to Nearby Bank chest.");

                if (Movement.traverse(NavPath.resolve(randomCoordinate)) == TraverseEvent.State.FINISHED) {
                    handleBankInteraction(player, selectedArchNames);
                }
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


    public static long handleBankInteraction(LocalPlayer player, List<String> selectedArchNames) {
        openBank();
        ResultSet<Item> soilBox = findSoilBoxInInventory();
        if (isBankOpen()) {
            delayRandomly();
            depositItems();
            interactWithSoilBoxIfPresent(soilBox);
            if (useGote) {
                if (VarManager.getVarbitValue(45141) != 1) {
                    component(1, -1, 33882270);
                    Execution.delay(random.nextLong(500, 600));
                }
                Execution.delay(handleGoteCharges());
                if (Equipment.contains("Grace of the elves")) {
                    Bank.close();
                    String currentPorter = porterTypes[currentPorterType.get()];
                    if (Backpack.contains(currentPorter)) {
                        log("[Archaeology] Found " + currentPorter + " in backpack, using it.");
                        Execution.delay(random.nextLong(500, 600));
                        useBankingPorter();
                        Execution.delay(random.nextLong(500, 600));
                    }
                    int CurrentGraceCharges = VarManager.getInvVarbit(94, 2, 30214);
                    if (CurrentGraceCharges < getBankingThreshold()) {
                        log("[Archaeology] Charges are below threshold, Banking again to withdraw more");
                        Execution.delay(handleBankInteractionforPorter());
                    } else {
                        log("[Archaeology] Charges are above threshold, continuing.");

                    }
                }
            }
            if (Backpack.contains("Complete tome")) {
                log("[Archaeology] Complete tome found, going to hand it in.");
                Execution.delay(handleCompleteTome());
            }
            log("[Archaeology] Finished banking, going back to work.");
            returnToLastLocation(player, selectedArchNames);
        } else {
            log("[Error] Bank did not open.");
            return random.nextLong(900, 1500);
        }
        return 0;
    }

    public static long handleBankInteractionforPorter() {
        openBank();
        if (!isBankOpen()) {
            log("[Error] Bank did not open.");
            return random.nextLong(900, 1500);
        }

        delayRandomly();

        if (VarManager.getVarbitValue(45141) != 1) {
            component(1, -1, 33882270);
            Execution.delay(random.nextLong(500, 600));
        }

        Execution.delay(handleGoteCharges());

        Bank.close();

        String currentPorter = porterTypes[currentPorterType.get()];
        if (!Backpack.contains(currentPorter)) {
            log("[Archaeology] Charges are above threshold, continuing.");
            return 0;
        }

        log("[Archaeology] Found " + currentPorter + " in backpack, using it.");
        Execution.delay(random.nextLong(500, 600));
        useBankingPorter();
        Execution.delay(random.nextLong(500, 600));

        return 0;
    }
    public static void useBankingPorter() {
        String currentPorter = porterTypes[currentPorterType.get()];

        if (equipment.contains("Grace of the elves")) {
            int CurrentGraceCharges = VarManager.getInvVarbit(94, 2, 30214);
            if (CurrentGraceCharges < getBankingThreshold()) {
                log("[Archaeology] Porters have " + CurrentGraceCharges + " charges. Charging.");
                boolean interactionResult = equipment.interact(NECK, "Charge all porters");
                if (interactionResult) {
                    log("[Archaeology] Interaction with Equipment was successful.");
                } else {
                    log("[Error] Interaction with Equipment failed.");
                }
            }
        } else {
            if (ComponentQuery.newQuery(284).spriteId(51490).results().isEmpty()) {
                boolean interactionResult = backpack.interact(currentPorter, "Wear");
                if (interactionResult) {
                    log("[Archaeology] Interaction with Backpack was successful.");
                } else {
                    log("[Error] Interaction with Backpack failed.");
                }
            }
        }
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
            Execution.delay(random.nextLong(1000, 1500));

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

    private static void depositAllExceptSelectedItems() {
        int[] itemIdsToKeep = {49538, 50096, 4614, 49976, 50431, 49947, 49949, 50753, 41092, 49429, 50161};
        Bank.depositAllExcept(itemIdsToKeep);
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
    private static long handleCompleteTome() {
        Coordinate DeskCoordinate = new Coordinate(3327, 3378, 1);
        if (Movement.traverse(NavPath.resolve(DeskCoordinate)) == TraverseEvent.State.FINISHED) {
            EntityResultSet<SceneObject> StudyDesk = SceneObjectQuery.newQuery().name("Desk").option("Study").results();
            if (!StudyDesk.isEmpty()) {
                SceneObject desk = StudyDesk.nearest();
                if (desk.interact("Study")) {
                    log("[Archaeology] Interacting with Study Desk.");
                    Execution.delayUntil(15000, () -> !Backpack.contains("Complete tome"));
                    if (selectedArchNames.contains("Castle hall rubble") || selectedArchNames.contains("Tunnelling equipment repository") || selectedArchNames.contains("Botanical reserve") || selectedArchNames.contains("Communal space") || selectedArchNames.contains("Traveller's station") || selectedArchNames.contains("Security booth") || selectedArchNames.contains("Projection space")) {
                        Coordinate bankingCoordinate = new Coordinate(3449, 3719, 0);
                        if (Movement.traverse(NavPath.resolve(bankingCoordinate)) == TraverseEvent.State.FINISHED) {
                            log("[Archaeology] Arrived at banker.");
                        }
                    }
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
}
