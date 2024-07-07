package net.botwithus.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.prayerPointsThreshold;
import static net.botwithus.Variables.Variables.random;

public class Familiar {

    public static boolean useFamiliarForCombat = false;
    public static boolean useFamiliarScrolls = false;

    public static long summonFamiliar() {
        if (VarManager.getVarbitValue(6055) > 1 && useFamiliarScrolls) {
            familiarScrolls();
        }
        LocalPlayer player = Client.getLocalPlayer();
        if (VarManager.getVarbitValue(6055) <= 1) {
            ResultSet<Item> pouch = InventoryItemQuery.newQuery(93).results();

            Item itemToSummon = pouch.stream()
                    .filter(item -> item.getName() != null &&
                            (item.getName().toLowerCase().contains("pouch") ||
                                    item.getName().toLowerCase().contains("contract")) &&
                            !item.getName().toLowerCase().contains("rune"))
                    .findFirst()
                    .orElse(null);

            if (itemToSummon == null) {
                log("[Familiar] No pouch found in inventory.");
                return 1L;
            }

            if (player.getSummoningPoints() > 1000) {
                Backpack.interact(itemToSummon.getName(), "Summon");
                Execution.delay(random.nextLong(800, 1000));
            } else {
                ResultSet<Item> items = InventoryItemQuery.newQuery(93).results();

                Item restorePotion = items.stream()
                        .filter(item -> item.getName() != null &&
                                item.getName().toLowerCase().contains("restore"))
                        .findFirst()
                        .orElse(null);

                if (restorePotion == null) {
                    log("[Error] No prayer or restore potions found in the backpack. Unable to summon.");
                    return 1L;
                }

                boolean success = backpack.interact(restorePotion.getName(), "Drink");
                if (success) {
                    log("[Familiar] Successfully drank " + restorePotion.getName());
                    Execution.delayUntil(random.nextLong(1800, 2000), () -> player.getPrayerPoints() > prayerPointsThreshold);
                    if (player.getSummoningPoints() > 1000) {
                        Backpack.interact(itemToSummon.getName(), "Summon");
                    } else {
                        log("[Caution] Not enough summoning points to summon familiar even after restore.");
                    }
                } else {
                    log("[Error] Failed to interact with " + restorePotion.getName());
                }
            }
        }
        return random.nextLong(1200, 1300);
    }

    private static void familiarScrolls() {
        int scrollsStored = getScrollsStored();
        boolean hasScrolls = hasScrollsInInventory();

        if (scrollsStored <= 10 && hasScrolls) {
            log("[Familiar] Handling inventory scrolls...");
            handleInventoryScrolls();
        }
    }

    private static int getScrollsStored() {
        return VarManager.getVarbitValue(25412);
    }

    private static boolean hasScrollsInInventory() {
        ResultSet<Item> scrolls = InventoryItemQuery.newQuery(93).results();
        return scrolls.stream().anyMatch(item -> item.getName() != null && item.getName().toLowerCase().contains("scroll"));
    }

    private static void handleInventoryScrolls() {
        if (hasScrollsInInventory()) {
            storeMaxScrolls();
        } else {
            log("[Error] No scrolls found in inventory.");
        }
    }

    private static void storeMaxScrolls() {
        log("[Familiar] Attempting to store scrolls in familiar.");
        boolean success = ComponentQuery.newQuery(662).componentIndex(78).results().first().interact(1);
        Execution.delay(random.nextLong(800, 1000));
        if (success) {
            log("[Success] Successfully stored scrolls in familiar.");
        } else {
            log("[Error] Failed to store scrolls in familiar.");
        }
    }
}

