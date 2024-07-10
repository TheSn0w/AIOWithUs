package net.botwithus.Combat;

import net.botwithus.Slayer.Main;
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

import java.util.regex.Pattern;

import static net.botwithus.Combat.Banking.bankToWars;
import static net.botwithus.Combat.Familiar.summonFamiliar;
import static net.botwithus.Combat.Familiar.useFamiliarForCombat;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Main.doSlayer;
import static net.botwithus.Slayer.Main.setSlayerState;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.random;

public class Potions {
    public static boolean usePowderOfProtection = false;
    public static boolean usePowderOfPenance = false;
    public static boolean useKwuarmSticks = false;
    public static boolean useLantadymeSticks = false;
    public static boolean useIritSticks = false;

    public static void managePotions(LocalPlayer player) {
        long totalDelay = random.nextLong(1000, 1500);

        long aggroCheck = useAggroPots ? useAggression(player) : 0;
        long prayerCheck = usePrayerPots ? usePrayerOrRestorePots(player) : 0;
        long overloadCheck = useOverloads ? drinkOverloads(player) : 0;
        long weaponPoisonCheck = useWeaponPoison ? useWeaponPoison(player) : 0;
        long familiarCheck = useFamiliarForCombat ? summonFamiliar() : 0;

        if (aggroCheck == 1L || prayerCheck == 1L || overloadCheck == 1L || weaponPoisonCheck == 1L || familiarCheck == 1L) {
            if (nearestBank) {
                if (doSlayer) {
                    setSlayerState(Main.SlayerState.WARS_RETREAT);
                } else {
                    log("[Info] One or more potions are missing, we're banking.");
                    setBotState(BANKING);
                }
            } else {
                log("[Info] One or more Potions/Pouches are missing, but Bank is disabled.");
            }
        }

        totalDelay += aggroCheck;
        totalDelay += prayerCheck;
        totalDelay += overloadCheck;
        totalDelay += weaponPoisonCheck;
        totalDelay += familiarCheck;

        Execution.delay(totalDelay);
    }


    public static long useAggression(LocalPlayer player) {
        if (!useAggroPots || player == null || !player.inCombat() || player.getAnimationId() == 18000 || VarManager.getVarbitValue(33448) != 0) {  // Check if aggression potions are enabled
            return 0;
        }

        ResultSet<Item> results = InventoryItemQuery.newQuery(93)
                .name("Aggression", String::contains)
                .option("Drink")
                .results();

        if (results.isEmpty()) {
            log("[Error] No aggression flasks found in the inventory.");
            return 1L;
        }

        Item aggressionFlask = results.first();
        if (aggressionFlask != null) {
            boolean success = backpack.interact(aggressionFlask.getName(), "Drink");
            if (success) {
                log("[Combat] Using aggression potion: " + aggressionFlask.getName());
                Execution.delayUntil(5000, () -> VarManager.getVarbitValue(33448) != 0);
            } else {
                log("[Error] Failed to use aggression potion: " + aggressionFlask.getName());
                return 0;
            }
        }

        return 0;
    }


    public static long usePrayerOrRestorePots(LocalPlayer player) {
        if (!usePrayerPots || player == null || !player.inCombat() || player.getAnimationId() == 18000 || player.getPrayerPoints() > prayerPointsThreshold) {  // Check if there's a local player
            return 0;
        }

        ResultSet<Item> items = InventoryItemQuery.newQuery(93).results();

        Item prayerOrRestorePot = items.stream()
                .filter(item -> item.getName() != null &&
                        (item.getName().toLowerCase().contains("prayer") ||
                                item.getName().toLowerCase().contains("restore")))
                .findFirst()
                .orElse(null);

        if (prayerOrRestorePot == null) {
            log("[Error]  No prayer or restore potions found in the backpack.");
            return 1L;
        }

        boolean success = backpack.interact(prayerOrRestorePot.getName(), "Drink");
        if (success) {
            log("[Combat] Successfully drank " + prayerOrRestorePot.getName());
            Execution.delayUntil(random.nextLong(1800, 2000), () -> player.getPrayerPoints() > prayerPointsThreshold);
            Execution.delay(random.nextLong(1250, 1500));
        } else {
            log("[Error] Failed to interact with " + prayerOrRestorePot.getName());
            return 0;
        }
        return 0;
    }

