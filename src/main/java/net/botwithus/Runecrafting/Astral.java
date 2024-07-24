package net.botwithus.Runecrafting;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Runecrafting.Astral.AstralState.*;
import static net.botwithus.Runecrafting.Runecrafting.familiarPattern;
import static net.botwithus.Runecrafting.Runecrafting.superRestorePattern;
import static net.botwithus.TaskScheduler.bankPin;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.player;
import static net.botwithus.rs3.game.minimenu.actions.SelectableAction.SELECT_TILE;

public class Astral {

    public static boolean useAstralAltar = false;

    public static AstralState nextState = TELEPORTTOBANK;

    public static Astral.AstralState getCurrentState() {
        return nextState;
    }

    public enum AstralState {
        IDLE,
        TELEPORTTOBANK, // Add this line
        BANKING,
        TELEPORT,
        TRAVELTOALTAR,
        INTERACTWITHALTER,
    }

    public static void runAstral() {

        switch (nextState) {
            case IDLE:
                Execution.delay(RandomGenerator.nextInt(2500, 5000));
                break;
            case TELEPORTTOBANK:
                useGote();
                break;
            case BANKING:
                interactwithBoat();
                break;
            case TELEPORT:
                teleportToAltar();
                break;
            case TRAVELTOALTAR:
                moveToAstralAltar();
                break;
            case INTERACTWITHALTER:
                interactWithAltar();
                break;
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
            nextState = BANKING;
        }
    }

    private static void interactwithBoat() {
        EntityResultSet<SceneObject> boatResults = SceneObjectQuery.newQuery().name("Rowboat").option("Bank").results();
        SceneObject boat = boatResults.nearest();

        if (boat != null && player.getAnimationId() == -1) {
            if (boat.interact("Load Last Preset from")) {
                log("[Runecrafting] Attempting to load last preset.");
                Execution.delayUntil(5000, () -> Backpack.contains("Pure essence") && Rune.EARTH.getQuantity() >= 2 && Rune.LAW.getQuantity() >= 1 && Rune.ASTRAL.getQuantity() >= 2);

                if (player.getCurrentHealth() < 5000 && Equipment.contains("Augmented enhanced Excalibur") && ComponentQuery.newQuery(291).spriteId(14632).results().isEmpty()) {
                    Equipment.interact(Equipment.Slot.SHIELD, "Activate");
                    Execution.delay(random.nextLong(2000, 3000));
                }

                if (Backpack.contains("Pure essence") && Rune.EARTH.getQuantity() >= 2 && Rune.LAW.getQuantity() >= 1 && Rune.ASTRAL.getQuantity() >= 2) {
                    if (ManageFamiliar) {
                        checkFamiliar();
                    } else {
                        nextState = TELEPORT;
                        log("[Runecrafting] Changed bot state to TELEPORTING.");
                    }
                } else {
                    log("[Error] Required runes or essence not found in backpack after loading preset.");
                    shutdown();
                }
            }
        }
    }

    public static Area lunarIsle = new Area.Rectangular(new Coordinate(2113, 3914, 0), 6, 6);

    private static void teleportToAltar() {
        if (ActionBar.containsAbility("Moonclan Teleport")) {
            if (ActionBar.useAbility("Moonclan Teleport")) {
                log("[Debug] Moonclan Teleport ability used.");
                Execution.delayUntil(random.nextLong(5000, 7500), () -> lunarIsle.contains(player.getCoordinate()));
                Execution.delayUntil(random.nextLong(7500, 10000), () -> player.getAnimationId() == -1);
                if (player.getAnimationId() == -1) {
                    nextState = TRAVELTOALTAR;
                    log("[Debug] nextState set to TRAVELTOALTAR.");
                }
            } else {
                log("[Debug] Failed to use Moonclan Teleport ability.");
            }
        } else {
            log("[Debug] Moonclan Teleport ability not found.");
        }
    }

    public static final Coordinate FIRST_SURGE = new Coordinate(2124, 3876, 0);
    public static final Area FIRST_COORDINATE = new Area.Rectangular(FIRST_SURGE, 3, 3);
    public static final Area ALTAR_COORDINATE = new Area.Rectangular(new Coordinate(2155, 3864, 0), 3, 3);
    public static final Area BD_FAILSAFE = new Area.Rectangular(new Coordinate(2134, 3866, 0), 3, 3);

