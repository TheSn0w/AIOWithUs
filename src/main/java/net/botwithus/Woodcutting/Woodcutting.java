package net.botwithus.Woodcutting;

import net.botwithus.Variables.Variables;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.inventory.equipment;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.Regex;


import java.util.*;
import java.util.regex.Pattern;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.inventory.equipment.Slot.NECK;

public class Woodcutting {

    public static long handleSkillingWoodcutting(LocalPlayer player, List<String> selectedTreeNames) {
        if(useGote) {
            setLastSkillingLocation(player.getCoordinate());
            Execution.delay(random.nextLong(1500, 3000));
            usePorter();
        }

        if (player.isMoving() || player.getAnimationId() != -1) {
            return random.nextLong(750, 1250);
        }

        if (acadiaTree || acadiaVIP) {

            Execution.delay(handleAcadiaTree());
        }
        if (!acadiaTree || !acadiaVIP) {
            Execution.delay(handleSkilling(player, selectedTreeNames));
        }
        return 0;
    }

    public static long handleFullBackpack(LocalPlayer player) {
        if (nearestBank) {
            setLastSkillingLocation(player.getCoordinate());
            log("[Woodcutting] Backpack is full. We're banking.");
            setBotState(BANKING);
            return random.nextLong(1500, 3000);
        }

        log("[Woodcutting] Backpack is full. Dropping all logs...");
        dropAllLogs();
        return random.nextLong(1500, 3000);
    }

    public static void dropAllLogs() {
        ResultSet<Item> allItems = InventoryItemQuery.newQuery(93).results();

        for (Item item : allItems) {
            if (item != null) {
                String itemName = item.getName();
                int category = item.getConfigType().getCategory();

                if (ActionBar.containsItem(itemName)) {
                    boolean success = ActionBar.useItem(itemName, "Drop");
                    if (success) {
                        log("[Woodcutting] Dropping (ActionBar): " + itemName);
                        Execution.delay(random.nextLong(206, 405));
                    }
                } else if (category == 22) {
                    boolean success = backpack.interact(itemName, "Drop");
                    if (success) {
                        log("[Woodcutting] Dropping (Backpack): " + itemName);
                        Execution.delay(random.nextLong(620, 650));
                    }
                }
            }
        }
    }

    public static long handleAcadiaTree() {
        if (Backpack.isFull()) {
            return handleFullBackpack((LocalPlayer) player);
        }
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Acadia tree").option("Cut down").results();
        if (results.isEmpty()) {
            log("[Error] No Acadia trees found.");
            return random.nextLong(750, 1000);
        } else {
            return interactWithAcadiaTree(results);
        }
    }

    public static long interactWithAcadiaTree(EntityResultSet<SceneObject> results) {
            if (results.isEmpty()) {
                log("[Error] No Acadia trees found.");
            } else {
                SceneObject nearestTree = results.nearest();
                if (nearestTree == null) {
                    log("[Error] Nearest tree is null.");
                } else {
                    currentTreeCoordinate = nearestTree.getCoordinate();

                    SceneObject treeStump = SceneObjectQuery.newQuery().name("Tree stump").results().nearestTo(currentTreeCoordinate);
                    if (treeStump == null || !treeStump.getCoordinate().equals(currentTreeCoordinate)) {
                        log("[Woodcutting] Interacting with the nearest tree again.");
                        if (nearestTree.interact("Cut down")) {
                            log("[Woodcutting] Successfully re-interacted with the nearest tree.");
                            Execution.delay(random.nextLong(1500, 3000));
                        } else {
                            log("[Error] Failed to re-interact with the nearest tree.");
                        }
                    } else {
                        List<Coordinate> currentTreeCoordinates = new ArrayList<>();
                        if (acadiaVIP) {
                            currentTreeCoordinates = vipTreeCoordinates;
                            log("[Woodcutting] VIP mode is active. Using VIP tree coordinates.");
                        } else if (acadiaTree) {
                            currentTreeCoordinates = treeCoordinates;
                            log("[Woodcutting] Acadia tree mode is active. Using regular tree coordinates.");
                        }
                        Collections.shuffle(currentTreeCoordinates); // Shuffle the treeCoordinates list
                        currentTreeIndex = (currentTreeIndex + 1) % currentTreeCoordinates.size();
                        Coordinate nextTreeCoordinate = currentTreeCoordinates.get(currentTreeIndex);

                        SceneObject nextTreeStump = SceneObjectQuery.newQuery().name("Tree stump").results().nearestTo(nextTreeCoordinate);
                        if (nextTreeStump != null && nextTreeStump.getCoordinate().equals(nextTreeCoordinate)) {
                            log("[Woodcutting] Tree stump found at the next tree coordinate. Skipping this coordinate.");
                        } else {
                            SceneObject nextTree = SceneObjectQuery.newQuery().name("Acadia tree").option("Cut down").results().stream()
                                    .filter(tree -> tree.getCoordinate().equals(nextTreeCoordinate))
                                    .findFirst()
                                    .orElse(null);

                            if (nextTree == null) {
                                log("[Error] Next tree at designated coordinates not found.");
                            } else if (!nextTree.interact("Cut down")) {
                                log("[Error] Failed to interact with the next tree.");
                            } else {
                                log("[Woodcutting] Interacted with another tree at: " + nextTreeCoordinate);
                                Execution.delay(random.nextLong(1500, 3000));
                            }
                        }
                    }
                }
            }
        return random.nextLong(750, 1000);
    }

