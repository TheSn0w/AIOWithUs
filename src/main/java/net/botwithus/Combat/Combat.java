package net.botwithus.Combat;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.PathingEntity;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.botwithus.Combat.Books.*;
import static net.botwithus.Combat.CombatManager.*;
import static net.botwithus.Combat.CombatManager.DeathEssence;
import static net.botwithus.Combat.Familiar.summonFamiliar;
import static net.botwithus.Combat.Familiar.useFamiliarForCombat;
import static net.botwithus.Combat.Food.eatFood;
import static net.botwithus.Combat.LootManager.*;
import static net.botwithus.Combat.Potions.*;
import static net.botwithus.Combat.Prayers.*;
import static net.botwithus.Combat.Radius.enableRadiusTracking;
import static net.botwithus.Combat.Radius.ensureWithinRadius;
import static net.botwithus.Combat.Travel.useHintArrow;
import static net.botwithus.Combat.Travel.useTraveltoLocation;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Combat {
    private SnowsScript snowsScript;

    public Combat(SnowsScript snowsScript) {
        this.snowsScript = snowsScript;
    }
    public void manageCombatAbilities() {
        while (snowsScript.isActive()) {

            LocalPlayer player = getLocalPlayer();
            if (player == null) {
                return;
            }
            if (SoulSplit && VarManager.getVarbitValue(16779) == 0 && (player.inCombat() || player.hasTarget() || player.getStanceId() == 2687)) {
                activateSoulSplit(player);
            }
            if (SoulSplit && VarManager.getVarbitValue(16779) == 1 && !player.inCombat()) {
                deactivateSoulSplit();
            }
            if (usequickPrayers) {
                updateQuickPrayersActiveStatus();
                if (!quickPrayersActive && (player.inCombat() || player.hasTarget() || player.getStanceId() == 2687)) {
                    activateQuickPrayers();
                } else {
                    if (quickPrayersActive && !player.inCombat()) {
                        deactivateQuickPrayers();
                    }
                }
            }
            managePotions(player);
            manageScripturesAndScrimshaws(player);

            if (player.hasTarget() && player.inCombat()) {
                // Backpack
                if (useVulnerabilityBomb) {
                    vulnerabilityBomb();
                }
                // Backpack
                if (useElvenRitual) {
                    activateElvenRitual();
                }
                // Backpack
                if (useExcalibur) {
                    activateExcalibur();
                }
                if (useUndeadSlayer) {
                    setup("Undead Slayer");
                    activateUndeadSlayer();
                }
                if (useDragonSlayer) {
                    setup("Dragon Slayer");
                    activateDragonSlayer();
                }
                if (useDemonSlayer) {
                    setup("Demon Slayer");
                    activateDemonSlayer();
                }
                if (useDarkness) {
                    setup("Darkness");
                    manageDarkness();
                }
                if (useAnimateDead) {
                    setup("Animate Dead");
                    manageAnimateDead();
                }
                if (useConjureUndeadArmy) {
                    setup("Conjure Undead Army");
                    keepArmyUp();
                }
                if (useThreadsofFate) {
                    setup("Threads of Fate");
                    manageThreadsOfFate();
                }
                if (useInvokeDeath) {
                    setup("Invoke Death");
                    invokeDeath();
                }
                if (useVolleyofSouls) {
                    setup("Volley of Souls");
                    volleyOfSouls();
                }
                if (useEssenceofFinality) {
                    setup("Essence of Finality");
                    essenceOfFinality();
                }
                if (useWeaponSpecialAttack) {
                    setup("Weapon Special Attack");
                    DeathEssence();
                }
                try {
                    Thread.sleep(random.nextLong(800, 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }


    public static double healthThreshold = 0.4;
    public static double getHealthThreshold() {
        return healthThreshold;
    }

    public static void setHealthThreshold(double healthThreshold) {
        Combat.healthThreshold = healthThreshold;
    }

    public static long attackTarget(LocalPlayer player) {
        if (player == null || useTraveltoLocation || useHintArrow) {
            return random.nextLong(600, 650);
        }
        // if Loot Inventory is not open, interact with ground items
        if (useCustomLoot) {
            useCustomLootFromGround();
        }
        if (useLootAllNotedItems) {
            useNotedLootFromGround();
        }
        if (useLootEverything) {
            useLootInventoryPickup();
        }

        handleMultitarget();

        if (useFamiliarForCombat) {
            summonFamiliar();
        }
        if (enableRadiusTracking) {
            ensureWithinRadius(player);
        }
        if (shouldEatFood) {
            eatFood(player);
        }

        // Handle specific item uses
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
        if (useDwarfcannon) {
            dwarvenSiegeCannon();
        }

        // Handle specific creature types
        if (player.isMoving()) {
            return random.nextLong(600, 650);
        }
        if (lavaStrykewyrms) {
            return lavaStrykewyrms();
        }
        if (iceStrykewyrms) {
            return iceStrykewyrms();
        }

        if (!player.hasTarget()) {
            handleCombat(player);
        }

        if (player.hasTarget()) {

            PathingEntity<?> target = player.getTarget();

            if (handleMultitarget) {
                if (target.getCurrentHealth() <= target.getMaximumHealth() * healthThreshold) {
                    Npc newTarget = findDifferentTarget(player, target.getId());
                    if (newTarget != null) {
                        return attackMonster(player, newTarget);
                    } else {
                        return random.nextLong(100, 200);
                    }
                } else {
                    return random.nextLong(100, 200);
                }
            }
        }

        return random.nextLong(100, 200);
    }



    private static long lavaStrykewyrms() {
    LocalPlayer player = getLocalPlayer();
    if (player == null || (player.hasTarget() && player.getFollowing() != null)) {
        return random.nextLong(400, 600);
    }
    EntityResultSet<Npc> mounds = NpcQuery.newQuery().byType(2417).option("Investigate").results();
    Npc strykewyrm = NpcQuery.newQuery().byType(20630).results().nearestTo(player);

    if (strykewyrm != null && strykewyrm.getCurrentHealth() > 0) {
        log("[Lava] Strykewyrm is being followed by the player and has health greater than 0.");
        strykewyrm.interact("Attack");
        Execution.delay(random.nextLong(1000, 2000));
        return 0;
    }

    if (!mounds.isEmpty()) {
        Npc nearestMound = mounds.nearest();
        log("[Lava] Interacting with the nearest mound.");
        nearestMound.interact("Investigate");
        if (nearestMound.getCoordinate().distanceTo(player.getCoordinate()) > 15.0D && ActionBar.containsAbility("Surge") && ActionBar.getCooldown("Surge") == 0) {
            Execution.delay(random.nextLong(600, 700));
            log("[Lava] Used Surge: " + ActionBar.useAbility("Surge"));
            Execution.delay(random.nextLong(200, 250));
            nearestMound.interact("Investigate");
        }
        return random.nextLong(1000, 2000);
    } else {
        log("[Lava] No Nearby Mounds available.");
    }
    return 0;
}

    private static long iceStrykewyrms() {
        LocalPlayer player = getLocalPlayer();
        if (player == null || (player.hasTarget() && player.getFollowing() != null)) {
            return random.nextLong(400, 600);
        }
        EntityResultSet<Npc> mounds = NpcQuery.newQuery().byType(9462).option("Investigate").results();
        Npc strykewyrm = NpcQuery.newQuery().byType(9463).results().nearestTo(player);

        if (strykewyrm != null && strykewyrm.getCurrentHealth() > 0) {
            log("[Ice] Strykewyrm is being followed by the player and has health greater than 0.");
            strykewyrm.interact("Attack");
            Execution.delay(random.nextLong(1000, 2000));
            return 0;
        }

        if (!mounds.isEmpty()) {
            Npc nearestMound = mounds.nearest();
            log("[Ice] Interacting with the nearest mound.");
            nearestMound.interact("Investigate");
            if (nearestMound.getCoordinate().distanceTo(player.getCoordinate()) > 15.0D && ActionBar.containsAbility("Surge") && ActionBar.getCooldown("Surge") == 0) {
                Execution.delay(random.nextLong(600, 700));
                log("[Ice] Used Surge: " + ActionBar.useAbility("Surge"));
                Execution.delay(random.nextLong(200, 250));
                nearestMound.interact("Investigate");
            }
            return random.nextLong(1000, 2000);
        } else {
            log("[Ice] No Nearby Mounds available.");
        }
        return 0;
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

    private static long attackMonster(LocalPlayer player, Npc monster) {
        boolean attack = monster.interact("Attack");
        log("[MultiTarget] Attacking " + monster.getName() + "...");
        Execution.delay(random.nextLong(500, 700));
        if (attack) {
            if (handleMultitarget) {
                recentlyAttackedTargets.add(monster.getId());
            }
        }
        return random.nextLong(random.nextLong(1000, 1500));
    }

    private static void handleMultitarget() {
        if (handleMultitarget) {
            if (System.currentTimeMillis() - lastClearTime > random.nextLong(5000)) {
                recentlyAttackedTargets.clear();
                lastClearTime = System.currentTimeMillis();
            }
        }
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
                logAndDelay("[Combat] Successfully attacked " + monster.getName() + ".", 500, 700);
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

    private static void teleportOnHealth() {
        LocalPlayer player = getLocalPlayer();
        if (player != null && player.getCurrentHealth() < player.getMaximumHealth() * 0.10) {
            if (ActionBar.containsAbility("Max guild Teleport")) {
                ActionBar.useAbility("Max guild Teleport");
                log("[Combat] Health is below 7.5% so we are teleporting to Max Guild.");
            } else if (ActionBar.containsAbility("War's Retreat Teleport")) {
                ActionBar.useAbility("War's Retreat Teleport");
                log("[Combat] Health is below 7.5% so we are teleporting to War's Retreat.");
            }
            Execution.delay(random.nextLong(10000, 20000));
            shutdown();
        }
    }



    public static void printSiegeEngineRemainingTime() {
        AtomicReference<ResultSet<Component>> components = new AtomicReference<>(ComponentQuery.newQuery(291).spriteId(2).results());
        Component component = components.get().first();

        if (component != null) {
            int interfaceIndex = component.getInterfaceIndex();
            int componentIndex = component.getComponentIndex();
            int subComponentIndex = component.getSubComponentIndex() + 1;

            ResultSet<Component> targetComponents = ComponentQuery.newQuery(interfaceIndex)
                    .componentIndex(componentIndex)
                    .subComponentIndex(subComponentIndex)
                    .results();

            Component targetComponent = targetComponents.first();
            String text = targetComponent.getText();

            if (isTimeLessThanFiveMinutes(text) || components.get().isEmpty()) {
                interactWithSiegeEngine();
            } else if (ComponentQuery.newQuery(284).spriteId(2).results().isEmpty()) {
                Thread.ofVirtual().start(() -> {
                    try {
                        Thread.sleep(5000);
                        // Requery for the component
                        components.set(ComponentQuery.newQuery(284).spriteId(2).results());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                if (components.get().isEmpty()) {
                    interactWithSiegeEngine();
                }
            }
        }
    }

    private static void interactWithSiegeEngine() {
        EntityResultSet<SceneObject> siegeEngine = SceneObjectQuery.newQuery().name("Dwarven siege engine").results();
        if (!siegeEngine.isEmpty() && Backpack.contains("Cannonball")) {
            log("[Combat] Interacting with Siege Engine.");
            siegeEngine.first().interact("Fire");
            Execution.delay(random.nextLong(750, 1250));
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
                    return value < 20;
                case "h":
                    return false;
            }
        }
        return false;
    }

    public static void dwarvenSiegeCannon() {
        EntityResultSet<SceneObject> siegeEngine = SceneObjectQuery.newQuery().name("Dwarven siege engine").option("Fire").results();
        if (!siegeEngine.isEmpty()) {
            if (Backpack.contains("Cannonball")) {
                printSiegeEngineRemainingTime();
            } else {
                log("[Combat] No Cannonball found in Backpack.");
                siegeEngine.first().interact("Pick up");
                log("[Combat] Picking up Cannonball from Siege Engine.");
                Execution.delay(random.nextLong(2500, 3500));
                useDwarfcannon = false;
            }
        } else {
            log("[Combat] No Dwarven siege engine found.");
        }
    }




}
