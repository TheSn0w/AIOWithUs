package net.botwithus.Runecrafting;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.characters.PlayerQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import static net.botwithus.CustomLogger.log;
import static net.botwithus.Runecrafting.Abyss.AbyssState.*;
import static net.botwithus.Runecrafting.Runecrafting.*;
import static net.botwithus.TaskScheduler.bankPin;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.player;
import static net.botwithus.rs3.game.minimenu.actions.SelectableAction.SELECT_TILE;

public class Abyss {

    public static boolean useAbyssRunecrafting = false;

    public static AbyssState thisState = TELEPORTTOBANK;

    public static AbyssState getCurrentState() {
        return thisState;
    }

    public enum AbyssState {
        IDLE,
        TELEPORTTOBANK,
        BANKING,
        INTERACTWITHWALL,
        INTERACTWITHMAGE,
        INTERACTWITHPORTAL,
        INTERACTWITHALTER,
    }

    public static void runAbyss() {
        checkForOtherPlayersAndHopWorldAbyss();

        switch (thisState) {
            case IDLE:
                Execution.delay(RandomGenerator.nextInt(2500, 5000));
                break;
            case TELEPORTTOBANK:
                useWildernessSword();
                break;
            case BANKING:
                interactWithCounter();
                break;
            case INTERACTWITHWALL:
                interactWithWall();
                break;
            case INTERACTWITHMAGE:
                interactWithMage();
                break;
            case INTERACTWITHPORTAL:
                interactWithPortal();
                break;
            case INTERACTWITHALTER:
                interactWithAltar();
                break;
        }
    }


    private static void useWildernessSword() {
        ResultSet<Item> sword = InventoryItemQuery.newQuery(94).name("Wilderness sword 4").results();
        EntityResultSet<SceneObject> bankResults = SceneObjectQuery.newQuery().id(42377).option("Bank").results();

        if (!sword.isEmpty() && bankResults.isEmpty()) {
            boolean success = false;
            while (player.getAnimationId() == -1) {
                success = Equipment.interact(Equipment.Slot.WEAPON, "Edgeville");
                log("[Wilderness Sword] Interacting with Wilderness Sword.");
                Execution.delay(random.nextLong(750, 1500)); // Wait for 1000ms before trying again
                if (player.getAnimationId() != -1) {
                    log("[Wilderness Sword] Player is teleporting.");
                }
            }
            if (success) {
                Execution.delay(random.nextLong(1500, 2000));
                Execution.delayUntil(15000, () -> !player.isMoving() && player.getAnimationId() == -1);
                log("[Wilderness Sword] Teleporting to Edgeville.");
                Execution.delay(random.nextLong(750, 1000));
                thisState = AbyssState.BANKING;
            }
        } else {
            Execution.delay(random.nextLong(750, 1000));
            thisState = AbyssState.BANKING;
        }
    }

    private static void interactWithCounter() {
        EntityResultSet<SceneObject> bankResults = SceneObjectQuery.newQuery().id(42377).option("Bank").results();
        if (!bankResults.isEmpty()) {
            SceneObject bank = bankResults.nearest();
            if (bank != null) {
                if (!player.isMoving() && ManageFamiliar) {
                    checkFamiliar();
                }
                if (!player.isMoving()) {
                    bank.interact("Load Last Preset from");
                    log("[Bank] Interacting with bank.");
                    Execution.delay(random.nextLong(600, 800));
                    if (player.isMoving() || Distance.between(player.getCoordinate(), new Coordinate(3097, 3496, 0)) > 15.0D) {
                        Execution.delayUntil(20000, () -> Backpack.contains("Pure essence") && player.getCoordinate().equals(new Coordinate(3097, 3496, 0)) || Interfaces.isOpen(759));
                        if (Interfaces.isOpen(759)) {
                            bankPin();
                        }
                        log("[Bank] Loaded last preset.");
                        if (Backpack.contains("Pure essence") && player.getCoordinate().equals(new Coordinate(3097, 3496, 0))) {
                            thisState = AbyssState.INTERACTWITHWALL;
                        } else {
                            log("[Backpack] Backpack does not contain Pure essence, logging off.");
                            shutdown();
                        }
                    }
                }
            }
        }
    }

