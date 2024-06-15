package ImGui.Skills;

import net.botwithus.Misc.PorterMaker;
import net.botwithus.TaskScheduler;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import ImGui.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

import static ImGui.PredefinedStrings.*;
import static ImGui.PredefinedStrings.pouchName;
import static ImGui.PredefinedStrings.secondaryItemName;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Misc.CaveNightshade.NightshadePicked;
import static net.botwithus.Misc.CrystalChests.useTaverly;
import static net.botwithus.Misc.Necro.handleNecro;
import static net.botwithus.Misc.PorterMaker.*;
import static net.botwithus.Misc.Smelter.handleGoldBar;
import static net.botwithus.Misc.Smelter.handleGoldGauntlets;
import static net.botwithus.SnowsScript.*;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.tasks;

public class MiscImGui {


    public static void renderMisc() {
        if (isMiscActive && !isDissasemblerActive && !showLogs) {
            float totalWidth = 360.0f;
            float checkboxWidth = 100.0f;
            float numItems = 3.0f;
            float spacing = (totalWidth - (numItems * checkboxWidth)) / (numItems + 1);
            ImGui.SeparatorText("Miscellaneous Options");

            boolean NoneSelected = isportermakerActive || handleNecro || handleHarps || isMakeUrnsActive || isCrystalChestActive || isPlanksActive || isCorruptedOreActive || isSummoningActive || isGemCutterActive || isdivinechargeActive || isSmeltingActive;

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
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Will only work with gems and Enchanted gems");
                }
            }
            if (!NoneSelected || pickCaveNightshade) {
                if (!NoneSelected) {
                    ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                } else {
                    ImGui.SetCursorPosX(spacing);
                }
                pickCaveNightshade = ImGui.Checkbox("Nightshade", pickCaveNightshade);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Will pick Cave Nightshade");
                }
                if (!NoneSelected) {
                    ImGui.SameLine();
                }
            }
            if (!NoneSelected || isSiftSoilActive) {
                if (!NoneSelected) {
                    ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                } else {
                    ImGui.SetCursorPosX(spacing);
                }
                isSiftSoilActive = ImGui.Checkbox("Sift Soil", isSiftSoilActive);
            }
            if (!NoneSelected || isCrystalChestActive) {
                if (!NoneSelected) {
                    ImGui.SetCursorPosX(spacing);
                } else {
                    ImGui.SetCursorPosX(spacing);
                }
                isCrystalChestActive = ImGui.Checkbox("Crystal Chest", isCrystalChestActive);
                if (!NoneSelected) {
                    ImGui.SameLine();
                }
            }
            if (!NoneSelected || isMakeUrnsActive) {
                if (!NoneSelected) {
                    ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                } else {
                    ImGui.SetCursorPosX(spacing);
                }
                isMakeUrnsActive = ImGui.Checkbox("Make Urns", isMakeUrnsActive);
                if (!NoneSelected) {
                    ImGui.SameLine();
                }
            }
            if (!NoneSelected || handleHarps) {
                if (!NoneSelected) {
                    ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                } else {
                    ImGui.SetCursorPosX(spacing);
                }
                handleHarps = ImGui.Checkbox("Prif Harps", handleHarps);
            }
            if (!NoneSelected || handleNecro) {
                if (!NoneSelected) {
                    ImGui.SetCursorPosX(spacing);
                } else {
                    ImGui.SetCursorPosX(spacing);
                }
                handleNecro = ImGui.Checkbox("Necro", handleNecro);
                if (!NoneSelected) {
                    ImGui.SameLine();
                }
            }
            if (isMakeUrnsActive) {
                ImGui.Text("Only works at menophos");
            }
            if (isCrystalChestActive) {
                ImGui.Text("Only works at prifddinas");
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
                ImGui.SetCursorPosX(spacing);
                handleGoldBar = ImGui.Checkbox("Gold Ore", handleGoldBar);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Will only work with Gold Ore from the bank and backpack");
                }
                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                handleGoldGauntlets = ImGui.Checkbox("Make Bars with Gauntlets", handleGoldGauntlets);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Will work for all types of bars if you have gauntlets equipped");
                }

                ImGui.SeparatorText("Smelted Items Count");
                for (Map.Entry<String, Integer> entry : smeltedItems.entrySet()) {
                    ImGui.Text(entry.getKey() + ": " + entry.getValue());
                }

                int totalSmeltedItems = 0;
                for (int count : smeltedItems.values()) {
                    totalSmeltedItems += count;
                }

                long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                double smeltedItemsPerHour = totalSmeltedItems / elapsedHours;
                int smeltedItemsPerHourInt = (int) smeltedItemsPerHour;

                ImGui.Text("Smelted Items Per Hour: " + smeltedItemsPerHourInt);
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
                            setSpiritStoneName(selectedName);
                            log("Spirit Stone selected: " + selectedName);
                        } else {
                            log("Please select a valid Spirit Stone.");
                        }
                    }
                }
                ImGui.SetItemWidth(150.0F);
                if (ImGui.Combo("Pouch Names", pouchName_current_idx, pouchName.toArray(new String[0]))) {
                    int selectedIndex = pouchName_current_idx.get();
                    if (selectedIndex >= 0 && selectedIndex < pouchName.size()) {
                        String selectedName = pouchName.get(selectedIndex);
                        setPouchName(selectedName);
                        log("Pouch Name selected: " + selectedName);
                    } else {
                        log("Please select a valid Pouch Name.");
                    }
                }
                ImGui.SetItemWidth(150.0F);
                if (ImGui.Combo("Secondary Items", secondaryItem_current_idx, secondaryItemName.values().toArray(new String[0]))) {
                    int selectedIndex = secondaryItem_current_idx.get();
                    if (selectedIndex >= 0 && selectedIndex < secondaryItemName.size()) {
                        int selectedId = (int) secondaryItemName.keySet().toArray()[selectedIndex];
                        String selectedItemName = secondaryItemName.get(selectedId);
                        setSecondaryItem(selectedId);
                        log("Secondary Item selected: " + selectedItemName + " (" + selectedId + ")");
                    } else {
                        log("Please select a valid Secondary Item.");
                    }
                }
            }
            if (isportermakerActive) {
                if (tooltipsEnabled) {
                    String[] texts = {
                            "Have Preset Ready Saved",
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
                ImGui.SetCursorPosX(spacing);
                MakePorterVII = ImGui.Checkbox("Porter VII", MakePorterVII);
                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                MakePorterVI = ImGui.Checkbox("Porter VI", MakePorterVI);
                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                MakePorterV = ImGui.Checkbox("Porter V", MakePorterV);

                ImGui.SetCursorPosX(spacing);
                MakePorterIV = ImGui.Checkbox("Porter IV", MakePorterIV);
                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                MakePorterIII = ImGui.Checkbox("Porter III", MakePorterIII);
                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                MakePorterII = ImGui.Checkbox("Porter II", MakePorterII);

                ImGui.SetCursorPosX(spacing);
                MakePorterI = ImGui.Checkbox("Porter I", MakePorterI);

               /* ImGui.Text("Task");
                ImGui.SameLine();
                ImGui.Text("Amount to Create");
                ImGui.SameLine();
                ImGui.Text("Number Remaining");

                for (String task : TaskScheduler.getTasks().keySet()) {
                    ImGui.Text(task);
                    ImGui.SameLine();
                    ImGui.Text(String.valueOf(getTaskCount(task)));
                    ImGui.SameLine();
                    ImGui.Text(String.valueOf(getTaskCount(task) - portersMade.getOrDefault(task, 0)));
                }


                ImGui.InputInt("Set Amount", getTaskCount("Porters"), 1, 100, ImGuiWindowFlag.None.getValue());
                if (ImGui.Button("Set")) {
                    setAmount(amount);
                    PorterMaker.userTaskCount = getAmount();
                    log("[ImGui] User set a task with count: " + PorterMaker.userTaskCount);

                    TaskScheduler.addTask("Porters", getAmount());
                }

                if (TaskScheduler.isTaskComplete("Porters")) {
                    log("[ImGui] Task is complete.");
                }

// Display the tasks and their remaining counts
                for (String task : PorterMaker.taskScheduler.getTasks().keySet()) {
                    ImGui.Text(task);
                    ImGui.SameLine();
                    int remainingCount = getTaskCount(task);
                    ImGui.Text("Remaining: " + remainingCount);
                }*/





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
                for (Map.Entry<String, Integer> entry : Gems.entrySet()) {
                    ImGui.Text(entry.getKey() + ": " + entry.getValue());
                }

                int totalGems = 0;
                for (int count : Gems.values()) {
                    totalGems += count;
                }

                long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                double gemsPerHour = totalGems / elapsedHours;
                int gemsPerHourInt = (int) gemsPerHour;

                ImGui.Text("Gems Per Hour: " + gemsPerHourInt);
            }
            if (handleHarps) {
                ImGui.SeparatorText("Harmonic Dust Counts");
                for (Map.Entry<String, Integer> entry : harmonicDust.entrySet()) {
                    ImGui.Text(entry.getKey() + ": " + entry.getValue());
                }

                int totalDust = 0;
                for (int count : harmonicDust.values()) {
                    totalDust += count;
                }

                long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                double dustPerHour = totalDust / elapsedHours;
                int dustPerHourInt = (int) dustPerHour;

                ImGui.Text("Harmonic Dust Per Hour: " + dustPerHourInt);

                ImGui.SeparatorText("Harps Tuned at %");

                tunePercentage = ImGui.InputInt("Tune Harp at (%)", tunePercentage);

                if (tunePercentage < 10) {
                    tunePercentage = 10;
                } else if (tunePercentage > 70) {
                    tunePercentage = 70;
                }
            }
            if (isPlanksActive) {
                if (tooltipsEnabled) {
                    String[] texts = {
                            "Have Preset Ready Saved",
                            "Start at Fort Bank chest"
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
            if(isCrystalChestActive) {
                if (tooltipsEnabled) {
                    String[] texts = {
                            "Have Preset Ready Saved",
                            "will use Prif unless Taverly is enabled"
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
                ImGui.SeparatorText("Crystal Chest Options");
                ImGui.SetCursorPosX(spacing);
                useTaverly = ImGui.Checkbox("Taverly", useTaverly);
                if(ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Will use Taverly instead of Prifddinas");
                }
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
    }
}
