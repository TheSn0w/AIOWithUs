package ImGui.Skills;

import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.imgui.Vector2f;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ImGui.PredefinedStrings.MiningList;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;

public class MiningImGui {

    public static void renderMining() {
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
                log("Rock name added: " + getRockName());
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
                    log("Predefined Rock added: " + selectedName);
                    selectedItemIndex.set(0);
                } else {
                    log("Please select a valid rock.");
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
                        log("Rock name removed: " + rock);
                    }

                    ImGui.PopStyleVar(2);
                    cursorPosX += buttonWidth + itemSpacing;
                }
                ImGui.EndChild();
            }

            ImGui.SeparatorText("Ores Mined Count");
            for (Map.Entry<String, Integer> entry : types.entrySet()) {
                String itemName = entry.getKey();
                int itemCount = entry.getValue();
                ImGui.Text(itemName + ": " + itemCount);
            }
        }
    }
}