    public static long handleOtherTrees(List<String> selectedTreeNames) {
        SceneObject nearestTree = SceneObjectQuery.newQuery().name(selectedTreeNames.toArray(new String[0])).hidden(false).results().nearest();
        if (nearestTree != null) {
            List<String> availableOptions = nearestTree.getOptions();
            if (availableOptions.contains("Chop")) {
                nearestTree.interact("Chop");
                log("[Woodcutting] Interacted with: " + nearestTree.getName() + " using 'Chop' option");
            } else if (availableOptions.contains("Chop down")) {
                nearestTree.interact("Chop down");
                log("[Woodcutting] Interacted with: " + nearestTree.getName() + " using 'Chop down' option");
            }
        } else {
            log("[Woodcutting] No suitable tree found.");
        }

        return random.nextLong(1467, 1985);
    }

    private static final Pattern woodboxPattern = Regex.getPatternForContainingOneOf("Wood box", "wood box");
    private static final Pattern logPattern = Regex.getPatternForContainingOneOf("Logs", "logs");

    private void fillBox(Item woodbox) {
        Component woodboxComp = ComponentQuery.newQuery(1473).componentIndex(5).itemName(woodbox.getName()).option("Fill").results().first();
        if (woodboxComp != null) {
            log("Filled woodbox: " + woodboxComp.interact("Fill"));
        }
    }

