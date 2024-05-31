package net.botwithus;

import net.botwithus.Variables.Variables;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.api.game.hud.inventories.LootInventory;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.GroundItemQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.item.GroundItem;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.findNearestBank;
import static net.botwithus.SnowsScript.setLastSkillingLocation;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Combat {
    public Combat(SnowsScript script) {
        skeletonScript = script;
    }
    public static SnowsScript skeletonScript;
    private static Random random = new Random();

    public static long attackTarget(LocalPlayer player) {
        if (player == null) {
            return logAndDelay("[Error] Local player not found.", 1500, 3000);
        }
        if (Variables.useLoot) {
            processLooting();
        }
        if (SoulSplit) {
            Execution.delay(manageSoulSplit(player));
        }
        if (usequickPrayers)
            manageQuickPrayers(player);

        if (shouldBank(player) && !usePOD) {
            return bankAndDelay(player);
        }
        if (scriptureofJas || scriptureofWen || animateDead || useScrimshaws) {
            if (scriptureofJas) {
                manageScriptureOfJas();
            }
            if (scriptureofWen) {
                manageScriptureOfWen();
            }
            if (animateDead) {
                manageAnimateDead(player);
            }
            if (useScrimshaws) {
                manageScrimshaws(player);
            }
        }

        if (isHealthLow(player)) {
            eatFood(player);
            return logAndDelay("[Error] Health is low, wont attack until more health.", 1000, 3000);
        }

        if (player.hasTarget()) {
            if (DeathGrasp) {
                essenceOfFinality();
            }
            if (InvokeDeath) {
                Deathmark();
            }
            if (VolleyofSouls) {
                volleyOfSouls();
            }
            if (SpecialAttack) {
                DeathEssence();
            }
            if (KeepArmyup) {
                KeepArmyup();
            }
            return random.nextLong(1000, 1500);
        }


        Npc monster = findTarget(player);
        if (monster == null) {
            return logAndDelay("[Error] No valid NPC target found.", 1000, 3000);
        }

        return attackMonster(player, monster);
    }

    private static boolean shouldBank(LocalPlayer player) {
        long overloadCheck = drinkOverloads(player);
        long prayerCheck = usePrayerOrRestorePots(player);
        long aggroCheck = useAggression(player);
        long weaponPoisonCheck = useWeaponPoison(player);
        return useWeaponPoison && weaponPoisonCheck == 1L || useOverloads && overloadCheck == 1L || usePrayerPots && prayerCheck == 1L || useAggroPots && aggroCheck == 1L;
    }

    private static long bankAndDelay(LocalPlayer player) {
        if (VarManager.getVarbitValue(16779) == 1) {
            ActionBar.useAbility("Soul Split");
        }
        setLastSkillingLocation(player.getCoordinate());
        ActionBar.useAbility("War's Retreat Teleport");
        SnowsScript.setBotState(SnowsScript.BotState.BANKING);
        return logAndDelay("[Combat] Banking.", 1500, 3000);
    }


    private static Npc findTarget(LocalPlayer player) {
        List<String> targetNames = getTargetNames();
        if (targetNames.isEmpty()) {
            return null;
        }

        Pattern monsterPattern = generateRegexPattern(targetNames);

        return NpcQuery.newQuery()
                .name(monsterPattern)
                .isReachable()
                .health(100, 1_000_000)
                .option("Attack")
                .results()
                .nearestTo(player.getCoordinate());
    }

    private static long attackMonster(LocalPlayer player, Npc monster) {
        boolean attack = monster.interact("Attack");
        if (attack) {
            return logAndDelay("[Combat] Successfully attacked " + monster.getName() + ".", 750, 1250);
        } else {
            return logAndDelay("[Error] Failed to attack " + monster.getName(), 1500, 3000);
        }
    }

    private static long logAndDelay(String message, int minDelay, int maxDelay) {
        log(message);
        long delay = random.nextLong(minDelay, maxDelay);
        Execution.delay(delay);
        return delay;
    }

    /*public static long BankforFood(LocalPlayer player) {
        ScriptConsole.println("[BankforFood] Method started.");

        Coordinate nearestBank = findNearestBank(player.getCoordinate());

        if (nearestBank != null) {
            if (!nearestBank.getArea().contains(player.getCoordinate())) {
                banking.navigateToNearestBank(player, nearestBank);
            } else {
                ScriptConsole.println("[BankforFood] Already in the bank area.");
            }

            banking.interactWithBank(nearestBank);
            Execution.delayUntil(15000, Bank::isOpen);
            ScriptConsole.println("[BankforFood] Interacting with bank.");

            if (Bank.isOpen()) {
                ScriptConsole.println("[BankforFood] Bank is open.");
                withdrawFood();
                returnToSkillingLocation(player);
            } else {
                ScriptConsole.println("[BankforFood] Bank is not open.");
            }
        } else {
            ScriptConsole.println("[BankforFood] No nearest bank found.");
        }

        ScriptConsole.println("[BankforFood] Method ended.");
        return 0;
    }*/


    private static void withdrawFood() {
        log("[Combat] Interface is open.");
        Execution.delay(RandomGenerator.nextInt(600, 1000)); // Short delay

        if (selectedFoodNames.isEmpty()) {
            log("[Error] No food names specified.");
            return;
        }

        for (String foodName : selectedFoodNames) {
            Bank.withdrawAll(foodName);
        }
    }

    private static void returnToSkillingLocation(LocalPlayer player) {
        log("[Combat] Returning to last skilling location: " + skeletonScript.getLastSkillingLocation());
        Movement.traverse(NavPath.resolve(skeletonScript.getLastSkillingLocation().getRandomWalkableCoordinate())); // Navigate back
        SnowsScript.setBotState(SnowsScript.BotState.SKILLING);
        Execution.delay(random.nextLong(1500, 3000));
    }

    public static List<String> targetItemNames = new ArrayList<>();
    public static String selectedItem = "";

    public static List<String> getTargetItemNames() {
        return targetItemNames;
    }

    public static String getSelectedItem() {
        return selectedItem;
    }

    public static void setSelectedItem(String selectedItem) {
        Combat.selectedItem = selectedItem;
    }

    public static void LootEverything() {
        if (Interfaces.isOpen(1622)) {
            LootAll();
        } else {
            lootInterface();
            Execution.delayUntil(10000, () -> Interfaces.isOpen(1622));
        }
    }

    public static void LootAll() {
        EntityResultSet<GroundItem> groundItems = GroundItemQuery.newQuery().results();
        if (groundItems.isEmpty()) {
            return;
        }

        Execution.delay(RandomGenerator.nextInt(1500, 2000));
        ComponentQuery lootAllQuery = ComponentQuery.newQuery(1622);
        List<Component> components = lootAllQuery.componentIndex(22).results().stream().toList();

        if (!components.isEmpty() && components.get(0).interact(1)) {
            log("[Combat] Successfully interacted with Loot All.");
            Execution.delay(RandomGenerator.nextInt(806, 1259));
        }
    }

    public static void lootInterface() {
        EntityResultSet<GroundItem> groundItems = GroundItemQuery.newQuery().results();
        if (groundItems.isEmpty()) {
            return;
        }

        if (!groundItems.isEmpty() && !Backpack.isFull()) {
            GroundItem groundItem = groundItems.nearest();
            if (groundItem != null) {
                groundItem.interact("Take");
                Execution.delayUntil(RandomGenerator.nextInt(5000, 5500), () -> getLocalPlayer().isMoving());

                if (getLocalPlayer().isMoving() && groundItem.getCoordinate() != null && Distance.between(getLocalPlayer().getCoordinate(), groundItem.getCoordinate()) > 10) {
                    log("[Combat] Used Surge: " + ActionBar.useAbility("Surge"));
                    Execution.delay(RandomGenerator.nextInt(200, 250));
                }

                if (groundItem.getCoordinate() != null) {
                    Execution.delayUntil(RandomGenerator.nextInt(100, 200), () -> Distance.between(getLocalPlayer().getCoordinate(), groundItem.getCoordinate()) <= 10);
                }

                if (groundItem.interact("Take")) {
                    log("[Combat] Taking " + groundItem.getName() + "...");
                    Execution.delay(RandomGenerator.nextInt(600, 700));
                }

                boolean interfaceOpened = Execution.delayUntil(15000, () -> Interfaces.isOpen(1622));
                if (!interfaceOpened) {
                    log("[Error] Interface 1622 did not open. Attempting to interact with ground item again.");
                    if (groundItem.interact("Take")) {
                        log("[Combat] Attempting to take " + groundItem.getName() + " again...");
                        Execution.delay(RandomGenerator.nextInt(250, 300));
                    }
                }
                LootAll();
            }
        }
    }
    private static void manageScriptureOfJas() {
        if (getLocalPlayer() != null) {
            if (getLocalPlayer().inCombat()) {
                Execution.delay(activateScriptureOfJas());
            } else {
                Execution.delay(deactivateScriptureOfJas());
            }
        }
    }

    private static long activateScriptureOfJas() {
        if (VarManager.getVarbitValue(30605) == 0 && VarManager.getVarbitValue(30604) >= 60) {
            log("[Combat] Activated Scripture of Jas:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            return random.nextLong(1500, 3000);
        }
        return 0L;
    }

    private static long deactivateScriptureOfJas() {
        if (VarManager.getVarbitValue(30605) == 1) {
            log("[Combat] Deactivated Scripture of Jas:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            return random.nextLong(1500, 3000);
        }
        return 0L;
    }

    private static void manageScriptureOfWen() {
        if (getLocalPlayer() != null) {
            if (getLocalPlayer().inCombat()) {
                Execution.delay(activateScriptureOfWen());
            } else {
                Execution.delay(deactivateScriptureOfWen());
            }
        }
    }

    private static long activateScriptureOfWen() {
        if (VarManager.getVarbitValue(30605) == 0 && VarManager.getVarbitValue(30604) >= 60) {
            log("[Combat] Activated Scripture of Wen:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            return random.nextLong(1500, 3000);
        }
        return 0L;
    }

    private static long deactivateScriptureOfWen() {
        if (VarManager.getVarbitValue(30605) == 1) {
            log("[Combat] Deactivated Scripture of Wen:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            return random.nextLong(1500, 3000);
        }
        return 0L;
    }

    public static void processLooting() {
        if (Backpack.isFull()) {
            log("[Combat] Backpack is full. Cannot loot more items.");
            return;
        }

        if (Interfaces.isOpen(1622)) {
            lootFromInventory();
        } else {
            lootFromGround();
        }
    }

    private static Pattern generateLootPattern(List<String> names) {
        return Pattern.compile(
                names.stream()
                        .map(Pattern::quote)
                        .reduce((name1, name2) -> name1 + "|" + name2)
                        .orElse(""),
                Pattern.CASE_INSENSITIVE
        );
    }

    private static boolean canLoot() {
        return !targetItemNames.isEmpty();
    }

    public static void lootFromInventory() {
        if (!canLoot()) {
            log("[Error] No target items specified for looting.");
            return;
        }

        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<Item> inventoryItems = LootInventory.getItems();

        for (Item item : inventoryItems) {
            if (item.getName() == null) {
                continue;
            }

            Matcher matcher = lootPattern.matcher(item.getName());
            if (matcher.find()) {
                LootInventory.take(item.getName());
                log("[Combat] Successfully looted item: " + item.getName());
            }
        }
    }

    public static void lootFromGround() {
        if (targetItemNames.isEmpty()) {
            log("[Error] No target items specified for looting.");
            return;
        }

        if (LootInventory.isOpen()) {
            log("[Combat] Loot interface is open, skipping ground looting.");
            return;
        }

        Pattern lootPattern = generateLootPattern(targetItemNames);
        List<GroundItem> groundItems = GroundItemQuery.newQuery().results().stream().toList();

        for (GroundItem groundItem : groundItems) {
            if (groundItem.getName() == null) {
                continue;
            }

            Matcher matcher = lootPattern.matcher(groundItem.getName());
            if (matcher.find()) {
                groundItem.interact("Take");
                log("[Combat] Interacted with: " + groundItem.getName() + " on the ground.");
                Execution.delay(5000);
            }
        }
    }

    public static void setPrayerPointsThreshold(int threshold) {
        prayerPointsThreshold = threshold;
    }

    public static void setHealthThreshold(int healthThreshold) {
        healthPointsThreshold = healthThreshold;
    }
    public static int getPrayerPointsThreshold() {
        return prayerPointsThreshold;
    }

    public static int getHealthPointsThreshold() {
        return healthPointsThreshold;
    }

    static long manageSoulSplit(LocalPlayer player) {
        if (player == null) {
            return 0;
        }
        if (!ActionBar.containsAbility("Soul Split")) {
            return 0;
        }

        boolean isSoulSplitActive = VarManager.getVarbitValue(16779) == 1;

        if (player.inCombat()) {
            if (!isSoulSplitActive && player.getPrayerPoints() > 1) {
                boolean success = ActionBar.useAbility("Soul Split");
                if (success) {
                    log("[Combat] Activating Soul Split.");
                    return random.nextLong(600, 1500);
                } else {
                    log("[Error] Failed to activate Soul Split.");
                    return 0;
                }
            }
        } else {
            if (isSoulSplitActive) {
                boolean success = ActionBar.useAbility("Soul Split");
                if (success) {
                    log("[Combat] Deactivating Soul Split.");
                    return random.nextLong(600, 1500);
                } else {
                    log("[Error] Failed to deactivate Soul Split.");
                    return 0;
                }
            }
        }

        return 0L;
    }

    public static int NecrosisStacksThreshold = 12;

    static void essenceOfFinality() {
        if (getLocalPlayer().getAdrenaline() >= 250
                && ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty()
                && ActionBar.getCooldownPrecise("Essence of Finality") == 0 && getLocalPlayer().inCombat() && getLocalPlayer().getFollowing() != null
                && getLocalPlayer().hasTarget()
                && ActionBar.getCooldownPrecise("Essence of Finality") == 0) {
            int currentNecrosisStacks = VarManager.getVarValue(VarDomainType.PLAYER, 10986);
            if (currentNecrosisStacks >= NecrosisStacksThreshold) {
                boolean abilityUsed = ActionBar.useAbility("Essence of Finality");
                if (abilityUsed) {
                    log("[Combat] Used Death Grasp with " + currentNecrosisStacks + " Necrosis stacks.");
                    Execution.delayUntil(RandomGenerator.nextInt(5000, 10000), () -> ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty());
                } else {
                    log("[Error] Attempted to use Death Grasp, but ability use failed.");
                }
            }
        }
    }

    static void DeathEssence() {
        if (getLocalPlayer() != null) {

            if (getLocalPlayer().getAdrenaline() >= 350 && ActionBar.getCooldownPrecise("Weapon Special Attack") == 0 && getLocalPlayer().getFollowing() != null && getLocalPlayer().getFollowing().getCurrentHealth() >= 500 && ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty() && getLocalPlayer().hasTarget()) {
                log("[Combat] Used Death Essence: " + ActionBar.useAbility("Weapon Special Attack"));
                Execution.delay(RandomGenerator.nextInt(600, 1500));
            }
        }
    }

    public static int VolleyOfSoulsThreshold = 5;

    static void volleyOfSouls() {
        if (getLocalPlayer() != null && VarManager.getVarValue(VarDomainType.PLAYER, 11035) >= VolleyOfSoulsThreshold && getLocalPlayer().inCombat() && getLocalPlayer().getFollowing() != null && getLocalPlayer().hasTarget()) {
            int currentResidualSouls = VarManager.getVarValue(VarDomainType.PLAYER, 11035); // Assuming this var tracks the relevant mechanic
            boolean abilityUsed = ActionBar.useAbility("Volley of Souls");
            if (abilityUsed) {
                log("[Combat] Used Volley of Souls with " + currentResidualSouls + " residual souls.");
                Execution.delayUntil(RandomGenerator.nextInt(2400, 3000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11035) >= VolleyOfSoulsThreshold);
            } else {
                log("[Error] Attempted to use Volley of Souls, but ability use failed.");
            }
        }
    }


    static void Deathmark() {
        if (VarManager.getVarbitValue(53247) == 0 && getLocalPlayer().getFollowing() != null && getLocalPlayer().getFollowing().getCurrentHealth() >= 500 && ActionBar.getCooldownPrecise("Invoke Death") == 0 && getLocalPlayer().hasTarget()) {
            log("[Combat] Used Invoke Death: " + ActionBar.useAbility("Invoke Death"));
            Execution.delay(RandomGenerator.nextInt(600, 1500));
        }
    }

    static void KeepArmyup() {
        if (VarManager.getVarValue(VarDomainType.PLAYER, 11018) == 0) {
            log("[Combat] Cast Conjure army: " + ActionBar.useAbility("Conjure Undead Army"));
            Execution.delay(RandomGenerator.nextInt(600, 1500));
        }
    }
    static void manageAnimateDead(LocalPlayer player) {
        if (player.inCombat()) {
            if (VarManager.getVarbitValue(49447) <= 1) {
                log("[Combat] Cast Animate Dead: " + ActionBar.useAbility("Animate Dead"));
                Execution.delay(RandomGenerator.nextInt(600, 1500));
            }
        }
    }

    static long useAggression(LocalPlayer player) {
        if (!useAggroPots || player == null || !player.inCombat() || player.getAnimationId() == 18000 || VarManager.getVarbitValue(33448) != 0) {  // Check if aggression potions are enabled
            return random.nextLong(300, 750);
        }

        ResultSet<Item> results = InventoryItemQuery.newQuery(93)
                .name("Aggression", String::contains)
                .option("Drink")
                .results();

        if (results.isEmpty()) {
            log("[Error] No aggression flasks found in the inventory.");
            return 1L;
        }

        Item aggressionFlask = results.first();
        if (aggressionFlask != null) {
            boolean success = backpack.interact(aggressionFlask.getName(), "Drink");
            if (success) {
                log("[Combat] Using aggression potion: " + aggressionFlask.getName());
                long delay = random.nextLong(1500, 3000);
                Execution.delay(delay);
                return delay;
            } else {
                log("[Error] Failed to use aggression potion: " + aggressionFlask.getName());
                return 0;
            }
        }

        return 0;
    }


    static long usePrayerOrRestorePots(LocalPlayer player) {
        if (!usePrayerPots || player == null || !player.inCombat() || player.getAnimationId() == 18000 || player.getPrayerPoints() > prayerPointsThreshold) {  // Check if there's a local player
            return random.nextLong(300, 750);
        }

        ResultSet<Item> items = InventoryItemQuery.newQuery(93).results();

        Item prayerOrRestorePot = items.stream()
                .filter(item -> item.getName() != null &&
                        (item.getName().toLowerCase().contains("prayer") ||
                                item.getName().toLowerCase().contains("restore")))
                .findFirst()
                .orElse(null);

        if (prayerOrRestorePot == null) {
            log("[Error]  No prayer or restore potions found in the backpack.");
            return 1L;
        }

        log("[Combat] Drinking " + prayerOrRestorePot.getName());
        boolean success = backpack.interact(prayerOrRestorePot.getName(), "Drink");
        if (success) {
            log("[Combat] Successfully drank " + prayerOrRestorePot.getName());
            long delay = random.nextLong(1500, 3000);
            Execution.delay(delay);
            return delay;
        } else {
            log("[Error] Failed to interact with " + prayerOrRestorePot.getName());
            return 0;
        }
    }

    static long drinkOverloads(LocalPlayer player) {
        if (!useOverloads) {
            return random.nextLong(300, 750);
        }

        if (player == null || !player.inCombat() || VarManager.getVarbitValue(48834) != 0 || player.getAnimationId() == 18000) {  // Ensure there's a valid player and conditions are right
            return 0L;
        }

        Pattern overloadPattern = Pattern.compile("overload", Pattern.CASE_INSENSITIVE);


        Item overloadPot = InventoryItemQuery.newQuery()
                .results()
                .stream()
                .filter(item -> item.getName() != null && overloadPattern.matcher(item.getName()).find())
                .findFirst()
                .orElse(null);

        if (overloadPot == null) {
            log("[Error] No overload potion found in the Backpack.");
            return 1L;
        }


        boolean success = backpack.interact(overloadPot.getName(), "Drink");
        if (success) {
            log("[Combat] Successfully drank " + overloadPot.getName());
            long delay = random.nextLong(1500, 3000);
            Execution.delay(delay);
            return delay;
        } else {
            log("[Error] Failed to interact with overload potion.");
            return 0L;
        }
    }
    static long useWeaponPoison(LocalPlayer player) {
        if (!useWeaponPoison) {
            return random.nextLong(300, 750);
        }
        if (player == null || player.getAnimationId() == 18068 || VarManager.getVarbitValue(2102) > 3) {  // Ensure there's a valid player and conditions are right
            return 0L;
        }

        Pattern poisonPattern = Pattern.compile("weapon poison\\+*?", Pattern.CASE_INSENSITIVE);

        Item weaponPoisonItem = InventoryItemQuery.newQuery()
                .results()
                .stream()
                .filter(item -> item.getName() != null && poisonPattern.matcher(item.getName()).find())
                .findFirst()
                .orElse(null);

        if (weaponPoisonItem == null) {
            log("[Error] No weapon poison found in the Backpack.");
            return 1L;
        }

        boolean success = backpack.interact(weaponPoisonItem.getName(), "Apply");
        if (success) {
            log("[Combat] Successfully applied " + weaponPoisonItem.getName());
            long delay = random.nextLong(1500, 3000);
            Execution.delay(delay);
            return delay;
        } else {
            log("[Error] Failed to apply weapon poison.");
            return 0L;
        }
    }
    private static void manageScrimshaws(LocalPlayer player) {
        Pattern scrimshawPattern = Pattern.compile("scrimshaw", Pattern.CASE_INSENSITIVE);
        Item Scrimshaw = InventoryItemQuery.newQuery(94).name(scrimshawPattern).results().first();

        if (Scrimshaw != null) {
            if (player.inCombat()) {
                Execution.delay(activateScrimshaws());
            } else {
                Execution.delay(deactivateScrimshaws());
            }
        } else {
            log("[Error] Pocket slot does not contain a scrimshaw.");
        }
    }

    private static long activateScrimshaws() {
        Pattern scrimshawPattern = Pattern.compile("scrimshaw", Pattern.CASE_INSENSITIVE);
        Item Scrimshaw = InventoryItemQuery.newQuery(94).name(scrimshawPattern).results().first();
        if (Scrimshaw != null && VarManager.getInvVarbit(Scrimshaw.getInventoryType().getId(), Scrimshaw.getSlot(), 17232) == 0) {
            log("[Combat] Activating Scrimshaws.");
            Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate");
            return RandomGenerator.nextInt(1500, 3000);
        }
        return 0L;
    }

    private static long deactivateScrimshaws() {
        Pattern scrimshawPattern = Pattern.compile("scrimshaw", Pattern.CASE_INSENSITIVE);
        Item Scrimshaw = InventoryItemQuery.newQuery(94).name(scrimshawPattern).results().first();
        if (Scrimshaw != null && VarManager.getInvVarbit(Scrimshaw.getInventoryType().getId(), Scrimshaw.getSlot(), 17232) == 1) {
            log("[Combat] Deactivating Scrimshaws.");
            Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate");
            return RandomGenerator.nextInt(1500, 3000);
        }
        return 0L;
    }
    private static boolean isQuickPrayersActive() {
        int[] varbitIds = {
                // Curses
                16761, 16762, 16763, 16786, 16764, 16765, 16787, 16788, 16765, 16766,
                16767, 16768, 16769, 16770, 16771, 16772, 16781, 16773, 16782, 16774,
                16775, 16776, 16777, 16778, 16779, 16780, 16784, 16783, 29065, 29066,
                29067, 29068, 29069, 49330, 29071, 34866, 34867, 34868, 53275, 53276,
                53277, 53278, 53279, 53280, 53281,
                // Normal
                16739, 16740, 16741, 16742, 16743, 16744, 16745, 16746, 16747, 16748,
                16749, 16750, 16751, 16752, 16753, 16754, 16755, 16756, 16757, 16758,
                16759, 16760, 53271, 53272, 53273, 53274
        };

        for (int varbitId : varbitIds) {
            if (VarManager.getVarbitValue(varbitId) == 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean quickPrayersActive = false;

    public static void manageQuickPrayers(LocalPlayer player) {

        if (player.inCombat() && !quickPrayersActive) {
            updateQuickPrayersActivation(player);
        } else if (!player.inCombat() && quickPrayersActive) {
            updateQuickPrayersActivation(player);
        }
    }

    private static void updateQuickPrayersActivation(LocalPlayer player) {
        boolean isCurrentlyActive = isQuickPrayersActive();
        boolean shouldBeActive = shouldActivateQuickPrayers(player);

        if (shouldBeActive && !isCurrentlyActive) {
            activateQuickPrayers();
        } else if (!shouldBeActive && isCurrentlyActive) {
            deactivateQuickPrayers();
        }
    }

    private static void activateQuickPrayers() {
        if (!quickPrayersActive) {
            log("[Combat] Activating Quick Prayers.");
            if (ActionBar.useAbility("Quick-prayers 1")) {
                log("[Combat] Quick Prayers activated successfully.");
                quickPrayersActive = true;
            } else {
                log("[Error] Failed to activate Quick Prayers.");
            }
        }
    }

    private static void deactivateQuickPrayers() {
        if (quickPrayersActive) {
            log("[Combat] Deactivating Quick Prayers.");
            if (ActionBar.useAbility("Quick-prayers 1")) {
                log("[Combat] Quick Prayers deactivated.");
                quickPrayersActive = false;
            } else {
                log("[Error] Failed to deactivate Quick Prayers.");
            }
        }
    }

    private static boolean shouldActivateQuickPrayers(LocalPlayer player) {
        return player.inCombat();
    }


    public static void eatFood(LocalPlayer player) {
        boolean isPlayerEating = player.getAnimationId() == 18001;
        double healthPercentage = calculateHealthPercentage(player);
        boolean isHealthAboveThreshold = healthPercentage > healthPointsThreshold;


        if (isPlayerEating || isHealthAboveThreshold) {
            return;
        }

        Execution.delay(healHealth(player));

    }

    public static double calculateHealthPercentage(LocalPlayer player) {
        double currentHealth = player.getCurrentHealth();
        double maximumHealth = player.getMaximumHealth();

        if (maximumHealth == 0) {
            throw new ArithmeticException("Maximum health cannot be zero.");
        }

        return (currentHealth / maximumHealth) * 100;
    }

    private static long healHealth(LocalPlayer player) {
        ResultSet<Item> foodItems = InventoryItemQuery.newQuery(93).option("Eat").results();
        Item food = foodItems.isEmpty() ? null : foodItems.first();

        if (food == null) {
            if (BankforFood) {
                log("[Error] No food found. Banking for food.");
                setLastSkillingLocation(player.getCoordinate());
                SnowsScript.setBotState(SnowsScript.BotState.BANKING);
                return random.nextLong(1500, 3000);
            } else {
                log("[Error] No food found and banking for food is disabled.");
                return 0;
            }
        }

        boolean eatSuccess = backpack.interact(food.getName(), "Eat");

        if (eatSuccess) {
            log("[Combat] Successfully ate " + food.getName());
            Execution.delay(RandomGenerator.nextInt(250, 450));
        } else {
            log("[Error] Failed to eat.");
        }
        return 0;
    }

    static boolean isHealthLow(LocalPlayer player) {
        double healthPercentage = calculateHealthPercentage(player);
        return healthPercentage < healthPointsThreshold;
    }

    private static int currentStep = 1;  // This controls which part of the switch statement to execute

    public static void handlePOD() {
        switch (currentStep) {
            case 1:
                if (travelToPOD()) {
                    log("[Combat] Arrived at POD. Proceeding to interaction.");
                    currentStep = 2;
                } else {
                    log("[Combat] Traveling to POD...");
                }
                break;
            case 2:
                if (interactWithKags()) {
                    log("[Combat] Interacted with Kags. Proceeding to the next step.");
                    currentStep = 3;
                }
                break;
            case 3:
                if (interactWithFirstDoor()) {
                    log("[Combat] Interacted with the first door. Proceeding to the next step.");
                    currentStep = 4;
                }
                break;
            case 4:
                if (interactWithOtherDoor()) {
                    log("[Combat] Interacted with the other door. Proceeding to the next step.");
                    currentStep = 5;
                }
                break;
            case 5:
                if (movePlayerEast()) {
                    log("[Combat] Moved player east. Proceeding to the next step.");
                    currentStep = 6;
                }
                break;
            case 6:
                attackTarget(getLocalPlayer());
                if (shouldBank(getLocalPlayer())) {
                    currentStep = 7;
                }
                break;
            case 7:
                if (BankingforPoD(getLocalPlayer())) {
                    currentStep = 1;
                }
                break;

            default:
                log("[Error] Invalid step. Please check the process flow.");
                break;
        }
    }

    private static boolean travelToPOD() {
        NavPath path = NavPath.resolve(new Coordinate(3122, 2632, 0));
        return Movement.traverse(path) == TraverseEvent.State.FINISHED;
    }

    private static boolean interactWithKags() {
        EntityResultSet<Npc> kags = NpcQuery.newQuery().name("Portmaster Kags").option("Travel").results();
        if (!kags.isEmpty()) {
            Npc nearestKags = kags.nearest();
            if (nearestKags != null && nearestKags.interact("Travel")) {
                Execution.delayUntil((5000), () -> Interfaces.isOpen(1188));
                if (Interfaces.isOpen(1188)) {
                    dialog( 0, -1, 77856776);
                    Execution.delay(RandomGenerator.nextInt(5000, 8000));
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean interactWithFirstDoor() {
        EntityResultSet<SceneObject> door = SceneObjectQuery.newQuery().name("Door").option("Open").results();
        if (!door.isEmpty()) {
            SceneObject nearestDoor = door.nearest();
            if (nearestDoor != null && nearestDoor.interact("Enter dungeon")) {
                Execution.delay(RandomGenerator.nextInt(5000, 8000));
                return true;
            }
        }
        return false;
    }

    private static boolean interactWithOtherDoor() {
        EntityResultSet<SceneObject> otherDoor = SceneObjectQuery.newQuery().name("Barrier").option("Pass through").results();
        if (!otherDoor.isEmpty()) {
            SceneObject nearestOtherDoor = otherDoor.nearest();
            if (nearestOtherDoor != null && nearestOtherDoor.interact("Pass through")) {
                Execution.delay(RandomGenerator.nextInt(5000, 8000));
                return true;
            }
        }
        return false;
    }

    private static boolean movePlayerEast() {
        if (getLocalPlayer() != null) {
            Coordinate targetCoordinate = getLocalPlayer().getCoordinate();
            Movement.walkTo(targetCoordinate.getX() + 7, targetCoordinate.getY(), true);
        }
        return true;
    }
    private static boolean BankingforPoD(LocalPlayer player) {
        if (VarManager.getVarbitValue(16779) == 1) {
            ActionBar.useAbility("Soul Split");
        }
        ActionBar.useAbility("War's Retreat Teleport");
        Execution.delay(RandomGenerator.nextInt(6000, 8000));

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
        if (!results.isEmpty()) {
            SceneObject chest = results.nearest();
            if (chest != null) {
                chest.interact("Load Last Preset from");
                Execution.delay(RandomGenerator.nextInt(6000, 8000));
            }
        }
        return true;
    }
    public static long handleArchGlacor() {
        switch (currentStep) {
            case 1:
                if (travelToArchGlacor(getLocalPlayer())) {
                    currentStep = 2;
                }
                break;
            case 2:
                attackTarget(getLocalPlayer());
                Execution.delay(printRemainingTime());
                Component timerComponent = getTimerComponent();
                if (shouldBank(getLocalPlayer()) || isTimerZero(timerComponent) || Backpack.isFull()) {
                    currentStep = 3;
                }
                break;
            case 3:
                if (BankingforArch(getLocalPlayer())) {
                    currentStep = 1;
                }
                break;
            default:
                break;
        }
        return 0;
    }

    private static boolean BankingforArch(LocalPlayer player) {
        ActionBar.useAbility("Max Guild Teleport");
        Execution.delay(RandomGenerator.nextInt(6000, 8000));

        EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Bank").results();
        if (!results.isEmpty()) {
            Npc banker = results.nearest();
            if (banker != null) {
                banker.interact("Load Last Preset from");
                Execution.delay(RandomGenerator.nextInt(3000, 4000));
                return true;
            }
        }
        return false;
    }

    private static boolean travelToArchGlacor(LocalPlayer player) {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Arch-Glacor portal").option("Enter").results();
        if (!results.isEmpty()) {
            SceneObject portal = results.nearest();
            if (portal != null) {
                portal.interact("Enter");
                Execution.delayUntil(random.nextLong(15000, 20000), () -> {
                    EntityResultSet<SceneObject> Aqueduct = SceneObjectQuery.newQuery().name("Aqueduct Portal").option("Enter").results();
                    return !Aqueduct.isEmpty();
                });
            }
        }

        EntityResultSet<SceneObject> Aqueduct = SceneObjectQuery.newQuery().name("Aqueduct Portal").option("Enter").results();
        while (Aqueduct.isEmpty()) {
            Execution.delay(1000);
            Aqueduct = SceneObjectQuery.newQuery().name("Aqueduct Portal").option("Enter").results();
        }

        SceneObject nearestAqueduct = Aqueduct.nearest();
        nearestAqueduct.interact("Enter");
        Execution.delayUntil(random.nextLong(10000, 15000), () -> Interfaces.isOpen(1591));

        if (Interfaces.isOpen(1591)) {
            component( 1, -1, 104267836);
            Execution.delay(random.nextLong(2500, 3500));
        } else {
            return false;
        }

        Movement.walkTo(player.getCoordinate().getX() + 11, player.getCoordinate().getY() -4 , true);

        return true;
    }

    private static Component getTimerComponent() {
        return ComponentQuery.newQuery(861).componentIndex(8).results().first();
    }

    public static boolean isTimerZero(Component timerComponent) {
        String timerText = timerComponent.getText();
        return "00:00".equals(timerText);
    }
    public static long printRemainingTime() {
        Component timerComponent = getTimerComponent();
        String remainingTime = timerComponent.getText();
        /*log("[Combat] Remaining time: " + remainingTime);*/
        return random.nextLong(1500, 3000);
    }
    public static boolean enableRadiusTracking = false;
    public static Coordinate centerCoordinate = new Coordinate(0, 0, 0);
    public static int radius = 10;

    public static boolean isWithinRadius(LocalPlayer player) {
        if (player == null) return false;
        return Distance.between(player.getCoordinate(), centerCoordinate) <= radius;
    }

    public static long ensureWithinRadius(LocalPlayer player) {
        if (!isWithinRadius(player)) {
            Movement.walkTo(centerCoordinate.getX(), centerCoordinate.getY(), true);
            Execution.delayUntil(15000, () -> isWithinRadius(player));
            log("[Combat] Moved player back to center.");
        }
        return random.nextLong(1500, 3000);
    }

    public static void setCenterCoordinate(Coordinate newCenter) {
        centerCoordinate = newCenter;
        log("[Combat] Center coordinate set to: " + newCenter);
    }

    public static void setRadius(int newRadius) {
        radius = newRadius;
        log("[Combat] Radius set to: " + newRadius);
    }
    public void handleBossAnimation(LocalPlayer player, Npc boss) {
        if (boss == null) {
            return;
        }

        int animationId = boss.getAnimationId();

        if (VarManager.getVarbitValue(16767) == 0) {
            if (animationId == 35832) {
                ActionBar.useAbility("Deflect Ranged");
            } else {
                ActionBar.useAbility("Deflect Necromancy");
            }
        }

        switch (animationId) {
            case 35831:
                break;
            case 35832:
                break;
            case 35833:
                break;
            case 35834:
                break;
            case 35835:
                break;
            default:
                // handle unknown animation
                break;
        }
    }
}