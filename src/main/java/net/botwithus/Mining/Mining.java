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
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.inventory.equipment.Slot.NECK;

public class Mining {

    private static long handleBackpack(LocalPlayer player) {
        if (!Backpack.isFull()) {
            return 0;
        }
        if (Backpack.containsItemByCategory(4448)) {
            Execution.delay(fillOreBox());
            if (Backpack.isFull()) {
                log("[Mining] Backpack is still full after filling ore box. Changing state to BANKING.");
                return random.nextLong(1500, 3000);
            }
            return random.nextLong(500, 1000);
        }


        if (nearestBank) {
            sendToBank(player);
            return random.nextLong(500, 1000);
        }

        log("[Mining] Backpack is full. Dropping all ores.");
        dropAllOres();
        return random.nextLong(1500, 3000);
    }

    private static long fillOreBox() {
        Pattern oreBoxesPattern = Pattern.compile("(?i)Bronze ore box|Iron ore box|Steel ore box|Mithril ore box|Adamant ore box|Rune ore box|Orikalkum ore box|Necronium ore box|Bane ore box|Elder rune ore box");

        Item oreBox = InventoryItemQuery.newQuery().name(oreBoxesPattern).results().first();

        if (oreBox != null) {
            int oreBoxSlot = oreBox.getSlot();

            component(1, oreBoxSlot, 96534533);

            Execution.delayUntil(random.nextInt(4500, 6000), () -> !Backpack.isFull());

            if (!Backpack.isFull()) {
                log("[Mining] Filled: " + oreBox.getName());
                return random.nextLong(1500, 3000);
            } else {
                log("[Error] Failed to interact with the ore box.");
            }
        } else {
            log("[Error] Ore box not found in the backpack.");
        }

        return 0;
    }


    private static void sendToBank(LocalPlayer player) {
        setLastSkillingLocation(player.getCoordinate());
        Execution.delay(random.nextLong(1500, 3000));
        setBotState(BANKING);
        log("[Mining] Traversing to bank.");
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
        long backpackDelay = handleBackpack(player);
        if (backpackDelay > 0) {
            return backpackDelay;
        }

        if (player.isMoving()) {
            return random.nextLong(1500, 3000);
        }

        if(useGote) {
            setLastSkillingLocation(player.getCoordinate());
            Execution.delay(random.nextLong(1500, 3000));
            usePorter();
        }

        long headbarDelay = handleHeadbars(player, selectedRockNames);
        if (headbarDelay > 0) {
            return headbarDelay;
        }

        return handleMiningInteractions(player, selectedRockNames);
    }

    private static long handleHeadbars(LocalPlayer player, List<String> selectedRockNames) {
        Optional<Headbar> bar = player.getHeadbars().stream()
                .filter(headbar -> headbar.getId() == 5 && headbar.getWidth() < RandomGenerator.nextInt(140, 180))
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
                    Execution.delayUntil(RandomGenerator.nextInt(1500, 3000), () -> player.getAnimationId() == -1);
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

        if (Backpack.contains(currentPorter) && varbitValue <= getEquipChargeThreshold()) {
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
        } else if (!Backpack.contains(currentPorter) && varbitValue <= getEquipChargeThreshold()) {
            if (nearestBank) {
                log("[Error] No " + currentPorter + " found in the Backpack");
                setBotState(BANKING);
            } else {
                log("[Error] No " + currentPorter + " found in the Backpack, and Banking is Disabled");
                useGote = false;
            }
        }
    }
}
