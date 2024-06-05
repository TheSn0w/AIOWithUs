package net.botwithus.Divination;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;

public class Divination {

    public static long handleDivination(LocalPlayer player) {
        int divinationLevel = Skills.DIVINATION.getActualLevel();
        String wispName = getWispName(divinationLevel);
        Coordinate wispCoordinates = getWispCoordinates(divinationLevel);

        if (wispCoordinates == null) {
            log("[Error] No valid wisp coordinates found for level: " + divinationLevel);
            return random.nextLong(1500, 3000);
        }

        if (wispName == null && player.getAnimationId() != 16385) {
            log("[Error] No valid wisp name for level: " + divinationLevel);
            return navigateTo(player, wispCoordinates);
        }

        updateCount();

        if (player.isMoving()) {
            return random.nextLong(1500, 3000);
        }
        if (useFamiliarSummoning) {
            checkAndPerformActions(player);
        }
        if (useDivineoMatic) {
            divineoMatic();
        }
        if(harvestChronicles) {
            EntityResultSet<Npc> chronicles = NpcQuery.newQuery().name("Chronicle fragment").results();
            if (!chronicles.isEmpty()) {
                Execution.delay(interactWithChronicle(chronicles));
                return 0;
            }
        }
        if (offerChronicles && Backpack.getQuantity("Chronicle fragment") >= 10)
            return convertChroniclesAtRift();

        if (Backpack.isFull()) {
            log("[Divination] Backpack is full, converting at the rift.");
            return handleFullBackpack();
        }
        if (!harvestChronicles) {
            Execution.delay(attemptHarvestEnriched(player, wispName));
        }

        if (player.getAnimationId() == 21228 || player.getAnimationId() == 31055) {
            return random.nextLong(1500, 3000);
        }

        EntityResultSet<Npc> nearbyWisps = NpcQuery.newQuery().name(wispName).results();
        if (!nearbyWisps.isEmpty()) {
            Npc nearestWisp = nearbyWisps.nearest();
            if (nearestWisp != null && Distance.between(player.getCoordinate(), nearestWisp.getCoordinate()) < 25.0D) {
                return attemptHarvest(player, wispName);
            }
        }

        return random.nextLong(750, 1250);
    }


    private static long interactWithChronicle(EntityResultSet<Npc> chronicles) {
        Npc nearestChronicle = chronicles.nearest();

        log("[Divination] Interacted with Chronicle: " + nearestChronicle.interact("Capture"));

        Execution.delayUntil(random.nextLong(10000, 15000), () -> !nearestChronicle.validate());

        return 0;
    }

    private static long navigateTo(LocalPlayer player, Coordinate destination) {
        if (player == null) {
            return random.nextLong(1500, 3000);
        }

        log("[Divination] Navigating to wisp coordinates");
        if (Movement.traverse(NavPath.resolve(destination)) == TraverseEvent.State.FINISHED) {
            log("[Divination] Arrived at wisp Destination");
            return random.nextLong(1500, 3000);
        } else {
            log("[Error] Failed to navigate to wisp coordinates");
        }

        return random.nextLong(1500, 3000);
    }


    private static long handleFullBackpack() {
        long delay = random.nextLong(500, 750);

        SceneObject nearestRift = findNearestRift();

        if (nearestRift == null) {
            log("[Error] No nearby rift found to convert memories.");
            delay += random.nextLong(500, 1000);
        } else {
            if (nearestRift.interact("Convert memories")) {
                Execution.delayUntil(60000, Divination::isBackpackEmpty);

                delay += random.nextLong(550, 800);
            } else {
                delay += random.nextLong(500, 750);
            }
        }

        return delay;
    }


    private static boolean isBackpackEmpty() {
        return !Backpack.containsItemByCategory(3030) && !Backpack.containsItemByCategory(3031);
    }

