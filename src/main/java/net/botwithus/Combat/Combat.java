package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.PathingEntity;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.botwithus.Combat.Books.*;
import static net.botwithus.Combat.Food.eatFood;
import static net.botwithus.Combat.Loot.*;
import static net.botwithus.Combat.Notepaper.useItemOnNotepaper;
import static net.botwithus.Combat.Potions.*;
import static net.botwithus.Combat.Prayers.*;
import static net.botwithus.Combat.Radius.enableRadiusTracking;
import static net.botwithus.Combat.Radius.ensureWithinRadius;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Combat {

    public static boolean handleMultitarget = false;
    public static boolean useUndeadSlayer = false;
    public static boolean useDragonSlayer = false;
    public static boolean useDemonSlayer = false;
    public static boolean useDwarfcannon = false;
    public static double healthThreshold = 0.8;
    public static boolean shouldEatFood = false;
    private static final Set<Integer> recentlyAttackedTargets = new HashSet<>();
    private static long lastClearTime = System.currentTimeMillis();

    public static double getHealthThreshold() {
        return healthThreshold;
    }

    public static void setHealthThreshold(double healthThreshold) {
        Combat.healthThreshold = healthThreshold;
    }

    public static boolean isStackable = false;



    public static long attackTarget(LocalPlayer player) {
        if (player == null) {
            return random.nextLong(100, 200);
        }
        if (usePowderOfProtection) {
            powderOfProtection();
        }
        if (usePowderOfPenance) {
            powderOfPenance();
        }
        if (useKwuarmSticks) {
            kwuarmSticks();
        }
        if (useLantadymeSticks) {
            lantadymeSticks();
        }
        if (useIritSticks) {
            iritSticks();
        }
        if (SoulSplit && !player.inCombat()) {
            deactivateSoulSplit();
        }
        if (usequickPrayers) {
            updateQuickPrayersActiveStatus();
            if (!player.inCombat() && quickPrayersActive) {
                deactivateQuickPrayers();
            }
        }

        if (enableRadiusTracking) {
            ensureWithinRadius(player);
        }

        if (useNotepaper) {
            useItemOnNotepaper();
        }
        if (useLoot) {
            Execution.delay(processLooting());
        }
        if (lootNoted) {
            lootNotedItemsFromInventory();
        }
        if (isStackable) {
            lootStackableItemsFromInventory();
        }

        if (useDwarfcannon) {
            dwarvenSiegeCannon();
        }

        if (shouldEatFood) {
            eatFood(player);
        }

        managePotions(player);
        manageScripturesAndScrimshaws(player);

        if (player.isMoving()) {
            return random.nextLong(100, 200);
        }

        if (!player.hasTarget()) {
            handleCombat(player);
        } else {
            if (SoulSplit && VarManager.getVarbitValue(16779) != 1) {
                activateSoulSplit(player);
            }
            if (usequickPrayers && !quickPrayersActive) {
                activateQuickPrayers();
            }
            manageCombatAbilities();
        }


        return random.nextLong(100, 200);
    }




    public static void handleCombat(LocalPlayer player) {
        List<String> targetNames = getTargetNames();
        if (targetNames.isEmpty()) {
            log("[Error] No target names specified.");
            return;
        }

        Pattern monsterPattern = generateRegexPattern(targetNames);
        Optional<Npc> nearestMonsterOptional = NpcQuery.newQuery()
                .name(monsterPattern)
                .isReachable()
                .health(100, 1_000_000)
                .option("Attack")
                .results()
                .stream()
                .min(Comparator.comparingDouble(npc -> npc.getCoordinate().distanceTo(player.getCoordinate())));

        Npc monster = nearestMonsterOptional.orElse(null);
        if (monster != null) {
            boolean attack = monster.interact("Attack");
            if (attack) {
                logAndDelay("[Combat] Successfully attacked " + monster.getName() + ".", 300, 400);
            } else {
                logAndDelay("[Error] Failed to attack " + monster.getName(), 1500, 3000);
            }
        } else {
            logAndDelay("[Error] No valid NPC target found.", 1000, 3000);
        }
    }


    public static void logAndDelay(String message, int minDelay, int maxDelay) {
        log(message);
        long delay = random.nextLong(minDelay, maxDelay);
        Execution.delay(delay);
    }

    public static void manageCombatAbilities() {
        LocalPlayer player = getLocalPlayer();

        if (DeathGrasp) {
            essenceOfFinality(player);
        }
        if (InvokeDeath) {
            InvokeDeath(player);
        }
        if (VolleyofSouls) {
            volleyOfSouls(player);
        }
        if (SpecialAttack) {
            DeathEssence(player);
        }
        if (KeepArmyup) {
            KeepArmyup(player);
        }
        if (useVulnerabilityBombs) {
            vulnerabilityBomb(player);
        }
        if (useThreadsofFate) {
            manageThreadsofFate(player);
        }
        if (useDarkness) {
            manageDarkness(player);
        }
        if (useElvenRitual) {
            activateElvenRitual(player);
        }
        if (useExcalibur) {
            activateExcalibur();
        }
        if (useAnimateDead) {
            manageAnimateDead(player);
        }
        if (useUndeadSlayer) {
            Execution.delay(activateUndeadSlayer());
        }
        if (useDragonSlayer) {
            Execution.delay(activateDragonSlayer());
        }
        if (useDemonSlayer) {
            Execution.delay(activateDemonSlayer());
        }
    }

    public static void essenceOfFinality(LocalPlayer player) {
        int currentNecrosisStacks = VarManager.getVarValue(VarDomainType.PLAYER, 10986);

        if (player.getAdrenaline() > 250
                && ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty()
                && player.inCombat()
                && player.hasTarget()
                && ActionBar.containsAbility("Essence of Finality")
                && currentNecrosisStacks >= NecrosisStacksThreshold) {

            boolean abilityUsed = ActionBar.useAbility("Essence of Finality");
            if (abilityUsed) {
                boolean effectConfirmed = Execution.delayUntil(random.nextLong(5500, 6500), () -> VarManager.getVarValue(VarDomainType.PLAYER, 10986) != currentNecrosisStacks);
                if (effectConfirmed) {
                    log("[Success] Essence of Finality effect confirmed with " + currentNecrosisStacks + " Necrosis stacks.");
                } else {
                    log("[Error] Essence of Finality effect not confirmed.");
                }
            }
        }
    }


    public static void DeathEssence(LocalPlayer player) {
        if (player.getAdrenaline() > 300
                && player.getFollowing().getCurrentHealth() > 500
                && ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty()
                && player.inCombat()
                && player.hasTarget()
                && ActionBar.containsAbility("Weapon Special Attack")) {

            boolean success = ActionBar.useAbility("Weapon Special Attack");
            if (success) {
                boolean abilityEffect = Execution.delayUntil(random.nextLong(5000, 6500), () -> !ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty());
                if (abilityEffect) {
                    log("[Success] Death Essence effect confirmed.");
                } else {
                    log("[Error] Death Essence effect not confirmed.");
                }
            } else {
                log("[Error] Attempted to use Death Essence, but ability use failed.");
            }
        }
    }

    public static void volleyOfSouls(LocalPlayer player) {
        int currentResidualSouls = VarManager.getVarValue(VarDomainType.PLAYER, 11035);

        if (currentResidualSouls >= VolleyOfSoulsThreshold
                && player.hasTarget()
                && player.inCombat()
                && ActionBar.containsAbility("Volley of Souls")) {

            boolean abilityUsed = ActionBar.useAbility("Volley of Souls");
            if (abilityUsed) {
                boolean effectConfirmed = Execution.delayUntil(random.nextLong(5000, 6500), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11035) != currentResidualSouls);
                if (effectConfirmed) {
                    log("[Success] Volley of Souls effect confirmed with " + currentResidualSouls + " residual souls.");
                } else {
                    log("[Error] Volley of Souls effect not confirmed.");
                }
            } else {
                log("[Error] Attempted to use Volley of Souls, but ability use failed.");
            }
        }
    }

    public static void InvokeDeath(LocalPlayer player) {
        if (player != null) {
            if (VarManager.getVarbitValue(53247) == 0 && getLocalPlayer().getFollowing() != null && getLocalPlayer().getFollowing().getCurrentHealth() >= 5000 && ActionBar.getCooldownPrecise("Invoke Death") == 0 && getLocalPlayer().hasTarget()) {
                log("[Success] Used Invoke Death: " + ActionBar.useAbility("Invoke Death"));
                Execution.delay(RandomGenerator.nextInt(600, 1500));
            }
        }
    }

    public static void KeepArmyup(LocalPlayer player) {
        if (VarManager.getVarValue(VarDomainType.PLAYER, 11018) == 0 && ActionBar.containsAbility("Conjure Undead Army")) {

            boolean success = ActionBar.useAbility("Conjure Undead Army");
            if (success) {
                log("[Success] Cast Conjure army: " + true);
                Execution.delayUntil(random.nextLong(3000, 5000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11018) != 0);
            } else {
                log("[Error] Attempted to cast Conjure army, but ability use failed.");
            }
        }
    }

    public static void manageAnimateDead(LocalPlayer player) {
        if (ComponentQuery.newQuery(284).spriteId(14764).results().isEmpty()) {

            boolean success = ActionBar.useAbility("Animate Dead");
            if (!success) {
                log("[Error] Attempted to cast Animate Dead, but ability use failed.");

                log("[Success] Cast Animate Dead: " + true);
                Execution.delayUntil(random.nextLong(3000, 5000), () -> !ComponentQuery.newQuery(284).spriteId(14764).results().isEmpty());
            }
        }
    }

    public static void manageThreadsofFate(LocalPlayer player) {
        if (ActionBar.containsAbility("Threads of Fate") && ActionBar.getCooldownPrecise("Threads of Fate") == 0) {

            boolean success = ActionBar.useAbility("Threads of Fate");
            if (success) {
                log("[Success] Cast Threads of Fate: " + true);
                Execution.delayUntil(random.nextLong(3000, 5000), () -> ActionBar.getCooldownPrecise("Threads of Fate") != 0);

            } else {
                log("[Error] Attempted to cast Threads of Fate, but ability use failed.");
            }
        }
    }

    public static void manageDarkness(LocalPlayer player) {
        if (ActionBar.containsAbility("Darkness") && VarManager.getVarValue(VarDomainType.PLAYER, 11074) == 0) {

            boolean success = ActionBar.useAbility("Darkness");
            if (success) {
                log("[Success] Cast Darkness: " + true);
                Execution.delayUntil(random.nextLong(3000, 5000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11074) != 0);

            } else {
                log("[Error] Attempted to cast Darkness, but ability use failed.");
            }
        }
    }

    public static void vulnerabilityBomb(LocalPlayer player) {
        int vulnDebuffVarbit = VarManager.getVarbitValue(1939);

        if (vulnDebuffVarbit == 0 && ActionBar.containsItem("Vulnerability bomb") && player.hasTarget()) {


            boolean success = ActionBar.useItem("Vulnerability bomb", "Throw");
            if (success) {
                log("[Success] Throwing Vulnerability bomb at " + player.getTarget().getName());
                Execution.delay(random.nextLong(1900, 2000));

            } else {
                log("[Error] Failed to use Vulnerability bomb!");
            }
        }
    }

    public static void activateElvenRitual(LocalPlayer player) {
        if (player.getPrayerPoints() < prayerPointsThreshold && Backpack.contains("Ancient elven ritual shard")) {

            Component elvenRitual = ComponentQuery.newQuery(291).spriteId(43358).results().first();
            if (elvenRitual != null) {

                boolean success = Backpack.interact("Ancient elven ritual shard", "Activate");
                if (success) {
                    log("[Success] Activated Elven Ritual Shard.");
                    Execution.delay(random.nextLong(1900, 2000));

                } else {
                    log("[Error] Failed to activate Elven Ritual Shard.");
                }
            }
        }
    }

    private static void activateExcalibur() {
        if (ComponentQuery.newQuery(291).spriteId(14632).results().first() == null) {

            LocalPlayer player = getLocalPlayer();
            if (player.getCurrentHealth() * 100 / player.getMaximumHealth() >= healthPointsThreshold) {

                ResultSet<net.botwithus.rs3.game.Item> items = InventoryItemQuery.newQuery().results();
                Item excaliburItem = items.stream()
                        .filter(item -> item.getName() != null && item.getName().toLowerCase().contains("excalibur"))
                        .findFirst()
                        .orElse(null);

                if (excaliburItem == null) {
                    log("No Excalibur found!");
                } else {

                    boolean success = Backpack.interact(excaliburItem.getName(), "Activate");
                    if (success) {
                        log("Activating " + excaliburItem.getName());
                        Execution.delay(random.nextLong(1900, 2000));

                    } else {
                        log("Failed to activate Excalibur.");
                    }
                }
            }
        }
    }

    public static void printSiegeEngineRemainingTime() {
        ResultSet<Component> components = ComponentQuery.newQuery(291).spriteId(2).results();
        if (components.isEmpty()) {
            log("[Error] No components with spriteId 2 found.");
            return;
        }

        for (Component component : components) {
            int interfaceIndex = component.getInterfaceIndex();
            int componentIndex = component.getComponentIndex();
            int subComponentIndex = component.getSubComponentIndex() + 1;

            ResultSet<Component> targetComponents = ComponentQuery.newQuery(interfaceIndex)
                    .componentIndex(componentIndex)
                    .subComponentIndex(subComponentIndex)
                    .results();

            if (targetComponents.isEmpty()) {
                log("[Error] No components found for Interface Index: " + interfaceIndex + ", Component Index: " + componentIndex + ", and Sub Component Index: " + subComponentIndex);
                continue;
            }

            Component targetComponent = targetComponents.first();
            String text = targetComponent.getText();

            if (isTimeLessThanFiveMinutes(text)) {
                log("[Combat] Interacting with Siege Engine.");
                EntityResultSet<SceneObject> siegeEngine = SceneObjectQuery.newQuery().name("Dwarven siege engine").results();
                if (!siegeEngine.isEmpty()) {
                    siegeEngine.first().interact("Fire");
                    Execution.delay(random.nextLong(600, 950));
                }
            }
        }
    }

    public static boolean isTimeLessThanFiveMinutes(String text) {
        Pattern pattern = Pattern.compile("(\\d+)([smh])");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);
            switch (unit) {
                case "s":
                    return true;
                case "m":
                    return value < 5;
                case "h":
                    return false;
            }
        }
        return false;
    }

    public static void dwarvenSiegeCannon() {
        EntityResultSet<SceneObject> siegeEngine = SceneObjectQuery.newQuery().name("Dwarven siege engine").option("Fire").results();
        if (!siegeEngine.isEmpty()) {
            printSiegeEngineRemainingTime();
        } else {
            log("[Combat] No Dwarven siege engine found.");
        }
    }
    public static long activateUndeadSlayer() {
        if (ActionBar.containsAbility("Undead Slayer") && ActionBar.getCooldownPrecise("Undead Slayer") == 0) {
            boolean success = ActionBar.useAbility("Undead Slayer");
            if (success) {
                log("[Success] Activated Undead Slayer.");
                return random.nextLong(1900, 2000);
            } else {
                log("[Error] Failed to activate Undead Slayer.");
            }
        }
        return 0;
    }
    public static long activateDragonSlayer() {
        if (ActionBar.containsAbility("Dragon Slayer") && ActionBar.getCooldownPrecise("Dragon Slayer") == 0) {
            boolean success = ActionBar.useAbility("Dragon Slayer");
            if (success) {
                log("[Success] Activated Dragon Slayer.");
                return random.nextLong(1900, 2000);
            } else {
                log("[Error] Failed to activate Dragon Slayer.");
            }
        }
        return 0;
    }
    public static long activateDemonSlayer() {
        if (ActionBar.containsAbility("Demon Slayer") && ActionBar.getCooldownPrecise("Demon Slayer") == 0) {
            boolean success = ActionBar.useAbility("Demon Slayer");
            if (success) {
                log("[Success] Activated Demon Slayer.");
                return random.nextLong(1900, 2000);
            } else {
                log("[Error] Failed to activate Demon Slayer.");
            }
        }
        return 0;
    }


}
