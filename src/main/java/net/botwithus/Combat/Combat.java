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
import static net.botwithus.Combat.Loot.*;
import static net.botwithus.Combat.Notepaper.useItemOnNotepaper;
import static net.botwithus.Combat.Potions.managePotions;
import static net.botwithus.Combat.Prayers.manageQuickPrayers;
import static net.botwithus.Combat.Prayers.manageSoulSplit;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;

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
            return random.nextLong(500, 750);
        }

        if (useLoot || useNotepaper || lootNoted || isStackable) {
            if (useNotepaper) {
                useItemOnNotepaper();
            }
            if (useLoot) {
                processLooting();
            }
            if (lootNoted) {
                lootNotedItemsFromInventory();
            }
            /*if (isStackable) {
                lootStackableItemsFromInventory();
            }*/
        }

        if (SoulSplit) {
            Execution.delay(manageSoulSplit(player));
        }

        if (usequickPrayers) {
            manageQuickPrayers(player);
        }

        managePotions(player);
        manageScripturesAndScrimshaws(player);
        manageCombatAbilities();

        if (isHealthLow(player) && shouldEatFood) {
            eatFood(player);
            return logAndDelay("[Error] Health is low, won't attack until more health.", 1000, 3000);
        }
        if (handleMultitarget) {
            if (System.currentTimeMillis() - lastClearTime > random.nextLong(5000, 7500)) {
                recentlyAttackedTargets.clear();
                lastClearTime = System.currentTimeMillis();
            }
        }

        if (player.hasTarget()) {
            PathingEntity<?> target = player.getTarget();
            if (!handleMultitarget) {
                return random.nextLong(100, 200);
            } else {
                if (target.getCurrentHealth() > target.getMaximumHealth() * healthThreshold) {
                    return random.nextLong(100, 200);
                } else {
                    Npc newTarget = findDifferentTarget(player, target.getId());
                    if (newTarget != null) {
                        return attackMonster(player, newTarget);
                    } else {
                        return logAndDelay("[Combat] No new target found.", 100, 200);
                    }
                }
            }
        }

        return attackNearestMonster(player);
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
            if (handleMultitarget) {
                recentlyAttackedTargets.add(monster.getId());
            }
            return logAndDelay("[Combat] Successfully attacked " + monster.getName() + ".", 300, 400);
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