    private static long convertChroniclesAtRift() {
        SceneObject nearestRift = findNearestRift();

        if (nearestRift == null) {
            log("[Error] No nearby rift found to convert memories.");
            return random.nextLong(500, 1000);
        }

        log("[Divination] Offering Chronicles: " + nearestRift.interact("Empower"));
        Execution.delayUntil(5000, () -> !Backpack.containsItemByCategory(3026));
        return random.nextLong(500, 750);
    }

    private static SceneObject findNearestRift() {
        int[] riftIds = {93489, 87306};

        for (int riftId : riftIds) {
            SceneObject nearestRift = SceneObjectQuery.newQuery().id(riftId).results().nearest();

            if (nearestRift != null) {
                return nearestRift;
            }
        }

        return null;
    }
    private static long attemptHarvestEnriched(LocalPlayer player, String wispName) {
            EntityResultSet<Npc> wispsToHarvest = NpcQuery.newQuery().name("Enriched incandescent spring").results();

            Npc nearestWisp = wispsToHarvest.nearest();
            if (nearestWisp != null) {
                Execution.delay(random.nextLong(500, 1250));
                boolean success = nearestWisp.interact("Harvest");
                if (success) {
                    log("[Enriched] Harvesting: " + wispName);
                    Execution.delayUntil(360000, () -> !wispsToHarvest.nearest().validate() || Backpack.isFull());
                }
            }

        return random.nextLong(1500, 2500);
    }

    private static long attemptHarvest(LocalPlayer player, String wispName) {
        if (player.getAnimationId() != 21228 && player.getAnimationId() != 31055) {
            EntityResultSet<Npc> wispsToHarvest = NpcQuery.newQuery().name(wispName).results();

            Npc nearestWisp = wispsToHarvest.nearest();
            if (nearestWisp != null) {
                Execution.delay(random.nextLong(500, 1250));
                boolean success = nearestWisp.interact("Harvest");
                if (success) {
                    log("[Divination] Harvesting " + wispName);
                    Execution.delay(random.nextLong(1500, 2500));
                }
            }
        }

        return random.nextLong(1500, 2500);
    }

