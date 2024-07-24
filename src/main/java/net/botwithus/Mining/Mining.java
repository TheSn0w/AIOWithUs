package net.botwithus.Mining;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.inventory.equipment;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.Headbar;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;
import java.util.*;
import java.util.regex.Pattern;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.BankInteractions.useDepositBox;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.inventory.equipment.Slot.NECK;

public class Mining {

    private static void handleBackpack(LocalPlayer player) {
        if (!Backpack.isFull()) {
            return;
        }
        if (Backpack.containsItemByCategory(4448)) {
            boolean success = fillOreBox();
            if (success) {
                setBotState(SKILLING);
            } else if (nearestBank) {
                log("[Mining] Failed to fill the ore box. Backpack is still full. starting to mine again.");
                sendToBank(player);
            } else {
                log("[Mining] Failed to fill the ore box. Backpack is full. Dropping all ores.");
                dropAllOres();
            }
        } else {
            if (nearestBank) {
                log("[Mining] Backpack is full. Changing state to BANKING.");
                sendToBank(player);
            } else {
                log("[Mining] Backpack is full. Dropping all ores.");
                dropAllOres();
            }
        }
    }

    private static boolean fillOreBox() {
        Pattern oreBoxesPattern = Pattern.compile("(?i)Bronze ore box|Iron ore box|Steel ore box|Mithril ore box|Adamant ore box|Rune ore box|Orikalkum ore box|Necronium ore box|Bane ore box|Elder rune ore box");

        Item oreBox = InventoryItemQuery.newQuery().name(oreBoxesPattern).results().first();

        if (oreBox != null) {
            Backpack.interact(oreBox.getName(), "Fill");
            Execution.delay(random.nextLong(1500, 3000));

            if (!Backpack.isFull()) {
                log("[Success] Filled: " + oreBox.getName());
                setBotState(SKILLING);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    private static void sendToBank(LocalPlayer player) {
        if (useDepositBox) {
            SceneObject depositBox = SceneObjectQuery.newQuery().name("Deposit box").results().nearest();
            if (depositBox != null) {
                if (depositBox.interact("Deposit-All")) {
                    log("[Banking] Deposited all items in deposit box.");
                    Execution.delayUntil(random.nextLong(20000, 30000), () -> !Backpack.isFull());
                    if (!Backpack.isFull()) {
                        return;
                    }
                } else {
                    log("[Error] Failed to deposit all items in deposit box.");
                }
            } else {
                log("[Banking] No deposit box found, proceeding to interact with the nearest bank ");
            }

            SceneObject bankChest = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
            if (bankChest != null) {
                if (bankChest.interact("Load Last Preset from")) {
                    Execution.delayUntil(random.nextLong(20000, 30000), () -> !Backpack.isFull());
                    log("[Banking] Loaded last preset from bank chest.");
                    return;
                } else {
                    log("[Error] Failed to load last preset from bank chest.");
                }
            } else {
                log("[Banking] No bank chest found, proceeding to interact with the nearest bank ");
            }
        }

        setLastSkillingLocation(player.getCoordinate());
        Execution.delay(random.nextLong(1500, 3000));
        log("[Mining] Traversing to bank.");
        setBotState(BANKING);
    }

    private static void dropAllOres() {
        log("[Mining] Backpack is full. Dropping all ores... its faster to drop from actionbar :)");

        ResultSet<Item> allItems = InventoryItemQuery.newQuery(93).results();

        for (Item item : allItems) {
            if (item != null) {
                String itemName = item.getName();
                int category = item.getConfigType().getCategory();

                if (ActionBar.containsItem(itemName)) {
                    boolean success = ActionBar.useItem(itemName, "Drop");
                    if (success) {
                        log("[Mining] Dropping (ActionBar): " + itemName);
                        Execution.delay(random.nextLong(206, 405));
                    }
                } else if (category == 91) {
                    boolean success = backpack.interact(itemName, "Drop");
                    if (success) {
                        log("[Mining] Dropping (Backpack): " + itemName);
                        Execution.delay(random.nextLong(620, 650));
                    }
                }
            }
        }
    }

    public static long handleSkillingMining(LocalPlayer player, List<String> selectedRockNames) {
        if(useGote) {
            setLastSkillingLocation(player.getCoordinate());
            Execution.delay(random.nextLong(1500, 3000));
            usePorter();
        }
        if (Backpack.isFull()) {
            handleBackpack(player);
            return 0;
        }

        if (player.isMoving()) {
            return random.nextLong(1500, 3000);
        }


        long headbarDelay = handleHeadbars(player, selectedRockNames);
        if (headbarDelay > 0) {
            return headbarDelay;
        }

        return handleMiningInteractions(player, selectedRockNames);
    }

    private static long handleHeadbars(LocalPlayer player, List<String> selectedRockNames) {
        Optional<Headbar> bar = player.getHeadbars().stream()
                .filter(headbar -> headbar.getId() == 5 && headbar.getWidth() < RandomGenerator.nextInt(200, 225))
                .findAny();

        EntityResultSet<SpotAnimation> animations = SpotAnimationQuery.newQuery().ids(7164, 7165).results();

        if (bar.isPresent() && animations.isEmpty()) {

            return interactWithSelectedRocks(player, selectedRockNames);
        }

        if (!animations.isEmpty()) {
            return interactWithSpotAnimations(selectedRockNames, animations);
        }
        return 0;
    }

    private static long interactWithSelectedRocks(LocalPlayer player, List<String> selectedRockNames) {
        for (String rockName : selectedRockNames) {
            SceneObject nearestRock = SceneObjectQuery.newQuery().name(rockName).results().nearest();

            if (nearestRock != null && Distance.between(player, nearestRock) <= 25.0D) {
                if (nearestRock.interact("Mine")) {
                    log("[Mining] Interacted with: " + rockName);

                }
            } else {
                log("[Error] Rock is too far away to interact, move closer.");
            }
        }
        return random.nextLong(1500, 3000);
    }

    private static long interactWithSpotAnimations(List<String> selectedRockNames, EntityResultSet<SpotAnimation> animations) {
        SpotAnimation currentAnimation = animations.first();
        for (String rockName : selectedRockNames) {
            SceneObject matchingRock = SceneObjectQuery.newQuery().name(rockName)
                    .results()
                    .stream()
                    .filter(rock -> rock.getCoordinate().equals(currentAnimation.getCoordinate()))
                    .findFirst()
                    .orElse(null);

            if (matchingRock != null) {
                Execution.delay(RandomGenerator.nextInt(750, 3000));
                if (matchingRock.interact("Mine")) {
                    log("[Mining] Interacted with: Rockertunity");
                    Execution.delayUntil(RandomGenerator.nextInt(1500, 3000), animations::isEmpty);
                }
            }
        }
        return random.nextLong(1500, 3000);
    }

    private static long handleMiningInteractions(LocalPlayer player, List<String> selectedRockNames) {
        if (player.getAnimationId() == -1) {
            return interactWithSelectedRocks(player, selectedRockNames);
        }

        return random.nextLong(1500, 3000);
    }
    private static void usePorter() {
        String currentPorter = porterTypes[currentPorterType.get()];
        int varbitValue = VarManager.getInvVarbit(94, 2, 30214);

        if (Backpack.contains(currentPorter) && varbitValue <= getGraceChargesThreshold()) {
            log("[Mining] Porters have " + varbitValue + " charges. Charging.");
            log("[Caution] Interacting with Equipment - Equipment needs to be OPEN.");
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
                        log("[Success] Interaction with Backpack was successful.");
                    } else {
                        log("[Error] Interaction with Backpack failed.");
                    }
                }
            }
            Execution.delay(random.nextLong(1500, 3000));
        }
    }
}
