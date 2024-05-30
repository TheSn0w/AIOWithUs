package net.botwithus.Variables;

import net.botwithus.Cooking;
import net.botwithus.Misc.CaveNightshade;
import net.botwithus.Misc.PorterMaker;
import net.botwithus.Runecrafting;
import net.botwithus.SnowsScript;
import net.botwithus.TaskScheduler;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.script.ScriptConsole;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

import static net.botwithus.SnowsScript.BotState.IDLE;

public class Variables {
    public static boolean pickCaveNightshade = false;
    public static boolean isAgilityActive = false;
    public static boolean isPlanksActive = false;
    public static Cooking cooking;
    public static boolean isCorruptedOreActive = false;
    public static boolean interactWithLootAll = false;
    public static boolean useLoot = false;
    public static boolean isCookingActive = false;
    public static boolean useOverloads = false;
    public static boolean usePrayerPots = false;
    public static boolean useAggroPots = false;
    public static boolean MaterialCache = false;
    public static boolean AnimationCheck = false;
    public static boolean agility = false;
    public static boolean isDivinationActive = false;
    public static boolean isFishingActive = false;
    public static boolean isWoodcuttingActive = false;
    public static boolean isMiningActive = false;
    public static boolean nearestBank = false;
    public static boolean isHerbloreActive = false;
    public static boolean isCombatActive = false;
    public static boolean isArcheologyActive = false;
    public static boolean BankforFood = false;
    public static boolean isThievingActive = false;
    public static boolean makeWines = false;
    public static boolean isRunecraftingActive = false;
    public static boolean isGemCutterActive = false;
    public static boolean isSmeltingActive = false;
    public static SnowsScript.BotState botState = IDLE;
    public static Instant startTime;
    public static Coordinate lastSkillingLocation;
    public static Instant scriptStartTime;
    public static long runStartTime;
    public static Random random = new Random();
    public static boolean archaeologistsTea = false;
    public static boolean hiSpecMonocle = false;
    public static boolean materialManual = false;
    public static boolean useGote = false;
    public static boolean DeathGrasp = false;
    public static boolean handleArchGlacor = false;
    public static boolean usePOD = false;
    public static boolean InvokeDeath = false;
    public static boolean VolleyofSouls = false;
    public static boolean SpecialAttack = false;
    public static boolean SoulSplit = false;
    public static boolean useWeaponPoison = false;
    public static boolean scriptureofWen = false;
    public static boolean scriptureofJas = false;
    public static boolean animateDead = false;
    public static boolean usequickPrayers = false;
    public static boolean useScrimshaws = false;
    public static boolean DeflectMagic = false;
    public static boolean DeflectMissiles = false;
    public static boolean DeflectMelee = false;
    public static boolean KeepArmyup = false;
    public static int prayerPointsThreshold = 5000;
    public static int healthPointsThreshold = 50;
    public static String targetName = "";
    public static final List<String> targetNames = new ArrayList<>();
    public static boolean offerChronicles = false;
    public static boolean useDivineoMatic = false;
    public static boolean useFamiliarSummoning = false;
    public static boolean harvestChronicles = false;
    public static boolean makeBombs = false;
    public static boolean HandleMiasmaAltar = false;
    public static boolean HandleBoneAltar = false;
    public static boolean HandleSpiritAltar = false;
    public static boolean HandleFleshAltar = false;
    public static boolean ManageFamiliar = false;
    public static boolean Powerburst = false;
    public static boolean notWearingRing = false;
    public static boolean WearingRing = false;
    public static boolean RingofDueling = false;
    public static boolean useWorldhop = false;
    public static boolean soulAltar = false;
    public static boolean useGraceoftheElves = false;
    public static boolean isDissasemblerActive;
    public static List<TaskScheduler> tasks = new ArrayList<>();
    public static int itemMenuSize = 1; // Add this line
    public static int itemsDestroyed = 0;
    public static boolean useAlchamise = false;
    public static boolean useDisassemble = false;
    public static boolean isportermakerActive;
    public static boolean isdivinechargeActive;
    public static boolean isSummoningActive;
    public static boolean useSpiritStone;
    public static boolean usePrifddinas;
    public static final Coordinate LOG_BALANCE = new Coordinate(2474, 3436, 0);
    public static final Coordinate OBSTACLE_NET = new Coordinate(2474, 3429, 0);
    public static final Coordinate TREE_BRANCH = new Coordinate(2473, 3423, 1);
    public static final Coordinate BALANCING_ROPE = new Coordinate(2473, 3420, 2);
    public static final Coordinate TREE_BRANCH_2 = new Coordinate(2483, 3420, 2);
    public static final Coordinate OBSTACLE_NET_2 = new Coordinate(2487, 3417, 0);
    public static final Coordinate OBSTACLE_PIPE = new Coordinate(2487, 3427, 0);
    public static final Coordinate VarrockWest = new Coordinate(3182, 3436, 0);
    public static final Coordinate VarrockEast = new Coordinate(3252, 3420, 0);
    public static final Coordinate GrandExchange = new Coordinate(3162, 3484, 0);
    public static final Coordinate Canafis = new Coordinate(3512, 3480, 0);
    public static final Coordinate AlKharid = new Coordinate(3271, 3168, 0);
    public static final Coordinate Lumbridge = new Coordinate(3214, 3257, 0);
    public static final Coordinate Draynor = new Coordinate(3092, 3245, 0);
    public static final Coordinate FaladorEast = new Coordinate(3012, 3355, 0);
    public static final Coordinate SmithingGuild = new Coordinate(3060, 3339, 0);
    public static final Coordinate FaladorWest = new Coordinate(2946, 3368, 0);
    public static final Coordinate Burthorpe = new Coordinate(2888, 3536, 0);
    public static final Coordinate Taverly = new Coordinate(2875, 3417, 0);
    public static final Coordinate Catherby = new Coordinate(2795, 3440, 0);
    public static final Coordinate Seers = new Coordinate(2724, 3493, 0);
    public static final Coordinate ArdougneSouth = new Coordinate(2655, 3283, 0);
    public static final Coordinate ArdougneNorth = new Coordinate(2616, 3332, 0);
    public static final Coordinate Yanille = new Coordinate(2613, 3094, 0);
    public  static final Coordinate Ooglog = new Coordinate(2556, 2840, 0);
    public static final Coordinate CityOfUm = new Coordinate(1149, 1804, 1);
    public static final Coordinate prifWest = new Coordinate(2153, 3340, 1);
    public static final Coordinate PrifddinasCenter = new Coordinate(2205, 3368, 1);
    public static final Coordinate PrifddinasEast = new Coordinate(2232, 3310, 1);
    public static final Coordinate WarsRetreat = new Coordinate(3299, 10131, 0);
    public static final Coordinate Anachronia = new Coordinate(5465, 2342, 0);
    public static final Coordinate Edgeville = new Coordinate(3096, 3496, 0);
    public static final Coordinate KharidEt = new Coordinate(3356, 3197, 0);
    public static final Coordinate VIP = new Coordinate(3182, 2742, 0);
    public static boolean makePlanks = false;
    public static boolean makeRefinedPlanks = false;
    public static boolean makeFrames = false;
    public static boolean isMiscActive = false;