    private static void moveToAstralAltar() {
        Coordinate randomCoord = FIRST_COORDINATE.getRandomWalkableCoordinate();
        Coordinate altarCoord = ALTAR_COORDINATE.getRandomWalkableCoordinate();
        if (!player.isMoving()) {
            log("[Debug] Player is not moving. Walking to random coordinate.");
            Movement.walkTo(randomCoord.getX(), randomCoord.getY(), true);
            Execution.delay(random.nextLong(2000, 3000));
        }
        Area.Rectangular firstArea = new Area.Rectangular(new Coordinate(2115, 3893, 0), new Coordinate(2110, 3898, 0));
        if (firstArea.contains(player.getCoordinate())) {
            if (Surge()) {
                log("[Debug] Surge successful. Walking to first surge coordinate.");
                Execution.delay(random.nextLong(400, 600));
                Movement.walkTo(FIRST_SURGE.getX(), FIRST_SURGE.getY(), true);
            } else {
                log("[Debug] Surge not successful. Walking to first surge coordinate.");
                Movement.walkTo(FIRST_SURGE.getX(), FIRST_SURGE.getY(), true);
            }
        }
        EntityResultSet<SceneObject> altar = SceneObjectQuery.newQuery().id(17010).option("Craft-rune").results();
        SceneObject nearestAltar = altar.nearest();
        Area.Rectangular secondArea = new Area.Rectangular(new Coordinate(2125, 3882, 0), new Coordinate(2119, 3873, 0));
        if (secondArea.contains(player.getCoordinate())) {
            if (castBladedDive()) {
                log("[Debug] CastBladedDive successful. Walking to altar coordinate.");
                Execution.delay(random.nextLong(400, 600));
                Movement.walkTo(altarCoord.getX(), altarCoord.getY(), true);
                if (nearestAltar != null) {
                    log("[Debug] Nearest altar is not null. Delaying until altar is empty.");
                    Execution.delayUntil(random.nextLong(7500, 10000), () -> !altar.isEmpty());
                }
                log("[Debug] Setting nextState to INTERACTWITHALTER.");
                nextState = INTERACTWITHALTER;
            } else {
                log("[Debug] CastBladedDive not successful. Walking to altar coordinate.");
                Coordinate bdFailsafeCoord = BD_FAILSAFE.getRandomWalkableCoordinate();
                Movement.walkTo(bdFailsafeCoord.getX(), bdFailsafeCoord.getY(), true);
                if (nearestAltar != null) {
                    log("[Debug] Nearest altar is not null. Delaying until altar is empty.");
                    Execution.delayUntil(random.nextLong(7500, 10000), () -> !altar.isEmpty());
                }
                log("[Debug] Setting nextState to INTERACTWITHALTER.");
                nextState = INTERACTWITHALTER;
            }
        }
    }

    private static void interactWithAltar() {
        Execution.delay(random.nextLong(200, 600));
        EntityResultSet<SceneObject> altar = SceneObjectQuery.newQuery().id(17010).option("Craft-rune").results();
        if (altar != null && !altar.isEmpty()) {
            SceneObject nearestAltar = altar.nearest();
            if (nearestAltar != null && nearestAltar.interact("Craft-rune")) {
                log("[Debug] Attempting to interact with altar.");
                if (canUsePotion() && Powerburst) {
                    Execution.delayUntil(random.nextLong(10000, 15000), () -> player.distanceTo(altar.nearest()) < random.nextInt(7, 10));
                    Powerburst();
                    nearestAltar.interact("Craft-rune");
                    Execution.delayUntil(random.nextLong(10000, 15000), () -> Backpack.contains("Astral rune"));
                } else {
                    Execution.delayUntil(random.nextLong(20000, 30000), () -> Backpack.contains("Astral rune"));
                }
                if (Backpack.contains("Astral rune")) {
                    log("[Debug] Backpack contains Astral rune.");
                    updateRunesQuantity();
                    loopCounter++;
                    nextState = TELEPORTTOBANK;
                    log("[Debug] nextState set to TELEPORTTOBANK.");
                }
            } else {
                log("[Debug] Nearest altar is null or interaction was unsuccessful.");
            }
        } else {
            log("[Debug] Altar is null or empty.");
        }
    }




