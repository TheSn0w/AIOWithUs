package net.botwithus;

import ImGui.SnowScriptGraphics;
import net.botwithus.Variables.Runnables;
import net.botwithus.Variables.Variables;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.EventBus;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.*;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

import static ImGui.Theme.*;
import static net.botwithus.Combat.*;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Runecrafting.ScriptState.TELEPORTINGTOBANK;
import static net.botwithus.SnowsScript.BotState.SKILLING;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.*;


public class SnowsScript extends LoopingScript {
    private static BotState botState = BotState.IDLE;
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
    }



    public SnowsScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new SnowScriptGraphics(getConsole(), this);
        startTime = Instant.now();
        runStartTime = System.currentTimeMillis();

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



    public void onLoop() {
        LocalPlayer player = Client.getLocalPlayer();

        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            return;
        }

        capturestuff();

        switch (botState) {
            case IDLE -> Execution.delay(random.nextLong(1500, 3000));
            case SKILLING -> skillingTasks.forEach((condition, task) -> {
                if (condition.getAsBoolean()) {
                    task.run();
                }
            });

            case BANKING -> {
                if (isThievingActive) {
                    Execution.delay(Thieving.bankForfood());
                }
                if (nearestBank) {
                    Execution.delay(useTheNearestBank(player));
                }
                if (isArcheologyActive)
                    Execution.delay(Archeology.BankforArcheology(player, selectedArchNames));
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

            npc.interact(interactionOption);
            log("[Info] Interacting with " + npcName + " using " + interactionOption + ".");
        } else {
            log("[Error] Failed to find nearest NPC.");
        }
    }



    @Override
    public void onActivation() {
        loadConfiguration();
        subscribeToEvents();
        super.initialize();
    }

    @Override
    public void onDeactivation() {
        saveConfiguration();
        unsubscribeFromEvents();
        super.onDeactivation();
    }

    private void subscribeToEvents() {
        EventBus.EVENT_BUS.subscribe(this, ChatMessageEvent.class, this::onChatMessageEvent);
        EventBus.EVENT_BUS.subscribe(this, InventoryUpdateEvent.class, this::onInventoryUpdate);
    }

    private void unsubscribeFromEvents() {
        EventBus.EVENT_BUS.unsubscribe(this, ChatMessageEvent.class, this::onChatMessageEvent);
        EventBus.EVENT_BUS.unsubscribe(this, InventoryUpdateEvent.class, this::onInventoryUpdate);
    }


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
                    divineCharges.put(itemName, newDivineCharges);
                }
            }
        }
        if (isArcheologyActive) {
            String itemName = event.getNewItem().getName();
            int oldCount = event.getOldItem() != null ? event.getOldItem().getStackSize() : 0;
            int newCount = event.getNewItem().getStackSize();
            if (newCount > oldCount) {
                int quantity = newCount - oldCount;
                int count = materialTypes.getOrDefault(itemName, 0);
                materialTypes.put(itemName, count + quantity);
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
        if (isCombatActive) {
            String itemName = event.getNewItem().getName();
            if (itemName.endsWith(" charm")) {
                int oldCount = event.getOldItem() != null ? event.getOldItem().getStackSize() : 0;
                int newCount = event.getNewItem().getStackSize();
                if (newCount > oldCount) {
                    int quantity = newCount - oldCount;

                    Map<String, Integer> charmMap;
                    switch (itemName) {
                        case "Blue charm":
                            charmMap = BlueCharms;
                            break;
                        case "Crimson charm":
                            charmMap = CrimsonCharms;
                            break;
                        case "Green charm":
                            charmMap = GreenCharms;
                            break;
                        case "Gold charm":
                            charmMap = GoldCharms;
                            break;
                        default:
                            return;
                    }

                    int currentCount = charmMap.getOrDefault(itemName, 0);
                    charmMap.put(itemName, currentCount + quantity);
                }
            }
        }
        if (isRunecraftingActive) {
            String itemName = event.getNewItem().getName();
            List<String> runeNames = Arrays.asList("Miasma rune", "Flesh rune", "Spirit rune", "Bone rune");

            if (runeNames.contains(itemName)) {
                int newCount = event.getNewItem().getStackSize();
                int currentCount = runeCount.getOrDefault(itemName, 0);
                int totalRuneCount = currentCount + newCount;

                runeCount.put(itemName, totalRuneCount);
                log("[Runecrafting] Rune count updated: " + totalRuneCount + " " + itemName + " - Traversing to bank");
                Runecrafting.setCurrentState(TELEPORTINGTOBANK);
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
        if (isDissasemblerActive) {
            if (botState == BotState.SKILLING) {
                TaskScheduler activeTask = getActiveTask();
                if (activeTask != null && Objects.requireNonNull(event.getNewItem().getName()).contains(activeTask.getItemToDisassemble())) {
                    itemsDestroyed++;
                    activeTask.incrementAmountDisassembled();
                }
            }
        }
    }


    void onChatMessageEvent(ChatMessageEvent event) {
        if (!isActive()) {
            return;
        }
        String message = event.getMessage();
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
        if (isHerbloreActive) {
            if (message.contains("You mix the ingredients")) {
                String potionType = "Potions Made";
                int count = Potions.getOrDefault(potionType, 0);
                Potions.put(potionType, count + 1);
            }
        }
        if (isRunecraftingActive) {
            if (message.contains("The charger cannot hold any more essence.")) {
                Execution.delay(Runecrafting.handleCharging());
            }
            if (message.contains("You do no have any essence to deposit")) {
                Execution.delay(Runecrafting.handleEdgevillebanking());
            }
            if (message.contains("The altar is already charged to its maximum capacity")) {
                Runecrafting.handleSoulAltar();
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

    public static class ComponentTextRetriever {
        public String getComponentText() {
            Component component = ComponentQuery.newQuery(1466)
                    .componentIndex(12)
                    .subComponentIndex(1)
                    .results()
                    .first();

            if (component == null) {
                return "[Main] Component not found";
            }

            return component.getText();
        }
    }
    public static int getTextValue() {
        ComponentTextRetriever textRetriever = new ComponentTextRetriever();
        String text = textRetriever.getComponentText();

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            log("[Error] Could not parse text to integer.");
            return 0;
        }
    }

    public static void setLastSkillingLocation(Coordinate location) {
        Variables.lastSkillingLocation = location;
    }

    public static Area.Rectangular getLastSkillingLocation() {
        if (lastSkillingLocation != null) {
            int x = lastSkillingLocation.getX();
            int y = lastSkillingLocation.getY();
            int z = lastSkillingLocation.getZ();

            Area.Rectangular area = new Area.Rectangular(
                    new Coordinate(x - 2, y - 2, z),
                    new Coordinate(x + 2, y + 2, z)
            );
            return area;
        }
        return null;
    }


    public static long useTheNearestBank(LocalPlayer player) {
        if (player.isMoving()) {
            return random.nextLong(1500, 3000);
        }

        Coordinate nearestBank = findNearestBank(player.getCoordinate());
        if (nearestBank != null) {
            double distanceToBank = Distance.between(player.getCoordinate(), nearestBank);

            String bankType = (nearestBank == STORMGUARD|| nearestBank == WarsRetreat || nearestBank == Anachronia || nearestBank == PrifddinasEast || nearestBank == CityOfUm || nearestBank == SmithingGuild || nearestBank == KharidEt || nearestBank == Lumbridge || nearestBank == VIP || nearestBank == prifWest) ? "Bank chest" :
                    (nearestBank == Edgeville || nearestBank == Draynor) ? "Counter" :
                            "Bank booth";
            List<SceneObject> bankBooths = SceneObjectQuery.newQuery().name(bankType).results().stream()
                    .filter(booth -> booth.getCoordinate().distanceTo(player.getCoordinate()) < 25.0D)
                    .toList();

            SceneObject randomBankBooth;
            if (!bankBooths.isEmpty()) {
                randomBankBooth = bankBooths.get(random.nextInt(bankBooths.size()));

                if (distanceToBank < 25.0D) {
                    log("[Main] Bank is near, interacting with nearest bank");
                    return interactWithBank(player, randomBankBooth);
                }
            } else {
                log("[Main] Bank is far, traversing to bank");
                Movement.traverse(NavPath.resolve(nearestBank));
            }
        } else {
            log("[Error] Nearest bank not found.");
        }
        return random.nextLong(1500, 3000);
    }

    public static Coordinate findNearestBank(Coordinate playerPosition) {
        int textValue = getTextValue();

        List<Coordinate> bankCoordinates = Arrays.asList(
                WarsRetreat, STORMGUARD, SmithingGuild, prifWest, VIP, AlKharid, Edgeville, Burthorpe, KharidEt, Catherby, Anachronia, CityOfUm, PrifddinasCenter, PrifddinasEast, Yanille, Ooglog, ArdougneSouth, ArdougneNorth, Seers, Taverly, FaladorWest, FaladorEast, Lumbridge, Draynor, GrandExchange, VarrockEast, VarrockWest, Canafis
        );

        if (textValue >= 60) {
            return bankCoordinates.stream()
                    .min(Comparator.comparingDouble(bank -> Distance.between(playerPosition, bank)))
                    .orElse(null);
        }

        List<Coordinate> otherBanks = bankCoordinates.stream()
                .filter(bank -> !bank.equals(WarsRetreat))
                .toList();

        return otherBanks.stream()
                .min(Comparator.comparingDouble(bank -> Distance.between(playerPosition, bank)))
                .orElse(null);
    }


    private static long interactWithBank(LocalPlayer player, SceneObject nearestBankBooth) {
        if (player.isMoving()) {
            return random.nextLong(1500, 3000);
        }
        boolean actionBank = Backpack.containsItemByCategory(4448);

        if (actionBank) {
            String interactionOption = "Bank";
            boolean interactionSuccess = nearestBankBooth.interact(interactionOption);
            log("[Main] Interacting with the bank: " + interactionSuccess);

            if (interactionSuccess) {
                Execution.delayUntil(15000, Bank::isOpen);
                if (Bank.isOpen()) {
                    Execution.delay(random.nextLong(1500, 3000));
                    Item oreBox = InventoryItemQuery.newQuery(93).category(4448).results().first();
                    Pattern oreBoxesPattern = Pattern.compile("(?i)Bronze ore box|Iron ore box|Steel ore box|Mithril ore box|Adamant ore box|Rune ore box|Orikalkum ore box|Necronium ore box|Bane ore box|Elder rune ore box");

                    if (oreBox != null) {
                        Bank.depositAllExcept(oreBoxesPattern);
                        log("[Main] Deposited everything except: " + oreBox.getName());

                        if (oreBox.getSlot() >= 0) {
                            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 8, oreBox.getSlot(), 33882127);
                            log("[Main] Emptied: " + oreBox.getName());
                        }
                    }
                }
            }
        } else {
            String interactionOption = "Load Last Preset from";
            boolean interactionSuccess = nearestBankBooth.interact(interactionOption);
            log("[Main] Interacting with the bank using Load Last Preset: " + interactionSuccess);

            Execution.delayUntil(15000, () -> !Backpack.isFull());

            if (!Backpack.isFull()) {
                log("[Main] Backpack is not full, returning to last skilling location.");
                if (Movement.traverse(NavPath.resolve(getLastSkillingLocation())) == TraverseEvent.State.FINISHED) {
                    setBotState(SKILLING);
                }
            }
        }
        return random.nextLong(1500, 3000);
    }


    public void saveConfiguration() {
        this.configuration.addProperty("InvokeDeath", String.valueOf(InvokeDeath));
        this.configuration.addProperty("SpecialAttack", String.valueOf(SpecialAttack));
        this.configuration.addProperty("VolleyofSouls", String.valueOf(VolleyofSouls));
        this.configuration.addProperty("DeathGrasp", String.valueOf(DeathGrasp));
        this.configuration.addProperty("agility", String.valueOf(agility));
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
        this.configuration.addProperty("KeepArmyup", String.valueOf(KeepArmyup));
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
        this.configuration.addProperty("interactWithLootAll", String.valueOf(interactWithLootAll));
        this.configuration.addProperty("useLoot", String.valueOf(useLoot));
        String InteractWithLootAllSerialized = String.join(",", targetItemNames);
        this.configuration.addProperty("selectedItemNames", InteractWithLootAllSerialized);
        this.configuration.addProperty("VolleyOfSoulsThreshold", String.valueOf(VolleyOfSoulsThreshold));
        this.configuration.addProperty("NecrosisStacksThreshold", String.valueOf(NecrosisStacksThreshold));
        this.configuration.addProperty("usePOD", String.valueOf(usePOD));
        this.configuration.addProperty("scriptureofJas", String.valueOf(scriptureofJas));
        this.configuration.addProperty("scriptureofWen", String.valueOf(scriptureofWen));
        this.configuration.addProperty("useWeaponPoison", String.valueOf(useWeaponPoison));
        this.configuration.addProperty("usequickPrayers", String.valueOf(usequickPrayers));
        this.configuration.addProperty("animateDead", String.valueOf(animateDead));
        this.configuration.addProperty("useScrimshaws", String.valueOf(useScrimshaws));
        this.configuration.addProperty("makePlanks", String.valueOf(makePlanks));
        this.configuration.addProperty("makeRefinedPlanks", String.valueOf(makeRefinedPlanks));
        this.configuration.addProperty("makeFrames", String.valueOf(makeFrames));
        this.configuration.addProperty("isMiscActive", String.valueOf(isMiscActive));
        this.configuration.addProperty("isPlanksActive", String.valueOf(isPlanksActive));
        this.configuration.addProperty("isPorterMakerActive", String.valueOf(isportermakerActive));
        this.configuration.addProperty("soulAltar", String.valueOf(soulAltar));
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
        this.configuration.addProperty("radius", String.valueOf(radius));
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
        this.configuration.save();
    }

    public void loadConfiguration() {
        try {
            isdivinechargeActive = Boolean.parseBoolean(this.configuration.getProperty("isdivinechargeActive"));
            isGemCutterActive = Boolean.parseBoolean(this.configuration.getProperty("isGemCutterActive"));
            isSmeltingActive = Boolean.parseBoolean(this.configuration.getProperty("isSmeltingActive"));
            isSummoningActive = Boolean.parseBoolean(this.configuration.getProperty("isSummoningActive"));
            isDissasemblerActive = Boolean.parseBoolean(this.configuration.getProperty("isDissasemblerActive"));
            isAgilityActive = Boolean.parseBoolean(this.configuration.getProperty("agility"));
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
            interactWithLootAll = Boolean.parseBoolean(this.configuration.getProperty("interactWithLootAll"));
            useLoot = Boolean.parseBoolean(this.configuration.getProperty("useLoot"));
            makeBombs = Boolean.parseBoolean(this.configuration.getProperty("makeBombs"));
            isHerbloreActive = Boolean.parseBoolean(this.configuration.getProperty("isHerbloreActive"));
            usePOD = Boolean.parseBoolean(this.configuration.getProperty("usePOD"));
            SoulSplit = Boolean.parseBoolean(this.configuration.getProperty("SoulSplit"));
            KeepArmyup = Boolean.parseBoolean(this.configuration.getProperty("KeepArmyup"));
            SpecialAttack = Boolean.parseBoolean(this.configuration.getProperty("SpecialAttack"));
            VolleyofSouls = Boolean.parseBoolean(this.configuration.getProperty("VolleyofSouls"));
            InvokeDeath = Boolean.parseBoolean(this.configuration.getProperty("InvokeDeath"));
            DeathGrasp = Boolean.parseBoolean(this.configuration.getProperty("DeathGrasp"));
            scriptureofWen = Boolean.parseBoolean(this.configuration.getProperty("scriptureofWen"));
            scriptureofJas = Boolean.parseBoolean(this.configuration.getProperty("scriptureofJas"));
            useWeaponPoison = Boolean.parseBoolean(this.configuration.getProperty("useWeaponPoison"));
            usequickPrayers = Boolean.parseBoolean(this.configuration.getProperty("usequickPrayers"));
            animateDead = Boolean.parseBoolean(this.configuration.getProperty("animateDead"));
            useScrimshaws = Boolean.parseBoolean(this.configuration.getProperty("useScrimshaws"));
            makePlanks = Boolean.parseBoolean(this.configuration.getProperty("makePlanks"));
            makeRefinedPlanks = Boolean.parseBoolean(this.configuration.getProperty("makeRefinedPlanks"));
            makeFrames = Boolean.parseBoolean(this.configuration.getProperty("makeFrames"));
            isMiscActive = Boolean.parseBoolean(this.configuration.getProperty("isMiscActive"));
            isPlanksActive = Boolean.parseBoolean(this.configuration.getProperty("isPlanksActive"));
            isportermakerActive = Boolean.parseBoolean(this.configuration.getProperty("isPorterMakerActive"));
            soulAltar = Boolean.parseBoolean(this.configuration.getProperty("soulAltar"));
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
            String radiusValue = this.configuration.getProperty("radius");
            if (radiusValue != null && !radiusValue.isEmpty()) {
                int radius = Integer.parseInt(radiusValue);
                if (radius < 0) radius = 0;
                else if (radius > 25) radius = 25;
                Combat.radius = radius;
            }
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
            log("[Settings] Configuration loaded successfully.");
        } catch (Exception e) {
            log("[Error] Failed to load configuration. Using defaults.");
        }
    }
}