    private static long handleSkilling(LocalPlayer player, List<String> selectedTreeNames) {

        if (Backpack.isFull()) {

            Item woodbox = InventoryItemQuery.newQuery(93).name(woodboxPattern).results().first();
            if (woodbox == null || woodbox.getId() == -1) {
                log("We did not find our woodox, so we should bank.");
                if (nearestBank) {
                    setLastSkillingLocation(player.getCoordinate());
                    log("Moving to banking state");
                    setBotState(BANKING);
                    return random.nextLong(1500, 3000);
                } else {
                    log("We did not find our woodbox, but we are not near a bank, so we should drop the logs.");
                    dropAllLogs();
                }
            } else {
                //we found our woodbox
                log("Yay, found found our woodbox: " + woodbox.getName());

                //do calcs
                if (woodbox.getName() != null) {
                    int capacity = getBaseWoodboxCapacity(woodbox.getName());
                    capacity = capacity + getAdditionalWoodboxCapacity();
                    log("Our expected capacity is: " + capacity);
                    Item logs = InventoryItemQuery.newQuery(93).name(logPattern).results().first();
                    if (logs == null && logs.getId() == -1 && logs.getName() == null) {
                        log("No log found in inventory.");
                    } else {
                        //we found the log, and can proceed
                        Item logsStored = InventoryItemQuery.newQuery(937).name(logs.getName()).results().first();
                        if (logsStored == null || logsStored.getId() == -1) {
                            log("We didnt find logs in the woodbox, but we have one, so fill it.");
                        } else {
                            //good to finally fill if math maths
                            if (logsStored.getStackSize() >= capacity) {
                                //we cant fill, our woodbox is full, and we should actually bank
                                if (nearestBank) {
                                    setLastSkillingLocation(player.getCoordinate());
                                    log("Moving to banking state");
                                    setBotState(BANKING);
                                    return random.nextLong(1500, 3000);
                                } else {
                                    log("Our woodbox is full, but banking is disabled, so we should drop the logs, have on action bar for faster dropping.");
                                    dropAllLogs();
                                }
                            }
                        }
                        //we can fill our box
                        Component woodboxComp = ComponentQuery.newQuery(1473).componentIndex(5).itemName(woodbox.getName()).option("Fill").results().first();
                        if (woodboxComp != null) {
                            log("Filled woodbox: " + woodboxComp.interact("Fill"));
                        }
                    }
                }
            }
            return random.nextLong(1500,3000);
        }

        /*log("Anim id: " + Variables.player.getAnimationId());
        log("Player moving: " + Variables.player.isMoving());*/
        if (player.getAnimationId() != -1 || player.isMoving()) {
            return random.nextLong(1500,5000);
        }

        for (String treeName : selectedTreeNames) {
            SceneObject nearestTree = SceneObjectQuery.newQuery().name(treeName).hidden(false).results().stream()
                    .sorted(Comparator.comparingInt(tree -> (int) tree.getCoordinate().distanceTo(player.getCoordinate()))) // Sort by distance to player
                    .filter(tree -> tree.getOptions().contains("Chop down") || tree.getOptions().contains("Chop"))
                    .findFirst()
                    .orElse(null);

            if (nearestTree != null) {
                String action = nearestTree.getOptions().contains("Chop down") ? "Chop down" : "Chop";
                log("Interacted tree: " + treeName + "  " + nearestTree.interact(action));
            }
        }

        return random.nextLong(1500,3000);
    }

    public static int getAdditionalWoodboxCapacity() {
        int level = Skills.WOODCUTTING.getActualLevel();
        for (int threshold = 95; threshold > 0; threshold -= 10) {
            if (level >= threshold)
                return threshold + 5;
        }
        return 0;
    }

    public static int getBaseWoodboxCapacity(String woodboxName) {
        switch (woodboxName) {
            case "Wood box":
                return 70;
            case "Oak wood box":
                return 80;
            case "Willow wood box":
                return 90;
            case "Teak wood box":
                return 100;
            case "Maple wood box":
                return 110;
            case "Acadia wood box":
                return 120;
            case "Mahogany wood box":
                return 130;
            case "Yew wood box":
                return 140;
            case "Magic wood box":
                return 150;
            case "Elder wood box":
                return 160;
        }
        return 0;
    }


