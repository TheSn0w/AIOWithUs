package net.botwithus.Herblore;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.AbstractMap.SimpleEntry;


import static net.botwithus.CustomLogger.log;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.component;
import static net.botwithus.Variables.Variables.dialog;

public class Herblore {
    public SnowsScript skeletonScript;

    public Herblore(SnowsScript script) {
        this.skeletonScript = script;
    }

    public static HerbloreRecipe getSelectedRecipe() {
        return SharedState.selectedRecipe;
    }

    private static final Random random = new Random();

    private static final Map<HerbloreRecipe, List<String>> potionMap = Map.ofEntries(
            new SimpleEntry<>(HerbloreRecipe.SUPREME_OVERLOADS, List.of("Overload (4)", "Crystal flask", "Super attack (4)", "Super strength (4)", "Super defence (4)", "Super ranging potion (4)", "Super magic potion (4)", "Super necromancy (4)")),
            new SimpleEntry<>(HerbloreRecipe.OVERLOADS, List.of("Extreme attack (3)", "Extreme strength (3)", "Extreme defence (3)", "Extreme magic (3)", "Extreme ranging (3)", "Clean torstol", "Extreme necromancy (3)")),
            new SimpleEntry<>(HerbloreRecipe.EXTREME_ATTACK, List.of("Super attack (3)", "Clean avantoe")),
            new SimpleEntry<>(HerbloreRecipe.EXTREME_STRENGTH, List.of("Super strength (3)", "Clean dwarf weed")),
            new SimpleEntry<>(HerbloreRecipe.EXTREME_DEFENCE, List.of("Super defence (3)", "Clean lantadyme")),
            new SimpleEntry<>(HerbloreRecipe.EXTREME_MAGIC, List.of("Super magic potion (3)", "Ground mud runes")),
            new SimpleEntry<>(HerbloreRecipe.EXTREME_RANGING, List.of("Super ranging potion (3)", "Grenwall spikes")),
            new SimpleEntry<>(HerbloreRecipe.EXTREME_NECROMANCY, List.of("Super necromancy (3)", "Ground miasma rune")),
            new SimpleEntry<>(HerbloreRecipe.SUPER_ATTACK, List.of("Irit potion (unf)", "Eye of newt")),
            new SimpleEntry<>(HerbloreRecipe.SUPER_STRENGTH, List.of("Kwuarm potion (unf)", "Limpwurt root")),
            new SimpleEntry<>(HerbloreRecipe.SUPER_DEFENCE, List.of("Cadantine potion (unf)", "White berries")),
            new SimpleEntry<>(HerbloreRecipe.SUPER_MAGIC, List.of("Lantadyme potion (unf)", "Potato cactus")),
            new SimpleEntry<>(HerbloreRecipe.SUPER_RANGED, List.of("Dwarf weed potion (unf)", "Wine of Zamorak")),
            new SimpleEntry<>(HerbloreRecipe.SUPER_NECROMANCY, List.of("Spirit weed potion (unf)", "Congealed blood"))
    );

    public enum HerbloreRecipe {
        SUPREME_OVERLOADS,
        OVERLOADS,
        EXTREME_POTIONS,
        NECROMANCY_POTIONS,
        EXTREME_ATTACK,
        EXTREME_STRENGTH,
        EXTREME_DEFENCE,
        EXTREME_MAGIC,
        EXTREME_RANGING,
        EXTREME_NECROMANCY,
        SUPER_ATTACK,
        SUPER_STRENGTH,
        SUPER_DEFENCE,
        SUPER_MAGIC,
        SUPER_RANGED,
        SUPER_NECROMANCY
    }