    private static void interactWithWall() {
        EntityResultSet<SceneObject> wildernessWall = SceneObjectQuery.newQuery().id(65084).option("Cross").results();
        if (player != null && !wildernessWall.isEmpty() && Backpack.contains("Pure essence")) {
            SceneObject wall = wildernessWall.nearest();
            if (wall != null && wall.interact("Cross")) {
                log("[Mage] Interacting with wall.");
                if (ActionBar.containsAbility("Surge")) {
                    boolean surgedAtCorrectLocation = Execution.delayUntil(15000, () -> {
                        Coordinate playerCoord = player.getCoordinate();
                        return playerCoord.getX() == 3101 && playerCoord.getY() == 3507 && Surge();
                    });
                    if (surgedAtCorrectLocation) {
                        Execution.delay(random.nextLong(600, 800));
                        wall.interact("Cross");
                        log("[Mage] Interacted with Wall.");
                        Execution.delayUntil(random.nextLong(15000, 20000), () -> !player.isMoving() && player.getAnimationId() == -1);
                        thisState = INTERACTWITHMAGE;
                    }
                } else {
                    Execution.delayUntil(random.nextLong(15000, 20000), () -> !player.isMoving() && player.getAnimationId() == -1);
                    thisState = INTERACTWITHMAGE;
                }
            }
        }
    }
    private static void interactWithMage() {
        EntityResultSet<SceneObject> natureRift = SceneObjectQuery.newQuery().name("Nature rift").option("Exit-through").results();
        EntityResultSet<Npc> zamorakMage = NpcQuery.newQuery().name("Mage of Zamorak").option("Teleport").results();

        if (!player.isMoving() && player.getAnimationId() == -1 && player.getCoordinate().equals(new Coordinate(3102, 3523, 0))) {
            randomWalk();
            Execution.delayUntil(random.nextLong(15000, 20000), () -> player.isMoving());
            if (player.isMoving()) {
                castBladedDive();
                Execution.delay(random.nextLong(100, 300));
            }

            randomWalk();


            Coordinate playerCoord = player.getCoordinate();
            while (!(playerCoord.getX() >= 3097 && playerCoord.getX() <= 3104 &&
                    playerCoord.getY() >= 3537 && playerCoord.getY() <= 3540)) {
                Execution.delay(random.nextLong(500, 1000));
                playerCoord = player.getCoordinate();
            }

            zamorakMage = NpcQuery.newQuery().name("Mage of Zamorak").option("Teleport").results();

            log("[Mage] Interacting with Mage of Zamorak.");
            zamorakMage.nearest().interact("Teleport");
            Execution.delay(random.nextLong(100, 1000));

            if (ActionBar.containsAbility("Surge")) {
                Surge();
                log("[Mage] Interacting with Mage of Zamorak.");
                Execution.delay(random.nextLong(100, 500));
                zamorakMage.nearest().interact("Teleport");
                Execution.delayUntil(random.nextLong(15000, 20000), () -> !player.isMoving());
                thisState = INTERACTWITHPORTAL;
            } else {
                Execution.delayUntil(random.nextLong(15000, 20000), () -> !player.isMoving());
                thisState = INTERACTWITHPORTAL;
            }
        }
    }

    public static void randomWalk() {
        Random random = new Random();
        int x = 3100 + random.nextInt(5) - 2; // Random integer between 3098 and 3102
        int y = 3550 + random.nextInt(5) - 2; // Random integer between 3548 and 3552
        Movement.walkTo(x, y, true);
        log("[Walk] Walking to random coordinates");
    }

    private static void interactWithPortal() {
        String riftName = getRiftName();
        EntityResultSet<SceneObject> riftResults = SceneObjectQuery.newQuery().name(riftName).option("Exit-through").results();
        SceneObject rift = riftResults.nearest();

        if (rift == null) {
            Execution.delay(random.nextLong(500, 1000));
            interactWithPortal();
        } else {
            log("[Portal] Found nearest rift. Interacting...");
            interactWithRift(rift, riftName);
        }
    }

