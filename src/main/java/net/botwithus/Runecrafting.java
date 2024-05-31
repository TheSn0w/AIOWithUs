package net.botwithus;

import net.botwithus.Variables.Variables;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static net.botwithus.Runecrafting.ScriptState.*;
import static net.botwithus.Variables.Variables.*;

public class Runecrafting {
    public Runecrafting(SnowsScript script) {
        this.skeletonScript = script; // Initialize with the correct instance
    }

    public SnowsScript skeletonScript; // Store a reference to SkeletonScript
    private static long lastMovedOrAnimatedTime = System.currentTimeMillis();
    public static ScriptState currentState = IDLE;
    private static Random random = new Random();
    private static final Map<String, Integer> runeQuantities = new ConcurrentHashMap<>();
    static Player player = Client.getLocalPlayer();

    public static Map<String, Integer> getRuneQuantities() {
        return runeQuantities;
    }

    private static final Pattern PowerburstPattern = Pattern.compile("Powerburst of sorcery \\([1-4]\\)", Pattern.CASE_INSENSITIVE);

    static int[] membersWorlds = new int[]{
            1, 2, 4, 5, 6, 9, 10, 12, 14, 15,
            16, 21, 22, 23, 24, 25, 26, 27, 28, 31,
            32, 35, 36, 37, 39, 40, 42, 44, 45, 46,
            47, 49, 50, 51, 53, 54, 56, 58, 59, 60,
            62, 63, 64, 65, 66, 67, 68, 69, 70, 71,
            72, 73, 74, 75, 76, 77, 78, 79, 82, 83,
            85, 87, 88, 89, 91, 92, 97, 98, 99, 100,
            102, 103, 104, 105, 106, 116, 117, 118, 119, 121,
            123, 124, 134, 138, 139, 140, 252, 257, 258};


    public static Runecrafting.ScriptState getScriptstate() {
        return currentState;
    }

    public void updateChatMessageEvent(ChatMessageEvent event) {
        String message = event.getMessage();
        if (isRunecraftingActive) {
            if (message.contains("The charger cannot hold any more essence.")) {
                Execution.delay(handleCharging());
            }
            if (message.contains("You do no have any essence to deposit")) {
                Execution.delay(handleEdgevillebanking());
            }
            if (message.contains("The altar is already charged to its maximum capacity")) {
                handleSoulAltar();
            }
        }
    }

    public enum ScriptState {
        IDLE,
        BANKING,
        TELEPORTING,
        INTERACTINGWITHPORTAL,
        CRAFTING,
        TELEPORTINGTOBANK, ScriptState,
    }
    public static void setCurrentState(ScriptState newState) {
        currentState = newState;
    }

    public static void handleRunecrafting(LocalPlayer player) {

        boolean playerIsIdle = !player.isMoving() && player.getAnimationId() == -1;
        if (playerIsIdle && System.currentTimeMillis() - lastMovedOrAnimatedTime > 20000) {
            ScriptConsole.println("Player has been idle for more than 20 seconds, teleporting to bank.");
            currentState = TELEPORTINGTOBANK;
            lastMovedOrAnimatedTime = System.currentTimeMillis();
            return;
        }

        /*if (useWorldhop) {
            if (nextWorldHopTime == 0) {
                int waitTimeInMs = RandomGenerator.nextInt(minHopIntervalMinutes * 60 * 1000, maxHopIntervalMinutes * 60 * 1000);
                nextWorldHopTime = System.currentTimeMillis() + waitTimeInMs;
                ScriptConsole.println("Next hop scheduled in " + (waitTimeInMs / 60000) + " minutes.");

                return;
            }

            if (System.currentTimeMillis() >= nextWorldHopTime) {
                int randomMembersWorldsIndex = RandomGenerator.nextInt(membersWorlds.length);
                HopWorlds(membersWorlds[randomMembersWorldsIndex]);
                ScriptConsole.println("Hopped to world: " + membersWorlds[randomMembersWorldsIndex]);

                nextWorldHopTime = 0;

                return;
            }
        }*/
        switch (currentState) {
            case IDLE -> {
                if (SceneObjectQuery.newQuery().name("Bank chest").results().nearest() != null || SceneObjectQuery.newQuery().name("Rowboat").results().nearest() != null) {
                    currentState = BANKING;
                    ScriptConsole.println("Proceeding to bank.");
                } else {
                    currentState = TELEPORTINGTOBANK;
                    ScriptConsole.println("Proceeding to teleport to bank.");
                }
                Execution.delay(RandomGenerator.nextInt(2500, 5000));
            }

            case BANKING -> {
                if (useGraceoftheElves) {
                    useGote();
                }
                if (!RingofDueling && !useGraceoftheElves) {
                    checkAndPerformActions();
                }
                if (RingofDueling) {
                    castleWars();
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
                    castleWars();
                }
                if (useGraceoftheElves) {
                    updateRuneQuantities();
                    useGote();
                }
                ++loopCounter;
                currentState = BANKING;
            }
        }
    }

