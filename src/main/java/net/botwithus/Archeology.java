package net.botwithus;

import net.botwithus.Variables.Variables;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.*;
import java.util.function.Supplier;

import static ImGui.SkeletonScriptGraphicsContext.*;
import static net.botwithus.Variables.Variables.*;

public class Archeology {
    public static Random random = new Random();
    public SnowsScript skeletonScript;
    final Map<String, Supplier<Long>> methodMap;
    final Map<String, Coordinate> coordinateMap;


    public Archeology(SnowsScript script) {
        this.skeletonScript = script;
        this.coordinateMap = new HashMap<>();
        this.methodMap = new HashMap<>();
        this.initializeCoordinateMap();
    }

    public void updateChatMessageEvent(ChatMessageEvent event) {
        String message = event.getMessage();
        if (isArcheologyActive) {
            if (message.contains("You transport the following item to your material storage:")) {
                String[] parts = message.split("storage: ");
                if (parts.length > 1) {
                    String materialType = parts[1].trim();
                    materialType = materialType.replace(".", "");
                    int count = materialsExcavated.getOrDefault(materialType, 0);
                    materialsExcavated.put(materialType, count + 1);
                }
            }
            if (message.contains("You find some")) {
                String[] parts = message.split("some ");
                if (parts.length > 1) {
                    String materialType = parts[1].trim();
                    materialType = materialType.replace(".", "");
                    int count = materialsExcavated.getOrDefault(materialType, 0);
                    materialsExcavated.put(materialType, count + 1);
                }
            }
        }
    }

    private void initializeCoordinateMap() {
        coordinateMap.put("Administratum debris", new Coordinate(2448, 7569, 0));
        coordinateMap.put("Castra debris", new Coordinate(2444, 7585, 0));
        coordinateMap.put("Legionary remains", new Coordinate(2437, 7596, 0));
        coordinateMap.put("Venator remains", new Coordinate(3373, 3190, 0));
        coordinateMap.put("Lodge art storage", new Coordinate(2589, 7331, 0));
        coordinateMap.put("Lodge bar storage", new Coordinate(2589, 7331, 0));
        coordinateMap.put("Material cache (samite silk)", new Coordinate(3373, 3200, 0));
    }

    public static String Rock1 = "";
    public static List<String> selectedArchNames = new ArrayList<>();

    public static String getName() {
        return Rock1;
    }

    public static List<String> getSelectedNames() {
        return selectedArchNames;
    }

    public static void addName(String name) {
        if (!selectedArchNames.contains(name)) {
            selectedArchNames.add(name);
        }
    }

    public static void removeName(String name) {
        selectedArchNames.remove(name);
    }

    public static void setName(String Rock1Name) {
        Rock1 = Rock1Name;
    }

    private Coordinate lastSpotAnimationCoordinate = null;

    public long findSpotAnimationAndAct(LocalPlayer player, List<String> selectedArchNames) {
        if (selectedArchNames == null || selectedArchNames.isEmpty()) {
            ScriptConsole.println("[Archaeology] No Excavation names provided.");
            return random.nextLong(1500, 3000);
        }

        String targetName = selectedArchNames.stream()
                .filter(name -> name != null && !name.isEmpty())
                .findFirst()
                .orElse(null);

        boolean isNearPlayer = SceneObjectQuery.newQuery()
                .name(targetName)
                .results()
                .nearest() != null;

        if (Variables.MaterialCache) {
            if (!isNearPlayer) {
                return handleExcavation(targetName);
            } else {
                return MaterialCaches(player, selectedArchNames);
            }
        }

        if (targetName == null) {
            return random.nextLong(1500, 3000);
        }


        if (isNearPlayer) {
            return doSomeArch(player, selectedArchNames);
        } else {
            return handleExcavation(targetName);
        }
    }