    private static String getWispName(int divinationLevel) {
        if (divinationLevel >= 1 && divinationLevel <= 9) {
            List<String> options = List.of(
                    "Enriched pale wisp",
                    "Enriched pale spring",
                    "Pale spring",
                    "Pale wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 10 && divinationLevel <= 19) {
            List<String> options = List.of(
                    "Enriched flickering wisp",
                    "Enriched flickering spring",
                    "Flickering spring",
                    "Flickering wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 20 && divinationLevel <= 29) {
            List<String> options = List.of(
                    "Enriched bright wisp",
                    "Enriched bright spring",
                    "Bright spring",
                    "Bright wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 30 && divinationLevel <= 39) {
            List<String> options = List.of(
                    "Enriched glowing wisp",
                    "Enriched glowing spring",
                    "Glowing spring",
                    "Glowing wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 40 && divinationLevel <= 49) {
            List<String> options = List.of(
                    "Enriched sparkling wisp",
                    "Enriched sparkling spring",
                    "Sparkling spring",
                    "Sparkling wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 50 && divinationLevel <= 59) {
            List<String> options = List.of(
                    "Enriched gleaming wisp",
                    "Enriched gleaming spring",
                    "Gleaming spring",
                    "Gleaming wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 60 && divinationLevel <= 69) {
            List<String> options = List.of(
                    "Enriched vibrant wisp",
                    "Enriched vibrant spring",
                    "Vibrant spring",
                    "Vibrant wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 70 && divinationLevel <= 79) {
            List<String> options = List.of(
                    "Enriched lustrous wisp",
                    "Enriched lustrous spring",
                    "Lustrous spring",
                    "Lustrous wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 80 && divinationLevel <= 84) {
            List<String> options = List.of(
                    "Enriched brilliant wisp",
                    "Enriched brilliant spring",
                    "Brilliant spring",
                    "Brilliant wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 85 && divinationLevel <= 89) {
            List<String> options = List.of(
                    "Enriched radiant wisp",
                    "Enriched radiant spring",
                    "Radiant spring",
                    "Radiant wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 90 && divinationLevel <= 94) {
            List<String> options = List.of(
                    "Enriched luminous wisp",
                    "Enriched luminous spring",
                    "Luminous spring",
                    "Luminous wisp"
            );
            return findAvailableOption(options);
        } else if (divinationLevel >= 95 && divinationLevel <= 99) {
            List<String> options = Arrays.asList(
                    "Enriched incandescent wisp",
                    "Enriched incandescent spring",
                    "Incandescent spring",
                    "Incandescent wisp"
            );
            return findAvailableOption(options);
        }

        return null;
    }

    private static String findAvailableOption(List<String> options) {
        for (String option : options) {
            if (NpcQuery.newQuery().name(option).results().nearest() != null) {
                return option;
            }
        }
        return null;
    }

    private static Coordinate getWispCoordinates(int divinationLevel) {
        if (divinationLevel >= 1 && divinationLevel <= 9) {
            return new Coordinate(3120, 3219, 0); // Pale Wisps
        } else if (divinationLevel >= 10 && divinationLevel <= 19) {
            return new Coordinate(3002, 3403, 0); // Flickering Wisps
        } else if (divinationLevel >= 20 && divinationLevel <= 29) {
            return new Coordinate(3299, 3395, 0); // Bright Wisps
        } else if (divinationLevel >= 30 && divinationLevel <= 39) {
            return new Coordinate(2735, 3414, 0); // Glowing Wisps
        } else if (divinationLevel >= 40 && divinationLevel <= 49) {
            return new Coordinate(2766, 3597, 0); // Sparkling Wisps
        } else if (divinationLevel >= 50 && divinationLevel <= 59) {
            return new Coordinate(2884, 3050, 0); // Gleaming Wisps
        } else if (divinationLevel >= 60 && divinationLevel <= 69) {
            return new Coordinate(2416, 2866, 0); // Vibrant Wisps
        } else if (divinationLevel >= 70 && divinationLevel <= 79) {
            return new Coordinate(3462, 3540, 0); // Lustrous Wisps
        } else if (divinationLevel >= 80 && divinationLevel <= 84) {
            return new Coordinate(3403, 3300, 0); // Brilliant Wisps
        } else if (divinationLevel >= 85 && divinationLevel <= 89) {
            return new Coordinate(3799, 3556, 0); // Radiant Wisps
        } else if (divinationLevel >= 90 && divinationLevel <= 94) {
            return new Coordinate(3310, 2665, 0); // Luminous Wisps
        } else if (divinationLevel >= 95 && divinationLevel <= 99) {
            return new Coordinate(2285, 3047, 0); // Incandescent Wisps
        }

        return null;
    }

    private static void divineoMatic() { // 37521 = empty charges // 37522 = filled charges
        ResultSet<Item> divineomaticvacuum = InventoryItemQuery.newQuery(94).ids(41083).results();
        if (!divineomaticvacuum.isEmpty()) {
            int emptyCharges = VarManager.getInvVarbit(94, 3, 37521);
            int filledCharges = VarManager.getInvVarbit(94, 3, 37522);
        /*    log("[Divination] Empty Charges: " + emptyCharges + " - Filled Charges: " + filledCharges);*/
            if (filledCharges > ThreadLocalRandom.current().nextInt(25, 100) && Equipment.interact(Equipment.Slot.WEAPON, "Withdraw")) {
                log("[Divination] Divine-o-matic is full, withdrawing.");
                Execution.delayUntil(30000, () -> VarManager.getInvVarbit(94, 3, 37521) + VarManager.getInvVarbit(94, 3, 37522) < 100);
                log("[Divination] After withdrawal, Empty Charges: " + VarManager.getInvVarbit(94, 3, 37521) + " - Filled Charges: " + VarManager.getInvVarbit(94, 3, 37522));
                if (Backpack.contains("Divine charge (empty)") && emptyCharges < 100) {
                    log("[Divination] Adding all to vacuum.");
                    if (backpack.interact("Divine charge (empty)", "Add all to vacuum")) {
                        log("[Divination] After adding to vacuum, Empty Charges: " + VarManager.getInvVarbit(94, 3, 37521) + " - Filled Charges: " + VarManager.getInvVarbit(94, 3, 37522));
                    } else {
                        log("[Error] Failed to add all to vacuum.");
                    }
                } else {
                    log("[Error] No empty charges found in backpack, turning option off");
                    useDivineoMatic = false;
                }
            }
        }
    }

    public static int initialValue = VarManager.getInvVarbit(94, 3, 37522);
    public static int count = 0;

    public static void updateCount() {
        int currentValue = VarManager.getInvVarbit(94, 3, 37522);
        if (currentValue > initialValue) {
            count++;
        } else if (currentValue < initialValue) {
            count += 100 - initialValue + currentValue;
        }
        initialValue = currentValue;
    }

    private static int randomValue = 0;


    public static void checkAndPerformActions(LocalPlayer player) {
        randomValue = random.nextInt(2, 5);
        int familiarTime = VarManager.getVarbitValue(6055);

        if (familiarTime <= randomValue) {
            log("[Caution] Familiar time is " + familiarTime + ", we're going to summon.");

            ResultSet<Item> items = InventoryItemQuery.newQuery(93).results();
            Item restorePotion = items.stream()
                    .filter(item -> item.getName() != null &&
                            (item.getName().toLowerCase().contains("restore")))
                    .findFirst()
                    .orElse(null);

            if (restorePotion == null) {
                log("[Error]  No restore potions found in the backpack, teleporting to Prif.");
                Execution.delay(useBank(player));
            } else {
                boolean success = backpack.interact(restorePotion.getName(), "Drink");
                if (success) {
                    log("[Success] Successfully drank " + restorePotion.getName());
                    long delay = random.nextLong(1500, 3000);
                    Execution.delay(delay);
                } else {
                    log("[Error]  Failed to interact with " + restorePotion.getName());
                }
            }

            ResultSet<Item> results = InventoryItemQuery.newQuery(93).option("Summon").results();
            if (VarManager.getVarbitValue(6055) <= randomValue) {
                log("[Error] Familiar is already summoned.");
            } else {
                if (!results.isEmpty()) {
                    Item summonItem = results.first();
                    if (summonItem != null) {
                        String itemName = summonItem.getName();
                        backpack.interact(itemName, "Summon");
                        Execution.delayUntil(5000, () -> VarManager.getVarbitValue(6055) > 10);
                    }
                } else {
                    log("[Error] No Pouches found, using bank.");
                    Execution.delay(useBank(player));
                }
            }
        }
    }

    private static final Coordinate BANK_COORDINATE = new Coordinate(2215, 3357, 1);

    private static long useBank(LocalPlayer player) {
        if (player.isMoving()) {
            return random.nextLong(1500, 3000);
        }
        EntityResultSet<Npc> bankers = NpcQuery.newQuery().name("Banker").option("Bank").results();

        if (bankers.isEmpty()) {
            log("[Error] No banker found to interact with.");
            if (Movement.traverse(NavPath.resolve(BANK_COORDINATE)) == TraverseEvent.State.FINISHED) {
                bankers = NpcQuery.newQuery().name("Banker").option("Bank").results();
                if (bankers.isEmpty()) {
                    log("[Error] Failed to find banker after moving to bank.");
                    return random.nextLong(1500, 3000);
                }
                Npc nearestBanker = bankers.nearest();
                if (nearestBanker != null) {
                    if (nearestBanker.interact("Load Last Preset from")) {
                        Execution.delay(random.nextLong(4500, 6000));
                        return handleDivination(player);
                    } else {
                        log("[Error] Failed to interact with banker.");
                        return random.nextLong(1500, 3000);
                    }
                }
            }
        }
        return random.nextLong(1500, 3000);
    }
}