    public static TaskScheduler getActiveTask() {
        if (!tasks.isEmpty()) {
            return tasks.get(0); // Return the first task in the list
        }
        return null; // Return null if the list is empty
    }

    public static void addTask(TaskScheduler task) {
        tasks.add(task);
    }

    public static String Item = "";

    public static String getItemName() {
        return Item;
    }
    public static void setItemName(String ItemName) {
        Item = ItemName;
    }

    public static int getLoopCounter() {
        return loopCounter;
    }

    public static int loopCounter = 0;



    public static String Rock = "";
    public static List<String> selectedRockNames = new ArrayList<>();

    public static String getRockName() {
        return Rock;
    }

    public static List<String> getSelectedRockNames() {
        return selectedRockNames;
    }

    public static void addRockName(String name) {
        if (!selectedRockNames.contains(name)) {
            selectedRockNames.add(name);
        }
    }

    public static void removeRockName(String name) {
        selectedRockNames.remove(name);
    }

    public static void setRockName(String RockName) {
        Rock = RockName;
    }

    public static final List<String> selectedFoodNames = new ArrayList<>();

    public static String FoodName = "";

    public static String getFoodName() {
        return FoodName;
    }

    public static void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public static List<String> getSelectedFoodNames() {
        return selectedFoodNames;
    }