    private static void Powerburst() {
        if (!canUsePotion()) {
            return;
        }

        ResultSet<Item> potionItems = InventoryItemQuery.newQuery(93).option("Drink").results();
        Item potion = potionItems.isEmpty() ? null : potionItems.first();

        if (potion != null) {
            boolean drinkSuccess = Backpack.interact(potion.getName(), "Drink");

            if (drinkSuccess) {
                log("[Runecrafting] Successfully drank " + potion.getName());
                Execution.delay(random.nextLong(200, 400));
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


    public static void checkFamiliar() {
        if (VarManager.getVarbitValue(6055) <= 1) {
            summonFamiliar();
        } else {
            nextState = TELEPORT;
        }
    }

    private static void summonFamiliar() {
        SceneObject bank = findBank();
        if (bank != null) {
            interactWithBank(bank);
        }
    }

    private static SceneObject findBank() {
        EntityResultSet<SceneObject> bankResults = SceneObjectQuery.newQuery().id(110591).option("Bank").results();
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
        Execution.delay(random.nextLong(1500, 2000));
        if (bank.interact("Bank")) {
            log("[Familiar] Interacting with " + bank.getName() + " using 'Bank'.");
            if (waitForBankInterface()) {
                performBankingActions();
            }
        }
    }

    private static boolean waitForBankInterface() {
        Execution.delayUntil(15000, () -> Interfaces.isOpen(517) || Interfaces.isOpen(759));
        if (Interfaces.isOpen(759)) {
            bankPin();
        }
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
        Execution.delay(random.nextLong(600, 800));
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
        Execution.delay(random.nextLong(600, 800));
        if (Bank.isOpen()) {
            Bank.close();
            Execution.delayUntil(random.nextLong(15000, 20000), () -> !Bank.isOpen());
            log("[Runecrafting] Attempted to close bank.");
        }
        log("[Runecrafting] Bank is closed.");
        if (!Bank.isOpen()) {
            if (!ManageFamiliar) {
                nextState = BANKING;
                return;
            }
            performPostBankingActions();
        }
    }

    private static void performPostBankingActions() {
        Item superRestoreItem = Backpack.getItem(superRestorePattern);
        if (superRestoreItem != null && Backpack.interact(superRestoreItem.getName(), "Drink")) {
            log("[Runecrafting] Drinking: " + superRestoreItem.getName());
            Execution.delay(random.nextLong(600, 800));
        }

        Item familiarItem = Backpack.getItem(familiarPattern);
        if (familiarItem != null && Backpack.interact(familiarItem.getName(), "Summon")) {
            log("[Runecrafting] Summoning: " + familiarItem.getName());
            Execution.delay(random.nextLong(600, 800));
        }
        nextState = BANKING;
    }

    public static Area BDCoords = new Area.Rectangular(new Coordinate(2134, 3867, 0), 3, 3);

    public static boolean castBladedDive() {
        if (Math.random() > 0.90) { // 8% chance not to cast
            return false;
        }

        String abilityName = ActionBar.containsAbility("Bladed Dive") ? "Bladed Dive" : "Dive";

        if (ActionBar.containsAbility(abilityName)) {
            if (ActionBar.getCooldownPrecise(abilityName) == 0) {
                if (ActionBar.useAbility(abilityName)) {
                    Execution.delay(RandomGenerator.nextInt(200, 400));

                    Coordinate selectedCoordinate = BDCoords.getRandomWalkableCoordinate();
                    MiniMenu.interact(SELECT_TILE.getType(), 0, selectedCoordinate.getX(), selectedCoordinate.getY());
                    log("[Success] Interaction with tile successful.");
                    return true;
                } else {
                    log("[Error] Failed to activate " + abilityName + " ability.");
                }
            } else {
                log("[Caution] " + abilityName + " ability is on cooldown.");
            }
        } else {
            log("[Error] " + abilityName + " ability not found in ActionBar.");
        }
        return false;
    }

    private static boolean Surge() {
        if (ActionBar.getCooldown("Surge") <= 0 && ActionBar.containsAbility("Surge")) {
            if (Math.random() <= 0.90) { // 92% chance to cast
                int delayBeforeCasting = RandomGenerator.nextInt(100, 1000);
                Execution.delay(delayBeforeCasting);
                log("[Success] Surge is not on cooldown. Casting Surge: " + ActionBar.useAbility("Surge"));
                log("[Debug] Casting Surge at coordinates (" + player.getCoordinate().getX() + ", " + player.getCoordinate().getY() + ").");
                return true;
            } else {
                return false;
            }
        } else {
            log("[Caution] Surge is on cooldown, cannot cast || Ability not found in ActionBar.");
            return false;
        }
    }


    public static Map<String, Integer> Astralrunes = new HashMap<>();


    private static void updateRunesQuantity() {
        String[] runeTypes = {"Astral"};

        for (String runeType : runeTypes) {
            String runeName = runeType + " rune";
            if (Backpack.contains(runeName)) {
                int newQuantity = Backpack.getQuantity(runeName);
                int existingQuantity = Astralrunes.getOrDefault(runeName, 0);
                int totalQuantity = existingQuantity + newQuantity;
                Astralrunes.put(runeName, totalQuantity);
            }
        }
    }

    public enum Rune {
        EARTH(5889),
        LAW(5900),
        ASTRAL(5903);

        private final int index;

        Rune(int index) {
            this.index = index;
        }

        public int getQuantity() {
            return VarManager.getVarc(index);
        }
    }
}
