package net.botwithus.Combat;

import net.botwithus.Variables.Variables;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.scene.entities.characters.PathingEntity;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import java.util.*;
import java.util.regex.Pattern;

import static net.botwithus.Combat.Abilities.*;
import static net.botwithus.Combat.Books.*;
import static net.botwithus.Combat.Food.eatFood;
import static net.botwithus.Combat.Food.isHealthLow;
import static net.botwithus.Combat.Loot.processLooting;
import static net.botwithus.Combat.Potions.managePotions;
import static net.botwithus.Combat.Prayers.manageQuickPrayers;
import static net.botwithus.Combat.Prayers.manageSoulSplit;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;

public class Combat {

    public static boolean handleMultitarget = false;
    public static double healthThreshold = 0.8;
    private static final Set<Integer> recentlyAttackedTargets = new HashSet<>();
    private static long lastClearTime = System.currentTimeMillis();

    public static double getHealthThreshold() {
        return healthThreshold;
    }

    public static void setHealthThreshold(double healthThreshold) {
        Combat.healthThreshold = healthThreshold;
    }

    public static long attackTarget(LocalPlayer player) {
        if (player == null || player.isMoving()) {
            return random.nextLong(1500, 3000);
        }

        if (useLoot) {
            processLooting();
        }

        if (SoulSplit) {
            Execution.delay(manageSoulSplit(player));
        }

        if (usequickPrayers) {
            manageQuickPrayers(player);
        }

        managePotions(player);
        manageScripturesAndScrimshaws(player);

        if (isHealthLow(player)) {
            eatFood(player);
            return logAndDelay("[Error] Health is low, won't attack until more health.", 1000, 3000);
        }

        if (System.currentTimeMillis() - lastClearTime > random.nextLong(10000, 15000)) {
            /*log("[Combat] Clearing recently attacked targets.");*/
            recentlyAttackedTargets.clear();
            lastClearTime = System.currentTimeMillis();
        }

        if (player.hasTarget()) {
            PathingEntity<?> target = player.getTarget();
            if (handleMultitarget) {
                if (target.getCurrentHealth() < target.getMaximumHealth() * healthThreshold) {
                    log("[Combat] Target health below threshold. Finding new target.");
                    recentlyAttackedTargets.add(target.getId());
                    return findAndAttackNewTarget(player);
                } else {
                    /*log("[Combat] Target health above threshold. Attacking.");*/
                    manageCombatAbilities();
                    return random.nextLong(1000, 1500);
                }
            } else {
                /*log("[Combat] Single-target handling. Attacking.");*/
                manageCombatAbilities();
                return random.nextLong(1000, 1500);
            }
        } else {
            log("[Combat] No target. Finding nearest monster.");
            return attackNearestMonster(player);
        }
    }

    private static long findAndAttackNewTarget(LocalPlayer player) {
        Npc newTarget = findTarget(player);
        if (newTarget == null) {
            return logAndDelay("[Error][MultiTarget] No valid NPC target found.", 1000, 3000);
        }

        return attackMonster(player, newTarget);
    }

    private static long attackNearestMonster(LocalPlayer player) {
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
        boolean attack = monster.interact("Attack");
        if (attack) {
            recentlyAttackedTargets.remove(monster.getId());
            return logAndDelay("[Combat] Successfully attacked " + monster.getName() + ".", 400, 500);
        } else {
            return logAndDelay("[Error] Failed to attack " + monster.getName(), 1500, 3000);
        }
    }

    public static long logAndDelay(String message, int minDelay, int maxDelay) {
        log(message);
        long delay = random.nextLong(minDelay, maxDelay);
        Execution.delay(delay);
        return delay;
    }
}
