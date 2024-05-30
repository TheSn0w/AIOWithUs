package ImGui;

import net.botwithus.*;
import net.botwithus.Misc.CaveNightshade;
import net.botwithus.Misc.Dissasembler;
import net.botwithus.Misc.PorterMaker;
import net.botwithus.Variables.Variables;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.imgui.Vector2f;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.Combat;
import net.botwithus.Archeology;
import net.botwithus.SnowsScript;
import net.botwithus.Mining;
import net.botwithus.Woodcutting;
import net.botwithus.Fishing;
import net.botwithus.Divination;
import net.botwithus.Misc.Summoning;


import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static net.botwithus.Combat.enableRadiusTracking;
import static net.botwithus.Combat.radius;
import static net.botwithus.Misc.CaveNightshade.NightshadePicked;
import static net.botwithus.Misc.Dissasembler.*;
import static net.botwithus.Runecrafting.*;
import static net.botwithus.SnowsScript.*;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Woodcutting.*;

public class SkeletonScriptGraphicsContext extends ScriptGraphicsContext {

    SnowsScript script;
    public Instant startTime;
    boolean ScriptisOn = false;
    private long totalElapsedTime = 0;
    private boolean tooltipsEnabled = false;
    public String saveSettingsFeedbackMessage = "";
    boolean autoScrollToBottom = false;


    public SkeletonScriptGraphicsContext(ScriptConsole scriptConsole, SnowsScript script) {
        super(scriptConsole);
        this.script = script;
        this.startTime = Instant.now();
        System.currentTimeMillis();
    }


    List<String> predefinedNames = List.of(
            "Venator remains",
            "Legionary remains",
            "Castra debris",
            "Lodge bar storage",
            "Lodge art storage",
            "Administratum debris",
            "Cultist footlocker",
            "Sacrificial altar",
            "Prodromoi remains",
            "Dis dungeon debris",
            "Praesidio remains",
            "Monoceros remains",
            "Amphitheatre debris",
            "Ceramics studio debris",
            "Carcerem debris",
            "Ministry remains",
            "Stadio debris",
            "Cathedral debris",
            "Marketplace debris",
            "Inquisitor remains",
            "Infernal art",
            "Gladiator remains",
            "Citizen remains",
            "Shakroth remains",
            "Dominion Games podium",
            "Ikovian memorial",
            "Dragonkin remains",
            "Oikos studio debris",
            "Kharid-et chapel debris",
            "Forum entrance",
            "Gladiatorial goblin remains",
            "Keshik ger",
            "Animal trophies",
            "Pontifex remains",
            "Crucible stands debris",
            "Tailory debris",
            "Goblin dorm debris",
            "Oikos fishing hut remnants",
            "Weapons research debris",
            "Orcus altar",
            "Standing stone debris",
            "Runic debris",
            "Dis overspill",
            "Big High War God shrine",
            "Orthen rubble",
            "Varanusaur remains",
            "Gravitron research debris",
            "Acropolis debris",
            "Armarium debris",
            "Yu'biusk animal pen",
            "Keshik tower debris",
            "Dragonkin reliquary",
            "Goblin trainee remains",
            "Byzroth remains",
            "Destroyed golem",
            "Dragonkin coffin",
            "Icyene weapon rack",
            "Culinarum debris",
            "Kyzaj champion's boudoir",
            "Autopsy table",
            "Experiment workbench",
            "Keshik weapon rack",
            "Hellfire forge",
            "Warforge scrap pile",
            "Stockpiled art",
            "Aughra remains",
            "Ancient magick munitions",
            "Moksha device",
            "Bibliotheke debris",
            "Chthonian trophies",
            "Warforge weapon rack",
            "Flight research debris",
            "Aetherium forge",
            "Xolo mine",
            "Praetorian remains",
            "Bandos's sanctum debris",
            "Tsutsaroth remains",
            "Optimatoi remains",
            "War table debris",
            "Howl's workshop debris",
            "Makeshift pie oven",
            "Xolo remains"
    );
    List<String> predefinedCacheNames = List.of(
            "Material cache (third Age iron)",
            "Material cache (zarosian insignia)",
            "Material cache (samite silk)",
            "Material cache (imperial steel)",
            "Material cache (white oak)",
            "Material cache (goldrune)",
            "Material cache (orthenglass)",
            "Material cache (vellum)",
            "Material cache (cadmium red)",
            "Material cache (ancient vis)",
            "Material cache (Tyrian purple)",
            "Material cache (leather scraps)",
            "Material cache (chaotic brimstone)",
            "Material cache (demonhide)",
            "Material cache (Eye of Dagon)",
            "Material cache (hellfire metal)",
            "Material cache (keramos)",
            "Material cache (white marble)",
            "Material cache (cobalt blue)",
            "Material cache (Everlight silvthril)",
            "Material cache (Star of Saradomin)",
            "Material cache (Blood of Orcus)",
            "Material cache (soapstone)",
            "Material cache (Stormguard steel)",
            "Material cache (Wings of War)",
            "Material cache (animal furs)",
            "Material cache (Armadylean yellow)",
            "Material cache (malachite green)",
            "Material cache (Mark of the Kyzaj)",
            "Material cache (vulcanised rubber)",
            "Material cache (warforged bronze)",
            "Material cache (fossilised bone)",
            "Material cache (Yu'biusk clay)",
            "Material cache (aetherium alloy)",
            "Material cache (compass rose)",
            "Material cache (felt)",
            "Material cache (quintessence)",
            "Material cache (dragon metal)",
            "Material cache (carbon black)"
    );
    List<String> MiningList = List.of(
            "Light animica rock",
            "Dark animica rock",
            "Banite rock",
            "Orichalcite rock",
            "Drakolith rock",
            "Necrite rock",
            "Phasmatite rock",
            "Luminite rock",
            "Runite rock",
            "Adamantite rock",
            "Mithril rock",
            "Iron rock",
            "Tin rock",
            "Copper rock",
            "Soft clay rock",
            "Crystal-flecked sandstone",
            "Prifddinas gem rock"
    );

    List<String> TreeList = List.of(
            "Tree",
            "Oak",
            "Willow",
            "Maple",
            "Magic",
            "Yew",
            "Elder",
            "Mahogany",
            "Teak"
    );
    List<String> CombatList = List.of(
            "Goblin",
            "Zombie",
            "Skeleton",
            "Giant Spider",
            "Hill Giant",
            "Abyssal Demon"

    );
    List<String> FoodList = List.of(
            "Shark",
            "Rocktail",
            "Salmon",
            "Trout",
            "Swordfish",
            "Lobster"

    );
    List<String> LootList = List.of(
            "Charms",
            "Coins"


    );

    List<String> spiritStone = List.of(
            "Spirit onyx (a)",
            "Spirit dragonstone (a)",
            "Spirit diamond (a)",
            "Spirit ruby (a)",
            "Spirit emerald (a)",
            "Spirit sapphire (a)"
    );

    List<String> pouchName = List.of(
            "Geyser titan pouch"
    );
    static Map<Integer, String> secondaryItemName = new HashMap<>();
    static {
        secondaryItemName.put(1444, "Water talisman");
    }

    public final NativeInteger spiritStone_current_idx = new NativeInteger(0);
    public final NativeInteger pouchName_current_idx = new NativeInteger(0);
    public final NativeInteger secondaryItem_current_idx = new NativeInteger(0);
    public static final String[] porterTypes = {"Sign of the porter I", "Sign of the porter II", "Sign of the porter III", "Sign of the porter IV", "Sign of the porter V", "Sign of the porter VI", "Sign of the porter VII"};
    public static final String[] quantities = {"ALL", "1", "5", "10"};
    public static final NativeInteger currentPorterType = new NativeInteger(0);
    public static final NativeInteger currentQuantity = new NativeInteger(0);









