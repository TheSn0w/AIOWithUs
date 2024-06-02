package net.botwithus.Misc;

import net.botwithus.Cooking;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.Headbar;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.Optional;
import java.util.regex.Pattern;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.dialog;
import static net.botwithus.Variables.Variables.random;


public class UrnMaker {
    private static boolean hasInteracted = false; // To track if the initial interaction has occurred
    private static UrnState urnState = UrnState.MINING;

    enum UrnState {
        IDLE,
        MINING,
        SOFTCLAY,
        URNS,
        OVEN,
        BANKING,
    }
    /**
     * Crafts urns based on the current state of the urn crafting process.
     *
     * @param player The local player who is crafting the urns.
     * @return
     */
    public static long craftUrns(LocalPlayer player) {
        switch (urnState) {
            case IDLE:
                Execution.delay(random.nextLong(3000, 7000));
                break;
            case MINING:
                Execution.delay(handleMining(player));
                break;

            case BANKING:
                hasInteracted = false;
                Execution.delay(handleBanking(player));
                break;

            case SOFTCLAY:
                Execution.delay(handleSoftClay(player));
                break;

            case URNS:
                Execution.delay(handleUrns(player));
                break;

            case OVEN:
                Execution.delay(handleOven(player));
                break;

            default:
                log("Unknown urn crafting state.");
                break;
        }
        return random.nextLong(1000, 3000);
    }

    private static long handleBanking(LocalPlayer player) {
        if (urnState == UrnState.BANKING) {
            SceneObject bank = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
            SceneObject deposit = SceneObjectQuery.newQuery().name("Deposit box").results().nearest();
            if (bank != null) {
                bank.interact("Load Last Preset from");
                log("[Urns] Load last preset");
                Execution.delayUntil(random.nextLong(15000, 30000), Backpack::isEmpty);
            } else {
                log("[Urns] Bank not found, looking for Deposit box");
                Execution.delay(random.nextLong(1500, 3000));
                if (deposit != null) {
                    deposit.interact("Deposit-All");
                    log("[Urns] Deposit all");
                    Execution.delayUntil(random.nextLong(15000, 30000), Backpack::isEmpty);
                }
            }
        }
        urnState = UrnState.MINING;
        return random.nextLong(1500, 3000);
    }


    private static long handleMining(LocalPlayer player) {
        SceneObject clayRock = SceneObjectQuery.newQuery().name("Clay rock").option("Mine").results().nearest();
        Coordinate clay = new Coordinate(3092, 3244, 0);
        if (clayRock == null) {
            log("[Urns] No clay rocks found, moving to bank");
            if (Movement.traverse(NavPath.resolve(clay)) == TraverseEvent.State.FINISHED) {
                log("[Urns] Arrived at Bank");
                urnState = UrnState.BANKING;
            }
            return 0;
        }

        if (Backpack.isFull()) {
            log("[Urns] Backpack is full, interacting with well");
            urnState = UrnState.SOFTCLAY;
            return random.nextLong(250, 1500);
        }

        if (!hasInteracted) {
            clayRock.interact("Mine");
            log("[Urns] Initial interaction with clay rock to start mining");
            hasInteracted = true;
            return random.nextLong(250, 1500);
        }

        Optional<Headbar> relevantHeadbar = player.getHeadbars().stream()
                .filter(headbar -> headbar.getId() == 5)
                .findAny();

        if (relevantHeadbar.isPresent()) {
            Headbar headbar = relevantHeadbar.get();
            int headbarWidth = headbar.getWidth();
            int randomWidth = RandomGenerator.nextInt(140, 180);

            if (headbarWidth < randomWidth) {
                clayRock.interact("Mine");
                log("[Urns] Low stamina detected, interacting with clay rock");
            }
        } else {
            log("[Urns] No relevant headbars detected, interacting with clay rock");
            clayRock.interact("Mine");
        }

        return random.nextLong(250, 1500);
    }

    private static long handleSoftClay(LocalPlayer player) {
            EntityResultSet<SceneObject> Well = SceneObjectQuery.newQuery().name("Well").option("Fill").results();
            if (Interfaces.isOpen(1251) || player.isMoving()) {
                return random.nextLong(1250, 2500);
            }
            if (Interfaces.isOpen(1370)) {
                dialog(0, -1, 89784350);
                log("[Urns] Selecting 'Craft'");
                return random.nextLong(1250, 1500);
            }
            if (Backpack.contains("Clay")) {
                if (!Well.isEmpty()) {
                    SceneObject wellObject = Well.nearest();
                    if (wellObject != null) {
                        wellObject.interact("Fill");
                        log("[Urns] Interacting with well");
                    }
                    return random.nextLong(1250, 1500);
                }
            } else {
                urnState = UrnState.URNS;
                return random.nextLong(1250, 1500);
            }
        return random.nextLong(1250, 1500);
    }

    //TODO
    // VARP value 1169 is the dropdown menu for the urns??
    // VARP value 1170 is the item selected to craft??

    private static long handleUrns(LocalPlayer player) {
        EntityResultSet<SceneObject> potteryWheel = SceneObjectQuery.newQuery().id(107724).option("Form").results();

        if (Interfaces.isOpen(1251) || player.isMoving()) {
            return random.nextLong(1250, 2500);
        }
        if (Interfaces.isOpen(1370)) {
            dialog(0, -1, 89784350);
            log("[Urns] Selecting 'Mould'");
            return random.nextLong(1250, 1500);
        }
        if (!potteryWheel.isEmpty() && Backpack.contains("Soft clay")) {
            SceneObject wheel = potteryWheel.nearest();
            if (wheel != null) {
                wheel.interact("Form");
                log("[Urns] interacting with pottery wheel;");
                return random.nextLong(1250, 1500);
            }
        }
        urnState = UrnState.OVEN;
        return random.nextLong(1250, 1500);
    }

    private static long handleOven(LocalPlayer player) {
        EntityResultSet<SceneObject> potteryOven = SceneObjectQuery.newQuery().name("Pottery oven").results();

        if (Interfaces.isOpen(1251) || player.isMoving()) {
            return random.nextLong(1250, 2500);
        }
        if (Interfaces.isOpen(1370)) {
            dialog(0, -1, 89784350);
            log("[Urns] Selecting 'Mould'");
            return random.nextLong(1250, 1500);
        }
        Pattern unfinishedUrn = Pattern.compile("unf", Pattern.CASE_INSENSITIVE);

        Item urns = InventoryItemQuery.newQuery(93)
                .results()
                .stream()
                .filter(item -> item.getName() != null && unfinishedUrn.matcher(item.getName()).find())
                .findFirst()
                .orElse(null);

        if (urns != null && !potteryOven.isEmpty()) {
            SceneObject oven = potteryOven.nearest();
            if (oven != null) {
                oven.interact("Use");
                log("[Urns] Interacting with oven");
                return random.nextLong(1250, 1500);
            }
        }

        urnState = UrnState.BANKING;
        return random.nextLong(1250, 1500);
    }

    public static UrnState getUrnState() {
        return urnState;
    }

}