    public static long nextWorldHopTime = 0;
    public static int minHopIntervalMinutes = 60; // Default minimum wait time in minutes
    public static int maxHopIntervalMinutes = 180; // Default maximum wait time in minutes


    public static void HopWorlds(int world) {
        MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 7, 93782016);
        boolean hopperOpen = Execution.delayUntil(5000, () -> Interfaces.isOpen(1433));
        Execution.delay(RandomGenerator.nextInt(1000, 2000));

        if (hopperOpen) {
            Component HopWorldsMenu = ComponentQuery.newQuery(1433).componentIndex(65).results().first();
            if (HopWorldsMenu != null) {
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 93913153);
                ScriptConsole.println("Hop Worlds Button Clicked.");
                boolean worldSelectOpen = Execution.delayUntil(5000, () -> Interfaces.isOpen(1587));
                Execution.delay(RandomGenerator.nextInt(1000, 2000));

                if (worldSelectOpen) {
                    MiniMenu.interact(ComponentAction.COMPONENT.getType(), 2, world, 104005640);
                    Execution.delay(RandomGenerator.nextInt(10000, 20000));
                }
            } else {
                ScriptConsole.println("Hop Worlds Button not found.");
            }
        }
    }
    private static void useGote() {
        EntityResultSet<SceneObject> bankChests = SceneObjectQuery.newQuery().name("Rowboat").option("Bank").results();

        Coordinate fishingHub = new Coordinate(2135, 7107, 0);

        if (bankChests.isEmpty()) {
            if (Movement.traverse(NavPath.resolve(fishingHub)) == TraverseEvent.State.FINISHED) {
                ScriptConsole.println("Traversed to Fishing hub.");
            }
        } else {
            interactwithBoat();
        }
    }

    private static void interactwithBoat() {
        EntityResultSet<SceneObject> bankChests = SceneObjectQuery.newQuery().name("Rowboat").option("Bank").results();
        if (!bankChests.isEmpty()) {
            if (bankChests.nearest().interact("Load Last Preset from")) {
                Execution.delayUntil(5000, Backpack::isFull);

                if (Backpack.isFull()) {
                    if (ManageFamiliar) {
                        checkAndPerformActions();
                    } else {
                        currentState = TELEPORTING;
                        ScriptConsole.println("Changed bot state to TELEPORTING.");
                    }
                } else {
                    if (!Backpack.isFull()) {
                        ScriptConsole.println("Essence not found in backpack after loading preset.");
                        performLogout();
                    }
                }
            }
        }
    }

    private static void castleWars() {
        EntityResultSet<SceneObject> bankChests = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
        Coordinate WarsBank = new Coordinate(2446, 3085, 0);

        if (bankChests.isEmpty()) {
            if (Movement.traverse(NavPath.resolve(WarsBank)) == TraverseEvent.State.FINISHED) {
                ScriptConsole.println("Traversed to Castle Wars bank.");
            }
        } else {
            interactwithBankChest();
        }
    }

    private static void interactwithBankChest() {
        EntityResultSet<SceneObject> bankChests = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
        if (!bankChests.isEmpty()) {
            if (bankChests.nearest().interact("Load Last Preset from")) {
                Execution.delayUntil(5000, Backpack::isFull);

                if (Backpack.isFull()) {
                    if (ManageFamiliar) {
                        checkAndPerformActions();
                    } else {
                        currentState = TELEPORTING;
                        ScriptConsole.println("Changed bot state to TELEPORTING.");
                    }
                } else {
                    if (!Backpack.isFull()) {
                        ScriptConsole.println("Essence not found in backpack after loading preset.");
                        performLogout();
                    }
                }
            }
        }
    }

    public static void checkAndPerformActions() {
        if (shouldInteractWithAltar()) {
            performAltarInteractions();
        } else {
            currentState = TELEPORTING;
        }
    }

    private static boolean shouldInteractWithAltar() {
        return !isFamiliarSummoned() || VarManager.getVarbitValue(6055) <= 5;
    }

    private static void performAltarInteractions() {
        ScriptConsole.println("Conditions met for interacting with Altar of War.");
        Execution.delay(interactWithAltarOfWar((LocalPlayer) player));
        summonFamiliar();
    }

    private void changeBotStateToTeleporting() {
        currentState = TELEPORTING;
        ScriptConsole.println("Proceeding to botState TELEPORTING.");
    }


    private void interactWithBankChest() {
        if (canInteractWithBank()) {
            interactAndCheckBackpack();
        }
    }

    private boolean canInteractWithBank() {
        return player.getAnimationId() == -1 && !player.isMoving();
    }


    private void interactAndCheckBackpack() {
        EntityResultSet<SceneObject> bankChests = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
        if (!bankChests.isEmpty()) {
            if (bankChests.nearest().interact("Load Last Preset from")) {
                Execution.delay(random.nextLong(750, 1500));

                if (Backpack.isFull()) {
                    currentState = TELEPORTING;
                    ScriptConsole.println("Changed bot state to TELEPORTING.");
                } else {
                    if (!Backpack.isFull()) {
                        ScriptConsole.println("Essence not found in backpack after loading preset.");
                        checkBackpackAndLogoutIfNeeded();
                    }
                }
            }
        }
    }


    private void checkBackpackAndLogoutIfNeeded() {
        if (!Backpack.isFull()) {
            performLogout();
            ScriptConsole.println("Backpack is not full, logging out.");
        }
    }

    public static void performLogout() {
        if (initiateLogoutSequence()) {
            waitForLogout();
        }
    }

    private static boolean initiateLogoutSequence() {
        MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 7, 93782016);
        return Interfaces.isOpen(1433);
    }

    private static void waitForLogout() {
        Component logoutButton = findLogoutButton();
        if (logoutButton != null && logoutButton.interact(1)) {
            ScriptConsole.println("Logout initiated.");
        } else {
            logLogoutFailure();
        }
    }

    private static Component findLogoutButton() {
        return ComponentQuery.newQuery(1433).componentIndex(71).results().first();
    }

    private static void logLogoutFailure() {
        if (Interfaces.isOpen(1433)) {
            ScriptConsole.println("Could not find or interact with the logout button.");
        } else {
            ScriptConsole.println("Failed to open logout menu.");
        }
    }

    private static void interactWithRing() {
        if (notWearingRing && Backpack.isFull()) {
            ActionBar.useItem("Passing bracelet", "Rub");
            Execution.delay(RandomGenerator.nextInt(600, 800));
            Execution.delayUntil(5000, () -> Interfaces.isOpen(720));
            ScriptConsole.println("Using Ring of Passing");
            if (Interfaces.isOpen(720)) {
                MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185940);
                ScriptConsole.println("Interacting with Haunt on the Hill");
                Execution.delay(RandomGenerator.nextInt(3500, 5000));
                Execution.delayUntil(RandomGenerator.nextInt(5000, 10000), () -> player.getAnimationId() == -1);
                lastMovedOrAnimatedTime = System.currentTimeMillis();
                currentState = INTERACTINGWITHPORTAL;
            }
        } else if (WearingRing && Backpack.isFull()) {
            ResultSet<Item> results = InventoryItemQuery.newQuery().ids(56416).option("City of Um: Haunt on the Hill").results();
            if (!results.isEmpty()) {
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 3, 9, 95944719);
                Execution.delay(RandomGenerator.nextInt(3500, 5000));
                Execution.delayUntil(RandomGenerator.nextInt(5000, 10000), () -> player.getAnimationId() == -1);
                lastMovedOrAnimatedTime = System.currentTimeMillis();
                currentState = INTERACTINGWITHPORTAL;
            } else {
                ScriptConsole.println("Ring of the City of Um: Haunt on the Hill not found in inventory.");
            }
        } else {
            ScriptConsole.println("Ring Option not chosen.");
        }
    }

    private static void interactWithDarkPortal() {
        Execution.delay(RandomGenerator.nextInt(600, 800));
        SceneObject Portal = SceneObjectQuery.newQuery().name("Dark portal").results().nearest();

        // Check if the portal is not null before interacting with it
        if (Portal != null) {
            while (!player.isMoving()) {
                Portal.interact("Enter");
                ScriptConsole.println("Attempting to interact with the Dark Portal.");

                // Delay for a random time between 600ms and 800ms
                Execution.delay(random.nextLong(600, 800));
            }

            if (Portal.distanceTo(player.getCoordinate()) >= 10) {
                boolean surgeUsed = Surge();
                if (surgeUsed) {
                    Portal.interact("Enter");
                    ScriptConsole.println("Attempting to interact with the Dark Portal after Surging.");
                }
            }

            lastMovedOrAnimatedTime = System.currentTimeMillis();
            currentState = CRAFTING;
        } else {
            ScriptConsole.println("Dark portal not found.");
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
        EntityResultSet<SceneObject> altar = SceneObjectQuery.newQuery().id(127383).option("Craft runes").results();
        if (!altar.isEmpty() && !player.isMoving() && player.getAnimationId() == -1) {
            ScriptConsole.println("Player animation is -1 and not moving");

            if (Powerburst) {
                if (Math.random() <= 0.85) {
                    Powerburst();
                    Execution.delay(RandomGenerator.nextInt(450, 550));
                } else {
                    ScriptConsole.println("Forgot to use Powerburst.");
                }
            }

            altar.nearest().interact("Craft runes");
            Execution.delayUntil(5000, () -> player.isMoving());
            ScriptConsole.println("Attempted to interact with Miasma altar.");

            if (player.isMoving()) {
                boolean surgeUsed = Surge();
                if (surgeUsed) {
                    altar.nearest().interact("Craft runes");
                    ScriptConsole.println("Attempting to interact with Spirit altar after Surging.");
                }
            }
        }
    }
    private void checkForRuneAndAnimationMiasma(long startTime, long timeout) {
        Execution.delay(RandomGenerator.nextInt(600, 1200));
        if (Backpack.contains("Miasma rune") && player.getAnimationId() == -1) {
            ScriptConsole.println("Miasma rune found in backpack, proceeding to next state.");
            updateRuneQuantities();
            Execution.delay(RandomGenerator.nextInt(650, 800));
            lastMovedOrAnimatedTime = System.currentTimeMillis();
            currentState = TELEPORTINGTOBANK;
        } else if (System.currentTimeMillis() - startTime < timeout) {
            checkForRuneAndAnimationMiasma(startTime, timeout);
        } else {
            ScriptConsole.println("Condition not met within the timeout period.");
            updateRuneQuantities();
            Execution.delay(RandomGenerator.nextInt(650, 800));
            lastMovedOrAnimatedTime = System.currentTimeMillis();
            currentState = TELEPORTINGTOBANK;
        }
    }


    private static void handleBoneAltar() {
        EntityResultSet<SceneObject> altar = SceneObjectQuery.newQuery().id(127381).option("Craft runes").results();
        if (!altar.isEmpty() && !player.isMoving() && player.getAnimationId() == -1) {
            ScriptConsole.println("Player animation is -1 and not moving");

            if (Powerburst) {
                if (Math.random() <= 0.85) {
                    Powerburst();
                    Execution.delay(RandomGenerator.nextInt(450, 550));
                } else {
                    ScriptConsole.println("Forgot to use Powerburst.");
                }
            }

            altar.nearest().interact("Craft runes");
            Execution.delayUntil(5000, () -> player.isMoving());
            ScriptConsole.println("Attempted to interact with Bone altar.");
            if (player.isMoving()) {
                boolean surgeUsed = Surge();
                if (surgeUsed) {
                    altar.nearest().interact("Craft runes");
                    ScriptConsole.println("Attempting to interact with Spirit altar after Surging.");
                }
            }
        }
    }

    private static void handleSpiritAltar() {
        EntityResultSet<SceneObject> altar = SceneObjectQuery.newQuery().id(127380).option("Craft runes").results();
        if (!altar.isEmpty() && !player.isMoving() && player.getAnimationId() == -1) {
            ScriptConsole.println("Player animation is -1 and not moving");

            if (Powerburst) {
                if (Math.random() <= 0.85) {
                    Powerburst();
                    Execution.delay(RandomGenerator.nextInt(450, 550));
                } else {
                    ScriptConsole.println("Forgot to use Powerburst.");
                }
            }

            altar.nearest().interact("Craft runes");
            Execution.delayUntil(5000, () -> player.isMoving());
            ScriptConsole.println("Attempted to interact with Spirit altar.");

            if (player.isMoving()) {
                boolean surgeUsed = Surge();
                if (surgeUsed) {
                    altar.nearest().interact("Craft runes");
                    ScriptConsole.println("Attempting to interact with Spirit altar after Surging.");
                }
            }
        }
    }


    private static void handleFleshAltar() {
        EntityResultSet<SceneObject> altar = SceneObjectQuery.newQuery().id(127382).option("Craft runes").results();
        if (!altar.isEmpty() && !player.isMoving() && player.getAnimationId() == -1) {
            ScriptConsole.println("Player animation is -1 and not moving");

            if (Powerburst) {
                if (Math.random() <= 0.85) {
                    Powerburst();
                    Execution.delay(RandomGenerator.nextInt(450, 550));
                } else {
                    ScriptConsole.println("Forgot to use Powerburst.");
                }
            }

            altar.nearest().interact("Craft runes");
            Execution.delayUntil(5000, () -> player.isMoving());
            ScriptConsole.println("Attempted to interact with Flesh altar.");

            if (player.isMoving()) {
                boolean surgeUsed = Surge();
                if (surgeUsed) {
                    altar.nearest().interact("Craft runes");
                    ScriptConsole.println("Attempting to interact with Spirit altar after Surging.");
                }
            }
        }
    }


    private void useWarsRetreat() {
        ScriptConsole.println("Using Wars Retreat: " + ActionBar.useAbility("War's Retreat Teleport"));
        Execution.delay(RandomGenerator.nextInt(6000, 6500));
        currentState = BANKING;
    }


    private static boolean isFamiliarSummoned() {
        Component familiarComponent = ComponentQuery.newQuery(284).spriteId(26095).results().first();
        return familiarComponent != null;
    }

    private static void summonFamiliar() {
        if (VarManager.getVarbitValue(6055) > 5) {
            ScriptConsole.println("Familiar is already summoned.");
        } else {
            ActionBar.useItem("Abyssal titan pouch", "Summon");
            ScriptConsole.println("Summoned Abyssal Titan.");
            Execution.delayUntil(10000, Runecrafting::isFamiliarSummoned);
        }

        lastMovedOrAnimatedTime = System.currentTimeMillis();
    }

    /*public void interactWithAltarOfWar() {
        SceneObject altarOfWar = SceneObjectQuery.newQuery().name("Altar of War").results().nearest();
        if (altarOfWar != null && altarOfWar.interact("Pray")) {
            ScriptConsole.println("Attempting to pray at the Altar of War...");
            Execution.delay(RandomGenerator.nextInt(4000, 5000));
        }

    }*/
    static long interactWithAltarOfWar(LocalPlayer player) {
        ResultSet<Item> items = InventoryItemQuery.newQuery(93).results();

        Item prayerOrRestorePot = items.stream()
                .filter(item -> item.getName() != null &&
                        (item.getName().toLowerCase().contains("prayer") ||
                                item.getName().toLowerCase().contains("restore")))
                .findFirst()
                .orElse(null);

        if (prayerOrRestorePot == null) {
            ScriptConsole.println("[Prayer Potions]  No prayer or restore potions found in the backpack.");
            return 1L;
        }

        ScriptConsole.println("Drinking " + prayerOrRestorePot.getName());
        boolean success = backpack.interact(prayerOrRestorePot.getName(), "Drink");
        if (success) {
            ScriptConsole.println("[Prayer Potions]  Successfully drank " + prayerOrRestorePot.getName());
            long delay = random.nextLong(1500, 3000);
            Execution.delay(delay);
            return delay;
        } else {
            ScriptConsole.println("[Prayer Potions]  Failed to interact with " + prayerOrRestorePot.getName());
            return 0;
        }
    }

    private static void Powerburst() {
        if (!canUsePotion()) {
            ScriptConsole.println("Powerburst of sorcery is on cooldown.");
            return;
        }

        String[] potionVariants = new String[]{"Powerburst of sorcery (4)", "Powerburst of sorcery (3)",
                "Powerburst of sorcery (2)", "Powerburst of sorcery (1)"};

        for (String potionName : potionVariants) {
            if (ActionBar.containsItem(potionName)) {
                boolean successfulDrink = ActionBar.useItem(potionName, "Drink");
                if (successfulDrink) {
                    ScriptConsole.println("Drank " + potionName + "!");
                    return;
                }
                break;
            }
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
                ScriptConsole.println("Delaying for " + delayBeforeCasting + "ms before casting Surge.");
                Execution.delay(delayBeforeCasting);
            }
            ScriptConsole.println("Surge is not on cooldown. Casting Surge: " + ActionBar.useAbility("Surge"));
            return true;
        } else {
            ScriptConsole.println("Surge is on cooldown, cannot cast.");
        }
        return false;
    }

    public long handleDepositing() {
        EntityResultSet<SceneObject> Charger = SceneObjectQuery.newQuery().name("Charger").results();
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1500, 3000);
        }
        if  (!Charger.isEmpty()) {
            if (Charger.nearest().interact("Deposit")) {
                ScriptConsole.println("Interacted with Charger to deposit.");
                return random.nextLong(1500, 3000);
            }
        }
        return 0;
    }

    public static void handleSoulAltar() {
        EntityResultSet<SceneObject> Soulaltar = SceneObjectQuery.newQuery().name("Soul altar").option("Craft-rune").results();
        if (Soulaltar.isEmpty()) {
            Soulaltar.nearest().interact("Craft-rune");
            Execution.delayUntil(5000, () -> Backpack.contains("Soul rune"));
            if (Backpack.contains("Soul rune")) {
                handleEdgevillebanking();
            }
        }
    }

    public static long handleCharging() {
        EntityResultSet<SceneObject> Charger = SceneObjectQuery.newQuery().name("Charger").results();
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1500, 3000);
        }
        if (!Charger.isEmpty()) {
            if (Charger.nearest().interact("Charge altar")) {
                ScriptConsole.println("Interacted with Charger to charge.");
                return random.nextLong(1500, 3000);
            }
        }
        return 0;
    }

    public static long handleEdgevillebanking() {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1500, 3000);
        }
        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            ScriptConsole.println("Selecting 'Weave'");
            return random.nextLong(1500, 3000);
        }
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Soul altar").option("Craft-rune").results();
        if (!results.isEmpty()) {
            results.nearest().interact("Craft-rune");
            return random.nextLong(1500, 3000);
        }
        return random.nextLong(1500, 3000);
    }


    private void interactwithBanker() {
        EntityResultSet<SceneObject> Bank = SceneObjectQuery.newQuery().id(42377).option("Bank").results();
        if (!Bank.isEmpty()) {
            Bank.nearest().interact("Load Last Preset from");
            if (Backpack.isFull()) {
                Execution.delay(Traversing());
            }
        }
    }


    private long Traversing() {
        if (player.isMoving()) {
            return random.nextLong(1500, 7000);
        }
        EntityResultSet<Npc> Mage = NpcQuery.newQuery().name("Mage of Zamorak").option("Teleport").results();
        EntityResultSet<SceneObject> Soulrift = SceneObjectQuery.newQuery().name("Soul rift").option("Exit-through").results();
        EntityResultSet<SceneObject> Charger = SceneObjectQuery.newQuery().id(109428).option("Deposit").results();
        if (Backpack.isFull()) {
            if (Movement.traverse(NavPath.resolve(new Coordinate(3102, 3556, 0))) == TraverseEvent.State.FINISHED) {
                if (!Mage.isEmpty()) {
                    Mage.nearest().interact("Teleport");
                    Execution.delayUntil(10000, () -> Soulrift.nearest() != null);
                    if (player.getAnimationId() == -1 && !player.isMoving() && Soulrift.nearest().interact("Exit-through")) {
                        Execution.delayUntil(10000, () -> Charger.nearest() != null);
                        Execution.delay(handleDepositing());
                    }
                }
            }
        }
        return random.nextLong(1500, 7000);
    }
}
