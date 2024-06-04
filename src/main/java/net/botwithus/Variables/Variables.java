package net.botwithus.Variables;

import net.botwithus.Combat.Combat;
import net.botwithus.Herblore.Herblore;
import net.botwithus.Herblore.SharedState;
import net.botwithus.SnowsScript;
import net.botwithus.TaskScheduler;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;

import static ImGui.PredefinedStrings.predefinedNames;
import static ImGui.PredefinedStrings.recipeNamesList;
import static net.botwithus.SnowsScript.BotState.IDLE;

public class Variables {




// =====================
// SECTION 1: Agility
// =====================
    public static final Coordinate LOG_BALANCE = new Coordinate(2474, 3436, 0);
    public static final Coordinate OBSTACLE_NET = new Coordinate(2474, 3429, 0);
    public static final Coordinate TREE_BRANCH = new Coordinate(2473, 3423, 1);
    public static final Coordinate BALANCING_ROPE = new Coordinate(2473, 3420, 2);
    public static final Coordinate TREE_BRANCH_2 = new Coordinate(2483, 3420, 2);
    public static final Coordinate OBSTACLE_NET_2 = new Coordinate(2487, 3417, 0);
    public static final Coordinate OBSTACLE_PIPE = new Coordinate(2487, 3427, 0);
    public static boolean isAgilityActive = false;



// =====================
// SECTION 2: Archaeology
// =====================
    public static NativeInteger selectedItemIndex = new NativeInteger(0);  // Archaeology
    public static List<String> allItems = new ArrayList<>(predefinedNames);  // Archaeology
    public static List<String> filteredItems = new ArrayList<>(predefinedNames);  // Archaeology
    public static boolean MaterialCache = false;
    public static boolean isArcheologyActive = false;
    public static boolean archaeologistsTea = false;
    public static boolean hiSpecMonocle = false;
    public static boolean materialManual = false;
    public static boolean useGote = false;
    public static final String[] porterTypes = {"Sign of the porter I", "Sign of the porter II", "Sign of the porter III", "Sign of the porter IV", "Sign of the porter V", "Sign of the porter VI", "Sign of the porter VII"};
    public static final String[] quantities = {"ALL", "1", "5", "10"};
    public static final NativeInteger currentPorterType = new NativeInteger(0);
    public static final NativeInteger currentQuantity = new NativeInteger(0);
    public static int chargeThreshold = 200;
    public static int equipChargeThreshold = 0;
    public static void removeName(String name) {
        selectedArchNames.remove(name);
    }
    public static void setName(String Rock1Name) {
        Rock1 = Rock1Name;
    }
    public static Coordinate lastSpotAnimationCoordinate = null;

    public static int getChargeThreshold() {
        return chargeThreshold;
    }
    public static int getEquipChargeThreshold() {
        return equipChargeThreshold;
    }
    public static void setEquipChargeThreshold(int equipChargeThreshold) {
        Variables.equipChargeThreshold = equipChargeThreshold;
    }
    public static void setChargeThreshold(int chargeThreshold) {
        Variables.chargeThreshold = chargeThreshold;
    }
    public static String Rock1 = "";
    public static List<String> selectedArchNames = new ArrayList<>();

    public static String getName() {
        return Rock1;
    }

    public static List<String> getSelectedNames() {
        return selectedArchNames;
    }