    /*public static long handleSkillingWoodcutting(LocalPlayer player, List<String> selectedTreeNames) {
        if (Backpack.isFull()) {
            if (nearestBank) {
                setLastSkillingLocation(player.getCoordinate());
                SnowsScript.setBotState(SnowsScript.BotState.BANKING);
                return random.nextLong(1500, 3000);
            }

            log("[Woodcutting] Backpack is full. Dropping all logs...");

            ResultSet<Item> allItems = InventoryItemQuery.newQuery(93).results();

            for (Item item : allItems) {
                if (item != null) {
                    String itemName = item.getName();
                    int category = item.getConfigType().getCategory();

                    if (ActionBar.containsItem(itemName)) {
                        boolean success = ActionBar.useItem(itemName, "Drop");
                        if (success) {
                            log("[Woodcutting] Dropping (ActionBar): " + itemName);
                            Execution.delay(random.nextLong(206, 405));
                        }
                    } else if (category == 22) {
                        boolean success = backpack.interact(itemName, "Drop");
                        if (success) {
                            log("[Woodcutting] Dropping (Backpack): " + itemName);
                            Execution.delay(random.nextLong(620, 650));
                        }
                    }
                }
            }
            return random.nextLong(1500, 3000);
        }
        GroundItem nearestBirdNest = GroundItemQuery.newQuery().name("Bird's nest").results().nearest();
        if (nearestBirdNest != null) {
            log("[Woodcutting] Interacted with: Bird nest");
            nearestBirdNest.interact("Take");
            Execution.delay(random.nextLong(1500, 3000));
        }

        if (player.isMoving() || player.getAnimationId() != -1) {
            return random.nextLong(1500, 7000);

        }
        if (acadiaTree || acadiaVIP) {
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Acadia tree").option("Cut down").results();
            if (results.isEmpty()) {
                log("[Error] No Acadia trees found.");
            } else {
                SceneObject nearestTree = results.nearest();
                if (nearestTree == null) {
                    log("[Error] Nearest tree is null.");
                } else {
                    currentTreeCoordinate = nearestTree.getCoordinate();

                    SceneObject treeStump = SceneObjectQuery.newQuery().name("Tree stump").results().nearestTo(currentTreeCoordinate);
                    if (treeStump == null || !treeStump.getCoordinate().equals(currentTreeCoordinate)) {
                        log("[Woodcutting] Interacting with the nearest tree again.");
                        if (nearestTree.interact("Cut down")) {
                            log("[Woodcutting] Successfully re-interacted with the nearest tree.");
                            Execution.delay(random.nextLong(1500, 3000));
                        } else {
                            log("[Error] Failed to re-interact with the nearest tree.");
                        }
                    } else {
                        List<Coordinate> currentTreeCoordinates = new ArrayList<>();
                        if (acadiaVIP) {
                            currentTreeCoordinates = vipTreeCoordinates;
                            log("[Woodcutting] VIP mode is active. Using VIP tree coordinates.");
                        } else if (acadiaTree) {
                            currentTreeCoordinates = treeCoordinates;
                            log("[Woodcutting] Acadia tree mode is active. Using regular tree coordinates.");
                        }
                        Collections.shuffle(currentTreeCoordinates); // Shuffle the treeCoordinates list
                        currentTreeIndex = (currentTreeIndex + 1) % currentTreeCoordinates.size();
                        Coordinate nextTreeCoordinate = currentTreeCoordinates.get(currentTreeIndex);

                        SceneObject nextTreeStump = SceneObjectQuery.newQuery().name("Tree stump").results().nearestTo(nextTreeCoordinate);
                        if (nextTreeStump != null && nextTreeStump.getCoordinate().equals(nextTreeCoordinate)) {
                            log("[Woodcutting] Tree stump found at the next tree coordinate. Skipping this coordinate.");
                        } else {
                            SceneObject nextTree = SceneObjectQuery.newQuery().name("Acadia tree").option("Cut down").results().stream()
                                    .filter(tree -> tree.getCoordinate().equals(nextTreeCoordinate))
                                    .findFirst()
                                    .orElse(null);

                            if (nextTree == null) {
                                log("[Error] Next tree at designated coordinates not found.");
                            } else if (!nextTree.interact("Cut down")) {
                                log("[Error] Failed to interact with the next tree.");
                            } else {
                                log("[Woodcutting] Interacted with another tree at: " + nextTreeCoordinate);
                                Execution.delay(random.nextLong(1500, 3000));
                            }
                        }
                    }
                }
            }
        } else {
            for (String treeName : selectedTreeNames) {
                SceneObject nearestTree = SceneObjectQuery.newQuery().name(treeName).option("Chop down").hidden(false).results().nearest();
                if (nearestTree != null) {
                    boolean Success = nearestTree.interact("Chop down");
                    if (Success) {
                        log("[Woodcutting] Interacted with: " + treeName);
                        Execution.delay(random.nextLong(1500, 3000));
                    }
                }
            }
        }
        return random.nextLong(750, 1000);
    }*/