    public long doSomeArch(LocalPlayer player, List<String> selectedArchNames) {
        if (Backpack.isFull()) {
            return backpackIsFull(player);
        }
        if (useGote) {
            if (Backpack.contains("Sign of the porter VII")) {
                ScriptConsole.println("[Archaeology] We have Porters in the inventory. Checking charges.");
                if (VarManager.getInvVarbit(94, 2, 30214) < 250) {
                    ScriptConsole.println("[Archaeology] Porters have less than 250 charges. Charging.");
                    Equipment.interact(Equipment.Slot.NECK, "Charge all porters");
                    Execution.delay(RandomGenerator.nextInt(1500, 3000));
                }
            }
        }
        if (archaeologistsTea || materialManual || hiSpecMonocle) {
            if (archaeologistsTea) {
                useArchaeologistsTea();
            }
            if (materialManual) {
                useMaterialManual();
            }
            if (hiSpecMonocle) {
                useHiSpecMonocle();
            }
        }

        long checkInterval = RandomGenerator.nextInt(3000, 10000);

        long defaultDelay = 1000;

        boolean playerIdle = (player == null || player.getAnimationId() == -1);


        SpotAnimation currentSpotAnimation = SpotAnimationQuery.newQuery()
                .ids(7307)
                .results()
                .nearest();

        if (currentSpotAnimation == null) {
            ScriptConsole.println("[Archaeology] No spot animation found.");
            return interactWithDefaultObjects(selectedArchNames, checkInterval);
        }

        Coordinate currentCoord = currentSpotAnimation.getCoordinate();

        boolean spotAnimationMoved = (lastSpotAnimationCoordinate == null || !lastSpotAnimationCoordinate.equals(currentCoord));

        if (spotAnimationMoved) {
            ScriptConsole.println("[Archaeology] Spot animation has moved.");
            Execution.delay(RandomGenerator.nextInt(1500, 3000));
            lastSpotAnimationCoordinate = currentCoord;
        }

        String[] archNamesArray = selectedArchNames.toArray(new String[0]);
        SceneObjectQuery query = SceneObjectQuery.newQuery()
                .name(archNamesArray)
                .hidden(false);

        SceneObject matchingArchObject = query.results()
                .nearestTo(currentCoord);

        if (matchingArchObject != null && matchingArchObject.getCoordinate().equals(currentCoord)) {
            if (spotAnimationMoved || playerIdle) {
                if (selectedArchNames.contains(matchingArchObject.getName())) {
                    matchingArchObject.interact("Excavate");
                    ScriptConsole.println("[Archaeology] Interacting with: " + matchingArchObject.getName());
                    return checkInterval;
                }
            }
        }
        if (playerIdle) {
            SceneObject nearestObject = query.results()
                    .nearestTo(currentCoord);

            if (nearestObject != null && selectedArchNames.contains(nearestObject.getName())) {
                nearestObject.interact("Excavate");
                ScriptConsole.println("[Archaeology] Interacting with: " + nearestObject.getName());
                return checkInterval;
            }
        }

        return defaultDelay;
    }
    private void useArchaeologistsTea() { // 47027 must be material + 1
        if (VarManager.getVarbitValue(47028) == 0) {
            if (Backpack.contains("Archaeologist's tea")) {
                Backpack.interact("Archaeologist's tea", "Drink");
                Execution.delay(RandomGenerator.nextInt(1500, 3000));
            }
        }
    }
    private void useHiSpecMonocle() {
        if (VarManager.getVarbitValue(47026) == 0) {
            if (Backpack.contains("Hi-spec monocle")) {
                Backpack.interact("Hi-spec monocle", "Wear");
                Execution.delay(RandomGenerator.nextInt(1500, 3000));
            }
        }
    }
    private void useMaterialManual() {
        if (VarManager.getVarbitValue(47025) == 0) {
            if (Backpack.contains("Material manual")) {
                Backpack.interact("Material manual", "Read");
                Execution.delay(RandomGenerator.nextInt(1500, 3000));
            }
        }
    }

