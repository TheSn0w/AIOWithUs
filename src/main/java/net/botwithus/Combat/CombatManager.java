package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.random;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class CombatManager {

    public static boolean useUndeadSlayer = false;
    public static boolean useDragonSlayer = false;
    public static boolean useDemonSlayer = false;

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

    private static final BlockingQueue<Runnable> abilityQueue = new LinkedBlockingQueue<>();
    private static final AtomicBoolean isProcessingAbilities = new AtomicBoolean(false);

    public static void startAbilityManager() {
        if (!isProcessingAbilities.get()) {
            isProcessingAbilities.set(true);
            Thread.ofVirtual().name("AbilityManagerThread").start(() -> {
                while (true) {
                    try {
                        Runnable abilityTask = abilityQueue.take();
                        abilityTask.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } finally {
                        isProcessingAbilities.set(false);
                    }
                }
            });
        }
    }

    public static void queueAbility(Runnable abilityTask) {
        abilityQueue.offer(abilityTask);
    }

    public static void interactWithAbility(String abilityName) {
        queueAbility(() -> {
            AbilityIndex index = abilityIndices.get(abilityName);
            if (index != null) {
                log("Interacting with ability: " + abilityName);
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, (index.interfaceIndex() << 16 | index.componentIndex()));
            } else {
                log("Ability not found in the map: " + abilityName);
            }
        });
    }

    public static void manageCombatAbilities() {

        startAbilityManager();

        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
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
    }

    private static volatile boolean isInvokingDeath = false;

    public static void invokeDeath() {
        if (!isInvokingDeath) {
            queueAbility(() -> {
                isInvokingDeath = true;
                try {
                    LocalPlayer player = getLocalPlayer();
                    if (player == null) {
                        return;
                    }
                    if (!isInvokingDeath && VarManager.getVarbitValue(53247) == 0
                            && player.inCombat()
                            && ComponentQuery.newQuery(284).spriteId(30100).results().isEmpty()
                            && ActionBar.getCooldownPrecise("Invoke Death") == 0
                            && player.hasTarget()
                            && player.getFollowing() != null
                            && player.getFollowing().getCurrentHealth() > 5000) {
                        interactWithAbility("Invoke Death");
                        boolean effectConfirmed = Execution.delayUntil(random.nextLong(4000, 5000), () -> VarManager.getVarbitValue(53247) == 1);
                        if (effectConfirmed) {
                            log("[Success] Invoke Death effect confirmed.");
                        }
                    }
                } finally {
                    isInvokingDeath = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingVolleyOfSouls = false;

    public static void volleyOfSouls() {
        if (!isInvokingVolleyOfSouls) {
            queueAbility(() -> {
                isInvokingVolleyOfSouls = true;
                try {
                    LocalPlayer player = getLocalPlayer();
                    if (player == null) {
                        return;
                    }
                    AtomicInteger currentResidualSouls = new AtomicInteger(VarManager.getVarValue(VarDomainType.PLAYER, 11035));

                    if (currentResidualSouls.get() >= VolleyOfSoulsThreshold && player.hasTarget() && player.inCombat()) {
                        currentResidualSouls.set(VarManager.getVarValue(VarDomainType.PLAYER, 11035));
                        interactWithAbility("Volley of Souls");
                        int finalCurrentResidualSouls = currentResidualSouls.get();
                        boolean effectConfirmed = Execution.delayUntil(random.nextLong(4000, 5000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11035) < finalCurrentResidualSouls);
                        if (effectConfirmed) {
                            log("[Success] Volley of Souls effect confirmed with " + currentResidualSouls.get() + " residual souls.");
                        }
                    }
                } finally {
                    isInvokingVolleyOfSouls = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingThreadsOfFate = false;

    public static void manageThreadsOfFate() {
        if (!isInvokingThreadsOfFate) {
            queueAbility(() -> {
                isInvokingThreadsOfFate = true;
                try {
                    if (ActionBar.getCooldownPrecise("Threads of Fate") == 0) {
                        interactWithAbility("Threads of Fate");
                        boolean effectConfirmed = Execution.delayUntil(random.nextLong(2000, 3000), () -> ActionBar.getCooldownPrecise("Threads of Fate") != 0);
                        if (effectConfirmed) {
                            log("[Success] Threads of Fate effect confirmed.");
                        }
                    }
                } finally {
                    isInvokingThreadsOfFate = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingKeepArmyUp = false;

    public static void keepArmyUp() {
        if (!isInvokingKeepArmyUp) {
            queueAbility(() -> {
                isInvokingKeepArmyUp = true;
                try {
                    LocalPlayer player = getLocalPlayer();
                    if (player == null) {
                        return;
                    }
                    if (VarManager.getVarValue(VarDomainType.PLAYER, 11018) == 0) {
                        interactWithAbility("Conjure Undead Army");
                        boolean effectConfirmed = Execution.delayUntil(random.nextLong(2000, 3000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11018) != 0);
                        if (effectConfirmed) {
                            log("[Success] Conjure Undead Army effect confirmed.");
                        }
                    }
                } finally {
                    isInvokingKeepArmyUp = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingEssenceOfFinality = false;

    public static void essenceOfFinality() {
        if (!isInvokingEssenceOfFinality) {
            queueAbility(() -> {
                isInvokingEssenceOfFinality = true;
                try {
                    LocalPlayer player = getLocalPlayer();
                    if (player == null) {
                        return;
                    }
                    AtomicInteger currentNecrosisStacks = new AtomicInteger(VarManager.getVarValue(VarDomainType.PLAYER, 10986));

                    if (player.getAdrenaline() > 250
                            && ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty()
                            && player.inCombat()
                            && currentNecrosisStacks.get() >= NecrosisStacksThreshold) {
                        interactWithAbility("Essence of Finality");
                        int finalCurrentNecrosisStacks = currentNecrosisStacks.get();
                        boolean effectConfirmed = Execution.delayUntil(random.nextLong(2000, 3000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 10986) != finalCurrentNecrosisStacks);
                        if (effectConfirmed) {
                            log("[Success] Essence of Finality effect confirmed with " + currentNecrosisStacks.get() + " Necrosis stacks.");
                        }
                    }
                } finally {
                    isInvokingEssenceOfFinality = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingDeathEssence = false;

    public static void DeathEssence() {
        if (!isInvokingDeathEssence) {
            queueAbility(() -> {
                isInvokingDeathEssence = true;
                try {
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
                        boolean abilityEffect = Execution.delayUntil(random.nextLong(2000, 3000), () -> !ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty());
                        if (abilityEffect) {
                            log("[Success] Weapon Special Attack effect confirmed.");
                        }
                    }
                } finally {
                    isInvokingDeathEssence = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingAnimateDead = false;

    public static void manageAnimateDead() {
        if (!isInvokingAnimateDead) {
            queueAbility(() -> {
                isInvokingAnimateDead = true;
                try {
                    LocalPlayer player = getLocalPlayer();
                    if (player == null) {
                        return;
                    }
                    if (ComponentQuery.newQuery(284).spriteId(14764).results().isEmpty()) {
                        interactWithAbility("Animate Dead");
                        boolean abilityEffect = Execution.delayUntil(random.nextLong(2000, 3000), () -> !ComponentQuery.newQuery(284).spriteId(14764).results().isEmpty());
                        if (abilityEffect) {
                            log("[Success] Animate Dead effect confirmed.");
                        }
                    }
                } finally {
                    isInvokingAnimateDead = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingDarkness = false;

    public static void manageDarkness() {
        if (!isInvokingDarkness) {
            queueAbility(() -> {
                isInvokingDarkness = true;
                try {
                    LocalPlayer player = getLocalPlayer();
                    if (player == null) {
                        return;
                    }
                    if (VarManager.getVarValue(VarDomainType.PLAYER, 11074) == 0) {
                        interactWithAbility("Darkness");
                        boolean effectConfirmed = Execution.delayUntil(random.nextLong(2000, 3000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11074) != 0);
                        if (effectConfirmed) {
                            log("[Success] Darkness effect confirmed.");
                        }
                    }
                } finally {
                    isInvokingDarkness = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingUndeadSlayer = false;

    public static void activateUndeadSlayer() {
        if (!isInvokingUndeadSlayer) {
            queueAbility(() -> {
                isInvokingUndeadSlayer = true;
                try {
                    if (ActionBar.containsAbility("Undead Slayer") && ActionBar.getCooldownPrecise("Undead Slayer") == 0) {
                        interactWithAbility("Undead Slayer");
                        log("[Success] Activated Undead Slayer.");
                        Execution.delay(random.nextLong(1900, 2000));
                    } else {
                        log("[[Caution] ] Failed to activate Undead Slayer.");
                    }
                } finally {
                    isInvokingUndeadSlayer = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingDragonSlayer = false;

    public static void activateDragonSlayer() {
        if (!isInvokingDragonSlayer) {
            queueAbility(() -> {
                isInvokingDragonSlayer = true;
                try {
                    if (ActionBar.containsAbility("Dragon Slayer") && ActionBar.getCooldownPrecise("Dragon Slayer") == 0) {
                        interactWithAbility("Dragon Slayer");
                        log("[Success] Activated Dragon Slayer.");
                        Execution.delay(random.nextLong(1900, 2000));
                    } else {
                        log("[[Caution] ] Failed to activate Dragon Slayer.");
                    }
                } finally {
                    isInvokingDragonSlayer = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingDemonSlayer = false;

    public static void activateDemonSlayer() {
        if (!isInvokingDemonSlayer) {
            queueAbility(() -> {
                isInvokingDemonSlayer = true;
                try {
                    if (ActionBar.containsAbility("Demon Slayer") && ActionBar.getCooldownPrecise("Demon Slayer") == 0) {
                        interactWithAbility("Demon Slayer");
                        log("[Success] Activated Demon Slayer.");
                        Execution.delay(random.nextLong(1900, 2000));
                    } else {
                        log("[[Caution] ] Failed to activate Demon Slayer.");
                    }
                } finally {
                    isInvokingDemonSlayer = false;
                }
            });
        }
    }
    private static volatile boolean isInvokingVulnerabilityBomb = false;

    public static void vulnerabilityBomb() {
        if (!isInvokingVulnerabilityBomb) {
            queueAbility(() -> {
                isInvokingVulnerabilityBomb = true;
                try {
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
                            log("[Caution] Failed to use Vulnerability bomb!");
                        }
                    }
                } finally {
                    isInvokingVulnerabilityBomb = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingElvenRitual = false;

    public static void activateElvenRitual() {
        if (!isInvokingElvenRitual) {
            queueAbility(() -> {
                isInvokingElvenRitual = true;
                try {
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
                                log("[Caution] Failed to activate Elven Ritual Shard.");
                            }
                        }
                    }
                } finally {
                    isInvokingElvenRitual = false;
                }
            });
        }
    }

    private static volatile boolean isInvokingExcalibur = false;

    private static void activateExcalibur() {
        if (!isInvokingExcalibur) {
            queueAbility(() -> {
                isInvokingExcalibur = true;
                try {
                    LocalPlayer player = getLocalPlayer();
                    if (player == null) {
                        return;
                    }
                    if (ComponentQuery.newQuery(291).spriteId(14632).results().isEmpty()) {
                        if (player.getCurrentHealth() * 100 / player.getMaximumHealth() <= healthPointsThreshold) {
                            ResultSet<net.botwithus.rs3.game.Item> items = InventoryItemQuery.newQuery().results();
                            Item excaliburItem = items.stream()
                                    .filter(item -> item.getName() != null && item.getName().toLowerCase().contains("excalibur"))
                                    .findFirst()
                                    .orElse(null);

                            if (excaliburItem == null) {
                                log("[Error] No Excalibur found!");
                            } else {
                                boolean success = backpack.interact(excaliburItem.getName(), "Activate");
                                if (success) {
                                    log("Activating " + excaliburItem.getName());
                                    Execution.delay(random.nextLong(1900, 2000));
                                } else {
                                    log("[Caution] Failed to activate Excalibur.");
                                }
                            }
                        }
                    }
                } finally {
                    isInvokingExcalibur = false;
                }
            });
        }
    }
}
