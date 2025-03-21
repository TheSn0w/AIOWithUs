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
import java.util.stream.Collectors;

import static ImGui.PredefinedStrings.CombatList;
import static ImGui.PredefinedStrings.predefinedCacheNames;
import static net.botwithus.Archaeology.SceneObjects.getSceneObjectTableData;
import static net.botwithus.Archaeology.SceneObjects.updateSceneObjectTableData;
import static net.botwithus.Combat.NPCs.getNpcTableData;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Main.useBankPin;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.TaskScheduler.*;
import static net.botwithus.TaskScheduler.pin4;
import static net.botwithus.Variables.Variables.*;

public class ArchaeologyImGui {

    public static boolean shownearbyCaches = false;

    public static void renderArchaeology() {
        if (isArcheologyActive) {
            if (tooltipsEnabled) {
                String[] texts = {
                        "Some areas are not supported by Traversal after banking",
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
            if (useBankPin) {
                if (ImGui.Begin("Bank Pin Settings", ImGuiWindowFlag.NoNav.getValue() | ImGuiWindowFlag.NoResize.getValue())) {
                    ImGui.SeparatorText("Pin Options");
                    ImGui.SetCursorPosX(15);
                    ImGui.SetItemWidth(100.0F);
                    pin1 = ImGui.InputInt("Pin 1", pin1);
                    ImGui.SetItemWidth(100.0F);
                    ImGui.SameLine();
                    pin2 = ImGui.InputInt("Pin 2", pin2);
                    ImGui.SetItemWidth(100.0F);
                    ImGui.SameLine();
                    pin3 = ImGui.InputInt("Pin 3", pin3);
                    ImGui.SetItemWidth(100.0F);
                    ImGui.SameLine();
                    pin4 = ImGui.InputInt("Pin 4", pin4);
                }
                ImGui.End();
            }
            float windowWidth1 = 400;
            String buttonText1 = "Show Nearby Caches?";
            float textWidth1 = ImGui.CalcTextSize(buttonText1).getX();
            float padding1 = (windowWidth1 - textWidth1) / 2;
            ImGui.PushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
            ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0);
            ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding1, 2.0f);
            ImGui.SetCursorPosX(padding1);
            ImGui.SetCursorPosX(0);
            if (ImGui.Button(buttonText1)) {
                shownearbyCaches = !shownearbyCaches;
            }
            ImGui.PopStyleVar(1);
            ImGui.PopStyleColor(2);
            ImGui.SeparatorText("Archaeology Options");

            if (shownearbyCaches) {
                if (ImGui.Begin("Nearby Caches", ImGuiWindowFlag.NoNav.getValue() | ImGuiWindowFlag.NoResize.getValue())) {
                    ImGui.SetWindowSize((float) 610, (float) 225);
                    ImGui.SeparatorText("Options");
                    List<List<String>> tableData = getSceneObjectTableData();

                    ImGui.SetItemWidth(600);

                    if (ImGui.ListBoxHeader("", 569, 0)) {
                        ImGui.Columns(1, "SceneObject names", true);
                        for (int i = 0; i < tableData.size(); i++) {
                            List<String> row = tableData.get(i);
                            String cacheName = row.get(0);

                            String sceneObjectIdentifier = cacheName + "##" + i;


                            if (!getSelectedNames().contains(cacheName)) {
                                ImGui.Selectable(sceneObjectIdentifier, false, 0);
                                if (ImGui.IsItemClicked(ImGui.MouseButton.LEFT_BUTTON)) {
                                    addName(cacheName);
                                    ScriptConsole.println("Added " + cacheName + " to selected names.");
                                }
                            }

                            ImGui.NextColumn();
                        }
                        ImGui.Columns(1, "Column", false);
                        ImGui.ListBoxFooter();
                    }
                }
                ImGui.End();
            }

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
                        log("Selected porter type: " + selectedPorterType);
                    } else {
                        log("Please select a valid porter type.");
                    }
                }
                ImGui.SetItemWidth(200.0F);

                if (ImGui.Combo("# of Porters to withdraw", currentQuantity, quantities)) {
                    int selectedIndex = currentQuantity.get();
                    if (selectedIndex >= 0 && selectedIndex < quantities.length) {
                        String selectedQuantity = quantities[selectedIndex];
                        log("Selected quantity: " + selectedQuantity);
                    } else {
                        log("Please select a valid quantity.");
                    }
                }
                ImGui.SeparatorText("Set Porter Withdraw Threshold from bank");
                ImGui.SetItemWidth(100.0F);
                int newThreshold = getBankingThreshold();
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
                int newEquipThreshold = getGraceChargesThreshold();
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

            /*    ImGui.SeparatorText("Whitelisted Excavation sites");
                for (CoordinateSceneObject obj : whitelistedSceneObjects) {
                    ImGui.Text(obj.getSceneObject().getName() + " at " + obj.getSceneObject().getCoordinate());
                }

                ImGui.SeparatorText("Blacklisted Excavation sites");
                for (CoordinateSceneObject obj : blacklistedSceneObjects) {
                    ImGui.Text(obj.getSceneObject().getName() + " at " + obj.getSceneObject().getCoordinate());
            }*/
        }
    }
}