    private long interactWithDefaultObjects(List<String> selectedArchNames, long checkInterval) {
        for (String excavationName : selectedArchNames) {
            SceneObject nearestSceneObject = SceneObjectQuery.newQuery()
                    .name(excavationName)
                    .hidden(false)
                    .results()
                    .nearest();

            if (nearestSceneObject != null) {
                nearestSceneObject.interact("Excavate");
                ScriptConsole.println("[Archaeology] Interacting with: " + nearestSceneObject.getName());
                return checkInterval;
            }
        }

        return checkInterval;
    }

    public long MaterialCaches(LocalPlayer player, List<String> selectedArchNames) {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(115427).option("Use").results();
        if (Backpack.isFull()) {
            if (!results.isEmpty()) {
                ScriptConsole.println("[Archaeology] Backpack is full, using Bank.");
                skeletonScript.setLastSkillingLocation(player.getCoordinate());
                handleBankInteraction(player, selectedArchNames);
            } else {
                skeletonScript.setLastSkillingLocation(player.getCoordinate());
                SnowsScript.setBotState(SnowsScript.BotState.BANKING);
                return random.nextLong(1500, 3000);
            }
        }

        if (player.isMoving() || player.getAnimationId() != -1) {
            return random.nextLong(1500, 5000);
        }

        for (String excavationName : selectedArchNames) {
            SceneObject nearestSceneObject = SceneObjectQuery.newQuery()
                    .name(excavationName)
                    .hidden(false)
                    .results()
                    .nearest();

            if (nearestSceneObject != null) {
                nearestSceneObject.interact("Excavate");
                ScriptConsole.println("[Archaeology] Interacting with: " + nearestSceneObject.getName());
                return random.nextLong(1500, 3000);
            }
        }

        return random.nextLong(1500, 3000);
    }


    public long BankforArcheology(LocalPlayer player, List<String> selectedArchNames) {
        Coordinate bankChestCoordinate = new Coordinate(3362, 3397, 0);
        skeletonScript.setLastSkillingLocation(player.getCoordinate());

        ScriptConsole.println("[Archaeology] Teleporting to bank.");
        if (Movement.traverse(NavPath.resolve(bankChestCoordinate)) == TraverseEvent.State.FINISHED) {
            ScriptConsole.println("[Archaeology] Finished traversing to bank.");
            Execution.delay(handleBankInteraction(player, selectedArchNames));
        }
        return random.nextLong(1500, 3000);
    }

    private boolean shouldInteractWithMaterialsCart(LocalPlayer player) {
        EntityResultSet<SceneObject> cartResults = SceneObjectQuery.newQuery()
                .name("Materials cart")
                .option("Deposit materials")
                .results();

        if (!cartResults.isEmpty()) {
            SceneObject cart = cartResults.first();
            double distanceToPlayer = cart.getCoordinate().distanceTo(player.getCoordinate());
            return distanceToPlayer <= 25.0D;
        }

        return false;
    }

    private long interactWithMaterialsCart(LocalPlayer player) {
        SceneObject materialsCart = SceneObjectQuery.newQuery()
                .name("Materials cart")
                .option("Deposit materials")
                .results()
                .nearest();

        boolean interactionSuccess = materialsCart.interact("Deposit materials");

        if (interactionSuccess) {
            ScriptConsole.println("[Archaeology] Interacted with the material cart.");
            Execution.delayUntil(360000, () -> !Backpack.isFull());

            ScriptConsole.println("[Archaeology] Returning to last location");
            Movement.traverse(NavPath.resolve(skeletonScript.getLastSkillingLocation().getRandomWalkableCoordinate()));
            SnowsScript.setBotState(SnowsScript.BotState.SKILLING);
            return random.nextLong(1500, 3000);
        } else {
            ScriptConsole.println("[Archaeology] Failed to interact with material cart.");
            return random.nextLong(1500, 3000);
        }
    }


