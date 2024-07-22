package net.botwithus;

import ImGui.SnowScriptGraphics;
import ImGui.Stopwatch;
import net.botwithus.Combat.Combat;
import net.botwithus.Combat.LootManager;
import net.botwithus.Cooking.Cooking;
import net.botwithus.Divination.Divination;
import net.botwithus.Variables.GlobalState;
import net.botwithus.Variables.Runnables;
import net.botwithus.Variables.Variables;
import net.botwithus.api.game.hud.inventories.LootInventory;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.EventBus;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.events.impl.ServerTickedEvent;
import net.botwithus.rs3.game.*;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.rs3.script.events.PropertyUpdateRequestEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static ImGui.PredefinedStrings.CombatList;
import static ImGui.Skills.CombatImGui.*;
import static ImGui.Theme.*;
import static net.botwithus.Archaeology.Banking.BankforArcheology;
import static net.botwithus.Combat.Banking.bankToWars;
import static net.botwithus.Combat.Combat.*;
import static net.botwithus.Combat.CombatManager.*;
import static net.botwithus.Combat.Familiar.useFamiliarForCombat;
import static net.botwithus.Combat.Familiar.useFamiliarScrolls;
import static net.botwithus.Combat.ItemRemover.dropItems;
import static net.botwithus.Combat.ItemRemover.isDropActive;
import static net.botwithus.Combat.LootManager.*;
import static net.botwithus.Combat.NPCs.updateNpcTableData;
import static net.botwithus.Combat.Notepaper.*;
import static net.botwithus.Combat.Potions.*;
import static net.botwithus.Combat.Radius.*;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Divination.Divination.checkAccountType;
import static net.botwithus.Misc.Fletching.makeDinarrow;
import static net.botwithus.Misc.Harps.useHarps;
import static net.botwithus.Misc.Necro.handleNecro;
import static net.botwithus.Runecrafting.Abyss.*;
import static net.botwithus.Runecrafting.Astral.useAstralAltar;
import static net.botwithus.Runecrafting.Runecrafting.*;
import static net.botwithus.Slayer.Main.doSlayer;
import static net.botwithus.Slayer.Main.useBankPin;
import static net.botwithus.Slayer.WarsRetreat.slayerPointFarming;
import static net.botwithus.TaskScheduler.*;
import static net.botwithus.Variables.BankInteractions.performBanking;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;


public class SnowsScript extends LoopingScript {
    public static BotState botState = BotState.IDLE;
    public static long runStartTime;
    public static Instant startTime;

    public static void setBotState(BotState state) {
        botState = state;
    }
    public static BotState getBotState() {
        return botState;
    }



    public enum BotState {
        IDLE,
        SKILLING,
        BANKING,
        BANKPIN,
        PAUSED,
    }



    public SnowsScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new SnowScriptGraphics(getConsole(), this);
        startTime = Instant.now();
        runStartTime = System.currentTimeMillis();
        loadConfiguration();

