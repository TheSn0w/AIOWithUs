package net.botwithus.Variables;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.inventory.backpack;
import net.botwithus.inventory.equipment;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.*;
import java.util.regex.Pattern;

import static net.botwithus.Archaeology.Porters.getQuantityFromOption;
import static net.botwithus.Combat.Banking.handleBankforFood;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.*;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.TaskScheduler.bankPin;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.inventory.equipment.Slot.NECK;

public class BankInteractions {
    public static final Coordinate VarrockWest = new Coordinate(3182, 3436, 0);
    public static final Coordinate VarrockEast = new Coordinate(3252, 3420, 0);
   /* public static final Coordinate GrandExchange = new Coordinate(3162, 3484, 0);*/
    public static final Coordinate Canafis = new Coordinate(3512, 3480, 0);
    public static final Coordinate AlKharid = new Coordinate(3271, 3168, 0);
    public static final Coordinate Lumbridge = new Coordinate(3214, 3257, 0);
    public static final Coordinate Draynor = new Coordinate(3092, 3245, 0);
    public static final Coordinate FaladorEast = new Coordinate(3012, 3355, 0);
    public static final Coordinate SmithingGuild = new Coordinate(3060, 3339, 0);
    public static final Coordinate FaladorWest = new Coordinate(2946, 3368, 0);
    public static final Coordinate Burthorpe = new Coordinate(2888, 3536, 0);
    public static final Coordinate Taverly = new Coordinate(2875, 3417, 0);
    public static final Coordinate Catherby = new Coordinate(2795, 3440, 0);
    public static final Coordinate Seers = new Coordinate(2724, 3493, 0);
    public static final Coordinate ArdougneSouth = new Coordinate(2655, 3283, 0);
    public static final Coordinate ArdougneNorth = new Coordinate(2616, 3332, 0);
    public static final Coordinate Yanille = new Coordinate(2613, 3094, 0);
    public static final Coordinate Ooglog = new Coordinate(2556, 2840, 0);
    public static final Coordinate CityOfUm = new Coordinate(1149, 1804, 1);
    public static final Coordinate prifWest = new Coordinate(2153, 3340, 1);
    public static final Coordinate PrifddinasCenter = new Coordinate(2205, 3368, 1);
    public static final Coordinate PrifddinasEast = new Coordinate(2232, 3310, 1);
    public static final Coordinate WarsRetreat = new Coordinate(3299, 10131, 0);
    public static final Coordinate Anachronia = new Coordinate(5465, 2342, 0);
  /*  public static final Coordinate Edgeville = new Coordinate(3096, 3496, 0);*/
    public static final Coordinate KharidEt = new Coordinate(3356, 3197, 0);
    public static final Coordinate STORMGUARD = new Coordinate(2675, 3404, 0);
    public static final Coordinate MAXGUILD = new Coordinate(2276, 3311, 1);
    public static final Coordinate Menophos = new Coordinate(3234, 2759, 0);
    public static boolean useDepositBox = false;

    public static final List<Coordinate> BANK_COORDINATES = new ArrayList<>(Arrays.asList(
            VarrockWest, VarrockEast,/* GrandExchange,*/ Canafis, AlKharid, Lumbridge,
            Draynor, FaladorEast, SmithingGuild, FaladorWest, Burthorpe, Taverly,
            Catherby, Seers, ArdougneSouth, ArdougneNorth, Yanille, Ooglog,
            CityOfUm, prifWest, PrifddinasCenter, PrifddinasEast, WarsRetreat,
            Anachronia, /*Edgeville, */KharidEt, STORMGUARD, Menophos
    ));

    public static List<String> BANK_TYPES = new ArrayList<>(Arrays.asList("Bank chest", "Bank booth", "Counter"));

    public static Coordinate findNearestBank(Coordinate playerPosition) {
        return BANK_COORDINATES.stream()
                .min(Comparator.comparingDouble(bank -> Distance.between(playerPosition, bank)))
                .orElse(null);
    }

