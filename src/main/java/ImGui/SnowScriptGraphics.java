package ImGui;

import net.botwithus.*;
import net.botwithus.Variables.Variables;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.imgui.*;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.Combat;
import net.botwithus.SnowsScript;
import net.botwithus.Misc.Summoning;


import java.awt.*;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;

import static ImGui.PredefinedStrings.*;
import static ImGui.Theme.*;
import static net.botwithus.Combat.enableRadiusTracking;
import static net.botwithus.Combat.radius;
import static net.botwithus.Misc.CaveNightshade.NightshadePicked;
import static net.botwithus.Runecrafting.*;
import static net.botwithus.SnowsScript.*;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Woodcutting.*;

public class SnowScriptGraphics extends ScriptGraphicsContext {

    SnowsScript script;
    public Instant startTime;
    boolean ScriptisOn = false;
    private long totalElapsedTime = 0;
    private boolean tooltipsEnabled = false;
    public String saveSettingsFeedbackMessage = "";
    boolean showLogs = false;
    public static boolean scrollToBottom = false;

    public SnowScriptGraphics(ScriptConsole scriptConsole, SnowsScript script) {
        super(scriptConsole);
        this.script = script;
        this.startTime = Instant.now();
        System.currentTimeMillis();
    }


    @Override
    public void drawSettings() {
        setDefaultTheme();
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

                buttonText = ScriptisOn ? "Stop Script" : "Start Script";
                textWidth1 = ImGui.CalcTextSize(buttonText).getX();
                padding = (childWidth - textWidth1) / 2;
                ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding, 15);
                ImGui.SetCursorPosX(0);
                if (ImGui.Button(buttonText)) {
                    if (ScriptisOn) {
                        botState = BotState.IDLE;
                        totalElapsedTime += Duration.between(startTime, Instant.now()).getSeconds();
                        ScriptisOn = false;
                    } else {
                        botState = BotState.SKILLING;
                        startTime = Instant.now();
                        ScriptisOn = true;
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
                    createCenteredButton("Agility", () -> Variables.agility = !Variables.agility, Variables.agility);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("1-35 Agility Only`");
                    }
                    createCenteredButton("Divination AIO", () -> isDivinationActive = !isDivinationActive, isDivinationActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Divination AIO 1-99");
                    }
                    createCenteredButton("Thieving", () -> Variables.isThievingActive = !Variables.isThievingActive, Variables.isThievingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Thieving - Read Tooltip");
                    }
                    createCenteredButton("Archaeology", () -> Variables.isArcheologyActive = !Variables.isArcheologyActive, Variables.isArcheologyActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Archaeology with Material Caches Etc..");
                    }
                    createCenteredButton("Fishing", () -> Variables.isFishingActive = !Variables.isFishingActive, Variables.isFishingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Fishing at any spot");
                    }
                    createCenteredButton("Mining", () -> Variables.isMiningActive = !Variables.isMiningActive, Variables.isMiningActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Mining at any spot");
                    }
                    createCenteredButton("Woodcutting", () -> Variables.isWoodcuttingActive = !Variables.isWoodcuttingActive, Variables.isWoodcuttingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Woodcutting at any spot Except Fort Forthry");
                    }
                    createCenteredButton("Cooking", () -> Variables.isCookingActive = !Variables.isCookingActive, Variables.isCookingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Cooking AIO with Fish, Recommended at Fort Forthry");
                    }
                    createCenteredButton("Combat", () -> Variables.isCombatActive = !Variables.isCombatActive, Variables.isCombatActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("AIO Fighter");
                    }
                    createCenteredButton("Runecrafting", () -> Variables.isRunecraftingActive = !Variables.isRunecraftingActive, Variables.isRunecraftingActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Runecrafting made easy doing Necrotic Runes");
                    }
                    createCenteredButton("Herblore", () -> Variables.isHerbloreActive = !Variables.isHerbloreActive, Variables.isHerbloreActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Use at `Bank chest` with a Portable Well nearby");
                    }
                    createCenteredButton("Misc", () -> Variables.isMiscActive = !Variables.isMiscActive, Variables.isMiscActive);
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Miscellaneous Options");
                    }
                } else {
                    if (agilitySelected) {
                        createCenteredButton("Agility AIO", () -> agility = !agility, agility);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("1-35 Agility Only`");
                        }
                    } else if (divinationSelected) {
                        createCenteredButton("Divination AIO", () -> isDivinationActive = !isDivinationActive, isDivinationActive);
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
                        createCenteredButton("Harvest Chronicles", () -> harvestChronicles = !harvestChronicles, harvestChronicles);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will harvest chronicles");
                        }
                    } else if (thievingSelected) {
                        createCenteredButton("Thieving AIO", () -> isThievingActive = !isThievingActive, isThievingActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Thieving - Read Tooltip");
                        }
                    } else if (archeologySelected) {
                        createCenteredButton("Archeology", () -> isArcheologyActive = !isArcheologyActive, isArcheologyActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Archaeology with Material Caches Etc..");
                        }
                        createCenteredButton("Material Cache", () -> MaterialCache = !MaterialCache, MaterialCache);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will do Material Cache");
                        }
                        createCenteredButton("Material Manual", () -> materialManual = !materialManual, materialManual);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will do Upkeep Material Manual");
                        }
                        createCenteredButton("Archaeologists Tea", () -> archaeologistsTea = !archaeologistsTea, archaeologistsTea);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Upkeep Archaeologists Tea");
                        }
                        createCenteredButton("Hi-Spec Monocle", () -> hiSpecMonocle = !hiSpecMonocle, hiSpecMonocle);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Upkeep Hi-Spec Monocle");
                        }
                        createCenteredButton("Use Gote", () -> useGote = !useGote, useGote);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Upkeep Grace of the Elves");
                        }
                    } else if (combatSelected) {
                        createCenteredButton("Combat", () -> isCombatActive = !isCombatActive, isCombatActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("AIO Fighter`");
                        }
                        createCenteredButton("Use Nearest Bank", () -> nearestBank = !nearestBank, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Nearest Bank");
                        }
                        createCenteredButton("Loot", () -> useLoot = !useLoot, useLoot);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Loot Items");
                        }
                        createCenteredButton("Loot All", () -> interactWithLootAll = !interactWithLootAll, interactWithLootAll);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Loot All Items");
                        }
                        createCenteredButton("Use POD", () -> usePOD = !usePOD, usePOD);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Player owned dungeons, first room only");
                        }
                        createCenteredButton("Arch Glacor", () -> handleArchGlacor = !handleArchGlacor, handleArchGlacor);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("start at portal and needs max guild teleport`");
                        }
                    } else if (fishingSelected) {
                        createCenteredButton("Fishing", () -> isFishingActive = !isFishingActive, isFishingActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Fishing at any spot using any option`");
                        }
                        createCenteredButton("Animation Check", () -> AnimationCheck = !AnimationCheck, AnimationCheck);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Enable when fishing tune/lobsters/swordfish/sharks`");
                        }
                        createCenteredButton("Use Nearest Bank", () -> nearestBank = !nearestBank, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Nearest Bank");
                        }
                    } else if (miningSelected) {
                        createCenteredButton("Mining", () -> isMiningActive = !isMiningActive, isMiningActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Mining at any spot using any option`");
                        }
                        createCenteredButton("Use Nearest Bank", () -> nearestBank = !nearestBank, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Nearest Bank");
                        }
                    } else if (woodcuttingSelected) {
                        createCenteredButton("Woodcutting", () -> isWoodcuttingActive = !isWoodcuttingActive, isWoodcuttingActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Woodcutting at any spot using any option`");
                        }
                        createCenteredButton("Use Nearest Bank", () -> nearestBank = !nearestBank, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Nearest Bank");
                        }
                        createCenteredButton("Acadia Tree", () -> acadiaTree = !acadiaTree, acadiaTree);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will cut Acadia Trees OUTSIDE of VIP area");
                        }
                        createCenteredButton("Acadia VIP", () -> acadiaVIP = !acadiaVIP, acadiaVIP);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will cut Acadia Trees INSIDE of VIP area");
                        }
                        createCenteredButton("Crystallise", () -> crystallise = !crystallise, crystallise);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Crystallise Acadia Trees, enable this and Acadia option");
                        }
                        createCenteredButton("Crystallise Mahogany", () -> crystalliseMahogany = !crystalliseMahogany, crystalliseMahogany);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will Crystallise Mahogany Trees");
                        }
                    } else if (rcselected) {
                        createCenteredButton("Runecrafting", () -> isRunecraftingActive = !isRunecraftingActive, isRunecraftingActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Runecrafting made easy doing Necrotic Runes`");
                        }
                        createCenteredButton("Bone Altar", () -> HandleBoneAltar = !HandleBoneAltar, HandleBoneAltar);
                        createCenteredButton("Flesh Altar", () -> HandleFleshAltar = !HandleFleshAltar, HandleFleshAltar);
                        createCenteredButton("Miasma Altar", () -> HandleMiasmaAltar = !HandleMiasmaAltar, HandleMiasmaAltar);
                        createCenteredButton("Spirit Altar", () -> HandleSpiritAltar = !HandleSpiritAltar, HandleSpiritAltar);
                        createCenteredButton("Soul Altar", () -> soulAltar = !soulAltar, soulAltar);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will do Soul Altar with Protean Essence");
                        }
                    } else if (miscselected) {
                        createCenteredButton("Misc", () -> isMiscActive = !isMiscActive, isMiscActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Miscellaneous Options");
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
                    } else if (herbloreselcted) {
                        createCenteredButton("Herblore", () -> isHerbloreActive = !isHerbloreActive, isHerbloreActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Use at `Bank chest` with a Portable Well nearby");
                        }
                        createCenteredButton("Make Bombs", () -> makeBombs = !makeBombs, makeBombs);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will make Bombs");
                        }
                    } else if (cookingselected){
                        createCenteredButton("Cooking", () -> isCookingActive = !isCookingActive, isCookingActive);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Cooking AIO with Fish, Recommended at Fort Forthry");
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
                        if (ImGui.Button("Scroll to Bottom")) {
                            scrollToBottom = !scrollToBottom;
                        }
                        ImGui.SameLine();
                        ImGui.SeparatorText("Console Logs");

                        ImGui.SetCursorPosX(13);
                        if (ImGui.BeginChild("LogRegion", 374, windowHeight - 57, true, 0)) {
                            List<String> logMessages = CustomLogger.getLogMessages();
                            for (String message : logMessages) {
                                if (message.contains("[Error]")) {
                                    String[] parts = message.split(" ", 2);
                                    ImGui.PushStyleColor(ImGuiCol.Text, 1.0f, 0.0f, 0.0f, 1.0f);
                                    ImGui.Text(parts[0]);
                                    ImGui.SameLine();
                                    ImGui.Text(parts[1]);
                                    ImGui.PopStyleColor();
                                } else {
                                    ImGui.Text(message);
                                }
                            }
                            if (scrollToBottom) {
                                ImGui.SetScrollHereY(1.0f);
                            }
                            ImGui.EndChild();  // End the child region
                        }
                    } else {
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
                                    "please note, these are just the barebones of some scripts",
                                    "that can be found on the marketplace",
                            };

                            for (String line : newLines) {
                                float windowWidth = 400;
                                float textWidth = ImGui.CalcTextSize(line).getX();
                                float centeredX = (windowWidth - textWidth) / 2;
                                ImGui.SetCursorPosX(centeredX);
                                ImGui.Text(line);
                            }

// Add the Discord support button
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
                            float buttonWidth2 = ImGui.CalcTextSize("Write a Review").getX();
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
                        if (isThievingActive && !showLogs) {
                            ImGui.SeparatorText("Thieving Options");
                            if (tooltipsEnabled) {
                                String[] texts = {
                                        "Currently does 1-5 @ Pompous Merchant",
                                        "Currently does 5-42 @ Bakery Stall",
                                        "Currently does 42-99 @ Crux Druid",
                                        "Crystal Mask Support + Lightform",
                                        "Will Bank to Load Last Preset for food",
                                        "if you like this script, consider looking at Pzoots Thiever",
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
                        if (isHerbloreActive && !showLogs) {
                            if (tooltipsEnabled) {
                                String[] texts = {
                                        "Uses `Load Last Preset from` Bank chest",
                                        "Uses Portable Well",
                                        "You dont need a Portable Well if Making Bombs",
                                        "if you like this script, consider looking at HerbloreWithUs",
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
                        if (isMiscActive && !isDissasemblerActive && !showLogs) {
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
                                            "will only work with gems and Enchanted gems",
                                            "if you like this script, consider looking at SmithWithUs",
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
                            if (isdivinechargeActive) {
                                ImGui.SeparatorText("Divine Charges Count");
                                for (Map.Entry<String, Integer> entry : divineCharges.entrySet()) {
                                    ImGui.Text(entry.getKey() + ": " + entry.getValue());
                                }

                                int totalDivineCharges = 0;
                                for (int count : divineCharges.values()) {
                                    totalDivineCharges += count;
                                }

                                long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                                double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                                double divineChargesPerHour = totalDivineCharges / elapsedHours;
                                int divineChargesPerHourInt = (int) divineChargesPerHour;

                                ImGui.Text("Divine Charges Per Hour: " + divineChargesPerHourInt);
                            }
                            if (isGemCutterActive) {
                                ImGui.SeparatorText("Gem Counts");
                                for (Map.Entry<String, Integer> entry : SnowsScript.Gems.entrySet()) {
                                    ImGui.Text(entry.getKey() + ": " + entry.getValue());
                                }

                                int totalGems = 0;
                                for (int count : SnowsScript.Gems.values()) {
                                    totalGems += count;
                                }

                                long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                                double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                                double gemsPerHour = totalGems / elapsedHours;
                                int gemsPerHourInt = (int) gemsPerHour;

                                ImGui.Text("Gems Per Hour: " + gemsPerHourInt);
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
                                makePlanks = ImGui.Checkbox("make planks", makePlanks);
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
                        if (isMiscActive && isDissasemblerActive && !showLogs) {
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

                        if (isDivinationActive && !showLogs) {
                            if (tooltipsEnabled) {
                                String[] texts = {
                                        "1-99 AIO, will move from spot to spot automatically",
                                        "have required quests complete, it follows wiki guide",
                                        "if using familiar, have pouches/restore potions in preset",
                                        "will bank at prif if out of pouches/restore potions",
                                        "will use Load Last Preset from, make sure preset is set",
                                        "if you like this script, consider looking at DivWithUs",
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
                        if (isRunecraftingActive && !showLogs) {
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
                                        "unless your doing soul altar",
                                        "if you like this script, consider looking at RCWithUs",
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
                    }


                    if (isArcheologyActive && !showLogs) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "Some areas are not supported by Traversal",
                                    "Have Arch Journal in Inventory",
                                    "will not destroy pylons/fragements",
                                    "will not hand in tomes",
                                    "it will not withdraw xp boosts from bank",
                                    "if you like this script, consider looking at ArchWithUs",
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
                            addName(getName());
                            ScriptConsole.println("Excavation added: " + getName());
                            setName("");
                        }
                        ImGui.SameLine();
                        ImGui.SetItemWidth(200.0F);
                        Rock1 = ImGui.InputText("##Excavation Name", getName());

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
                                addName(selectedName);
                                ScriptConsole.println("Predefined excavation added: " + selectedName);
                                excavationItemIndex.set(0);
                            } else {
                                ScriptConsole.println("Please select a valid excavation.");
                            }
                        }
                        ImGui.SetItemWidth(362.0F);
                        if (MaterialCache) {
                            if (ImGui.Combo("##cacheCombo", cacheItemIndex, Caches)) {
                                int selectedIndex = cacheItemIndex.get();
                                if (selectedIndex > 0 && selectedIndex < Caches.length) {
                                    String selectedName = Caches[selectedIndex];
                                    addName(selectedName);
                                    MaterialCache = true;
                                    ScriptConsole.println("Predefined material cache added: " + selectedName);
                                    cacheItemIndex.set(0);
                                } else {
                                    ScriptConsole.println("Please select a valid cache.");
                                }
                            }
                        }

                        if (ImGui.BeginTable("Selected Excavation", 2, ImGuiWindowFlag.None.getValue())) {
                            ImGui.TableNextRow();
                            ImGui.TableSetupColumn("Excavation Name", 0);
                            ImGui.TableSetupColumn("Remove", 1);
                            ImGui.TableHeadersRow();

                            List<String> selectedNames = new ArrayList<>(getSelectedNames());
                            for (String name : selectedNames) {
                                ImGui.TableNextRow();
                                ImGui.TableNextColumn();
                                ImGui.Text(name);
                                ImGui.TableNextColumn();
                                if (ImGui.Button("Remove##" + name)) {
                                    removeName(name);
                                    MaterialCache = false;
                                    ScriptConsole.println("Excavation name removed: " + name);
                                }
                            }
                            ImGui.EndTable();
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


                    if (isCombatActive && !showLogs) {
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
                                    "make sure to set your target arch archglacor",
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
                    if (isCombatActive && useLoot && !showLogs) {
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
                    if (isCombatActive && BankforFood && !showLogs) {
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

                    if (isMiningActive && !showLogs) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "start anywhere, will move to the closest rock",
                                    "will interact with Rockertunity",
                                    "have ores on action bar for faster dropping",
                                    "use nearest bank will use the closest bank when backpack full",
                                    "will empty ore box",
                                    "if you like this script, consider looking at MineWithUs",
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

                    if (isFishingActive && !showLogs) {
                        if (tooltipsEnabled) {
                            String[] texts = {
                                    "start anywhere, will move to the closest spot",
                                    "have fish on action bar for faster dropping",
                                    "use nearest bank will use the closest bank when backpack full",
                                    "will only load last preset, so make sure you have items on preset",
                                    "if you like this script, consider looking at FishWithUs",
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
                        if (ImGui.Button("Add Fishing Action")) {
                            String actionInput = getFishingAction();
                            if (actionInput != null && !actionInput.trim().isEmpty()) {
                                addFishingAction(actionInput);
                                ScriptConsole.println("Fishing action added: " + actionInput);
                                setFishingAction("");
                            } else {
                                ScriptConsole.println("Invalid fishing action.");
                            }
                        }
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("I.E Lure, or Net");
                        }
                        ImGui.SameLine();

                        ImGui.SetItemWidth(227.0F);
                        ImGui.PushStyleVar(ImGuiStyleVar.FrameBorderSize, 2.0f);
                        String actionInput = ImGui.InputText("##Fishing Action", getFishingAction());
                        ImGui.PopStyleVar(1);
                        setFishingAction(actionInput);
                        if (ImGui.Button("Add Fishing Location")) {
                            String locationInput = getFishingLocation();
                            if (locationInput != null && !locationInput.trim().isEmpty()) {
                                addFishingLocation(locationInput);
                                ScriptConsole.println("Fishing location added: " + locationInput);
                                setFishingLocation("");
                            } else {
                                ScriptConsole.println("Invalid fishing location.");
                            }
                        }
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("I.E Fishing spot, or Fishing ID");
                        }
                        ImGui.SameLine();
                        ImGui.SetItemWidth(214.0F);
                        String locationInput = ImGui.InputText("##Fishing Location", getFishingLocation());
                        setFishingLocation(locationInput);

                        if (ImGui.BeginTable("Selected Fishing", 3, ImGuiWindowFlag.None.getValue())) {
                            ImGui.TableNextRow();
                            ImGui.TableSetupColumn("Fishing Action", 0);
                            ImGui.TableSetupColumn("Fishing Location", 1);
                            ImGui.TableSetupColumn("Action", 2);
                            ImGui.TableHeadersRow();

                            List<String> selectedActions = new ArrayList<>(getSelectedFishingActions());
                            List<String> selectedLocations = new ArrayList<>(getSelectedFishingLocations());

                            for (int i = 0; i < Math.max(selectedActions.size(), selectedLocations.size()); i++) {
                                ImGui.TableNextRow();
                                ImGui.TableNextColumn();
                                ImGui.Text(i < selectedActions.size() ? selectedActions.get(i) : "");
                                ImGui.TableNextColumn();
                                ImGui.Text(i < selectedLocations.size() ? selectedLocations.get(i) : "");
                                ImGui.TableNextColumn();
                                if (i < selectedActions.size() && ImGui.Button("Remove Action##" + selectedActions.get(i))) {
                                    removeFishingAction(selectedActions.get(i));
                                    ScriptConsole.println("Fishing action removed: " + selectedActions.get(i));
                                }
                                if (i < selectedLocations.size() && ImGui.Button("Remove Location##" + selectedLocations.get(i))) {
                                    removeFishingLocation(selectedLocations.get(i));
                                    ScriptConsole.println("Fishing location removed: " + selectedLocations.get(i));
                                }
                            }
                            ImGui.EndTable();
                        }

                        /*ImGui.Text("Selected Fishing Actions:");*/

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
                    if (isCookingActive && !showLogs) {
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
                        long startTime = SnowsScript.startTime.toEpochMilli();
                        double hoursElapsed = (endTime - startTime) / 1000.0 / 60.0 / 60.0;
                        double averageFishPerHour = totalFishCooked / hoursElapsed;
                        ImGui.Text("Average fish cooked per hour: " + (int) averageFishPerHour);

                        for (Map.Entry<String, Integer> entry : fishCookedCount.entrySet()) {
                            ImGui.Text("Cooked: " + entry.getKey() + " - " + entry.getValue());
                        }
                    }
                    if (isWoodcuttingActive && !showLogs) {
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

                        if (ImGui.BeginTable("Selected Trees", 2, ImGuiWindowFlag.None.getValue())) {
                            ImGui.TableNextRow();
                            ImGui.TableSetupColumn("Tree Name", 0);
                            ImGui.TableSetupColumn("Remove", 1);
                            ImGui.TableHeadersRow();

                            List<String> selectedTrees = new ArrayList<>(getSelectedTreeNames());
                            for (String tree : selectedTrees) {
                                ImGui.TableNextRow();
                                ImGui.TableNextColumn();
                                ImGui.Text(tree);
                                ImGui.TableNextColumn();
                                if (ImGui.Button("Remove##" + tree)) {
                                    removeTreeName(tree);
                                    ScriptConsole.println("Tree removed: " + tree);
                                }
                            }
                            ImGui.EndTable();
                        }
                        int totalLogsCut = 0;
                        for (int count : logCount.values()) {
                            totalLogsCut += count;
                        }

                        long endTime = System.currentTimeMillis();
                        long startTime = SnowsScript.startTime.toEpochMilli();
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
                int noScrollbarFlag = 0x00000008; // ImGuiWindowFlags_NoScrollbar
                int noScrollWithMouseFlag = 0x00000010; // ImGuiWindowFlags_NoScrollWithMouse
                int combinedFlags = noScrollbarFlag | noScrollWithMouseFlag;

                if (ImGui.BeginChild("Child1", 580, 60, true, combinedFlags)) {


                    String botState;
                    if (isRunecraftingActive) {
                        botState = String.valueOf(Runecrafting.getScriptstate());
                    } else {
                        botState = String.valueOf(getBotState());
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
                    ImGui.SetCursorPosY(12);
                    ImGui.SetCursorPosX(10);

                    ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 20, 10); // Increase height padding
                    ImGui.PushStyleVar(ImGuiStyleVar.FrameRounding, 5);
                    ImGui.PushStyleColor(ImGuiCol.Button,0, 0, 0, 0);
                    ImGui.PushStyleColor(ImGuiCol.Text, 1, 1, 1, 1);
                    ImGui.PushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);


                    float buttonWidth = 580.0f - 2 * 20; // Width of BeginChild minus padding on both sides

                    ImGui.SetItemWidth(buttonWidth); // Set the button width

                    ImGui.PushStyleColor(ImGuiCol.Border, 0, 0, 0, 0); // Set the border color to transparent
                    ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0); // Set the border color to transparent



                    if (ImGui.Button("Enable Tooltips")) { // Set the button size
                        tooltipsEnabled = !tooltipsEnabled;
                    }

                    ImGui.PopStyleColor(2); // Reset the border color to the default value

                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Enable or disable tooltips in the Options tab");
                    }

                    ImGui.SameLine(); // This will place the next button on the same line

                    ImGui.PushStyleColor(ImGuiCol.Border, 0, 0, 0, 0); // Set the border color to transparent
                    ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0); // Set the border color to transparent

                    if (ImGui.Button("Logs")) { // Set the button size
                        showLogs = !showLogs;
                    }

                    ImGui.PopStyleColor(2); // Reset the border color to the default value

                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Show Console Logs");
                    }

                    ImGui.PopStyleVar(2);
                    ImGui.PopStyleColor(5);
                    float windowWidth = 577.0f;
                    float textWidth = ImGui.CalcTextSize("" + botState).getX();
                    float centeredX = (windowWidth - textWidth) / 2;

                    ImGui.SetCursorPosX(0);
                    ImGui.SetCursorPosY(10);
                    ImGui.SetCursorPosX(centeredX);
                    ImGui.Text("" + botState);
                    ImGui.PopStyleColor(1);

                    long elapsedTime = ScriptisOn ? Duration.between(startTime, Instant.now()).getSeconds() + totalElapsedTime : totalElapsedTime;
                    String elapsedTimeText = String.format("%02d:%02d:%02d", elapsedTime / 3600, (elapsedTime % 3600) / 60, elapsedTime % 60);
                    textWidth = ImGui.CalcTextSize(elapsedTimeText).getX();
                    centeredX = (windowWidth - textWidth) / 2;
                    ImGui.PopStyleColor(1);

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

                    ImGui.Text(versionText);



                    ImGui.EndChild();
                }
                ImGui.PopStyleColor();
                ImGui.PopStyleVar(1);
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
    private void createCenteredButton(String buttonText, Runnable onClick, boolean isClicked) {
        ImGui.PushStyleVar(ImGuiStyleVar.FrameRounding, 0.0f); // Control the frame rounding

        if (isClicked) {
            if (PurpleThemeSelected) {
                setStyleColor(ImGuiCol.Button, 80, 0, 150, 200);
            } else if (BlueThemeSelected) {
                setStyleColor(ImGuiCol.Button, 70, 130, 180, 200);
            } else if (RedThemeSelected) {
                setStyleColor(ImGuiCol.Button, 178, 34, 34, 200);
            } else if (OrangeThemeSelected) {
                setStyleColor(ImGuiCol.Button, 255, 140, 0, 200);
            } else if (YellowThemeSelected) {
                setStyleColor(ImGuiCol.Button, 255, 223, 0, 200);
            } else {
                setStyleColor(ImGuiCol.Button, 39, 92, 46, 255);

            }
        } else {
            ImGui.PushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        }

        ImGui.PushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
        ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0);
        float textWidth = ImGui.CalcTextSize(buttonText).getX();
        float padding = (170 - textWidth) / 2;
        ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding, 2.0f);
        ImGui.SetCursorPosX(0);
        if (ImGui.Button(buttonText)) {
            onClick.run();
        }
        ImGui.PopStyleVar(2);
        ImGui.PopStyleColor(3);
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