    private static void interactWithRift(SceneObject rift, String riftName) {
        rift.interact("Exit-through");
        Execution.delay(random.nextLong(1000, 1250));
        EntityResultSet<Npc> results = NpcQuery.newQuery().name("Dark mage").option("Talk-to").results();
        if (results.isEmpty()) {
            log("[Portal] Interaction with " + riftName + " initiated.");
            thisState = INTERACTWITHALTER;
        } else {
            Execution.delay(random.nextLong(1000, 1250));
            interactWithRift(rift, riftName);
        }
    }

    private static void interactWithAltar() {
        String altarName = getAltarName();
        EntityResultSet<SceneObject> altarResults = SceneObjectQuery.newQuery().name(altarName).option("Craft-rune").results();

        while (!altarResults.isEmpty()) {
            SceneObject altar = altarResults.nearest();
            if (altar != null) {
                if (Backpack.contains("Pure essence")) {
                    if (Powerburst) {
                        Powerburst();
                    }
                    altar.interact("Craft-rune");
                    Execution.delay(random.nextLong(1000, 1500));
                }
                if (!Backpack.contains("Pure essence")) {
                    log("[Success] Successfully interacted with " + altarName + ".");
                    loopCounter++;
                    updateRunesQuantity();
                    updateMagicalThreadQuantity();
                    thisState = TELEPORTTOBANK;
                    break;
                }
            }
        }
    }
    public static boolean craftNatureRunes = false;
    public static boolean craftBloodRunes = false;
    public static boolean craftCosmicRunes = false;
    public static boolean craftFireRunes = false;
    public static boolean craftEarthRunes = false;
    public static boolean craftMindRunes = false;
    public static boolean craftAirRunes = false;
    public static boolean craftWaterRunes = false;
    public static boolean craftDeathRunes = false;
    public static boolean craftLawRunes = false;
    public static boolean craftChaosRunes = false;


    private static String getRiftName() {
        if (craftNatureRunes) return "Nature rift";
        if (craftBloodRunes) return "Blood rift";
        if (craftCosmicRunes) return "Cosmic rift";
        if (craftFireRunes) return "Fire rift";
        if (craftEarthRunes) return "Earth rift";
        if (craftMindRunes) return "Mind rift";
        if (craftAirRunes) return "Air rift";
        if (craftWaterRunes) return "Water rift";
        if (craftDeathRunes) return "Death rift";
        if (craftLawRunes) return "Law rift";
        if (craftChaosRunes) return "Chaos rift";
        return "";
    }

