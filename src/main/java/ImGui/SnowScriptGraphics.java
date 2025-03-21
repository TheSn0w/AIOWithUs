package ImGui;

import net.botwithus.*;
import net.botwithus.Combat.Combat;
import net.botwithus.Combat.LootManager;
import net.botwithus.Slayer.NPCs;
import net.botwithus.rs3.events.EventBus;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.ScenePosition;
import net.botwithus.rs3.imgui.*;
import net.botwithus.rs3.script.ImmutableScript;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptController;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.SnowsScript;
import net.botwithus.rs3.script.events.PropertyUpdateRequestEvent;


import java.awt.*;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static ImGui.CentreButton.createCenteredButton;
import static ImGui.Skills.ArchaeologyImGui.renderArchaeology;
import static ImGui.Skills.BottomChild.renderBottom;
import static ImGui.Skills.CombatImGui.renderCombat;
import static ImGui.Skills.CombatImGui.resetSlayerPoints;
import static ImGui.Skills.CookingImGui.renderCooking;
import static ImGui.Skills.DivinationImGui.renderDivination;
import static ImGui.Skills.FishingImGui.renderFishing;
import static ImGui.Skills.HerbloreImGui.renderHerblore;
import static ImGui.Skills.MiningImGui.renderMining;
import static ImGui.Skills.MiscImGui.renderMisc;
import static ImGui.Skills.RunecraftingImGui.renderRunecrafting;
import static ImGui.Skills.ThievingImGui.renderThieving;
import static ImGui.Skills.WoodcuttingImGui.renderWoodcutting;
import static ImGui.Theme.*;
import static net.botwithus.Archaeology.Archeology.dropSoil;
import static net.botwithus.Archaeology.Archeology.hopWorldsforCaches;
import static net.botwithus.Archaeology.WorldHop.hopWorldsforArchaeology;
import static net.botwithus.Combat.ItemRemover.isDropActive;
import static net.botwithus.Combat.LootManager.useLootAllNotedItems;
import static net.botwithus.Combat.LootManager.walkToLoot;
import static net.botwithus.Combat.Travel.useHintArrow;
import static net.botwithus.Combat.Travel.useTraveltoLocation;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Runecrafting.Abyss.useAbyssRunecrafting;
import static net.botwithus.Runecrafting.Astral.useAstralAltar;
import static net.botwithus.Runecrafting.SteamRunes.useSteamRunes;
import static net.botwithus.Slayer.Main.doSlayer;
import static net.botwithus.Slayer.Main.useBankPin;
import static net.botwithus.SnowsScript.setBotState;
import static net.botwithus.Variables.BankInteractions.useDepositBox;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.script.ScriptController.getActiveScript;

public class SnowScriptGraphics extends ScriptGraphicsContext {

    SnowsScript script;

    public Instant startTime;


    public SnowScriptGraphics(ScriptConsole scriptConsole, SnowsScript script) {
        super(scriptConsole);
        this.script = script;
        this.startTime = Instant.now();
    }

    public void setActive(String name, boolean active) {
        ImmutableScript activeScript = getActiveScript();
        if (activeScript == null) {
            log("No active script to set active");
        } else {
            ImmutableScript owner = ScriptController.getScripts().stream()
                    .filter(script -> script.getName().equals(name))
                    .findFirst()
                    .orElse(null);
            if(owner == null) {
                log("No script found with name: " + name);
                return;
            }
            EventBus.EVENT_BUS.publish(owner, new PropertyUpdateRequestEvent(activeScript, "isActive", Boolean.toString(active)));
            log("Script " + name + " set to " + (active ? "active" : "inactive"));
        }
    }

    public static Thread combatThread;
    public static Thread lootManagerThread;




