package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.LinkedList;
import java.util.Queue;

import static net.botwithus.Combat.Combat.attackNearestMonster;
import static net.botwithus.Combat.Potions.vulnerabilityBomb;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Abilities {
    public static boolean useThreadsofFate = false;
    public static boolean useDarkness = false;
    public static boolean useExcalibur = false;
    public static int NecrosisStacksThreshold = 12;
    public static int VolleyOfSoulsThreshold = 5;
    public static boolean useElvenRitual = false;

    public static void manageCombatAbilities() {
        LocalPlayer player = getLocalPlayer();
        int currentNecrosisStacks = VarManager.getVarValue(VarDomainType.PLAYER, 10986);

        if (DeathGrasp && player.getAdrenaline() >= 250 && ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty() && ActionBar.getCooldownPrecise("Essence of Finality") == 0 && player.hasTarget() && ActionBar.getCooldownPrecise("Essence of Finality") == 0 && ActionBar.containsAbility("Essence of Finality") && currentNecrosisStacks >= NecrosisStacksThreshold) {
            Execution.delay(essenceOfFinality(player));
            return;
        }
        if (InvokeDeath && VarManager.getVarbitValue(53247) == 0 &&  player.getFollowing().getCurrentHealth() > 1000 && ActionBar.getCooldownPrecise("Invoke Death") == 0 && ActionBar.containsAbility("Invoke Death") && player.hasTarget() && player.inCombat() && player.getFollowing() != null) {
            Execution.delay(Deathmark(player));
            return;
        }
        if (VolleyofSouls && VarManager.getVarValue(VarDomainType.PLAYER, 11035) >= VolleyOfSoulsThreshold && player.inCombat() &&  player.hasTarget() && ActionBar.containsAbility("Volley of Souls")) {
            Execution.delay(volleyOfSouls(player));
            return;
        }
        if (SpecialAttack && player.getAdrenaline() >= 300 && ActionBar.getCooldownPrecise("Weapon Special Attack") == 0 && ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty() && player.hasTarget() && ActionBar.containsAbility("Weapon Special Attack")) {
            Execution.delay(DeathEssence(player));
            return;
        }
        if (KeepArmyup && VarManager.getVarValue(VarDomainType.PLAYER, 11018) == 0 && ActionBar.containsAbility("Conjure Undead Army")) {
            Execution.delay(KeepArmyup(player));
            return;
        }
        if (useVulnerabilityBombs) {
            Execution.delay(vulnerabilityBomb(player));
            return;
        }
        if (useThreadsofFate && ActionBar.containsAbility("Threads of Fate") && ActionBar.getCooldownPrecise("Threads of Fate") == 0) {
            Execution.delay(manageThreadsofFate(player));
            return;
        }
        if (useDarkness && ActionBar.containsAbility("Darkness") && ActionBar.getCooldownPrecise("Darkness") == 0 && VarManager.getVarValue(VarDomainType.PLAYER, 11074) == 0) {
            Execution.delay(manageDarkness(player));
            return;
        }
        if (useElvenRitual) {
            Execution.delay(activateElvenRitual(player));
            return;
        }
        if (useExcalibur && ComponentQuery.newQuery(291).spriteId(14632).results().first() == null) {
            Execution.delay(activateExcalibur());
            return;
        }
        attackNearestMonster(player);
    }

    public static long essenceOfFinality(LocalPlayer player) {
        int currentNecrosisStacks = VarManager.getVarValue(VarDomainType.PLAYER, 10986);
        boolean abilityUsed = ActionBar.useAbility("Essence of Finality");
        if (abilityUsed) {
            log("[Success] Used Death Grasp with " + currentNecrosisStacks + " Necrosis stacks.");
            return random.nextLong(600);
        } else {
            log("[Error] Attempted to use Death Grasp, but ability use failed.");
        }
        return 0;
    }

    public static long DeathEssence(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Weapon Special Attack");
        if (success) {
            log("[Success] Used Death Essence: " + true);
            return random.nextLong(600);
        } else {
            log("[Error] Attempted to use Death Essence, but ability use failed.");
        }
        return 0;
    }

    public static long volleyOfSouls(LocalPlayer player) {
        int currentResidualSouls = VarManager.getVarValue(VarDomainType.PLAYER, 11035);
        boolean abilityUsed = ActionBar.useAbility("Volley of Souls");
        if (abilityUsed) {
            log("[Success] Used Volley of Souls with " + currentResidualSouls + " residual souls.");
            return random.nextLong(600);
        } else {
            log("[Error] Attempted to use Volley of Souls, but ability use failed.");
        }
        return 0;
    }

    public static long Deathmark(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Invoke Death");
        if (success) {
            log("[Success] Used Invoke Death: " + true);
            return random.nextLong(600);
        } else {
            log("[Error] Attempted to use Invoke Death, but ability use failed.");
        }
        return 0;
    }

    public static long KeepArmyup(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Conjure Undead Army");
        if (success) {
            log("[Success] Cast Conjure army: " + true);
            return random.nextLong(600);
        } else {
            log("[Error] Attempted to cast Conjure army, but ability use failed.");
        }
        return 0;
    }

    public static long manageAnimateDead(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Animate Dead");
        if (success) {
            log("[Success] Cast Animate Dead: " + true);
            return random.nextLong(600);
        } else {
            log("[Error] Attempted to cast Animate Dead, but ability use failed.");
        }
        return 0;
    }
    public static long manageThreadsofFate(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Threads of Fate");
        if (success) {
            log("[Success] Cast Threads of Fate: " + true);
            return random.nextLong(600);
        } else {
            log("[Error] Attempted to cast Threads of Fate, but ability use failed.");
        }
        return 0;
    }
    public static long manageDarkness(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Darkness");
        if (success) {
            log("[Success] Cast Darkness: " + true);
            return random.nextLong(600);
        } else {
            log("[Error] Attempted to cast Darkness, but ability use failed.");
        }
        return 0;
    }
    public static long activateElvenRitual(LocalPlayer player) {
        if (player.getPrayerPoints() > prayerPointsThreshold && Backpack.contains("Ancient elven ritual shard")) {
            Component elvenRitual = ComponentQuery.newQuery(291).spriteId(43358).results().first();
            if (elvenRitual == null) {
                boolean success = backpack.interact("Ancient elven ritual shard", "Activate");
                if (success) {
                    log("[Success] Activated Elven Ritual Shard.");
                    return random.nextLong(600);
                } else {
                    log("[Error] Failed to activate Elven Ritual Shard.");
                }
            }
        }
        return 0;
    }

    private static long activateExcalibur() {
        LocalPlayer player = getLocalPlayer();
        if (player.getCurrentHealth() * 100 / player.getMaximumHealth() >= healthPointsThreshold) {
            return 0;
        }

        ResultSet<Item> items = InventoryItemQuery.newQuery().results();
        Item excaliburItem = items.stream()
                .filter(item -> item.getName() != null && item.getName().toLowerCase().contains("excalibur"))
                .findFirst()
                .orElse(null);

        if (excaliburItem != null) {
            boolean success = Backpack.interact(excaliburItem.getName(), "Activate");
            if (success) {
                log("Activating " + excaliburItem.getName());
                return random.nextLong(600);
            } else {
                log("Failed to activate Excalibur.");
            }
        } else {
            log("No Excalibur found!");
        }

        return 0;
    }
}