    public static long handleHerblore(LocalPlayer player) {
        HerbloreRecipe selectedRecipe = SharedState.selectedRecipe;
        SceneObject portable = SceneObjectQuery.newQuery().name("Portable well").results().nearest();

        if (Interfaces.isOpen(1251)) {
            return random.nextLong(600, 800);
        }
        if (Interfaces.isOpen(1370)) {
            dialog(0, -1, 89784350);
            return random.nextLong(750, 1250);
        }

        switch (selectedRecipe) {
            case SUPREME_OVERLOADS:
                if (Backpack.getCount("Overload (4)") == 3 && Backpack.getCount("Crystal flask") == 3 && Backpack.getCount("Super attack (4)") == 3 &&
                        Backpack.getCount("Super strength (4)") == 3 && Backpack.getCount("Super defence (4)") == 3 && Backpack.getCount("Super ranging potion (4)") == 3 &&
                        Backpack.getCount("Super magic potion (4)") == 3 && Backpack.getCount("Super necromancy (4)") == 3) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case OVERLOADS:
                if (Backpack.getCount("Extreme attack (3)") == 4 && Backpack.getCount("Extreme strength (3)") == 4 &&
                        Backpack.getCount("Extreme defence (3)") == 4 && Backpack.getCount("Extreme magic (3)") == 4 &&
                        Backpack.getCount("Extreme ranging (3)") == 4 && Backpack.getCount("Clean torstol") == 4 && Backpack.getCount("Extreme necromancy (3)") == 4) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                        shutdown();
                    }
                } else {
                    return handleBankOperations(player);
                }
                break;
            case EXTREME_ATTACK:
                if (Backpack.getCount("Super attack (3)") == 14 && Backpack.getCount("Clean avantoe") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_STRENGTH:
                if (Backpack.getCount("Super strength (3)") == 14 && Backpack.getCount("Clean dwarf weed") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_DEFENCE:
                if (Backpack.getCount("Super defence (3)") == 14 && Backpack.getCount("Clean lantadyme") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_MAGIC:
                if (Backpack.getCount("Super magic potion (3)") == 14 && Backpack.getCount("Ground mud runes") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_RANGING:
                if (Backpack.getCount("Super ranging potion (3)") == 14 && Backpack.getCount("Grenwall spikes") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_NECROMANCY:
                if (Backpack.getCount("Super necromancy (3)") == 14 && Backpack.getCount("Ground miasma rune") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_ATTACK:
                if (Backpack.getCount("Irit potion (unf)") == 14 && Backpack.getCount("Eye of newt") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Attack.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_STRENGTH:
                if (Backpack.getCount("Kwuarm potion (unf)") == 14 && Backpack.getCount("Limpwurt root") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Strength.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_DEFENCE:
                if (Backpack.getCount("Cadantine potion (unf)") == 14 && Backpack.getCount("White berries") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Defence.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_MAGIC:
                if (Backpack.getCount("Lantadyme potion (unf)") == 14 && Backpack.getCount("Potato cactus") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Magic.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_RANGED:
                if (Backpack.getCount("Dwarf weed potion (unf)") == 14 && Backpack.getCount("Wine of Zamorak") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Ranged.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_NECROMANCY:
                if (Backpack.getCount("Spirit weed potion (unf)") == 14 && Backpack.getCount("Congealed blood") == 14) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Necromancy.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            default:
                log("[Error] Unknown recipe.");
                return random.nextLong(600, 800);
        }

        return random.nextLong(600, 800);
    }


    private static long handleBankInteraction(LocalPlayer player) {
        EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Load Last Preset from").results();
        if (!results.isEmpty()) {
            log("[Herblore] Loading last preset from banker");
            results.nearest().interact("Load Last Preset from");
            return random.nextLong(750, 1050);
        } else {
            EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery().name("Bank chest").option("Load Last Preset from").results();
            if (!chestResults.isEmpty()) {
                log("[Herblore] Loading last preset from bank chest");
                chestResults.nearest().interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            }
        }
    return random.nextLong(750, 1050);
    }

    private static long handleBankOperations(Player player) {
        if (player != null && player.isMoving()) {
            return random.nextLong(600, 800);
        }

        SimpleEntry<SceneObject, String> bankAndOption = findBankBooth();

        if (bankAndOption != null) {
            SceneObject bank = bankAndOption.getKey();
            String option = bankAndOption.getValue();
            if (bank != null) {
                return handleBankInteractions(bank, option);
            } else {
                log("[Error] Bank not found");
            }
        }

        return random.nextLong(600, 800);
    }

    private static long handleBankInteractions(SceneObject bank, String option) {
        if (bank.interact("Load Last Preset from")) {
            log("[Herblore] Interacting with Bank using 'Load Last Preset'");

            HerbloreRecipe selectedRecipe = SharedState.selectedRecipe;
            List<String> requiredItems = potionMap.get(selectedRecipe);

            if (requiredItems != null && Backpack.containsAllOf(requiredItems.toArray(new String[0]))) {
                return random.nextLong(750, 1050);
            } else {
                log("[Error] No potions in backpack - Loading preset 9");
                Bank.loadPreset(9);
                Execution.delayUntil(10000, () -> Backpack.containsAllOf(requiredItems.toArray(new String[0])));

                if (Backpack.containsAllOf(requiredItems.toArray(new String[0]))) {
                    return random.nextLong(750, 1050);
                } else {
                    log("[Error] Still no potions in backpack - creating new preset");
                    return handleBankWithdrawals(new SimpleEntry<>(bank, option));
                }
            }
        }
        return random.nextLong(750, 1050);
    }


    private static SimpleEntry<SceneObject, String> findBankBooth() {
        SceneObject bankBooth = SceneObjectQuery.newQuery()
                .name("Bank booth")
                .option("Bank")
                .results()
                .nearest();

        if (bankBooth != null) {
            return new SimpleEntry<>(bankBooth, "Bank");
        }

        SceneObject bankChest = SceneObjectQuery.newQuery()
                .name("Bank chest")
                .option("Use")
                .results()
                .nearest();

        if (bankChest != null) {
            return new SimpleEntry<>(bankChest, "Use");
        }

        return null;
    }

    private static boolean waitForBankToOpen() {
        boolean isBankOpen = Execution.delayUntil(30000, () -> Bank.isOpen());
        if (isBankOpen) {
            log("[Herblore] Bank interface is open");
        }
        return isBankOpen;
    }

    private static void handleDeposits() {
        List<Item> itemsInBackpack = Backpack.getItems();
        Map<String, Integer> itemCounts = itemsInBackpack.stream()
                .collect(Collectors.groupingBy(Item::getName, Collectors.summingInt(Item::getStackSize)));

        if (!itemCounts.isEmpty()) {
            log("[Herblore] Depositing items:");
            itemCounts.forEach((name, stackSize) -> {
                log("[Herblore] - " + name + " (Stack size: " + stackSize + ")");
            });
            Bank.depositAll();
            Execution.delay(random.nextLong(500, 1000));
        } else {
            log("[Error] No depositable items found in the Backpack");
        }
    }
    private static boolean handleWithdrawals() {
        HerbloreRecipe selectedRecipe = SharedState.selectedRecipe;

        List<String> potionsToWithdraw = potionMap.get(selectedRecipe);

        if (potionsToWithdraw != null && !potionsToWithdraw.isEmpty()) {
            for (String potionName : potionsToWithdraw) {

                if (InventoryItemQuery.newQuery().category(93).results().size() >= 28) {
                    log("[Herblore] Backpack is full.");
                    return false;
                }

                if (Bank.isOpen() && !InventoryItemQuery.newQuery(95).name(potionName).results().isEmpty()) {
                    log("[Herblore] Withdrawing: " + potionName);
                    int potionslot = getPotionSlot(potionName);
                    component(1, potionslot, 33882307);
                    log("[Herblore] Successfully withdrew: " + potionName);
                    Execution.delay(random.nextLong(600, 750));
                } else {
                    log("[Error] " + potionName + " does not exist in the bank.");
                    break;
                }

                if (Bank.isOpen()) {
                    if (VarManager.getVarbitValue(45189) != 2) {
                        component(1, -1, 33882205);
                    }
                }
            }
        } else {
            log("[Error] No available potions found in the bank.");
        }

        return true;
    }

    private static int getPotionSlot(String potionName) {
        ResultSet<Item> potion = InventoryItemQuery.newQuery(95).name(potionName).results();
        if (potion != null && !potion.isEmpty()) {
            return potion.first().getSlot();
        } else {
            log("[Error] Potion with name " + potionName + " not found in the bank.");
            return -1;
        }
    }
   // Based on IDs
   /* private static final Map<HerbloreRecipe, List<Integer>> potionMaps = Map.ofEntries(
            new SimpleEntry<>(HerbloreRecipe.OVERLOADS, List.of(15309, 15313, 15317, 15325, 15321, 55326, 269)) // Replace these numbers with the actual item IDs
    );

    private static boolean handleWithdrawals() {
        HerbloreRecipe selectedRecipe = SharedState.selectedRecipe;

        List<Integer> potionsToWithdraw = potionMaps.get(selectedRecipe);

        if (potionsToWithdraw != null && !potionsToWithdraw.isEmpty()) {
            for (Integer potionId : potionsToWithdraw) { // Loop to iterate over potions
                // Check if backpack is full
                if (InventoryItemQuery.newQuery().category(93).results().size() >= 28) {
                    log("[Herblore] Backpack is full.");
                    return false; // Exit the method if the backpack is full
                }

                if (Bank.isOpen() && !InventoryItemQuery.newQuery(95).ids(potionId).results().isEmpty()) {
                    log("[Herblore] Withdrawing: " + potionId);
                    int potionslot = getPotionSlot(potionId);
                    component(1, potionslot, 33882307);
                    log("[Herblore] Successfully withdrew: " + potionId);
                    Execution.delay(random.nextLong(600, 750));
                } else {
                    log("[Error] " + potionId + " does not exist in the bank.");
                    break;
                }

                // Close and reopen the bank to refresh the potions
                if (Bank.isOpen()) {
                    if (VarManager.getVarbitValue(45189) != 2) {
                        component(1, -1, 33882205);
                    }
                }
            }

            // Include component interaction logic if needed
        } else {
            log("[Error] No available potions found in the bank.");
        }

        return true;
    }
    private static int getPotionSlot(int itemId) {
        ResultSet<Item> potion = InventoryItemQuery.newQuery(95).ids(itemId).results();
        if (potion != null && !potion.isEmpty()) {
            return potion.first().getSlot();
        } else {
            log("[Error] Potion with ID " + itemId + " not found in the bank.");
            return -1;
        }
    }*/

    private static long handleBankWithdrawals(SimpleEntry<SceneObject, String> bankAndOption) {
        SceneObject bank = bankAndOption.getKey();
        String option = bankAndOption.getValue();
        if (bank.interact(option)) {
            log("[Herblore] Interacting with Bank using " + option + " action");
            Execution.delay(random.nextLong(500, 1000));
            if (waitForBankToOpen()) {
                Execution.delay(random.nextLong(500, 1000));
                handleDeposits();
                Execution.delay(random.nextLong(500, 1000));

                // Determine how many times to call handleWithdrawals based on the selected recipe
                int timesToCallHandleWithdrawals = 1;
                if (SharedState.selectedRecipe == HerbloreRecipe.OVERLOADS) {
                    timesToCallHandleWithdrawals = 4;
                }
                if (SharedState.selectedRecipe == HerbloreRecipe.SUPREME_OVERLOADS) {
                    timesToCallHandleWithdrawals = 3;
                }
                if (SharedState.selectedRecipe == HerbloreRecipe.EXTREME_ATTACK || SharedState.selectedRecipe == HerbloreRecipe.EXTREME_STRENGTH ||
                        SharedState.selectedRecipe == HerbloreRecipe.EXTREME_DEFENCE || SharedState.selectedRecipe == HerbloreRecipe.EXTREME_MAGIC ||
                        SharedState.selectedRecipe == HerbloreRecipe.EXTREME_RANGING || SharedState.selectedRecipe == HerbloreRecipe.EXTREME_NECROMANCY) {
                    timesToCallHandleWithdrawals = 14;
                }

                for (int i = 0; i < timesToCallHandleWithdrawals; i++) {
                    boolean success = handleWithdrawals();
                    Execution.delay(random.nextLong(500, 1000));
                    if (!success) {
                        return random.nextLong(750, 1050);
                    }
                }

                component(2, 1, 33882231); // save preset
                Execution.delay(random.nextLong(500, 1000));
                return random.nextLong(750, 1050);
            }
        }
        return random.nextLong(750, 1050);
    }
}