    private long handleBankInteraction(LocalPlayer player, List<String> selectedArchNames) {
        interactWithBankChest();
        ScriptConsole.println("[Archaeology] Waiting for bank to open.");
        waitForBankToOpen();

        ResultSet<Item> soilBox = findSoilBoxInInventory();
        if (Interfaces.isOpen(517)) {
            ScriptConsole.println("[Archaeology] Bank is open.");

            delayRandomly();
            depositAllExceptSelectedItems();
            logItemsDeposited();

            delayRandomly();
            interactWithSoilBoxIfPresent(soilBox);

            if (useGote) {
                handleGoteCharges();
            }

            delayRandomly();
            returnToLastLocation(player, selectedArchNames);
            return randomDelay();
        }

        ScriptConsole.println("[Archaeology] Bank did not open.");
        return random.nextLong(750, 1250);
    }

    private void interactWithBankChest() {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery()
                .name("Bank chest")
                .option("Use")
                .results();

        if (!results.isEmpty()) {
            SceneObject nearestBankChest = results.nearest();

            if (nearestBankChest != null) {
                for (int attempts = 0; attempts < 3; attempts++) {
                    if (nearestBankChest.interact("Use")) {
                        Execution.delay(randomDelay());
                        return;
                    } else {
                        Execution.delay(randomDelay());
                    }
                }
                ScriptConsole.println("[Error] Failed to interact with Bank chest after 3 attempts.");
            } else {
                ScriptConsole.println("[Error] No nearest Bank chest found.");
            }
        } else {
            ScriptConsole.println("[Error] No Bank chest found.");
        }
    }

    private void waitForBankToOpen() {
        boolean bankOpened = Execution.delayUntil(30000, () -> Interfaces.isOpen(517));
        if (!bankOpened) {
            ScriptConsole.println("[Error] Bank did not open within 30 seconds.");
        }
    }

    private ResultSet<Item> findSoilBoxInInventory() {
        return InventoryItemQuery.newQuery(93).name("Archaeological soil box").results();
    }

    private void delayRandomly() {
        Execution.delay(RandomGenerator.nextInt(1500, 3000));
    }

    private void depositAllExceptSelectedItems() {
        int[] itemIdsToKeep = {49538, 50096, 4614, 49976, 50431, 49947, 49949, 50753};
        Bank.depositAllExcept(itemIdsToKeep);
    }

    private void logItemsDeposited() {
        ScriptConsole.println("[Archaeology] Deposited all items except selected.");
    }

    private void interactWithSoilBoxIfPresent(ResultSet<Item> soilBox) {
        if (soilBox != null && !soilBox.isEmpty()) {
            int slotIndex = soilBox.first().getSlot();
            if (slotIndex >= 0) {
                boolean interactionSuccess = MiniMenu.interact(ComponentAction.COMPONENT.getType(), 9, slotIndex, 33882127);
                if (interactionSuccess) {
                    ScriptConsole.println("[Archaeology] Interacted with Archaeological soil box.");
                } else {
                    ScriptConsole.println("[Error] Failed to interact with Archaeological soil box.");
                }
            } else {
                ScriptConsole.println("[Error] Invalid slot index for Archaeological soil box.");
            }
        } else {
            ScriptConsole.println("[Error] No Archaeological soil box found in inventory.");
        }
    }

