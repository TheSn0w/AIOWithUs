package ImGui;

import net.botwithus.*;
import net.botwithus.rs3.imgui.*;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.SnowsScript;


import java.awt.*;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static ImGui.CentreButton.createCenteredButton;
import static ImGui.Skills.ArchaeologyImGui.renderArchaeology;
import static ImGui.Skills.BottomChild.renderBottom;
import static ImGui.Skills.CombatImGui.renderCombat;
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
import static net.botwithus.Archaeology.Porters.bankwithoutPorter;
import static net.botwithus.Combat.Combat.shouldEatFood;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.*;
import static net.botwithus.Variables.Variables.*;

public class SnowScriptGraphics extends ScriptGraphicsContext {

    SnowsScript script;
    public Instant startTime;



    public SnowScriptGraphics(ScriptConsole scriptConsole, SnowsScript script) {
        super(scriptConsole);
        this.script = script;
        this.startTime = Instant.now();
    }


    public static void setScriptStatus(boolean status) {
        ScriptisOn = status;
    }




    @Override
    public void drawSettings() {
        setDefaultTheme();
        applyGreenTheme();

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

                buttonText = ScriptisOn ? "Stop Script" : "Start Script";
                textWidth1 = ImGui.CalcTextSize(buttonText).getX();
                padding = (childWidth - textWidth1) / 2;
                ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding, 15);
                ImGui.SetCursorPosX(0);
                if (ImGui.Button(buttonText)) {
                    if (ScriptisOn) {
                        setBotState(BotState.IDLE);
                        totalElapsedTime += Duration.between(startTime, Instant.now()).getSeconds();
                        ScriptisOn = false;
                    } else {
                        setBotState(BotState.SKILLING);
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
                        createCenteredButton("Harvest Chronicles", () -> harvestChronicles = !harvestChronicles, harvestChronicles);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will harvest chronicles");
                        }
                    } else if (isThievingActive) {
                        createCenteredButton("Thieving AIO", () -> isThievingActive = !isThievingActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Thieving - Read Tooltip");
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
                        createCenteredButton("Bank when 0 porter", () -> bankwithoutPorter = !bankwithoutPorter, bankwithoutPorter);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will force to bank when backpack doesnt contain any porters");
                        }
                    } else if (isCombatActive) {
                        createCenteredButton("Combat", () -> isCombatActive = !isCombatActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("AIO Fighter`");
                        }
                        createCenteredButton("Use Nearest Bank", () -> nearestBank = !nearestBank, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Nearest Bank");
                        }
                        createCenteredButton("Banks for food", () -> BankforFood = !BankforFood, BankforFood);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("will go wars to bank and withdraw fish food and go back to combat");
                        }
                        createCenteredButton("Eat food?", () -> shouldEatFood = !shouldEatFood, shouldEatFood);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will eat food when health is low");
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
                            ImGui.SetTooltip("start anywhere, only toggle this and looting if you want to loot`");
                        }
                        createCenteredButton("Use Notepaper", () -> useNotepaper = !useNotepaper, useNotepaper);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Magic notepaper or Enchant notepaper");
                        }
                    } else if (isFishingActive) {
                        createCenteredButton("Fishing", () -> isFishingActive = !isFishingActive, true);
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
                        createCenteredButton("Use Gote/Porter", () -> useGote = !useGote, useGote);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Gote/Porter");
                        }
                    } else if (isMiningActive) {
                        createCenteredButton("Mining", () -> isMiningActive = !isMiningActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Mining at any spot using any option`");
                        }
                        createCenteredButton("Use Nearest Bank", () -> nearestBank = !nearestBank, nearestBank);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Nearest Bank");
                        }
                        createCenteredButton("Use Gote/Porter", () -> useGote = !useGote, useGote);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Gote/Porter");
                        }
                    } else if (isWoodcuttingActive) {
                        createCenteredButton("Woodcutting", () -> isWoodcuttingActive = !isWoodcuttingActive, true);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Woodcutting at any spot using any option`");
                        }
                        createCenteredButton("Use Nearest Bank", () -> {
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
                        createCenteredButton("Use Gote/Porter", () -> useGote = !useGote, useGote);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will use Gote/Porter");
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
                        createCenteredButton("Soul Altar", () -> soulAltar = !soulAltar, soulAltar);
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Will do Soul Altar with Protean Essence");
                        }
                    } else if (isMiscActive) {
                        createCenteredButton("Misc", () -> isMiscActive = !isMiscActive, true);
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
                                } else if (message.contains("[Loot]")) {
                                    String[] parts = message.split(" ", 2);
                                    setStyleColor(ImGuiCol.Text, 0, 255, 0, 255);
                                    ImGui.Text(parts[0]);
                                    ImGui.SameLine();
                                    ImGui.Text(parts[1]);
                                    ImGui.PopStyleColor();
                                } else if (message.contains("[Success]")) {
                                    String[] parts = message.split(" ", 2);
                                    setStyleColor(ImGuiCol.Text, 0, 255, 0, 255);
                                    ImGui.Text(parts[0]);
                                    ImGui.SameLine();
                                    ImGui.Text(parts[1]);
                                    ImGui.PopStyleColor();
                                } else if (message.contains("[Caution]")) {
                                    String[] parts = message.split(" ", 2);
                                    setStyleColor(ImGuiCol.Text, 242, 140, 40, 255);
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
                            ImGui.EndChild();
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

                ImGui.End();
            }
        }
    }
    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }

}

