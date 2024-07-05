package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.botwithus.Combat.Books.*;
import static net.botwithus.Combat.Food.eatFood;
import static net.botwithus.Combat.Loot.*;
import static net.botwithus.Combat.NPCs.updateNpcTableData;
import static net.botwithus.Combat.Notepaper.useItemOnNotepaper;
import static net.botwithus.Combat.Potions.*;
import static net.botwithus.Combat.Prayers.*;
import static net.botwithus.Combat.Radius.enableRadiusTracking;
import static net.botwithus.Combat.Radius.ensureWithinRadius;
import static net.botwithus.Combat.Travel.useTraveltoLocation;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.TaskScheduler.shutdown;
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

        if (player == null || useTraveltoLocation) {
            return random.nextLong(600, 650);
        }
        teleportOnHealth();

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
            return random.nextLong(600, 650);
        }

        if (!player.hasTarget()) {
            handleCombat(player);
        } else {
            manageCombatAbilities();
        }


        return 0;
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
                logAndDelay("[Combat] Successfully attacked " + monster.getName() + ".", 200, 300);
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

    private static Map<String, AbilityIndex> abilityIndices = new HashMap<>();

    private static class AbilityIndex {
        private final int interfaceIndex;
        private final int componentIndex;

        public AbilityIndex(int interfaceIndex, int componentIndex) {
            this.interfaceIndex = interfaceIndex;
            this.componentIndex = componentIndex;
        }

        public int interfaceIndex() {
            return interfaceIndex;
        }

        public int componentIndex() {
            return componentIndex;
        }
    }


    public static void setup(String abilityName) {
        int spriteID = ActionBar.getActionSprite(ActionBar.getActionStruct(abilityName).getParams());

        Component component = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673)
                .spriteId(spriteID)
                .option("Customise-keybind")
                .results()
                .first();

        if (component != null) {
            int interfaceIndex = component.getInterfaceIndex();
            int componentIndex = component.getComponentIndex();
            abilityIndices.put(abilityName, new AbilityIndex(interfaceIndex, componentIndex));
        }
    }

    public static void interactWithAbility(String abilityName) {
        AbilityIndex index = abilityIndices.get(abilityName);
        if (index != null) {
            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, (index.interfaceIndex() << 16 | index.componentIndex()));
        } else {
            log("Ability not found in the map: " + abilityName);
        }
    }


    public static void manageCombatAbilities() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }

        if (useVolleyofSouls) {
            setup("Volley of Souls");
            volleyOfSouls();
        }
        if (useThreadsofFate) {
            setup("Threads of Fate");
            manageThreadsOfFate();
        }
        if (useConjureUndeadArmy) {
            setup("Conjure Undead Army");
            keepArmyUp();
        }
        if (useEssenceofFinality) {
            setup("Essence of Finality");
            essenceOfFinality();
        }
        if (useWeaponSpecialAttack) {
            setup("Weapon Special Attack");
            DeathEssence();
        }
        if (useInvokeDeath) {
            setup("Invoke Death");
            invokeDeath();
        }
        if (useDarkness) {
            setup("Darkness");
            manageDarkness();
        }
        if (useAnimateDead) {
            setup("Animate Dead");
            manageAnimateDead();
        }
        if (useVulnerabilityBomb) {
            vulnerabilityBomb();
        }
        if (useElvenRitual) {
            activateElvenRitual();
        }
        if (useExcalibur) {
            activateExcalibur();
        }
        if (useUndeadSlayer) {
            activateUndeadSlayer();
        }
        if (useDragonSlayer) {
            activateDragonSlayer();
        }
        if (useDemonSlayer) {
            activateDemonSlayer();
        }

    }

    public static void volleyOfSouls() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        int currentResidualSouls = VarManager.getVarValue(VarDomainType.PLAYER, 11035);

        if (currentResidualSouls >= VolleyOfSoulsThreshold
                && player.hasTarget()
                && player.inCombat()) {

            currentResidualSouls = VarManager.getVarValue(VarDomainType.PLAYER, 11035);
            interactWithAbility("Volley of Souls");
            int finalCurrentResidualSouls = currentResidualSouls;
            boolean effectConfirmed = Execution.delayUntil(random.nextLong(5000, 6500), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11035) < finalCurrentResidualSouls);

            if (effectConfirmed) {
                log("[Success] Volley of Souls effect confirmed with " + currentResidualSouls + " residual souls.");
            } else {
                log("[Error] Volley of Souls effect not confirmed.");
            }
        }
    }

    public static void manageThreadsOfFate() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.getCooldownPrecise("Threads of Fate") == 0) {

            interactWithAbility("Threads of Fate");

            boolean effectConfirmed = Execution.delayUntil(random.nextLong(5000, 6500), () -> ActionBar.getCooldownPrecise("Threads of Fate") != 0);

            if (effectConfirmed) {
                log("[Success] Threads of Fate effect confirmed.");
            } else {
                log("[Error] Threads of Fate effect not confirmed.");
            }
        }
    }

    public static void keepArmyUp() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (VarManager.getVarValue(VarDomainType.PLAYER, 11018) == 0) {

            interactWithAbility("Conjure Undead Army");

            boolean effectConfirmed = Execution.delayUntil(random.nextLong(5000, 6500), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11018) != 0);

            if (effectConfirmed) {
                log("[Success] Conjure Undead Army effect confirmed.");
            } else {
                log("[Error] Conjure Undead Army effect not confirmed.");
            }
        }
    }


    public static void essenceOfFinality() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        int currentNecrosisStacks = VarManager.getVarValue(VarDomainType.PLAYER, 10986);

        if (player.getAdrenaline() > 250
                && ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty()
                && player.inCombat()
                && currentNecrosisStacks >= NecrosisStacksThreshold) {

            interactWithAbility("Essence of Finality");
            boolean effectConfirmed = Execution.delayUntil(random.nextLong(5500, 6500), () -> VarManager.getVarValue(VarDomainType.PLAYER, 10986) != currentNecrosisStacks);
            if (effectConfirmed) {
                log("[Success] Essence of Finality effect confirmed with " + currentNecrosisStacks + " Necrosis stacks.");
            } else {
                log("[Error] Essence of Finality effect not confirmed.");
            }
        }
    }


    public static void DeathEssence() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (player.getAdrenaline() > 300
                && player.getFollowing() != null
                && player.getFollowing().getCurrentHealth() > 500
                && ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty()
                && player.inCombat()) {

            interactWithAbility("Weapon Special Attack");

            boolean abilityEffect = Execution.delayUntil(random.nextLong(5000, 6500), () -> !ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty());
            if (abilityEffect) {
                log("[Success] Weapon Special Attack effect confirmed.");
            } else {
                log("[Error] Weapon Special Attack effect not confirmed.");
            }
        }
    }


    public static void invokeDeath() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (VarManager.getVarbitValue(53247) == 0
                && player.inCombat()
                && ComponentQuery.newQuery(284).spriteId(30100).results().isEmpty()
                && ActionBar.getCooldown("Invoke Death") == 0
                && player.hasTarget()
                && player.getFollowing() != null
                && player.getFollowing().getCurrentHealth() > 5000) {

            interactWithAbility("Invoke Death");

            boolean effectConfirmed = Execution.delayUntil(random.nextLong(5000, 6500), () -> VarManager.getVarbitValue(53247) == 1);

            if (effectConfirmed) {
                log("[Success] Invoke Death effect confirmed.");
            } else {
                log("[Error] Invoke Death effect not confirmed.");
            }
        }
    }

    public static void manageAnimateDead() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ComponentQuery.newQuery(284).spriteId(14764).results().isEmpty()) {

            interactWithAbility("Animate Dead");

            boolean abilityEffect = Execution.delayUntil(random.nextLong(5000, 6500), () -> !ComponentQuery.newQuery(284).spriteId(14764).results().isEmpty());
            if (abilityEffect) {
                log("[Success] Animate Dead effect confirmed.");
            } else {
                log("[Error] Animate Dead effect not confirmed.");
            }
        }
    }


    public static void manageDarkness() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (VarManager.getVarValue(VarDomainType.PLAYER, 11074) == 0) {

            interactWithAbility("Darkness");

            boolean effectConfirmed = Execution.delayUntil(random.nextLong(3000, 5000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11074) != 0);

            if (effectConfirmed) {
                log("[Success] Darkness effect confirmed.");
            } else {
                log("[Error] Darkness effect not confirmed.");
            }
        }
    }


    public static void vulnerabilityBomb() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
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

    public static void activateElvenRitual() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (player.getPrayerPoints() < prayerPointsThreshold && Backpack.contains("Ancient elven ritual shard")) {

            Component elvenRitual = ComponentQuery.newQuery(291).spriteId(43358).results().first();
            if (elvenRitual != null) {

                boolean success = backpack.interact("Ancient elven ritual shard", "Activate");
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
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ComponentQuery.newQuery(291).spriteId(14632).results().isEmpty()) {
            if (player.getCurrentHealth() * 100 / player.getMaximumHealth() >= healthPointsThreshold) {

                ResultSet<net.botwithus.rs3.game.Item> items = InventoryItemQuery.newQuery().results();
                Item excaliburItem = items.stream()
                        .filter(item -> item.getName() != null && item.getName().toLowerCase().contains("excalibur"))
                        .findFirst()
                        .orElse(null);

                if (excaliburItem == null) {
                    log("No Excalibur found!");
                } else {

                    boolean success = backpack.interact(excaliburItem.getName(), "Activate");
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

            if (isTimeLessThanFiveMinutes(text) || components.isEmpty() || ComponentQuery.newQuery(284).spriteId(2).results().isEmpty()) {
                log("[Combat] Interacting with Siege Engine.");
                EntityResultSet<SceneObject> siegeEngine = SceneObjectQuery.newQuery().name("Dwarven siege engine").results();
                if (!siegeEngine.isEmpty()) {
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
            printSiegeEngineRemainingTime();
        } else {
            log("[Combat] No Dwarven siege engine found.");
        }
    }
    public static void activateUndeadSlayer() {
        if (ActionBar.containsAbility("Undead Slayer") && ActionBar.getCooldownPrecise("Undead Slayer") == 0) {
            interactWithAbility("Undead Slayer");
            log("[Success] Activated Undead Slayer.");
            Execution.delay(random.nextLong(1900, 2000));
        } else {
            log("[Error] Failed to activate Undead Slayer.");
        }
    }

    public static void activateDragonSlayer() {
        if (ActionBar.containsAbility("Dragon Slayer") && ActionBar.getCooldownPrecise("Dragon Slayer") == 0) {
            interactWithAbility("Dragon Slayer");
            log("[Success] Activated Dragon Slayer.");
            Execution.delay(random.nextLong(1900, 2000));
        } else {
            log("[Error] Failed to activate Dragon Slayer.");
        }
    }

    public static void activateDemonSlayer() {
        if (ActionBar.containsAbility("Demon Slayer") && ActionBar.getCooldownPrecise("Demon Slayer") == 0) {
            interactWithAbility("Demon Slayer");
            log("[Success] Activated Demon Slayer.");
            Execution.delay(random.nextLong(1900, 2000));
        } else {
            log("[Error] Failed to activate Demon Slayer.");
        }
    }


}
