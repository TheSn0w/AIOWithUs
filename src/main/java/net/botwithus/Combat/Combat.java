package net.botwithus.Combat;

import net.botwithus.Slayer.NPCs;
import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
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
import static net.botwithus.Slayer.Main.doSlayer;
import static net.botwithus.Slayer.NPCs.*;
import static net.botwithus.Slayer.WarsRetreat.camelWarriors;
import static net.botwithus.Slayer.WarsRetreat.slayerPointFarming;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Combat {
    private SnowsScript snowsScript;

    public Combat(SnowsScript snowsScript) {
        this.snowsScript = snowsScript;
    }


    public static boolean useDefensives = false;


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
                if (useDefensives) {
                    useDefensives();
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


    public static double healthThreshold = 0.25;
    public static double getHealthThreshold() {
        return healthThreshold;
    }

    public static void setHealthThreshold(double healthThreshold) {
        Combat.healthThreshold = healthThreshold;
    }

    public static void attackTarget(LocalPlayer player) {
        if (player == null || useTraveltoLocation || useHintArrow) {
            Execution.delay(random.nextLong(600, 650));
            return;
        }

        if (doSlayer && slayerPointFarming && VarManager.getVarValue(VarDomainType.PLAYER, 183) == 0) {
            int varValue = VarManager.getVarValue(VarDomainType.PLAYER, 10077);
            int lastDigit = varValue % 10;
            if (lastDigit >= 0 && lastDigit <= 8) {
                Execution.delay(random.nextLong(600, 650));
                return;
            }
        }
        NpcQuery.newQuery().overheadText("your blacklsited").results().isEmpty();

        if (useCustomLoot) {
            useCustomLootFromGround();
        } else if (useLootAllNotedItems) {
            useNotedLootFromGround();
        } else if (useLootEverything) {
            useLootInventoryPickup();
        } else if (useLootAllStackableItems) {
            lootStackableItemsFromGround();
        }

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

        // If moving, delay and return
        if (player.isMoving()) {
            Execution.delay(random.nextLong(600, 650));
            return;
        }

        // Handle specific creature types
        if (lavaStrykewyrms) {
            lavaStrykewyrms();
            return;
        } else if (iceStrykewyrms) {
            iceStrykewyrms();
            return;
        } else if (camelWarriors) {
            camelWarriors(player);
            return;
        }

        // Combat module
        if (!player.hasTarget()) {
            handleCombat(player);
            Execution.delay(random.nextLong(300, 500));
            return;
        }

        if (player.hasTarget()) {
            PathingEntity<?> target = player.getTarget();

            if (handleMultitarget) {
                if (target.getCurrentHealth() <= target.getMaximumHealth() * healthThreshold) {
                    Npc newTarget = findDifferentTarget(player, target.getId());
                    if (newTarget != null) {
                        attackMonster(player, newTarget);
                        return;
                    }
                }
            }
        } else {
            Execution.delay(random.nextLong(600, 650));
            return;
        }

        Execution.delay(random.nextLong(600, 650));
    }



        private static Npc findDifferentTarget(LocalPlayer player, int currentTargetId) {
        List<String> targetNames = getTargetNames();
        if (targetNames.isEmpty()) {
            log("[Error] No target names specified.");
            return null;
        }

        log("Trying to find a different target.");

        Pattern monsterPattern = generateRegexPattern(targetNames);

        Npc newTarget = NpcQuery.newQuery()
                .name(monsterPattern)
                .health(100, 1_000_000)
                .option("Attack")
                .isReachable()
                .results()
                .stream()
                .filter(npc -> npc.getId() != currentTargetId && npc.getCurrentHealth() > npc.getMaximumHealth() * healthThreshold)
                .min(Comparator.comparingDouble(npc -> npc.distanceTo(player.getCoordinate())))
                .orElse(null);

        if (newTarget == null) {
            newTarget = NpcQuery.newQuery()
                    .name(monsterPattern)
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

    private static void attackMonster(LocalPlayer player, Npc monster) {
        monster.interact("Attack");
        log("[MultiTarget] Attacking " + monster.getName() + "...");
        Execution.delay(random.nextLong(600, 650));
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

        if (nearestMonsterOptional.isEmpty()) {
            nearestMonsterOptional = NpcQuery.newQuery()
                    .name(monsterPattern)
                    .health(100, 1_000_000)
                    .option("Attack")
                    .results()
                    .stream()
                    .min(Comparator.comparingDouble(npc -> npc.getCoordinate().distanceTo(player.getCoordinate())));
        }

        Npc monster = nearestMonsterOptional.orElse(null);
        if (monster != null) {
            boolean attack = monster.interact("Attack");
            if (attack) {
                log("[Combat] Successfully attacked: " + monster.getName());
                Execution.delay(random.nextLong(750, 985));
            }
        } else {
            log("[Combat] No valid target found.");
        }
    }


    public static void printSiegeEngineRemainingTime() {
        ResultSet<Component> components = ComponentQuery.newQuery(291).spriteId(2).results();

        for (Component component : components) {
            int interfaceIndex = component.getInterfaceIndex();
            int componentIndex = component.getComponentIndex();
            int subComponentIndex = component.getSubComponentIndex() + 1;

            ResultSet<Component> targetComponents = ComponentQuery.newQuery(interfaceIndex)
                    .componentIndex(componentIndex)
                    .subComponentIndex(subComponentIndex)
                    .results();

            Component targetComponent = targetComponents.first();
            String text = targetComponent.getText();

            if (isTimeLessThanFiveMinutes(text) || components.isEmpty() || VarManager.getVarValue(VarDomainType.PLAYER, 2735) == 0) {
                log("[Combat] Interacting with Siege Engine.");
                EntityResultSet<SceneObject> siegeEngine = SceneObjectQuery.newQuery().name("Dwarven siege engine").results();
                if (!siegeEngine.isEmpty() && Backpack.contains("Cannonball")) {
                    siegeEngine.first().interact("Fire");
                    Execution.delay(random.nextLong(2500, 3500));
                    break;
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
