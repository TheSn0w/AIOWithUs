package net.botwithus;

import ImGui.SkeletonScriptGraphicsContext;
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
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.Misc.*;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

import static ImGui.SkeletonScriptGraphicsContext.*;
import static net.botwithus.Misc.Dissasembler.*;
import static net.botwithus.Misc.Summoning.isSummoningActive;
import static net.botwithus.Runecrafting.ScriptState.TELEPORTINGTOBANK;
import static net.botwithus.SnowsScript.BotState.IDLE;
import static net.botwithus.SnowsScript.BotState.SKILLING;


public class SnowsScript extends LoopingScript {


    public static BotState botState = IDLE;
    public static boolean attackOsseous;
    public Instant startTime;
    static boolean ScriptisOn = false;
    private Coordinate lastSkillingLocation;
    public static Instant scriptStartTime;
    long runStartTime;
    private boolean someBool = true;
    private Random random = new Random();
    public boolean agility;
    Divination divination;
    public boolean isDivinationActive;
    Banking Banking;
    Mining mining;
    Woodcutting woodcutting;
    Fishing fishing;
    public boolean isFishingActive;
    public boolean isWoodcuttingActive;
    public boolean isMiningActive;
    public boolean nearestBank;
    public boolean isHerbloreActive;
    Herblore herblore;
    Combat combat;
    public boolean isCombatActive;
    Archeology archeology;
    public boolean isArcheologyActive;
    public boolean BankforFood;
    Thieving thieving;
    public boolean isThievingActive;
    Cooking cooking;
    public boolean makeWines;
    Planks planks;
    public boolean isRunecraftingActive;
    Runecrafting runecrafting;
    public boolean isGemCutterActive;
    GemCutter GemCutter;
    public boolean isSmeltingActive;
    Smelter Smelter;
    CorruptedOre CorruptedOre;
    public boolean isPlanksActive;
    public boolean isCorruptedOreActive;

    public boolean interactWithLootAll;
    public boolean useLootInterface;
    public boolean useLoot;
    public boolean isCookingActive;
    public boolean useOverloads;
    public boolean usePrayerPots;
    public boolean useAggroPots;
    public boolean MaterialCache;
    public boolean AnimationCheck;
    private final Coordinate LOG_BALANCE = new Coordinate(2474, 3436, 0);
    private final Coordinate OBSTACLE_NET = new Coordinate(2474, 3429, 0);
    private final Coordinate TREE_BRANCH = new Coordinate(2473, 3423, 1);
    private final Coordinate BALANCING_ROPE = new Coordinate(2473, 3420, 2);
    private final Coordinate TREE_BRANCH_2 = new Coordinate(2483, 3420, 2);
    private final Coordinate OBSTACLE_NET_2 = new Coordinate(2487, 3417, 0);
    private final Coordinate OBSTACLE_PIPE = new Coordinate(2487, 3427, 0);
    private final Coordinate RESTART = new Coordinate(2487, 3437, 0);
    final Coordinate VarrockWest = new Coordinate(3182, 3436, 0);
    final Coordinate VarrockEast = new Coordinate(3252, 3420, 0);
    final Coordinate GrandExchange = new Coordinate(3162, 3484, 0);
    final Coordinate Canafis = new Coordinate(3512, 3480, 0);
    final Coordinate AlKharid = new Coordinate(3271, 3168, 0);
    final Coordinate Lumbridge = new Coordinate(3214, 3257, 0);
    final Coordinate Draynor = new Coordinate(3092, 3245, 0);
    final Coordinate FaladorEast = new Coordinate(3012, 3355, 0);
    final Coordinate SmithingGuild = new Coordinate(3060, 3339, 0);
    final Coordinate FaladorWest = new Coordinate(2946, 3368, 0);
    final Coordinate Burthorpe = new Coordinate(2888, 3536, 0);
    final Coordinate Taverly = new Coordinate(2875, 3417, 0);
    final Coordinate Catherby = new Coordinate(2795, 3440, 0);
    final Coordinate Seers = new Coordinate(2724, 3493, 0);
    final Coordinate ArdougneSouth = new Coordinate(2655, 3283, 0);
    final Coordinate ArdougneNorth = new Coordinate(2616, 3332, 0);
    final Coordinate Yanille = new Coordinate(2613, 3094, 0);
    final Coordinate Ooglog = new Coordinate(2556, 2840, 0);
    final Coordinate CityOfUm = new Coordinate(1149, 1804, 1);
    final Coordinate PrifddinasCenter = new Coordinate(2205, 3368, 1);
    final Coordinate PrifddinasEast = new Coordinate(2232, 3310, 1);
    final Coordinate WarsRetreat = new Coordinate(3299, 10131, 0);
    final Coordinate Anachronia = new Coordinate(5465, 2342, 0);
    final Coordinate Edgeville = new Coordinate(3096, 3496, 0);
    final Coordinate KharidEt = new Coordinate(3356, 3197, 0);
    final Coordinate VIP = new Coordinate(3182, 2742, 0);
    public static boolean makePlanks = false;
    public static boolean makeRefinedPlanks = false;
    public static boolean makeFrames = false;
    public static boolean isMiscActive = false;
    PorterMaker porterMaker;



    /*public void start() {
        println("Attempting to start script...");
        if (!ScriptisOn) {
            ScriptisOn = true;
            scriptStartTime = Instant.now();
            println("Script started at: " + scriptStartTime);

            // Subscribe to ChatMessageEvent
            EventBus.EVENT_BUS.subscribe(this, ChatMessageEvent.class, this::onChatMessageEvent);
            *//*EventBus.EVENT_BUS.subscribe(this, InventoryUpdateEvent.class, this::onInventoryUpdate);*//*
        } else {
            println("Attempted to start script, but it is already running.");
        }
    }*/


