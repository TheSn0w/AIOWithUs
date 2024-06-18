package net.botwithus.Runecrafting;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.PlayerQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Runecrafting.Runecrafting.ScriptState.*;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;

public class Runecrafting {

    public static long lastMovedOrAnimatedTime = System.currentTimeMillis();
    private static final Pattern superRestorePattern = Pattern.compile("Super restore.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern familiarPattern = Pattern.compile("Abyssal parasite|Abyssal lurker|Abyssal titan", Pattern.CASE_INSENSITIVE);
    public static final Map<String, Integer> runeQuantities = new ConcurrentHashMap<>();
    public static Player player = Client.getLocalPlayer();
    public static boolean hopDuetoPlayers = false;

    public static Map<String, Integer> getRuneQuantities() {
        return runeQuantities;
    }

    public static ScriptState getCurrentState() {
        return currentState;
    }

    public static ScriptState currentState = IDLE;


    public enum ScriptState {
        IDLE,
        BANKING,
        TELEPORTING,
        INTERACTINGWITHPORTAL,
        CRAFTING,
        WORLDHOPPING,
        TELEPORTINGTOBANK, ScriptState,
    }

    public static void handleRunecrafting(LocalPlayer player) {

        boolean playerIsIdle = !player.isMoving() && player.getAnimationId() == -1;
        if (playerIsIdle && System.currentTimeMillis() - lastMovedOrAnimatedTime > 20000) {
            log("[Error] Player has been idle for more than 20 seconds, teleporting to bank.");
            currentState = TELEPORTINGTOBANK;
            lastMovedOrAnimatedTime = System.currentTimeMillis();
            return;
        }

        checkForOtherPlayersAndHopWorld();

        if (useWorldhop && currentState == BANKING) {
            if (nextWorldHopTime == 0) {
                int waitTimeInMs = RandomGenerator.nextInt(minHopIntervalMinutes * 60 * 1000, maxHopIntervalMinutes * 60 * 1000);
                nextWorldHopTime = System.currentTimeMillis() + waitTimeInMs;
                log("Next hop scheduled in " + (waitTimeInMs / 60000) + " minutes.");
                return;
            }

            if (System.currentTimeMillis() >= nextWorldHopTime) {
                int randomMembersWorldsIndex = RandomGenerator.nextInt(membersWorlds.length);
                HopWorlds(membersWorlds[randomMembersWorldsIndex]);
                log("Hopped to world: " + membersWorlds[randomMembersWorldsIndex]);

                nextWorldHopTime = 0;
                currentState = WORLDHOPPING;
                return;
            }
        }
        switch (currentState) {
            case IDLE -> {
                if (SceneObjectQuery.newQuery().name("Bank chest").results().nearest() != null || SceneObjectQuery.newQuery().name("Rowboat").results().nearest() != null) {
                    currentState = BANKING;
                    log("[Runecrafting] Proceeding to bank.");
                } else {
                    currentState = TELEPORTINGTOBANK;
                    log("[Runecrafting] Proceeding to teleport to bank.");
                }
                Execution.delay(RandomGenerator.nextInt(2500, 5000));
            }

            case BANKING -> {
                if (useGraceoftheElves) {
                    useGote();
                }
                if (RingofDueling) {
                    useRoD();
                }
            }
            case TELEPORTING -> {
                interactWithRing();
            }
            case INTERACTINGWITHPORTAL -> {
                interactWithDarkPortal();
                currentState = CRAFTING;
            }
            case CRAFTING -> {
                Execution.delay(RandomGenerator.nextInt(300, 600));
                if (HandleMiasmaAltar) {
                    handleMiasmaAltar();
                } else if (HandleBoneAltar) {
                    handleBoneAltar();
                } else if (HandleSpiritAltar) {
                    handleSpiritAltar();
                } else if (HandleFleshAltar) {
                    handleFleshAltar();
                }
            }
            case TELEPORTINGTOBANK -> {
                if (RingofDueling) {
                    useRoD();
                }
                if (useGraceoftheElves) {
                    useGote();
                }
                ++loopCounter;
                currentState = BANKING;
            }
            case WORLDHOPPING -> {
                if (nextWorldHopTime == 0) {
                    Execution.delayUntil(10000, () -> Client.getGameState() == Client.GameState.LOGGED_IN);
                    currentState = BANKING;
                }
            }
        }
    }
    /*public static void checkForOtherPlayersAndHopWorld() {
        if (hopDuetoPlayers) {
            if (currentState.equals(INTERACTINGWITHPORTAL) || currentState.equals(CRAFTING)) {
                Player localPlayer = Client.getLocalPlayer();
                if (localPlayer == null) {
                    log("Local player not found.");
                    return;
                }
                String localPlayerName = localPlayer.getName();
                Coordinate localPlayerLocation = localPlayer.getCoordinate();

                PlayerQuery query = PlayerQuery.newQuery();

                EntityResultSet<Player> players = query.results();

                boolean otherPlayersPresent = players.stream()
                        .filter(player -> !player.getName().equals(localPlayerName))
                        .filter(player -> {
                            Coordinate playerLocation = player.getCoordinate();
                            return playerLocation != null && localPlayerLocation.distanceTo(playerLocation) <= 25.0D;
                        })
                        .peek(player -> log("Found player within distance: " + player.getName()))
                        .findAny()
                        .isPresent();

                if (otherPlayersPresent) {
                    log("Other players found within distance. Initiating world hop.");
                    int randomMembersWorldsIndex = RandomGenerator.nextInt(membersWorlds.length);
                    HopWorlds(membersWorlds[randomMembersWorldsIndex]);
                    log("Hopped to world: " + membersWorlds[randomMembersWorldsIndex]);
                    currentState = TELEPORTINGTOBANK;
                }
            }
        }
    }*/

    public static List<String> playerNames = new ArrayList<>();
    public static List<PlayerInfo> playerInfo = new ArrayList<>();


    public static void checkForOtherPlayersAndHopWorld() {
        if (hopDuetoPlayers) {
            if (currentState.equals(INTERACTINGWITHPORTAL) || currentState.equals(CRAFTING)) {
                Player localPlayer = Client.getLocalPlayer();
                if (localPlayer == null) {
                    log("Local player not found.");
                    return;
                }
                String localPlayerName = localPlayer.getName();
                Coordinate localPlayerLocation = localPlayer.getCoordinate();

                PlayerQuery query = PlayerQuery.newQuery();

                EntityResultSet<Player> players = query.results();

                boolean otherPlayersPresent = players.stream()
                        .filter(player -> !player.getName().equals(localPlayerName))
                        .filter(player -> {
                            Coordinate playerLocation = player.getCoordinate();
                            return playerLocation != null && localPlayerLocation.distanceTo(playerLocation) <= 25.0D;
                        })
                        .peek(player -> {
                            log("Found player within distance: " + player.getName());
                            playerInfo.add(new PlayerInfo(player.getName(), System.currentTimeMillis(), currentWorld));
                        })
                        .findAny()
                        .isPresent();

                if (otherPlayersPresent) {
                    log("Other players found within distance. Initiating world hop.");
                    int randomMembersWorldsIndex = RandomGenerator.nextInt(membersWorlds.length);
                    ScriptState previousState = currentState;
                    HopWorlds(membersWorlds[randomMembersWorldsIndex]);
                    log("Hopped to world: " + membersWorlds[randomMembersWorldsIndex]);
                    currentState = previousState;
                }
            }
        }
    }


    private static void useGote() {
        EntityResultSet<SceneObject> bankChests = SceneObjectQuery.newQuery().name("Rowboat").option("Bank").results();

        Coordinate fishingHub = new Coordinate(2135, 7107, 0);

        if (bankChests.isEmpty()) {
            while (player.getAnimationId() == -1 && (player.getCoordinate() != null && !player.getCoordinate().equals(fishingHub))) {
                if (Equipment.interact(Equipment.Slot.NECK, "Deep sea fishing hub")) {
                    log("[Runecrafting] Attempting to traverse to Fishing hub.");
                    Execution.delay(random.nextLong(750, 999));
                }
                if (player.getAnimationId() != -1) {
                    log("[Runecrafting] Player is teleporting.");
                    break;
                }
            }
        } else {
            interactwithBoat();
        }
    }

    private static void interactwithBoat() {
        EntityResultSet<SceneObject> boatResults = SceneObjectQuery.newQuery().name("Rowboat").option("Bank").results();
        SceneObject boat = boatResults.nearest();

        if (boat != null) {
            if (boat.interact("Load Last Preset from")) {
                Execution.delayUntil(5000, () -> Backpack.contains("Impure essence"));

                if (Backpack.contains("Impure essence")) {
                    if (ManageFamiliar) {
                        checkFamiliar();
                    } else {
                        currentState = TELEPORTING;
                        log("[Runecrafting] Changed bot state to TELEPORTING.");
                    }
                } else {
                    log("[Error] Essence not found in backpack after loading preset.");
                    shutdown();
                }
            }
        }
    }

    private static void useRoD() {
        EntityResultSet<SceneObject> bankChests = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
        Coordinate WarsBank = new Coordinate(2446, 3085, 0);

        if (bankChests.isEmpty()) {
            if (Movement.traverse(NavPath.resolve(WarsBank)) == TraverseEvent.State.FINISHED) {
                log("[Runecrafting] Traversed to Castle Wars bank.");
            }
        } else {
            interactwithBankChest();
        }
    }

    private static void interactwithBankChest() {
        EntityResultSet<SceneObject> bankChestsResults = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
        SceneObject bankChest = bankChestsResults.nearest();

        if (bankChest != null) {
            if (bankChest.interact("Load Last Preset from")) {
                Execution.delayUntil(5000, () -> Backpack.contains("Impure essence"));

                if (Backpack.contains("Impure essence")) {
                    if (ManageFamiliar) {
                        checkFamiliar();
                    } else {
                        currentState = TELEPORTING;
                        log("[Runecrafting] Changed bot state to TELEPORTING.");
                    }
                } else {
                    log("[Error] Essence not found in backpack after loading preset.");
                    shutdown();
                }
            }
        }
    }

    public static void checkFamiliar() {
        if (VarManager.getVarbitValue(6055) <= 1) {
            summonFamiliar();
        } else {
            currentState = TELEPORTING;
        }
    }

    private static void summonFamiliar() {
        SceneObject bank = findBank();
        if (bank != null) {
            interactWithBank(bank);
        }
    }

    private static SceneObject findBank() {
        EntityResultSet<SceneObject> bankResults;
        if (useGraceoftheElves) {
            bankResults = SceneObjectQuery.newQuery().name("Rowboat").option("Bank").results();
        } else if (RingofDueling) {
            bankResults = SceneObjectQuery.newQuery().name("Bank chest").results();
        } else {
            return null;
        }
        SceneObject bank = bankResults.nearest();
        if (bank != null) {
            log("[Runecrafting] Found " + bank.getName());
        }
        return bank;
    }

    private static void interactWithBank(SceneObject bank) {
        String interaction = useGraceoftheElves ? "Bank" : "Use";
        if (bank.interact(interaction)) {
            log("[Runecrafting] Interacting with " + bank.getName());
            if (waitForBankInterface()) {
                performBankingActions();
            }
        }
    }

    private static boolean waitForBankInterface() {
        Execution.delayUntil(5000, () -> Interfaces.isOpen(517));
        if (Interfaces.isOpen(517)) {
            log("[Runecrafting] Bank interface is open.");
            Execution.delay(random.nextLong(600, 800));
            return true;
        }
        return false;
    }

    private static void performBankingActions() {
        Bank.depositAll();
        log("[Runecrafting] Deposited all items.");
        Execution.delay(random.nextLong(600, 800));
        interactWithComponents();
        withdrawItems();
        closeBank();
    }

    private static void interactWithComponents() {
        if (VarManager.getVarbitValue(45141) != 1) {
            component(1, -1, 33882270);
            log("[Runecrafting] Interacting with component: " + VarManager.getVarbitValue(45141));
            Execution.delay(random.nextLong(600, 800));
        }
        if (VarManager.getVarbitValue(45189) != 2) {
            component(1, -1, 33882205);
            log("[Runecrafting] Interacting with component: " + VarManager.getVarbitValue(45189));
            Execution.delay(random.nextLong(600, 800));
        }
    }

    private static void withdrawItems() {
        boolean restoreWithdrawn = Bank.withdraw(superRestorePattern, 2);
        Execution.delay(random.nextLong(600, 800));
        log("[Runecrafting] Attempted to withdraw Super Restore.");

        boolean familiarWithdrawn = Bank.withdraw(familiarPattern, 2);
        Execution.delay(random.nextLong(600, 800));
        log("[Runecrafting] Attempted to withdraw Familiar.");

        if (!familiarWithdrawn || !restoreWithdrawn) {
            ManageFamiliar = false;
            log("[Runecrafting] Familiar or Super Restore not found in bank. Disabling familiar usage.");
        }
    }

    private static void closeBank() {
        Bank.close();
        Execution.delayUntil(5000, () -> !Interfaces.isOpen(517));
        log("[Runecrafting] Closed bank.");
        if (!Interfaces.isOpen(517)) {
            if (!ManageFamiliar) {
                currentState = BANKING;
                return;
            }
            performPostBankingActions();
        }
    }

    private static void performPostBankingActions() {
        Item superRestoreItem = backpack.getItem(superRestorePattern);
        if (superRestoreItem != null && backpack.interact(superRestoreItem.getName(), "Drink")) {
            log("[Runecrafting] Drinking: " + superRestoreItem.getName());
            Execution.delay(random.nextLong(600, 800));
        }

        Item familiarItem = backpack.getItem(familiarPattern);
        if (familiarItem != null && backpack.interact(familiarItem.getName(), "Summon")) {
            log("[Runecrafting] Summoning: " + familiarItem.getName());
            Execution.delay(random.nextLong(600, 800));
        }
        currentState = BANKING;
    }


    private static void interactWithRing() {
        if (notWearingRing && Backpack.contains("Impure essence") && backpack.contains("Passing bracelet")) {
            log("[Runecrafting] Not wearing ring, but backpack contains 'Impure essence' and 'Passing bracelet'.");
            backpack.interact("Passing bracelet", "Rub");
            log("[Runecrafting] Interacted with 'Passing bracelet'.");

            Execution.delay(RandomGenerator.nextInt(600, 800));
            Execution.delayUntil(5000, () -> Interfaces.isOpen(720));

            if (Interfaces.isOpen(720)) {
                log("[Runecrafting] Interface 720 is open.");
                dialog(0, -1, 47185940);
                log("[Runecrafting] Interacting with 'Haunt on the Hill'.");

                Execution.delay(RandomGenerator.nextInt(3500, 5000));
                Execution.delayUntil(RandomGenerator.nextInt(5000, 10000), () -> player.getAnimationId() == -1);

                lastMovedOrAnimatedTime = System.currentTimeMillis();
                currentState = INTERACTINGWITHPORTAL;
            }
        } else if (WearingRing && Backpack.contains("Impure essence") && Equipment.contains("Passing bracelet")) {
            log("[Runecrafting] Wearing ring and backpack contains 'Impure essence' and 'Passing bracelet'.");

            ResultSet<Item> results = InventoryItemQuery.newQuery().name("Passing bracelet").option("City of Um: Haunt on the Hill").results();
            if (!results.isEmpty()) {
                log("[Runecrafting] Found 'Passing bracelet' with option 'City of Um: Haunt on the Hill' in inventory.");
                Equipment.interact(Equipment.Slot.HANDS, "City of Um: Haunt on the Hill");
                log("[Runecrafting] Interacted with 'City of Um: Haunt on the Hill'.");

                Execution.delay(RandomGenerator.nextInt(3500, 5000));
                Execution.delayUntil(RandomGenerator.nextInt(5000, 10000), () -> player.getAnimationId() == -1);

                lastMovedOrAnimatedTime = System.currentTimeMillis();
                currentState = INTERACTINGWITHPORTAL;
            } else {
                log("[Runecrafting] 'Passing bracelet' with option 'City of Um: Haunt on the Hill' not found in inventory.");
            }
        } else {
            log("[Error] Ring Option not chosen, or wrong option chosen.");
        }
    }

    private static void interactWithDarkPortal() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            SceneObject Portal = SceneObjectQuery.newQuery().name("Dark portal").results().nearest();

            if (Portal != null) {
                while (!player.isMoving()) {
                    Portal.interact("Enter");
                    log("[Runecrafting] Attempting to interact with the Dark Portal.");
                    Execution.delay(random.nextLong(600, 800));

                    if (player.isMoving()) {
                        break;
                    }
                }

                if (Portal.distanceTo(player.getCoordinate()) >= 10) {
                    boolean surgeUsed = Surge();
                    if (surgeUsed) {
                        Portal.interact("Enter");
                        log("[Runecrafting] Attempting to interact with the Dark Portal after Surging.");
                    }
                }

                lastMovedOrAnimatedTime = System.currentTimeMillis();
                currentState = CRAFTING;
            } else {
                log("[Error] Dark portal not found.");
            }
        }
    }


    public static void updateRuneQuantities() {

        Map<String, Integer> runeIDs = Map.of(
                "Miasma Runes", 55340,
                "Flesh Runes", 55339,
                "Bone Runes", 55338,
                "Spirit Runes", 55337
        );

        runeIDs.forEach((runeName, id) -> {
            int quantity = Backpack.getQuantity(id);
            runeQuantities.put(runeName, runeQuantities.getOrDefault(runeName, 0) + quantity);
        });
    }

    private static void handleMiasmaAltar() {
        EntityResultSet<SceneObject> altarResults = SceneObjectQuery.newQuery().id(127383).option("Craft runes").results();
        SceneObject altar = altarResults.nearest();

        if (altar != null && !player.isMoving() && player.getAnimationId() == -1) {

            if (Powerburst) {
                if (Math.random() <= 0.85) {
                    Powerburst();
                    Execution.delay(RandomGenerator.nextInt(450, 550));
                } else {
                    log("[Runecrafting] Forgot to use Powerburst.");
                }
            }

            altar.interact("Craft runes");
            Execution.delayUntil(5000, () -> player.isMoving());
            log("[Runecrafting] Attempted to interact with Miasma altar.");

            if (player.isMoving()) {
                boolean surgeUsed = Surge();
                if (surgeUsed) {
                    altar.interact("Craft runes");
                    log("[Runecrafting] Attempting to interact with Spirit altar after Surging.");
                }
            }
            checkForRuneAndAnimationMiasma(System.currentTimeMillis());
        }
    }

    private static void checkForRuneAndAnimationMiasma(long startTime) {
        while (System.currentTimeMillis() - startTime < (long) 10000) {
            if (Backpack.contains("Miasma rune")) {
                log("[Runecrafting] Miasma rune found in backpack, proceeding to next state.");
                updateRuneQuantities();
                lastMovedOrAnimatedTime = System.currentTimeMillis();
                currentState = TELEPORTINGTOBANK;
                break;
            } else {
                Execution.delay(random.nextLong(100, 200));
            }
        }

        if (currentState != TELEPORTINGTOBANK) {
            log("[Error] Condition not met within the timeout period.");
            updateRuneQuantities();
            Execution.delay(RandomGenerator.nextInt(650, 800));
            lastMovedOrAnimatedTime = System.currentTimeMillis();
            currentState = TELEPORTINGTOBANK;
        }
    }


    private static void handleBoneAltar() {
        EntityResultSet<SceneObject> altarResults = SceneObjectQuery.newQuery().id(127381).option("Craft runes").results();
        SceneObject altar = altarResults.nearest();

        if (altar != null && !player.isMoving() && player.getAnimationId() == -1) {

            if (Powerburst) {
                if (Math.random() <= 0.85) {
                    Powerburst();
                    Execution.delay(RandomGenerator.nextInt(450, 550));
                } else {
                    log("[Runecrafting] Forgot to use Powerburst.");
                }
            }

            altar.interact("Craft runes");
            Execution.delayUntil(5000, () -> player.isMoving());
            log("[Runecrafting] Attempted to interact with Bone altar.");

            if (player.isMoving()) {
                boolean surgeUsed = Surge();
                if (surgeUsed) {
                    altar.interact("Craft runes");
                    log("[Runecrafting] Attempting to interact with Spirit altar after Surging.");
                }
            }
            checkForRuneAndAnimationBone(System.currentTimeMillis());
        }
    }

    private static void checkForRuneAndAnimationBone(long startTime) {
        while (System.currentTimeMillis() - startTime < (long) 10000) {
            if (Backpack.contains("Bone rune") && player.getAnimationId() == -1) {
                log("[Runecrafting] Bone rune found in backpack, proceeding to next state.");
                updateRuneQuantities();
                lastMovedOrAnimatedTime = System.currentTimeMillis();
                currentState = TELEPORTINGTOBANK;
                break;
            } else {
                Execution.delay(random.nextLong(100, 200));
            }
        }

        if (currentState != TELEPORTINGTOBANK) {
            log("[Error] Condition not met within the timeout period.");
            updateRuneQuantities();
            Execution.delay(RandomGenerator.nextInt(650, 800));
            lastMovedOrAnimatedTime = System.currentTimeMillis();
            currentState = TELEPORTINGTOBANK;
        }
    }

    private static void handleSpiritAltar() {
        EntityResultSet<SceneObject> altarResults = SceneObjectQuery.newQuery().id(127380).option("Craft runes").results();
        SceneObject altar = altarResults.nearest();

        if (altar != null && !player.isMoving() && player.getAnimationId() == -1) {

            if (Powerburst) {
                if (Math.random() <= 0.85) {
                    Powerburst();
                    Execution.delay(RandomGenerator.nextInt(450, 550));
                } else {
                    log("[Runecrafting] Forgot to use Powerburst.");
                }
            }

            altar.interact("Craft runes");
            Execution.delayUntil(5000, () -> player.isMoving());
            log("[Runecrafting] Attempted to interact with Spirit altar.");

            if (player.isMoving()) {
                boolean surgeUsed = Surge();
                if (surgeUsed) {
                    altar.interact("Craft runes");
                    log("[Runecrafting] Attempting to interact with Spirit altar after Surging.");
                }
            }
            checkForRuneAndAnimationSpirit(System.currentTimeMillis());
        }
    }

    private static void checkForRuneAndAnimationSpirit(long startTime) {
        while (System.currentTimeMillis() - startTime < (long) 10000) {
            if (Backpack.contains("Spirit rune") && player.getAnimationId() == -1) {
                log("[Runecrafting] Spirit rune found in backpack, proceeding to next state.");
                updateRuneQuantities();
                lastMovedOrAnimatedTime = System.currentTimeMillis();
                currentState = TELEPORTINGTOBANK;
                break;
            } else {
                Execution.delay(random.nextLong(100, 200));
            }
        }

        if (currentState != TELEPORTINGTOBANK) {
            log("[Error] Condition not met within the timeout period.");
            updateRuneQuantities();
            Execution.delay(RandomGenerator.nextInt(650, 800));
            lastMovedOrAnimatedTime = System.currentTimeMillis();
            currentState = TELEPORTINGTOBANK;
        }
    }


    private static void handleFleshAltar() {
        EntityResultSet<SceneObject> altarResults = SceneObjectQuery.newQuery().id(127382).option("Craft runes").results();
        SceneObject altar = altarResults.nearest();

        if (altar != null && !player.isMoving() && player.getAnimationId() == -1) {

            if (Powerburst) {
                if (Math.random() <= 0.85) {
                    Powerburst();
                    Execution.delay(RandomGenerator.nextInt(450, 550));
                } else {
                    log("[Runecrafting] Forgot to use Powerburst.");
                }
            }

            altar.interact("Craft runes");
            Execution.delayUntil(5000, () -> player.isMoving());
            log("[Runecrafting] Attempted to interact with Flesh altar.");

            if (player.isMoving()) {
                boolean surgeUsed = Surge();
                if (surgeUsed) {
                    altar.interact("Craft runes");
                    log("[Runecrafting] Attempting to interact with Spirit altar after Surging.");
                }
            }
            checkForRuneAndAnimationFlesh(System.currentTimeMillis());
        }
    }

    private static void checkForRuneAndAnimationFlesh(long startTime) {
        while (System.currentTimeMillis() - startTime < (long) 10000) {
            if (Backpack.contains("Flesh rune") && player.getAnimationId() == -1 && !player.isMoving()) {
                log("[Runecrafting] Flesh rune found in backpack, proceeding to next state.");
                updateRuneQuantities();
                lastMovedOrAnimatedTime = System.currentTimeMillis();
                currentState = TELEPORTINGTOBANK;
                break;
            } else {
                Execution.delay(random.nextLong(100, 200));
            }
        }

        if (currentState != TELEPORTINGTOBANK) {
            log("[Error] Condition not met within the timeout period.");
            updateRuneQuantities();
            Execution.delay(RandomGenerator.nextInt(650, 800));
            lastMovedOrAnimatedTime = System.currentTimeMillis();
            currentState = TELEPORTINGTOBANK;
        }
    }


    private static void Powerburst() {
        if (!canUsePotion()) {
            log("[Runecrafting] Powerburst of sorcery is on cooldown.");
            return;
        }

        ResultSet<Item> potionItems = InventoryItemQuery.newQuery(93).option("Drink").results();
        Item potion = potionItems.isEmpty() ? null : potionItems.first();

        if (potion != null) {
            boolean drinkSuccess = backpack.interact(potion.getName(), "Drink");

            if (drinkSuccess) {
                log("[Combat] Successfully drank " + potion.getName());
            } else {
                log("[Error] Failed to drink.");
            }
        } else {
            log("[Error] No potion found");
            Powerburst = false;
        }
    }

    public static boolean canUsePotion() {
        Component powerburstCooldown = ComponentQuery.newQuery(291).item(48960).results().first();
        return powerburstCooldown == null;
    }

    private static boolean Surge() {
        if (ActionBar.getCooldown("Surge") <= 0) {
            if (Math.random() <= 0.96) {
                int delayBeforeCasting = RandomGenerator.nextInt(500, 1000);
                Execution.delay(delayBeforeCasting);
            }
            log("[Runecrafting] Surge is not on cooldown. Casting Surge: " + ActionBar.useAbility("Surge"));
            return true;
        } else {
            log("[Caution] Surge is on cooldown, cannot cast.");
        }
        return false;
    }


    public static long handleSoulAltar() {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1500, 3000);
        }
        if (Interfaces.isOpen(1370)) {
            dialog(0, -1, 89784350);
            log("[Runecrafting] Selecting 'Weave'");
            return random.nextLong(1500, 3000);
        }
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Soul altar").option("Craft-rune").results();
        SceneObject soulAltar = results.nearest();
        if (soulAltar != null) {
            soulAltar.interact("Craft-rune");
        } else {
            log("[Runecrafting] Soul altar not found.");
        }
        return random.nextLong(1500, 3000);
    }

    public static long nextWorldHopTime = 0;
    public static int minHopIntervalMinutes = 60; // Default minimum wait time in minutes
    public static int maxHopIntervalMinutes = 180; // Default maximum wait time in minutes
    public static int currentWorld = -1; // Initialize to an invalid world number


    public static void HopWorlds(int world) {
        if (Interfaces.isOpen(1431)) {
            log("[Runecrafting] Interacting with Settings Icon.");
            component(1, 7, 93782016);
            boolean hopperOpen = Execution.delayUntil(5000, () -> Interfaces.isOpen(1433));
            log("Settings Menu Open: " + hopperOpen);
            Execution.delay(RandomGenerator.nextInt(1000, 2000));

            if (hopperOpen) {
                Component HopWorldsMenu = ComponentQuery.newQuery(1433).componentIndex(65).results().first();
                if (HopWorldsMenu != null) {
                    Execution.delay(random.nextLong(750, 1250));
                    component(1, -1, 93913153);
                    log("[Runecrafting] Hop Worlds Button Clicked.");
                    boolean worldSelectOpen = Execution.delayUntil(5000, () -> Interfaces.isOpen(1587));
                    Execution.delay(random.nextLong(750, 1250));

                    if (worldSelectOpen) {
                        log("[Runecrafting] World Select Interface Open.");
                        Execution.delay(random.nextLong(750, 1250));
                        component(2, world, 104005640);
                        log("[Runecrafting] Selected World: " + world);
                        if (Client.getGameState() == Client.GameState.LOGGED_IN && player != null) {
                            Execution.delay(random.nextLong(7548, 9879));
                            currentWorld = world;
                            log("[Runecrafting] Resuming script.");
                        } else {
                            log("[Runecrafting] Failed to resume script. GameState is not LOGGED_IN or player is null.");
                        }
                    } else {
                        log("[Runecrafting] Failed to open World Select Interface.");
                    }
                } else {
                    log("[Runecrafting] Failed to find Hop Worlds Menu.");
                }
            } else {
                log("[Runecrafting] Failed to open hopper. Retrying...");
                HopWorlds(world);
            }
        } else {
            log("[Runecrafting] Interface 1431 is not open.");
        }
    }


    static int[] membersWorlds = new int[]{
           1, 2, 4, 5, 6, 9, 10, 12, 14, 15,
           16, 21, 22, 23, 24, 25, 26, 27, 28, 31,
           32, 35, 36, 37, 39, 40, 42, 44, 45, 46,
           47, 49, 50, 51, 53, 54, 56, 58, 59, 60,
           62, 63, 64, 65, 66, 67, 68, 69, 70, 71,
           72, 73, 74, 76, 77, 78, 79, 82, 83,
           85, 87, 88, 89, 91, 92, 97, 98, 99, 100,
           102, 103, 104, 105, 106, 116, 117, 118, 119, 121,
           123, 124, 134, 138, 139, 140, 252, 257, 258};

}