        skillingTasks.put(() -> Variables.isHerbloreActive, Runnables::handleHerblore);
        skillingTasks.put(() -> Variables.isRunecraftingActive, Runnables::handleRunecrafting);
        skillingTasks.put(() -> Variables.isMiningActive, Runnables::handleMining);
        skillingTasks.put(() -> Variables.isThievingActive, Runnables::handleThieving);
        skillingTasks.put(() -> Variables.isWoodcuttingActive, Runnables::handleWoodcutting);
        skillingTasks.put(() -> Variables.isAgilityActive, Runnables::handleSkillingAgility);
        skillingTasks.put(() -> Variables.isDivinationActive, Runnables::handleDivination);
        skillingTasks.put(() -> Variables.isFishingActive, Runnables::handleFishing);
        skillingTasks.put(() -> Variables.isCombatActive, Runnables::handleCombat);
        skillingTasks.put(() -> Variables.isCookingActive, Runnables::handleCooking);
        skillingTasks.put(() -> Variables.isArcheologyActive, Runnables::handleArcheology);
        skillingTasks.put(() -> Variables.isMiscActive, Runnables::handleMisc);
    }

    public static Divination.AccountType getAccountType() {
        if (checkAccountType(Divination.AccountType.IRONMAN)) {
            return Divination.AccountType.IRONMAN;
        } else if (checkAccountType(Divination.AccountType.HARDCORE)) {
            return Divination.AccountType.HARDCORE;
        } else if (checkAccountType(Divination.AccountType.REGULAR)) {
            return Divination.AccountType.REGULAR;
        } else if (checkAccountType(Divination.AccountType.HARDIRON)) {
            return Divination.AccountType.HARDIRON;
        }
        return null;
    }

    private boolean scriptActive = false;

    private void onPropertyUpdateRequest(PropertyUpdateRequestEvent event) {
        getConsole().addLineToConsole("Received PropertyUpdateRequestEvent: " + event.getValue());
        boolean newState = Boolean.parseBoolean(event.getValue());
        if (newState != this.scriptActive) {
            scriptActive = newState;
            this.setActive(newState);
        }
    }


    public void onLoop() {
        LocalPlayer player = getLocalPlayer();

        if(LoginManager.isLoginInProgress()) {
            return;
        }

        capturestuff();

        if (isDropActive) {
            dropItems();
        }

        updateNpcTableData(player);


        switch (botState) {
            case IDLE -> Execution.delay(random.nextLong(1500, 3000));
            case SKILLING -> skillingTasks.forEach((condition, task) -> {
                if (condition.getAsBoolean()) {
                    task.run();
                }
            });

            case BANKING -> {
                if (nearestBank && !isCombatActive) {
                    Execution.delay(performBanking(player));
                }
                if (isArcheologyActive) {
                    Execution.delay(BankforArcheology(player, selectedArchNames));
                }
                if (nearestBank && isCombatActive) {
                    bankToWars(player);
                }
            }
        }
    }


    private void capturestuff() {
        EntityResultSet<Npc> npcResults = NpcQuery.newQuery()
                .name("Seren spirit", "Divine blessing", "Catalyst of alteration", "Fire spirit", "Forge phoenix")
                .results();

        if (npcResults.isEmpty()) {
            return;
        }

        Npc npc = npcResults.nearest();
        if (npc != null) {
            String npcName = npc.getName();
            String interactionOption;

            switch (npcName) {
                case "Seren spirit":
                case "Divine blessing":
                case "Catalyst of alteration":
                    interactionOption = "Capture";
                    break;
                case "Fire spirit":
                case "Forge phoenix":
                    interactionOption = "Collect reward";
                    break;
                default:
                    log("[Error] Unknown NPC found: " + npcName);
                    return;
            }
            Execution.delay(random.nextLong(1500, 3000));
            npc.interact(interactionOption);
            log("[Info] Interacting with " + npcName + " using " + interactionOption + ".");
        } else {
            log("[Error] Failed to find nearest NPC.");
        }
    }

    @Override
    public void onActivation() {
        super.initialize();
    }



    @Override
    public void onDeactivation() {
        if (SnowScriptGraphics.combatThread != null) {
            SnowScriptGraphics.combatThread.interrupt();
            log("[Thread] Stopped CombatAbilities thread");
        }
        if (SnowScriptGraphics.lootManagerThread != null) {
            SnowScriptGraphics.lootManagerThread.interrupt();
            log("[Thread] Stopped LootManager thread");
        }
        ScriptisOn = false;

        unsubscribeAll();
        super.onDeactivation();
    }

    public void subscribeToEvents() {
        EventBus.EVENT_BUS.subscribe(this, ChatMessageEvent.class, this::onChatMessageEvent);
        EventBus.EVENT_BUS.subscribe(this, InventoryUpdateEvent.class, this::onInventoryUpdate);
        EventBus.EVENT_BUS.subscribe(this, PropertyUpdateRequestEvent.class, this::onPropertyUpdateRequest);
    }



    public static int tick = 0;
    private void onTickEvent(ServerTickedEvent event) {
        tick = event.getTicks();
    }


    public static Map<String, Integer> divineCharges = new HashMap<>();
    public static Map<String, Integer> Gems = new HashMap<>();
    public static Map<String, Integer> steamRunes = new HashMap<>();


    private void onInventoryUpdate(InventoryUpdateEvent event) {
        if (!isActive()) {
            return;
        }
        if (event.getInventoryId() != 93) {
            return;
        }
        if (isdivinechargeActive) {
            String itemName = event.getNewItem().getName();
            if ("Divine charge".equals(itemName)) {
                int oldCount = event.getOldItem() != null ? event.getOldItem().getStackSize() : 0;
                int newCount = event.getNewItem().getStackSize();
                if (newCount > oldCount) {
                    int newDivineCharges = newCount - oldCount;
                    int existingCharges = divineCharges.getOrDefault(itemName, 0);
                    divineCharges.put(itemName, existingCharges + newDivineCharges);
                }
            }
        }
        if (isArcheologyActive) {
            String itemName = event.getNewItem().getName();
            if (itemName != null && !itemName.contains("soil box") && !itemName.contains("porter")) {
                int count = materialTypes.getOrDefault(itemName, 0);
                materialTypes.put(itemName, count + 1);
            }
        }
        if (isThievingActive) {
            String itemName = event.getNewItem().getName();
            if (itemName != null) {
                int count = materialTypes.getOrDefault(itemName, 0);
                materialTypes.put(itemName, count + 1);
            }
        }
        if (isCorruptedOreActive) {
            String itemName = event.getNewItem().getName();
            if ("Corrupted ore".equals(itemName)) {
                int oldCount = event.getOldItem().getStackSize();
                int newCount = event.getNewItem().getStackSize();
                if (newCount < oldCount) {
                    int count = corruptedOre.getOrDefault(itemName, 0);
                    corruptedOre.put(itemName, count + 1);
                }
            }
        }
        if (isDivinationActive) {
            String itemName = event.getNewItem().getName();
            if (itemName.contains("energy")) {
                int oldCount = event.getOldItem() != null ? event.getOldItem().getStackSize() : 0;
                int newCount = event.getNewItem().getStackSize();
                if (newCount > oldCount) {
                    int quantity = newCount - oldCount;

                    int currentCount = Variables.energy.getOrDefault(itemName, 0);

                    energy.put(itemName, currentCount + quantity);
                }
            }
        }
        if (handleHarps) {
            String itemName = event.getNewItem().getName();
            if (itemName != null && itemName.equals("Harmonic dust")) {
                int oldCount = event.getOldItem() != null ? event.getOldItem().getStackSize() : 0;
                int newCount = event.getNewItem().getStackSize();
                if (newCount > oldCount) {
                    int quantity = newCount - oldCount;
                    int currentCount = harmonicDust.getOrDefault(itemName, 0);
                    harmonicDust.put(itemName, currentCount + quantity);
                }
            }
        }
        if (isSmeltingActive) {
            String newItemName = event.getNewItem().getName();
            List<String> excludedItems = Arrays.asList("Enchanted gem", "Dragonstone", "Sapphire", "Emerald", "Ruby", "Diamond", "Onyx", "Opal", "Jade", "Red topaz");
            if (newItemName != null && !excludedItems.contains(newItemName)) {
                int currentCount = smeltedItems.getOrDefault(newItemName, 0);
                smeltedItems.put(newItemName, currentCount + 1);
                log("[Smelter] Created " + newItemName);
            }
        }
        if (makeDinarrow) {
            String itemName = event.getNewItem().getName();
            if ("Headless dinarrow".equals(itemName)) {
                int oldCount = event.getOldItem() != null ? event.getOldItem().getStackSize() : 0;
                int newCount = event.getNewItem().getStackSize();
                if (newCount > oldCount) {
                    int newHeadlessDinarrows = newCount - oldCount;
                    int existingQuantity = headlessDinarrows.getOrDefault(itemName, 0);
                    headlessDinarrows.put(itemName, existingQuantity + newHeadlessDinarrows);
                }
            }

            if ("Dinarrow".equals(itemName)) {
                int oldCount = event.getOldItem() != null ? event.getOldItem().getStackSize() : 0;
                int newCount = event.getNewItem().getStackSize();
                if (newCount > oldCount) {
                    int newDinarrows = newCount - oldCount;
                    int existingQuantity = dinarrows.getOrDefault(itemName, 0);
                    dinarrows.put(itemName, existingQuantity + newDinarrows);
                }
            }
        }
        if (isCombatActive) {
            if (getBotState() == BotState.SKILLING) {
                String itemName = event.getNewItem().getName();
                int oldCount = event.getOldItem() != null ? event.getOldItem().getStackSize() : 0;
                int newCount = event.getNewItem().getStackSize();
                if (newCount > oldCount) {
                    int quantity = newCount - oldCount;
                    int currentCount = lootedItems.getOrDefault(itemName, 0);
                    lootedItems.put(itemName, currentCount + quantity);

                }
            }
        }
    }


    public static final Map<String, Integer> lootedItems = new HashMap<>();
    public static Map<String, Integer> headlessDinarrows = new HashMap<>();
    public static Map<String, Integer> dinarrows = new HashMap<>();
    public static int tunePercentage = 20;
    public static Map<String, Integer> smeltedItems = new HashMap<>();
    public static Map<String, Integer> necroItemsAdded = new HashMap<>();
    public static Map<String, Integer> materialsGained = new HashMap<>();
    Queue<String> lastTwoMessages = new LinkedList<>();






    void onChatMessageEvent(ChatMessageEvent event) {
        LocalPlayer player = getLocalPlayer();
        if (!isActive()) {
            return;
        }
        String message = event.getMessage();
        if (isDissasemblerActive) {
            if (message.contains("Materials gained:")) {
                String[] parts = message.split(": ");
                if (parts.length > 1) {
                    String[] materials = parts[1].split(", ");
                    for (String material : materials) {
                        String[] materialParts = material.split(" x ");
                        if (materialParts.length > 1) {
                            String materialName = materialParts[1].trim();
                            int materialCount = Integer.parseInt(materialParts[0].trim());
                            int existingCount = materialsGained.getOrDefault(materialName, 0);
                            materialsGained.put(materialName, existingCount + materialCount);
                        }
                    }
                }
            }
            if (message.contains("You don't have enough")) {
                log("[Error] You have run out of supplies, logging off");
                shutdown();
            }
        }

        if (handleNecro) {
            if (message.contains("The following reward is added to the ritual chest:")) {
                String[] parts = message.split(": ");
                if (parts.length > 1) {
                    String[] itemParts = parts[1].split("x ");
                    if (itemParts.length > 1) {
                        String itemType = itemParts[1].trim();
                        itemType = itemType.replace("</col>", "");

                        if (lastTwoMessages.size() == 2) {
                            lastTwoMessages.poll();
                        }
                        lastTwoMessages.add(message);

                        int itemCount = Integer.parseInt(itemParts[0].trim());
                        int currentCount = necroItemsAdded.getOrDefault(itemType, 0);
                        necroItemsAdded.put(itemType, currentCount + itemCount);
                    }
                }
            }
            if (message.contains("You need to choose a focus object before starting a ritual.")) {
                log("[Error] You have run out of supplies in your Focus Storage, logging off");
                shutdown();
            }
            if (message.contains("You need the following materials to repair")) {
                log("[Error] You are missing materials to repair, logging off");
                shutdown();
            }
        }

        if (handleHarps) {
            if (message.contains("Your harp is ")) {
                String percentageStr = message.substring(message.indexOf("Your harp is ") + 13, message.indexOf("% out of tune"));
                int currentTunePercentage = Integer.parseInt(percentageStr);
                if (currentTunePercentage >= tunePercentage) {
                    useHarps(player);
                }
            }
        }
        if (isSiftSoilActive) {
            if (message.contains("Item could not be found")) {
                log("[Error] Item could not be found, logging off");
                shutdown();
            }
        }
        if (isGemCutterActive) {
            if (message.startsWith("You cut the")) {
                String gemName = message.substring(11, message.length() - 1);
                int count = Gems.getOrDefault(gemName, 0);
                Gems.put(gemName, count + 1);
            }
        }
        if (isportermakerActive) {
            if (message.contains("You create: 1")) {
                String itemType = message.substring(message.indexOf("1") + 2).trim();
                itemType = itemType.replace(".", "");
                int count = portersMade.getOrDefault(itemType, 0);
                portersMade.put(itemType, count + 1);
            }
        }
        if (isCookingActive) {
            if (message.contains("You successfully cook")) {
                String fishType = message.substring(message.lastIndexOf("cook") + 5).trim();
                int count = fishCookedCount.getOrDefault(fishType, 0);
                fishCookedCount.put(fishType, count + 1);
            }
            if (message.contains("Your extreme cooking potion is about to wear off.")) {
                Cooking cooking = new Cooking();
                cooking.cookingPotion();
            }
        }
        if (isWoodcuttingActive) {
            if (message.contains("You get some")) {
                String logType = message.substring(message.lastIndexOf("some") + 5).trim();
                logType = logType.replace(".", "");
                int count = logCount.getOrDefault(logType, 0);
                logCount.put(logType, count + 1);
            }
            if (message.toLowerCase().contains("bird's nest")) {
                int count = nestCount.getOrDefault("Bird's nest", 0);
                nestCount.put("Bird's nest", count + 1);
            }
            if (message.contains("You transport the following item to your bank:")) {
                String logType = message.substring(message.lastIndexOf("bank:") + 6).trim();
                logType = logType.replace(".", "");
                int count = logCount.getOrDefault(logType, 0);
                logCount.put(logType, count + 1);
            }
            if (message.contains("As you cut from the tree, the log:")) {
                String logType = message.substring(message.lastIndexOf("log:") + 7).trim();
                logType = logType.replace(".", "");
                int count = logCount.getOrDefault(logType, 0);
                logCount.put(logType, count + 1);
            }
            if (message.contains("Crystallise takes your resource and converts it into XP.")) {
                String key = "Resources";
                int count = logCount.getOrDefault(key, 0);
                logCount.put(key, count + 1);
            }
        }
        if (isFishingActive) {
            if (message.contains("You catch a")) {
                String[] words = message.split(" ");
                int startIndex = 0;
                for (int i = 0; i < words.length; i++) {
                    if (words[i].equals("a")) {
                        startIndex = i + 1;
                        break;
                    }
                }
                String fishType = String.join(" ", Arrays.copyOfRange(words, startIndex, words.length));
                fishType = fishType.split("Already cooked")[0].trim();
                fishType = fishType.replace(".", "");
                int count = fishCaughtCount.getOrDefault(fishType, 0);
                fishCaughtCount.put(fishType, count + 1);
            }
            if (message.contains("You catch some")) {
                String[] words = message.split(" ");
                int startIndex = 0;
                for (int i = 0; i < words.length; i++) {
                    if (words[i].equals("some")) {
                        startIndex = i + 1;
                        break;
                    }
                }
                String fishType = String.join(" ", Arrays.copyOfRange(words, startIndex, words.length));
                fishType = fishType.split("Already cooked")[0].trim();
                fishType = fishType.replace(".", "");
                int count = fishCaughtCount.getOrDefault(fishType, 0);
                fishCaughtCount.put(fishType, count + 1);
            }
        }
        if (isArcheologyActive) {
            if (message.contains("You transport the following item to your material storage:")) {
                String[] parts = message.split("storage: ");
                if (parts.length > 1) {
                    String materialType = parts[1].trim();
                    materialType = materialType.replace(".", "");
                    int count = materialsExcavated.getOrDefault(materialType, 0);
                    materialsExcavated.put(materialType, count + 1);
                }
            }
            if (message.contains("You find some")) {
                String[] parts = message.split("some ");
                if (parts.length > 1) {
                    String materialType = parts[1].trim();
                    materialType = materialType.replace(".", "");
                    int count = materialsExcavated.getOrDefault(materialType, 0);
                    materialsExcavated.put(materialType, count + 1);
                }
            }
        }
        if (isDivinationActive) {
            if (message.contains("You capture the chronicle fragment and place it in your inventory")) {
                String chronicleType = "Chronicle fragment";
                int count = chroniclesCaughtCount.getOrDefault(chronicleType, 0);
                chroniclesCaughtCount.put(chronicleType, count + 1);
            }
            if (message.contains("Your divination outfit")) {
                String chronicleType = "Chronicle fragment";
                int count = chroniclesCaughtCount.getOrDefault(chronicleType, 0);
                chroniclesCaughtCount.put(chronicleType, count + 2);
            }
        }
    }

    public static void setLastSkillingLocation(Coordinate location) {
        lastSkillingLocation = location;
    }



    public void saveConfiguration() {
        this.configuration.addProperty("InvokeDeath", String.valueOf(useInvokeDeath));
        this.configuration.addProperty("SpecialAttack", String.valueOf(useEssenceofFinality));
        this.configuration.addProperty("VolleyofSouls", String.valueOf(useVolleyofSouls));
        this.configuration.addProperty("DeathGrasp", String.valueOf(useWeaponSpecialAttack));
        this.configuration.addProperty("isAgilityActive", String.valueOf(isAgilityActive));
        this.configuration.addProperty("isDivinationActive", String.valueOf(isDivinationActive));
        this.configuration.addProperty("isMiningActive", String.valueOf(isMiningActive));
        this.configuration.addProperty("isWoodcuttingActive", String.valueOf(isWoodcuttingActive));
        this.configuration.addProperty("isFishingActive", String.valueOf(isFishingActive));
        this.configuration.addProperty("isCombatActive", String.valueOf(isCombatActive));
        this.configuration.addProperty("isArcheologyActive", String.valueOf(isArcheologyActive));
        this.configuration.addProperty("isThievingActive", String.valueOf(isThievingActive));
        this.configuration.addProperty("iseatFoodActive", String.valueOf(BankforFood));
        this.configuration.addProperty("isCookingActive", String.valueOf(isCookingActive));
        this.configuration.addProperty("useOverloads", String.valueOf(useOverloads));
        this.configuration.addProperty("usePrayerPots", String.valueOf(usePrayerPots));
        this.configuration.addProperty("useAggroPots", String.valueOf(useAggroPots));
        this.configuration.addProperty("MaterialCache", String.valueOf(MaterialCache));
        this.configuration.addProperty("offerChronicles", String.valueOf(offerChronicles));
        this.configuration.addProperty("Combat", String.valueOf(isCombatActive));
        this.configuration.addProperty("SoulSplit", String.valueOf(SoulSplit));
        this.configuration.addProperty("KeepArmyup", String.valueOf(useConjureUndeadArmy));
        String targetNamesSerialized = String.join(",", targetNames);
        this.configuration.addProperty("TargetNames", targetNamesSerialized);
        String ExcavationNamesSerialized = String.join(",", selectedArchNames);
        this.configuration.addProperty("selectedArchNames", ExcavationNamesSerialized);
        String MiningNamesSerialized = String.join(",", selectedRockNames);
        this.configuration.addProperty("selectedRockNames", MiningNamesSerialized);
        String FishingLocationsSerialized = String.join(",", selectedFishingLocations);
        this.configuration.addProperty("selectedFishingLocations", FishingLocationsSerialized);
        String FishingActionsSerialized = String.join(",", selectedFishingActions);
        this.configuration.addProperty("selectedFishingActions", FishingActionsSerialized);
        String WoodcuttingNamesSerialized = String.join(",", selectedTreeNames);
        this.configuration.addProperty("selectedTreeNames", WoodcuttingNamesSerialized);
        String EatingFoodNamesSerialized = String.join(",", selectedFoodNames);
        this.configuration.addProperty("selectedFoodNames", EatingFoodNamesSerialized);
        this.configuration.addProperty("interactWithLootAll", String.valueOf(useLootEverything));
        this.configuration.addProperty("useLoot", String.valueOf(useCustomLoot));
        String InteractWithLootAllSerialized = String.join(",", targetItemNames);
        this.configuration.addProperty("selectedItemNames", InteractWithLootAllSerialized);
        this.configuration.addProperty("VolleyOfSoulsThreshold", String.valueOf(VolleyOfSoulsThreshold));
        this.configuration.addProperty("NecrosisStacksThreshold", String.valueOf(NecrosisStacksThreshold));
        this.configuration.addProperty("usePOD", String.valueOf(usePOD));
        this.configuration.addProperty("scriptureofJas", String.valueOf(scriptureofJas));
        this.configuration.addProperty("scriptureofWen", String.valueOf(scriptureofWen));
        this.configuration.addProperty("useWeaponPoison", String.valueOf(useWeaponPoison));
        this.configuration.addProperty("usequickPrayers", String.valueOf(usequickPrayers));
        this.configuration.addProperty("animateDead", String.valueOf(Variables.useAnimateDead));
        this.configuration.addProperty("useScrimshaws", String.valueOf(useScrimshaws));
        this.configuration.addProperty("makePlanks", String.valueOf(makePlanks));
        this.configuration.addProperty("makeRefinedPlanks", String.valueOf(makeRefinedPlanks));
        this.configuration.addProperty("makeFrames", String.valueOf(makeFrames));
        this.configuration.addProperty("isMiscActive", String.valueOf(isMiscActive));
        this.configuration.addProperty("isPlanksActive", String.valueOf(isPlanksActive));
        this.configuration.addProperty("isPorterMakerActive", String.valueOf(isportermakerActive));
        this.configuration.addProperty("soulAltar", String.valueOf(useSoulAltar));
        this.configuration.addProperty("isRunecraftingActive", String.valueOf(isRunecraftingActive));
        this.configuration.addProperty("RingofDueling", String.valueOf(RingofDueling));
        this.configuration.addProperty("HandleBoneAltar", String.valueOf(HandleBoneAltar));
        this.configuration.addProperty("HandleFleshAltar", String.valueOf(HandleFleshAltar));
        this.configuration.addProperty("HandleMiasmaAltar", String.valueOf(HandleMiasmaAltar));
        this.configuration.addProperty("HandleSpiritAltar", String.valueOf(HandleSpiritAltar));
        this.configuration.addProperty("WearingRing", String.valueOf(WearingRing));
        this.configuration.addProperty("ManageFamiliar", String.valueOf(ManageFamiliar));
        this.configuration.addProperty("Powerburst", String.valueOf(Powerburst));
        this.configuration.addProperty("notWearingRing", String.valueOf(notWearingRing));
        this.configuration.addProperty("isCorruptedOreActive", String.valueOf(isCorruptedOreActive));
        this.configuration.addProperty("handleArchGlacor", String.valueOf(handleArchGlacor));
        this.configuration.addProperty("enableRadiusTracking", String.valueOf(enableRadiusTracking));
       /* this.configuration.addProperty("radius", String.valueOf(radius));*/
        this.configuration.addProperty("hiSpecMonocle", String.valueOf(hiSpecMonocle));
        this.configuration.addProperty("materialManual", String.valueOf(materialManual));
        this.configuration.addProperty("archaeologistsTea", String.valueOf(archaeologistsTea));
        this.configuration.addProperty("isHerbloreActive", String.valueOf(isHerbloreActive));
        this.configuration.addProperty("makeBombs", String.valueOf(makeBombs));
        this.configuration.addProperty("useGraceoftheElves", String.valueOf(useGraceoftheElves));
        this.configuration.addProperty("useGote", String.valueOf(useGote));
        this.configuration.addProperty("harvestChronicles", String.valueOf(harvestChronicles));
        this.configuration.addProperty("useFamiliarSummoning", String.valueOf(useFamiliarSummoning));
        this.configuration.addProperty("useDivineoMatic", String.valueOf(useDivineoMatic));
        String selectedPorterType = porterTypes[currentPorterType.get()];
        this.configuration.addProperty("selectedPorterType", selectedPorterType);
        String selectedQuantity = quantities[currentQuantity.get()];
        this.configuration.addProperty("selectedQuantity", selectedQuantity);
        this.configuration.addProperty("selectedItemToDisassemble", Item);
        this.configuration.addProperty("useAlchamise", String.valueOf(useAlchamise));
        this.configuration.addProperty("useDisassemble", String.valueOf(useDisassemble));
        this.configuration.addProperty("isDissasemblerActive", String.valueOf(isDissasemblerActive));
        this.configuration.addProperty("isGemCutterActive", String.valueOf(isGemCutterActive));
        this.configuration.addProperty("isSmeltingActive", String.valueOf(isSmeltingActive));
        this.configuration.addProperty("isSummoningActive", String.valueOf(isSummoningActive));
        this.configuration.addProperty("isdivinechargeActive", String.valueOf(isdivinechargeActive));
        this.configuration.addProperty("OrangeThemeSelected", String.valueOf(OrangeThemeSelected));
        this.configuration.addProperty("BlueThemeSelected", String.valueOf(BlueThemeSelected));
        this.configuration.addProperty("PurpleThemeSelected", String.valueOf(PurpleThemeSelected));
        this.configuration.addProperty("RedThemeSelected", String.valueOf(RedThemeSelected));
        this.configuration.addProperty("YellowThemeSelected", String.valueOf(YellowThemeSelected));
        this.configuration.addProperty("GreenThemeSelected", String.valueOf(GreenThemeSelected));
        this.configuration.addProperty("isSiftSoilActive", String.valueOf(isSiftSoilActive));
        this.configuration.addProperty("chargeThreshold", String.valueOf(chargeThreshold));
        this.configuration.addProperty("equipChargeThreshold", String.valueOf(equipChargeThreshold));
        this.configuration.addProperty("handleMultitarget", String.valueOf(handleMultitarget));
        this.configuration.addProperty("targetThreshold", String.valueOf(getHealthThreshold()));
        this.configuration.addProperty("PrayerPointsThreshold", String.valueOf(getPrayerPointsThreshold()));
        this.configuration.addProperty("HealthPointsThreshold", String.valueOf(getHealthPointsThreshold()));
        this.configuration.addProperty("useNotepaper", String.valueOf(useNotepaper));
        String serializedItemNamesForNotepaper = String.join(",", selectedNotepaperNames);
        this.configuration.addProperty("selectedNotepaperNames", serializedItemNamesForNotepaper);
        this.configuration.addProperty("handleOnlyChonicles", String.valueOf(handleOnlyChonicles));
        this.configuration.addProperty("lootNoted", String.valueOf(useLootAllNotedItems));
        this.configuration.addProperty("useWorldhop", String.valueOf(useWorldhop));
        this.configuration.addProperty("hopDuetoPlayers", String.valueOf(hopDuetoPlayers));
        this.configuration.addProperty("useWorldhop", String.valueOf(useWorldhop));
        String centerCoordStr = centerCoordinate.getX() + "," + centerCoordinate.getY() + "," + centerCoordinate.getZ();
        this.configuration.addProperty("centerCoordinate", centerCoordStr);
        this.configuration.addProperty("useDarkness", String.valueOf(useDarkness));
        this.configuration.addProperty("useVulnerabilityBombs", String.valueOf(useVulnerabilityBomb));
        this.configuration.addProperty("useThreadsofFate", String.valueOf(useThreadsofFate));
        this.configuration.addProperty("useDwarfcannon", String.valueOf(useDwarfcannon));
        this.configuration.addProperty("useElvenRitual", String.valueOf(useElvenRitual));
        this.configuration.addProperty("useExcalibur", String.valueOf(useExcalibur));
        this.configuration.addProperty("useDemonSlayer", String.valueOf(useDemonSlayer));
        this.configuration.addProperty("useUndeadSlayer", String.valueOf(useUndeadSlayer));
        this.configuration.addProperty("useDragonSlayer", String.valueOf(useDragonSlayer));
        this.configuration.addProperty("showLogs", String.valueOf(showLogs));
        this.configuration.addProperty("showCheckboxesWindow", String.valueOf(showCheckboxesWindow));
        this.configuration.addProperty("usePowderOfProtection", String.valueOf(usePowderOfProtection));
        this.configuration.addProperty("usePowderOfPenance", String.valueOf(usePowderOfPenance));
        this.configuration.addProperty("useLantadymeSticks", String.valueOf(useLantadymeSticks));
        this.configuration.addProperty("useKwuarmSticks", String.valueOf(useKwuarmSticks));
        this.configuration.addProperty("useIritSticks", String.valueOf(useIritSticks));
        this.configuration.addProperty("scrollToBottom", String.valueOf(scrollToBottom));
        String combatListSerialized = String.join(",", CombatList);
        this.configuration.addProperty("CombatList", combatListSerialized);
        this.configuration.addProperty("shouldEatFood", String.valueOf(shouldEatFood));
        this.configuration.addProperty("useAbyssRunecrafting", String.valueOf(useAbyssRunecrafting));
        this.configuration.addProperty("makeDinarrow", String.valueOf(makeDinarrow));
        this.configuration.addProperty("showAllLoot", String.valueOf(showAllLoot));
        this.configuration.addProperty("lavaStrykewyrms", String.valueOf(lavaStrykewyrms));
        this.configuration.addProperty("iceStrykewyrms", String.valueOf(iceStrykewyrms));
        this.configuration.addProperty("doSlayer", String.valueOf(doSlayer));
        this.configuration.addProperty("useLootAllStackableItems", String.valueOf(useLootAllStackableItems));
        this.configuration.addProperty("useBankPin", String.valueOf(useBankPin));
        this.configuration.addProperty("pin1", String.valueOf(pin1));
        this.configuration.addProperty("pin2", String.valueOf(pin2));
        this.configuration.addProperty("pin3", String.valueOf(pin3));
        this.configuration.addProperty("pin4", String.valueOf(pin4));
        String tasksToSkipSerialized = String.join(",", tasksToSkip);
        this.configuration.addProperty("TasksToSkip", tasksToSkipSerialized);
        this.configuration.addProperty("showSlayerOptions", String.valueOf(showSlayerOptions));
        this.configuration.addProperty("slayerPointFarming", String.valueOf(slayerPointFarming));
        this.configuration.addProperty("useFamiliarForCombat", String.valueOf(useFamiliarForCombat));
        this.configuration.addProperty("useFamiliarScrolls", String.valueOf(useFamiliarScrolls));
        this.configuration.addProperty("craftNatureRunes", String.valueOf(craftNatureRunes));
        this.configuration.addProperty("craftBloodRunes", String.valueOf(craftBloodRunes));
        String excludedKeywordsSerialized = String.join(",", excludedKeywords);
        this.configuration.addProperty("excludedKeywords", excludedKeywordsSerialized);
        this.configuration.addProperty("useAstralAltar", String.valueOf(useAstralAltar));

        this.configuration.save();
    }

    public void loadConfiguration() {
        try {
            useAstralAltar = Boolean.parseBoolean(this.configuration.getProperty("useAstralAltar"));
            String tasksToSkipSerialized = this.configuration.getProperty("TasksToSkip");
            if (tasksToSkipSerialized != null && !tasksToSkipSerialized.isEmpty()) {
                tasksToSkip = new ArrayList<>(Arrays.asList(tasksToSkipSerialized.split(",")));
            }
            String excludedKeywordsSerialized = this.configuration.getProperty("excludedKeywords");
            if (excludedKeywordsSerialized != null && !excludedKeywordsSerialized.isEmpty()) {
                String[] loadedExcludedKeywords = excludedKeywordsSerialized.split(",");
                excludedKeywords.clear();
                excludedKeywords.addAll(Arrays.asList(loadedExcludedKeywords));
            }
            craftBloodRunes = Boolean.parseBoolean(this.configuration.getProperty("craftBloodRunes"));
            craftNatureRunes = Boolean.parseBoolean(this.configuration.getProperty("craftNatureRunes"));
            useFamiliarScrolls = Boolean.parseBoolean(this.configuration.getProperty("useFamiliarScrolls"));
            useFamiliarForCombat = Boolean.parseBoolean(this.configuration.getProperty("useFamiliarForCombat"));
            slayerPointFarming = Boolean.parseBoolean(this.configuration.getProperty("slayerPointFarming"));
            showSlayerOptions = Boolean.parseBoolean(this.configuration.getProperty("showSlayerOptions"));
            useLootAllStackableItems = Boolean.parseBoolean(this.configuration.getProperty("useLootAllStackableItems"));
            doSlayer = Boolean.parseBoolean(this.configuration.getProperty("doSlayer"));
            iceStrykewyrms = Boolean.parseBoolean(this.configuration.getProperty("iceStrykewyrms"));
            lavaStrykewyrms = Boolean.parseBoolean(this.configuration.getProperty("lavaStrykewyrms"));
            showAllLoot = Boolean.parseBoolean(this.configuration.getProperty("showAllLoot"));
            makeDinarrow = Boolean.parseBoolean(this.configuration.getProperty("makeDinarrow"));
            useAbyssRunecrafting = Boolean.parseBoolean(this.configuration.getProperty("useAbyssRunecrafting"));
            shouldEatFood = Boolean.parseBoolean(this.configuration.getProperty("shouldEatFood"));
            scrollToBottom = Boolean.parseBoolean(this.configuration.getProperty("scrollToBottom"));
            useIritSticks = Boolean.parseBoolean(this.configuration.getProperty("useIritSticks"));
            useKwuarmSticks = Boolean.parseBoolean(this.configuration.getProperty("useKwuarmSticks"));
            useLantadymeSticks = Boolean.parseBoolean(this.configuration.getProperty("useLantadymeSticks"));
            usePowderOfPenance = Boolean.parseBoolean(this.configuration.getProperty("usePowderOfPenance"));
            usePowderOfProtection = Boolean.parseBoolean(this.configuration.getProperty("usePowderOfProtection"));
            showCheckboxesWindow = Boolean.parseBoolean(this.configuration.getProperty("showCheckboxesWindow"));
            showLogs = Boolean.parseBoolean(this.configuration.getProperty("showLogs"));
            useDarkness = Boolean.parseBoolean(this.configuration.getProperty("useDarkness"));
            useVulnerabilityBomb = Boolean.parseBoolean(this.configuration.getProperty("useVulnerabilityBombs"));
            useThreadsofFate = Boolean.parseBoolean(this.configuration.getProperty("useThreadsofFate"));
            useDwarfcannon = Boolean.parseBoolean(this.configuration.getProperty("useDwarfcannon"));
            useElvenRitual = Boolean.parseBoolean(this.configuration.getProperty("useElvenRitual"));
            useExcalibur = Boolean.parseBoolean(this.configuration.getProperty("useExcalibur"));
            useDemonSlayer = Boolean.parseBoolean(this.configuration.getProperty("useDemonSlayer"));
            useUndeadSlayer = Boolean.parseBoolean(this.configuration.getProperty("useUndeadSlayer"));
            useDragonSlayer = Boolean.parseBoolean(this.configuration.getProperty("useDragonSlayer"));
            useWorldhop = Boolean.parseBoolean(this.configuration.getProperty("useWorldhop"));
            hopDuetoPlayers = Boolean.parseBoolean(this.configuration.getProperty("hopDuetoPlayers"));
            useLootAllNotedItems = Boolean.parseBoolean(this.configuration.getProperty("lootNoted"));
            handleOnlyChonicles = Boolean.parseBoolean(this.configuration.getProperty("handleOnlyChonicles"));
            useNotepaper = Boolean.parseBoolean(this.configuration.getProperty("useNotepaper"));
            isdivinechargeActive = Boolean.parseBoolean(this.configuration.getProperty("isdivinechargeActive"));
            isGemCutterActive = Boolean.parseBoolean(this.configuration.getProperty("isGemCutterActive"));
            isSmeltingActive = Boolean.parseBoolean(this.configuration.getProperty("isSmeltingActive"));
            isSummoningActive = Boolean.parseBoolean(this.configuration.getProperty("isSummoningActive"));
            isDissasemblerActive = Boolean.parseBoolean(this.configuration.getProperty("isDissasemblerActive"));
            isAgilityActive = Boolean.parseBoolean(this.configuration.getProperty("isAgilityActive"));
            isDivinationActive = Boolean.parseBoolean(this.configuration.getProperty("isDivinationActive"));
            isMiningActive = Boolean.parseBoolean(this.configuration.getProperty("isMiningActive"));
            isWoodcuttingActive = Boolean.parseBoolean(this.configuration.getProperty("isWoodcuttingActive"));
            isFishingActive = Boolean.parseBoolean(this.configuration.getProperty("isFishingActive"));
            isCombatActive = Boolean.parseBoolean(this.configuration.getProperty("isCombatActive"));
            isArcheologyActive = Boolean.parseBoolean(this.configuration.getProperty("isArcheologyActive"));
            isThievingActive = Boolean.parseBoolean(this.configuration.getProperty("isThievingActive"));
            BankforFood = Boolean.parseBoolean(this.configuration.getProperty("iseatFoodActive"));
            isCookingActive = Boolean.parseBoolean(this.configuration.getProperty("isCookingActive"));
            useOverloads = Boolean.parseBoolean(this.configuration.getProperty("useOverloads"));
            usePrayerPots = Boolean.parseBoolean(this.configuration.getProperty("usePrayerPots"));
            useAggroPots = Boolean.parseBoolean(this.configuration.getProperty("useAggroPots"));
            MaterialCache = Boolean.parseBoolean(this.configuration.getProperty("MaterialCache"));
            offerChronicles = Boolean.parseBoolean(this.configuration.getProperty("offerChronicles"));
            isCombatActive = Boolean.parseBoolean(this.configuration.getProperty("Combat"));
            useLootEverything = Boolean.parseBoolean(this.configuration.getProperty("interactWithLootAll"));
            useCustomLoot = Boolean.parseBoolean(this.configuration.getProperty("useLoot"));
            makeBombs = Boolean.parseBoolean(this.configuration.getProperty("makeBombs"));
            isHerbloreActive = Boolean.parseBoolean(this.configuration.getProperty("isHerbloreActive"));
            usePOD = Boolean.parseBoolean(this.configuration.getProperty("usePOD"));
            SoulSplit = Boolean.parseBoolean(this.configuration.getProperty("SoulSplit"));
            useConjureUndeadArmy = Boolean.parseBoolean(this.configuration.getProperty("KeepArmyup"));
            useEssenceofFinality = Boolean.parseBoolean(this.configuration.getProperty("SpecialAttack"));
            useVolleyofSouls = Boolean.parseBoolean(this.configuration.getProperty("VolleyofSouls"));
            useInvokeDeath = Boolean.parseBoolean(this.configuration.getProperty("InvokeDeath"));
            useWeaponSpecialAttack = Boolean.parseBoolean(this.configuration.getProperty("DeathGrasp"));
            scriptureofWen = Boolean.parseBoolean(this.configuration.getProperty("scriptureofWen"));
            scriptureofJas = Boolean.parseBoolean(this.configuration.getProperty("scriptureofJas"));
            useWeaponPoison = Boolean.parseBoolean(this.configuration.getProperty("useWeaponPoison"));
            usequickPrayers = Boolean.parseBoolean(this.configuration.getProperty("usequickPrayers"));
            Variables.useAnimateDead = Boolean.parseBoolean(this.configuration.getProperty("animateDead"));
            useScrimshaws = Boolean.parseBoolean(this.configuration.getProperty("useScrimshaws"));
            makePlanks = Boolean.parseBoolean(this.configuration.getProperty("makePlanks"));
            makeRefinedPlanks = Boolean.parseBoolean(this.configuration.getProperty("makeRefinedPlanks"));
            makeFrames = Boolean.parseBoolean(this.configuration.getProperty("makeFrames"));
            isMiscActive = Boolean.parseBoolean(this.configuration.getProperty("isMiscActive"));
            isPlanksActive = Boolean.parseBoolean(this.configuration.getProperty("isPlanksActive"));
            isportermakerActive = Boolean.parseBoolean(this.configuration.getProperty("isPorterMakerActive"));
            useSoulAltar = Boolean.parseBoolean(this.configuration.getProperty("soulAltar"));
            isRunecraftingActive = Boolean.parseBoolean(this.configuration.getProperty("isRunecraftingActive"));
            RingofDueling = Boolean.parseBoolean(this.configuration.getProperty("RingofDueling"));
            HandleBoneAltar = Boolean.parseBoolean(this.configuration.getProperty("HandleBoneAltar"));
            HandleFleshAltar = Boolean.parseBoolean(this.configuration.getProperty("HandleFleshAltar"));
            HandleMiasmaAltar = Boolean.parseBoolean(this.configuration.getProperty("HandleMiasmaAltar"));
            HandleSpiritAltar = Boolean.parseBoolean(this.configuration.getProperty("HandleSpiritAltar"));
            WearingRing = Boolean.parseBoolean(this.configuration.getProperty("WearingRing"));
            ManageFamiliar = Boolean.parseBoolean(this.configuration.getProperty("ManageFamiliar"));
            Powerburst = Boolean.parseBoolean(this.configuration.getProperty("Powerburst"));
            notWearingRing = Boolean.parseBoolean(this.configuration.getProperty("notWearingRing"));
            useGraceoftheElves = Boolean.parseBoolean(this.configuration.getProperty("useGraceoftheElves"));
            isCorruptedOreActive = Boolean.parseBoolean(this.configuration.getProperty("isCorruptedOreActive"));
            handleArchGlacor = Boolean.parseBoolean(this.configuration.getProperty("handleArchGlacor"));
            enableRadiusTracking = Boolean.parseBoolean(this.configuration.getProperty("enableRadiusTracking"));
            hiSpecMonocle = Boolean.parseBoolean(this.configuration.getProperty("hiSpecMonocle"));
            materialManual = Boolean.parseBoolean(this.configuration.getProperty("materialManual"));
            archaeologistsTea = Boolean.parseBoolean(this.configuration.getProperty("archaeologistsTea"));
            harvestChronicles = Boolean.parseBoolean(this.configuration.getProperty("harvestChronicles"));
            useFamiliarSummoning = Boolean.parseBoolean(this.configuration.getProperty("useFamiliarSummoning"));
            useDivineoMatic = Boolean.parseBoolean(this.configuration.getProperty("useDivineoMatic"));
            useGote = Boolean.parseBoolean(this.configuration.getProperty("useGote"));
            useAlchamise = Boolean.parseBoolean(this.configuration.getProperty("useAlchamise"));
            useDisassemble = Boolean.parseBoolean(this.configuration.getProperty("useDisassemble"));
            OrangeThemeSelected = Boolean.parseBoolean(this.configuration.getProperty("OrangeThemeSelected"));
            PurpleThemeSelected = Boolean.parseBoolean(this.configuration.getProperty("PurpleThemeSelected"));
            BlueThemeSelected = Boolean.parseBoolean(this.configuration.getProperty("BlueThemeSelected"));
            RedThemeSelected = Boolean.parseBoolean(this.configuration.getProperty("RedThemeSelected"));
            YellowThemeSelected = Boolean.parseBoolean(this.configuration.getProperty("YellowThemeSelected"));
            GreenThemeSelected = Boolean.parseBoolean(this.configuration.getProperty("GreenThemeSelected"));
            isSiftSoilActive = Boolean.parseBoolean(this.configuration.getProperty("isSiftSoilActive"));
            chargeThreshold = Integer.parseInt(this.configuration.getProperty("chargeThreshold"));
            equipChargeThreshold = Integer.parseInt(this.configuration.getProperty("equipChargeThreshold"));
            handleMultitarget = Boolean.parseBoolean(this.configuration.getProperty("handleMultitarget"));
            useWorldhop = Boolean.parseBoolean(this.configuration.getProperty("useWorldhop"));
            useBankPin = Boolean.parseBoolean(this.configuration.getProperty("useBankPin"));
            String pin1Value = this.configuration.getProperty("pin1");
            if (pin1Value != null && !pin1Value.isEmpty()) {
                pin1 = Integer.parseInt(pin1Value);
            }

            String pin2Value = this.configuration.getProperty("pin2");
            if (pin2Value != null && !pin2Value.isEmpty()) {
                pin2 = Integer.parseInt(pin2Value);
            }

            String pin3Value = this.configuration.getProperty("pin3");
            if (pin3Value != null && !pin3Value.isEmpty()) {
                pin3 = Integer.parseInt(pin3Value);
            }

            String pin4Value = this.configuration.getProperty("pin4");
            if (pin4Value != null && !pin4Value.isEmpty()) {
                pin4 = Integer.parseInt(pin4Value);
            }
            String prayerPointsThresholdValue = this.configuration.getProperty("PrayerPointsThreshold");
            if (prayerPointsThresholdValue != null && !prayerPointsThresholdValue.isEmpty()) {
                int prayerPointsThreshold = Integer.parseInt(prayerPointsThresholdValue);
                setPrayerPointsThreshold(prayerPointsThreshold);
            }

            String healthPointsThresholdValue = this.configuration.getProperty("HealthPointsThreshold");
            if (healthPointsThresholdValue != null && !healthPointsThresholdValue.isEmpty()) {
                int healthPointsThreshold = Integer.parseInt(healthPointsThresholdValue);
                setHealthThreshold(healthPointsThreshold);
            }
            String targetThresholdValue = this.configuration.getProperty("targetThreshold");
            if (targetThresholdValue != null && !targetThresholdValue.isEmpty()) {
                double targetThreshold = Double.parseDouble(targetThresholdValue);
                if (targetThreshold < 0.0) targetThreshold = 0.0;
                else if (targetThreshold > 1.0) targetThreshold = 1.0;
                setHealthThreshold(targetThreshold);
            }
            String loadedItemToDisassemble = this.configuration.getProperty("selectedItemToDisassemble");
            if (loadedItemToDisassemble != null && !loadedItemToDisassemble.isEmpty()) {
                Item = loadedItemToDisassemble;
            }
            String loadedPorterType = this.configuration.getProperty("selectedPorterType");
            if (loadedPorterType != null && !loadedPorterType.isEmpty()) {
                int index = Arrays.asList(porterTypes).indexOf(loadedPorterType);
                if (index != -1) {
                    currentPorterType.set(index);
                }
            }

            String loadedQuantity = this.configuration.getProperty("selectedQuantity");
            if (loadedQuantity != null && !loadedQuantity.isEmpty()) {
                int index = Arrays.asList(quantities).indexOf(loadedQuantity);
                if (index != -1) {
                    currentQuantity.set(index);
                }
            }
            /*String radiusValue = this.configuration.getProperty("radius");
            if (radiusValue != null && !radiusValue.isEmpty()) {
                int radius = Integer.parseInt(radiusValue);
                if (radius < 0) radius = 0;
                else if (radius > 25) radius = 25;
                Radius.radius = radius;
            }*/
            String necrosisThresholdValue = this.configuration.getProperty("NecrosisStacksThreshold");
            if (necrosisThresholdValue != null && !necrosisThresholdValue.isEmpty()) {
                int necrosisThreshold = Integer.parseInt(necrosisThresholdValue);
                if (necrosisThreshold < 0) necrosisThreshold = 0;
                else if (necrosisThreshold > 12) necrosisThreshold = 12;
                NecrosisStacksThreshold = necrosisThreshold;
            }

            String volleyThresholdValue = this.configuration.getProperty("VolleyOfSoulsThreshold");
            if (volleyThresholdValue != null && !volleyThresholdValue.isEmpty()) {
                int volleyThreshold = Integer.parseInt(volleyThresholdValue);
                if (volleyThreshold < 0) volleyThreshold = 0;
                VolleyOfSoulsThreshold = volleyThreshold;
            }
            String InteractWithLootAllSerialized = this.configuration.getProperty("selectedItemNames");
            if (InteractWithLootAllSerialized != null && !InteractWithLootAllSerialized.isEmpty()) {
                String[] loadedInteractWithLootAll = InteractWithLootAllSerialized.split(",");
                targetItemNames.clear();
                targetItemNames.addAll(Arrays.asList(loadedInteractWithLootAll));
            }
            String targetNamesSerialized = this.configuration.getProperty("TargetNames");
            if (targetNamesSerialized != null && !targetNamesSerialized.isEmpty()) {
                String[] loadedTargetNames = targetNamesSerialized.split(",");
                targetNames.clear();
                for (String targetName : loadedTargetNames) {
                    addTargetName(targetName);
                }
            }
            String ExcavationNamesSerialized = this.configuration.getProperty("selectedArchNames");
            if (ExcavationNamesSerialized != null && !ExcavationNamesSerialized.isEmpty()) {
                String[] loadedExcavationNames = ExcavationNamesSerialized.split(",");
                selectedArchNames.clear();
                for (String excavationName : loadedExcavationNames) {
                    addName(excavationName);
                }
            }
            String MiningNamesSerialized = this.configuration.getProperty("selectedRockNames");
            if (MiningNamesSerialized != null && !MiningNamesSerialized.isEmpty()) {
                String[] loadedMiningNames = MiningNamesSerialized.split(",");
                selectedRockNames.clear();
                for (String miningName : loadedMiningNames) {
                    addRockName(miningName);
                }
            }
            String FishingLocationsSerialized = this.configuration.getProperty("selectedFishingLocations");
            if (FishingLocationsSerialized != null && !FishingLocationsSerialized.isEmpty()) {
                String[] loadedFishingLocations = FishingLocationsSerialized.split(",");
                selectedFishingLocations.clear();
                for (String fishingLocation : loadedFishingLocations) {
                    addFishingLocation(fishingLocation);
                }
            }
            String FishingActionsSerialized = this.configuration.getProperty("selectedFishingActions");
            if (FishingActionsSerialized != null && !FishingActionsSerialized.isEmpty()) {
                String[] loadedFishingActions = FishingActionsSerialized.split(",");
                selectedFishingActions.clear();
                for (String fishingAction : loadedFishingActions) {
                    addFishingAction(fishingAction);
                }
            }
            String WoodcuttingNamesSerialized = this.configuration.getProperty("selectedTreeNames");
            if (WoodcuttingNamesSerialized != null && !WoodcuttingNamesSerialized.isEmpty()) {
                String[] loadedWoodcuttingNames = WoodcuttingNamesSerialized.split(",");
                selectedTreeNames.clear();
                for (String woodcuttingName : loadedWoodcuttingNames) {
                    addTreeName(woodcuttingName);
                }
            }
            String EatingFoodNamesSerialized = this.configuration.getProperty("selectedFoodNames");
            if (EatingFoodNamesSerialized != null && !EatingFoodNamesSerialized.isEmpty()) {
                String[] loadedEatingFoodNames = EatingFoodNamesSerialized.split(",");
                selectedFoodNames.clear();
                for (String foodName : loadedEatingFoodNames) {
                    addFoodName(foodName);
                }
            }
            String serializedItemNamesForNotepaper = this.configuration.getProperty("selectedNotepaperNames");
            if (serializedItemNamesForNotepaper != null && !serializedItemNamesForNotepaper.isEmpty()) {
                String[] loadedItemNamesForNotepaper = serializedItemNamesForNotepaper.split(",");
                selectedNotepaperNames.clear();
                selectedNotepaperNames.addAll(Arrays.asList(loadedItemNamesForNotepaper));
            }
            String centerCoordStr = this.configuration.getProperty("centerCoordinate");
            if (centerCoordStr != null && !centerCoordStr.isEmpty()) {
                String[] parts = centerCoordStr.split(",");
                if (parts.length == 3) {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int z = Integer.parseInt(parts[2]);
                    centerCoordinate = new Coordinate(x, y, z);
                }
            }
            String combatListSerialized = this.configuration.getProperty("CombatList");
            if (combatListSerialized != null && !combatListSerialized.isEmpty()) {
                String[] loadedCombatList = combatListSerialized.split(",");
                CombatList.clear();
                CombatList.addAll(Arrays.asList(loadedCombatList));
            }
            log("[Settings] Configuration loaded successfully.");
        } catch (Exception e) {
            log("[Error] Failed to load configuration. Using defaults.");
        }
    }

}