    @Override
    public void drawSettings() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.Button, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.ButtonHovered, 91, 102, 91, 250);
        setStyleColor(ImGuiCol.Text, 208, 217, 209, 255);
        setStyleColor(ImGuiCol.Separator, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.TitleBgActive, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.TitleBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.CheckMark, 35, 219, 60, 200);
        setStyleColor(ImGuiCol.ResizeGripHovered, 35, 219, 60, 200);
        setStyleColor(ImGuiCol.ResizeGripActive, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.ResizeGrip, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.SliderGrab, 39, 92, 46, 200);
        setStyleColor(ImGuiCol.SliderGrabActive, 39, 92, 46, 200);
        setStyleColor(ImGuiCol.SeparatorHovered, 35, 219, 60, 200);
        setStyleColor(ImGuiCol.Border, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.BorderShadow, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.ScrollbarGrab, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.ScrollbarGrabHovered, 35, 219, 60, 200);
        setStyleColor(ImGuiCol.ScrollbarGrabActive, 35, 219, 60, 200);
        setStyleColor(ImGuiCol.MenuBarBg, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.TabActive, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.Tab, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.TabHovered, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.TabUnfocused, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.TabUnfocusedActive, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.FrameBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.PopupBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.HeaderHovered, 91, 102, 91, 250);
        setStyleColor(ImGuiCol.HeaderActive, 32, 77, 30, 100);
        setStyleColor(ImGuiCol.Header, 32, 77, 30, 100);
        setStyleColor(ImGuiCol.FrameBgHovered, 91, 102, 91, 250);

        ImGui.PushStyleVar(ImGuiStyleVar.WindowRounding, (float) 6);
        ImGui.PushStyleVar(ImGuiStyleVar.ChildRounding, (float) 6);
        ImGui.PushStyleVar(ImGuiStyleVar.FrameRounding, (float) 3);
        ImGui.PushStyleVar(ImGuiStyleVar.GrabRounding, (float) 3);
        ImGui.PushStyleVar(ImGuiStyleVar.PopupRounding, (float) 3);
        ImGui.PushStyleVar(ImGuiStyleVar.ScrollbarSize, (float) 9);
        ImGui.PushStyleVar(ImGuiStyleVar.ChildBorderSize, (float) 2);
        ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, (float) 6, (float) 2);
        ImGui.PushStyleVar(ImGuiStyleVar.WindowPadding, (float) 15, (float) 15);
        ImGui.PushStyleVar(ImGuiStyleVar.WindowBorderSize, (float) 3);
        ImGui.PushStyleVar(ImGuiStyleVar.FrameBorderSize, (float) 2);


        float columnWidth = 180;
        float childWidth = columnWidth - 10;

        if (ImGui.Begin("AIO Settings", ImGuiWindowFlag.NoNav.getValue() | ImGuiWindowFlag.NoResize.getValue() | ImGuiWindowFlag.NoCollapse.getValue() | ImGuiWindowFlag.NoTitleBar.getValue())) {
            ImGui.SetWindowSize((float) 610, (float) 510);
            ImGui.Columns(2, "Column", false);
            ImGui.SetColumnWidth(0, columnWidth);
            float windowHeight = 415;


            if (ImGui.BeginChild("Column1", childWidth, windowHeight, true, 0)) {

                float buttonW1 = 145;
                float buttonW2 = 145;
                float windowW = 180;
                float centeredX1 = (windowW - buttonW1) / 2;
                float centeredX2 = (windowW - buttonW2) / 2;
                if (ScriptisOn) {
                    ImGui.SetCursorPosX(centeredX1);
                    setStyleColor(ImGuiCol.Button, 39, 92, 46, 255);
                    setStyleColor(ImGuiCol.Border, 29, 242, 57, 255);
                    setStyleColor(ImGuiCol.BorderShadow, 240, 132, 57, 255);
                    ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 32, 15);
                    if (ImGui.Button("Stop Script")) {
                        botState = BotState.IDLE;
                        totalElapsedTime += Duration.between(startTime, Instant.now()).getSeconds();
                        ScriptisOn = false;
                    }
                    ImGui.PopStyleVar(1);
                    ImGui.PopStyleColor(3);
                } else {
                    ImGui.SetCursorPosX(centeredX1);
                    setStyleColor(ImGuiCol.Button, 120, 11, 20, 255);
                    setStyleColor(ImGuiCol.Border, 120, 11, 20, 255);
                    setStyleColor(ImGuiCol.BorderShadow, 240, 132, 57, 255);
                    ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 32, 15);
                    if (ImGui.Button("Start Script")) {
                        botState = BotState.SKILLING;
                        startTime = Instant.now();
                        ScriptisOn = true;
                    }
                    ImGui.PopStyleVar(1);
                    ImGui.PopStyleColor(3);
                }

                ImGui.SetCursorPosX(centeredX2);
                ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 24, 5);
                setStyleColor(ImGuiCol.BorderShadow, 94, 255, 0, 255);
                if (ImGui.Button("Save Settings")) {
                    try {
                        script.saveConfiguration();
                        saveSettingsFeedbackMessage = "Settings saved successfully.";
                    } catch (Exception e) {
                        saveSettingsFeedbackMessage = "Failed to save settings: " + e.getMessage();
                    }
                }
                ImGui.PopStyleColor(1);
                ImGui.PopStyleVar(1);
                ImGui.Separator();

                boolean agilitySelected = agility;
                boolean divinationSelected = isDivinationActive;
                boolean thievingSelected = isThievingActive;
                boolean archeologySelected = isArcheologyActive;
                boolean combatSelected = isCombatActive;
                boolean fishingSelected = isFishingActive;
                boolean miningSelected = isMiningActive;
                boolean woodcuttingSelected = isWoodcuttingActive;
                boolean cookingselected = isCookingActive;
                boolean rcselected = isRunecraftingActive;
                boolean miscselected = isMiscActive;
                boolean herbloreselcted = isHerbloreActive;

