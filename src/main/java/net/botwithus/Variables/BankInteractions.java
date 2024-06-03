package net.botwithus.Variables;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.Variables.Variables.component;
import static net.botwithus.Variables.Variables.random;

public class BankInteractions {
    public static final Coordinate VarrockWest = new Coordinate(3182, 3436, 0);
    public static final Coordinate VarrockEast = new Coordinate(3252, 3420, 0);
    public static final Coordinate GrandExchange = new Coordinate(3162, 3484, 0);
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
    public static final Coordinate Edgeville = new Coordinate(3096, 3496, 0);
    public static final Coordinate KharidEt = new Coordinate(3356, 3197, 0);
    public static final Coordinate VIP = new Coordinate(3182, 2742, 0);
    public static final Coordinate STORMGUARD = new Coordinate(2675, 3404, 0);

    public static final List<Coordinate> BANK_COORDINATES = Arrays.asList(
            VarrockWest, VarrockEast, GrandExchange, Canafis, AlKharid, Lumbridge,
            Draynor, FaladorEast, SmithingGuild, FaladorWest, Burthorpe, Taverly,
            Catherby, Seers, ArdougneSouth, ArdougneNorth, Yanille, Ooglog,
            CityOfUm, prifWest, PrifddinasCenter, PrifddinasEast, WarsRetreat,
            Anachronia, Edgeville, KharidEt, VIP, STORMGUARD
    );

    public static final List<String> BANK_TYPES = Arrays.asList("Bank chest", "Bank booth", "Counter");

    public static Coordinate findNearestBank(Coordinate playerPosition) {
        return BANK_COORDINATES.stream()
                .min(Comparator.comparingDouble(bank -> Distance.between(playerPosition, bank)))
                .orElse(null);
    }

    public static long performBanking(LocalPlayer player) {
        Coordinate nearestBank = findNearestBank(player.getCoordinate());
        if (nearestBank != null) {
            if (Movement.traverse(NavPath.resolve(nearestBank)) == TraverseEvent.State.FINISHED) {
                SceneObject nearestBankBooth = findNearestBankBooth(player, nearestBank);
                if (nearestBankBooth != null) {
                    return interactWithBank(player, nearestBankBooth);
                } else {
                    log("[Main] No bank booth found at the bank location.");
                }
            } else {
                log("[Main] Failed to traverse to the nearest bank.");
            }
        } else {
            log("[Main] No nearby banks found.");
        }
        return 1500;
    }

    public static SceneObject findNearestBankBooth(LocalPlayer player, Coordinate nearestBank) {
        for (String bankType : BANK_TYPES) {
            List<SceneObject> bankBooths = SceneObjectQuery.newQuery().name(bankType).results().stream()
                    .filter(booth -> booth.getCoordinate().distanceTo(player.getCoordinate()) < 25.0D)
                    .toList();

            if (!bankBooths.isEmpty()) {
                log("[Main] Found " + bankType + " at the bank location.");
                return bankBooths.get(0);
            }
        }

        log("[Main] No bank booth found at the bank location.");
        return null;
    }

    public static long interactWithBank(LocalPlayer player, SceneObject nearestBankBooth) {
        Item oreBox = InventoryItemQuery.newQuery().category(4448).results().first();

        if (oreBox != null) {
            return handleOreBoxBanking(player, nearestBankBooth, oreBox);
        } else {
            return handleNormalBanking(player, nearestBankBooth);
        }
    }

    public static long handleOreBoxBanking(LocalPlayer player, SceneObject nearestBankBooth, Item oreBox) {
        Pattern oreBoxesPattern = Pattern.compile("(?i)Bronze ore box|Iron ore box|Steel ore box|Mithril ore box|Adamant ore box|Rune ore box|Orikalkum ore box|Necronium ore box|Bane ore box|Elder rune ore box");
        List<String> interactionOptions = Arrays.asList("Bank", "Use");
        String bankType = nearestBankBooth.getName();

        if (BANK_TYPES.contains(bankType)) {
            for (String interactionOption : interactionOptions) {
                log("[Main] Ore box detected. Trying interaction option: " + interactionOption + " on " + bankType);

                for (int i = 0; i < 1; i++) { // tries to interact with the bank 1 time for each option
                    boolean interactionSuccess = nearestBankBooth.interact(interactionOption);
                    log("[Main] Trying to interact with bank using " + interactionOption + " on " + bankType + ": " + interactionSuccess);

                    if (interactionSuccess) {
                        Execution.delayUntil(random.nextLong(10000, 15000), Bank::isOpen);
                        if (Bank.isOpen()) {
                            log("[Main] Bank is open. Depositing items except ore box.");
                            Bank.depositAllExcept(oreBoxesPattern);
                            log("[Main] Deposited everything except: " + oreBox.getName());

                            if (oreBox.getSlot() >= 0) {
                                component(8, oreBox.getSlot(), 33882127);
                                log("[Main] Emptied: " + oreBox.getName());
                                setBotState(SKILLING);
                            }
                            return random.nextLong(1500, 3000); // Random delay time
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
        return random.nextLong(1500, 3000); // Random delay time
    }

    public static long handleNormalBanking(LocalPlayer player, SceneObject nearestBankBooth) {
        String bankType = nearestBankBooth.getName();
        if (BANK_TYPES.contains(bankType)) {
            String interactionOption = "Load Last Preset from";
            log("[Main] Trying interaction option: " + interactionOption + " on " + bankType);

            for (int i = 0; i < 1; i++) {  // Retry 3 times
                boolean interactionSuccess = nearestBankBooth.interact(interactionOption);
                log("[Main] Trying to interact with " + bankType + ": " + interactionSuccess);

                if (interactionSuccess) {
                    Execution.delay(random.nextLong(3000, 5000));
                    setBotState(SKILLING);
                    return random.nextLong(1500, 3000); // Random delay time
                } else {
                    log("[Error] Failed to interact with " + bankType + ". Retrying...");
                    Execution.delay(random.nextLong(1500, 3000));  // Wait 1 second before retrying
                }
            }
        }

        log("[Error] Failed to interact with the bank using all available options.");
        return random.nextLong(1500, 3000); // Random delay time
    }
}