    public static long performBanking(LocalPlayer player) {
        if (isCombatActive) {
            ActionBar.useAbility("War's Retreat Teleport");
            Execution.delay(random.nextLong(6000, 7500));
        }

        if (player.getAnimationId() != -1) {
            log("[Caution] Player is currently performing an action. Moving away to be able to teleport.");
            movePlayerAwayFromAction(player);
        }

        Coordinate nearestBank = findNearestBank(player.getCoordinate());
        if (nearestBank != null) {
            if (Movement.traverse(NavPath.resolve(nearestBank)) == TraverseEvent.State.FINISHED) {
                Execution.delay(random.nextLong(1500, 3000));
                return docheck(player, nearestBank);
            } else {
                log("[Banking] Failed to traverse to the nearest bank.");
            }
        } else {
            log("[Banking] No nearby banks found.");
        }
        return 0;
    }

    private static void movePlayerAwayFromAction(LocalPlayer player) {
        Coordinate playerCoordinate = player.getCoordinate();
        List<Coordinate> nearbyCoordinates = Arrays.asList(
                new Coordinate(playerCoordinate.getX() + 1, playerCoordinate.getY(), playerCoordinate.getZ()),
                new Coordinate(playerCoordinate.getX() - 1, playerCoordinate.getY(), playerCoordinate.getZ()),
                new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() + 1, playerCoordinate.getZ()),
                new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() - 1, playerCoordinate.getZ())
        );

        for (Coordinate nearbyCoordinate : nearbyCoordinates) {
            if (nearbyCoordinate.isWalkable()) {
                Movement.walkTo(nearbyCoordinate.getX(), nearbyCoordinate.getY(), true);
                Execution.delay(random.nextLong(1500, 2500));

                if (player.getCoordinate().equals(nearbyCoordinate)) {
                    log("[Success] Player has moved to the desired coordinate.");
                    break;
                } else {
                    log("[Caution] Player has not moved to the desired coordinate. Trying the next one.");
                }
            }
        }
    }

    private static long docheck(LocalPlayer player, Coordinate nearestBank) {
        if (player.getCoordinate().equals(nearestBank)) {
            SceneObject bankObject = findNearestBankBooth(player);
            if (bankObject != null) {
                interactWithBank(player, bankObject);
            } else {
                log("[Banking] No suitable bank object found at the bank location.");
            }
        } else {
            log("[Banking] Player is not at the bank coordinate.");
        }
        return 0;
    }

    public static SceneObject findNearestBankBooth(LocalPlayer player) {
        Coordinate playerCoordinate = player.getCoordinate();
        Area searchArea = new Area.Rectangular(
                new Coordinate(playerCoordinate.getX() - 1, playerCoordinate.getY() - 1, playerCoordinate.getZ()),
                new Coordinate(playerCoordinate.getX() + 1, playerCoordinate.getY() + 1, playerCoordinate.getZ())
        );

        for (String bankType : BANK_TYPES) {
            SceneObjectQuery query = SceneObjectQuery.newQuery().name(bankType).inside(searchArea);
            List<SceneObject> bankBooths = query.results().stream().toList();

            log("[Banking] Searching for " + bankType + " within the area. Found " + bankBooths.size() + " objects.");

            if (!bankBooths.isEmpty()) {
                log("[Banking] Found " + bankType + " at the bank location.");
                return bankBooths.get(0);
            }
        }
        log("[Banking] No suitable bank object found at the bank location.");
        return null;
    }

    public static void interactWithBank(LocalPlayer player, SceneObject nearestBankBooth) {
        if (isMiningActive) {
            handleOreBoxBanking(player, nearestBankBooth);
        } else if (BankforFood) {
            handleBankforFood(player, nearestBankBooth);
        } else if (useGote && nearestBank) {
            Execution.delay(handleGoteBanking(player, nearestBankBooth));
        } else if (!useGote && nearestBank) {
            log("Handling normal Banking.");
            handleNormalBanking(player, nearestBankBooth);
        } else {
            log("[Banking] No suitable banking method found.");
        }
    }

    public static long handleGoteBanking(LocalPlayer player, SceneObject nearestBankBooth) {
        List<String> interactionOptions = Arrays.asList("Bank", "Use");
        String bankType = nearestBankBooth.getName();
        String[] bankTypes = {"Bank booth", "Bank chest", "Counter"};

        if (Arrays.asList(bankTypes).contains(bankType)) {
            for (String interactionOption : interactionOptions) {
                log("[Banking] Trying interaction option: " + interactionOption + " on " + bankType);

                for (int i = 0; i < 1; i++) {
                    boolean interactionSuccess = nearestBankBooth.interact(interactionOption);
                    log("[Banking] Trying to interact with bank using " + interactionOption + " on " + bankType + ": " + interactionSuccess);

                    if (interactionSuccess) {
                        Execution.delayUntil(random.nextLong(10000, 15000), Bank::isOpen);
                        if (Interfaces.isOpen(759)) {
                            bankPin();
                            Execution.delay(random.nextLong(1500, 3000));
                        }
                        if (Bank.isOpen()) {
                            log("[Banking] Bank is open. Depositing items.");

                            if (isFishingActive) {
                                log("[Banking] Bank is open. Depositing all items, Except for Feathers.");
                                Bank.depositAllExcept(314);
                            } else {
                                log("[Banking] Bank is open. Depositing items.");
                                Bank.depositAll();
                            }
                            Execution.delay(random.nextLong(1500, 3000));

                            if (Movement.traverse(NavPath.resolve(lastSkillingLocation)) == TraverseEvent.State.FINISHED) {
                                log("[Banking] Traversing to last skilling location.");
                                Execution.delay(random.nextLong(1500, 3000));
                                setBotState(SKILLING);
                            }

                            return random.nextLong(1500, 3000);
                        }
                    } else {
                        log("[Error] Failed to interact with bank using " + interactionOption + " option. Retrying, with Use option.");
                        Execution.delay(random.nextLong(1500, 3000));
                    }
                }
            }
        } else {
            log("[Error] Bank type " + bankType + " not recognized.");
        }

        log("[Error] Failed to interact with the bank using all available options.");
        return random.nextLong(1500, 3000);
    }

    private static void handleGote(int varbitValue, LocalPlayer player, SceneObject nearestBankBooth) {
        Item oreBox = InventoryItemQuery.newQuery(93).category(4448).results().first();
        if (useGote) {
            if (VarManager.getVarbitValue(45141) != 1) {
                component(1, -1, 33882270);
                Execution.delay(random.nextLong(1000, 2000));
            }
            Execution.delay(handleGoteWithdrawing());
            if (useGote) {
                Bank.close();
                Execution.delay(random.nextLong(1500, 2500));
                interactWithPorter();
                Execution.delay(random.nextLong(1500, 2500));
                if (varbitValue < getBankingThreshold()) {
                    handleOreBoxBanking(player, nearestBankBooth);
                } else {
                    log("[Banking] Grace of the Elves is charged, Going back to skilling.");
                    if (Movement.traverse(NavPath.resolve(lastSkillingLocation)) == TraverseEvent.State.FINISHED) {
                        log("[Porter] Traversing to last skilling location.");
                        Execution.delay(random.nextLong(1500, 3000));
                        setBotState(SKILLING);
                    }
                }
            } else {
                log("[Error] No " + porterTypes[currentPorterType.get()] + " found in the Backpack.");
            }
        }
    }

    public static void handleOreBoxBanking(LocalPlayer player, SceneObject nearestBankBooth) {
        Item oreBox = InventoryItemQuery.newQuery(93).category(4448).results().first();

        Pattern oreBoxesPattern = Pattern.compile("(?i)Bronze ore box|Iron ore box|Steel ore box|Mithril ore box|Adamant ore box|Rune ore box|Orikalkum ore box|Necronium ore box|Bane ore box|Elder rune ore box");

        List<String> interactionOptions = Arrays.asList("Bank", "Use");
        String bankType = nearestBankBooth.getName();

        if (BANK_TYPES.contains(bankType)) {
            for (String interactionOption : interactionOptions) {
                log("[Banking] Trying interaction option: " + interactionOption + " on " + bankType);

                for (int i = 0; i < 1; i++) {
                    boolean interactionSuccess = nearestBankBooth.interact(interactionOption);
                    log("[Banking] Trying to interact with bank using " + interactionOption + " on " + bankType + ": " + interactionSuccess);

                    if (interactionSuccess) {
                        Execution.delayUntil(random.nextLong(10000, 15000), Bank::isOpen);
                        if (Interfaces.isOpen(759)) {
                            bankPin();
                            Execution.delay(random.nextLong(1500, 3000));
                        }
                        if (Bank.isOpen()) {
                            log("[Banking] Bank is open. Depositing items.");
                            Bank.depositAllExcept(oreBoxesPattern);
                            Execution.delay(random.nextLong(1500, 3000));

                            Execution.delay(random.nextLong(1500, 3000));

                            if (oreBox.getSlot() >= 0) {
                                component(8, oreBox.getSlot(), 33882127);
                                log("[Banking] Emptied: " + oreBox.getName());
                                setBotState(SKILLING);
                            }
                            random.nextLong(1500, 3000);
                            return;
                        }
                    } else {
                        log("[Error] Failed to interact with bank using " + interactionOption + " option. Retrying, with Use option.");
                        Execution.delay(random.nextLong(1500, 3000));
                    }
                }
            }
        } else {
            log("[Error] Bank type " + bankType + " not recognized.");
        }

        log("[Error] Failed to interact with the bank using all available options.");
        random.nextLong(1500, 3000);
    }

    private static long handleGoteWithdrawing() {
        if (useGote) {
            int charges = VarManager.getInvVarbit(94, 2, 30214);

            if (charges < getBankingThreshold()) {
                String selectedPorter = porterTypes[currentPorterType.get()];
                int quantity = getQuantityFromOption(quantities[currentQuantity.get()]);
                boolean withdrew;
                if (VarManager.getVarbitValue(45189) != 7) {
                    component(1, -1, 33882215);
                }
                withdrew = Bank.withdraw(selectedPorter, quantity);
                Execution.delay(random.nextLong(1500, 3000));
                if (withdrew && !InventoryItemQuery.newQuery(93).name(selectedPorter).results().isEmpty()) {
                    log("[Banking] Withdrew: " + selectedPorter + ".");
                } else {
                    log("[Error] Failed to withdraw " + selectedPorter + ".");
                    log("[Caution] use Gote/Porter has been disabled.");
                    useGote = false;
                    return random.nextLong(1500, 3000);
                }
            }
        }
        return random.nextLong(1500, 2500);
    }

    private static void interactWithPorter() {
        String currentPorter = porterTypes[currentPorterType.get()];
        int varbitValue = VarManager.getInvVarbit(94, 2, 30214);
        if (useGote) {
            if (Backpack.contains(currentPorter) && varbitValue <= getBankingThreshold()) {
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
                        if (!nearestBank) {
                            log("[Error] No " + currentPorter + " found in the Backpack, and Banking is disabled");
                            useGote = false;
                        }
                    }
                }
            }
        } else {
            log("[Error] No " + currentPorter + " found in the Backpack.");
        }
    }


    public static void handleNormalBanking(LocalPlayer player, SceneObject nearestBankBooth) {

        SceneObject bankChest = SceneObjectQuery.newQuery().name("Bank chest").isReachable().results().nearest();
        if (bankChest != null) {
            if (bankChest.interact("Load Last Preset from")) {
                log("[Banking] Loaded last preset from bank chest.");
                return;
            } else {
                log("[Error] Failed to load last preset from bank chest.");
            }
        }

        List<String> bankTypes = Arrays.asList("Bank booth", "Bank chest", "Counter");
        String interactionOption = "Load Last Preset from";

        for (String bankType : bankTypes) {
            if (Objects.equals(nearestBankBooth.getName(), bankType)) {
                log("[Banking] Trying interaction option: " + interactionOption + " on " + bankType);

                for (int i = 0; i < 1; i++) {
                    boolean interactionSuccess = nearestBankBooth.interact(interactionOption);
                    log("[Banking] Trying to interact with " + bankType + ": " + interactionSuccess);

                    if (interactionSuccess) {
                        Execution.delay(random.nextLong(3000, 5000));
                        if (Interfaces.isOpen(759)) {
                            bankPin();
                        }
                        log("[Banking] Traversing to last skilling location.");
                        if (Movement.traverse(NavPath.resolve(lastSkillingLocation)) == TraverseEvent.State.FINISHED) {
                            setBotState(SKILLING);
                            random.nextLong(1500, 3000);
                            return;
                        }
                    } else {
                        log("[Error] Failed to interact with " + bankType + ". Retrying...");
                        Execution.delay(random.nextLong(1500, 3000));
                    }
                }
            }
        }

        random.nextLong(1500, 3000);
    }

}
