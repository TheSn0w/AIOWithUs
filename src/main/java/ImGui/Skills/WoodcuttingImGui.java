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
}