    public static void addFoodName(String name) {
        if (!selectedFoodNames.contains(name)) {
            selectedFoodNames.add(name);
        }
    }

    private static List<String> combatList = new ArrayList<>();

    public static void removeFoodName(String name) {
        selectedFoodNames.remove(name);
    }


    public static void addTargetName(String targetName) {
        ScriptConsole.println("[Combat] Adding target name: " + targetName);
        String lowerCaseName = targetName.toLowerCase();
        synchronized (targetNames) {
            if (!targetNames.contains(lowerCaseName)) {
                targetNames.add(lowerCaseName);
            }
        }
    }

    public static void addTarget(String target) {
        if (!combatList.contains(target)) {
            combatList.add(target);
        }
    }

    public static void removeTargetName(String targetName) {
        synchronized (targetNames) {
            targetNames.remove(targetName.toLowerCase());
        }
    }

    public static List<String> getTargetNames() {
        synchronized (targetNames) {
            return new ArrayList<>(targetNames);
        }
    }

    public static Pattern generateRegexPattern(List<String> names) {
        return Pattern.compile(
                names.stream()
                        .map(Pattern::quote)
                        .reduce((name1, name2) -> name1 + "|" + name2)
                        .orElse(""),
                Pattern.CASE_INSENSITIVE
        );
    }
    public static boolean acadiaTree = false;

    public static String Tree = "";
    public static final List<String> selectedTreeNames = new ArrayList<>();

    public static String getTreeName() {
        return Tree;
    }

    public static List<String> getSelectedTreeNames() {
        return selectedTreeNames;
    }

    public static void addTreeName(String name) {
        if (!selectedTreeNames.contains(name)) {
            selectedTreeNames.add(name);
        }
    }

    public static void removeTreeName(String name) {
        selectedTreeNames.remove(name);
    }

    public static void setTreeName(String TreeName) {
        Tree = TreeName;
    }
    public static final Map<String, Integer> fishCookedCount = new HashMap<>();
    public static final Map<String, Integer> logCount = new HashMap<>();
    public static final Map<String, Integer> nestCount = new HashMap<>();
    public static final Map<String, Integer> fishCaughtCount = new HashMap<>();
    public static final Map<String, Integer> materialsExcavated = new HashMap<>();
    public static final Map<String, Integer> materialTypes = new HashMap<>();
    public static final Map<String, Integer> chroniclesCaughtCount = new HashMap<>();
    public static final Map<String, Integer> portersMade = new HashMap<>();
    public static final Map<String, Integer> corruptedOre = new HashMap<>();
    public static final Map<String, Integer> runeCount = new HashMap<>();
    public static final Map<String, Integer> energy = new HashMap<>();
    public static final Map<String, Integer> types = new HashMap<>();
    public static final Map<String, Integer> BlueCharms = new HashMap<>();
    public static final Map<String, Integer> CrimsonCharms = new HashMap<>();
    public static final Map<String, Integer> GreenCharms = new HashMap<>();
    public static final Map<String, Integer> GoldCharms = new HashMap<>();
    public static final Map<String, Integer> Potions = new HashMap<>();
}
