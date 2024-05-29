package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
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

public class Woodcutting {
    private final Random random = new Random();
    public static boolean acadiaTree = false;

    public static String Tree = "";
    public static final List<String> selectedTreeNames = new ArrayList<>();

    public static String getTreeName() {
        return Tree;
    }

    public static List<String> getSelectedTreeNames() {
        return selectedTreeNames;
    }

    public static void addTreeName(String name) {
        if (!selectedTreeNames.contains(name)) {
            selectedTreeNames.add(name);
        }
    }

    public static void removeTreeName(String name) {
        selectedTreeNames.remove(name);
    }

    public static void setTreeName(String TreeName) {
        Tree = TreeName;
    }

    private final SnowsScript skeletonScript;

    public Woodcutting(SnowsScript script) {
        this.skeletonScript = script;
    }

    int currentTreeIndex = 0;
    List<Coordinate> treeCoordinates = Arrays.asList(
            new Coordinate(3183, 2722, 0),
            new Coordinate(3183, 2716, 0),
            new Coordinate(3189, 2722, 0)
    );
    List<Coordinate> vipTreeCoordinates = Arrays.asList(
            new Coordinate(3180, 2753, 0),
            new Coordinate(3180, 2747, 0)
    );
    List<Coordinate> mahoganyCoordinates = Arrays.asList(
            new Coordinate(2819, 3079, 0)
    );

    public static boolean acadiaVIP = false;
    public static boolean crystallise = false;
    private Coordinate currentTreeCoordinate = null; // Add this line
    private long lastCrystalliseCast = 0;
    public static boolean crystalliseMahogany = false;


