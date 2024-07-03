package net.botwithus.Runecrafting;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.steamRunes;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.minimenu.actions.SelectableAction.SELECT_TILE;

public class SteamRunes {
    public static boolean useSteamRunes = false;
    private static Random random = new Random();
    public static Player player = Client.getLocalPlayer();
    private static final Pattern superRestorePattern = Pattern.compile("Super restore.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern familiarPattern = Pattern.compile("Abyssal parasite|Abyssal lurker|Abyssal titan", Pattern.CASE_INSENSITIVE);

    public static RunecraftingState currentState = RunecraftingState.BANKING;

    public static RunecraftingState getCurrentState() {
        return currentState;
    }

    public enum RunecraftingState {
        BANKING,
        INTERACTWITHRING,
        INTERACTWITHALTER,
    }

    public static void run() {
        LocalPlayer player = Client.getLocalPlayer();

        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            return;
        }

        switch (currentState) {
            case BANKING:
                useGote();
                break;
            case INTERACTWITHRING:
                useRingofDueling();
                break;
            case INTERACTWITHALTER:
                useAlter();
                break;
        }
    }

    private static void useGote() {
        EntityResultSet<SceneObject> bankChests = SceneObjectQuery.newQuery().name("Rowboat").option("Bank").results();

        Coordinate fishingHub = new Coordinate(2135, 7107, 0);

        if (bankChests.isEmpty()) {
            while (player.getAnimationId() == -1 && (player.getCoordinate() != null && !player.getCoordinate().equals(fishingHub))) {
                if (Equipment.contains("Grace of the elves") && Equipment.interact(Equipment.Slot.NECK, "Deep sea fishing hub")) {
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
    /*public enum Rune {
        AIR(5886),
        WATER(5887),
        EARTH(5889),
        FIRE(5888),
        MIND(5902),
        BODY(5896),
        COSMIC(5897),
        CHAOS(5898),
        NATURE(5899),
        LAW(5900),
        ASTRAL(5903),
        DEATH(5901),
        BLOOD(5904),
        SOUL(5905);

        private final int index;

        Rune(int index) {
            this.index = index;
        }

        public int getQuantity() {
            return VarManager.getVarc(index);
        }
    }*/
    public enum Rune {
        WATER(5887),
        FIRE(5888),
        ASTRAL(5903);

        private final int index;

        Rune(int index) {
            this.index = index;
        }

        public int getQuantity() {
            return VarManager.getVarc(index);
        }
    }

    private static void interactwithBoat() {
        EntityResultSet<SceneObject> boatResults = SceneObjectQuery.newQuery().name("Rowboat").option("Bank").results();
        SceneObject boat = boatResults.nearest();

        if (boat != null) {
            if (boat.interact("Load Last Preset from")) {
                Execution.delayUntil(5000, () -> backpack.contains("Pure essence"));

                int waterRuneQuantity = Rune.WATER.getQuantity();
                int fireRuneQuantity = Rune.FIRE.getQuantity();
                int astralRuneQuantity = Rune.ASTRAL.getQuantity();

                boolean sufficientWaterRunes = waterRuneQuantity > 150;
                boolean sufficientFireRunes = fireRuneQuantity > 150;
                boolean sufficientAstralRunes = astralRuneQuantity > 150;

                if (backpack.contains("Pure essence") && sufficientWaterRunes && sufficientFireRunes && sufficientAstralRunes) {
                    if (ManageFamiliar) {
                        checkFamiliar();
                    } else {
                        currentState = RunecraftingState.INTERACTWITHRING;
                        log("[Runecrafting] Changed bot state to TELEPORTING.");
                    }
                } else {
                    String insufficientRunes = "";
                    if (!sufficientWaterRunes) insufficientRunes += "Water, ";
                    if (!sufficientFireRunes) insufficientRunes += "Fire, ";
                    if (!sufficientAstralRunes) insufficientRunes += "Astral, ";
                    if (!insufficientRunes.isEmpty()) {
                        insufficientRunes = insufficientRunes.substring(0, insufficientRunes.length() - 2); // Remove trailing comma and space
                        log("[Error] Insufficient rune quantities for: " + insufficientRunes + ".");
                    } else {
                        log("[Error] Essence not found in backpack after loading preset.");
                    }
                    shutdown();
                }
            }
        }
    }

    private static Optional<Item> findRingOfDuelling() {
        Pattern pattern = Pattern.compile("Ring of duelling \\((1|2|3|4|5|6|7|8)\\)");
        return backpack.getItems().stream()
                .filter(item -> pattern.matcher(item.getName()).find())
                .findFirst();
    }

    private static void useRingofDueling() {
        Optional<Item> ringOfDuelling = findRingOfDuelling();
        if (ringOfDuelling.isPresent()) {
            boolean interfaceOpened = false;
            int retryCount = 0;
            int maxRetries = 10; // Maximum number of retries
            while (!interfaceOpened && retryCount < maxRetries) {
                log("[useRingofDueling] Found Ring of Dueling, using it.");
                backpack.interact(ringOfDuelling.get().getName(), "Rub");
                interfaceOpened = Execution.delayUntil(random.nextLong(1500, 2500), () -> Interfaces.isOpen(720));
                if (!interfaceOpened) {
                    log("[useRingofDueling] Interface 720 did not open, retrying...");
                    retryCount++;
                }
            }
            if (interfaceOpened) {
                MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185921);
                Execution.delay(random.nextLong(4000, 5000));
                EntityResultSet<SceneObject> fireruins = SceneObjectQuery.newQuery().name("Fire ruins").option("Enter").results();
                SceneObject fireruin = fireruins.nearest();
                while (!player.isMoving() && player.getAnimationId() == -1 && fireruin != null) {
                    Execution.delay(random.nextLong(600, 700));
                    castBladedDive();
                    Execution.delay(random.nextLong(200, 500));
                    if (fireruin.interact("Enter") || !player.isMoving()) {
                        break;
                    }
                }
                final long startTime = System.currentTimeMillis();
                final long timeout = 20000;
                boolean altarFound = false;
                while (System.currentTimeMillis() - startTime < timeout && !altarFound) {
                    EntityResultSet<SceneObject> altarResults = SceneObjectQuery.newQuery().name("Fire altar").option("Craft-rune").results();
                    if (altarResults.nearest() != null) {
                        altarFound = true;
                        currentState = RunecraftingState.INTERACTWITHALTER;
                        break;
                    }
                    Execution.delay(random.nextLong(620, 980));
                }
                if (!altarFound) {
                    log("[useRingofDueling] Failed to find Fire altar within timeout.");
                }
            } else {
                log("[useRingofDueling] Failed to open interface 720 after retries.");
            }
        }
    }

    private static void useAlter() {
        EntityResultSet<SceneObject> alterResults = SceneObjectQuery.newQuery().name("Fire altar").option("Craft-rune").results();
        SceneObject alter = alterResults.nearest();

        if (alter != null) {
            if (backpack.contains("Binding necklace") && backpack.interact("Binding necklace", "Wear")) {
                log("[useAlter] Binding necklace equipped.");
            }
            if (backpack.contains("Water rune")) {
                Item waterRune = backpack.getItem("Water rune");
                if (waterRune != null) {
                    int waterRuneSlot = waterRune.getSlot();
                    MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), -1, waterRuneSlot, 96534533);
                    Execution.delay(random.nextLong(500, 600));
                    MiniMenu.interact(SelectableAction.SELECT_OBJECT.getType(), 2482, 2584, 4837);
                }
            }
            log("[useAlter] Interacted with Fire altar.");
            Execution.delayUntil(random.nextLong(1000, 2000), () -> player.isMoving());
            while (player.isMoving()) {
                Execution.delay(random.nextLong(600, 1000));
                if (ActionBar.containsAbility("Magic Imbue") && ActionBar.useAbility("Magic Imbue")) {
                    log("[useAlter] Using Magic Imbue, waiting for action to complete.");
                    break;
                }
            }
            Execution.delayUntil(random.nextLong(5000, 10000), () -> !backpack.contains("Pure essence"));
            if (backpack.contains("Grace of the elves")) {
                while (!Equipment.contains("Grace of the elves")) {
                    backpack.interact("Grace of the elves", "Wear");
                    Execution.delayUntil(random.nextLong(600, 1000), () -> Equipment.contains("Grace of the elves"));
                }
                log("[useAlter] Grace of the elves equipped.");
                updateSteamRunesQuantity();
                loopCounter++;
                currentState = RunecraftingState.BANKING;
            }
        }
    }
    public static void updateSteamRunesQuantity() {
        int newQuantity = Backpack.getQuantity("Steam rune");
        int existingQuantity = steamRunes.getOrDefault("Steam rune", 0);
        int totalQuantity = existingQuantity + newQuantity;
        steamRunes.put("Steam rune", totalQuantity);
    }

    public static void checkFamiliar() {
        if (VarManager.getVarbitValue(6055) <= 1) {
            summonFamiliar();
        } else {
            currentState = RunecraftingState.INTERACTWITHRING;
        }
    }

    private static void summonFamiliar() {
        SceneObject bank = findBank();
        if (bank != null) {
            interactWithBank(bank);
        }
    }

    private static SceneObject findBank() {
        EntityResultSet<SceneObject> bankResults = SceneObjectQuery.newQuery().name("Rowboat").option("Bank").results();
        if (bankResults.isEmpty()) {
            return null;
        }
        SceneObject bank = bankResults.nearest();
        if (bank != null) {
            log("[Familiar] Found " + bank.getName());
        }
        return bank;
    }

    private static void interactWithBank(SceneObject bank) {
        if (bank.interact("Bank")) {
            log("[Familiar] Interacting with " + bank.getName() + " using 'Bank'.");
            if (waitForBankInterface()) {
                performBankingActions();
            }
        }
    }

    private static boolean waitForBankInterface() {
        Execution.delayUntil(5000, () -> Interfaces.isOpen(517));
        if (Interfaces.isOpen(517)) {
            log("[Familiar] Bank interface is open.");
            Execution.delay(random.nextLong(600, 800));
            return true;
        }
        return false;
    }

    private static void performBankingActions() {
        Bank.depositAll();
        log("[Familiar] Deposited all items.");
        Execution.delay(random.nextLong(600, 800));
        interactWithComponents();
        withdrawItems();
        closeBank();
    }

    private static void interactWithComponents() {
        if (VarManager.getVarbitValue(45141) != 1) {
            component(1, -1, 33882270);
            log("[Familiar] Interacting with component: " + VarManager.getVarbitValue(45141));
            Execution.delay(random.nextLong(600, 800));
        }
        if (VarManager.getVarbitValue(45189) != 2) {
            component(1, -1, 33882205);
            log("[Familiar] Interacting with component: " + VarManager.getVarbitValue(45189));
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
                currentState = RunecraftingState.BANKING;
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
        currentState = RunecraftingState.BANKING;
    }



    public static void castBladedDive() {
        Coordinate[] coordinates = {
                new Coordinate(3321, 3246, 0),
                new Coordinate(3322, 3247, 0),
                new Coordinate(3322, 3246, 0),
                new Coordinate(3323, 3245, 0),
                new Coordinate(3321, 3244, 0),
                new Coordinate(3321, 3245, 0)
        };

        if (ActionBar.containsAbility("Bladed Dive")) {
            log("[Debug] Bladed Dive ability found.");

            if (ActionBar.getCooldownPrecise("Bladed Dive") == 0) {
                log("[Debug] Bladed Dive ability is not on cooldown.");

                if (ActionBar.useAbility("Bladed Dive")) {
                    log("Bladed Dive ability activated at specific coordinates.");
                    Execution.delay(RandomGenerator.nextInt(200, 400));

                    Coordinate selectedCoordinate = coordinates[random.nextInt(coordinates.length)];
                    log("[Debug] Attempting to interact with tile at coordinates (" + selectedCoordinate.getX() + ", " + selectedCoordinate.getY() + ").");
                    MiniMenu.interact(SELECT_TILE.getType(), 0, selectedCoordinate.getX(), selectedCoordinate.getY());
                    log("Bladed Dive ability activated. Interaction with tile successful.");
                } else {
                    log("[Error] Failed to activate Bladed Dive ability.");
                }
            } else {
                log("[Error] Bladed Dive ability is on cooldown.");
            }
        } else {
            log("[Error] Bladed Dive ability not found in ActionBar.");
        }
    }
}

