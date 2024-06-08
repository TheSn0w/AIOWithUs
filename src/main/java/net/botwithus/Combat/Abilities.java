package net.botwithus.Combat;

import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
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
    public static int NecrosisStacksThreshold = 12;
    public static int VolleyOfSoulsThreshold = 5;

    private static Queue<Runnable> abilityQueue = new LinkedList<>();

    public static void manageCombatAbilities() {
        LocalPlayer player = getLocalPlayer();
        int currentNecrosisStacks = VarManager.getVarValue(VarDomainType.PLAYER, 10986);


        if (DeathGrasp && player.getAdrenaline() >= 250 && ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty() && ActionBar.getCooldownPrecise("Essence of Finality") == 0 && player.inCombat() && player.getFollowing() != null && player.hasTarget() && ActionBar.getCooldown("Essence of Finality") == 0 && ActionBar.containsAbility("Essence of Finality") && currentNecrosisStacks >= NecrosisStacksThreshold) {
            abilityQueue.add(() -> essenceOfFinality(player));
        }
        if (InvokeDeath && VarManager.getVarbitValue(53247) == 0 && player.getFollowing() != null && player.getFollowing().getCurrentHealth() >= 500 && ActionBar.getCooldown("Invoke Death") == 0 && ActionBar.containsAbility("Invoke Death")) {
            abilityQueue.add(() -> Deathmark(player));
        }
        if (VolleyofSouls && VarManager.getVarValue(VarDomainType.PLAYER, 11035) >= VolleyOfSoulsThreshold && player.inCombat() && player.getFollowing() != null && player.hasTarget() && ActionBar.containsAbility("Volley of Souls")) {
            abilityQueue.add(() -> volleyOfSouls(player));
        }
        if (SpecialAttack && player.getAdrenaline() >= 300 && ActionBar.getCooldown("Weapon Special Attack") == 0 && player.getFollowing() != null && player.getFollowing().getCurrentHealth() >= 500 && ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty() && player.hasTarget() && ActionBar.containsAbility("Weapon Special Attack")) {
            abilityQueue.add(() -> DeathEssence(player));
        }
        if (KeepArmyup && VarManager.getVarValue(VarDomainType.PLAYER, 11018) == 0 && ActionBar.containsAbility("Conjure Undead Army")) {
            abilityQueue.add(() -> KeepArmyup(player));
        }
        if (useVulnerabilityBombs) {
            abilityQueue.add(() -> vulnerabilityBomb(player));
        }

        while (!abilityQueue.isEmpty()) {
            abilityQueue.poll().run();
            Execution.delay(random.nextLong(1900, 2000));
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
}