    private void handleGoteCharges() {
        int charges = VarManager.getInvVarbit(94, 2, 30214);
        ScriptConsole.println("Charges remaining: " + charges);
        if (charges < 250) {
            String selectedPorter = porterTypes[currentPorterType.get()];
            int quantity = getQuantityFromOption(quantities[currentQuantity.get()]);
            boolean withdrew = false;
            if (VarManager.getVarbitValue(45189) != 7) {
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 33882215);
                withdrew = Bank.withdraw(selectedPorter, quantity);
            } else {
                withdrew = Bank.withdraw(selectedPorter, quantity);
            }
            if (withdrew) {
                ScriptConsole.println("[Archaeology] Withdrew: " + selectedPorter + ".");
            } else {
                ScriptConsole.println("[Error] Failed to withdraw " + selectedPorter + ".");
            }
        }
    }

    private int getQuantityFromOption(String option) {
        return switch (option) {
            case "ALL" -> 1;
            case "1" -> 2;
            case "5" -> 3;
            case "10" -> 4;
            case "Preset amount" -> 5;
            default -> 0;
        };
    }

    private void returnToLastLocation(LocalPlayer player, List<String> selectedArchNames) {
        ScriptConsole.println("[Archaeology] Returning to last location");

        if (shouldTraverseToHellfire(selectedArchNames)) {
            traverseToHellfireLift(selectedArchNames);
        }
        if (shouldTraverseToKharidEt(selectedArchNames)) {
            traverseToKharidEt(selectedArchNames);
        } else {
            traverseToLastSkillingLocation();
        }
    }
    private boolean shouldTraverseToKharidEt(List<String> selectedArchNames) {
        return selectedArchNames.contains("Legionary remains")
                || selectedArchNames.contains("Castra debris")
                || selectedArchNames.contains("Administratum debris")
                || selectedArchNames.contains("Praesidio remains");

    }

    private boolean shouldTraverseToHellfire(List<String> selectedArchNames) {
        return selectedArchNames.contains("Sacrificial altar")
                || selectedArchNames.contains("Dis dungeon debris")
                || selectedArchNames.contains("Cultist footlocker")
                || selectedArchNames.contains("Lodge bar storage")
                || selectedArchNames.contains("Lodge art storage");
    }
    private void traverseToKharidEt(List<String> selectedArchNames) {
        Coordinate hellfire = new Coordinate(3374, 3181, 0);
        ScriptConsole.println("[Archaeology] Traversing to Fort Entrance.");

        if (Movement.traverse(NavPath.resolve(hellfire)) == TraverseEvent.State.FINISHED) {
            ScriptConsole.println("[Archaeology] Finished traversing to Fort Entrance.");
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116926).option("Enter").results();
            if (!results.isEmpty()) {
                SceneObject Fort = results.first();
                if (Fort != null && Fort.interact("Enter")) {
                    Execution.delayUntil(5000, () -> Interfaces.isOpen(720));
                    ScriptConsole.println("[Archaeology] Interface is Open.");
                    Execution.delay(random.nextLong(1000, 1500));
                    interactWithDialogOption(selectedArchNames);
                    Execution.delay(random.nextLong(4500, 6000));
                    traverseToLastSkillingLocation();
                } else {
                    ScriptConsole.println("[Error] Failed to interact with Hellfire Lift.");
                }
            } else {
                ScriptConsole.println("[Error] No Hellfire Lift found.");
            }
        } else {
            ScriptConsole.println("[Error] Failed to traverse to Hellfire Lift.");
        }
    }

    private void traverseToHellfireLift(List<String> selectedArchNames) {
        Coordinate hellfire = new Coordinate(3263, 3504, 0);
        ScriptConsole.println("[Archaeology] Traversing to Hellfire Lift.");

        if (Movement.traverse(NavPath.resolve(hellfire)) == TraverseEvent.State.FINISHED) {
            ScriptConsole.println("[Archaeology] Finished traversing to Hellfire Lift.");
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116691).option("Descend").results();
            if (!results.isEmpty()) {
                SceneObject hellfireLift = results.first();
                if (hellfireLift != null && hellfireLift.interact("Descend")) {
                    Execution.delayUntil(5000, () -> Interfaces.isOpen(720));
                    ScriptConsole.println("[Archaeology] Interface is Open.");
                    Execution.delay(random.nextLong(1000, 1500));
                    interactWithDialogOption(selectedArchNames);
                    Execution.delay(random.nextLong(4500, 6000));
                    traverseToLastSkillingLocation();
                } else {
                    ScriptConsole.println("[Error] Failed to interact with Hellfire Lift.");
                }
            } else {
                ScriptConsole.println("[Error] No Hellfire Lift found.");
            }
        } else {
            ScriptConsole.println("[Error] Failed to traverse to Hellfire Lift.");
        }
    }

    private void interactWithDialogOption(List<String> selectedArchNames) {
        if ((selectedArchNames.contains("Sacrificial altar") || selectedArchNames.contains("Dis dungeon debris") || selectedArchNames.contains("Cultist footlocker"))) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185940);
            ScriptConsole.println("[Archaeology] Selecting: Dungeon of Disorder.");
        } else if ((selectedArchNames.contains("Lodge bar storage") || selectedArchNames.contains("Lodge art storage"))) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185921);
            ScriptConsole.println("[Archaeology] Selecting: Star Lodge cellar.");
        } else if ((selectedArchNames.contains("Legionary remains") || selectedArchNames.contains("Castra debris") || selectedArchNames.contains("Administratum debris"))) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185921);
            ScriptConsole.println("[Archaeology] Selecting: Main Fortress.");
        } else if (selectedArchNames.contains("Praesidio remains")) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185940);
            ScriptConsole.println("[Archaeology] Selecting: Prison block.");
        } else {
            ScriptConsole.println("[Error] No valid dialog option found.");
            traverseToLastSkillingLocation();
        }
    }
    private void traverseToLastSkillingLocation() {
        if (Movement.traverse(NavPath.resolve(skeletonScript.getLastSkillingLocation())) == TraverseEvent.State.FINISHED) {
            ScriptConsole.println("[Archaeology] Finished traversing to last location.");
            SnowsScript.setBotState(SnowsScript.BotState.SKILLING);
        } else {
            ScriptConsole.println("[Error] Failed to traverse to last location.");
        }
    }

    /*private void traverseToLastSkillingLocation() {
        Movement.walkTo(skeletonScript.getLastSkillingLocation().getCoordinate().getX(), skeletonScript.getLastSkillingLocation().getCoordinate().getY(), true);
            ScriptConsole.println("[Archaeology] Finished traversing to last location.");
            SkeletonScript.setBotState(SkeletonScript.BotState.SKILLING);
    }*/


    private long randomDelay() {
        return random.nextLong(1500, 3000);
    }

    public long handleExcavation(String targetName) {
        Coordinate targetCoordinate = coordinateMap.get(targetName);

        if (targetCoordinate == null) {
            ScriptConsole.println("[Archeology] No coordinate for " + targetName);
            return random.nextLong(1500, 3000);
        }

        EntityResultSet<SceneObject> results = SceneObjectQuery
                .newQuery()
                .name(targetName)
                .option("Excavate")
                .results();

        if (results.isEmpty()) {
            ScriptConsole.println("[Archeology] No " + targetName + " found.");

            Movement.traverse(NavPath.resolve(targetCoordinate)); // Traverse to coordinate
            return random.nextLong(1500, 3000);
        }

        SceneObject nearest = results.nearest();
        if (nearest != null) {
            SnowsScript.setBotState(SnowsScript.BotState.SKILLING);
            return random.nextLong(1500, 3000);
        }

        return random.nextLong(1500, 3000);
    }

    public long backpackIsFull(LocalPlayer player) {
        if (Backpack.contains("Archaeological soil box")) {
            Backpack.interact("Archaeological soil box", "Fill");
            ScriptConsole.println("[Archeology] Filling soil box.");
            Execution.delay(RandomGenerator.nextInt(1500, 3000));
        }

        if (Backpack.isFull()) {
            SnowsScript.setBotState(SnowsScript.BotState.BANKING);
        }

        return random.nextLong(1500, 3000);
    }
}