    @Override
    public void drawSettings() {
        if (Client.getLocalPlayer() != null && Client.getGameState() == Client.GameState.LOGGED_IN) {
            drawTile(Client.getLocalPlayer().getScene());

        }
        setDefaultTheme();
        applyBlueTheme();

        if (PurpleThemeSelected) {
            applyPurpleTheme();
        } else if (BlueThemeSelected) {
            applyBlueTheme();
        } else if (RedThemeSelected) {
            applyRedTheme();
        } else if (YellowThemeSelected) {
            applyYellowTheme();
        } else if (GreenThemeSelected) {
            applyGreenTheme();
        } else if (OrangeThemeSelected) {
            applyOrangeTheme();
        }

        float columnWidth = 180;
        float childWidth = columnWidth - 10;

        if (ImGui.Begin("AIO Settings", ImGuiWindowFlag.NoNav.getValue() | ImGuiWindowFlag.NoResize.getValue() | ImGuiWindowFlag.NoCollapse.getValue() | ImGuiWindowFlag.NoTitleBar.getValue())) {
            ImGui.SetWindowSize((float) 610, (float) 510);
            ImGui.Columns(2, "Column", false);
            ImGui.SetColumnWidth(0, columnWidth);
            float windowHeight = 415;


            if (ImGui.BeginChild("Column1", childWidth, windowHeight, true, 0)) {
                String buttonText;
                float textWidth1;
                float padding;

                buttonText = ScriptisOn ? "Pause Script" : "Resume Script";
                textWidth1 = ImGui.CalcTextSize(buttonText).getX();
                padding = (childWidth - textWidth1) / 2;
                ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding, 15);
                ImGui.SetCursorPosX(0);
                if (ImGui.Button(buttonText)) {
                    if (ScriptisOn) {
                        ScriptisOn = false;
                        Stopwatch.stop();

                        if (combatThread != null) {
                            combatThread.interrupt();
                            log("[Thread] Stopped CombatAbilities thread");
                        }
                        if (lootManagerThread != null) {
                            log("[Thread] Stopped LootManager thread");
                            lootManagerThread.interrupt();
                        }
                        script.unsubscribeAll();

                        script.pause();
                    } else {
                        ScriptisOn = true;
                        Stopwatch.start();
                        script.saveConfiguration();
                        setBotState(SnowsScript.BotState.SKILLING);

                        resetSlayerPoints();
                        script.subscribeToEvents();

                        Combat combat = new Combat(script);
                        combatThread = Thread.ofVirtual().name("CombatAbilities").start(combat::manageCombatAbilities);
                        if (combatThread.isAlive()) {
                            log("[Thread] Started CombatAbilities thread");
                        }

                        LootManager lootManager = new LootManager(script);
                        lootManagerThread = Thread.ofVirtual().name("LootManager").start(lootManager::manageLoot);
                        if (lootManagerThread.isAlive()) {
                            log("[Thread] Started LootManager thread");
                        }

                        script.resume();
                    }
                }
                ImGui.PopStyleVar(1);
                buttonText = "Save Settings";
                textWidth1 = ImGui.CalcTextSize(buttonText).getX();
                padding = (childWidth - textWidth1) / 2;
                ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding, 5);
                ImGui.SetCursorPosX(0);
                if (ImGui.Button(buttonText)) {
                    try {
                        script.saveConfiguration();
                        saveSettingsFeedbackMessage = "Settings saved successfully.";
                    } catch (Exception e) {
                        saveSettingsFeedbackMessage = "Failed to save settings: " + e.getMessage();
                    }
                }
                ImGui.PopStyleVar(1);

                boolean anySelected =
                        isAgilityActive ||
                                isDivinationActive ||
                                isThievingActive ||
                                isArcheologyActive ||
                                isCombatActive ||
                                isFishingActive ||
                                isMiningActive ||
                                isWoodcuttingActive ||
                                isCookingActive ||
                                isRunecraftingActive ||
                                isMiscActive ||
                                isHerbloreActive;


                if (!anySelected) {
                    createCenteredButton("Agility", () -> isAgilityActive = !isAgilityActive, isAgilityActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("1-35 Agility Only`");
                    }
                    createCenteredButton("Divination AIO", () -> isDivinationActive = !isDivinationActive, isDivinationActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Divination AIO 1-99");
                    }
                    createCenteredButton("Thieving", () -> isThievingActive = !isThievingActive, isThievingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Thieving - Read Tooltip");
                    }
                    createCenteredButton("Archaeology", () -> isArcheologyActive = !isArcheologyActive, isArcheologyActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Archaeology with Material Caches Etc..");
                    }
                    createCenteredButton("Fishing", () -> isFishingActive = !isFishingActive, isFishingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Fishing at any spot");
                    }
                    createCenteredButton("Mining", () -> isMiningActive = !isMiningActive, isMiningActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Mining at any spot");
                    }
                    createCenteredButton("Woodcutting", () -> isWoodcuttingActive = !isWoodcuttingActive, isWoodcuttingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Woodcutting at any spot Except Fort Forthry");
                    }
                    createCenteredButton("Cooking", () -> isCookingActive = !isCookingActive, isCookingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Cooking AIO with Fish, Recommended at Fort Forthry");
                    }
                    createCenteredButton("Combat", () -> isCombatActive = !isCombatActive, isCombatActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("AIO Fighter");
                    }
                    createCenteredButton("Runecrafting", () -> isRunecraftingActive = !isRunecraftingActive, isRunecraftingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Runecrafting made easy doing Necrotic Runes");
                    }
                    createCenteredButton("Herblore", () -> isHerbloreActive = !isHerbloreActive, isHerbloreActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Use at `Bank chest` with a Portable Well nearby");
                    }
                    createCenteredButton("Misc", () -> isMiscActive = !isMiscActive, isMiscActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Miscellaneous Options");
                    }
                } else {
                    if (isAgilityActive) {
                        createCenteredButton("Agility AIO", () -> isAgilityActive = !isAgilityActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("1-35 Agility Only`");
                        }
                    } else if (isDivinationActive) {
                        createCenteredButton("Divination AIO", () -> isDivinationActive = !isDivinationActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Divination AIO 1-99");
                        }
                        createCenteredButton("Offer Chronicles", () -> offerChronicles = !offerChronicles, offerChronicles);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Enable offering chronicles at the rift, will hand in at 15");
                        }
                        createCenteredButton("Use Divine-o-matic", () -> useDivineoMatic = !useDivineoMatic, useDivineoMatic);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Enable withdrawing and depositing charges into the Divine-o-matic, make sure to have empty charges in Backpack");
                        }
                        createCenteredButton("Use Familiar", () -> useFamiliarSummoning = !useFamiliarSummoning, useFamiliarSummoning);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will upkeep familiar, Have pouch and Restore Potions in Backpack, will bank at prif if out of pouches/restore pots");
                        }
                        createCenteredButton("Bank Pin", () -> useBankPin = !useBankPin, useBankPin);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will enter Bank Pin if it comes up, enter the pin and you can close settings");
                        }
                        createCenteredButton("Harvest Chronicles", () -> harvestChronicles = !harvestChronicles, harvestChronicles);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will harvest chronicles");
                        }
                        createCenteredButton("Only Chronicles", () -> handleOnlyChonicles = !handleOnlyChonicles, handleOnlyChonicles);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will harvest chronicles ONLY, w79, incandescent energy, start near rift");
                        }
                    } else if (isThievingActive) {
                        createCenteredButton("Thieving AIO", () -> isThievingActive = !isThievingActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Thieving - Read Tooltip");
                        }
                        createCenteredButton("Bank", () -> nearestBank = !nearestBank, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Bank to withdraw food");
                        }
                    } else if (isArcheologyActive) {
                        createCenteredButton("Archeology", () -> isArcheologyActive = !isArcheologyActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Archaeology with Material Caches Etc..");
                        }
                        createCenteredButton("Material Cache", () -> MaterialCache = !MaterialCache, MaterialCache);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will do Material Cache");
                        }
                        createCenteredButton("Material Manual", () -> materialManual = !materialManual, materialManual);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Upkeep Material Manual");
                        }
                        createCenteredButton("Archaeologists Tea", () -> archaeologistsTea = !archaeologistsTea, archaeologistsTea);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Upkeep Archaeologists Tea");
                        }
                        createCenteredButton("Hi-Spec Monocle", () -> hiSpecMonocle = !hiSpecMonocle, hiSpecMonocle);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Upkeep Hi-Spec Monocle");
                        }
                        createCenteredButton("Use Gote/Porter", () -> useGote = !useGote, useGote);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Upkeep Grace of the Elves");
                        }
                        createCenteredButton("Drop soil", () -> dropSoil = !dropSoil, dropSoil);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will drop soil instead of banking to deposit");
                        }
                       /* createCenteredButton("Bank when 0 porter", () -> bankwithoutPorter = !bankwithoutPorter, bankwithoutPorter);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will force to bank when backpack doesnt contain any porters");
                        }*/
                        /*createCenteredButton("Travel Via Coord", () -> useTraveltoLocation = !useTraveltoLocation, useTraveltoLocation);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will travel to location, set coords in settings");
                        }*/
                        createCenteredButton("Travel via Marker", () -> useHintArrow = !useHintArrow, useHintArrow);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Marker placed on the map, i recommend setting marker first then enabling this option");
                        }
                        createCenteredButton("hop worlds", () -> hopWorldsforArchaeology = !hopWorldsforArchaeology, hopWorldsforArchaeology);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will hop worlds when player is within 8 tiles of you");
                        }
                        createCenteredButton("Hop worlds for caches", () -> hopWorldsforCaches = !hopWorldsforCaches, hopWorldsforCaches);
                        createCenteredButton("Bank Pin", () -> useBankPin = !useBankPin, useBankPin);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will enter Bank Pin if it comes up, enter the pin and you can close settings");
                        }
                    } else if (isCombatActive) {
                        createCenteredButton("Combat", () -> isCombatActive = !isCombatActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("AIO Fighter`");
                        }
                        createCenteredButton("Bank", () -> nearestBank = !nearestBank, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Wars to Load Last Preset from Bank chest");
                        }
                        createCenteredButton("Bank Pin", () -> useBankPin = !useBankPin, useBankPin);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will enter Bank Pin if it comes up, enter the pin and you can close settings");
                        }
                        /*createCenteredButton("Banks for food", () -> BankforFood = !BankforFood, BankforFood);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("will go wars to bank and withdraw fish food and go back to combat");
                        }*/
                        createCenteredButton("Eat food?", () -> shouldEatFood = !shouldEatFood, shouldEatFood);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will eat food when health is low");
                        }
                        createCenteredButton("Loot", () -> useCustomLoot = !useCustomLoot, useCustomLoot);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Loot Items");
                        }
                        createCenteredButton("Loot All", () -> useLootEverything = !useLootEverything, useLootEverything);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Loot All Items");
                        }
                        createCenteredButton("Loot All Noted", () -> {
                            useLootAllNotedItems = !useLootAllNotedItems;
                            if (useLootAllNotedItems) {
                                useLootAllStackableItems = false;
                            }
                        }, useLootAllNotedItems);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Loot All Noted Items in Loot Inventory");
                        }
                        createCenteredButton("Loot Stackables", () -> {
                            useLootAllStackableItems = !useLootAllStackableItems;
                            if (useLootAllStackableItems) {
                                useLootAllNotedItems = false;
                            }
                        }, useLootAllStackableItems);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will loot all stackables");
                        }
                        /*createCenteredButton("Loot per cost", () -> lootBasedonCost = !lootBasedonCost, lootBasedonCost);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will loot based on cost you set");
                        }*/
                        createCenteredButton("Walk to Loot (BETA)", () -> walkToLoot = !walkToLoot, walkToLoot);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("[BETA] if item does not appear in loot inventory but is on the ground, it will walk to it until it appears in loot inventory, works for Loot, Loot Noted and Loot Stackables");
                        }
                        createCenteredButton("Use Notepaper", () -> useNotepaper = !useNotepaper, useNotepaper);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Magic notepaper or Enchant notepaper");
                        }
                        createCenteredButton("Use POD", () -> usePOD = !usePOD, usePOD);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Player owned dungeons, first room only");
                        }
                        createCenteredButton("Arch Glacor", () -> handleArchGlacor = !handleArchGlacor, handleArchGlacor);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("start anywhere, only toggle this and looting if you want to loot`");
                        }
                        createCenteredButton("Travel Via Coord", () -> useTraveltoLocation = !useTraveltoLocation, useTraveltoLocation);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will travel to location, set coords in settings");
                        }
                        createCenteredButton("Travel via Marker", () -> useHintArrow = !useHintArrow, useHintArrow);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Marker placed on the map, i recommend setting marker first then enabling this option");
                        }
                        createCenteredButton("Drop items?", () -> isDropActive = !isDropActive, isDropActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will drop certain items from backpack");
                        }
                        createCenteredButton("Lava wyrm", () -> lavaStrykewyrms = !lavaStrykewyrms, lavaStrykewyrms);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will kill Lava Strykewyrms, will even travel there automatically");
                        }
                        createCenteredButton("Ice wyrm", () -> iceStrykewyrms = !iceStrykewyrms, iceStrykewyrms);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will kill Ice Strykewyrms, will even travel there automatically");
                        }
                        /*createCenteredButton("Camel Warriors", () -> NPCs.camelWarriors = !NPCs.camelWarriors, NPCs.camelWarriors);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will kill Ice Strykewyrms, will even travel there automatically");
                        }*/
                        createCenteredButton("Slayer", () -> doSlayer = !doSlayer, doSlayer);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will do Ikeagirl Slayer Tasks, will cancel unknown tasks");
                        }
                    } else if (isFishingActive) {
                        createCenteredButton("Fishing", () -> isFishingActive = !isFishingActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Fishing at any spot using any option`");
                        }
                        createCenteredButton("Animation Check", () -> AnimationCheck = !AnimationCheck, AnimationCheck);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Enable when fishing Tuna/Lobsters/Swordfish/Sharks`");
                        }
                        createCenteredButton("Bank", () -> nearestBank = !nearestBank, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Nearest Bank");
                        }

                        createCenteredButton("Bank Pin", () -> useBankPin = !useBankPin, useBankPin);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will enter Bank Pin if it comes up, enter the pin and you can close settings");
                        }
                        /*createCenteredButton("Use Gote/Porter", () -> useGote = !useGote, useGote);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Gote/Porter");
                        }*/
                        /*createCenteredButton("Drop items?", () -> isDropActive = !isDropActive, isDropActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will drop certain items from backpack, not used to drop fish!");
                        }*/
                        /*createCenteredButton("Travel Via Coord", () -> useTraveltoLocation = !useTraveltoLocation, useTraveltoLocation);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will travel to location, set coords in settings");
                        }*/
                        createCenteredButton("Travel via Marker", () -> useHintArrow = !useHintArrow, useHintArrow);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Marker placed on the map, i recommend setting marker first then enabling this option");
                        }
                    } else if (isMiningActive) {
                        createCenteredButton("Mining", () -> isMiningActive = !isMiningActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Mining at any spot using any option`");
                        }
                        createCenteredButton("Bank", () -> nearestBank = !nearestBank, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Nearest Bank");
                        }
                        createCenteredButton("Nearby Banking", () -> useDepositBox = !useDepositBox, useDepositBox);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use nearby Deposit Box OR Bank chest if found, must have Bank enabled too!!");
                        }
                        createCenteredButton("Bank Pin", () -> useBankPin = !useBankPin, useBankPin);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will enter Bank Pin if it comes up, enter the pin and you can close settings");
                        }
                        /*createCenteredButton("Use Gote/Porter", () -> useGote = !useGote, useGote);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Gote/Porter");
                        }*/
                        /*createCenteredButton("Drop items?", () -> isDropActive = !isDropActive, isDropActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will drop certain items from backpack");
                        }*/
                        createCenteredButton("Travel Via Coord", () -> useTraveltoLocation = !useTraveltoLocation, useTraveltoLocation);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will travel to location, set coords in settings");
                        }
                        createCenteredButton("Travel via Marker", () -> useHintArrow = !useHintArrow, useHintArrow);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Marker placed on the map, i recommend setting marker first then enabling this option");
                        }
                    } else if (isWoodcuttingActive) {
                        createCenteredButton("Woodcutting", () -> isWoodcuttingActive = !isWoodcuttingActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Woodcutting at any spot using any option`");
                        }
                        createCenteredButton("Bank", () -> {
                            nearestBank = !nearestBank;
                            if (nearestBank) {
                                log("Nearest bank enabled");
                            } else {
                                log("Nearest bank disabled");
                            }
                        }, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Nearest Bank");
                        }
                        createCenteredButton("Bank Pin", () -> useBankPin = !useBankPin, useBankPin);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will enter Bank Pin if it comes up, enter the pin and you can close settings");
                        }
                        createCenteredButton("Acadia Tree", () -> acadiaTree = !acadiaTree, acadiaTree);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will cut Acadia Trees OUTSIDE of VIP area [Banking not supported]");
                        }
                        createCenteredButton("Acadia VIP", () -> acadiaVIP = !acadiaVIP, acadiaVIP);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will cut Acadia Trees INSIDE of VIP area [Banking not supported]");
                        }
                        createCenteredButton("Crystallise", () -> crystallise = !crystallise, crystallise);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Crystallise Acadia Trees, enable this and Acadia option");
                        }
                        createCenteredButton("Crystallise Mahogany", () -> crystalliseMahogany = !crystalliseMahogany, crystalliseMahogany);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Crystallise Mahogany Trees");
                        }
                        /*createCenteredButton("Use Gote/Porter", () -> useGote = !useGote, useGote);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Gote/Porter");
                        }*/
                        /*createCenteredButton("Drop items?", () -> isDropActive = !isDropActive, isDropActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will drop certain items from backpack");
                        }*/
                        /*createCenteredButton("Travel Via Coord", () -> useTraveltoLocation = !useTraveltoLocation, useTraveltoLocation);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will travel to location, set coords in settings");
                        }*/
                        createCenteredButton("Travel via Marker", () -> useHintArrow = !useHintArrow, useHintArrow);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Marker placed on the map, i recommend setting marker first then enabling this option");
                        }
                    } else if (isRunecraftingActive) {
                        createCenteredButton("Runecrafting", () -> isRunecraftingActive = !isRunecraftingActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Runecrafting made easy doing Necrotic Runes`");
                        }
                        createCenteredButton("Bone Altar", () -> HandleBoneAltar = !HandleBoneAltar, HandleBoneAltar);
                        createCenteredButton("Flesh Altar", () -> HandleFleshAltar = !HandleFleshAltar, HandleFleshAltar);
                        createCenteredButton("Miasma Altar", () -> HandleMiasmaAltar = !HandleMiasmaAltar, HandleMiasmaAltar);
                        createCenteredButton("Spirit Altar", () -> HandleSpiritAltar = !HandleSpiritAltar, HandleSpiritAltar);
                        createCenteredButton("Soul Altar", () -> useSoulAltar = !useSoulAltar, useSoulAltar);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will do Soul Altar with Protean Essence");
                        }
                        createCenteredButton("Steam Runes", () -> useSteamRunes = !useSteamRunes, useSteamRunes);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will do Steam Runes");
                        }
                        createCenteredButton("Abyss Crafting", () -> useAbyssRunecrafting = !useAbyssRunecrafting, useAbyssRunecrafting);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will craft runes Via the Abyss, HIGH REQUIREMENTS");
                        }
                        createCenteredButton("Astral Altar", () -> useAstralAltar = !useAstralAltar, useAstralAltar);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Astral Altar");
                        }
                        createCenteredButton("Bank Pin", () -> useBankPin = !useBankPin, useBankPin);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will enter Bank Pin if it comes up, enter the pin and you can close settings");
                        }

                    } else if (isMiscActive) {
                        createCenteredButton("Misc", () -> isMiscActive = !isMiscActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Miscellaneous Options");
                        }
                        createCenteredButton("Bank Pin", () -> useBankPin = !useBankPin, useBankPin);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will enter Bank Pin if it comes up, enter the pin and you can close settings");
                        }
                        createCenteredButton("Magic", () -> isDissasemblerActive = !isDissasemblerActive, isDissasemblerActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Disassemble/High Alchemy Items");
                        }

                        createCenteredButton("Violet Theme", () -> {
                            if (!PurpleThemeSelected) {
                                PurpleThemeSelected = true;
                                BlueThemeSelected = false;
                                RedThemeSelected = false;
                                OrangeThemeSelected = false;
                                YellowThemeSelected = false;
                                GreenThemeSelected = false;
                            }
                        }, PurpleThemeSelected);

                        createCenteredButton("Blue Theme", () -> {
                            if (!BlueThemeSelected) {
                                BlueThemeSelected = true;
                                PurpleThemeSelected = false;
                                RedThemeSelected = false;
                                OrangeThemeSelected = false;
                                YellowThemeSelected = false;
                                GreenThemeSelected = false;
                            }
                        }, BlueThemeSelected);

                        createCenteredButton("Red Theme", () -> {
                            if (!RedThemeSelected) {
                                RedThemeSelected = true;
                                PurpleThemeSelected = false;
                                BlueThemeSelected = false;
                                OrangeThemeSelected = false;
                                YellowThemeSelected = false;
                                GreenThemeSelected = false;
                            }
                        }, RedThemeSelected);

                        createCenteredButton("Orange Theme", () -> {
                            if (!OrangeThemeSelected) {
                                OrangeThemeSelected = true;
                                PurpleThemeSelected = false;
                                BlueThemeSelected = false;
                                RedThemeSelected = false;
                                YellowThemeSelected = false;
                                GreenThemeSelected = false;
                            }
                        }, OrangeThemeSelected);

                        createCenteredButton("Yellow Theme", () -> {
                            if (!YellowThemeSelected) {
                                YellowThemeSelected = true;
                                PurpleThemeSelected = false;
                                BlueThemeSelected = false;
                                RedThemeSelected = false;
                                OrangeThemeSelected = false;
                                GreenThemeSelected = false;
                            }
                        }, YellowThemeSelected);
                        createCenteredButton("Green Theme", () -> {
                            if (!GreenThemeSelected) {
                                GreenThemeSelected = true;
                                PurpleThemeSelected = false;
                                BlueThemeSelected = false;
                                RedThemeSelected = false;
                                OrangeThemeSelected = false;
                                YellowThemeSelected = false;
                            }
                        }, GreenThemeSelected);
                    } else if (isHerbloreActive) {
                        createCenteredButton("Herblore", () -> isHerbloreActive = !isHerbloreActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Use at `Bank chest` with a Portable Well nearby");
                        }
                        /*createCenteredButton("Make Bombs", () -> makeBombs = !makeBombs, makeBombs);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will make Bombs");
                        }*/
                    } else {
                        createCenteredButton("Cooking", () -> isCookingActive = !isCookingActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Cooking AIO with Fish, Recommended at Fort");
                        }
                        createCenteredButton("Make Wines", () -> makeWines = !makeWines, makeWines);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will make Wines");
                        }
                    }
                }
                ImGui.EndChild();
                ImGui.NextColumn();
                if (ImGui.BeginChild("Column2", 400, windowHeight, true, 0)) {
                    if (showLogs) {
                        if (ImGui.Begin("Logs", ImGuiWindowFlag.NoDecoration.getValue())) {
                            ImGui.SetWindowSize((float) 610, (float) 225);
                            if (ImGui.Button("Scroll to Bottom")) {
                                scrollToBottom = !scrollToBottom;
                            }
                            ImGui.SameLine();
                            ImGui.SeparatorText("Console Logs");

                            ImGui.SetCursorPosX(13);
                            if (ImGui.BeginChild("LogRegion", 0, 0, false, 0)) {
                                List<String> logMessages = CustomLogger.getLogMessages();
                                for (String message : logMessages) {
                                    if (message.contains("[Error]")) {
                                        String[] parts = message.split(" ", 2);
                                        ImGui.PushStyleColor(ImGuiCol.Text, 1.0f, 0.0f, 0.0f, 1.0f);
                                        ImGui.Text(parts[0]);
                                        ImGui.SameLine();
                                        ImGui.Text(parts[1]);
                                        ImGui.PopStyleColor(1);
                                    } else if (message.contains("[Loot]") || message.contains("[NotedItems]") || message.contains("[NotedItemsFromGround]") || message.contains("[CustomLooting]") || message.contains("[LootEverything]") || message.contains("[LootAll]") || message.contains("CustomLootingFromGround") || message.contains("StackedItem")) {
                                        String[] parts = message.split(" ", 2);
                                        setStyleColor(ImGuiCol.Text, 70, 130, 180, 255);
                                        ImGui.Text(parts[0]);
                                        ImGui.SameLine();
                                        ImGui.Text(parts[1]);
                                        ImGui.PopStyleColor(1);
                                    } else if (message.contains("[Success]")) {
                                        String[] parts = message.split(" ", 2);
                                        setStyleColor(ImGuiCol.Text, 0, 255, 0, 255);
                                        ImGui.Text(parts[0]);
                                        ImGui.SameLine();
                                        ImGui.Text(parts[1]);
                                        ImGui.PopStyleColor(1);
                                    } else if (message.contains("[Caution]")) {
                                        String[] parts = message.split(" ", 2);
                                        setStyleColor(ImGuiCol.Text, 242, 140, 40, 255);
                                        ImGui.Text(parts[0]);
                                        ImGui.SameLine();
                                        ImGui.Text(parts[1]);
                                        ImGui.PopStyleColor(1);
                                    } else {
                                        ImGui.Text(message);
                                    }
                                }
                                if (scrollToBottom) {
                                    ImGui.SetScrollHereY(1.0f);
                                }
                                ImGui.EndChild();
                            }
                        }
                        ImGui.End();
                    }
                    if (!anySelected) {
                        String[] snowTexts = {
                                " SSSS         N   N       OOO       W      W",
                                "S             NN  N      O   O      W      W",
                                " SSS          N N N      O   O      W      W",
                                "    S         N  NN      O   O      W  W  W",
                                "    S         N   N      O   O      W  W  W",
                                "S   S         N   N      O   O      W  W  W",
                                " SSS          N   N       OOO        W W W "
                        };

                        for (String text : snowTexts) {
                            float windowWidth = 400;
                            float textWidth = ImGui.CalcTextSize(text).getX();
                            float centeredX = (windowWidth - textWidth) / 2;
                            ImGui.SetCursorPosX(centeredX);
                            ImGui.Text(text);
                            ImGui.Spacing(5, 5);
                        }
                        ImGui.SetCursorPosY(220);

                        String[] newLines = {
                                "Welcome to the AIO Script",
                                "Please select a skill to start",
                                "if you like the script please",
                                "consider leaving a review",
                                "if you have any bugs or issues",
                                "please report them on the forum",
                        };

                        for (String line : newLines) {
                            float windowWidth = 400;
                            float textWidth = ImGui.CalcTextSize(line).getX();
                            float centeredX = (windowWidth - textWidth) / 2;
                            ImGui.SetCursorPosX(centeredX);
                            ImGui.Text(line);
                        }

                        String discordLink = "https://discordapp.com/channels/973830420858810378/1192140405689548891";
                        float buttonWidth1 = ImGui.CalcTextSize("Discord Support").getX();
                        float centeredButtonX1 = (362 - buttonWidth1) / 3;
                        ImGui.SetCursorPosX(centeredButtonX1);
                        if (ImGui.Button("Discord Support")) {
                            try {
                                Desktop.getDesktop().browse(new URI(discordLink));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Click here to join the Discord server for support");
                        }

                        ImGui.SameLine();

                        String reviewLink = "https://discord.com/channels/973830420858810378/1116465231141544038";
                        float centeredButtonX2 = centeredButtonX1 + buttonWidth1 + 20;
                        ImGui.SetCursorPosX(centeredButtonX2);
                        if (ImGui.Button("Write a Review")) {
                            try {
                                Desktop.getDesktop().browse(new URI(reviewLink));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Click here to write a review on Discord");
                        }
                    }

                    renderThieving();
                    renderHerblore();
                    renderMisc();
                    renderDivination();
                    renderRunecrafting();
                    renderArchaeology();
                    renderCombat();
                    renderMining();
                    renderFishing();
                    renderCooking();
                    renderWoodcutting();
                }

                ImGui.EndChild();
                ImGui.Columns(1, "Column", false);

                renderBottom();
                ImGui.PopStyleColor(49);
                ImGui.PopStyleVar(49);

                ImGui.End();
            }
        }
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }

    private Color getThemeColor() {
        if (PurpleThemeSelected) {
            return new Color(128, 0, 128); // RGB values for purple
        } else if (BlueThemeSelected) {
            return new Color(180, 130, 70, 255); // RGB values for blue
        } else if (RedThemeSelected) {
            return new Color(0, 0, 255); // RGB values for red
        } else if (YellowThemeSelected) {
            return new Color(0, 255, 255); // RGB values for yellow
        } else if (GreenThemeSelected) {
            return new Color(0, 128, 0); // RGB values for green
        } else if (OrangeThemeSelected) {
            return new Color(0, 165, 255); // RGB values for orange
        } else {
            return new Color(180, 130, 70, 255); // RGB values for blue
        }
    }

    private void drawTile(ScenePosition scene) {
        // Implementation of drawTile method
        ScenePosition LeftBack = new ScenePosition(scene.getX() - 250.f, scene.getY(), scene.getZ() - 250.f);
        ScenePosition RightBack = new ScenePosition(scene.getX() + 250.f, scene.getY(), scene.getZ() - 250.f);
        ScenePosition LeftFront = new ScenePosition(scene.getX() - 250.f, scene.getY(), scene.getZ() + 250.f);
        ScenePosition RightFront = new ScenePosition(scene.getX() + 250.f, scene.getY(), scene.getZ() + 250.f);
        Vector2f LeftFront_s = Client.project(LeftFront);
        Vector2f RightFront_s = Client.project(RightFront);
        Vector2f LeftBack_s = Client.project(LeftBack);
        Vector2f RightBack_s = Client.project(RightBack);

        Color themeColor = getThemeColor();

        int colour = themeColor.getRGB();
        BGList.DrawLine(LeftBack_s.getX(), LeftBack_s.getY(), RightBack_s.getX(), RightBack_s.getY(), 3, colour);
        BGList.DrawLine(LeftBack_s.getX(), LeftBack_s.getY(), LeftFront_s.getX(), LeftFront_s.getY(), 3, colour);
        BGList.DrawLine(RightFront_s.getX(), RightFront_s.getY(), RightBack_s.getX(), RightBack_s.getY(), 3, colour);
        BGList.DrawLine(RightFront_s.getX(), RightFront_s.getY(), LeftFront_s.getX(), LeftFront_s.getY(), 3, colour);
    }



}