    private static String getAltarName() {
        if (craftNatureRunes) return "Nature altar";
        if (craftBloodRunes) return "Blood altar";
        if (craftCosmicRunes) return "Cosmic altar";
        if (craftFireRunes) return "Fire altar";
        if (craftEarthRunes) return "Earth altar";
        if (craftMindRunes) return "Mind altar";
        if (craftAirRunes) return "Air altar";
        if (craftWaterRunes) return "Water altar";
        if (craftDeathRunes) return "Death altar";
        if (craftLawRunes) return "Law altar";
        if (craftChaosRunes) return "Chaos altar";
        return "";
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
            thisState = AbyssState.INTERACTWITHWALL;
        }
    }

    private static void summonFamiliar() {
        SceneObject bank = findBank();
        if (bank != null) {
            interactWithBank(bank);
        }
    }

    private static SceneObject findBank() {
        EntityResultSet<SceneObject> bankResults = SceneObjectQuery.newQuery().id(42377).option("Bank").results();
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
        Execution.delayUntil(15000, () -> Interfaces.isOpen(517));
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
        Bank.close();
        Execution.delayUntil(random.nextLong(15000, 20000), () -> !Interfaces.isOpen(517));
        log("[Runecrafting] Closed bank.");
        if (!Interfaces.isOpen(517)) {
            if (!ManageFamiliar) {
                thisState = AbyssState.TELEPORTTOBANK;
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
        thisState = AbyssState.TELEPORTTOBANK;
    }



    public static void castBladedDive() {
        Coordinate[] coordinates = {
                new Coordinate(3101, 3534, 0),
                new Coordinate(3100, 3534, 0),
                new Coordinate(3100, 3536, 0),
                new Coordinate(3099, 3536, 0),
                new Coordinate(3099, 3535, 0),
                new Coordinate(3099, 3533, 0)
        };

        if (ActionBar.containsAbility("Bladed Dive")) {

            if (ActionBar.getCooldownPrecise("Bladed Dive") == 0) {

                if (ActionBar.useAbility("Bladed Dive")) {
                    Execution.delay(RandomGenerator.nextInt(200, 400));

                    Coordinate selectedCoordinate = coordinates[random.nextInt(coordinates.length)];
                    /*log("[Debug] Attempting to interact with tile at coordinates (" + selectedCoordinate.getX() + ", " + selectedCoordinate.getY() + ").");*/
                    MiniMenu.interact(SELECT_TILE.getType(), 0, selectedCoordinate.getX(), selectedCoordinate.getY());
                    log("[Success] Interaction with tile successful.");
                } else {
                    log("[Error] Failed to activate Bladed Dive ability.");
                }
            } else {
                log("[Caution] Bladed Dive ability is on cooldown.");
            }
        } else {
            log("[Error] Bladed Dive ability not found in ActionBar.");
        }
    }

    private static boolean Surge() {
        if (ActionBar.getCooldown("Surge") <= 0 && ActionBar.containsAbility("Surge")) {
            if (Math.random() <= 0.96) {
                int delayBeforeCasting = RandomGenerator.nextInt(100, 1000);
                Execution.delay(delayBeforeCasting);
            }
            log("[Success] Surge is not on cooldown. Casting Surge: " + ActionBar.useAbility("Surge"));
            log("[Debug] Casting Surge at coordinates (" + player.getCoordinate().getX() + ", " + player.getCoordinate().getY() + ").");
            return true;
        } else {
            log("[Caution] Surge is on cooldown, cannot cast || Ability not found in ActionBar.");
        }
        return false;
    }

    public static void checkForOtherPlayersAndHopWorldAbyss() {
        if (hopDuetoPlayers) {
            if (thisState.equals(Abyss.AbyssState.BANKING) || thisState.equals(Abyss.AbyssState.INTERACTWITHMAGE) || thisState.equals(Abyss.AbyssState.INTERACTWITHPORTAL) || thisState.equals(Abyss.AbyssState.INTERACTWITHWALL) || thisState.equals(Abyss.AbyssState.INTERACTWITHALTER)) {
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
                            return playerLocation != null && localPlayerLocation.distanceTo(playerLocation) <= 10.0D;
                        })
                        .peek(player -> {
                            log("Found player within distance: " + player.getName());
                            playerInfo.add(new PlayerInfo(player.getName(), System.currentTimeMillis(), LoginManager.getWorld()));
                        })
                        .findAny()
                        .isPresent();

                if (otherPlayersPresent) {
                    log("Other players found within distance. Initiating world hop.");
                    int currentWorld = LoginManager.getWorld();
                    int randomMembersWorldsIndex;
                    do {
                        randomMembersWorldsIndex = RandomGenerator.nextInt(membersWorlds.length);
                    } while (membersWorlds[randomMembersWorldsIndex] == currentWorld);
                    AbyssState previousState = thisState;
                    HopWorlds(membersWorlds[randomMembersWorldsIndex]);
                    log("Hopped to world: " + membersWorlds[randomMembersWorldsIndex]);
                    thisState = previousState;
                }
            }
        }
    }

    public static Map<String, Integer> runes = new HashMap<>();
    public static Map<String, Integer> magicalThreads = new HashMap<>();


    private static void updateRunesQuantity() {
        String[] runeTypes = {"Blood", "Mind", "Water", "Earth", "Fire", "Air", "Nature", "Cosmic", "Law", "Chaos", "Death"};

        for (String runeType : runeTypes) {
            String runeName = runeType + " rune";
            if (Backpack.contains(runeName)) {
                int newQuantity = Backpack.getQuantity(runeName);
                int existingQuantity = runes.getOrDefault(runeName, 0);
                int totalQuantity = existingQuantity + newQuantity;
                runes.put(runeName, totalQuantity);
            }
        }
    }

    public static void updateMagicalThreadQuantity() {
        int newQuantity = Backpack.getCount("Magical thread");
        if (newQuantity <= 0) {
            newQuantity = 0;
        }
        int existingQuantity = magicalThreads.getOrDefault("Magical thread", 0);
        int totalQuantity = existingQuantity + newQuantity;
        magicalThreads.put("Magical thread", totalQuantity);
    }
}
