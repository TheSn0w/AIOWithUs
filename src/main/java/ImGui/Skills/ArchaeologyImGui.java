package ImGui.Skills;

import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ImGui.PredefinedStrings.predefinedCacheNames;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.materialTypes;

public class ArchaeologyImGui {

    public static void renderArchaeology() {
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

            ImGui.SetItemWidth(200.0F);
            filterText = ImGui.InputText("##FilterAndName", filterText);
            setName(filterText);

            filterItems();

            ImGui.SameLine();
            if (ImGui.Button("Enter Excavation Name")) {
                addName(getName());
                log("Excavation added: " + getName());
                setName("");
                filterText = "";
            }

            List<String> combinedFilteredItems = new ArrayList<>();
            combinedFilteredItems.add("Select an item...");
            combinedFilteredItems.addAll(filteredItems);
            if (MaterialCache) {
                for (String item : predefinedCacheNames) {
                    if (item.toLowerCase().contains(filterText.toLowerCase())) {
                        combinedFilteredItems.add(item);
                    }
                }
            }
            String[] comboItems = combinedFilteredItems.toArray(new String[0]);

            if (ImGui.Combo("##DynamicComboBox", selectedItemIndex, comboItems)) {
                int selectedIndex = selectedItemIndex.get();
                if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                    String selectedName = comboItems[selectedIndex];
                    addName(selectedName);
                    log("Predefined excavation/material cache added: " + selectedName);
                    selectedItemIndex.set(0);
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
                        log("Excavation name removed: " + name);
                    }
                }
                ImGui.EndTable();
            }

            if (useGote) {
                ImGui.SeparatorText("GOTE/Porter Options");
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
                ImGui.SeparatorText("Set Porter Withdraw Threshold from bank");
                ImGui.SetItemWidth(100.0F);
                int newThreshold = getChargeThreshold();
                newThreshold = ImGui.InputInt("##ChargeThreshold", newThreshold);
                if (newThreshold < 0) {
                    newThreshold = 0;
                } else if (newThreshold > 2000) {
                    newThreshold = 2000;
                }
                setChargeThreshold(newThreshold);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("if banking due to full inv, it will withdraw porters if porter count is below this threshold");
                }

                ImGui.SeparatorText("Set Porter Equip Charge Threshold from Inventory");
                ImGui.SetItemWidth(100.0F);
                int newEquipThreshold = getEquipChargeThreshold();
                newEquipThreshold = ImGui.InputInt("##EquipChargeThreshold", newEquipThreshold);
                if (newEquipThreshold < 0) {
                    newEquipThreshold = 0;
                } else if (newEquipThreshold > 2000) {
                    newEquipThreshold = 2000;
                }
                setEquipChargeThreshold(newEquipThreshold);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Will equip/charge porters when the current one reaches this threshold OR will bank if none are available in backpack");
                }
            }

            ImGui.SeparatorText("Materials Excavated Count");
            for (Map.Entry<String, Integer> entry : materialsExcavated.entrySet()) {
                ImGui.Text(entry.getKey() + ": " + entry.getValue());
            }

            int totalMaterialsExcavated = 0;
            for (int count : materialsExcavated.values()) {
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


    }
}
