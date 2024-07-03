package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.PathingEntity;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.*;
import java.util.regex.Pattern;

import static net.botwithus.Combat.Books.*;
import static net.botwithus.Combat.Food.eatFood;
import static net.botwithus.Combat.Food.isHealthLow;
import static net.botwithus.Combat.Loot.*;
import static net.botwithus.Combat.Notepaper.useItemOnNotepaper;
import static net.botwithus.Combat.Potions.managePotions;
import static net.botwithus.Combat.Prayers.manageQuickPrayers;
import static net.botwithus.Combat.Prayers.manageSoulSplit;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Combat {

    public static boolean handleMultitarget = false;
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
        if (player == null || player.isMoving()) {
            return random.nextLong(100, 200);
        }

        if (shouldHandleLooting()) {
            handleLooting(player);
        }

        if (shouldHandleSoulSplit(player)) {
            handleSoulSplit(player);
        }

        if (shouldHandleQuickPrayers()) {
            handleQuickPrayers(player);
        }

        if (shouldEatFood && isHealthLow(player)) {
            long result = handleFood(player);
            if (result == 1L) {
                log("[Warning] No food found, continuing with the script.");
            }
        }

        managePotions(player);
        manageScripturesAndScrimshaws(player);
        handleMultitarget();


        if (!player.hasTarget()) {
            log("[Combat] Player's current target does not match the specified target name. Attacking nearest monster.");
            attackNearestMonster(player);
        }

        if (player.hasTarget()) {
            manageCombatAbilities();

            PathingEntity<?> target = player.getTarget();

            if (handleMultitarget) {
                if (target.getCurrentHealth() <= target.getMaximumHealth() * healthThreshold) {
                    Npc newTarget = findDifferentTarget(player, target.getId());
                    if (newTarget != null) {
                        return attackMonster(player, newTarget);
                    } else {
                        return logAndDelay("[Combat] No new target found.", 100, 200);
                    }
                }
            } else {
                if (target.getCurrentHealth() <= 0) {
                    log("[Combat] Current target is dead. Finding a new target.");
                    Npc newTarget = findDifferentTarget(player, target.getId());
                    if (newTarget != null) {
                        return attackMonster(player, newTarget);
                    } else {
                        return logAndDelay("[Combat] No new target found.", 100, 200);
                    }
                }
            }

            return random.nextLong(100, 200);
        }

        return random.nextLong(100, 200);
    }


    private static boolean shouldHandleLooting() {
        return useLoot || useNotepaper || lootNoted || isStackable;
    }

    private static void handleLooting(LocalPlayer player) {
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
    }

    private static boolean shouldHandleSoulSplit(LocalPlayer player) {
        return SoulSplit;
    }

    private static void handleSoulSplit(LocalPlayer player) {
        Execution.delay(manageSoulSplit(player));
    }

    private static boolean shouldHandleQuickPrayers() {
        return usequickPrayers;
    }

    private static void handleQuickPrayers(LocalPlayer player) {
        manageQuickPrayers(player);
    }

    private static boolean shouldEatFood() {
        return shouldEatFood;
    }

    private static long handleFood(LocalPlayer player) {
        eatFood(player);
        return 0;
    }

    private static void handleMultitarget() {
        if (handleMultitarget) {
            if (System.currentTimeMillis() - lastClearTime > random.nextLong(5000, 7500)) {
                recentlyAttackedTargets.clear();
                lastClearTime = System.currentTimeMillis();
            }
        }
    }


    private static Npc findDifferentTarget(LocalPlayer player, int currentTargetId) {
        List<String> targetNames = getTargetNames();
        if (targetNames.isEmpty()) {
            log("[Error] No target names specified.");
            return null;
        }

        Pattern monsterPattern = generateRegexPattern(targetNames);

        Npc newTarget = NpcQuery.newQuery()
                .name(monsterPattern)
                .isReachable()
                .health(100, 1_000_000)
                .option("Attack")
                .results()
                .stream()
                .filter(npc -> npc.getId() != currentTargetId && npc.getCurrentHealth() > npc.getMaximumHealth() * healthThreshold)
                .min(Comparator.comparingDouble(npc -> npc.distanceTo(player.getCoordinate())))
                .orElse(null);

        if (newTarget == null) {
            newTarget = NpcQuery.newQuery()
                    .name(monsterPattern)
                    .isReachable()
                    .health(100, 1_000_000)
                    .option("Attack")
                    .results()
                    .stream()
                    .filter(npc -> npc.getId() != currentTargetId)
                    .min(Comparator.comparingDouble(npc -> npc.distanceTo(player.getCoordinate())))
                    .orElse(null);
        }

        return newTarget;
    }


    static long attackNearestMonster(LocalPlayer player) {
       /* String playerName = player.getName();
        Npc followingNpc = null;

        for (Npc npc : net.botwithus.rs3.game.queries.builders.characters.NpcQuery.newQuery().results()) {
            PathingEntity<?> npcFollowing = npc.getFollowing();
            String npcName = npc.getName();

            // Check if the NPC has the "Attack" option
            if (npcFollowing != null && npcFollowing.getId() == player.getId() && !npcName.contains(playerName) && npc.getOptions().contains("Attack")) {
                followingNpc = npc;
                break;
            }
        }

        if (followingNpc != null) {
            log("[Info] Attacking NPC that is following the player: " + followingNpc.getName());
            return attackMonster(player, followingNpc);
        }*/

        Npc monster = findTarget(player);
        if (monster == null) {
            return logAndDelay("[Error] No valid NPC target found.", 1000, 3000);
        }

        return attackMonster(player, monster);
    }


    private static Npc findTarget(LocalPlayer player) {
        List<String> targetNames = getTargetNames();
        if (targetNames.isEmpty()) {
            log("[Error] No target names specified.");
            return null;
        }

        Pattern monsterPattern = generateRegexPattern(targetNames);

        return NpcQuery.newQuery()
                .name(monsterPattern)
                .isReachable()
                .health(100, 1_000_000)
                .option("Attack")
                .results()
                .stream()
                .filter(npc -> !recentlyAttackedTargets.contains(npc.getId()) || npc.getCurrentHealth() == 0)
                .min(Comparator.comparingDouble(npc -> npc.distanceTo(player.getCoordinate())))
                .orElse(null);
    }

    private static long attackMonster(LocalPlayer player, Npc monster) {
        if (monster.getOptions().contains("Attack")) {
            boolean attack = monster.interact("Attack");
            if (attack) {
                if (handleMultitarget) {
                    recentlyAttackedTargets.add(monster.getId());
                }
                return logAndDelay("[Combat] Successfully attacked " + monster.getName() + ".", 300, 400);
            } else {
                return logAndDelay("[Error] Failed to attack " + monster.getName(), 1500, 3000);
            }
        } else {
            return logAndDelay("[Error] No attack option for " + monster.getName(), 1000, 3000);
        }
    }

    public static long logAndDelay(String message, int minDelay, int maxDelay) {
        log(message);
        long delay = random.nextLong(minDelay, maxDelay);
        Execution.delay(delay);
        return delay;
    }

    public static boolean useThreadsofFate = false;
    public static boolean useAnimateDead = false;
    public static boolean useDarkness = false;
    public static int NecrosisStacksThreshold = 12;
    public static int VolleyOfSoulsThreshold = 5;
    public static boolean useExcalibur = false;
    public static boolean useElvenRitual = false;

    public static void manageCombatAbilities() {
        LocalPlayer player = getLocalPlayer();

        if (DeathGrasp) {
            Execution.delay(essenceOfFinality(player));
        }
        if (InvokeDeath) {
            Execution.delay(Deathmark(player));
        }
        if (VolleyofSouls) {
            Execution.delay(volleyOfSouls(player));
        }
        if (SpecialAttack) {
            Execution.delay(DeathEssence(player));
        }
        if (KeepArmyup) {
            Execution.delay(KeepArmyup(player));
        }
        if (useVulnerabilityBombs) {
            Execution.delay(vulnerabilityBomb(player));
        }
        if (useThreadsofFate) {
            Execution.delay(manageThreadsofFate(player));
        }
        if (useDarkness) {
            Execution.delay(manageDarkness(player));
        }
        if (useElvenRitual) {
            Execution.delay(activateElvenRitual(player));
        }
        if (useExcalibur) {
            Execution.delay(activateExcalibur());
        }
        if (useAnimateDead) {
            Execution.delay(manageAnimateDead(player));
        }
    }

    public static long essenceOfFinality(LocalPlayer player) {
        int currentNecrosisStacks = VarManager.getVarValue(VarDomainType.PLAYER, 10986);

        // Check all conditions and return early if any of them are not met
        if (player.getAdrenaline() < 250
                || !ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty()
                || !player.inCombat()
                || !player.hasTarget()
                || !ActionBar.containsAbility("Essence of Finality")
                || currentNecrosisStacks < NecrosisStacksThreshold) {
            return 0;
        }

        // If all conditions are met, proceed with the main logic
        boolean abilityUsed = ActionBar.useAbility("Essence of Finality");
        if (abilityUsed) {
            log("[Success] Used Death Grasp with " + currentNecrosisStacks + " Necrosis stacks.");
            return random.nextLong(1900, 2000);
        } else {
            log("[Error] Attempted to use Death Grasp, but ability use failed.");
        }

        return 0;
    }


    public static long DeathEssence(LocalPlayer player) {
        if (player.getAdrenaline() < 300
                || player.getFollowing().getCurrentHealth() < 500
                || !ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty()
                || !player.hasTarget()
                || !ActionBar.containsAbility("Weapon Special Attack")) {
            return 0;
        }

        boolean success = ActionBar.useAbility("Weapon Special Attack");
        if (success) {
            log("[Success] Used Death Essence: " + true);
            return random.nextLong(1900, 2000);
        } else {
            log("[Error] Attempted to use Death Essence, but ability use failed.");
        }

        return 0;
    }

    public static long volleyOfSouls(LocalPlayer player) {
        int currentResidualSouls = VarManager.getVarValue(VarDomainType.PLAYER, 11035);

        if (currentResidualSouls < VolleyOfSoulsThreshold
                || !player.hasTarget()
                || !ActionBar.containsAbility("Volley of Souls")) {
            return 0;
        }

        boolean abilityUsed = ActionBar.useAbility("Volley of Souls");
        if (abilityUsed) {
            log("[Success] Used Volley of Souls with " + currentResidualSouls + " residual souls.");
            return random.nextLong(1900, 2000);
        } else {
            log("[Error] Attempted to use Volley of Souls, but ability use failed.");
        }

        return 0;
    }

    public static long Deathmark(LocalPlayer player) {
        PathingEntity<?> target = player.getTarget();
        if (target == null) {
            log("[Error] No target found.");
            return 0;
        }

        double healthPercentage = (double) target.getCurrentHealth() / target.getMaximumHealth();

        if (VarManager.getVarbitValue(53247) != 0
                || healthPercentage <= 0.15
                || !ActionBar.containsAbility("Invoke Death")) {
            return 0;
        }

        boolean success = ActionBar.useAbility("Invoke Death");
        if (success) {
            log("[Success] Used Invoke Death: " + true);
            Execution.delayUntil(random.nextLong(3000,5000), () -> VarManager.getVarbitValue(53247) != 0);
            return random.nextLong(1900, 2000);
        } else {
            log("[Error] Attempted to use Invoke Death, but ability use failed.");
        }

        return 0;
    }

    public static long KeepArmyup(LocalPlayer player) {
        if (VarManager.getVarValue(VarDomainType.PLAYER, 11018) != 0 || !ActionBar.containsAbility("Conjure Undead Army")) {
            return 0;
        }

        boolean success = ActionBar.useAbility("Conjure Undead Army");
        if (success) {
            log("[Success] Cast Conjure army: " + true);
            return random.nextLong(1900, 2000);
        } else {
            log("[Error] Attempted to cast Conjure army, but ability use failed.");
        }

        return 0;
    }

    public static long manageAnimateDead(LocalPlayer player) {
        if (ComponentQuery.newQuery(284).spriteId(14764).results() != null) {
            return 0;
        }

        boolean success = ActionBar.useAbility("Animate Dead");
        if (!success) {
            log("[Error] Attempted to cast Animate Dead, but ability use failed.");
            return 0;
        }

        log("[Success] Cast Animate Dead: " + true);
        return random.nextLong(1900, 2000);
    }

    public static long manageThreadsofFate(LocalPlayer player) {
        if (!ActionBar.containsAbility("Threads of Fate") || ActionBar.getCooldownPrecise("Threads of Fate") != 0) {
            return 0;
        }

        boolean success = ActionBar.useAbility("Threads of Fate");
        if (success) {
            log("[Success] Cast Threads of Fate: " + true);
            return random.nextLong(1900, 2000);
        } else {
            log("[Error] Attempted to cast Threads of Fate, but ability use failed.");
        }

        return 0;
    }

    public static long manageDarkness(LocalPlayer player) {
        if (!ActionBar.containsAbility("Darkness") || VarManager.getVarValue(VarDomainType.PLAYER, 11074) != 0) {
            return 0;
        }

        boolean success = ActionBar.useAbility("Darkness");
        if (success) {
            log("[Success] Cast Darkness: " + true);
            return random.nextLong(1900, 2000);
        } else {
            log("[Error] Attempted to cast Darkness, but ability use failed.");
        }

        return 0;
    }
    public static long vulnerabilityBomb(LocalPlayer player) {
        int vulnDebuffVarbit = VarManager.getVarbitValue(1939);

        if (vulnDebuffVarbit != 0 || !ActionBar.containsItem("Vulnerability bomb")) {
            return 0;
        }

        boolean success = ActionBar.useItem("Vulnerability bomb", "Throw");
        if (success) {
            log("[Success] Throwing Vulnerability bomb at " + player.getTarget().getName());
            return random.nextLong(1900, 2000);
        } else {
            log("[Error] Failed to use Vulnerability bomb!");
        }

        return 0;
    }

    public static long activateElvenRitual(LocalPlayer player) {
        if (player.getPrayerPoints() >= prayerPointsThreshold || !Backpack.contains("Ancient elven ritual shard")) {
            return 0;
        }

        Component elvenRitual = ComponentQuery.newQuery(291).spriteId(43358).results().first();
        if (elvenRitual != null) {
            return 0;
        }

        boolean success = Backpack.interact("Ancient elven ritual shard", "Activate");
        if (success) {
            log("[Success] Activated Elven Ritual Shard.");
            return random.nextLong(1900, 2000);
        } else {
            log("[Error] Failed to activate Elven Ritual Shard.");
        }

        return 0;
    }

    private static long activateExcalibur() {
        if (ComponentQuery.newQuery(291).spriteId(14632).results().first() != null) {
            return 0;
        }

        LocalPlayer player = getLocalPlayer();
        if (player.getCurrentHealth() * 100 / player.getMaximumHealth() >= healthPointsThreshold) {
            return 0;
        }

        ResultSet<net.botwithus.rs3.game.Item> items = InventoryItemQuery.newQuery().results();
        Item excaliburItem = items.stream()
                .filter(item -> item.getName() != null && item.getName().toLowerCase().contains("excalibur"))
                .findFirst()
                .orElse(null);

        if (excaliburItem == null) {
            log("No Excalibur found!");
            return 0;
        }

        boolean success = Backpack.interact(excaliburItem.getName(), "Activate");
        if (success) {
            log("Activating " + excaliburItem.getName());
            return random.nextLong(1900, 2000);
        } else {
            log("Failed to activate Excalibur.");
            return 0;
        }
    }
}