    long handleSkillingWoodcutting(LocalPlayer player, List<String> selectedTreeNames) {
        if (Backpack.isFull()) {
            if (skeletonScript.nearestBank) {
                skeletonScript.setLastSkillingLocation(player.getCoordinate());
                SnowsScript.setBotState(SnowsScript.BotState.BANKING);
                return random.nextLong(1500, 3000);
            }

            ScriptConsole.println("Backpack is full. Dropping all logs...");

            ResultSet<Item> allItems = InventoryItemQuery.newQuery(93).results();

            for (Item item : allItems) {
                if (item != null) {
                    String itemName = item.getName();
                    int category = item.getConfigType().getCategory();

                    if (ActionBar.containsItem(itemName)) {
                        boolean success = ActionBar.useItem(itemName, "Drop");
                        if (success) {
                            ScriptConsole.println("Dropping (ActionBar): " + itemName);
                            Execution.delay(random.nextLong(206, 405));
                        }
                    } else if (category == 22) {
                        boolean success = Backpack.interact(itemName, "Drop");
                        if (success) {
                            ScriptConsole.println("Dropping (Backpack): " + itemName);
                            Execution.delay(random.nextLong(620, 650));
                        }
                    }
                }
            }
            return random.nextLong(1500, 3000);
        }
        GroundItem nearestBirdNest = GroundItemQuery.newQuery().name("Bird's nest").results().nearest();
        if (nearestBirdNest != null) {
            ScriptConsole.println("Interacted with: Bird nest");
            nearestBirdNest.interact("Take");
            Execution.delay(random.nextLong(1500, 3000));
        }

        if (player.isMoving() || player.getAnimationId() != -1) {
            return random.nextLong(1500, 7000);

        }
        if (acadiaTree || acadiaVIP) {
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Acadia tree").option("Cut down").results();
            if (results.isEmpty()) {
                ScriptConsole.println("No Acadia trees found.");
            } else {
                SceneObject nearestTree = results.nearest();
                if (nearestTree == null) {
                    ScriptConsole.println("Nearest tree is null.");
                } else {
                    currentTreeCoordinate = nearestTree.getCoordinate(); // Update the current tree coordinate

                    SceneObject treeStump = SceneObjectQuery.newQuery().name("Tree stump").results().nearestTo(currentTreeCoordinate);
                    if (treeStump == null || !treeStump.getCoordinate().equals(currentTreeCoordinate)) {
                        ScriptConsole.println("No stump found at the tree location or stump is not at the expected tree coordinate. Interacting with the nearest tree again.");
                        if (nearestTree.interact("Cut down")) {
                            ScriptConsole.println("Successfully re-interacted with the nearest tree.");
                            Execution.delay(random.nextLong(1500, 3000));
                        } else {
                            ScriptConsole.println("Failed to re-interact with the nearest tree.");
                        }
                    } else {
                        List<Coordinate> currentTreeCoordinates = new ArrayList<>();
                        if (acadiaVIP) {
                            currentTreeCoordinates = vipTreeCoordinates;
                            ScriptConsole.println("VIP mode is active. Using VIP tree coordinates.");
                        } else if (acadiaTree) {
                            currentTreeCoordinates = treeCoordinates;
                            ScriptConsole.println("Acadia tree mode is active. Using regular tree coordinates.");
                        }
                        Collections.shuffle(currentTreeCoordinates); // Shuffle the treeCoordinates list
                        currentTreeIndex = (currentTreeIndex + 1) % currentTreeCoordinates.size();
                        Coordinate nextTreeCoordinate = currentTreeCoordinates.get(currentTreeIndex);

                        SceneObject nextTreeStump = SceneObjectQuery.newQuery().name("Tree stump").results().nearestTo(nextTreeCoordinate);
                        if (nextTreeStump != null && nextTreeStump.getCoordinate().equals(nextTreeCoordinate)) {
                            ScriptConsole.println("Tree stump found at the next tree coordinate. Skipping this coordinate.");
                        } else {
                            SceneObject nextTree = SceneObjectQuery.newQuery().name("Acadia tree").option("Cut down").results().stream()
                                    .filter(tree -> tree.getCoordinate().equals(nextTreeCoordinate))
                                    .findFirst()
                                    .orElse(null);

                            if (nextTree == null) {
                                ScriptConsole.println("Next tree at designated coordinates not found.");
                            } else if (!nextTree.interact("Cut down")) {
                                ScriptConsole.println("Failed to interact with the next tree.");
                            } else {
                                ScriptConsole.println("Interacted with another tree at: " + nextTreeCoordinate);
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
                        ScriptConsole.println("Interacted with: " + treeName);
                        Execution.delay(random.nextLong(1500, 3000));
                    }
                }
            }
        }
        return random.nextLong(750, 1000);
    }
    private static final int TREE_OBJECT_ID = 109007; // Acadia in VIP
    Player player = Client.getLocalPlayer();

    public long handleCrystallise() {
        EntityResultSet<SpotAnimation> animations = SpotAnimationQuery.newQuery().ids(5802).results();
        if (animations.isEmpty()) {

            MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, 109510839);
            ScriptConsole.println("Interacted with: Crystallise");
            Execution.delay(random.nextLong(450, 500));

            MiniMenu.interact(SelectableAction.SELECT_OBJECT.getType(), TREE_OBJECT_ID, currentTreeCoordinate.getX(), currentTreeCoordinate.getY());
            ScriptConsole.println("Cast Crystallise on: Acadia Tree");
            Execution.delay(random.nextLong(750, 1250));


            boolean success = SceneObjectQuery.newQuery().name("Acadia tree").option("Cut down").results().nearest().interact("Cut down");
            if (success) {
                ScriptConsole.println("Interacted with: Acadia Tree");
                Execution.delay(random.nextLong(750, 1250));
            }
        }
        return random.nextLong(750, 1250);
    }

    public long handleit() {
        EntityResultSet<SpotAnimation> animations = SpotAnimationQuery.newQuery().ids(5802).results();
        if (animations.isEmpty()) {
            Execution.delay(handleAcadiaCrystallise((LocalPlayer) player));
        } else {
            Execution.delay(handleCrystallise());
        }
        return 0;
    }

    public long handleAcadiaCrystallise(LocalPlayer player) {
        EntityResultSet<SceneObject> acadiaTree = SceneObjectQuery.newQuery().name("Acadia tree").option("Cut down").results();
        if (!acadiaTree.isEmpty()) {
            SceneObject nearestTree = acadiaTree.nearest();
            if (nearestTree != null) {

                currentTreeCoordinate = nearestTree.getCoordinate();

                boolean Success = nearestTree.interact("Cut down");
                if (Success) {
                    ScriptConsole.println("Interacted with: Acadia Tree");
                    Execution.delay(random.nextLong(750, 1250));
                }
            }
        }
        return handleCrystallise();
    }

    private long nextCrystalliseDelay = random.nextInt(21000, 22500);

    public long handleCrystalliseMahogany() {
        if (System.currentTimeMillis() - lastCrystalliseCast >= nextCrystalliseDelay) {
            if (ActionBar.containsAbility("Crystallise")) {
                MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, 109510839);
                ScriptConsole.println("Interacted with: Crystallise");
                Execution.delay(random.nextLong(450, 500));

                for (Coordinate mahoganyCoordinate : mahoganyCoordinates) {
                    SceneObject nearestTree = SceneObjectQuery.newQuery().name("Mahogany").option("Chop down").hidden(false).results().stream()
                            .filter(tree -> tree.getCoordinate().equals(mahoganyCoordinate))
                            .findFirst()
                            .orElse(null);
                    if (nearestTree != null) {
                        MiniMenu.interact(SelectableAction.SELECT_OBJECT.getType(), 70076, mahoganyCoordinate.getX(), mahoganyCoordinate.getY());
                        ScriptConsole.println("Interacted with: Tree");
                        Execution.delay(random.nextLong(750, 1000));

                        boolean Success = nearestTree.interact("Chop down");
                        if (Success) {
                            ScriptConsole.println("Interacted with: Mahogany Tree");
                            Execution.delay(random.nextLong(1500, 3000));
                        }
                    }
                }

                // Update the lastCrystalliseCast time and nextCrystalliseDelay
                lastCrystalliseCast = System.currentTimeMillis();
                nextCrystalliseDelay = random.nextInt(21000, 22500);
            }
        }
        return random.nextLong(750, 1000);
    }
}