    public static long handleCrystallise() {
        EntityResultSet<SpotAnimation> animations = SpotAnimationQuery.newQuery().ids(5802).results();
        if (animations.isEmpty()) {
            Component Crystallise = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673).spriteId(25939).option("Customise-keybind").results().first();
            if (Crystallise != null) {
                log("[Woodcutting] Used Crystallise spell: " + MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, Crystallise.getInterfaceIndex() << 16 | Crystallise.getComponentIndex()));
            }
            Execution.delay(random.nextLong(450, 500));

            MiniMenu.interact(SelectableAction.SELECT_OBJECT.getType(), TREE_OBJECT_ID, currentTreeCoordinate.getX(), currentTreeCoordinate.getY());
            log("[Woodcutting] Cast Crystallise on: Acadia Tree");
            Execution.delay(random.nextLong(750, 1250));


            boolean success = SceneObjectQuery.newQuery().name("Acadia tree").option("Cut down").results().nearest().interact("Cut down");
            if (success) {
                log("[Woodcutting] Interacted with: Acadia Tree");
                Execution.delay(random.nextLong(750, 1250));
            }
        }
        return random.nextLong(750, 1250);
    }

    public static long handleit() {
        EntityResultSet<SpotAnimation> animations = SpotAnimationQuery.newQuery().ids(5802).results();
        if (animations.isEmpty()) {
            Execution.delay(handleAcadiaCrystallise((LocalPlayer) player));
        } else {
            Execution.delay(handleCrystallise());
        }
        return 0;
    }

    public static long handleAcadiaCrystallise(LocalPlayer player) {
        EntityResultSet<SceneObject> acadiaTree = SceneObjectQuery.newQuery().name("Acadia tree").option("Cut down").results();
        if (!acadiaTree.isEmpty()) {
            SceneObject nearestTree = acadiaTree.nearest();
            if (nearestTree != null) {

                currentTreeCoordinate = nearestTree.getCoordinate();

                boolean Success = nearestTree.interact("Cut down");
                if (Success) {
                    log("[Woodcutting] Interacted with: Acadia Tree");
                    Execution.delay(random.nextLong(750, 1250));
                }
            }
        }
        return handleCrystallise();
    }

    private static long nextCrystalliseDelay = random.nextInt(21000, 22500);

    public static long handleCrystalliseMahogany() {
        if (System.currentTimeMillis() - lastCrystalliseCast >= nextCrystalliseDelay) {
            Component Crystallise = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673).spriteId(25939).option("Customise-keybind").results().first();
            if (Crystallise != null) {
                log("[Woodcutting] Used Crystallise spell: " + MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, Crystallise.getInterfaceIndex() << 16 | Crystallise.getComponentIndex()));
                Execution.delay(random.nextLong(450, 500));

                for (Coordinate mahoganyCoordinate : mahoganyCoordinates) {
                    SceneObject nearestTree = SceneObjectQuery.newQuery().name("Mahogany").option("Chop down").hidden(false).results().stream()
                            .filter(tree -> tree.getCoordinate().equals(mahoganyCoordinate))
                            .findFirst()
                            .orElse(null);
                    if (nearestTree != null) {
                        MiniMenu.interact(SelectableAction.SELECT_OBJECT.getType(), 70076, mahoganyCoordinate.getX(), mahoganyCoordinate.getY());
                        log("[Woodcutting] Interacted with: Tree");
                        Execution.delay(random.nextLong(750, 1000));

                        boolean Success = nearestTree.interact("Chop down");
                        if (Success) {
                            log("[Woodcutting] Interacted with: Mahogany Tree");
                            Execution.delay(random.nextLong(1500, 3000));
                        }
                    }
                }

                lastCrystalliseCast = System.currentTimeMillis();
                nextCrystalliseDelay = random.nextInt(21000, 22500);
            }
        }
        return random.nextLong(750, 1000);
    }
    private static void usePorter() {
        String currentPorter = porterTypes[currentPorterType.get()];
        int varbitValue = VarManager.getInvVarbit(94, 2, 30214);

        if (Backpack.contains(currentPorter) && varbitValue <= getGraceChargesThreshold()) {
            log("[Woodcutting] Porters have " + varbitValue + " charges. Charging.");
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