// Check if any checkbox is selected
                boolean anySelected =
                        agilitySelected ||
                                divinationSelected ||
                                thievingSelected ||
                                archeologySelected ||
                                combatSelected ||
                                fishingSelected ||
                                miningSelected ||
                                cookingselected ||
                                rcselected ||
                                miscselected ||
                                herbloreselcted ||
                                woodcuttingSelected;


                if (!anySelected) {
                    Variables.agility = ImGui.Checkbox("Agility AIO", Variables.agility);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("1-35 Agility Only`");
                    }
                    Variables.isDivinationActive = ImGui.Checkbox("Divination AIO", Variables.isDivinationActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Divination AIO`");
                    }
                    Variables.isThievingActive = ImGui.Checkbox("Thieving AIO", Variables.isThievingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Thieving AIO`");
                    }
                    Variables.isArcheologyActive = ImGui.Checkbox("Archaeology", Variables.isArcheologyActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Archaeology with Material Caches Etc..`");
                    }
                    Variables.isFishingActive = ImGui.Checkbox("Fishing", Variables.isFishingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Fishing at any spot using any option`");
                    }
                    Variables.isMiningActive = ImGui.Checkbox("Mining", Variables.isMiningActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Mining at any spot using any option`");
                    }
                    Variables.isWoodcuttingActive = ImGui.Checkbox("Woodcutting", Variables.isWoodcuttingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Woodcutting at any spot using any option`");
                    }
                    isCookingActive = ImGui.Checkbox("Cooking", isCookingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Use at `Range/Portable Range AIO too!`");
                    }
                    Variables.isCombatActive = ImGui.Checkbox("Combat", Variables.isCombatActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("AIO Fighter`");
                    }
                    isRunecraftingActive = ImGui.Checkbox("Runecrafting", isRunecraftingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Does necrotic runes`");
                    }
                    isHerbloreActive = ImGui.Checkbox("Herblore", isHerbloreActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Use at `Bank chest` with a Portable Well nearby");
                    }
                    isMiscActive = ImGui.Checkbox("Misc", isMiscActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("random smaller stuff`");
                    }
                } else {
                    if (agilitySelected) {
                        agility = ImGui.Checkbox("Agility AIO", agility);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("1-35 Agility Only`");
                        }
                    } else if (divinationSelected) {
                        isDivinationActive = ImGui.Checkbox("Divination AIO", isDivinationActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Divination AIO`");
                        }
                        offerChronicles = ImGui.Checkbox("Offer Chronicles", offerChronicles);
                        useDivineoMatic = ImGui.Checkbox("Use Divine-o-matic", useDivineoMatic);
                        useFamiliarSummoning = ImGui.Checkbox("Use Familiar", useFamiliarSummoning);
                        harvestChronicles = ImGui.Checkbox("Harvest Chronicles", harvestChronicles);
                    } else if (thievingSelected) {
                        isThievingActive = ImGui.Checkbox("Thieving AIO", isThievingActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Thieving AIO`");
                        }
                    } else if (archeologySelected) {
                        isArcheologyActive = ImGui.Checkbox("Archeology", isArcheologyActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Archaeology with Material Caches Etc..`");
                        }
                        MaterialCache = ImGui.Checkbox("Material Cache", MaterialCache);
                        materialManual = ImGui.Checkbox("Material Manual", materialManual);
                        archaeologistsTea = ImGui.Checkbox("Archaeologists Tea", archaeologistsTea);
                        hiSpecMonocle = ImGui.Checkbox("Hi-Spec Monocle", hiSpecMonocle);
                        useGote = ImGui.Checkbox("Use Gote", useGote);
                    } else if (combatSelected) {
                        /*attackOsseous = ImGui.Checkbox("Attack Osseous", attackOsseous);*/
                        isCombatActive = ImGui.Checkbox("Combat", isCombatActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("AIO Fighter`");
                        }
                        /*BankforFood = ImGui.Checkbox("Bank for food", BankforFood);*/
                        nearestBank = ImGui.Checkbox("Use Nearest Bank", nearestBank);
                        useLoot = ImGui.Checkbox("Loot", useLoot);
                        interactWithLootAll = ImGui.Checkbox("Loot All", interactWithLootAll);
                        usePOD = ImGui.Checkbox("Use POD", usePOD);
                        handleArchGlacor = ImGui.Checkbox("Arch Glacor", handleArchGlacor);
                    } else if (fishingSelected) {
                        isFishingActive = ImGui.Checkbox("Fishing", isFishingActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Fishing at any spot using any option`");
                        }
                        AnimationCheck = ImGui.Checkbox("Animation Check", AnimationCheck);
                        nearestBank = ImGui.Checkbox("Use Nearest Bank", nearestBank);
                    } else if (miningSelected) {
                        isMiningActive = ImGui.Checkbox("Mining", isMiningActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Mining at any spot using any option`");
                        }
                        nearestBank = ImGui.Checkbox("Use Nearest Bank", nearestBank);
                    } else if (woodcuttingSelected) {
                        isWoodcuttingActive = ImGui.Checkbox("Woodcutting", isWoodcuttingActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Woodcutting at any spot using any option`");
                        }
                        nearestBank = ImGui.Checkbox("Use Nearest Bank", nearestBank);
                        acadiaTree = ImGui.Checkbox("Acadia Tree", acadiaTree);
                        acadiaVIP = ImGui.Checkbox("Acadia VIP", acadiaVIP);
                        crystallise = ImGui.Checkbox("Crystallise", crystallise);
                        crystalliseMahogany = ImGui.Checkbox("Crystallise Mahogany", crystalliseMahogany);
                    } else if (rcselected) {
                        isRunecraftingActive = ImGui.Checkbox("Runecrafting", isRunecraftingActive);
                        HandleBoneAltar = ImGui.Checkbox("Bone Altar", HandleBoneAltar);
                        HandleFleshAltar = ImGui.Checkbox("Flesh Altar", HandleFleshAltar);
                        HandleMiasmaAltar = ImGui.Checkbox("Miasma Altar", HandleMiasmaAltar);
                        HandleSpiritAltar = ImGui.Checkbox("Spirit Altar", HandleSpiritAltar);
                        soulAltar = ImGui.Checkbox("Soul Altar", soulAltar);
                    } else if (miscselected) {
                        isMiscActive = ImGui.Checkbox("Misc", isMiscActive);
                        isDissasemblerActive = ImGui.Checkbox("Disassembler", isDissasemblerActive);
                    } else if (herbloreselcted) {
                        isHerbloreActive = ImGui.Checkbox("Herblore", isHerbloreActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Use at `Bank chest` with a Portable Well nearby");
                        }
                        makeBombs = ImGui.Checkbox("Make Bombs", makeBombs);
                    } else {
                        isCookingActive = ImGui.Checkbox("Cooking", isCookingActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Use at `Range/Portable Range`");
                        }
                        makeWines = ImGui.Checkbox("Make Wines", makeWines);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Have Grapes and Jug of Water Saved as Preset`");
                        }
                    }
                }
                ImGui.EndChild();
                ImGui.NextColumn();
                if (ImGui.BeginChild("Column2", 400, windowHeight, true, 0)) {
                    if (autoScrollToBottom) {
                        ImGui.SetScrollHereY(1.0f);
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
                    }
                    if (isThievingActive) {
                        ImGui.SeparatorText("Thieving Options");
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "Currently does 1-5 @ Pompous Merchant",
                                    "Currently does 5-42 @ Bakery Stall",
                                    "Currently does 42-99 @ Crux Druid",
                                    "Crystal Mask Support + Lightform",
                                    "Will Bank to Load Last Preset for food"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                            for (String text : texts) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }
                    }
                    if (isHerbloreActive) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "Uses `Load Last Preset from` Bank chest",
                                    "Uses Portable Well",
                                    "You dont need a Portable Well if Making Bombs"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                            for (String text : texts) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }
                        ImGui.SeparatorText("Potions Made Count");
                        for (Map.Entry<String, Integer> entry : Potions.entrySet()) {
                            ImGui.Text(entry.getKey() + ": " + entry.getValue());
                        }

                        int totalPotionsMade = 0;
                        for (int count : Potions.values()) {
                            totalPotionsMade += count;
                        }

                        long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                        double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                        double potionsMadePerHour = totalPotionsMade / elapsedHours;
                        int potionsMadePerHourInt = (int) potionsMadePerHour;

                        ImGui.Text("Potions Made Per Hour: " + potionsMadePerHourInt);
                    }
                    if (isMiscActive && !isDissasemblerActive) {
                        float totalWidth = 360.0f;
                        float checkboxWidth = 100.0f;
                        float numItems = 3.0f;
                        float spacing = (totalWidth - (numItems * checkboxWidth)) / (numItems + 1);
                        ImGui.SeparatorText("Miscellaneous Options");

                        boolean NoneSelected = isportermakerActive || isPlanksActive || isCorruptedOreActive || isSummoningActive || isGemCutterActive || isdivinechargeActive || isSmeltingActive;

                        if (!NoneSelected || isportermakerActive) {
                            if (!NoneSelected) {
                                ImGui.SetCursorPosX(spacing);
                            } else {
                                ImGui.SetCursorPosX(spacing);
                            }
                            isportermakerActive = ImGui.Checkbox("Porter Maker", isportermakerActive);
                            if (!NoneSelected) {
                                ImGui.SameLine();
                            }
                        }

                        if (!NoneSelected || isPlanksActive) {
                            if (!NoneSelected) {
                                ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                            } else {
                                ImGui.SetCursorPosX(spacing);
                            }
                            isPlanksActive = ImGui.Checkbox("Planks", isPlanksActive);
                            if (!NoneSelected) {
                                ImGui.SameLine();
                            }
                        }

                        if (!NoneSelected || isCorruptedOreActive) {
                            if (!NoneSelected) {
                                ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                            } else {
                                ImGui.SetCursorPosX(spacing);
                            }
                            isCorruptedOreActive = ImGui.Checkbox("Corrupted Ore", isCorruptedOreActive);

                        }

                        if (!NoneSelected || isSummoningActive) {
                            if (!NoneSelected) {
                                ImGui.SetCursorPosX(spacing);
                            } else {
                                ImGui.SetCursorPosX(spacing);
                            }
                            isSummoningActive = ImGui.Checkbox("Summoning", isSummoningActive);
                            if (!NoneSelected) {
                                ImGui.SameLine();
                            }
                        }

                        if (!NoneSelected || isGemCutterActive) {
                            if (!NoneSelected) {
                                ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                            } else {
                                ImGui.SetCursorPosX(spacing);
                            }
                            isGemCutterActive = ImGui.Checkbox("Gem Cutter", isGemCutterActive);
                            if (!NoneSelected) {
                                ImGui.SameLine();
                            }
                        }

                        if (!NoneSelected || isdivinechargeActive) {
                            if (!NoneSelected) {
                                ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                            } else {
                                ImGui.SetCursorPosX(spacing);
                            }
                            isdivinechargeActive = ImGui.Checkbox("Divine Charge", isdivinechargeActive);
                        }

                        if (!NoneSelected || isSmeltingActive) {
                            if (!NoneSelected) {
                                ImGui.SetCursorPosX(spacing);
                            } else {
                                ImGui.SetCursorPosX(spacing);
                            }
                            isSmeltingActive = ImGui.Checkbox("Smelting", isSmeltingActive);
                            if (!NoneSelected) {
                                ImGui.SameLine();
                            }
                        }
                        if (!NoneSelected || pickCaveNightshade) {
                            if (!NoneSelected) {
                                ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                            } else {
                                ImGui.SetCursorPosX(spacing);
                            }
                            pickCaveNightshade = ImGui.Checkbox("Cave Nightshade", pickCaveNightshade);
                        }
                        if (pickCaveNightshade) {
                            ImGui.SeparatorText("Cave Nightshade Picked Count");
                            for (Map.Entry<String, Integer> entry : NightshadePicked.entrySet()) {
                                ImGui.Text(entry.getKey() + ": " + entry.getValue());
                            }

                            int totalNightshadePicked = 0;
                            for (int count : NightshadePicked.values()) {
                                totalNightshadePicked += count;
                            }

                            long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                            double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                            double nightshadePickedPerHour = totalNightshadePicked / elapsedHours;
                            int nightshadePickedPerHourInt = (int) nightshadePickedPerHour;

                            ImGui.Text("Cave Nightshade Picked Per Hour: " + nightshadePickedPerHourInt);
                        }

                        if (isSmeltingActive) {
                            ImGui.SeparatorText("Smelting Options");
                            if (tooltipsEnabled) {
                                String[] texts = {
                                        "Load Last Preset from Bank chest",
                                        "Set your item before starting",
                                        "will only work with gems and Enchanted gems"
                                };

                                ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                                for (String text : texts) {
                                    float windowWidth = 400;
                                    float textWidth = ImGui.CalcTextSize(text).getX();
                                    float centeredStartPos = (windowWidth - textWidth) / 2;

                                    ImGui.SetCursorPosX(centeredStartPos);
                                    ImGui.Text(text);
                                }

                                ImGui.PopStyleColor(1);
                            }
                        }



                        if (isSummoningActive) {
                            if (tooltipsEnabled) {
                                String[] texts = {
                                        "Will use buy sell method at taverly summoning shop",
                                        "Taverly currently only supports Geyser titan pouch",
                                        "If using spirit stone will use it to teleport to bank when out",
                                        "Will only load last preset for spirit stones",
                                        "Ff using prifddinas will load last preset.",
                                        "Prif uses crystal teleport crystal so make sure its in preset",
                                };

                                ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                                for (String text : texts) {
                                    float windowWidth = 400;
                                    float textWidth = ImGui.CalcTextSize(text).getX();
                                    float centeredStartPos = (windowWidth - textWidth) / 2;

                                    ImGui.SetCursorPosX(centeredStartPos);
                                    ImGui.Text(text);
                                }

                                ImGui.PopStyleColor(1);
                            }
                            ImGui.SeparatorText("Summoning Options");
                            ImGui.SetCursorPosX(spacing);
                            useSpiritStone = ImGui.Checkbox("Spirit Stone", useSpiritStone);
                            ImGui.SameLine();
                            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                            usePrifddinas = ImGui.Checkbox("Prifddinas", usePrifddinas);


                            if (useSpiritStone) {
                                ImGui.SetItemWidth(150.0F);
                                if (ImGui.Combo("Spirit Stones", spiritStone_current_idx, spiritStone.toArray(new String[0]))) {
                                    int selectedIndex = spiritStone_current_idx.get();
                                    if (selectedIndex >= 0 && selectedIndex < spiritStone.size()) {
                                        String selectedName = spiritStone.get(selectedIndex);
                                        Summoning.setSpiritStoneName(selectedName);
                                        ScriptConsole.println("Spirit Stone selected: " + selectedName);
                                    } else {
                                        ScriptConsole.println("Please select a valid Spirit Stone.");
                                    }
                                }
                            }
                            ImGui.SetItemWidth(150.0F);
                            if (ImGui.Combo("Pouch Names", pouchName_current_idx, pouchName.toArray(new String[0]))) {
                                int selectedIndex = pouchName_current_idx.get();
                                if (selectedIndex >= 0 && selectedIndex < pouchName.size()) {
                                    String selectedName = pouchName.get(selectedIndex);
                                    Summoning.setPouchName(selectedName);
                                    ScriptConsole.println("Pouch Name selected: " + selectedName);
                                } else {
                                    ScriptConsole.println("Please select a valid Pouch Name.");
                                }
                            }
                            ImGui.SetItemWidth(150.0F);
                            if (ImGui.Combo("Secondary Items", secondaryItem_current_idx, secondaryItemName.values().toArray(new String[0]))) {
                                int selectedIndex = secondaryItem_current_idx.get();
                                if (selectedIndex >= 0 && selectedIndex < secondaryItemName.size()) {
                                    int selectedId = (int) secondaryItemName.keySet().toArray()[selectedIndex];
                                    String selectedItemName = secondaryItemName.get(selectedId);
                                    Summoning.setSecondaryItem(selectedId);
                                    ScriptConsole.println("Secondary Item selected: " + selectedItemName + " (" + selectedId + ")");
                                } else {
                                    ScriptConsole.println("Please select a valid Secondary Item.");
                                }
                            }
                        }
                        if (isportermakerActive) {
                            if (tooltipsEnabled) {
                                String[] texts = {
                                        "Will only work with Sign of the Porter VII",
                                        "Needs Incandescent Energy to make Porters",
                                        "Need Dragonstone Necklace to make Porters",
                                        "Will use `Bank chest` or `Banker`"
                                };

                                ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                                for (String text : texts) {
                                    float windowWidth = 400;
                                    float textWidth = ImGui.CalcTextSize(text).getX();
                                    float centeredStartPos = (windowWidth - textWidth) / 2;

                                    ImGui.SetCursorPosX(centeredStartPos);
                                    ImGui.Text(text);
                                }

                                ImGui.PopStyleColor(1);
                            }
                            ImGui.SeparatorText("Porters Made Count");
                            for (Map.Entry<String, Integer> entry : portersMade.entrySet()) {
                                ImGui.Text(entry.getKey() + ": " + entry.getValue());
                            }

                            int totalPortersMade = 0;
                            for (int count : portersMade.values()) {
                                totalPortersMade += count;
                            }

                            long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                            double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                            double portersMadePerHour = totalPortersMade / elapsedHours;
                            int portersMadePerHourInt = (int) portersMadePerHour;

                            ImGui.Text("Porters Made Per Hour: " + portersMadePerHourInt);
                        }
                        if (isPlanksActive) {
                            if (tooltipsEnabled) {
                                String[] texts = {
                                        "Have Preset Ready Saved",
                                        "Start at Fort Bank chest"
                                };

                                ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                                for (String text : texts) {
                                    float windowWidth = 400; // Set the window width
                                    float textWidth = ImGui.CalcTextSize(text).getX();
                                    float centeredStartPos = (windowWidth - textWidth) / 2;

                                    ImGui.SetCursorPosX(centeredStartPos);
                                    ImGui.Text(text);
                                }

                                ImGui.PopStyleColor(1);
                            }
                            ImGui.SeparatorText("Plank Options");
                            ImGui.SetCursorPosX(spacing);
                            makeFrames = ImGui.Checkbox("Frames", makeFrames);
                            ImGui.SameLine();
                            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                            makePlanks = ImGui.Checkbox("Planks", makePlanks);
                            ImGui.SameLine();
                            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                            makeRefinedPlanks = ImGui.Checkbox("Refined Planks", makeRefinedPlanks);
                        }
                        if (isCorruptedOreActive) {
                            if (tooltipsEnabled) {
                                String[] texts = {
                                        "Have Corrupted Ore in Backpack and be at Prif Furnace",
                                };

                                ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                                for (String text : texts) {
                                    float windowWidth = 400;
                                    float textWidth = ImGui.CalcTextSize(text).getX();
                                    float centeredStartPos = (windowWidth - textWidth) / 2;

                                    ImGui.SetCursorPosX(centeredStartPos);
                                    ImGui.Text(text);
                                }

                                ImGui.PopStyleColor(1);
                            }
                            ImGui.SeparatorText("Corrupted Ore Count");
                            for (Map.Entry<String, Integer> entry : corruptedOre.entrySet()) {
                                ImGui.Text(entry.getKey() + ": " + entry.getValue());
                            }

                            int totalCorruptedOre = 0;
                            for (int count : corruptedOre.values()) {
                                totalCorruptedOre += count;
                            }

                            long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                            double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                            double corruptedOrePerHour = totalCorruptedOre / elapsedHours;
                            int corruptedOrePerHourInt = (int) corruptedOrePerHour;

                            ImGui.Text("Corrupted Ore Per Hour: " + corruptedOrePerHourInt);
                        }
                    }
                    if (isMiscActive && isDissasemblerActive) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "have Either High Alch or Disassemble on action bar",
                                    "Have Enough Runes for High Alch",
                                    "Will log out once the count is finished"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                            for (String text : texts) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }
                        ImGui.SeparatorText("Dissasembler/High Alcher Options");
                        useDisassemble = ImGui.Checkbox("Disassemble", useDisassemble);
                        ImGui.SameLine();
                        useAlchamise = ImGui.Checkbox("High Alch", useAlchamise);
                        ImGui.Separator();
                        setItemName(ImGui.InputText("Item name", getItemName(), 100, ImGuiWindowFlag.None.getValue()));
                        itemMenuSize = ImGui.InputInt("Item amount: ", itemMenuSize, 1, 100, ImGuiWindowFlag.None.getValue());
                        if (ImGui.Button("Add to queue")) {
                            addTask(new TaskScheduler(itemMenuSize, getItemName()));
                        }
                        ImGui.Separator();
                        if (ImGui.BeginTable("Tasks", 3, ImGuiWindowFlag.None.getValue())) {
                            ImGui.TableNextRow();
                            ImGui.TableSetupColumn("Item name", 0);
                            ImGui.TableSetupColumn("Item amount", 1);
                            ImGui.TableSetupColumn("Delete task", 2);
                            ImGui.TableHeadersRow();
                            for (Iterator<TaskScheduler> iterator = tasks.iterator(); iterator.hasNext(); ) {
                                TaskScheduler task = iterator.next();
                                ImGui.TableNextRow();
                                ImGui.TableNextColumn();
                                ImGui.Text(task.itemToDisassemble);
                                ImGui.TableNextColumn();
                                ImGui.Text("x" + (task.amountToDisassemble - task.getAmountDisassembled()));
                                ImGui.TableNextColumn();
                                if (ImGui.Button("Remove") || task.isComplete()) {
                                    iterator.remove();
                                }
                            }
                            ImGui.EndTable();
                        }
                    }

                    if (isDivinationActive) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "1-99 AIO, will move from spot to spot automatically",
                                    "have required quests complete, it follows wiki guide",
                                    "if using familiar, have pouches/restore potions in preset",
                                    "will bank at prif if out of pouches/restore potions",
                                    "will use Load Last Preset from, make sure preset is set"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                            for (String text : texts) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }
                        ImGui.SeparatorText("Chronicles Captured Count");
                        for (Map.Entry<String, Integer> entry : chroniclesCaughtCount.entrySet()) {
                            ImGui.Text(entry.getKey() + ": " + entry.getValue());
                        }

                        int totalChroniclesCaptured = 0;
                        for (int count : chroniclesCaughtCount.values()) {
                            totalChroniclesCaptured += count;
                        }

                        long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                        double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                        double chroniclesCapturedPerHour = totalChroniclesCaptured / elapsedHours;
                        int chroniclesCapturedPerHourInt = (int) chroniclesCapturedPerHour;

                        ImGui.Text("Chronicles Captured Per Hour: " + chroniclesCapturedPerHourInt);

                        ImGui.SeparatorText("Energy Gathered Count");
                        for (Map.Entry<String, Integer> entry : energy.entrySet()) {
                            ImGui.Text(entry.getKey() + ": " + entry.getValue());
                        }

                        int totalEnergyGathered = 0;
                        for (int count : energy.values()) {
                            totalEnergyGathered += count;
                        }

                        double energyGatheredPerHour = totalEnergyGathered / elapsedHours;
                        int energyGatheredPerHourInt = (int) energyGatheredPerHour;

                        ImGui.Text("Energy Gathered Per Hour: " + energyGatheredPerHourInt);
                    }
                    if (isRunecraftingActive) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "Select your option and it will run",
                                    "Will log out if Backpack is not full after Banking",
                                    "have all stuff on action bar",
                                    "Have restore potions in preset if using familiar",
                                    "Soul altar will only work with protean essence",
                                    "if soul altar, start next to it",
                                    "you have to choose a ring choice",
                                    "meaning you need passing bracelet for this to work",
                                    "unless your doing soul altar"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                            for (String text : texts) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }
                        ImGui.SeparatorText("Runecrafting Options");
                        float totalWidth = 360.0f;
                        float checkboxWidth = 100.0f;
                        float numItems = 3.0f;
                        float spacing = (totalWidth - (numItems * checkboxWidth)) / (numItems + 1);

                        ImGui.SetCursorPosX(spacing);
                        ManageFamiliar = ImGui.Checkbox("Familiar", ManageFamiliar);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use level 93 Beast of Burden");
                        }
                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                        Powerburst = ImGui.Checkbox("Powerburst", Powerburst);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Powerburst of Sorcery, have on action bar");
                        }
                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                        notWearingRing = ImGui.Checkbox("Wearing Bracelet", notWearingRing);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("have passing bracelet in Backpack and select this and have on actionbar");
                        }

                        ImGui.SetCursorPosX(spacing);
                        WearingRing = ImGui.Checkbox("Wearing Ring", WearingRing);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("if you have equipped passing bracelet, select this");
                        }
                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                        RingofDueling = ImGui.Checkbox("Ring of Dueling", RingofDueling);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("if you have Ring of Dueling, select this,doesnt matter where if its in backpack or not");
                        }
                        ImGui.SetCursorPosX(spacing);

                        /*ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                        useGraceoftheElves = ImGui.Checkbox("Grace of the Elves", useGraceoftheElves);*/


                        ImGui.SeparatorText("Statistics");
                        displayLoopCountAndRunesPerHour(determineSelectedRuneType());
                    }


                    if (isArcheologyActive) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "Some areas are not supported by Traversal",
                                    "Have Arch Journal in Inventory",
                                    "will not destroy pylons/fragements",
                                    "will not hand in tomes",
                                    "it will not withdraw xp boosts from bank"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                            for (String text : texts) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }
                        ImGui.SeparatorText("Archaeology Options");

                        if (ImGui.Button("Enter Excavation Name")) {
                            Archeology.addName(Archeology.getName());
                            ScriptConsole.println("Excavation added: " + Archeology.getName());
                            Archeology.setName("");
                        }
                        ImGui.SameLine();
                        ImGui.SetItemWidth(200.0F);
                        Archeology.Rock1 = ImGui.InputText("##Excavation Name", Archeology.getName());

                        List<String> comboItemsList = new ArrayList<>(predefinedNames);
                        comboItemsList.add(0, "                          Select Excavation Name");
                        String[] comboItems = comboItemsList.toArray(new String[0]);

                        List<String> cacheItemsList = new ArrayList<>(predefinedCacheNames);
                        cacheItemsList.add(0, "                            Select Material Cache");
                        String[] Caches = cacheItemsList.toArray(new String[0]);

                        NativeInteger excavationItemIndex = new NativeInteger(0);
                        NativeInteger cacheItemIndex = new NativeInteger(0);

                        ImGui.SetItemWidth(362.0F);
                        setStyleColor(ImGuiCol.CheckMark, 32, 77, 30, 200);

                        if (ImGui.Combo("##excavationCombo", excavationItemIndex, comboItems)) {
                            int selectedIndex = excavationItemIndex.get();
                            if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                                String selectedName = comboItems[selectedIndex];
                                Archeology.addName(selectedName);
                                ScriptConsole.println("Predefined excavation added: " + selectedName);
                                excavationItemIndex.set(0);
                            } else {
                                ScriptConsole.println("Please select a valid excavation.");
                            }
                        }
                        ImGui.SetItemWidth(362.0F);

                        if (ImGui.Combo("##cacheCombo", cacheItemIndex, Caches)) {
                            int selectedIndex = cacheItemIndex.get();
                            if (selectedIndex > 0 && selectedIndex < Caches.length) {
                                String selectedName = Caches[selectedIndex];
                                Archeology.addName(selectedName);
                                MaterialCache = true;
                                ScriptConsole.println("Predefined material cache added: " + selectedName);
                                cacheItemIndex.set(0);
                            } else {
                                ScriptConsole.println("Please select a valid cache.");
                            }
                        }

                        if (ImGui.BeginChild("Selected Excavation", 362, 43, true, 0)) {
                            ImGui.SetCursorPosX(10.0f);
                            ImGui.SetCursorPosY(10.0f);

                            List<String> selectedNames = new ArrayList<>(Archeology.getSelectedNames());
                            float itemSpacing = 10.0f;
                            float lineSpacing = 10.0f;
                            float buttonHeight = 20.0f;
                            float windowWidth = 380.0f;

                            float cursorPosX = 10.0f;
                            float cursorPosY = 10.0f;

                            for (String name : selectedNames) {
                                Vector2f textSize = ImGui.CalcTextSize(name);
                                float buttonWidth = textSize.getX();

                                if (cursorPosX + buttonWidth > windowWidth) {
                                    cursorPosX = 10.0f;
                                    cursorPosY += buttonHeight + lineSpacing;
                                }

                                ImGui.SetCursorPosX(cursorPosX);
                                ImGui.SetCursorPosY(cursorPosY);

                                ImGui.PushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.5f);
                                ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 1.0f, 1.0f);

                                if (ImGui.Button(name)) {
                                    Archeology.removeName(name);
                                    MaterialCache = false;
                                    ScriptConsole.println("Excavation name removed: " + name);
                                }

                                ImGui.PopStyleVar(2);

                                cursorPosX += buttonWidth + itemSpacing;
                            }
                            ImGui.PopStyleColor(2);
                            ImGui.EndChild();
                        }
                        if (useGote) {
                            ImGui.SetItemWidth(200.0F);
                            if (ImGui.Combo("Type of Porter", currentPorterType, porterTypes)) {
                                int selectedIndex = currentPorterType.get();
                                if (selectedIndex >= 0 && selectedIndex < porterTypes.length) {
                                    String selectedPorterType = porterTypes[selectedIndex];
                                    ScriptConsole.println("Selected porter type: " + selectedPorterType);
                                } else {
                                    ScriptConsole.println("Please select a valid porter type.");
                                }
                            }
                            ImGui.SetItemWidth(200.0F);

                            if (ImGui.Combo("# of Porters to withdraw", currentQuantity, quantities)) {
                                int selectedIndex = currentQuantity.get();
                                if (selectedIndex >= 0 && selectedIndex < quantities.length) {
                                    String selectedQuantity = quantities[selectedIndex];
                                    ScriptConsole.println("Selected quantity: " + selectedQuantity);
                                } else {
                                    ScriptConsole.println("Please select a valid quantity.");
                                }
                            }
                        }

                        ImGui.SeparatorText("Materials Excavated Count");
                        for (Map.Entry<String, Integer> entry : Variables.materialsExcavated.entrySet()) {
                            ImGui.Text(entry.getKey() + ": " + entry.getValue());
                        }

                        int totalMaterialsExcavated = 0;
                        for (int count : Variables.materialsExcavated.values()) {
                            totalMaterialsExcavated += count;
                        }

                        long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                        double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                        double materialsExcavatedPerHour = totalMaterialsExcavated / elapsedHours;
                        int materialsExcavatedPerHourInt = (int) materialsExcavatedPerHour;

                        ImGui.Text("Materials Excavated Per Hour: " + materialsExcavatedPerHourInt);

                        ImGui.SeparatorText("Material Types Count");
                        for (Map.Entry<String, Integer> entry : materialTypes.entrySet()) {
                            ImGui.Text(entry.getKey() + ": " + entry.getValue());
                        }

                        int totalMaterialTypes = 0;
                        for (int count : materialTypes.values()) {
                            totalMaterialTypes += count;
                        }

                        double materialTypesPerHour = totalMaterialTypes / elapsedHours;
                        int materialTypesPerHourInt = (int) materialTypesPerHour;

                        ImGui.Text("Material Types Per Hour: " + materialTypesPerHourInt);
                    }


                    if (isCombatActive) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "use nearest bank - will use a predefined",
                                    "bank and load last preset",
                                    "loot - will use loot interface to loot items",
                                    "use loot all - will open loot inventory and loot everything",
                                    "use POD - will use POD to train combat",
                                    "Arch glacor, will use MAX GUILD and then",
                                    "go to arch glacor and farm",
                                    "if using arch glacor, only have 1st mechanic selected",
                                    "radius, will set a radius around the player and stay inside",
                                    "rest is self explanatory"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                            for (String text : texts) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }

                        ImGui.SeparatorText("Charms Obtained Count");
                        List<Map<String, Integer>> allCharms = Arrays.asList(BlueCharms, CrimsonCharms, GreenCharms, GoldCharms);

                        for (Map<String, Integer> charmMap : allCharms) {
                            for (Map.Entry<String, Integer> entry : charmMap.entrySet()) {
                                long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                                double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                                double charmsObtainedPerHour = entry.getValue() / elapsedHours;
                                int charmsObtainedPerHourInt = (int) charmsObtainedPerHour;

                                ImGui.Text(entry.getKey() + ": " + entry.getValue() + " - per hour: " + charmsObtainedPerHourInt);
                            }
                        }
                        ImGui.SeparatorText("Attack Options");
                        float totalWidth = 375.0f;
                        float checkboxWidth = 105.0f;
                        float numItems = 3.0f;
                        float spacing = (totalWidth - (numItems * checkboxWidth)) / (numItems + 1);

                        ImGui.SetCursorPosX(spacing);
                        ImGui.SetItemWidth(110.0F);
                        Combat.setHealthThreshold(ImGui.InputInt("      Health : Prayer  ", Combat.getHealthPointsThreshold()));
                        if (Combat.getHealthPointsThreshold() < 0) {
                            Combat.setHealthThreshold(0);
                        } else if (Combat.getHealthPointsThreshold() > 100) {
                            Combat.setHealthThreshold(100);
                        }

                        ImGui.SameLine();
                        ImGui.SetCursorPosX(spacing * 10 + checkboxWidth * 1);
                        ImGui.SameLine();
                        ImGui.SetItemWidth(110.0F);
                        if (Combat.getPrayerPointsThreshold() < 0) {
                            Combat.setPrayerPointsThreshold(0);
                        } else if (Combat.getPrayerPointsThreshold() > 9900) {
                            Combat.setPrayerPointsThreshold(9900);
                        }

                        int displayedThreshold = Combat.getPrayerPointsThreshold() / 10;

                        int inputThreshold = ImGui.InputInt("", displayedThreshold);

                        Combat.setPrayerPointsThreshold(inputThreshold * 10);


                        ImGui.SetCursorPosX(spacing);
                        usePrayerPots = ImGui.Checkbox("Prayer Pots", usePrayerPots);
                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                        useOverloads = ImGui.Checkbox("Overloads", useOverloads);
                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                        useAggroPots = ImGui.Checkbox("Aggro Pots", useAggroPots);

                        ImGui.SetCursorPosX(spacing);
                        useWeaponPoison = ImGui.Checkbox("Wep Poison", useWeaponPoison);
                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                        scriptureofJas = ImGui.Checkbox("Jas Book", scriptureofJas);
                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                        scriptureofWen = ImGui.Checkbox("Wen Book", scriptureofWen);

                        ImGui.SetCursorPosX(spacing);
                        DeathGrasp = ImGui.Checkbox("EOF", DeathGrasp);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Do not have Finger of Death in Revo bar.");
                        }
                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                        SpecialAttack = ImGui.Checkbox("OmniGuard", SpecialAttack);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Have on Action Bar");
                        }
                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                        VolleyofSouls = ImGui.Checkbox("Volley of Souls", VolleyofSouls);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Do not have Volley on Revo bar.");
                        }
                        ImGui.SetCursorPosX(spacing);
                        InvokeDeath = ImGui.Checkbox("Invoke Death", InvokeDeath);

                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                        SoulSplit = ImGui.Checkbox("Soul Split", SoulSplit);

                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                        KeepArmyup = ImGui.Checkbox("Army 24/7", KeepArmyup);

                        ImGui.SetCursorPosX(spacing);
                        animateDead = ImGui.Checkbox("Animate Dead", animateDead);

                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                        usequickPrayers = ImGui.Checkbox("Quick Prayers", usequickPrayers);

                        ImGui.SameLine();
                        ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                        useScrimshaws = ImGui.Checkbox("Scrimshaws", useScrimshaws);

                        ImGui.SetCursorPosX(spacing);
                        enableRadiusTracking = ImGui.Checkbox("Enable Radius", enableRadiusTracking);

                        if (VolleyofSouls) {
                            ImGui.SetCursorPosX(spacing);
                            ImGui.SetItemWidth(85.0F);
                            Combat.VolleyOfSoulsThreshold = ImGui.InputInt("       Volley Stacks", Combat.VolleyOfSoulsThreshold);
                            if (ImGui.IsItemHovered()) {
                                ImGui.SetTooltip("Stacks to cast at");
                            }
                            if (Combat.VolleyOfSoulsThreshold < 0) {
                                Combat.VolleyOfSoulsThreshold = 0;
                            } else if (Combat.VolleyOfSoulsThreshold > 5) {
                                Combat.VolleyOfSoulsThreshold = 5;
                            }
                        }
                        if (DeathGrasp) {
                            ImGui.SetItemWidth(85.0F);
                            Combat.NecrosisStacksThreshold = ImGui.InputInt("     Necrosis Stacks", Combat.NecrosisStacksThreshold);
                            if (ImGui.IsItemHovered()) {
                                ImGui.SetTooltip("Stacks to cast at");
                            }
                            if (Combat.NecrosisStacksThreshold < 0) {
                                Combat.NecrosisStacksThreshold = 0;
                            } else if (Combat.NecrosisStacksThreshold > 12) {
                                Combat.NecrosisStacksThreshold = 12;
                            }
                        }
                        if (enableRadiusTracking) {
                            ImGui.SetItemWidth(85.0F);
                            int newRadius = ImGui.InputInt("Radius (tiles)", radius);
                            if (newRadius < 0) {
                                newRadius = 0;
                            } else if (newRadius > 25) {
                                newRadius = 25;
                            }
                            if (newRadius != radius) {
                                radius = newRadius;
                                ScriptConsole.println("Radius distance changed to: " + radius);
                            }
                            ImGui.SameLine();
                            if (ImGui.Button("Set Center")) {
                                Combat.setCenterCoordinate(Client.getLocalPlayer().getCoordinate());
                            }
                        }
                        ImGui.SeparatorText("Target Options");
                        if (ImGui.Button("Add Target") && !targetName.isEmpty()) {
                            addTargetName(targetName);
                            addTarget(targetName);
                            targetName = "";
                        }
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Enter the name of the target to attack. Case-insensitive, partial names allowed.");
                        }
                        ImGui.SameLine();
                        ImGui.SetItemWidth(273.0F);
                        targetName = ImGui.InputText("##Targetname", targetName);

                        List<String> comboItemsList = new ArrayList<>(CombatList);
                        comboItemsList.add(0, "                          Select Enemy to Attack");
                        String[] comboItems = comboItemsList.toArray(new String[0]);

                        NativeInteger selectedItemIndex = new NativeInteger(0);

                        ImGui.SetItemWidth(360.0f);
                        if (ImGui.Combo("##EnemyType", selectedItemIndex, comboItems)) {
                            int selectedIndex = selectedItemIndex.get();

                            if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                                String selectedName = comboItems[selectedIndex];
                                addTargetName(selectedName);
                                ScriptConsole.println("Predefined Enemy added: " + selectedName);
                                selectedItemIndex.set(0);
                            } else {
                                ScriptConsole.println("Please select a valid enemy.");
                            }
                        }


                        if (ImGui.BeginChild("Targets List", 360, 50, true, 0)) {
                            int count = 0;
                            for (String targetName : new ArrayList<>(getTargetNames())) {
                                if (count > 0 && count % 5 == 0) {
                                    ImGui.Text("");
                                } else if (count > 0) {
                                    ImGui.SameLine();
                                }

                                if (ImGui.Button(targetName)) {
                                    removeTargetName(targetName);
                                    break;
                                }

                                if (ImGui.IsItemHovered()) {
                                    ImGui.SetTooltip("Click to remove this target");
                                }
                                count++;
                            }
                        }

                        ImGui.EndChild();
                    }
                    if (isCombatActive && useLoot) {
                        ImGui.SeparatorText("Loot Options");

                        if (ImGui.Button("Add Item") && !Combat.getSelectedItem().isEmpty()) {
                            Combat.getTargetItemNames().add(Combat.getSelectedItem());
                            Combat.setSelectedItem("");
                        }

                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Enter the name of the item to add to your list. Case-insensitive.");
                        }
                        ImGui.SameLine();
                        ImGui.SetItemWidth(284.0F);
                        Combat.setSelectedItem(ImGui.InputText("##Itemname", Combat.getSelectedItem()));

                        List<String> comboItemsList = new ArrayList<>(LootList);
                        comboItemsList.add(0, "                          Select Loot to Add");
                        String[] comboItems = comboItemsList.toArray(new String[0]);

                        NativeInteger selectedItemIndex = new NativeInteger(0);

                        ImGui.SetItemWidth(360.0F);
                        if (ImGui.Combo("##LootType", selectedItemIndex, comboItems)) {
                            int selectedIndex = selectedItemIndex.get();

                            if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                                String selectedName = comboItems[selectedIndex];
                                Combat.getTargetItemNames().add(selectedName);
                                ScriptConsole.println("Predefined Loot added: " + selectedName);
                                selectedItemIndex.set(0);
                            } else {
                                ScriptConsole.println("Please select a valid loot.");
                            }
                        }

                        if (ImGui.BeginChild("Item List", 360, 100, true, 0)) {
                            int count = 0;
                            for (String itemName : new ArrayList<>(Combat.getTargetItemNames())) {
                                if (count > 0 && count % 5 == 0) {
                                    ImGui.Text("");
                                } else if (count > 0) {
                                    ImGui.SameLine();
                                }

                                if (ImGui.Button(itemName)) {
                                    Combat.getTargetItemNames().remove(itemName);
                                    break;
                                }

                                if (ImGui.IsItemHovered()) {
                                    ImGui.SetTooltip("Click to remove this item");
                                }
                                count++;
                            }
                        }


                        ImGui.EndChild();
                    }
                    if (isCombatActive && BankforFood) {
                        ImGui.SeparatorText("Food Options");
                        if (ImGui.Button("Add Food") && !getFoodName().isEmpty()) {
                            addFoodName(getFoodName());
                            setFoodName("");
                        }

                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Enter the name of the food to add to your list. Case-insensitive.");
                        }
                        ImGui.SameLine();
                        ImGui.SetItemWidth(272.0F);
                        setFoodName(ImGui.InputText("##Foodname", getFoodName()));

                        List<String> comboItemsList = new ArrayList<>(FoodList);
                        comboItemsList.add(0, "                          Select Food to Add");
                        String[] comboItems = comboItemsList.toArray(new String[0]);

                        NativeInteger selectedItemIndex = new NativeInteger(0);

                        ImGui.SetItemWidth(360.0F);
                        if (ImGui.Combo("##FoodType", selectedItemIndex, comboItems)) {
                            int selectedIndex = selectedItemIndex.get();

                            if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                                String selectedName = comboItems[selectedIndex];
                                addFoodName(selectedName);
                                ScriptConsole.println("Predefined Food added: " + selectedName);
                                selectedItemIndex.set(0);
                            } else {
                                ScriptConsole.println("Please select a valid food.");
                            }
                        }

                        if (ImGui.BeginChild("Food List", 355, 50, true, 0)) {
                            int count = 0;
                            for (String foodName : new ArrayList<>(getSelectedFoodNames())) {
                                if (count > 0 && count % 5 == 0) {
                                    ImGui.Text("");
                                } else if (count > 0) {
                                    ImGui.SameLine();
                                }

                                if (ImGui.Button(foodName)) {
                                    removeFoodName(foodName);
                                    break;
                                }

                                if (ImGui.IsItemHovered()) {
                                    ImGui.SetTooltip("Click to remove this food");
                                }
                                count++;
                            }
                        }

                        ImGui.EndChild();
                    }

                    if (isMiningActive) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "start anywhere, will move to the closest rock",
                                    "will interact with Rockertunity",
                                    "have ores on action bar for faster dropping",
                                    "use nearest bank will use the closest bank when backpack full",
                                    "will empty ore box"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                            for (String text : texts) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }
                        ImGui.SeparatorText("Mining Options");
                        if (ImGui.Button("Add Rock Name")) {
                            addRockName(getRockName());
                            ScriptConsole.println("Rock name added: " + getRockName());
                            setRockName("");
                        }
                        ImGui.SameLine();
                        ImGui.SetItemWidth(245.0F);
                        Rock = ImGui.InputText("##Rock Name", getRockName());

                        List<String> comboItemsList = new ArrayList<>(MiningList);
                        comboItemsList.add(0, "                          Select Rock to Mine");
                        String[] comboItems = comboItemsList.toArray(new String[0]);

                        NativeInteger selectedItemIndex = new NativeInteger(0);
                        ImGui.SetItemWidth(365.0F);

                        if (ImGui.Combo("##RockType", selectedItemIndex, comboItems)) {
                            int selectedIndex = selectedItemIndex.get();

                            if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                                String selectedName = comboItems[selectedIndex];
                                addRockName(selectedName);
                                ScriptConsole.println("Predefined Rock added: " + selectedName);
                                selectedItemIndex.set(0);
                            } else {
                                ScriptConsole.println("Please select a valid rock.");
                            }
                        }

                        if (ImGui.BeginChild("Selected Rock Names", 365, 43, true, 0)) {
                            ImGui.SetCursorPosX(10.0f);
                            ImGui.SetCursorPosY(10.0f);

                            List<String> selectedRocks = new ArrayList<>(getSelectedRockNames());
                            float itemSpacing = 10.0f;
                            float lineSpacing = 10.0f;
                            float buttonHeight = 20.0f;
                            float windowWidth = 365.0f;

                            float cursorPosX = 10.0f;
                            float cursorPosY = 10.0f;

                            for (String rock : selectedRocks) {
                                Vector2f textSize = ImGui.CalcTextSize(rock);
                                float buttonWidth = textSize.getX();

                                if (cursorPosX + buttonWidth > windowWidth) {
                                    cursorPosX = 10.0f;
                                    cursorPosY += buttonHeight + lineSpacing;
                                }

                                ImGui.SetCursorPosX(cursorPosX);
                                ImGui.SetCursorPosY(cursorPosY);

                                ImGui.PushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.5f);
                                ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 1.0f, 1.0f);

                                if (ImGui.Button(rock)) {
                                    removeRockName(rock);
                                    ScriptConsole.println("Rock name removed: " + rock);
                                }

                                ImGui.PopStyleVar(2);
                                cursorPosX += buttonWidth + itemSpacing;
                            }
                            ImGui.EndChild();
                        }
                        ImGui.SeparatorText("Ores Mined Count");
                        for (Map.Entry<String, Integer> entry : Variables.types.entrySet()) {
                            String itemName = entry.getKey();
                            int itemCount = entry.getValue();
                            ImGui.Text(itemName + ": " + itemCount);
                        }
                    }

                    if (isFishingActive) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "start anywhere, will move to the closest spot",
                                    "have fish on action bar for faster dropping",
                                    "use nearest bank will use the closest bank when backpack full",
                                    "will only load last preset, so make sure you have items on preset"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                            for (String text : texts) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }
                        ImGui.SeparatorText("Fishing Options");
                        if (ImGui.Button("Add Fishing Location")) {
                            String locationInput = Fishing.getFishingLocation();
                            if (locationInput != null && !locationInput.trim().isEmpty()) {
                                Fishing.addFishingLocation(locationInput);
                                ScriptConsole.println("Fishing location added: " + locationInput);
                                Fishing.setFishingLocation("");
                            } else {
                                ScriptConsole.println("Invalid fishing location.");
                            }
                        }
                        ImGui.SameLine();
                        ImGui.SetItemWidth(214.0F);
                        String locationInput = ImGui.InputText("##Fishing Location", Fishing.getFishingLocation());
                        Fishing.setFishingLocation(locationInput);

                        if (ImGui.BeginChild("Selected Fishing Locations", 365, 43, true, 0)) {
                            ImGui.SetCursorPosX(10.0f);
                            ImGui.SetCursorPosY(10.0f);

                            List<String> selectedLocations = new ArrayList<>(Fishing.getSelectedFishingLocations());
                            float itemSpacing = 10.0f;
                            float lineSpacing = 10.0f;
                            float buttonHeight = 20.0f;
                            float windowWidth = 365.0f;

                            float cursorPosX = 10.0f;
                            float cursorPosY = 10.0f;

                            for (String location : selectedLocations) {
                                Vector2f textSize = ImGui.CalcTextSize(location);
                                float buttonWidth = textSize.getX();

                                if (cursorPosX + buttonWidth > windowWidth) {
                                    cursorPosX = 10.0f;
                                    cursorPosY += buttonHeight + lineSpacing;
                                }

                                ImGui.SetCursorPosX(cursorPosX);
                                ImGui.SetCursorPosY(cursorPosY);

                                ImGui.PushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.5f);
                                ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 1.0f, 1.0f);

                                if (ImGui.Button(location)) {
                                    Fishing.removeFishingLocation(location);
                                    ScriptConsole.println("Fishing location removed: " + location);
                                }

                                ImGui.PopStyleVar(2);

                                cursorPosX += buttonWidth + itemSpacing;
                            }
                            ImGui.EndChild();
                        }

                        if (ImGui.Button("Add Fishing Action")) {
                            String actionInput = Fishing.getFishingAction();
                            if (actionInput != null && !actionInput.trim().isEmpty()) {
                                Fishing.addFishingAction(actionInput);
                                ScriptConsole.println("Fishing action added: " + actionInput);
                                Fishing.setFishingAction("");
                            } else {
                                ScriptConsole.println("Invalid fishing action.");
                            }
                        }
                        ImGui.SameLine();

                        ImGui.SetItemWidth(227.0F);
                        ImGui.PushStyleVar(ImGuiStyleVar.FrameBorderSize, 2.0f);
                        String actionInput = ImGui.InputText("##Fishing Action", Fishing.getFishingAction());
                        ImGui.PopStyleVar(1);
                        Fishing.setFishingAction(actionInput);

                        /*ImGui.Text("Selected Fishing Actions:");*/
                        if (ImGui.BeginChild("Selected Fishing Actions", 365, 43, true, 0)) {
                            ImGui.SetCursorPosX(10.0f);
                            ImGui.SetCursorPosY(10.0f);

                            List<String> selectedActions = new ArrayList<>(Fishing.getSelectedFishingActions());
                            float itemSpacing = 10.0f;
                            float lineSpacing = 10.0f;
                            float buttonHeight = 20.0f;
                            float windowWidth = 365.0f;

                            float cursorPosX = 10.0f;
                            float cursorPosY = 10.0f;

                            for (String action : selectedActions) {
                                Vector2f textSize = ImGui.CalcTextSize(action);
                                float buttonWidth = textSize.getX();

                                if (cursorPosX + buttonWidth > windowWidth) {
                                    cursorPosX = 10.0f;
                                    cursorPosY += buttonHeight + lineSpacing;
                                }

                                ImGui.SetCursorPosX(cursorPosX);
                                ImGui.SetCursorPosY(cursorPosY);

                                ImGui.PushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.5f);
                                ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 1.0f, 1.0f);

                                if (ImGui.Button(action)) {
                                    Fishing.removeFishingAction(action);
                                    ScriptConsole.println("Fishing action removed: " + action);
                                }

                                ImGui.PopStyleVar(2);

                                cursorPosX += buttonWidth + itemSpacing;
                            }
                            ImGui.EndChild();
                        }
                        ImGui.SeparatorText("Fish Caught Count");
                        for (Map.Entry<String, Integer> entry : fishCaughtCount.entrySet()) {
                            ImGui.Text(entry.getKey() + ": " + entry.getValue());
                        }

                        int totalFishCaught = 0;
                        for (int count : fishCaughtCount.values()) {
                            totalFishCaught += count;
                        }

                        long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                        double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                        double fishCaughtPerHour = totalFishCaught / elapsedHours;
                        int fishCaughtPerHourInt = (int) fishCaughtPerHour;

                        ImGui.Text("Fish Caught Per Hour: " + fishCaughtPerHourInt);
                    }
                    if (isCookingActive) {
                        if (tooltipsEnabled) {
                            float windowWidth = 400;
                            String[] texts = {
                                    "Have Preset Ready",
                                    "if preset does not contain food, it will load preset 9",
                                    "if preset 9 will not have food",
                                    "it will manually withdraw highest tier fish",
                                    "and will save the preset",
                                    "Will use Bank chest or Bank booth to withdraw food",
                                    "Will use any type of Cooking potion to boost cooking level"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);
                            for (String text : texts) {
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }
                        ImGui.SeparatorText("Cooking Options");

                        int totalFishCooked = 0;
                        for (int count : fishCookedCount.values()) {
                            totalFishCooked += count;
                        }

                        long endTime = System.currentTimeMillis();
                        long startTime = Variables.startTime.toEpochMilli();
                        double hoursElapsed = (endTime - startTime) / 1000.0 / 60.0 / 60.0;
                        double averageFishPerHour = totalFishCooked / hoursElapsed;
                        ImGui.Text("Average fish cooked per hour: " + (int) averageFishPerHour);

                        for (Map.Entry<String, Integer> entry : fishCookedCount.entrySet()) {
                            ImGui.Text("Cooked: " + entry.getKey() + " - " + entry.getValue());
                        }
                    }
                    if (isWoodcuttingActive) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "start anywhere, will move to the closest tree",
                                    "will not work at woodcutters grove",
                                    "have logs on action bar for faster dropping",
                                    "use nearest bank will use the closest bank when backpack full",
                                    "woodbox not supported yet",
                                    "crystallise will only work on Acadia tree or acadia VIP",
                                    "have crystallise on actionbar, and lightform",
                                    "make sure you have required runes for crystallise",
                                    "will not pick up birds nests"
                            };

                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                            for (String text : texts) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(text).getX();
                                float centeredStartPos = (windowWidth - textWidth) / 2;

                                ImGui.SetCursorPosX(centeredStartPos);
                                ImGui.Text(text);
                            }

                            ImGui.PopStyleColor(1);
                        }
                        ImGui.SeparatorText("Woodcutting Options");
                        if (ImGui.Button("Add Tree Name")) {
                            addTreeName(getTreeName());
                            ScriptConsole.println("Tree added: " + getTreeName());
                            setTreeName("");
                        }
                        ImGui.SameLine();
                        ImGui.SetItemWidth(249.0F);
                        Tree = ImGui.InputText("##Tree Name", getTreeName());
                        List<String> comboItemsList = new ArrayList<>(TreeList);
                        comboItemsList.add(0, "                          Select Tree to Cut");
                        String[] comboItems = comboItemsList.toArray(new String[0]);

                        NativeInteger selectedItemIndex = new NativeInteger(0);
                        ImGui.SetItemWidth(365.0F);

                        if (ImGui.Combo("##TreeType", selectedItemIndex, comboItems)) {
                            int selectedIndex = selectedItemIndex.get();

                            if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                                String selectedName = comboItems[selectedIndex];
                                addTreeName(selectedName);
                                ScriptConsole.println("Predefined Tree added: " + selectedName);
                                selectedItemIndex.set(0);
                            } else {
                                ScriptConsole.println("Please select a valid tree.");
                            }
                        }

                        if (ImGui.BeginChild("Selected Trees", 365, 43, true, 0)) {
                            ImGui.SetCursorPosX(10.0f);
                            ImGui.SetCursorPosY(10.0f);

                            List<String> selectedTrees = new ArrayList<>(getSelectedTreeNames());
                            float itemSpacing = 10.0f;
                            float lineSpacing = 10.0f;
                            float buttonHeight = 20.0f;
                            float windowWidth = 365.0f;

                            float cursorPosX = 10.0f;
                            float cursorPosY = 10.0f;

                            for (String tree : selectedTrees) {
                                Vector2f textSize = ImGui.CalcTextSize(tree);
                                float buttonWidth = textSize.getX();

                                if (cursorPosX + buttonWidth > windowWidth) {
                                    cursorPosX = 10.0f;
                                    cursorPosY += buttonHeight + lineSpacing;
                                }

                                ImGui.SetCursorPosX(cursorPosX);
                                ImGui.SetCursorPosY(cursorPosY);

                                ImGui.PushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.5f);
                                ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 1.0f, 1.0f);

                                if (ImGui.Button(tree)) {
                                    removeTreeName(tree);
                                    ScriptConsole.println("Tree removed: " + tree);
                                }

                                ImGui.PopStyleVar(2);
                                cursorPosX += buttonWidth + itemSpacing;
                            }
                            ImGui.EndChild();
                        }
                        int totalLogsCut = 0;
                        for (int count : logCount.values()) {
                            totalLogsCut += count;
                        }

                        long endTime = System.currentTimeMillis();
                        long startTime = Variables.startTime.toEpochMilli();
                        double hoursElapsed = (endTime - startTime) / 1000.0 / 60.0 / 60.0;

                        double averageLogsPerHour = totalLogsCut / hoursElapsed;

                        ImGui.SeparatorText("Logs Chopped Count");

                        for (Map.Entry<String, Integer> entry : logCount.entrySet()) {
                            ImGui.Text(entry.getKey() + ": " + entry.getValue());
                        }
                        ImGui.Text("Average logs cut per hour: " + (int) averageLogsPerHour);
                    }
                }

                ImGui.EndChild();
                ImGui.Columns(1, "Column", false);

                if (ImGui.BeginChild("Child1", 580, 60, true, 0)) {

                    String botState;
                    if (isRunecraftingActive) {
                        botState = String.valueOf(Runecrafting.getScriptstate());
                    } else {
                        botState = String.valueOf(script.getBotState());
                    }

                    if (botState.equals("SKILLING")) {
                        setStyleColor(ImGuiCol.Text, 0, 255, 0, 255); // RGBA: Green
                    } else if (botState.equals("BANKING")) {
                        setStyleColor(ImGuiCol.Text, 255, 0, 0, 255); // RGBA: Red
                    } else {
                        setStyleColor(ImGuiCol.Text, 255, 255, 255, 255); // RGBA: White
                    }

                    if (Runecrafting.currentState == ScriptState.IDLE) {
                        setStyleColor(ImGuiCol.Text, 255, 165, 0, 255); // RGBA: Orange
                    } else if (Runecrafting.currentState == ScriptState.BANKING) {
                        setStyleColor(ImGuiCol.Text, 255, 0, 0, 255); // RGBA: Red
                    } else if (Runecrafting.currentState == ScriptState.TELEPORTING || Runecrafting.currentState == ScriptState.INTERACTINGWITHPORTAL) {
                        setStyleColor(ImGuiCol.Text, 128, 0, 128, 255); // RGBA: Purple
                    } else if (Runecrafting.currentState == ScriptState.CRAFTING) {
                        setStyleColor(ImGuiCol.Text, 0, 255, 0, 255); // RGBA: Green
                    } else if (Runecrafting.currentState == ScriptState.TELEPORTINGTOBANK) {
                        setStyleColor(ImGuiCol.Text, 128, 0, 128, 255); // RGBA: Purple
                    } else {
                        setStyleColor(ImGuiCol.Text, 255, 255, 255, 255);
                    }

                    ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 10, 5);
                    ImGui.PushStyleVar(ImGuiStyleVar.FrameRounding, 20);
                    ImGui.PushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                    ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 255, 255);

                    if (ImGui.Button("Enable Tooltips")) {
                        tooltipsEnabled = !tooltipsEnabled;
                    }
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Enable or disable tooltips in the Options tab");
                    }
                    ImGui.SameLine(); // This will place the next button on the same line
                    if (ImGui.Button("Scroll")) {
                        autoScrollToBottom = !autoScrollToBottom;
                    }
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Auto scroll to bottom");
                    }

                    ImGui.PopStyleVar(2);
                    ImGui.PopStyleColor(2);

                    float windowWidth = 577.0f;
                    float textWidth = ImGui.CalcTextSize("Script state: " + botState).getX();
                    float centeredX = (windowWidth - textWidth) / 2;

                    ImGui.SetCursorPosX(0);
                    ImGui.SetCursorPosY(10);
                    ImGui.SetCursorPosX(centeredX);
                    ImGui.Text("Script state: " + botState);
                    ImGui.PopStyleColor(1);

                    long elapsedTime = ScriptisOn ? Duration.between(startTime, Instant.now()).getSeconds() + totalElapsedTime : totalElapsedTime;
                    String elapsedTimeText = String.format("Runtime: %02d:%02d:%02d", elapsedTime / 3600, (elapsedTime % 3600) / 60, elapsedTime % 60);
                    textWidth = ImGui.CalcTextSize(elapsedTimeText).getX();
                    centeredX = (windowWidth - textWidth) / 2;

                    ImGui.SetCursorPosX(0);
                    ImGui.SetCursorPosY(25);
                    ImGui.SetCursorPosX(centeredX);
                    ImGui.Text(elapsedTimeText);

                    String versionText = "V1.0.1";
                    float versionTextWidth = ImGui.CalcTextSize(versionText).getX();
                    float rightAlignedX = windowWidth - versionTextWidth -23;

                    ImGui.SetCursorPosX(0);
                    ImGui.SetCursorPosY(22);
                    ImGui.SetCursorPosX(rightAlignedX);

                    setStyleColor(ImGuiCol.Text, 39, 92, 46, 255);
                    ImGui.Text(versionText);
                    ImGui.PopStyleColor(1);


                    ImGui.EndChild();
                }

                ImGui.End();
            }
        }
    }
    public static void setStyleColor(int colorEnum, int r, int g, int b, int a) {
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        a = Math.max(0, Math.min(255, a));

        float floatColorR = r / 255.0f;
        float floatColorG = g / 255.0f;
        float floatColorB = b / 255.0f;
        float floatColorA = a / 255.0f;

        ImGui.PushStyleColor(colorEnum, floatColorR, floatColorG, floatColorB, floatColorA);
    }
    private void displayLoopCountAndRunesPerHour(String selectedRuneType) {
        int loopCount = getLoopCounter();
        ImGui.Text("Number of Runs: " + loopCount);

        Duration elapsedTime = Duration.between(startTime, Instant.now());

        float runsPerHour = calculatePerHour(elapsedTime, loopCount);
        ImGui.Text(String.format("Runs Per Hour: %.2f", runsPerHour));

        if (!selectedRuneType.equals("None")) {
            Map<String, Integer> runeQuantities = getRuneQuantities();
            Integer quantity = runeQuantities.getOrDefault(selectedRuneType, 0);

            float runesPerHour = calculatePerHour(elapsedTime, quantity);
            ImGui.Text("Rune Type: " + selectedRuneType);
            ImGui.Text("Runes Crafted: " + quantity);
            ImGui.Text(String.format("Per Hour: %.2f", runesPerHour));
        }
    }

    private float calculatePerHour(Duration elapsed, int quantity) {
        long elapsedSeconds = elapsed.getSeconds();
        if (elapsedSeconds == 0) return 0;
        return (float) quantity / elapsedSeconds * 3600;
    }
    private String determineSelectedRuneType() {
        if (HandleSpiritAltar) return "Spirit Runes";
        if (HandleBoneAltar) return "Bone Runes";
        if (HandleMiasmaAltar) return "Miasma Runes";
        if (HandleFleshAltar) return "Flesh Runes";
        return "None";
    }



    private String formatNumberForDisplay(double number) {
        if (number < 1000.0) {
            return String.format("%.0f", number);
        } else if (number < 1000000.0) {
            return String.format("%.1fk", number / 1000.0);
        } else {
            return number < 1.0E9 ? String.format("%.1fM", number / 1000000.0) : String.format("%.1fB", number / 1.0E9);
        }
    }


    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }

}

