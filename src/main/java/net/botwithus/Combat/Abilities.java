package net.botwithus.Combat;

import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.Combat.Potions.vulnerabilityBomb;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Abilities {
    public static int NecrosisStacksThreshold = 12;
    public static int VolleyOfSoulsThreshold = 5;

    public static void manageCombatAbilities() {
        LocalPlayer player = getLocalPlayer();
        long totalDelay = 0;

        if (DeathGrasp) {
            totalDelay += essenceOfFinality(player);
        }
        if (InvokeDeath) {
            totalDelay += Deathmark(player);
        }
        if (VolleyofSouls) {
            totalDelay += volleyOfSouls(player);
        }
        if (SpecialAttack) {
            totalDelay += DeathEssence(player);
        }
        if (KeepArmyup) {
            totalDelay += KeepArmyup(player);
        }
        if (useVulnerabilityBombs) {
            totalDelay += vulnerabilityBomb(player);
        }

        Execution.delay(totalDelay);
    }

    public static long essenceOfFinality(LocalPlayer player) {
        if (player != null) {
            if (player.getAdrenaline() >= 250 && ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty() && ActionBar.getCooldownPrecise("Essence of Finality") == 0 && player.inCombat() && player.getFollowing() != null && player.hasTarget() && ActionBar.getCooldown("Essence of Finality") == 0 && ActionBar.containsAbility("Essence of Finality")) {
                int currentNecrosisStacks = VarManager.getVarValue(VarDomainType.PLAYER, 10986);
                if (currentNecrosisStacks >= NecrosisStacksThreshold) {
                    boolean abilityUsed = ActionBar.useAbility("Essence of Finality");
                    if (abilityUsed) {
                        log("[Success] Used Death Grasp with " + currentNecrosisStacks + " Necrosis stacks.");
                        return random.nextLong(1900, 2000);
                    } else {
                        log("[Error] Attempted to use Death Grasp, but ability use failed.");
                    }
                }
            }
        }
        return 0;
    }

    public static long DeathEssence(LocalPlayer player) {
        if (player != null) {
            if (player.getAdrenaline() >= 350 && ActionBar.getCooldown("Weapon Special Attack") == 0 && player.getFollowing() != null && player.getFollowing().getCurrentHealth() >= 500 && ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty() && player.hasTarget() && ActionBar.containsAbility("Weapon Special Attack")) {
                boolean success = ActionBar.useAbility("Weapon Special Attack");
                log("[Success] Used Death Essence: " + success);
                if (success) {
                    return random.nextLong(1900, 2000);
                } else {
                    log("[Error] Attempted to use Death Essence, but ability use failed.");
                }
            }
        }
        return 0;
    }

    public static long volleyOfSouls(LocalPlayer player) {
        if (player != null) {
            if (VarManager.getVarValue(VarDomainType.PLAYER, 11035) >= VolleyOfSoulsThreshold && player.inCombat() && player.getFollowing() != null && player.hasTarget() && ActionBar.containsAbility("Volley of Souls")) {
                int currentResidualSouls = VarManager.getVarValue(VarDomainType.PLAYER, 11035);
                boolean abilityUsed = ActionBar.useAbility("Volley of Souls");
                if (abilityUsed) {
                    log("[Success] Used Volley of Souls with " + currentResidualSouls + " residual souls.");
                    return random.nextLong(1900, 2000);
                } else {
                    log("[Error] Attempted to use Volley of Souls, but ability use failed.");
                }
            }
        }
        return 0;
    }

    public static long Deathmark(LocalPlayer player) {
        if (player != null) {
            if (VarManager.getVarbitValue(53247) == 0 && player.getFollowing() != null && player.getFollowing().getCurrentHealth() >= 500 && ActionBar.getCooldown("Invoke Death") == 0 && ActionBar.containsAbility("Invoke Death")) {
                boolean success = ActionBar.useAbility("Invoke Death");
                log("[Success] Used Invoke Death: " + success);
                if (success) {
                    return random.nextLong(1900, 2000);
                } else {
                    log("[Error] Attempted to use Invoke Death, but ability use failed.");
                }
            }
        }
        return 0;
    }

    public static long KeepArmyup(LocalPlayer player) {
        if (player != null) {
            if (VarManager.getVarValue(VarDomainType.PLAYER, 11018) == 0) {
                if (ActionBar.containsAbility("Conjure Undead Army")) {
                    boolean success = ActionBar.useAbility("Conjure Undead Army");
                    log("[Success] Cast Conjure army: " + success);
                    if (success) {
                        return random.nextLong(1900, 2000);
                    } else {
                        log("[Error] Attempted to cast Conjure army, but ability use failed.");
                    }
                }
            }
        }
        return 0;
    }

    public static long manageAnimateDead(LocalPlayer player) {
        if (player != null) {
            if (VarManager.getVarbitValue(49447) <= 1 && ActionBar.containsAbility("Animate Dead")) {
                boolean success = ActionBar.useAbility("Animate Dead");
                log("[Success] Cast Animate Dead: " + success);
                if (success) {
                    return random.nextLong(1900, 2000);
                } else {
                    log("[Error] Attempted to cast Animate Dead, but ability use failed.");
                }
            }
        }
        return 0;
    }
}