    public static long drinkOverloads(LocalPlayer player) {
        if (!useOverloads) {
            return 0;
        }

        if (player == null || !player.inCombat() || VarManager.getVarbitValue(48834) != 0 || player.getAnimationId() == 18000) {
            return 0L;
        }

        Pattern overloadPattern = Pattern.compile("overload", Pattern.CASE_INSENSITIVE);


        Item overloadPot = InventoryItemQuery.newQuery(93)
                .results()
                .stream()
                .filter(item -> item.getName() != null && overloadPattern.matcher(item.getName()).find())
                .findFirst()
                .orElse(null);

        if (overloadPot == null) {
            log("[Error] No overload potion found in the Backpack.");
            return 1L;
        }


        boolean success = backpack.interact(overloadPot.getName(), "Drink");
        if (success) {
            log("[Combat] Successfully drank " + overloadPot.getName());
            Execution.delayUntil(5000, () -> VarManager.getVarbitValue(48834) != 0);
            Execution.delay(random.nextLong(1250, 1500));
        } else {
            log("[Error] Failed to interact with overload potion.");
            return 0L;
        }
        return 0;
    }

    public static long useWeaponPoison(LocalPlayer player) {
        if (!useWeaponPoison) {
            return 0;
        }
        if (player == null || player.getAnimationId() == 18068 || VarManager.getVarbitValue(2102) > 3) {  // Ensure there's a valid player and conditions are right
            return 0L;
        }

        Pattern poisonPattern = Pattern.compile("weapon poison\\+*?", Pattern.CASE_INSENSITIVE);

        Item weaponPoisonItem = InventoryItemQuery.newQuery(93)
                .results()
                .stream()
                .filter(item -> item.getName() != null && poisonPattern.matcher(item.getName()).find())
                .findFirst()
                .orElse(null);

        if (weaponPoisonItem == null) {
            log("[Error] No weapon poison found in the Backpack.");
            return 1L;
        }

        boolean success = backpack.interact(weaponPoisonItem.getName(), "Apply");
        if (success) {
            log("[Combat] Successfully applied " + weaponPoisonItem.getName());
            Execution.delayUntil(5000, () -> VarManager.getVarbitValue(2102) > 1);
            Execution.delay(random.nextLong(1250, 1500));
        } else {
            log("[Error] Failed to apply weapon poison.");
            return 0L;
        }
        return 0;
    }

    public static void handleIncenseSticks(String stickName, int overloadVarId, int durationVarId) {
        if (Backpack.getQuantity(stickName) > 10) {
            if (VarManager.getVarbitValue(overloadVarId) != 4) {
                log("[Incense Sticks] " + stickName + " overload not active, activating.");
                backpack.interact(stickName, "Overload");
                Execution.delayUntil(random.nextLong(2000, 3000), () -> VarManager.getVarbitValue(overloadVarId) == 4);
            }

            int currentValue = VarManager.getVarbitValue(durationVarId);
            boolean shouldUseSticks = currentValue <= 60 || (currentValue < 120 && currentValue >= 60);

            if (shouldUseSticks) {
                log("[Incense Sticks] We have below 30 minutes or are continuing to top up, interacting with " + stickName + ".");
                Backpack.interact(stickName, "Light");
                Execution.delay(random.nextLong(650, 700));
            }
        }
    }

    public static void lantadymeSticks() {
        handleIncenseSticks("Lantadyme incense sticks", 43699, 43717);
    }

    public static void kwuarmSticks() {
        handleIncenseSticks("Kwuarm incense sticks", 43707, 43725);
    }

    public static void iritSticks() {
        handleIncenseSticks("Irit incense sticks", 43696, 43714);
    }

    public static void powderOfProtection() {
        if (Backpack.contains("Powder of protection") && VarManager.getVarbitValue(50837) <= 1) {
            log("[Powder] We need to activate Powder of Protection.");
            Backpack.interact("Powder of protection", "Scatter");
            Execution.delayUntil(random.nextLong(2000, 3000), () -> VarManager.getVarbitValue(50837) > 1);
        }
    }
    public static void powderOfPenance() {
        if (Backpack.contains("Powder of penance") && VarManager.getVarbitValue(50841) <= 1) {
            log("[Powder] We need to activate Powder of Penance.");
            Backpack.interact("Powder of penance", "Scatter");
            Execution.delayUntil(random.nextLong(2000, 3000), () -> VarManager.getVarbitValue(50841) > 1);
        }
    }







}