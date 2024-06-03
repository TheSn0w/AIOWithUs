package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.GroundItemQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.scene.entities.item.GroundItem;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;


import java.util.*;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.logCount;

public class Woodcutting {

    public static long handleSkillingWoodcutting(LocalPlayer player, List<String> selectedTreeNames) {
        if (Backpack.isFull()) {
            return handleFullBackpack(player);
        }

        if (player.isMoving() || player.getAnimationId() != -1) {
            return random.nextLong(1500, 7000);
        }

        if (acadiaTree || acadiaVIP) {
            return handleAcadiaTree();
        } else {
            return handleOtherTrees(selectedTreeNames);
        }
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
        for (String treeName : selectedTreeNames) {
            SceneObject nearestTree = SceneObjectQuery.newQuery().name(treeName).option("Chop down").hidden(false).results().nearest();
            if (nearestTree != null) {
                boolean Success = nearestTree.interact("Chop down");
                if (Success) {
                    log("[Woodcutting] Interacted with: " + treeName);
                    return random.nextLong(750, 1000);
                }
            }
        }
        return random.nextLong(750, 1000);
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
}

