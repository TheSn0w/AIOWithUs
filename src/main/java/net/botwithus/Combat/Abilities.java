package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.LinkedList;
import java.util.Queue;

import static net.botwithus.Combat.Potions.vulnerabilityBomb;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Abilities {
    public static boolean useThreadsofFate = false;
    public static boolean useDarkness = false;
    public static int NecrosisStacksThreshold = 12;
    public static int VolleyOfSoulsThreshold = 5;
    private static long lastAbilityTime = 0;
    public static boolean useExcalibur = false;
    public static boolean useElvenRitual = false;

    public static void manageCombatAbilities() {
        LocalPlayer player = getLocalPlayer();
        int currentNecrosisStacks = VarManager.getVarValue(VarDomainType.PLAYER, 10986);

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAbilityTime < 1200) {
            return;
        }

        if (DeathGrasp && player.getAdrenaline() >= 250 && ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty() && ActionBar.getCooldownPrecise("Essence of Finality") == 0 && player.inCombat() && player.getFollowing() != null && player.hasTarget() && ActionBar.getCooldownPrecise("Essence of Finality") == 0 && ActionBar.containsAbility("Essence of Finality") && currentNecrosisStacks >= NecrosisStacksThreshold) {
            essenceOfFinality(player);
            lastAbilityTime = currentTime;
        } else if (InvokeDeath && VarManager.getVarbitValue(53247) == 0 && player.getFollowing() != null && player.getFollowing().getCurrentHealth() >= 500 && ActionBar.getCooldownPrecise("Invoke Death") == 0 && ActionBar.containsAbility("Invoke Death")) {
            Deathmark(player);
            lastAbilityTime = currentTime;
        } else if (VolleyofSouls && VarManager.getVarValue(VarDomainType.PLAYER, 11035) >= VolleyOfSoulsThreshold && player.inCombat() && player.getFollowing() != null && player.hasTarget() && ActionBar.containsAbility("Volley of Souls")) {
            volleyOfSouls(player);
            lastAbilityTime = currentTime;
        } else if (SpecialAttack && player.getAdrenaline() >= 300 && ActionBar.getCooldownPrecise("Weapon Special Attack") == 0 && player.getFollowing() != null && player.getFollowing().getCurrentHealth() >= 500 && ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty() && player.hasTarget() && ActionBar.containsAbility("Weapon Special Attack")) {
            DeathEssence(player);
            lastAbilityTime = currentTime;
        } else if (KeepArmyup && VarManager.getVarValue(VarDomainType.PLAYER, 11018) == 0 && ActionBar.containsAbility("Conjure Undead Army")) {
            KeepArmyup(player);
            lastAbilityTime = currentTime;
        } else if (useVulnerabilityBombs) {
            vulnerabilityBomb(player);
            lastAbilityTime = currentTime;
        } else if (useThreadsofFate && ActionBar.containsAbility("Threads of Fate") && ActionBar.getCooldownPrecise("Threads of Fate") == 0){
            manageThreadsofFate(player);
            lastAbilityTime = currentTime;
        } else if (useDarkness && ActionBar.containsAbility("Darkness") && ActionBar.getCooldownPrecise("Darkness") == 0 && VarManager.getVarValue(VarDomainType.PLAYER, 11074) == 0) {
            manageDarkness(player);
            lastAbilityTime = currentTime;
        } else if (useElvenRitual) {
            activateElvenRitual(player);
            lastAbilityTime = currentTime;
        } else if (useExcalibur) {
            activateExcalibur();
            lastAbilityTime = currentTime;
        }
    }

    public static void essenceOfFinality(LocalPlayer player) {
        int currentNecrosisStacks = VarManager.getVarValue(VarDomainType.PLAYER, 10986);
        boolean abilityUsed = ActionBar.useAbility("Essence of Finality");
        if (abilityUsed) {
            log("[Success] Used Death Grasp with " + currentNecrosisStacks + " Necrosis stacks.");
        } else {
            log("[Error] Attempted to use Death Grasp, but ability use failed.");
        }
    }

    public static void DeathEssence(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Weapon Special Attack");
        if (success) {
            log("[Success] Used Death Essence: " + true);
        } else {
            log("[Error] Attempted to use Death Essence, but ability use failed.");
        }
    }

    public static void volleyOfSouls(LocalPlayer player) {
        int currentResidualSouls = VarManager.getVarValue(VarDomainType.PLAYER, 11035);
        boolean abilityUsed = ActionBar.useAbility("Volley of Souls");
        if (abilityUsed) {
            log("[Success] Used Volley of Souls with " + currentResidualSouls + " residual souls.");
        } else {
            log("[Error] Attempted to use Volley of Souls, but ability use failed.");
        }
    }

    public static void Deathmark(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Invoke Death");
        if (success) {
            log("[Success] Used Invoke Death: " + true);
        } else {
            log("[Error] Attempted to use Invoke Death, but ability use failed.");
        }
    }

    public static void KeepArmyup(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Conjure Undead Army");
        if (success) {
            log("[Success] Cast Conjure army: " + true);
        } else {
            log("[Error] Attempted to cast Conjure army, but ability use failed.");
        }
    }

    public static void manageAnimateDead(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Animate Dead");
        if (success) {
            log("[Success] Cast Animate Dead: " + true);
        } else {
            log("[Error] Attempted to cast Animate Dead, but ability use failed.");
        }
    }
    public static void manageThreadsofFate(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Threads of Fate");
        if (success) {
            log("[Success] Cast Threads of Fate: " + true);
        } else {
            log("[Error] Attempted to cast Threads of Fate, but ability use failed.");
        }
    }
    public static void manageDarkness(LocalPlayer player) {
        boolean success = ActionBar.useAbility("Darkness");
        if (success) {
            log("[Success] Cast Darkness: " + true);
        } else {
            log("[Error] Attempted to cast Darkness, but ability use failed.");
        }
    }
    public static void activateElvenRitual(LocalPlayer player) {
        if (player.getPrayerPoints() < prayerPointsThreshold && Backpack.contains("Ancient elven ritual shard")) {
            Component elvenRitual = ComponentQuery.newQuery(291).spriteId(43358).results().first();
            if (elvenRitual == null) {
                boolean success = backpack.interact("Ancient elven ritual shard", "Activate");
                if (success) {
                    log("[Success] Activated Elven Ritual Shard.");
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
                return;
            }

            ResultSet<net.botwithus.rs3.game.Item> items = InventoryItemQuery.newQuery().results();
            Item excaliburItem = items.stream()
                    .filter(item -> item.getName() != null && item.getName().toLowerCase().contains("excalibur"))
                    .findFirst()
                    .orElse(null);

            if (excaliburItem != null) {
                boolean success = Backpack.interact(excaliburItem.getName(), "Activate");
                if (success) {
                    log("Activating " + excaliburItem.getName());
                } else {
                    log("Failed to activate Excalibur.");
                }
            } else {
                log("No Excalibur found!");
            }
        }
    }
}