    public static void addName(String name) {
        if (!selectedArchNames.contains(name)) {
            selectedArchNames.add(name);
        }
    }
    public static void filterItems() {
        filteredItems.clear();
        for (String item : allItems) {
            if (item.toLowerCase().contains(filterText.toLowerCase())) {
                filteredItems.add(item);
            }
        }
    }



// =====================
// SECTION 3: Combat
// =====================
    public static boolean isCombatActive = false;
    public static boolean interactWithLootAll = false;
    public static boolean useVulnerabilityBombs = false;
    public static boolean useLoot = false;
    public static boolean useOverloads = false;
    public static boolean usePrayerPots = false;
    public static boolean useAggroPots = false;
    public static boolean BankforFood = false;
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
    public static boolean KeepArmyup = false;
    public static void setPrayerPointsThreshold(int threshold) {
        prayerPointsThreshold = threshold;
    }
    public static void setHealthThreshold(int healthThreshold) {
        healthPointsThreshold = healthThreshold;
    }
    public static int getPrayerPointsThreshold() {
        return prayerPointsThreshold;
    }
    public static int getHealthPointsThreshold() {
        return healthPointsThreshold;
    }
    public static int prayerPointsThreshold = 5000;
    public static int healthPointsThreshold = 50;
    public static String targetName = "";
    public static final List<String> targetNames = new ArrayList<>();
    public static String Item = "";
    public static String getItemName() {
        return Item;
    }
    public static void setItemName(String ItemName) {
        Item = ItemName;
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
    public static List<String> targetItemNames = new ArrayList<>();
    public static String selectedItem = "";

    public static List<String> getTargetItemNames() {
        return targetItemNames;
    }

    public static String getSelectedItem() {
        return selectedItem;
    }

    public static void setSelectedItem(String selectedItem) {
        Variables.selectedItem = selectedItem;
    }



// =====================
// SECTION 4: Cooking
// =====================
    public static boolean isCookingActive = false;
    public static boolean AnimationCheck = false;



// =====================
// SECTION 5: Divination
// =====================
    public static boolean isDivinationActive = false;
    public static boolean offerChronicles = false;
    public static boolean useDivineoMatic = false;
    public static boolean useFamiliarSummoning = false;
    public static boolean harvestChronicles = false;



// =====================
// SECTION 6: Fishing
// =====================
    public static boolean isFishingActive = false;
    public static Coordinate lastFishingSpotCoord = null;
    public static void removeFishingLocation(String location) {
        selectedFishingLocations.remove(location);
    }
    public static String getFishingAction() {
        return fishingAction;
    }
    public static void setFishingAction(String action) {
        fishingAction = action;
    }
    public static List<String> getSelectedFishingActions() {
        return new ArrayList<>(selectedFishingActions);
    }
    public static String fishingLocation = "";
    public static String fishingAction = "";
    public static List<String> selectedFishingLocations = new ArrayList<>();
    public static List<String> selectedFishingActions = new ArrayList<>();
    public static String getFishingLocation() {
        return fishingLocation;
    }
    public static void setFishingLocation(String location) {
        fishingLocation = location;
    }
    public static List<String> getSelectedFishingLocations() {
        return selectedFishingLocations;
    }
    public static void removeFishingAction(String action) {
        selectedFishingActions.remove(action);
    }
    public static void addFishingLocation(String location) {
        if (location != null && !location.trim().isEmpty() && !selectedFishingLocations.contains(location)) {
            selectedFishingLocations.add(location);
        }
    }
    public static void addFishingAction(String action) {
        if (action != null && !action.trim().isEmpty() && !selectedFishingActions.contains(action)) {
            selectedFishingActions.add(action);
        }
    }
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }




    // =====================
// SECTION 7: Herblore
// =====================
    public static boolean isHerbloreActive = false;
    public static boolean makeBombs = false;
    //TODO
    public static final String[] recipeNames = recipeNamesList.toArray(new String[0]);
    public static final NativeInteger currentRecipeIndex = new NativeInteger(0);
    public static String selectedCategory = "";
    public static NativeInteger currentCategoryIndex = new NativeInteger(0);
    public static Herblore.HerbloreRecipe getSelectedRecipe() {
        return SharedState.selectedRecipe;
    }




// =====================
// SECTION 8: Mining
// =====================
    public static boolean isMiningActive = false;
    public static void removeRockName(String name) {
        selectedRockNames.remove(name);
    }
    public static void setRockName(String RockName) {
        Rock = RockName;
    }
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



// =====================
// SECTION 9: Miscellaneous
// =====================
    public static boolean pickCaveNightshade = false;
    public static boolean isPlanksActive = false;
    public static boolean isCorruptedOreActive = false;
    public static boolean makePlanks = false;
    public static boolean makeRefinedPlanks = false;
    public static boolean makeFrames = false;
    public static boolean isMiscActive = false;
    public static boolean isCrystalChestActive = false;
    public static boolean isMakeUrnsActive = false;
    public static boolean isDissasemblerActive;
    public static int itemMenuSize = 1; // Add this line
    public static int itemsDestroyed = 0;
    public static boolean useAlchamise = false;
    public static boolean useDisassemble = false;
    public static boolean isportermakerActive;
    public static boolean isdivinechargeActive;
    public static boolean isGemCutterActive = false;
    public static boolean isSiftSoilActive = false;
    public static boolean isSmeltingActive = false;
    public static boolean makeWines = false;
    public static List<TaskScheduler> tasks = new ArrayList<>();
    public static TaskScheduler getActiveTask() {
        if (!tasks.isEmpty()) {
            return tasks.get(0);
        }
        return null;
    }
    public static void addTask(TaskScheduler task) {
        tasks.add(task);
    }



// =====================
// SECTION 10: Runecrafting
// =====================
    public static boolean isRunecraftingActive = false;
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
    public static int getLoopCounter() {
        return loopCounter;
    }
    public static int loopCounter = 0;


// =====================
// SECTION 11: Summoning
// =====================
    public static boolean isSummoningActive;
    public static boolean useSpiritStone;
    public static boolean usePrifddinas;
    public static final NativeInteger spiritStone_current_idx = new NativeInteger(0);
    public static final NativeInteger pouchName_current_idx = new NativeInteger(0);
    public static final NativeInteger secondaryItem_current_idx = new NativeInteger(0);
    public static final int INVENTORYID = 94;
    public static final int OBOLISK_ID = 67036;
    public static final String INFUSE_POUCH_OPTION = "Infuse-pouch";
    public static int secondaryItem = 1444;
    public static String spiritStoneName = "Spirit onyx (a)";
    public static String pouchName = "Geyser titan pouch";
    public static final int INTERFACE_ID = 1370;
    public static final int DELAY_MIN = 660;
    public static final int DELAY_MAX = 720;
    public static void setSecondaryItem(int newSecondaryItem) {
        secondaryItem = newSecondaryItem;
    }
    public static int getSecondaryItem() {
        return secondaryItem;
    }
    public static void setSpiritStoneName(String newSpiritStoneName) {
        spiritStoneName = newSpiritStoneName;
    }
    public static String getPouchName() {
        return pouchName;
    }
    public static void setPouchName(String newPouchName) {
        pouchName = newPouchName;
    }


// =====================
// SECTION 12: Thieving
// =====================
    public static boolean isThievingActive = false;




// =====================
// SECTION 13: Woodcutting
// =====================
    public static boolean isWoodcuttingActive = false;
    public static boolean acadiaVIP = false;
    public static boolean crystallise = false;
    public static Coordinate currentTreeCoordinate = null; // Add this line
    public static long lastCrystalliseCast = 0;
    public static boolean crystalliseMahogany = false;
    public static boolean acadiaTree = false;
    public static String Tree = "";
    public static final List<String> selectedTreeNames = new ArrayList<>();
    public static String getTreeName() {
        return Tree;
    }
    public static List<String> getSelectedTreeNames() {
        return selectedTreeNames;
    }
    public static void removeTreeName(String name) {
        selectedTreeNames.remove(name);
    }
    public static void setTreeName(String TreeName) {
        Tree = TreeName;
    }
    public static final int TREE_OBJECT_ID = 109007; // Acadia in VIP
    public static void addTreeName(String name) {
        if (!selectedTreeNames.contains(name)) {
            selectedTreeNames.add(name);
        }
    }
    public static int currentTreeIndex = 0;
    public static List<Coordinate> treeCoordinates = Arrays.asList(
            new Coordinate(3183, 2722, 0),
            new Coordinate(3183, 2716, 0),
            new Coordinate(3189, 2722, 0)
    );
    public static List<Coordinate> vipTreeCoordinates = Arrays.asList(
            new Coordinate(3180, 2753, 0),
            new Coordinate(3180, 2747, 0)
    );
    public static List<Coordinate> mahoganyCoordinates = Arrays.asList(
            new Coordinate(2819, 3079, 0)
    );




// =====================
// SECTION 14: Components and Maps
// =====================
public static void dialog(int option1, int option2, int option3) {
    MiniMenu.interact(ComponentAction.DIALOGUE.getType(), option1, option2, option3);
}
    public static boolean component(int option1, int option2, int option3) {
        MiniMenu.interact(ComponentAction.COMPONENT.getType(), option1, option2, option3);
        return false;
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



// =====================
// SECTION 15: Variables
// =====================
    public static boolean nearestBank = false;
    public static final Map<BooleanSupplier, Runnable> skillingTasks = new HashMap<>();
    public static Coordinate lastSkillingLocation;
    public static Random random = new Random();
    public static Player player = Client.getLocalPlayer();



// =====================
// SECTION 16: ImGui
// =====================
    public static String filterText = ""; // Archaeology
    public static boolean ScriptisOn = false;
    public static long totalElapsedTime = 0;
    public static boolean tooltipsEnabled = false;
    public static String saveSettingsFeedbackMessage = "";
    public static boolean showLogs = false;
    public static boolean scrollToBottom = false;
}
