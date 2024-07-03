package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.regex.Pattern;

import static net.botwithus.Combat.Banking.bankToWars;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.BANKING;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.random;

public class Potions {

    public static void managePotions(LocalPlayer player) {
        long totalDelay = 0;

        long aggroCheck = useAggression(player);
        long prayerCheck = usePrayerOrRestorePots(player);
        long overloadCheck = drinkOverloads(player);
        long weaponPoisonCheck = useWeaponPoison(player);

        if (aggroCheck == 1L || prayerCheck == 1L || overloadCheck == 1L || weaponPoisonCheck == 1L) {
            if (nearestBank) {
                log("[Info] One or more potions are missing, consider banking.");
                setBotState(BANKING);
            } else {
                log("[Info] One or more potions are missing, but Nearest Bank is disabled.");
            }
        }

        totalDelay += aggroCheck != 1L ? aggroCheck : 0;
        totalDelay += prayerCheck != 1L ? prayerCheck : 0;
        totalDelay += overloadCheck != 1L ? overloadCheck : 0;
        totalDelay += weaponPoisonCheck != 1L ? weaponPoisonCheck : 0;

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
                return random.nextLong(650, 950);
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

        log("[Combat] Drinking " + prayerOrRestorePot.getName());
        boolean success = backpack.interact(prayerOrRestorePot.getName(), "Drink");
        if (success) {
            log("[Combat] Successfully drank " + prayerOrRestorePot.getName());
            return random.nextLong(650, 950);
        } else {
            log("[Error] Failed to interact with " + prayerOrRestorePot.getName());
            return 0;
        }
    }

    public static long drinkOverloads(LocalPlayer player) {
        if (!useOverloads) {
            return 0;
        }

        if (player == null || !player.inCombat() || VarManager.getVarbitValue(48834) != 0 || player.getAnimationId() == 18000) {
            return 0L;
        }

        Pattern overloadPattern = Pattern.compile("overload", Pattern.CASE_INSENSITIVE);


        Item overloadPot = InventoryItemQuery.newQuery()
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
            return random.nextLong(100, 200);
        } else {
            log("[Error] Failed to interact with overload potion.");
            return 0L;
        }
    }

    public static long useWeaponPoison(LocalPlayer player) {
        if (!useWeaponPoison) {
            return 0;
        }
        if (player == null || player.getAnimationId() == 18068 || VarManager.getVarbitValue(2102) > 3) {  // Ensure there's a valid player and conditions are right
            return 0L;
        }

        Pattern poisonPattern = Pattern.compile("weapon poison\\+*?", Pattern.CASE_INSENSITIVE);

        Item weaponPoisonItem = InventoryItemQuery.newQuery()
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
            return random.nextLong(650, 950);
        } else {
            log("[Error] Failed to apply weapon poison.");
            return 0L;
        }
    }


}