    /*public static void stop(SkeletonScript instance) {
            Instant stopTime = Instant.now();
            ScriptConsole.println("Script stopped at: " + stopTime);
            long duration = Duration.between(scriptStartTime, stopTime).toMillis();
            ScriptConsole.println("Script ran for: " + duration + " milliseconds.");

            // Unsubscribe from ChatMessageEvent
            EventBus.EVENT_BUS.unsubscribe(instance, ChatMessageEvent.class, instance::onChatMessageEvent);
            EventBus.EVENT_BUS.unsubscribe(instance, InventoryUpdateEvent.class, instance::onInventoryUpdate);
    }*/

    public static void setBotState(BotState state) {
        botState = state; // Method to set botState
    }

    public BotState getBotState() {
        return botState; // Method to get botState
    }

    public enum BotState {
        IDLE,
        SKILLING,
        BANKING,
    }

    public SnowsScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new SkeletonScriptGraphicsContext(getConsole(), this);
        this.divination = new Divination();
        this.isDivinationActive = false;
        this.mining = new Mining(this);
        this.isMiningActive = false;
        this.woodcutting = new Woodcutting(this);
        this.isWoodcuttingActive = false;
        this.fishing = new Fishing(this);
        this.isFishingActive = false;
        this.combat = new Combat(this);
        this.isCombatActive = false;
        this.BankforFood = false;
        this.archeology = new Archeology(this);
        this.isArcheologyActive = false;
        this.thieving = new Thieving(this);
        this.isThievingActive = false;
        this.herblore = new Herblore(this);
        this.isHerbloreActive = false;
        this.planks = new Planks();
        this.isPlanksActive = false;
        this.cooking = new Cooking();
        this.isCookingActive = false;
        isMiscActive = false;
        this.porterMaker = new PorterMaker(this);
        this.CorruptedOre = new CorruptedOre(this);
        this.Banking = new Banking();
        this.isGemCutterActive = false;
        this.GemCutter = new GemCutter(this);
        this.isRunecraftingActive = false;
        this.Smelter = new Smelter(this);
        this.isGemCutterActive = false;
        this.runecrafting = new Runecrafting(this);
        this.loadConfiguration();
        this.startTime = Instant.now();
        this.runStartTime = System.currentTimeMillis();
        EventBus.EVENT_BUS.subscribe(this, ChatMessageEvent.class, this::onChatMessageEvent);
        EventBus.EVENT_BUS.subscribe(this, InventoryUpdateEvent.class, this::onInventoryUpdate);

    }

    public void onLoop() {
        LocalPlayer player = Client.getLocalPlayer();

        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            return;
        }
        List<String> selectedRockNames = Mining.getSelectedRockNames();
        List<String> selectedTreeNames = Woodcutting.getSelectedTreeNames();
        List<String> selectedFishingLocations = Fishing.getSelectedFishingLocations();
        List<String> selectedFishingActions = Fishing.getSelectedFishingActions();
        List<String> selectedArchNames = Archeology.getSelectedNames();

        switch (botState) {
            case IDLE -> {
                Execution.delay(random.nextLong(1500, 3000));
            }
            case SKILLING -> {
                if (isHerbloreActive) {
                    Execution.delay(herblore.handleHerblore(player));
                }
                if (isRunecraftingActive && !Runecrafting.soulAltar) {
                    runecrafting.handleRunecrafting(player);
                }
                if (isRunecraftingActive && Runecrafting.soulAltar) {
                    Execution.delay(runecrafting.handleEdgevillebanking());
                }
                if (isMiningActive) {
                    Execution.delay(mining.handleMining(player, selectedRockNames));
                }
                if (isThievingActive) {
                    Execution.delay(thieving.handleThieving(player));
                }
                if (isWoodcuttingActive) {
                    Execution.delay(woodcutting.handleSkillingWoodcutting(player, selectedTreeNames));
                    if (Woodcutting.crystallise) {
                        Execution.delay(woodcutting.handleit());
                    }
                    if (Woodcutting.crystalliseMahogany) {
                        Execution.delay(woodcutting.handleCrystalliseMahogany());
                    }
                }
                if (agility) {
                    Execution.delay(handleSkillingAgility(player));
                }
                if (isDivinationActive) {
                    Execution.delay(Divination.handleDivination(player));
                }
                if (isFishingActive) {
                    if (!selectedFishingLocations.isEmpty() && !selectedFishingActions.isEmpty()) {
                        Execution.delay(fishing.handleFishing(player, selectedFishingLocations.get(0), selectedFishingActions.get(0)));
                    }
                }
                if (isCombatActive) {
                    if (Combat.enableRadiusTracking) {
                        Execution.delay(Combat.ensureWithinRadius(player));
                    }
                    if (Combat.usePOD) {
                        combat.handlePOD();
                    }
                    if (!Combat.usePOD && !Combat.handleArchGlacor && !attackOsseous) {
                        Execution.delay(combat.attackTarget(player));
                    }
                    if (Combat.handleArchGlacor) {
                        Execution.delay(combat.handleArchGlacor());
                    }
                    if (interactWithLootAll) {
                        combat.LootEverything();
                    }
                    if (attackOsseous) {
                        Npc osseous = NpcQuery.newQuery().name("Osseous").results().nearest();
                        if (osseous != null) {
                            combat.handleBossAnimation(player, osseous);
                        }
                    }
                }
                if (isCookingActive && !makeWines) {
                    Execution.delay(cooking.handleCooking());
                }
                if (isCookingActive && makeWines) {
                    Execution.delay(cooking.useGrapesOnJugOfWater());
                }
                if (isArcheologyActive)
                    Execution.delay(archeology.findSpotAnimationAndAct(player, selectedArchNames));
                if (isMiscActive) {
                    if (PorterMaker.isportermakerActive) {
                        Execution.delay(porterMaker.makePorters());
                    }
                    if (PorterMaker.isdivinechargeActive) {
                        Execution.delay(porterMaker.divineCharges());
                    }
                    if (isPlanksActive) {
                        Execution.delay(planks.handlePlankMaking());
                    }
                    if (isCorruptedOreActive) {
                        Execution.delay(CorruptedOre.mineCorruptedOre());
                    }
                    if (isSummoningActive && Summoning.usePrifddinas) {
                        Execution.delay(Summoning.makePouches(player));
                    } else if (isSummoningActive) {
                        Execution.delay(Summoning.interactWithObolisk(player));
                    }
                    if (isDissasemblerActive) {
                        if (useDisassemble) {
                            Execution.delay(Dissasembler.Dissasemble(player));
                        }
                        if (Dissasembler.useAlchamise) {
                            Execution.delay(Dissasembler.castHighLevelAlchemy(player));
                        }
                    }
                    if (isGemCutterActive) {
                        Execution.delay(GemCutter.cutGems());
                    }
                    if (isSmeltingActive) {
                        Execution.delay(Smelter.handleSmelter(player));
                    }
                }
            }

            case BANKING -> {
                if (isThievingActive) {
                    Execution.delay(thieving.bankForfood());
                }
                if (nearestBank) {
                    Execution.delay(useTheNearestBank(player));
                }
                if (BankforFood) {
                    Execution.delay(combat.BankforFood(player));
                }
                if (isArcheologyActive)
                    Execution.delay(archeology.BankforArcheology(player, selectedArchNames));
            }
        }
    }
    private long idle() {
        return random.nextLong(1500, 3000);
    }


    public final Map<String, Integer> fishCookedCount = new HashMap<>();
    public final Map<String, Integer> logCount = new HashMap<>();
    public final Map<String, Integer> nestCount = new HashMap<>();
    public final Map<String, Integer> fishCaughtCount = new HashMap<>();
    public final Map<String, Integer> materialsExcavated = new HashMap<>();
    public final Map<String, Integer> chroniclesCaughtCount = new HashMap<>();
    public final Map<String, Integer> portersMade = new HashMap<>();


    public final Map<String, Integer> corruptedOre = new HashMap<>();
    public final Map<String, Integer> runeCount = new HashMap<>();
    public final Map<String, Integer> energy = new HashMap<>();


    private void onInventoryUpdate(InventoryUpdateEvent event) {
        if (event.getInventoryId() != 93) {
            return; // If the inventory ID is not 93, ignore the event
        }
        if (isCorruptedOreActive) {
            String itemName = event.getNewItem().getName(); // Assume adding items only
            if ("Corrupted ore".equals(itemName)) { // Check if the item is 'Corrupted Ore'
                int oldCount = event.getOldItem().getStackSize();
                int newCount = event.getNewItem().getStackSize();
                if (newCount < oldCount) { // Check if the count has decreased
                    int count = corruptedOre.getOrDefault(itemName, 0);
                    corruptedOre.put(itemName, count + 1); // Increment the count
                }
            }
        }

        if (isCombatActive) {
            String itemName = event.getNewItem().getName(); // Assume adding items only
            if (itemName.endsWith(" charm")) { // Check if the item is a charm
                int oldCount = event.getOldItem() != null ? event.getOldItem().getStackSize() : 0;
                int newCount = event.getNewItem().getStackSize();
                if (newCount > oldCount) { // Check if the new stack size is greater than the old stack size
                    int quantity = newCount - oldCount; // Calculate the quantity of new charms obtained

                    // Update the appropriate Charms map with the charm name as the key
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
                            return; // If the charm is not one of the expected types, ignore it
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

                // Get the current count of the rune in the runeCount map
                int currentCount = runeCount.getOrDefault(itemName, 0);

                // Calculate the total rune count by adding the current count and the new count
                int totalRuneCount = currentCount + newCount;

                // Update the runeCount map with the total rune count and print the message
                runeCount.put(itemName, totalRuneCount);
                ScriptConsole.println("Rune count updated: " + totalRuneCount + " " + itemName + " - Traversing to bank");
                Runecrafting.currentState = TELEPORTINGTOBANK;
            }
        }
        if (isDivinationActive) {
            String itemName = event.getNewItem().getName(); // Assume adding items only
            if ("Incandescent energy".equals(itemName)) { // Check if the item is 'Incandescent energy'
                int oldCount = event.getOldItem() != null ? event.getOldItem().getStackSize() : 0;
                int newCount = event.getNewItem().getStackSize();
                if (newCount > oldCount) { // Check if the new stack size is greater than the old stack size
                    int quantity = newCount - oldCount; // Calculate the quantity of new energy obtained

                    // Get the current count of the energy in the energy map
                    int currentCount = energy.getOrDefault(itemName, 0);

                    // Update the energy map with the total energy count
                    energy.put(itemName, currentCount + quantity);
                }
            }
        }
        if (isDissasemblerActive) {
            if (botState == BotState.SKILLING) {
                TaskScheduler activeTask = getActiveTask();
                if (activeTask != null && Objects.requireNonNull(event.getNewItem().getName()).contains(activeTask.getItemToDisassemble())) {
                    Dissasembler.itemsDestroyed++;
                    activeTask.incrementAmountDisassembled();
                }
            }
        }
    }

    public final Map<String, Integer> BlueCharms = new HashMap<>();
    public final Map<String, Integer> CrimsonCharms = new HashMap<>();
    public final Map<String, Integer> GreenCharms = new HashMap<>();
    public final Map<String, Integer> GoldCharms = new HashMap<>();
    public final Map<String, Integer> Potions = new HashMap<>();
    public final Map<String, Integer> extraPotions = new HashMap<>();

    void onChatMessageEvent(ChatMessageEvent event) {
        String message = event.getMessage();
        if (PorterMaker.isportermakerActive) {
            if (message.contains("You create: 1")) {
                String itemType = message.substring(message.indexOf("1") + 2).trim();
                itemType = itemType.replace(".", ""); // Remove the period
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
                Execution.delay(runecrafting.handleCharging());
            }
            if (message.contains("You do no have any essence to deposit")) {
                Execution.delay(runecrafting.handleEdgevillebanking());
            }
            if (message.contains("The altar is already charged to its maximum capacity")) {
                runecrafting.handleSoulAltar();
            }
        }
        if (isCookingActive) {
            if (message.contains("You successfully cook")) {
                String fishType = message.substring(message.lastIndexOf("cook") + 5).trim();
                int count = fishCookedCount.getOrDefault(fishType, 0);
                fishCookedCount.put(fishType, count + 1);
            }
            if (message.contains("Your extreme cooking potion is about to wear off.")) {
                cooking.cookingPotion();
            }
        }

        if (isWoodcuttingActive) {
            if (message.contains("You get some")) {
                String logType = message.substring(message.lastIndexOf("some") + 5).trim();
                logType = logType.replace(".", ""); // Remove the period
                int count = logCount.getOrDefault(logType, 0);
                logCount.put(logType, count + 1);
            }
            if (message.toLowerCase().contains("bird's nest")) {
                int count = nestCount.getOrDefault("Bird's nest", 0);
                nestCount.put("Bird's nest", count + 1);
            }
            if (message.contains("You transport the following item to your bank:")) {
                String logType = message.substring(message.lastIndexOf("bank:") + 6).trim();
                logType = logType.replace(".", ""); // Remove the period
                int count = logCount.getOrDefault(logType, 0);
                logCount.put(logType, count + 1);
            }
            if (message.contains("As you cut from the tree, the log:")) {
                String logType = message.substring(message.lastIndexOf("log:") + 7).trim();
                logType = logType.replace(".", ""); // Remove the period
                int count = logCount.getOrDefault(logType, 0);
                logCount.put(logType, count + 1);
            }
            if (message.contains("Crystallise takes your resource and converts it into XP.")) {
                String key = "Resources"; // The key for the logCount map
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
    public int getTextValue() {
        ComponentTextRetriever textRetriever = new ComponentTextRetriever();
        String text = textRetriever.getComponentText();

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            ScriptConsole.println("[Main] Error: Could not parse text to integer.");
            return 0;
        }
    }

    public void setLastSkillingLocation(Coordinate location) {
        this.lastSkillingLocation = location;
    }

    public Area.Rectangular getLastSkillingLocation() {
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


    public long useTheNearestBank(LocalPlayer player) {
        if (player.isMoving()) {
            return random.nextLong(1500, 3000);
        }

        Coordinate nearestBank = findNearestBank(player.getCoordinate());
        if (nearestBank != null) {
            double distanceToBank = Distance.between(player.getCoordinate(), nearestBank);

            String bankType = (nearestBank == WarsRetreat || nearestBank == Anachronia || nearestBank == PrifddinasEast || nearestBank == CityOfUm || nearestBank == SmithingGuild || nearestBank == KharidEt || nearestBank == Lumbridge || nearestBank == VIP) ? "Bank chest" :
                    (nearestBank == Edgeville || nearestBank == Draynor) ? "Counter" :
                            "Bank booth";
            List<SceneObject> bankBooths = SceneObjectQuery.newQuery().name(bankType).results().stream()
                    .filter(booth -> booth.getCoordinate().distanceTo(player.getCoordinate()) < 25.0D)
                    .toList();

            SceneObject randomBankBooth;
            if (!bankBooths.isEmpty()) {
                randomBankBooth = bankBooths.get(random.nextInt(bankBooths.size()));

                if (distanceToBank < 25.0D) {
                    println("[Main] Bank is near, interacting with nearest bank");
                    return interactWithBank(player, randomBankBooth);
                }
            } else {
                println("[Main] Bank is far, traversing to bank");
                Movement.traverse(NavPath.resolve(nearestBank));
            }
        } else {
            println("[Main] Nearest bank not found.");
        }
        return random.nextLong(1500, 3000);
    }

    public Coordinate findNearestBank(Coordinate playerPosition) {
        int textValue = getTextValue();

        List<Coordinate> bankCoordinates = Arrays.asList(
                WarsRetreat, VIP, AlKharid, Edgeville, Burthorpe, KharidEt, Catherby, Anachronia, CityOfUm, PrifddinasCenter, PrifddinasEast, Yanille, Ooglog, ArdougneSouth, ArdougneNorth, Seers, Taverly, FaladorWest, FaladorEast, Lumbridge, Draynor, GrandExchange, VarrockEast, VarrockWest, Canafis
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


    private long interactWithBank(LocalPlayer player, SceneObject nearestBankBooth) {
        if (player.isMoving()) {
            return random.nextLong(1500, 3000);
        }
        boolean actionBank = Backpack.containsItemByCategory(4448);

        if (actionBank) {
            String interactionOption = "Bank";
            boolean interactionSuccess = nearestBankBooth.interact(interactionOption);
            println("[Main] Interacting with the bank: " + interactionSuccess);

            if (interactionSuccess) {
                Execution.delayUntil(15000, Bank::isOpen);
                if (Bank.isOpen()) {
                    Execution.delay(random.nextLong(1500, 3000));
                    Item oreBox = InventoryItemQuery.newQuery(93).category(4448).results().first();
                    Pattern oreBoxesPattern = Pattern.compile("(?i)Bronze ore box|Iron ore box|Steel ore box|Mithril ore box|Adamant ore box|Rune ore box|Orikalkum ore box|Necronium ore box|Bane ore box|Elder rune ore box");

                    if (oreBox != null) {
                        Bank.depositAllExcept(oreBoxesPattern);
                        println("[Main] Deposited everything except: " + oreBox.getName());

                        if (oreBox.getSlot() >= 0) {
                            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 8, oreBox.getSlot(), 33882127);
                            println("[Main] Emptied: " + oreBox.getName());
                        }
                    }
                }
            }
        } else {
            String interactionOption = "Load Last Preset from";
            boolean interactionSuccess = nearestBankBooth.interact(interactionOption);
            println("[Main] Interacting with the bank using Load Last Preset: " + interactionSuccess);

            Execution.delayUntil(15000, () -> !Backpack.isFull());

            if (!Backpack.isFull()) {
                println("[Main] Backpack is not full, returning to last skilling location.");
                if (Movement.traverse(NavPath.resolve(getLastSkillingLocation())) == TraverseEvent.State.FINISHED) {
                    setBotState(SKILLING);
                }
            }
        }
        return random.nextLong(1500, 3000);
    }





    private void interactWithSceneObject(LocalPlayer player, Coordinate location, String objectName, String interaction, Coordinate successLocation) {
        if (player.getCoordinate().equals(location)) {
            SceneObject nearestObject = SceneObjectQuery.newQuery().name(objectName).results().nearest();
            if (nearestObject != null) {
                if (nearestObject.interact(interaction)) {
                    println("[Main] Interacted with: " + objectName);
                    Execution.delayUntil(random.nextLong(10000, 15000), () -> player.getCoordinate().equals(successLocation));
                    Execution.delay(random.nextLong(500, 1500));
                }
            }
        }
    }

    public long handleSkillingAgility(LocalPlayer player) {
        int agilityLevel = Skills.AGILITY.getActualLevel();

        if (agilityLevel >= 1 && agilityLevel <= 34) {
            println("[Main] Using Tree Gnome agility course.");

            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(69526).option("Walk-across").results();
            if (results.isEmpty() && !player.isMoving()) {
                println("[Main] Walking to the starting point of the agility course.");
                Movement.traverse(NavPath.resolve(LOG_BALANCE));
                return random.nextLong(1500, 3000);
            }
            if (!results.isEmpty() && !player.isMoving() && !LOG_BALANCE.equals(player.getCoordinate())) {
                println("[Main] Player is not at the correct location, walking to LOG_BALANCE.");
                Movement.walkTo(LOG_BALANCE.getX(), LOG_BALANCE.getY(), true);
                return random.nextLong(1500, 3000);
            }

            interactWithSceneObject(player, LOG_BALANCE, "Log balance", "Walk-across", OBSTACLE_NET);
            interactWithSceneObject(player, OBSTACLE_NET, "Obstacle net", "Climb-over", TREE_BRANCH);
            interactWithSceneObject(player, TREE_BRANCH, "Tree branch", "Climb", BALANCING_ROPE);
            interactWithSceneObject(player, BALANCING_ROPE, "Balancing rope", "Walk-on", TREE_BRANCH_2);
            interactWithSceneObject(player, TREE_BRANCH_2, "Tree branch", "Climb-down", OBSTACLE_NET_2);
            interactWithSceneObject(player, OBSTACLE_NET_2, "Obstacle net", "Climb-over", OBSTACLE_PIPE);
            interactWithSceneObject(player, OBSTACLE_PIPE, "Obstacle pipe", "Squeeze-through", RESTART);
        }
        return random.nextLong(1500, 3000);
    }




    public void saveConfiguration() {
        this.configuration.addProperty("InvokeDeath", String.valueOf(Combat.InvokeDeath));
        this.configuration.addProperty("SpecialAttack", String.valueOf(Combat.SpecialAttack));
        this.configuration.addProperty("VolleyofSouls", String.valueOf(Combat.VolleyofSouls));
        this.configuration.addProperty("DeathGrasp", String.valueOf(Combat.DeathGrasp));
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
        this.configuration.addProperty("offerChronicles", String.valueOf(Divination.offerChronicles));
        this.configuration.addProperty("Combat", String.valueOf(isCombatActive));
        this.configuration.addProperty("SoulSplit", String.valueOf(Combat.SoulSplit));
        this.configuration.addProperty("KeepArmyup", String.valueOf(Combat.KeepArmyup));
        String targetNamesSerialized = String.join(",", Combat.targetNames);
        this.configuration.addProperty("TargetNames", targetNamesSerialized);
        String ExcavationNamesSerialized = String.join(",", Archeology.selectedArchNames);
        this.configuration.addProperty("selectedArchNames", ExcavationNamesSerialized);
        String MiningNamesSerialized = String.join(",", Mining.selectedRockNames);
        this.configuration.addProperty("selectedRockNames", MiningNamesSerialized);
        String FishingLocationsSerialized = String.join(",", Fishing.selectedFishingLocations);
        this.configuration.addProperty("selectedFishingLocations", FishingLocationsSerialized);
        String FishingActionsSerialized = String.join(",", Fishing.selectedFishingActions);
        this.configuration.addProperty("selectedFishingActions", FishingActionsSerialized);
        String WoodcuttingNamesSerialized = String.join(",", Woodcutting.selectedTreeNames);
        this.configuration.addProperty("selectedTreeNames", WoodcuttingNamesSerialized);
        String EatingFoodNamesSerialized = String.join(",", Combat.selectedFoodNames);
        this.configuration.addProperty("selectedFoodNames", EatingFoodNamesSerialized);
        this.configuration.addProperty("interactWithLootAll", String.valueOf(interactWithLootAll));
        this.configuration.addProperty("useLoot", String.valueOf(useLoot));
        String InteractWithLootAllSerialized = String.join(",", Combat.targetItemNames);
        this.configuration.addProperty("selectedItemNames", InteractWithLootAllSerialized);
        this.configuration.addProperty("VolleyOfSoulsThreshold", String.valueOf(Combat.VolleyOfSoulsThreshold));
        this.configuration.addProperty("NecrosisStacksThreshold", String.valueOf(Combat.NecrosisStacksThreshold));
        this.configuration.addProperty("usePOD", String.valueOf(Combat.usePOD));
        this.configuration.addProperty("scriptureofJas", String.valueOf(Combat.scriptureofJas));
        this.configuration.addProperty("scriptureofWen", String.valueOf(Combat.scriptureofWen));
        this.configuration.addProperty("useWeaponPoison", String.valueOf(Combat.useWeaponPoison));
        this.configuration.addProperty("usequickPrayers", String.valueOf(Combat.usequickPrayers));
        this.configuration.addProperty("animateDead", String.valueOf(Combat.animateDead));
        this.configuration.addProperty("useScrimshaws", String.valueOf(Combat.useScrimshaws));
        this.configuration.addProperty("makePlanks", String.valueOf(makePlanks));
        this.configuration.addProperty("makeRefinedPlanks", String.valueOf(makeRefinedPlanks));
        this.configuration.addProperty("makeFrames", String.valueOf(makeFrames));
        this.configuration.addProperty("isMiscActive", String.valueOf(isMiscActive));
        this.configuration.addProperty("isPlanksActive", String.valueOf(isPlanksActive));
        this.configuration.addProperty("isPorterMakerActive", String.valueOf(PorterMaker.isportermakerActive));
        this.configuration.addProperty("soulAltar", String.valueOf(Runecrafting.soulAltar));
        this.configuration.addProperty("isRunecraftingActive", String.valueOf(isRunecraftingActive));
        this.configuration.addProperty("RingofDueling", String.valueOf(Runecrafting.RingofDueling));
        this.configuration.addProperty("HandleBoneAltar", String.valueOf(Runecrafting.HandleBoneAltar));
        this.configuration.addProperty("HandleFleshAltar", String.valueOf(Runecrafting.HandleFleshAltar));
        this.configuration.addProperty("HandleMiasmaAltar", String.valueOf(Runecrafting.HandleMiasmaAltar));
        this.configuration.addProperty("HandleSpiritAltar", String.valueOf(Runecrafting.HandleSpiritAltar));
        this.configuration.addProperty("WearingRing", String.valueOf(Runecrafting.WearingRing));
        this.configuration.addProperty("ManageFamiliar", String.valueOf(Runecrafting.ManageFamiliar));
        this.configuration.addProperty("Powerburst", String.valueOf(Runecrafting.Powerburst));
        this.configuration.addProperty("notWearingRing", String.valueOf(Runecrafting.notWearingRing));
        this.configuration.addProperty("isCorruptedOreActive", String.valueOf(isCorruptedOreActive));
        this.configuration.addProperty("handleArchGlacor", String.valueOf(Combat.handleArchGlacor));
        this.configuration.addProperty("enableRadiusTracking", String.valueOf(Combat.enableRadiusTracking));
        this.configuration.addProperty("radius", String.valueOf(Combat.radius));
        this.configuration.addProperty("hiSpecMonocle", String.valueOf(Archeology.hiSpecMonocle));
        this.configuration.addProperty("materialManual", String.valueOf(Archeology.materialManual));
        this.configuration.addProperty("archaeologistsTea", String.valueOf(Archeology.archaeologistsTea));
        this.configuration.addProperty("isHerbloreActive", String.valueOf(isHerbloreActive));
        this.configuration.addProperty("makeBombs", String.valueOf(Herblore.makeBombs));
        this.configuration.addProperty("useGraceoftheElves", String.valueOf(Runecrafting.useGraceoftheElves));
        this.configuration.addProperty("useGote", String.valueOf(Archeology.useGote));
        this.configuration.addProperty("harvestChronicles", String.valueOf(Divination.harvestChronicles));
        this.configuration.addProperty("useFamiliarSummoning", String.valueOf(Divination.useFamiliarSummoning));
        this.configuration.addProperty("useDivineoMatic", String.valueOf(Divination.useDivineoMatic));
        String selectedPorterType = porterTypes[currentPorterType.get()];
        this.configuration.addProperty("selectedPorterType", selectedPorterType);
        String selectedQuantity = quantities[currentQuantity.get()];
        this.configuration.addProperty("selectedQuantity", selectedQuantity);
        this.configuration.addProperty("selectedItemToDisassemble", Item);
        this.configuration.addProperty("useAlchamise", String.valueOf(Dissasembler.useAlchamise));
        this.configuration.addProperty("useDisassemble", String.valueOf(Dissasembler.useDisassemble));
        this.configuration.addProperty("isDissasemblerActive", String.valueOf(isDissasemblerActive));
        this.configuration.addProperty("isGemCutterActive", String.valueOf(isGemCutterActive));
        this.configuration.addProperty("isSmeltingActive", String.valueOf(isSmeltingActive));
        this.configuration.addProperty("isSummoningActive", String.valueOf(isSummoningActive));
        this.configuration.addProperty("isdivinechargeActive", String.valueOf(PorterMaker.isdivinechargeActive));
        this.configuration.save();
    }

    public void loadConfiguration() {
        try {
            PorterMaker.isdivinechargeActive = Boolean.parseBoolean(this.configuration.getProperty("isdivinechargeActive"));
            isGemCutterActive = Boolean.parseBoolean(this.configuration.getProperty("isGemCutterActive"));
            isSmeltingActive = Boolean.parseBoolean(this.configuration.getProperty("isSmeltingActive"));
            isSummoningActive = Boolean.parseBoolean(this.configuration.getProperty("isSummoningActive"));
            isDissasemblerActive = Boolean.parseBoolean(this.configuration.getProperty("isDissasemblerActive"));
            this.agility = Boolean.parseBoolean(this.configuration.getProperty("agility"));
            this.isDivinationActive = Boolean.parseBoolean(this.configuration.getProperty("isDivinationActive"));
            this.isMiningActive = Boolean.parseBoolean(this.configuration.getProperty("isMiningActive"));
            this.isWoodcuttingActive = Boolean.parseBoolean(this.configuration.getProperty("isWoodcuttingActive"));
            this.isFishingActive = Boolean.parseBoolean(this.configuration.getProperty("isFishingActive"));
            this.isCombatActive = Boolean.parseBoolean(this.configuration.getProperty("isCombatActive"));
            this.isArcheologyActive = Boolean.parseBoolean(this.configuration.getProperty("isArcheologyActive"));
            this.isThievingActive = Boolean.parseBoolean(this.configuration.getProperty("isThievingActive"));
            this.BankforFood = Boolean.parseBoolean(this.configuration.getProperty("iseatFoodActive"));
            this.isCookingActive = Boolean.parseBoolean(this.configuration.getProperty("isCookingActive"));
            this.useOverloads = Boolean.parseBoolean(this.configuration.getProperty("useOverloads"));
            this.usePrayerPots = Boolean.parseBoolean(this.configuration.getProperty("usePrayerPots"));
            this.useAggroPots = Boolean.parseBoolean(this.configuration.getProperty("useAggroPots"));
            this.MaterialCache = Boolean.parseBoolean(this.configuration.getProperty("MaterialCache"));
            Divination.offerChronicles = Boolean.parseBoolean(this.configuration.getProperty("offerChronicles"));
            this.isCombatActive = Boolean.parseBoolean(this.configuration.getProperty("Combat"));
            this.interactWithLootAll = Boolean.parseBoolean(this.configuration.getProperty("interactWithLootAll"));
            this.useLoot = Boolean.parseBoolean(this.configuration.getProperty("useLoot"));
            Herblore.makeBombs = Boolean.parseBoolean(this.configuration.getProperty("makeBombs"));
            this.isHerbloreActive = Boolean.parseBoolean(this.configuration.getProperty("isHerbloreActive"));
            Combat.usePOD = Boolean.parseBoolean(this.configuration.getProperty("usePOD"));
            Combat.SoulSplit = Boolean.parseBoolean(this.configuration.getProperty("SoulSplit"));
            Combat.KeepArmyup = Boolean.parseBoolean(this.configuration.getProperty("KeepArmyup"));
            Combat.SpecialAttack = Boolean.parseBoolean(this.configuration.getProperty("SpecialAttack"));
            Combat.VolleyofSouls = Boolean.parseBoolean(this.configuration.getProperty("VolleyofSouls"));
            Combat.InvokeDeath = Boolean.parseBoolean(this.configuration.getProperty("InvokeDeath"));
            Combat.DeathGrasp = Boolean.parseBoolean(this.configuration.getProperty("DeathGrasp"));
            Combat.scriptureofWen = Boolean.parseBoolean(this.configuration.getProperty("scriptureofWen"));
            Combat.scriptureofJas = Boolean.parseBoolean(this.configuration.getProperty("scriptureofJas"));
            Combat.useWeaponPoison = Boolean.parseBoolean(this.configuration.getProperty("useWeaponPoison"));
            Combat.usequickPrayers = Boolean.parseBoolean(this.configuration.getProperty("usequickPrayers"));
            Combat.animateDead = Boolean.parseBoolean(this.configuration.getProperty("animateDead"));
            Combat.useScrimshaws = Boolean.parseBoolean(this.configuration.getProperty("useScrimshaws"));
            makePlanks = Boolean.parseBoolean(this.configuration.getProperty("makePlanks"));
            makeRefinedPlanks = Boolean.parseBoolean(this.configuration.getProperty("makeRefinedPlanks"));
            makeFrames = Boolean.parseBoolean(this.configuration.getProperty("makeFrames"));
            isMiscActive = Boolean.parseBoolean(this.configuration.getProperty("isMiscActive"));
            isPlanksActive = Boolean.parseBoolean(this.configuration.getProperty("isPlanksActive"));
            PorterMaker.isportermakerActive = Boolean.parseBoolean(this.configuration.getProperty("isPorterMakerActive"));
            Runecrafting.soulAltar = Boolean.parseBoolean(this.configuration.getProperty("soulAltar"));
            isRunecraftingActive = Boolean.parseBoolean(this.configuration.getProperty("isRunecraftingActive"));
            Runecrafting.RingofDueling = Boolean.parseBoolean(this.configuration.getProperty("RingofDueling"));
            Runecrafting.HandleBoneAltar = Boolean.parseBoolean(this.configuration.getProperty("HandleBoneAltar"));
            Runecrafting.HandleFleshAltar = Boolean.parseBoolean(this.configuration.getProperty("HandleFleshAltar"));
            Runecrafting.HandleMiasmaAltar = Boolean.parseBoolean(this.configuration.getProperty("HandleMiasmaAltar"));
            Runecrafting.HandleSpiritAltar = Boolean.parseBoolean(this.configuration.getProperty("HandleSpiritAltar"));
            Runecrafting.WearingRing = Boolean.parseBoolean(this.configuration.getProperty("WearingRing"));
            Runecrafting.ManageFamiliar = Boolean.parseBoolean(this.configuration.getProperty("ManageFamiliar"));
            Runecrafting.Powerburst = Boolean.parseBoolean(this.configuration.getProperty("Powerburst"));
            Runecrafting.notWearingRing = Boolean.parseBoolean(this.configuration.getProperty("notWearingRing"));
            Runecrafting.useGraceoftheElves = Boolean.parseBoolean(this.configuration.getProperty("useGraceoftheElves"));
            isCorruptedOreActive = Boolean.parseBoolean(this.configuration.getProperty("isCorruptedOreActive"));
            Combat.handleArchGlacor = Boolean.parseBoolean(this.configuration.getProperty("handleArchGlacor"));
            Combat.enableRadiusTracking = Boolean.parseBoolean(this.configuration.getProperty("enableRadiusTracking"));
            Archeology.hiSpecMonocle = Boolean.parseBoolean(this.configuration.getProperty("hiSpecMonocle"));
            Archeology.materialManual = Boolean.parseBoolean(this.configuration.getProperty("materialManual"));
            Archeology.archaeologistsTea = Boolean.parseBoolean(this.configuration.getProperty("archaeologistsTea"));
            Divination.harvestChronicles = Boolean.parseBoolean(this.configuration.getProperty("harvestChronicles"));
            Divination.useFamiliarSummoning = Boolean.parseBoolean(this.configuration.getProperty("useFamiliarSummoning"));
            Divination.useDivineoMatic = Boolean.parseBoolean(this.configuration.getProperty("useDivineoMatic"));
            Archeology.useGote = Boolean.parseBoolean(this.configuration.getProperty("useGote"));
            Dissasembler.useAlchamise = Boolean.parseBoolean(this.configuration.getProperty("useAlchamise"));
            Dissasembler.useDisassemble = Boolean.parseBoolean(this.configuration.getProperty("useDisassemble"));
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
                Combat.NecrosisStacksThreshold = necrosisThreshold;
            }

            String volleyThresholdValue = this.configuration.getProperty("VolleyOfSoulsThreshold");
            if (volleyThresholdValue != null && !volleyThresholdValue.isEmpty()) {
                int volleyThreshold = Integer.parseInt(volleyThresholdValue);
                if (volleyThreshold < 0) volleyThreshold = 0;
                Combat.VolleyOfSoulsThreshold = volleyThreshold;
            }
            String InteractWithLootAllSerialized = this.configuration.getProperty("selectedItemNames");
            if (InteractWithLootAllSerialized != null && !InteractWithLootAllSerialized.isEmpty()) {
                String[] loadedInteractWithLootAll = InteractWithLootAllSerialized.split(",");
                Combat.targetItemNames.clear();
                Combat.targetItemNames.addAll(Arrays.asList(loadedInteractWithLootAll));
            }
            String targetNamesSerialized = this.configuration.getProperty("TargetNames");
            if (targetNamesSerialized != null && !targetNamesSerialized.isEmpty()) {
                String[] loadedTargetNames = targetNamesSerialized.split(",");
                Combat.targetNames.clear();
                for (String targetName : loadedTargetNames) {
                    Combat.addTargetName(targetName);
                }
            }
            String ExcavationNamesSerialized = this.configuration.getProperty("selectedArchNames");
            if (ExcavationNamesSerialized != null && !ExcavationNamesSerialized.isEmpty()) {
                String[] loadedExcavationNames = ExcavationNamesSerialized.split(",");
                Archeology.selectedArchNames.clear();
                for (String excavationName : loadedExcavationNames) {
                    Archeology.addName(excavationName);
                }
            }
            String MiningNamesSerialized = this.configuration.getProperty("selectedRockNames");
            if (MiningNamesSerialized != null && !MiningNamesSerialized.isEmpty()) {
                String[] loadedMiningNames = MiningNamesSerialized.split(",");
                Mining.selectedRockNames.clear();
                for (String miningName : loadedMiningNames) {
                    Mining.addRockName(miningName);
                }
            }
            String FishingLocationsSerialized = this.configuration.getProperty("selectedFishingLocations");
            if (FishingLocationsSerialized != null && !FishingLocationsSerialized.isEmpty()) {
                String[] loadedFishingLocations = FishingLocationsSerialized.split(",");
                Fishing.selectedFishingLocations.clear();
                for (String fishingLocation : loadedFishingLocations) {
                    Fishing.addFishingLocation(fishingLocation);
                }
            }
            String FishingActionsSerialized = this.configuration.getProperty("selectedFishingActions");
            if (FishingActionsSerialized != null && !FishingActionsSerialized.isEmpty()) {
                String[] loadedFishingActions = FishingActionsSerialized.split(",");
                Fishing.selectedFishingActions.clear();
                for (String fishingAction : loadedFishingActions) {
                    Fishing.addFishingAction(fishingAction);
                }
            }
            String WoodcuttingNamesSerialized = this.configuration.getProperty("selectedTreeNames");
            if (WoodcuttingNamesSerialized != null && !WoodcuttingNamesSerialized.isEmpty()) {
                String[] loadedWoodcuttingNames = WoodcuttingNamesSerialized.split(",");
                Woodcutting.selectedTreeNames.clear();
                for (String woodcuttingName : loadedWoodcuttingNames) {
                    Woodcutting.addTreeName(woodcuttingName);
                }
            }
            String EatingFoodNamesSerialized = this.configuration.getProperty("selectedFoodNames");
            if (EatingFoodNamesSerialized != null && !EatingFoodNamesSerialized.isEmpty()) {
                String[] loadedEatingFoodNames = EatingFoodNamesSerialized.split(",");
                Combat.selectedFoodNames.clear();
                for (String foodName : loadedEatingFoodNames) {
                    Combat.addFoodName(foodName);
                }
            }
            println("Configuration loaded successfully.");
        } catch (Exception e) {
            println("Failed to load configuration. Using defaults.");
        }
    }
}

