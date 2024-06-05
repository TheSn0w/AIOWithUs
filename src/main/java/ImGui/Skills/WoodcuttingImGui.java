package ImGui.Skills;

import net.botwithus.SnowsScript;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.ScriptConsole;
import ImGui.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ImGui.PredefinedStrings.TreeList;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.logCount;

public class WoodcuttingImGui {

    public static void renderWoodcutting() {
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
                log("Tree added: " + getTreeName());
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
                    log("Predefined Tree added: " + selectedName);
                    selectedItemIndex.set(0);
                } else {
                    log("Please select a valid tree.");
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
                        log("Tree removed: " + tree);
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
        }
    }
}
